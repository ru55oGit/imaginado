package com.luckypalm.acertijos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.luckypalm.acertijos.R;

public class MainActivity extends AppCompatActivity {

    private ImageView play;
    private CountDownTimer timer;
    private ImageView clover;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        editor.putBoolean("autoclick", false);
        editor.commit();


        play = (ImageView) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });
        clover = (ImageView) findViewById(R.id.clover);

        timer = new CountDownTimer(4100, 900) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 4100 && millisUntilFinished > 4000 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.clover4));
                }
                if (millisUntilFinished < 4000 && millisUntilFinished > 3000 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.clover3));
                }
                if (millisUntilFinished < 3000 && millisUntilFinished > 2000 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.clover2));
                }
                if (millisUntilFinished < 2000 && millisUntilFinished > 1000 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.clover1));
                }
            }
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
