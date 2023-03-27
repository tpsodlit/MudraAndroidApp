package com.collection.tpwodloffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.activity.AcCollection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaySummaryNonAcnt extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatabaseAccess databaseAccess = null;
    final Context context = this;
    private static RadioButton rbCash;
    private static RadioButton rbChq;
    private static RadioButton rbdd;
    private static RadioButton rbpos;
    private static EditText strchqdt;
    private static EditText strchqno;
    private static TextView strCons_no;
    private static TextView strcustName;
    private static TextView strpayamt;
    private static String Paymode = "";
    private Spinner Bankspinner = null;
    private String BankID = "";
    private String BankName = "";
    private Button pkdate;
    private String vstrCons_no = "";
    private String vstrpayamt = "";
    private String vstrchqno = "";
    private String vstrchqdt = "";
    private String strdate = "";
    private String strID = "0";
    private static TextView chqformattxt;
    private DateFormat dateFormat = null;
    private Date curdate = null;
    private String custID = "";
    private String PosID = "";
    private static EditText strposid;
    private String SelChoice = "";
    private String BalFetch = "";
    private String namefetch = "";
    private String TransID = "";
    private EditText et_Bank_name;
    private String MobileNofetch = "";
    private String otherBankName = "";
    private RadioButton rtgs;
    private RadioButton neft;
    private EditText micr_no;
    private String micrNumber="";
    @Override
    protected void onResume() {
        super.onResume();
      //  CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_summary_non_acnt);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String Pay_Amt = "";
        String consacc = "";
        vstrCons_no = "";
        vstrpayamt = "";
        vstrchqno = "";
        vstrchqdt = "";
        Paymode = "";
        strID = "0";
        strCons_no = (TextView) findViewById(R.id.Cons_no);
        strpayamt = (TextView) findViewById(R.id.PayAmt);
        strcustName = (TextView) findViewById(R.id.CustName);
        rbCash = (RadioButton) findViewById(R.id.cash);
        chqformattxt = (TextView) findViewById(R.id.chqformattxt);
        rbChq = (RadioButton) findViewById(R.id.chq);
        rbdd = (RadioButton) findViewById(R.id.dd);
        rbpos = (RadioButton) findViewById(R.id.pos);
        strchqno = (EditText) findViewById(R.id.chqno);
        strchqdt = (EditText) findViewById(R.id.chqdt);
        strposid = (EditText) findViewById(R.id.posid);
        et_Bank_name = findViewById(R.id.et_Bank_name);
        neft = findViewById(R.id.neft);
        rtgs = findViewById(R.id.rtgs);
        micr_no=findViewById(R.id.micr_no);
        micr_no.setVisibility(View.GONE);
        Button btnNext = (Button) findViewById(R.id.submitbtn);
        Button btnBack = (Button) findViewById(R.id.back);
        Bankspinner = (Spinner) findViewById(R.id.spinBank);
        pkdate = (Button) findViewById(R.id.pkdate);
        strchqdt.setEnabled(false);
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
        Bundle pmtsmrytemp1 = null;
        Bundle extrasvalcol = null;

        try {

            pmtsmrytemp1 = getIntent().getExtras();
            strID = pmtsmrytemp1.getString("strID");
            if (strID.equals("") || strID == null) {
                strID = "0";
            }
            // Log.d("DemoApp", " strID   " + strID);
        } catch (Exception e) {
            e.printStackTrace();
            strID = "0";
        }
        try {
            Log.d("DemoApp", " strID11   " + strID);
            if (strID.equals("1")) {
                vstrCons_no = pmtsmrytemp1.getString("vstrCons_no");
                vstrpayamt = pmtsmrytemp1.getString("vstrpayamt");
                vstrchqno = pmtsmrytemp1.getString("vstrchqno");
                vstrchqdt = pmtsmrytemp1.getString("vstrchqdt");
                Paymode = pmtsmrytemp1.getString("Paymode");
                BankName = pmtsmrytemp1.getString("BankName");
                BankID = pmtsmrytemp1.getString("BankID");
                strdate = pmtsmrytemp1.getString("strdate");
                strID = pmtsmrytemp1.getString("strID");
                TransID = pmtsmrytemp1.getString("TransID");
                BalFetch = pmtsmrytemp1.getString("BalFetch");
                namefetch = pmtsmrytemp1.getString("namefetch");
                MobileNofetch = pmtsmrytemp1.getString("MobileNofetch");
                otherBankName = pmtsmrytemp1.getString("otherBankName");
                et_Bank_name.setText(otherBankName);


                Log.d("DemoApp", " vstrCons_no1   " + vstrCons_no);
                Log.d("DemoApp", " vstrpayamt1   " + vstrpayamt);
                Log.d("DemoApp", " vstrchqno 1  " + vstrchqno);
                Log.d("DemoApp", " vstrchqdt1   " + vstrchqdt);
                Log.d("DemoApp", " BankName   " + BankName);
                Log.d("DemoApp", " BankID   " + BankID);
                Log.d("DemoApp", " strID   " + strID);
                Log.d("DemoApp", " TransID   " + TransID);
                Log.d("DemoApp", " BalFetch   " + BalFetch);
                if (Paymode.equals("cash") || Paymode.equals("") || Paymode == null) {
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
                    micr_no.setVisibility(View.GONE);

                } else if (Paymode.equals("chq")) {
                    rbChq.setChecked(true);
                    rbCash.setChecked(false);
                    rbdd.setChecked(false);
                    rbpos.setChecked(false);
                    strposid.setVisibility(EditText.GONE);
                    strposid.setText("");
                    strchqno.setVisibility(EditText.VISIBLE);
                    strchqno.setHint("CHEQUE NUMBER");
                    //strchqno.setText("");
                    strchqdt.setVisibility(EditText.VISIBLE);
                    Bankspinner.setVisibility(View.VISIBLE);
                    pkdate.setVisibility(Button.VISIBLE);
                    chqformattxt.setVisibility(TextView.VISIBLE);
                    micr_no.setVisibility(View.VISIBLE);
                } else if (Paymode.equals("dd")) {
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
                    micr_no.setVisibility(View.VISIBLE);
                } else if (Paymode.equals("NEFT")) {
                    rbdd.setChecked(true);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
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
                    micr_no.setVisibility(View.GONE);
                } else if (Paymode.equals("RTGS")) {
                    rbdd.setChecked(true);
                    rbCash.setChecked(false);
                    rbChq.setChecked(false);
                    rbpos.setChecked(false);
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
                    micr_no.setVisibility(View.GONE);
                } else if (Paymode.equals("pos")) {
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
                    micr_no.setVisibility(View.GONE);
                }


                strCons_no.setText(vstrCons_no);
                strpayamt.setText(vstrpayamt);
                strcustName.setText("Name:" + namefetch);
                //btnNext.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ////////////

        rbCash.setOnClickListener(myOptionOnClickListener);// include hide to  mtr reading inputbox
        rbChq.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        rbdd.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        rbpos.setOnClickListener(myOptionOnClickListener);// include visible to  mtr reading inputbox
        neft.setOnClickListener(myOptionOnClickListener);
        rtgs.setOnClickListener(myOptionOnClickListener);
        //rbCash.setChecked(false);
        dateFormat = new SimpleDateFormat("ddMMyyyy");
        curdate = new Date();
        Log.d("DemoApp", " dateFormat.format(date)   " + dateFormat.format(curdate));
        String Pay_Amt1 = "";
        try {
            Bundle Bunpayamtdtls = getIntent().getExtras();
            Pay_Amt1 = Bunpayamtdtls.getString("Pableamt");
            consacc = Bunpayamtdtls.getString("consacc");
            custID = Bunpayamtdtls.getString("custID");
            TransID = Bunpayamtdtls.getString("TransID");
            SelChoice = Bunpayamtdtls.getString("SelChoice");
            BalFetch = Bunpayamtdtls.getString("BalFetch");
            namefetch = Bunpayamtdtls.getString("namefetch");
        } catch (Exception e) {
        }
        if (!strID.equals("1")) {
            strCons_no.setText(consacc);
            strpayamt.setText(Pay_Amt1);
            strcustName.setText("Name:" + namefetch);
            //comming from calender activity//
        }

        // Spinner click listener
        Bankspinner.setOnItemSelectedListener(this);
        if (Paymode.equals("cash") || Paymode.equals("") || Paymode == null) {
            Bankspinner.setVisibility(View.INVISIBLE);
        }
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strSelectSQL_01 = "";
        try {
            if (strID.equals("1")) {
                strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1 and bank_id='" + BankID + "' union all SELECT bank_id,bank_name,status FROM mst_bank where status=1 and bank_id!='" + BankID + "' ";
            } else {
                strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1  order by bank_id";
            }
        } catch (Exception e) {
            strSelectSQL_01 = "SELECT bank_id,bank_name,status FROM mst_bank where status=1 order by bank_id";
        }

        Log.d("DemoApp", " strSelectSQL_01   " + strSelectSQL_01);
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        ArrayList<BankList> BankDtlList = new ArrayList<>();
        int i = 0;
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


        String a = String.valueOf(Bankspinner.getSelectedItem());

        toolbarback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AcCollection.class));
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if ((Paymode.equals("chq") || (Paymode.equals("dd")) || (Paymode.equals("NEFT")) || (Paymode.equals("RTGS"))) && ((strchqno.getText().toString().length() < 4) || (strchqno.getText().toString().length() == 0))) {
                    strchqno.setError("Enter Correct DD/Cheque/NEFT/RTGS number");
                } else if ((Paymode.equals("chq") || Paymode.equals("dd") || Paymode.equals("NEFT") ||
                        Paymode.equals("RTGS")) && BankID.equals("0")) {
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
                                    PaySummaryNonAcnt.this.finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    vstrCons_no = strCons_no.getText().toString();
                    vstrpayamt = strpayamt.getText().toString();
                    vstrchqno = strchqno.getText().toString();
                    vstrchqdt = strchqdt.getText().toString();
                    PosID = strposid.getText().toString();
                    micrNumber=micr_no.getText().toString().trim();
                    if (Paymode.equals("") || Paymode == null) {
                        Paymode = "cash";
                    }

                    Log.d("DemoApp", "vstrCons_no" + vstrCons_no);
                    Log.d("DemoApp", "vstrpayamt" + vstrpayamt);
                    Log.d("DemoApp", "vstrchqno" + vstrchqno);
                    Log.d("DemoApp", "vstrchqdt" + vstrchqdt);
                    Log.d("DemoApp", "Paymode" + Paymode);
                    Log.d("DemoApp", " custID" + custID);

                    Intent accountsum = new Intent(getApplicationContext(), SumDtlNonAcnt.class);
                    Bundle pmtsmry = new Bundle();
                    pmtsmry.putString("vstrCons_no", vstrCons_no);
                    pmtsmry.putString("vstrpayamt", vstrpayamt);
                    pmtsmry.putString("vstrchqno", vstrchqno);
                    pmtsmry.putString("vstrchqdt", vstrchqdt);
                    pmtsmry.putString("Paymode", Paymode);
                    pmtsmry.putString("micr_no",micrNumber);

                    if (BankName.equalsIgnoreCase("OTHER")) {
                        if ((et_Bank_name.getText().toString().trim().length() == 0) || (et_Bank_name.getText().toString().trim().equalsIgnoreCase(""))) {
                            Toast.makeText(PaySummaryNonAcnt.this, "Please enter bank name", Toast.LENGTH_SHORT).show();
                        } else {
                            BankName = et_Bank_name.getText().toString().trim();
                            pmtsmry.putString("BankName", BankName);
                            pmtsmry.putString("PosID", PosID);
                            pmtsmry.putString("BankID", BankID);
                            pmtsmry.putString("custID", custID);
                            pmtsmry.putString("TransID", TransID);
                            pmtsmry.putString("SelChoice", SelChoice);
                            pmtsmry.putString("BalFetch", BalFetch);
                            pmtsmry.putString("namefetch", namefetch);
                            pmtsmry.putString("MobileNofetch", MobileNofetch);

                            accountsum.putExtras(pmtsmry);
                            startActivity(accountsum);
                            finish();
                        }
                    } else {
                        pmtsmry.putString("BankName", BankName);

                        pmtsmry.putString("PosID", PosID);
                        pmtsmry.putString("BankID", BankID);
                        pmtsmry.putString("custID", custID);
                        pmtsmry.putString("TransID", TransID);
                        pmtsmry.putString("SelChoice", SelChoice);
                        pmtsmry.putString("BalFetch", BalFetch);
                        pmtsmry.putString("namefetch", namefetch);
                        pmtsmry.putString("MobileNofetch", MobileNofetch);

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
                Intent accountinfo = new Intent(getApplicationContext(), AcCollection.class);
                startActivity(accountinfo);
                finish();
            }
        });


        pkdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenttemp = new Intent(PaySummaryNonAcnt.this, CalenderViewP.class);
                vstrCons_no = strCons_no.getText().toString();
                vstrpayamt = strpayamt.getText().toString();
                vstrchqno = strchqno.getText().toString();
                // vstrchqdt = strchqdt.getText().toString();
                if (Paymode.equals("") || Paymode == null) {
                    Paymode = "cash";
                }
                Bundle pmtsmrytemp = new Bundle();
                pmtsmrytemp.putString("vstrCons_no", vstrCons_no);
                pmtsmrytemp.putString("vstrpayamt", vstrpayamt);
                pmtsmrytemp.putString("vstrchqno", vstrchqno);
                pmtsmrytemp.putString("vstrchqdt", vstrchqdt);
                pmtsmrytemp.putString("Paymode", Paymode);

                if (BankName.equalsIgnoreCase("OTHER")) {
                    if ((et_Bank_name.getText().toString().trim().length() == 0) || (et_Bank_name.getText().toString().trim().equalsIgnoreCase(""))) {
                        Toast.makeText(PaySummaryNonAcnt.this, "Please enter bank name", Toast.LENGTH_SHORT).show();
                    } else {
                        BankName = et_Bank_name.getText().toString().trim();
                        pmtsmrytemp.putString("BankName", BankName);
                    }
                } else {
                    pmtsmrytemp.putString("BankName", BankName);
                }

                pmtsmrytemp.putString("BankID", BankID);
                pmtsmrytemp.putString("custID", custID);
                pmtsmrytemp.putString("TransID", TransID);
                pmtsmrytemp.putString("SelChoice", SelChoice);
                pmtsmrytemp.putString("BalFetch", BalFetch);
                pmtsmrytemp.putString("namefetch", namefetch);
                pmtsmrytemp.putString("otherBankName",et_Bank_name.getText().toString().trim());
                intenttemp.putExtras(pmtsmrytemp);
                startActivity(intenttemp);
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
            new RadioButton.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (rbCash.isChecked()) {
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.GONE);
                        strchqdt.setText("");
                        Bankspinner.setVisibility(View.GONE);
                        Paymode = "cash";
                        pkdate.setVisibility(Button.GONE);
                        chqformattxt.setVisibility(TextView.GONE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        et_Bank_name.setVisibility(View.GONE);
                        micr_no.setVisibility(View.GONE);

                    } else if (rbChq.isChecked()) {
                        strchqno.setVisibility(EditText.VISIBLE);

                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode = "chq";
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        micr_no.setVisibility(View.VISIBLE);
                        strchqno.setHint("CHEQUE NUMBER");
                        strposid.setText("");

                        if (BankName.equalsIgnoreCase("OTHER")) {
                            et_Bank_name.setVisibility(View.VISIBLE);
                        } else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    } else if (rbdd.isChecked()) {
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode = "dd";
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        micr_no.setVisibility(View.VISIBLE);
                        strchqno.setHint("DD NUMBER");
                        if (BankName.equalsIgnoreCase("OTHER")) {
                            et_Bank_name.setVisibility(View.VISIBLE);
                        } else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    } else if (rtgs.isChecked()) {
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode = "RTGS";
                        strchqno.setHint("RTGS NO.");
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        micr_no.setVisibility(View.GONE);
                        if (BankName.equalsIgnoreCase("OTHER")) {
                            et_Bank_name.setVisibility(View.VISIBLE);
                        } else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    } else if (neft.isChecked()) {
                        strchqno.setVisibility(EditText.VISIBLE);
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.VISIBLE);
                        Paymode = "NEFT";
                        strchqno.setHint("NEFT NO.");
                        pkdate.setVisibility(Button.VISIBLE);
                        chqformattxt.setVisibility(TextView.VISIBLE);
                        strposid.setVisibility(EditText.GONE);
                        strposid.setText("");
                        micr_no.setVisibility(View.GONE);
                        if (BankName.equalsIgnoreCase("OTHER")) {
                            et_Bank_name.setVisibility(View.VISIBLE);
                        } else {
                            et_Bank_name.setVisibility(View.GONE);
                        }


                    } else if (rbpos.isChecked()) {

                        strposid.setVisibility(EditText.VISIBLE);
                        strchqno.setVisibility(EditText.GONE);
                        strchqno.setText("");
                        strchqdt.setVisibility(EditText.VISIBLE);
                        strchqdt.setText(dateFormat.format(curdate));
                        Bankspinner.setVisibility(View.GONE);
                        Paymode = "pos";
                        pkdate.setVisibility(Button.GONE);
                        micr_no.setVisibility(View.GONE);
                        chqformattxt.setVisibility(TextView.GONE);
                        et_Bank_name.setVisibility(View.GONE);

                    } else {
                        Paymode = "cash";
                    }
                }

                ;//end


            };

}
