package com.collection.tpwodloffline;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintEpsonThermal extends Activity implements  ReceiveListener {
    private static final int REQUEST_PERMISSION = 100;
    private Context mContext = null;
    public static Printer mPrinter = null;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private   DatabaseAccess databaseAccess=null;
    // TextView myLabel;
    static TextView strPrntMsg;
    final Context context = this;
    private String AccNum="";
    private  String address = "";
    private String rcptType="";
    private String TransID="";
    private String ReportTyp="";
    private String mmDeviceAdr=null;
    private String devicename="nodevice";
    private String CustID="";
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_epson_thermal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        //strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        //strPrntMsg.setText("Printing");
        mmOutputStream=null;
        mmInputStream=null;
        mmDevice=null;
        mBluetoothAdapter=null;
        ReportTyp="";
        String dubl="";
        String accnumber="";

        try {
            Bundle Report = getIntent().getExtras();
            ReportTyp = Report.getString("ReportTyp");
            CustID = Report.getString("CustID");
            Log.d("DemoApp", "ReportTyp   " + ReportTyp);
        }catch(Exception e){
            //  Toast.makeText(ReportPrintActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
        //    Toast.makeText(ReportPrintActivity.this, "message888   " + ReportTyp, Toast.LENGTH_LONG).show();
        Log.d("DemoApp", "devicename" + devicename);
        runPrintReceiptSequence();
    }
    private void runPrintReceiptSequence() {

        initializeObject();
        createReceiptData();
        printData();

    }
    private boolean createReceiptData() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {

            int monthname=0;
            String doubleHeight = "";
            String widthoff = "";
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();

            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_01 = "";
            Cursor rs = null;

            if (ReportTyp.equals("D")) {
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                textData.append("DETAIL REPORT ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("DATE:" + dateFormat.format(cal.getTime()) + "\n");
                textData.append("----------------------------------------" + "\n");

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {

                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1("TOTAL CONSUMER:" , rs.getString(0), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("CONS_ACC" , "  " + "AMT RECVD", 36) + "\n");
                textData.append(leftAppend1("MR_NO", "  " + "RECPT DT", 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(2) , " " + rs.getString(3)+" " + rs.getString(4), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }
            else if (ReportTyp.equalsIgnoreCase("DN"))
            {
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                textData.append("DETAIL REPORT NON-ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("DATE:" + dateFormat.format(cal.getTime()) + "\n");
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {

                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1("TOTAL CONSUMER:" , rs.getString(0), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("CONS_ACC" , "  " + "AMT RECVD", 36) + "\n");
                textData.append(leftAppend1("MR_NO", "  " + "RECPT DT", 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(2) , " " + rs.getString(3)+" " + rs.getString(4), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }

            else if (ReportTyp.equals("S")) {
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("SUMMARY REPORT ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");

                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1) , 36) + "\n");
                    textData.append(leftAppend1("TOTAL MR:" , rs.getString(0) , 36) + "\n");
                    textData.append(leftAppend1("TOTAL RECVD:" , rs.getString(2) , 36) + "\n");
                }
                rs.close();
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append("TOT_MR " + "RCPT_DT " + "AMT_RECVD"  + "\n");
                textData.append("......" + " " + "........" + " " + "......." + "\n");
                while (rs.moveToNext()) {
                    textData.append( rs.getString(0)+ " " + rs.getString(1)+ " " + rs.getString(2)+" " + rs.getString(3));
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }
            else if (ReportTyp.equalsIgnoreCase("SN")){
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("SUMMARY REPORT NON-ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");

                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1) , 36) + "\n");
                    textData.append(leftAppend1("TOTAL MR:" , rs.getString(0) , 36) + "\n");
                    textData.append(leftAppend1("TOTAL RECVD:" , rs.getString(2) , 36) + "\n");
                }
                rs.close();
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append("TOT_MR " + "RCPT_DT " + "AMT_RECVD"  + "\n");
                textData.append("......" + " " + "........" + " " + "......." + "\n");
                while (rs.moveToNext()) {
                    textData.append( rs.getString(0)+ " " + rs.getString(1)+ " " + rs.getString(2)+" " + rs.getString(3));
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }

            else if (ReportTyp.equals("U")) {
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("DAILY REPORT ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1("TOTAL MR:" , rs.getString(0), 36) + "\n");
                    textData.append(leftAppend1("TOTAL RECVD:" , rs.getString(2), 36) + "\n");
                }
                rs.close();
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("CONS_ACC" , "  " + "AMT RECVD" , 36) + "\n");
                textData.append(leftAppend1("MR_NO" , "  " + "RECPT DT", 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(2) , " " + rs.getString(3)+" " + rs.getString(4), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }

            else if (ReportTyp.equalsIgnoreCase("UN")){
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("DAILY REPORT NON-ENERGY" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1("TOTAL MR:" , rs.getString(0), 36) + "\n");
                    textData.append(leftAppend1("TOTAL RECVD:" , rs.getString(2), 36) + "\n");
                }
                rs.close();
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("CONS_ACC" , "  " + "AMT RECVD" , 36) + "\n");
                textData.append(leftAppend1("MR_NO" , "  " + "RECPT DT", 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(2) , " " + rs.getString(3)+" " + rs.getString(4), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }


            else if (ReportTyp.equals("C")) {

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("CONSUMER REPORT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append(leftAppend1("COLL MONTH:" , rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1("TOTAL MR:" , rs.getString(0) , 36) + "\n");
                    textData.append(leftAppend1("TOTAL RECVD:" , rs.getString(2) , 36) + "\n");
                }
                rs.close();
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "";
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("CONS_ACC:" , "  " + "AMT RECVD", 36) + "\n");
                textData.append(leftAppend1("MR_NO:" , "  " + "RECPT DT" , 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(1), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(2) , " " + rs.getString(3)+" " + rs.getString(4), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }else  if(ReportTyp.equals("N")){

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("NON RECEIPT REPORT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("DATE:" , dateFormat.format(cal.getTime()), 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "";
                strUpdateSQL_01 = "select trans_id,tot_paid,cust_id,case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                textData.append(leftAppend1("TRANS ID:" , "  " + "CUST ID", 36) + "\n");
                textData.append(leftAppend1("AMOUNT:" , "  " + "" , 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                while (rs.moveToNext()) {
                    textData.append(leftAppend1(rs.getString(0) , " " + rs.getString(2), 36) + "\n");
                    textData.append(leftAppend1(rs.getString(1) , " " + rs.getString(3), 36) + "\n");
                    textData.append("----------------------------------------" + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }
            databaseAccess.close();

            Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
            reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(reports);
            finish();

        }catch (Exception e) {
            //  ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;
        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        //    dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            //ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            //  ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_M10, Printer.MODEL_ANK, this);
        } catch (Exception e) {
            //  ShowMsg.showException(e, "Printer", mContext);
            return false;
        }
        mPrinter.setReceiveEventListener(this);
        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);

        if (mPrinter == null) {
            return false;
        }

        try {//BT:00:01:90:C2:DB:00
            mPrinter.connect("BT:" + address, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            //ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            // ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    //  ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    //    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                //   ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                //  dispPrinterWarnings(status);

                //   updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }
    String findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //  myLabel.setText("No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // MP300 is the name of the bluetooth printer device
                    mmDevice = device;
                    mmDeviceAdr=device.getAddress();
                    Log.d("DemoApp", "mmDeviceAdr  " + mmDeviceAdr);
                }
            }
            // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 5  " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 6  " + e);
            e.printStackTrace();
        }
        return mmDeviceAdr;
    }

    //DATE CONVERSION
    public static String convertDateFormat(String strTokenValue, String strDataFormat)
    {
        String strTokenValueRevDt = "";
        String strTokenValueOrgDt = strTokenValue;
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth =strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");
        int idxEYear = strDataFormat.lastIndexOf("Y");
        int idxSHour = strDataFormat.indexOf("HH");

        try{
            strTokenValueRevDt = strTokenValueOrgDt.substring(idxSDate, idxSDate+2)+ "-" +
                    strTokenValueOrgDt.substring(idxSMonth, idxSMonth+2) + "-" +
                    strTokenValueOrgDt.substring(idxSYear+2, idxSYear+4);

        }
        catch (Exception e)
        {
            strTokenValueRevDt = "01-01-99";
            Log.d("DemoApp", "e   " + e);
        }
        return strTokenValueRevDt;
    }



    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }
    public static String leftAppend1(String str,String str1,int maxlen){
        String retStr="";
        int strlen=0;
        strlen=str.length()+str1.length();
        for(int i=0;i<(maxlen-strlen);i++){
            retStr+=" ";
        }
        str="  "+str+retStr+str1;
        return str;

    }
    public static String leftAppend2(String str0,String str,int leftlen,String Str1,int maxlen){
        String retStr="";
        for(int i=0;i<leftlen-str.length();i++){
            retStr+=" ";
        }
        str=str+retStr;
        str0=str0+str;
        retStr="";
        for(int i=0;i<(maxlen-(str0.length()+Str1.length()));i++){
            retStr+=" ";
        }
        Str1=retStr+Str1;
        str0="  "+str0+Str1;
        return str0;

    }
    public static String leftAppend3(String str0,String str,int rlen,String Str1,int Rlen1,String Str2,int maxlen){

        String retStr="";
        for(int i=0;i<(rlen-str.length());i++){
            retStr+=" ";
        }
        str=str+retStr;
        str0=str0+str;
        retStr="";
        for(int i=0;i<(Rlen1-Str1.length());i++){
            retStr+=" ";
        }
        Str1=Str1+retStr;
        str0=str0+Str1;

        for(int i=0;i<(maxlen-(Str2.length()+str0.length()));i++){
            retStr+=" ";
        }
        Str2=retStr+Str2;
        str0="  "+str0+Str2;
        return str0;
    }
    @Override
    public void onBackPressed() {
        Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
        reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reports);
        finish();
    }
}

