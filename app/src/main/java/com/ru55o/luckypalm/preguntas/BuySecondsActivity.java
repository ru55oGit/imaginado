package com.ru55o.luckypalm.preguntas;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.ru55o.luckypalm.preguntas.util.IabHelper;
import com.ru55o.luckypalm.preguntas.util.IabResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ToledoP on 27/01/2017.
 */

public class BuySecondsActivity extends AppCompatActivity {
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

    private String inappid_three = "com.ru55o.luckypalm.acertijos.three_minutes";
    private String inappid_five = "com.ru55o.luckypalm.acertijos.five_minutes";
    private String inappid_seven = "com.ru55o.luckypalm.acertijos.seven_minutes";
    private String inappid_ten = "com.ru55o.luckypalm.acertijos.ten_minutes";
    private String inappid_fifteen = "com.ru55o.luckypalm.acertijos.fifteen_minutes";
    private String inappid_twenty = "com.ru55o.luckypalm.acertijos.twenty_minutes";
    private String purchaseToken = "android.test.purchased";

    private Button three_minutesBtn;
    private Button five_minutesBtn;
    private Button seven_minutesBtn;
    private Button ten_minutesBtn;
    private Button fifteen_minutesBtn;
    private Button twenty_minutesBtn;

    private TextView three_minutesTxt;
    private TextView five_minutesTxt;
    private TextView seven_minutesTxt;
    private TextView ten_minutesTxt;
    private TextView fifteen_minutesTxt;
    private TextView twenty_minutesTxt;
    private TextView title;

