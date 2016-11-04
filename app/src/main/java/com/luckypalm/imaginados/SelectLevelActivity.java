package com.luckypalm.imaginados;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SelectLevelActivity extends AppCompatActivity {
    private TextView play;
    private LinearLayout contenedorNiveles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_level);

        /*play = (TextView) findViewById(com.luckypalm.imaginados.R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this, SelectImagesActivity.class);
                startActivity(intent);
            }
        });*/

        contenedorNiveles = (LinearLayout) findViewById(R.id.innerLay);

        for (int i = 1;i<=20;i++) {
            TextView letter = new TextView(this);
            letter.setGravity(Gravity.CENTER_HORIZONTAL);
            int dim = 210;
            letter.setTextSize((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.margin_clock));
            letter.setBackground(getResources().getDrawable(R.drawable.circle));
            letter.setText(i+"");
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            if (i%2 == 0) {
                marginLetters.setMargins(0, 25, 30, 0);
            } else {
                marginLetters.setMargins(0, 0, 30, 25);
            }

            letter.setLayoutParams(marginLetters);

            contenedorNiveles.addView(letter);
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        // arranco el timer cuando arriesga la primer tecla
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();

    }

}
