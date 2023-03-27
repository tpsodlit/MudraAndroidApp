package com.collection.tpwodloffline.ReportPrinters;

import android.annotation.SuppressLint;
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

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.otp.UnicodeFormatter;
import com.collection.tpwodloffline.utils.DeviceList;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PrintReportAmigoThermalNew extends Activity implements Runnable {

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
    private String Type = "";
    private String TransID = "";
    private String AccNum = "";
    Thread mBlutoothConnectThread;
    private DatabaseAccess databaseAccess = null;
    SharedPreferenceClass sharedPreferenceClass;
    String type = "";
    String cdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_report_amigo_thermal);

        sharedPreferenceClass = new SharedPreferenceClass(PrintReportAmigoThermalNew.this);
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        ReprntRcpt = (Button) findViewById(R.id.ReprntRcpt);
        strPrntMsg.setText("Scan to connect");

        mBluetoothAdapter = null;
        rcptType = "ORIGINAL";
        AccNum = "";
        type = getIntent().getStringExtra("type");
        cdate = getIntent().getStringExtra("cdate");

        Log.d("DemoApp", "devicename  " + devicename);

        Button Exit = (Button) findViewById(R.id.Exit);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                // System.exit(0);
                //onBackPressed();
            }
        });//end

        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                   // Toast.makeText(PrintRecptAmigoThermalNew.this, "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintReportAmigoThermalNew.this,
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
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {

                        String DeviceAddress =  sharedPreferenceClass.getValue_string("DeviceAddress1");
                        if(!DeviceAddress.equals("")){
                            devicename = DeviceAddress;
                            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(DeviceAddress);
                            mBluetoothConnectProgressDialog = ProgressDialog.show(PrintReportAmigoThermalNew.this,
                                    "Connecting...", mBluetoothDevice.getName() , true, true);
                            mBlutoothConnectThread = new Thread(PrintReportAmigoThermalNew.this);
                            mBlutoothConnectThread.start();

                            //sendData();

                        }else {

                            Toast.makeText(context, "Connect to printer", Toast.LENGTH_SHORT).show();

                        }


                    }
                    Log.d("DemoApp", "sending data  ");

                    //    Log.d("DemoApp", "data sent ");
                } catch (Exception ex) {
                    Log.d("DemoApp", "Exception 3 " + ex);
                    ex.printStackTrace();
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
        String currentDate;
        try {

            String version = "";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm = sessiondata.getString("usrID", null);
            String username = sharedPreferenceClass.getValue_string("username");
            String un = sharedPreferenceClass.getValue_string("un");
            String mobile = sharedPreferenceClass.getValue_string("mobile");


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
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                Cursor rs;
                if (type.equals("old")) {
                    String strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data_bkp where recpt_flg=1 and '" + cdate + "' =strftime('%d-%m-%Y', recpt_date)";
                    Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    rcptType = "DUPLICATE";
                } else {
                    String strUpdateSQL_01 = "select count(1),strftime('%m-%Y', 'now'),Sum(TOT_PAID)  from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date)";
                    Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    rcptType = "";
                }
                while (rs.moveToNext()) {

                    String tot_mr = rs.getString(0);
                    String tot_recvd = rs.getString(2);
                    String col_mnth = rs.getString(1);
                    String uploaded = String.valueOf(getUploadedCount());
                    String pending = String.valueOf(getOfflineCount());

                    if (type.equals("old")) {
                        currentDate = cdate;
                    } else {
                        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    }

                    try {
                        OutputStream os = mBluetoothSocket
                                .getOutputStream();
                        String BILL = "";

                        BILL = "                     " + rcptType + "\n" +
                                "             TPWODL    \n" +
                                "        SUMMARISED REPORT     \n";
                        BILL = BILL +
                                "--------------------------------\n";

                        BILL = BILL + "AGENT ID:       " + un +"\n";
                        BILL = BILL + "MOBILE NO:      " + mobile +"\n";

                        BILL = BILL +
                                "--------------------------------\n";

                        BILL = BILL + "DATE:           " + currentDate +"\n";
                        //BILL = BILL + "RECEIPT NO:     " + rs.getString(20) + "\n";
                        BILL = BILL + "TOTAL MR :      " + tot_mr + "\n";
                        BILL = BILL + "TOTAL RECEIVED: Rs." + tot_recvd + "\n";
                        if (type.equals("new")) {
                            BILL = BILL + "UPLOADED:       " + uploaded + "\n";
                            BILL = BILL + "PENDING:        " + pending + "\n";
                        }

                        BILL = BILL + "\n";
                        BILL = BILL
                                + "--------------------------------\n";
                        BILL = BILL + CommonMethods.getcurrentTime() + "\n";
                        BILL = BILL
                                + "--------------------------------\n";
                        //////

                        BILL = BILL + "\n\n ";
                        os.write(BILL.getBytes());

                        int gs = 29;
                        os.write(intToByteArray(gs));
                        int h = 104;
                        os.write(intToByteArray(h));
                        int n = 162;
                        os.write(intToByteArray(n));

                        // Setting Width
                        int gs_width = 29;
                        os.write(intToByteArray(gs_width));
                        int w = 119;
                        os.write(intToByteArray(w));
                        int n_width = 2;
                        os.write(intToByteArray(n_width));
                        rs.close();


                    } catch (Exception e) {
                        Log.e("MainActivity", "Exe ", e);
                    }

                   }

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

                if (devicename.equals("nodevice")) {
                    try {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Toast.makeText(PrintReportAmigoThermalNew.this, "Message1", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(
                                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent,
                                        REQUEST_ENABLE_BT);
                            } else {
                                ListPairedDevices();
                                Intent connectIntent = new Intent(PrintReportAmigoThermalNew.this,
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
    }
    //DATE CONVERSION

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
                    sharedPreferenceClass.setValue_string("DeviceAddress1",mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }else {
                  //mBlutoothConnectThread.suspend();
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintReportAmigoThermalNew.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintReportAmigoThermalNew.this, "Switch on bluetooth", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            try {
                sendData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(PrintReportAmigoThermalNew.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
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
       // System.runFinalizersOnExit(true);
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

    private int getOfflineCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private int getUploadedCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}



