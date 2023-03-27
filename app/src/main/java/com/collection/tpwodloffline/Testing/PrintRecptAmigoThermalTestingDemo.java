package com.collection.tpwodloffline.Testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.NumberToWordConverter;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.DeviceList;
import com.collection.tpwodloffline.otp.UnicodeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class PrintRecptAmigoThermalTestingDemo extends AppCompatActivity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    TextView strPrntMsg;
    Button ReprntRcpt;
    String devicename = "nodevice";
    private String rcptType = "";
    private String address = "";
    private String fromActivity = "";
    private String operationType = "";
    private String TransID = "";
    private String AccNum = "";

    private DatabaseAccess databaseAccess = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_amigo_thermal);


        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        ReprntRcpt = (Button) findViewById(R.id.ReprntRcpt);
        strPrntMsg.setText("Printing");

        mBluetoothAdapter = null;
        rcptType = "ORIGINAL";
        AccNum = "";
        String dubl = "";
        String accnumber = "";
        Bundle PrintBun = getIntent().getExtras();
        AccNum = PrintBun.getString("custID");
        TransID= PrintBun.getString("TransID");
        //fromActivity=PrintBun.getString("from");

        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
        Log.d("DemoApp", "devicename  " + devicename);

        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PrintRecptAmigoThermalTestingDemo.this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintRecptAmigoThermalTestingDemo.this,
                                DeviceList.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        if (devicename.equals("nodevice")) {
            try {
                //     Log.d("DemoApp", "Entering findbt  " );

                Log.d("DemoApp", "BT found ");
            } catch (Exception ex) {

                //     Log.d("DemoApp", "Exception 1  " + ex);
            }
        }

        ReprntRcpt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                try {
                    Log.d("DemoApp", "sending data  ");
                    sendData();
                    //    Log.d("DemoApp", "data sent ");
                } catch (Exception ex) {
                    Log.d("DemoApp", "Exception 3 " + ex);
                }
            }
        });



        try {
            //workerThread.sleep(20000);
            // Thread.sleep(20000);
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
        }
    }

    final Context context = this;

    // This will find a bluetooth printer device
    void sendData() throws IOException {
        String billprint = "";
        try {

            String version = "";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm = sessiondata.getString("usrID", null);


            // Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());
            if (!devicename.equals("nodevice")) {
                Log.d("DemoApp", "Printing Data ...  ");
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
                DatabaseAccess.database.execSQL(strUpdateSQL_01);

                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    try {
                        operationType = rs.getString(46);

                    } catch (Exception ex) {
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
                    } else if (rs.getInt(23) == 8) {
                        paymode = "NEFT";
                    } else if (rs.getInt(23) == 9) {
                        paymode = "RTGS";
                    } else if (rs.getInt(23) == 4) {
                        // paymode = "MONEY";
                        paymode = "MR";
                    } else {
                        paymode = "CASH";
                    }
                    if (!rs.getString(35).equals("1")) {
                        rcptType = "DUPLICATE";
                    }


                } //while loop close


                String finalBlPrepTm = BlPrepTm;
                String finalPaymode = paymode;
                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = "";

                            BILL = "                     " + rcptType + "\n" +
                                    "            TPWODL    \n" +
                                    "         MONEY RECEIPT.     \n";
                            BILL = BILL +
                                    "------------------------------\n";


                            //BILL = BILL + "RECPT DT:" + convertDateFormat(rs.getString(18), "DD-MM-YYYY") + ":" + finalBlPrepTm + "\n";
                            BILL = BILL + "RECPT DT:" + "TPWODL"+ "\n";
                            BILL = BILL + "RECEIPT NO:      " + "TPWODL" + "\n";
                            BILL = BILL + "TRANSACTION ID:  " + "TPWODL" + "\n";
                            BILL = BILL + "DIVN:            " + "TPWODL" + "\n";
                            BILL = BILL + "CONSUMER NO:     " + "TPWODL" + "\n";
                            BILL = BILL + "NAME: " + "TPWODL" + "\n";
                            BILL = BILL + "ADDRS:" + "TPWODL"+ "," + "TPWODL" + "\n";

                            BILL = BILL + "\n";
                            BILL = BILL
                                    + "------------------------------\n";

                            String pmttype = "BILL";
                           // if (!rs.getString(36).equals("AcctNo")) {
                                pmttype = "TPWODL";
                           //}
                           // if (!(rs.getString(46).equalsIgnoreCase("1"))) {
                                pmttype = "TPWODL";
                           // }
                            BILL = BILL + "PAYMENT AGAINST:  " + pmttype + "\n";
                            BILL = BILL + "PAYMENT MODE:     " + finalPaymode + "\n";

                          /*  if (rs.getString(23).equals("7")) {
                                BILL = BILL + "RECEIVED AMT: " + rs.getString(22) + "\n";
                                BILL = BILL + "POS ID:       " + rs.getString(39) + "\n";
                                BILL = BILL + "POS DATE:     " + rs.getString(27) + "\n";


                            } else if (rs.getString(23).equals("3")) {
                                BILL = BILL + "RECEIVED CHQ: " + rs.getString(22) + "\n";
                                BILL = BILL + "CHQ NO:       " + rs.getString(24) + "\n";
                                BILL = BILL + "CHQ DATE:     " + rs.getString(25) + "\n";
                            } else if (rs.getString(23).equals("2")) {
                                BILL = BILL + "RECEIVED DD: " + rs.getString(22) + "\n";
                                BILL = BILL + "DD NO:       " + rs.getString(26) + "\n";
                                BILL = BILL + "DD DATE:     " + rs.getString(27) + "\n";
                            } else if (rs.getString(23).equals("8")) {
                                BILL = BILL + "RECEIVED NEFT: " + rs.getString(22) + "\n";
                                BILL = BILL + "NEFT NO:       " + rs.getString(40) + "\n";
                                BILL = BILL + "NEFT DATE:     " + rs.getString(41) + "\n";

                            } else if (rs.getString(23).equals("9")) {
                                BILL = BILL + "RECEIVED RTGS: " + rs.getString(22) + "\n";
                                BILL = BILL + "RTGS NO:       " + rs.getString(42) + "\n";
                                BILL = BILL + "RTGS DATE:     " + rs.getString(43) + "\n";

                            } else if (rs.getString(23).equals("4")) {
                                BILL = BILL + "RECEIVED MR: " + rs.getString(22) + "\n";
                                BILL = BILL + "MR NO:       " + rs.getString(44) + "\n";
                                BILL = BILL + "MR DATE:     " + rs.getString(45) + "\n";

                            } else {*/
                                BILL = BILL + "RECEIVED CASH: " + "TPWODL" + "\n";

                            //}

                               BILL = BILL + "TOT PAID:      " + "TPWODL" + ".00" +"\n";
                               BILL = BILL + "AMOUNT RECEIVED (in word):" + NumberToWordConverter.numberToWord(603) + " only" +"\n";
                               BILL = BILL + "SIGNATURE" +"\n";
                               BILL = BILL + "Thanks" +"\n\n";
                               BILL = BILL + "RECEIVED BY:  "+ Usernm+"\n";

                            BILL = BILL +
                                    "------------------------------\n";
                            BILL = BILL + "THIS IS AUTO-GENERATED " +"\n";
                            BILL = BILL + "DOCUMENT AND SIGNATURE" +"\n";
                            BILL = BILL + "MAY NOT BE REQUIRED" +"\n";

                            //////

                            BILL = BILL + "\n\n ";
                            os.write(BILL.getBytes());
                            //This is printer specific code you can comment ==== > Start
                           /* btpObject.sendMessage("\n".getBytes());
                            btpObject.sendMessage("\n".getBytes());

                            btpObject.sendMessage_HW(CenterAppend1("TPSODL",  32) , Normal);
                            btpObject.sendMessage_HW(CenterAppend1("Testing",  32) , Normal);
                            btpObject.sendMessage("\n".getBytes());
                            btpObject.sendMessage("\n".getBytes());
                            btpObject.sendMessage("\n".getBytes());
                            btpObject.sendMessage("\n".getBytes());*/
                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 100;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));
                            rs.close();


                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
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

                if (devicename.equals("nodevice")) {
                    try {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Toast.makeText(PrintRecptAmigoThermalTestingDemo.this, "Message1", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(
                                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent,
                                        REQUEST_ENABLE_BT);
                            } else {
                                ListPairedDevices();
                                Intent connectIntent = new Intent(PrintRecptAmigoThermalTestingDemo.this,
                                        DeviceList.class);
                                startActivityForResult(connectIntent,
                                        REQUEST_CONNECT_DEVICE);
                            }
                        }
                    } catch (Exception ex) {
                        //  Toast.makeText(BillPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        sendData();
                    } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
                    }
                }
                try {
                    //workerThread.sleep(20000);
                    // Thread.sleep(20000);
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
                if (fromActivity.equalsIgnoreCase("non-account")) {
                    Intent reports2 = new Intent(getApplicationContext(), NoNAccountActivity.class);
                    startActivity(reports2);
                    finish();
                } else {
                    Intent reports2 = new Intent(getApplicationContext(), AcCollection.class);
                    startActivity(reports2);
                    finish();
                }


                //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //     .setAction("Action", null).show();

            }
        });//end
    }      //DATE CONVERSION

    public static String convertDateFormat(String strTokenValue, String strDataFormat) {
        String strTokenValueRevDt = "";
        String strTokenValueOrgDt = strTokenValue;
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth = strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");
        int idxEYear = strDataFormat.lastIndexOf("Y");
        int idxSHour = strDataFormat.indexOf("HH");

        try {
            strTokenValueRevDt = strTokenValueOrgDt.substring(idxSDate, idxSDate + 2) + "-" +
                    strTokenValueOrgDt.substring(idxSMonth, idxSMonth + 2) + "-" +
                    strTokenValueOrgDt.substring(idxSYear + 2, idxSYear + 4);

        } catch (Exception e) {
            strTokenValueRevDt = "01-01-99";
            Log.d("DemoApp", "e   " + e);
        }
        return strTokenValueRevDt;
    }

    // Close the connection to bluetooth printer.
    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    devicename = mDeviceAddress;
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintRecptAmigoThermalTestingDemo.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintRecptAmigoThermalTestingDemo.this, "Switch on bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String font_Double_Height_Width_On() {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 8;
        String s = new String(rf1);
        return s;
    }

    public String font_Double_Height_Width_Off() {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 0;
        String s = new String(rf1);
        return s;
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrintRecptAmigoThermalTestingDemo.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }

    public static String leftAppend1(String str, String str1, int maxlen) {
        String retStr = "";
        int strlen = 0;
        strlen = str.length() + str1.length();
        for (int i = 0; i < (maxlen - strlen); i++) {
            retStr += " ";
        }
        str = str + retStr + str1;
        return str;

    }

    public static String leftAppend2(String str0, String str, int leftlen, String Str1, int maxlen) {
        String retStr = "";
        for (int i = 0; i < leftlen - str.length(); i++) {
            retStr += " ";
        }
        str = str + retStr;
        str0 = str0 + str;
        retStr = "";
        for (int i = 0; i < (maxlen - (str0.length() + Str1.length())); i++) {
            retStr += " ";
        }
        Str1 = retStr + Str1;
        str0 = str0 + Str1;
        return str0;

    }

    public static String leftAppend3(String str0, String str, int rlen, String Str1, int Rlen1, String Str2, int maxlen) {

        String retStr = "";
        for (int i = 0; i < (rlen - str.length()); i++) {
            retStr += " ";
        }
        str = str + retStr;
        str0 = str0 + str;
        retStr = "";
        for (int i = 0; i < (Rlen1 - Str1.length()); i++) {
            retStr += " ";
        }
        Str1 = Str1 + retStr;
        str0 = str0 + Str1;

        for (int i = 0; i < (maxlen - (Str2.length() + str0.length())); i++) {
            retStr += " ";
        }
        Str2 = retStr + Str2;
        str0 = str0 + Str2;
        return str0;

    }

    public static String CenterAppend1(String str1, int maxlen) {
        String retStr = "";
        String str = "";
        int strlen = 0;
        int lendiff = 0;
        lendiff = maxlen - str1.length();
        strlen = lendiff / 2;
        Log.d("DemoApp", "strlen " + strlen);
        for (int i = 0; i < strlen; i++) {
            retStr += " ";
        }
        str = str + retStr + str1;
        return str;
    }
}



