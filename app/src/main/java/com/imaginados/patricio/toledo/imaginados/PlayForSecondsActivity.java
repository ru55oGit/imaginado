package com.imaginados.patricio.toledo.imaginados;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.imaginados.patricio.toledo.imaginados.pojo.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayForSecondsActivity extends AppCompatActivity implements BackDialog.BackDialogListener {
    private RelativeLayout frameLayout;
    private TextView preguntaView;
    private TextView transition;
    private ImageView nosumaste;
    private int dim;
    private CountDownTimer timer;
    private TextView counter;
    private static final String FORMAT = "%02d:%02d";
    private static final String FORMAT_TRANSITION = "%02d";
    private int milisegundos = 0;
    private ArrayList<Question> pregResp;
    private Question question;
    private int aciertos;
    private TextView letter;
    private LinearLayout firstLine;
    private LinearLayout ll;
    private int gainedTime = 0;
    private int questionsSum = 0;
    private GradientDrawable gd;
    InputMethodManager inputMethodManager;


    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_for_seconds);

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        pregResp = getQuestion ();
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
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        timerTranstion(4000);
        aciertos = 0;
        // Instancio el reloj
        counter = (TextView) findViewById(R.id.counterText);
        Typeface digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        counter.setTypeface(digifont);
        // Instancio el contenedor de las letras
        firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        // Limpio todas las letras para la proxima pregunta
        firstLine.removeAllViews();

        question = pregResp.get((int)(Math.random() * pregResp.size()));

        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);
        // Instancio y seteo la pregunta
        preguntaView = (TextView) findViewById(R.id.question);
        preguntaView.setText(question.getPregunta());
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        preguntaView.setTypeface(lobsterFont);

        // Instancio y limpio el reloj de transicion
        transition = (TextView) findViewById(R.id.transition);

        nosumaste = (ImageView) findViewById(R.id.nosumaste);

        // Border radius para las letras
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int)getResources().getDimension(R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(R.dimen.border_letters_guess), getResources().getColor(R.color.secondaryColor));

        //timer(11000);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            letter = new TextView(this);
            if (Character.isWhitespace(question.getRespuesta().charAt(i))) {
                letter.setText(" ");
            } else {
                letter.setText("__");
                letter.setAllCaps(true);
                letter.setBackgroundResource(R.color.backLetters);
                letter.setBackground(gd);
            }

            letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dim = (int) getResources().getDimension(R.dimen.bg_letter_size);
            letter.setTextSize((int)getResources().getDimension(R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins(0, 0, (int)getResources().getDimension(R.dimen.border_radius), 0);
            letter.setLayoutParams(marginLetters);

            firstLine.addView(letter);
        }
    }

    public String AssetJSONFile (String filename, Context context) throws IOException {
        InputStream file = getAssets().open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    private ArrayList<Question> getQuestion () {
        ArrayList<Question> preguntaList = new ArrayList<Question>();
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("questions.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de preguntas
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("preguntas");
            for (int i=0; i<jarray.length();i++) {
                Question question = new Question();
                question.setPregunta(((JSONObject)jarray.get(i)).getString("pregunta"));
                question.setRespuesta(((JSONObject) jarray.get(i)).getString("respuesta"));
                preguntaList.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preguntaList;
    }

    // maneja la presion de las teclas
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BackDialog bd = new BackDialog();
            bd.show(getFragmentManager(), "finnish");
            timer.cancel();
            return false;
        }

        ll = (LinearLayout)findViewById(R.id.wordContainerFirst);
        ll.getChildCount();
        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
            if (Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                letter = new TextView(this);
                Character letra = (char) event.getDisplayLabel();
                letter.setText(letra.toString());
                letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                letter.setBackgroundResource(R.color.backLetters);
                letter.setBackground(gd);

                LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                marginLetters.setMargins(0, 0, (int)getResources().getDimension(R.dimen.border_radius), 0);
                letter.setLayoutParams(marginLetters);
                ll.removeViewAt(i);
                ll.addView(letter, i);

                aciertos++;
            }
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == question.getRespuesta().replaceAll(" ", "").length()) {
            // paro el reloj
           timer.cancel();
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            try {
                Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
                Integer segundos = Integer.parseInt(tiempo[1])*1000;
                milisegundos+= segundos;
            } catch (Exception e) {
                e.printStackTrace();
            }

            questionsSum++;
            if (questionsSum <= 5){
                //timerTranstion(4000);
                onResume();
            } else {
                showSecondsGained(milisegundos);
            }
        }
        return true;
    }

    private void showSecondsGained(int milis) {
        //counter.setText("Has acumulado "+ (milisegundos/1000) + " segundos.");
        //toggleKeyboardVisible(true);
        editor.putInt("time", milisegundos);
        editor.commit();
        finish();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible (final boolean flag) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                if (flag) {
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 1);
                } else {
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }

    private void timerTranstion(final int milliseconds) {
        timer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                transition.setText(""+String.format(FORMAT_TRANSITION,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                transition.setVisibility(View.VISIBLE);
                firstLine.setVisibility(View.INVISIBLE);
                toggleKeyboardVisible(false);
            }
            @Override
            public void onFinish() {
                transition.setVisibility(View.INVISIBLE);
                firstLine.setVisibility(View.VISIBLE);
                toggleKeyboardVisible(true);
                //onResume();
                timer(11000);
            }
        }.start();
    }

    /**
     *  Maneja el reloj con el tiempo que se dispone para adivinar
     *  @param milliseconds tiempo en milisegundos para setear el reloj
     *  */
    private void timer(final int milliseconds) {
        timer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                counter.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                nosumaste.setVisibility(View.INVISIBLE);
                counter.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                questionsSum++;
                // Cuando el reloj llega a cero, se cambia el mensaje
                if (questionsSum <= 5) {
                    counter.setVisibility(View.INVISIBLE);
                    nosumaste.setVisibility(View.VISIBLE);
                    //timerTranstion(4000);
                    onResume();
                } else {
                    showSecondsGained(milliseconds);
                }
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

        try {
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = (Integer.parseInt(tiempo[1]) + 1) * 1000;
            milisegundos = minutos + segundos;
            timer(milisegundos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
