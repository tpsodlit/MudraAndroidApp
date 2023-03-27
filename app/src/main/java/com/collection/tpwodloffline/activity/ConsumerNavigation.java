package com.collection.tpwodloffline.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.adapter.NavAdapter;
import com.collection.tpwodloffline.utils.Distance;
import com.collection.tpwodloffline.utils.NavReport;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConsumerNavigation extends AppCompatActivity {

    RecyclerView rv_nav_recycler;
    private ArrayList<NavReport> navReportsList = new ArrayList<>();
    private ArrayList<NavReport> originalData = new ArrayList<>();
    NavAdapter navAdapter;
    String Cname, Clat, Clang, Scnum, Payable;
    public DatabaseAccess databaseAccess = null;
    private List<NavReport> model = new ArrayList<>();
    private ImageView iv_search;
    private EditText et_search;
    private ImageView iv_close;
    private TextView tv_no_data_found;
    SharedPreferenceClass sharedPreferenceClass;

    ArrayList<String> km_list = new ArrayList<>();
    String Custom_km;
    Spinner KmSpnr;
    int OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());

        sharedPreferenceClass= new SharedPreferenceClass(this);
        rv_nav_recycler = findViewById(R.id.rv_nav_recycler);
        iv_search = findViewById(R.id.iv_search);
        et_search = findViewById(R.id.et_search);
        iv_close = findViewById(R.id.iv_close);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);

        KmSpnr = findViewById(R.id.km);
        km_list.add("1~km");
        km_list.add("2~km");
        km_list.add("3~km");
        km_list.add("4~km");
        km_list.add("5~km");
        km_list.add("6~km");


        rv_nav_recycler.setLayoutManager(new LinearLayoutManager(this));
        navAdapter = new NavAdapter(this, navReportsList);
        rv_nav_recycler.setAdapter(navAdapter);
        navReportsList.clear();


        ArrayAdapter year_adapter = new ArrayAdapter(ConsumerNavigation.this, android.R.layout.simple_spinner_item, km_list);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        KmSpnr.setAdapter(year_adapter);

        KmSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));
                navAdapter.notifyDataSetChanged();
                navReportsList.clear();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String km = parent.getItemAtPosition(position).toString();
                String[] separated = km.split("~");
                String km_data = separated[0];
                getData(km_data);

                // CollectionType = parent.getItemAtPosition(position).toString();
                // Toast.makeText(NonEnergyPayDetails.this,CollectionType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        iv_search.setOnClickListener(v -> {

            if (et_search.getText().toString().trim().length() > 0) {

                iv_close.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.GONE);

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                searchResult(et_search.getText().toString().trim());

            } else {
                Toast.makeText(ConsumerNavigation.this, "Please enter consumer no. or consumer name", Toast.LENGTH_SHORT).show();
            }

        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_search.setText("");
                iv_close.setVisibility(View.GONE);
                iv_search.setVisibility(View.VISIBLE);
                navAdapter.filterList(originalData, "");

            }
        });

        try{
            Boolean ol = sharedPreferenceClass.getValue_boolean("overlay");
            if(!ol){
                OverlayDialog();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void checkPermissionOL() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
            );
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);

            /* var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                 if (result.resultCode == Activity.RESULT_OK) {
                     // There are no request codes
                     val data: Intent? = result.data
                 }
             }
             val intents= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                 Uri.parse("package:$packageName"))
             resultLauncher.launch(intents)*/
        }
    }

    private void OverlayDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Overlay Permission");
        alertDialogBuilder.setMessage("Allow overlay permission for floating feature.")
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, id) -> {
                    sharedPreferenceClass.setValue_boolean("overlay",true);
                    dialog.dismiss();
                    checkPermissionOL();
                })
                .setNegativeButton("Deny", (dialog, id) -> {
                    sharedPreferenceClass.setValue_boolean("overlay",true);
                    dialog.dismiss();
                });
        // create alert dialog
        // show it
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    void searchResult(String text) {
        ArrayList<NavReport> temp = new ArrayList<>();
        for (NavReport d : navReportsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if ((d.getScnum().contains(text)) || ((d.getCname().contains(text)))) {
                temp.add(d);
            }
        }


        if (navReportsList.size() >= 1) {
            tv_no_data_found.setVisibility(View.GONE);
            rv_nav_recycler.setVisibility(View.VISIBLE);
            navAdapter.filterList(temp, et_search.getText().toString().trim());

        } else {
            tv_no_data_found.setVisibility(View.VISIBLE);
            rv_nav_recycler.setVisibility(View.GONE);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData(String km_data) {
        try {
            String lat = sharedPreferenceClass.getValue_string("Latitude");
            String lang = sharedPreferenceClass.getValue_string("Longitude");
            databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            String strUpdateSQL_01 = "select CON_NAME,FIELD2,FIELD3,FIELD1,BILL_TOTAL from CUST_DATA where FIELD2!='' and FIELD3!=''";
            Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);


            while (rs.moveToNext()) {

                Cname = rs.getString(0);
                Clat = rs.getString(1);
                Clang = rs.getString(2);
                Scnum = rs.getString(3);
                Payable = rs.getString(4);
                String distance = Distance.Distancekm(lat, lang, Clat, Clang);
                Double dis=0.0;
                try {
                 dis = Double.parseDouble(distance);}
                catch (Exception e){
                    e.printStackTrace();
                }
                if (Double.parseDouble(distance)<=Double.parseDouble(km_data)) {
                    NavReport navReport = new NavReport(Cname, Clat, Clang, Scnum, Payable,dis);
                    navReportsList.add(navReport);

                }
                if (navReportsList.size() >= 1) {
                    tv_no_data_found.setVisibility(View.GONE);
                    rv_nav_recycler.setVisibility(View.VISIBLE);

                } else {
                    tv_no_data_found.setVisibility(View.VISIBLE);
                    rv_nav_recycler.setVisibility(View.GONE);
                }

            }
            Collections.sort(navReportsList, new Comparator<NavReport>() {
                @Override
                public int compare(NavReport o1, NavReport o2) {
                  // return o1.distance > o2.distance ? -1 : 0;
                    return Double.compare( o1.getDistance(), o2.getDistance());
                   // return 0;
                }

               /* @Override
                public int compare(CustomData lhs, CustomData rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.getId() > rhs.getId() ? -1 : (lhs.customInt < rhs.customInt ) ? 1 : 0;
                }*/
            });
            originalData.addAll(navReportsList);
            navAdapter.notifyDataSetChanged();
            databaseAccess.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}