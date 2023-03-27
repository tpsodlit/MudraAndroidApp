package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.ReportPrinters.PrintDetailedReportAmigoThermalNew;
import com.collection.tpwodloffline.ReportPrinters.PrintDetailedReportAnalogicImpactNew;
import com.collection.tpwodloffline.ReportPrinters.PrintReportAmigoThermalNew;
import com.collection.tpwodloffline.ReportPrinters.PrintReportAnalogicImpactNew;
import com.collection.tpwodloffline.adapter.DetailsReportAdapter;
import com.collection.tpwodloffline.model.DetailsReport;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OldDetailsReportActivity extends AppCompatActivity {

    private RecyclerView rv_details_recycler;
    private Context context;
    private Cursor rs = null;
    private DatabaseAccess databaseAccess = null;
    private int sbmflg = 0;
    private String Usernm = "";
    private Intent reports;

    private String date = "";
    private String month = "";
    private String consumerAccount = "";
    private String amountReceived = "";
    private String mrNumber = "";
    private String receiptDate = "";
    private DetailsReportAdapter detailsReportAdapter;
    private TextView tv_print;

    private ArrayList<DetailsReport> detailsReportsList = new ArrayList<>();
    private ArrayList<DetailsReport> originalData = new ArrayList<>();


    String BillContents = "";
    String doubleHeight = "";
    String widthoff = "";
    String ReportTyp = "";
    private String CustID = "";
    private LinearLayout ll_amount_received;
    private TextView tv_total_received;
    private String totalCollection = "";
    private TextView tv_report_title;
    private String screenName = "";
    private TextView tv_no_data_found;
    private ImageView iv_back;
    private ImageView iv_search;
    private EditText et_search;
    private String consumerName = "";
    private ImageView iv_close;
    private TextView date_topay;
    private Button view_record;
    public DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_details_report);

        context = this;
        intView();
        getIntentData();
        clickListener();
        initAdapter();

    }

    private void fetchData(String dateChosen) {

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm = sessiondata.getString("userID", null);
        //to get SBM print
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";


        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);
        }
        rs.close();
        databaseAccess.close();

        try {

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();

            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();


            Cursor rs = null;

            if (ReportTyp.equals("D")) {


                BillContents = "";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += doubleHeight + "   DETAIL REPORT" + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += "\n";
                BillContents += widthoff + "DATE:" + dateFormat.format(cal.getTime()) + "\n";
                BillContents += widthoff + "....................." + "\n";
                this.date = dateFormat.format(cal.getTime());


                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";


                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents += widthoff + "COLL MONTH:" + rs.getString(1) + "\n";
                    BillContents += widthoff + "TOTAL CONSUMER:" + rs.getString(0) + "\n";
                    BillContents += widthoff + "....................." + "\n";
                    BillContents += widthoff + "....................." + "\n";

                    month = rs.getString(1);

                    System.out.println("details22==" + BillContents);
                }
                rs.close();

                String paymode = "";
                int pay;

                strUpdateSQL_01 = "select Cons_Acc,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),CON_NAME,PAY_MODE from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents += widthoff + "CONS_ACC" + "  " + "AMT RECVD" + "\n";
                BillContents += widthoff + "MR_NO" + "  " + "RECPT DT" + "\n";
                BillContents += widthoff + "PAYMENT MODE" + " " + "CON_NAME" + "\n";

                BillContents += widthoff + "....................." + "\n";


                while (rs.moveToNext()) {
                    BillContents += widthoff + rs.getString(0) + " " + rs.getString(1) + "\n";
                    BillContents += widthoff + rs.getString(2) + " " + rs.getString(3) + "\n";
                    if (rs.getInt(5) == 2) {
                        paymode = "DD";
                    } else if (rs.getInt(5) == 3) {
                        paymode = "CHEQUE";
                    } else if (rs.getInt(5) == 7) {
                        paymode = "POS";
                    } else {
                        paymode = "CASH";
                    }

                    BillContents += widthoff + paymode + " " + rs.getString(4) + "\n";
                    BillContents += widthoff + "....................." + "\n";


                    consumerAccount = rs.getString(0);
                    amountReceived = rs.getString(1);
                    mrNumber = rs.getString(2);
                    receiptDate = rs.getString(3);
                    consumerName = widthoff + rs.getString(4);

                    DetailsReport detailsReport = new DetailsReport(this.date, month, consumerAccount, amountReceived, mrNumber, receiptDate, "D", consumerName);

                    detailsReportsList.add(detailsReport);
                    originalData.addAll(detailsReportsList);

                }

            } else if (ReportTyp.equals("S"))
            {
                String paymode;
                BillContents = "";
                BillContents += widthoff + "....................." + "\n";
                BillContents += doubleHeight + "    SUMMARY REPORT" + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += "\n";
                BillContents += widthoff + "DATE:" + dateFormat.format(cal.getTime()) + "\n";
                BillContents += widthoff + "....................." + "\n";

                this.date = dateFormat.format(cal.getTime());
                System.out.println("details==" + BillContents);

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);


                while (rs.moveToNext()) {
                    BillContents += widthoff + "COLL MONTH:" + rs.getString(1) + "\n";
                    BillContents += widthoff + "TOTAL MR:" + rs.getString(0) + "\n";
                    BillContents += widthoff + "TOTAL RECVD:" + rs.getString(2) + "\n";


                    month = rs.getString(1);
                    totalCollection = rs.getString(2);

                    System.out.println("details22==" + BillContents);

                }
                tv_total_received.setText(totalCollection);
                rs.close();
                BillContents += widthoff + "....................." + "\n";


                strUpdateSQL_01 = "select Cons_Acc,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),CON_NAME, PAY_MODE from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents += widthoff + "CONS_ACC" + "  " + "AMT RECVD" + "\n";
                BillContents += widthoff + "MR_NO" + "  " + "RECPT DT" + "\n";
                BillContents += widthoff + "PAYMENT MODE" + " " + "CON_NAME" + "\n";

                BillContents += widthoff + "....................." + "\n";


                while (rs.moveToNext()) {
                    /*   BillContents += widthoff+String.format("%-7s%8s%9s", rs.getString(0), " "+rs.getString(1)," "+rs.getString(2));*/

                    BillContents += widthoff + rs.getString(0) + " " + rs.getString(1) + "\n";
                    BillContents += widthoff + rs.getString(2) + " " + rs.getString(3) + "\n";
                    if (rs.getInt(5) == 2) {
                        paymode = "DD";
                    } else if (rs.getInt(5) == 3) {
                        paymode = "CHEQUE";
                    } else if (rs.getInt(5) == 7) {
                        paymode = "POS";
                    } else {
                        paymode = "CASH";
                    }

                    BillContents += widthoff + paymode + " " + rs.getString(4) + "\n";


                    BillContents += widthoff + "....................." + "\n";

                    consumerAccount = rs.getString(0);
                    amountReceived = rs.getString(1);
                    mrNumber = rs.getString(2);
                    receiptDate = rs.getString(3);
                    consumerName = rs.getString(4);


                    DetailsReport detailsReport = new DetailsReport(this.date, month, consumerAccount, amountReceived, mrNumber, receiptDate, "S", consumerName);

                    detailsReportsList.add(detailsReport);
                    originalData.addAll(detailsReportsList);

                }
                rs.close();

            } else if (ReportTyp.equals("U"))
            {
                ////This is used
                BillContents = "";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += doubleHeight + "   DAILY REPORT" + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += "\n";
                BillContents += widthoff + "DATE:" + dateFormat.format(cal.getTime()) + "\n";
                BillContents += widthoff + "....................." + "\n";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data_bkp where recpt_flg=1 and '" + dateChosen + "' =strftime('%d-%m-%Y', recpt_date)";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);

                System.out.println("details22==" + BillContents);

                while (rs.moveToNext()) {
                    BillContents += widthoff + "COLL MONTH:" + rs.getString(1) + "\n";
                    BillContents += widthoff + "TOTAL MR:" + rs.getString(0) + "\n";
                    BillContents += widthoff + "TOTAL RECVD:" + rs.getString(2) + "\n";

                    totalCollection = rs.getString(2);


                    System.out.println("details22==" + BillContents);
                }

                tv_total_received.setText(totalCollection);
                rs.close();
                BillContents += widthoff + "....................." + "\n";
                strUpdateSQL_01 = "select Cons_Acc,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),CON_NAME,PAY_MODE  from coll_sbm_data_bkp where recpt_flg=1 and '" + dateChosen + "' =strftime('%d-%m-%Y', recpt_date)";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents += widthoff + "CONS_ACC" + "  " + "AMT RECVD" + "\n";
                BillContents += widthoff + "MR_NO" + "  " + "RECPT DT" + "\n";
                BillContents += widthoff + "PAYMENT MODE" + " " + "CON_NAME" + "\n";

                BillContents += widthoff + "....................." + "\n";
                String paymode = "";

                while (rs.moveToNext()) {
                    BillContents += widthoff + rs.getString(0) + " " + rs.getString(1) + "\n";
                    BillContents += widthoff + rs.getString(2) + " " + rs.getString(3) + "\n";

                    if (rs.getInt(5) == 2) {
                        paymode = "DD";
                    } else if (rs.getInt(5) == 3) {
                        paymode = "CHEQUE";
                    } else if (rs.getInt(5) == 7) {
                        paymode = "POS";
                    } else {
                        paymode = "CASH";
                    }

                    BillContents += widthoff + paymode + " " + rs.getString(4) + "\n";

                    BillContents += widthoff + "....................." + "\n";

                    consumerAccount = rs.getString(0);
                    amountReceived = rs.getString(1);
                    mrNumber = rs.getString(2);
                    receiptDate = rs.getString(3);
                    consumerName = rs.getString(4);

                    DetailsReport detailsReport = new DetailsReport(this.date, month, consumerAccount, amountReceived, mrNumber, receiptDate, "U", consumerName);

                    detailsReportsList.add(detailsReport);
                    originalData.addAll(detailsReportsList);

                }


                rs.close();
            } else if (ReportTyp.equals("C"))
            {


                BillContents = "";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += doubleHeight + "   CONSUMER REPORT" + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += widthoff + "....................." + "\n";
                BillContents += "\n";
                BillContents += widthoff + "DATE:" + dateFormat.format(cal.getTime()) + "\n";
                BillContents += widthoff + "....................." + "\n";

                this.date = dateFormat.format(cal.getTime());
                strUpdateSQL_01 = "";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents += widthoff + "COLL MONTH:" + rs.getString(1) + "\n";
                    BillContents += widthoff + "TOTAL MR:" + rs.getString(0) + "\n";
                    BillContents += widthoff + "TOTAL RECVD:" + rs.getString(2) + "\n";

                    totalCollection = rs.getString(2);
                }

                tv_total_received.setText(totalCollection);
                rs.close();
                BillContents += widthoff + "....................." + "\n";
                strUpdateSQL_01 = "";
                strUpdateSQL_01 = "select Cons_Acc,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),CON_NAME,PAY_MODE  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents += widthoff + "CONS_ACC" + "  " + "AMT RECVD" + "\n";
                BillContents += widthoff + "MR_NO" + "  " + "RECPT DT" + "\n";
                BillContents += widthoff + "PAYMENT MODE" + " " + "CON_NAME" + "\n";

                BillContents += widthoff + "....................." + "\n";

                while (rs.moveToNext()) {
                    BillContents += widthoff + rs.getString(0) + " " + rs.getString(1) + "\n";
                    BillContents += widthoff + rs.getString(2) + " " + rs.getString(3) + "\n";
                    String paymode = "";
                    if (rs.getInt(5) == 2) {
                        paymode = "DD";
                    } else if (rs.getInt(5) == 3) {
                        paymode = "CHEQUE";
                    } else if (rs.getInt(5) == 7) {
                        paymode = "POS";
                    } else {
                        paymode = "CASH";
                    }

                    BillContents += widthoff + paymode + " " + rs.getString(4) + "\n";

                    BillContents += widthoff + "....................." + "\n";

                    consumerAccount = rs.getString(0);
                    amountReceived = rs.getString(1);
                    mrNumber = rs.getString(2);
                    receiptDate = rs.getString(3);
                    consumerName = rs.getString(4);


                    DetailsReport detailsReport = new DetailsReport(this.date, month, consumerAccount, amountReceived, mrNumber, receiptDate, "C", consumerName);

                    detailsReportsList.add(detailsReport);
                    originalData.addAll(detailsReportsList);

                }
                rs.close();
            }
            databaseAccess.close();


            if (detailsReportsList.size() >= 1) {

                if ((ReportTyp.equalsIgnoreCase("S")) || (ReportTyp.equalsIgnoreCase("U")) || (ReportTyp.equalsIgnoreCase("C"))) {
                    ll_amount_received.setVisibility(View.VISIBLE);
                } else {
                    ll_amount_received.setVisibility(View.GONE);
                }

                tv_no_data_found.setVisibility(View.GONE);
                rv_details_recycler.setVisibility(View.VISIBLE);
            } else {
                tv_no_data_found.setVisibility(View.VISIBLE);
                rv_details_recycler.setVisibility(View.GONE);
                ll_amount_received.setVisibility(View.GONE);
            }


            detailsReportAdapter.notifyDataSetChanged();


            //  sendData();
        } catch (Exception ex) {
            //  Toast.makeText(ReportPrint.this, "message13", Toast.LENGTH_LONG).show();
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        ReportTyp = intent.getStringExtra("ReportTyp");
        CustID = intent.getStringExtra("CustID");
        screenName = intent.getStringExtra("screenName");

        tv_report_title.setText(R.string.old_reports);
    }

    private void clickListener() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        date_topay.setOnClickListener(v-> {

            final Calendar cldr = Calendar.getInstance();
            cldr.add(Calendar.DAY_OF_MONTH, -1);
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DecimalFormat mFormat = new DecimalFormat("00");

            // date picker dialog
            datePickerDialog = new DatePickerDialog(OldDetailsReportActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> date_topay.setText(mFormat.format(dayOfMonth) + "-" +mFormat.format(monthOfYear + 1) + "-" + year1), year, month, day);

            // set maximum date to be selected as today
            //           picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
            // set minimum date to be selected as today
            //datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(cldr.getTimeInMillis());

            datePickerDialog.show();
        });

        view_record.setOnClickListener(v->{

            String date = date_topay.getText().toString();
            if(date.equals("")){
                Toast.makeText(OldDetailsReportActivity.this, "Select date to view record", Toast.LENGTH_SHORT).show();
            }else {
                detailsReportsList.clear();
                fetchData(date);
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_search.setText("");

                iv_close.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
                detailsReportAdapter.filterList(originalData, "");

            }
        });


        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    iv_close.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);

                    iv_search.performClick();

                    handled = true;
                }
                return handled;
            }
        });


        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_search.getText().toString().trim().length() > 0) {

                    iv_close.setVisibility(View.VISIBLE);
                    iv_search.setVisibility(View.GONE);

                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    searchResult(et_search.getText().toString().trim());

                } else {
                    Toast.makeText(OldDetailsReportActivity.this, "Please enter consumer no. or consumer name", Toast.LENGTH_SHORT).show();
                }

            }
        });


        tv_print.setOnClickListener(v -> {
            String Cdate = date_topay.getText().toString();
            if(Cdate.equals("")){
                Toast.makeText(OldDetailsReportActivity.this, "Select date to view record", Toast.LENGTH_SHORT).show();
            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OldDetailsReportActivity.this);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Please select report type")
                        .setCancelable(false)
                        .setPositiveButton("Summarised", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                // thermal 8,2
                                // impact 5,6
                                if (sbmflg == 2)
                                {
                                    reports = new Intent(getApplicationContext(), PrintReportAmigoThermalNew.class);///Thermal
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 5)
                                {
                                    reports = new Intent(getApplicationContext(), PrintReportAnalogicImpactNew.class);///impact
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 6)
                                {
                                    reports = new Intent(getApplicationContext(), PrintReportAnalogicImpactNew.class);///impact
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 8)
                                {
                                    reports = new Intent(getApplicationContext(), PrintReportAmigoThermalNew.class);///Thermal
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else
                                {
                                    Toast.makeText(context, "Printer not configured", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Detailed", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                // thermal 8,2
                                // impact 5,6
                                if (sbmflg == 2)
                                {
                                    reports = new Intent(getApplicationContext(), PrintDetailedReportAmigoThermalNew.class);///Thermal
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 5)
                                {
                                    reports = new Intent(getApplicationContext(), PrintDetailedReportAnalogicImpactNew.class);///impact
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 6)
                                {
                                    reports = new Intent(getApplicationContext(), PrintDetailedReportAnalogicImpactNew.class);///impact
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else if (sbmflg == 8)
                                {
                                    reports = new Intent(getApplicationContext(), PrintDetailedReportAmigoThermalNew.class);///Thermal
                                    Bundle Report = new Bundle();
                                    Report.putString("ReportTyp", ReportTyp);
                                    Report.putString("CustID", CustID);
                                    Report.putString("type", "old");
                                    Report.putString("cdate", Cdate);
                                    reports.putExtras(Report);
                                    startActivity(reports);
                                } else
                                {
                                    Toast.makeText(context, "Printer not configured", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNeutralButton("Close", (dialog, which) -> {
                            dialog.dismiss();
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }


        });
    }

    void searchResult(String text) {
        ArrayList<DetailsReport> temp = new ArrayList<>();
        for (DetailsReport d : detailsReportsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if ((d.getConsumerAccount().contains(text)) || ((d.getConsumerName().contains(text)))) {
                temp.add(d);
            }
        }


        if (detailsReportsList.size() >= 1) {
            tv_no_data_found.setVisibility(View.GONE);
            rv_details_recycler.setVisibility(View.VISIBLE);

            detailsReportAdapter.filterList(temp, et_search.getText().toString().trim());

        } else {
            tv_no_data_found.setVisibility(View.VISIBLE);
            rv_details_recycler.setVisibility(View.GONE);
        }

    }

    private void initAdapter() {
        rv_details_recycler.setLayoutManager(new LinearLayoutManager(context));

        detailsReportAdapter = new DetailsReportAdapter(context, detailsReportsList);
        rv_details_recycler.setAdapter(detailsReportAdapter);
    }

    private void intView() {
        rv_details_recycler = findViewById(R.id.rv_details_recycler);
        tv_print = findViewById(R.id.tv_print);
        ll_amount_received = findViewById(R.id.ll_amount_received);
        tv_total_received = findViewById(R.id.tv_total_received);
        tv_report_title = findViewById(R.id.tv_report_title);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        iv_back = findViewById(R.id.iv_back);
        iv_search = findViewById(R.id.iv_search);
        et_search = findViewById(R.id.et_search);
        iv_close = findViewById(R.id.iv_close);
        date_topay = findViewById(R.id.date_topay);
        view_record = findViewById(R.id.view_record);

        detailsReportsList.clear();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
