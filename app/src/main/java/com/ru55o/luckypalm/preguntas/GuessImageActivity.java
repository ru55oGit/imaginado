package com.ru55o.luckypalm.preguntas;

import android.Manifest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class GuessImageActivity extends AppCompatActivity implements BackDialog.BackDialogListener{
    private RelativeLayout frameLayout;
    private ImageView imageToGuess;
    // Counters variables
    private CountDownTimer timer;
    private Boolean timerFlag;
    private CountDownTimer showSoftKey;
    private TextView counter;
    private TextView labelLevelText;
    private static final String FORMAT = "%02d:%02d";
    private int milisegundos;
    private int secondsToSubtract;

    private String word;
    private int dim;
    private int aciertos = 0;
    private String uri;
    private String categorySelected;
    private String levelSelected;
    private String level;
    private GradientDrawable gd;
    private Typeface digifont;
    private Typeface lobsterFont;
    private Toast toastLose;
    private Toast toastWin;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private InputMethodManager inputMethodManager;
    private ImageView sharewsap;
    private ImageView shareTwitter;
    private ImageView shareFacebook;
    private ImageView volver;
    private ImageView title;
    private int res;
    private TextView sharesTitle;
    private int sharesCount;

    private LinearLayout firstLine;
    private LinearLayout secondLine;
    private RelativeLayout frameCounter;

    private ImageView leftArrow;
    private ImageView rightArrow;
    private Boolean languageSelected;

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
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static Boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
        setContentView(R.layout.activity_guess_image);

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
                if (!languageSelected) {
                    Toast.makeText(GuessImageActivity.this, "You have obtained " + rewardItem.getAmount() +" seconds", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GuessImageActivity.this, "Has obtenido " + rewardItem.getAmount() +" "+ rewardItem.getType(), Toast.LENGTH_LONG).show();
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
                    if (!languageSelected) {
                        Toast.makeText(GuessImageActivity.this, "Fallo la carga del video, igual obtiene 15\"" , Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GuessImageActivity.this, "Video load failed, gains 15\" anyway" , Toast.LENGTH_LONG).show();
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
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }.start();
        }

        // share wsap
        sharewsap = (ImageView) findViewById(R.id.sharewsap);
        sharewsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppInstalled(getBaseContext(), "com.whatsapp")){
                    if (verifyStoragePermissions(GuessImageActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;
                        if (timer != null) {
                            timer.cancel();
                        }
                        volver.setVisibility(View.INVISIBLE);
                        labelLevelText.setVisibility(View.VISIBLE);

                        String sharetext = !languageSelected? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));
                        sharingIntent.setPackage("com.whatsapp");
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);

                        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                    }
                } else {
                    if (!languageSelected) {
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
                    if (verifyStoragePermissions(GuessImageActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;
                        if (timer != null) {
                            timer.cancel();
                        }
                        if (Build.VERSION.SDK_INT > 16) {
                            title.setBackground(getResources().getDrawable(R.drawable.acertijos_title));
                        }
                        volver.setVisibility(View.INVISIBLE);
                        labelLevelText.setVisibility(View.VISIBLE);

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
                    if (!languageSelected) {
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
                    if (verifyStoragePermissions(GuessImageActivity.this)) {
                        avoidInterstitialOnShare = false;
                        sharesCount++;
                        volver.setVisibility(View.INVISIBLE);
                        labelLevelText.setVisibility(View.VISIBLE);

                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot(), false));

                        String sharetext = !languageSelected? getResources().getString(R.string.generic_share_text) + " Descifralo: https://goo.gl/CrnO9M":getResources().getString(R.string.generic_share_text_en) + " Descifralo: https://goo.gl/CrnO9M";

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                        sharingIntent.setType("image/png");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
                        sharingIntent.setType("text/plain");

                        sharingIntent.setPackage("com.twitter.android");
                        startActivity(sharingIntent);
                    }
                } else {
                    if (!languageSelected) {
                        Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
        if (settings.getBoolean("showAds", true) && mVideoAd != null) {
            mVideoAd.resume(this);
        }

        super.onResume();

        // llevo la cuenta de los shares apagar el interstitial
        sharesCount = settings.getInt("sharesCount", 0);

        secondsToSubtract = 0;

        if (toastWin != null) {
            toastWin.cancel();
        }
        timerFlag = true;

        // seteo el tiempo que tengo para jugar en el reloj
        milisegundos = settings.getInt("time", 120000);
        counter = (TextView) findViewById(R.id.counterText);
        counter.setTypeface(digifont);
        counter.setText(""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));

        labelLevelText = (TextView) findViewById(R.id.labelLevelText);
        frameLayout = (RelativeLayout) findViewById(R.id.frameCounter);
        title = (ImageView) findViewById(R.id.title);
        frameCounter = (RelativeLayout) findViewById(R.id.frameCounter);

        if (!languageSelected && Build.VERSION.SDK_INT > 16) {
            frameCounter.setBackground(getResources().getDrawable(R.drawable.tile_en));
            title.setBackground(getResources().getDrawable(R.drawable.acertijos_title_en));
        }

        // traigo el Nivel
        //level = !languageSelected ? settings.getString("levelEnglish","1") : settings.getString("levelSpanish","1");
        categorySelected = settings.getString("categorySelected","emojis");
        levelSelected = settings.getString("levelSelected","1");
        level = getLevelByCategory(categorySelected);

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
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            });
        }

        sharesTitle = (TextView) findViewById(R.id.sharesTitle);
        if (sharesCount < 10) {
            if (!languageSelected) {
                sharesTitle.setText("Share " + (10 - sharesCount) + " time to hide ads");
            } else {
                sharesTitle.setText("Comparte " + (10 - sharesCount) + " veces y elimina las publicidades");
            }
        } else {
            if (!languageSelected) {
                sharesTitle.setText("Ask your friends for help");
            } else {
                sharesTitle.setText("Pide ayuda a tus amigos");
            }
        }

        // Interstitial
        if (settings.getBoolean("showAds", true) && Integer.parseInt(levelSelected) % 4 == 0 && settings.getInt("sharesCount", 0) < 10) {
            // ADS
            //MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_interstitial));
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_interstitial));

            requestNewInterstitial();
        }

        toggleKeyboardVisible();


        // border radius
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int) getResources().getDimension(R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(R.dimen.border_letters_guess), getResources().getColor(R.color.secondaryColor));


        labelLevelText.setText(!languageSelected? "Level " + levelSelected: "Nivel " + levelSelected);
        labelLevelText.setTypeface(lobsterFont);
        Bundle extras = getIntent().getExtras();
        // Traigo la imagen que se eligio para adivinar
        uri = categorySelected + levelSelected;
        res = getResources().getIdentifier(uri, "drawable", getPackageName());
        // seteo la imagen en el imageview
        imageToGuess = (ImageView) findViewById(R.id.imageToGuess);
        imageToGuess.setImageResource(res);
        // Zoom de la imagen a adivinar
        imageToGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no se abre si no hay tiempo o si el nivel seleccionado es mayor al nivel jugado
                if (!("00:00").equals(counter.getText())) {
                    showImage(res);
                }
            }
        });
        // obtengo la palabra que se va adivinar segun el idioma seleccionado
        word = getWordByCategory();

        // volver
        volver = (ImageView) findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);
                closeAndSave();
            }
        });

        firstLine = (LinearLayout)findViewById(R.id.wordContainerFirst);
        secondLine = (LinearLayout)findViewById(R.id.wordContainerSecond);
        // Remuevo todas la letras porque se apendean cuando hago compartir y cancelo
        firstLine.removeAllViews();
        secondLine.removeAllViews();

        LinearLayout thirdLine = (LinearLayout)findViewById(R.id.wordContainerThird);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < word.length(); i++) {
            TextView letter = new TextView(this);
            if (Character.isWhitespace(word.charAt(i))) {
                letter.setText("");
                dim = (int) getResources().getDimension(R.dimen.letter_size_whitespace);
            } else if ('|' != word.charAt(i)){
                letter.setText("__");
                letter.setAllCaps(true);
                if (Build.VERSION.SDK_INT > 16) {
                    letter.setBackground(gd);
                } else {
                    //letter.setBackgroundResource();
                }
                dim = (int) getResources().getDimension(R.dimen.bg_letter_size);
            }
            letter.setGravity(Gravity.CENTER_HORIZONTAL);

            letter.setTextSize((int)getResources().getDimension(R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
            letter.setLayoutParams(marginLetters);
            // uso el pipe para mandar al segundo renglon palabras cuando
            // la cantidad de las mismas superan el ancho de la pantalla
            if (word.indexOf("|") > 0) {
                if (i < word.indexOf("|")) {
                    firstLine.addView(letter);
                } else {
                    if (i > word.indexOf("|")) {
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

        leftArrow = (ImageView) findViewById(R.id.leftarrow);
        leftArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToNextOrPrevious("previous");
                finish();
            }
        });

        rightArrow = (ImageView) findViewById(R.id.rightarrow);
        rightArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                moveToNextOrPrevious("next");
                finish();
            }
        });
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
        }

        return result;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancelo el custom toastLose cdo salgo
        if (toastLose != null) {
            toastLose.cancel();
        }

        if (settings.getBoolean("showAds", true) && Integer.parseInt(levelSelected) % 4 == 0 && mInterstitialAd != null && mInterstitialAd.isLoaded() && avoidInterstitialOnShare && settings.getInt("sharesCount", 0) < 10){
            mInterstitialAd.show();
        }

        editor.putInt("sharesCount", sharesCount);
        editor.commit();

        imageToGuess = null;

        finish();
    }

    private void closeAndSave() {
        if (timer != null) {
            timer.cancel();
        }
        editor.putBoolean("autoclick", false);
        if (aciertos != word.replaceAll(" ", "").replaceAll("\\|","").length() && !"00:00".equalsIgnoreCase(this.counter.getText().toString())) {
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

        LinearLayout ll = (LinearLayout)findViewById(R.id.wordContainerFirst);
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.wordContainerSecond);

        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < word.length(); i++) {
            // si viene un pipe, es que las palabras estan divididas en 2 renglones
            if (word.indexOf("|") < 0) {
                // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
                if (Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                    TextView letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(R.color.primaryColor);
                    if (Build.VERSION.SDK_INT > 15)
                        letter.setBackground(gd);

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
                    letter.setLayoutParams(marginLetters);
                    ll.removeViewAt(i);
                    ll.addView(letter, i);

                    aciertos++;
                }
            } else if ('|' != word.charAt(i)) {
                // si las letras evaluadas estan antes que el pipe, evaluo en el primer renglon...si estan despues evaluo en el segundo
                if (i < word.indexOf("|") && Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__") ||
                        i > word.indexOf("|") && Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll2.getChildAt(i -(word.indexOf("|")+1))).getText().equals("__")) {
                    TextView letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(R.color.primaryColor);
                    if (Build.VERSION.SDK_INT > 15)
                        letter.setBackground(gd);

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins((int)getResources().getDimension(R.dimen.margin_right_play), 0, (int)getResources().getDimension(R.dimen.margin_right_play), 0);
                    letter.setLayoutParams(marginLetters);
                    if (i < word.indexOf("|")) {
                        ll.removeViewAt(i);
                        ll.addView(letter, i);
                    } else {
                        ll2.removeViewAt(i - (word.indexOf("|")+1));
                        ll2.addView(letter, i - (word.indexOf("|")+1));
                    }
                    aciertos++;
                }
            }
        }
        int errores = 0;
        for (int i = 0;i < word.length(); i++) {
            if (Character.toUpperCase(word.charAt(i))!=event.getDisplayLabel()) {
                errores++;
            }
        }
        // si ingreso un caracter que no esta en la/s palabra/s
        if (errores == word.length()) {
            // paro el reloj
            timer.cancel();
            // en el primer error le descuento 1 segundo, por cada error subsiguiente le descuento cantidad de errores x 1 seg
            secondsToSubtract++;
            // obtengo los segundos que habia y le resto 1 segundo
            String tiempo[] = ((String)this.counter.getText()).split(":");
            Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
            Integer segundos = Integer.parseInt(tiempo[1])*1000>=secondsToSubtract*1000? Integer.parseInt(tiempo[1])*1000-secondsToSubtract*1000 : 0;
            milisegundos = minutos + segundos;
            timer(milisegundos);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_lose,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            if (!languageSelected) {
                text.setText("You failed. -"+secondsToSubtract+" Secs");
            } else {
                text.setText("Fallaste. -"+secondsToSubtract+" Segundos");
            }
            text.setTypeface(lobsterFont);

            toastLose = new Toast(getApplicationContext());
            toastLose.setGravity(Gravity.TOP, 0, (int)getResources().getDimension(R.dimen.top_toast));
            toastLose.setDuration(Toast.LENGTH_SHORT);
            toastLose.setView(layout);
            toastLose.show();
        }

        // si la cantidad de aciertos es igual a la cantidad de letras de la palabra
        if (aciertos == word.replaceAll(" ", "").replaceAll("\\|","").length()) {
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
            if (!languageSelected) {
                text.setText(getResources().getString(R.string.toast_win_en));
            } else {
                text.setText(getResources().getString(R.string.toast_win_es));
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
            if (Integer.parseInt(level) == Integer.parseInt(levelSelected)){
                saveStateOfLevel();
            }
            editor.commit();
            counter.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                    TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));
            // si es el primer nivel, no muestro la flechita de volver para atras
            if (Integer.parseInt(levelSelected) > 1) {
                leftArrow.setVisibility(View.VISIBLE);
            }
            // si es el ultimo nivel, no muestro la flechita de ir para adelante
            if (Integer.parseInt(levelSelected) < getLevelCount()) {
                rightArrow.setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    private String getWordByCategory() {
        String respuesta = "";
        try {
            //obtengo el archivo
            String jsonLocation = null;
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
            } else if("escudos".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("escudos.json", getBaseContext());
            }

            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("listado");
            //obtengo el nivel
            JSONObject nivel = (JSONObject)jarray.get(Integer.parseInt(levelSelected));
            //obtengo la palabra del nivel correspondiente, segun la categoria elegida
            respuesta = nivel.getString(categorySelected);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    private void moveToNextOrPrevious(String moveTo) {
        if ("next".equals(moveTo)) {
            editor.putString("levelSelected", ((Integer)(Integer.parseInt(levelSelected) + 1)).toString());
        }
        if ("previous".equals(moveTo)) {
            editor.putString("levelSelected", ((Integer)(Integer.parseInt(levelSelected) - 1)).toString());
        }
        editor.putBoolean("autoclick", true);

        saveStateOfLevel();
        editor.commit();
    }

    private void saveStateOfLevel(){
        if (Integer.parseInt(levelSelected) == Integer.parseInt(level)) {
            level = ((Integer)(Integer.parseInt(level) + 1)).toString();
        }
        if ("adivinanzas".equals(categorySelected)) {
            editor.putString("levelAdivinanzas", level);
        } else if("wuzzles".equals(categorySelected)) {
            editor.putString("levelWuzzles", level);
        } else if("emojis".equals(categorySelected)) {
            editor.putString("levelEmojis", level);
        } else if("enojis".equals(categorySelected)) {
            editor.putString("levelEnojis", level);
        } else if("peliculas".equals(categorySelected)) {
            editor.putString("levelPeliculas", level);
        } else if("movies".equals(categorySelected)) {
            editor.putString("levelMovies", level);
        } else if("escudos".equals(categorySelected)) {
            editor.putString("levelEscudos", level);
        }
        //editor.putString("levelSelected", level);
        editor.commit();
    }

    /*
    *  abre/cierra el teclado
    * */
    private void toggleKeyboardVisible () {
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (aciertos != word.replaceAll(" ", "").replaceAll("\\|","").length()) {
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
    private void timer(int milliseconds) {
        timer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                counter.setText(""+String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
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
            labelLevelText.setVisibility(View.INVISIBLE);
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
        finish();
    }
    // en el back abro un popup, en el cancelar sigo con el timer
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // obtengo la cantidad de segundos restantes y los convierto en milisegundos
        String tiempo[] = ((String)this.counter.getText()).split(":");
        Integer minutos = Integer.parseInt(tiempo[0])*60*1000;
        Integer segundos = (Integer.parseInt(tiempo[1]) + 1) * 1000;
        milisegundos = minutos + segundos;
        timer(milisegundos);
    }

    private void showImage(int res) {
        final Dialog dialogCustomImage = new Dialog(GuessImageActivity.this);
        dialogCustomImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCustomImage.setContentView(R.layout.custom_dialog_zoomimage);

        inputMethodManager.hideSoftInputFromWindow(frameLayout.getApplicationWindowToken(), 0);

        ImageView imgZoom = (ImageView) dialogCustomImage.findViewById(R.id.imageToGuessZoom);
        imgZoom.setImageResource(res);
        dialogCustomImage.setOnDismissListener(new Dialog.OnDismissListener() {
            public void onDismiss(final DialogInterface dialog) {
                // Abro el teclado cuando me quedo sin tiempo
                inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });

        if (dialogCustomImage != null) {
            dialogCustomImage.show();
        }
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
        dialogCustom = new Dialog(GuessImageActivity.this);
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
            buyContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            winContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            watchContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            shareContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            shareContainerTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
                if (verifyStoragePermissions(GuessImageActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.facebook.katana")){
                        avoidInterstitialOnShare = false;
                        String sharedDescription = !languageSelected? getResources().getString(R.string.generic_share_text) : getResources().getString(R.string.generic_share_text_en);
                        String sharedTitle = getResources().getString(R.string.title_share_text_en);
                        String sharedImage = !languageSelected? "https://lh3.googleusercontent.com/WjHSbuxdCfYAIjrvq3aZI9LxSeysMZ6oQPBCnJ6I2WpjCQdBn2iiiPo0u7moJrAEYCc=h900-rw":"https://lh3.googleusercontent.com/pPkfzgA9TVDFEUnZ9qfdkiTI1WVqNeZdgG1-nG2ZB1WnBcwXFDEUAiw1j4ODR7nujmw=h900-rw";
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
                        if (!languageSelected) {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        shareTwit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(GuessImageActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.twitter.android")){
                        avoidInterstitialOnShare = false;
                        Uri screenshotUri = !languageSelected? Uri.parse("android.resource://com.ru55o.luckypalm.acertijos/drawable/sharetwitterimage"):Uri.parse("android.resource://com.ru55o.luckypalm.acertijos/drawable/sharetwitterimageen");
                        String shareText = !languageSelected? getResources().getString(R.string.generic_share_text) + " https://goo.gl/CrnO9M" : getResources().getString(R.string.generic_share_text_en) + " https://goo.gl/CrnO9M";

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
                        if (!languageSelected) {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        shareWsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(GuessImageActivity.this)) {
                    if(isAppInstalled(getBaseContext(), "com.whatsapp")){
                        avoidInterstitialOnShare = false;
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Bitmap largeIcon;
                        if (!languageSelected) {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sharetwitterimage);
                        } else {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sharetwitterimageen);
                        }
                        Uri screenshotUri = Uri.parse(saveBitmap(largeIcon, true));
                        String shareText = !languageSelected? getResources().getString(R.string.generic_share_text) + " https://goo.gl/CrnO9M": getResources().getString(R.string.generic_share_text_en) + " https://goo.gl/CrnO9M";
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
                        if (!languageSelected) {
                            Toast.makeText(getBaseContext(),"App not installed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(),"Aplicación no instalada", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        ganar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuessImageActivity.this, PlayForSecondsActivity.class);
                startActivity(intent);
            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuessImageActivity.this, BuySecondsActivity.class);
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
                            if(!(GuessImageActivity.this).isFinishing()){
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

    // Retorno la cantidad de niveles que tengo en el juego (es -1 porque la primer posicion es cero)
    private int getLevelCount() {
        int count = 0;
        try {
            //obtengo el archivo
            String jsonLocation = null;
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
            } else if("escudos".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("escudos.json", getBaseContext());
            } else if("marcas".equals(categorySelected)) {
                jsonLocation = AssetJSONFile("marcas.json", getBaseContext());
            }
            JSONObject jsonobject = new JSONObject(jsonLocation);
            //obtengo el array de niveles
            JSONArray jarray = (JSONArray) jsonobject.getJSONArray("listado");
            count = jarray.length();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count-1;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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