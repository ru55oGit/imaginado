package com.luckypalm.imaginados;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

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
    private static final String FORMAT = "%02d:%02d";
    private int milisegundos;
    private int secondsToSubtract;

    private String word;
    private int dim;
    private int aciertos = 0;
    private String uri;
    private String level;
    private GradientDrawable gd;
    private Typeface digifont;
    private Typeface lobsterFont;
    private Toast toast;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private InputMethodManager inputMethodManager;
    private ImageView sharewsap;
    private ImageView shareInstagram;
    private ImageView shareTwitter;
    private ImageView shareFacebook;
    private ImageView volver;

    private LinearLayout firstLine;
    private LinearLayout secondLine;

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
        setContentView(com.luckypalm.imaginados.R.layout.activity_guess_image);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        digifont = Typeface.createFromAsset(getAssets(), "fonts/ds-digi.ttf");
        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        timerFlag = true;

        // seteo el tiempo que tengo para jugar en el reloj
        milisegundos = settings.getInt("time", 120000);
        counter = (TextView) findViewById(com.luckypalm.imaginados.R.id.counterText);
        counter.setTypeface(digifont);
        counter.setText(""+String.format(FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(milisegundos) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milisegundos)),
                TimeUnit.MILLISECONDS.toSeconds(milisegundos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milisegundos))));



        // share wsap
        sharewsap = (ImageView) findViewById(com.luckypalm.imaginados.R.id.sharewsap);
        sharewsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(GuessImageActivity.this)) {
                    if (timer != null) {
                        timer.cancel();
                    }

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot()));
                    sharingIntent.setPackage("com.whatsapp");
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);

                    if (uri.contains("adivinanzas")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Ayudame a resolver este acertijo:  https://goo.gl/OufAlF");
                    } else if (uri.contains("banderas")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "No recuerdo de que país es esta bandera:  https://goo.gl/OufAlF");
                    } else if (uri.contains("escudos")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "¿De qué equipo de fútbol es este escudo?:  https://goo.gl/OufAlF");
                    } else if (uri.contains("marcas")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Este logo era de... mmmmm:  https://goo.gl/OufAlF");
                    } else if (uri.contains("peliculas")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "¿Viste esta película? ¿Cuál es?:  https://goo.gl/OufAlF");
                    } else if (uri.contains("personajes")) {
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "¿ehhh... cómo se llamaba?:  https://goo.gl/OufAlF");
                    }
                    startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                } else {

                }
            }
        });

        shareFacebook = (ImageView) findViewById(com.luckypalm.imaginados.R.id.sharefacebook);
        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyStoragePermissions(GuessImageActivity.this)) {
                    if (timer != null) {
                        timer.cancel();
                    }

                    String sharedDescription = new String();
                    String sharedImage = new String();

                    if (uri.contains("adivinanzas")) {
                        sharedDescription = "Ayudame a resolver este acertijo";
                        sharedImage = "https://lh3.googleusercontent.com/moMMSTbr5XwIDZLUE54ttInkjRdPV47yzjdV1wv6zFKAvZLDOTwkegQruLZI-6i1aeU=h900";
                    } else if (uri.contains("banderas")) {
                        sharedDescription = "No recuerdo de que país es esta bandera";
                        sharedImage = "https://lh3.googleusercontent.com/WfCmVxD93WtUdXoJkseTlxiAIDWwIFncxW7w7YczvGGhPl9hXd3oXGLZDL5m5AX9ir-T=h900";
                    } else if (uri.contains("escudos")) {
                        sharedDescription =  "¿De qué equipo de fútbol es este escudo?";
                        sharedImage = "https://lh3.googleusercontent.com/UkB7i-HW02E-SdQ7GIiVRmsP1j3BDNwavGOEZOkApwBSQ--SfDO77nqWL25rTOReH5R3=h900";
                    } else if (uri.contains("marcas")) {
                        sharedDescription =  "Este logo era de... mmmmm";
                        sharedImage = "https://lh3.googleusercontent.com/UzbkxKXgbh6VMLJPOiGPD5lbdZzJT_W6YnDoqkjYELbpU8NAdWnRazePlq5-eJNAag=h900";
                    } else if (uri.contains("peliculas")) {
                        sharedDescription =  "¿Viste esta película? ¿Cuál es?";
                        sharedImage = "https://lh3.googleusercontent.com/jyVxq8hW4_uIWBeDrfp5csrTHEW0hspMskCeX4QTJLR0VTlflw007imyvDacyf8Q3PPq=h900";
                    } else if (uri.contains("personajes")) {
                        sharedDescription =  "¿ehhh... cómo se llamaba este personaje?";
                        sharedImage = "https://lh3.googleusercontent.com/8BcK8Ul1X926Pti1rGrGMoIVshOEQkiT6TZ9C6P_f2FsYmpNOaPLQo3npkchMgVHQH0=h900";
                    }

                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot()));
                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                .setContentTitle("Imaginados")
                                .setContentDescription(sharedDescription)
                                .setContentUrl(Uri.parse("https://goo.gl/OufAlF"))
                                .setImageUrl(Uri.parse(sharedImage))
                                .build();

                        shareDialog.show(shareLinkContent);
                    }
                }

            }
        });

        shareTwitter = (ImageView) findViewById(com.luckypalm.imaginados.R.id.sharetwitter);
        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri screenshotUri = Uri.parse(saveBitmap(takeScreenshot()));
                String shareText = new String();

                if (uri.contains("adivinanzas")) {
                    shareText = "Ayudame a resolver este acertijo https://goo.gl/OufAlF";
                } else if (uri.contains("banderas")) {
                    shareText = "No recuerdo de que país es esta bandera https://goo.gl/OufAlF";
                } else if (uri.contains("escudos")) {
                    shareText = "¿De qué equipo de fútbol es este escudo https://goo.gl/OufAlF";
                } else if (uri.contains("marcas")) {
                    shareText = "Este logo era de... mmmmm https://goo.gl/OufAlF";
                } else if (uri.contains("peliculas")) {
                    shareText = "¿Viste esta película? ¿Cuál es? https://goo.gl/OufAlF";
                } else if (uri.contains("personajes")) {
                    shareText = "¿ehhh... cómo se llamaba? https://goo.gl/OufAlF";
                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sharingIntent.setType("text/plain");

                sharingIntent.setPackage("com.twitter.android");
                startActivity(sharingIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        secondsToSubtract = 0;

        frameLayout = (RelativeLayout) findViewById(com.luckypalm.imaginados.R.id.frameCounter);
        toggleKeyboardVisible();

        // border radius
        gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius((int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_radius));
        gd.setStroke((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.border_letters_guess), getResources().getColor(com.luckypalm.imaginados.R.color.secondaryColor));

        // traigo el Nivel
        level = settings.getString("level","1");

        Bundle extras = getIntent().getExtras();
        // Traigo la imagen que se eligio para adivinar
        uri = extras.getString("src");
        int res = getResources().getIdentifier(uri, "drawable", getPackageName());
        // seteo la imagen en el imageview
        imageToGuess = (ImageView) findViewById(com.luckypalm.imaginados.R.id.imageToGuess);
        imageToGuess.setImageResource(res);
        // obtengo la palabra que se va adivinar
        word = extras.getString("word");

        // volver
        volver = (ImageView) findViewById(com.luckypalm.imaginados.R.id.volver);

        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                }
                finish();
            }
        });

        firstLine = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerFirst);
        secondLine = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerSecond);
        // Remuevo todas la letras porque se apendean cuando hago compartir y cancelo
        firstLine.removeAllViews();
        secondLine.removeAllViews();


        LinearLayout thirdLine = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerThird);

        // dibujo los guiones correspondientes a cada letra de la palabra
        for (int i = 0; i < word.length(); i++) {
            TextView letter = new TextView(this);
            if (Character.isWhitespace(word.charAt(i))) {
                letter.setText("");
                dim = (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size_whitespace);
            } else if ('|' != word.charAt(i)){
                letter.setText("__");
                letter.setAllCaps(true);
                if (Build.VERSION.SDK_INT > 16) {
                    letter.setBackground(gd);
                } else {
                    //letter.setBackgroundResource();
                }
                dim = (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.bg_letter_size);
            }
            letter.setGravity(Gravity.CENTER_HORIZONTAL);

            letter.setTextSize((int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
            LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
            marginLetters.setMargins(0, 0, 10, 0);
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
        } else {
            // si hay segundos abro el teclado
            showSoftKey = new CountDownTimer(700, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
            }.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancelo el custom toast cdo salgo
        if (toast != null) {
            toast.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
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
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        inputMethodManager.toggleSoftInputFromWindow(frameLayout.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        finish();
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

        if (toast != null){
            toast.cancel();
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            timer.cancel();
            editor.putInt("time", milisegundos);
            editor.putString("statusLevel", saveStateOfLevel(settings.getString("statusLevel", "000000")));
            editor.commit();
            finish();
            return false;
        }

        LinearLayout ll = (LinearLayout)findViewById(com.luckypalm.imaginados.R.id.wordContainerFirst);
        LinearLayout ll2 = (LinearLayout) findViewById(com.luckypalm.imaginados.R.id.wordContainerSecond);

        // por cada letra ingresada, evaluo en toda la palabra
        for (int i = 0; i < word.length(); i++) {
            // si viene un pipe, es que las palabras estan divididas en 2 renglones
            if (word.indexOf("|") < 0) {
                // si el caracter ingresado coincide con la posicion[i] de la palabra && no fue previamente adivinado
                if (Character.toUpperCase(word.charAt(i)) == event.getDisplayLabel() && ((TextView) ll.getChildAt(i)).getText().equals("__")) {
                    TextView letter = new TextView(this);
                    Character letra = (char) event.getDisplayLabel();
                    letter.setText(letra.toString());
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(com.luckypalm.imaginados.R.color.primaryColor);
                    if (Build.VERSION.SDK_INT > 15)
                        letter.setBackground(gd);

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins(0, 0, 10, 0);
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
                    letter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(com.luckypalm.imaginados.R.dimen.letter_size));
                    letter.setGravity(Gravity.CENTER_HORIZONTAL);
                    letter.setBackgroundResource(com.luckypalm.imaginados.R.color.primaryColor);
                    if (Build.VERSION.SDK_INT > 15)
                        letter.setBackground(gd);

                    LinearLayout.LayoutParams marginLetters = new LinearLayout.LayoutParams(dim, dim);
                    marginLetters.setMargins(0, 0, 10, 0);
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
            Integer segundos = Integer.parseInt(tiempo[1])*1000-secondsToSubtract*1000;
            milisegundos = minutos + segundos;
            timer(milisegundos);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(com.luckypalm.imaginados.R.layout.toast_layout,
                    (ViewGroup) findViewById(com.luckypalm.imaginados.R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(com.luckypalm.imaginados.R.id.text);
            text.setText("Fallaste. -"+secondsToSubtract+" Segundos");
            text.setTypeface(lobsterFont);

            toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP, 0, (int)getResources().getDimension(com.luckypalm.imaginados.R.dimen.top_toast));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
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
            Toast.makeText(getBaseContext(),"has ganado 10 segundos",Toast.LENGTH_LONG).show();
            // guardo los segundos totales para ser usados en la proxima palabra
            settings = getSharedPreferences("Status", 0);

            editor.putInt("time", milisegundos);
            editor.putString("statusLevel", saveStateOfLevel(settings.getString("statusLevel", "000000")));
            editor.commit();
        }
        return true;
    }

    private String saveStateOfLevel(String status){
        StringBuilder sts = new StringBuilder(status);
        if(this.uri.contains("adivinanzas")){
            sts.replace(0,1,"1");
        }
        if(this.uri.contains("escudos")){
            sts.replace(1,2,"1");
        }
        if(this.uri.contains("marcas")){
            sts.replace(2,3,"1");
        }
        if(this.uri.contains("peliculas")){
            sts.replace(3,4,"1");
        }
        if(this.uri.contains("personajes")){
            sts.replace(4, 5, "1");
        }
        if(this.uri.contains("banderas")){
            sts.replace(5, 6, "1");
        }
        // si adivine todas las palabras, paso al siguiente nivel
        if ("111111".equals(sts.toString())) {
            level = ((Integer)(Integer.parseInt(level) + 1)).toString();
            editor.putString("level", level);
            editor.commit();
            sts = new StringBuilder("000000");
        }

        return sts.toString();
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

                SharedPreferences settings = getSharedPreferences("Status", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("time", 0);
                editor.commit();
                customDialog();
            }
        }.start();
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public String saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/_sinsegundos.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
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

    private void customDialog(){
        // custom dialog
        final Dialog dialogCustom = new Dialog(GuessImageActivity.this);
        dialogCustom.setContentView(R.layout.custom_dialog_withoutseconds);

        ImageView ganar = (ImageView) dialogCustom.findViewById(R.id.ganarSegundos);
        ImageView comprar = (ImageView) dialogCustom.findViewById(R.id.comprarSegundos);

        ImageView shareFace = (ImageView) dialogCustom.findViewById(R.id.sharefacebookDialog);
        ImageView shareTwit = (ImageView) dialogCustom.findViewById(R.id.sharetwitterDialog);
        ImageView shareWsap = (ImageView) dialogCustom.findViewById(R.id.sharewsapDialog);

        shareFace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String sharedDescription =  getResources().getString(R.string.generic_share_text);
                String sharedImage = "https://lh3.googleusercontent.com/qJAwISZCFEdEtr1-RaZd1ZyA_aUk1mR3LHDlFvKevp9qOkRR8krfGYfgICbHFMtDsg=h900";

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Imaginados")
                            .setContentDescription(sharedDescription)
                            .setContentUrl(Uri.parse("https://goo.gl/OufAlF"))
                            .setImageUrl(Uri.parse(sharedImage))
                            .build();
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        shareTwit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri screenshotUri = Uri.parse("android.resource://com.luckypalm.imaginados/drawable/sharetwitterimage");
                String shareText = getResources().getString(R.string.generic_share_text) + "https://goo.gl/OufAlF";

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                sharingIntent.setPackage("com.twitter.android");
                startActivity(sharingIntent);
            }
        });

        shareWsap.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                 Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sharetwitterimage);
                 Uri screenshotUri = Uri.parse(saveBitmap(largeIcon));
                 String shareText = getResources().getString(R.string.generic_share_text) + "https://goo.gl/OufAlF";
                 sharingIntent.setPackage("com.whatsapp");
                 sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                 sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                 sharingIntent.setType("image/*");

                 startActivity(sharingIntent);
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
            Toast.makeText(getBaseContext(), "Muy pronto podras comprar segundos", Toast.LENGTH_LONG).show();
            }
        });

        ImageButton dialogButton = (ImageButton) dialogCustom.findViewById(com.luckypalm.imaginados.R.id.dialogButtonOK);
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
}