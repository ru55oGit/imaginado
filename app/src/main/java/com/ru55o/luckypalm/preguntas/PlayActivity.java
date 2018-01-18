package com.ru55o.luckypalm.preguntas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
        languageSwitch.setChecked(languageSelected);



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
                Intent intent = new Intent(PlayActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });
        emojis.setTypeface(lobsterFont);
    }

}
