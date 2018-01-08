package com.ru55o.luckypalm.acertijos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
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
    private String levelSpanish;
    private String levelEnglish;
    private Boolean languageSelected;
    private ScrollView hsv;
    private Switch mySwitch;
    private RelativeLayout header, footer;
    private InputMethodManager inputMethodManager;
    private RelativeLayout frameLayout;
    Typeface lobsterFont;
    boolean autoclick;
    LoadTask downloadTask;


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

    }

    @Override
    public void onResume() {
        super.onResume();
        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
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
        languageSelected = settings.getBoolean("languageSelected", true);
        mySwitch = (Switch) findViewById(R.id.switchy);
        mySwitch.setChecked(languageSelected);
        header = (RelativeLayout) findViewById(R.id.headerSelectLevel);
        footer = (RelativeLayout) findViewById(R.id.footerContainer);
        // por true es English
        if (!mySwitch.isChecked()) {
            header.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            footer.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            title.setText(getResources().getString(R.string.select_level_title_es));
        } else {
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            footer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            title.setText(getResources().getString(R.string.select_level_title_en));
        }

        levelSpanish = settings.getString("levelSpanish", "1");
        levelEnglish = settings.getString("levelEnglish", "1");

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySwitch.setVisibility(View.GONE);
                if (mySwitch.isChecked()) {
                    editor.putBoolean("languageSelected", true);
                    editor.commit();
                    downloadTask.cancel(true);
                    onResume();
                } else {
                    editor.putBoolean("languageSelected", false);
                    editor.commit();
                    downloadTask.cancel(true);
                    onResume();
                }
            }
        });

        contenedorNiveles.removeAllViews();

        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        title.setTypeface(lobsterFont);
    }

    // uso este metodo para escalar y comprimir la imagen grande para mostrar
    // los thumbnails
    public Drawable scaleImage(Drawable image, float scaleFactor) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap original = ((BitmapDrawable) image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);
        // escalo
        Bitmap bitmapResized = Bitmap.createScaledBitmap(original, sizeX, sizeY, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // comprimo
        bitmapResized.compress(Bitmap.CompressFormat.JPEG, 50, stream);
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
    public void onPause() {
        super.onPause();
        downloadTask.cancel(true);
        contenedorNiveles.removeAllViews();
    }

    @Override
    public void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
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
            String jsonLocation;
            JSONArray jarray;
            if (!mySwitch.isChecked()) {
                jsonLocation = AssetJSONFile("data.json", getBaseContext());
            } else {
                jsonLocation = AssetJSONFile("data_en.json", getBaseContext());
            }
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            if (!mySwitch.isChecked()) {
                jarray = (JSONArray) jsonobject.getJSONArray("palabras");
            } else {
                jarray = (JSONArray) jsonobject.getJSONArray("words");
            }

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
                    // por true es English
                    if (!mySwitch.isChecked()) {
                        int res = getResources().getIdentifier("adivinanzas" + i, "drawable", getPackageName());
                        backgroundLevel = getResources().getDrawable(res);
                        if (i > Integer.parseInt(levelSpanish)) {
                            levelCircle.setAlpha(0.35f);
                        } else {
                            levelCircle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mySwitch.setVisibility(View.GONE);
                                    editor.putString("levelSelected", ((TextView) v).getText().toString());
                                    editor.commit();
                                    downloadTask.cancel(true);

                                    Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        int res = getResources().getIdentifier("wuzzles" + i, "drawable", getPackageName());
                        backgroundLevel = getResources().getDrawable(res);
                        if (i > Integer.parseInt(levelEnglish)) {
                            levelCircle.setAlpha(0.35f);
                        } else {
                            levelCircle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mySwitch.setVisibility(View.GONE);
                                    editor.putString("levelSelected", ((TextView) v).getText().toString());
                                    editor.commit();
                                    downloadTask.cancel(true);

                                    Intent intent = new Intent(SelectLevelActivity.this, GuessImageActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    //levelCircle.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
                    levelCircle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    levelCircle.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                    levelCircle.setText(i + "");
                    levelCircle.setTextColor(getResources().getColor(R.color.numberLevel));
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(contenedorNiveles.getLayoutParams());
                    params.setMargins(1, 1, 1, 1);

                    if (Build.VERSION.SDK_INT > 15) {
                        levelCircle.setBackground(scaleImage(backgroundLevel, 0.22f));
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
            mySwitch.setVisibility(View.VISIBLE);
        }
    }
}
