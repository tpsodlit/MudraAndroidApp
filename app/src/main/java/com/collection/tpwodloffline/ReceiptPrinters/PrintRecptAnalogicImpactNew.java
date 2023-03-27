package com.collection.tpwodloffline.ReceiptPrinters;

import static com.collection.tpwodloffline.CommonMethods.convertDateFormatPrint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.NumberToWordConverter;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.otp.DeviceList;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PrintRecptAnalogicImpactNew extends Activity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan;
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
    private String fromActivity = "";
    private String TransID = "";
    private String AccNum = "";
    private String from = "";
    Thread mBlutoothConnectThread;
    private DatabaseAccess databaseAccess = null;
    SharedPreferenceClass sharedPreferenceClass;
    final Context context = this;

    Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
    String doubleHeight = BPImpact.font_Double_Height_On();
    String widthoff = BPImpact.font_Double_Height_Width_Off();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_recpt_amigo_thermal);

        sharedPreferenceClass = new SharedPreferenceClass
                (PrintRecptAnalogicImpactNew.this);
        strPrntMsg = findViewById(R.id.PrntMsg);
        ReprntRcpt = findViewById(R.id.ReprntRcpt);
        strPrntMsg.setText("Scan to connect");

        Button Exit = findViewById(R.id.Exit);
        Exit.setOnClickListener(view -> {
            finish();
        });

        mBluetoothAdapter = null;
        rcptType = "ORIGINAL";
        Bundle PrintBun = getIntent().getExtras();
        AccNum = PrintBun.getString("custID");
        from = PrintBun.getString("from");
        TransID = Objects.requireNonNull(PrintBun.getString("TransID")).trim();
        String type = PrintBun.getString("type");

        if (type.equals("D")) {
            rcptType = "DUPLICATE";
        } else if (type.equals("O")) {
            rcptType = "ORIGINAL";
        }

        mScan = findViewById(R.id.scan);
        mScan.setOnClickListener(mView -> {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this,
                        "Bluetooth Adapter found null",
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

        ReprntRcpt.setOnClickListener(mView -> {
            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    String DeviceAddress = sharedPreferenceClass.
                            getValue_string("DeviceAddress3");
                    if (!DeviceAddress.equals("")) {
                        devicename = DeviceAddress;
                        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(DeviceAddress);
                        mBluetoothConnectProgressDialog = ProgressDialog.
                                show(this,
                                        "Connecting...", mBluetoothDevice.getName(),
                                        true, true);
                        mBlutoothConnectThread = new
                                Thread(this);
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
            String username = sharedPreferenceClass.getValue_string("username");
            String uid = sharedPreferenceClass.getValue_string("un");
            if (!devicename.equals("nodevice")) {
                String paymode, BlPrepTm = "", billType, ReceiptNo;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                String strUpdateSQL_01 = "Select" +
                        " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision, A.section,A.CON_NAME, " +//5
                        " A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                        " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                        " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                        " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                        " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                        " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                        " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG, A.DEL_FLG,A" +
                        ".Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID, A.PHONE_NO," +
                        "BAL_FETCH,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,A.RTGS_DATE, A.MONEY_RECPT_ID," +
                        "A.MONEY_RECPT_DATE,A.DB_TYPE_SERVER,A.SPINNER_NON_ENERGY" +
                        " FROM " +
                        " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and " +
                        "CUST_ID = '" + AccNum + "' AND TRANS_ID='" + TransID + "'";
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {

                    try {
                        BlPrepTm = rs.getString(19);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        paymode = "MR";
                    } else {
                        paymode = "  CASH";
                    }

                    String Mobile = sharedPreferenceClass.getValue_string("mobile");
                    String pay_cnt = rs.getString(47);
                    String collMode = rs.getString(48);
                    if (collMode.equals("ADV")) {
                        billType = "D";
                    } else {
                        billType = CommonMethods.getBillType(pay_cnt);
                    }
                    //ReceiptNo = billType + rs.getString(1) + rs.getString(10);
                    ReceiptNo = billType + rs.getString(1) + uid;
                    try {
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        String BillContents = "";

                        if (!BlPrepTm.contains(":")) {
                            BlPrepTm = BlPrepTm.substring(0, 2) + ":" + BlPrepTm.substring(2, 4)
                                    + ":" + BlPrepTm.substring(4, 6);
                        }
                        BillContents = "\n";
                        BillContents += doubleHeight + String.format("%-16s", "");
                        BillContents += widthoff + String.format("%10s%10s", "", rcptType);
                        BillContents += doubleHeight + String.format("%-24s", "          TPWODL");
                        BillContents += doubleHeight + String.format("%-24s", "      " +
                                ServerLinks.BillName2 + "");
                        BillContents += widthoff + "------------------------" + "\n";

                        BillContents += widthoff + String.format("%-24s",
                                "RECPT DT:   " + convertDateFormatPrint(rs.getString(18),
                                        "DD-MM-YYYY"));
                        BillContents += widthoff + String.format("%-24s", "RECPT TIME: " + BlPrepTm);
                        BillContents += widthoff + String.format("%-24s", "RECEIPT NO:  ");
                        BillContents += widthoff + String.format("%-24s", ReceiptNo);
                        BillContents += widthoff + String.format("%-24s", "TRANSACTION ID:  ");
                        BillContents += widthoff + String.format("%-24s", rs.getString(20));
                        BillContents += widthoff + String.format("%-24s",
                                "DIVN:  " + rs.getString(2));
                        BillContents += widthoff + String.format("%-24s", "CONSUMER NO:  ");
                        BillContents += widthoff + String.format("%-24s", rs.getString(0));

                        if (rs.getString(5).trim().length() <= 19) {
                            BillContents += String.format("%5s%-19s", "NAME:",
                                    rs.getString(5));
                            BillContents += "" + "\n";
                        } else {
                            BillContents += String.format("%5s%-43s", "NAME:",
                                    rs.getString(5));
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(6) + ","
                                + rs.getString(7));
                        if (rs.getString(6).trim().length()
                                + rs.getString(7).trim().length() > 17) {
                            if (strAddr.length() >= 41) {
                                strAddr.setLength(41);
                            }
                            BillContents += String.format("%6s%-42s", "ADDRESS:", strAddr);
                        } else {
                            BillContents += String.format("%6s%-18s", "",
                                    (rs.getString(6))) + ",\n" +
                                    rs.getString(7);
                            BillContents += "" + "\n";
                        }
                        BillContents += widthoff + "------------------------" + "\n";
                        String pmttype = "ENERGY BILL";
                        if (!rs.getString(36).equals("AcctNo")) {
                            pmttype = rs.getString(36);
                        }

                        BillContents += widthoff + String.format("%4s%4s",
                                "PAID AGAINST:" + pmttype, "");
                        BillContents += widthoff + String.format("%4s%4s", "PAYMENT MODE: "
                                + paymode, "");

                        if (rs.getString(23).equals("7")) {

                            BillContents += widthoff + String.format("%13s%10s", "RECEIVED AMT:",
                                    rs.getString(22));
                            BillContents += String.format("%7s%17s", "POS ID:",
                                    rs.getString(39));
                            BillContents += String.format("%9s%15s", "POS DATE:",
                                    rs.getString(27));
                        } else if (rs.getString(23).equals("3")) {

                            BillContents += widthoff + String.format("%13s%11s",
                                    "RECEIVED CHQ:", rs.getString(22));
                            BillContents += String.format("%7s%17s", "CHQ NO:",
                                    rs.getString(24));
                            BillContents += String.format("%9s%15s", "CHQ DATE:",
                                    rs.getString(25));
                        } else if (rs.getString(23).equals("2")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED DD:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "DD NO:",
                                    rs.getString(26));
                            BillContents += String.format("%-10s%14s", "DD DATE:",
                                    rs.getString(27));
                        } else if (rs.getString(23).equals("8")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED NEFT:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "NEFT NO:",
                                    rs.getString(40));
                            BillContents += String.format("%-10s%14s", "NEFT DATE:",
                                    rs.getString(41));

                        } else if (rs.getString(23).equals("9")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED RTGS:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "RTGS NO:",
                                    rs.getString(42));
                            BillContents += String.format("%-10s%14s", "RTGS DATE:",
                                    rs.getString(43));

                        } else if (rs.getString(23).equals("4")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED MR:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "RECEIPT NO:",
                                    rs.getString(44));
                            BillContents += String.format("%-10s%14s", "RECEIPT DATE:",
                                    rs.getString(45));
                        } else {
                            BillContents += widthoff + String.format("%4s%4s",
                                    "RECEIVED CASH:", rs.getString(22));
                        }
                        if (rs.getString(37).trim().length() <= 14 &&
                                !rs.getString(28).equals("0")) {

                            BillContents += widthoff + String.format("%10s%14s",
                                    "BANK NAME:", rs.getString(37));

                        } else if (rs.getString(37).trim().length() > 14 &&
                                !rs.getString(28).equals("0")) {

                            BillContents += String.format("%10s%-38s", "BANK NAME:",
                                    rs.getString(37));
                        }
                        BillContents += doubleHeight + String.format("%4s%4s", "TOTAL PAID:",
                                rs.getString(22));
                        StringBuilder strword = new StringBuilder("AMOUNT RECEIVED(word)\n" +
                                NumberToWordConverter.numberToWord(rs.getInt(22))
                                + " only");

                        BillContents += widthoff + String.format("%-20s", strword);

                        BillContents += widthoff + String.format("%-24s", "SIGNATURE");
                        BillContents += widthoff + String.format("%-24s", "Thanks.");
                        BillContents += widthoff + String.format("%-24s", "RECEIVED BY:" + username);
                        BillContents += widthoff + String.format("%-24s", "MOBILE NO.:" + Mobile);
                        BillContents += widthoff + "------------------------" + "\n";
                        BillContents += widthoff + String.format("%-24s",
                                CommonMethods.SHA1(rs.getString(20) +
                                        convertDateFormatPrint(rs.getString(18),
                                                "DD-MM-YYYY")));
                        BillContents += widthoff + "------------------------" + "\n";
                        BillContents += widthoff + String.format("%-24s", "THIS IS AUTO-GENERATED  ");
                        BillContents += widthoff + String.format("%-24s", "DOCUMENT AND SIGNATURE  ");
                        BillContents += widthoff + String.format("%-24s", "MAY NOT BE REQUIRED     ");
                        BillContents += widthoff + "-" + "\n";
                        BillContents += widthoff + "\n\n";

                        BillContents = BillContents + "\n ";
                        os.write(BillContents.getBytes());

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
                        rs.close();
                        rcptType = "DUPLICATE";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                        Toast.makeText(PrintRecptAnalogicImpactNew.this,
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (from.equals("en")) {
                        printEnergy();
                    } else if (from.equals("nonen")) {
                        printNonEnergy();
                    } else if (from.contentEquals("enOTS")) {
                        printOTSEnergy();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void printOTSEnergy() {
        try {
            String username = sharedPreferenceClass.getValue_string("username");
            String uid = sharedPreferenceClass.getValue_string("un");
            if (!devicename.equals("nodevice")) {
                String paymode, divn = "",
                        BlPrepTm = "",
                        ReceiptNo;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                String strUpdateSQL_01 = "Select" +
                        " A.CONS_ACC, CUST_ID, A.Division, A.Subdivision," + //3
                        " A.section,A.CON_NAME, " +//5
                        " A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                        " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                        " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                        " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                        " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                        " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                        " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG, A.DEL_FLG, " + //33
                        " A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID, A.PHONE_NO," + //39
                        " A.BAL_FETCH,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,A.RTGS_DATE, " + //44
                        " A.MONEY_RECPT_ID, " + //45
                        " A.MONEY_RECPT_DATE,A.DB_TYPE_SERVER,A.SPINNER_NON_ENERGY, " + //48
                        " A.OTSReferenceNo, A.InstallmentNo, A.totalInstallment " + //51
                        " FROM " +
                        " OTSConsumerDataUpload A,mst_bank b WHERE a.bank_id=b.bank_id and " +
                        " CUST_ID = '" + AccNum + "' AND TRANS_ID='" + TransID + "'";
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {

                    try {
                        BlPrepTm = rs.getString(19);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                        paymode = "MR";
                    } else {
                        paymode = "  CASH";
                    }
                    divn = rs.getString(2);
                    if (divn == null)
                        divn = "";

                    String Mobile = sharedPreferenceClass.getValue_string("mobile");
                    ReceiptNo = new DatabaseAccess().receiptNoOTS(context, AccNum, TransID);
                    try {
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        String BillContents = "";

                        if (!BlPrepTm.contains(":")) {
                            BlPrepTm = BlPrepTm.substring(0, 2) + ":"
                                    + BlPrepTm.substring(2, 4)
                                    + ":" + BlPrepTm.substring(4, 6);
                        }
                        BillContents = "\n";
                        BillContents += doubleHeight + String.format("%-16s", "");
                        BillContents += widthoff + String.format("%10s%10s", "", rcptType);
                        BillContents += doubleHeight + String.format("%-24s", "          TPWODL");
                        BillContents += doubleHeight + String.format("%-24s", "      " +
                                ServerLinks.BillName2 + "");
                        BillContents += widthoff + "------------------------" + "\n";

                        BillContents += widthoff + String.format("%-24s",
                                "RECPT DT:   " + convertDateFormatPrint(rs.getString(18),
                                        "DD-MM-YYYY"));
                        BillContents += widthoff + String.format("%-24s", "RECPT TIME: " + BlPrepTm);
                        BillContents += widthoff + String.format("%-24s", "RECEIPT NO:  ");
                        BillContents += widthoff + String.format("%-24s", ReceiptNo);
                        BillContents += widthoff + String.format("%-24s", "TRANSACTION ID:  ");
                        BillContents += widthoff + String.format("%-24s", rs.getString(20));
                        BillContents += widthoff + String.format("%-24s", "DIVN:  " + divn);
                        BillContents += widthoff + String.format("%-24s", "CONSUMER NO:  ");
                        BillContents += widthoff + String.format("%-24s", rs.getString(0));

                        BillContents += widthoff + String.format("%-24s", "OTS INST. NO.: ");
                        BillContents += widthoff + String.format("%-24s", rs.getString(50));

                        if (rs.getString(5).trim().length() <= 19) {
                            BillContents += String.format("%5s%-19s", "NAME:",
                                    rs.getString(5));
                            BillContents += "" + "\n";
                        } else {
                            BillContents += String.format("%5s%-43s", "NAME:",
                                    rs.getString(5));
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(6));
                        if (rs.getString(6).trim().length() > 17) {
                            if (strAddr.length() >= 41) {
                                strAddr.setLength(41);
                            }
                            BillContents += String.format("%6s%-42s", "ADDRESS:", strAddr);
                        } else {
                            BillContents += String.format("%6s%-18s", "",
                                    (rs.getString(6))) + ",\n" +
                                    rs.getString(7);
                            BillContents += "" + "\n";
                        }
                        BillContents += widthoff + "------------------------" + "\n";

                        BillContents += widthoff + String.format("%4s%4s",
                                "PAID AGAINST:   " + rs.getString(48), "");
                        BillContents += widthoff + String.format("%4s%4s", "PAYMENT MODE: "
                                + paymode, "");

                        if (rs.getString(23).equals("7")) {

                            BillContents += widthoff + String.format("%13s%10s", "RECEIVED AMT:",
                                    rs.getString(22));
                            BillContents += String.format("%7s%17s", "POS ID:",
                                    rs.getString(39));
                            BillContents += String.format("%9s%15s", "POS DATE:",
                                    rs.getString(27));
                        } else if (rs.getString(23).equals("3")) {

                            BillContents += widthoff + String.format("%13s%11s",
                                    "RECEIVED CHQ:", rs.getString(22));
                            BillContents += String.format("%7s%17s", "CHQ NO:",
                                    rs.getString(24));
                            BillContents += String.format("%9s%15s", "CHQ DATE:",
                                    rs.getString(25));
                        } else if (rs.getString(23).equals("2")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED DD:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "DD NO:",
                                    rs.getString(26));
                            BillContents += String.format("%-10s%14s", "DD DATE:",
                                    rs.getString(27));
                        } else if (rs.getString(23).equals("8")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED NEFT:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "NEFT NO:",
                                    rs.getString(40));
                            BillContents += String.format("%-10s%14s", "NEFT DATE:",
                                    rs.getString(41));

                        } else if (rs.getString(23).equals("9")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED RTGS:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "RTGS NO:",
                                    rs.getString(42));
                            BillContents += String.format("%-10s%14s", "RTGS DATE:",
                                    rs.getString(43));

                        } else if (rs.getString(23).equals("4")) {

                            BillContents += widthoff + String.format("%12s%-12s",
                                    "RECEIVED MR:", rs.getString(22));
                            BillContents += String.format("%-10s%14s", "RECEIPT NO:",
                                    rs.getString(44));
                            BillContents += String.format("%-10s%14s", "RECEIPT DATE:",
                                    rs.getString(45));
                        } else {
                            BillContents += widthoff + String.format("%4s%4s",
                                    "RECEIVED CASH:", rs.getString(22));
                        }
                        if (rs.getString(37).trim().length() <= 14 &&
                                !rs.getString(28).equals("0")) {

                            BillContents += widthoff + String.format("%10s%14s",
                                    "BANK NAME:", rs.getString(37));

                        } else if (rs.getString(37).trim().length() > 14 &&
                                !rs.getString(28).equals("0")) {

                            BillContents += String.format("%10s%-38s", "BANK NAME:",
                                    rs.getString(37));
                        }
                        BillContents += doubleHeight + String.format("%4s%4s", "TOTAL PAID:",
                                rs.getString(22));
                        StringBuilder strword = new StringBuilder("AMOUNT RECEIVED(word)\n" +
                                NumberToWordConverter.numberToWord(rs.getInt(22))
                                + " only");

                        BillContents += widthoff + String.format("%-20s", strword);

                        BillContents += widthoff + String.format("%-24s", "SIGNATURE");
                        BillContents += widthoff + String.format("%-24s", "Thanks.");
                        BillContents += widthoff + String.format("%-24s", "RECEIVED BY:" + username);
                        BillContents += widthoff + String.format("%-24s", "MOBILE NO.:" + Mobile);
                        BillContents += widthoff + "------------------------" + "\n";
                        BillContents += widthoff + String.format("%-24s",
                                CommonMethods.SHA1(rs.getString(20) +
                                        convertDateFormatPrint(rs.getString(18),
                                                "DD-MM-YYYY")));
                        BillContents += widthoff + "------------------------" + "\n";
                        BillContents += widthoff + String.format("%-24s", "THIS IS AUTO-GENERATED  ");
                        BillContents += widthoff + String.format("%-24s", "DOCUMENT AND SIGNATURE  ");
                        BillContents += widthoff + String.format("%-24s", "MAY NOT BE REQUIRED     ");
                        BillContents += widthoff + "-" + "\n";
                        BillContents += widthoff + "\n\n";

                        BillContents = BillContents + "\n ";
                        os.write(BillContents.getBytes());

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
                        rs.close();
                        rcptType = "DUPLICATE";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                        Toast.makeText(PrintRecptAnalogicImpactNew.this,
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (from.equals("en")) {
                        printEnergy();
                    } else if (from.equals("nonen")) {
                        printNonEnergy();
                    } else if (from.contentEquals("enOTS")) {
                        printOTSEnergy();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    void printNonEnergy() {
        try {
            String username = sharedPreferenceClass.getValue_string("username");
            String uid = sharedPreferenceClass.getValue_string("un");
            if (!devicename.equals("nodevice")) {

                String pmtType = "NON-ENERGY", payMode = "  CASH",
                        BlPrepTm = "", billType = "S", receiptNo,
                        printData;

                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                String strUpdateSQL_01 = "Select" +
                        " USER_ID,COMPANY_CODE,SCNO,REF_MODULE, REF_REG_NO,CUST_ID,DIVISION," +
                        "SUBDIVISION," + //7
                        "SECTION,CON_NAME,CON_ADD1,AMOUNT, DEMAND_DATE,MOBILE_NO,EMAIL," +
                        "RECPT_DATE,RECPT_TIME," + //16
                        "MR_No,MACHINE_NO,TOT_PAID,PAY_MODE, RECPT_FLG,OPERATOR_ID,OPERATOR_NAME," +
                        "SEND_FLG," + //24
                        "COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE, BAL_FETCH,OPERATION_TYPE,REMARKS," +
                        "LATTITUDE," + //32
                        "LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE" + //39
                        " FROM " +
                        " COLL_NEN_DATA WHERE COLL_FLG = '" + "1" + "' " +
                        " AND RECPT_FLG='" + "1" + "' AND TRANS_ID='" + TransID + "'";
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    try {
                        BlPrepTm = rs.getString(16);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String Mobile = sharedPreferenceClass.getValue_string("mobile");
                    String ref_module = rs.getString(3);
                    String rec_num = rs.getString(4);

                    receiptNo = billType + ref_module + rec_num;

                    try {
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        if (!BlPrepTm.contains(":")) {
                            BlPrepTm = BlPrepTm.substring(0, 2) + ":" +
                                    BlPrepTm.substring(2, 4) + ":" + BlPrepTm.substring(4, 6);
                        }
                        printData = "\n";
                        printData += doubleHeight + String.format("%-16s", "");
                        printData += widthoff + String.format("%10s%10s", "", rcptType);
                        printData += doubleHeight + String.format("%-24s", "          TPWODL");
                        printData += doubleHeight + String.format("%-24s", "      " +
                                ServerLinks.BillName2 + "");
                        printData += widthoff + "------------------------" + "\n";

                        printData += widthoff + String.format("%-24s",
                                "RECPT DT:   " + rs.getString(15));
                        printData += widthoff + String.format("%-24s", "RECPT TIME: " + BlPrepTm);
                        printData += widthoff + String.format("%-24s", "RECEIPT NO:  ");
                        printData += widthoff + String.format("%-24s", receiptNo);
                        printData += widthoff + String.format("%-24s", "REF NO: ");
                        printData += widthoff + String.format("%-24s", rec_num);
                        printData += widthoff + String.format("%-24s", "TRANSACTION ID:  ");
                        printData += widthoff + String.format("%-24s", rs.getString(26));
                        printData += widthoff + String.format("%-24s", "SECTION:  ");
                        printData += widthoff + String.format("%-24s", rs.getString(8));
                        printData += widthoff + String.format("%-24s", "CONSUMER NO:  ");
                        printData += widthoff + String.format("%-24s", rs.getString(2));

                        if (rs.getString(5).trim().length() <= 19) {
                            printData += String.format("%5s%-19s", "NAME:",
                                    rs.getString(9));
                            printData += "" + "\n";
                        } else {
                            printData += String.format("%5s%-43s", "NAME:",
                                    rs.getString(9));
                        }

                        printData += String.format("%6s%-42s", "ADDRESS:",
                                rs.getString(10));

                        printData += widthoff + "------------------------" + "\n";
                        printData += widthoff + String.format("%4s%4s",
                                "PAID AGAINST:" + pmtType, "");
                        printData += widthoff + String.format("%5s%-19s", "SOURCE:",
                                rs.getString(3));

                        printData += widthoff + String.format("%4s%4s",
                                "PAYMENT MODE: " + payMode, "");

                        printData += doubleHeight + String.format("%4s%4s", "TOTAL PAID:",
                                rs.getString(19));
                        StringBuilder strword = new StringBuilder("AMOUNT RECEIVED(word)\n" +
                                NumberToWordConverter.numberToWord(rs.getInt(19)) +
                                " only");

                        printData += widthoff + String.format("%-20s", strword);

                        printData += widthoff + String.format("%-24s", "SIGNATURE");
                        printData += widthoff + String.format("%-24s", "Thanks.");

                        if (rs.getString(3).equals("FRM")) {

                            printData += widthoff + String.format("%-24s", "RECEIVED BY:" + uid);
                        } else {
                            printData += widthoff + String.format("%-24s", "RECEIVED BY:" + username);
                            printData += widthoff + String.format("%-24s", "MOBILE NO.:" + Mobile);
                        }
                        printData += widthoff + "------------------------" + "\n";
                        printData += widthoff + String.format("%-24s",
                                CommonMethods.SHA1(rs.getString(4) +
                                        (rs.getString(28))));
                        printData += widthoff + "------------------------" + "\n";
                        printData += widthoff + String.format("%-24s", "THIS IS AUTO-GENERATED  ");
                        printData += widthoff + String.format("%-24s", "DOCUMENT AND SIGNATURE  ");
                        printData += widthoff + String.format("%-24s", "MAY NOT BE REQUIRED     ");
                        printData += widthoff + "-" + "\n";
                        printData += widthoff + "\n\n";

                        printData = printData + "\n ";
                        os.write(printData.getBytes());
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
                        rs.close();
                        rcptType = "DUPLICATE";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                        Toast.makeText(PrintRecptAnalogicImpactNew.this,
                                "Bluetooth Adapter founds null", Toast.LENGTH_SHORT).show();
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    if (from.equals("en")) {
                        printEnergy();
                    } else if (from.equals("nonen")) {
                        printNonEnergy();
                    } else if (from.contentEquals("enOTS")) {
                        printOTSEnergy();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button contd = findViewById(R.id.contd);
        contd.setOnClickListener(view -> {
            if (fromActivity.equalsIgnoreCase("non-account")) {
                Intent reports2 = new Intent(getApplicationContext(), NoNAccountActivity.class);
                startActivity(reports2);
                finish();
            } else {
                Intent reports2 = new Intent(getApplicationContext(), AcCollection.class);
                startActivity(reports2);
                finish();
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
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    devicename = mDeviceAddress;
                    sharedPreferenceClass.setValue_string("DeviceAddress3",
                            mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(),
                            true, true);
                    mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrintRecptAnalogicImpactNew.this,
                            "Switch on bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            eConnectException.printStackTrace();
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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            try {
                if (from.equals("en")) {
                    printEnergy();
                } else if (from.equals("nonen")) {
                    printNonEnergy();
                } else if (from.contentEquals("enOTS")) {
                    printOTSEnergy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(PrintRecptAnalogicImpactNew.this,
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