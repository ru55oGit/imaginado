package com.ru55o.luckypalm.preguntas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class SelectLevelActivity extends AppCompatActivity {
    private TextView title;
    private GridLayout contenedorNiveles;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String levelByCategory;
    private String categorySelected;
    private Boolean languageSelected;
    private ScrollView hsv;
    private RelativeLayout header, footer;
    private InputMethodManager inputMethodManager;
    private RelativeLayout frameLayout;
    boolean autoclick;
    LoadTask downloadTask;
    Typeface lobsterFont;
    private ImageView volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_level);

        contenedorNiveles = (GridLayout) findViewById(R.id.innerLay);
        hsv = (ScrollView) findViewById(R.id.hsv);
        title = (TextView) findViewById(R.id.title);
        frameLayout = (RelativeLayout) findViewById(R.id.activity_select_level);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);

        // volver
        volver = (ImageView) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                downloadTask.cancel(true);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        header = (RelativeLayout) findViewById(R.id.headerSelectLevel);
        languageSelected = settings.getBoolean("languageSelected", true);
        categorySelected = settings.getString("categorySelected", "emojis");
        // por true es English
        if(languageSelected){
            header.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            title.setText(getResources().getString(R.string.select_level_title_es));
        }else{
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            title.setText(getResources().getString(R.string.select_level_title_en));
        }

        ((ProgressBar) findViewById(R.id.loader)).setVisibility(View.VISIBLE);
        autoclick = settings.getBoolean("autoclick", false);
        // si hizo siguiente o anterior en la pantalla de guessimage, voy directo al nivel seteado
        if (autoclick) {
            Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
            startActivity(intent);
        } else {
            // Creating a new non-ui thread task to download json data
            downloadTask = new LoadTask();
            // Starting the download process
            downloadTask.execute();
        }

        levelByCategory = getLevelByCategory(categorySelected);

        contenedorNiveles.removeAllViews();

        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        title.setTypeface(lobsterFont);
    }

    public String getLevelByCategory(String cat){
        String result = "";
        if ("adivinanzas".equals(cat)) {
            result = settings.getString("levelAdivinanzas","1");
        } else if ("wuzzles".equals(cat)) {
            result = settings.getString("levelWuzzles","1");
        } else if ("emojis".equals(cat)) {
            result = settings.getString("levelEmojis","1");
        } else if ("enojis".equals(cat)) {
            result = settings.getString("levelEnojis","1");
        } else if ("peliculas".equals(cat)) {
            result = settings.getString("levelPeliculas","1");
        } else if ("movies".equals(cat)) {
            result = settings.getString("levelMovies","1");
        } else if ("escudos".equals(cat)) {
            result = settings.getString("levelEscudos","1");
        } else if ("marcas".equals(cat)) {
            result = settings.getString("levelMarcas","1");
        } else if ("banderas".equals(cat)) {
            result = settings.getString("levelBanderas","1");
        }

        return result;
    }

    // uso este metodo para escalar y comprimir la imagen grande para mostrar
    // los thumbnails
    public Drawable scaleImage(Drawable image) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap original = ((BitmapDrawable) image).getBitmap();

        int sizeX = Math.round(width/4);
        int sizeY = Math.round(width/4);
        // escalo
        Bitmap bitmapResized = Bitmap.createScaledBitmap(original, sizeX, sizeY, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // comprimo
        bitmapResized.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image = new BitmapDrawable(getResources(), compressedBitmap);

        return image;
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
        //android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
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
            String jsonLocation="";
            JSONArray jarray;
            if ("adivinanzas".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("adivinanzas.json", getBaseContext());
            } else if("wuzzles".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("wuzzles.json", getBaseContext());
            } else if("emojis".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("emojis.json", getBaseContext());
            } else if("enojis".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("enojis.json", getBaseContext());
            } else if("peliculas".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("peliculas.json", getBaseContext());
            } else if("movies".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("movies.json", getBaseContext());
            } else if ("escudos".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("escudos.json", getBaseContext());
            } else if ("marcas".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("marcas.json", getBaseContext());
            } else if ("banderas".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("banderas.json", getBaseContext());
            }
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            jarray = (JSONArray) jsonobject.getJSONArray("listado");

            count = jarray.length();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count-1;
    }
    private class LoadTask extends AsyncTask<String, TextView, Void> {
        @Override
        protected Void doInBackground(String... url) {
            try {
                for (int i = 1; i <= getLevelCount(); i++) {
                    TextView levelCircle = new TextView(getBaseContext());
                    Drawable backgroundLevel;

                    int res = getResources().getIdentifier(categorySelected + i, "drawable", getPackageName());
                    backgroundLevel = getResources().getDrawable(res);
                    if (i > Integer.parseInt(levelByCategory)) {
                        levelCircle.setAlpha(0.35f);
                    } else {
                        levelCircle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString("levelSelected", ((TextView) v).getText().toString());
                                editor.commit();
                                downloadTask.cancel(true);

                                Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    //levelCircle.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                    levelCircle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    levelCircle.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                    levelCircle.setText(i + "");
                    levelCircle.setTextColor(getResources().getColor(R.color.numberLevel));
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(contenedorNiveles.getLayoutParams());
                    params.setMargins(1, 1, 1, 1);

                    if (Build.VERSION.SDK_INT > 15) {
                        levelCircle.setBackground(scaleImage(backgroundLevel));
                    }
                    if (Build.VERSION.SDK_INT > 17) {
                        levelCircle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                    }
                    levelCircle.setTypeface(lobsterFont);
                    levelCircle.setLayoutParams(params);
                    publishProgress(levelCircle);
                }
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(TextView... levelCircle) {
            contenedorNiveles.addView(levelCircle[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            ((ProgressBar) findViewById(R.id.loader)).setVisibility(View.GONE);
        }
    }
}
