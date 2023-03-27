package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.BankList;
import com.collection.tpwodloffline.CalenderViewP;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaySummary extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatabaseAccess databaseAccess=null;
    final Context context = this;
    private static RadioButton rbCash;
    private static RadioButton rbChq;
    private static RadioButton rbdd;
    private static RadioButton rbEzetap;
    private static RadioButton rbpos;
    private  RadioButton rtgs;
    private  RadioButton neft;
    // private RadioButton rb_money;
    private static TextView strchqdt;
    private static EditText strchqno;
    private static EditText strposid;

    private static TextView strCons_no ;
    private static TextView strcustName ;
    private static TextView strpayamt ;
    private static  String Paymode="";
    private Spinner Bankspinner =null;
    private String BankID="";
    private String BankName="";
    private String PosID="";
    private Button pkdate;
    private String vstrCons_no = "";
    private String vstrpayamt = "";
    private String vstrchqno = "";
    private String vstrchqdt = "";
    private String strdate= "";
    private String strID= "0";
    private static TextView chqformattxt;
    private DateFormat dateFormat =null;
    private Date curdate =null;
    private String custID="";
    private String TransID="";
    private String SelChoice="";
    private String BalFetch="";
    private String namefetch="";
    private String MobileNofetch="";
    private EditText et_Bank_name;
    private String otherBankName="";

    private EditText micr_number;
    private String micrNumber="";
    private View view2;
    private LinearLayout ll_spinner;
    Button btnNext;
    private String posIdNo="";
    private EditText et_money_id;
    private String moneyID="";
    private String fromActivity="";
    private boolean manualPay= false;
    private RadioGroup rg_manual_pay;
    private RadioButton money_receipt;
    private boolean firstTime=true;
    private String poscollflg = "0";

    private String payFlag = "";

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_summary);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String Pay_Amt="";
        String consacc="";
        vstrCons_no = "";
        vstrpayamt = "";
        vstrchqno = "";
        vstrchqdt = "";
        Paymode="";
        //strID= "0";
        strCons_no=(TextView)findViewById(R.id.Cons_no);
        strpayamt=(TextView)findViewById(R.id.PayAmt);
        strcustName=(TextView)findViewById(R.id.CustName);
        rbCash=(RadioButton)findViewById(R.id.cash);
        chqformattxt=(TextView)findViewById(R.id.chqformattxt);
        rbChq=(RadioButton)findViewById(R.id.chq);
        rbdd=(RadioButton)findViewById(R.id.dd);
        rbEzetap = findViewById(R.id.rbEzetap);
        rbpos=(RadioButton)findViewById(R.id.pos);
        et_money_id=findViewById(R.id.money_id);
        strchqno=(EditText)findViewById(R.id.chqno);
        strchqdt=(TextView) findViewById(R.id.chqdt);
        strposid=(EditText)findViewById(R.id.posid);
        et_Bank_name=findViewById(R.id.et_Bank_name);
        micr_number=findViewById(R.id.micr_number);
        rg_manual_pay=findViewById(R.id.rg_manual_pay);
        money_receipt=findViewById(R.id.money_receipt);

        view2=findViewById(R.id.view2);
        view2.setVisibility(View.GONE);
        ll_spinner=findViewById(R.id.ll_spinner);
        ll_spinner.setVisibility(View.GONE);

        neft=findViewById(R.id.neft);
        rtgs=findViewById(R.id.rtgs);
        micr_number.setVisibility(View.GONE);
        et_money_id.setVisibility(View.GONE);

        btnNext= (Button) findViewById(R.id.submitbtn);

        btnNext.setClickable(true);
        btnNext.setEnabled(true);

        Button btnBack = (Button) findViewById(R.id.back);
        Bankspinner = (Spinner) findViewById(R.id.spinBank);
        pkdate = (Button) findViewById(R.id.pkdate);
        //  strchqdt.setEnabled(false);
        strchqno.setVisibility(EditText.GONE);
        strchqno.setText("");
        strposid.setVisibility(EditText.GONE);
        strposid.setText("");
        strchqdt.setVisibility(EditText.GONE);
        strchqdt.setText("");
        chqformattxt.setVisibility(TextView.GONE);
        pkdate.setVisibility(Button.GONE);

        Intent incoming = getIntent();
        strchqdt.setText(incoming.getStringExtra("strdate"));
        strchqno.setText(incoming.getStringExtra("vstrchqno"));
        // strID=incoming.getStringExtra("strID");
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        String serverDate=sessionssodata.getString("serverDate", null);
        String cashcollflg =sessionssodata.getString("cashcollflg", null); // getting String
        String chqcollflg=sessionssodata.getString("chqcollflg", null);
        String ddcollflg =sessionssodata.getString("ddcollflg", null); // getting String
        poscollflg=sessionssodata.getString("poscollflg", null);
        dateFormat = new SimpleDateFormat("ddMMyyyy");
        curdate = new Date();
        if(cashcollflg.equals("1")){
            Paymode = "Cash";
            rbCash.setClickable(true);
            rbCash.setEnabled(true);
        }else{
            Paymode="";
            rbCash.setClickable(false);
            rbCash.setEnabled(false);
        }
        if(chqcollflg.equals("1")){
            rbChq.setClickable(true);
            rbChq.setEnabled(true);
        }else{
            rbChq.setClickable(false);
            rbChq.setEnabled(false);
        }
        if(ddcollflg.equals("1")){
            rbdd.setClickable(true);
            rbdd.setEnabled(true);
        }else{
            rbdd.setClickable(false);
            rbdd.setEnabled(false);
        }
        if(poscollflg.equals("1")){
            rbpos.setClickable(true);
            rbpos.setEnabled(true);
        }else{
            rbpos.setClickable(false);
            rbpos.setEnabled(false);
        }
        if (CommonMethods.validateEzetap(this)) {
            rbEzetap.setVisibility(View.VISIBLE);
            rbEzetap.setChecked(true);
            rbEzetap.setTextColor(Color.parseColor("#ca2626"));
            poscollflg = "1";
            Paymode = "pos";

            rbCash.setChecked(false);
            rbCash.setTextColor(Color.parseColor("#000000"));
        }


        Bundle pmtsmrytemp1 =null;
        try {
            pmtsmrytemp1 = getIntent().getExtras();
            // Ezetap and OTS changes uncommented
            strID ="0";
            if(strID.equals("") || strID==null){
                strID="0";
            }
             Log.d("DemoApp", " strID   " + strID);
        }catch(Exception e){e.printStackTrace();strID="0";}

        try {
            fromActivity = pmtsmrytemp1.getString("from");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            Log.d("DemoApp",  " strID11   " + strID);
            if(strID.equals("1")) {
                vstrCons_no = pmtsmrytemp1.getString("vstrCons_no");
                custID = pmtsmrytemp1.getString("custID");
                vstrpayamt = pmtsmrytemp1.getString("vstrpayamt");
                vstrchqno = pmtsmrytemp1.getString("vstrchqno");
                vstrchqdt = pmtsmrytemp1.getString("vstrchqdt");
                Paymode = pmtsmrytemp1.getString("Paymode");
                BankName = pmtsmrytemp1.getString("BankName");
                BankID = pmtsmrytemp1.getString("BankID");
                strdate = pmtsmrytemp1.getString("strdate");
                strID = pmtsmrytemp1.getString("strID");
                TransID = pmtsmrytemp1.getString("TransID");
                BalFetch= pmtsmrytemp1.getString("BalFetch");
                namefetch=pmtsmrytemp1.getString("namefetch");
                MobileNofetch=pmtsmrytemp1.getString("MobileNofetch");
                otherBankName=pmtsmrytemp1.getString("otherBankName");
                micrNumber=pmtsmrytemp1.getString("micr_no");
                moneyID=pmtsmrytemp1.getString("moneyId");
                fromActivity=pmtsmrytemp1.getString("from");
                manualPay=pmtsmrytemp1.getBoolean("manual");
                firstTime=pmtsmrytemp1.getBoolean("firstTime");
                payFlag = pmtsmrytemp1.getString("PayFlag");




                if (manualPay){
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);
                    rbCash.setChecked(true);
                    rbChq.setVisibility(View.GONE);
                    rbdd.setVisibility(View.GONE);
                    rbpos.setVisibility(View.GONE);
                    neft.setVisibility(View.GONE);
                    rtgs.setVisibility(View.GONE);
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);
                    strposid.setVisibility(EditText.GONE);
                    strchqno.setVisibility(EditText.GONE);
                    strchqno.setText("");
                    strchqdt.setVisibility(EditText.VISIBLE);

                    try {
                        //  DateFormat   dateFormat = new SimpleDateFormat("ddMMyyyy");

                        if ((manualPay)&&(firstTime)){
                            strchqdt.setText(dateFormat.format(curdate));

                        }
                        else {
                            strchqdt.setText(vstrchqdt);
                        }



                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    Bankspinner.setVisibility(View.GONE);
                    Paymode="money";
                    micr_number.setVisibility(View.GONE);
                    pkdate.setVisibility(Button.GONE);
                    view2.setVisibility(View.VISIBLE);
                    chqformattxt.setVisibility(TextView.GONE);
                    ll_spinner.setVisibility(View.GONE);
                    et_Bank_name.setVisibility(View.GONE);
                    et_money_id.setVisibility(View.VISIBLE);
                    et_money_id.setText(moneyID);

                }
                else {
                    rg_manual_pay.setVisibility(View.GONE);
                    money_receipt.setChecked(false);

                    /* Cheque/DD option not required for all types of WODL payments
                    if (payFlag.contentEquals("OTS"))
                        rbChq.setVisibility(View.GONE);
                    else
                        rbChq.setVisibility(View.VISIBLE);

                    //rbdd.setVisibility(View.VISIBLE);
                        */
                    rbdd.setVisibility(View.GONE);
                    rbpos.setVisibility(View.GONE);
                    neft.setVisibility(View.GONE);
                    rtgs.setVisibility(View.GONE);
                }

                micr_number.setText(micrNumber);
                et_Bank_name.setText(otherBankName);
                strposid.setText(posIdNo);


                Log.d("DemoApp", " vstrCons_no1   " + vstrCons_no);
                Log.d("DemoApp", " vstrpayamt1   " + vstrpayamt);
                Log.d("DemoApp", " vstrchqno 1  " + vstrchqno);
                Log.d("DemoApp", " vstrchqdt1   " + vstrchqdt);
                Log.d("DemoApp", " BankName   " + BankName);
                Log.d("DemoApp", " BankID   " + BankID);
                Log.d("DemoApp", " strID   " + strID);
                Log.d("DemoApp", " TransID   " + TransID);
                Log.d("DemoApp", " BalFetch   " + BalFetch);
                System.out.println("paymode=="+Paymode);

                if(Paymode.equals("cash") || Paymode.equals("") || Paymode==null ){
                    rbCash.setChecked(true);
                    rbChq.setChecked(false);
                    rbdd.setChecked(false);
                    rbpos.setChecked(false);
                    strchqno.setVisibility(EditText.GONE);
                    strchqno.setText("");
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqdt.setVisibility(EditText.GONE);
                    strchqdt.setText("");
                    Bankspinner.setVisibility(View.GONE);
                    pkdate.setVisibility(Button.GONE);
                    chqformattxt.setVisibility(TextView.GONE);
                    micr_number.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    ll_spinner.setVisibility(View.GONE);
                    et_money_id.setVisibility(View.GONE);

                }else if(Paymode.equals("chq")){
                    rbChq.setChecked(true);
                    rbCash.setChecked(false);
                    rbdd.setChecked(false);
                    rbpos.setChecked(false);
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqno.setVisibility(EditText.VISIBLE);
                    //strchqno.setText("");
                    strchqno.setHint("CHEQUE NUMBER");
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.VISIBLE);
                    pkdate.setVisibility(Button.VISIBLE);
                    chqformattxt.setVisibility(TextView.VISIBLE);
                    micr_number.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    ll_spinner.setVisibility(View.VISIBLE);
                    et_money_id.setVisibility(View.GONE);
                }else if(Paymode.equals("dd")) {
                    rbdd.setChecked(true);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbpos.setChecked(false);
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqno.setHint("DD NUMBER");
                    strchqno.setVisibility(EditText.VISIBLE);
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.VISIBLE);
                    pkdate.setVisibility(Button.VISIBLE);
                    chqformattxt.setVisibility(TextView.VISIBLE);
                    micr_number.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    ll_spinner.setVisibility(View.VISIBLE);
                    et_money_id.setVisibility(View.GONE);
                }
                else if(Paymode.equals("NEFT")) {
                    rbdd.setChecked(true);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbEzetap.setChecked(false);
                    rbpos.setChecked(false);
                    neft.setChecked(true);
                    rtgs.setChecked(false);
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqno.setHint("NEFT NUMBER");
                    strchqno.setVisibility(EditText.VISIBLE);
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.VISIBLE);
                    pkdate.setVisibility(Button.VISIBLE);
                    chqformattxt.setVisibility(TextView.VISIBLE);
                    micr_number.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    ll_spinner.setVisibility(View.VISIBLE);
                    et_money_id.setVisibility(View.GONE);
                }
                else if(Paymode.equals("RTGS")) {
                    rbdd.setChecked(true);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbpos.setChecked(false);
                    rbEzetap.setChecked(false);
                    neft.setChecked(false);
                    rtgs.setChecked(true);
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqno.setHint("RTGS NUMBER");
                    strchqno.setVisibility(EditText.VISIBLE);
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.VISIBLE);
                    pkdate.setVisibility(Button.VISIBLE);
                    chqformattxt.setVisibility(TextView.VISIBLE);
                    micr_number.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    ll_spinner.setVisibility(View.VISIBLE);
                    et_money_id.setVisibility(View.GONE);
                }



                else if(Paymode.equals("pos")){
                    rbEzetap.setChecked(true);
                    rbpos.setChecked(true);
                    rbdd.setChecked(false);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbpos.setChecked(false);
                    strposid.setVisibility(EditText.VISIBLE);
                    strchqno.setVisibility(EditText.GONE);
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.GONE);
                    pkdate.setVisibility(Button.GONE);
                    chqformattxt.setVisibility(TextView.GONE);
                    micr_number.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    ll_spinner.setVisibility(View.GONE);
                    et_money_id.setVisibility(View.GONE);
                }
                else if(Paymode.equals("money")){
                    rbEzetap.setChecked(true);
                    rbpos.setChecked(true);
                    rbdd.setChecked(false);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbpos.setChecked(false);
                    rbCash.setChecked(true);
                    money_receipt.setChecked(true);
                    try {
                        strposid.setVisibility(EditText.GONE);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        et_money_id.setVisibility(View.VISIBLE);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        strchqno.setVisibility(EditText.GONE);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        strchqdt.setVisibility(EditText.VISIBLE);


                        if ((manualPay)&&(firstTime)){
                            strchqdt.setText(dateFormat.format(curdate));
                        }
                        else {
                            strchqdt.setText(vstrchqdt);
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }



                    try {
                        Bankspinner.setVisibility(View.GONE);

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        pkdate.setVisibility(Button.GONE);

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        chqformattxt.setVisibility(TextView.GONE);


                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        micr_number.setVisibility(View.GONE);


                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        view2.setVisibility(View.VISIBLE);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        ll_spinner.setVisibility(View.GONE);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }




                }

                strCons_no.setText(vstrCons_no);
                strpayamt.setText(vstrpayamt);
                strcustName.setText("Name:"+namefetch);
                //btnNext.requestFocus();
                if (fromActivity.equalsIgnoreCase("non-account")){
                    rbChq.setVisibility(View.GONE);
                }
                else {
                    if (manualPay) {
                        rbChq.setVisibility(View.GONE);
                    }
                }

                    /* Cheque or DD option not required for WODL in all types of payments
                    else {
                        if (payFlag.contentEquals("OTS"))
                            rbChq.setVisibility(View.GONE);
                        else
                            rbChq.setVisibility(View.VISIBLE); */

            }
        }catch(Exception e){ e.printStackTrace();}
        ////////////

        rbCash.setOnClickListener(myOptionOnClickListener);// include hide to  mtr reading inputbox
        rbChq.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        rbdd.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        rbpos.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        rbEzetap.setOnClickListener(myOptionOnClickListener);
        neft.setOnClickListener(myOptionOnClickListener);
        rtgs.setOnClickListener(myOptionOnClickListener);

        money_receipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);

                    rbChq.setVisibility(View.GONE);
                    rbdd.setVisibility(View.GONE);
                    rbEzetap.setVisibility(View.GONE);
                    rbpos.setVisibility(View.GONE);
                    neft.setVisibility(View.GONE);
                    rtgs.setVisibility(View.GONE);
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);
                    strposid.setVisibility(EditText.GONE);
                    strchqno.setVisibility(EditText.GONE);
                    strchqno.setText("");
                    strchqdt.setVisibility(EditText.VISIBLE);


                    if ((manualPay)&&(firstTime)){
                        strchqdt.setText(dateFormat.format(curdate));

                    }else {
                        strchqdt.setText(vstrchqdt);

                    }


                    Bankspinner.setVisibility(View.GONE);
                    Paymode="money";
                    micr_number.setVisibility(View.GONE);
                    pkdate.setVisibility(Button.GONE);
                    view2.setVisibility(View.VISIBLE);
                    chqformattxt.setVisibility(TextView.GONE);
                    ll_spinner.setVisibility(View.GONE);
                    et_Bank_name.setVisibility(View.GONE);
                    et_money_id.setVisibility(View.VISIBLE);
                }
                else {
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);

                    rbChq.setVisibility(View.GONE);
                    rbdd.setVisibility(View.GONE);
                    rbEzetap.setVisibility(View.GONE);
                    rbpos.setVisibility(View.GONE);
                    neft.setVisibility(View.GONE);
                    rtgs.setVisibility(View.GONE);
                    rg_manual_pay.setVisibility(View.VISIBLE);
                    money_receipt.setChecked(true);
                    strposid.setVisibility(EditText.GONE);
                    strchqno.setVisibility(EditText.GONE);
                    strchqno.setText("");
                    strchqdt.setVisibility(EditText.VISIBLE);

                    if ((manualPay)&&(firstTime)){
                        strchqdt.setText(dateFormat.format(curdate));

                    }
                    else {
                        strchqdt.setText(vstrchqdt);
                    }


                    Bankspinner.setVisibility(View.GONE);
                    Paymode="money";
                    micr_number.setVisibility(View.GONE);
                    pkdate.setVisibility(Button.GONE);
                    view2.setVisibility(View.VISIBLE);
                    chqformattxt.setVisibility(TextView.GONE);
                    ll_spinner.setVisibility(View.GONE);
                    et_Bank_name.setVisibility(View.GONE);
                    et_money_id.setVisibility(View.VISIBLE);
                }
            }
        });

        // money_receipt.setOnClickListener(myOptionOnClickListener);


        //rbCash.setChecked(false);

        Log.d("DemoApp", " dateFormat.format(date)   " + dateFormat.format(curdate));
        String Pay_Amt1="";
        try {
            Bundle Bunpayamtdtls = getIntent().getExtras();
            Pay_Amt1 = Bunpayamtdtls.getString("Pableamt");
            consacc = Bunpayamtdtls.getString("consacc");
            custID = Bunpayamtdtls.getString("custID");
            TransID = Bunpayamtdtls.getString("TransID");
            SelChoice = Bunpayamtdtls.getString("SelChoice");
            BalFetch= Bunpayamtdtls.getString("BalFetch");
            namefetch=Bunpayamtdtls.getString("namefetch");
            MobileNofetch=Bunpayamtdtls.getString("MobileNofetch");
            fromActivity=Bunpayamtdtls.getString("from");
            manualPay=Bunpayamtdtls.getBoolean("manual");
            micrNumber=Bunpayamtdtls.getString("micr_no");
            micr_number.setText(micrNumber);
            payFlag = Bunpayamtdtls.getString("PayFlag");

            if ((manualPay)&&(firstTime)){
                strchqdt.setText(dateFormat.format(curdate));

            }



            if (manualPay){
                rg_manual_pay.setVisibility(View.VISIBLE);
                money_receipt.setChecked(true);

                rbChq.setVisibility(View.GONE);
                rbdd.setVisibility(View.GONE);
                rbpos.setVisibility(View.GONE);
                neft.setVisibility(View.GONE);
                rtgs.setVisibility(View.GONE);
                rg_manual_pay.setVisibility(View.VISIBLE);
                money_receipt.setChecked(true);
                strposid.setVisibility(EditText.GONE);
                strchqno.setVisibility(EditText.GONE);
                strchqno.setText("");
                strchqdt.setVisibility(EditText.VISIBLE);

                if ((manualPay)&&(firstTime)){
                    strchqdt.setText(dateFormat.format(curdate));
                }
                else {
                    strchqdt.setText(vstrchqdt);

                }Bankspinner.setVisibility(View.GONE);
                Paymode="money";
                micr_number.setVisibility(View.GONE);
                pkdate.setVisibility(Button.GONE);
                view2.setVisibility(View.VISIBLE);
                chqformattxt.setVisibility(TextView.GONE);
                ll_spinner.setVisibility(View.GONE);
                et_Bank_name.setVisibility(View.GONE);
                et_money_id.setVisibility(View.VISIBLE);

            } else {
                rg_manual_pay.setVisibility(View.GONE);
                money_receipt.setChecked(false);

                /* Cheque or DD option not required for all payments in WODL
                if (payFlag.contentEquals("OTS"))
                    rbChq.setVisibility(View.GONE);
                else
                    rbChq.setVisibility(View.VISIBLE);

                rbdd.setVisibility(View.VISIBLE); */

                rbpos.setVisibility(View.GONE);
                //neft.setVisibility(View.VISIBLE);
                //rtgs.setVisibility(View.VISIBLE);
            }

            if (fromActivity.equalsIgnoreCase("non-account")){
                rbChq.setVisibility(View.GONE);
            }
            else {
                if (manualPay){
                    rbChq.setVisibility(View.GONE);
                }

                /* Checque or DD not required for all WODL payments
                else {
                    if (payFlag.contentEquals("OTS"))
                        rbChq.setVisibility(View.GONE);
                    else
                        //rbChq.setVisibility(View.VISIBLE);
                        rbChq.setVisibility(View.GONE);

                } */
            }

        }catch (Exception e){}
        if(!strID.equals("1")) {
            strCons_no.setText(consacc);
            strpayamt.setText(Pay_Amt1);
            strcustName.setText("Name:"+namefetch);
            //comming from calender activity//
        }

        // Spinner click listener
        Bankspinner.setOnItemSelectedListener(this);
        if(Paymode.equals("cash") || Paymode.equals("") || Paymode==null ) {
            Bankspinner.setVisibility(View.INVISIBLE);
        }
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strSelectSQL_01 ="";
        try {
            if (strID.equals("1")) {
                strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1 and bank_id='" + BankID + "' union all SELECT bank_id,bank_name,status FROM mst_bank where status=1 and bank_id!='" + BankID +"' ";
            }else{
                strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1  order by bank_id";
            }
        }catch(Exception e){
            strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1 order by bank_id";
        }

        Log.d("DemoApp", " strSelectSQL_01   " + strSelectSQL_01);
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        ArrayList<BankList> BankDtlList = new ArrayList<>();
        int i=0;

        while (cursor.moveToNext()) {

            BankDtlList.add(new BankList(cursor.getString(0), cursor.getString(1)));

        }
        cursor.close();
        databaseAccess.close();

        // Creating adapter for spinner
        // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BankDtlList);
        ArrayAdapter<BankList> dataAdapter = new ArrayAdapter<BankList>(context, android.R.layout.simple_spinner_dropdown_item, BankDtlList);
        Bankspinner.setAdapter(dataAdapter);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Bankspinner.setAdapter(dataAdapter);
        Log.d("DemoApp", "  BankDtlList.size()   " + BankDtlList.size());

        if (payFlag.contentEquals("OTS")) {
            getSupportActionBar().setTitle("OTS PaySummary");
        } else {
            getSupportActionBar().setTitle("PaySummary");
        }

        String a=String.valueOf(Bankspinner.getSelectedItem());

        toolbarback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fromActivity.equalsIgnoreCase("non-account")){
                    Intent accountinfo = new Intent(getApplicationContext(), NoNAccountActivity.class);
                    accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(accountinfo);
                    finish();
                }
                else {
                    Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                    accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(accountinfo);
                    finish();
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("asdfg"+Paymode);

                if ((Paymode.equals("chq")||(Paymode.equals("dd"))||(Paymode.equals("NEFT"))||(Paymode.equals("RTGS")))&&((strchqno.getText().toString().length() < 4)||(strchqno.getText().toString().length()==0))){


                    strchqno.setError("Enter Correct DD/Cheque/NEFT/RTGS number");


                }
                else if ((Paymode.equals("chq")||(Paymode.equals("dd")))&&((strchqno.getText().toString().trim().length()<6)||(strchqno.getText().toString().trim().length()>8))){
                    Toast.makeText(PaySummary.this, "Cheque/DD number must be at least of 6 and max. of 8 character", Toast.LENGTH_SHORT).show();
                }

                else if ((Paymode.equals("chq")||(Paymode.equals("dd")))&&((micr_number.getText().toString().trim().length()==0)||(micr_number.getText().toString().length()<9))){
                    micr_number.setError("MICR number must be of 9 digit");
                }

                /*if ((Paymode.equals("chq") || Paymode.equals("dd") || Paymode.equals("NEFT") || Paymode.equals("RTGS"))){
                    if ((strchqno.getText().toString().length() < 4)||(strchqno.getText().toString().length()==0)) {


                        strchqno.setError("Enter Correct DD/Cheque/NEFT/RTGS number");
                    }
*/
                /*&& (strchqno.getText().toString().length() < 4)||(strchqno.getText().toString().length()==0)*/

                else if ((Paymode.equals("chq") || Paymode.equals("dd") /*|| Paymode.equals("NEFT")||
                        Paymode.equals("RTGS")*/) && BankID.equals("0")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Select Bank Name");
                    alertDialogBuilder.setMessage("Select Bank Name")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PaySummary.this.finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }

                else if ((Paymode.equalsIgnoreCase("money"))&&(et_money_id.getText().toString().trim().length() == 0)) {

                        et_money_id.setError("Please Enter Manual Money Transaction Id");

                }
                else {
                    vstrCons_no = strCons_no.getText().toString();
                    vstrpayamt = strpayamt.getText().toString();
                    vstrchqno = strchqno.getText().toString();
                    vstrchqdt = strchqdt.getText().toString();
                    PosID= strposid.getText().toString();
                    micrNumber=micr_number.getText().toString().trim();

                    if (Paymode.equals("") || Paymode == null) {
                        Paymode = "cash";
                    }
                    Intent accountsum;
                    if (Paymode.equals("pos")) {
                        accountsum = new Intent(getApplicationContext(), OnlinePayment.class);
                    } else {
                        accountsum = new Intent(getApplicationContext(), SummaryDtl.class);
                    }

                    Log.d("DemoApp", "vstrCons_no" + vstrCons_no);
                    Log.d("DemoApp", "vstrpayamt" + vstrpayamt);
                    Log.d("DemoApp", "vstrchqno" + vstrchqno);
                    Log.d("DemoApp", "vstrchqdt" + vstrchqdt);
                    Log.d("DemoApp", "Paymode" + Paymode);
                    Log.d("DemoApp", " custID" + custID);
                    Log.d("DemoApp","payFlag"+payFlag);




//Commented for Ezetap changes
//                    btnNext.setClickable(false);
//                    btnNext.setEnabled(false);
//                    accountsum = new Intent(getApplicationContext(), SummaryDtl.class);


                    Bundle pmtsmry = new Bundle();

                    if (payFlag.contentEquals("OTS")) {
                        pmtsmry.putString("vstrCons_no", new DatabaseAccess().
                                getInstallmntNo(context, custID, TransID));
                    } else {
                        pmtsmry.putString("vstrCons_no", vstrCons_no);
                    }
                    //pmtsmry.putString("vstrCons_no", vstrCons_no);
                    pmtsmry.putString("vstrpayamt", vstrpayamt);
                    pmtsmry.putString("MobileNofetch", MobileNofetch);
                    pmtsmry.putString("vstrchqno", vstrchqno);
                    pmtsmry.putString("vstrchqdt", vstrchqdt);
                    pmtsmry.putString("Paymode", Paymode);
                    pmtsmry.putString("micr_no",micrNumber);
                    pmtsmry.putString("moneyId", et_money_id.getText().toString().trim());
                    pmtsmry.putString("from",fromActivity);
                    pmtsmry.putString("PayFlag", payFlag);


                    if (BankName.equalsIgnoreCase("OTHER")){
                        if ((et_Bank_name.getText().toString().trim().length()==0)||(et_Bank_name.getText().toString().trim().equalsIgnoreCase(""))){
                            Toast.makeText(PaySummary.this,"Please enter bank name",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            BankName=et_Bank_name.getText().toString().trim();
                            pmtsmry.putString("BankName", BankName);
                            pmtsmry.putString("PosID", PosID);
                            pmtsmry.putString("BankID", BankID);
                            pmtsmry.putString("custID", custID);
                            pmtsmry.putString("TransID", TransID);
                            pmtsmry.putString("SelChoice", SelChoice);
                            pmtsmry.putString("BalFetch", BalFetch);
                            pmtsmry.putString("namefetch", namefetch);
                            pmtsmry.putString("MobileNofetch", MobileNofetch);
                            pmtsmry.putString("PayFlag", payFlag);

                            System.out.println("pay===="+custID);
                            accountsum.putExtras(pmtsmry);
                            startActivity(accountsum);
                            finish();
                        }
                    }
                    else {
                        pmtsmry.putString("BankName", BankName);

                        pmtsmry.putString("PosID", PosID);
                        pmtsmry.putString("BankID", BankID);
                        pmtsmry.putString("custID", custID);
                        pmtsmry.putString("TransID", TransID);
                        pmtsmry.putString("SelChoice", SelChoice);
                        pmtsmry.putString("BalFetch", BalFetch);
                        pmtsmry.putString("namefetch", namefetch);
                        pmtsmry.putString("MobileNofetch", MobileNofetch);
                        pmtsmry.putString("PayFlag", payFlag);
                        System.out.println("summary===="+custID);
                        accountsum.putExtras(pmtsmry);
                        startActivity(accountsum);
                        finish();
                    }



                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromActivity.equalsIgnoreCase("non-account")){
                    Intent accountinfo = new Intent(getApplicationContext(), NoNAccountActivity.class);
                    accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(accountinfo);
                    finish();
                }
                else {
                    Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                    accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(accountinfo);
                    finish();
                }
            }
        });




        strchqdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttemp = new Intent(PaySummary.this, CalenderViewP.class);
                vstrCons_no = strCons_no.getText().toString();
                vstrpayamt = strpayamt.getText().toString();
                vstrchqno = strchqno.getText().toString();

                vstrchqdt = strchqdt.getText().toString();
                if(Paymode.equals("") || Paymode==null ){
                    Paymode="cash";
                }
                Bundle pmtsmrytemp = new Bundle();
                pmtsmrytemp.putString("vstrCons_no", vstrCons_no);
                pmtsmrytemp.putString("vstrpayamt", vstrpayamt);
                pmtsmrytemp.putString("vstrchqno", vstrchqno);
                pmtsmrytemp.putString("vstrchqdt", vstrchqdt);
                pmtsmrytemp.putString("Paymode", Paymode);
                pmtsmrytemp.putString("moneyId", strposid.getText().toString().trim());
                pmtsmrytemp.putString("from",fromActivity);
                pmtsmrytemp.putBoolean("manual",manualPay);
                pmtsmrytemp.putBoolean("firstTime",false);
                pmtsmrytemp.putString("PayFlag", payFlag);


                if (BankName.equalsIgnoreCase("OTHER")){
                    if ((et_Bank_name.getText().toString().trim().length()==0)||(et_Bank_name.getText().toString().trim().equalsIgnoreCase(""))){
                        Toast.makeText(PaySummary.this,"Please enter bank name",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        BankName=et_Bank_name.getText().toString().trim();
                        pmtsmrytemp.putString("BankName", BankName);
                    }
                }
                else {
                    pmtsmrytemp.putString("BankName", BankName);
                }
                pmtsmrytemp.putString("BankID", BankID);
                pmtsmrytemp.putString("custID", custID);
                pmtsmrytemp.putString("TransID", TransID);
                pmtsmrytemp.putString("SelChoice", SelChoice);
                pmtsmrytemp.putString("BalFetch", BalFetch);
                pmtsmrytemp.putString("namefetch", namefetch);
                pmtsmrytemp.putString("MobileNofetch", MobileNofetch);

                pmtsmrytemp.putString("otherBankName",et_Bank_name.getText().toString().trim());
                pmtsmrytemp.putString("micr_no",micr_number.getText().toString().trim());
                pmtsmrytemp.putString("moneyId", et_money_id.getText().toString().trim());

                //pmtsmrytemp.putString("autoSelectDate",strchqdt.getText().toString().trim());

                intenttemp.putExtras(pmtsmrytemp);
                startActivity(intenttemp);
                finish();
            }
        });

    }



    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        Log.d("DemoApp", "item" + item);
        //  Toast.makeText(parent.getContext(), "ff: " +  parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        // NEED to change here==========
        BankList bankdtlsID = (BankList) parent.getSelectedItem();
        BankID = bankdtlsID.getId();
        BankName = bankdtlsID.getName();


        if (BankName.equalsIgnoreCase("OTHER")){
            et_Bank_name.setVisibility(View.VISIBLE);
        }
        else {
            et_Bank_name.setVisibility(View.GONE);
        }


    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    RadioButton.OnClickListener myOptionOnClickListener =
            new RadioButton.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (rbCash.isChecked()) {
                        poscollflg = "0";
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.GONE);
                        strchqdt.setText("");
                        Bankspinner.setVisibility(View.GONE);
                        Paymode="cash";
                        pkdate.setVisibility(Button.GONE);
                        chqformattxt.setVisibility(TextView.GONE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        et_Bank_name.setVisibility(View.GONE);
                        micr_number.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        ll_spinner.setVisibility(View.GONE);
                        et_money_id.setVisibility(View.GONE);

                        if (manualPay){
                            rg_manual_pay.setVisibility(View.VISIBLE);
                            money_receipt.setChecked(true);
                            rbCash.setChecked(true);
                            rbChq.setVisibility(View.GONE);
                            rbdd.setVisibility(View.GONE);
                            rbpos.setVisibility(View.GONE);
                            neft.setVisibility(View.GONE);
                            rtgs.setVisibility(View.GONE);
                            rg_manual_pay.setVisibility(View.VISIBLE);
                            money_receipt.setChecked(true);
                            strposid.setVisibility(EditText.GONE);
                            strchqno.setVisibility(EditText.GONE);
                            strchqno.setText("");
                            strchqdt.setVisibility(EditText.VISIBLE);


                            if ((manualPay)&&(firstTime)){
                                strchqdt.setText(dateFormat.format(curdate));

                            }
                            else {
                                strchqdt.setText(vstrchqdt);

                            }

                            Bankspinner.setVisibility(View.GONE);
                            Paymode="money";
                            micr_number.setVisibility(View.GONE);
                            pkdate.setVisibility(Button.GONE);
                            view2.setVisibility(View.VISIBLE);
                            chqformattxt.setVisibility(TextView.GONE);
                            ll_spinner.setVisibility(View.GONE);
                            et_Bank_name.setVisibility(View.GONE);
                            et_money_id.setVisibility(View.VISIBLE);
                        }
                        else {
                            rg_manual_pay.setVisibility(View.GONE);
                            money_receipt.setChecked(false);
                        }

                    }



                    else if(rbChq.isChecked()){
                        strchqno.setVisibility(EditText.VISIBLE);

                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode="chq";
                        poscollflg = "0";
                        view2.setVisibility(View.VISIBLE);
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        micr_number.setVisibility(View.VISIBLE);
                        strchqno.setHint("CHEQUE NUMBER");
                        strposid.setText("");
                        ll_spinner.setVisibility(View.VISIBLE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                        if (BankName.equalsIgnoreCase("OTHER")){
                            et_Bank_name.setVisibility(View.VISIBLE);
                        }
                        else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    }else if(rbdd.isChecked()){
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode="dd";
                        poscollflg = "0";
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        strchqno.setHint("DD NUMBER");
                        micr_number.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        ll_spinner.setVisibility(View.VISIBLE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                        if (BankName.equalsIgnoreCase("OTHER")){
                            et_Bank_name.setVisibility(View.VISIBLE);
                        }
                        else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    }

                    else if(rtgs.isChecked()){
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode="RTGS";
                        strchqno.setHint("RTGS NO.");
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        micr_number.setVisibility(View.GONE);
                        view2.setVisibility(View.VISIBLE);
                        ll_spinner.setVisibility(View.VISIBLE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                        if (BankName.equalsIgnoreCase("OTHER")){
                            et_Bank_name.setVisibility(View.VISIBLE);
                        }
                        else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    }

                    else if(neft.isChecked()){
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode="NEFT";
                        strchqno.setHint("NEFT NO.");
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        micr_number.setVisibility(View.GONE);
                        view2.setVisibility(View.VISIBLE);
                        ll_spinner.setVisibility(View.VISIBLE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                        if (BankName.equalsIgnoreCase("OTHER")){
                            et_Bank_name.setVisibility(View.VISIBLE);
                        }
                        else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    }else if (rbEzetap.isChecked()) {
                        poscollflg = "1";
                        Paymode = "pos";
                        strposid.setVisibility(EditText.GONE);
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.GONE);
                        strchqdt.setText(vstrchqdt);
                        Bankspinner.setVisibility(View.GONE);

                        micr_number.setVisibility(View.GONE);
                        pkdate.setVisibility(Button.GONE);
                        view2.setVisibility(View.GONE);
                        chqformattxt.setVisibility(TextView.GONE);
                        ll_spinner.setVisibility(View.GONE);
                        et_Bank_name.setVisibility(View.GONE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                    }



                    else if(rbpos.isChecked()){

                        strposid.setVisibility(EditText.VISIBLE);
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(vstrchqdt);
                        Bankspinner.setVisibility(View.GONE);
                        Paymode="pos";
                        micr_number.setVisibility(View.GONE);
                        pkdate.setVisibility(Button.GONE);
                        view2.setVisibility(View.VISIBLE);
                        chqformattxt.setVisibility(TextView.GONE);
                        ll_spinner.setVisibility(View.GONE);
                        et_Bank_name.setVisibility(View.GONE);
                        et_money_id.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.GONE);
                        money_receipt.setChecked(false);

                    }
                    else if(money_receipt.isChecked()){

                        rg_manual_pay.setVisibility(View.VISIBLE);
                        money_receipt.setChecked(true);

                        rbChq.setVisibility(View.GONE);
                        rbdd.setVisibility(View.GONE);
                        rbpos.setVisibility(View.GONE);
                        neft.setVisibility(View.GONE);
                        rtgs.setVisibility(View.GONE);
                        rg_manual_pay.setVisibility(View.VISIBLE);
                        money_receipt.setChecked(true);
                        strposid.setVisibility(EditText.GONE);
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.VISIBLE);


                        if ((manualPay)&&(firstTime)){
                            strchqdt.setText(dateFormat.format(curdate));

                        }
                        else {
                            strchqdt.setText(vstrchqdt);

                        }

                        Bankspinner.setVisibility(View.GONE);
                        Paymode="money";
                        micr_number.setVisibility(View.GONE);
                        pkdate.setVisibility(Button.GONE);
                        view2.setVisibility(View.VISIBLE);
                        chqformattxt.setVisibility(TextView.GONE);
                        ll_spinner.setVisibility(View.GONE);
                        et_Bank_name.setVisibility(View.GONE);
                        et_money_id.setVisibility(View.VISIBLE);
                    }


                    else{
                        Paymode="cash";
                    }

                }
            }; //end



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (fromActivity.equalsIgnoreCase("non-account")){
            Intent accountinfo = new Intent(getApplicationContext(), NoNAccountActivity.class);
            accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(accountinfo);
            finish();
        }
        else {
            Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
            accountinfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(accountinfo);
            finish();
        }

    }

}
