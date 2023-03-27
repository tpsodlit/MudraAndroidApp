package com.collection.tpwodloffline;

import android.app.Activity;
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

import com.collection.tpwodloffline.activity.AcCollection;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class PrintRecptEpsonThermal extends Activity implements ReceiveListener {
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
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private  String address = "";
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
        setContentView(R.layout.activity_print_recpt_epson_thermal);
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

        runPrintReceiptSequence();
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
                runPrintReceiptSequence();
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

            String version="";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm =sessiondata.getString("UserName", null); // getting String
            String BillContents = "";
            String paymode="";

            String BlPrepTm = "";
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
                    mPrinter.addFeedLine(3);
                    try {
                        BlPrepTm= rs.getString(19);
                    }catch(Exception e){
                        //   BlPrepTm="0.00";
                    }

                try {
                    operationType=rs.getString(46);

                }catch (Exception ex){
                    ex.printStackTrace();
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
                mPrinter.addTextAlign(Printer.ALIGN_RIGHT);
                mPrinter.addTextSize(1, 1);
                textData.append(rcptType + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("TPCODL" + "\n");
                textData.append("MONEY RECEIPT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                textData.append("  RECPT DT:" + convertDateFormat(rs.getString(18), "DD-MM-YYYY") + ":" + BlPrepTm + "\n");
                textData.append(leftAppend1("RECEIPT NO:", rs.getString(20), 36) + "\n");
                textData.append(leftAppend1("TRANSACTION ID:", rs.getString(38), 36) + "\n");
                textData.append(leftAppend1("DIVN:", rs.getString(2), 36) + "\n");
                textData.append(leftAppend1("CONSUMER NO:", rs.getString(0), 36) + "\n");

                if (operationType.equalsIgnoreCase("1")){

                    textData.append(leftAppend1("CUST ID:", rs.getString(1), 36) + "\n");
                }
                else {
                    textData.append(leftAppend1("NOTIFICATION NO:", rs.getString(1), 36) + "\n");
                }




                textData.append(leftAppend1("RECEIPT NO:", rs.getString(20), 36) + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                textData.append("  NAME:" + rs.getString(5) + "\n");
                textData.append("  ADDRS:" + rs.getString(6) + "," + rs.getString(7) + "\n");
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                   String pmttype="BILL";
                    if(!rs.getString(36).equals("AcctNo")){
                        pmttype=rs.getString(36);
                    }

                if (!(rs.getString(46).equalsIgnoreCase("1"))){
                    pmttype=rs.getString(47);
                }
                textData.append(leftAppend1("PAYMENT AGAINST:", pmttype, 36) + "\n");
                textData.append(leftAppend1("PAYMENT MODE:", paymode, 36) + "\n");

                    if(rs.getString(23).equals("7")){
                        textData.append(leftAppend1("RECEIVED AMT:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("POS ID:", rs.getString(39), 36) + "\n");
                        textData.append(leftAppend1("POS DATE:", rs.getString(27), 36) + "\n");

                    }else if(rs.getString(23).equals("3")){
                        textData.append(leftAppend1("RECEIVED CHQ:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("CHQ NO:", rs.getString(24), 36) + "\n");
                        textData.append(leftAppend1("CHQ DATE:", rs.getString(25), 36) + "\n");

                    }
                    else if(rs.getString(23).equals("2")){
                        textData.append(leftAppend1("RECEIVED DD:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("DD NO:", rs.getString(26), 36) + "\n");
                        textData.append(leftAppend1("DD DATE:", rs.getString(27), 36) + "\n");

                    }
                    else if(rs.getString(23).equals("8")){
                        textData.append(leftAppend1("RECEIVED NEFT:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("NEFT NO:", rs.getString(40), 36) + "\n");
                        textData.append(leftAppend1("NEFT DATE:", rs.getString(41), 36) + "\n");

                    }
                    else if(rs.getString(23).equals("9")){
                        textData.append(leftAppend1("RECEIVED RTGS:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("RTGS NO:", rs.getString(42), 36) + "\n");
                        textData.append(leftAppend1("RTGS DATE:", rs.getString(43), 36) + "\n");

                    }
                    else if(rs.getString(23).equals("4")){
                        textData.append(leftAppend1("RECEIVED MR:", rs.getString(22), 36) + "\n");
                        textData.append(leftAppend1("MR NO:", rs.getString(44), 36) + "\n");
                        textData.append(leftAppend1("MR DATE:", rs.getString(45), 36) + "\n");

                    }



                    else{
                        textData.append(leftAppend1("RECEIVED CASH:", rs.getString(22), 36) + "\n");
                    }
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                if (rs.getString(37).trim().length() <= 14 && !rs.getString(28).equals("0")) {
                    textData.append(leftAppend1("BANK NAME:", rs.getString(37), 36) + "\n");
                } else if(rs.getString(37).trim().length() > 14 && !rs.getString(28).equals("0")){
                    textData.append(leftAppend1("BANK NAME:", rs.getString(37), 36) + "\n");
                }
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                mPrinter.addTextSize(2, 2);
                textData.append(" TOT PAID:" + rs.getString(22) + ".00" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);

                textData.append(leftAppend1("AMOUNT RECEIVED (in word):", NumberToWordConverter.numberToWord(rs.getInt(22)) + " only", 36) + "\n");
                mPrinter.addFeedLine(2);
                textData.append(leftAppend1("SIGNATURE", " ", 36) + "\n");
                textData.append(leftAppend1(" ", " ", 36) + "\n");
                textData.append(leftAppend1("Thanks.", " ", 36) + "\n");
                textData.append(leftAppend1("RECEIVED BY:",Usernm, 36) + "\n");
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("THIS IS AUTO-GENERATED DOCUMENT AND"," ", 36) + "\n");
                textData.append(leftAppend1("SIGNATURE MAY NOT BE REQUIRED ", " ", 36) + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addFeedLine(2);
                mPrinter.addFeedLine(2);




            } //while loop close
            rs.close();

            String strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET COLL_FLG=2";
            strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='"+ TransID +"'";
            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
            DatabaseAccess.database.execSQL(strSelectSQL_01);
            databaseAccess.close();


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

}
