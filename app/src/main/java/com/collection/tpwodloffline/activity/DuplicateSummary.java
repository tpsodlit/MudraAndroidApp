package com.collection.tpwodloffline.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAmigoThermalNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicImpactNew;
import com.collection.tpwodloffline.ReceiptPrinters.PrintRecptAnalogicThermalNew;

public class DuplicateSummary extends AppCompatActivity {
    private static RadioButton rbbill;
    private DatabaseAccess databaseAccess = null;
    final Context context = this;
    private String transID = "";
    private String AmountPay = "";
    private String EntryNum = "";
    private EditText transIDtxt = null;
    private String cust_id = "";
    private String Usernm = "";
    private Cursor rs = null;
    private int sbmflg = 0;
    private String from="";

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_summary);
        Toolbar toolbard = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rbbill = (RadioButton) findViewById(R.id.bill);

        EntryNum = "";
        Bundle extrasvalcol = getIntent().getExtras();
        String SelChoice = extrasvalcol.getString("SelChoice");
        EntryNum = extrasvalcol.getString("EntryNum");
        from = extrasvalcol.getString("from");

        Log.d("Duplicate Summary Bundle",SelChoice);
        Log.d("Duplicate Summary Bundle",EntryNum);
        Log.d("Duplicate Summary Bundle",from);
//////////////////////
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm = sessiondata.getString("userID", null);
        //to get SBM print
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";
        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);
        }
        //   Log.d("DemoApp", "strUpdateSQL_01  01");
        rs.close();
        databaseAccess.close();
        ///////////////////////////
        Log.d("DemoApp", "sbmflg  01" + sbmflg);
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        String strSelectSQL_02="";

        if (from.contentEquals("enOTS")) {
            getSupportActionBar().setTitle("OTS Duplicate Summary");
            strSelectSQL_02 = "Select TRANS_ID, TOT_PAID, CUST_ID, " +
                    "InstallmentNo FROM OTSConsumerDataUpload " +
                    "WHERE CONS_ACC='" + EntryNum + "' and " +
                    "RECPT_FLG = 1  order by trans_id desc";
        } else {
            getSupportActionBar().setTitle("Duplicate Summary");
            strSelectSQL_02 = "Select TRANS_ID,TOT_PAID,CUST_ID, '' FROM " +
                    "COLL_SBM_DATA " +
                    "WHERE CONS_ACC='" + EntryNum + "' and " +
                    "RECPT_FLG = 1  order by trans_id desc";
        }

        /*  old code before ezetap and OTS
        strSelectSQL_02 = "Select A.TRANS_ID,TOT_PAID,CUST_ID FROM COLL_SBM_DATA A WHERE CONS_ACC='" + EntryNum + "' and RECPT_FLG=1  order by trans_id desc";
        //String strSelectSQL_02 = "Select A.TRANS_ID,TOT_PAID,CUST_ID FROM COLL_SBM_DATA A WHERE CONS_ACC='" + EntryNum + "' and RECPT_FLG=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)  order by trans_id desc";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);  */
        Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);

        LinearLayout layout = (LinearLayout) findViewById(R.id.rootContainer);
        RadioGroup ll = new RadioGroup(this);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.addView(ll, p);
        while (rs1.moveToNext()) {
            transID = rs1.getString(0);
            AmountPay = rs1.getString(1);
            cust_id = rs1.getString(2);
            String instNo = rs1.getString(3);
            RadioButton rdbtn = new RadioButton(this);
            if (from.contentEquals("enOTS")) {
                rdbtn.setText("Trans Id :  " + transID + "\nCons No : "
                        + EntryNum + "\n Amount :  " + AmountPay +
                        "\nInstallment No : " + instNo +"\n");
            } else {
                rdbtn.setText("Trans Id:  " + transID + "\nCons No: "
                        + EntryNum + "\n Amount:  " + AmountPay + "\n");
            }


            //old code before exetap and OTS
            // rdbtn.setText("Trans Id:  " + transID + "\nCons No: " + EntryNum + "\n Amount:  " + AmountPay);
            rdbtn.setTextSize(17);
            rdbtn.setOnClickListener(mThisButtonListener);
            ll.addView(rdbtn, p);
        }
        databaseAccess.close();
        toolbard.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CollectionDashBoard.class));
                finish();
            }
        });

    }

    private String TransID = "";
    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            String s = ((RadioButton) v).getText().toString();
            // Toast.makeText(DuplicateSummary.this, "Hello from 2!" + s, Toast.LENGTH_LONG).show();
            String[] TransInfo = s.split("[:]");
            TransID = TransInfo[1].replace("\nCons No", "");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Do You Want to print");
            alertDialogBuilder.setMessage("Tap Print if yes" + "\n" + " Tap Cancel to re-select ")
                    .setCancelable(false)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Print", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  ReceiptGen.this.finish();
                            ////////////////////Printer Selection/////////////////////
                            // Intent RecptPrintIntent=null;
                         /*   if(sbmflg==1){
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicSBM.class);
                            }else if(sbmflg==2){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermal.class);
                            }else if(sbmflg==3){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptEpsonThermal.class);
                            }else if(sbmflg==4){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptSoftlandImpact.class);
                            }else if(sbmflg==5){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoImpact.class);
                            }else if(sbmflg==6){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpact.class);
                            }else if(sbmflg==7){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptPhiThermal.class);
                            }else if(sbmflg==8){
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermal.class);
                            }else {
                                RecptPrintIntent = new Intent(getApplicationContext(), PrintRecpt.class);
                            }*/
                            ////////////////////////
                            // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecpt.class);
                            //Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);

                            /*  Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                           // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                            Bundle PrintBun = new Bundle();
                            PrintBun.putString("custID", cust_id);
                            PrintBun.putString("TransID", TransID.trim());
                            PrintBun.putString("type", "D");
                            RecptPrintIntent.putExtras(PrintBun);
                            startActivity(RecptPrintIntent);
                            finish();*/
Log.d("duplicatesmmary-print",String.valueOf(sbmflg));
                            if (sbmflg == 8) {
                                //Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                                 Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", from);
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            } else if (sbmflg == 6) {
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicImpactNew.class);
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAmigoThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", from);
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            } else if (sbmflg ==2) {
                                Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                                // Intent RecptPrintIntent = new Intent(getApplicationContext(), PrintRecptAnalogicThermalNew.class);
                                Bundle PrintBun = new Bundle();
                                PrintBun.putString("custID", cust_id);
                                PrintBun.putString("TransID", TransID.trim());
                                PrintBun.putString("type", "D");
                                PrintBun.putString("from", from);
                                RecptPrintIntent.putExtras(PrintBun);
                                startActivity(RecptPrintIntent);
                                finish();
                            }
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    };
}
