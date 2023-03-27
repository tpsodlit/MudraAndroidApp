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
import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class PrintRecptAnalogicImpact extends AppCompatActivity {
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
    private Printer printer;
    private  String address = "";
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
        setContentView(R.layout.activity_print_recpt_analogic_impact);
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
        try{
            sendData();
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
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
    void sendData() throws IOException {
        try {
            String version="";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm =sessiondata.getString("UserName", null); // getting String
            Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getName());
            String BillContents = "";
            String filldata = "";
            String paymode="";
            Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
            String doubleHeight = BPImpact.font_Double_Height_On();
            String lnfeed = BPImpact.line_Feed();
            String widthon = BPImpact.font_Double_Height_Width_On();
            String widthoff = BPImpact.font_Double_Height_Width_Off();
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
                if (Billformat.equals("PrePrinted")) {
                    // this is added to handle mtr time print for 24 hrs at midnight

                    try {
                        operationType=rs.getString(46);

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

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
                    else if (rs.getInt(23) == 4) {
                       // paymode = "MONEY";
                        paymode = "MR";
                    }
                    else{
                        paymode="CASH";
                    }
                    if(!rs.getString(35).equals("1")){
                        rcptType="DUPLICATE";
                    }
                    BillContents = "\n\n";
                    BillContents += doubleHeight + String.format("%-24s", "");
                    BillContents += widthoff + String.format("%10s%14s", "          ",rcptType);
                    BillContents += doubleHeight + String.format("%-24s", "          TPWODL");
                    BillContents += doubleHeight + String.format("%-24s", "      MONEY RECEIPT");
                    BillContents += widthoff+"------------------------"+"\n";
                    // BillContents += String.format("%16s", convertDateFormat(rs.getString(79), "DD-MM-YYYY")) + String.format("%8s", BlPrepTm);
                    BillContents += String.format("%9s%8s%5s", "RECPT DT:", convertDateFormat(rs.getString(18), "DD-MM-YYYY"),":"+ BlPrepTm);
                    BillContents += widthoff+String.format("%11s%-13s", "RECEIPT NO:", rs.getString(20));
                    BillContents += widthoff+String.format("%15s%-9s", "TRANSACTION ID:", rs.getString(38));
                    BillContents += widthoff+String.format("%5s%-19s", "DIVN:", rs.getString(2));
                    BillContents += doubleHeight+String.format("%12s%-12s", "CONSUMER NO:", rs.getString(0));

                 /*   if (operationType.equalsIgnoreCase("1")){
                        BillContents += widthoff+String.format("%8s%16s", "CUST ID:", rs.getString(1));
                    }
                    else {
                        BillContents += widthoff+String.format("%8s%16s", "NOTIFICATION NO:", rs.getString(1));
                    }
*/


                    if (rs.getString(5).trim().length() <= 19) {
                        BillContents += String.format("%5s%-19s", "NAME:", rs.getString(5));//name
                        BillContents += "" + "\n";
                    } else {
                        BillContents += String.format("%5s%-43s", "NAME:", rs.getString(5));
                    }
                    StringBuilder strAddr = new StringBuilder(rs.getString(6) + "," + rs.getString(7));
                    if (rs.getString(6).trim().length() + rs.getString(7).trim().length() > 17) {
                        // Log.d("DemoApp", "Exception 1  " + strAddr.length());
                        if (strAddr.length() >= 41) {
                            strAddr.setLength(41);
                        }
                        BillContents += String.format("%6s%-42s", "ADDRS:", strAddr);
                    } else {
                        BillContents += String.format("%6s%-18s", "", (rs.getString(6) + "," + rs.getString(7)));
                        BillContents += "" + "\n";
                    }
                    BillContents += widthoff+"------------------------"+"\n";
                    String pmttype="BILL";
                    if(!rs.getString(36).equals("AcctNo")){
                        pmttype=rs.getString(36);
                    }
                    if (!(rs.getString(46).equalsIgnoreCase("1"))){
                        pmttype=rs.getString(47);
                    }
                    BillContents += widthoff+String.format("%16s%8s", "PAYMENT AGAINST:", pmttype);
                    BillContents += widthoff+String.format("%13s%11s", "PAYMENT MODE:", paymode);
                    if(rs.getString(23).equals("7")){
                        BillContents += widthoff+String.format("%13s%10s", "RECEIVED AMT:", rs.getString(22));
                        BillContents += String.format("%7s%17s", "POS ID:", rs.getString(39));
                        BillContents += String.format("%9s%15s", "POS DATE:", rs.getString(27));
                    }else if(rs.getString(23).equals("3")){
                        BillContents += widthoff+String.format("%13s%11s", "RECEIVED CHQ:", rs.getString(22));
                        BillContents += String.format("%7s%17s", "CHQ NO:", rs.getString(24));
                        BillContents += String.format("%9s%15s", "CHQ DATE:", rs.getString(25));
                    }else if(rs.getString(23).equals("2")){
                        BillContents += widthoff+String.format("%12s%-12s", "RECEIVED DD:", rs.getString(22));
                        BillContents += String.format("%-10s%14s", "DD NO:", rs.getString(26));
                        BillContents += String.format("%-10s%14s", "DD DATE:", rs.getString(27));
                    }

                    else if(rs.getString(23).equals("8")){
                        BillContents += widthoff+String.format("%12s%-12s", "RECEIVED NEFT:", rs.getString(22));
                        BillContents += String.format("%-10s%14s", "NEFT NO:", rs.getString(40));
                        BillContents += String.format("%-10s%14s", "NEFT DATE:", rs.getString(41));
                    }

                    else if(rs.getString(23).equals("9")){
                        BillContents += widthoff+String.format("%12s%-12s", "RECEIVED RTGS:", rs.getString(22));
                        BillContents += String.format("%-10s%14s", "RTGS NO:", rs.getString(42));
                        BillContents += String.format("%-10s%14s", "RTGS DATE:", rs.getString(43));
                    }
                    else if(rs.getString(23).equals("4")){
                        BillContents += widthoff+String.format("%12s%-12s", "RECEIVED MR:", rs.getString(22));
                        BillContents += String.format("%-10s%14s", "RECEIPT NO:", rs.getString(44));
                        BillContents += String.format("%-10s%14s", "RECEIPT DATE:", rs.getString(45));
                    }


                    else{
                        BillContents += widthoff+String.format("%14s%10s", "RECEIVED CASH:", rs.getString(22));
                    }
                    if (rs.getString(37).trim().length() <= 14 && !rs.getString(28).equals("0")) {
                        BillContents += widthoff+String.format("%10s%14s", "BANK NAME:", rs.getString(37));
                    } else if(rs.getString(37).trim().length() > 14 && !rs.getString(28).equals("0")){
                        BillContents += String.format("%10s%-38s", "BANK NAME:", rs.getString(37));
                    }
                    BillContents += doubleHeight+String.format("%11s%13s", "TOTAL PAID:", rs.getString(22));
                    StringBuilder strword = new StringBuilder("AMOUNT RECEIVED (in word):"+NumberToWordConverter.numberToWord(rs.getInt(22))+" only");
                    Log.d("DemoApp", "on strword "+strword+"strword.length()"+strword.length());
                    if (strword.length() > 24 && strword.length() <= 48) {
                        //strword.setLength(48);
                        BillContents += widthoff+String.format("%-48s", strword);
                    }else if(strword.length() > 48 && strword.length() <= 72){
                        BillContents += widthoff+String.format("%-72s", strword);
                    }else if(strword.length() > 72 && strword.length() <= 96){
                        BillContents += widthoff+String.format("%-96s",strword );
                    }else{
                        BillContents += widthoff+String.format("%-24s",strword );
                    }
                    BillContents += widthoff + String.format("%-24s", "");
                    BillContents += widthoff + String.format("%-24s", "");
                    BillContents += widthoff + String.format("%-24s", "SIGNATURE");
                    BillContents += widthoff + String.format("%-24s", "Thanks.");
                    BillContents += widthoff + String.format("%-24s", "RECEIVED BY:" + Usernm);
                    BillContents += widthoff+"------------------------"+"\n";
                    BillContents += widthoff + String.format("%-24s", "THIS IS AUTO-GENERATED  ");
                    BillContents += widthoff + String.format("%-24s", "DOCUMENT AND SIGNATURE  ");
                    BillContents += widthoff + String.format("%-24s", "MAY NOT BE REQUIRED     ");
                    BillContents += widthoff + "\n\n\n\n";

                }
            } //while loop close
            rs.close();
            String strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET COLL_FLG=2";
            strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='"+ TransID +"'";
            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
            DatabaseAccess.database.execSQL(strSelectSQL_01);
            databaseAccess.close();
            AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();
            print.openBT(mmDevice.getAddress());
            print.printData(BillContents);
            print.closeBT();

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
                if(devicename.equals("nodevice")) {
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
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }

}



