package com.collection.tpwodloffline;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class CollSetupMenuActivity extends AppCompatActivity {

    Button switch_url;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_setup_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnprintertye = (Button) findViewById(R.id.printertye);
        switch_url=findViewById(R.id.switch_url);

        btnprintertye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CheckCamera = new Intent(getApplicationContext(), SetPrinterTypeActivity.class);
                startActivity(CheckCamera);
                finish();
            }
        });//end

        switch_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CheckCamera = new Intent(getApplicationContext(), SwitchUrlSetupActivity.class);
                startActivity(CheckCamera);
                finish();
            }
        });

    }

}
