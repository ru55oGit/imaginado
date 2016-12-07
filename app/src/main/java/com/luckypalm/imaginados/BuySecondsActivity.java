package com.luckypalm.imaginados;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuySecondsActivity extends AppCompatActivity {

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    String inappid_three = "com.luckypalm.imaginados.three_minutes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy_seconds);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        ImageView three_minutesBtn = (ImageView) findViewById(R.id.three_minutes);

        three_minutesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> skuList = new ArrayList<String> ();
                skuList.add("three_minutes");
                skuList.add("five_minutes");
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                Bundle skuDetails;

                try {
                    skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        for (String thisResponse:responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");

                            if (sku.equals(inappid_three)) {
                                Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),sku, "inapp","")
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
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
}
