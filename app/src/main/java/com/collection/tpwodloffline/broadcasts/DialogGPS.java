package com.collection.tpwodloffline.broadcasts;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.R;

public class DialogGPS extends AppCompatActivity {
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_gps);
        fa = this;

        //TextView textViewGuide = (TextView) findViewById(R.id.txtMessage);
        Button buttonOkDialog = (Button) findViewById(R.id.buttonOkDialog);
        //textViewGuide.setText("Text");
        buttonOkDialog.setText("Retry");
        buttonOkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonMethods.isDataEnabled(getApplicationContext())){
                    finish();
                }else{
                    Toast.makeText(DialogGPS.this, "Connect to internet to continue", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        /*if(CommonMethods.isConnected(getApplicationContext())){
            finish();
        }else{
            Toast.makeText(DialogGPS.this, "Connect to internet to continue", Toast.LENGTH_SHORT).show();
        }*/
        finish();
    }
}

