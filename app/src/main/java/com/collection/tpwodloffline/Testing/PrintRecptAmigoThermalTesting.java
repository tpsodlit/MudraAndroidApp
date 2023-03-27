package com.collection.tpwodloffline.Testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.DeviceList;
import com.collection.tpwodloffline.otp.UnicodeFormatter;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class PrintRecptAmigoThermalTesting extends AppCompatActivity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, Print;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    TextView strPrntMsg;
    String devicename = "nodevice";


    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_amigo_thermal_testing);

        sharedPreferenceClass = new SharedPreferenceClass(PrintRecptAmigoThermalTesting.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);

        mBluetoothAdapter = null;
        //Log.d("DemoApp", "devicename  " + devicename);

        Print = (Button) findViewById(R.id.print);
        mScan = (Button) findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(PrintRecptAmigoThermalTesting.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(PrintRecptAmigoThermalTesting.this,
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

        Print.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                try {
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(PrintRecptAmigoThermalTesting.this, "Connect to printer first", Toast.LENGTH_SHORT).show();
                    } else {
                        sendData();
                    }
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
       // strPrntMsg.setText("Printing");
        try {

            String version = "";
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm = sessiondata.getString("usrID", null);
            String username = sharedPreferenceClass.getValue_string("username");
            String date = CommonMethods.getTodaysDate();

            // Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());
            if (!devicename.equals("nodevice")) {

                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = "";

                            BILL = "              TPWODL   \n"+date;

                            BILL = BILL
                                    + "--------------------------------\n";

                            BILL = BILL
                                    + "Printer tested successfully\n";
                            BILL = BILL
                                    + " By \n"+username+"\n";

                            BILL = BILL
                                    + "--------------------------------\n\n";


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


                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
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

        Button contd = (Button) findViewById(R.id.contd);

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
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintRecptAmigoThermalTesting.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintRecptAmigoThermalTesting.this, "Switch on bluetooth", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PrintRecptAmigoThermalTesting.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
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



