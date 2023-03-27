package com.collection.tpwodloffline.activity;

import static com.collection.tpwodloffline.utils.Constants.datePref;
import static com.collection.tpwodloffline.utils.Constants.isDataSynced;
import static com.collection.tpwodloffline.utils.Constants.isFirstTimeLoginPref;
import static com.collection.tpwodloffline.utils.Constants.passwordPref;
import static com.collection.tpwodloffline.utils.Constants.userIdPref;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.collection.tpwodloffline.AlertErrorCall;
import com.collection.tpwodloffline.BuildConfig;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.UploadManager;
import com.collection.tpwodloffline.model.ResponseModel;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.GpsUtils;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int STORAGE_PERMISSION_CODE = 66;
    private static final int STORAGE_PERMISSION_CODE2 = 866;

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }
    public DatabaseAccess databaseAccess = null;
    final Context context = this;
    private String useridfl = null;
    private String Password = null;
    private String CompanyID = null;
    private String AuthURL = null;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static final int RequestPermissionCode = 1;
    private int chkresponse = 1;
    private String imeinum = "";
    private String Ind_Limit = "";
    private String Franch_Limit = "";
    private String sdflg = "";
    private String advflg = "";
    private String rcflg = "";
    private String assflg = "";
    private String asdlg = "";
    private String pfflg = "";
    private String dwflg = "";
    private String blflg = "";
    private String Tcflg = "0";
    private String accolflg = "";
    private String nonaccolflg = "";
    private String cashcollflg = "";
    private String chqcollflg = "";
    private String ddcollflg = "";
    private String poscollflg = "";
    private String usname = "";
    private String dbpwdnm = "";
    private String sbmBlPrv = "0";
    private String limit = "0";
    private String NoticeTitle = "";
    private String NoticeDescription = "";
    private String NoticeDate = "";
    String energy, nonenergy, nsc, csc, dnd, frm;
    private String version = "0";
    EditText userID;
    EditText PwdID;
    ProgressDialog progressDialog;
    private String savedUrl = "";
    SharedPreferenceClass sharedPreferenceClass;

    // By Sradhendu 19/05/2021
    private static final int PERMISSION_REQUEST_CODE = 2;
    TelephonyManager telephonyManager;
    ArrayList<String> phoneNumber = new ArrayList<>();
    String SimNumber, Mobile;
    String from_flag;

    private static final long MIN_TIME_BW_UPDATES = 2 * 1000;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private final float MIN_DISTANCE = 2; // 6 meters

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
    LocationManager locationManager;
    int OVERLAY_PERMISSION_REQUEST_CODE = 5469;


    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
    }


    //App Update Code
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                //popupSnackbarForCompleteUpdate();
            } else {
                //Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    TextView Valid;
    ImageView Refresh;
    String BalFetch;
    Integer finalBalance = 0;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

////////doubleBackPressedExecution
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

////////doubleBackPressed

    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    Button login;

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    boolean isAppInstalled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE,
                            MainActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                //popupSnackbarForCompleteUpdate();
            } else {
                //Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        //date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String make = Build.MANUFACTURER;
        String model = Build.MODEL;
        int SDKversion = Build.VERSION.SDK_INT;
        Valid = findViewById(R.id.valid);
        Refresh = findViewById(R.id.refresh);
        TextView version = findViewById(R.id.textView2);
        login = findViewById(R.id.login);
        userID = findViewById(R.id.userid);
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());
        getValiddateInfo();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //CommonMethods.openSettingsAllFilesAccess(MainActivity.this);
        requestStoragePermission();
        //requestWritePermission();
        sharedPreferenceClass = new SharedPreferenceClass(MainActivity.this);
        Mobile = sharedPreferenceClass.getValue_string("mobile");
        from_flag = sharedPreferenceClass.getValue_string("from_flag");
        createCustomerTable();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        if (isGPSEnabled(MainActivity.this)) {
            //checkPermissions();
            checkPermission();
            getLastLocation();
            //Toast.makeText(MainActivity.this, "Ask for permission", Toast.LENGTH_SHORT).show();
        } else {
            new GpsUtils(this).turnGPSOn(isGPSEnable -> {
                // turn on GPS
                isGPS = isGPSEnable;

            });

        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }

                        latitude = String.valueOf(wayLatitude);
                        longitude = String.valueOf(wayLongitude);


                        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses.size() > 0) {
                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality();
                                String subLocality = addresses.get(0).getSubLocality();
                                state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();

                                sharedPreferenceClass.setValue_string("Latitude", latitude);
                                sharedPreferenceClass.setValue_string("Longitude", longitude);
                               /* sharedPreferenceClass.setValue_string("Address", address);
                                sharedPreferenceClass.setValue_string("City", city);
                                sharedPreferenceClass.setValue_string("State", state);*/
                                //Toast.makeText(context, "Lat:- "+latitude+" Long:- "+longitude+" Address:- "+address+" City:- "+city+" State:- "+state, Toast.LENGTH_SHORT).show();

                                //progressDialog.dismiss();
                                //getData();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

       /* telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        } else {
           // TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                for (int i = 0; i < subscription.size(); i++) {
                    SubscriptionInfo info = subscription.get(i);
                    Log.d("num", "number " + info.getNumber());
                    // Log.d("network_name", "network name : " + info.getCarrierName());
                    //Log.d("country iso", "country iso " + info.getCountryIso());
                    phoneNumber.add(info.getNumber());
                }
                String n1 = phoneNumber.get(0);
                //String n2 = phoneNumber.get(1);
                if (n1.length() > 10) {
                    SimNumber = n1.length() >= 10 ? n1.substring(n1.length() - 10) : "";

                } else {
                    SimNumber = phoneNumber.get(0).trim();
                }

            *//*    if (n2.length() > 10) {
                    SimNumber2 = n1.length() >= 10 ? n2.substring(n2.length() - 10) : "";

                } else {
                    SimNumber2 = phoneNumber.get(1).trim();
                }*//*

            } else {
                // SimNumber = (telephonyManager.getLine1Number());
            }
        }*/

        if (CommonMethods.getBooleanPreference(getApplicationContext(), isFirstTimeLoginPref, true)) {
            CommonMethods.saveBooleanPreference(getApplicationContext(), isFirstTimeLoginPref, false);
        }

        //final String h="hello";
        // sessiondata="";
        String appversion = BuildConfig.VERSION_NAME;
        version.setText(make + "-" + model + "-" + SDKversion + "\n" + "App Version ~ v" + appversion + "\n" + ServerLinks.ReleaseDate);

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("userInfo", 0);
        userID.setText(sessiondata.getString("userIdPrefill", ""));
        version.setOnClickListener(v -> {
            String uuid = CommonMethods.getImageUUID();
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* try {
                    isAppInstalled = appInstalledOrNot("com.countercollection.tpwodloffline");

                } catch (Exception ex) {
                    isAppInstalled = false;
                }*/
                if (isAppInstalled) {
                    Toast.makeText(context, "Uninstall Mudra CC from this device to use Mudra", Toast.LENGTH_SHORT).show();
                } else {
                    useridfl = userID.getText().toString();
                    //Narendra: Passing mobile number to download data api
                    CommonMethods.mobNum = useridfl;
                    PwdID = (EditText) findViewById(R.id.pwd);
                    Password = PwdID.getText().toString();
                    // LogIn loginobj = new LogIn();
               /* if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !Mobile.equals(SimNumber)) {
                    Toast.makeText(MainActivity.this, "Please insert your registered sim", Toast.LENGTH_SHORT).show();
                } else */
                    if (userID.length() == 0) {
                        Toast.makeText(MainActivity.this, "Please enter user id", Toast.LENGTH_SHORT).show();
                    } else if (Password.length() == 0) {
                        Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

                    } else {
                        scheduleWork();
                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        String Prv_flg = databaseAccess.getAuthenticate(useridfl, Password);
                        databaseAccess.close();

                        //   Log.d("DemoApp", "2nd Strat");
                        Log.d("DemoApp", "prv flag   " + Prv_flg);
                        // Prv_flg= "C" collection agent
                        // Prv_flg= "X" Admin collection
                        // int flag = LogIn.ChkCredential(useridfl, Password);
                        String phnumber = "";
                        if (Prv_flg.equals("c") || Prv_flg.equals("x")) {

                            //permission
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                    android.Manifest.permission.READ_PHONE_STATE)) {

                            } else {
                                imeinum = userID.getText().toString();
                                //phnumber = tm.getLine1Number();
                                phnumber = "";
                            }
                            // imeinum="358461097642898";
                            databaseAccess = DatabaseAccess.getInstance(context);
                            databaseAccess.open();
                            String strSelectSQL_01 = "SELECT userid,passkey,company_id,DATE('now'),prv_bill,office_code,user_name  " +
                                    "FROM sa_user  where date('now')>=date(substr(valid_startdate,7,4)||'-'||substr(valid_startdate,4,2)||'-'||substr(valid_startdate,1,2)) and date('now')<=date(substr(valid_enddate,7,4)||'-'||substr(valid_enddate,4,2)||'-'||substr(valid_enddate,1,2)) and lock_flag=0 " +
                                    "and userid='" + useridfl + "' and passkey='" + Password + "'";
                            Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                            Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
                            String toDayDt = "";
                            String prv_bill = "0";
                            String div_code = "";
                            String UserName = "";
                            while (cursor.moveToNext()) {

                                CompanyID = CommonMethods.getCompanyID();

                                toDayDt = cursor.getString(3);
                                prv_bill = cursor.getString(4);
                                div_code = cursor.getString(5);
                                UserName = cursor.getString(6);
                                Log.d("DemoApp", "in Loop CompanyID" + CompanyID);
                            }
                            cursor.close();
                            databaseAccess.close();

                            try {
                                if (prv_bill == null || prv_bill.equals("")) {
                                    prv_bill = "0";
                                }
                            } catch (Exception e) {
                                prv_bill = "0";
                            }
                            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
                            SharedPreferences.Editor editor = sessiondata.edit();
                            editor.putString("userID", useridfl); //Storing userid
                            editor.putString("phnumber", phnumber); //Storing phone number
                            editor.putString("Previlage", Prv_flg); //Storing previlage
                            editor.putString("CompanyID", CommonMethods.getCompanyID()); //Storing previlage
                            editor.putString("toDayDt", toDayDt); //Storing Today Date
                            editor.putString("prv_bill", prv_bill); //Storing prv_bill
                            editor.putString("div_code", div_code); //Storing div_code
                            editor.putString("imeinum", imeinum); //Storing div_code
                            editor.putString("UserName", UserName); //Storing div_code
                            editor.commit(); // commit changes

                            try {
                                //checking network live or not
                                boolean chksts = false;
                                chksts = isNetworkAvailable();
                                Log.d("DemoApp", "in Loop CompanyID 111" + chksts);
                                //if(chksts==false){
                                if (!chksts) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    alertDialogBuilder.setTitle("No internet");
                                    alertDialogBuilder.setMessage("You are not connected to internet !")
                                            .setCancelable(false)
                                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .setNeutralButton("Offline", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    loginUser();
                                                }
                                            })
                                            .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    MainActivity.this.finish();
                                                }
                                            });
                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    // show it
                                    alertDialog.show();
                                } else {
                                    loginUser();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (Prv_flg.equals("0")) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Invalid Credential/No Privilege/ Validity Expired!");
                            alertDialogBuilder.setMessage("Please Click Retry for Login !!")
                                    .setCancelable(false)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        } else if (Prv_flg.equals("11")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Please Check Current Date");
                            alertDialogBuilder.setMessage("Change the Date and Login !!")
                                    .setCancelable(false)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("No authorization!");
                            alertDialogBuilder.setMessage("Please Click Retry for Login !")
                                    .setCancelable(false)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            MainActivity.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                    }
                }
            }
        });

        Button bnexit = findViewById(R.id.exit);
        bnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String un = sharedPreferenceClass.getValue_string("un");
                String pw = sharedPreferenceClass.getValue_string("pw");
                String mobile = sharedPreferenceClass.getValue_string("mobile");
                //finish();
                System.exit(0);
                //funcUrlCheck(5);
                //new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un, pw, mobile));

                //startActivity(new Intent(MainActivity.this, ConsumerBillinfo.class));
                //startActivity(new Intent(MainActivity.this, MainActivity2.class));

              /*  try {
                    final String inFileName = "/data/data/com.collection.tpwodloffline/files/COLLDB.db";
                    //final String inFileName = context.getDatabasePath("COLLDB.db").getPath();
                    Log.v("path1", inFileName);
                    //Log.v("path2", inFileName2);
                    File dbFile = new File(inFileName);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(dbFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    String outFileName = Environment.getExternalStorageDirectory() + "/database_copy.db";

                    // Open the empty db as the output stream
                    OutputStream output = null;

                    output = new FileOutputStream(outFileName);


                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //startActivity(new Intent(MainActivity.this, GetMobileActivity.class));

                }*/
            }
        });

        Refresh.setOnClickListener(v -> {
            if (CommonMethods.isConnected(MainActivity.this)) {
                useridfl = userID.getText().toString();
                CommonMethods.mobNum = useridfl;
                PwdID = (EditText) findViewById(R.id.pwd);
                Password = PwdID.getText().toString();
               /* if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !Mobile.equals(SimNumber)) {
                    Toast.makeText(MainActivity.this, "Please insert your registered sim", Toast.LENGTH_SHORT).show();
                } else */
                if (userID.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter user id", Toast.LENGTH_SHORT).show();
                } else if (Password.length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

                } else {
                    String device_id = CommonMethods.getDeviceid(getApplicationContext());
                    String company_id = CommonMethods.CompanyID;
                    Refresh.startAnimation(rotate);

                    if (new DatabaseAccess().getOfflineCountAll(this)) {
                        GetUpdatedData(useridfl, Password, device_id, company_id);
                    } else {
                        scheduleWork();

                        //data pending
                        Toast.makeText(MainActivity.this,
                                "Data pending to be uploaded", Toast.LENGTH_SHORT).show();
                    }

             /* databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String Prv_flg = databaseAccess.getAuthenticate(useridfl, Password);
                databaseAccess.close();
                if (Prv_flg.equals("c") || Prv_flg.equals("x")) {
                    GetUpdatedData(useridfl,Password,device_id,company_id);
                }else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }*/

                }
            } else {
                Toast.makeText(context, "No internet connection found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        //popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    }  //Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());

                }
            };

    private void getValiddateInfo() {
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("userInfo", 0);
        String userId = (sessiondata.getString("userIdPrefill", ""));
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        //String strSelectSQL_01 = "SELECT date(substr(valid_enddate,1,2)||'-'||substr(valid_enddate,4,2)||'-'||substr(valid_enddate,7,4)) " +
        String strSelectSQL_01 = "SELECT strftime('%d-%m-%Y',substr(valid_enddate,7,4)||'-'||substr(valid_enddate,4,2)||'-'||substr(valid_enddate,1,2)) " +
                "FROM sa_user  where lock_flag=0 " + "and userid='" + userId + "'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

        while (cursor.moveToNext()) {

            String VendDate = cursor.getString(0);
            Valid.setText("Valid up to " + VendDate);

            // Valid.setText(R.string.valid+VendDate);
        }
        cursor.close();
        databaseAccess.close();
    }

    private void GetUpdatedData(String useridfl, String password, String device_id, String company_id) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //https://staging.tpsouthernodisha.com/api/fetchBalance?strUserName=1001&
        //strPassword=123456&strCompanyID=3&strDeviceid=a2a3a8d2b6527dfa
        String url = ServerLinks.fetchBalance + "strUserName=" + useridfl
                + "&strPassword=" + password + "&strCompanyID=" + company_id
                + "&strDeviceid=" + device_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        progressDialog.dismiss();
                        Gson gson = new Gson();
                        ResponseModel dataModel = gson.fromJson(response, ResponseModel.class);
                        if (dataModel.getStatusCode() == 200) {
                            int rows = new DatabaseAccess().refreshUserData(this, dataModel);
                            Log.d("Update", "No of rows updated :: " + rows);
                            getValiddateInfo();
                            Toast.makeText(MainActivity.this,
                                    "Updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception jsn) {
                        jsn.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,
                    "Something went wrong", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        }
        //And finally ask for the permission
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void requestWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE2);

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE2);
    }

    private void createCustomerTable() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strSelectSQL_01 = "CREATE TABLE IF NOT EXISTS \"CUST_DATA\" ( \"USER_ID\" TEXT, \"DIVISION_CODE\" TEXT, \"CONS_ACC\" TEXT, \"CUST_ID\" TEXT, \"DIVISION\" TEXT, \"SUBDIVISION\" TEXT, \"SECTION\" TEXT, \"CON_NAME\" TEXT, \"CON_ADD1\" TEXT, \"CON_ADD2\" TEXT, \"PRSN_KWH\" TEXT, \"CUR_TOTAL\" TEXT, \"BILL_TOTAL\" TEXT, \"REBATE\" TEXT, \"DUE_DATE\" TEXT, \"MOBILE_NO\" TEXT, \"EMAIL\" TEXT, \"PAY_COUNT\" TEXT )";//, "PAY_COUNT" TEXT
        DatabaseAccess.database.execSQL(strSelectSQL_01);
        databaseAccess.close();
    }

    private void loginUser() {

        String un = sharedPreferenceClass.getValue_string("un");
        String pw = sharedPreferenceClass.getValue_string("pw");
        String mobile = sharedPreferenceClass.getValue_string("mobile");

        //funcUrlCheck(chkresponse);
        //String authData = "http://portal.tpcentralodisha.com:8070/CESU_API_Report/CESU_DynamicReport.jsp?un=TPCODL_COL_OFF&pw=OFF_2020&CompanyID=14&ReportID=1093&strMobileNo=9438233014";
        if (CommonMethods.getStringPreference(getApplicationContext(),
                userIdPref, "abc").equals("abc") ||
                !CommonMethods.getStringPreference(getApplicationContext(),
                        datePref, "abc").equals(CommonMethods.getTodaysDate()) ||
                CommonMethods.getStringPreference(getApplicationContext(),
                        datePref, "abc").equals("abc")) {

            boolean download = CommonMethods.getBooleanPreference(getApplicationContext(), isDataSynced, false);

            if (download) {
                if (CommonMethods.isConnected(getApplicationContext())) {
                    /*if(from_flag.equals("0")){
                        funcUrlCheck(chkresponse);
                    }else  if(from_flag.equals("1")){
                        chkresponse = 5;
                        funcUrlCheck(chkresponse);
                    }*/
                    chkresponse = 5;
                    funcUrlCheck(chkresponse);
                } else {
                    Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
                }
            } else {

                if (!(getOfflineCount() > 0)) {
                    if (CommonMethods.isConnected(getApplicationContext())) {
                        chkresponse = 5;
                        funcUrlCheck(chkresponse);
                        //new MainActivity.DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un, pw, mobile));
                    } else {
                        Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("Alert");
                    alertDialogBuilder.setMessage("Previous collected data is not yet uploaded, Data can not be downloaded before upload of previous data")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
            }
        } else {
            if (CommonMethods.getStringPreference(getApplicationContext(),
                    userIdPref, "abc").equals(userID.getText().toString()) &&
                    CommonMethods.getStringPreference(getApplicationContext(),
                            passwordPref, "abc").equals(PwdID.getText().toString())) {

                SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
                SharedPreferences.Editor ssodata = sessionssodata.edit();
                String serverDate = sessionssodata.getString("serverDate", null);
                Date dates = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Log.v("startdate", serverDate);
                Log.v("startdate", serverDate);


                boolean download1 = CommonMethods.getBooleanPreference(getApplicationContext(), isDataSynced, false);

                if (download1) {
                    boolean chksts = false;
                    chksts = isNetworkAvailable();
                    try {
                        Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates).toString());
                        //Date serverDateParse = sdf.parse(CommonMethods.convertStringToDate2(serverDate).toString());
                        Date serverDateParse = sdf.parse(CommonMethods.getFormattedDateDDMMYYYY(serverDate.replace("-", "")));
                        Log.v("serverDateParse", String.valueOf(currentDate));
                        if (serverDateParse.compareTo(currentDate) == 0) {
                            if (!(getOfflineCount() > 0)) {
                                Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                                startActivity(ColDashboard);
                            } else {
                                Toast.makeText(context, "Data pending to be uploaded", Toast.LENGTH_SHORT).show();
                                scheduleWork();
                                Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                                startActivity(ColDashboard);
                            }
                        } else {
                            if ((getOfflineCount() > 0)) {
                                scheduleWork();
                            } else {
                                funcUrlCheck(5);

                            }
                        }

                        //Toast.makeText(context, "Synced already", Toast.LENGTH_SHORT).show();
                        //new MainActivity.DownloadCustData().execute(authData);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (!(getOfflineCount() > 0)) {
                        login.setKeepScreenOn(true);
                        new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un, pw, mobile));
                    } else {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder.setMessage("Previous collected data is not yet uploaded, Data can not be downloaded before upload of previous data")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
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
                }

            } else {
                //Toast.makeText(context, "Incorrect userId or password", Toast.LENGTH_SHORT).show();
                CommonMethods.showDialog(MainActivity.this, context,
                        "Invalid userId or password", "Please retry again!");

            }
        }
    }


    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         int id = item.getItemId();

         //noinspection SimplifiableIfStatement
         if (id == R.id.action_settings) {
             return true;
         }

         return super.onOptionsItemSelected(item);
     }*/

    public static WorkManager getWorkManager(Context context) {
        return WorkManager.getInstance(context);
    }
    private void scheduleWork() {
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadManager.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (location == null) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.d("GPS Enabled", "GPS Enabled");
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
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


    private class DownloadCustData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
              /*  Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);*/
            } catch (Exception e) {
                e.printStackTrace();

            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Downloading consumer data", "Please Wait:: connecting to server");
            } else {
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
                                MainActivity.this.finish();
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
            if (getSBMDataCount() > 0) {
                sendDataNow();
            }
            deleteDeniedData();
            new InsertIntoDb().execute(str);
        }

    }

    private void sendDataNow() {
        String username = sharedPreferenceClass.getValue_string("un");
        StringBuilder dump = new StringBuilder();
        //  ArrayList<String>dd = new ArrayList<>();
        String LcollMode, rcptTime, rcptDate, LReceiptNo, Lsendflg, LcustID, LConcc, Lpaymode, Lcollmonth, LBalRemain, LamountPay, LtransId, LBankName, Lddno, Ldddate, Lchqno, Lchqdate;

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        String strSelectSQL_01 = "Select" +
                " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,'',A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH, A.TOT_PAID,TRANS_ID,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,RTGS_DATE,A.OPERATION_TYPE,A.SPINNER_NON_ENERGY" +
                " FROM " +
                " COLL_SBM_DATA A ";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + rs);
        if (rs.moveToFirst()) {
            do {
                LConcc = rs.getString(0);
                LcustID = rs.getString(1);
                Lpaymode = rs.getString(23);
                Lchqno = rs.getString(24);
                Lchqdate = rs.getString(25);
                Lcollmonth = rs.getString(10);
                LBalRemain = rs.getString(40);
                LamountPay = String.valueOf(rs.getInt(41));
                LtransId = "" + rs.getString(42);
                Lsendflg = rs.getString(32);
                LcollMode = "" + rs.getString(48);
                rcptTime = rs.getString(19);
                rcptDate = rs.getString(18);
                // rtgsNo = rs.getString(45);
                // rtgsDate = rs.getString(46);
                String BillType = "";
                String pay_cnt = rs.getString(47);
                if (LcollMode.equals("ADV")) {
                    BillType = "D";
                } else {
                    if (pay_cnt.equals("0")) {
                        BillType = "B";
                    } else if (pay_cnt.equals("1")) {
                        BillType = "A";
                    } else if (pay_cnt.equals("2")) {
                        BillType = "C";
                    } else {
                        BillType = "E";
                    }
                }
                LReceiptNo = BillType + rs.getString(1) + rs.getString(10);

                String raw = username + "|" + LConcc + "|" + LcustID + "|" + LamountPay + "|" + Lpaymode + "|" + LtransId + "|" + LReceiptNo + "|" + Lcollmonth + "|" + "MD" + "|" + rcptDate + "|" + rcptTime + "|" + LBalRemain + "|" + LcollMode + "|" + Lsendflg + ";\n";
                dump = dump.append(raw);
                //  dd.add(ss);
                // do what ever you want here
            } while (rs.moveToNext());
        }
        rs.close();
        //Toast.makeText(MainActivity.this, ""+dump, Toast.LENGTH_LONG).show();
        uploadSBMDump(username, dump);

    }

    private void uploadSBMDump(String username, StringBuilder dump) {
        /*ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerLinks.logData,
                response -> {
                    try {

                        deleteCollSBMData();
//                        int tdate = Integer.parseInt(CommonMethods.getCurrentDate());
//                        if (tdate > 5) {
//                            deleteCollSBMDataBKP();
//                        }

                    } catch (Exception jsn) {
                        jsn.printStackTrace();
                        // progressDialog.dismiss();
                        //Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                }, error -> {
            // progressDialog.dismiss();
            // Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, "Response Error "+error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("log", String.valueOf(dump));
                param.put("un", username);
                return param;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
        int socketTimeout = 30000;

        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

    }

    private int getSBMDataCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA ";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void deleteDeniedData() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String deleteData = "DELETE from DENIEDCONSUMER where SEND_FLG = '1'";
        DatabaseAccess.database.execSQL(deleteData);
        databaseAccess.close();
    }

    private void deleteCustData() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String insertCredentials = "DELETE from CUST_DATA";
        DatabaseAccess.database.execSQL(insertCredentials);
        databaseAccess.close();
    }

    private void deleteCollSBMData() {
        int curDate = Integer.parseInt(CommonMethods.getCurrentDate());
        int curMonth = Integer.parseInt(CommonMethods.getCurrentMonth());
        Toast.makeText(MainActivity.this, curMonth, Toast.LENGTH_SHORT).show();
        curMonth = curMonth - 1;
        if (curMonth == 0) {
            curMonth = 12;
        }
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        String deleteData = "DELETE from COLL_SBM_DATA where SEND_FLG = '1'";
        DatabaseAccess.database.execSQL(deleteData);

        String deleteBkpData = "DELETE from COLL_SBM_DATA_BKP where cast(strftime('%m', recpt_date) AS INTEGER)=" + curMonth;
        DatabaseAccess.database.execSQL(deleteBkpData);

        String deleteDataOTS = "DELETE from OTSConsumerDataUpload where SEND_FLG = '1'";
        DatabaseAccess.database.execSQL(deleteDataOTS);
        databaseAccess.close();
    }

    private class InsertIntoDb extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(MainActivity.this);

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
                        Log.d("Printing JSONObject",jsonObject.toString());

                            JSONArray jsonArray = jsonObject.getJSONArray("cData");
                            final int max = jsonArray.length();
                            if (jsonArray.length() > 0) {

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
                                CommonMethods.saveBooleanPreference(MainActivity.this, isDataSynced, true);
                                //insertCustData(Division, AccNo, ConsRef, SdoCd, SectionName, Name, address1, address2, CurrentAmount, TotalAmount, REBATE, DUEDATE, MobileNo, BINDER, CollectionCount, BILL_MTH, Advance_Count, ConsumerFlag, payDate, lastPayAmount, lastPayMode, paymentRcpt, scno, latitude, longitude, false, true);

                            } else {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "No records found", Toast.LENGTH_SHORT).show());
                            }

                        //download OTS Data
                        try {
                            JSONArray jsonArrayOTS = jsonObject.getJSONArray("otsData");
                            if(jsonArrayOTS!=null) {
                                final int maxOTS = jsonArrayOTS.length();
                                if (jsonArrayOTS.length() > 0) {
                                    runOnUiThread(() -> {
                                        progressDialog1.setCancelable(false);
                                        progressDialog1.setMessage("Downloading OTS data don't " +
                                                "press any key, please wait...");
                                        progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressDialog1.setProgress(0);
                                        progressDialog1.setMax(maxOTS);
                                        progressDialog1.show();
                                    });
                                    for (int i = 0; i < jsonArrayOTS.length(); i++) {
                                        progressDialog1.setProgress(i);
                                        JSONObject otsJsonObject = jsonArrayOTS.getJSONObject(i);
                                        CommonMethods.downloadOTS(context, otsJsonObject);
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                            "No OTS records found", Toast.LENGTH_SHORT).show());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }




                    }else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());

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
        useridfl = userID.getText().toString();
        if (doOpen) {
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
        }
        String strSelectSQL_02 = "INSERT INTO CUST_DATA  " +
                " (USER_ID, DIVISION_CODE, CONS_ACC, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, CON_ADD2, PRSN_KWH, CUR_TOTAL, BILL_TOTAL , REBATE, DUE_DATE, MOBILE_NO, EMAIL, PAY_CNT, VTYPE, PYMT_DATE, PYMT_AMT, PYMT_MODE, PYMT_RCPT, COLOR_FLG, FIELD1,FIELD2, FIELD3)" +
                " VALUES('" + useridfl + "','" + division + "','" + sdoCd + binder+accNo + "','" + consRef + "'," +
                " '" + division + "','" + sdoCd + "','" + sectionName + "','" + name + "','" + address1 + "','" + address2 + "'," +
                " '" + bill_mth + "', '" + currentAmount.toString() + "', '" + totalAmount.toString() + "', '" + rebate.toString() + "', '" + duedate + "', '" + mobileNo + "', '" + "email" + "', '" + collectionCount + "', '" + advance_count + "', '" + payDate + "', '" + lastPayAmount + "', '" + lastPayMode + "', '" + paymentRcpt + "', '" + consumerFlag + "', '" + scno + "', '" + latitude + "', '" + longitude + "') ";// "', '" + "1" +
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);
        if (doClose) {
            databaseAccess.close();
            CommonMethods.saveBooleanPreference(this, isDataSynced, true);
         /*   Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                    startActivity(ColDashboard);*/
        }

    }

    private class UserAuthOnline extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
               /* Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
                /*Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);*/
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();

                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Not Connectd to Server");
                alertDialogBuilder.setMessage("Please Retry")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();*/
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Authorization Check", "Please Wait:: connecting to server");
            } else {
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
                                MainActivity.this.finish();
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
            //Log.d("App", " str   " + str);
            String dateResponse = "";
            SharedPreferences sessionUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
            SharedPreferences.Editor sessionData = sessionUrl.edit();
            sessionData.putString("savedUrl", savedUrl);
            sessionData.apply();

            progressDialog.dismiss();
            if (str != null) {
                if (progressDialog != null) {
                    progressDialog.dismiss();

                }
                switch (str.substring(0, 1)) {
                    case "1":
                        //Toast.makeText(MainActivity.this, "Switch case 1  - "+str.substring(0, 1), Toast.LENGTH_SHORT).show();
                        //case1
                        String Max_Date = "";
                        String todaydate = "";
                        String yr = str.substring(1, 5);
                        String mm = str.substring(5, 7);
                        String dd = str.substring(7, 9);
                        todaydate = yr + "-" + mm + "-" + dd;
                        Log.d("App", "in Loop todaydate" + todaydate);
                        String[] BillInfo = str.split("[|]");
                        try {
                            sdflg = BillInfo[1];
                            advflg = BillInfo[2];
                            rcflg = BillInfo[3];
                            assflg = BillInfo[4];
                            asdlg = BillInfo[5];
                            pfflg = BillInfo[6];
                            dwflg = BillInfo[7];
                            blflg = BillInfo[8];
                            accolflg = BillInfo[9];
                            nonaccolflg = BillInfo[10];
                            cashcollflg = BillInfo[11];
                            chqcollflg = BillInfo[12];
                            ddcollflg = BillInfo[13];
                            poscollflg = BillInfo[14];
                            Tcflg = BillInfo[15];
                            usname = BillInfo[16];
                            dbpwdnm = BillInfo[17];
                            sbmBlPrv = BillInfo[18];
                            version = BillInfo[19];
                            dateResponse = BillInfo[20];
                            limit = BillInfo[21];
                            NoticeTitle = BillInfo[22];
                            NoticeDescription = BillInfo[23];
                            NoticeDate = BillInfo[24];
                            energy = String.valueOf(BillInfo[25]);
                            nonenergy = String.valueOf(BillInfo[26]);
                            nsc = String.valueOf(BillInfo[27]);
                            csc = String.valueOf(BillInfo[28]);
                            dnd = String.valueOf(BillInfo[29]);
                            frm = String.valueOf(BillInfo[30]);
                            sharedPreferenceClass.setValue_string("noticetitle", NoticeTitle);
                            sharedPreferenceClass.setValue_string("noticedes", NoticeDescription);
                            sharedPreferenceClass.setValue_string("noticedate", NoticeDate);

                            if (!version.equals(BuildConfig.VERSION_NAME)) {

                                showWarning();
                                return;
                            }
                            databaseAccess = DatabaseAccess.getInstance(context);
                            databaseAccess.open();

                            //String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "' WHERE USERID='" + useridfl + "'";
                            String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "',energy_flag='" + energy + "',non_energy_flag='" + nonenergy + "',NSC_flag='" + nsc + "',CSC_flag='" + csc + "',DND_flag='" + dnd + "',FRM_flag='" + frm + "' WHERE USERID='" + useridfl + "'";
                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                            databaseAccess.close();

                            //  sbmBlPrv="0";
                            if (sbmBlPrv.equals("1")) {
                                databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                String strSelectSQL_01 = "";
                                strSelectSQL_01 = "UPDATE SA_USER SET PRV_BILL=1";
                                strSelectSQL_01 = strSelectSQL_01 + " WHERE USERID='" + useridfl + "'";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                                DatabaseAccess.database.execSQL(strSelectSQL_01);
                                databaseAccess.close();
                            } else {
                                databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                String strSelectSQL_01 = "";
                                strSelectSQL_01 = "UPDATE SA_USER SET PRV_BILL=0";
                                strSelectSQL_01 = strSelectSQL_01 + " WHERE USERID='" + useridfl + "'";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                                DatabaseAccess.database.execSQL(strSelectSQL_01);
                                databaseAccess.close();
                            }
                            //  nonaccolflg="0";
                            //  cashcollflg="1";
                            //  chqcollflg="0";
                            //  ddcollflg="0";
                            //  poscollflg="0";
                            Log.d("DemoApp", "in asdlg" + asdlg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //checking date
                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now')  " +
                                " FROM sa_user  where date('now')>=valid_startdate and date('now')<=valid_enddate and lock_flag=0 " +
                                " and userid='" + useridfl + "' and passkey='" + Password + "'";
                        Cursor cursor = databaseAccess.database.rawQuery(strSelectSQL_01, null);
                        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
                        while (cursor.moveToNext()) {
                            Max_Date = cursor.getString(11);// phone current date
                            Log.d("DemoApp", "in Loop" + Max_Date);
                        }
                        cursor.close();

                        try {
                            Date dates = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates.getTime()).toString());
                            //before// 21052021232248
                            String serverDate = dateResponse.substring(0, 8);
                            //after// 21052021

                            Date serverDateParse = sdf.parse(CommonMethods.getFormattedDateDDMMYYYY(serverDate));

                            Log.v("ServerDate:::", serverDateParse.toString());
                            if (!version.equals(BuildConfig.VERSION_NAME)) {

                                showWarning();

                            } else if (currentDate.compareTo(serverDateParse) == 0) {
                                SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
                                SharedPreferences.Editor ssodata = sessionssodata.edit();
                                ssodata.putString("sdflg", sdflg);
                                ssodata.putString("advflg", advflg);
                                ssodata.putString("rcflg", rcflg);
                                ssodata.putString("assflg", assflg);
                                ssodata.putString("asdlg", asdlg);
                                ssodata.putString("pfflg", pfflg);
                                ssodata.putString("dwflg", dwflg);
                                ssodata.putString("blflg", blflg);
                                ssodata.putString("Tcflg", Tcflg);
                                ssodata.putString("accolflg", accolflg);
                                ssodata.putString("nonaccolflg", nonaccolflg);
                                ssodata.putString("cashcollflg", cashcollflg);
                                ssodata.putString("chqcollflg", chqcollflg);
                                ssodata.putString("ddcollflg", ddcollflg);
                                ssodata.putString("poscollflg", poscollflg);
                                ssodata.putString("usname", usname);
                                ssodata.putString("dbpwdnm", dbpwdnm);
                                ssodata.putString("sbmBlPrv", sbmBlPrv);
                                ssodata.putString("serverDate", CommonMethods.getFormattedDateDDMMYYYY(serverDate));

                                Log.d("DemoApp", "in    asdlg:" + dbpwdnm);

                                System.out.println("sdfg" + usname);

                                ssodata.commit(); // commit changes

                                CommonMethods.saveStringPreference(getApplicationContext(), userIdPref, userID.getText().toString());
                                CommonMethods.saveStringPreference(getApplicationContext(), passwordPref, PwdID.getText().toString());
                                CommonMethods.saveStringPreference(getApplicationContext(), datePref, CommonMethods.getTodaysDate());


                                Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                                startActivity(ColDashboard);


                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("Please Check Current Date");
                                alertDialogBuilder.setMessage("Change the Date and Login !!")
                                        .setCancelable(false)
                                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                MainActivity.this.finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "2": {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Login Denied!");
                        alertDialogBuilder.setMessage("Another mobile number registered with this device")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        break;
                    }
                    default: {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Invalid Userid or Password or No Privilege!");
                        alertDialogBuilder.setMessage("Please Click Retry for Login !!")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        break;
                    }
                }
            } else {
                chkresponse = 2;
                funcUrlCheck(chkresponse);
            }
        }


    }

    private class UserAuthUpdate extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog;
        String un = sharedPreferenceClass.getValue_string("un");
        String pw = sharedPreferenceClass.getValue_string("pw");
        String mobile = sharedPreferenceClass.getValue_string("mobile");


        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
               /* Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
                /*Log.d("DemoApp", " start   " + start);
                Log.d("DemoApp", " end   " + end);*/
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();

                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Not Connectd to Server");
                alertDialogBuilder.setMessage("Please Retry")
                        .setCancelable(false)
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();*/
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Authorization Check", "Please Wait:: connecting to server");
            } else {
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
                                MainActivity.this.finish();
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
            //Log.d("App", " str   " + str);
            String dateResponse = "";
            SharedPreferences sessionUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
            SharedPreferences.Editor sessionData = sessionUrl.edit();
            sessionData.putString("savedUrl", savedUrl);
            sessionData.apply();

            progressDialog.dismiss();
            if (str != null) {
                if (progressDialog != null) {
                    progressDialog.dismiss();

                }
                switch (str.substring(0, 1)) {
                    case "1":
                        //Toast.makeText(MainActivity.this, "Switch case 1  - "+str.substring(0, 1), Toast.LENGTH_SHORT).show();
                        //case1
                        String Max_Date = "";
                        String todaydate = "";
                        String yr = str.substring(1, 5);
                        String mm = str.substring(5, 7);
                        String dd = str.substring(7, 9);
                        todaydate = yr + "-" + mm + "-" + dd;
                        Log.d("App", "in Loop todaydate" + todaydate);
                        String[] BillInfo = str.split("[|]");
                        try {
                            sdflg = BillInfo[1];
                            advflg = BillInfo[2];
                            rcflg = BillInfo[3];
                            assflg = BillInfo[4];
                            asdlg = BillInfo[5];
                            pfflg = BillInfo[6];
                            dwflg = BillInfo[7];
                            blflg = BillInfo[8];
                            accolflg = BillInfo[9];
                            nonaccolflg = BillInfo[10];
                            cashcollflg = BillInfo[11];
                            chqcollflg = BillInfo[12];
                            ddcollflg = BillInfo[13];
                            poscollflg = BillInfo[14];
                            Tcflg = BillInfo[15];
                            usname = BillInfo[16];
                            dbpwdnm = BillInfo[17];
                            sbmBlPrv = BillInfo[18];
                            version = BillInfo[19];
                            dateResponse = BillInfo[20];
                            limit = BillInfo[21];
                            NoticeTitle = BillInfo[22];
                            NoticeDescription = BillInfo[23];
                            NoticeDate = BillInfo[24];
                            energy = String.valueOf(BillInfo[25]);
                            nonenergy = String.valueOf(BillInfo[26]);
                            nsc = String.valueOf(BillInfo[27]);
                            csc = String.valueOf(BillInfo[28]);
                            dnd = String.valueOf(BillInfo[29]);
                            frm = String.valueOf(BillInfo[30]);
                            sharedPreferenceClass.setValue_string("noticetitle", NoticeTitle);
                            sharedPreferenceClass.setValue_string("noticedes", NoticeDescription);
                            sharedPreferenceClass.setValue_string("noticedate", NoticeDate);

                            if (!version.equals(BuildConfig.VERSION_NAME)) {

                                showWarning();
                                return;
                            }
                            databaseAccess = DatabaseAccess.getInstance(context);
                            databaseAccess.open();

                            //String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "' WHERE USERID='" + useridfl + "'";
                            String strSelectSQL_02 = "UPDATE SA_USER SET BAL_REMAIN='" + limit + "',energy_flag='" + energy + "',non_energy_flag='" + nonenergy + "',NSC_flag='" + nsc + "',CSC_flag='" + csc + "',DND_flag='" + dnd + "',FRM_flag='" + frm + "' WHERE USERID='" + useridfl + "'";
                            Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
                            DatabaseAccess.database.execSQL(strSelectSQL_02);

                            databaseAccess.close();

                            //  sbmBlPrv="0";
                            if (sbmBlPrv.equals("1")) {
                                databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                String strSelectSQL_01 = "";
                                strSelectSQL_01 = "UPDATE SA_USER SET PRV_BILL=1";
                                strSelectSQL_01 = strSelectSQL_01 + " WHERE USERID='" + useridfl + "'";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                                DatabaseAccess.database.execSQL(strSelectSQL_01);
                                databaseAccess.close();
                            } else {
                                databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                String strSelectSQL_01 = "";
                                strSelectSQL_01 = "UPDATE SA_USER SET PRV_BILL=0";
                                strSelectSQL_01 = strSelectSQL_01 + " WHERE USERID='" + useridfl + "'";
                                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                                DatabaseAccess.database.execSQL(strSelectSQL_01);
                                databaseAccess.close();
                            }
                            // nonaccolflg="0";
                            //   cashcollflg="1";
                            //  chqcollflg="0";
                            //  ddcollflg="0";
                            //  poscollflg="0";
                            Log.d("DemoApp", "in asdlg" + asdlg);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //checking date
                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now')  " +
                                " FROM sa_user  where date('now')>=valid_startdate and date('now')<=valid_enddate and lock_flag=0 " +
                                " and userid='" + useridfl + "' and passkey='" + Password + "'";
                        Cursor cursor = databaseAccess.database.rawQuery(strSelectSQL_01, null);
                        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
                        while (cursor.moveToNext()) {
                            Max_Date = cursor.getString(11);// phone current date
                            Log.d("DemoApp", "in Loop" + Max_Date);
                        }
                        cursor.close();

                        try {
                            Date dates = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", dates.getTime()).toString());
                            //before// 21052021232248
                            String serverDate = dateResponse.substring(0, 8);
                            //after// 21052021

                            Date serverDateParse = sdf.parse(CommonMethods.getFormattedDateDDMMYYYY(serverDate));

                            Log.v("ServerDate:::", serverDateParse.toString());
                            if (!version.equals(BuildConfig.VERSION_NAME)) {

                                showWarning();

                            } else if (currentDate.compareTo(serverDateParse) == 0) {
                                SharedPreferences sessionssodata = getApplicationContext().getSharedPreferences("sessionval1", 0);
                                SharedPreferences.Editor ssodata = sessionssodata.edit();
                                ssodata.putString("sdflg", sdflg);
                                ssodata.putString("advflg", advflg);
                                ssodata.putString("rcflg", rcflg);
                                ssodata.putString("assflg", assflg);
                                ssodata.putString("asdlg", asdlg);
                                ssodata.putString("pfflg", pfflg);
                                ssodata.putString("dwflg", dwflg);
                                ssodata.putString("blflg", blflg);
                                ssodata.putString("Tcflg", Tcflg);
                                ssodata.putString("accolflg", accolflg);
                                ssodata.putString("nonaccolflg", nonaccolflg);
                                ssodata.putString("cashcollflg", cashcollflg);
                                ssodata.putString("chqcollflg", chqcollflg);
                                ssodata.putString("ddcollflg", ddcollflg);
                                ssodata.putString("poscollflg", poscollflg);
                                ssodata.putString("usname", usname);
                                ssodata.putString("dbpwdnm", dbpwdnm);
                                ssodata.putString("sbmBlPrv", sbmBlPrv);
                                ssodata.putString("serverDate", CommonMethods.getFormattedDateDDMMYYYY(serverDate));

                                Log.d("DemoApp", "in    asdlg:" + dbpwdnm);

                                System.out.println("sdfg" + usname);

                                ssodata.commit(); // commit changes

                                deleteNonEnData();


                                CommonMethods.saveStringPreference(getApplicationContext(), userIdPref, userID.getText().toString());
                                CommonMethods.saveStringPreference(getApplicationContext(), passwordPref, PwdID.getText().toString());
                                CommonMethods.saveStringPreference(getApplicationContext(), datePref, CommonMethods.getTodaysDate());


                                if (energy.equals("1")) {
                                    login.setKeepScreenOn(true);
                                    new DownloadCustData().execute(CommonMethods.getDownloadUrlNow(un, pw, mobile));
                                } else {
                                    Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                                    startActivity(ColDashboard);
                                }


                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle("Please Check Current Date");
                                alertDialogBuilder.setMessage("Change the Date and Login !!")
                                        .setCancelable(false)
                                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                MainActivity.this.finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "2": {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Login Denied!");
                        alertDialogBuilder.setMessage("Another mobile number registered with this device")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        break;
                    }
                    default: {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Invalid Userid or Password or No Privilege!");
                        alertDialogBuilder.setMessage("Please Click Retry for Login !!")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        break;
                    }
                }
            } else {
                chkresponse = 2;
                funcUrlCheck(chkresponse);
            }
        }


    }


    private void deleteNonEnData() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String deleteData = "DELETE from COLL_NEN_DATA where SEND_FLG = '1'";
        DatabaseAccess.database.execSQL(deleteData);
        //databaseAccess.close();
    }

    private void showWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Version Conflict");
        alertDialogBuilder.setMessage("Please contact department or update for latest version of APK !!")
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openPlaystore();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openPlaystore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private String funcUrlCheck(int resCode) {

        String device_id = CommonMethods.getDeviceid(getApplicationContext());
        String company_id = CommonMethods.getCompanyID();

        if (resCode == 1) {
            AuthURL = ServerLinks.mcollection1 + "un=" + useridfl + "&pw=" + Password + "&deviceId=" + device_id + "&strCompanyID=" + company_id;
            //AuthURL = "https://collectionapi.tpsouthernodisha.com/mcollection1.aspx?un=" + useridfl + "&pw=" + Password + "&deviceId=" + device_id + "&strCompanyID=" + company_id;


            new UserAuthOnline().execute(AuthURL);
        } else if (resCode == 2) {
            //AuthURL="http://portal.tpcentralodisha.com:8070/IncomingSMS/CESU_mCollection1.jsp?strCompanyID="+CompanyID+"&un="+useridfl+"&pw="+Password+"&imei="+imeinum;
            AuthURL = ServerLinks.mcollection1 + "un=" + useridfl + "&pw=" + Password + "&deviceId=" + device_id + "&strCompanyID=" + company_id;

            new UserAuthOnline().execute(AuthURL);

        } else if (resCode == 5) {
            //AuthURL="http://portal.tpcentralodisha.com:8070/IncomingSMS/CESU_mCollection1.jsp?strCompanyID="+CompanyID+"&un="+useridfl+"&pw="+Password+"&imei="+imeinum;
            AuthURL = ServerLinks.mcollection1 + "un=" + useridfl + "&pw=" + Password + "&deviceId=" + device_id + "&strCompanyID=" + company_id;

            new UserAuthUpdate().execute(AuthURL);

        } else {
            AuthURL = "ServerOut";
            int code = AlertErrorCall.ErrorMsgType(6);
            if (code == 1) {
                MainActivity.this.finish();
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Server Down");
            alertDialogBuilder.setMessage("Please Wait !! retry after some time ")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        Log.d("DemoApp", "in Loop AuthURL" + AuthURL);
        return AuthURL;
    }

    private void insertCustomerData(String custData[], boolean doClose, boolean doOpen) {

        if (doOpen) {
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();   //1-4 2-10
        }
        String strSelectSQL_02 = "INSERT INTO CUST_DATA  " +
                " (USER_ID, DIVISION_CODE, CONS_ACC, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, CON_ADD2, PRSN_KWH, CUR_TOTAL, BILL_TOTAL , REBATE, DUE_DATE, MOBILE_NO, EMAIL, PAY_CNT, VTYPE, PYMT_DATE, PYMT_AMT, PYMT_MODE, PYMT_RCPT, COLOR_FLG, FIELD1,FIELD2, FIELD3)" +
                " VALUES('" + useridfl + "','" + custData[0].replace("'", "''") + "','" + custData[4].replace("'", "''") + custData[14].replace("'", "''") + custData[1].replace("'", "''") + "','" + custData[2].replace("'", "''") + "'," +
                " '" + custData[3].replace("'", "''") + "','" + custData[4].replace("'", "''") + "','" + custData[5].replace("'", "''") + "','" + custData[6].replace("'", "''") + "','" + custData[7].replace("'", "''") + "','" + custData[8].replace("'", "''") + "'," +
                " '" + custData[16].replace("'", "''") + "', '" + custData[9].replace("'", "''") + "', '" + custData[10].replace("'", "''") + "', '" + custData[11].replace("'", "''") + "', '" + custData[12].replace("'", "''") + "', '" + custData[13].replace("'", "''") + "', '" + custData[18].replace("'", "''") + "', '" + custData[15].replace("'", "''") + "', '" + custData[17].replace("'", "''") + "', '" + custData[19].replace("'", "''") + "', '" + custData[20].replace("'", "''") + "', '" + custData[21].replace("'", "''") + "', '" + custData[22].replace("'", "''") + "', '" + custData[18].replace("'", "''") + "', '" + custData[23].replace("'", "''") + "', '" + custData[24].replace("'", "''") + "', '" + custData[25].replace("'", "''") + "') ";// "', '" + "1" +
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);
        DatabaseAccess.database.execSQL(strSelectSQL_02);
        if (doClose) {
            databaseAccess.close();
            CommonMethods.saveBooleanPreference(this, isDataSynced, true);
         /*   Intent ColDashboard = new Intent(getApplicationContext(), CollectionDashBoard.class);
                    startActivity(ColDashboard);*/
        }
    }

    private int getOfflineCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
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
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    wayLatitude = location.getLatitude();
                                    wayLongitude = location.getLongitude();

                                    latitude = String.valueOf(wayLatitude);
                                    longitude = String.valueOf(wayLongitude);

                                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                                    List<Address> addresses;

                                    try {
                                        addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (addresses.size() > 0) {
                                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                            city = addresses.get(0).getLocality();
                                            String subLocality = addresses.get(0).getSubLocality();
                                            state = addresses.get(0).getAdminArea();
                                            String country = addresses.get(0).getCountryName();
                                            String postalCode = addresses.get(0).getPostalCode();
                                            String knownName = addresses.get(0).getFeatureName();

                                            sharedPreferenceClass.setValue_string("Latitude", latitude);
                                            sharedPreferenceClass.setValue_string("Longitude", longitude);
                                            sharedPreferenceClass.setValue_string("Address", address);
                                            sharedPreferenceClass.setValue_string("City", city);
                                            sharedPreferenceClass.setValue_string("State", state);

                                            //Toast.makeText(context, "Lat:- "+latitude+" Long:- "+longitude+" Address:- "+address+" City:- "+city+" State:- "+state, Toast.LENGTH_SHORT).show();

                                            //progressDialog.dismiss();
                                            //getData();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (ActivityCompat.checkSelfPermission(
                                            MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                                }
                            }
                        });
                    }
                } else {
                    // Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(
                    MainActivity.this, PERMISSIONS, Constants.LOCATION_REQUEST);
        } else {
            getLastLocation();

        }
    }

    private boolean isLocationEnabled() {
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
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        sharedPreferenceClass.setValue_string("Lat", String.valueOf(location.getLatitude()));
                        sharedPreferenceClass.setValue_string("Long", String.valueOf(location.getLongitude()));
                    }
                });

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            checkPermission();
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_REQUEST);

        } else {
          /*  gps = new GPSTracker(MainActivity.this);

// check if GPS enabled
            if(gps.canGetLocation()){
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Toast.makeText(context, "Lat = "+latitude+": Long = "+longitude, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "no location found", Toast.LENGTH_SHORT).show();

// can't get location
// GPS or Network is not enabled
// Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }*/
            // locationManager.addGpsStatusListener(this);

            //LocationListener listener = (LocationListener) this;

            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, MIN_DISTANCE, listener);


        }
    }

    private String getPrivilageFlags() {
        String energy_flag = "0";
        String un = sharedPreferenceClass.getValue_string("un");
        try {
            databaseAccess = DatabaseAccess.getInstance(MainActivity.this);
            databaseAccess.open();
            String strSelectSQL_01 = "SELECT energy_flag,non_energy_flag," +
                    "NSC_flag,CSC_flag,DND_flag,FRM_flag FROM SA_User WHERE " +
                    "lock_flag=0 and userid='" + un + "'";
            Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
            while (cursor.moveToNext()) {
                energy_flag = cursor.getString(0);
               /* non_energy_flag = cursor.getString(1);
                NSC_flag = cursor.getString(2);
                CSC_flag = cursor.getString(3);
                DND_flag = cursor.getString(4);
                FRM_flag = cursor.getString(5);*/
            }
            databaseAccess.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return energy_flag;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    //OTS Implementation

    private void deleteData() {
        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String delQueryCust = "DELETE from CUST_DATA";
        DatabaseAccess.database.execSQL(delQueryCust);
        String delQueryOTS = "DELETE from OTSConsumerData";
        DatabaseAccess.database.execSQL(delQueryOTS);
        String deleteData = "DELETE from DENIEDCONSUMER where SEND_FLG = '1'";
        DatabaseAccess.database.execSQL(deleteData);
        databaseAccess.close();
    }

}
