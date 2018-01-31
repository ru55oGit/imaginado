package com.ru55o.luckypalm.preguntas;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private ImageView volver;

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
    private Toast toastLose;
    private Toast toastWin;
    private int secondsToSubtract;

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

        // volver
        volver = (ImageView) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);
                closeAndSave();
            }
        });

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
                //Toast.makeText(PlayForSecondsActivity.this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdOpened() {
                //Toast.makeText(PlayForSecondsActivity.this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoStarted() {
                //Toast.makeText(PlayForSecondsActivity.this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdClosed() {
                //Toast.makeText(PlayForSecondsActivity.this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
                // Preload the next video ad.
            }
            @Override
            public void onRewarded(RewardItem rewardItem) {
                if (languageSelected) {
                    Toast.makeText(PlayForSecondsActivity.this, "Has obtenido " + rewardItem.getAmount() +" "+ rewardItem.getType(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlayForSecondsActivity.this, "You have obtained " + rewardItem.getAmount() +" seconds", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(PlayForSecondsActivity.this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRewardedVideoAdFailedToLoad(int err) {
                if (milisegundos <= 0 ) {
                    if (languageSelected) {
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
            //Toast.makeText(PlayForSecondsActivity.this, "showAds", Toast.LENGTH_LONG).show();
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
        pregResp = !languageSelected? getQuestion() : obtenerPreguntas();
        for (int i=0; i<pregResp.size();i++) {
            random.add(i);
        }
        Collections.shuffle(random);
        Collections.shuffle(random);
        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);

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
                        volver.setVisibility(View.INVISIBLE);
                        String sharetext = languageSelected? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));
                        sharingIntent.setPackage("com.whatsapp");
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);

                        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                    }
                } else {
                    if (languageSelected) {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
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
                        volver.setVisibility(View.INVISIBLE);
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
                    if (languageSelected) {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
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
                        volver.setVisibility(View.INVISIBLE);
                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));

                        String sharetext = languageSelected? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.setType("image/png");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
                        sharingIntent.setType("text/plain");

                        sharingIntent.setPackage("com.twitter.android");
                        startActivity(sharingIntent);
                    }
                } else {
                    if (languageSelected) {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toggleKeyboardVisible();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        editor.putInt("sharesCount", sharesCount);
        editor.commit();

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        milisegundos = settings.getInt("time", 120000);
        counter = (TextView) findViewById(R.id.chrono);
        counter.setTypeface(digifont);
        counter.setText(""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));
        counter.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                onResume();
                return true;
            }
        });

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
                    keyboardIcon.setVisibility(View.INVISIBLE);
                    if (milisegundos > 0) {
                        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            });
        }

        sharesTitle = (TextView) findViewById(R.id.sharesTitle);
        if (sharesCount < 10) {
            if (languageSelected) {
                sharesTitle.setText("Comparte " + (10 - sharesCount) + " veces y elimina las publicidades");
            } else {
                sharesTitle.setText("Share " + (10 - sharesCount) + " time to hide ads");
            }
        } else {
            if (languageSelected) {
                sharesTitle.setText("Pide ayuda a tus amigos");
            } else {
                sharesTitle.setText("Ask your friends for help");
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
        secondsToSubtract = 0;
        aciertos = 0;
        counter.setTypeface(digifont);

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

        // si no hay segundos, abro el popup sin tiempo
        if (milisegundos == 0) {
            customDialog();
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
        // arranco el timer cuando arriesga la primer tecla
        if (timerFlag) {
            milisegundos = settings.getInt("time", 120000);
            timer(milisegundos);
            timerFlag = false;
        }
        // si estaba activo el Toast de miss lo cierro
        if (toastLose != null){
            toastLose.cancel();
        }
        // si toca el back cierro y salvo
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeAndSave();
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
        int errores = 0;
        for (int i = 0;i < question.getRespuesta().length(); i++) {
            if (Character.toUpperCase(question.getRespuesta().charAt(i))!=event.getDisplayLabel()) {
                errores++;
            }
        }
        // si ingreso un caracter que no esta en la/s palabra/s
        if (errores == question.getRespuesta().length()) {
            // paro el reloj
            timer.cancel();
            // en el primer error le descuento 1 segundo, por cada error subsiguiente le descuento cantidad de errores x 1 seg
            secondsToSubtract++;
            // obtengo los segundos que habia y le resto 1 segundo
            String tiempo[] = ((String) this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0]) * 60 * 1000;
            Integer segundos = Integer.parseInt(tiempo[1]) * 1000 >= secondsToSubtract * 1000 ? Integer.parseInt(tiempo[1]) * 1000 - secondsToSubtract * 1000 : 0;
            milisegundos = minutos + segundos;
            timer(milisegundos);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_lose,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            if (languageSelected) {
                text.setText("Fallaste. -" + secondsToSubtract + " Segundos");
            } else {
                text.setText("You failed. -" + secondsToSubtract + " Secs");
            }
            text.setTypeface(lobsterFont);

            toastLose = new Toast(getApplicationContext());
            toastLose.setGravity(Gravity.TOP, 0, (int) getResources().getDimension(R.dimen.top_toast));
            toastLose.setDuration(Toast.LENGTH_SHORT);
            toastLose.setView(layout);
            toastLose.show();
        }
        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == question.getRespuesta().replaceAll(" ", "").replaceAll("\\|","").length()) {
            // paro el reloj
            timer.cancel();
            // Cierro el teclado
            inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000;
            milisegundos = minutos + segundos;
            // a los segundos restantes, por haber acertado la palabra, le sumo tantos segundos como letras tenga la misma como bonus
            milisegundos+= 10000;
            // muestro la cantidad de segundos obtenidos
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_win, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            if (languageSelected) {
                text.setText(getResources().getString(R.string.toast_win_es));
            } else {
                text.setText(getResources().getString(R.string.toast_win_en));
            }
            text.setTypeface(lobsterFont);

            toastWin = new Toast(getApplicationContext());
            toastWin.setGravity(Gravity.TOP, 0, (int)getResources().getDimension(R.dimen.top_toast));
            toastWin.setDuration(Toast.LENGTH_SHORT);
            toastWin.setView(layout);
            toastWin.show();

            // guardo los segundos totales para ser usados en la proxima palabra
            settings = getSharedPreferences("Status", 0);

            editor.putInt("time", milisegundos);

            editor.commit();
            counter.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                    TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));
            this.onResume();
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
                if (aciertos != question.getRespuesta().replaceAll(" ", "").replaceAll("\\|","").length()) {
                    if (!("00:00").equals(counter.getText())) {
                        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    } else {
                        customDialog();
                    }
                }
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
                counter.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                counter.setVisibility(View.VISIBLE);
            }
            public void onFinish() {
                // Cuando el reloj llega a cero, se cambia el mensaje
                counter.setText("00:00");
                // Cierro el teclado cuando me quedo sin tiempo
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                milisegundos = 0;
                settings = getSharedPreferences("Status", 0);
                editor = settings.edit();
                editor.putInt("time", 0);
                editor.commit();
                customDialog();
            }
        }.start();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    private void customDialog() {
        loadRewardedVideoAd();
        // custom dialog
        dialogCustom = new Dialog(PlayForSecondsActivity.this);
        dialogCustom.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustom.setContentView(R.layout.custom_dialog_withoutseconds);

        LinearLayout buyContainer, winContainer, watchContainer, shareContainer, shareContainerTitle, secondSeparator, thirdSeparator;
        ImageView ganar, comprar, vervideo, shareFace, shareTwit, shareWsap;
        TextView titleText, buyText, keepplayingText, watchvideoText, shareText;

        buyContainer = (LinearLayout) dialogCustom.findViewById(R.id.buyContainer);
        winContainer = (LinearLayout) dialogCustom.findViewById(R.id.winContainer);
        watchContainer = (LinearLayout) dialogCustom.findViewById(R.id.watchContainer);
        shareContainer = (LinearLayout) dialogCustom.findViewById(R.id.shareContainer);
        shareContainerTitle = (LinearLayout) dialogCustom.findViewById(R.id.shareContainerTitle);

        ganar = (ImageView) dialogCustom.findViewById(R.id.ganarSegundos);
        comprar = (ImageView) dialogCustom.findViewById(R.id.comprarSegundos);
        vervideo = (ImageView) dialogCustom.findViewById(R.id.watchVideo);
        shareFace = (ImageView) dialogCustom.findViewById(R.id.sharefacebookDialog);
        shareTwit = (ImageView) dialogCustom.findViewById(R.id.sharetwitterDialog);
        shareWsap = (ImageView) dialogCustom.findViewById(R.id.sharewsapDialog);

        titleText = (TextView) dialogCustom.findViewById(R.id.titleText);
        buyText = (TextView) dialogCustom.findViewById(R.id.buyText);
        keepplayingText = (TextView) dialogCustom.findViewById(R.id.keepplayingText);
        watchvideoText = (TextView) dialogCustom.findViewById(R.id.watchvideoText);
        shareText = (TextView) dialogCustom.findViewById(R.id.shareText);

        // Cambio los textos y los colores segun el idioma
        if (!languageSelected) {
            buyContainer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
            winContainer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
            watchContainer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
            shareContainer.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));
            shareContainerTitle.setBackgroundColor(getResources().getColor(R.color.backgroundSpanish));

            titleText.setText(getResources().getText(R.string.sin_tiempo_title_en));
            buyText.setText(getResources().getText(R.string.sin_tiempo_comprar_en));
            keepplayingText.setText(getResources().getText(R.string.sin_tiempo_jugar_en));
            watchvideoText.setText(getResources().getText(R.string.sin_tiempo_vervideo_en));
            shareText.setText(getResources().getText(R.string.sin_tiempo_compartir_en));
        }

        if (haveNetworkConnection()) {
            secondSeparator = (LinearLayout) dialogCustom.findViewById(R.id.secondSeparator);
            secondSeparator.setVisibility(View.GONE);
            keepplayingText.setVisibility(View.GONE);
            ganar.setVisibility(View.GONE);
            winContainer.setVisibility(View.GONE);
        } else {
            thirdSeparator = (LinearLayout) dialogCustom.findViewById(R.id.thirdSeparator);
            thirdSeparator.setVisibility(View.GONE);
            watchvideoText.setVisibility(View.GONE);
            vervideo.setVisibility(View.GONE);
            watchContainer.setVisibility(View.GONE);

            buyText.setVisibility(View.GONE);
            comprar.setVisibility(View.GONE);
            buyContainer.setVisibility(View.GONE);
        }

        shareFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.facebook.katana")){
                        avoidInterstitialOnShare = false;
                        String sharedDescription = languageSelected? getResources().getString(R.string.generic_share_text) : getResources().getString(R.string.generic_share_text_en);
                        String sharedTitle = getResources().getString(R.string.title_share_text_en);
                        String sharedImage = languageSelected? "https://lh3.googleusercontent.com/WjHSbuxdCfYAIjrvq3aZI9LxSeysMZ6oQPBCnJ6I2WpjCQdBn2iiiPo0u7moJrAEYCc=h900-rw":"https://lh3.googleusercontent.com/pPkfzgA9TVDFEUnZ9qfdkiTI1WVqNeZdgG1-nG2ZB1WnBcwXFDEUAiw1j4ODR7nujmw=h900-rw";
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(sharedTitle)
                                    .setContentDescription(sharedDescription)
                                    .setContentUrl(Uri.parse("https://goo.gl/CrnO9M"))
                                    .setImageUrl(Uri.parse(sharedImage))
                                    .build();

                            shareDialog.show(shareLinkContent);

                            showSoftKey = new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                }
                                public void onFinish() {
                                    milisegundos+= 30000;
                                    editor.putInt("time", milisegundos);
                                    editor.commit();
                                }
                            }.start();
                        }
                    } else {
                        if (languageSelected) {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        shareTwit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.twitter.android")){
                        avoidInterstitialOnShare = false;
                        Uri screenshotUri = languageSelected? Uri.parse("android.resource://com.ru55o.luckypalm.acertijos/drawable/sharetwitterimage"):Uri.parse("android.resource://com.ru55o.luckypalm.acertijos/drawable/sharetwitterimageen");
                        String shareText = languageSelected? getResources().getString(R.string.generic_share_text) + " https://goo.gl/CrnO9M" : getResources().getString(R.string.generic_share_text_en) + " https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.setType("image/png");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        sharingIntent.setPackage("com.twitter.android");
                        startActivity(sharingIntent);

                        showSoftKey = new CountDownTimer(3000, 1000) {
                            public void onTick(long millisUntilFinished) {

                            }
                            public void onFinish() {
                                milisegundos+= 30000;
                                editor.putInt("time", milisegundos);
                                editor.commit();
                            }
                        }.start();
                    } else {
                        if (languageSelected) {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        shareWsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(PlayForSecondsActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.whatsapp")){
                        avoidInterstitialOnShare = false;
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Bitmap largeIcon;
                        if (languageSelected) {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sharetwitterimageen);
                        } else {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sharetwitterimage);
                        }
                        Uri screenshotUri = Uri.parse(saveBitmap(largeIcon, true));
                        String shareText = languageSelected? getResources().getString(R.string.generic_share_text) + " https://goo.gl/CrnO9M": getResources().getString(R.string.generic_share_text_en) + " https://goo.gl/CrnO9M";
                        sharingIntent.setPackage("com.whatsapp");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.setType("image/*");
                        startActivity(sharingIntent);

                        showSoftKey = new CountDownTimer(3000, 1000) {
                            public void onTick(long millisUntilFinished) {

                            }
                            public void onFinish() {
                                milisegundos+= 30000;
                                editor.putInt("time", milisegundos);
                                editor.commit();
                            }
                        }.start();
                    } else {
                        if (languageSelected) {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        ganar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayForSecondsActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayForSecondsActivity.this, BuySecondsActivity.class);
                startActivity(intent);
            }
        });

        vervideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sino se cargo el video lanzo un timer hasta que se cargue
                if(mVideoAd.isLoaded()) {
                    mVideoAd.show();
                } else {
                    ((ProgressBar) findViewById(R.id.loader)).setVisibility(View.VISIBLE);
                    showSoftKey =  new CountDownTimer(10000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if (dialogCustom != null) {
                                dialogCustom.dismiss();
                            }
                            if (mVideoAd.isLoaded()) {
                                showSoftKey.cancel();
                                mVideoAd.show();
                                ((ProgressBar) findViewById(R.id.loader)).setVisibility(View.INVISIBLE);
                            }
                        }
                        public void onFinish() {
                            if(!(PlayForSecondsActivity.this).isFinishing()){
                                dialogCustom.show();
                            }
                        }
                    }.start();
                }
            }
        });

        ImageButton dialogButton = (ImageButton) dialogCustom.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCustom.dismiss();
            }
        });

        dialogCustom.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                // Cierro el teclado cuando me quedo sin tiempo
                inputMethodManager.hideSoftInputFromWindow(frameLayout.getWindowToken(), 0);
            }
        });

        if (dialogCustom != null) {
            dialogCustom.show();
        }
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
            volver.setVisibility(View.VISIBLE);
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

    @Override
    public void onPause() {
        if (settings.getBoolean("showAds", true) && mVideoAd != null) {
            mVideoAd.pause(this);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (settings.getBoolean("showAds", true) && mVideoAd != null) {
            mVideoAd.destroy(this);
        }
        super.onDestroy();
    }

    private void closeAndSave() {
        if (timer != null) {
            timer.cancel();
        }
        editor.putBoolean("autoclick", false);
        if (aciertos != question.getRespuesta().replaceAll(" ", "").replaceAll("\\|","").length() && !"00:00".equalsIgnoreCase(this.counter.getText().toString())) {
            // obtengo la cantidad de segundos restantes y los convierto en milisegundos
            String tiempo[] = ((String)this.counter.getText()).split(":");
            try {
                Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
                Integer segundos = Integer.parseInt(tiempo[1]) * 1000;
                milisegundos = minutos + segundos;
                // guardo los segundos totales para ser usados en la proxima palabra
                settings = getSharedPreferences("Status", 0);
                editor.putInt("time", milisegundos);

                if (editor.commit()) {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (editor.commit()) {
                finish();
            }
        }
    }

    private void  loadRewardedVideoAd () {
        mVideoAd.loadAd(getResources().getString(R.string.banner_ad_unit_video), new AdRequest.Builder().build());
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
