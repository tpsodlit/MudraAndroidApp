package com.collection.tpwodloffline.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.GpsUtils;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class AccountInfo extends AppCompatActivity implements LocationListener {
    String SelChoice_string = "";
    String EntryNum_string = "";
    private TextView strtxtconsno;
    private TextView strtxtname;
    private TextView strtxtaddress;
    private TextView strtxtrebate;
    private TextView strtxtduedate;
    private EditText strtxtcurbill;
    private EditText strtxttotbill;
    private EditText strtxtamtpayble;
    private TextView sreb1;
    private TextView sduedt1;
    private TextView scbl1;
    private TextView stbl1;
    private TextView spbl1;
    private TextView lastPdate;
    private TextView lastPamount;
    private String lastPmtDate = "";
    private String lastPmtAmt = "";
    private DatabaseAccess databaseAccess = null;
    final Context context = this;
    String totBillAmt;
    //access for billig data
    public static final String AUTHORITY = "com.cesuodisha.demoapp.Dictionary";
    //public static final String PATH  = "/words";
    public static final String PATH = "/BILL_SBM_DATA";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + PATH);
    private String pybleamt = "";
    private String payment_count = "";
    private String vtype = "";
    private String cons_acc = "";
    private String custID = "";
    private String StrBillInfo = "";
    private String strCompanyId = "";
    private String div_code = "";
    private String cons_accfetch = "";
    private String Cons_idfetch = "";
    private String namefetch = "";
    private String add1fetch = "";
    private String add2fetch = "";
    private String billMonth = "";
    private String payblefetch = "";
    private String divisionfetch = "";
    private String subdivisionfetch = "";
    private String sectionfetch = "";
    private String Trans_IDfetch = "";
    private String rebatefetch = "";
    private String duedtfetch = "";
    private String curbillfetch = "";
    private String totbillfetch = "";
    private String BalFetch = "";
    private String usernm = "";
    private String MobileNo = "";
    private int billchkflg = 0;
    private String billflg = "0";
    private String EntryMob_string = "";
    private String usname = "";
    private String dbpwdnm = "";
    int limitAllowed = 30000;
    private String urlName = "";
    private String currentTotalBill = "";
    private String currentTotalBillRound = "";
    private String currentTotalBillDynamic = "";
    private String totalBillAmount = "";
    private String fromActivity = "";
    private boolean checkBoxClick = false;
    private CheckBox checkbox_manual;
    private String CA_server = "";
    private String DB_TYPE_Server = "";
    private String operationType = "";
    private String spinnerText = "NRML";
    SharedPreferenceClass sharedPreferenceClass;
    String username;
    Timer timer;
    LocationManager locationManager;
    Location location;
    String lat = "0.0";
    String lang = "0.0";
    ///////Location
    FusedLocationProviderClient mFusedLocationClient;
    String latitude, longitude, city, state, address;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private int locationRequestCode = 1000;
    private StringBuilder stringBuilder;
    private boolean isContinue = false;
    private boolean isGPS = false;


    private static final long MIN_TIME_BW_UPDATES = 2 * 1000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        //checkPermissions();
        LocationManager locationManager;
        Location location = null;
        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(AccountInfo.this);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Log.d("GPS Enabled", "GPS Enabled");
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (latitude != 0.0) {
                    sharedPreferenceClass.setValue_string("Latitude", String.valueOf(latitude));
                    sharedPreferenceClass.setValue_string("Longitude", String.valueOf(longitude));
                    // Toast.makeText(context, "Lat:- "+latitude+" Long:- "+longitude, Toast.LENGTH_SHORT).show();

                }
                //Toast.makeText(context, "Lat:- "+latitude+" Long:- "+longitude, Toast.LENGTH_SHORT).show();

            }
        }

    /*    if (timer != null) {
            timer.cancel();
            // Log.i("Main", "cancel timer");
            timer = null;
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                checkPermissions();

            }
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(AccountInfo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountInfo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AccountInfo.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST);

        } else {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

      /*  timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 900000); //auto logout in 15 minutes*/
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (latitude != 0.0) {
                //Toast.makeText(context, "Lat:- "+latitude+" Long:- "+longitude, Toast.LENGTH_SHORT).show();
                sharedPreferenceClass.setValue_string("Latitude", String.valueOf(latitude));
                sharedPreferenceClass.setValue_string("Longitude", String.valueOf(longitude));
            }
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(AccountInfo.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
    Button btnDenied;
    ArrayList<String> data_list = new ArrayList<>();
    Spinner drSpinner;
    String ReasonTxt = "";
    public DatePickerDialog datePickerDialog;
    public BottomSheetDialog mBottomSheetDialog;
    TextView textView21,textView25,textView27,textView_lpd,textView_lpa;
    ScrollView scrollview;
    int colorNow = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        data_list.add("Select Reason");
        data_list.add("Already Paid");
        data_list.add("Bill Dispute");
        data_list.add("Bill Not delivered");
        data_list.add("Double Consumer Account");
        data_list.add("Legal Cases");
        data_list.add("Enforcement Cases");
        data_list.add("Power Supply Issue");
        data_list.add("Premises Locked");
        data_list.add("Promised To Pay"); //prospective date of payment from calander
        data_list.add("Refused To Pay");
        data_list.add("Ghost Consumer");
        data_list.add("Wrong Reading");
        data_list.add("Others"); // Remarks mandatory

        scrollview = (ScrollView) findViewById(R.id.scrollview);
        textView21 = findViewById(R.id.textView21);
        textView25 = findViewById(R.id.textView25);
        textView27 = findViewById(R.id.textView27);
        textView_lpd = findViewById(R.id.textView_lpd);
        textView_lpa = findViewById(R.id.textView_lpa);

        sharedPreferenceClass = new SharedPreferenceClass(AccountInfo.this);
        Toolbar toolbarback = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        urlName = savedUrl.getString("savedUrl", null); // getting String
        username = sharedPreferenceClass.getValue_string("un");

        StrBillInfo = "";

        btnDenied = findViewById(R.id.btnDenied);
        strtxtconsno = findViewById(R.id.consno);
        strtxtname = findViewById(R.id.name);
        strtxtaddress = findViewById(R.id.address);
        strtxtrebate = findViewById(R.id.rebate);
        strtxtduedate = findViewById(R.id.duedate);
        strtxtcurbill = findViewById(R.id.curbill);
        strtxttotbill = findViewById(R.id.totbill);
        strtxtamtpayble = findViewById(R.id.amtpayble);
        lastPamount = findViewById(R.id.lastpamt);
        lastPdate = findViewById(R.id.lastpdate);
        SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor ssodata = sessionssodata.edit();
        usname = sessionssodata.getString("usname", null); // getting String
        dbpwdnm = sessionssodata.getString("dbpwdnm", null);

        Button btnSearch = (Button) findViewById(R.id.btnSubmit);
        sreb1 = (TextView) findViewById(R.id.reb1);
        sduedt1 = (TextView) findViewById(R.id.duedt1);
        scbl1 = (TextView) findViewById(R.id.cbl1);
        stbl1 = (TextView) findViewById(R.id.tbl1);
        spbl1 = (TextView) findViewById(R.id.pbl1);
        checkbox_manual = findViewById(R.id.checkbox_manual);
        checkBoxClick = false;
        checkbox_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkBoxClick = true;
                } else {
                    checkBoxClick = false;
                }
            }
        });


        SelChoice_string = "";
        Bundle extrasvalcol = getIntent().getExtras();
        SelChoice_string = extrasvalcol.getString("SelChoice");
        EntryNum_string = extrasvalcol.getString("EntryNum");
        EntryMob_string = extrasvalcol.getString("MobNonew");
        fromActivity = extrasvalcol.getString("from");
        cons_acc = extrasvalcol.getString("EntryNum");
        namefetch = "";
        add1fetch = "";
        add2fetch = "";
        payblefetch = "";

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        /*String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "' WHERE USERID='" + "usernm" + "'";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);*/

        String strSelectSQL_01 = "SELECT BAL_REMAIN  " +
                "FROM SA_USER  WHERE USERID='" + username + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {
            //CompanyID = cursor.getString(2);
            //CompanyID = cursor.getString(2);
            // CompanyID = "3";
            BalFetch = cursor.getString(0);
            Log.i("BalanceRemained", BalFetch);
            Log.i("BalanceRemained:::", BalFetch);

        }

        databaseAccess.close();

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String toDayDt = sessiondata.getString("toDayDt", null); // getting String
        //  String prv_bill=sessiondata.getString("prv_bill", null);+

        SharedPreferences sessiondata1 = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor editor1 = sessiondata1.edit();
        String prv_bill = sessiondata1.getString("sbmBlPrv", null);
        if (prv_bill == null || prv_bill.equals("")) {
            prv_bill = "0";
        }
        Log.d("DemoApp", "prv_bill" + prv_bill);
        usernm = sessiondata.getString("userID", null);
        // String pwd=sessiondata.getString("prv_bill", null);
        strCompanyId = CommonMethods.CompanyID;
        div_code = sessiondata.getString("div_code", null);
        String imeinum = sessiondata.getString("imeinum", null);

        if (SelChoice_string.equals("AcctNo")) {

            // fetching information from billing app
            if (!prv_bill.equals("1")) {
                billchkflg = 0;
                try {
                    //Cursor mCursor = getContentResolver().query(CONTENT_URI, null, "cons_acc='" + EntryNum_string + "' and bill_flag=1", null, null, null); // changed on 130619 here app  will allow for bill DB consumer for payment only
                    Cursor mCursor = getContentResolver().query(CONTENT_URI, null, "cons_acc='" + EntryNum_string + "' ", null, null, null);
                    Log.d("DemoApp", " billflg1   " + billflg);
                    if (mCursor.moveToFirst()) {
                        do {
                            billflg = mCursor.getString(84);//bill prepared
                            Log.d("DemoApp", " billflg222   " + billflg);
                            if (billflg.equals("1")) {
                                cons_accfetch = mCursor.getString(1);
                                namefetch = mCursor.getString(2);
                                add1fetch = mCursor.getString(3);
                                add2fetch = mCursor.getString(4);
                                rebatefetch = mCursor.getString(68);
                                duedtfetch = mCursor.getString(76);
                                curbillfetch = mCursor.getString(67);
                                totbillfetch = mCursor.getString(69);
                                //custID = mCursor.getString(46);
                                billchkflg = 1;//bill prepared
                            } else {
                                cons_accfetch = mCursor.getString(1);
                                namefetch = mCursor.getString(2);
                                add1fetch = mCursor.getString(3);
                                add2fetch = mCursor.getString(4);
                                //rebatefetch = "0";
                                duedtfetch = "0";
                                curbillfetch = "0";
                                totbillfetch = "0";
                                //custID = mCursor.getString(46);
                                billchkflg = 2;//bill not prepared
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("Consumer Not Billed");
                                alertDialogBuilder.setMessage("Please do the Bill")
                                        .setCancelable(false)
                                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(new Intent(getApplicationContext(), AcCollection.class));
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(new Intent(getApplicationContext(), CollectionDashBoard.class));
                                                finish();
                                            }
                                        });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }

                            if (EntryNum_string.trim().length() == 12) {
                                Cons_idfetch = EntryNum_string;
                            } else {
                                // Cons_idfetch=div_code+"S"+EntryNum_string;
                                //Cons_idfetch="sdocode"+"binder"+"account";
                            }

                            Log.d("DemoApp", " custID   " + Cons_idfetch);
                            Log.d("DemoApp", " billchkflg   " + billchkflg);
                            //   words.add(word);
                        } while (mCursor.moveToNext());
                        mCursor.close();
                        strtxtconsno.setText(EntryNum_string);
                        strtxtname.setText(namefetch);
                        strtxtaddress.setText(add1fetch);
                        strtxtrebate.setText(rebatefetch);
                        strtxtduedate.setText(duedtfetch);
                        Double payble = 0.0;
                        try {
                            // have created trans_id but it is online so commented on 070619
                            //DateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
                            //  Trans_IDfetch=dateFormat.format(new Date());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Date date1 = sdf.parse(duedtfetch);
                            Date date2 = sdf.parse(toDayDt);
                            Log.d("DemoApp", "date1" + sdf.format(date1));
                            Log.d("DemoApp", "date2" + sdf.format(date2));

                            if (date1.compareTo(date2) > 0) {
                                Log.d("DemoApp", "Date1 is after Date2");
                                payble = Math.ceil(Double.parseDouble(totbillfetch) - Double.parseDouble(rebatefetch));
                            } else if (date1.compareTo(date2) <= 0) {
                                Log.d("DemoApp", "Date1 is before Date2");
                                payble = Math.ceil(Double.parseDouble(totbillfetch));
                            }
                            Log.d("DemoApp", " payble   " + payble);
                        } catch (Exception e) {

                        }
                        payblefetch = payble.toString();
                        strtxtcurbill.setText(curbillfetch);
                        strtxttotbill.setText(totbillfetch);
                        strtxtamtpayble.setText(payblefetch);
                        //strtxtamtpayble.setClickable(false);// non editable
                        // strtxtamtpayble.setFocusable(false);// non editable
                        //   strtxtamtpayble.setEnabled(false);// non editable
                        // online calling happend to get trans id online Dt 070619
                        //Add code
                        if (billchkflg == 1) {
                            StrBillInfo = StrBillInfo + "un=" + usname + "&pw=" + dbpwdnm + "&CompanyID=" + strCompanyId + "&ConsumerID=" + div_code + "S" + EntryNum_string + "&imei=" + imeinum + "&mosarkar=0&mobile_no=" + EntryMob_string;
                            Log.d("DemoApp", "in Loop AuthURL" + StrBillInfo);
                            try {
                                //new FetchBillDetOnline().execute(StrBillInfo);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Payment Cannot be made");
                        alertDialogBuilder.setMessage("Consumer Not found in Billing File")
                                .setCancelable(false)
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), AcCollection.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), CollectionDashBoard.class));
                                        finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    /* disabled to prevent the consumer online fetching
                    Toast.makeText(AccountInfo.this, "Consumer Not found in Billing APP "+"\n"+"Consumer Details checking online ", Toast.LENGTH_LONG).show();
                    StrBillInfo="http://portal.tpcentralodisha.com:8080/IncomingSMS/CESU_BillInfo.jsp?un=TEST&pw=TEST&CompanyID="+strCompanyId+"&ConsumerID="+div_code+"S"+EntryNum_string+"&imei="+imeinum;
                    Log.d("DemoApp", "in Loop AuthURL" + StrBillInfo);
                    try {
                        new FetchBillDetOnline().execute(StrBillInfo);

                    } catch (Exception e) {  e.printStackTrace(); }
                    sreb1.setVisibility(View.GONE);
                    sduedt1.setVisibility(View.GONE);
                    scbl1.setVisibility(View.GONE);
                    stbl1.setVisibility(View.GONE);
                    strtxtrebate.setVisibility(View.GONE);
                    strtxtduedate.setVisibility(View.GONE);
                    strtxttotbill.setVisibility(View.GONE);
                    strtxtcurbill.setVisibility(View.GONE);
                    strtxtconsno.setText(EntryNum_string);
                    */
                    }
                } catch (Exception e) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Billing Application Not found");
                    alertDialogBuilder.setMessage("Please Load Billing APP" + "\n" + "then retry")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(getApplicationContext(), ColDashboard.class));
                                    finish();
                                    //dialog.cancel();
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
                }
            } else {
                //consumer details checking online
                //Narendra

                StrBillInfo = StrBillInfo + "un=" + usname + "&pw=" + dbpwdnm + "&CompanyID=" + strCompanyId + "&ConsumerID=" + Cons_idfetch + "&imei=" + imeinum + "&mosarkar=0&mobile_no=" + EntryMob_string;
                Log.d("DemoApp", "in Loop AuthURL1" + StrBillInfo);
                sreb1.setVisibility(View.GONE);
                sduedt1.setVisibility(View.GONE);
                scbl1.setVisibility(View.GONE);
                stbl1.setVisibility(View.GONE);
                strtxtrebate.setVisibility(View.VISIBLE);
                strtxtduedate.setVisibility(View.GONE);
                strtxttotbill.setVisibility(View.GONE);
                strtxtcurbill.setVisibility(View.GONE);
                strtxtconsno.setText(EntryNum_string);
                try {
                    //new FetchBillDetOnline().execute(StrBillInfo);
                    setBillInfromFromLocal();
                } catch (Exception e) {
                }

            }
            /////ending
        } else {
            if (SelChoice_string.equals("Asd")) {
                spbl1.setText("ASD Amount");
            } else if (SelChoice_string.equals("Adv")) {
                spbl1.setText("Advance Amount");
            } else if (SelChoice_string.equals("rcf")) {
                spbl1.setText("Reconnection Fee ");
            } else if (SelChoice_string.equals("Assmnt")) {
                spbl1.setText("Assesment Bill");
            }

            StrBillInfo = StrBillInfo + "un=" + usname + "&pw=" + dbpwdnm + "&CompanyID=" + strCompanyId + "&ConsumerID=" + Cons_idfetch + "&imei=" + imeinum + "&mosarkar=0&mobile_no=" + EntryMob_string;
            Log.d("DemoApp", "in Loop AuthURL2" + StrBillInfo);
            try {
                // new FetchBillDetOnline().execute(StrBillInfo);
            } catch (Exception e) {
            }
            sreb1.setVisibility(View.GONE);
            sduedt1.setVisibility(View.GONE);
            scbl1.setVisibility(View.GONE);
            stbl1.setVisibility(View.GONE);
            strtxtrebate.setVisibility(View.GONE);
            strtxtduedate.setVisibility(View.GONE);
            strtxttotbill.setVisibility(View.GONE);
            strtxtcurbill.setVisibility(View.GONE);
            strtxtconsno.setText(EntryNum_string);

        }
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
                    lat = sharedPreferenceClass.getValue_string("Latitude");
                    lang = sharedPreferenceClass.getValue_string("Longitude");

                    if (lat.equals("")) {
                        lat = "0.0";
                    }

                    if (lang.equals("")) {
                        lang = "0.0";
                    }

                    String time = getCurrentTime();
                    int payble = 0;
                    pybleamt = strtxtamtpayble.getText().toString().trim();
                    //BalFetch="8000";
                    Log.d("DemoApp", "BalFetch" + BalFetch);
                    //Log.d("DemoApp", "pybleamt" + pybleamt);
                    //if(Double.parseDouble(BalFetch)<Double.parseDouble(pybleamt)){
                    if (getBillCount(EntryNum_string) > 0) {
                        showDialognow();
                    }
                    else if (Double.parseDouble(BalFetch) < Double.parseDouble(pybleamt)) {//Narendra: Commented above line because this condition need not to be checked in offline case.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Balance Not Available");
                        alertDialogBuilder.setMessage("Deposit Cash and Contact Divisional / Agency" + "\n" + "Finance Section")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                               /* .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });*/
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }else if (Double.parseDouble(pybleamt)>=Double.parseDouble("200000")) {
                        showITWarnDialognow();

                    }
                    else if (payment_count.equals("0")) {
                        String currBill = strtxtcurbill.getText().toString();
                        String totBill = strtxttotbill.getText().toString();

                        Double dCB = 0.0;
                        Double dTB = 0.0;

                        try {
                            dCB = Double.parseDouble(currBill);
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                        try {
                            dTB = Double.parseDouble(totBill);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (dTB < dCB) {
                            currentTotalBillDynamic = String.valueOf(Math.floor(dTB + Double.parseDouble(strtxtrebate.getText().toString())));
                            //currentTotalBillDynamic = String.valueOf(dTB + Double.parseDouble(strtxtrebate.getText().toString()));
                        }

                        //  if (Double.parseDouble(pybleamt) >= Double.parseDouble(payblefetch) && Double.parseDouble(pybleamt) > 0) {
                        // if(Double.parseDouble(currentTotalBillDynamic) > Double.parseDouble((strtxtcurbill.getText().toString())))
                        if ((Double.parseDouble(pybleamt)) < Double.parseDouble(currentTotalBillDynamic)) {
                            Toast.makeText(AccountInfo.this, "Payable amount should not  be less than current bill", Toast.LENGTH_SHORT).show();

                        } else if (Double.parseDouble(pybleamt) > 0 && Double.parseDouble(pybleamt) <= Double.parseDouble(totBillAmt)) {
                            Log.d("DemoApp", "pybleamt SQL " + pybleamt);
                            //   isExistsHeader(cons_accfetch);
                            //int BalFetch_int = Integer.parseInt(BalFetch) - Integer.parseInt(strtxtamtpayble.getText().toString());
                            //BalFetch = String.valueOf(BalFetch_int);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Confirmation");
                            alertDialogBuilder.setMessage("You have collected ₹" + pybleamt + " from consumer.")
                                    .setCancelable(false)
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            databaseAccess = DatabaseAccess.getInstance(context);
                                            databaseAccess.open();
                                            if(getTxnIdCount(Trans_IDfetch)>0){
                                                Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
                                            }
                                            String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                                    " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,COLL_MONTH,TOT_PAID,TRANS_ID,RECPT_FLG,TRANS_DATE,RECPT_DATE,RECPT_TIME,CA_SERVER,DB_TYPE_SERVER,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE,LONGITUDE)" +
                                                    " VALUES('" + cons_accfetch + "','" + Cons_idfetch + "','" + divisionfetch + "','" + subdivisionfetch + "'," +
                                                    " '" + sectionfetch + "','" + namefetch.replace("'", "''") + "','" + add1fetch.replace("'", "''") + "','" + add2fetch.replace("'", "''") + "','" + billMonth + "','" + pybleamt + "','" + Trans_IDfetch + "',0,date('now'),date('now'),'" + time + "','" + CA_server + "','" + payment_count + "','" + vtype + "','" + spinnerText + "','" + lat + "','" + lang + "' ) ";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                                            strSelectSQL_02 = "";
                                            strSelectSQL_02 = "UPDATE COLL_SBM_DATA SET PHONE_NO ='" + EntryMob_string + "' WHERE CUST_ID='" + Cons_idfetch + "'";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                                            databaseAccess.close();
                                            if (checkbox_manual.isChecked()) {
                                                checkBoxClick = true;
                                            }

                                            Intent paysumry = new Intent(getApplicationContext(), PaySummary.class);
                                            Bundle Bunpayamtdtls = new Bundle();
                                            Bunpayamtdtls.putString("Pableamt", pybleamt);
                                            Bunpayamtdtls.putString("consacc", cons_accfetch);
                                            Bunpayamtdtls.putString("custID", Cons_idfetch);
                                            Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                                            Bunpayamtdtls.putString("SelChoice", SelChoice_string);
                                            Bunpayamtdtls.putString("BalFetch", BalFetch);
                                            Bunpayamtdtls.putString("namefetch", namefetch);
                                            Bunpayamtdtls.putString("MobileNofetch", MobileNo);
                                            Bunpayamtdtls.putString("from", fromActivity);
                                            Bunpayamtdtls.putBoolean("manual", checkBoxClick);
                                            Bunpayamtdtls.putString("PayFlag", "EnergyNRML");

                                            // extrasval.putString("Validcon", "0");

                                            System.out.println("account==" + Cons_idfetch);
                                            paysumry.putExtras(Bunpayamtdtls);
                                            startActivity(paysumry);
                                            finish();

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();

                            //finish();
                        } else if (Double.parseDouble(pybleamt) > 0 && Double.parseDouble(pybleamt) > Double.parseDouble(totBillAmt)) {
                            String extra = String.valueOf(Double.parseDouble(pybleamt) - Double.parseDouble(totBillAmt));
                            String[] ext = extra.split("\\.");
                            String extval = ext[0];
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Confirmation \n₹" + extval + " as Advance Payment");
                            alertDialogBuilder.setMessage("You have collected ₹" + pybleamt + " from consumer which includes ₹" + extval + " more than payable amount as advance payment.")
                                    .setCancelable(false)
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Log.d("DemoApp", "pybleamt SQL " + pybleamt);
                                            //   isExistsHeader(cons_accfetch);
                                            // int BalFetch_int = Integer.parseInt(BalFetch) - Integer.parseInt(strtxtamtpayble.getText().toString());
                                            //BalFetch = String.valueOf(BalFetch_int);
                                            databaseAccess = DatabaseAccess.getInstance(context);
                                            databaseAccess.open();
                                            if(getTxnIdCount(Trans_IDfetch)>0){
                                                Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
                                            }
                                            String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                                    " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,COLL_MONTH,TOT_PAID,TRANS_ID,RECPT_FLG,TRANS_DATE,RECPT_DATE,RECPT_TIME,CA_SERVER,DB_TYPE_SERVER,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE,LONGITUDE)" +
                                                    " VALUES('" + cons_accfetch + "','" + Cons_idfetch + "','" + divisionfetch + "','" + subdivisionfetch + "'," +
                                                    " '" + sectionfetch + "','" + namefetch.replace("'", "''") + "','" + add1fetch.replace("'", "''") + "','" + add2fetch.replace("'", "''") + "','" + billMonth + "','" + pybleamt + "','" + Trans_IDfetch + "',0,date('now'),date('now'),'" + time + "','" + CA_server + "','" + payment_count + "','" + vtype + "','" + spinnerText + "','" + lat + "','" + lang + "' ) ";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);
                                          /*  strSelectSQL_02 = "";
                                            strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + BalFetch + "' WHERE USERID='" + usernm + "'";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);
*/
                                            strSelectSQL_02 = "";
                                            strSelectSQL_02 = "UPDATE COLL_SBM_DATA SET PHONE_NO ='" + EntryMob_string + "' WHERE CUST_ID='" + Cons_idfetch + "'";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                                            databaseAccess.close();
                                            if (checkbox_manual.isChecked()) {
                                                checkBoxClick = true;
                                            }
                                            dialog.cancel();

                                            Intent paysumry = new Intent(getApplicationContext(), PaySummary.class);
                                            Bundle Bunpayamtdtls = new Bundle();
                                            Bunpayamtdtls.putString("Pableamt", pybleamt);
                                            Bunpayamtdtls.putString("consacc", cons_accfetch);
                                            Bunpayamtdtls.putString("custID", Cons_idfetch);
                                            Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                                            Bunpayamtdtls.putString("SelChoice", SelChoice_string);
                                            Bunpayamtdtls.putString("BalFetch", BalFetch);
                                            Bunpayamtdtls.putString("namefetch", namefetch);
                                            Bunpayamtdtls.putString("MobileNofetch", MobileNo);
                                            Bunpayamtdtls.putString("from", fromActivity);
                                            Bunpayamtdtls.putBoolean("manual", checkBoxClick);
                                            Bunpayamtdtls.putString("PayFlag", "EnergyNRML");

                                            // extrasval.putString("Validcon", "0");

                                            System.out.println("account==" + Cons_idfetch);
                                            paysumry.putExtras(Bunpayamtdtls);
                                            startActivity(paysumry);
                                            finish();
                                            //finish();


                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        } else {
                            strtxtamtpayble.setError("Enter Amount Cannot be less or Zero");
                        }
                    }
                    else if ((payment_count.equals("1")))
                    {
                        double minpaybleamt = 0.0;
                        //Change request dated 1309 minimum amount Rs100 or total arrears whichever is less.
                        if(Double.parseDouble(totBillAmt)> 100) {
                            minpaybleamt = 100;
                        }
                        else if(Double.parseDouble(totBillAmt) < 100 && Double.parseDouble(totBillAmt) > 0){
                            minpaybleamt = Double.parseDouble(totBillAmt);
                        }
                        //if (Double.parseDouble(pybleamt) >= Double.parseDouble(payblefetch) && Double.parseDouble(pybleamt) > 0) {
                        //if (Double.parseDouble(pybleamt) > 19 && Double.parseDouble(pybleamt) <= Double.parseDouble(totBillAmt)) {

                        if (Double.parseDouble(pybleamt) >= minpaybleamt) {
                            Log.d("DemoApp", "pybleamt SQL " + pybleamt);
                            //isExistsHeader(cons_accfetch);
                            //int BalFetch_int = Integer.parseInt(BalFetch) - Integer.parseInt(strtxtamtpayble.getText().toString());
                            //BalFetch = String.valueOf(BalFetch_int);
                            //////start
                            try {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("Confirmation");
                                alertDialogBuilder.setMessage("You have collected ₹" + pybleamt + " from consumer.")
                                        .setCancelable(false)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                databaseAccess = DatabaseAccess.getInstance(context);
                                                databaseAccess.open();
                                                if(getTxnIdCount(Trans_IDfetch)>0){
                                                    Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
                                                }
                                                String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                                        " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME,CON_ADD1,CON_ADD2,COLL_MONTH,TOT_PAID,TRANS_ID,RECPT_FLG,TRANS_DATE,RECPT_DATE,RECPT_TIME,CA_SERVER,DB_TYPE_SERVER,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE,LONGITUDE)" +
                                                        " VALUES('" + cons_accfetch + "','" + Cons_idfetch + "','" + divisionfetch + "','" + subdivisionfetch + "'," +
                                                        " '" + sectionfetch + "','" + namefetch.replace("'", "''") + "','" + add1fetch.replace("'", "''") + "','" + add2fetch.replace("'", "''") + "','" + billMonth + "','" + pybleamt + "','" + Trans_IDfetch + "',0,date('now'),date('now'),'" + time + "','" + CA_server + "','" + payment_count + "','" + vtype + "','" + spinnerText + "','" + lat + "','" + lang + "'); ";
                                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                                DatabaseAccess.database.execSQL(strSelectSQL_02);

                                                strSelectSQL_02 = "";
                                                strSelectSQL_02 = "UPDATE COLL_SBM_DATA SET PHONE_NO ='" + EntryMob_string + "' WHERE CUST_ID='" + Cons_idfetch + "'";
                                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                                DatabaseAccess.database.execSQL(strSelectSQL_02);

                                                databaseAccess.close();
                                                if (checkbox_manual.isChecked()) {
                                                    checkBoxClick = true;
                                                }

                                                Intent paysumry = new Intent(getApplicationContext(), PaySummary.class);
                                                Bundle Bunpayamtdtls = new Bundle();
                                                Bunpayamtdtls.putString("Pableamt", pybleamt);
                                                Bunpayamtdtls.putString("consacc", cons_accfetch);
                                                Bunpayamtdtls.putString("custID", Cons_idfetch);
                                                Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                                                Bunpayamtdtls.putString("SelChoice", SelChoice_string);
                                                Bunpayamtdtls.putString("BalFetch", BalFetch);
                                                Bunpayamtdtls.putString("namefetch", namefetch);
                                                Bunpayamtdtls.putString("MobileNofetch", MobileNo);
                                                Bunpayamtdtls.putString("from", fromActivity);
                                                Bunpayamtdtls.putBoolean("manual", checkBoxClick);
                                                Bunpayamtdtls.putString("PayFlag", "EnergyNRML");

                                                // extrasval.putString("Validcon", "0");

                                                System.out.println("account==" + Cons_idfetch);
                                                paysumry.putExtras(Bunpayamtdtls);
                                                startActivity(paysumry);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();

                                            }
                                        });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                                ////////end
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        else if (Double.parseDouble(pybleamt) > minpaybleamt && Double.parseDouble(pybleamt) > Double.parseDouble(totBillAmt)) {
                            String extra = String.valueOf(Double.parseDouble(pybleamt) - Double.parseDouble(totBillAmt));
                            String[] ext = extra.split("\\.");
                            String extval = ext[0];

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Confirmation \n₹" + extval + " as Advance Payment");
                            alertDialogBuilder.setMessage("You have collected ₹" + pybleamt + " from consumer which includes ₹" + extval + " more than payable amount as advance payment.")
                                    .setCancelable(false)
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Log.d("DemoApp", "pybleamt SQL " + pybleamt);
                                            //   isExistsHeader(cons_accfetch);
                                            //int BalFetch_int = Integer.parseInt(BalFetch) - Integer.parseInt(strtxtamtpayble.getText().toString());
                                            // BalFetch = String.valueOf(BalFetch_int);
                                            databaseAccess = DatabaseAccess.getInstance(context);
                                            databaseAccess.open();
                                            if(getTxnIdCount(Trans_IDfetch)>0){
                                                Trans_IDfetch = usernm + CommonMethods.getMilliSeconds();
                                            }
                                            String strSelectSQL_02 = "INSERT INTO COLL_SBM_DATA  " +
                                                    " (CONS_ACC,CUST_ID,Division,Subdivision,section,CON_NAME," +
                                                    "CON_ADD1,CON_ADD2,COLL_MONTH,TOT_PAID,TRANS_ID,RECPT_FLG," +
                                                    "TRANS_DATE,RECPT_DATE,RECPT_TIME,CA_SERVER,DB_TYPE_SERVER," +
                                                    "OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE,LONGITUDE)" +
                                                    " VALUES('" + cons_accfetch + "','" + Cons_idfetch + "','" +
                                                    divisionfetch + "','" + subdivisionfetch + "'," +
                                                    " '" + sectionfetch + "','" + namefetch.replace("'", "''") +
                                                    "','" + add1fetch.replace("'", "''") + "','" +
                                                    add2fetch.replace("'", "''") + "','" + billMonth + "','"
                                                    + pybleamt + "','" + Trans_IDfetch + "',0,date('now'),date('now'),'" + time
                                                    + "','" + CA_server + "','" + payment_count + "','" + vtype + "','" +
                                                    spinnerText + "','" + lat + "','" + lang + "') ";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);
                                            strSelectSQL_02 = "";
                                           /* strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + BalFetch + "' WHERE USERID='" + usernm + "'";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);*/

                                            strSelectSQL_02 = "";
                                            strSelectSQL_02 = "UPDATE COLL_SBM_DATA SET PHONE_NO ='" + EntryMob_string + "' WHERE CUST_ID='" + Cons_idfetch + "'";
                                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                                            databaseAccess.close();
                                            if (checkbox_manual.isChecked()) {
                                                checkBoxClick = true;
                                            }
                                            dialog.cancel();

                                            Intent paysumry = new Intent(getApplicationContext(), PaySummary.class);
                                            Bundle Bunpayamtdtls = new Bundle();
                                            Bunpayamtdtls.putString("Pableamt", pybleamt);
                                            Bunpayamtdtls.putString("consacc", cons_accfetch);
                                            Bunpayamtdtls.putString("custID", Cons_idfetch);
                                            Bunpayamtdtls.putString("TransID", Trans_IDfetch);
                                            Bunpayamtdtls.putString("SelChoice", SelChoice_string);
                                            Bunpayamtdtls.putString("BalFetch", BalFetch);
                                            Bunpayamtdtls.putString("namefetch", namefetch);
                                            Bunpayamtdtls.putString("MobileNofetch", MobileNo);
                                            Bunpayamtdtls.putString("from", fromActivity);
                                            Bunpayamtdtls.putBoolean("manual", checkBoxClick);
                                            Bunpayamtdtls.putString("PayFlag", "EnergyNRML");

                                            // extrasval.putString("Validcon", "0");

                                            System.out.println("account==" + Cons_idfetch);
                                            paysumry.putExtras(Bunpayamtdtls);
                                            startActivity(paysumry);
                                            finish();
                                            //finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        } else {
                            strtxtamtpayble.setError("Amount cannot be less than "+ String.valueOf(minpaybleamt));
                        }

                    } else {
                        Toast.makeText(context, "Collection not allowed more than two times for one consumer", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Error Occurred");
                    alertDialogBuilder.setMessage("Intimate the case to IT center" + "\n" + "for checking")
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

    private void showITWarnDialognow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Cash transaction/collection is not allowed more than 2 Lakh")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                               /* .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });*/
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    private void showDialognow() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Collection limit");
        alertDialogBuilder.setMessage("You can collect only once in a day from one consumer.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private int getTxnIdCount(String trans_ID) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where TRANS_ID=" +trans_ID+ "";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("Query SQL", strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private int getBillCount(String entryNum_string) {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where CONS_ACC = '" + entryNum_string + "' and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', recpt_date) and RECPT_FLG=1";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        //date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String localTime = date.format(currentLocalTime);

        return localTime;

    }

    private void setBillInfromFromLocal() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select * from CUST_DATA where CONS_ACC='" + EntryNum_string + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        String mobileNum = "";
        while (cursor.moveToNext()) {
            String userid = cursor.getString(0);
            String divcode = cursor.getString(1);
            String consno = cursor.getString(2);
            strtxtconsno.setText(cursor.getString(2));
            cons_accfetch = cursor.getString(2);//cons_acc 0046
            Cons_idfetch = cursor.getString(3);//cos id 10digit
            divisionfetch = cursor.getString(4);
            subdivisionfetch = cursor.getString(5);
            sectionfetch = cursor.getString(6);
            namefetch = cursor.getString(7);
            add1fetch = cursor.getString(8);
            add2fetch = cursor.getString(9);
            billMonth = cursor.getString(10);
            payment_count = cursor.getString(17);
            vtype = cursor.getString(18);
            pybleamt = cursor.getString(12);
            MobileNo = EntryMob_string;
            String trid = cursor.getString(2);
            String dueDate = cursor.getString(14).trim();
            String color_flag = cursor.getString(23); //or 16 also
            lastPmtDate = cursor.getString(19);
            lastPmtAmt = cursor.getString(20);
            Log.v("payment", payment_count);
            Log.v("pybleamt", pybleamt);

            switch (color_flag) {
                case "0":
                    scrollview.setBackgroundColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView21.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    strtxtconsno.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    textView25.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    strtxtname.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    textView27.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    strtxtaddress.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    sreb1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    strtxtrebate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    sduedt1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    strtxtduedate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    textView_lpd.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    lastPdate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    textView_lpa.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.txt_color));
                    lastPamount.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));


                    scbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.red));
                    strtxtcurbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    stbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.red));
                    strtxttotbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    spbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.red));
                    strtxtamtpayble.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));


                    break;
                case "1":
                    scrollview.setBackgroundColor(ContextCompat.getColor(AccountInfo.this, R.color.color_one));
                    textView21.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtconsno.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView25.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtname.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView27.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtaddress.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    sreb1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtrebate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    sduedt1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtduedate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView_lpd.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    lastPdate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView_lpa.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    lastPamount.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));



                    scbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtcurbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_two));
                    stbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxttotbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_two));
                    spbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtamtpayble.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));


                    break;
                case "2":
                    scrollview.setBackgroundColor(ContextCompat.getColor(AccountInfo.this, R.color.color_two));
                    textView21.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    strtxtconsno.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    textView25.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    strtxtname.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    textView27.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    strtxtaddress.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    sreb1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    strtxtrebate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    sduedt1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    strtxtduedate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    textView_lpd.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    lastPdate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    textView_lpa.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));
                    lastPamount.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));


                    scbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtcurbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    stbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxttotbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_txt));
                    spbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtamtpayble.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.black));

                    break;
                case "3":
                    scrollview.setBackgroundColor(ContextCompat.getColor(AccountInfo.this, R.color.color_three));
                    textView21.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtconsno.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView25.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtname.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView27.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtaddress.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    sreb1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtrebate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    sduedt1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    strtxtduedate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView_lpd.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    lastPdate.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    textView_lpa.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));
                    lastPamount.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));


                    scbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtcurbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_two));
                    stbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxttotbill.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.gray_two));
                    spbl1.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.redptn));
                    strtxtamtpayble.setTextColor(ContextCompat.getColor(AccountInfo.this, R.color.white));

                    break;
            }

            String addOne = (cursor.getString(8) + "\n" + cursor.getString(9));
            strtxtname.setText(cursor.getString(7));
            strtxtaddress.setText(addOne);
            lastPdate.setText(lastPmtDate);
            lastPamount.setText(lastPmtAmt);
            //strtxtaddress.setText(cursor.getString(8) + "\n" + cursor.getString(9));

            //Bill info
            strtxtcurbill.setText(cursor.getString(11));
            strtxtconsno.setText(EntryNum_string);

            currentTotalBill = cursor.getString(11);
            totalBillAmount = cursor.getString(12);////total bill

            double curbill = Float.parseFloat(currentTotalBill);
            long intcurbill = (long) Math.floor(curbill);
            currentTotalBillRound = String.valueOf(intcurbill);

            //double totBill = Math.ceil(Float.parseFloat(totalBillAmount));
            //long inttotBill = Math.round(Float.parseFloat(totalBillAmount)); //5jan2022
            long inttotBill = (long) Math.floor(Float.parseFloat(totalBillAmount));
            totBillAmt = String.valueOf(inttotBill);

            // String[] ss = totalBillAmount.split("\\.");
            //  totBillAmt = ss[0];
            Log.v("value", totBillAmt);

            //String[] separated = name.split(" ");
            //String lt = String.valueOf(separated.length);
            //  String f_name = separated[0];


            if (!(dueDate.equalsIgnoreCase("Null"))) {

                Log.v("dateone:::", CommonMethods.convertStringToDate(cursor.getString(14)).toString());
                Log.v("todaydate", (CommonMethods.getTodaysPlainDate()).toString());

                if (CommonMethods.convertStringToDate(cursor.getString(14)).after(CommonMethods.getTodaysPlainDate()) ||
                        CommonMethods.convertStringToDate(cursor.getString(14)).equals(CommonMethods.getTodaysPlainDate())) {
                    if (payment_count.equals("0")) {
                        //rebate//
                        strtxtrebate.setText(cursor.getString(13));
                        strtxtduedate.setText(CommonMethods.getFormattedDate(cursor.getString(14)));
                        String s = strtxtrebate.getText().toString(); //-10.40
                        double rebate1 = 0;

                        if (!s.isEmpty()) {
                            double rbt = (Double.parseDouble(strtxtrebate.getText().toString()));
                            rebate1 = rbt;
                            //rebate1 = Integer.parseInt(String.valueOf(rbt));
                            //    reb = Long.parseLong(s);
                        }
                        int currenttotalAmount = Integer.parseInt(currentTotalBillRound);
                        double outvalue = Math.floor(curbill + rebate1);
                        currentTotalBillDynamic = String.valueOf(outvalue);
                    } else {
                        //no rebate//
                        strtxtrebate.setText("0");
                        strtxtduedate.setText(duedtfetch);
                        currentTotalBillDynamic = currentTotalBillRound;
                        strtxtamtpayble.setText(totBillAmt);
                    }

                } else {
                    //no rebate//
                    strtxtrebate.setText("0");
                    strtxtduedate.setText(duedtfetch);
                    currentTotalBillDynamic = currentTotalBillRound;
                    strtxtamtpayble.setText(totBillAmt);
                }

                strtxtduedate.setText(CommonMethods.getFormattedDate(cursor.getString(14)));
            }

            //Due date
          /*  if (((Integer.parseInt(currentTotalBill)==0))||((Integer.parseInt(totalBillAmount)==0))){
                limitAllowed=1000;
            }*/

            Log.v("dataone", String.valueOf(limitAllowed));
            String s = strtxtrebate.getText().toString();
            double rebate = 0.0;
            if (!s.isEmpty()) {
                rebate = (Double.parseDouble(strtxtrebate.getText().toString()));
                //rebate = Integer.parseInt(String.valueOf(sss));

            }


            Log.v("dataone", String.valueOf(rebate));
            int totalAmount = Integer.parseInt(totBillAmt);

            int toPay =(int) Math.floor(Double.parseDouble(totalBillAmount) + rebate);
            Log.d("toPay", "setBillInfromFromLocal: " + toPay);
            Log.d("toPay", "setBillInfromFromLocal: " + totalAmount);

            if (totalAmount < 0) {
                strtxttotbill.setText("0");
            } else {
                strtxttotbill.setText(cursor.getString(12));
            }
            if (toPay <= 0) {
                strtxtamtpayble.setText("0");
                // limitAllowed = 20000; // previously 50000 made changes as per advidse of vipul sir
            } else {
                strtxtamtpayble.setText("" + toPay);
                //  limitAllowed = toPay * 5; // previously 12 times made changes as per advidse of vipul sir
            }

            btnDenied.setOnClickListener(v -> {
                String amt = strtxttotbill.getText().toString();
                openBottomsheet(data_list, userid, divcode, consno, Cons_idfetch, divisionfetch, subdivisionfetch, sectionfetch, namefetch, add1fetch, add2fetch, billMonth, amt, dueDate);
            });

        }
        sreb1.setVisibility(View.VISIBLE);
        sduedt1.setVisibility(View.VISIBLE);
        scbl1.setVisibility(View.VISIBLE);
        stbl1.setVisibility(View.VISIBLE);
        strtxtrebate.setVisibility(View.VISIBLE);
        strtxtduedate.setVisibility(View.VISIBLE);
        strtxttotbill.setVisibility(View.VISIBLE);
        strtxtcurbill.setVisibility(View.VISIBLE);
        databaseAccess.close();
    }
    private void openBottomsheet(ArrayList<String> reason_list, String userid, String divcode, String consno, String cons_idfetch, String divisionfetch, String subdivisionfetch, String sectionfetch, String namefetch, String add1fetch, String add2fetch, String billMonth, String pybleamt, String dueDate) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheetLayout = inflater.inflate(R.layout.bottomsheet_dialog, null);
        TextView cons_num = bottomSheetLayout.findViewById(R.id.cons_num);
        TextView name = bottomSheetLayout.findViewById(R.id.name);
        LinearLayout datetopay_layout = bottomSheetLayout.findViewById(R.id.datetopay_layout);
        TextView date_topay = bottomSheetLayout.findViewById(R.id.date_topay);
        drSpinner = bottomSheetLayout.findViewById(R.id.drspinner);
        EditText remarks = bottomSheetLayout.findViewById(R.id.remarks);
        Button submit = bottomSheetLayout.findViewById(R.id.submit);

        cons_num.setText("Cons Acc. - " + consno);
        name.setText("Name - " + namefetch);

        ArrayAdapter adapter = new ArrayAdapter(AccountInfo.this, android.R.layout.simple_spinner_item, reason_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drSpinner.setAdapter(adapter);

        drSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ((TextView)parent.getChildAt(0)).setTextAppearance((R.style.mySpinnerText));

                ReasonTxt = parent.getItemAtPosition(position).toString();
                //Toast.makeText(AccountInfo.this, ""+ReasonTxt, Toast.LENGTH_SHORT).show();
                if (ReasonTxt.equals("Promised To Pay")) {
                    datetopay_layout.setVisibility(View.VISIBLE);
                } else {
                    datetopay_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date_topay.setOnClickListener(v -> {

            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DecimalFormat mFormat = new DecimalFormat("00");

            // date picker dialog
            datePickerDialog = new DatePickerDialog(AccountInfo.this,
                    (view, year1, monthOfYear, dayOfMonth) -> date_topay.setText(mFormat.format(dayOfMonth) + "/" +mFormat.format(monthOfYear + 1) + "/" + year1), year, month, day);

            // set maximum date to be selected as today
            //           picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
            // set minimum date to be selected as today
            datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
            //datePickerDialog.getDatePicker().setMaxDate(cldr.getTimeInMillis());

            datePickerDialog.show();
        });


        submit.setOnClickListener(v -> {

            String futurepayDate = date_topay.getText().toString();
            String Remarks = remarks.getText().toString();

            if (ReasonTxt.equals("Select Reason")) {
                Toast.makeText(AccountInfo.this, "Select deny reason", Toast.LENGTH_SHORT).show();
            } else if (ReasonTxt.equals("Promised To Pay") && futurepayDate.equals("")) {
                Toast.makeText(AccountInfo.this, "Select promised payment date", Toast.LENGTH_SHORT).show();
            } else if (ReasonTxt.equals("Others") && Remarks.equals("")) {
                Toast.makeText(AccountInfo.this, "Enter remarks", Toast.LENGTH_SHORT).show();
            } else {
                mBottomSheetDialog.dismiss();
                savedatatolocal(ReasonTxt, futurepayDate, Remarks, userid, divcode, consno, cons_idfetch, divisionfetch, subdivisionfetch, sectionfetch, namefetch, add1fetch, add2fetch, billMonth, pybleamt, dueDate);
                finish();
            }


        });

        mBottomSheetDialog = new BottomSheetDialog(AccountInfo.this, R.style.SheetDialog);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.show();

    }

    private void savedatatolocal(String reasonTxt, String futurepayDate, String remarks, String userid, String divcode, String consno, String cons_idfetch, String divisionfetch, String subdivisionfetch, String sectionfetch, String namefetch, String add1fetch, String add2fetch, String billMonth, String pybleamt, String dueDate) {
        lat = sharedPreferenceClass.getValue_string("Latitude");
        lang = sharedPreferenceClass.getValue_string("Longitude");
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open(); //SEND_FLG used as upload flag 0/1
        String strSelectSQL_02 = "INSERT INTO DENIEDCONSUMER  " +
                " (USER_ID, SEND_FLG, CONS_ACC, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, CON_ADD2, BILL_MTH, BILL_TOTAL , DUE_DATE, REASON, REMARKS, FIELD1, FIELD2, ENTRYDATE)" + //f1-latlong , f2-futurepaymentdate , f3- (entrydate for server), f4- , f5- ,
                " VALUES('" + userid + "','" + "0" + "','" + consno.replace("'", "''") + "','" + cons_idfetch.replace("'", "''") + "'," +
                " '" + divisionfetch.replace("'", "''") + "','" + subdivisionfetch.replace("'", "''") + "','" + sectionfetch.replace("'", "''") + "','" + namefetch.replace("'", "''") + "','" + add1fetch.replace("'", "''") + "','" + add2fetch.replace("'", "''") + "'," +
                " '" + billMonth.replace("'", "''") + "', '" + pybleamt.replace("'", "''") + "', '" + dueDate.replace("'", "''") + "', '" + reasonTxt.replace("'", "''") + "', '" + remarks.replace("'", "''") + "', '" + (lat + "," + lang) + "', '" + futurepayDate.replace("'", "''") + "', date('now')) ";// "', '" + "1" +
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);

        Toast.makeText(AccountInfo.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
        databaseAccess.close();
    }


/*  private void setBillInfromFromLocal() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String getMobileNum = "select * from CUST_DATA where CONS_ACC='" + EntryNum_string + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(getMobileNum, null);
        String mobileNum = "";
        while (cursor.moveToNext()) {
            mobileNum = cursor.getString(0);
            strtxtconsno.setText(cursor.getString(2));
            cons_accfetch = cursor.getString(2);//cons_acc 0046
            Cons_idfetch = cursor.getString(3);//cos id 10digit
            divisionfetch = cursor.getString(4);
            subdivisionfetch = cursor.getString(5);
            sectionfetch = cursor.getString(6);
            namefetch = cursor.getString(7);
            add1fetch = cursor.getString(8);
            add2fetch = cursor.getString(9);
            billMonth = cursor.getString(10);
            payment_count = cursor.getString(17);
            vtype = cursor.getString(18);
            pybleamt = cursor.getString(12);
            MobileNo = EntryMob_string;
            String trid = cursor.getString(2);

            Log.v("payment", payment_count);
            Log.v("pybleamt", pybleamt);

            //String strLastFourDi = trid.length() >= 4 ? trid.substring(trid.length() - 4) : "";

            String addOne = (cursor.getString(8) + "\n" + cursor.getString(9));
            strtxtname.setText(cursor.getString(7));
            strtxtaddress.setText(addOne);
            //strtxtaddress.setText(cursor.getString(8) + "\n" + cursor.getString(9));

            //Bill info
            strtxtcurbill.setText(cursor.getString(11));
            strtxtconsno.setText(EntryNum_string);

            currentTotalBill = cursor.getString(11);
            totalBillAmount = cursor.getString(12);////total bill
            // String currBill = String.valueOf(Math.ceil(Float.parseFloat(currentTotalBill)));
            //currentTotalBillRound = currBill;

            //  String[] curbill = currentTotalBill.split("\\.");
            //  currentTotalBillRound = curbill[0];

          */
/*  double curbill = Math.ceil(Float.parseFloat(currentTotalBill));
            long intcurbill = Math.round(curbill);
            currentTotalBillRound = String.valueOf(intcurbill);

           *//*

            double curbill = Float.parseFloat(currentTotalBill);
            long intcurbill = Math.round(curbill);
            currentTotalBillRound = String.valueOf(intcurbill);

            //double totBill = Math.ceil(Float.parseFloat(totalBillAmount));
            long inttotBill = Math.round(Float.parseFloat(totalBillAmount));
            totBillAmt = String.valueOf(inttotBill);

            // String[] ss = totalBillAmount.split("\\.");
            //  totBillAmt = ss[0];
            Log.v("value", totBillAmt);

            //String[] separated = name.split(" ");
            //String lt = String.valueOf(separated.length);
            //  String f_name = separated[0];


            String dueDate = cursor.getString(14).trim();


            if (!(dueDate.equalsIgnoreCase("Null"))) {

                Log.v("dateone:::", CommonMethods.convertStringToDate(cursor.getString(14)).toString());
                Log.v("todaydate", (CommonMethods.getTodaysPlainDate()).toString());

                if (CommonMethods.convertStringToDate(cursor.getString(14)).after(CommonMethods.getTodaysPlainDate()) ||
                        CommonMethods.convertStringToDate(cursor.getString(14)).equals(CommonMethods.getTodaysPlainDate())) {
                    if (payment_count.equals("0")) {
                        //rebate//
                        strtxtrebate.setText(cursor.getString(13));
                        strtxtduedate.setText(CommonMethods.getFormattedDate(cursor.getString(14)));
                        String s = strtxtrebate.getText().toString(); //-10.40
                        int rebate1 = 0;

                        if (!s.isEmpty()) {
                            long rbt = Math.round(Double.parseDouble(strtxtrebate.getText().toString()));
                            rebate1 = Integer.parseInt(String.valueOf(rbt));
                            //    reb = Long.parseLong(s);
                        }
                        int currenttotalAmount = Integer.parseInt(currentTotalBillRound);
                        int outvalue = currenttotalAmount + rebate1;
                        currentTotalBillDynamic = String.valueOf(outvalue);
                    } else {
                        //no rebate//
                        strtxtrebate.setText("0");
                        strtxtduedate.setText(duedtfetch);
                        currentTotalBillDynamic = currentTotalBillRound;
                        strtxtamtpayble.setText(totBillAmt);
                    }

                } else {
                    //no rebate//
                    strtxtrebate.setText("0");
                    strtxtduedate.setText(duedtfetch);
                    currentTotalBillDynamic = currentTotalBillRound;
                    strtxtamtpayble.setText(totBillAmt);
                }

                strtxtduedate.setText(CommonMethods.getFormattedDate(cursor.getString(14)));
            }

            //Due date
          */
/*  if (((Integer.parseInt(currentTotalBill)==0))||((Integer.parseInt(totalBillAmount)==0))){
                limitAllowed=1000;
            }*//*


            Log.v("dataone", String.valueOf(limitAllowed));
            String s = strtxtrebate.getText().toString();
            int rebate = 0;
            if (!s.isEmpty()) {
                long sss = Math.round(Double.parseDouble(strtxtrebate.getText().toString()));
                rebate = Integer.parseInt(String.valueOf(sss));

            }


            Log.v("dataone", String.valueOf(rebate));
            int totalAmount = Integer.parseInt(totBillAmt);

            int toPay = totalAmount + rebate;
            Log.d("toPay", "setBillInfromFromLocal: " + toPay);
            Log.d("toPay", "setBillInfromFromLocal: " + totalAmount);

            if (totalAmount < 0) {
                strtxttotbill.setText("0");
            } else {
                strtxttotbill.setText(cursor.getString(12));
            }
            if (toPay <= 0) {
                strtxtamtpayble.setText("0");
                // limitAllowed = 20000; // previously 50000 made changes as per advidse of vipul sir
            } else {
                strtxtamtpayble.setText("" + toPay);
                //  limitAllowed = toPay * 5; // previously 12 times made changes as per advidse of vipul sir
            }

          */
/*  if (toPay <= 0) {
                limitAllowed = 1000;
            } else {
                limitAllowed = 30000;
            }*//*



        } //Trans_IDfetch + "',0,date('now')) ";
        sreb1.setVisibility(View.VISIBLE);
        sduedt1.setVisibility(View.VISIBLE);
        scbl1.setVisibility(View.VISIBLE);
        stbl1.setVisibility(View.VISIBLE);
        strtxtrebate.setVisibility(View.VISIBLE);
        strtxtduedate.setVisibility(View.VISIBLE);
        strtxttotbill.setVisibility(View.VISIBLE);
        strtxtcurbill.setVisibility(View.VISIBLE);
        databaseAccess.close();
    } //end of set from local
*/

/*
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
                progressDialog = ProgressDialog.show(AccountInfo.this, "Fetching Bill Details", "Please Wait:: connecting to server");
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
                                AccountInfo.this.finish();
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
            String ConsValid="";
            String AccessFlg = "";
            String PaybleFlg = "0";
            try {
                ConsValid = BillInfo[2];
                AccessFlg = BillInfo[1];
                if (!ConsValid.equals("0")) {
                    PaybleFlg = BillInfo[11];
                }
            }catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Not Allow to Pay");
                    alertDialogBuilder.setMessage("Either Pay Amount -ve or Zero "+"\n"+"Intimate IT if any problem")
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
                }

            try {
                if (!PaybleFlg.equals("0")) {
                    MobileNo = BillInfo[18];
                }
            }catch (Exception e) {
                MobileNo="9999999999";
            }
            // Advance payment not allowed 26042019
            if(BillInfo[0].equals("0")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Not Authorized Err-01");
                alertDialogBuilder.setMessage("Contact IT Center")
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
            }
            if(!PaybleFlg.equals("0")){
                MobileNo = BillInfo[18];
            }
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
                if (ConsValid.equals("1") && BillInfo[0].equals("1") && !PaybleFlg.equals("0")) {
                    divisionfetch = BillInfo[4];
                    subdivisionfetch = BillInfo[5];
                    sectionfetch = BillInfo[6];
                    cons_accfetch = BillInfo[7];
                    namefetch = BillInfo[8];
                    add1fetch = BillInfo[9];
                    add2fetch = BillInfo[10];
                    Trans_IDfetch = BillInfo[13];
                    if (BillInfo[3].equals("1")) {//curent month bill available
                        if(BillInfo[11].equals("1")){
                            payblefetch = BillInfo[12];
                        }else if(BillInfo[11].equals("0")){
                            payblefetch ="0";
                        }else if(BillInfo[11].equals("2")){
                            payblefetch = BillInfo[12];
                        }

                    } else {// curent month bill not available
                        if(BillInfo[11].equals("1")){
                            payblefetch = BillInfo[12];
                        }else if(BillInfo[11].equals("0")){
                            payblefetch ="0";
                        }else if(BillInfo[11].equals("2")){
                            payblefetch = BillInfo[12];
                        }
                    }
                      BalFetch=BillInfo[14];

                    if (EntryNum_string.trim().length()==11){
                        Cons_idfetch =EntryNum_string;
                    }
                    else {
                        Cons_idfetch = div_code + "S" + EntryNum_string;
                    }
                    ///inserting into collection database
                } else {
                    if(PaybleFlg.equals("0") && ConsValid.equals("1") ){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Already Paid");
                            alertDialogBuilder.setMessage("No Advance payment")
                                    .setCancelable(false)
                                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(getApplicationContext(), AcCollection.class));
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(getApplicationContext(), CollectionDashBoard.class));
                                            finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();


                    }else {

                      //  Intent ConsNotFound = new Intent(getApplicationContext(), ConsNotFound.class);
                     //   startActivity(ConsNotFound);
                      //  finish();
                        String strMsg="";

                        if((BillInfo[3].equals("0"))){
                            strMsg=BillInfo[4];
                        }else{
                            strMsg=BillInfo[3];
                        }
                        Log.d("DemoApp", " strMsg:   " + strMsg);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("No Payment");
                        alertDialogBuilder.setMessage(strMsg)
                                .setCancelable(false)
                                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), AcCollection.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(getApplicationContext(), CollectionDashBoard.class));
                                        finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }
                }
            }
            strtxtconsno.setText(cons_accfetch);
            strtxtname.setText(namefetch);
            strtxtaddress.setText(add1fetch);

            if(SelChoice_string.equals("Asd")|| SelChoice_string.equals("Adv")|| SelChoice_string.equals("rcf")|| SelChoice_string.equals("Assmnt") ) {
                strtxtamtpayble.setText("");
            }else{
                if(billchkflg!=1){
                    strtxtamtpayble.setText(payblefetch);
                   // strtxtamtpayble.setClickable(false);// non editable
                    //strtxtamtpayble.setFocusable(false);// non editable
                  //  strtxtamtpayble.setEnabled(false);
                }

            }
        }

    }
*/

    public boolean isExistsHeader(String conAcc) {

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        String selectString = "SELECT CONS_ACC FROM " + "COLL_SBM_DATA" + " WHERE " + "CONS_ACC" + " =?";
        Cursor cursor = DatabaseAccess.database.rawQuery(selectString, new String[]{conAcc});
        boolean isExist = false;

        if (cursor.moveToFirst()) {
            isExist = true;
            DatabaseAccess.database.execSQL("DELETE FROM " + "COLL_SBM_DATA" + " WHERE " + "CONS_ACC" + "='" + conAcc + "'");
        }
        databaseAccess.close();
        return isExist;
    }

}
