package com.collection.tpwodloffline.nonenergy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NonEnergySync extends AppCompatActivity {

    Button btnUploadPending;
    TextView txtPending, txtUploaded, txtuid, txtdid;
    private DatabaseAccess databaseAccess = null;
    private String custId = "";
    private String custNamenonen = "";
    private String custAdd = "";
    private String scnum = "";
    private String rem_module = "";
    private String ref_reg_num = "";
    private String section = "";
    private String RecptGenURLNen = null;
    private String strmsgnen = "";
    private String TxnTime = "";
    String usname = "";
    String dbpwdnm = "";
    int amount = 0;
    String paymode_;
    String remarks;
    String BalRemains;
    String demand_date;
    String TransID;
    String CompanyId = "";
    private String serverDate = "";
    private String ReceiptNonen = "";
    private String lat = "";
    private String lang = "";
    private String tr_date = "";
    private String oprn_type = "";

    SharedPreferenceClass sharedPreferenceClass;
    ProgressDialog progressDialog;
    String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimarynonentop));
        setContentView(R.layout.activity_nonenergy_sync);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        btnUploadPending = findViewById(R.id.btnUploadPending);
        txtUploaded = findViewById(R.id.txtUploaded);
        txtPending = findViewById(R.id.txtPending);
        txtuid = findViewById(R.id.txtuid);
        txtdid = findViewById(R.id.txtdid);
        sharedPreferenceClass = new SharedPreferenceClass(NonEnergySync.this);
        progressDialog = new ProgressDialog(NonEnergySync.this);

        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        serverDate = sessionssodata.getString("serverDate", null);
        usname = sharedPreferenceClass.getValue_string("un");
        dbpwdnm = sharedPreferenceClass.getValue_string("pw");
        device_id = CommonMethods.getDeviceid(getApplicationContext());

        btnUploadPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("sdfg", "onClick: " + RecptGenURLNen);

                try {
                    Date dates = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates.getTime()).toString());
                    Date serverDateParse = sdf.parse(serverDate);
                    if ((currentDate.compareTo(serverDateParse) == 0)) {

                        if (getNenOfflineCount() > 0) {
                            uploadOfflineNenData();
                        } else {
                            Toast.makeText(NonEnergySync.this, "No pending data to upload.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NonEnergySync.this);
                        alertDialogBuilder.setTitle("Please Check Current Date");
                        alertDialogBuilder.setMessage("Change the Date and try again !!")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        NonEnergySync.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                //scheduleWork();
            }
        });
        txtPending.setText("" + getNenOfflineCount());
        txtUploaded.setText("" + getUploadedCount());
        txtuid.setText("(" + usname + " : ");
        txtdid.setText(device_id + ")");
    }

    private int getNenOfflineCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_NEN_DATA where SEND_FLG=0 AND RECPT_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private int getUploadedCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_NEN_DATA where SEND_FLG=1 AND RECPT_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void uploadOfflineNenData() {
        resetStringsNen();
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "Select" +
                " USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION," + //7
                " SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,RECPT_DATE,RECPT_TIME," + //16
                " MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG," + //24
                " COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH,OPERATION_TYPE,REMARKS,LATTITUDE," + //32
                " LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE " + //39
                " FROM " +
                " COLL_NEN_DATA WHERE RECPT_FLG='1' and SEND_FLG = '0'";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while (rs.moveToNext()) {
            scnum = rs.getString(2);
            rem_module = rs.getString(3);
            ref_reg_num = rs.getString(4);
            section = rs.getString(8);
            custId = rs.getString(5);
            custNamenonen = rs.getString(9);
            custAdd = rs.getString(10);
            paymode_ = rs.getString(20);
            BalRemains = rs.getString(29);
            demand_date = rs.getString(12);
            amount = rs.getInt(19);
            TransID = "" + rs.getString(26);
            lat = rs.getString(32);
            lang = rs.getString(33);
            tr_date = rs.getString(28);
            oprn_type = rs.getString(30);
            remarks = rs.getString(31);
            String dt = rs.getString(28); //2022-12-01
            String tm = rs.getString(16);// 00:00:00
            /*if (dt.contains("-")) {
                dt = dt.replace("-", "");
            }*/
            /*if (tm.contains(":")) {
                tm = tm.replace(":", "");
            }*/
            TxnTime = dt +" "+ tm;

           /* String BillType = "";
            String pay_cnt = rs.getString(47);
            if(collMode.equals("ADV")){
                BillType = "D";
            }else {
                switch (pay_cnt) {
                    case "0":
                        BillType = "B";
                        break;
                    case "1":
                        BillType = "A";
                        break;
                    case "2":
                        BillType = "C";
                        break;
                }
            }*/
            String BillType = "W";

            ReceiptNonen = BillType+rs.getString(17);

        }
        String device_id = CommonMethods.getDeviceid(getApplicationContext());

        Log.d("userName", "" + usname);

        RecptGenURLNen = ServerLinks.postPayment_ne;
        CompanyId = CommonMethods.CompanyID;

        String postData = usname + "|"+device_id+ "|" + CompanyId + "|" + scnum + "|" + rem_module + "|" + ref_reg_num + "|" + custId + "|" + section + "|" + custNamenonen + "|" + custAdd + "|" + amount + "|" + demand_date + "|" + TxnTime + "|" + ReceiptNonen + "|" + "9999999999" + "|" + paymode_ + "|" + TransID + "|" + "NRML" + "|" + tr_date + "|" + BalRemains + "|" + oprn_type + "|" + remarks + "|" + lat + "|" + lang + "|" + "MD";
        RecptGenURLNen = RecptGenURLNen + "encKey=" + CommonMethods.encryptText(postData);

        Log.d("Url:::", RecptGenURLNen);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            new ReceiptNenGenOnline().execute(RecptGenURLNen);
        } else {
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetStringsNen() {
        scnum = "";
        rem_module = "";
        ref_reg_num = "";
        custId = "";
        section = "";
        custNamenonen = "";
        custAdd = "";
        amount = 0;
        demand_date = "";
        TxnTime = "";
        ReceiptNonen = "";
        paymode_ = "";
        TransID = "";
        tr_date = "";
        BalRemains = "";
        oprn_type = "";
        remarks = "";
        lat = "";
        lang = "";
    }

    private class ReceiptNenGenOnline extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
            Log.d("DemoApp", " strURL   " + strURL);
            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                bodycontent = a.toString();
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bodycontent;
        }

        @Override
        protected void onPreExecute() {

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String strSelectSQL_01 = "";
                strSelectSQL_01 = "UPDATE COLL_NEN_DATA SET MACHINE_NO=1";
                strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custId + "' AND TRANS_ID='" + TransID + "'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            }
        }

        @Override
        protected void onPostExecute(String str) {

            try {
                Log.d("res", " str   " + str);
                strmsgnen = str;
                String pipeDelRecptInfo = str;
                String[] RecptInfo = pipeDelRecptInfo.split("[|]");
                String ConsValid = RecptInfo[0];
                String Txn_svr = RecptInfo[6];
                String recprno = "";
                String RecptDt = "";
                String RecptTime = "";

                if (ConsValid.equals("1")) {
                  /*  recprno = RecptInfo[3];
                    RecptDt = RecptInfo[4];

                    RecptTime = RecptDt.substring(8, 14);
                    RecptDt = RecptDt.substring(4, 8) + "-" + RecptDt.substring(2, 4) + "-" + RecptDt.substring(0, 2);
                    Log.d("DemoApp", " RecptDt   " + RecptDt);
                    Log.d("DemoApp", " RecptTime   " + RecptTime);
                    int balremain = 0;*/

                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String strSelectSQL_01 = "";
                    strSelectSQL_01 = "UPDATE COLL_NEN_DATA SET RECPT_FLG=1,SEND_FLG=1";

                    strSelectSQL_01 = strSelectSQL_01 + " WHERE REF_REG_NO='" + ref_reg_num + "' AND TRANS_ID='" + Txn_svr + "'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();
                    // progressDialog.dismiss();
                    if (getNenOfflineCount() > 0) {
                        uploadOfflineNenData();
                    } else {
                        Toast.makeText(NonEnergySync.this, "Data has been uploaded successfully...", Toast.LENGTH_SHORT).show();
                    }
                    txtPending.setText("" + getNenOfflineCount());
                    txtUploaded.setText("" + getUploadedCount());
                }else {
                    Toast.makeText(NonEnergySync.this, "Uploading Failed", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception ex) {
                Toast.makeText(NonEnergySync.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }


        }
    }
}
