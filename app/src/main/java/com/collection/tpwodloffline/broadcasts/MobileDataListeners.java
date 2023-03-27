package com.collection.tpwodloffline.broadcasts;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.collection.tpwodloffline.CommonMethods;

import static android.content.Context.LOCATION_SERVICE;

public class MobileDataListeners extends BroadcastReceiver {
    AlertDialog alert;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().matches(LocationManager.MODE_CHANGED_ACTION)){
           // Toast.makeText(context, "GPS Trigger", Toast.LENGTH_SHORT).show();

            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(context, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            }else{
                //showGPSDisabledAlertToUser(context);
                Toast.makeText(context, "GPS is Disabled in your devide", Toast.LENGTH_SHORT).show();

            }

        }
        if(intent.getAction().matches(ConnectivityManager.CONNECTIVITY_ACTION)){
            CommonMethods.internetListener(context);
        }


    }

    private void showGPSDisabledAlertToUser(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it to proceed")
                .setCancelable(false)
                .setPositiveButton("Go to Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private Boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void showConnectivityPopup(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Please switch on your internet.")
                .setCancelable(false)
                .setPositiveButton("Connected", null);

        alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button onOk = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                onOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isConnected(context)){
                            alert.dismiss();
                        }else{
                            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        alert.show();

    }
}
