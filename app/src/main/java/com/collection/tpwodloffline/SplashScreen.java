package com.collection.tpwodloffline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.collection.tpwodloffline.activity.MainActivity;
import com.collection.tpwodloffline.otp.GenerateOTP;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import static com.collection.tpwodloffline.utils.Constants.isFirstTimeLoginPref;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3200;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash_screen);

        sharedPreferenceClass = new SharedPreferenceClass(SplashScreen.this);

        if(!CommonMethods.getBooleanPreference(getApplicationContext(), isFirstTimeLoginPref, true)){
            /*String temp[]={"1006", "8882826885", "a", "333", "20-07-2020", "20-07-2022", "8882826885", "efs", "0", "0", "c"};
            onVerifyOtpSCB(temp);*/
            sharedPreferenceClass.setValue_string("from_flag","1");
            Thread myThread = new Thread()
            {
                @Override
                public void run() {
                    try {
                        sleep(SPLASH_TIME_OUT);
                        Intent ColDashboard = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(ColDashboard);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();

        }else {
            sharedPreferenceClass.setValue_string("from_flag","0");

            Thread myThread = new Thread()
            {
                @Override
                public void run() {
                    try {
                        sleep(SPLASH_TIME_OUT);
                        Intent ColDashboard = new Intent(getApplicationContext(), GenerateOTP.class);
                        startActivity(ColDashboard);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
        }
    }


}