package com.luckypalm.imaginados;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luckypalm.imaginados.pojo.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlayForSecondsActivity extends AppCompatActivity implements BackDialog.BackDialogListener {
    private RelativeLayout frameLayout;
    private TextView preguntaView;
    private TextView preguntaTitle;
    private ImageView imageForPlay;
    private TextView transition;
    private Boolean timerFlag;
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
    private LinearLayout secondLine;
    private LinearLayout ll;
    private LinearLayout ll2;
    private int gainedTime = 0;
    private int errors = 0;
    private GradientDrawable gd;
    private ArrayList<Integer> random = new ArrayList<Integer>();
    InputMethodManager inputMethodManager;


    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.luckypalm.imaginados.R.layout.activity_play_for_seconds);

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        pregResp = getQuestion();
        for (int i=0; i<pregResp.size();i++) {
            random.add(i);
        }
        Collections.shuffle(random);
        Collections.shuffle(random);
        frameLayout = (RelativeLayout) findViewById(com.luckypalm.imaginados.R.id.frameCounter);
        toggleKeyboardVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerFlag = true;

        //timerTranstion(6000);
        aciertos = 0;
        // Instancio el reloj
        counter = (TextView) findViewById(com.luckypalm.imaginados.R.id.counterText);
        Typeface digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        counter.setTypeface(digifont);
        counter.setText("00:10");
        // Instancio el contenedor de las letras
        firstLine = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerFirst);
        secondLine = (LinearLayout) findViewById(com.luckypalm.imaginados.R.id.wordContainerSecond);
        // Limpio todas las letras para la proxima pregunta
        firstLine.removeAllViews();
        secondLine.removeAllViews();
        random.remove(random.size()-1);
        int questionNumber = random.get(random.size()-1);
        //int questionNumber = (int) (pregResp.size() - Math.random() * pregResp.size());

        question = pregResp.get(questionNumber);

        // Instancio y seteo la pregunta
        preguntaTitle = (TextView) findViewById(com.luckypalm.imaginados.R.id.title);
        preguntaView = (TextView) findViewById(com.luckypalm.imaginados.R.id.question);
        preguntaTitle.setText(question.getTitulo());

        imageForPlay = (ImageView) findViewById(com.luckypalm.imaginados.R.id.imageForPlay);
        if (question.getPregunta().contains("jpg")) {
            preguntaView.setVisibility(View.INVISIBLE);
            imageForPlay.setVisibility(View.VISIBLE);
            imageForPlay.setImageResource(getResources().getIdentifier(question.getPregunta().replace(".jpg", ""),"drawable",getPackageName()));
        } else {
            preguntaView.setVisibility(View.VISIBLE);
            imageForPlay.setVisibility(View.INVISIBLE);
            preguntaView.setText(question.getPregunta());
        }
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        preguntaView.setTypeface(lobsterFont);
        preguntaTitle.setTypeface(lobsterFont);

        // Border radius para las letras
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_letters_guess), getResources().getColor(com.luckypalm.imaginados.R.color.secondaryColor));

        //timer(11000);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            letter = new TextView(this);
            if (Character.isWhitespace(question.getRespuesta().charAt(i))) {
                letter.setText(" ");
            } else {
                letter.setText("__");
                letter.setAllCaps(true);
                letter.setBackgroundResource(com.luckypalm.imaginados.R.color.backLetters);
                if  (Build.VERSION.SDK_INT > 15 ) {
                    letter.setBackground(gd);
                }
            }

            letter.setGravity(Gravity.CENTER_HORIZONTAL);
            dim = (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.bg_letter_size);
            letter.setTextSize((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins(0, 0, (int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_radius), 0);
            letter.setLayoutParams(marginLetters);

            if (question.getRespuesta().indexOf("|") > 0) {
                if (i < question.getRespuesta().indexOf("|")) {
                    firstLine.addView(letter);
                } else {
                    if (i > question.getRespuesta().indexOf("|")) {
                        secondLine.addView(letter);
                    }
                }
            } else {
                firstLine.addView(letter);
            }
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
                question.setTitulo(((JSONObject) jarray.get(i)).getString("title"));
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
        if (timerFlag) {
            timer(10000);
            timerFlag = false;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BackDialog bd = new BackDialog();
            bd.show(getFragmentManager(), "finnish");
            timer.cancel();
            return false;
        }

        ll = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerFirst);
        ll2 = (LinearLayout) findViewById(com.luckypalm.imaginados.R.id.wordContainerSecond);
        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            // si viene un pipe, es que las palabras estan divididas en 2 renglones
            if (question.getRespuesta().indexOf("|") < 0) {
                // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
                if (Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                    letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(com.luckypalm.imaginados.R.color.backLetters);
                    if  (Build.VERSION.SDK_INT > 15 ) {
                        letter.setBackground(gd);
                    }

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins(0, 0, (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_radius), 0);
                    letter.setLayoutParams(marginLetters);
                    ll.removeViewAt(i);
                    ll.addView(letter, i);

                    aciertos++;
                }
            }  else if ('|' != question.getRespuesta().charAt(i)) {
                // si las letras evaluadas estan antes que el pipe, evaluo en el primer renglon...si estan despues evaluo en el segundo
                if (i < question.getRespuesta().indexOf("|") && Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__") ||
                        i > question.getRespuesta().indexOf("|") && Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll2.getChildAt(i -(question.getRespuesta().indexOf("|")+1))).getText().equals("__")) {
                    TextView letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(com.luckypalm.imaginados.R.color.primaryColor);
                    if  (Build.VERSION.SDK_INT > 15 ) {
                        letter.setBackground(gd);
                    }

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins(0, 0, 10, 0);
                    letter.setLayoutParams(marginLetters);
                    if (i < question.getRespuesta().indexOf("|")) {
                        ll.removeViewAt(i);
                        ll.addView(letter, i);
                    } else {
                        ll2.removeViewAt(i - (question.getRespuesta().indexOf("|")+1));
                        ll2.addView(letter, i - (question.getRespuesta().indexOf("|")+1));
                    }
                    aciertos++;
                }
            }
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == question.getRespuesta().replaceAll(" ", "").replaceAll("\\|","").length()) {
            // paro el reloj
           timer.cancel();
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            onResume();
            try {
                Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
                Integer segundos = Integer.parseInt(tiempo[1])*1000;
                milisegundos+= segundos;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void backToPlay(int milis) {
        editor.putInt("time", milisegundos);
        editor.commit();
        finish();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible() {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
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
                secondLine.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFinish() {
                transition.setVisibility(View.INVISIBLE);
                firstLine.setVisibility(View.VISIBLE);
                secondLine.setVisibility(View.VISIBLE);
                //onResume();
                timer(10000);
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
                counter.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                errors++;
                // Cuando el reloj llega a cero, se cambia el mensaje
                if (errors == 3) {
                    counter.setVisibility(View.INVISIBLE);
                    backToPlay(milliseconds);
                } else {
                    onResume();
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
