package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.activity.AcCollection;
import com.qps.btgenie.BluetoothManager;
import com.qps.btgenie.QABTPAccessory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;


public class ReportPrintAmigoImpact extends AppCompatActivity {
    //AMIGOS
    BluetoothManager btpObject;
    public static final int DoubleWidth = 3,DoubleHght = 2,Normal = 1;

    boolean closeprinter = false;
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
    private String AccNum="";
    private  String address = "";
    private String rcptType="";
    private String TransID="";
    private String ReportTyp="";
    private String mmDeviceAdr=null;
    private String devicename="nodevice";
    private String CustID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_amigo_impact);
        btpObject= BluetoothManager.getInstance(this, new QABTPAccessory() {
            @Override
            public void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice) {
                Log.d("DemoApp", "devicename 1  ");
            }

            @Override
            public void onClientConnectionSuccess() {
                Log.d("DemoApp", "devicename 2  ");
                //Do Not start printing here
            }

            @Override
            public void onClientConnectionFail() {
                Log.d("DemoApp", "devicename 3  ");
            }

            @Override
            public void onClientConnecting() {
                Log.d("DemoApp", "devicename 4 ");
            }

            @Override
            public void onClientDisconnectSuccess() {
                Log.d("DemoApp", "devicename 5  ");
            }

            @Override
            public void onNoClientConnected() {
                Log.d("DemoApp", "devicename 6 ");

            }

            @Override
            public void onBluetoothStartDiscovery() {
                Log.d("DemoApp", "devicename 7  ");
            }

            @Override
            public void onBluetoothNotAvailable() {
                Log.d("DemoApp", "devicename 8  ");
            }

            @Override
            public void onBatterystatuscheck(String s) {
                if (closeprinter == true) {       //Added to close the printer
                    btpObject.closeConnection();
                    closeprinter = false;
                }
            }

            @Override
            public void onresponsefrmBluetoothdevice(String s) {

            }

            @Override
            public void onError(String s) {

            }

        });
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
        Log.d("DemoApp", "devicename  " + devicename);
        if(devicename.equals("nodevice")) {
            try {
                //     Log.d("DemoApp", "Entering findbt  " );
                address = findBT();
                Log.d("DemoApp", "BT found " );
            } catch (Exception ex) {

                //     Log.d("DemoApp", "Exception 1  " + ex);
            }
        }

        try{
            Log.d("DemoApp", "Entering open bt  " );
            if(!btpObject.isConnected() == true && address != null) {
                btpObject.createClient(address);

                Log.d("DemoApp", "BT opened ");
            }else{
                System.out.println("BT Closed!..");
            }

        } catch (Exception ex) {
            //     Log.d("DemoApp", "Exception 2  " + ex);

        }


        try{
            Log.d("DemoApp", "sending data  ");
            sendData();
            //    Log.d("DemoApp", "data sent ");
        } catch (Exception ex) {Log.d("DemoApp", "Exception 3 " + ex);
        }try{
            //workerThread.sleep(20000);
            // Thread.sleep(20000);
            closeBT();
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
        }
    }
    final Context context = this;
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
    void sendData() throws IOException {
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
            if (btpObject.isConnected() == true) {
                Log.d("DemoApp", "Printing Data ...  ");
                btpObject.printerfilter(btpObject.PRINTER_DEFAULT);
                if (ReportTyp.equals("D")) {
                    billprint = "";
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage_HW(CenterAppend1("--------------------", 24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("DETAIL REPORT ENERGY",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24) , Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------", 24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24), Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL CONSUMER:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    }
                    rs.close();
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT' when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') order by recpt_date";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("CONS_ACC", "  " + "AMT RECVD", 24),Normal);
                    btpObject.sendMessage_HW(leftAppend1("MR_NO", "  " + "RECPT DT", 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(0), " " + rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    }

                    btpObject.sendMessage("\n".getBytes());
                    rs.close();
                }
                else if (ReportTyp.equalsIgnoreCase("DN")){
                    billprint = "";
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage_HW(CenterAppend1("--------------------", 24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("DETAIL REPORT NON-ENERGY",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24) , Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------", 24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now')  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24), Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL CONSUMER:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    }
                    rs.close();
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT' when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode  from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' order by recpt_date";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("CONS_ACC", "  " + "AMT RECVD", 24),Normal);
                    btpObject.sendMessage_HW(leftAppend1("MR_NO", "  " + "RECPT DT", 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(0), " " + rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    }

                    btpObject.sendMessage("\n".getBytes());
                    rs.close();
                }
                else if (ReportTyp.equals("S")) {
                    billprint = "";
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("SUMMARY REPORT ENERGY",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL MR:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL RECVD:", rs.getString(2), 24),Normal);
                    }
                    rs.close();
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='') group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("TOT_MR " + "RCPT_DT " + "AMT_RECVD","",24),Normal) ;
                    btpObject.sendMessage_HW(leftAppend1("......" + " " + "........" + " " + ".......","",24),Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(0)+ " " + rs.getString(1), " " + rs.getString(2)+" " + rs.getString(3),24),Normal);

                    }
                    rs.close();

                }
                else if (ReportTyp.equals("SN")){
                    billprint = "";
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("SUMMARY REPORT NON-ENERGY",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),sum(tot_paid)    from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL MR:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL RECVD:", rs.getString(2), 24),Normal);
                    }
                    rs.close();
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%d-%m-%Y',recpt_date),sum(tot_paid),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"' group by recpt_date,pay_mode order by recpt_date,pay_mode ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("TOT_MR " + "RCPT_DT " + "AMT_RECVD","",24),Normal) ;
                    btpObject.sendMessage_HW(leftAppend1("......" + " " + "........" + " " + ".......","",24),Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(0)+ " " + rs.getString(1), " " + rs.getString(2)+" " + rs.getString(3),24),Normal);

                    }
                    rs.close();
                }

                else if (ReportTyp.equals("U")) {
                    billprint = "";
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("DAILY REPORT ENERGY",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL MR:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL RECVD:", rs.getString(2), 24),Normal);
                    }
                    rs.close();
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND (OPERATION_TYPE='1' OR  ifnull(OPERATION_TYPE,'')='')";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("CONS_ACC", "  " + "AMT RECVD", 24),Normal);
                    btpObject.sendMessage_HW(leftAppend1("MR_NO", "  " + "RECPT DT", 24),Normal);
                    ;
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(widthoff + rs.getString(0), " " + rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    }
                    rs.close();
                }
                else if (ReportTyp.equalsIgnoreCase("UN")){
                    billprint = "";
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("DAILY REPORT NON-ENERGY",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL MR:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL RECVD:", rs.getString(2), 24),Normal);
                    }
                    rs.close();
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) AND OPERATION_TYPE!='1' AND   OPERATION_TYPE!=+'"+"'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("CONS_ACC", "  " + "AMT RECVD", 24),Normal);
                    btpObject.sendMessage_HW(leftAppend1("MR_NO", "  " + "RECPT DT", 24),Normal);
                    ;
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(widthoff + rs.getString(0), " " + rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    }
                    rs.close();
                }

                else if (ReportTyp.equals("C")) {

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("CONSUMER REPORT",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "";
                    strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("COLL MONTH:", rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL MR:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL RECVD:", rs.getString(2), 24),Normal);
                    }
                    rs.close();
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "";
                    strUpdateSQL_01 = "select CUST_ID,Tot_Paid,mr_no,strftime('%d-%m-%Y',recpt_date),case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR' else 'CASH' end as pay_mode   from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and cons_acc='" + CustID + "'";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    btpObject.sendMessage_HW(leftAppend1("CONS_ACC", "  " + "AMT RECVD", 24),Normal);
                    btpObject.sendMessage_HW(leftAppend1("MR_NO", "  " + "RECPT DT", 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(0), " " + rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(2), " " + rs.getString(3)+" " + rs.getString(4), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    }
                    rs.close();
                } else if (ReportTyp.equals("N")) {
                    billprint = "";
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(CenterAppend1("NON RECPT. REPORT",24), Normal);

                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    btpObject.sendMessage_HW(leftAppend1("DATE:", dateFormat.format(cal.getTime()), 24),Normal);
                    btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);
                    strUpdateSQL_01 = "select trans_id,tot_paid,cust_id,case when pay_mode='3' then 'CHQUE' when pay_mode='2' then 'DD' when pay_mode='7' then 'POS' when pay_mode='8' then 'NEFT'when pay_mode='9' then 'RTGS' when pay_mode='4' then 'MR'  else 'CASH' end as pay_mode  from coll_sbm_data where strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    //btpObject.sendMessage_HW(CenterAppend1("CUSTOMER ID" + "TRANS.ID" + "AMT",24), Normal);
                    int iCnt = 0;
                    while (rs.moveToNext()) {
                        btpObject.sendMessage_HW(leftAppend1("TRANS.ID:", rs.getString(0), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("CUSTOMER ID:", rs.getString(2), 24),Normal);
                        btpObject.sendMessage_HW(leftAppend1("AMOUNT:", rs.getString(1)+" " + rs.getString(3), 24),Normal);
                        //   btpObject.sendMessage_HW(CenterAppend1(leftAppend2(rs.getString(2), rs.getString(0), 7, rs.getString(1), 24),Normal);
                        btpObject.sendMessage_HW(CenterAppend1("--------------------",24), Normal);

                    }

                    rs.close();
                }

                databaseAccess.close();
                btpObject.sendMessage("\n".getBytes());
                btpObject.sendMessage("\n".getBytes());
                btpObject.sendMessage("\n".getBytes());
            }
            Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
            reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(reports);
            finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();
            Log.d("DemoApp", "Exception 13  " + e22);

        } catch (Exception e23) {
            Log.d("DemoApp", "Exception 14  " + e23);
            e23.printStackTrace();
        }
        //  strPrntMsg.setText("Data Sent to Bluetooth Printer");
        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntRcpt);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rcptType="DUPLICATE";//
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET COLL_FLG=2";
                strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='" + TransID + "'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
                if (devicename.equals("nodevice")) {
                    try {
                        sendData();
                    } catch (Exception ex) {
                        //  Toast.makeText(BillPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });//end
        //Exit
        Button Exit = (Button) findViewById(R.id.Exit);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);

            }
        });//end
        //Continue
        Button contd = (Button) findViewById(R.id.contd);
        contd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reports2 = new Intent(getApplicationContext(), AcCollection.class);
                startActivity(reports2);
                finish();
                //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //     .setAction("Action", null).show();

            }
        });//end
    }      //DATE CONVERSION
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

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            //  stopWorker = true;

            if(mmOutputStream!= null) {
                mmOutputStream.flush();
                mmOutputStream.close();
            }
            if(mmInputStream != null)
                mmInputStream.close();

            try{
                if(btpObject.isConnected() == true) {
                    closeprinter = true;
                    btpObject.Batterystatus();
                    long curntime = System.currentTimeMillis();
                    while(curntime+10000 < System.currentTimeMillis()){
                        if(closeprinter == false){
                            break;
                        }
                    }
                    if(closeprinter == true) {
                        btpObject.closeConnection();
                        closeprinter = false;
                    }
                }
                if (mmSocket != null) {
                    Log.d("DemoApp", "on 10 " );
                    try {Log.d("DemoApp", "on 11 " );
                        mmSocket.close();Log.d("DemoApp", "on 12 ");
                        mmSocket = null;} catch (Exception e) {
                        mmSocket = null;Log.d("DemoApp", "on 8 "+e );}

                    Log.d("DemoApp", "on 13 " );
                }

            } catch (Exception e) { Log.d("DemoApp", "on 9 "+e );}
            //   strPrntMsg.setText("Bluetooth Closed");;
        } catch (NullPointerException e) {
            //  Toast.makeText(BillPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(BillPrintActivity.this, "message11" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public String font_Double_Height_Width_On()
    {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 8;
        String s = new String(rf1);
        return s;
    }
    public String font_Double_Height_Width_Off()
    {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 0;
        String s = new String(rf1);
        return s;
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
    public static String CenterAppend1(String str1,int maxlen){
        String retStr="";
        String str="";
        int strlen=0;
        int lendiff=0;
        lendiff=maxlen-str1.length();
        strlen=lendiff/2;
        Log.d("DemoApp", "strlen " + strlen);
        for(int i=0;i<strlen;i++){
            retStr+=" ";
        }
        str=str+retStr+str1;
        return str;

    }

    @Override
    public void onBackPressed() {
        Intent reports = new Intent(getApplicationContext(), CollReportActivity.class);
        reports.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(reports);
        finish();
    }
}

