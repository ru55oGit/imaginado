package com.imaginados.patricio.toledo.imaginados;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ObtainSecondsActivity extends AppCompatActivity {
    private Button participar;
    private Button comprar;
    private Button obtener;
    private ImageView volverajugar;
    private TextView mensaje;
    private int milisegundos;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_seconds);

        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }

        settings = getSharedPreferences("Status", 0);
    }

    @Override
    protected void onResume () {
        super.onResume();

        milisegundos = settings.getInt("time", 30000);

        mensaje = (TextView) findViewById(R.id.mensaje);
        volverajugar = (ImageView) findViewById(R.id.volverajugar);
        obtener = (Button) findViewById(R.id.obtener);
        participar = (Button) findViewById(R.id.participar);
        comprar = (Button) findViewById(R.id.comprar);

        if (milisegundos > 0) {
            mensaje.setVisibility(View.VISIBLE);
            volverajugar.setVisibility(View.VISIBLE);
            mensaje.setText("Has ganado " + milisegundos / 1000 + " segundos para seguir jugando.");
            Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
            mensaje.setTypeface(lobsterFont);
            participar.setVisibility(View.INVISIBLE);
            obtener.setVisibility(View.INVISIBLE);
            comprar.setVisibility(View.INVISIBLE);
            volverajugar.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v){
                   Intent intent = new Intent(ObtainSecondsActivity.this, SelectImagesActivity.class);
                   startActivity(intent);
               }
            });

        } else {
            participar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ObtainSecondsActivity.this, PlayForSecondsActivity.class);
                    startActivity(intent);
                }
            });
            comprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "Muy pronto podras comprar segundos", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
