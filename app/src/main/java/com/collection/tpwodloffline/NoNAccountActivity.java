package com.collection.tpwodloffline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.collection.tpwodloffline.activity.AccountInfo;
import com.collection.tpwodloffline.activity.CollectionDashBoard;
import com.collection.tpwodloffline.modal.SpinnerItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class NoNAccountActivity extends AppCompatActivity {

    private Button btn_submit;
    private LinearLayout ll_spinner;
    private Spinner spin_type;
    private EditText et_ca,et_notification;
    private Context mContext;
    /*String[] collectionType = { "Demand Note", "Theft Payment", "Energy Instalment",
            "Demand Note Instalment","Theft Instalment"};*/
    private ArrayList<SpinnerItem> collectionType=new ArrayList<>();
    private ArrayList<String>collectionTypeItem=new ArrayList<>();

    private int spinnerPosition;
    private String remarksSpinner;

    private String referenceNumber="";
    private String date="";
    private String amount="";
    String caNumber="";
    String notificationNo="";
    String spinnerValue="";
    private ProgressDialog progressDialog;
    private String responseSubmit="";
    String encoded="";
    private String[] DetData;
    private String spinpos="";
    private String spinnerId="";
    private String name="";
    private String address="";
    private DatabaseAccess databaseAccess;
    private Button btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_n_account);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        mContext=this;
        btn_submit=findViewById(R.id.btn_submit);
        ll_spinner=findViewById(R.id.ll_spinner);
        spin_type=findViewById(R.id.spin_type);
        et_notification=findViewById(R.id.et_notification);
        et_ca=findViewById(R.id.et_ca);
        btn_back=findViewById(R.id.btn_back);

        callSpinnerItem();
        addListenerOnSpinnerItemSelection();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoNAccountActivity.this, CollectionDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, collectionTypeItem);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spin_type.setAdapter(dataAdapter);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* if ((et_ca.getText().toString().trim().length()==0)||(et_ca.getText().toString().trim().length()<11)){

                    Toast.makeText(NoNAccountActivity.this,"Please enter correct CA number",Toast.LENGTH_SHORT).show();
                }
                else*/
                if (spinnerValue.equalsIgnoreCase("Select Collection Type")){
                    Toast.makeText(NoNAccountActivity.this,"Please select collection type.",Toast.LENGTH_SHORT).show();

                }

                else if (et_notification.getText().toString().trim().length()==0){
                    Toast.makeText(NoNAccountActivity.this,"Please enter notification number.",Toast.LENGTH_SHORT).show();

                }

                else {

                    if (CommonMethods.isConnected(NoNAccountActivity.this)){

                        String url="http://portal.tpcentralodisha.com:8090/TPCODL_AllInOne/SAP_API_Call?CA_NO="+et_ca.getText().toString().trim()+"&NOTICE_NO="+et_notification.getText().toString().trim()+"&BILL_TYPE="+spinnerId;
                        //   new TestAsynk().execute(url);

                        Intent accountinfo = new Intent(getApplicationContext(), AccountInfo.class);
                        Bundle extrasvalcol = new Bundle();
                        extrasvalcol.putString("SelChoice", "");
                        extrasvalcol.putString("EntryNum", et_notification.getText().toString().trim());
                        extrasvalcol.putString("MobNonew", "");
                        extrasvalcol.putString("conType","");
                        extrasvalcol.putString("spinnerId",spinnerId);
                        extrasvalcol.putString("from","non-account");
                        extrasvalcol.putString("spinnerText",spinnerValue);
                        // extrasval.putString("Validcon", "0");
                        accountinfo.putExtras(extrasvalcol);
                        startActivity(accountinfo);

                    }
                    else {
                        Toast.makeText(NoNAccountActivity.this,"Please connect to intenet",Toast.LENGTH_SHORT).show();
                    }


                }



            }
        });
    }

    private void callSpinnerItem() {

        if (CommonMethods.isConnected(mContext)){
            String url="http://portal.tpcentralodisha.com:8090/ePortalAPP/ePortal_App.jsp?RequestType=9";
            new fetchSpinnerItem().execute(url);
        }
        else {
            Toast.makeText(mContext,"No internet connection, please connect to internet",Toast.LENGTH_SHORT).show();
        }
    }



    private class TestAsynk extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {
            String strURL=params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent=null;
            Log.d("DemoApp", " strURL   " + strURL);

            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();

                // String htmlPage = a.toString();
                /*String versionNumber = htmlPage.replaceAll("\\<.*?>","");
                System.out.println("sdf=="+versionNumber);*/

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
            super.onPreExecute();

            ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(NoNAccountActivity.this, "Submitting Data", "Please Wait:: connecting to server");

            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("Enable Data");
                alertDialogBuilder.setMessage("Enable Data & Retry")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            try {
                System.out.println("zdsfgfb=="+result);
                result="080002982017|M/S Manapasand Pan Masala|IDCO allotted Plot No. 4/25 Rev. Plot No. 1614(P) Khata No. 450 At Industrial Estate, Ramdaspur Cuttack-754005 |00000000|00000000|0.00 |00|PRVT|005000009911|12|0.00 |00000000|0.00 |0.00 |100583.00 |63692.00 |20210402|N1|N1A1|0|0.00 |0.00 |0.00 |0.00 |0.00 |0.00 |0.00 |0.00 |9937332335|9937332335";

                String[]  response=result.split("\\|");
                referenceNumber="23456dfcgh";
                date=response[17];
                amount=response[15];
                name=response[1];
                address=response[2];

                Intent intent=new Intent(NoNAccountActivity.this,OthCollection.class);
                intent.putExtra("refNo",referenceNumber);
                intent.putExtra("date",date);
                intent.putExtra("amount",amount);
                intent.putExtra("caNumber",et_ca.getText().toString().trim().trim());
                intent.putExtra("notificationNo",et_notification.getText().toString().trim().trim());
                intent.putExtra("spinnerValue",spinnerValue);
                intent.putExtra("name",name);
                intent.putExtra("address",address);
                intent.putExtra("spinnerText",spinnerValue);
                startActivity(intent);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }



        }

    }

    private  class fetchSpinnerItem extends AsyncTask<String, Integer, String> {


        private String comData="";

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL=params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent=null;
            Log.d("DemoApp", " strURL   " + strURL);

            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
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
            progressDialog = ProgressDialog.show(NoNAccountActivity.this, "Fetching Data", "Please Wait:: connecting to server");

            ConnectivityManager cm = (ConnectivityManager)NoNAccountActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoNAccountActivity.this);
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
                                dialog.cancel();
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


            try {

                if (str!=null){
                    String[] BillInfo = str.split("[|]");

                    if (BillInfo[0].equalsIgnoreCase("1")){


                        for (int i=1;i<BillInfo.length;i++){

                            comData = BillInfo[i];
                            //Log.d("DemoApp", "comData   " + comData);
                            //comprmlen=comsubcatcode.length;
                            DetData = comData.split("[;]");

                            SpinnerItem complainModal=new SpinnerItem();
                            complainModal.setSpinnerName(DetData[1]);
                            complainModal.setSpinnerID(DetData[0]);
                            collectionTypeItem.add(DetData[1]);
                            collectionType.add(complainModal);
/*
                            databaseAccess = DatabaseAccess.getInstance(NoNAccountActivity.this);
                            databaseAccess.open();
                            String strSelectSQL_02 = "INSERT INTO MST_NON_ENERGY " +
                                    " (Spinner_Id,Spinner_Name)" +
                                    " VALUES('" + DetData[0] + "','" + DetData[1] + "') ";
                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                            databaseAccess.close();*/


                        }
                    }



                }

            }

            catch (Exception ex){
                ex.printStackTrace();
            }

            //fetChImage(empId);
        }

    }

    public void addListenerOnSpinnerItemSelection() {
        collectionTypeItem.clear();

        collectionTypeItem.add("Select Collection Type");
        for (int i=0;i<collectionType.size();i++){
            collectionTypeItem.add(collectionType.get(i).getSpinnerName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NoNAccountActivity.this,
                android.R.layout.simple_spinner_item, collectionTypeItem);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_type.setAdapter(dataAdapter);
        /////////////
        spin_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                // String value = String.valueOf(item.toString());
                String value = String.valueOf(position);
                //  spinpos1= String.valueOf(position);
                spinpos= String.valueOf(position);
                String categoryId="";
                spinnerValue=item.toString();

                if (position>=1){


                    spinnerId=collectionType.get(Integer.parseInt(spinpos)-1).getSpinnerID();

                  /*  for (int j=0;j<complainModals.size();j++){
                        if (item.toString().equalsIgnoreCase(complainModals.get(j).getComapinReaon())){
                            categoryId=complainModals.get(j).getId();
                        }
                    }*/


                }


            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}