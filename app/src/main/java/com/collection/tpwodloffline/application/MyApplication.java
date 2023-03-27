package com.collection.tpwodloffline.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.collection.tpwodloffline.broadcasts.MobileDataListeners;

public class MyApplication extends Application {
    BroadcastReceiver mobileDataBr = new MobileDataListeners();
    BroadcastReceiver mobileDataBr1 = new MobileDataListeners();
    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "App has been created", Toast.LENGTH_SHORT).show();
        IntentFilter internetIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter gpsIntentFilter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(mobileDataBr, internetIntentFilter);
        //getApplicationContext().registerReceiver(mobileDataBr1, gpsIntentFilter);
    }
}

