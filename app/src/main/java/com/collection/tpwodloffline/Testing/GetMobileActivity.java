package com.collection.tpwodloffline.Testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class GetMobileActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    TextView textView,textView13;
    TelephonyManager telephonyManager;
    ArrayList<String> phoneNumber = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_mobile);

        textView13 = findViewById(R.id.textView13);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;

        Log.e("MyActivity", "manufacturer " + manufacturer
                + " \n model " + model
                + " \n version " + version
                + " \n versionRelease " + versionRelease
        );

        textView13.setText(manufacturer+" "+model+" "+version+versionRelease);
        sendSMS("9090455728",manufacturer+model+version+versionRelease);


       /* boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        if(mobileDataEnabled ==true){
            Toast.makeText(this, "Data is on", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Data is off", Toast.LENGTH_SHORT).show();
        }*/

        textView = findViewById(R.id.text);
        //textView13 = findViewById(R.id.textView13);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                for (int i = 0; i < subscription.size(); i++) {
                    SubscriptionInfo info = subscription.get(i);
                    Log.d("num", "number " + info.getNumber());
                    Log.d("network_name", "network name : " + info.getCarrierName());
                    Log.d("country iso", "country iso " + info.getCountryIso());
                    phoneNumber.add(info.getNumber());
                }
                String n1 = phoneNumber.get(0);
                String strLastFourDi = n1.length() >= 10 ? n1.substring(n1.length() - 10): "";

               // textView13.setText(strLastFourDi+"\n"+phoneNumber.get(1));
            }else {
                textView.setText(telephonyManager.getLine1Number());
            }
        }


    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                        for (int i = 0; i < subscription.size(); i++) {
                            SubscriptionInfo info = subscription.get(i);
                            Log.d("num", "number " + info.getNumber());
                            Log.d("network_name", "network name : " + info.getCarrierName());
                            Log.d("country iso", "country iso " + info.getCountryIso());
                            phoneNumber.add(info.getNumber());
                        }
                        String n1 = phoneNumber.get(0);
                        String strLastFourDi = n1.length() >= 10 ? n1.substring(n1.length() - 10): "";

                       // textView13.setText(strLastFourDi+"\n"+phoneNumber.get(1));

                    }else {
                        textView.setText(telephonyManager.getLine1Number());
                    }
                }
        }
    }
}