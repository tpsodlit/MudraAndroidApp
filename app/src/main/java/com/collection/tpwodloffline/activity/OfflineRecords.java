package com.collection.tpwodloffline.activity;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.UploadManager;
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
import java.util.concurrent.TimeUnit;

public class OfflineRecords extends AppCompatActivity {

    Button btnUploadPending;
    TextView txtPending, txtUploaded, txtuid, txtdid,txtUploadedOTS, txtPendingOTS, txtuidOTS,
            txtdidOTS;
    private DatabaseAccess databaseAccess=null;
    private String custID = "";
    private String consumerNo = "";
    private String RecptGenURL = null;
    private String TransID="";
    private String strmsg="";
    private String Recfound="";
    private String  usernm="";
    private String ActPayMode="";
    private String CurTime="";
    private String CurTime1="";
    String usname = "";
    String dbpwdnm = "";
    int amountPay = 0;
    String paymode;
    String chqno;
    String chqdate;
    String collmonth;
    String ddno;
    String dddate;
    String BankName;
    String PosTransID;
    String phoneNo;
    String BalRemain;
    String transId;
    String custName;
    private String urlName="";
    private String StrBillInfo="";
    private String  serverDate="";
    private String neftNo="";
    private String neftDate="";
    private String rtgsNo="";
    private String rtgsDate="";
    private String ReceiptNo="";
    private String lattitude = "";
    private String longitude = "";
    private String vtype = "";
    private String collMode = "";

    //OTS
    Button btnUploadPendingOTS;

