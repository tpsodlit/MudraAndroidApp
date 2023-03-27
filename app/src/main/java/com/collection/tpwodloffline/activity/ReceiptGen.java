package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAmigoThermalNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicImpactNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicThermalNew;
import com.collection.tpwodloffline.UploadManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptGen extends AppCompatActivity {
    private String custID = "";
    private String vstrpayamt = "";
    final Context context = this;
    private String RecptGenURL = null;
    private String TransID="";
    private String BalFetch="";
    private String strCompanyId="";
    private String imeinum="";
    private String BankName="";
    private DatabaseAccess databaseAccess=null;
    private String  paymode="";
    private String  chqno="";
    private String  chqdate="";
    private String  ddno="";
    private String  dddate="";
    private static TextView strRecptNo ;
    private static TextView strAmtPaid ;
    private static TextView strRecptDT ;
    private static TextView strcustid;
    private static TextView strtransID;
    private static TextView strRecptMsg;
    private  Button printrcptBtn ;
    private  Button RegenBtn ;
    private String Recfound="";
    private String  usernm="";
    private String CurTime="";
    private String CurTime1="";
    private String ActPayMode="";
    private String PosTransID="";
    private String phoneNo="";
    private String BalRemain="";
    private String strmsg="";
    private String Usernm ="";
    private Cursor rs=null;
    private String usname="";
    private  String dbpwdnm="";
    private String strTransDt="";
    private String conType="";
    private String urlName="";
    private String neftDate="";
    private String neftNO="";
    private String rtgsNo="";
    private String rtgsDate="";
    private String micrNo="";
    private String fromNonAccount="";
    private int count=0;
    private String moneyId="";
    private String moneyDate="";
    private String ReceiptNo="";
    private String payFlag = "";

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_gen);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String


       // RecptGenURL = urlName+"IncomingSMS/CESU_CollInfo.jsp?"; // need to modify

        //RecptGenURL = urlName+"IncomingSMS_tst/TPCODL_CollInfo.jsp?"; // need to modify


        Button btnBackprnt = (Button) findViewById(R.id.backbtn);
        RegenBtn = (Button) findViewById(R.id.RegenBtn);
        printrcptBtn = (Button) findViewById(R.id.printrcptBtn);
        strRecptNo=(TextView)findViewById(R.id.RecptNo);
        strAmtPaid=(TextView)findViewById(R.id.AmtPaid);
        strRecptDT=(TextView)findViewById(R.id.RecptDT);
        strcustid=(TextView)findViewById(R.id.custid);
        strtransID=(TextView)findViewById(R.id.transID);
        strRecptMsg=(TextView)findViewById(R.id.RecptMsg);

        //Dateformat checking
        Date TransDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
        strTransDt= formatter.format(TransDate);
        Log.d("DemoApp", "strTransDate:" + strTransDt);
        /////////
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        //usname =sessionssodata.getString("usname", null); // getting String
        //dbpwdnm=sessionssodata.getString("dbpwdnm", null);
        usname="";
        dbpwdnm="";


        try {
            Bundle RecptBun = getIntent().getExtras();
            Log.d("What is in this Bundle",RecptBun.getString("custID"));
            Log.d("What is in this Bundle",RecptBun.getString("vstrpayamt"));
            Log.d("What is in this Bundle",RecptBun.getString("TransID"));
            Log.d("What is in this Bundle",String.valueOf(RecptBun.containsKey("payFlag")));
            Log.d("What is in this Bundle",String.valueOf(RecptBun.containsKey("payFlag")));

            custID = RecptBun.getString("custID");
            vstrpayamt = RecptBun.getString("vstrpayamt");
            TransID= RecptBun.getString("TransID");

            payFlag=RecptBun.getString("payFlag");
           // BankName= RecptBun.getString("BankName");
           // Log.d("PayFlag here in GenReceipt",payFlag);
            if (payFlag.contentEquals("OTS")) {
                toolbarback.setTitle("OTS Receipt");
                ReceiptNo = new DatabaseAccess().
                        receiptNoOTS(context, custID, TransID);
                Log.d("Where is Receipt No:",ReceiptNo);
            }
            else{
                BalFetch= RecptBun.getString("BalFetch");
                micrNo=RecptBun.getString("micr_no");
                fromNonAccount=RecptBun.getString("from");

                normalPaymentMode();
            }

        } catch (Exception e) {
            Log.d("Exception came here...",e.getMessage());
            e.printStackTrace();
        }

        /*
         ##############################################################################
          -----------------DEAD CODE Start -------------------TO BE  TESTED AND VERIFIED
         ##############################################################################
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        CurTime=dateFormat.format(date);
        Log.d("DemoApp", " CurTime" + CurTime);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyMMddHHmmss");
        Date date1 = new Date();
       // CurTime1=dateFormat1.format(date1);
      //  CurTime1=CurTime1+custID.substring(9);
        CurTime1=TransID;
        Log.d("DemoApp", " CurTime1 " + CurTime1);
        BankName = BankName.replaceAll("\\s+","_");
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        strCompanyId=sessiondata.getString("CompanyID", null);
        //Narendra: Hardcoding to 14 as suggested in mail
        strCompanyId="3";
        imeinum=sessiondata.getString("imeinum", null);
        usernm=sessiondata.getString("userID", null);
        Log.d("DemoApp", " custID" + custID);
        Log.d("DemoApp", " phoneNo" + phoneNo);
        //String strnum=imeinum.substring(13,15);
        Log.d("DemoApp", " imeinum" + imeinum);
        //Log.d("DemoApp", " custID.substring(9)" + custID.substring(9));
        //Log.d("DemoApp", " imeinum.substring(14,16)" + imeinum.substring(13,15));
        //Log.d("DemoApp", " imeinum.substring(13)" + imeinum.substring(13));
        //RecptGenURL = "http://portal.tpcentralodisha.com:8070/IncomingSMS/CESU_CollInfo.jsp?";
        if(paymode.equals("2")){//dd
            ActPayMode="2";//      "+strTransDt+"
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+CommonMethods.getFormattedDate1(dddate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
        }else if(paymode.equals("3")){//chq
            ActPayMode="0";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+chqno+"&ClearDate="+CommonMethods.getFormattedDate1(chqdate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
        } else if(paymode.equals("4")){//money
            ActPayMode="0";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+"MR"+"&Ins_No="+moneyId+"&ClearDate="+CommonMethods.getFormattedDateMR(moneyDate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
        } else if(paymode.equals("7")) {//Pos
            ActPayMode = "7";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CurTime+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName=&Ins_No=" + PosTransID + "&ClearDate=" + CommonMethods.getFormattedDate1(dddate) + "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
        } else if(paymode.equals("8")) {//NEFT
            ActPayMode = "8";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CommonMethods.getFormattedDateDDMMYYYY(neftDate)+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+neftNO+"&ClearDate="+CommonMethods.getFormattedDate(neftDate) + "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
        }
        else if(paymode.equals("9")) {//RTGS
            ActPayMode = "9";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CommonMethods.getFormattedDateDDMMYYYY(rtgsDate)+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+rtgsNo+"&ClearDate="+CommonMethods.getFormattedDate(rtgsDate)+ "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;;
        } else{//cash
            ActPayMode="1";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName=&Ins_No=&ClearDate=&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;;
        }
        Log.d("UrlsToCheck", RecptGenURL);
         /*
         ################################################################################
          -----------------DEAD CODE End ----------------------- TO BE  TESTED AND VERIFIED
         ################################################################################
         */


        try {
            //new ReceiptGenOnline().execute(RecptGenURL);
            offlineDumpData();

        } catch (Exception e) {
        }



        btnBackprnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Recfound.equals("0")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("You cannot go back");
                    alertDialogBuilder.setMessage("Receipt already generated" + "\n" + " Print the receipt")
                            .setCancelable(false)
                            .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  ReceiptGen.this.finish();
                                    Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                                    startActivity(accountinfo);
                                    finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("You can go back");
                    alertDialogBuilder.setMessage("Information store on server" + "\n" + " check Online for details ")
                            .setCancelable(false)
                            .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  ReceiptGen.this.finish();
                                    Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                                    startActivity(accountinfo);
                                    finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }

            }
        });


        printrcptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                SharedPreferences.Editor editor = sessiondata.edit();
                Usernm =sessiondata.getString("userID", null);
                //to get SBM print
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";
                Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                int sbmflg = 0;
                while (rs.moveToNext()) {
                    sbmflg = rs.getInt(0);
                }
                //   Log.d("DemoApp", "strUpdateSQL_01  01");
                rs.close();
                databaseAccess.close();
                ////
                Log.d("DemoApp", "sbmflg"+sbmflg);
                if (sbmflg == 8) {
                    //Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),
                            PrintRecptAmigoThermalNew.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID.trim());
                    PrintBun.putString("type", "O");
                    if(payFlag.contentEquals("OTS")){
                        PrintBun.putString("from", "enOTS");
                    }
                    else{
                        PrintBun.putString("from", "en");
                    }
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                } else if (sbmflg == 6) {
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                    // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID.trim());
                    PrintBun.putString("type", "O");

                    if(payFlag.contentEquals("OTS")){
                        PrintBun.putString("from", "enOTS");
                    }
                    else{
                        PrintBun.putString("from", "en");
                    }
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                } else if (sbmflg ==2) {
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                    // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID.trim());
                    PrintBun.putString("type", "O");
                    PrintBun.putString("from", "en");
                    if(payFlag.contentEquals("OTS")){
                        PrintBun.putString("from", "enOTS");
                    }
                    else{
                        PrintBun.putString("from", "en");
                    }
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else {
                    Toast.makeText(ReceiptGen.this, "Configure printer", Toast.LENGTH_SHORT).show();
                }
            }
        });


        RegenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //RecptGenURL = "http://portal.tpcentralodisha.com:8070/IncomingSMS/CESU_CollInfo.jsp?";
                if(paymode.equals("2")){//dd
                    ActPayMode="0";//      "+strTransDt+"
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+CommonMethods.getFormattedDate1(dddate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
                }else if(paymode.equals("3")){//chq
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+chqno+"&ClearDate="+CommonMethods.getFormattedDate1(chqdate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
                } else if(paymode.equals("4")){//money
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+"MR"+"&Ins_No="+moneyId+"&ClearDate="+CommonMethods.getFormattedDateMR(moneyDate)+"&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
                } else if(paymode.equals("7")) {//Pos
                    ActPayMode = "7";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CurTime+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName=&Ins_No=" + PosTransID + "&ClearDate=" + CommonMethods.getFormattedDate1(dddate) + "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
                } else if(paymode.equals("8")) {//NEFT
                    ActPayMode = "8";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CommonMethods.getFormattedDateDDMMYYYY(neftDate)+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+neftNO+"&ClearDate="+CommonMethods.getFormattedDate(neftDate) + "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;
                }
                else if(paymode.equals("9")) {//RTGS
                    ActPayMode = "9";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" +CommonMethods.getFormattedDateDDMMYYYY(rtgsDate)+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+rtgsNo+"&ClearDate="+CommonMethods.getFormattedDate(rtgsDate)+ "&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;;
                } else{//cash
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName=&Ins_No=&ClearDate=&PaymentMthh=0"+"&BBPS=1&OffLine=1&MICR="+micrNo;;
                }
                try {
                   // new ReceiptGenOnline().execute(RecptGenURL);

                } catch (Exception e) {
                }

            }
        });

    }

    private void normalPaymentMode() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        //Added

        String strUpdateSQL_01 = "Select" +
                " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,A.RTGS_DATE,A.MONEY_RECPT_ID,A.MONEY_RECPT_DATE,A.DB_TYPE_SERVER" +
                " FROM " +
                " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and CUST_ID = '" + custID + "' AND TRANS_ID='"+ TransID +"'";
        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            paymode=rs.getString(23);
            chqno=rs.getString(24);
            chqdate=rs.getString(25);
            ddno=rs.getString(26);
            dddate=rs.getString(27);
            BankName=rs.getString(37);
            PosTransID=rs.getString(38);
            phoneNo=rs.getString(39);
            BalRemain=rs.getString(40);
            //custID=rs.getString(1);
            neftNO=rs.getString(41);
            neftDate=rs.getString(42);
            rtgsNo=rs.getString(43);
            rtgsDate=rs.getString(44);
            moneyId=rs.getString(45);
            moneyDate=rs.getString(46);

            String BillType = "";
            String pay_cnt = rs.getString(47);
            if(pay_cnt.equals("0")){
                BillType = "B";
            }else {
                BillType = "A";
            }
            ReceiptNo = BillType+rs.getString(1)+rs.getString(10);

        }
    }

    private void offlineDumpData(){
        strRecptNo.setText("Receipt No: "+ReceiptNo);
        strAmtPaid.setText("Amount Paid(Rs): "+vstrpayamt);
        strRecptDT.setText("Recpt Date: "+CommonMethods.getTodaysDate());
        strcustid.setText("Cust. ID: "+custID);
        strtransID.setText("Trans ID: "+TransID);
        strRecptMsg.setText("Receipt Generation Successful");

        if (payFlag.contentEquals("OTS")) {

            int rows = new DatabaseAccess().
                    updateCollFlagRecptPrintOTS(this, custID, TransID);
            Log.d("COLL_FLAG OTS", "Rows updated OTS::: " + rows);
            new DatabaseAccess().insertIntoOTSConsumerDataUpload_BKP(this, custID, TransID);
        }
        else {

            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strSelectSQL_01 = "";
            strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET SEND_FLG=0,COLL_FLG=1, RECPT_FLG=1,MR_NO='" + TransID + "'";
            strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + TransID + "'";
            System.out.println();
            DatabaseAccess.database.execSQL(strSelectSQL_01);

            try {

                String backupData = "INSERT INTO COLL_SBM_DATA_BKP (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,CAT_CODE,RCF,COLL_MONTH,COLL_YEAR,Message,CHQ_DISHNRD,Cur_TOTAL,BILL_TOTAL,Rebate,Due_Date,RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,CHEQUE_NO,CHEQUE_DATE,DD_NO,DD_DATE,Bank_ID,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG,DEL_FLG,Bill_ID,COLL_FLG,TRANS_ID,PMT_TYP,POS_TRANS_ID,PHONE_NO,TRANS_DATE,BAL_FETCH,EMAIL,NEFT_NO,NEFT_DATE,RTGS_NO,RTGS_DATE,MICR_NO,DIV_CODE_SERVER,CA_SERVER,DB_TYPE_SERVER,NON_ENERGY_TYPE,MONEY_RECPT_ID,MONEY_RECPT_DATE,MONEY_TYPE,OPERATION_TYPE,SPINNER_NON_ENERGY)   SELECT CONS_ACC,CUST_ID,Division,Subdivision ,section,CON_NAME,CON_ADD1,CON_ADD2,CAT_CODE,RCF,COLL_MONTH,COLL_YEAR,Message,CHQ_DISHNRD,Cur_TOTAL,BILL_TOTAL,Rebate,Due_Date,RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,CHEQUE_NO,CHEQUE_DATE,DD_NO,DD_DATE,Bank_ID,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG,DEL_FLG,Bill_ID,COLL_FLG,TRANS_ID,PMT_TYP,POS_TRANS_ID,PHONE_NO,TRANS_DATE,BAL_FETCH,EMAIL,NEFT_NO,NEFT_DATE,RTGS_NO,RTGS_DATE,MICR_NO,DIV_CODE_SERVER,CA_SERVER,DB_TYPE_SERVER,NON_ENERGY_TYPE,MONEY_RECPT_ID,MONEY_RECPT_DATE,MONEY_TYPE,OPERATION_TYPE,SPINNER_NON_ENERGY FROM COLL_SBM_DATA WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + TransID + "'";
                DatabaseAccess.database.execSQL(backupData);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            databaseAccess.close();
        }
        scheduleWork();
    }

    private void scheduleWork(){
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadManager.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);
    }


}


 /*  if(sbmflg==1){
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),PrintRecptSBM.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==2){ //analogic thermal blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),PrintRecptAnalogicThermal.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==3){ //Epson thermal blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),PrintRecptEpsonThermal.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==4) { //SOFTLAND IMPACT blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptSoftlandImpact.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==5){ //amigo IMPACT blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),PrintRecptAmigoImpact.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==6) { //Analogic IMPACT blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpact.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==7){
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptPhiThermal.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else if(sbmflg==8){ //amigo Thermal blutooth printer
                    Intent RecptPrintIntent = new Intent(getApplicationContext(),PrintRecptAmigoThermalNew.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }else{
                    Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecpt.class);
                    Bundle PrintBun = new Bundle();
                    PrintBun.putString("custID", custID);
                    PrintBun.putString("TransID", TransID);
                    RecptPrintIntent.putExtras(PrintBun);
                    startActivity(RecptPrintIntent);
                    finish();
                }*/
/*
                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", custID);
                PrintBun.putString("TransID", TransID);
                PrintBun.putString("type", "O");
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();*/
