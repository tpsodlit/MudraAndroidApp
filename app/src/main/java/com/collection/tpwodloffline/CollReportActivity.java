package com.collection.tpwodloffline;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.activity.DetailsReportActivity;

import java.io.InputStream;
import java.io.OutputStream;

public class CollReportActivity extends AppCompatActivity {
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
    final Context context = this;
    // TextView myLabel;
    static TextView strPrntMsg;
    private String TransID="";
    private static EditText strTxtconsno;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String Usernm ="";
    private Cursor rs=null;
    private DatabaseAccess databaseAccess=null;
    private int sbmflg = 0;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button SumRep = (Button) findViewById(R.id.SumRep);
        Button DetRep = (Button) findViewById(R.id.DetRep);
        Button Backbtn = (Button) findViewById(R.id.Backbtn);
        Button ConRep = (Button) findViewById(R.id.ConRep);
        Button DailyRep = (Button) findViewById(R.id.DailyRep);
        Button norecpRep = (Button) findViewById(R.id.norecp);

        strTxtconsno=(EditText) findViewById(R.id.consno);
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm =sessiondata.getString("userID", null);
        //to get SBM print
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";


        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);

            System.out.println("ddff"+sbmflg);
        }

        //   Log.d("DemoApp", "strUpdateSQL_01  01");
        rs.close();
        databaseAccess.close();
        SumRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent accol = new Intent(getApplicationContext(), AcCollection.class);
                //startActivity(accol);
                // finish();
            }
        });
        DetRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CollReportActivity.this, DetailsReportActivity.class);
                intent.putExtra("ReportTyp","D");
                intent.putExtra("CustID","X");
                intent.putExtra("screenName","Details Report");
                startActivity(intent);

            }
        });
        SumRep.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent reports =null;


                Intent intent=new Intent(CollReportActivity.this,DetailsReportActivity.class);
                intent.putExtra("ReportTyp","S");
                intent.putExtra("CustID","X");
                intent.putExtra("screenName","Summary Report");
                startActivity(intent);


            }
        });
        ConRep.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                String strconsno = "";
                strconsno = strTxtconsno.getText().toString();

                if (strconsno.length()>=1){
                    Intent intent=new Intent(CollReportActivity.this,DetailsReportActivity.class);

                    intent.putExtra("ReportTyp","C");
                    intent.putExtra("CustID",strconsno);
                    intent.putExtra("screenName","Consumer Report");
                    startActivity(intent);

                }
                else {
                    Toast.makeText(CollReportActivity.this,"Please enter consumer number",Toast.LENGTH_SHORT).show();
                }




            }
        });
        DailyRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CollReportActivity.this,DetailsReportActivity.class);
                intent.putExtra("ReportTyp","U");
                intent.putExtra("CustID","X");
                intent.putExtra("screenName","Daily Report");
                startActivity(intent);

            }
        });
        norecpRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent reports =null;
                if(sbmflg==1){
                    //    reports = new Intent(getApplicationContext(), ReportPrintAnalogicSBM.class);
                }else if(sbmflg==2){
                    reports = new Intent(getApplicationContext(), ReportPrintAnalogicThermal.class);
                }else if(sbmflg==3){
                    reports = new Intent(getApplicationContext(), ReportPrintEpsonThermal.class);
                }else if(sbmflg==4){
                    reports = new Intent(getApplicationContext(), ReportPrintSoftlandImpact.class);
                }else if(sbmflg==5){
                    reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                }else if(sbmflg==6){
                    reports = new Intent(getApplicationContext(), ReportPrintAnalogicImpact.class);
                }else if(sbmflg==7){
                    reports = new Intent(getApplicationContext(), ReportPrintPhiThermal.class);
                }else if(sbmflg==8){
                    reports = new Intent(getApplicationContext(), ReportPrintAmigoThermal.class);
                }else {
                    reports = new Intent(getApplicationContext(), ReportPrint.class);
                }

                Bundle Report = new Bundle();
                Report.putString("ReportTyp", "N");
                Report.putString("CustID", "X");
                reports.putExtras(Report);
                startActivity(reports);
                //finish();
            }
        });
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                Intent reports = new Intent(getApplicationContext(), ColDashboard.class);
                startActivity(reports);*/

                onBackPressed();
                //finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
