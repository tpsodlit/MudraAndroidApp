package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AdvanceCollection extends AppCompatActivity {
    EditText ConsumerNoEdtxt, amount;
    String ConsumerNo, CustId, CName, CMobile, CAdd1, CAdd2, BMonth, PayCount, Vtype;
    private boolean checkBoxClick = false;
    private String divisionfetch = "";
    private String subdivisionfetch = "";
    private String sectionfetch = "";
    private String cur_total = "";
    private String due_date="";
    String CompanyId = CommonMethods.CompanyID;
    String BillMonth;
    Button Fetch,Collect;
    private DatabaseAccess databaseAccess = null;
    FrameLayout progressView;
    final Context context = this;
    TextView Name,Address,Amounttv;
    RelativeLayout relativeLayout;
    CardView card;
    ArrayList<String> Month_list = new ArrayList<>();
    Spinner MSpinner;
    String Monthdata;
    private String Trans_IDfetch = "";
    private String usernm = "",username;
    private String CA_server = "";
    private final String spinnerText = "ADV";
    SharedPreferenceClass sharedPreferenceClass;
    String lat = "0.0";
    String lang = "0.0";
    private String BalFetch = "";
    private int allowCollection = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_collection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        sharedPreferenceClass = new SharedPreferenceClass(AdvanceCollection.this);
        relativeLayout = findViewById(R.id.relativeLayout);
        amount = findViewById(R.id.amount);
        Collect = findViewById(R.id.collect);
        card = findViewById(R.id.card);
        MSpinner = findViewById(R.id.month);
        progressView = findViewById(R.id.progressView);
        Fetch = findViewById(R.id.proceed);
        ConsumerNoEdtxt = findViewById(R.id.con_num);
        Name = findViewById(R.id.name);
        Address = findViewById(R.id.address);
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        usernm = sessiondata.getString("userID", null);
        username = sharedPreferenceClass.getValue_string("un");

        setBalfetch();

        Fetch.setOnClickListener(v -> {
            ConsumerNo = ConsumerNoEdtxt.getText().toString();
            if(recordCount(ConsumerNo)>0) {
                getcustId(ConsumerNo);
            }else {
                relativeLayout.setVisibility(View.GONE);
                Toast.makeText(AdvanceCollection.this, "No records found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBalfetch() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        String strSelectSQL_01 = "SELECT BAL_REMAIN  " +
                "FROM SA_USER  WHERE USERID='" + username + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {
            //CompanyID = cursor.getString(2);
            //CompanyID = cursor.getString(2);
            // CompanyID = "3";
            BalFetch = cursor.getString(0);
            Log.i("BalanceRemained:::", BalFetch);

        }

        databaseAccess.close();

    }

    private int recordCount(String consumerNo) {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select count(*) from CUST_DATA where CONS_ACC='" + consumerNo + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        int i = 0;
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        return count;
    }

    /* USER_ID" TEXT,
             "DIVISION_CODE" TEXT,
             "CONS_ACC" TEXT,
             "CUST_ID" TEXT,
             "DIVISION" TEXT,
             "SUBDIVISION" TEXT,
             "SECTION" TEXT,
             "CON_NAME" TEXT,
             "CON_ADD1" TEXT,
             "CON_ADD2" TEXT,
             "PRSN_KWH" TEXT,
             "CUR_TOTAL" TEXT,
             "BILL_TOTAL" TEXT,
             "REBATE" TEXT,
             "DUE_DATE" TEXT,
             "MOBILE_NO" TEXT,
             "EMAIL" TEXT,
             "PAY_CNT" TEXT,
             "VTYPE"*/
    private void getcustId(String consumerNo) {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        resetStrings();
        String strSelectSQL_01 = "SELECT CUST_ID,CON_NAME,MOBILE_NO,CON_ADD1,CON_ADD2,PRSN_KWH,PAY_CNT,VTYPE,DIVISION,SUBDIVISION,SECTION,CUR_TOTAL,DUE_DATE " +
                "FROM CUST_DATA  WHERE CONS_ACC='" + consumerNo + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {
            CustId = cursor.getString(0);
            CName = cursor.getString(1);
            CMobile = cursor.getString(2);
            CAdd1 = cursor.getString(3);
            CAdd2 = cursor.getString(4);
            BMonth = cursor.getString(5);
            PayCount = cursor.getString(6); //
            Vtype = cursor.getString(7);
            divisionfetch = cursor.getString(8);
            subdivisionfetch = cursor.getString(9);
            sectionfetch = cursor.getString(10);
            cur_total = cursor.getString(11);
            due_date = cursor.getString(12);
        }
        cursor.close();
        databaseAccess.close();
        if (Integer.parseInt(Vtype) > 0) {
            Toast.makeText(AdvanceCollection.this,
                    "Already taken advance payment in this month",
                    Toast.LENGTH_SHORT).show();
            relativeLayout.setVisibility(View.GONE);

            return;
        }
        Name.setText(CName);
        Address.setText(CAdd1 + "\n" + CAdd2);

        amount.setText(String.valueOf(Math.round(Math.floor(Double.parseDouble(cur_total)))));
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        Month_list.clear();
        SimpleDateFormat df = new SimpleDateFormat("dd");
        SimpleDateFormat cmon = new SimpleDateFormat("MM");
        SimpleDateFormat cYear = new SimpleDateFormat("yyyy");
        int todaysDate = Integer.parseInt(df.format(c));
        int billYear = Integer.parseInt(cYear.format(c));
        int currMon = Integer.parseInt(cmon.format(c));
        Month_list.add("Bill Month");
        allowCollection = 1;
        if(todaysDate <=7)
        {
           // Month_list.add(billMonth);
            if(currMon ==1)
            {
                String tempYear = String.valueOf(billYear-1) + String.valueOf(12);
                Month_list.add(tempYear);
                if(BMonth.equals(tempYear))
                {
                    allowCollection = 0;
                    showDialognow2();
                    return;
                }
                String tempYear2 = String.valueOf(billYear-1) + String.valueOf(11);
                Month_list.add(tempYear2);
            }
            else if(currMon ==2)
            {   String tempYear = String.valueOf(billYear) + String.format("%02d",currMon-1);
                Month_list.add(tempYear);
                if(BMonth.equals(tempYear))
                {
                    allowCollection = 0;
                    showDialognow2();
                    return;
                }
                String tempYear2 = String.valueOf(billYear-1) + String.valueOf(12);
                Month_list.add(tempYear2);
            }
            else
            {   String tempYear = String.valueOf(billYear) +  String.format("%02d",currMon-1);
                Month_list.add(tempYear);
                if(BMonth.equals(tempYear))
                {
                    allowCollection = 0;
                    showDialognow2();
                    return;
                }
                String tempYear2 = String.valueOf(billYear) +  String.format("%02d",currMon-2);
                Month_list.add(tempYear2);
            }
        }
        else
        {
            if(currMon ==1)
            {
                String tempYear = String.valueOf(billYear-1) + String.valueOf(12);
                Month_list.add(tempYear);
                if(BMonth.equals(tempYear))
                {
                    allowCollection = 0;
                    showDialognow2();
                    return;
                }
            }
            else
            {   String tempYear = String.valueOf(billYear) + String.format("%02d",currMon-1);
                Month_list.add(tempYear);
                if(BMonth.equals(tempYear))
                {
                    allowCollection = 0;
                    showDialognow2();
                    return;
                }
            }

        }

        //Check for not allowing advance collection if duedate >= todays date (Mail from Rajiv Sir dated-01-12-2022)
        if (CommonMethods.convertStringToDate(due_date).after(CommonMethods.getTodaysPlainDate()) ||
                CommonMethods.convertStringToDate(due_date).equals(CommonMethods.getTodaysPlainDate()))
        {
            allowCollection = 0;
            showDialognow2();
            return;
        }

       /* Monthdata = BMonth;
        Month_list.add("Bill Month");
        Month_list.add(Monthdata);
        Month_list.add(String.valueOf(Integer.parseInt(Monthdata)+1));*/

        ArrayAdapter adapter = new ArrayAdapter(AdvanceCollection.this, android.R.layout.simple_spinner_item, Month_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MSpinner.setAdapter(adapter);

        MSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));
                BillMonth = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AdvanceCollection.this, BillMonth, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        relativeLayout.setVisibility(View.VISIBLE);
        databaseAccess.close();
        Collect.setOnClickListener(v->{
            setBalfetch();
            String payable = amount.getText().toString();
            //Change request dated 1309 Advance should always be > Rs. 100 or 50 % of last month demand
            String setError = "";
            double advpaybleamt = (0.5)*(Double.parseDouble(cur_total));
            if(advpaybleamt > 100){
                setError = "Payable amount cannot be less than "+ String.valueOf(advpaybleamt);
            }
            else{
                setError = "Payable amount cannot be less than 100";
            }

            if(BMonth.equals(BillMonth))
            {
                showDialognow2();
                return;
            }
            if(allowCollection == 0)
            {
                showDialognow2();
                //Toast.makeText(AdvanceCollection.this, "Bill already available, Please go to normal collection", Toast.LENGTH_SHORT).show();
                return;
            }

            if(BillMonth.equals("Bill Month")){
                Toast.makeText(AdvanceCollection.this, "Select bill month to proceed", Toast.LENGTH_SHORT).show();

            } else if (getBillCount(ConsumerNo) > 0) {

                showDialognow();

            } else if(payable.equals("")){
                Toast.makeText(AdvanceCollection.this, "Enter payable amount", Toast.LENGTH_SHORT).show();
            }else if(payable.equals("0")){
                Toast.makeText(AdvanceCollection.this, "Payable amount should not be zero", Toast.LENGTH_SHORT).show();
            }else if (Double.parseDouble(BalFetch) < Double.parseDouble(payable)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Balance Not Available");
                alertDialogBuilder.setMessage("Deposit Cash and Contact Divisional / Agency Finance Section")
                        .setCancelable(false)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                               /* .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });*/
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            } else if (Double.parseDouble(payable)>=Double.parseDouble("200000")) {
                showITWarnDialognow();
                //Math.floor(Double.parseDouble(cur_total))
            }
            //else if (Double.parseDouble(payable) > 0 && Double.parseDouble(payable) >= Math.floor(Double.parseDouble(cur_total))) {
            else if (Double.parseDouble(payable) > 100 && Double.parseDouble(payable) >= Math.floor(advpaybleamt)) {
                StartPaymentProcess(payable);
//            }else if(Double.parseDouble(payable) > 19) {
//                StartPaymentProcess(payable);
            }else {
                //amount.setError("Amount cannot be less than 100/last months current bill");
                amount.setError(setError);
                Toast.makeText(AdvanceCollection.this, setError, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialognow2() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Bill already available, Please go to normal collection")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) ->{
                            dialog.cancel();
                            finish();
                        }
                        );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void showITWarnDialognow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Cash transaction/collection is not allowed more than 2 Lakh")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                               /* .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });*/
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void StartPaymentProcess(String payable) {
        Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
        lat = sharedPreferenceClass.getValue_string("Latitude");
        lang = sharedPreferenceClass.getValue_string("Longitude");

        if (lat.equals("")) {
            lat = "0.0";
        }

        if (lang.equals("")) {
            lang = "0.0";
        }
        String time = getCurrentTime();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setMessage("You have collected ₹" + payable + " as advance payment from consumer.")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();

                        String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,COLL_MONTH,TOT_PAID,TRANS_ID,RECPT_FLG,TRANS_DATE,RECPT_DATE,RECPT_TIME,CA_SERVER,DB_TYPE_SERVER,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE,LONGITUDE)" +
                                " VALUES('" + ConsumerNo + "','" + CustId + "','" + divisionfetch + "','" + subdivisionfetch + "'," +
                                " '" + sectionfetch + "','" + CName.replace("'", "''") + "','" + CAdd1.replace("'", "''") + "','" + CAdd2.replace("'", "''") + "','" + BillMonth + "','" + payable + "','" + Trans_IDfetch + "',0,date('now'),date('now'),'" + time + "','" + CA_server + "','" + PayCount + "','" + Vtype + "','" + spinnerText + "','" + lat + "','" + lang + "' ) ";
                        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                        DatabaseAccess.database.execSQL(strSelectSQL_02);

                        databaseAccess.close();

                        Intent paysumry = new Intent(AdvanceCollection.this, PaySummary.class);
                        Bundle Bunpayamtdtls = new Bundle();
                        Bunpayamtdtls.putString("Pableamt", payable);
                        Bunpayamtdtls.putString("consacc", ConsumerNo);
                        Bunpayamtdtls.putString("custID", CustId);
                        Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                        Bunpayamtdtls.putString("SelChoice", "AcctNo");
                        Bunpayamtdtls.putString("BalFetch", BalFetch);
                        Bunpayamtdtls.putString("namefetch", CName);
                        Bunpayamtdtls.putString("MobileNofetch", CMobile);
                        Bunpayamtdtls.putString("from", "account");
                        Bunpayamtdtls.putBoolean("manual", checkBoxClick);
                        Bunpayamtdtls.putString("PayFlag", "EnergyNRML");
                        paysumry.putExtras(Bunpayamtdtls);
                        startActivity(paysumry);
                        finish();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();


    }

    private void resetStrings() {
        CustId = "";
        CName = "";
        CMobile = "";
        CAdd1 = "";
        CAdd2 = "";
        BMonth = "";
        PayCount = "";
        Vtype = "";
        divisionfetch = "";
        subdivisionfetch = "";
        sectionfetch = "";
    }
    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        //date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String localTime = date.format(currentLocalTime);

        return localTime;
    }
    private int getBillCount(String entryNum_string) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where CONS_ACC = '" + entryNum_string + "' and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and RECPT_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private void showDialognow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Collection limit");
        alertDialogBuilder.setMessage("You can collect only once in a day from one consumer.")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, id) -> dialog.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}