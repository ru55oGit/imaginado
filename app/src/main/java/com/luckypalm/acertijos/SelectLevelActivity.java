package com.luckypalm.acertijos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

        contenedorNiveles.removeAllViews();

        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        title.setTypeface(lobsterFont);

        for (int i = 1;i<=116;i++) {
            TextView levelCircle = new TextView(this);
            levelCircle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            levelCircle.setTextSize((int)getResources().getDimension(R.dimen.select_level_fontsize));
            levelCircle.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
            levelCircle.setText(i+"");
            levelCircle.setTextColor(getResources().getColor(R.color.numberLevel));
            levelCircle.setBackground(getResources().getDrawable(R.drawable.selectlevelback));
            levelCircle.setTypeface(lobsterFont);
            levelCircle.setPadding(0,30,20,0);

            levelCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putString("levelSelected", ((TextView) v).getText().toString());
                    editor.commit();

                    Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
                    intent.putExtra("src", "adivinanzas" + ((TextView) v).getText().toString());
                    intent.putExtra("word", getWord("adivinanzas", ((TextView) v).getText().toString()));
                    startActivity(intent);
                }
            });
            if (i <= Integer.parseInt(level)) {

            } else {
                levelCircle.setAlpha(0.35f);
            }
            contenedorNiveles.addView(levelCircle);
        }
        //hsv.scrollTo(0, Integer.parseInt(level)*50);
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

    private int getSrcByLevel (String level, String category) {
        String uri = category + level;
        int res = getResources().getIdentifier(uri, "drawable", getPackageName());
        return res;
    }

    public String AssetJSONFile (String filename, Context context) throws IOException {
        InputStream file = getAssets().open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    private String getWord (String categoria, String level) {
        String word = "";
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("data.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("palabras");
            //obtengo el nivel
            JSONObject nivel = (JSONObject)jarray.get(Integer.parseInt(level));
            //obtengo la palabra del nivel correspondiente, segun la categoria elegida
            word = nivel.getString(categoria);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

}
