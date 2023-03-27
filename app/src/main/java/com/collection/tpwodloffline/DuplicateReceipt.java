package com.collection.tpwodloffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.collection.tpwodloffline.activity.CollectionDashBoard;

public class DuplicateReceipt extends AppCompatActivity {
    private static RadioButton rbbill;
    private   DatabaseAccess databaseAccess=null;
    final Context context = this;
    private String transID="";
    private String AmountPay="";
    private String EntryNum="";
    private EditText transIDtxt=null;
    private String cust_id="";
    private String vstrpayamt="";
    private String Usernm ="";
    private Cursor rs=null;
    private int sbmflg = 0;
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_receipt);
        Toolbar toolbard = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rbbill=(RadioButton)findViewById(R.id.bill);

        EntryNum="";
        Bundle extrasvalcol = getIntent().getExtras();
        String SelChoice = extrasvalcol.getString("SelChoice");
        EntryNum = extrasvalcol.getString("EntryNum");

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();


        String strSelectSQL_02 = "select trans_id,tot_paid,cust_id from coll_sbm_data where cons_acc='" + EntryNum + "' and strftime('%m-%Y', 'now') =strftime('%m-%Y', trans_date) and machine_no=1 and recpt_flg<>1 order by trans_id desc";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
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
            vstrpayamt=AmountPay;
            cust_id = rs1.getString(2);
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setText("TRANS.ID:"+transID+": AMOUNT:"+AmountPay);
            rdbtn.setTextSize(18);
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
    private String TransID="";
    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            String s = ((RadioButton) v).getText().toString();
            String[] TransInfo = s .split("[:]");
            TransID=TransInfo[1];
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Do You Want to Generate Receipt");
            alertDialogBuilder.setMessage("Tap Generate if yes" + "\n" + " Tap Cancel to re-select ")
                    .setCancelable(false)
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Generate", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent RecptPrintIntent = new Intent(getApplicationContext(), ReceiptGenDuplicate.class);
                            Bundle RecptBun = new Bundle();
                            RecptBun.putString("custID", cust_id);
                            RecptBun.putString("TransID", TransID);
                            RecptBun.putString("vstrpayamt", vstrpayamt);
                            RecptPrintIntent.putExtras(RecptBun);
                            startActivity(RecptPrintIntent);
                            finish();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    };
}
