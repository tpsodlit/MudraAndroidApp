package com.collection.tpwodloffline;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.collection.tpwodloffline.utils.SharedPreferenceClass;

public class SetPrinterTypeActivity extends AppCompatActivity {
    private   DatabaseAccess databaseAccess=null;
    final Context context = this;
    private RadioGroup ll;
    private static RadioButton rdbtn;
    private static String strprintid="";
    private boolean stsval=false;
    private TextView strprintername=null;
    private TextView strprintername1=null;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_printer_type);

        sharedPreferenceClass = new SharedPreferenceClass(SetPrinterTypeActivity.this);

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strSelectSQL_03 = "UPDATE MST_PRINTERTYPE SET PRINTER_STS=0 WHERE PRINTER_VAL in ('7','9','4','3','1','0')";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_03);
        DatabaseAccess.database.execSQL(strSelectSQL_03);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        Button btnprinterset = (Button) findViewById(R.id.printerset);
        Button btnback = (Button) findViewById(R.id.back);
        strprintername=(TextView) findViewById(R.id.printername);
        strprintername1=(TextView) findViewById(R.id.printername1);
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strSelectSQL_01 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG "+
                "FROM MST_PRINTERTYPE WHERE PRINTER_STS=1 AND PRINTERSET_FLG=1 ORDER BY ID";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        String printtype = "";
        while (rs.moveToNext()) {
            printtype= rs.getString(0);
        }
        databaseAccess.close();
        strprintername.setText("Default Printer - " + printtype);
        strprintername1.setText("Please select default printer for print");
        databaseAccess.open();
        String strSelectSQL_02 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG "+
                "FROM MST_PRINTERTYPE WHERE PRINTER_STS=1 AND PRINTERSET_FLG<>1 ORDER BY ID";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);

        LinearLayout layout = (LinearLayout) findViewById(R.id.rootContainer);
        ll = new RadioGroup(this);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.addView(ll, p);

        String printername="";
        String printerid="";
        while (rs1.moveToNext()) {
            printerid= rs1.getString(1);
            printername = rs1.getString(0);
            rdbtn = new RadioButton(this);
            rdbtn.setText("ID:" + printerid + ":NAME:  " + printername);
            rdbtn.setTextSize(15);


            //   rdbtn.setOnClickListener(mThisButtonListener);
            ll.addView(rdbtn, p);
        }
        databaseAccess.close();


        btnprinterset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll.getCheckedRadioButtonId() == -1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("No Printer selected");
                    alertDialogBuilder.setMessage("Please Select One printer")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent UserDashboard = new Intent(getApplicationContext(), CollSetupMenuActivity.class);
                                    startActivity(UserDashboard);
                                    finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                    // internetStatus.setText("Internet Disconnected.");
                    //  internetStatus.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    // get selected radio button from radioGroup
                    SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                    String Usernm =sessiondata.getString("userID", null);
                    Log.d("DemoApp", " Usernm   " + Usernm);
                    int selectedId = ll.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    rdbtn = (RadioButton) findViewById(selectedId);
                    String printeriid= rdbtn.getText().toString();
                    String[] rbinfo = printeriid.split("[:]");
                    strprintid = rbinfo[1];
                    Log.d("DemoApp", " strprintid   " + strprintid);
                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    String strSelectSQL_01 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=0 ";
                    Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);

                    String strSelectSQL_02 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=1 WHERE PRINTER_VAL='" + strprintid + "' ";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                    DatabaseAccess.database.execSQL(strSelectSQL_02);

                    String strSelectSQL_03 = "UPDATE SA_USER SET SBMPRV='" + strprintid + "' WHERE userid = '" + Usernm + "' ";
                    Log.d("DemoApp", "strSelectSQL_03" + strSelectSQL_03);
                    DatabaseAccess.database.execSQL(strSelectSQL_03);
                    databaseAccess.close();

                    sharedPreferenceClass.setValue_string("DeviceAddress1","");
                    sharedPreferenceClass.setValue_string("DeviceAddress2","");
                    sharedPreferenceClass.setValue_string("DeviceAddress3","");


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Default Printer Setting");
                    alertDialogBuilder.setMessage("Printer set successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
        });


    }
    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            String s = ((RadioButton) v).getText().toString();
            String[] TransInfo = s .split("[:]");
            String printID=TransInfo[1];
            rdbtn.setChecked(false);
            // get selected radio button from radioGroup
            int selectedId = ll.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            rdbtn = (RadioButton) findViewById(selectedId);
            Toast.makeText(SetPrinterTypeActivity.this,
                    rdbtn.getText(), Toast.LENGTH_SHORT).show();

        }
    };
}
