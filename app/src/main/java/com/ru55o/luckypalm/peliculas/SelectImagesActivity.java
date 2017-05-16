package com.ru55o.luckypalm.peliculas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class SelectImagesActivity extends AppCompatActivity {
    private ImageView imagen1;
    private ImageView imagen2;
    private ImageView imagen3;
    private ImageView imagen4;
    private ImageView imagen5;
    private ImageView imagen6;
    private StringBuilder statusOfLevel;
    private String level;
    private SharedPreferences settings;
    private RelativeLayout frameLayout;
    private SharedPreferences.Editor editor;
    private Typeface digifont;
    private TextView chrono;
    private int milisegundos;
    private String levelSelected;
    private static final String FORMAT = "%02d:%02d";
    private Boolean previousLevel;
    private Boolean nextsLevels;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_images);

        imagen1 = (ImageView) findViewById(R.id.imgView1);
        imagen2 = (ImageView) findViewById(R.id.imgView2);
        imagen3 = (ImageView) findViewById(R.id.imgView3);
        imagen4 = (ImageView) findViewById(R.id.imgView4);
        imagen5 = (ImageView) findViewById(R.id.imgView5);
        imagen6 = (ImageView) findViewById(R.id.imgView6);

        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout = (RelativeLayout) findViewById(R.id.selectLayout);
        // cerrar teclado
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);

        // seteo el tiempo que tengo para jugar en el reloj
        milisegundos = settings.getInt("time", 120000);

        digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        chrono = (TextView) findViewById(R.id.chrono);
        chrono.setTypeface(digifont);
        chrono.setText(""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));

        statusOfLevel = new StringBuilder(settings.getString("statusLevel","000000"));
        imagen1.setImageResource(getResources().getIdentifier("com.ru55o.luckypalm.acertijos", "drawable", getPackageName()));
        imagen2.setImageResource(getResources().getIdentifier("escudosfutbol", "drawable", getPackageName()));
        imagen3.setImageResource(getResources().getIdentifier("logos", "drawable", getPackageName()));
        imagen4.setImageResource(getResources().getIdentifier("iconospeliculas", "drawable", getPackageName()));
        imagen5.setImageResource(getResources().getIdentifier("personajesanimados", "drawable", getPackageName()));
        imagen6.setImageResource(getResources().getIdentifier("paises", "drawable", getPackageName()));

        // nivel seleccionado
        levelSelected = settings.getString("levelSelected", "1");
        // nivel actual jugado
        level = settings.getString("level", "1");
        // si el nivel seleccionado es menor al actual jugado, seteo un flag para no grisar las imagenes adivinadas
        level = Integer.parseInt(level) != Integer.parseInt(levelSelected)? levelSelected:level;
        previousLevel = Integer.parseInt(settings.getString("levelSelected", "1"))< Integer.parseInt(settings.getString("level", "1"));
        // si selecciona un nivel mayor al actual, les dejo ver las imagenes pero no jugar
        nextsLevels = Integer.parseInt(settings.getString("levelSelected", "1"))> Integer.parseInt(settings.getString("level", "1"));

        // Seteo en nivel en el que estamos en la etiqueta de la pantalla
        TextView label = (TextView)findViewById(R.id.labelLevelText);
        label.setText(level);

        // for testing
        /*label.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                level = ((Integer)(Integer.parseInt(level) + 1)).toString();
                editor.putString("level", level);
                editor.commit();
                ((TextView)v).setText(level);
            }
        });
        ImageView levelImage = (ImageView)findViewById(R.id.level);
        levelImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                level = ((Integer)(Integer.parseInt(level) - 1)).toString();
                editor.putString("level", level);
                editor.commit();
            }
        });*/

        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        label.setTypeface(lobsterFont);

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(0) == '1' && !previousLevel || nextsLevels){
            imagen1.setAlpha(0.35f);
            imagen1.setClickable(false);
        } else {
            imagen1.setAlpha(1f);
            imagen1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen1.setDrawingCacheEnabled(true);
                    Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                    intent.putExtra("src", "adivinanzas" + level);
                    intent.putExtra("word", getWord("adivinanzas", level));
                    startActivity(intent);
                }
            });
        }

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(1) == '1'&& !previousLevel || nextsLevels) {
            imagen2.setAlpha(0.35f);
            imagen2.setClickable(false);
        } else {
            imagen2.setAlpha(1f);
            imagen2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imagen2.setDrawingCacheEnabled(true);
                Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                intent.putExtra("src", "escudos"+level);
                intent.putExtra("word", getWord("escudos", level));
                startActivity(intent);
                }
            });
        }

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(2) == '1'&& !previousLevel || nextsLevels) {
            imagen3.setAlpha(0.35f);
            imagen3.setClickable(false);
        } else {
            imagen3.setAlpha(1f);
            imagen3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imagen3.setDrawingCacheEnabled(true);
                Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                intent.putExtra("src", "marcas"+ level);
                intent.putExtra("word", getWord("marcas", level));
                startActivity(intent);
                }
            });
        }

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(3) == '1'&& !previousLevel || nextsLevels) {
            imagen4.setAlpha(0.35f);
            imagen4.setClickable(false);
        } else {
            imagen4.setAlpha(1f);
            imagen4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imagen4.setDrawingCacheEnabled(true);
                Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                intent.putExtra("src", "peliculas"+ level);
                intent.putExtra("word", getWord("peliculas", level));
                startActivity(intent);
                }
            });
        }

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(4) == '1'&& !previousLevel || nextsLevels) {
            imagen5.setAlpha(0.35f);
            imagen5.setClickable(false);
        } else {
            imagen5.setAlpha(1f);
            imagen5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imagen5.setDrawingCacheEnabled(true);
                Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                intent.putExtra("src", "personajes"+ level);
                intent.putExtra("word", getWord("personajes", level));
                startActivity(intent);
                }
            });
        }

        // // si la imagen ya fue adivinada, le pongo opacity y le saco el click, o si selecciona un nivel superior
        if (statusOfLevel.charAt(5) == '1'&& !previousLevel || nextsLevels) {
            imagen6.setAlpha(0.35f);
            imagen6.setClickable(false);
        } else {
            imagen6.setAlpha(1f);
            imagen6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imagen6.setDrawingCacheEnabled(true);
                Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                intent.putExtra("src", "banderas"+ level);
                intent.putExtra("word", getWord("banderas", level));
                startActivity(intent);
                }
            });
        }

        new CountDownTimer(8000, 1000) {
            public void onTick(long millis) {
                switch ((int) millis/1000){
                    case 1:
                        imagen6.setImageResource(getSrcByLevel(level, "banderas"));
                        break;
                    case 2:
                        imagen5.setImageResource(getSrcByLevel(level, "personajes"));
                        break;
                    case 3:
                        imagen4.setImageResource(getSrcByLevel(level, "peliculas"));
                        break;
                    case 4:
                        imagen3.setImageResource(getSrcByLevel(level, "marcas"));
                        break;
                    case 5:
                        imagen2.setImageResource(getSrcByLevel(level, "escudos"));
                        break;
                    case 6:
                        imagen1.setImageResource(getSrcByLevel(level, "adivinanzas"));
                        break;
                }
            }
            public void onFinish() {
            }
        }.start();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private int getSrcByLevel (String level, String category) {
        String uri = category + level;
        int res = getResources().getIdentifier(uri, "drawable", getPackageName());
        return res;
    }

    public String AssetJSONFile (String filename, Context context) throws IOException {
        InputStream file = getAssets().open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    private String getWord (String categoria, String level) {
        String word = "";
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("data.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("palabras");
            //obtengo el nivel
            JSONObject nivel = (JSONObject)jarray.get(Integer.parseInt(level));
            //obtengo la palabra del nivel correspondiente, segun la categoria elegida
            word = nivel.getString(categoria);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }
}
