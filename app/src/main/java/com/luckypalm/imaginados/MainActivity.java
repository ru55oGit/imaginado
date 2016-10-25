package com.luckypalm.imaginados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView play;
    private SharedPreferences settings;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.luckypalm.imaginados.R.layout.activity_main);


        play = (ImageView) findViewById(com.luckypalm.imaginados.R.id.play);
        settings = getSharedPreferences("Status", 0);
        level = settings.getString("level", "1");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, SelectImagesActivity.class);
            startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
