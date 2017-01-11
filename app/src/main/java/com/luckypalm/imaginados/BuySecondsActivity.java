package com.luckypalm.imaginados;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy_seconds);

        // ...
        String base64EncodedPublicKey = getResources().getString(R.string.inapp_key);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    class GetItemList extends AsyncTask<Integer, Integer, ArrayList<String>> {
        private String sku = "";
        private String price = "";
        private String title = "";
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
                    title = object.getString("description");

                    if (inappid_three.equals(sku)) {
                        three_minutesBtn.setText(price);
                        three_minutesTxt.setText(title);
                        three_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            buy(sku, title);
                            }
                        });
                    }
                    if (inappid_five.equals(sku)) {
                        five_minutesBtn.setText(price);
                        five_minutesTxt.setText(title);
                        five_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(sku, title);
                            }
                        });
                    }
                    if (inappid_seven.equals(sku)) {
                        seven_minutesBtn.setText(price);
                        seven_minutesTxt.setText(title);
                        seven_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(sku, title);
                            }
                        });
                    }
                    if (inappid_ten.equals(sku)) {
                        ten_minutesBtn.setText(price);
                        ten_minutesTxt.setText(title);
                        ten_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(sku, title);
                            }
                        });
                    }
                    if (inappid_fifteen.equals(sku)) {
                        fifteen_minutesBtn.setText(price);
                        fifteen_minutesTxt.setText(title);
                        fifteen_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(sku, title);
                            }
                        });
                    }
                    if (inappid_twenty.equals(sku)) {
                        twenty_minutesBtn.setText(price);
                        twenty_minutesTxt.setText(title);
                        twenty_minutesBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buy(sku, title);
                            }
                        });
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void buy (String mSku, String mTitle) {
            try {
                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), mSku, "inapp", mSku + mTitle.replaceAll(" ",""));
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }
}
    /*String sku = object.getString("productId");
    String price = object.getString("price");
    String description = object.getString("description");
    String title = object.getString("title");*/
