package com.collection.tpwodloffline.nonenergy;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAmigoThermalNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicImpactNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicThermalNew;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class NonEnergyDashboard extends AppCompatActivity {

    CardView notified_coll,reprint_receipt,sync,download_data;
    ProgressDialog progressDialog;
    SharedPreferenceClass sharedPreferenceClass;
    final Context context = this;
    String un,pw,mobile;
    public DatabaseAccess databaseAccess = null;
    private Cursor rs = null;

    private TextView usercount;
    private TextView uid;
    private TextView strtdcoll;
    private TextView strtotcoll;
    private TextView strtotmr;
    private TextView strbalrem;
    private TextView strtodaytmr;
    public BottomSheetDialog mBottomSheetDialog;
    private String transID = "";
    private String cust_id = "";
    private int sbmflg = 0;

    @Override
    protected void onResume() {
        super.onResume();
        getReport();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimarynonentop));
        setContentView(R.layout.activity_non_energy_dashboard);

        sharedPreferenceClass = new SharedPreferenceClass(NonEnergyDashboard.this);
        un = sharedPreferenceClass.getValue_string("un");
        pw = sharedPreferenceClass.getValue_string("pw");
        mobile = sharedPreferenceClass.getValue_string("mobile");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        notified_coll =  findViewById(R.id.notified_coll);
        reprint_receipt = findViewById(R.id.reprint_receipt);
        usercount = findViewById(R.id.usercount);
        sync =  findViewById(R.id.sync);
        download_data = findViewById(R.id.download_data);
        ImageView tpsodllogo = findViewById(R.id.tpsodllogo);
        tpsodllogo.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimarynonentop), android.graphics.PorterDuff.Mode.MULTIPLY);
        usercount.setText("Total Records Available: " + getUserCount());
        getReport();
        //to get SBM print
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + un + "'";
        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);
        }
        //   Log.d("DemoApp", "strUpdateSQL_01  01");
        rs.close();
        databaseAccess.close();

        notified_coll.setOnClickListener(v->{
            if (recordsInLocal() > 0) {
                Intent non_energy = new Intent(getApplicationContext(), NonEnergyPayDetails.class);
                startActivity(non_energy);
            }else {
                un = sharedPreferenceClass.getValue_string("un");
                pw = sharedPreferenceClass.getValue_string("pw");
                mobile = sharedPreferenceClass.getValue_string("mobile");
                notified_coll.setKeepScreenOn(true);
                new DownloadCustData().execute(CommonMethods.getDownloadNonenUrlNow(un, pw, mobile));
            }

        });

        sync.setOnClickListener(v->{

                Intent non_energy = new Intent(getApplicationContext(), NonEnergySync.class);
                startActivity(non_energy);

        });
        download_data.setOnClickListener(v->{
            un = sharedPreferenceClass.getValue_string("un");
            pw = sharedPreferenceClass.getValue_string("pw");
            mobile = sharedPreferenceClass.getValue_string("mobile");
            download_data.setKeepScreenOn(true);
            new DownloadCustData().execute(CommonMethods.getDownloadNonenUrlNow(un, pw, mobile));

        });

        reprint_receipt.setOnClickListener(v->{
            openBottomsheet();
        });

    }
    private int recordsInLocal() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from NONENERGY_DATA";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        //Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void openBottomsheet() {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View bottomSheetLayout = inflater.inflate(R.layout.bottomsheet_reprint_dialog, null);
        final ImageView search = bottomSheetLayout.findViewById(R.id.search);
        final EditText rec_num = bottomSheetLayout.findViewById(R.id.rec_num);

        search.setOnClickListener(v->{
        String values = rec_num.getText().toString();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
            if(values.equals("")){
                rec_num.setError("Enter record number to search");
            }else {
                //loadData(values);

                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                String strSelectSQL_02 = "Select TRANS_ID,TOT_PAID,SCNO FROM COLL_NEN_DATA WHERE REF_REG_NO='" + values + "' and RECPT_FLG=1  order by trans_id desc";
                //String strSelectSQL_02 = "Select A.TRANS_ID,TOT_PAID,CUST_ID FROM COLL_SBM_DATA A WHERE CONS_ACC='" + EntryNum + "' and RECPT_FLG=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)  order by trans_id desc";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);

                LinearLayout layout = bottomSheetLayout.findViewById(R.id.rootContainersheet);
                RadioGroup ll = new RadioGroup(this);

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layout.addView(ll, p);
                while (rs1.moveToNext()) {
                    transID = rs1.getString(0);
                    String AmountPay = rs1.getString(1);
                    cust_id = rs1.getString(2);
                    RadioButton rdbtn = new RadioButton(this);
                    rdbtn.setText("Trans Id:  " + transID + "\nRef No: " + values + "\n Amount:  " + AmountPay);
                    rdbtn.setTextSize(16);
                    rdbtn.setOnClickListener(mThisButtonListener);
                    ll.addView(rdbtn, p);
                }
                if(transID.equals("")){
                    Toast.makeText(context, "No transactions found!", Toast.LENGTH_SHORT).show();
                }
                databaseAccess.close();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(NonEnergyDashboard.this, R.style.SheetDialog);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.show();

    }
    private String TransID = "";
    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            String s = ((RadioButton) v).getText().toString();
            // Toast.makeText(DuplicateSummary.this, "Hello from 2!" + s, Toast.LENGTH_LONG).show();
            String[] TransInfo = s.split("[:]");
            TransID = TransInfo[1].replace("\nRef No", "");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Do You Want to print");
            alertDialogBuilder.setMessage("Tap Print if yes" + "\n" + " Tap Cancel to re-select ")
                    .setCancelable(false)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Print", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (sbmflg == 8) {
                                //Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", "nonen");
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            } else if (sbmflg == 5) {
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", "nonen");
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            }else if (sbmflg == 6) {
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", "nonen");
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            } else if (sbmflg ==2) {
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", "nonen");
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            }else {
                                Toast.makeText(context, "Printer not configured!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    };


    private int getUserCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from NONENERGY_DATA where USER_ID= " + un + "";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private class DownloadCustData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (NonEnergyDashboard)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
            Log.d("DemoApp", " strURL   " + strURL);
            // String strURL="http://portal.tpcentralodisha.com:8080/IncomingSMS/CESU_mCollection1.jsp?strCompanyID=1&un=1&pw=A&imei=356154070159681/01";

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
              /*  Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>")+"<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
              /*  Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);*/
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
                progressDialog = ProgressDialog.show(NonEnergyDashboard.this, "Downloading consumer data", "Please Wait:: connecting to server");
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
                                NonEnergyDashboard.this.finish();
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
            progressDialog.dismiss();
            //scheduleWork();
            //deleteNonEnergyData();
           /* if(getSBMDataCount()>0){
                sendDataNow();
            }*/
            new InsertIntoDb().execute(str);
        }


    }
    private class InsertIntoDb extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(NonEnergyDashboard.this);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog1.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                String[] custData = str[0].split("[;]");
                Log.d("Index0Data", custData[0]);
                //Log.d("DataAt4557", ""+custData[4557]);
                final int max = custData.length;


                if (custData.length > 1) {
                    deleteNonenData();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog1.setCancelable(false);
                            progressDialog1.setMessage("Downloading data don't press any key, please wait...");
                            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog1.setProgress(0);
                            progressDialog1.setMax(max);
                            progressDialog1.show();
                        }
                    });

                    for (int colonIndex = 0; colonIndex < custData.length; colonIndex++) {
                        progressDialog1.setProgress(colonIndex);
                        try {
                            if (colonIndex == 0) {
                                String[] singleData = custData[colonIndex].substring(6).split("[|]", -1);
                                insertCustomerData(singleData, false, true);

                            } else {
                                String[] singleData = custData[colonIndex].split("[|]", -1);
                                if (colonIndex == custData.length - 1) {
                                    insertCustomerData(singleData, true, false);
                                } else {
                                    insertCustomerData(singleData, false, false);
                                }

                            }
                        }
                        catch (Exception e){
                            //Toast.makeText(context, "ERROR : " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NonEnergyDashboard.this, "No records found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
    }

    private void deleteNonenData() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String insertCredentials = "DELETE from NONENERGY_DATA";
        DatabaseAccess.database.execSQL(insertCredentials);
        databaseAccess.close();
    }

    private void insertCustomerData(String[] custData, boolean doClose, boolean doOpen) {

        if (doOpen) {
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
        }
        String strSelectSQL_02 = "INSERT INTO NONENERGY_DATA  " +
                " (USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION,SECTION,CON_NAME,CON_ADD1,CON_ADD2,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,REMARKS,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE)" +
                " VALUES('" + un + "','" + CommonMethods.CompanyID + "','" + custData[7].replace("'", "''") +"','" + custData[3].replace("'", "''") +"','" + custData[4].replace("'", "''") + "','" + custData[10].replace("'", "''") + "'," +
                " '" + custData[0].replace("'", "''") + "','" + custData[1].replace("'", "''") + "','" + custData[2].replace("'", "''") + "','" + custData[8].replace("'", "''") + "','" + custData[9].replace("'", "''") + "','" + custData[9].replace("'", "''") + "'," +
                " '" + custData[5].replace("'", "''") + "', '" + custData[6].replace("'", "''") + "', '" + custData[8].replace("'", "''") + "', '" + custData[8].replace("'", "''") + "', '" + "Remarks" + "', '" + 0 + "', '" + 0 + "', '" + 0 + "', '" + 0+ "', '" + 0 + "', date('now')) ";// "', '" + "1" +
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);
        if (doClose) {
            databaseAccess.close();
            usercount.setText("Total Records Available: " + getUserCount());
            //CommonMethods.saveBooleanPreference(this, isDataSynced, true);

        }
    }

    private void getReport() {

        String device_id = CommonMethods.getDeviceid(getApplicationContext());
        final Context context = this;
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String Usernm = sessiondata.getString("userID", null); // getting String
        String Coll_Limit = "";
        String Bal_Remain = "";
        String tot_mr = "";
        String todaymr = "";
        String todaycol = "";
        String totcoll = "";
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        /*String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now'),tot_mr  " +
                " FROM sa_user  where date('now')>=valid_startdate and date('now')<=valid_enddate and lock_flag=0 " +
                " and userid='" + Usernm + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            Coll_Limit = cursor.getString(8);
            Bal_Remain = cursor.getString(10);
            tot_mr = cursor.getString(12);

            Log.d("DemoApp", "in Loop" + Coll_Limit);
            Log.d("DemoApp", "in Loop" + Bal_Remain);
            Log.d("DemoApp", "in Loop" + tot_mr);
        }*/
        String strSelectSQL_01 = "select count(1) from COLL_NEN_DATA where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', trans_date)"; //,'+05 hours','+30 minutes'
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            todaymr = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "";
        strSelectSQL_01 = "select sum(tot_paid) from COLL_NEN_DATA where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', trans_date)";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            todaycol = cursor.getString(0);
        }
        cursor.close();

        strSelectSQL_01 = "select count(1) from COLL_NEN_DATA_BKP where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date)";// for indian time date('now','+05 hours','+30 minutes')
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            tot_mr = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "select sum(tot_paid) from COLL_NEN_DATA_BKP where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date)";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totcoll = cursor.getString(0);
        }
        cursor.close();

        strSelectSQL_01 ="";
        strSelectSQL_01 = "select bal_remain from sa_user where userid='" + un + "'";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            Bal_Remain = cursor.getString(0);
        }
        cursor.close();
        databaseAccess.close();
        //strcolllimit= (TextView) findViewById(R.id.colllimit);
        uid = (TextView) findViewById(R.id.uid);
        strtdcoll = (TextView) findViewById(R.id.tdcoll);
        strtotcoll = (TextView) findViewById(R.id.totcoll);
        strtotmr = (TextView) findViewById(R.id.totmr);
        strtodaytmr = (TextView) findViewById(R.id.todaytmr);
        strbalrem = (TextView) findViewById(R.id.balrem);

        strtotmr.setText(tot_mr);
        if(todaycol == null){
            strtdcoll.setText("0");
        }else {
            strtdcoll.setText(todaycol);
        }

        if(totcoll == null){
            strtotcoll.setText("0");
        }else {
            strtotcoll.setText(totcoll);
        }
        strtodaytmr.setText(todaymr);
        strbalrem.setText(Bal_Remain);
        uid.setText("("+Usernm+" : "+device_id+")");
    }


}