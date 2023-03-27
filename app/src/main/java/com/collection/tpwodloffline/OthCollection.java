package com.collection.tpwodloffline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.collection.tpwodloffline.activity.ColDashboard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class OthCollection extends AppCompatActivity {
    private static RadioButton rbSD;
    private static RadioButton rbProcFee;
    private static RadioButton rbTempCon;
    private static RadioButton rbDepoWork;
    private static EditText strRefNo;
    private static EditText strPaybleAmt;
    private static EditText strName;
    private TextView btn_get_info;
    private  String strRefNum ="";
    private  String strPamt ="";
    private  String strRefName ="";
    private String[] BillInfo;
    private Context context;
    private String StrBillInfo="";
    private String urlName="";
    Button btnSearch;
    String  imeinum="";
    private String usernm="";
    private String strCompanyId="";
    private String div_code="";
    private String custId="";
    private String conType="";
    private String consumerMobNo="";
    private String BalFetch="";
    private DatabaseAccess databaseAccess=null;
    private String cons_accfetch="";
    private String subdivisionfetch="";
    private String sectionfetch="";
    private String add1fetch="";
    private String add2fetch="";
    private String pybleamt="";
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oth_collection);

        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        rbSD=(RadioButton)findViewById(R.id.SD);
        rbProcFee=(RadioButton)findViewById(R.id.ProcFee);
        rbTempCon=(RadioButton)findViewById(R.id.TempCon);
        rbDepoWork=(RadioButton)findViewById(R.id.DepoWork);
        btn_get_info=findViewById(R.id.btn_get_info);
        strRefNo = (EditText) findViewById(R.id.RefNo);
        strPaybleAmt = (EditText) findViewById(R.id.PaybleAmt);
        strName = (EditText) findViewById(R.id.Name);
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String

        StrBillInfo=urlName+"CESU_API_Chk_Mobile_No/Mobile_EMail.jsp?";

        btnSearch = (Button) findViewById(R.id.btnSearch);
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        String sdflg =sessionssodata.getString("sdflg", null); // getting String
        String pfflg=sessionssodata.getString("pfflg", null);
        String dwflg =sessionssodata.getString("dwflg", null); // getting String
        String Tcflg =sessionssodata.getString("Tcflg", null);
        usernm=sessionssodata.getString("userID", null);
        // String pwd=sessiondata.getString("prv_bill", null);
        strCompanyId=sessionssodata.getString("CompanyID", null);
        div_code=sessionssodata.getString("div_code", null);
        imeinum=sessionssodata.getString("imeinum", null);
        conType="C";

        if(sdflg.equals("1")){
            rbSD.setClickable(true);
            rbSD.setEnabled(true);
        }else{
            rbSD.setClickable(false);
            rbSD.setEnabled(false);
        }
        if(pfflg.equals("1")){
            rbProcFee.setClickable(true);
            rbProcFee.setEnabled(true);
        }else{
            rbProcFee.setClickable(false);
            rbProcFee.setEnabled(false);
        }
        if(dwflg.equals("1")){
            rbDepoWork.setClickable(true);
            rbDepoWork.setEnabled(true);
        }else{
            rbDepoWork.setClickable(false);
            rbDepoWork.setEnabled(false);
        }
        if(Tcflg.equals("1")){
            rbTempCon.setClickable(true);
            rbTempCon.setEnabled(true);
        }else{
            rbTempCon.setClickable(false);
            rbTempCon.setEnabled(false);
        }

        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ColDashboard.class));
                finish();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strRefNum = strRefNo.getText().toString();
                strPamt = strPaybleAmt.getText().toString();
                strRefName = strName.getText().toString();

                Log.d("DemoApp", "strRefNum" + strRefNum);

                if (TextUtils.isEmpty(strRefName)) {
                    strName.setError("Enter  Name");
                    //  Log.d("DemoApp", "Query SQL " + strRefNum);
                }else if (TextUtils.isEmpty(strRefNum)) {
                    strRefNo.setError("Blank no not taken");
                    // Log.d("DemoApp", "Query SQL " + strRefNum.length());
                } else if (TextUtils.isEmpty(strPamt)) {
                    strPaybleAmt.setError("Enter  amount");
                    //Log.d("DemoApp", "Query SQL " + strRefNum.length());
                }
                else {
                    String SelChoice = "";
                    if (rbSD.isChecked() == true) {
                        SelChoice = "SD";
                    }
                    if (rbProcFee.isChecked() == true) {
                        SelChoice = "ProcFee";
                    }
                    if (rbTempCon.isChecked() == true) {
                        SelChoice = "TempCon";
                    }
                    if (rbDepoWork.isChecked() == true) {
                        SelChoice = "DepoWork";
                    }


                    databaseAccess = DatabaseAccess.getInstance(context);
                    databaseAccess.open();
                    String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                            " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,TOT_PAID,TRANS_ID,RECPT_FLG,TRANS_DATE)" +
                            " VALUES('" + cons_accfetch + "','" + custId + "','" + div_code + "','" + subdivisionfetch + "'," +
                            " '" + sectionfetch + "','" + strRefName + "','" + add1fetch + "','" + add2fetch + "','" + pybleamt + "','" + strRefNum + "',0,date('now')) ";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                    DatabaseAccess.database.execSQL(strSelectSQL_02);
                    strSelectSQL_02="";
                    strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='"+ BalFetch +"' WHERE USERID='"+ usernm +"'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                    DatabaseAccess.database.execSQL(strSelectSQL_02);

                    databaseAccess.close();



                    Intent accountinfo = new Intent(getApplicationContext(),PaySummaryNonAcnt.class);
                    Bundle Bunpayamtdtls = new Bundle();
                    Bunpayamtdtls.putString("SelChoice", SelChoice);
                    Bunpayamtdtls.putString("consacc", strRefNum);
                    Bunpayamtdtls.putString("Pableamt", strPamt);
                    Bunpayamtdtls.putString("namefetch", strRefName);
                    Bunpayamtdtls.putString("conType",conType);
                    Bunpayamtdtls.putString("TransID", strRefNum);
                    Bunpayamtdtls.putString("EntryNum", consumerMobNo);
                    Bunpayamtdtls.putString("MobNonew", consumerMobNo);
                    Bunpayamtdtls.putString("custID", custId);
                    Bunpayamtdtls.putString("MobileNofetch", consumerMobNo);
                    Bunpayamtdtls.putString("BalFetch", BalFetch);
                    // extrasval.putString("Validcon", "0");
                    accountinfo.putExtras(Bunpayamtdtls);
                    startActivity(accountinfo);
                    finish();
                }
            }
        });

        btn_get_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            /*    if (conType.equalsIgnoreCase("S")){
                    StrBillInfo=StrBillInfo+"un=TEST&pw=TEST&CompanyID=10&strDivCode="+div_code+"&strCons_Acc="+EntryNum_string+"&strDB_Type=1";
                }
                else {
                    StrBillInfo=StrBillInfo+"un=TEST&pw=TEST&CompanyID=10&strDivCode="+div_code+"&strCons_Acc="+EntryNum_string+"&strDB_Type=3";
                }*/
                if ("conType".equalsIgnoreCase("S")){
                    StrBillInfo=StrBillInfo+"un=TEST&pw=TEST&CompanyID=10&strDivCode="+"div_code"+"&strCons_Acc="+"EntryNum_string"+"&strDB_Type=1";
                }
                else {
                    StrBillInfo=StrBillInfo+"un=TEST&pw=TEST&CompanyID=10&strDivCode="+"div_code"+"&strCons_Acc="+"EntryNum_string"+"&strDB_Type=3";
                }


                new FetchBillDetOnline().execute(StrBillInfo);
            }
        });

    }
    private  class FetchBillDetOnline extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL=params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent=null;
            Log.d("DemoApp", " strURL   " + strURL);
            try {
                URL url =null;
                url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = null;
                in=new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine="";
                StringBuilder a = new StringBuilder();
                Log.d("DemoApp", " a size   " + a.length());
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                    //  Log.d("DemoApp", " input line " + a.toString());
                }
                in.close();

                Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>")+"<body>".length();
                int end = html.indexOf("</body>", start);
                bodycontent = html.substring(start, end);
                Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bodycontent;
        }
        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(OthCollection.this, "Fetching Information", "Please Wait:: connecting to server");
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Enable Data");
                alertDialogBuilder.setMessage("Enable Data & Retry")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                OthCollection.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }
        @Override
        protected void onPostExecute(String str) {
            Log.d("DemoApp", " str   " + str);
            try {
                strPaybleAmt.setVisibility(View.VISIBLE);
                strName.setVisibility(View.VISIBLE);
                btnSearch.setVisibility( View.VISIBLE);
                progressDialog.dismiss();
                custId = div_code + conType + "EntryNum_string";
                consumerMobNo="";


                if (str!=null){

                    String pipeDelBillInfo =str;
                    String MobNum="";

                    try { BillInfo = pipeDelBillInfo.split("[|]");
                        BalFetch=BillInfo[14];

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                        MobNum="9999999999";
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }



        }

    }
}
