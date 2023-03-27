package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.softland.printerlib.PrinterSection.CharaStyle;
import com.softland.printerlib.PrinterSection.ConnectionStatus;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;
import com.softland.printerlib.PrinterSection.iPrinter;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintSoftlandImpact extends AppCompatActivity {
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
    private String ReportTyp="";
    private String mmDeviceAdr=null;
    private String devicename="nodevice";
    private String CustID="";
    private  String address = "";
    private Printer printer;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_softland_impact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //    getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        Log.d("DemoApp", "devicename   " + devicename);
        printer=new Printer2inch();
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        connectTodevice(address);

    }

    final Context context = this;

    // This will find a bluetooth printer device
    // This will find a bluetooth printer device
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
            //    Toast.makeText(ReportPrintActivity.this, "message1", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(ReportPrintActivity.this, "message2", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return mmDeviceAdr;
    }
    void connectTodevice(String address)
    {
        printer.connect(iPrinter.ConnectionType.BT,address, new ConnectionStatus()
        {
            @Override
            public void onSucess()
            {
                Log.e("innn", "Already connected");
                Log.d("DemoApp", "rrrrr 10  ");
                sendData();
                printer.disonnected();
            }
            @Override
            public void onFailure(String s)
            {
                Log.d("DemoApp", "rrrrr 11 " );
                Log.e("innn", "Connection Status Failure: "+printer.getConnectionStatus() );
            }
        });
    }
    void sendData() {
        String st="";
        try {

            int monthname=0;
            String doubleHeight = "";
            String widthoff = "";
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_01="";
            Cursor rs=null;
            st=printer.reset();
            st+=printer.printNewLine();
            if(ReportTyp.equals("D")){
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("     DETAIL REPORT ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL CONSUMER:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("CONS_ACC"+"  "+"AMT RECVD" , 24));
                st+= printer.printLine(leftAppend1("MR_NO"+"  "+"RECPT DT" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(2)+" "+rs.getString(3) +" " + rs.getString(4), 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }
            else if (ReportTyp.equalsIgnoreCase("DN")){
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("     DETAIL REPORT NON-ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL CONSUMER:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("CONS_ACC"+"  "+"AMT RECVD" , 24));
                st+= printer.printLine(leftAppend1("MR_NO"+"  "+"RECPT DT" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(2)+" "+rs.getString(3) +" " + rs.getString(4), 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }
            else if(ReportTyp.equals("S")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("    SUMMARY REPORT ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL MR:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL RECVD:"+rs.getString(2) , 24));
                }
                rs.close();
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("TOT_MR "+"RCPT_DT "+"AMT_RECVD" , 24));
                st+= printer.printLine(leftAppend1("......"+" "+"........"+" "+"......." , 24));
                while (rs.moveToNext()) {
                    st +=  printer.printLine(leftAppend1(rs.getString(0)+ " "+rs.getString(1)+" "+rs.getString(2)+" " + rs.getString(3), 24));
                }
                rs.close();
            }
            else if (ReportTyp.equalsIgnoreCase("SN")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("    SUMMARY REPORT NON-ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL MR:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL RECVD:"+rs.getString(2) , 24));
                }
                rs.close();
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("TOT_MR "+"RCPT_DT "+"AMT_RECVD" , 24));
                st+= printer.printLine(leftAppend1("......"+" "+"........"+" "+"......." , 24));
                while (rs.moveToNext()) {
                    st +=  printer.printLine(leftAppend1(rs.getString(0)+ " "+rs.getString(1)+" "+rs.getString(2)+" " + rs.getString(3), 24));
                }
                rs.close();
            }

            else if(ReportTyp.equals("U")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("   DAILY REPORT ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL MR:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL RECVD:"+rs.getString(2) , 24));
                }
                rs.close();
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("CONS_ACC"+"  "+"AMT RECVD" , 24));
                st+= printer.printLine(leftAppend1("MR_NO"+"  "+"RECPT DT" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(2)+" "+rs.getString(3) +" " + rs.getString(4), 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }
            else if (ReportTyp.equalsIgnoreCase("UN")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("   DAILY REPORT NON-ENERGY" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL MR:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL RECVD:"+rs.getString(2) , 24));
                }
                rs.close();
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("CONS_ACC"+"  "+"AMT RECVD" , 24));
                st+= printer.printLine(leftAppend1("MR_NO"+"  "+"RECPT DT" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(2)+" "+rs.getString(3) +" " + rs.getString(4), 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }

            else if(ReportTyp.equals("C")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("   CONSUMER REPORT" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("COLL MONTH:"+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL MR:"+rs.getString(0) , 24));
                    st+= printer.printLine(leftAppend1("TOTAL RECVD:"+rs.getString(2) , 24));
                }
                rs.close();
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("CONS_ACC"+"  "+"AMT RECVD" , 24));
                st+= printer.printLine(leftAppend1("MR_NO"+"  "+"RECPT DT" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(1) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4) , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }else  if(ReportTyp.equals("N")){
                st = "";
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1("   NON RECEIPT REPORT" , 24));
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                st+=printer.printNewLine();
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()) , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select trans_id,tot_paid,cust_id,case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                st+= printer.printLine(leftAppend1("TRANS ID"+"  "+"CUST ID" , 24));
                st+= printer.printLine(leftAppend1("AMOUNT"+"  "+"" , 24));
                st+= printer.printLine(leftAppend1("....................." , 24));
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1(rs.getString(0)+" "+rs.getString(2) , 24));
                    st+= printer.printLine(leftAppend1(rs.getString(1)+" "+rs.getString(3) , 24));
                    st+= printer.printLine(leftAppend1("....................." , 24));
                }
                rs.close();
            }
            databaseAccess.close();

            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            printer.printText(st);
            // printer.disonnected();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
            // startActivity(reports);
            // finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintSoftlandImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

    }
    public static String leftAppend1(String str,int maxlen){
        String retStr="";
        for(int i=0;i<(maxlen-str.length());i++){
            retStr+=" ";
        }
        str=retStr+str;
        return str;
    }
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        System.runFinalization();
        //   System.run
        System.exit(0);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
        reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reports);
        finish();
    }

}
