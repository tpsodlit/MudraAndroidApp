package com.collection.tpwodloffline;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.collection.tpwodloffline.activity.AcCollection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptGenDuplicate extends AppCompatActivity {
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
    private Button printrcptBtn ;
    private  Button RegenBtn ;
    private String Recfound="";
    private String  usernm="";
    private String CurTime="";
    private String CurTime1="";
    private String ActPayMode="";
    private String PosTransID="";
    private String phoneNo="";
    private String Usernm ="";
    private Cursor rs=null;
    private int sbmflg = 0;
    private String usname="";
    private  String dbpwdnm="";
    private String strTransDt="";
    private String urlName="";

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_gen_duplicate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String

        RecptGenURL = urlName+"IncomingSMS/CESU_CollInfo.jsp?";
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
        usname =sessionssodata.getString("usname", null); // getting String
        dbpwdnm=sessionssodata.getString("dbpwdnm", null);



        try {
            Bundle PrintBun = getIntent().getExtras();
            custID = PrintBun.getString("custID");
            TransID= PrintBun.getString("TransID");
            vstrpayamt=PrintBun.getString("vstrpayamt");
            // BankName= RecptBun.getString("BankName");
            /////////////////////////////////////////
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            Usernm =sessiondata.getString("userID", null);
            //to get SBM print
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_02 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";
            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_02);
            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_02, null);
            sbmflg = 0;
            while (rs.moveToNext()) {
                sbmflg = rs.getInt(0);
            }
            //   Log.d("DemoApp", "strUpdateSQL_01  01");
            rs.close();
            databaseAccess.close();
            ///////////////////////////


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
                    " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO" +
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        CurTime=dateFormat.format(date);
        Log.d("DemoApp", " CurTime" + CurTime);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyMMddHHmmss");
        Date date1 = new Date();
        CurTime1=dateFormat1.format(date1);
        CurTime1=CurTime1+custID.substring(9);
        Log.d("DemoApp", " CurTime1 " + CurTime1);
        BankName = BankName.replaceAll("\\s+","_");
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        strCompanyId=sessiondata.getString("CompanyID", null);
        imeinum=sessiondata.getString("imeinum", null);
        usernm=sessiondata.getString("userID", null);
        Log.d("DemoApp", " custID" + custID);
        Log.d("DemoApp", " phoneNo" + phoneNo);
        if(paymode.equals("2")){//dd
            ActPayMode="0";//      "+strTransDt+"
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+dddate+"&PaymentMthh=0";
        }else if(paymode.equals("3")){//chq
            ActPayMode="0";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+chqno+"&ClearDate="+chqdate+"&PaymentMthh=0";
        }else if(paymode.equals("7")) {//Pos
            ActPayMode = "7";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+ "&DateTime=" + CurTime + "&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName=&Ins_No=" + PosTransID + "&ClearDate=" + dddate + "&PaymentMthh=0";
        }else{//cash
            ActPayMode="0";
            RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName=&Ins_No=&ClearDate=&PaymentMthh=0";
        }
        try {
            new ReceiptGenOnline().execute(RecptGenURL);

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

                //  ReceiptGen.this.finish();
                ////////////////////Printer Selection/////////////////////
                Intent RecptPrintIntent=null;

                if(sbmflg==1){
                    // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicSBM.class);
                }else if(sbmflg==2){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermal.class);

                }else if(sbmflg==3){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptEpsonThermal.class);
                }else if(sbmflg==4){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptSoftlandImpact.class);

                }else if(sbmflg==5){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoImpact.class);

                }else if(sbmflg==6){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpact.class);
                }else if(sbmflg==7){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptPhiThermal.class);
                }else if(sbmflg==8){
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermal.class);
                }else {
                    RecptPrintIntent = new Intent(getApplicationContext(), PrintRecpt.class);
                }

                ////////////////////////
              //  Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecpt.class);
                Bundle PrintBun = new Bundle();
                PrintBun.putString("custID", custID);
                PrintBun.putString("TransID", TransID);
                RecptPrintIntent.putExtras(PrintBun);
                startActivity(RecptPrintIntent);
                finish();

            }
        });
        RegenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymode.equals("2")){//dd
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+dddate+"&PaymentMthh=0";
                }else if(paymode.equals("3")){//chq
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName="+BankName+"&Ins_No="+chqno+"&ClearDate="+chqdate+"&PaymentMthh=0";
                }else if(paymode.equals("7")) {//Pos
                    ActPayMode = "7";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=" + strCompanyId + "&ConsumerID=" + custID + "&imei=" + imeinum + "&RefID=" + TransID + "&Amount=" + vstrpayamt + "&DateTime="+CurTime+"&PayMod=" + ActPayMode + "&RecNo="+CurTime1+"&BankName=&Ins_No=" + PosTransID + "&ClearDate=" + dddate + "&PaymentMthh=0";
                }else{//cash
                    ActPayMode="0";
                    RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+strCompanyId+"&ConsumerID="+custID+"&imei="+imeinum+"&RefID="+TransID+"&Amount="+vstrpayamt+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+CurTime1+"&BankName=&Ins_No=&ClearDate=&PaymentMthh=0";
                }
                try {
                    new ReceiptGenOnline().execute(RecptGenURL);

                } catch (Exception e) {
                }

            }
        });

    }

    private class ReceiptGenOnline extends AsyncTask<String, Integer, String> {
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

                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
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
                bodycontent="0";
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                progressDialog = ProgressDialog.show(ReceiptGenDuplicate.this, "Trying to generate Receipt", "Please Wait:: connecting to server");
            }else{
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
                                ReceiptGenDuplicate.this.finish();
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
            String pipeDelRecptInfo =str;
            String[] RecptInfo = pipeDelRecptInfo.split("[|]");
            String ConsValid=RecptInfo[0];
            String imeiflg=RecptInfo[1];
            String recprno="";
            String RecptDt="";
            String RecptTime="";
            Recfound=RecptInfo[2];;

            if(ConsValid.equals("1") && imeiflg.equals("1")){
                recprno=RecptInfo[3];
                RecptDt=RecptInfo[4];
                RecptTime=RecptDt.substring(8,14);
                //RecptDt=RecptDt.substring(0,2)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(4,8);
                RecptDt=RecptDt.substring(4,8)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(0,2);
                Log.d("DemoApp", " RecptDt   " + RecptDt);
                Log.d("DemoApp", " RecptTime   " + RecptTime);
                //  Date date1 =null;
                //   try {
                //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //date1 = sdf.parse(RecptDt);

                // }catch (Exception e){
                //   e.printStackTrace();
                // }
             //   int balremain=0;
              //  balremain=Integer.parseInt(BalFetch)-Integer.parseInt(vstrpayamt);
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strSelectSQL_01 ="";
                strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MR_NO='"+ recprno +"',RECPT_DATE=strftime('%Y-%m-%d', '"+ RecptDt +"'),RECPT_FLG=1,SEND_FLG=1,coll_flg=1,RECPT_TIME='"+ RecptTime +"'";

                strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='"+ custID +"' AND TRANS_ID='"+ TransID +"'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                strSelectSQL_01="";
              //  strSelectSQL_01 = "UPDATE SA_USER SET BAL_REMAIN='"+ balremain +"' WHERE USERID='"+ usernm +"'";
               // Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
               // DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            }
            strRecptNo.setText("Receipt No:"+recprno);
            strAmtPaid.setText("Amount Paid"+vstrpayamt);
            strRecptDT.setText("Recpt Date:"+RecptDt);
            strcustid.setText("Cust. ID:"+custID);
            strtransID.setText("Trans ID:"+TransID);
            if(Recfound.equals("0") || recprno.length()==0 || Recfound.equals("")){
                printrcptBtn.setVisibility(View.INVISIBLE);
                RegenBtn.setVisibility(View.VISIBLE);
                strRecptMsg.setText("Recpt Generation Uncessful"+"Please click regenerate Receipt");
            }else{
                printrcptBtn.setVisibility(View.VISIBLE);
                //  RegenBtn.setVisibility(View.GONE);
                strRecptMsg.setText(" Receipt Generation Successful");
            }
        }
    }

}