package com.collection.tpwodloffline.ReceiptPrinters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.otp.DeviceList;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class PrintRecptAmigoThermalNew extends Activity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, printRcpt, btnExit;
    BluetoothAdapter mBluetoothAdapter;
    private final UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    TextView strPrntMsg;
    String devicename = "nodevice";
    private String rcptType = "";
    private final String fromActivity = "";
    private String TransID = "";
    private String AccNum = "";
    private String from = "";
    Thread mBlutoothConnectThread;
    SharedPreferenceClass sharedPreferenceClass;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_amigo_thermal);

        sharedPreferenceClass = new
                SharedPreferenceClass(this);

        mBluetoothAdapter = null;
        rcptType = "ORIGINAL";

        strPrntMsg = findViewById(R.id.PrntMsg);
        printRcpt = findViewById(R.id.ReprntRcpt);
        strPrntMsg.setText("Scan to connect");
        mScan = findViewById(R.id.scan);
        btnExit = findViewById(R.id.Exit);

        Bundle bundle = getIntent().getExtras();
        from = bundle.getString("from");
        AccNum = bundle.getString("custID");
        TransID = bundle.getString("TransID").trim();
        String type = bundle.getString("type");

        if (type.equals("D")) {
            rcptType = "DUPLICATE";
        } else if (type.equals("O")) {
            rcptType = "ORIGINAL";
        }

        btnExit.setOnClickListener(view -> finish());

        mScan.setOnClickListener(mView -> {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this,
                        "Bluetooth Adapter founds null",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(this,
                            DeviceList.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);
                }
            }
        });

        printRcpt.setOnClickListener(mView -> {
            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    String DeviceAddress = sharedPreferenceClass.
                            getValue_string("DeviceAddress1");
                    if (!DeviceAddress.equals("")) {
                        devicename = DeviceAddress;
                        mBluetoothDevice = mBluetoothAdapter.
                                getRemoteDevice(DeviceAddress);
                        mBluetoothConnectProgressDialog =
                                ProgressDialog.show(this,
                                        "Connecting...",
                                        mBluetoothDevice.getName(),
                                        true, true);
                        mBlutoothConnectThread = new Thread(this);
                        mBlutoothConnectThread.start();
                    } else {
                        Toast.makeText(context, "Connect to printer",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    void printEnergy() {
        try {
            if (!devicename.equals("nodevice")) {
                OutputStream os = mBluetoothSocket.getOutputStream();
                String bill;
                if (from.contentEquals("enOTS")) {
                    bill = DatabaseAccess.getInstance(this).
                            printOTSEnergyData(this, AccNum, TransID,
                                    rcptType, "");
                } else {
                    bill = DatabaseAccess.getInstance(this).
                            printEnergyData(this, AccNum, TransID,
                                    rcptType, "");
                }

                os.write(bill.getBytes());

                int gs = 29;
                os.write(intToByteArray(gs));
                int h = 104;
                os.write(intToByteArray(h));
                int n = 162;
                os.write(intToByteArray(n));

                int gs_width = 29;
                os.write(intToByteArray(gs_width));
                int w = 119;
                os.write(intToByteArray(w));
                int n_width = 2;
                os.write(intToByteArray(n_width));
                rcptType = "DUPLICATE";
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

        Button ReprntBl = findViewById(R.id.ReprntRcpt);
        ReprntBl.setOnClickListener(view -> {

            int rows = DatabaseAccess.getInstance(this).
                    updateCollFlagInPrint(this, AccNum, TransID);
            Log.d("COLL_FLAG", "Rows updated ::: " + rows);

            if (devicename.equals("nodevice")) {
                try {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(this,
                                "Bluetooth Adapter founds null",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(
                                    this,
                                    DeviceList.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (from.equals("nonen")) {
                        printNonEnergy();
                    } else {
                        printEnergy();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void printNonEnergy() {
        try {
            if (!devicename.equals("nodevice")) {
                OutputStream os = mBluetoothSocket.getOutputStream();
                String bill = DatabaseAccess.getInstance(this).
                        printNonEnergyData(this, TransID, "S",
                                rcptType, "");
                os.write(bill.getBytes());

                int gs = 29;
                os.write(intToByteArray(gs));
                int h = 104;
                os.write(intToByteArray(h));
                int n = 162;
                os.write(intToByteArray(n));

                int gs_width = 29;
                os.write(intToByteArray(gs_width));
                int w = 119;
                os.write(intToByteArray(w));
                int n_width = 2;
                os.write(intToByteArray(n_width));
                rcptType = "DUPLICATE";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        strPrntMsg.setText("Data Sent to Bluetooth Printer");

        Button ReprntBl = findViewById(R.id.ReprntRcpt);
        ReprntBl.setOnClickListener(view -> {

            int rows = DatabaseAccess.getInstance(this).
                    updateCollFlagInPrint(this, AccNum, TransID);
            Log.d("COLL_FLAG", "Rows updated ::: " + rows);

            if (devicename.equals("nodevice")) {
                try {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Toast.makeText(this,
                                "Bluetooth Adapter not found",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent,
                                    REQUEST_ENABLE_BT);
                        } else {
                            ListPairedDevices();
                            Intent connectIntent = new Intent(
                                    this,
                                    DeviceList.class);
                            startActivityForResult(connectIntent,
                                    REQUEST_CONNECT_DEVICE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (from.equals("nonen")) {
                        printNonEnergy();
                    } else {
                        printEnergy();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

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
                    devicename = mDeviceAddress;
                    sharedPreferenceClass.setValue_string("DeviceAddress1", mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrintRecptAmigoThermalNew.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintRecptAmigoThermalNew.this,
                            "Switch on bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            closeSocket(mBluetoothSocket);
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
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            try {
                if (from.equals("nonen")) {
                    printNonEnergy();
                } else {
                    printEnergy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(PrintRecptAmigoThermalNew.this,
                    "Device Connected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();
        return b[3];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}