package com.collection.tpwodloffline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.collection.tpwodloffline.activity.CollectionDashBoard;

public class CollDownloadMenuActivity extends AppCompatActivity {
    private   DatabaseAccess databaseAccess=null;
    Context context = this;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_download_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button Backbtn = (Button) findViewById(R.id.Back);
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reports = new Intent(getApplicationContext(), CollectionDashBoard.class);
                startActivity(reports);
                finish();

            }
        });
        Button DwnldText = (Button) findViewById(R.id.DwnldText);
        DwnldText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DemoApp", "enter 2");
              /*  try {
                     databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    SbmUtilities.SBMRTFile(1, "I");// to import rt file to table
                    databaseAccess.close();
                }catch(Exception e){
                    Log.d("DemoApp", "Exception"+e);
                }*/

                Intent dwnldText = new Intent(getApplicationContext(), CollUtilitiesActivity.class);
                Bundle Dwnldval = new Bundle();
                Dwnldval.putString("param", "3");//collection
                Dwnldval.putString("paramtype", "I");
                dwnldText.putExtras(Dwnldval);
                startActivity(dwnldText);
                finish();
            }
        });

    }

}