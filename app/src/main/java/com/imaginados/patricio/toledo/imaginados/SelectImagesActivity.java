package com.imaginados.patricio.toledo.imaginados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectImagesActivity extends AppCompatActivity {
    ImageView imagen1;
    ImageView imagen2;
    ImageView imagen3;
    ImageView imagen4;
    ImageView imagen5;
    ImageView imagen6;
    SharedPreferences settings;
    StringBuilder statusOfLevel;
    String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);

        settings = getSharedPreferences("Status", 0);

    }

    private int getSrcByLevel (String level, String category) {
        String uri = category + level;
        int res = getResources().getIdentifier(uri, "drawable", getPackageName());
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.gc();
        statusOfLevel = new StringBuilder(settings.getString("statusLevel","000000"));
        level = settings.getString("level", "1");

        // Seteo en nivel en el que estamos en la etiqueta de la pantalla
        TextView label = (TextView)findViewById(R.id.labelLevelText);
        label.setText("Nivel "+ level);

        imagen1 = (ImageView) findViewById(R.id.imgView1);
        imagen1.setImageResource(getSrcByLevel(level, "adivinanzas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(0) == '1'){
            imagen1.setAlpha(0.35f);
        } else {
            imagen1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagen1.setDrawingCacheEnabled(true);
                    Intent intent = new Intent(SelectImagesActivity.this, GuessImageActivity.class);
                    intent.putExtra("src", "adivinanzas"+level);
                    intent.putExtra("word", getWord("adivinanzas", level));
                    startActivity(intent);
                }
            });
        }

        imagen2 = (ImageView) findViewById(R.id.imgView2);
        imagen2.setImageResource(getSrcByLevel(level, "escudos"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(1) == '1') {
            imagen2.setAlpha(0.35f);
        } else {
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

        imagen3 = (ImageView) findViewById(R.id.imgView3);
        imagen3.setImageResource(getSrcByLevel(level, "marcas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(2) == '1') {
            imagen3.setAlpha(0.35f);
        } else {
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
        imagen4 = (ImageView) findViewById(R.id.imgView4);
        imagen4.setImageResource(getSrcByLevel(level, "peliculas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(3) == '1') {
            imagen4.setAlpha(0.35f);
        } else {
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

        imagen5 = (ImageView) findViewById(R.id.imgView5);
        imagen5.setImageResource(getSrcByLevel(level, "personajes"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(4) == '1') {
            imagen5.setAlpha(0.35f);
        } else {
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


        imagen6 = (ImageView) findViewById(R.id.imgView6);
        imagen6.setImageResource(getSrcByLevel(level, "banderas"));

        // si la imagen ya fue adivinada, le pongo opacity y le saco el click
        if (statusOfLevel.charAt(5) == '1') {
            imagen6.setAlpha(0.35f);
        } else {
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
    }

    private String getWord (String mode, String level) {
        String word = "";
        ArrayList<String> adivinanzas = new ArrayList<String>();
        ArrayList<String> escudos = new ArrayList<String>();
        ArrayList<String> marcas = new ArrayList<String>();
        ArrayList<String> peliculas = new ArrayList<String>();
        ArrayList<String> personajes = new ArrayList<String>();
        ArrayList<String> banderas = new ArrayList<String>();

        if (mode.equals("adivinanzas")) {
            adivinanzas.add("0");
            adivinanzas.add("limonada");
            adivinanzas.add("berenjena");
            word = adivinanzas.get(Integer.parseInt(level));
        }

        if (mode.equals("escudos")) {
            escudos.add("0");
            escudos.add("river plate");
            escudos.add("valencia");
            word = escudos.get(Integer.parseInt(level));
        }

        if (mode.equals("marcas")) {
            marcas.add("0");
            marcas.add("audi");
            marcas.add("bridgestone");
            word = marcas.get(Integer.parseInt(level));
        }

        if (mode.equals("peliculas")) {
            peliculas.add("0");
            peliculas.add("101 dalmatas");
            peliculas.add("300");
            word = peliculas.get(Integer.parseInt(level));
        }

        if (mode.equals("personajes")) {
            personajes.add("0");
            personajes.add("astro");
            personajes.add("aquaman");
            word = personajes.get(Integer.parseInt(level));
        }

        if (mode.equals("banderas")) {
            banderas.add("0");
            banderas.add("argentina");
            banderas.add("ghana");
            word = banderas.get(Integer.parseInt(level));
        }

        return word;
    }
}
