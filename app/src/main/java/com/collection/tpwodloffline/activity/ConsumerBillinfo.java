package com.collection.tpwodloffline.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ServerError;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.collection.tpwodloffline.utils.ServerLinks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConsumerBillinfo extends AppCompatActivity {

    ArrayList<String> Month_list = new ArrayList<>();
    ArrayList<String> Year_list = new ArrayList<>();
    EditText ConsumerNoEdtxt;
    String ConsumerNo, Month, Year;
    String CompanyId = CommonMethods.getCompanyID();
    Spinner MSpinner, YSpinner;
    Button Fetch;
    private DatabaseAccess databaseAccess = null;
    FrameLayout progressView;
    TextView NosPaytv,BillMonthtv,Amounttv;
    //RelativeLayout relativeLayout;
    CardView card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_billinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
        card = findViewById(R.id.card);
        //relativeLayout = findViewById(R.id.relativeLayout);
        progressView = findViewById(R.id.progressView);
        MSpinner = findViewById(R.id.month);
        YSpinner = findViewById(R.id.year);
        Fetch = findViewById(R.id.proceed);
        ConsumerNoEdtxt = findViewById(R.id.con_num);
        NosPaytv = findViewById(R.id.textView18);
        BillMonthtv = findViewById(R.id.textView17);
        Amounttv = findViewById(R.id.textView16);


        Month_list.add("Month");
        Month_list.add("01");
        Month_list.add("02");
        Month_list.add("03");
        Month_list.add("04");
        Month_list.add("05");
        Month_list.add("06");
        Month_list.add("07");
        Month_list.add("08");
        Month_list.add("09");
        Month_list.add("10");
        Month_list.add("11");
        Month_list.add("12");

        ArrayAdapter month_adapter = new ArrayAdapter(ConsumerBillinfo.this, android.R.layout.simple_spinner_item, Month_list);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MSpinner.setAdapter(month_adapter);

        Year_list.add("Year");
        Year_list.add("2021");
        Year_list.add("2022");

        ArrayAdapter year_adapter = new ArrayAdapter(ConsumerBillinfo.this, android.R.layout.simple_spinner_item, Year_list);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YSpinner.setAdapter(year_adapter);

        MSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));

                Month = parent.getItemAtPosition(position).toString();
                //Toast.makeText(StudentSignupActivity.this, classtypeName+" is "+ classtypeId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        YSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));

                Year = parent.getItemAtPosition(position).toString();
                //Toast.makeText(StudentSignupActivity.this, classtypeName+" is "+ classtypeId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumerNo = ConsumerNoEdtxt.getText().toString();
                card.setVisibility(View.GONE);

                if (Month.equals("Month")) {

                    Toast.makeText(ConsumerBillinfo.this, "Select billing month", Toast.LENGTH_SHORT).show();

                } else if (Year.equals("Year")) {

                    Toast.makeText(ConsumerBillinfo.this, "Select billing year", Toast.LENGTH_SHORT).show();

                } else if (ConsumerNo.equals("") || ConsumerNo.length() < 12) {

                    Toast.makeText(ConsumerBillinfo.this, "Provide valid 12 digit consumer number", Toast.LENGTH_SHORT).show();

                } else {
                  //  getBillinfo( CompanyId,Year+Month, "343102150775","3431045629");

                    String consref = getcustId(ConsumerNo);

                    if(consref.length()<10 || consref.equals("")){
                        Toast.makeText(ConsumerBillinfo.this, "Consumer not found", Toast.LENGTH_SHORT).show();

                    }else {
                        getBillinfo( CompanyId,Year+Month, ConsumerNo,consref);
                        //Toast.makeText(ConsumerBillinfo.this, "" + Month + " " + Year + " " + ConsumerNo+" "+consref, Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

    private String getcustId(String consumerNo) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String  ConsRef = "";

        String strSelectSQL_01 = "SELECT CUST_ID  " +
                "FROM CUST_DATA  WHERE CONS_ACC='" + consumerNo + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {

          ConsRef = cursor.getString(0);

        }

        databaseAccess.close();

        return ConsRef;
    }

    private void getBillinfo(String compId,String billmonth, String consumerNo, String consref) {
        String url = ServerLinks.baseUrl_payments + "CompID="+compId+"&ConsRef="+consref+"&ConsNo="+consumerNo+"&BillMth="+billmonth;
        progressView.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                  /* InvaliCompanyID = 455;
                    InvalidConsref = 456;
                    InvalidConsumerNo = 457;
                    InvalidBillMth = 458;*/

                    if (jsonObject.getInt("valCode") == 550) {

                        //new Response{"pmtInfo":{"consRef":"1234567890","billMth":"202111","amount":"100","nop":"2"},"valCode":550}

                        JSONObject object = jsonObject.getJSONObject("pmtInfo");

                       String BillMth = object.getString("billMth");
                       String Amount = object.getString("amount");
                       String Nop = object.getString("nop");

                       BillMonthtv.setText("Bill month : "+BillMth);
                          NosPaytv.setText("No. of payment : "+Nop);
                          Amounttv.setText("Amount paid : ₹"+Amount);
                        card.setVisibility(View.VISIBLE);
                       progressView.setVisibility(View.GONE);


                    }else if(jsonObject.getInt("ValCode") == 458){
                        Toast.makeText(ConsumerBillinfo.this, "Invalid bill month", Toast.LENGTH_SHORT).show();
                        progressView.setVisibility(View.GONE);
                        card.setVisibility(View.GONE);
                    }else if(jsonObject.getInt("ValCode") == 457){
                        Toast.makeText(ConsumerBillinfo.this, "Invalid consumer number", Toast.LENGTH_SHORT).show();
                        progressView.setVisibility(View.GONE);
                        card.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ConsumerBillinfo.this, "Payment info not found", Toast.LENGTH_SHORT).show();
                        progressView.setVisibility(View.GONE);
                        card.setVisibility(View.GONE);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ConsumerBillinfo.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    card.setVisibility(View.GONE);
                    progressView.setVisibility(View.GONE);

                }
            }//onResponse()

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ConsumerBillinfo.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                card.setVisibility(View.GONE);
                progressView.setVisibility(View.GONE);

            }
        });

        int socketTimeout = 60000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(ConsumerBillinfo.this);
        requestQueue.add(stringRequest);
    }
}