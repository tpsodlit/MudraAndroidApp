package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class PrintRecptAmigoImpact extends AppCompatActivity {
    //AMIGOS
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
    private String TransID="";
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String rcptType="";

    private  String address = "";
    //AMIGOS
    BluetoothManager btpObject;
    public static final int DoubleWidth = 3,DoubleHght = 2,Normal = 1;

    boolean closeprinter = false;
    private String fromActivity="";
    private String operationType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_amigo_impact);
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
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mmOutputStream=null;
        mmInputStream=null;
        mmDevice=null;
        mBluetoothAdapter=null;
        rcptType="ORIGINAL";
        AccNum="";
        String dubl="";
        String accnumber="";
        Bundle PrintBun = getIntent().getExtras();
        AccNum = PrintBun.getString("custID");
        TransID= PrintBun.getString("TransID");
        fromActivity=PrintBun.getString("from");

        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
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

            String version = "";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm = sessiondata.getString("UserName", null); // getting String
            // Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());
            if (btpObject.isConnected() == true) {
                Log.d("DemoApp", "Printing Data ...  ");
                btpObject.printerfilter(btpObject.PRINTER_DEFAULT);
                String filldata = "";
                String paymode = "";

                String BlPrepTm = "";
                String Billformat = "PrePrinted";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
                Date vardate = null;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                //to get the current version of software
                String strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
                Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
                while (rs1.moveToNext()) {
                    version = rs1.getString(0);
                }
                rs1.close();
                //getting user name
                ////
                String strUpdateSQL_01 = "Select" +
                        " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                        " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                        " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                        " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                        " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                        " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                        " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,TRANS_ID," +//38
                        " A.POS_TRANS_ID,A.NEFT_NO, strftime('%d-%m-%Y',A.NEFT_DATE),A.RTGS_NO,strftime('%d-%m-%Y',A.RTGS_DATE),A.MONEY_RECPT_ID,strftime('%d-%m-%Y',A.MONEY_RECPT_DATE), case when ifnull(OPERATION_TYPE,'')= '' then '1' else OPERATION_TYPE end ,SPINNER_NON_ENERGY" +
                        " FROM " +
                        " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and CUST_ID = '" + AccNum + "' AND TRANS_ID='" + TransID + "'";
                Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());

                    try {
                        operationType=rs.getString(46);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    try {
                        BlPrepTm = rs.getString(19);
                    } catch (Exception e) {
                        //   BlPrepTm="0.00";
                    }
                    if (rs.getInt(23) == 2) {
                        paymode = "DD";
                    } else if (rs.getInt(23) == 3) {
                        paymode = "CHEQUE";
                    } else if (rs.getInt(23) == 7) {
                        paymode = "POS";
                    }
                    else if (rs.getInt(23) == 8) {
                        paymode = "NEFT";
                    }
                    else if (rs.getInt(23) == 9) {
                        paymode = "RTGS";
                    }
                    else if (rs.getInt(23)==4){
                       // paymode = "MONEY";
                        paymode = "MR";
                    }

                    else {
                        paymode = "CASH";
                    }
                    if (!rs.getString(35).equals("1")) {
                        rcptType = "DUPLICATE";
                    }
                    btpObject.sendMessage_HW(leftAppend1("", rcptType, 24) , Normal);
                    btpObject.sendMessage_HW(CenterAppend1("TPWODL", 24),DoubleHght);
                    btpObject.sendMessage_HW(CenterAppend1("MONEY RECEIPT", 24),DoubleHght);
                    btpObject.sendMessage_HW("--------------------",Normal);

                    btpObject.sendMessage_HW(leftAppend1("RECPT DT:" + convertDateFormat(rs.getString(18), "DD-MM-YYYY") + ":", BlPrepTm, 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("RECEIPT NO:", rs.getString(20), 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("TRANSACTION ID:", rs.getString(38), 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("DIVN:", rs.getString(2), 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("CONSUMER NO:", rs.getString(0), 24) , DoubleHght);

               /*     if (operationType.equalsIgnoreCase("1")){
                        btpObject.sendMessage_HW(leftAppend1("CUST ID:", rs.getString(1), 24) , Normal);
                    }
                    else {
                        btpObject.sendMessage_HW(leftAppend1("NOTIFICATION NO:", rs.getString(1), 24) , Normal);
                    }*/



                    btpObject.sendMessage_HW("NAME:" + rs.getString(5), Normal);
                    btpObject.sendMessage_HW("ADDRS:" + rs.getString(6) + "," + rs.getString(7), Normal);
                    btpObject.sendMessage_HW("--------------------",Normal);
                    String pmttype = "BILL";
                    if (!rs.getString(36).equals("AcctNo")) {
                        pmttype = rs.getString(36);
                    }
                    if (!(rs.getString(46).equalsIgnoreCase("1"))){
                        pmttype=rs.getString(47);
                    }

                    btpObject.sendMessage_HW(leftAppend1("PAYMENT AGAINST:", pmttype, 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("PAYMENT MODE:", paymode, 24) , Normal);
                    if (rs.getString(23).equals("7")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED AMT:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("POS ID:", rs.getString(39), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("POS DATE:", rs.getString(27), 24) , Normal);

                    }

                    else if (rs.getString(23).equals("3")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED CHQ:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("CHQ NO:", rs.getString(24), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("CHQ DATE:", rs.getString(25), 24) , Normal);
                    } else if (rs.getString(23).equals("2")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED DD:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("DD NO:", rs.getString(26), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("DD DATE:", rs.getString(27), 24) , Normal);
                    }

                    else if (rs.getString(23).equals("8")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED NEFT:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("NEFT NO:", rs.getString(40), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("NEFT DATE:", rs.getString(41), 24) , Normal);
                    }
                    else if (rs.getString(23).equals("9")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED RTGS:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("RTGS NO:", rs.getString(42), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("RTGS DATE:", rs.getString(43), 24) , Normal);
                    }
                    else if (rs.getString(23).equals("4")) {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED MR:", rs.getString(22), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("MR NO:", rs.getString(44), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("MR DATE:", rs.getString(45), 24) , Normal);
                    }

                    else {
                        btpObject.sendMessage_HW(leftAppend1("RECEIVED CASH:", rs.getString(22), 24) , Normal);
                    }

                    if (rs.getString(37).trim().length() <= 14 && !rs.getString(28).equals("0")) {
                        btpObject.sendMessage_HW(leftAppend1("BANK NAME:", rs.getString(37), 24) , Normal);
                    } else if (rs.getString(37).trim().length() > 14 && !rs.getString(28).equals("0")) {
                        btpObject.sendMessage_HW(leftAppend1("BANK NAME:", rs.getString(37), 24) , Normal);
                    }
                    btpObject.sendMessage_HW(leftAppend1("TOT PAID:", rs.getString(22) + ".00", 24) , Normal);
                    btpObject.sendMessage_HW("AMOUNT RECEIVED (in word):" + NumberToWordConverter.numberToWord(rs.getInt(22)) + " only", Normal);

                    btpObject.sendMessage_HW(leftAppend1(" ", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("SIGNATURE", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("Thanks.", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1(" ", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("RECEIVED BY:", Usernm, 24) , Normal);
                    btpObject.sendMessage_HW("--------------------",Normal);
                    btpObject.sendMessage_HW(leftAppend1("THIS IS AUTO-GENERATED  ", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("DOCUMENT AND SIGNATURE  ", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("MAY NOT BE REQUIRED     ", "", 24) , Normal);
                    btpObject.sendMessage_HW(leftAppend1("   ", "", 24) , Normal);
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());

                } //while loop close
                rs.close();
                btpObject.sendMessage("\n".getBytes());

                // printer.disonnected();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }
        } catch (NullPointerException e22) {
            e22.printStackTrace();
            Log.d("DemoApp", "Exception 13  " + e22);

        } catch (Exception e23) {
            Log.d("DemoApp", "Exception 14  " + e23);
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");
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
                btpObject.createClient(address);
                if(devicename.equals("nodevice")) {
                    try {
                        devicename = findBT();
                    } catch (Exception ex) {
                        //  Toast.makeText(BillPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                }
                btpObject.createClient(address);
                try{
                    sendData();
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
                }try{
                    //workerThread.sleep(20000);
                    // Thread.sleep(20000);
                    closeBT();
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
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
                if (fromActivity.equalsIgnoreCase("non-account")){
                    Intent reports2 = new Intent(getApplicationContext(), NoNAccountActivity.class);
                    startActivity(reports2);
                    finish();
                }
                else {
                    Intent reports2 = new Intent(getApplicationContext(), AcCollection.class);
                    startActivity(reports2);
                    finish();
                }


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
                    try {Log.d("DemoApp", "on 11 " );mmSocket.close();Log.d("DemoApp", "on 12 ");mmSocket = null;} catch (Exception e) { mmSocket = null;Log.d("DemoApp", "on 8 "+e );}

                    Log.d("DemoApp", "on 13 " );
                }

            } catch (Exception e) { Log.d("DemoApp", "on 9 "+e );}
            strPrntMsg.setText("Bluetooth Closed");;
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
}


