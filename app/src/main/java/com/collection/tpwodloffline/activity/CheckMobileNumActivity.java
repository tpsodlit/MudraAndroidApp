package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class CheckMobileNumActivity extends AppCompatActivity {
    public DatabaseAccess databaseAccess = null;
    String SelChoice_string = "";
    String EntryNum_string = "";
    private static EditText strMobNonew;
    final Context context = this;
    //access for billig data
    private String cons_acc = "";
    private String StrBillInfo = "";
    private String strCompanyId = "";
    private String div_code = "";
    private String usernm = "";
    private String MobileNo = "";
    private static Button btnsaveMob;
    private String urlName = "";
    Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
       /* if (timer != null) {
            timer.cancel();
            // Log.i("Main", "cancel timer");
            timer = null;
        }
*/
    }

    @Override
    protected void onPause() {
        super.onPause();

       /* timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 900000); //auto logout in 5 minutes*/
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(CheckMobileNumActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mobile_num);
       /* Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName = savedUrl.getString("savedUrl", null); // getting String

        System.out.println("sadfghj==" + urlName);

        StrBillInfo = urlName + "CESU_API_Chk_Mobile_No/Mobile_EMail.jsp?";
        strMobNonew = (EditText) findViewById(R.id.MobNonew);
        btnsaveMob = (Button) findViewById(R.id.saveMob);
        SelChoice_string = "";
        Bundle extrasvalcol1 = getIntent().getExtras();
        SelChoice_string = extrasvalcol1.getString("SelChoice");
        EntryNum_string = extrasvalcol1.getString("EntryNum");

        cons_acc = "";
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        usernm = sessiondata.getString("userID", null);
        // String pwd=sessiondata.getString("prv_bill", null);
        div_code = sessiondata.getString("div_code", null);
        String imeinum = sessiondata.getString("imeinum", null);

        StrBillInfo = StrBillInfo + "un=TEST&pw=TEST&CompanyID=10&strDivCode=" + div_code + "&strCons_Acc=" + EntryNum_string + "&strDB_Type=1";
        Log.d("DemoApp", "in Loop AuthURL2" + StrBillInfo);
        try {
            // new FetchBillDetOnline().execute(StrBillInfo);
            if (recordCount() > 0) {
                getMobileNumberFromLocal();
            } else {
                Toast.makeText(context, "No records found...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
        }

        btnsaveMob.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              try {
                                                  String txtMobNonew = strMobNonew.getText().toString().trim();
                                                  if (!CommonMethods.validateNumber(txtMobNonew)) {
                                                      strMobNonew.setError("Enter valid 10 digit mobile number!");
                                                  } else {
                                                      Intent accountinfo = new Intent(getApplicationContext(), AccountInfo.class);
                                                      Bundle extrasvalcol = new Bundle();
                                                      extrasvalcol.putString("SelChoice", SelChoice_string);
                                                      extrasvalcol.putString("EntryNum", EntryNum_string);
                                                      extrasvalcol.putString("MobNonew", txtMobNonew);
                                                      extrasvalcol.putString("from", "account");
                                                      // extrasval.putString("Validcon", "0");
                                                      accountinfo.putExtras(extrasvalcol);
                                                      startActivity(accountinfo);
                                                      finish();
                                                  }
                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                                  alertDialogBuilder.setTitle("Error Occured");
                                                  alertDialogBuilder.setMessage("Intimate the case to IT Center" + "\n" + "For checking")
                                                          .setCancelable(false)
                                                          .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                                              public void onClick(DialogInterface dialog, int id) {
                                                                  dialog.cancel();
                                                              }
                                                          })
                                                          .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                                              public void onClick(DialogInterface dialog, int id) {
                                                                  startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                  finish();
                                                              }
                                                          });
                                                  // create alert dialog
                                                  AlertDialog alertDialog = alertDialogBuilder.create();
                                                  // show it
                                                  alertDialog.show();
                                              }
                                          }
                                      }
        );
    }

    private void getMobileNumberFromLocal() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select MOBILE_NO from CUST_DATA where CONS_ACC='" + EntryNum_string + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        int i = 0;
        String mobileNum = "";
        while (cursor.moveToNext()) {
            mobileNum = cursor.getString(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        strMobNonew.setText(mobileNum);
        databaseAccess.close();
    }

    private int recordCount() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select count(*) from CUST_DATA where CONS_ACC='" + EntryNum_string + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        int i = 0;
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        return count;
    }

    private class FetchBillDetOnline extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
            Log.d("DemoApp", " strURL   " + strURL);
            try {
                URL url = null;
                url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = null;
                in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine = "";
                StringBuilder a = new StringBuilder();
                Log.d("DemoApp", " a size   " + a.length());
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                    //  Log.d("DemoApp", " input line " + a.toString());
                }
                in.close();

                Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);
                bodycontent = html.substring(start, end);
                Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(CheckMobileNumActivity.this, "Fetching Mobile Number", "Please Wait:: connecting to server");
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Enable Data");
                alertDialogBuilder.setMessage("Enable Data & Retry")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CheckMobileNumActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String str) {
            Log.d("DemoApp", " str   " + str);
            progressDialog.dismiss();
            String pipeDelBillInfo = str;
            String[] BillInfo = pipeDelBillInfo.split("[|]");
            String MobNum = "";
            try {
                MobNum = BillInfo[2];
                strMobNonew.setText(MobNum);
                if (!BillInfo[1].equals("0")) {
                    // btnsaveMob.setEnabled(false);
                    btnsaveMob.setVisibility(View.GONE);
                    Intent accountinfo = new Intent(getApplicationContext(), AccountInfo.class);
                    Bundle extrasvalcol = new Bundle();
                    extrasvalcol.putString("SelChoice", SelChoice_string);
                    extrasvalcol.putString("EntryNum", EntryNum_string);
                    extrasvalcol.putString("MobNonew", MobNum);
                    extrasvalcol.putString("from", "account");
                    // extrasval.putString("Validcon", "0");
                    accountinfo.putExtras(extrasvalcol);
                    startActivity(accountinfo);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
