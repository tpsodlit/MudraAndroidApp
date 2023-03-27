package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.DuplicateReceipt;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AcCollection extends AppCompatActivity {

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    public DatabaseAccess databaseAccess = null;
    private static EditText strEntryNum;
    private static RadioButton rbbill;
    private static RadioButton rbAsd;
    private static RadioButton rbAdv;
    private static RadioButton rbrcf;
    private static RadioButton rbassmnt;
    private static RadioButton rbDRecpt;
    private static RadioButton rbDRecptr;
    private String strconsno = "";
    private String serverDat = "";
    final Context context = this;
    Timer timer;
    private ImageView enrgy_cons_srch;

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

       /* timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 900000); //auto logout in 5 minutes*/
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(AcCollection.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_collection);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarback.getNavigationIcon().setColorFilter(getResources().
                getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        final Context context = this;

        Log.d("DemoApp", "strconsno" + strconsno);
        rbbill = (RadioButton) findViewById(R.id.bill);
        rbAsd = (RadioButton) findViewById(R.id.asd);
        rbAdv = (RadioButton) findViewById(R.id.advpay);
        rbrcf = (RadioButton) findViewById(R.id.rcf);
        rbassmnt = (RadioButton) findViewById(R.id.assmnt);
        rbDRecpt = (RadioButton) findViewById(R.id.DRecpt);
        rbDRecptr = (RadioButton) findViewById(R.id.DRecptr);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        enrgy_cons_srch = findViewById(R.id.enrgy_cons_srch);
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();

        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        String sdflg = sessionssodata.getString("sdflg", null); // getting String
        String advflg = sessionssodata.getString("advflg", null);
        String rcflg = sessionssodata.getString("rcflg", null); // getting String
        String assflg = sessionssodata.getString("assflg", null);
        String asdlg = sessionssodata.getString("asdlg", null); // getting String
        String pfflg = sessionssodata.getString("pfflg", null);
        String dwflg = sessionssodata.getString("dwflg", null); // getting String
        String blflg = sessionssodata.getString("blflg", null);
        serverDat = sessionssodata.getString("serverDate", null);

        Log.d("DemoApp", "asdlg" + asdlg);
        if (blflg.equals("1")) {
            rbbill.setClickable(true);
            rbbill.setEnabled(true);
        } else {
            rbbill.setClickable(false);
            rbbill.setEnabled(false);
        }
        if (asdlg.equals("1")) {
            rbAsd.setClickable(true);
            rbAsd.setEnabled(true);
        } else {
            rbAsd.setClickable(false);
            rbAsd.setEnabled(false);
        }
        if (advflg.equals("1")) {
            rbAdv.setClickable(true);
            rbAdv.setEnabled(true);
        } else {
            rbAdv.setClickable(false);
            rbAdv.setEnabled(false);
        }
        if (rcflg.equals("1")) {
            rbrcf.setClickable(true);
            rbrcf.setEnabled(true);
        } else {
            rbrcf.setClickable(false);
            rbrcf.setEnabled(false);
        }
        if (assflg.equals("1")) {
            rbassmnt.setClickable(true);
            rbassmnt.setEnabled(true);
        } else {
            rbassmnt.setClickable(false);
            rbassmnt.setEnabled(false);
        }
        //Consumer No. search
        enrgy_cons_srch.setOnClickListener(v -> {
            consumerNoSearch(context);
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEntryNum = (EditText) findViewById(R.id.consno);
                strconsno = strEntryNum.getText().toString();
                Log.d("DemoApp", "strconsno" + strconsno);
                if (TextUtils.isEmpty(strconsno)) {
                    strEntryNum.setError("Blank Account Not taken");
                    Log.d("DemoApp", "Query SQL " + strconsno);
                } else if (strconsno.length() > 0 && strconsno.length() < 12) {
                    strEntryNum.setError("Enter Valid Account of 12 digit");
                    Log.d("DemoApp", "Query SQL " + strconsno.length());
                } else {
                    String SelChoice = "";
                    if (rbbill.isChecked() == true) {
                        SelChoice = "AcctNo";
                    }
                    if (rbAsd.isChecked() == true) {
                        SelChoice = "Asd";
                    }
                    if (rbAdv.isChecked() == true) {
                        SelChoice = "Adv";
                    }
                    if (rbrcf.isChecked() == true) {
                        SelChoice = "rcf";
                    }
                    if (rbassmnt.isChecked() == true) {
                        SelChoice = "Assmnt";
                    }
                    if (rbDRecpt.isChecked() == true) {
                        SelChoice = "DupRecpt";
                    }
                    if (rbDRecptr.isChecked() == true) {
                        SelChoice = "DupRecptr";
                    }
                    Log.d("DemoApp", SelChoice);
                    EditText Entry = (EditText) findViewById(R.id.consno);
                    String EntryNum = Entry.getText().toString();
                    Log.d("Analogic Impact Reprint",SelChoice);
                    Log.d("Analogic Impact Reprint",EntryNum);
                    if (SelChoice.equals("DupRecpt")) {
                        try {
                            // new FetchBillDetOnline().execute(StrBillInfo);
                            if (new DatabaseAccess().checkOTSData(context, strconsno) > 0) {
                                showDialogColPrnt(Constants.btnOTSPrint,
                                        Constants.btnPrint,
                                        SelChoice, strconsno,
                                        Constants.bodyMsgOTSNormalPrint);
                            } else {
                                collectionData(SelChoice, strconsno, 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                            //OLD Code------Before Ezetap,OTS changes
                            /*
                            if(recordCount()>0) {
                                Intent accountinfo = new Intent(getApplicationContext(), DuplicateSummary.class);
                                Bundle extrasvalcol = new Bundle();
                                extrasvalcol.putString("SelChoice", SelChoice);
                                extrasvalcol.putString("EntryNum", EntryNum);
                                // extrasval.putString("Validcon", "0");
                                accountinfo.putExtras(extrasvalcol);
                                startActivity(accountinfo);
                                finish();
                            }else{
                                Toast.makeText(context, "No records found...", Toast.LENGTH_SHORT).show();
                            }*/

                    } else if (SelChoice.equals("DupRecptr")) {
                        Intent accountinfo = new Intent(getApplicationContext(), DuplicateReceipt.class);
                        Bundle extrasvalcol = new Bundle();
                        extrasvalcol.putString("SelChoice", SelChoice);
                        extrasvalcol.putString("EntryNum", EntryNum);
                        // extrasval.putString("Validcon", "0");
                        accountinfo.putExtras(extrasvalcol);
                        startActivity(accountinfo);
                        finish();
                    }
                    else {
                        collectionData(SelChoice, EntryNum, 1);
                    }

                }
            }
        });


        toolbarback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ColDashboard.class));
                finish();
            }
        });
    }
    private void showDialog(String SelChoice, String cons_no) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Constants.confTitleDialog);
        alertDialogBuilder.setMessage(Constants.bodyMsgOTSORNormal)
                .setCancelable(false)
                .setNegativeButton("OTS Amount",
                        (dialog, id) -> {
                            collectionData(SelChoice, cons_no, 0);
                            dialog.cancel();
                        })
                .setPositiveButton("Current Amount",
                        (dialog, id) -> {
                            collectionData(SelChoice, cons_no, 1);
                            dialog.cancel();
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void collectionData(String SelChoice, String cons_no, int OtsNormalFlag) {

        try {
            if (SelChoice.equals("DupRecpt")) {

                if (OtsNormalFlag == 0) {
                    if (new DatabaseAccess().otsRecordCountReceipt(this, cons_no) > 0) {
                        String from = "enOTS";
                        dupSummaryIntent(from, SelChoice, cons_no);
                    } else {
                        Toast.makeText(this,
                                "No OTS records found...",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (new DatabaseAccess().recordCountReceipt(this, cons_no) > 0) {
                        String from = "en";
                        dupSummaryIntent(from, SelChoice, cons_no);
                    } else {
                        Toast.makeText(this,
                                "No records found...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Date dates = new Date();
                SimpleDateFormat sdf = new
                        SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date currentDate = sdf.parse(
                        DateFormat.format("dd-MM-yyyy",
                                dates.getTime()).toString());
                Date serverDateParse = sdf.parse(serverDat);
                assert currentDate != null;
                if (currentDate.compareTo(serverDateParse) == 0) {

                    Intent intent = new Intent(getApplicationContext(),
                            RadioOTSNonOTS.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("SelChoice", SelChoice);
                    bundle.putString("EntryNum", cons_no);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    CommonMethods.showDialog(this, this,
                            Constants.titleCurrentDate, Constants.bodyMsgDate);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void getMobileNumberFromLocal(){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select MOBILE_NO from CUST_DATA where CONS_ACC='"+strconsno+"'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        int i=0;
        String mobileNum = "";
        while (cursor.moveToNext()) {
            mobileNum = cursor.getString(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        //strMobNonew.setText(mobileNum);
        databaseAccess.close();
    }
    private int recordCount(){
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        //String getMobileNum = "select count(*) from CUST_DATA where CONS_ACC='"+strconsno+"'";
        String getMobileNum = "select count(1) FROM COLL_SBM_DATA A WHERE CONS_ACC='" + strconsno + "' and RECPT_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        int i=0;
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        //Toast.makeText(context, "Mobile number: " +mobileNum, Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        return count;
    }
    private void consumerNoSearch(Context context) {
        if (new DatabaseAccess().custDataCnt(context) > 0) {
            startActivity(new Intent(this, EnergySearchActivity.class));
        } else {
            Toast.makeText(context,
                    "Download data to search", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogColPrnt(String otsNBtn,
                                   String otsPBtn,
                                   String SelChoice,
                                   String cons_no,
                                   String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Constants.confTitleDialog);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton(otsNBtn,
                        (dialog, id) -> {
                            collectionData(SelChoice, cons_no, 0);
                            dialog.cancel();
                        })
                .setPositiveButton(otsPBtn,
                        (dialog, id) -> {
                            collectionData(SelChoice, cons_no, 1);
                            dialog.cancel();
                        })
                .setNeutralButton("Cancel",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void dupSummaryIntent(String from,
                                  String SelChoice,
                                  String cons_no) {
        Intent intent = new Intent(getApplicationContext(),
                DuplicateSummary.class);
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        bundle.putString("SelChoice", SelChoice);
        bundle.putString("EntryNum", cons_no);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }



}

// replaced the following code to a common method collectionData in  (onClick of btnSearch.setOnClickListener)
/*


try {
                            Date dates = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates.getTime()).toString());
                            Date serverDateParse = sdf.parse(serverDat);
                            if (currentDate.compareTo(serverDateParse) == 0) {
                                if (timer != null) {
                                    timer.cancel();
                                    // Log.i("Main", "cancel timer");
                                    timer = null;
                                }
                                // Intent accountinfo = new Intent(getApplicationContext(), AccountInfo.class);
                                Intent accountinfo = new Intent(getApplicationContext(), CheckMobileNumActivity.class);
                                Bundle extrasvalcol1 = new Bundle();
                                extrasvalcol1.putString("SelChoice", SelChoice);
                                extrasvalcol1.putString("EntryNum", EntryNum);
                                // extrasval.putString("Validcon", "0");
                                accountinfo.putExtras(extrasvalcol1);
                                startActivity(accountinfo);
                                //finish();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                                                AcCollection.this.finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }







 */