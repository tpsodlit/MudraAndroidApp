package com.collection.tpwodloffline.nonenergy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAmigoThermalNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicImpactNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicThermalNew;
import com.collection.tpwodloffline.UploadManager;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.util.concurrent.TimeUnit;

public class NonenReceiptGen extends AppCompatActivity {

    String REF_MODULE, REF_REG_NO, AMOUNT, SCNO, CUST_ID, Trans_Id, BalFetch_org, BalFetch, CON_NAME, MOBILE_NO, SECTION, DATE_TXN;
    TextView refModule, refRegdno, amount, scNum, protxt, transId, custName, section, date;
    private DatabaseAccess databaseAccess = null;
    FrameLayout progressView;
    Button startpayment;
    private static int SPLASH_TIME_OUT = 1000;
    final Context context = this;
    String Usernm = "";
    private Cursor rs = null;
    SharedPreferenceClass sharedPreferenceClass;
    String username, lat, lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimarynonentop));
        setContentView(R.layout.activity_nonen_receipt_gen);

        REF_MODULE = getIntent().getStringExtra("refmodule");
        REF_REG_NO = getIntent().getStringExtra("refregno");
        AMOUNT = getIntent().getStringExtra("amout");
        SCNO = getIntent().getStringExtra("scno");
        CUST_ID = getIntent().getStringExtra("custID");
        Trans_Id = getIntent().getStringExtra("TransID");
        BalFetch_org = getIntent().getStringExtra("BalFetch");
        CON_NAME = getIntent().getStringExtra("namefetch");
        MOBILE_NO = getIntent().getStringExtra("MobileNofetch");
        SECTION = getIntent().getStringExtra("Section");
        DATE_TXN = getIntent().getStringExtra("Date");
        sharedPreferenceClass = new SharedPreferenceClass(NonenReceiptGen.this);
        username = sharedPreferenceClass.getValue_string("un");
        lat = sharedPreferenceClass.getValue_string("Latitude");
        lang = sharedPreferenceClass.getValue_string("Longitude");
        protxt = findViewById(R.id.protxt);
        startpayment = findViewById(R.id.startpayment);
        progressView = findViewById(R.id.progressView);
        refModule = findViewById(R.id.deptval);
        refRegdno = findViewById(R.id.rec_numval);
        custName = findViewById(R.id.nameval);
        scNum = findViewById(R.id.scnumval);
        section = findViewById(R.id.sectionval);
        date = findViewById(R.id.ddateval);
        amount = findViewById(R.id.amountval);
        transId = findViewById(R.id.transidval);
        protxt.setText("Please Wait...");
        progressView.setVisibility(View.VISIBLE);
        UpdateTxn();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void scheduleWork() {
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadManager.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);
    }

    private void scheduleBackgrounService() {
        String TAG_NAME = "uploadData";
        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(UploadManager.class, 20, TimeUnit.MINUTES)
                        .addTag(TAG_NAME)
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(TAG_NAME, ExistingPeriodicWorkPolicy.REPLACE,
                periodicSyncDataWork);
    }

    private void UpdateTxn() {

        try {
            double BalFetch_int = Double.parseDouble(BalFetch_org) - Double.parseDouble(AMOUNT);
            BalFetch = String.valueOf(BalFetch_int);
            databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            databaseAccess.open();
            String strSelectSQL_00 = "";
            strSelectSQL_00 = "UPDATE SA_USER SET BAL_REMAIN='" + BalFetch + "' WHERE USERID='" + username + "'";
            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_00);
            DatabaseAccess.database.execSQL(strSelectSQL_00);

            String operationType = String.valueOf(isNetworkConnected());
            String strSelectSQL_01 = "";
            strSelectSQL_01 = "UPDATE COLL_NEN_DATA SET RECPT_FLG=1,SEND_FLG=0,COLL_FLG=1,OPERATION_TYPE='" + operationType + "',BAL_FETCH='" + BalFetch_int + "'";

            strSelectSQL_01 = strSelectSQL_01 + " WHERE REF_REG_NO='" + REF_REG_NO + "' AND TRANS_ID='" + Trans_Id + "'";
            Log.d("Update FLAG", "strSelectSQL_02" + strSelectSQL_01);
            DatabaseAccess.database.execSQL(strSelectSQL_01);
            try {
                String backupData = "INSERT INTO COLL_NEN_DATA_BKP (USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION,SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG,COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH,OPERATION_TYPE,REMARKS,LATTITUDE,LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE)   SELECT USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION,SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG,COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH,OPERATION_TYPE,REMARKS,LATTITUDE,LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE FROM COLL_NEN_DATA WHERE REF_REG_NO='"+ REF_REG_NO +"' AND TRANS_ID='"+ Trans_Id +"'";
                DatabaseAccess.database.execSQL(backupData);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            databaseAccess.close();

            scheduleWork();
            //scheduleBackgrounService();

            refModule.setText("Source : " + REF_MODULE);
            refRegdno.setText("Ref. No. : " + REF_REG_NO);
            custName.setText("Name : " + CON_NAME);
            scNum.setText("SC No. :" + SCNO);
            section.setText("Section : " + SECTION);
            date.setText("Date : " + DATE_TXN);
            amount.setText("Received : ₹ " + AMOUNT);
            transId.setText("Txd ID :" + Trans_Id);
            progressView.setVisibility(View.GONE);

            startpayment.setOnClickListener(v -> {
                try{

                protxt.setText("Preparing receipt to print...");
                progressView.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.GONE);
                printingBlock();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
               /* Thread myThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(SPLASH_TIME_OUT);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                myThread.start();*/
            });
        } catch (Exception exc) {
            exc.printStackTrace();
            progressView.setVisibility(View.GONE);
        }

    }

    private void printingBlock() {
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm = sessiondata.getString("userID", null);
        //to get SBM print
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + username + "'";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        int sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);
        }
        //   Log.d("DemoApp", "strUpdateSQL_01  01");
        rs.close();
        databaseAccess.close();
        ////
        Log.d("Printer flag", "sbmflg" + sbmflg);
        try {


            if (sbmflg == 8) {
                //Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", CUST_ID);
                PrintBun.putString("TransID", Trans_Id.trim());
                PrintBun.putString("type", "O");
                PrintBun.putString("from", "nonen");
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();
            } else if (sbmflg == 5) {
                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", CUST_ID);
                PrintBun.putString("TransID", Trans_Id.trim());
                PrintBun.putString("type", "O");
                PrintBun.putString("from", "nonen");
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();
            } else if (sbmflg == 6) {
                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", CUST_ID);
                PrintBun.putString("TransID", Trans_Id.trim());
                PrintBun.putString("type", "O");
                PrintBun.putString("from", "nonen");
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();
            } else if (sbmflg == 2) {
                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", CUST_ID);
                PrintBun.putString("TransID", Trans_Id.trim());
                PrintBun.putString("type", "O");
                PrintBun.putString("from", "nonen");
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();
            } else {
                Toast.makeText(NonenReceiptGen.this, "Please configure printer", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}