package com.luckypalm.imaginados;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.luckypalm.imaginados.util.IabHelper;
import com.luckypalm.imaginados.util.IabResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuySecondsActivity extends AppCompatActivity {

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

    private String inappid_three = "com.luckypalm.imaginados.three_minutes";
    private String inappid_five = "com.luckypalm.imaginados.five_minutes";
    private String inappid_seven = "com.luckypalm.imaginados.seven_minutes";
    private String inappid_ten = "com.luckypalm.imaginados.ten_minutes";
    private String inappid_fifteen = "com.luckypalm.imaginados.fifteen_minutes";
    private String inappid_twenty = "com.luckypalm.imaginados.twenty_minutes";
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

    private ImageButton close;
    private IabHelper mHelper;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy_seconds);

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
        five_minutesTxt = (TextView) findViewById(R.id.fiveMinutesTxt);
        seven_minutesTxt = (TextView) findViewById(R.id.sevenMinutesTxt);
        ten_minutesTxt = (TextView) findViewById(R.id.tenMinutesTxt);
        fifteen_minutesTxt = (TextView) findViewById(R.id.fifteenMinutesTxt);
        twenty_minutesTxt = (TextView) findViewById(R.id.twentyMinutesTxt);

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
            int response = 0;
            try {
                response = mService.consumePurchase(3, getPackageName(), params[0].toString());
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
            } else if (inappid_ten.equals(id)) {
                editor.putInt("time", 600000);
            } else if (inappid_fifteen.equals(id)) {
                editor.putInt("time", 900000);
            } else if (inappid_twenty.equals(id)) {
                editor.putInt("time", 1200000);
            } else if (purchaseToken.equals(id)) {
                editor.putInt("time", 1200000);
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
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), "android.test.purchased", "inapp", mSku + mDesc.replaceAll(" ",""));
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

    /*String sku = object.getString("productId");
    String price = object.getString("price");
    String description = object.getString("description");
    String description = object.getString("description");
    "android.test.purchased"
    "android.test.cancelled"
    Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
    int response = ownedItems.getInt("RESPONSE_CODE");
    if (response == 0) {
       ArrayList<String> ownedSkus =
          ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
       ArrayList<String>  purchaseDataList =
          ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
       ArrayList<String>  signatureList =
          ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
       String continuationToken =
          ownedItems.getString("INAPP_CONTINUATION_TOKEN");

       for (int i = 0; i < purchaseDataList.size(); ++i) {
          String purchaseData = purchaseDataList.get(i);
          String signature = signatureList.get(i);
          String sku = ownedSkus.get(i);

          // do something with this purchase information
          // e.g. display the updated list of products owned by user
       }

       // if continuationToken != null, call getPurchases again
       // and pass in the token to retrieve more items
    }

    */
