package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.aem.api.AEMPrinter;
import com.aem.api.AEMScrybeDevice;
import com.aem.api.IAemScrybe;
import com.epson.epos2.printer.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintPhiThermal extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 100;
    private Context mContext = null;
    public static Printer mPrinter = null;
    // TextView myLabel;
    private String ReportTyp="";
    private String CustID="";
    private AEMScrybeDevice m_Aem;
    private String BlutoothPrinter;
    private  boolean connectPrinterBool;
    private AEMPrinter aemPrinter;
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
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private  String address = "";
    private String rcptType="";
    private String TransID="";
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_phi_thermal);
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
        Log.d("DemoApp", "devicename   " + devicename);
        m_Aem = new AEMScrybeDevice(new IAemScrybe() {
            @Override
            public void onDiscoveryComplete(ArrayList<String> arrayList) {


            }
        });
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        try{
            sendData();
        }catch (Exception e){}

    }
    void sendData() throws IOException {

        try {
            BlutoothPrinter = address;
            try {
                connectPrinterBool = m_Aem.connectToPrinter(BlutoothPrinter);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "catch error", Toast.LENGTH_SHORT).show();
            }
            if (connectPrinterBool) {
                aemPrinter = m_Aem.getAemPrinter();
                Toast.makeText(getApplicationContext(), "Printer connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), " error while connecting", Toast.LENGTH_SHORT).show();
            }


            String billprint = "";
            try {
                int monthname=0;


                String doubleHeight = "";
                String widthoff = "";



                String Billformat="PrePrinted";

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strUpdateSQL_01="";
                Cursor rs=null;

                if(ReportTyp.equals("D")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "DETAIL REPORT ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += leftAppend1("DATE:",dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint += "--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL CONSUMER:", rs.getString(0), 32)+"\n";
                        billprint += "--------------------"+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') order by recpt_date";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += leftAppend1("CONS_ACC", "  " + "AMT RECVD", 32)+"\n";
                    billprint += leftAppend1("MR_NO", "  " + "RECPT DT", 32)+"\n";
                    billprint += "--------------------"+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1(rs.getString(0), " " + rs.getString(1), 32)+"\n";
                        billprint += leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 32)+"\n";
                        billprint += "--------------------"+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }


                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";

                    rs.close();
                }
                else if (ReportTyp.equalsIgnoreCase("DN")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "DETAIL REPORT NON-ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += leftAppend1("DATE:",dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint += "--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL CONSUMER:", rs.getString(0), 32)+"\n";
                        billprint += "--------------------"+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' order by recpt_date";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += leftAppend1("CONS_ACC", "  " + "AMT RECVD", 32)+"\n";
                    billprint += leftAppend1("MR_NO", "  " + "RECPT DT", 32)+"\n";
                    billprint += "--------------------"+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1(rs.getString(0), " " + rs.getString(1), 32)+"\n";
                        billprint += leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 32)+"\n";
                        billprint += "--------------------"+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }


                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";

                    rs.close();
                }
                else if(ReportTyp.equals("S")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "SUMMARY REPORT ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";

                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL MR:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("TOTAL RECVD:", rs.getString(2), 32)+"\n";
                    }
                    rs.close();
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += "TOT_MR "+"RCPT_DT "+"AMT_RECVD"+"\n";
                    billprint += "......"+" "+"........"+" "+"......."+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += String.format("%-1s%1s%1s", rs.getString(0), " " + rs.getString(1), " " + rs.getString(2)+" " + rs.getString(3))+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }
                    }
                    rs.close();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                }

                else if (ReportTyp.equalsIgnoreCase("SN")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "SUMMARY REPORT NON-ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";

                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL MR:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("TOTAL RECVD:", rs.getString(2), 32)+"\n";
                    }
                    rs.close();
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += "TOT_MR "+"RCPT_DT "+"AMT_RECVD"+"\n";
                    billprint += "......"+" "+"........"+" "+"......."+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += String.format("%-1s%1s%1s", rs.getString(0), " " + rs.getString(1), " " + rs.getString(2)+" " + rs.getString(3))+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }
                    }
                    rs.close();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                }


                else if(ReportTyp.equals("U")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "DAILY REPORT ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL MR:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("TOTAL RECVD:", rs.getString(2), 32)+"\n";
                    }
                    rs.close();
                    billprint +="--------------------"+"\n";;
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += leftAppend1("CONS_ACC", "  " + "AMT RECVD", 32)+"\n";
                    billprint += leftAppend1("MR_NO", "  " + "RECPT DT", 32)+"\n";
                    billprint +="--------------------"+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1(widthoff + rs.getString(0), " " + rs.getString(1), 32)+"\n";
                        billprint += leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 32)+"\n";
                        billprint +="--------------------"+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }
                    }
                    rs.close();
                }
                else if (ReportTyp.equalsIgnoreCase("UN")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "DAILY REPORT NON-ENERGY"+"\n";
                    billprint +="--------------------"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL MR:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("TOTAL RECVD:", rs.getString(2), 32)+"\n";
                    }
                    rs.close();
                    billprint +="--------------------"+"\n";;
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += leftAppend1("CONS_ACC", "  " + "AMT RECVD", 32)+"\n";
                    billprint += leftAppend1("MR_NO", "  " + "RECPT DT", 32)+"\n";
                    billprint +="--------------------"+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1(widthoff + rs.getString(0), " " + rs.getString(1), 32)+"\n";
                        billprint += leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 32)+"\n";
                        billprint +="--------------------"+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }
                    }
                    rs.close();
                }

                else if(ReportTyp.equals("C")){
                    billprint = "";
                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint += "CONSUMER REPORT"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint +="--------------------"+"\n";
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 ="";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("COLL MONTH:", rs.getString(1), 32)+"\n";
                        billprint += leftAppend1("TOTAL MR:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("TOTAL RECVD:", rs.getString(2), 32)+"\n";
                    }
                    rs.close();
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 ="";
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    billprint += leftAppend1("CONS_ACC", "  " + "AMT RECVD", 32)+"\n";
                    billprint += leftAppend1("MR_NO", "  " + "RECPT DT", 32)+"\n";
                    billprint +="--------------------"+"\n";
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1(rs.getString(0), " " + rs.getString(1), 32)+"\n";
                        billprint += leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 32)+"\n";
                        billprint +="--------------------"+"\n";
                        if((iCnt%5)==0){
                            aemPrinter.print(billprint);
                            aemPrinter.setFontNormal();
                            aemPrinter.setLeftAlign();
                            billprint = "";
                            Thread.sleep(300);
                        }
                    }
                    rs.close();
                }else  if(ReportTyp.equals("N")){
                    billprint = "";

                    aemPrinter.setFontNormal();
                    aemPrinter.setCenterAlign();
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint +="--------------------"+"\n";
                    billprint +="NON RECPT. REPORT"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint +="--------------------"+"\n";
                    billprint += leftAppend1("DATE:", dateFormat.format(cal.getTime()), 32)+"\n";
                    billprint +="--------------------"+"\n";
                    strUpdateSQL_01 = "select trans_id,tot_paid,cust_id,case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    //billprint += "CUSTOMER ID" + "TRANS.ID" + "AMT");
                    int iCnt=0;
                    while (rs.moveToNext()) {
                        billprint += leftAppend1("TRANS.ID:", rs.getString(0), 32)+"\n";
                        billprint += leftAppend1("CUSTOMER ID:", rs.getString(2), 32)+"\n";
                        billprint += leftAppend1("AMOUNT:", rs.getString(1)+" " + rs.getString(3), 32)+"\n";
                        //   billprint += leftAppend2(rs.getString(2), rs.getString(0), 7, rs.getString(1), 32);
                        billprint +="--------------------"+"\n";

                    }
                    rs.close();
                }
                databaseAccess.close();
                aemPrinter.print(billprint);
                aemPrinter.setFontNormal();
                aemPrinter.setLeftAlign();
                Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
                reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(reports);
                finish();
            }

            catch (Exception printerExceptions) {
                printerExceptions.printStackTrace();
                Log.e("innn", "createPrintData: "+printerExceptions.getMessage() );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

        } catch (Exception ex) {        }

        strPrntMsg.setText("Data Sent to Bluetooth Printer");

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
            Log.d("DemoApp","e   "+e);
        }
        return strTokenValueRevDt;
    }

    // Close the connection to bluetooth printer.


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
        str=str+retStr+str1;
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
        str0=str0+Str1;
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
        str0=str0+Str2;
        return str0;

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
                    mmDeviceAdr = device.getName();
                    // mmDeviceAdr=device.getAddress();
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

    @Override
    public void onBackPressed() {
        Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
        reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reports);
        finish();
    }
}
