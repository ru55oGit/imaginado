package com.imaginados.patricio.toledo.imaginados;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
    private Boolean timerFlag;
    private CountDownTimer showSoftKey;
    private TextView counter;
    private static final String FORMAT = "%02d:%02d";
    private int milisegundos;

    private String word;
    private int dim;
    private int aciertos = 0;
    private String uri;
    private String level;
    private GradientDrawable gd;
    private Typeface digifont;
    private Typeface lobsterFont;
    private Toast toast;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private InputMethodManager inputMethodManager;
    private ImageView sharewsap;
    private ImageView volver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_image);
        digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        timerFlag = true;

        // seteo el tiempo que tengo para jugar en el reloj
        milisegundos = settings.getInt("time", 60000);
        counter = (TextView) findViewById(R.id.counterText);
        counter.setTypeface(digifont);
        counter.setText(""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));

        // share wsap
        sharewsap = (ImageView) findViewById(R.id.sharewsap);
        sharewsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);

            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);
        toggleKeyboardVisible();

        // border radius
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int) getResources().getDimension(R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(R.dimen.border_letters_guess), getResources().getColor(R.color.secondaryColor));

        // traigo el Nivel
        level = settings.getString("level","1");

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

        // volver
        volver = (ImageView) findViewById(R.id.volver);

        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        LinearLayout secondLine = (LinearLayout)findViewById(R.id.wordContainerSecond);
        LinearLayout thirdLine = (LinearLayout)findViewById(R.id.wordContainerThird);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < word.length(); i++) {
            TextView letter = new TextView(this);
            if (Character.isWhitespace(word.charAt(i))) {
                letter.setText(" ");
            } else if ('|' != word.charAt(i)){
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
            // uso el pipe para mandar al segundo renglon palabras cuando
            // la cantidad de las mismas superan el ancho de la pantalla
            if (word.indexOf("|") > 0) {
                if (i < word.indexOf("|")) {
                    firstLine.addView(letter);
                } else {
                    if (i > word.indexOf("|")) {
                        secondLine.addView(letter);
                    }
                }
            } else {
                firstLine.addView(letter);
            }
        }
        // si no hay segundos, abro el popup sin tiempo
        if (milisegundos == 0) {
            customDialog();
        } else {
            showSoftKey = new CountDownTimer(700, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancelo el custom toast cdo salgo
        if (toast != null) {
            toast.cancel();
        }

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
        // arranco el timer cuando arriesga la primer tecla
        if (timerFlag) {
            milisegundos = settings.getInt("time", 60000);
            timer(milisegundos);
            timerFlag = false;
        }


        if (toast != null){
            toast.cancel();
        }

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
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.wordContainerSecond);

        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < word.length(); i++) {
            // si viene un pipe, es que las palabras estan divididas en 2 renglones
            if (word.indexOf("|") < 0) {
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
            } else if ('|' != word.charAt(i)) {
                // si las letras evaluadas estan antes que el pipe, evaluo en el primer renglon...si estan despues evaluo en el segundo
                if (i < word.indexOf("|") && Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__") ||
                        i > word.indexOf("|") && Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll2.getChildAt(i -(word.indexOf("|")+1))).getText().equals("__")) {
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
                    if (i < word.indexOf("|")) {
                        ll.removeViewAt(i);
                        ll.addView(letter, i);
                    } else {
                        ll2.removeViewAt(i - (word.indexOf("|")+1));
                        ll2.addView(letter, i - (word.indexOf("|")+1));
                    }
                    aciertos++;
                }
            }


        }
        int errores = 0;
        for (int i = 0;i < word.length(); i++) {
            if (Character.toUpperCase(word.charAt(i))!=event.getDisplayLabel()) {
                errores++;
            }
        }
        // si ingreso un caracter que no esta en la/s palabra/s
        if (errores == word.length()) {
            // paro el reloj
            timer.cancel();
            // obtengo los segundos que habia y le resto 1 segundo
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000-2000;
            milisegundos = minutos + segundos;
            timer(milisegundos);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Fallaste. -2 Segundos");
            text.setTypeface(lobsterFont);

            toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP, 0, (int)getResources().getDimension(R.dimen.top_toast));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == word.replaceAll(" ", "").replaceAll("\\|","").length()) {
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
            milisegundos+= 10000;
            // muestro la cantidad de segundos obtenidos
            Toast.makeText(getBaseContext(),"has ganado 10 segundos",Toast.LENGTH_LONG).show();
            // guardo los segundos totales para ser usados en la proxima palabra
            settings = getSharedPreferences("Status", 0);

            editor.putInt("time", milisegundos);
            editor.putString("statusLevel", saveStateOfLevel(settings.getString("statusLevel", "000000")));
            editor.commit();


            //Intent intent = new Intent(GuessImageActivity.this, SelectImagesActivity.class);
            //startActivity(intent);
            //this.finish();
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
                customDialog();
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

    private void customDialog(){
        // custom dialog
        final Dialog dialogCustom = new Dialog(GuessImageActivity.this);
        dialogCustom.setContentView(R.layout.custom_dialog_withoutseconds);

        ImageView ganar = (ImageView) dialogCustom.findViewById(R.id.ganarSegundos);
        ImageView comprar = (ImageView) dialogCustom.findViewById(R.id.comprarSegundos);
        ganar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuessImageActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Muy pronto podras comprar segundos", Toast.LENGTH_LONG).show();
            }
        });

        ImageButton dialogButton = (ImageButton) dialogCustom.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustom.dismiss();
                sinTiempo.setVisibility(View.VISIBLE);
                counter.setVisibility(View.INVISIBLE);
            }
        });

        dialogCustom.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                // Cierro el teclado cuando me quedo sin tiempo
                inputMethodManager.hideSoftInputFromWindow(frameLayout.getWindowToken(), 0);
            }
        });
        dialogCustom.show();
    }
}
