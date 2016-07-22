package com.imaginados.patricio.toledo.imaginados;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

public class GuessImageActivity extends AppCompatActivity implements BackDialog.BackDialogListener{
    private RelativeLayout frameLayout;
    private ImageView imageToGuess;
    private ImageView sinTiempo;
    // Counters variables
    private CountDownTimer timer;
    private TextView counter;
    private static final String FORMAT = "%02d:%02d";
    private int milisegundos;

    private String word;
    private int dim;
    private int aciertos = 0;
    private String uri;
    private String level;
    private GradientDrawable gd;

    SharedPreferences settings;
    SharedPreferences.Editor editor;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_image);

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius(10);
        gd.setStroke((int)getResources().getDimension(R.dimen.border_letters_guess), getResources().getColor(R.color.secondaryColor));

        milisegundos = settings.getInt("time", 30000);
        timer(milisegundos);

        // traigo el Nivel
        level = settings.getString("level","1");

        counter = (TextView) findViewById(R.id.counterText);
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        counter.setTypeface(lobsterFont);
        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);

        toggleKeyboardVisible();

        Bundle extras = getIntent().getExtras();
        // Traigo la imagen que se eligio para adivinar
        uri = extras.getString("src");
        int res = getResources().getIdentifier(uri, "drawable", getPackageName());
        // seteo la imagen en el imageview
        imageToGuess = (ImageView) findViewById(R.id.imageToGuess);
        imageToGuess.setImageResource(res);
        // obtengo la palabra que se va adivinar
        word = extras.getString("word");

        // titulo sin tiempos
        sinTiempo = (ImageView) findViewById(R.id.sintiempo);

        LinearLayout firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        LinearLayout secondLine = (LinearLayout)findViewById(R.id.wordContainerSecond);
        LinearLayout thirdLine = (LinearLayout)findViewById(R.id.wordContainerThird);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < word.length(); i++) {
            TextView letter = new TextView(this);
            if (Character.isWhitespace(word.charAt(i))) {
                letter.setText("  ");
            } else {
                letter.setText("__");
                letter.setAllCaps(true);
                letter.setBackgroundResource(R.color.primaryColor);
                letter.setBackground(gd);
            }

            letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dim = (int) getResources().getDimension(R.dimen.bg_letter_size);
            letter.setTextSize((int)getResources().getDimension(R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins(0, 0, 10, 0);
            letter.setLayoutParams(marginLetters);

            firstLine.addView(letter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (aciertos != word.length() && !"00:00".equalsIgnoreCase(this.counter.getText().toString())) {
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            try {
                Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
                Integer segundos = (Integer.parseInt(tiempo[1]) + 1) * 1000;
                milisegundos = minutos + segundos;
                // guardo los segundos totales para ser usados en la proxima palabra
                settings = getSharedPreferences("Status", 0);
                editor.putInt("time", milisegundos);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        finish();
    }

    // maneja la presion de las teclas
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!"00:00".equalsIgnoreCase(counter.getText().toString())) {
                BackDialog bd = new BackDialog();
                bd.show(getFragmentManager(), "finnish");
                timer.cancel();
            } else {
                finish();
            }
            return false;
        }

        LinearLayout ll = (LinearLayout)findViewById(R.id.wordContainerFirst);
        ll.getChildCount();
        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < word.length(); i++) {
            // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
            if (Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                TextView letter = new TextView(this);
                Character letra = (char) event.getDisplayLabel();
                letter.setText(letra.toString());
                letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                letter.setBackgroundResource(R.color.primaryColor);
                letter.setBackground(gd);

                LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                marginLetters.setMargins(0, 0, 10, 0);
                letter.setLayoutParams(marginLetters);
                ll.removeViewAt(i);
                ll.addView(letter, i);

                aciertos++;
            }
        }
        int errores = 0;
        for (int i = 0;i < word.length(); i++) {
            if (Character.toUpperCase(word.charAt(i))!=event.getDisplayLabel()) {
                errores++;
            }
        }
        if (errores == word.length()) {
            timer.cancel();
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000-1000;
            milisegundos = minutos + segundos;
            timer(milisegundos);
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == word.replaceAll(" ", "").length()) {
            // paro el reloj
            timer.cancel();
            // Cierro el teclado
            inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000;
            milisegundos = minutos + segundos;
            // a los segundos restantes, por haber acertado la palabra, le sumo tantos segundos como letras tenga la misma como bonus
            milisegundos+= 5000;
            // muestro la cantidad de segundos obtenidos
            Toast.makeText(getBaseContext(),"has ganado 5 segundos",Toast.LENGTH_LONG).show();
            // guardo los segundos totales para ser usados en la proxima palabra
            settings = getSharedPreferences("Status", 0);

            editor.putInt("time", milisegundos);
            editor.putString("statusLevel", saveStateOfLevel(settings.getString("statusLevel", "000000")));
            editor.commit();


            Intent intent = new Intent(GuessImageActivity.this, SelectImagesActivity.class);
            startActivity(intent);
            this.finish();
        }
        return true;
    }

    private String saveStateOfLevel(String status){
        StringBuilder sts = new StringBuilder(status);
        if(this.uri.contains("adivinanzas")){
            sts.replace(0,1,"1");
        }
        if(this.uri.contains("escudos")){
            sts.replace(1,2,"1");
        }
        if(this.uri.contains("marcas")){
            sts.replace(2,3,"1");
        }
        if(this.uri.contains("peliculas")){
            sts.replace(3,4,"1");
        }
        if(this.uri.contains("personajes")){
            sts.replace(4,5,"1");
        }
        if(this.uri.contains("banderas")){
            sts.replace(5,6,"1");
        }
        // si adivine todas las palabras, paso al siguiente nivel
        if ("111111".equals(sts.toString())) {
            level = ((Integer)(Integer.parseInt(level) + 1)).toString();
            editor.putString("level", level);
            editor.commit();
            sts = new StringBuilder("000000");
        }

        return sts.toString();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible () {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aciertos != word.replaceAll(" ", "").length()) {
                    if (!("00:00").equals(counter.getText())) {
                        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            }
        });
    }

    /**
     *  Maneja el reloj con el tiempo que se dispone para adivinar
     *  @param milliseconds tiempo en milisegundos para setear el reloj
     *  */
    private void timer(int milliseconds) {
        timer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                counter.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }
            public void onFinish() {
                // Cuando el reloj llega a cero, se cambia el mensaje
                sinTiempo.setVisibility(View.VISIBLE);
                counter.setText("00:00");
                counter.setVisibility(View.INVISIBLE);
                // Cierro el teclado cuando me quedo sin tiempo
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                SharedPreferences settings = getSharedPreferences("Status", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("time", 0);
                editor.commit();
            }
        }.start();
    }
    // en el back abro un popup, en el aceptar termino el activity
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        finish();
    }
    // en el back abro un popup, en el cancelar sigo con el timer
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // obtengo la cantidad de segundos restantes y los convierto en milisegundos
        String tiempo[] = ((String)this.counter.getText()).split(":");
        Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
        Integer segundos = (Integer.parseInt(tiempo[1]) + 1) * 1000;
        milisegundos = minutos + segundos;
        timer(milisegundos);
    }
}
