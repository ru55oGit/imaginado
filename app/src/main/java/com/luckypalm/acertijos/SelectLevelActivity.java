package com.luckypalm.acertijos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class SelectLevelActivity extends AppCompatActivity {
    private TextView title;
    private GridLayout contenedorNiveles;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String level;
    private ScrollView hsv;
    private TextView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_level);

        contenedorNiveles = (GridLayout) findViewById(R.id.innerLay);
        hsv = (ScrollView) findViewById(R.id.hsv);
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        level = settings.getString("level","1");

        boolean autoclick = settings.getBoolean("autoclick", false);

        contenedorNiveles.removeAllViews();

        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        title.setTypeface(lobsterFont);

        // si hizo siguiente o anterior en la pantalla de guessimage, voy directo al nivel seteado
        if (autoclick) {
            Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
            startActivity(intent);
        } else {
            for (int i = 1;i<=getLevelCount();i++) {
                TextView levelCircle = new TextView(this);
                levelCircle.setTextSize((int)getResources().getDimension(R.dimen.select_level_fontsize));
                levelCircle.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                levelCircle.setText(i+"");
                levelCircle.setTextColor(getResources().getColor(R.color.numberLevel));
                if (Build.VERSION.SDK_INT > 15) {
                    levelCircle.setBackground(getResources().getDrawable(R.drawable.selectlevelback));
                }
                if (Build.VERSION.SDK_INT > 17) {
                    levelCircle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    levelCircle.setPadding(0,30,20,0);
                } else {
                    levelCircle.setPadding(45,30,20,0);
                }
                levelCircle.setTypeface(lobsterFont);
                levelCircle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putString("levelSelected", ((TextView) v).getText().toString());
                        editor.commit();

                        Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
                        startActivity(intent);
                    }
                });
                if (i > Integer.parseInt(level)) {
                    levelCircle.setAlpha(0.35f);
                }
                contenedorNiveles.addView(levelCircle);
            }
            footer = (TextView) findViewById(R.id.footer);
            footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
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

    public void goToSelectImages(View v){
        editor.putString("levelSelected", ((TextView) v).getText().toString());
        editor.commit();

        Intent intent = new Intent(SelectLevelActivity.this, SelectImagesActivity.class);
        startActivity(intent);
    }

    public String AssetJSONFile (String filename, Context context) throws IOException {
        InputStream file = getAssets().open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }
    // Retorno la cantidad de niveles que tengo en el juego (es -1 porque la primer posicion es cero)
    private int getLevelCount() {
        int count = 0;
        try {
            String jsonLocation = AssetJSONFile("data.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("palabras");
            count = jarray.length();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count-1;
    }
}
