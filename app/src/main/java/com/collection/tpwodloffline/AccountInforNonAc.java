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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.collection.tpwodloffline.activity.AcCollection;
import com.collection.tpwodloffline.activity.MainActivity;
import com.collection.tpwodloffline.activity.PaySummary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AccountInforNonAc extends AppCompatActivity {
    String SelChoice_string="";
    String EntryNum_string="";
    private static TextView strtxtconsno;
    private static TextView strtxtname;

    private static EditText strtxtamtpayble;


    private static TextView scbl1 ;
    private static TextView stbl1;
    private static TextView spbl1;
    private  DatabaseAccess databaseAccess=null;
    final Context context = this;
    //access for billig data
    private String pybleamt ="";
    private String cons_acc="";
    private String custID="";
    private String StrBillInfo="";
    private String strCompanyId="";
    private String div_code="";
    private String cons_accfetch="";
    private String Cons_idfetch="";
    private  String namefetch="";
    private String add1fetch="";
    private String add2fetch="";
    private String payblefetch="";
    private String divisionfetch="";
    private String subdivisionfetch="";
    private String sectionfetch="";
    private String Trans_IDfetch="";
    private String BalFetch="";
    private String  usernm="";
    private String usname="";
    private  String dbpwdnm="";
    private String urlName="";
    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_infor_non);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName =savedUrl.getString("savedUrl", null); // getting String


        StrBillInfo = urlName+"IncomingSMS/CESU_BillInfo.jsp?";
        strtxtconsno=(TextView)findViewById(R.id.consno);
        strtxtname=(TextView)findViewById(R.id.name);
        strtxtamtpayble=(EditText) findViewById(R.id.amtpayble);
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        usname =sessionssodata.getString("usname", null); // getting String
        dbpwdnm=sessionssodata.getString("dbpwdnm", null);
        Button btnSearch = (Button) findViewById(R.id.btnSubmit);
        spbl1=(TextView)findViewById(R.id.pbl1);
        SelChoice_string="";
        Bundle extrasvalcol = getIntent().getExtras();
        SelChoice_string = extrasvalcol.getString("SelChoice");
        EntryNum_string = extrasvalcol.getString("EntryNum");
        cons_acc="";
        namefetch="";
        add1fetch="";
        add2fetch="";
        payblefetch="";

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String toDayDt =sessiondata.getString("toDayDt", null); // getting String
        String prv_bill=sessiondata.getString("prv_bill", null);
        usernm=sessiondata.getString("userID", null);
        // String pwd=sessiondata.getString("prv_bill", null);
        strCompanyId=sessiondata.getString("CompanyID", null);
        div_code=sessiondata.getString("div_code", null);
        String  imeinum=sessiondata.getString("imeinum", null);

        if(SelChoice_string.equals("AcctNo")) {
            // fetching information from billing app

            //consumer details checking online
            StrBillInfo = StrBillInfo+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=" + strCompanyId + "&ConsumerID=" + div_code + "S" + EntryNum_string + "&imei=" + imeinum;
            Log.d("DemoApp", "in Loop AuthURL1" + StrBillInfo);
            try {
                new FetchBillDetOnline().execute(StrBillInfo);
            } catch (Exception e) {
            }

            scbl1.setVisibility(View.GONE);
            stbl1.setVisibility(View.GONE);
            strtxtconsno.setText(EntryNum_string);

            /////ending

            if (SelChoice_string.equals("Asd")) {
                spbl1.setText("ASD Amount");
            } else if (SelChoice_string.equals("Adv")) {
                spbl1.setText("Advance Amount");
            } else if (SelChoice_string.equals("rcf")) {
                spbl1.setText("Reconnection Fee ");
            }
            StrBillInfo = StrBillInfo+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=" + strCompanyId + "&ConsumerID=" + div_code + "S" + EntryNum_string + "&imei=" + imeinum;
            Log.d("DemoApp", "in Loop AuthURL2" + StrBillInfo);
            try {
                new FetchBillDetOnline().execute(StrBillInfo);
            } catch (Exception e) {
            }

            scbl1.setVisibility(View.GONE);
            stbl1.setVisibility(View.GONE);
            strtxtconsno.setText(EntryNum_string);

        }
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pybleamt = strtxtamtpayble.getText().toString();
                    // BalFetch="1000";
                    if(Integer.parseInt(BalFetch)<Integer.parseInt(pybleamt)){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Balance Not Available");
                        alertDialogBuilder.setMessage("Deposit Cash and Contact Divisional"+"\n"+"Finance Section")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }else {
                        if (Integer.parseInt(pybleamt) >= Integer.parseInt(payblefetch) && Integer.parseInt(pybleamt) > 0) {
                            Log.d("DemoApp", "pybleamt SQL " + pybleamt);
                            databaseAccess = DatabaseAccess.getInstance(context);
                            databaseAccess.open();
                            String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                    " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,TOT_PAID,TRANS_ID,RECPT_FLG)" +
                                    " VALUES('" + cons_accfetch + "','" + Cons_idfetch + "','" + divisionfetch + "','" + subdivisionfetch + "'," +
                                    " '" + sectionfetch + "','" + namefetch + "','" + add1fetch + "','" + add2fetch + "','" + pybleamt + "','" + Trans_IDfetch + "',0) ";
                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                            DatabaseAccess.database.execSQL(strSelectSQL_02);
                            strSelectSQL_02="";
                            strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='"+ BalFetch +"' WHERE USERID='"+ usernm +"'";
                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                            databaseAccess.close();
                            Intent paysumry = new Intent(getApplicationContext(), PaySummary.class);
                            Bundle Bunpayamtdtls = new Bundle();
                            Bunpayamtdtls.putString("Pableamt", pybleamt);
                            Bunpayamtdtls.putString("consacc", cons_accfetch);
                            Bunpayamtdtls.putString("custID", Cons_idfetch);
                            Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                            Bunpayamtdtls.putString("SelChoice", SelChoice_string);
                            Bunpayamtdtls.putString("BalFetch", BalFetch);
                            Bunpayamtdtls.putString("namefetch", namefetch);
                            // extrasval.putString("Validcon", "0");
                            paysumry.putExtras(Bunpayamtdtls);
                            startActivity(paysumry);
                            finish();
                        } else {
                            strtxtamtpayble.setError("Enter Amount Cannot be less or Zero");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        toolbarback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AcCollection.class));
                finish();
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
                progressDialog = ProgressDialog.show(AccountInforNonAc.this, "Fetching Bill Details", "Please Wait:: connecting to server");
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
                                AccountInforNonAc.this.finish();
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
            progressDialog.dismiss();
            String pipeDelBillInfo =str;
            String[] BillInfo = pipeDelBillInfo.split("[|]");
            String ConsValid=BillInfo[2];
            String AccessFlg=BillInfo[1];
            if(AccessFlg.equals("0")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Access Denied");
                alertDialogBuilder.setMessage("Authorization Fail")
                        .setCancelable(false)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }else {
                if (ConsValid.equals("1") && BillInfo[0].equals("1")) {
                    divisionfetch = BillInfo[4];
                    subdivisionfetch = BillInfo[5];
                    sectionfetch = BillInfo[6];
                    cons_accfetch = BillInfo[7];
                    namefetch = BillInfo[8];
                    add1fetch = BillInfo[9];
                    add2fetch = BillInfo[10];
                    Trans_IDfetch = BillInfo[12];
                    if (BillInfo[3].equals("1")) {
                        payblefetch = BillInfo[11];
                    } else {
                        payblefetch = "0";
                    }
                    BalFetch=BillInfo[13];
                    Cons_idfetch = div_code + "S" + EntryNum_string;
                    ///inserting into collection database
                } else {
                    Intent ConsNotFound = new Intent(getApplicationContext(), ConsNotFound.class);
                    startActivity(ConsNotFound);
                    finish();
                }
            }
            strtxtconsno.setText(cons_accfetch);
            strtxtname.setText(namefetch);


            if(SelChoice_string.equals("Asd")|| SelChoice_string.equals("Adv")|| SelChoice_string.equals("rcf")) {
                strtxtamtpayble.setText("");
            }else{
                strtxtamtpayble.setText(payblefetch);
            }
        }

    }

}
