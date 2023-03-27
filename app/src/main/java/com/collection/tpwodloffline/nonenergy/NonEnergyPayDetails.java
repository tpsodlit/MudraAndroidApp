package com.collection.tpwodloffline.nonenergy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class NonEnergyPayDetails extends AppCompatActivity {

    ArrayList<String> coll_type_list = new ArrayList<>();
    EditText RecordNoEdtxt;
    String RecordNo, CollectionType;
    Spinner CollectionTypeSpnr;
    Button Proceed, startpayment;
    private String BalFetch = "";
    TextView nameval, scnumval, sectionval, amountval, ddateval;
    private DatabaseAccess databaseAccess = null;
    Context context = this;
    String Trans_Id, USER_ID, COMPANY_CODE, SCNO, REF_MODULE, REF_REG_NO, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, AMOUNT,finalAmt, DEMAND_DATE, MOBILE_NO, EMAIL, REMARKS;
    String username;
    String lat = "0.0";
    String lang = "0.0";
    String extrafields = "0";
    SharedPreferenceClass sharedPreferenceClass;
    CardView ConsumerLayout;
    ImageView search;
    String energy_flag = "0";
    String non_energy_flag = "0";
    String NSC_flag = "0";
    String CSC_flag = "0";
    String DND_flag = "0";
    String FRM_flag = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimarynonentop));
        setContentView(R.layout.activity_non_energy_pay_details);

        search = findViewById(R.id.search);
        ConsumerLayout = findViewById(R.id.data_layout);
        RecordNoEdtxt = findViewById(R.id.rec_num);
        Proceed = findViewById(R.id.proceed);
        nameval = findViewById(R.id.nameval);
        scnumval = findViewById(R.id.scnumval);
        sectionval = findViewById(R.id.sectionval);
        amountval = findViewById(R.id.amountval);
        ddateval = findViewById(R.id.ddateval);
        startpayment = findViewById(R.id.startpayment);
        sharedPreferenceClass = new SharedPreferenceClass(NonEnergyPayDetails.this);
        username = sharedPreferenceClass.getValue_string("un");
        lat = sharedPreferenceClass.getValue_string("Latitude");
        lang = sharedPreferenceClass.getValue_string("Longitude");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        getPrivilageFlags();

        search.setOnClickListener(v -> {
            if(recordsInLocal()>0){
                startActivity(new Intent(this,SearchActivity.class));
            }else {
                Toast.makeText(context, "Download data to search", Toast.LENGTH_SHORT).show();
            }
        });
        CollectionTypeSpnr = findViewById(R.id.collection_type);
        coll_type_list.add("Collection Type(ସଂଗ୍ରହ ପ୍ରକାର)");
        if (NSC_flag.equals("1")) {
            coll_type_list.add("NSC(ନୂତନ ସଂଯୋଗ)");
        }
        if (DND_flag.equals("1")) {
            coll_type_list.add("DND(ପୁନ ସଂଯୋଗ/ସଂଯୋଗ ବିଚ୍ଛିନ୍ନ)");
        }
        if (FRM_flag.equals("1")) {
            coll_type_list.add("THEFT(ଠକେଇ ପରିଚାଳନା)");
        }
        if (CSC_flag.equals("1")) {
            coll_type_list.add("MISCELLANEOUS(ବିଭିନ୍ନ)");
        }
       /* coll_type_list.add("Name Change(ନାମ ପରିବର୍ତନ)");
        coll_type_list.add("Load Change(ଲୋଡ ପରିବର୍ତନ)");
        coll_type_list.add("New Connection(ନୂତନ ସଂଯୋଗ )");
        coll_type_list.add("Enforcement(ନିୟମ ବିଭାଗ)");
        coll_type_list.add("Meter Change(ମିଟର ପରିବର୍ତ୍ତନ)");
        coll_type_list.add("RC/DC(ପୁନ ସଂଯୋଗ/ସଂଯୋଗ ବିଚ୍ଛିନ୍ନ)");*/

        ArrayAdapter year_adapter = new ArrayAdapter(NonEnergyPayDetails.this, android.R.layout.simple_spinner_item, coll_type_list);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CollectionTypeSpnr.setAdapter(year_adapter);

        CollectionTypeSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));

                String ref_module = parent.getItemAtPosition(position).toString();
                switch (ref_module) {
                    case "DND(ପୁନ ସଂଯୋଗ/ସଂଯୋଗ ବିଚ୍ଛିନ୍ନ)":
                        CollectionType = "DND";
                        break;
                    case "NSC(ନୂତନ ସଂଯୋଗ)":
                        CollectionType = "NSC";
                        break;
                    case "THEFT(ଠକେଇ ପରିଚାଳନା)":
                        CollectionType = "FRM";
                        break;
                    case "MISCELLANEOUS(ବିଭିନ୍ନ)":
                        CollectionType = "CSC";
                        break;
                    default:
                        CollectionType = "Collection Type(ସଂଗ୍ରହ ପ୍ରକାର)";
                        break;
                }
                //CollectionType = parent.getItemAtPosition(position).toString();
                // Toast.makeText(NonEnergyPayDetails.this,CollectionType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        /*String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "' WHERE USERID='" + "usernm" + "'";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);*/

        String strSelectSQL_01 = "SELECT BAL_REMAIN  " +
                "FROM SA_USER  WHERE USERID='" + username + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {

            BalFetch = cursor.getString(0);
            Log.i("BalanceRemained:::", BalFetch);

        }

        databaseAccess.close();

        Proceed.setOnClickListener(v -> {
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            RecordNo = RecordNoEdtxt.getText().toString();
            ConsumerLayout.setVisibility(View.GONE);
            if (CollectionType.equals("Collection Type(ସଂଗ୍ରହ ପ୍ରକାର)")) {
                Toast.makeText(this, "Select collection type", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(RecordNo)) {
                RecordNoEdtxt.setError("Enter reference number");
                Toast.makeText(this, "Enter reference number", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    if(recordCount(RecordNo)>0) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Proceed.getWindowToken(), 0);
                        FetchDatafromLocal(CollectionType, RecordNo);
                    }else {
                        Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private int recordsInLocal() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from NONENERGY_DATA";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    private void getPrivilageFlags() {
        databaseAccess = DatabaseAccess.getInstance(NonEnergyPayDetails.this);
        databaseAccess.open();
        String strSelectSQL_01 = "SELECT energy_flag,non_energy_flag,NSC_flag,CSC_flag,DND_flag,FRM_flag FROM SA_User WHERE lock_flag=0 and userid='" + username + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while (cursor.moveToNext()) {
            energy_flag = cursor.getString(0);
            non_energy_flag = cursor.getString(1);
            NSC_flag = cursor.getString(2);
            CSC_flag = cursor.getString(3);
            DND_flag = cursor.getString(4);
            FRM_flag = cursor.getString(5);

            sharedPreferenceClass.setValue_string("NSC_flag",NSC_flag);
            sharedPreferenceClass.setValue_string("CSC_flag",CSC_flag);
            sharedPreferenceClass.setValue_string("DND_flag",DND_flag);
            sharedPreferenceClass.setValue_string("FRM_flag",FRM_flag);
        }
        databaseAccess.close();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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
    private int recordCount(String REF_REG_NO){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getRecordCount = "select count(1) FROM NONENERGY_DATA WHERE REF_REG_NO='" + REF_REG_NO + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getRecordCount, null);
        int i=0;
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        return count;
    }


    private void FetchDatafromLocal(String collectionType, String recordNo) {
        try{
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getDataNonEn = "SELECT distinct * FROM NONENERGY_DATA WHERE REF_MODULE='" + collectionType + "' AND REF_REG_NO ='" + recordNo + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getDataNonEn, null);

        while (cursor.moveToNext()) {
            USER_ID = cursor.getString(0);
            COMPANY_CODE = cursor.getString(1);
            SCNO = cursor.getString(2);
            REF_MODULE = cursor.getString(3);
            REF_REG_NO = cursor.getString(4);
            CUST_ID = cursor.getString(5);
            DIVISION = cursor.getString(6);
            SUBDIVISION = cursor.getString(7);
            SECTION = cursor.getString(8);
            CON_NAME = cursor.getString(9);
            CON_ADD1 = cursor.getString(10);
            String CON_ADD2 = cursor.getString(11);
            AMOUNT = cursor.getString(12);
            DEMAND_DATE = cursor.getString(13);
            MOBILE_NO = cursor.getString(14);
            EMAIL = cursor.getString(15);
            REMARKS = cursor.getString(16);
            String FIELD1 = cursor.getString(17);
            String FIELD2 = cursor.getString(18);
            String FIELD3 = cursor.getString(19);
            String FIELD4 = cursor.getString(20);
            String FIELD5 = cursor.getString(21);
            String ENTRYDATE = cursor.getString(22);
            finalAmt = String.valueOf(Math.floor(Double.parseDouble(AMOUNT)));
            nameval.setText("Name        : " + CON_NAME);
            scnumval.setText("SC No.      : " + SCNO);
            sectionval.setText("Section     : " + SECTION);
            ddateval.setText("Dmd Date : " + DEMAND_DATE);
            //ddateval.setText("Dmd Date : "+ convertDateFormat(DEMAND_DATE, "DD-MM-YYYY"));
            //ddateval.setText("Dmd Date : "+new StringBuilder(DEMAND_DATE).reverse());
            amountval.setText("Payable ₹  : " + finalAmt);
            ConsumerLayout.setVisibility(View.VISIBLE);
        }
        databaseAccess.close();

        startpayment.setOnClickListener(v -> {
            lat = sharedPreferenceClass.getValue_string("Latitude");
            lang = sharedPreferenceClass.getValue_string("Longitude");
            String compId = CommonMethods.CompanyID;
            String time = CommonMethods.getCurrentTimes();
            String MR_No = REF_MODULE + REF_REG_NO;
            String MACHINE_NO = "1";
            String PAY_MODE = "1";
            String RECPT_FLG = "0";
            String OPERATOR_ID = username;
            String SEND_FLG = "0";
            String COLL_FLG = "0";
            String PMT_TYP = "NRML";
            String OPERATION_TYPE = String.valueOf(isNetworkConnected());
            String REMARKS = "ok";

            try {
                if (Double.parseDouble(BalFetch) < Double.parseDouble(finalAmt)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Balance Not Available");
                    alertDialogBuilder.setMessage("Deposit Cash and Contact Divisional / Agency" + "\n" + "Finance Section")
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
                } else if (getTxnCount(REF_REG_NO) > 0) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setMessage("Collection already done for this record number")
                            .setCancelable(false)
                            .setPositiveButton("Close", (dialog, id) -> dialog.dismiss());
                               /* .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });*/
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }else if (Double.parseDouble(finalAmt) >= Double.parseDouble("200000")) {
                    showITWarnDialognow();

                    // }else if (strtxtconsno.getText().toString().startsWith("71")) {
                } else {
                    Trans_Id = username + CommonMethods.getMilliSeconds();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Confirmation Alert");
                    alertDialogBuilder.setMessage("Have you collected ₹" + finalAmt + " from consumer towards " + collectionType + "?\n Please confirm to generate receipt.")
                            .setCancelable(false)
                            .setPositiveButton("Confirm", (dialog, id) -> {

                                databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                if (getTxnIdCount(Trans_Id) > 0) {
                                    Trans_Id = username + CommonMethods.getMilliSeconds();
                                }

                                String strSelectSQL_02 = "INSERT INTO COLL_NEN_DATA  " +
                                        " (USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION," +
                                        "SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,RECPT_DATE,RECPT_TIME," +
                                        "MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG," +
                                        "COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH,OPERATION_TYPE,REMARKS,LATTITUDE," +
                                        "LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE)" +
                                        " VALUES('" + username + "','" + compId + "','" + SCNO + "','" + REF_MODULE + "'," +
                                        " '" + REF_REG_NO + "','" + CUST_ID.replace("'", "''") + "','" + DIVISION.replace("'", "''") + "','" + SUBDIVISION.replace("'", "''") + "','" + SECTION + "','" + CON_NAME + "','" + CON_ADD1 + "','" + finalAmt + "','" + DEMAND_DATE + "','" + MOBILE_NO + "','" + EMAIL + "',strftime('%d-%m-%Y', 'now'),'" + time + "','" + MR_No + "','" + MACHINE_NO + "','" + finalAmt + "','" + PAY_MODE + "','" + RECPT_FLG + "','" + OPERATOR_ID + "','" + OPERATOR_ID + "','" + SEND_FLG + "','" + COLL_FLG + "','" + Trans_Id + "','" + PMT_TYP + "',date('now'),'" + BalFetch + "','" + OPERATION_TYPE + "','" + REMARKS + "','" + lat + "','" + lang + "','" + extrafields + "','" + extrafields + "','" + extrafields + "','" + extrafields + "','" + extrafields + "',date('now'))";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                DatabaseAccess.database.execSQL(strSelectSQL_02);

                                /*strSelectSQL_02 = "";
                                strSelectSQL_02 = "UPDATE COLL_SBM_DATA SET PHONE_NO ='" + EntryMob_string + "' WHERE CUST_ID='" + Cons_idfetch + "'";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                DatabaseAccess.database.execSQL(strSelectSQL_02);*/

                                databaseAccess.close();

                                Log.v("scnum", SCNO);
                                Intent paysumry = new Intent(getApplicationContext(), NonenReceiptGen.class);
                                paysumry.putExtra("refmodule", REF_MODULE);
                                paysumry.putExtra("refregno", REF_REG_NO);
                                paysumry.putExtra("amout", finalAmt);
                                paysumry.putExtra("scno", SCNO);
                                paysumry.putExtra("custID", CUST_ID);
                                paysumry.putExtra("TransID", Trans_Id);
                                paysumry.putExtra("BalFetch", BalFetch);
                                paysumry.putExtra("namefetch", CON_NAME);
                                paysumry.putExtra("MobileNofetch", MOBILE_NO);
                                paysumry.putExtra("Section", SECTION);
                                paysumry.putExtra("Date", CommonMethods.getTodaysDate());
                                startActivity(paysumry);
                                finish();
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                    ////////end
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private int getTxnIdCount(String trans_ID) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_NEN_DATA where TRANS_ID=" + trans_ID + "";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("Query SQL", strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private int getTxnCount(String REF_REG_NO) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_NEN_DATA where REF_REG_NO=" + REF_REG_NO + " AND RECPT_FLG=1 ";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("Query SQL", strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

}