    SharedPreferenceClass sharedPreferenceClass;
    ProgressDialog progressDialog;
    String CompanyId = CommonMethods.getCompanyID();
    String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_records);

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
        sharedPreferenceClass = new SharedPreferenceClass(OfflineRecords .this);
        progressDialog = new ProgressDialog(OfflineRecords .this);

        //OTS
        btnUploadPendingOTS = findViewById(R.id.btnUploadPendingOTS);
        txtUploadedOTS = findViewById(R.id.txtUploadedOTS);
        txtPendingOTS = findViewById(R.id.txtPendingOTS);
        txtuidOTS = findViewById(R.id.txtuidOTS);
        txtdidOTS = findViewById(R.id.txtdidOTS);

        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String ///

        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        serverDate=sessionssodata.getString("serverDate",null);
        usname = sharedPreferenceClass.getValue_string("un");
        dbpwdnm = sharedPreferenceClass.getValue_string("pw");
        device_id = CommonMethods.getDeviceid(getApplicationContext());

        btnUploadPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("sdfg", "onClick: "+RecptGenURL);

                try {
                    Date dates = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates.getTime()).toString());
                    Date serverDateParse = sdf.parse(serverDate);
                    if ((currentDate.compareTo(serverDateParse) == 0)) {

                        if(getOfflineCount()>0) {
                            uploadOfflineData();
                        }else{
                            Toast.makeText(OfflineRecords.this, "No pending data to upload.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OfflineRecords.this);
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
                                        OfflineRecords.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }

                    }catch (Exception ex){
                    ex.printStackTrace();
                }



                //scheduleWork();
            }
        });

        //OTS implementation
        btnUploadPendingOTS.setOnClickListener(view -> {
            try {
                Date dates = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy",
                        dates.getTime()).toString());
                Date serverDateParse = sdf.parse(serverDate);
                if ((currentDate.compareTo(serverDateParse) == 0)) {

                    ConnectivityManager cm = (ConnectivityManager)
                            getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                        if (new DatabaseAccess().getOfflineCountOTS(this) > 0) {
                            uploadOTSData();
                        } else {
                            Toast.makeText(this,
                                    "No pending OTS data to upload.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this,
                                "No internet connection found",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.
                            Builder(OfflineRecords.this);
                    alertDialogBuilder.setTitle("Please Check Current Date");
                    alertDialogBuilder.setMessage("Change the Date and try again !!")
                            .setCancelable(false)
                            .setPositiveButton("Retry", (dialog, id) -> dialog.cancel())
                            .setNegativeButton("Exit App", (dialog, id) ->
                                    OfflineRecords.this.finish());
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        txtPending.setText(""+getOfflineCount());
        txtUploaded.setText(""+getUploadedCount());
        txtuid.setText("("+usname+" : ");
        txtdid.setText(device_id+")");

        //OTS
        txtPendingOTS.setText("" + new DatabaseAccess().getOfflineCountOTS(this));
        txtUploadedOTS.setText("" + new DatabaseAccess().getUploadedCountOTS(this));
        txtuidOTS.setText("(" + usname + " : ");
        txtdidOTS.setText(device_id + ")");
    }

    private int getOfflineCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0 AND RECPT_FLG='1'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private int getUploadedCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=1 AND RECPT_FLG='1'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void scheduleBackgrounService(){
        String TAG_NAME= "uploadData";
        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(UploadManager.class, 30, TimeUnit.MINUTES)
                        .addTag(TAG_NAME)
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(TAG_NAME, ExistingPeriodicWorkPolicy.KEEP,
                periodicSyncDataWork);
        WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag(TAG_NAME);
    }

    private void scheduleWork(){


        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadManager.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);

        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(uploadWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                    }
                });
    }

    private void uploadOfflineData(){

        resetStrings();
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "Select" +
                " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH, A.TOT_PAID,TRANS_ID,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,RTGS_DATE,A.DB_TYPE_SERVER,A.LATTITUDE,A.LONGITUDE,A.OPERATION_TYPE,A.SPINNER_NON_ENERGY" + //51
                " FROM " +
                " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and SEND_FLG = '0' AND RECPT_FLG= '1'";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while (rs.moveToNext()) {
            consumerNo=rs.getString(0);
            custID=rs.getString(1);
            custName = rs.getString(5);
            paymode=rs.getString(23);
            chqno=rs.getString(24);
            chqdate=rs.getString(25);
            collmonth= rs.getString(10);
            ddno=rs.getString(26);
            dddate= rs.getString(27);
            BankName=rs.getString(37);
            PosTransID=rs.getString(38);
            phoneNo=rs.getString(39);
            BalRemain=rs.getString(40);
            amountPay = rs.getInt(41);
            transId = ""+rs.getString(42);
            neftNo=rs.getString(43);
            neftDate=rs.getString(44);
            rtgsNo=rs.getString(45);
            rtgsDate=rs.getString(46);
            lattitude = rs.getString(48);
            longitude = rs.getString(49);
            vtype = rs.getString(50);
            collMode = rs.getString(51);
            String dt= rs.getString(18);
            String tm = rs.getString(19);
            if(dt.contains("-")){
                dt= dt.replace("-","");
            }
            if(tm.contains(":")){
                tm = tm.replace(":","");
            }
            CurTime = dt+tm;
            String BillType = "";
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
                    default:
                        BillType = "E";
                        break;
                }
            }
           /* if(pay_cnt.equals("0")){
                BillType = "B";
            }else if(pay_cnt.equals("1")) {
                BillType = "A";
            }else if(pay_cnt.equals("2")){
                BillType = "C";
            }else {
                BillType = "D";
            }*/
            ReceiptNo = BillType+rs.getString(1)+rs.getString(10);

          /*  if(!dddate.equals("null")){
               dddate =  CommonMethods.getFormattedDate1(dddate);
            }else {
                dddate =  "null";

            }*/

        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
        Date date = new Date();
        //CurTime=dateFormat.format(date);

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String imeinum=sessiondata.getString("imeinum", null);
        String device_id = CommonMethods.getDeviceid(getApplicationContext());

        SharedPreferences sessiondata1 = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor editor1 = sessiondata.edit();
        //usname=sessiondata1.getString("usname", null);
      //  dbpwdnm=sessiondata1.getString("dbpwdnm", null);


        Log.d("userName", ""+usname);
        Log.d("pwd", ""+dbpwdnm);
        Log.d("Phone", ""+phoneNo);
        Log.d("collmonth", ""+collmonth);
        Log.d("TransactionId", ""+transId);
        Log.d("BankName", ""+BankName);
        Log.d("imei", ""+imeinum);
        Log.d("AmountToPay", ""+amountPay);
        Log.d("CurrentTime", ""+CurTime);
        Log.d("DDNo", ""+ddno);
        Log.d("DDDate", ""+dddate);
        Log.d("ChequeNo", ""+chqno);
        Log.d("consumerNo", ""+consumerNo);
        Log.d("ReceiptNo", ""+ReceiptNo);

        //RecptGenURL = ServerLinks.CollInfo;
        //phoneNo added on 04/06/2021 by Sradhendu
        RecptGenURL = ServerLinks.postPayment;
        // RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=3&ConsumerID="+custID+"&deviceId="+device_id+"&RefID=0&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+CommonMethods.getFormattedDate1(dddate)+"&PaymentMthh=0&BBPS=0&OffLine=1";
        CompanyId = CommonMethods.CompanyID;

        if(paymode.equals("2")){//dd
            ActPayMode="3";//      "+strTransDt+"
            //RecptGenURL = RecptGenURL+un=atul&pw=atulkumar&companyID=3&ConsumerID=2921028800&deviceId=9935cbbda6b4c723&RefID=0&Amount=100&DateTime=05-05-2021&PayMod=CASH&RecNo=124&BankName=NA&Ins_No=NA&ClearDate=05-05-2021&PaymentMthh=05&BBPS=0&OffLine=1
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }else if(paymode.equals("3")){//chq
            ActPayMode="2";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }else if(paymode.equals("7")) {//Pos
            ActPayMode = "7";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        else if(paymode.equals("8")) {//NEFT
            ActPayMode = "8";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        else if(paymode.equals("9")) {//RTGS
            ActPayMode = "9";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        } else if(paymode.equals("0")){//cash
            ActPayMode="1";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);            // new OfflineRecords.ReceiptGenOnline().execute(RecptGenURL);
        }else {
            ActPayMode="1";
            // RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        Log.d("Url:::", RecptGenURL);

        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            new ReceiptGenOnline().execute(RecptGenURL);
        }else {
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
       // new OfflineRecords.ReceiptGenOnline().execute(RecptGenURL);


    }
    private void resetStrings(){
        custID = "";
        custName = "";
        TransID = "";
        strmsg = "";
        Recfound = "";
        usernm = "";
        ActPayMode = "";
        CurTime = "";
        CurTime1 = "";
        amountPay = 0;
        paymode = "";
        chqno = "";
        chqdate = "";
        ddno = "";
        dddate = "";
        BankName = "";
        PosTransID = "";
        phoneNo = "";
        transId = "";
        lattitude = "";
        longitude = "";
        vtype = "";
        BalRemain = "";
        collMode = "";

    }
    private class ReceiptGenOnline extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];

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
               /* Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
               /* Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);*/
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {

            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String strSelectSQL_01 ="";
                strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MACHINE_NO=1";
                strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='"+ custID +"' AND TRANS_ID='"+ TransID +"'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            }else{
            }
        }

        @Override
        protected void onPostExecute(String str) {

            try {
                Log.d("DemoApp", " str   " + str);
                strmsg=str;
                String pipeDelRecptInfo =str;
                String[] RecptInfo = pipeDelRecptInfo.split("[|]");
                String ConsValid=RecptInfo[0];
                String imeiflg=RecptInfo[1];
                String Txn_svr = RecptInfo[3];
                String recprno="";
                String RecptDt="";
                String RecptTime="";
                //Recfound=RecptInfo[2];;

                if(ConsValid.equals("1") && imeiflg.equals("1")){
                    recprno=RecptInfo[3];
                    RecptDt=RecptInfo[4];
                    /*RecptTime=RecptDt.substring(8,14);
                    //RecptDt=RecptDt.substring(0,2)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(4,8);
                    RecptDt=RecptDt.substring(4,8)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(0,2);
*/
                    RecptTime=RecptDt.substring(8,14);
                    //RecptTime="071518";
                    //RecptDt=RecptDt.substring(0,2)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(4,8);
                    RecptDt=RecptDt.substring(4,8)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(0,2);
                    Log.d("DemoApp", " RecptDt   " + RecptDt);
                    Log.d("DemoApp", " RecptTime   " + RecptTime);
                    int balremain=0;
                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String strSelectSQL_01 ="";
                    strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MR_NO='"+ recprno +"',RECPT_DATE=strftime('%Y-%m-%d', '"+ RecptDt +"'),RECPT_FLG=1,SEND_FLG=1,coll_flg=1,RECPT_TIME='"+ RecptTime +"'";

                    strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='"+ custID +"' AND TRANS_ID='"+ Txn_svr +"'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();
                   // progressDialog.dismiss();
                    if(getOfflineCount()>0) {
                        uploadOfflineData();
                    }else{
                        Toast.makeText(OfflineRecords.this, "Data has been uploaded successfully...", Toast.LENGTH_SHORT).show();
                    }
                    txtPending.setText(""+getOfflineCount());
                    txtUploaded.setText(""+getUploadedCount());
                }
            }catch (Exception ex){
                Toast.makeText(OfflineRecords.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }


        }
    }

    //OTS implementation
    private void uploadOTSData() {

        String email = "", reason = "", remarks = "",
                otskey = "", otsrefno = "", installmentNo = "";

        resetStrings();

        usname = sharedPreferenceClass.getValue_string("un");
        dbpwdnm = sharedPreferenceClass.getValue_string("pw");

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        String query = "Select" +
                " A.CONS_ACC, A.CUST_ID, A.OTSKey, A.OTSReferenceNo, " +
                " A.TotalInstallment, A.InstallmentNo, " +
                " A.InstallmentAmount, A.InstallmentDate, A.InstallmentDueDate, " +
                " A.CON_NAME, A.CON_ADD1, A.CON_ADD2, " +
                " strftime('%d-%m-%Y',A.RECPT_DATE) RECPT_DATE," +
                " A.RECPT_TIME, A.MR_No, A.MACHINE_NO, A.TOT_PAID, A.PAY_MODE, " +
                " A.DD_NO, strftime('%d-%m-%Y',A.DD_DATE) DD_DATE, A.Bank_ID," +
                " A.RECPT_FLG, A.SEND_FLG, A.DEL_FLG, " +
                " A.COLL_FLG, A.PMT_TYP, b.bank_name as BankName, A.PHONE_NO, A.BAL_FETCH, " +
                " A.TRANS_ID, A.DB_TYPE_SERVER, " +
                " A.LATTITUDE, A.LONGITUDE, A.SPINNER_NON_ENERGY, " +
                " A.REASON, A.REMARKS, A.EMAIL " +
                " FROM OTSConsumerDataUpload A, mst_bank b WHERE " +
                " a.bank_id=b.bank_id and RECPT_FLG='1' " +
                " and SEND_FLG = '0'";

        Cursor rs = DatabaseAccess.database.rawQuery(query, null);
        while (rs.moveToNext()) {
            otskey = rs.getString(rs.getColumnIndexOrThrow("OTSKey"));
            otsrefno = rs.getString(rs.getColumnIndexOrThrow("OTSReferenceNo"));
            installmentNo = rs.getString(rs.getColumnIndexOrThrow("InstallmentNo"));

            consumerNo = rs.getString(rs.getColumnIndexOrThrow("CONS_ACC"));
            custID = rs.getString(rs.getColumnIndexOrThrow("CUST_ID"));
            custName = rs.getString(rs.getColumnIndexOrThrow("CON_NAME"));

            paymode = rs.getString(rs.getColumnIndexOrThrow("PAY_MODE"));
            if (paymode.equals("") || paymode.equals("Null") || paymode.equals("NULL")) {
                paymode = "1";
            }
            collmonth = "";
            ddno = rs.getString(rs.getColumnIndexOrThrow("DD_NO"));
            if (ddno == null)
                ddno = "";
            dddate = rs.getString(rs.getColumnIndexOrThrow("DD_DATE"));
            if (dddate == null)
                dddate = "";
            BankName = rs.getString(rs.getColumnIndexOrThrow("BankName"));
            phoneNo = "";
            BalRemain = rs.getString(rs.getColumnIndexOrThrow("BAL_FETCH"));
            amountPay = rs.getInt(rs.getColumnIndexOrThrow("TOT_PAID"));
            transId = rs.getString(rs.getColumnIndexOrThrow("TRANS_ID"));
            lattitude = rs.getString(rs.getColumnIndexOrThrow("LATTITUDE"));
            longitude = rs.getString(rs.getColumnIndexOrThrow("LONGITUDE"));
            collMode = rs.getString(rs.getColumnIndexOrThrow("SPINNER_NON_ENERGY"));
            vtype = "FG";
            String d = rs.getString(rs.getColumnIndexOrThrow("RECPT_DATE"));
            String t = rs.getString(rs.getColumnIndexOrThrow("RECPT_TIME"));
            if (d.contains("-")) {
                d = d.replace("-", "");
            }
            if (t.contains(":")) {
                t = t.replace(":", "");
            }
            CurTime = d + t;

            ReceiptNo = new DatabaseAccess().
                    receiptNoOTS(getApplicationContext(), custID, transId);
            email = rs.getString(rs.getColumnIndexOrThrow("EMAIL"));
            if (email == null)
                email = "";
            reason = rs.getString(rs.getColumnIndexOrThrow("REASON"));
            if (reason == null)
                reason = "";
            remarks = rs.getString(rs.getColumnIndexOrThrow("REMARKS"));
            if (remarks == null)
                remarks = "";
        }
        String device_id = CommonMethods.getDeviceid(getApplicationContext());
        RecptGenURL = ServerLinks.postPaymentOTS;
        CompanyId = CommonMethods.CompanyID;
        ActPayMode = CommonMethods.getActMode(paymode);
        if (!otskey.isEmpty()) {
            String postData = usname + "|" + dbpwdnm + "|" + CompanyId + "|"
                    + custID + "|" + device_id + "|" + ReceiptNo + "|" + amountPay
                    + "|" + CurTime + "|" + ActPayMode + "|" + transId + "|"
                    + BankName + "|" + ddno + "|" + (dddate) + "|" + (collmonth)
                    + "|" + phoneNo + "|0|1" + "|" + consumerNo + "|" + lattitude
                    + "|" + longitude + "|" + vtype + "|" + custName + "|" + BalRemain
                    + "|" + collMode + "|" + reason + "|" + remarks + "|" + email
                    + "|" + otskey + "|" + otsrefno + "|" + installmentNo;

            RecptGenURL = RecptGenURL + "encKey=" + CommonMethods.encryptText(postData);

            Log.d("Url:::", RecptGenURL);

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                new UploadOTSDataAsync().execute(RecptGenURL);
            }
        }
    }

    private class UploadOTSDataAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String strURL = params[0];
            String bodycontent = null, inputLine;
            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(uc.getInputStream()));
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
        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                int rows = new DatabaseAccess().
                        updateMachineNoOTS(getApplicationContext(), custID, transId);
                Log.d("MachineNo OTS", "Rows updated OTS::: " + rows);
            }
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                String[] RecptInfo = str.split("[|]");
                String ConsValid = RecptInfo[0];
                String imeiflg = RecptInfo[1];
                String Txn_Svr = RecptInfo[3];
                String recprno, RecptDt, RecptTime;

                if (ConsValid.equals("1") && imeiflg.equals("1")) {
                    recprno = RecptInfo[3];
                    RecptDt = RecptInfo[4];
                    RecptTime = RecptDt.substring(8, 14);

                    RecptDt = RecptDt.substring(4, 8) + "-"
                            + RecptDt.substring(2, 4) + "-"
                            + RecptDt.substring(0, 2);
                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String query = "UPDATE OTSConsumerDataUpload SET MR_NO='" + recprno
                            + "',RECPT_DATE=strftime('%Y-%m-%d', '" + RecptDt + "')," +
                            "RECPT_FLG=1, SEND_FLG=1, " +
                            "coll_flg=1, RECPT_TIME='" + RecptTime + "'";

                    query = query + " WHERE CUST_ID='" + custID + "' AND " +
                            "TRANS_ID='" + Txn_Svr + "'";
                    DatabaseAccess.database.execSQL(query);
                    databaseAccess.close();
                    if (new DatabaseAccess().
                            getOfflineCountOTS(OfflineRecords.this) > 0) {
                        uploadOTSData();
                    } else {
                        Toast.makeText(OfflineRecords.this,
                                "OTS Data has been uploaded successfully...",
                                Toast.LENGTH_SHORT).show();
                    }
                    txtPendingOTS.setText("" + new DatabaseAccess().
                            getOfflineCountOTS(OfflineRecords.this));
                    txtUploadedOTS.setText("" + new DatabaseAccess().
                            getUploadedCountOTS(OfflineRecords.this));
                }
            } catch (Exception ex) {
                Toast.makeText(OfflineRecords.this,
                        "OTS Data Uploading Failed :: " + ex,
                        Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
    }
}
