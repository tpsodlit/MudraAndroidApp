package com.collection.tpwodloffline.Testing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.NoNAccountActivity;
import com.collection.tpwodloffline.R;
import com.aem.api.AEMPrinter;
import com.aem.api.AEMScrybeDevice;
import com.aem.api.IAemScrybe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class PrintRecptPhiThermalTesting extends AppCompatActivity {
    private AEMScrybeDevice m_Aem;
    private String BlutoothPrinter;
    private  boolean connectPrinterBool;
    private AEMPrinter aemPrinter;
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
    private DatabaseAccess databaseAccess=null;
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
        setContentView(R.layout.activity_print_recpt_phi_thermal);
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
      /*  Bundle PrintBun = getIntent().getExtras();
        AccNum = PrintBun.getString("custID");
        TransID= PrintBun.getString("TransID");
        fromActivity=PrintBun.getString("from");*/
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
        m_Aem = new AEMScrybeDevice(new IAemScrybe() {
            @Override
            public void onDiscoveryComplete(ArrayList<String> arrayList) {


            }
        });
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        try{
            sendData();
        }catch (Exception e){}

        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntRcpt);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //rcptType="DUPLICATE";//
             /*   databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET COLL_FLG=2";
                strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + AccNum + "' AND TRANS_ID='" + TransID + "'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
*/

                try {
                    sendData();
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
                    mmDeviceAdr = device.getName();
                    // mmDeviceAdr=device.getAddress();
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
            BlutoothPrinter = address;
            try {
                connectPrinterBool = m_Aem.connectToPrinter(BlutoothPrinter);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "catch error", Toast.LENGTH_SHORT).show();
            }
            if (connectPrinterBool) {
                aemPrinter = m_Aem.getAemPrinter();
                Toast.makeText(getApplicationContext(), "Printer connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), " error while connecting", Toast.LENGTH_SHORT).show();
            }

            String billprint = "";
            try {

                String version = "";
                SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                SharedPreferences.Editor editor = sessiondata.edit();
                String Usernm = sessiondata.getString("UserName", null); // getting String
//                Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());

                String filldata = "";
                String paymode = "";

                String BlPrepTm = "";
                String Billformat = "PrePrinted";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");

                //getting user name
                ////

                 //while loop close
                aemPrinter.print(billprint);
                aemPrinter.setFontNormal();
                aemPrinter.setLeftAlign();
                billprint = "";
                aemPrinter.setTextDoubleHeight();
                billprint += leftAppend1("TOTAL PAID:", ".00", 32)+"\n";
                aemPrinter.print(billprint);
                aemPrinter.setFontNormal();
                aemPrinter.setLeftAlign();
                billprint = "";

                billprint += leftAppend1(" ", "", 32)+"\n";
                billprint += leftAppend1("SIGNATURE", "", 32)+"\n";
                billprint += leftAppend1("Thanks.", "", 32)+"\n";
                billprint += leftAppend1(" ", "", 32)+"\n";
                billprint += leftAppend1("RECEIVED BY:", "sss", 32)+"\n";
                billprint += "------------------------" + "\n";
                billprint += leftAppend1("THIS IS AUTO-GENERATED  ", "", 32)+"\n";
                billprint += leftAppend1("DOCUMENT AND SIGNATURE  ", "", 32)+"\n";
                billprint += leftAppend1("MAY NOT BE REQUIRED     ", "", 32)+"\n";
                billprint += leftAppend1("   ", "", 32)+"\n";
                billprint+="\n\n";
                aemPrinter.print(billprint);
                aemPrinter.setFontNormal();

                // printer.disonnected();
                try {
                    try {
                        m_Aem.disConnectPrinter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                } catch (Exception printerExceptions) {
                    printerExceptions.printStackTrace();
                    Log.e("innn", "createPrintData: " + printerExceptions.getMessage());
                }
            } catch (Exception printerExceptions) {
                printerExceptions.printStackTrace();
                Log.e("innn", "createPrintData: "+printerExceptions.getMessage() );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        strPrntMsg.setText("Data Sent to Bluetooth Printer");

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
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }




}
