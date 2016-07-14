package com.imaginados.patricio.toledo.imaginados;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ObtainSecondsActivity extends AppCompatActivity {
    private Button participar;
    private Button comprar;
    private TextView mensaje;
    private int milisegundos;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_seconds);

        settings = getSharedPreferences("Status", 0);
    }

    @Override
    protected void onResume () {
        super.onResume();

        milisegundos = settings.getInt("time", 30000);

        mensaje = (TextView) findViewById(R.id.mensaje);
        participar = (Button) findViewById(R.id.participar);
        comprar = (Button) findViewById(R.id.comprar);

        if (milisegundos > 0) {
            mensaje.setVisibility(View.VISIBLE);
            participar.setVisibility(View.INVISIBLE);
            comprar.setVisibility(View.INVISIBLE);
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
                    Toast.makeText(getBaseContext(),"Muy pronto podras comprar segundos",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
