package com.imaginados.patricio.toledo.imaginados;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class SelectImagesActivity extends AppCompatActivity {
    private ImageView imagen1;
    private ImageView imagen2;
    private ImageView imagen3;
    private ImageView imagen4;
    private ImageView imagen5;
    private ImageView imagen6;
    private SharedPreferences settings;
    private StringBuilder statusOfLevel;
    private String level;
    private int milisegundos;
    private InputMethodManager inputMethodManager;
    private RelativeLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);

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

        settings = getSharedPreferences("Status", 0);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        frameLayout = (RelativeLayout) findViewById(R.id.selectLayout);
        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        statusOfLevel = new StringBuilder(settings.getString("statusLevel","000000"));
        level = settings.getString("level", "1");
        milisegundos = settings.getInt("time", 30000);

        // Seteo en nivel en el que estamos en la etiqueta de la pantalla
        TextView label = (TextView)findViewById(R.id.labelLevelText);
        label.setText(level);
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        label.setTypeface(lobsterFont);

        imagen1 = (ImageView) findViewById(R.id.imgView1);
        imagen1.setImageResource(getSrcByLevel(level, "adivinanzas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(0) == '1'){
            imagen1.setAlpha(0.35f);
            imagen1.setClickable(false);
        } else {
            imagen1.setAlpha(1f);
            imagen1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen1.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "adivinanzas" + level);
                        intent.putExtra("word", getWord("adivinanzas", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }

        imagen2 = (ImageView) findViewById(R.id.imgView2);
        imagen2.setImageResource(getSrcByLevel(level, "escudos"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(1) == '1') {
            imagen2.setAlpha(0.35f);
            imagen2.setClickable(false);
        } else {
            imagen2.setAlpha(1f);
            imagen2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen2.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "escudos"+level);
                        intent.putExtra("word", getWord("escudos", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }

        imagen3 = (ImageView) findViewById(R.id.imgView3);
        imagen3.setImageResource(getSrcByLevel(level, "marcas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(2) == '1') {
            imagen3.setAlpha(0.35f);
            imagen3.setClickable(false);
        } else {
            imagen3.setAlpha(1f);
            imagen3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen3.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "marcas"+ level);
                        intent.putExtra("word", getWord("marcas", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }
        imagen4 = (ImageView) findViewById(R.id.imgView4);
        imagen4.setImageResource(getSrcByLevel(level, "peliculas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(3) == '1') {
            imagen4.setAlpha(0.35f);
            imagen4.setClickable(false);
        } else {
            imagen4.setAlpha(1f);
            imagen4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen4.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "peliculas"+ level);
                        intent.putExtra("word", getWord("peliculas", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }

        imagen5 = (ImageView) findViewById(R.id.imgView5);
        imagen5.setImageResource(getSrcByLevel(level, "personajes"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(4) == '1') {
            imagen5.setAlpha(0.35f);
            imagen5.setClickable(false);
        } else {
            imagen5.setAlpha(1f);
            imagen5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen5.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "personajes"+ level);
                        intent.putExtra("word", getWord("personajes", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }


        imagen6 = (ImageView) findViewById(R.id.imgView6);
        imagen6.setImageResource(getSrcByLevel(level, "banderas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(5) == '1') {
            imagen6.setAlpha(0.35f);
            imagen6.setClickable(false);
        } else {
            imagen6.setAlpha(1f);
            imagen6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen6.setDrawingCacheEnabled(true);
                    Intent intent;
                    if (milisegundos > 0) {
                        intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                        intent.putExtra("src", "banderas"+ level);
                        intent.putExtra("word", getWord("banderas", level));
                    } else {
                        intent = new Intent(SelectImagesActivity.this, ObtainSecondsActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }
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
