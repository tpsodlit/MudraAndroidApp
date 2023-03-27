package com.collection.tpwodloffline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class SwitchUrlSetupActivity extends AppCompatActivity {

    private Button btn_set;
    private RadioGroup rg_url;
    private RadioButton url_1;
    private RadioButton url_2;
    private ImageView iv_back;
    private String urlName="";
    private String setUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_url_setup);

        btn_set=findViewById(R.id.btn_set);
        rg_url=findViewById(R.id.rg_url);
        url_1=findViewById(R.id.url_1);
        url_2=findViewById(R.id.url_2);
        iv_back=findViewById(R.id.iv_back);

        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String
        setUrl=urlName;

        assert urlName != null;
        if (urlName.equalsIgnoreCase("http://portal.tpcentralodisha.com:8070/")){
            url_1.setChecked(true);
        }
        else {
            url_2.setChecked(true);
        }

        rg_url.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.url_1:
                        setUrl = "http://portal.tpcentralodisha.com:8070/";
                        break;
                    case R.id.url_2:
                        setUrl = "http://portal.tpcentralodisha.com:8070/";
                        break;
                }
            }
        });

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sessionUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
                SharedPreferences.Editor sessionData = sessionUrl.edit();
                sessionData.putString("savedUrl",setUrl);
                sessionData.apply();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SwitchUrlSetupActivity.this);
               // alertDialogBuilder.setTitle(message);
                alertDialogBuilder.setMessage("URL Set Up Successfully")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();


                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SwitchUrlSetupActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });




        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}