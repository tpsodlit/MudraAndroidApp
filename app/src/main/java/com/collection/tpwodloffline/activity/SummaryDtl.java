package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SummaryDtl extends AppCompatActivity {
    private DatabaseAccess databaseAccess = null;
    final Context context = this;
    private static TextView amount;
    private static TextView strAccNo;
    private static TextView strAmtRec;
    private static TextView strpaymode;
    private static TextView strddno;
    private static TextView strdddate;
    private static TextView strchqno;
    private static TextView strchqdt;
    private static TextView strbankname;
    private static TextView strposidname;
    private static TextView custName;
    private String strPhoneNo = "";
    private String vstrCons_no = "";
    private String vstrpayamt = "";
    private String vstrchqno = "";
    private String vstrchqdt = "";
    private String Paymode = "";
    private String BankName = "";
    private String posidName = "";
    private String BankID = "";
    private String custID = "";
    String SelChoice = "";
    String TransID = "";
    String BalFetch = "";
    private String namefetch = "";
    private String MobileNofetch = "";

    private static EditText strtxtPhoneNo;

    private TableRow trneft;
    private TextView neftno1;
    private TextView neftno;
    private TableRow neft_date;
    private TextView neftdate1;
    private TextView neftdate;
    private TableRow rtgs;
    private TextView rtgsno1;
    private TextView rtgsno;
    private TableRow rtgs_date;
    private TextView rtgsdate1;
    private TextView rtgsdate;
    private String micrNo = "";
    private TableRow tr_micr;
    private TextView tv_micr;
    private Button GenRecp;
    private TextView moneyId;
    private TextView money_value;
    private String moneyIdd = "";
    private String fromActivity = "";
    private String payFlag = "";
    Timer timer;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
     /*   if (timer != null) {
            timer.cancel();
            // Log.i("Main", "cancel timer");
            timer = null;
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
     /*   timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 15 *60 * 1000); //auto logout in 5 minutes*/
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(SummaryDtl.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_dtl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strAccNo = (TextView) findViewById(R.id.AccNo);
        amount = (TextView) findViewById(R.id.textView5);
        strAmtRec = (TextView) findViewById(R.id.AmtRec);
        strpaymode = (TextView) findViewById(R.id.paymode);
        strddno = (TextView) findViewById(R.id.ddno);
        strdddate = (TextView) findViewById(R.id.dddate);
        strchqno = (TextView) findViewById(R.id.chqno);
        strchqdt = (TextView) findViewById(R.id.chqdt);
        strbankname = (TextView) findViewById(R.id.bankname);
        moneyId = findViewById(R.id.moneyId);
        money_value = findViewById(R.id.money_value);

        strposidname = (TextView) findViewById(R.id.posidname);
        GenRecp = (Button) findViewById(R.id.GenRecp);
        TextView ddno1 = (TextView) findViewById(R.id.ddno1);
        TextView dddate1 = (TextView) findViewById(R.id.dddate1);
        TextView chqno1 = (TextView) findViewById(R.id.chqno1);
        TextView chqdt1 = (TextView) findViewById(R.id.chqdt1);
        TextView bankname1 = (TextView) findViewById(R.id.bankname1);
        TextView posidname1 = (TextView) findViewById(R.id.posidname1);
        Button btnBackprnt = (Button) findViewById(R.id.back);
        tv_micr = findViewById(R.id.tv_micr);
        tr_micr = findViewById(R.id.tr_micr);
        custName = (TextView) findViewById(R.id.custName);
        strtxtPhoneNo = (EditText) findViewById(R.id.PhoneNo);

        GenRecp.setClickable(true);
        GenRecp.setEnabled(true);

        trneft = findViewById(R.id.trneft);
        neftno1 = findViewById(R.id.neftno1);
        neftno = findViewById(R.id.neftno);
        neft_date = findViewById(R.id.neft_date);
        neftdate1 = findViewById(R.id.neftdate1);
        neftdate = findViewById(R.id.neftdate);
        rtgs = findViewById(R.id.rtgs);
        rtgsno1 = findViewById(R.id.rtgsno1);
        rtgsno = findViewById(R.id.rtgsno);
        rtgs_date = findViewById(R.id.rtgs_date);
        rtgsdate1 = findViewById(R.id.rtgsdate1);
        rtgsdate = findViewById(R.id.rtgsdate);


        try {
            Bundle pmtsmry = getIntent().getExtras();
            vstrCons_no = pmtsmry.getString("vstrCons_no");
            vstrpayamt = pmtsmry.getString("vstrpayamt");
            vstrchqno = pmtsmry.getString("vstrchqno");
            vstrchqdt = pmtsmry.getString("vstrchqdt");
            Paymode = pmtsmry.getString("Paymode");

            Log.d("dsfgh", "onCreate: " + Paymode);

            BankName = pmtsmry.getString("BankName");
            posidName = pmtsmry.getString("PosID");
            BankID = pmtsmry.getString("BankID");
            custID = pmtsmry.getString("custID");
            TransID = pmtsmry.getString("TransID");
            SelChoice = pmtsmry.getString("SelChoice");
            BalFetch = pmtsmry.getString("BalFetch");
            namefetch = pmtsmry.getString("namefetch");
            MobileNofetch = pmtsmry.getString("MobileNofetch");
            micrNo = pmtsmry.getString("micr_no");
            moneyIdd = pmtsmry.getString("moneyId");
            fromActivity = pmtsmry.getString("from");
            payFlag = pmtsmry.getString("PayFlag");
            Log.d("Summary Detail:",payFlag);

            money_value.setText(moneyIdd);


            if (MobileNofetch.length() < 10) {
                strtxtPhoneNo.setVisibility(EditText.GONE);
                strtxtPhoneNo.setText(MobileNofetch);
                strtxtPhoneNo.setEnabled(false);
            } else {
                strtxtPhoneNo.setText(MobileNofetch);
                strtxtPhoneNo.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strAccNo.setText(vstrCons_no);
        strAmtRec.setText(vstrpayamt);
        amount.setText("Have You Received = " + vstrpayamt);
        custName.setText(namefetch);
        strpaymode.setText(Paymode);

        try {
            double BalFetch_int = Integer.parseInt(BalFetch) - Integer.parseInt(vstrpayamt);
            BalFetch = String.valueOf(BalFetch_int);

        }catch (Exception ex){
            ex.printStackTrace();
        }



        if (Paymode.equals("chq")) {
            strchqno.setText(vstrchqno);
            strchqdt.setText(vstrchqdt);
            if (BankName.equalsIgnoreCase("SELECT BANK")) {
                strbankname.setText("");
            } else {
                strbankname.setText(BankName);
            }


            tv_micr.setText(micrNo);

        } else if (Paymode.equals("dd")) {
            strddno.setText(vstrchqno);
            strdddate.setText(vstrchqdt);

            if (BankName.equalsIgnoreCase("SELECT BANK")) {
                strbankname.setText("");
            } else {
                strbankname.setText(BankName);
            }
            tv_micr.setText(micrNo);
        } else if (Paymode.equals("NEFT")) {
            neftno.setText(vstrchqno);
            neftdate.setText(vstrchqdt);
            if (BankName.equalsIgnoreCase("SELECT BANK")) {
                strbankname.setText("");
            } else {
                strbankname.setText(BankName);
            }
        } else if (Paymode.equals("RTGS")) {
            rtgsno.setText(vstrchqno);
            rtgsdate.setText(vstrchqdt);
            if (BankName.equalsIgnoreCase("SELECT BANK")) {
                strbankname.setText("");
            } else {
                strbankname.setText(BankName);
            }
        } else if (Paymode.equals("pos")) {
            strposidname.setText(posidName);
        }

        if (Paymode.equals("chq")) {
            chqno1.setVisibility(EditText.VISIBLE);
            chqdt1.setVisibility(EditText.VISIBLE);
            bankname1.setVisibility(EditText.VISIBLE);
            strchqno.setVisibility(EditText.VISIBLE);
            strchqdt.setVisibility(EditText.VISIBLE);
            strbankname.setVisibility(EditText.VISIBLE);
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            posidname1.setVisibility(EditText.GONE);
            strposidname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.VISIBLE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);
        } else if (Paymode.equals("dd")) {
            ddno1.setVisibility(EditText.VISIBLE);
            dddate1.setVisibility(EditText.VISIBLE);
            strddno.setVisibility(EditText.VISIBLE);
            strdddate.setVisibility(EditText.VISIBLE);
            bankname1.setVisibility(EditText.VISIBLE);
            strbankname.setVisibility(EditText.VISIBLE);
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            posidname1.setVisibility(EditText.GONE);
            strposidname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.VISIBLE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);
        } else if (Paymode.equals("NEFT")) {
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            strddno.setVisibility(EditText.GONE);
            strdddate.setVisibility(EditText.GONE);
            bankname1.setVisibility(EditText.VISIBLE);
            strbankname.setVisibility(EditText.VISIBLE);
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            posidname1.setVisibility(EditText.GONE);
            strposidname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.VISIBLE);
            neft_date.setVisibility(EditText.VISIBLE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.GONE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);
        } else if (Paymode.equals("RTGS")) {
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            strddno.setVisibility(EditText.GONE);
            strdddate.setVisibility(EditText.GONE);
            bankname1.setVisibility(EditText.VISIBLE);
            strbankname.setVisibility(EditText.VISIBLE);
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            posidname1.setVisibility(EditText.GONE);
            strposidname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.VISIBLE);
            rtgs_date.setVisibility(EditText.VISIBLE);
            tr_micr.setVisibility(View.GONE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);
        } else if (Paymode.equals("pos")) {
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            bankname1.setVisibility(EditText.GONE);
            strddno.setVisibility(EditText.GONE);
            strdddate.setVisibility(EditText.GONE);
            strchqno.setVisibility(EditText.GONE);
            strchqdt.setVisibility(EditText.GONE);
            strbankname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.GONE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);

        } else if (Paymode.equals("money")) {
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            bankname1.setVisibility(EditText.GONE);
            strddno.setVisibility(EditText.GONE);
            strdddate.setVisibility(EditText.GONE);
            strchqno.setVisibility(EditText.GONE);
            strchqdt.setVisibility(EditText.GONE);
            strbankname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.GONE);
            posidname1.setVisibility(View.GONE);
            posidname1.setVisibility(View.GONE);

            moneyId.setVisibility(View.VISIBLE);
            money_value.setVisibility(View.VISIBLE);
        } else {
            chqno1.setVisibility(EditText.GONE);
            chqdt1.setVisibility(EditText.GONE);
            ddno1.setVisibility(EditText.GONE);
            dddate1.setVisibility(EditText.GONE);
            bankname1.setVisibility(EditText.GONE);
            strddno.setVisibility(EditText.GONE);
            strdddate.setVisibility(EditText.GONE);
            strchqno.setVisibility(EditText.GONE);
            strchqdt.setVisibility(EditText.GONE);
            strbankname.setVisibility(EditText.GONE);
            posidname1.setVisibility(EditText.GONE);
            strposidname.setVisibility(EditText.GONE);
            trneft.setVisibility(EditText.GONE);
            neft_date.setVisibility(EditText.GONE);
            rtgs.setVisibility(EditText.GONE);
            rtgs_date.setVisibility(EditText.GONE);
            tr_micr.setVisibility(View.GONE);
            moneyId.setVisibility(View.GONE);
            money_value.setVisibility(View.GONE);
        }

        GenRecp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usernm = "";
                SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                SharedPreferences.Editor editor = sessiondata.edit();
                String toDayDt = sessiondata.getString("toDayDt", null); // getting String
                usernm = sessiondata.getString("userID", null);

                Date initDate = null;
                try {
                    initDate = new SimpleDateFormat("dd-mm-yyyy").parse(toDayDt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String parsedDate = formatter.format(initDate);
                String strsql1 = "";
                int Pay_ID = 0;
                String convertdt = "";
                strPhoneNo = strtxtPhoneNo.getText().toString();
                if(MobileNofetch.isEmpty()){
                    MobileNofetch = "9999999999";
                }

                if (TextUtils.isEmpty(MobileNofetch)) {

                    //strtxtPhoneNo.setError("Enter  Mobile No");
                    //  Log.d("DemoApp", "Query SQL " + strRefNum);
                } else {
                    if (Paymode.equals("chq")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        Pay_ID = 3;// do not mix up with existing paymode
                        strsql1 = ",CHEQUE_NO='" + vstrchqno + "',CHEQUE_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                    } else if (Paymode.equals("dd")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",DD_NO='" + vstrchqno + "',DD_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 2;// do not mix up with existing paymode
                    } else if (Paymode.equals("NEFT")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",NEFT_NO='" + vstrchqno + "',NEFT_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 8;// do not mix up with existing paymode
                    } else if (Paymode.equals("RTGS")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",RTGS_NO='" + vstrchqno + "',RTGS_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 9;// do not mix up with existing paymode
                    } else if (Paymode.equals("money")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        Pay_ID = 1;// do not mix up with existing paymode
                        strsql1 = ",MONEY_RECPT_ID='" + moneyIdd + "',MONEY_RECPT_DATE =strftime('%Y-%m-%d','" + convertdt + "'),MONEY_TYPE='" + "MR" + "'";
                        BankID = "0";//for cash collection
                    } else if (Paymode.equals("pos")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",POS_TRANS_ID='" + posidName + "',DD_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 7;
                    } else {
                        BankID = "0";//for cash collection
                        Pay_ID = 1;

                    }

                    if (BankID.equalsIgnoreCase("31")) {

                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();

                        String strSelectSQL_01 = "";
                        int cnt_BankId = 0;

                        strSelectSQL_01 = "SELECT  COUNT(1) from mst_Bank ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                        //  DatabaseAccess.database.execSQL(strSelectSQL_01);

                        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                        while (rs.moveToNext()) {
                            cnt_BankId = rs.getInt(0);
                        }
                        cnt_BankId = cnt_BankId + 1;
                        strSelectSQL_01 = "";
                        strSelectSQL_01 = "INSERT  INTO mst_Bank (bank_Id,Bank_Name,Status) VALUES ('" + cnt_BankId + "','" + BankName + "',1)  ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                        DatabaseAccess.database.execSQL(strSelectSQL_01);

                        BankID = String.valueOf(cnt_BankId);
                        databaseAccess.close();
                    }

                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    ContentValues cv = new ContentValues();
                    cv.put("BAL_REMAIN", BalFetch);

                    int rowCount = DatabaseAccess.database.update("SA_USER",
                            cv, "USERID=?", new String[]{usernm});

                    if (rowCount > 0) {
                        //OTS Update
                        if (payFlag.contentEquals("OTS")) {
                            try {
                                String queryOTS = "UPDATE OTSConsumerDataUpload " +
                                        "SET TOT_PAID='" + vstrpayamt
                                        + "',PAY_MODE=" + Pay_ID + ",PMT_TYP='" +
                                        SelChoice + "', BANK_ID" +
                                        "='" + BankID + "',BAL_FETCH='" + BalFetch + "', " +
                                        "MICR_NO='" + micrNo + "'" + strsql1;

                                queryOTS = queryOTS + " WHERE CUST_ID='" + custID
                                        + "' AND TRANS_ID='" + TransID + "'";
                                DatabaseAccess.database.execSQL(queryOTS);
                                DatabaseAccess.getInstance(context)
                                        .updateFIELD1ForPaymentDone(context,custID,TransID);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            String query = "UPDATE COLL_SBM_DATA SET TOT_PAID='" + vstrpayamt
                                    + "',PAY_MODE=" + Pay_ID + ",PMT_TYP='" +
                                    SelChoice + "', BANK_ID" +
                                    "='" + BankID + "',BAL_FETCH='" + BalFetch + "', " +
                                    "MICR_NO='" + micrNo + "'" + strsql1;

                            query = query + " WHERE CUST_ID='" + custID
                                    + "' AND TRANS_ID='" + TransID + "'";
                            DatabaseAccess.database.execSQL(query);
                        }
                    } else {
                        Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseAccess.close();


                    SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
                    SharedPreferences.Editor ssodata = sessionssodata.edit();
                    String serverDate = sessionssodata.getString("serverDate", null);
                    String sdate = null;
                    SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
                    Date date;
                    try {
                        date = originalFormat.parse(serverDate);
                        sdate = targetFormat.format(date);
                        System.out.println("Old Format :   " + originalFormat.format(date));
                        System.out.println("New Format :   " + targetFormat.format(date));

                    } catch (ParseException ex) {
                        // Handle Exception.
                    }

                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();

                    String strSelectSQL_01 = "";
                    int cnt_BankId = 0;
                    strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET TOT_PAID='" + vstrpayamt + "',PAY_MODE=" + Pay_ID + ",PMT_TYP='" + SelChoice + "',BANK_ID='" + BankID + "',BAL_FETCH='" + BalFetch + "',MICR_NO='" + micrNo + "'" + strsql1;

                    strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + TransID + "'";

                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    //added on 10062019//
                    strSelectSQL_01 = "";
                    strSelectSQL_01 = "UPDATE SA_USER SET BAL_REMAIN='" + BalFetch + "' WHERE USERID='" + usernm + "'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);

                    databaseAccess.close();
                    //
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date sysDate = new Date();
                        Date date1 = sdf.parse(sdf.format(sysDate));
                        Date date2 = sdf.parse(sdate);
                        Log.d("DemoApp", "date1" + sdf.format(date1));
                        Log.d("DemoApp", "date2" + sdf.format(date2));
                        Log.d("DemoApp", "date2" + toDayDt);
                        Log.d("DemoApp", "date2" + sdate);
                        if (date1 == null || date1.equals("") || date1.equals(" ")) {
                            date1 = date2;
                        }
                        if (date1.compareTo(date2) > 0) {
                            Log.d("DemoApp", "Date1 is after Date2");
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Please Check Current Date");
                            alertDialogBuilder.setMessage("Change the Date and Re-Generate")
                                    .setCancelable(false)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SummaryDtl.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();

                        } else if (date1.compareTo(date2) == 0) {
                            Log.d("DemoApp", "Date1 is before Date2");
                       /*     ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){*/

                            GenRecp.setClickable(false);
                            GenRecp.setEnabled(false);

                            Intent RecptGenIntent = new Intent(getApplicationContext(), ReceiptGen.class);
                            Bundle RecptBun = new Bundle();
                            RecptBun.putString("custID", custID);
                            RecptBun.putString("vstrpayamt", vstrpayamt);
                            RecptBun.putString("TransID", TransID);
                            RecptBun.putString("BankName", BankName);
                            RecptBun.putString("BalFetch", BalFetch);
                            RecptBun.putString("MobileNofetch", MobileNofetch);
                            RecptBun.putString("micr_no", micrNo);
                            RecptBun.putString("from", fromActivity);
                            RecptBun.putString("payFlag", payFlag);
                            Log.d("In summaryDetail Class PayFlag",payFlag);
                            RecptGenIntent.putExtras(RecptBun);

                            System.out.println("summary====" + custID);
                            startActivity(RecptGenIntent);
                            finish();
                            //   }

                   /*         else{
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
                                                SummaryDtl.this.finish();
                                            }
                                        });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }*/
                        } else {
                            Toast.makeText(SummaryDtl.this, "Please correct the system date", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnBackprnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                startActivity(accountinfo);
                finish();*/

                if (fromActivity.equalsIgnoreCase("non-account")) {
                    startActivity(new Intent(getApplicationContext(), NoNAccountActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), AcCollection.class));
                    finish();
                }


            }
        });
    }

    @Override
    public void onBackPressed() {

        if (fromActivity.equalsIgnoreCase("non-account")) {
            startActivity(new Intent(getApplicationContext(), NoNAccountActivity.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), AcCollection.class));
            finish();
        }


        /*Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
        accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(accountinfo);
        finish();*/
    }

}
