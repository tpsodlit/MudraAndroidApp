package com.collection.tpwodloffline.activity;

import static com.collection.tpwodloffline.utils.Constants.isDataSynced;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.UploadManager;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class ColDashboard extends AppCompatActivity {
    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }
    public DatabaseAccess databaseAccess=null;
    private TextView uid;
    private TextView strtdcoll;
    private TextView strtotcoll;
    private TextView strtotmr;
    private TextView strcash;
    private TextView strdd;
    private TextView strcheque;
    private TextView strbalrem;
    private TextView strtodaytmr;
    private String  Usernm="";
    private ProgressDialog progressDialog;
    private TextView neft;
    private TextView rtgs;
    private String totNeft="";
    private String totRtgs="";
    SharedPreferenceClass sharedPreferenceClass;
    String un;
    TextView usercount;
    String usname,device_id;

    @Override
    protected void onResume() {
        super.onResume();
        usname = sharedPreferenceClass.getValue_string("un");
        device_id = CommonMethods.getDeviceid(getApplicationContext());
        getReport();
        CommonMethods.checkConnection(getApplicationContext());
        scheduleBackgrounService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.col_toolbar_menu, menu);
        return true;
    }

    private int getOfflineCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private int getUserCount(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from CUST_DATA where USER_ID= " + un +"";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void scheduleBackgrounService(){
        String TAG_NAME= "uploadData";
        PeriodicWorkRequest periodicSyncDataWork =
                new PeriodicWorkRequest.Builder(UploadManager.class, 30, TimeUnit.MINUTES)
                        .addTag(TAG_NAME)
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(TAG_NAME, ExistingPeriodicWorkPolicy.KEEP,
                periodicSyncDataWork);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean chksts = false;
        chksts = isNetworkAvailable();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            String un = sharedPreferenceClass.getValue_string("un");
            String pw = sharedPreferenceClass.getValue_string("pw");
            String mobile = sharedPreferenceClass.getValue_string("mobile");

            if(CommonMethods.isConnected(getApplicationContext())){
                if(recordsInLocal()>0) {
                    if (!(getOfflineCount()>0)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ColDashboard.this);
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder.setMessage("This is going to re download consumer data. Are you sure you want to re-download?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        usercount.setKeepScreenOn(true);
                                        new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un,pw,mobile));
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                    else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ColDashboard.this);
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder.setMessage("There is some pending data to upload,please upload pendind data first.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                }
            }else {
                Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
            }

            return true;
        }/*else if(id == R.id.fetch_payment){
            if(chksts){
                startActivity(new Intent(ColDashboard.this,ConsumerBillinfo.class));
            }else {
                Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();

            }*/


        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    FusedLocationProviderClient mFusedLocationClient;
    private boolean checkPermissionnow() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    private void getLastLocation() {
        if (checkPermissionnow()) {
            if (isLocationEnabled()) {

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if(location ==null){
                        requestNewLocationData();
                    }else {
                        sharedPreferenceClass.setValue_string("Latitude", String.valueOf(location.getLatitude()));
                        sharedPreferenceClass.setValue_string("Longitude", String.valueOf(location.getLongitude()));
                    }
                });
            /*{ task ->
                        Location location = task.result;
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //Toast.makeText(this, location.latitude.toString()+" "+ location.longitude.toString(), Toast.LENGTH_SHORT).show()

                          // findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                        // findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()
                    }
                }*/
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                /* GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
                     override fun gpsStatus(isGPSEnable: Boolean) {
                         // turn on GPS
                         //isGPS = isGPSEnable
                     }
                 })*/
            }
        } else {
            checkPermission();
        }
    }
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                ColDashboard.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ColDashboard.this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(
                    ColDashboard.this, PERMISSIONS, Constants.LOCATION_REQUEST);
        } else {
            getLastLocation();

        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER);

    }
    private void requestNewLocationData() {
        // val mLocationRequest = LocationRequest()
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // location is received
            Location mCurrentLocation = locationResult.getLastLocation();
            sharedPreferenceClass.setValue_string("Latitude", String.valueOf(mCurrentLocation.getLatitude()));
            sharedPreferenceClass.setValue_string("Longitude", String.valueOf(mCurrentLocation.getLongitude()));


        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coll_dashboard_new);
        sharedPreferenceClass = new SharedPreferenceClass(ColDashboard .this);
        un = sharedPreferenceClass.getValue_string("un");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CardView accol = (CardView) findViewById(R.id.acc_coll);
        CardView advcol = (CardView) findViewById(R.id.adv_col);
        CardView offline_report = (CardView) findViewById(R.id.offline_report);
        CardView locate_now = (CardView) findViewById(R.id.locate_now);
        usercount = (TextView) findViewById(R.id.usercount);
        usercount.setText("Total Consumer available: "+ getUserCount());
        neft=findViewById(R.id.neft);
        rtgs=findViewById(R.id.rtgs);
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        String accolflg =sessionssodata.getString("accolflg", null); // getting String
        String nonaccolflg=sessionssodata.getString("nonaccolflg", null);
        try {
            if (accolflg.equals("1")) {
                accol.setClickable(true);
                accol.setEnabled(true);
            } else {
                accol.setClickable(false);
                accol.setEnabled(false);
            }
        }catch(Exception e){
            accol.setClickable(false);
            accol.setEnabled(false);
            e.printStackTrace();
        }
       /* try {
            if(nonaccolflg!=null && nonaccolflg.equals("1")){
                othcol.setClickable(true);
                othcol.setEnabled(true);
            }else{
                othcol.setClickable(false);
                othcol.setEnabled(false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }*/
        offline_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent offlineRecords = new Intent(getApplicationContext(), OfflineRecords.class);
                startActivity(offlineRecords);
            }
        });
        advcol.setOnClickListener(view -> {
            String un = sharedPreferenceClass.getValue_string("un");
            String pw = sharedPreferenceClass.getValue_string("pw");
            String mobile = sharedPreferenceClass.getValue_string("mobile");

            if (recordsInLocal() > 0) {
                if (sumOfCollectedAmountInLocal() > 20000) {  // previously it was 30k made changes as per advidse of vipul sir
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ColDashboard.this);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setMessage("Please upload pending data, in case of error/issue contact IT Center.")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    Intent accol1 = new Intent(getApplicationContext(), AdvanceCollection.class);
                    startActivity(accol1);
                }
            } else {
                advcol.setKeepScreenOn(true);
                new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un, pw, mobile));
            }
            //finish();
        });

        accol.setOnClickListener(view -> {
            String un = sharedPreferenceClass.getValue_string("un");
            String pw = sharedPreferenceClass.getValue_string("pw");
            String mobile = sharedPreferenceClass.getValue_string("mobile");
            /*if(7==7){

            }else*/ if(recordsInLocal()>0) {
                if(sumOfCollectedAmountInLocal()>20000){  // previously it was 30k made changes as per advidse of vipul sir
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ColDashboard.this);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setMessage("Please upload pending data, in case of error/issue contact IT Center.")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }else {
                    Intent accol12 = new Intent(getApplicationContext(), AcCollection.class);
                    startActivity(accol12);
                }
            }else{
                accol.setKeepScreenOn(true);
                new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un,pw,mobile));
            }
            //finish();
        });

        locate_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ColDashboard.this,ConsumerNavigation.class));
            }
        });


    }


    private void getReport(){
        final Context context = this;
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm = sessiondata.getString("userID", null); // getting String
        String Coll_Limit = "";
        String Bal_Remain = "";
        String tot_mr = "";
        String todaymr = "";
        String Max_Date = "";
        String todaydate = "";
        String todaycol = "";
        String totcoll = "";
        String totcash = "";
        String totchq = "";
        String totdd = "";
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        /*String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now'),tot_mr  " +
                " FROM sa_user  where date('now')>=valid_startdate and date('now')<=valid_enddate and lock_flag=0 " +
                " and userid='" + Usernm + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            Coll_Limit = cursor.getString(8);
            Bal_Remain = cursor.getString(10);
            tot_mr = cursor.getString(12);

            Log.d("DemoApp", "in Loop" + Coll_Limit);
            Log.d("DemoApp", "in Loop" + Bal_Remain);
            Log.d("DemoApp", "in Loop" + tot_mr);
        }*/
        String strSelectSQL_01 = "select count(1) from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now','+05 hours','+30 minutes') =strftime('%d-%m-%Y', recpt_date)";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            todaymr = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "";
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date)";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            todaycol = cursor.getString(0);
        }
        cursor.close();

        strSelectSQL_01 = "select count(1) from coll_sbm_data_bkp where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";// for indian time date('now','+05 hours','+30 minutes')
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            tot_mr = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data_bkp where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totcoll = cursor.getString(0);
        }
        cursor.close();


        /*cursor.close();
        strSelectSQL_01 = "select count(1) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";// for indian time date('now','+05 hours','+30 minutes')
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            tot_mr = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date)";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totcoll = cursor.getString(0);
        }*/
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and pay_mode=0";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totcash = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and pay_mode=3";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totchq = cursor.getString(0);
        }
        cursor.close();
        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and pay_mode=2";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totdd = cursor.getString(0);
        }
        cursor.close();

        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and pay_mode=8";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totNeft = cursor.getString(0);
        }
        cursor.close();

        strSelectSQL_01 = "select sum(tot_paid) from coll_sbm_data where recpt_flg=1 and strftime('%m-%Y', 'now') =strftime('%m-%Y', recpt_date) and pay_mode=9";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            totRtgs = cursor.getString(0);
        }
        cursor.close();


        strSelectSQL_01 = "select bal_remain from sa_user where userid='" + Usernm + "'";
        cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            Bal_Remain = cursor.getString(0);
        }
        cursor.close();
        databaseAccess.close();
        uid = (TextView) findViewById(R.id.uid);
        strtdcoll = (TextView) findViewById(R.id.tdcoll);
        strtotcoll = (TextView) findViewById(R.id.totcoll);
        strtotmr = (TextView) findViewById(R.id.totmr);
        strcash = (TextView) findViewById(R.id.cash);
        strdd = (TextView) findViewById(R.id.dd);
        strcheque = (TextView) findViewById(R.id.cheque);
        strtodaytmr = (TextView) findViewById(R.id.todaytmr);
        strbalrem = (TextView) findViewById(R.id.balrem);

        strtotmr.setText(tot_mr);
        if(todaycol == null){
            strtdcoll.setText("0");
        }else {
            strtdcoll.setText(todaycol);
        }

        if(totcoll == null){
            strtotcoll.setText("0");
        }else {
            strtotcoll.setText(totcoll);
        }
        strtodaytmr.setText(todaymr);
        strcash.setText(totcash);
        strdd.setText(totdd);
        strcheque.setText(totchq);
        strbalrem.setText(Bal_Remain);
        uid.setText("("+usname+" : "+device_id+")");
        neft.setText(totNeft);
        rtgs.setText(totRtgs);
    }

    private int recordsInLocal(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from CUST_DATA";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private int sumOfCollectedAmountInLocal(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select SUM(TOT_PAID) from COLL_SBM_DATA where SEND_FLG=0";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private void deleteCustData(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String insertCredentials = "DELETE from CUST_DATA";
        DatabaseAccess.database.execSQL(insertCredentials);

        String delQueryOTS = "DELETE from OTSConsumerData";
        DatabaseAccess.database.execSQL(delQueryOTS);
        databaseAccess.close();
    }
    private void deleteCollSBMData(){
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String insertCredentials = "DELETE from COLL_SBM_DATA";
        DatabaseAccess.database.execSQL(insertCredentials);
        databaseAccess.close();
    }

    private  class DownloadCustData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL=params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent=null;
            Log.d("DemoApp", " strURL   " + strURL);
            // String strURL="http://portal.tpcentralodisha.com:8080/IncomingSMS/CESU_mCollection1.jsp?strCompanyID=1&un=1&pw=A&imei=356154070159681/01";

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
              /*  Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>")+"<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();

                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return bodycontent;
        }
        @Override

        protected void onPreExecute() {

            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(ColDashboard.this, "Downloading consumer data", "Please Wait:: connecting to server");
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ColDashboard.this);
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
                                ColDashboard.this.finish();
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
            progressDialog.dismiss();
            deleteCustData();
            //deleteCollSBMData();
            new ColDashboard.InsertIntoDb().execute(str);
        }

    }

    private class InsertIntoDb extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(ColDashboard.this);

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog1.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... str) {

            try {


                //  if (custData.length > 1) {

                JSONArray ary = new JSONArray(str);
                String obj = ary.getString(0);
                JSONObject jsonObject = new JSONObject(obj);
                int resCode = jsonObject.getInt("resCode");
                if (resCode == 200) {

                    JSONArray jsonArray = jsonObject.getJSONArray("cData");
                    final int max = jsonArray.length();
                    if(jsonArray.length()>0) {

                        runOnUiThread(() -> {
                            progressDialog1.setCancelable(false);
                            progressDialog1.setMessage("Downloading data don't press any key, please wait...");
                            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog1.setProgress(0);
                            progressDialog1.setMax(max);
                            progressDialog1.show();
                        });

                        for (int i = 0; i < jsonArray.length(); i++) {
                            progressDialog1.setProgress(i);
                            //HashMap<String, String> hashMap = new HashMap<>();
                            JSONObject jOb = jsonArray.getJSONObject(i);
                            //hashMap.put("id", jOb.optString("id"));
                            String Division = jOb.getString("Division").replace("'", "''");
                            String AccNo = jOb.getString("AccNo").replace("'", "''");
                            String ConsRef = jOb.getString("ConsRef");
                            String SdoCd = jOb.getString("SdoCd");
                            String SectionName = jOb.getString("SectionName").replace("'", "''");
                            String Name = jOb.getString("Name").replace("'", "''");
                            String address1 = jOb.getString("address1").replace("'", "''");
                            String address2 = jOb.getString("address2").replace("'", "''");
                            Double CurrentAmount = jOb.getDouble("CurrentAmount");
                            Double TotalAmount = jOb.getDouble("TotalAmount");
                            Double REBATE = jOb.getDouble("REBATE");
                            String DUEDATE = jOb.getString("DUEDATE");
                            String MobileNo = jOb.getString("MobileNo");
                            String BINDER = jOb.getString("BINDER");
                            int CollectionCount = jOb.getInt("CollectionCount");
                            String BILL_MTH = jOb.getString("BILL_MTH");
                            int Advance_Count = jOb.getInt("Advance_Count");
                            int ConsumerFlag = jOb.getInt("ConsumerFlag");
                            String payDate = jOb.getString("payDate");
                            String lastPayAmount = jOb.getString("lastPayAmount");
                            String lastPayMode = jOb.getString("lastPayMode");
                            String paymentRcpt = jOb.getString("paymentRcpt");
                            String scno = jOb.getString("scno");
                            String latitude = jOb.getString("latitude");
                            String longitude = jOb.getString("longitude");

                            insertCustData(Division, AccNo, ConsRef, SdoCd, SectionName, Name, address1, address2, CurrentAmount, TotalAmount, REBATE, DUEDATE, MobileNo, BINDER, CollectionCount, BILL_MTH, Advance_Count, ConsumerFlag, payDate, lastPayAmount, lastPayMode, paymentRcpt, scno, latitude, longitude, false, true);

                        }
                        databaseAccess.close();
                        CommonMethods.saveBooleanPreference(ColDashboard.this, isDataSynced, true);
                        runOnUiThread(() -> {
                            usercount.setText("Total Consumer available: "+ getUserCount());
                        });



                        //insertCustData(Division, AccNo, ConsRef, SdoCd, SectionName, Name, address1, address2, CurrentAmount, TotalAmount, REBATE, DUEDATE, MobileNo, BINDER, CollectionCount, BILL_MTH, Advance_Count, ConsumerFlag, payDate, lastPayAmount, lastPayMode, paymentRcpt, scno, latitude, longitude, false, true);

                    }else {
                        runOnUiThread(() -> Toast.makeText(ColDashboard.this, "No record found!", Toast.LENGTH_SHORT).show());

                    }
                    //download OTS Data
                    try {
                        JSONArray jsonArrayOTS = jsonObject.getJSONArray("otsData");
                        Log.d("OTS Data",jsonArrayOTS.toString());
                        if (jsonArrayOTS != null) {
                            final int maxOTS = jsonArrayOTS.length();
                            if (maxOTS > 0) {
                                runOnUiThread(() -> {
                                    progressDialog1.setCancelable(false);
                                    progressDialog1.setMessage("Downloading OTS data don't " +
                                            "press any key, please wait...");
                                    progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressDialog1.setProgress(0);
                                    progressDialog1.setMax(maxOTS);
                                    progressDialog1.show();
                                });
                                Log.d("OTS Data is going to be inserted..","OTS Data going to be inserted..");
                                for (int i = 0; i < jsonArrayOTS.length(); i++) {
                                    progressDialog1.setProgress(i);
                                    JSONObject otsJsonObject = jsonArrayOTS.getJSONObject(i);
                                    CommonMethods.downloadOTS(ColDashboard.this, otsJsonObject);
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(ColDashboard.this,
                                        "No OTS records found", Toast.LENGTH_SHORT).show());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    runOnUiThread(() -> Toast.makeText(ColDashboard.this, "Something went wrong!", Toast.LENGTH_SHORT).show());

                }

            } catch (JSONException jexcpn) {
                jexcpn.printStackTrace();

            }

         /*   try {

                String[] custData = str[0].split("[;]");
                Log.d("Index0Data", custData[0]);
                //Log.d("DataAt4557", ""+custData[4557]);
                final int max = custData.length;


                if (custData.length > 1) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog1.setCancelable(false);
                            progressDialog1.setMessage("Downloading data don't press any key, please wait...");
                            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog1.setProgress(0);
                            progressDialog1.setMax(max);
                            progressDialog1.show();
                        }
                    });

                    for (int colonIndex = 0; colonIndex < custData.length; colonIndex++) {
                        progressDialog1.setProgress(colonIndex);
                        try {
                            if (colonIndex == 0) {
                                String[] singleData = custData[colonIndex].substring(6, custData[colonIndex].length()).split("[|]", -1);
                                insertCustomerData(singleData, false, true);

                            } else {
                                String[] singleData = custData[colonIndex].split("[|]", -1);
                                if (colonIndex == custData.length - 1) {
                                    insertCustomerData(singleData, true, false);
                                } else {
                                    insertCustomerData(singleData, false, false);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

//                            Toast.makeText(context, "ERROR : " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/


            return null;
        }
    }

    private void insertCustData(String division, String accNo, String consRef, String sdoCd, String sectionName, String name, String address1, String address2, Double currentAmount, Double totalAmount, Double rebate, String duedate, String mobileNo, String binder, int collectionCount, String bill_mth, int advance_count, int consumerFlag, String payDate, String lastPayAmount, String lastPayMode, String paymentRcpt, String scno, String latitude, String longitude, boolean doClose, boolean doOpen) {
        if (doOpen) {
            databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
        }
        String strSelectSQL_02 = "INSERT INTO CUST_DATA  " +
                " (USER_ID, DIVISION_CODE, CONS_ACC, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, CON_ADD2, PRSN_KWH, CUR_TOTAL, BILL_TOTAL , REBATE, DUE_DATE, MOBILE_NO, EMAIL, PAY_CNT, VTYPE, PYMT_DATE, PYMT_AMT, PYMT_MODE, PYMT_RCPT, COLOR_FLG, FIELD1,FIELD2, FIELD3)" +
                " VALUES('" + un + "','" + division + "','" + sdoCd + binder+accNo + "','" + consRef + "'," +
                " '" + division + "','" + sdoCd + "','" + sectionName + "','" + name + "','" + address1 + "','" + address2 + "'," +
                " '" + bill_mth + "', '" + currentAmount.toString() + "', '" + totalAmount.toString() + "', '" + rebate.toString() + "', '" + duedate + "', '" + mobileNo + "', '" + "email" + "', '" + collectionCount + "', '" + advance_count + "', '" + payDate + "', '" + lastPayAmount + "', '" + lastPayMode + "', '" + paymentRcpt + "', '" + consumerFlag + "', '" + scno + "', '" + latitude + "', '" + longitude + "') ";// "', '" + "1" +
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);
        if (doClose) {
            databaseAccess.close();
           // CommonMethods.saveBooleanPreference(this, isDataSynced, true);
         /*   Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                    startActivity(ColDashboard);*/
        }

    }




}


