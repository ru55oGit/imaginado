package com.ru55o.luckypalm.preguntas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    private Button aleatorio, emojis, acertijos,
    peliculas, escudos, marcas, banderas, famosos, shadows,
    cat_aleatorio, cat_acertijos, cat_logos, cat_peliculas,
    cat_comosellama,cat_emojis, cat_logosdeportes, cat_paises, cat_shadows;
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


        if (languageSelected) {
            title.setText(getResources().getText(R.string.select_category_title_es));
            header.setBackgroundColor(getResources().getColor(R.color.backgroundEnglish));
            footer.setBackgroundColor(getResources().getColor(R.color.backgroundEnglish));
        } else {
            title.setText(getResources().getText(R.string.select_category_title_en));
            header.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
            footer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
        }

        // Fila aleatorio
        cat_aleatorio = (Button) findViewById(R.id.cat_aleatorio);
        aleatorio = (Button) findViewById(R.id.aleatorio);
        aleatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });
        // Fila acertijos
        cat_acertijos = (Button) findViewById(R.id.cat_acertijos);
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
        //Fila logos
        cat_logos = (Button) findViewById(R.id.cat_logos);
        marcas = (Button) findViewById(R.id.marcas);
        marcas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","marcas");
                } else {
                    editor.putString("categorySelected","logos");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });
        //Fila peliculas
        cat_peliculas = (Button) findViewById(R.id.cat_peliculas);
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
        //Fila comosellama
        cat_comosellama = (Button) findViewById(R.id.cat_comosellama);
        famosos = (Button) findViewById(R.id.famosos);
        famosos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","famosos");
                } else {
                    editor.putString("categorySelected","celebrities");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });
        //Fila emojis
        cat_emojis = (Button) findViewById(R.id.cat_emojis);
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
        //Fila escudos
        cat_logosdeportes = (Button) findViewById(R.id.cat_logosdeportes);
        escudos = (Button) findViewById(R.id.escudos);
        escudos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","escudos");
                } else {
                    editor.putString("categorySelected","teams");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        //Fila paises
        cat_paises = (Button) findViewById(R.id.cat_paises);
        banderas = (Button) findViewById(R.id.banderas);
        banderas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","banderas");
                } else {
                    editor.putString("categorySelected","flags");
                }
                editor.commit();
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        //Fila shadows
        cat_shadows = (Button) findViewById(R.id.cat_shadows);
        shadows = (Button) findViewById(R.id.shadows);
        shadows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","sombras");
                } else {
                    editor.putString("categorySelected","shadows");
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
                    title.setText(getResources().getText(R.string.select_category_title_en));
                    header.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
                    footer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
                    editor.putBoolean("languageSelected", false);
                }else{
                    title.setText(getResources().getText(R.string.select_category_title_es));
                    header.setBackgroundColor(getResources().getColor(R.color.backgroundEnglish));
                    footer.setBackgroundColor(getResources().getColor(R.color.backgroundEnglish));
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
            if (!languageSwitch.isChecked()) {
                aleatorio.setBackground(resizeImageByCategory("aleatorio",((int)(Math.random() * ((10 - 1) + 1)) + 1)));
                cat_aleatorio.setBackground(resizeImageByName("cat_aleatorio"));
            } else {
                aleatorio.setBackground(resizeImageByCategory("aleatorio_en",((int)(Math.random() * ((10 - 1) + 1)) + 1)));
                cat_aleatorio.setBackground(resizeImageByName("cat_aleatorio_en"));
            }

            if (!languageSwitch.isChecked()) {
                acertijos.setBackground(resizeImageByCategory("adivinanzas", Integer.parseInt(getLevelByCategory("adivinanzas"))));
                cat_acertijos.setBackground(resizeImageByName("cat_acertijos"));
            } else {
                acertijos.setBackground(resizeImageByCategory("wuzzles", Integer.parseInt(getLevelByCategory("wuzzles"))));
                cat_acertijos.setBackground(resizeImageByName("cat_acertijos_en"));
            }

            if (!languageSwitch.isChecked()) {
                marcas.setBackground(resizeImageByCategory("marcas", Integer.parseInt(getLevelByCategory("marcas"))));
                cat_logos.setBackground(resizeImageByName("cat_logos"));
            } else {
                marcas.setBackground(resizeImageByCategory("logos", Integer.parseInt(getLevelByCategory("logos"))));
                cat_logos.setBackground(resizeImageByName("cat_logos_en"));
            }

            if (!languageSwitch.isChecked()) {
                peliculas.setBackground(resizeImageByCategory("peliculas", Integer.parseInt(getLevelByCategory("peliculas"))));
                cat_peliculas.setBackground(resizeImageByName("cat_peliculas"));
            } else {
                peliculas.setBackground(resizeImageByCategory("movies", Integer.parseInt(getLevelByCategory("movies"))));
                cat_peliculas.setBackground(resizeImageByName("cat_peliculas_en"));
            }

            if (!languageSwitch.isChecked()) {
                famosos.setBackground(resizeImageByCategory("famosos", Integer.parseInt(getLevelByCategory("famosos"))));
                cat_comosellama.setBackground(resizeImageByName("cat_comosellama"));
            } else {
                famosos.setBackground(resizeImageByCategory("celebrities", Integer.parseInt(getLevelByCategory("celebrities"))));
                cat_comosellama.setBackground(resizeImageByName("cat_comosellama_en"));
            }

            if (!languageSwitch.isChecked()) {
                emojis.setBackground(resizeImageByCategory("emojis", Integer.parseInt(getLevelByCategory("emojis"))));
                cat_emojis.setBackground(resizeImageByName("cat_emojis"));
            } else {
                emojis.setBackground(resizeImageByCategory("enojis", Integer.parseInt(getLevelByCategory("enojis"))));
                cat_emojis.setBackground(resizeImageByName("cat_emojis_en"));
            }

            if (!languageSwitch.isChecked()) {
                escudos.setBackground(resizeImageByCategory("escudos", Integer.parseInt(getLevelByCategory("escudos"))));
                cat_logosdeportes.setBackground(resizeImageByName("cat_logosdeportes"));
            } else {
                escudos.setBackground(resizeImageByCategory("teams", Integer.parseInt(getLevelByCategory("teams"))));
                cat_logosdeportes.setBackground(resizeImageByName("cat_logosdeportes_en"));
            }

            if (!languageSwitch.isChecked()) {
                banderas.setBackground(resizeImageByCategory("banderas", Integer.parseInt(getLevelByCategory("banderas"))));
                cat_paises.setBackground(resizeImageByName("cat_paises"));
            } else {
                banderas.setBackground(resizeImageByCategory("flags", Integer.parseInt(getLevelByCategory("flags"))));
                cat_paises.setBackground(resizeImageByName("cat_paises_en"));
            }

            if (!languageSwitch.isChecked()) {
                shadows.setBackground(resizeImageByCategory("sombras", Integer.parseInt(getLevelByCategory("sombras"))));
                cat_shadows.setBackground(resizeImageByName("cat_shadows"));
            } else {
                shadows.setBackground(resizeImageByCategory("shadows", Integer.parseInt(getLevelByCategory("shadows"))));
                cat_shadows.setBackground(resizeImageByName("cat_shadows_en"));
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
        } else if ("teams".equals(cat)) {
            result = settings.getString("levelTeams","1");
        } else if ("banderas".equals(cat)) {
            result = settings.getString("levelBanderas","1");
        } else if ("flags".equals(cat)) {
            result = settings.getString("levelFlags","1");
        } else if ("marcas".equals(cat)) {
            result = settings.getString("levelMarcas","1");
        } else if ("logos".equals(cat)) {
            result = settings.getString("levelLogos","1");
        } else if ("famosos".equals(cat)) {
            result = settings.getString("levelFamosos","1");
        } else if ("celebrities".equals(cat)) {
            result = settings.getString("levelCelebrities","1");
        } else if ("sombras".equals(cat)) {
            result = settings.getString("levelSombras","1");
        } else if ("shadows".equals(cat)) {
            result = settings.getString("levelShadows","1");
        }

        return result;
    }
    private String getImagePath (String categorySelected) {
        String path = "";
        if ("adivinanzas".equals(categorySelected)) {
            path = "adivinanzas";
        } else if("wuzzles".equals(categorySelected)) {
            path = "wuzzles";
        } else if("emojis".equals(categorySelected)) {
            path = "emojis";
        } else if("enojis".equals(categorySelected)) {
            path = "enojis";
        } else if("peliculas".equals(categorySelected)) {
            path = "peliculas";
        } else if("movies".equals(categorySelected)) {
            path = "movies";
        } else if ("escudos".equals(categorySelected)) {
            path = "escudos";
        } else if ("teams".equals(categorySelected)) {
            path = "escudos";
        } else if ("banderas".equals(categorySelected)) {
            path = "banderas";
        } else if ("flags".equals(categorySelected)) {
            path = "banderas";
        } else if ("marcas".equals(categorySelected)) {
            path = "marcas";
        } else if ("logos".equals(categorySelected)) {
            path = "marcas";
        } else if ("famosos".equals(categorySelected)) {
            path = "comosellama";
        } else if ("celebrities".equals(categorySelected)) {
            path = "comosellama";
        } else if ("aleatorio".equals(categorySelected)) {
            path = "aleatorio";
        } else if ("aleatorio_en".equals(categorySelected)) {
            path = "aleatorio_en";
        } else if ("sombras".equals(categorySelected)) {
            path = "shadows";
        } else if ("shadows".equals(categorySelected)) {
            path = "shadows";
        }

        return path;
    }

    public Drawable resizeImageByCategory(String categorySelected, int i) {
        Drawable backgroundLevel;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int res = getResources().getIdentifier(getImagePath(categorySelected) + i, "drawable", getPackageName());
        // cuando llega a la ultima imagen, se pasa el contador y no encuentra la imagen
        if (res ==0) {
            res = getResources().getIdentifier(getImagePath(categorySelected) + (i-1), "drawable", getPackageName());
        }
        backgroundLevel = getResources().getDrawable(res);

        Bitmap original = ((BitmapDrawable) backgroundLevel).getBitmap();

        int sizeX = Math.round(width/2);
        int sizeY = Math.round(width/2);
        // escalo
        Bitmap bitmapResized = Bitmap.createScaledBitmap(original, sizeX, sizeY, false);

        backgroundLevel = new BitmapDrawable(getResources(), bitmapResized);

        return backgroundLevel;
    }

    public Drawable resizeImageByName(String name) {
        Drawable backgroundLevel;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int res = getResources().getIdentifier(name, "drawable", getPackageName());
        backgroundLevel = getResources().getDrawable(res);

        Bitmap original = ((BitmapDrawable) backgroundLevel).getBitmap();

        int sizeX = Math.round(width/2);
        int sizeY = Math.round(width/2);
        // escalo
        Bitmap bitmapResized = Bitmap.createScaledBitmap(original, sizeX, sizeY, false);

        backgroundLevel = new BitmapDrawable(getResources(), bitmapResized);

        return backgroundLevel;
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
