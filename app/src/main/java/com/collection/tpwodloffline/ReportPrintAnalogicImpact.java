package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintAnalogicImpact extends AppCompatActivity {
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
    private Printer printer;
    private  String address = "";
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_analogic_impact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Log.d("DemoApp", "devicename   " + devicename);

        printer=new Printer2inch();
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        try{
            sendData();
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
        }
    }
    final Context context = this;
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

    void sendData() throws IOException {

        try {

            int monthname=0;
            String BillContents = "";
            String Doublewidth = "";
            byte cmd = (byte) 0x0A; //softland
            String prevCmd = ""; //softland
            String endstr = "";
            //  String filldata="";
            Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
            String doubleHeight = BPImpact.font_Double_Height_On();
            String lnfeed = BPImpact.line_Feed();
            String widthon = BPImpact.font_Double_Height_Width_On();
            String widthoff = BPImpact.font_Double_Height_Width_Off();
            String Billformat="PrePrinted";
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_01="";
            Cursor rs=null;

            if(ReportTyp.equals("D")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   DETAIL REPORT ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"CONS_ACC"+"  "+"AMT RECVD"+"\n";
                BillContents+=widthoff+"MR_NO"+"  "+"RECPT DT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(1)+"\n";
                    BillContents+=widthoff+rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }
            else if (ReportTyp.equalsIgnoreCase("DN")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   DETAIL REPORT NON-ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND  OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);

                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }

                rs.close();
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND  OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' order by recpt_date";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"CONS_ACC"+"  "+"AMT RECVD"+"\n";
                BillContents+=widthoff+"MR_NO"+"  "+"RECPT DT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(1)+"\n";
                    BillContents+=widthoff+rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }

            else if(ReportTyp.equals("S")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"    SUMMARY REPORT ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL MR:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"TOTAL RECVD:"+rs.getString(2)+"\n";
                }
                rs.close();
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"TOT_MR "+"RCPT_DT "+"AMT_RECVD";
                BillContents+=widthoff+"......"+" "+"........"+" "+"......."+"\n";
                while (rs.moveToNext()) {
                    BillContents += widthoff+String.format("%-7s%8s%9s", rs.getString(0), " "+rs.getString(1)," "+rs.getString(2)+" " + rs.getString(3));
                }
                rs.close();

            }
            else if (ReportTyp.equalsIgnoreCase("SN")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"    SUMMARY REPORT NON-ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";

                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL MR:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"TOTAL RECVD:"+rs.getString(2)+"\n";
                }
                rs.close();
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"TOT_MR "+"RCPT_DT "+"AMT_RECVD";
                BillContents+=widthoff+"......"+" "+"........"+" "+"......."+"\n";
                while (rs.moveToNext()) {
                    BillContents += widthoff+String.format("%-7s%8s%9s", rs.getString(0), " "+rs.getString(1)," "+rs.getString(2)+" " + rs.getString(3));
                }
                rs.close();
            }

            else if(ReportTyp.equals("U")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   DAILY REPORT ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL MR:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"TOTAL RECVD:"+rs.getString(2)+"\n";
                }
                rs.close();
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"CONS_ACC"+"  "+"AMT RECVD"+"\n";
                BillContents+=widthoff+"MR_NO"+"  "+"RECPT DT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(1)+"\n";
                    BillContents+=widthoff+rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }
            else if (ReportTyp.equalsIgnoreCase("UN")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   DAILY REPORT NON-ENERGY"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL MR:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"TOTAL RECVD:"+rs.getString(2)+"\n";
                }
                rs.close();
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"CONS_ACC"+"  "+"AMT RECVD"+"\n";
                BillContents+=widthoff+"MR_NO"+"  "+"RECPT DT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(1)+"\n";
                    BillContents+=widthoff+rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }

            else if(ReportTyp.equals("C")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   CONSUMER REPORT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"COLL MONTH:"+rs.getString(1)+"\n";
                    BillContents+=widthoff+"TOTAL MR:"+rs.getString(0)+"\n";
                    BillContents+=widthoff+"TOTAL RECVD:"+rs.getString(2)+"\n";
                }
                rs.close();
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"CONS_ACC"+"  "+"AMT RECVD"+"\n";
                BillContents+=widthoff+"MR_NO"+"  "+"RECPT DT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(1)+"\n";
                    BillContents+=widthoff+rs.getString(2)+" "+rs.getString(3)+" " + rs.getString(4)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }else  if(ReportTyp.equals("N")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   NON RECPT. REPORT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 ="";
                strUpdateSQL_01 = "select trans_id,tot_paid,cust_id,case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                BillContents+=widthoff+"TRANS ID"+"  "+"CUST ID"+"\n";
                BillContents+=widthoff+"AMONT"+"  "+""+"\n";
                BillContents+=widthoff+"....................."+"\n";
                while (rs.moveToNext()) {
                    BillContents+=widthoff+rs.getString(0)+" "+rs.getString(2)+"\n";
                    BillContents+=widthoff+rs.getString(1)+" "+rs.getString(3)+"\n";
                    BillContents+=widthoff+"....................."+"\n";
                }
                rs.close();
            }
            databaseAccess.close();
            BillContents+="\n\n\n\n\n\n";
            AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();
            print.openBT(mmDevice.getAddress());
            print.printData(BillContents);
            print.closeBT();
            //     Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
            //    startActivity(reports);
            //    finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintAnalogicImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

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