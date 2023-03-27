package com.collection.tpwodloffline.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RadioOTSNonOTS extends AppCompatActivity {

    private RadioButton rdBtnOts, rdBtnNonOts;
    String SelChoice_string = "", EntryNum_string = "",un = "", pw = "", mobile = "";
    private int OtsNormalFlag = 0;

    private Context context = this;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ots_nonots_screen);
        getIntentData();
        initViews();
    }

    private void getIntentData() {
        Bundle extrasVal = getIntent().getExtras();
        SelChoice_string = extrasVal.getString("SelChoice");
        EntryNum_string = extrasVal.getString("EntryNum");

        sharedPreferenceClass = new SharedPreferenceClass(this);
        un = sharedPreferenceClass.getValue_string("un");
        pw = sharedPreferenceClass.getValue_string("pw");
        mobile = sharedPreferenceClass.getValue_string("mobile");
    }

    private void initViews() {

        Toolbar toolBarBack = findViewById(R.id.toolbar);
        setSupportActionBar(toolBarBack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolBarBack.getNavigationIcon().setColorFilter(getResources().
                getColor(R.color.white, null), PorterDuff.Mode.SRC_ATOP);

        toolBarBack.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ColDashboard.class));
            finish();
        });

        rdBtnOts = findViewById(R.id.rdb_ots);
        rdBtnNonOts = findViewById(R.id.rdb_non_ots);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        if (rdBtnOts.isChecked()) {
            OtsNormalFlag = 0;
        }
        if (rdBtnNonOts.isChecked()) {
            OtsNormalFlag = 1;
        }
        rdBtnOts.setOnClickListener(v -> OtsNormalFlag = 0);

        rdBtnNonOts.setOnClickListener(v -> OtsNormalFlag = 1);

        btnSubmit.setOnClickListener(view -> submit());
    }

    private void submit() {
        Intent intent;
        if (OtsNormalFlag == 0) {
            if (new DatabaseAccess().checkOTSData(this, EntryNum_string) == 0) {
                if (CommonMethods.isConnected(this)) {
                    new DownloadOTSData().execute(
                            CommonMethods.getOTSDownloadUrl(un, pw, mobile, EntryNum_string));
                } else {
                    Toast.makeText(context,
                            "No internet connection found",
                            Toast.LENGTH_SHORT).show();
                }
            }else if (new DatabaseAccess().checkOTSDataSearching(this, EntryNum_string) > 0) {
                intent = new Intent(getApplicationContext(),OTSInfo.class);
                setIntentAndStartActivity(intent);
            } else {
                Toast.makeText(this,
                        "No records found...",
                        Toast.LENGTH_SHORT).show();
                //return;
            }
        } else {
            intent = new Intent(getApplicationContext(), CheckMobileNumActivity.class);
            setIntentAndStartActivity(intent);
        }
//        Bundle bundle = new Bundle();
//        bundle.putString("SelChoice", SelChoice_string);
//        bundle.putString("EntryNum", EntryNum_string);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        finish();
    }

    private class DownloadOTSData extends AsyncTask<String, Integer, String> {
        ProgressDialog progressD;

        @Override
        protected void onPreExecute() {

            progressD = new ProgressDialog(context);

            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressD = ProgressDialog.show(context,
                        "Checking OTS data",
                        "Please Wait:: connecting to server");
            } else {
                CommonMethods.showDialog(RadioOTSNonOTS.this, context,
                        Constants.titleEnableData, Constants.bodyEnableData);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strURL = params[0];
            String bodycontent = null, inputLine;
            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(uc.getInputStream()));
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                bodycontent = a.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bodycontent;
        }

        @Override
        protected void onPostExecute(String str) {
            progressD.dismiss();
            new InsertOTSData().execute(str);
        }
    }

    private class InsertOTSData extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                JSONArray ary = new JSONArray(str);
                String obj = ary.getString(0);
                JSONObject jsonObject = new JSONObject(obj);
                int resCode = jsonObject.getInt("resCode");
                String message = jsonObject.getString("message");
                if (resCode == 200) {
                    try {
                        JSONArray jsonArrayOTS = jsonObject.getJSONArray("otsData");
                        if (jsonArrayOTS != null) {
                            final int maxOTS = jsonArrayOTS.length();
                            if (jsonArrayOTS.length() > 0) {
                                runOnUiThread(() -> {
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Downloading OTS data don't " +
                                            "press any key, please wait...");
                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDialog.setProgress(0);
                                    progressDialog.setMax(maxOTS);
                                    progressDialog.show();
                                });
                                for (int i = 0; i < jsonArrayOTS.length(); i++) {
                                    progressDialog.setProgress(i);
                                    JSONObject otsJsonObject = jsonArrayOTS.getJSONObject(i);
                                    Log.d("OTS", "OTS Data ::" + jsonArrayOTS);
                                    CommonMethods.downloadOTS(context, otsJsonObject);
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(context,
                                        "No OTS records found",
                                        Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            runOnUiThread(() -> Toast.makeText(context,
                                    "No OTS records found",
                                    Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(context,
                                "JSON Exception! :::"
                                        + resCode + ":::" + str,
                                Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(context,
                            "Something went wrong! :::"
                                    + resCode + ":::" + message,
                            Toast.LENGTH_SHORT).show());
                }
            } catch (JSONException jexcpn) {
                jexcpn.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (new DatabaseAccess().checkOTSDataSearching(context, EntryNum_string) > 0) {
                Intent intent = new Intent(getApplicationContext(),
                        OTSInfo.class);
                setIntentAndStartActivity(intent);
            } else {
                Toast.makeText(context,
                        "Online checked but no records found...",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setIntentAndStartActivity(Intent intent) {
        Bundle bundle = new Bundle();
        bundle.putString("SelChoice", SelChoice_string);
        bundle.putString("EntryNum", EntryNum_string);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}