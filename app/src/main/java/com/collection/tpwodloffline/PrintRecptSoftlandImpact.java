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
import com.softland.printerlib.PrinterSection.CharaStyle;
import com.softland.printerlib.PrinterSection.ConnectionStatus;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;
import com.softland.printerlib.PrinterSection.iPrinter;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class PrintRecptSoftlandImpact extends AppCompatActivity {
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
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private  String address = "";
    private Printer printer;
    private String rcptType="";
    private String TransID="";
    private String fromActivity="";
    private String operationType="";

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_softland_impact);
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
    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.


    void sendData()  {
        String st="";
        try {

            String version="";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm =sessiondata.getString("UserName", null); // getting String
            Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getName());
            String BillContents = "";
            String filldata = "";
            String paymode="";
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

                    try {
                        operationType=rs.getString(46);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    if (Billformat.equals("PrePrinted")) {
                        st=printer.reset();
                        st+=printer.printNewLine();
                        st+=printer.printLine(" ");
                        st+=printer.printNewLine();
                        try {
                            BlPrepTm= rs.getString(19);
                        }catch(Exception e){
                            //   BlPrepTm="0.00";
                        }
                        if(rs.getInt(23)==2) {
                            paymode="DD";
                        }else if(rs.getInt(23)==3) {
                            paymode="CHEQUE";
                        }else if(rs.getInt(23)==7) {
                            paymode = "POS";
                        }
                        else if (rs.getInt(23) == 8) {
                            paymode = "NEFT";
                        }
                        else if (rs.getInt(23) == 9) {
                            paymode = "RTGS";
                        }
                        else if (rs.getInt(23)==4){
                           // paymode="MONEY";
                            paymode = "MR";
                        }

                        else{
                            paymode="CASH";
                        }
                        if(!rs.getString(35).equals("1")){
                            rcptType="DUPLICATE";
                        }
                        st += printer.printLine(leftAppend2("", 10, rcptType, 24));
                        printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                        st += printer.printLine(leftAppend2("         TPWODL  ", 10, "", 24));
                        st += printer.printLine(leftAppend2("     MONEY RECEIPT", 18, "", 24));
                        printer.restCHARASTYLE();
                        st += printer.printLine(leftAppend1("------------------------", 24));
                        st += printer.printLine(leftAppend1("RECPT DT:"+ convertDateFormat(rs.getString(18),"DD-MM-YYYY")+ ":" + BlPrepTm, 24));
                        st += printer.printLine(leftAppend2("RECEIPT NO:",11, rs.getString(20), 24));
                        st += printer.printLine(leftAppend2("TRANSACTION ID:", 15, rs.getString(38), 24));
                        st += printer.printLine(leftAppend2("DIVN:", 5, rs.getString(2), 24));
                        printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                        st += printer.printLine(leftAppend2("CONSUMER NO:", 12, rs.getString(0), 24));
                        printer.restCHARASTYLE();

                        if (operationType.equalsIgnoreCase("1")){

                            st += printer.printLine(leftAppend2("CUST ID:", 8, rs.getString(1), 24));


                        }
                        else {
                            st += printer.printLine(leftAppend2("NOTIFICATION NO:", 8, rs.getString(1), 24));
                        }



                        if (rs.getString(5).trim().length() <= 19) {
                            st += printer.printLine(leftAppend2("NAME:", 5, rs.getString(5), 24));//name
                            st+=printer.printNewLine();
                        } else {
                            st += printer.printLine(leftAppend2("NAME:", 5, rs.getString(5), 24));
                            st+=printer.printNewLine();
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(6) + "," + rs.getString(7));
                        if (rs.getString(6).trim().length() + rs.getString(7).trim().length() > 17) {
                            // Log.d("DemoApp", "Exception 1  " + strAddr.length(), 24));
                            if (strAddr.length() >= 41) {
                                strAddr.setLength(41);
                            }
                            st += printer.printLine(leftAppend1("ADDRS:" + strAddr, 24));
                            st+=printer.printNewLine();
                        } else {
                            st += printer.printLine(leftAppend1((rs.getString(6) + "," + rs.getString(7)), 24));
                            st+=printer.printNewLine();
                        }
                        st += printer.printLine(leftAppend1("------------------------", 24));

                        String pmttype="BILL";
                        if(!rs.getString(36).equals("AcctNo")){
                            pmttype=rs.getString(36);
                        }
                        if (!(rs.getString(46).equalsIgnoreCase("1"))){
                            pmttype=rs.getString(47);
                        }
                        st += printer.printLine(leftAppend2("PAYMENT AGAINST:", 16, pmttype, 24));
                        st += printer.printLine(leftAppend2("PAYMENT MODE:", 13, paymode, 24));
                        if(rs.getString(23).equals("7")){
                            st += printer.printLine(leftAppend2("RECEIVED AMT:", 13, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("POS ID:", 7, rs.getString(39), 24));
                            st += printer.printLine(leftAppend2("POS DATE:", 9, rs.getString(27), 24));
                        }else if(rs.getString(23).equals("3")){
                            st += printer.printLine(leftAppend2("RECEIVED CHQ:", 13, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("CHQ NO:", 7, rs.getString(24), 24));
                            st += printer.printLine(leftAppend2("CHQ DATE:", 9, rs.getString(25), 24));
                        }
                        else if(rs.getString(23).equals("2")){
                            st += printer.printLine(leftAppend2("RECEIVED DD:", 12, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("DD NO:", 6, rs.getString(26), 24));
                            st += printer.printLine(leftAppend2("DD DATE:", 8, rs.getString(27), 24));
                        }

                        else if(rs.getString(23).equals("8")){
                            st += printer.printLine(leftAppend2("RECEIVED NEFT:", 12, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("NEFT NO:", 6, rs.getString(40), 24));
                            st += printer.printLine(leftAppend2("NEFT DATE:", 8, rs.getString(41), 24));
                        }
                        else if(rs.getString(23).equals("9")){
                            st += printer.printLine(leftAppend2("RECEIVED RTGS:", 12, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("RTGS NO:", 6, rs.getString(42), 24));
                            st += printer.printLine(leftAppend2("RTGS DATE:", 8, rs.getString(43), 24));
                        }
                        else if(rs.getString(23).equals("4")){
                            st += printer.printLine(leftAppend2("RECEIVED MR:", 12, rs.getString(22), 24));
                            st += printer.printLine(leftAppend2("MR NO:", 6, rs.getString(44), 24));
                            st += printer.printLine(leftAppend2("MR DATE:", 8, rs.getString(45), 24));
                        }



                        else{
                            st += printer.printLine(leftAppend2("RECEIVED CASH:", 14, rs.getString(22), 24));
                        }
                        if (rs.getString(37).trim().length() <= 14 && !rs.getString(28).equals("0")) {
                            st += printer.printLine(leftAppend2("BANK NAME:", 10, rs.getString(37), 24));
                        } else if(rs.getString(37).trim().length() > 14 && !rs.getString(28).equals("0")){
                            st += printer.printLine(leftAppend2("BANK NAME:", 10, rs.getString(37), 48));
                        }
                        printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                        st += printer.printLine(leftAppend2("TOTAL PAID:", 11, rs.getString(22), 24));
                        printer.restCHARASTYLE();
                        StringBuilder strword = new StringBuilder("AMOUNT RECEIVED (in word):"+NumberToWordConverter.numberToWord(rs.getInt(22))+" only");
                        Log.d("DemoApp", "on strword "+strword+"strword.length()"+strword.length());
                        if (strword.length() > 24 && strword.length() <= 48) {
                            //strword.setLength(48, 24));
                            st += printer.printLine(leftAppend1(strword.toString(), 48));
                        }else if(strword.length() > 48 && strword.length() <= 72){
                            st += printer.printLine(leftAppend1(strword.toString(), 72));
                        }else if(strword.length() > 72 && strword.length() <= 96){
                            st += printer.printLine(leftAppend1(strword.toString() , 96));
                        }else{
                            st += printer.printLine(leftAppend1(strword.toString() , 24));
                        }
                        st += printer.printLine(leftAppend1("", 24));
                        st += printer.printLine(leftAppend1("", 24));
                        st +=  printer.printLine(leftAppend1("SIGNATURE", 24));
                        st +=  printer.printLine(leftAppend1("Thanks.", 24));
                        st +=  printer.printLine(leftAppend1("RECEIVED BY:" + Usernm, 24));
                        st += printer.printLine(leftAppend1("------------------------", 24));
                        st +=  printer.printLine(leftAppend1("THIS IS AUTO-GENERATED  ", 24));
                        st +=  printer.printLine(leftAppend1("DOCUMENT AND SIGNATURE  ", 24));
                        st +=  printer.printLine(leftAppend1("MAY NOT BE REQUIRED     ", 24));
                        st+=printer.printNewLine();
                        st+=printer.printNewLine();
                        st+=printer.printNewLine();
                        st+=printer.printNewLine();
                        st+=printer.printNewLine();

                    }
                } //while loop close
            rs.close();

            String strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET COLL_FLG=2";
            strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='"+ TransID +"'";
            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
            DatabaseAccess.database.execSQL(strSelectSQL_01);
            databaseAccess.close();
            printer.printText(st);
            // printer.disonnected();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
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
                strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='"+ TransID +"'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
                try{
                    connectTodevice(address);
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
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
    public static String leftAppend1(String str,int maxlen){
        String retStr="";
        for(int i=0;i<(maxlen-str.length());i++){
            retStr+=" ";
        }
        str=retStr+str;
        return str;
    }
    public static String leftAppend2(String str,int leftlen,String Str1,int maxlen){
        String retStr="";
        for(int i=0;i<leftlen-str.length();i++){
            retStr+=" ";
        }
        str=str+retStr;
        retStr="";
        for(int i=0;i<(maxlen-(str.length()+Str1.length()));i++){
            retStr+=" ";
        }
        Str1=retStr+Str1;
        str=str+Str1;
        return str;

    }
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }

}