    private ImageButton close;
    private IabHelper mHelper;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private Typeface lobsterFont;
    private Boolean languageSelected;
    private RelativeLayout frameCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy_seconds);
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        languageSelected = settings.getBoolean("languageSelected", true);

        lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/lobster-two.italic.ttf");
        frameCounter = (RelativeLayout) findViewById(R.id.activity_buy_seconds);
        title = (TextView) findViewById(R.id.title);


        // ...
        String base64EncodedPublicKey = getResources().getString(R.string.inapp_key);

        // Traigo el tiempo acumulado para setear el timer
        settings = getSharedPreferences("Status", 0);
        editor = settings.edit();

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(getApplicationContext(), base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("BuySecondsActivity", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                new GetItemList().execute();
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        three_minutesBtn = (Button) findViewById(R.id.threeMinutesBtn);
        five_minutesBtn = (Button) findViewById(R.id.fiveMinutesBtn);
        seven_minutesBtn = (Button) findViewById(R.id.sevenMinutesBtn);
        ten_minutesBtn = (Button) findViewById(R.id.tenMinutesBtn);
        fifteen_minutesBtn = (Button) findViewById(R.id.fifteenMinutesBtn);
        twenty_minutesBtn = (Button) findViewById(R.id.twentyMinutesBtn);

        three_minutesTxt = (TextView) findViewById(R.id.threeMinutesTxt);
        three_minutesTxt.setTypeface(lobsterFont);
        five_minutesTxt = (TextView) findViewById(R.id.fiveMinutesTxt);
        five_minutesTxt.setTypeface(lobsterFont);
        seven_minutesTxt = (TextView) findViewById(R.id.sevenMinutesTxt);
        seven_minutesTxt.setTypeface(lobsterFont);
        ten_minutesTxt = (TextView) findViewById(R.id.tenMinutesTxt);
        ten_minutesTxt.setTypeface(lobsterFont);
        fifteen_minutesTxt = (TextView) findViewById(R.id.fifteenMinutesTxt);
        fifteen_minutesTxt.setTypeface(lobsterFont);
        twenty_minutesTxt = (TextView) findViewById(R.id.twentyMinutesTxt);
        twenty_minutesTxt.setTypeface(lobsterFont);

        if (languageSelected.booleanValue() && Build.VERSION.SDK_INT > 16) {
            frameCounter.setBackground(getResources().getDrawable(R.drawable.tile_en));
            title.setText(getResources().getText(R.string.sin_tiempo_title_en));
        }

        close = (ImageButton) findViewById(R.id.dialogButtonOK);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // manejos la vuelta de la compra
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String purchaseToken = jo.getString("purchaseToken");
                    String productId = jo.getString("productId");
                    new ConsumePurchase().execute(purchaseToken, productId);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // consumo la compra y acredito los segundos correspondientes
    class ConsumePurchase extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            try {
                int response = mService.consumePurchase(3, getPackageName(), params[0].toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return params[1].toString();
        }
        protected void onPostExecute(String id) {
            if (inappid_three.equals(id)) {
                editor.putInt("time", 180000);
            } else if (inappid_five.equals(id)) {
                editor.putInt("time", 300000);
            } else if (inappid_seven.equals(id)) {
                editor.putInt("time", 420000);
                editor.putBoolean("showAds", false);
            } else if (inappid_ten.equals(id)) {
                editor.putInt("time", 600000);
                editor.putBoolean("showAds", false);
            } else if (inappid_fifteen.equals(id)) {
                editor.putInt("time", 900000);
                editor.putBoolean("showAds", false);
            } else if (inappid_twenty.equals(id)) {
                editor.putInt("time", 1200000);
                editor.putBoolean("showAds", false);
            } else if (purchaseToken.equals(id)) {
                editor.putInt("time", 900000);
                editor.putBoolean("showAds", false);
            }
            editor.commit();
            finish();
        }
    }

    class GetItemList extends AsyncTask<Integer, Integer, ArrayList<String>> {
        private String sku = "";
        private String price = "";
        private String description = "";
        protected ArrayList<String> doInBackground(Integer... params) {
            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add(inappid_three);
            skuList.add(inappid_five);
            skuList.add(inappid_seven);
            skuList.add(inappid_ten);
            skuList.add(inappid_fifteen);
            skuList.add(inappid_twenty);

            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
            Bundle skuDetails;
            ArrayList<String> responseList = null;
            try {
                skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return responseList;
        }
        protected void onPostExecute(ArrayList<String> responseList) {
            try {
                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    sku = object.getString("productId");
                    price = object.getString("price");
                    description = object.getString("description");

                    if (inappid_three.equals(sku)) {
                        three_minutesBtn.setText(price);
                        three_minutesTxt.setText(description);
                        three_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_three, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            three_minutesTxt.setText(three_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                    if (inappid_five.equals(sku)) {
                        five_minutesBtn.setText(price);
                        five_minutesTxt.setText(description);
                        five_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_five, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            five_minutesTxt.setText(five_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                    if (inappid_seven.equals(sku)) {
                        seven_minutesBtn.setText(price);
                        seven_minutesTxt.setText(description);
                        seven_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_seven, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            seven_minutesTxt.setText(seven_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                    if (inappid_ten.equals(sku)) {
                        ten_minutesBtn.setText(price);
                        ten_minutesTxt.setText(description);
                        ten_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_ten, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            ten_minutesTxt.setText(ten_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                    if (inappid_fifteen.equals(sku)) {
                        fifteen_minutesBtn.setText(price);
                        fifteen_minutesTxt.setText(description);
                        fifteen_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_fifteen, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            fifteen_minutesTxt.setText(fifteen_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                    if (inappid_twenty.equals(sku)) {
                        twenty_minutesBtn.setText(price);
                        twenty_minutesTxt.setText(description);
                        twenty_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(inappid_twenty, description);
                            }
                        });
                        if (languageSelected.booleanValue()) {
                            twenty_minutesTxt.setText(twenty_minutesTxt.getText().toString().replace("minutos","minutes"));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void buy (String mSku, String mDesc) {
            try {
                // limpiar purchase token de prueba
                //int response = mService.consumePurchase(3, getPackageName(), "inapp:com.luckypalm.imaginados:android.test.purchased");
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), mSku, "inapp", mSku + mDesc.replaceAll(" ",""));
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
