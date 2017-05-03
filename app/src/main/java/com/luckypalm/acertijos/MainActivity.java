package com.luckypalm.acertijos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

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

        clover = (ImageView) findViewById(R.id.clover);

        timer = new CountDownTimer(3000, 500) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 3100 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.loader5));
                }
                if (millisUntilFinished < 2600 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.loader4));
                }
                if (millisUntilFinished < 2100 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.loader3));
                }
                if (millisUntilFinished < 1600 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.loader2));
                }
                if (millisUntilFinished < 1100 ) {
                    clover.setImageDrawable(getResources().getDrawable(R.drawable.loader1));
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
