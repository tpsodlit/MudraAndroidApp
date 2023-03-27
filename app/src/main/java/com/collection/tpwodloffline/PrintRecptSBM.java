package com.collection.tpwodloffline;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class PrintRecptSBM extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_sbm);
    }
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
}
