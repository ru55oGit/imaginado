package com.ru55o.luckypalm.preguntas;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PlayActivity extends Activity {

    private Button jugar;
    private Button emojis;
    private Button acertijos;
    private Button peliculas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


        jugar = (Button) findViewById(R.id.jugar);
        jugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });
    }

}
