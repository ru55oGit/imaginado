package com.ru55o.luckypalm.preguntas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class PlayActivity extends AppCompatActivity {

    private Button aleatorio;
    private Button emojis;
    private Button acertijos;
    private Button peliculas;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Boolean languageSelected;
    private Switch languageSwitch;
    private RelativeLayout footer;
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

        languageSelected = settings.getBoolean("languageSelected", true);
        languageSwitch = (Switch) findViewById(R.id.languageSwitch);
        languageSwitch.setChecked(!languageSelected);

        //attach a listener to check for changes in state
        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(languageSwitch.isChecked()){
                    editor.putBoolean("languageSelected", false);
                }else{
                    editor.putBoolean("languageSelected", true);
                }
                editor.commit();
                onResume();
            }
        });
        aleatorio = (Button) findViewById(R.id.aleatorio);
        aleatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });
        aleatorio.setTypeface(lobsterFont);
        emojis = (Button) findViewById(R.id.emojis);
        emojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!languageSwitch.isChecked()){
                    editor.putString("categorySelected","emojis");
                } else {
                    editor.putString("categorySelected","enojis");
                }                editor.commit();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 16) {
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
        }

        return result;
    }
    public Drawable getImage(String categorySelected, int i) {
        Drawable backgroundLevel;

        int res = getResources().getIdentifier(categorySelected + i, "drawable", getPackageName());
        backgroundLevel = getResources().getDrawable(res);

        return backgroundLevel;
    }

}
