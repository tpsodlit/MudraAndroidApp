package com.collection.tpwodloffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.activity.ReceiptGen;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SumDtlNonAcnt extends AppCompatActivity {
    private DatabaseAccess databaseAccess=null;
    final   Context context = this;
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
    private String strPhoneNo ="";
    private String vstrCons_no = "";
    private String vstrpayamt ="";
    private String vstrchqno = "";
    private String vstrchqdt = "";
    private String Paymode = "";
    private String BankName = "";
    private String posidName = "";
    private String BankID = "";
    private String custID="";
    String  SelChoice="";
    String  TransID="";
    String  BalFetch="";
    private String namefetch="";
    private String MobileNofetch="";
    private EditText strtxtPhoneNo;
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
    private String micrNo="";
    private TextView tv_micr;
    private TableRow tr_micr;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_dtl_non_acnt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strAccNo=(TextView)findViewById(R.id.AccNo);
        strAmtRec=(TextView)findViewById(R.id.AmtRec);
        strpaymode=(TextView)findViewById(R.id.paymode);
        strddno=(TextView)findViewById(R.id.ddno);
        strdddate=(TextView)findViewById(R.id.dddate);
        strchqno=(TextView)findViewById(R.id.chqno);
        strchqdt=(TextView)findViewById(R.id.chqdt);
        strbankname=(TextView)findViewById(R.id.bankname);
        strposidname=(TextView)findViewById(R.id.posidname);
        tv_micr=findViewById(R.id.tv_micr);
        Button GenRecp= (Button) findViewById(R.id.GenRecp);
        TextView ddno1=(TextView)findViewById(R.id.ddno1);
        TextView dddate1=(TextView)findViewById(R.id.dddate1);
        TextView chqno1=(TextView)findViewById(R.id.chqno1);
        TextView chqdt1=(TextView)findViewById(R.id.chqdt1);
        TextView bankname1=(TextView)findViewById(R.id.bankname1);
        TextView posidname1=(TextView)findViewById(R.id.posidname1);
        Button btnBackprnt = (Button) findViewById(R.id.back);
        custName=(TextView)findViewById(R.id.custName);
        strtxtPhoneNo=(EditText) findViewById(R.id.PhoneNo);
        tr_micr=findViewById(R.id.tr_micr);
        trneft=findViewById(R.id.trneft);
        neftno1=findViewById(R.id.neftno1);
        neftno=findViewById(R.id.neftno);
        neft_date=findViewById(R.id.neft_date);
        neftdate1=findViewById(R.id.neftdate1);
        neftdate=findViewById(R.id.neftdate);
        rtgs=findViewById(R.id.rtgs);
        rtgsno1=findViewById(R.id.rtgsno1);
        rtgsno=findViewById(R.id.rtgsno);
        rtgs_date=findViewById(R.id.rtgs_date);
        rtgsdate1=findViewById(R.id.rtgsdate1);
        rtgsdate=findViewById(R.id.rtgsdate);


        try {
            Bundle pmtsmry = getIntent().getExtras();
            vstrCons_no = pmtsmry.getString("vstrCons_no");
            vstrpayamt = pmtsmry.getString("vstrpayamt");
            vstrchqno = pmtsmry.getString("vstrchqno");
            vstrchqdt = pmtsmry.getString("vstrchqdt");
            Paymode = pmtsmry.getString("Paymode");
            BankName = pmtsmry.getString("BankName");
            posidName = pmtsmry.getString("PosID");
            BankID = pmtsmry.getString("BankID");
            custID = pmtsmry.getString("custID");
            TransID = pmtsmry.getString("TransID");
            SelChoice= pmtsmry.getString("SelChoice");
            BalFetch= pmtsmry.getString("BalFetch");
            namefetch= pmtsmry.getString("namefetch");
            MobileNofetch= pmtsmry.getString("MobileNofetch");
            micrNo= pmtsmry.getString("micr_no");
            System.out.println("consumerId="+custID +TransID);

            if(MobileNofetch.length()<10){
                strtxtPhoneNo.setVisibility(EditText.GONE);
                strtxtPhoneNo.setText(MobileNofetch);
                strtxtPhoneNo.setEnabled(false);
            }else{
                strtxtPhoneNo.setText(MobileNofetch);
                strtxtPhoneNo.setEnabled(false);
            }

        }catch(Exception e){e.printStackTrace();}
        strAccNo.setText(vstrCons_no);
        strAmtRec.setText(vstrpayamt);
        custName.setText(namefetch);
        strpaymode.setText(Paymode);
        if(Paymode.equals("chq")){
            strchqno.setText(vstrchqno);
            strchqdt.setText(vstrchqdt);
            strbankname.setText(BankName);
            tv_micr.setText(micrNo);
        }else if(Paymode.equals("dd")){
            strddno.setText(vstrchqno);
            strdddate.setText(vstrchqdt);
            strbankname.setText(BankName);
            tv_micr.setText(micrNo);
        }
        else if (Paymode.equals("NEFT")){
            neftno.setText(vstrchqno);
            neftdate.setText(vstrchqdt);
            strbankname.setText(BankName);
            // tv_micr.setText(micrNo);
        }
        else if (Paymode.equals("RTGS")){
            rtgsno.setText(vstrchqno);
            rtgsdate.setText(vstrchqdt);
            strbankname.setText(BankName);
        }

        else if(Paymode.equals("pos")){
            strposidname.setText(posidName);
        }

        if(Paymode.equals("chq")){
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

        }
        else if(Paymode.equals("dd")){
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
        }
        else if(Paymode.equals("NEFT")){
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

        }
        else if(Paymode.equals("RTGS")){
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

        }




        else if(Paymode.equals("pos")) {
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
        }else {
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
        }

        GenRecp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernm="";
                SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                SharedPreferences.Editor editor = sessiondata.edit();
                String toDayDt = sessiondata.getString("toDayDt", null); // getting String
                usernm=sessiondata.getString("userID", null);
                String strsql1="";
                int Pay_ID=0;
                String convertdt="";
                strPhoneNo = strtxtPhoneNo.getText().toString();

                strPhoneNo="9999999999";

                if (TextUtils.isEmpty(strPhoneNo)) {

                    //strtxtPhoneNo.setError("Enter  Mobile No");
                    //  Log.d("DemoApp", "Query SQL " + strRefNum);
                }else {
                    if (Paymode.equals("chq")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        Pay_ID = 3;// do not mix up with existing paymode
                        strsql1 = ",CHEQUE_NO='" + vstrchqno + "',CHEQUE_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                    }

                    else if (Paymode.equals("dd")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",DD_NO='" + vstrchqno + "',DD_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 2;// do not mix up with existing paymode
                    }
                    else if (Paymode.equals("NEFT")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",NEFT_NO='" + vstrchqno + "',NEFT_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 8;// do not mix up with existing paymode
                    }
                    else if (Paymode.equals("RTGS")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",RTGS_NO='" + vstrchqno + "',RTGS_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 9;// do not mix up with existing paymode
                    }




                    else if (Paymode.equals("pos")) {
                        convertdt = vstrchqdt.substring(4, 8) + "-" + vstrchqdt.substring(2, 4) + "-" + vstrchqdt.substring(0, 2);
                        strsql1 = ",POS_TRANS_ID='" + posidName + "',DD_DATE =strftime('%Y-%m-%d','" + convertdt + "')";
                        Pay_ID = 7;
                    } else {
                        BankID = "0";//for cash collection
                    }

                    if (BankID.equalsIgnoreCase("31")){

                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();

                        String strSelectSQL_01="";
                        int cnt_BankId=0;

                        strSelectSQL_01 = "SELECT  COUNT(1) from mst_Bank ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                        //  DatabaseAccess.database.execSQL(strSelectSQL_01);

                        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                        while (rs.moveToNext()) {
                            cnt_BankId=rs.getInt(0);
                        }
                        cnt_BankId=cnt_BankId+1;
                        strSelectSQL_01="";
                        strSelectSQL_01 = "INSERT  INTO mst_Bank (bank_Id,Bank_Name,Status) VALUES ('"+ cnt_BankId +"','"+ BankName +"',1)  ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                        DatabaseAccess.database.execSQL(strSelectSQL_01);

                        BankID=String.valueOf(cnt_BankId);
                        databaseAccess.close();
                    }


                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();

                    String strSelectSQL_01="";
                    int cnt_BankId=0;
                    strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET TOT_PAID='" + vstrpayamt + "',PAY_MODE=" + Pay_ID + ",PMT_TYP='" + SelChoice + "',BANK_ID='" + BankID + "',PHONE_NO='" + strPhoneNo + "',BAL_FETCH='" + BalFetch +"',MICR_NO='" +micrNo+"'"+ strsql1;
                    strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + TransID + "'";

                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    //added on 10062019//
                    strSelectSQL_01="";
                    strSelectSQL_01 = "UPDATE SA_USER SET BAL_REMAIN='"+ BalFetch +"' WHERE USERID='"+ usernm +"'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();
                    //
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date sysDate = new Date();
                        Date date1 = sdf.parse(sdf.format(sysDate));
                        Date date2 = sdf.parse(toDayDt);
                        Log.d("DemoApp", "date1" + sdf.format(date1));
                        Log.d("DemoApp", "date2" + sdf.format(date2));
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
                                            SumDtlNonAcnt.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();

                        } else if (date1.compareTo(date2) <= 0) {
                            Log.d("DemoApp", "Date1 is before Date2");
                            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                                Intent RecptGenIntent = new Intent(getApplicationContext(), ReceiptGen.class);
                                Bundle RecptBun = new Bundle();
                                RecptBun.putString("custID", custID);
                                RecptBun.putString("vstrpayamt", vstrpayamt);
                                RecptBun.putString("TransID", TransID);
                                RecptBun.putString("BankName", BankName);
                                RecptBun.putString("BalFetch", BalFetch);
                                RecptBun.putString("MobileNofetch", MobileNofetch);
                                RecptBun.putString("micr_no",micrNo);
                                RecptBun.putBoolean("from_non_account",false);
                                RecptGenIntent.putExtras(RecptBun);
                                startActivity(RecptGenIntent);
                                finish();


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
                                                SumDtlNonAcnt.this.finish();
                                            }
                                        });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }
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
                Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                startActivity(accountinfo);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
        accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(accountinfo);
        finish();
    }
}
