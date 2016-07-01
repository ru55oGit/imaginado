package com.imaginados.patricio.toledo.imaginados;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ObtainSecondsActivity extends AppCompatActivity {
    Button participar;
    Button comprar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtain_seconds);

        participar = (Button) findViewById(R.id.participar);

        participar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ObtainSecondsActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });

        comprar = (Button) findViewById(R.id.comprar);

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Muy pronto podras comprar segundos",Toast.LENGTH_LONG).show();
            }
        });
    }
}
