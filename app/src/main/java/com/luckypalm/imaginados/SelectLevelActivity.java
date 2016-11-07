package com.luckypalm.imaginados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;

public class SelectLevelActivity extends AppCompatActivity {
    private TextView play;
    private GridLayout contenedorNiveles;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_level);

        contenedorNiveles = (GridLayout) findViewById(R.id.innerLay);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        level = settings.getString("level","1");

        /*contenedorNiveles.removeAllViews();

        for (int i = 1;i<=100;i++) {
            TextView levelCircle = new TextView(this);
            levelCircle.setGravity(Gravity.CENTER_HORIZONTAL);
            int dim = 210;
            levelCircle.setTextSize((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.margin_clock));
            levelCircle.setBackground(getResources().getDrawable(R.drawable.circle));
            levelCircle.setText(i+"");
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);

            switch ((int)Math.round(Math.sin(i))+2) {
                case 1: marginLetters.setMargins(0, 0, 140, 100);break;
                case 2: marginLetters.setMargins(0, 0, 100, 300);break;
                case 3: marginLetters.setMargins(0, 100, 190, 0);break;
                default:
                    marginLetters.setMargins(0, 0, 30, 20);break;
            }

            levelCircle.setLayoutParams(marginLetters);
            if (i <= Integer.parseInt(level)) {
                levelCircle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString("levelSelected", ((TextView) v).getText().toString());
                        editor.commit();

                        Intent intent = new Intent(SelectLevelActivity.this, SelectImagesActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                levelCircle.setAlpha(0.35f);
                levelCircle.setClickable(false);
            }
            contenedorNiveles.addView(levelCircle);
        }*/
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

    public void goToSelectImages(View v){
        editor.putString("levelSelected", ((TextView) v).getText().toString());
        editor.commit();

        Intent intent = new Intent(SelectLevelActivity.this, SelectImagesActivity.class);
        startActivity(intent);
    }

}
