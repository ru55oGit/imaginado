package com.imaginados.patricio.toledo.imaginados;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private String pregunta;
    private String respuesta;
    private int dim;
    private CountDownTimer timer;
    private TextView counter;
    private static final String FORMAT = "%02d:%02d";
    private static final String FORMAT_TRANSITION = "%02d";
    private int milisegundos = 0;
    private ArrayList<String> pregResp;
    private int aciertos;
    private TextView letter;
    private LinearLayout firstLine;
    private LinearLayout ll;
    private int gainedTime = 0;
    private int questionsSum = 0;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        pregResp = getQuestion();
        aciertos = 0;
        pregunta = (pregResp.get(0));
        respuesta = (pregResp.get(1));
        counter = (TextView) findViewById(R.id.counterText);
        firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        firstLine.removeAllViews();

        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);

        preguntaView = (TextView) findViewById(R.id.question);
        preguntaView.setText(pregunta);
        transition = (TextView) findViewById(R.id.transition);
        transition.setText("");

        timer(11000);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < respuesta.length(); i++) {
            letter = new TextView(this);
            if (Character.isWhitespace(respuesta.charAt(i))) {
                letter.setText("  ");
            } else {
                letter.setText("__");
                letter.setAllCaps(true);
                letter.setBackgroundResource(R.color.backLetters);
            }

            letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dim = (int) getResources().getDimension(R.dimen.bg_letter_size);
            letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int)getResources().getDimension(R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins(0, 0, 10, 0);
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

    private ArrayList<String> getQuestion () {
        ArrayList<String> pregunta = new ArrayList<String>();
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("questions.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de preguntas
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("preguntas");
            //obtengo la pregunta y respuesta aleatoriamente
            JSONObject jpregunta = (JSONObject)jarray.get((int)(Math.random() * jarray.length()));
            //obtengo la pregunta
            pregunta.add(jpregunta.getString("pregunta"));
            //obtengo la respuesta
            pregunta.add(jpregunta.getString("respuesta"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pregunta;
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
        for (int i = 0; i < respuesta.length(); i++) {
            // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
            if (Character.toUpperCase(respuesta.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                letter = new TextView(this);
                Character letra = (char) event.getDisplayLabel();
                letter.setText(letra.toString());
                letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                letter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                letter.setBackgroundResource(R.color.backLetters);

                LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                marginLetters.setMargins(0, 0, 10, 0);
                letter.setLayoutParams(marginLetters);
                ll.removeViewAt(i);
                ll.addView(letter, i);

                aciertos++;
            }
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == respuesta.replaceAll(" ", "").length()) {
            // paro el reloj
           timer.cancel();
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000;
            milisegundos+= segundos;
            counter.setText("¡Has ganado "+segundos/1000+ " segundos!");
            questionsSum++;
            if (questionsSum <= 5){
                timerTranstion(4000);
            } else {
                showSecondsGained(milisegundos);
            }
        }
        return true;
    }

    private void showSecondsGained(int milis){
        counter.setText("Has ganado "+ (milisegundos/1000) + " segundos para seguir jugando");
        toggleKeyboardVisible(false);

        editor.putInt("time", milisegundos);
        editor.commit();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible (final boolean flag) {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }

            @Override
            public void onFinish() {
                onResume();
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
            }
            public void onFinish() {
                questionsSum++;
                // Cuando el reloj llega a cero, se cambia el mensaje
                if (questionsSum <= 5) {
                    counter.setText("No sumaste segundos");
                    timerTranstion(4000);
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
        Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
        Integer segundos = (Integer.parseInt(tiempo[1]) + 1) * 1000;
        milisegundos = minutos + segundos;
        timer(milisegundos);
    }
}
