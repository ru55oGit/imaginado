package com.ru55o.luckypalm.preguntas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    private Button aleatorio, emojis, acertijos,
    peliculas, escudos, marcas, banderas;

    private TextView title;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Boolean languageSelected;
    private Switch languageSwitch;
    private RelativeLayout header, footer;
    private Typeface lobsterFont;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);

        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");

        header = (RelativeLayout) findViewById(R.id.headerCategory);
        footer = (RelativeLayout) findViewById(R.id.footerCategory);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(lobsterFont);
        languageSelected = settings.getBoolean("languageSelected", true);
        languageSwitch = (Switch) findViewById(R.id.languageSwitch);
        languageSwitch.setChecked(!languageSelected);

        aleatorio = (Button) findViewById(R.id.aleatorio);
        aleatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });
        aleatorio.setTypeface(lobsterFont);
        if (languageSelected) {
            aleatorio.setText(getResources().getString(R.string.random_text_es));
            aleatorio.setTextColor(getResources().getColor(R.color.primaryColor));
            title.setText(getResources().getString(R.string.select_category_title_es));
            header.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            footer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        } else {
            aleatorio.setText(getResources().getString(R.string.random_text_en));
            aleatorio.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            title.setText(getResources().getString(R.string.select_category_title_en));
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            footer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        emojis = (Button) findViewById(R.id.emojis);
        emojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","emojis");
                } else {
                    editor.putString("categorySelected","enojis");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        acertijos = (Button) findViewById(R.id.acertijos);
        acertijos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","adivinanzas");
                } else {
                    editor.putString("categorySelected","wuzzles");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        peliculas = (Button) findViewById(R.id.peliculas);
        peliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","peliculas");
                } else {
                    editor.putString("categorySelected","movies");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });
        escudos = (Button) findViewById(R.id.escudos);
        escudos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","escudos");
                } else {
                    editor.putString("categorySelected","escudos");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        marcas = (Button) findViewById(R.id.marcas);
        marcas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","marcas");
                } else {
                    editor.putString("categorySelected","marcas");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        banderas = (Button) findViewById(R.id.banderas);
        banderas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","banderas");
                } else {
                    editor.putString("categorySelected","banderas");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        //attach a listener to check for changes in state
        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(languageSwitch.isChecked()){
                    title.setText(getResources().getString(R.string.select_category_title_en));
                    aleatorio.setText(getResources().getString(R.string.random_text_en));
                    header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    footer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    aleatorio.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    editor.putBoolean("languageSelected", false);
                }else{
                    title.setText(getResources().getString(R.string.select_category_title_es));
                    aleatorio.setText(getResources().getString(R.string.random_text_es));
                    header.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    footer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    aleatorio.setTextColor(getResources().getColor(R.color.primaryColor));
                    editor.putBoolean("languageSelected", true);
                }
                editor.commit();
                onResume();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 16) {
            aleatorio.setBackground(getImage("whiteback",0));
            if (!languageSwitch.isChecked()) {
                emojis.setBackground(getImage("emojis", Integer.parseInt(getLevelByCategory("emojis"))));
            } else {
                emojis.setBackground(getImage("enojis", Integer.parseInt(getLevelByCategory("enojis"))));
            }

            if (!languageSwitch.isChecked()) {
                acertijos.setBackground(getImage("adivinanzas", Integer.parseInt(getLevelByCategory("adivinanzas"))));
            } else {
                acertijos.setBackground(getImage("wuzzles", Integer.parseInt(getLevelByCategory("wuzzles"))));
            }

            if (!languageSwitch.isChecked()) {
                peliculas.setBackground(getImage("peliculas", Integer.parseInt(getLevelByCategory("peliculas"))));
            } else {
                peliculas.setBackground(getImage("movies", Integer.parseInt(getLevelByCategory("movies"))));
            }
            if (!languageSwitch.isChecked()) {
                peliculas.setBackground(getImage("peliculas", Integer.parseInt(getLevelByCategory("peliculas"))));
            } else {
                peliculas.setBackground(getImage("movies", Integer.parseInt(getLevelByCategory("movies"))));
            }
            if (!languageSwitch.isChecked()) {
                escudos.setBackground(getImage("escudos", Integer.parseInt(getLevelByCategory("escudos"))));
            } else {
                escudos.setBackground(getImage("escudos", Integer.parseInt(getLevelByCategory("escudos"))));
            }
            if (!languageSwitch.isChecked()) {
                marcas.setBackground(getImage("marcas", Integer.parseInt(getLevelByCategory("marcas"))));
            } else {
                marcas.setBackground(getImage("marcas", Integer.parseInt(getLevelByCategory("marcas"))));
            }
            if (!languageSwitch.isChecked()) {
                banderas.setBackground(getImage("banderas", Integer.parseInt(getLevelByCategory("banderas"))));
            } else {
                banderas.setBackground(getImage("banderas", Integer.parseInt(getLevelByCategory("banderas"))));
            }
        }
    }

    public String getLevelByCategory(String cat){
        String result = "";
        if ("adivinanzas".equals(cat)) {
            result = settings.getString("levelAdivinanzas","1");
        } else if ("wuzzles".equals(cat)) {
            result = settings.getString("levelWuzzles","1");
        } else if ("emojis".equals(cat)) {
            result = settings.getString("levelEmojis","1");
        } else if ("enojis".equals(cat)) {
            result = settings.getString("levelEnojis","1");
        } else if ("peliculas".equals(cat)) {
            result = settings.getString("levelPeliculas","1");
        } else if ("movies".equals(cat)) {
            result = settings.getString("levelMovies","1");
        } else if ("escudos".equals(cat)) {
            result = settings.getString("levelEscudos","1");
        } else if ("marcas".equals(cat)) {
            result = settings.getString("levelMarcas","1");
        } else if ("banderas".equals(cat)) {
            result = settings.getString("levelBanderas","1");
        }

        return result;
    }
    public Drawable getImage(String categorySelected, int i) {
        Drawable backgroundLevel;

        int res = "whiteback".equals(categorySelected)? getResources().getIdentifier("whiteback", "drawable", getPackageName()) : getResources().getIdentifier(categorySelected + i, "drawable", getPackageName());
        backgroundLevel = getResources().getDrawable(res);

        Bitmap original = ((BitmapDrawable) backgroundLevel).getBitmap();

        int sizeX = Math.round(backgroundLevel.getIntrinsicWidth() * 0.43f);
        int sizeY = Math.round(backgroundLevel.getIntrinsicHeight() * 0.43f);
        // escalo
        Bitmap bitmapResized = Bitmap.createScaledBitmap(original, sizeX, sizeY, false);

        backgroundLevel = new BitmapDrawable(getResources(), bitmapResized);

        return backgroundLevel;
    }

}
