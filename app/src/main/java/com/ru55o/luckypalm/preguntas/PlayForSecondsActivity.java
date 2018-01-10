package com.ru55o.luckypalm.preguntas;

import android.*;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.ru55o.luckypalm.preguntas.pojo.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class PlayForSecondsActivity extends AppCompatActivity implements BackDialog.BackDialogListener {
    private RelativeLayout frameLayout;
    private TextView preguntaView;
    private TextView preguntaTitle;
    private ImageView imageForPlay;
    private TextView transition;
    private Boolean timerFlag;
    private CountDownTimer showSoftKey;
    private int dim;
    private CountDownTimer timer;
    private TextView counter;
    private TextView timeGained;
    private TextView questionCircle;
    private static final String FORMAT = "%02d:%02d";
    private static final String FORMAT_TRANSITION = "%02d";
    private int milisegundos = 0;
    private ArrayList<Question> pregResp;
    private Question question;
    private int aciertos;
    private TextView letter;
    private LinearLayout firstLine;
    private LinearLayout secondLine;
    private LinearLayout ll;
    private LinearLayout ll2;
    private int gainedTime = 0;
    private int cantPreguntas = 1;
    private GradientDrawable gd;
    private ArrayList<Integer> random = new ArrayList<Integer>();
    private Typeface lobsterFont;
    private Typeface  digifont;
    InputMethodManager inputMethodManager;
    private RelativeLayout frameCounter;
    private String level;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    private Boolean languageSelected;
    private ImageView sharewsap;
    private ImageView shareTwitter;
    private ImageView shareFacebook;
    private ImageView title;
    private int res;
    private TextView sharesTitle;
    private int sharesCount;

    private AdView mAdView;
    private AdRequest adRequest;
    private RewardedVideoAd mVideoAd;
    private InterstitialAd mInterstitialAd;
    private Boolean avoidInterstitialOnShare;

    private Dialog dialogCustom;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        return permission == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play_for_seconds);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        languageSelected = settings.getBoolean("languageSelected", true);

        /**
         *  COMIENZA LA CARGA DE LOS BANNERS
         */
        // evito que se muestre el Interstitial cdo quiero compartir
        avoidInterstitialOnShare = true;
        // llevo la cuenta de los shares apagar el interstitial
        sharesCount = settings.getInt("sharesCount", 0);

        // Use an activity context to get the rewarded video instance.
        mVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                //Toast.makeText(GuessImageActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdOpened() {
                //Toast.makeText(GuessImageActivity.this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoStarted() {
                //Toast.makeText(GuessImageActivity.this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdClosed() {
                //Toast.makeText(GuessImageActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
                // Preload the next video ad.
            }
            @Override
            public void onRewarded(RewardItem rewardItem) {
                if (languageSelected.booleanValue()) {
                    Toast.makeText(PlayForSecondsActivity.this, "You have obtained " + rewardItem.getAmount() +" seconds", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlayForSecondsActivity.this, "Has obtenido " + rewardItem.getAmount() +" "+ rewardItem.getType(), Toast.LENGTH_LONG).show();
                }
                milisegundos+= rewardItem.getAmount()*1000;
                editor.putInt("time", milisegundos);
                editor.commit();
                if (dialogCustom != null) {
                    dialogCustom.dismiss();
                }
            }
            @Override
            public void onRewardedVideoAdLeftApplication() {
                //Toast.makeText(GuessImageActivity.this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdFailedToLoad(int err) {
                if (milisegundos <= 0 ) {
                    if (!languageSelected.booleanValue()) {
                        Toast.makeText(PlayForSecondsActivity.this, "Fallo la carga del video, igual obtiene 15\"" , Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PlayForSecondsActivity.this, "Video load failed, gains 15\" anyway" , Toast.LENGTH_LONG).show();
                    }

                    editor.putInt("time", 15000);
                    editor.commit();
                    finish();
                }
            }
        });

        // Banner footer
        if (settings.getBoolean("showAds", true)) {
            //Toast.makeText(GuessImageActivity.this, "showAds", Toast.LENGTH_LONG).show();
            // ADS
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        } else {
            showSoftKey = new CountDownTimer(700, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    if (Integer.parseInt(settings.getString("levelSelected", "1"))<= Integer.parseInt(level)) {
                        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            }.start();
        }

        /**
         *  TERMINA LA CARGA DE LOS BANNERS
         * */

        /**
         * COMIENZA LA CARGA DE LAS PREGUNTAS
         * */
        pregResp = languageSelected.booleanValue()? getQuestion() : obtenerPreguntas();
        for (int i=0; i<pregResp.size();i++) {
            random.add(i);
        }
        Collections.shuffle(random);
        Collections.shuffle(random);
        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);
        timeGained = (TextView) findViewById(R.id.chrono);
        questionCircle = (TextView) findViewById(R.id.questionCircle);
        questionCircle.setTypeface(lobsterFont);
        questionCircle.setText(cantPreguntas + "/10");
        questionCircle.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                onResume();
                return true;
            }
        });
        /**
         * TERMINA LA CARGA DE LAS PREGUNTAS
         * */

        /**
         *  SHARES
         */
        // share wsap
        sharewsap = (ImageView) findViewById(R.id.sharewsap);
        sharewsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppInstalled(getBaseContext(), "com.whatsapp")){
                    if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;
                        if (timer != null) {
                            timer.cancel();
                        }

                        String sharetext = !languageSelected.booleanValue()? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));
                        sharingIntent.setPackage("com.whatsapp");
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);

                        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                    }
                } else {
                    if (languageSelected.booleanValue()) {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        shareFacebook = (ImageView) findViewById(R.id.sharefacebook);
        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppInstalled(getBaseContext(), "com.facebook.katana")) {
                    if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;
                        if (timer != null) {
                            timer.cancel();
                        }
                        if (Build.VERSION.SDK_INT > 16) {
                            title.setBackground(getResources().getDrawable(R.drawable.acertijos_title));
                        }

                        Bitmap image = takeScreenshot();
                        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), 1100);
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(image)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();

                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            shareDialog.show(content);
                        }
                    }
                } else {
                    if (languageSelected.booleanValue()) {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        shareTwitter = (ImageView) findViewById(R.id.sharetwitter);
        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppInstalled(getBaseContext(), "com.twitter.android")) {
                    if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;

                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));

                        String sharetext = !languageSelected.booleanValue()? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.setType("image/png");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
                        sharingIntent.setType("text/plain");

                        sharingIntent.setPackage("com.twitter.android");
                        startActivity(sharingIntent);
                    }
                } else {
                    if (languageSelected.booleanValue()) {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toggleKeyboardVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Cargo el banner footer cada vez que se carga la pantalla
        if (settings.getBoolean("showAds", true)) {
            // ADS
            mAdView = (AdView) findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    RelativeLayout focus = (RelativeLayout) findViewById(R.id.frameCounter);
                    focus.setFocusableInTouchMode(true);
                    focus.requestFocus();
                    ImageView keyboardIcon = (ImageView) findViewById(R.id.keyboardIcon);
                    if (milisegundos > 0) {
                        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            });
        }

        sharesTitle = (TextView) findViewById(R.id.sharesTitle);
        if (sharesCount < 10) {
            if (languageSelected.booleanValue()) {
                sharesTitle.setText("Share " + (10 - sharesCount) + " time to hide ads");
            } else {
                sharesTitle.setText("Comparte " + (10 - sharesCount) + " veces y elimina las publicidades");
            }
        } else {
            if (languageSelected.booleanValue()) {
                sharesTitle.setText("Ask your friends for help");
            } else {
                sharesTitle.setText("Pide ayuda a tus amigos");
            }
        }

        // Interstitial
        if (settings.getBoolean("showAds", true) && cantPreguntas % 4 == 0 && settings.getInt("sharesCount", 0) < 10) {
            // ADS
            //MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_interstitial));
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_interstitial));

            requestNewInterstitial();
        }

        timerFlag = true;
        aciertos = 0;
        timeGained.setTypeface(digifont);

        // Instancio el contenedor de las letras
        firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        secondLine = (LinearLayout) findViewById(R.id.wordContainerSecond);
        // Limpio todas las letras para la proxima pregunta
        firstLine.removeAllViews();
        secondLine.removeAllViews();
        random.remove(random.size()-1);
        int questionNumber = random.get(random.size()-1);
        //int questionNumber = (int) (pregResp.size() - Math.random() * pregResp.size());

        question = pregResp.get(questionNumber);

        // Instancio y seteo la pregunta
        preguntaTitle = (TextView) findViewById(R.id.title);
        preguntaView = (TextView) findViewById(R.id.question);
        preguntaTitle.setText(question.getTitulo());

        imageForPlay = (ImageView) findViewById(R.id.imageForPlay);
        res = getResources().getIdentifier(question.getPregunta().replace(".jpg", ""),"drawable",getPackageName());
        if (question.getPregunta().contains("jpg")) {
            preguntaView.setVisibility(View.INVISIBLE);
            imageForPlay.setVisibility(View.VISIBLE);
            imageForPlay.setImageResource(res);
        } else {
            preguntaView.setVisibility(View.VISIBLE);
            imageForPlay.setVisibility(View.INVISIBLE);
            preguntaView.setText(question.getPregunta());
        }

        imageForPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(res);
            }
        });

        preguntaView.setTypeface(lobsterFont);
        preguntaTitle.setTypeface(lobsterFont);

        // Border radius para las letras
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int)getResources().getDimension(R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(R.dimen.border_letters_guess), getResources().getColor(R.color.secondaryColor));

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            letter = new TextView(this);
            if (Character.isWhitespace(question.getRespuesta().charAt(i))) {
                letter.setText("");
            } else {
                letter.setText("__");
                letter.setAllCaps(true);
                letter.setBackgroundResource(R.color.backLetters);
                if  (Build.VERSION.SDK_INT > 15 ) {
                    letter.setBackground(gd);
                }
            }

            letter.setGravity(Gravity.CENTER_HORIZONTAL);
            dim = (int) getResources().getDimension(R.dimen.bg_letter_size);
            letter.setTextSize((int)getResources().getDimension(R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
            letter.setLayoutParams(marginLetters);

            if (question.getRespuesta().indexOf("|") > 0) {
                if (i < question.getRespuesta().indexOf("|")) {
                    firstLine.addView(letter);
                } else {
                    if (i > question.getRespuesta().indexOf("|")) {
                        secondLine.addView(letter);
                    }
                }
            } else {
                firstLine.addView(letter);
            }
        }
    }

    public String AssetJSONFile (String filename, Context context) throws IOException {
        InputStream file = getAssets().open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    private ArrayList<Question> obtenerPreguntas () {
        ArrayList<Question> preguntaList = new ArrayList<Question>();
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("questions.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de preguntas
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("preguntas");
            for (int i=0; i<jarray.length();i++) {
                Question question = new Question();
                question.setPregunta(((JSONObject)jarray.get(i)).getString("pregunta"));
                question.setRespuesta(((JSONObject) jarray.get(i)).getString("respuesta"));
                question.setTitulo(((JSONObject) jarray.get(i)).getString("titulo"));
                preguntaList.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preguntaList;
    }

    private ArrayList<Question> getQuestion () {
        ArrayList<Question> preguntaList = new ArrayList<Question>();
        try {
            //obtengo el archivo
            String jsonLocation = AssetJSONFile("questions_en.json", getBaseContext());
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de preguntas
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("questions");
            for (int i=0; i<jarray.length();i++) {
                Question question = new Question();
                question.setPregunta(((JSONObject)jarray.get(i)).getString("question"));
                question.setRespuesta(((JSONObject) jarray.get(i)).getString("answer"));
                question.setTitulo(((JSONObject) jarray.get(i)).getString("title"));
                preguntaList.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return preguntaList;
    }

    // maneja la presion de las teclas
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){
        if (timerFlag) {
            timer(15000);
            timerFlag = false;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BackDialog bd = new BackDialog();
            bd.show(getFragmentManager(), "finnish");
            timer.cancel();
            return false;
        }

        ll = (LinearLayout)findViewById(R.id.wordContainerFirst);
        ll2 = (LinearLayout) findViewById(R.id.wordContainerSecond);
        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < question.getRespuesta().length(); i++) {
            // si viene un pipe, es que las palabras estan divididas en 2 renglones
            if (question.getRespuesta().indexOf("|") < 0) {
                // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
                if (Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                    letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(R.color.backLetters);
                    if  (Build.VERSION.SDK_INT > 15 ) {
                        letter.setBackground(gd);
                    }

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
                    letter.setLayoutParams(marginLetters);
                    ll.removeViewAt(i);
                    ll.addView(letter, i);

                    aciertos++;
                }
            }  else if ('|' != question.getRespuesta().charAt(i)) {
                // si las letras evaluadas estan antes que el pipe, evaluo en el primer renglon...si estan despues evaluo en el segundo
                if (i < question.getRespuesta().indexOf("|") && Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__") ||
                        i > question.getRespuesta().indexOf("|") && Character.toUpperCase(question.getRespuesta().charAt(i)) == event.getDisplayLabel() && ((TextView) ll2.getChildAt(i -(question.getRespuesta().indexOf("|")+1))).getText().equals("__")) {
                    TextView letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(R.color.primaryColor);
                    if  (Build.VERSION.SDK_INT > 15 ) {
                        letter.setBackground(gd);
                    }

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
                    letter.setLayoutParams(marginLetters);
                    if (i < question.getRespuesta().indexOf("|")) {
                        ll.removeViewAt(i);
                        ll.addView(letter, i);
                    } else {
                        ll2.removeViewAt(i - (question.getRespuesta().indexOf("|")+1));
                        ll2.addView(letter, i - (question.getRespuesta().indexOf("|")+1));
                    }
                    aciertos++;
                }
            }
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == question.getRespuesta().replaceAll(" ", "").replaceAll("\\|","").length()) {
            // incremento el numero de preguntas
            cantPreguntas++;
            // paro el reloj
            timer.cancel();
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            //String tiempo[] = ((String)this.counter.getText()).split(":");
            inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);
            onResume();
            /*try {
                Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
                Integer segundos = Integer.parseInt(tiempo[1])*1000;
                milisegundos+= segundos;
                if (cantPreguntas<11) {
                    Toast showSecondsGained = Toast.makeText(getBaseContext(),"+"+segundos/1000+"seg.",Toast.LENGTH_SHORT);
                    showSecondsGained.setGravity(Gravity.TOP,Gravity.CENTER, 0);
                    showSecondsGained.show();
                    questionCircle.setText(cantPreguntas + "/10");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            /*timeGained.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                    TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));*/

            // Cuando el reloj llega a cero, se cambia el mensaje
            if (cantPreguntas == 11) {
                /*counter.setVisibility(View.INVISIBLE);*/
                backToPlay(milisegundos);
            }

        }
        return true;
    }

    private void backToPlay(int milis) {
        editor.putInt("time", milisegundos);
        editor.commit();
        finish();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible() {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    /**
     *  Maneja el reloj con el tiempo que se dispone para adivinar
     *  @param milliseconds tiempo en milisegundos para setear el reloj
     *  */
    private void timer(final int milliseconds) {
        timer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                /*counter.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                counter.setVisibility(View.VISIBLE);*/
            }
            public void onFinish() {
                cantPreguntas++;
                if (cantPreguntas<11) {
                    Toast showSecondsGained = Toast.makeText(getBaseContext(),"No sumaste segundos",Toast.LENGTH_SHORT);
                    showSecondsGained.setGravity(Gravity.TOP,Gravity.CENTER, 0);
                    showSecondsGained.show();
                    questionCircle.setText(cantPreguntas + "/10");
                } else {
                    backToPlay(milisegundos);
                }
                inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);
                onResume();
            }
        }.start();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showImage(int res){
        // custom dialog
        final Dialog dialogCustom = new Dialog(PlayForSecondsActivity.this);
        dialogCustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustom.setContentView(R.layout.custom_dialog_zoomimage);

        inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);

        ImageView imgZoom = (ImageView) dialogCustom.findViewById(R.id.imageToGuessZoom);
        imgZoom.setImageResource(res);
        dialogCustom.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                // Abro el teclado cuando me quedo sin tiempo
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });

        if (dialogCustom != null) {
            dialogCustom.show();
        }
    }
    // Devuelvo un Bitmap con un screenshot
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }
    // retorno la ruta del screenshot
    public String saveBitmap(Bitmap bitmap, Boolean fullImage) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/_acertijos_"+ Math.random()*1000 +".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            if (fullImage) {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
            } else {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 1100);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
        return imagePath.toString();
    }

    // en el back abro un popup, en el aceptar termino el activity
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        backToPlay(milisegundos);
    }
    // en el back abro un popup, en el cancelar sigo con el timer
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
