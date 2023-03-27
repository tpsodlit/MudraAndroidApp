package com.collection.tpwodloffline.otp;

import static com.collection.tpwodloffline.utils.Constants.isFirstTimeLoginPref;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.collection.tpwodloffline.BuildConfig;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.activity.MainActivity;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GenerateOTP extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE2 = 866;
    private static final int STORAGE_PERMISSION_CODE3 = 944;
    Button btnGenerateOtp, btnResendOTP;
    EditText etMobile, etOtp;
    final Context context = this;
    private  DatabaseAccess databaseAccess=null;
    private  String companyId="",Mobile;
    SharedPreferenceClass sharedPreferenceClass;

    // By Sradhendu 19/05/2021
    private static final int PERMISSION_REQUEST_CODE = 1;
    TelephonyManager telephonyManager;
    ArrayList<String> phoneNumber = new ArrayList<>();
    String SimNumber;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_o_t_p);

        //Build.VERSION.RELEASE

        sharedPreferenceClass = new SharedPreferenceClass(GenerateOTP.this);
        sharedPreferenceClass.setValue_string("from_flag","0");

        companyId=CommonMethods.getCompanyID();
        TextView version = (TextView) findViewById(R.id.textView2);



        requestWritePermission();
        String make = Build.MANUFACTURER;
        String model = Build.MODEL;
        int SDKversion = Build.VERSION.SDK_INT;
        String appversion = BuildConfig.VERSION_NAME;
        //version.setText("Offline Collection App: Ver "+appversion);
       // version.setText("App Version ~ v" + appversion+"\n"+"031221");
        version.setText(make+"-"+model+"-"+SDKversion+"\n"+"App Version ~ v" + appversion+"\n"+ServerLinks.ReleaseDate);

        if(!CommonMethods.getBooleanPreference(getApplicationContext(), isFirstTimeLoginPref, true)){
            /*String temp[]={"1006", "8882826885", "a", "333", "20-07-2020", "20-07-2022", "8882826885", "efs", "0", "0", "c"};
            onVerifyOtpSCB(temp);*/
            Intent ColDashboard = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(ColDashboard);
            finish();
        }

        //telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    /*    if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ READ_SMS,READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
          //  telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        } else {
          //  telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            // requestreadPermission();

            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
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

              *//*  if (n2.length() > 10) {
                    SimNumber2 = n1.length() >= 10 ? n2.substring(n2.length() - 10) : "";

                } else {
                    SimNumber2 = phoneNumber.get(1).trim();
                }*//*

            } else {
              //  TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
              //  String mPhoneNumber = tMgr.getLine1Number();
             //   SimNumber = (tMgr.getLine1Number());
              //  Log.v("simnooo",SimNumber);
            }
        }*/
//        Log.v("simnooo",SimNumber);

        mapIds();
    }

    private void GetDeviceIdnow() {
        String deviceId = Settings.Secure.getString
                (GenerateOTP.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.v("deviceid", deviceId);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Device id");
        dialog.setMessage("Device id is- "+ deviceId);
        dialog.setCancelable(false);
        dialog.setIcon(R.mipmap.ic_launcher_offline);
        dialog.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("deviceID", deviceId);
                clipboard.setPrimaryClip(clip);
                dialog.dismiss();
                Toast.makeText(GenerateOTP.this, "Device id copied successfully", Toast.LENGTH_SHORT).show();


            }
        });
        /*dialog.setNeutralButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

            }
        });
*/
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void mapIds(){
        btnGenerateOtp = findViewById(R.id.btnGenerateOtp);
        btnResendOTP = findViewById(R.id.btnResendOTP);
        etMobile = findViewById(R.id.etMobile);
        etOtp = findViewById(R.id.etOtp);
        etOtp.setVisibility(View.GONE);
        btnGenerateOtp.setText("Verify");
        btnResendOTP.setVisibility(View.GONE);
        setOnClickListeners();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
              /*  if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ActivityCompat.requestPermissions(this, new String[]{ READ_SMS,READ_PHONE_NUMBERS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
                    }else {
                       // ActivityCompat.requestPermissions(this, new String[]{ READ_SMS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);

                    }

                    requestWritePermission();

                    return;
                } else {
                    requestWritePermission();

                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                        for (int i = 0; i < subscription.size(); i++) {
                            SubscriptionInfo info = subscription.get(i);
                            Log.d("num", "number " + info.getNumber());
                           // Log.d("network_name", "network name : " + info.getCarrierName());
                           // Log.d("country iso", "country iso " + info.getCountryIso());
                            phoneNumber.add(info.getNumber());
                        }

                        String n1 = phoneNumber.get(0);
                        //String n2 = phoneNumber.get(1);
                        if (n1.length() > 10) {
                            SimNumber = n1.length() >= 10 ? n1.substring(n1.length() - 10) : "";

                        } else {
                            SimNumber = phoneNumber.get(0).trim();
                        }



                    }else {
                       // TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
                       // SimNumber = (tMgr.getLine1Number());
                      //  Log.v("simnooo",SimNumber);

                    }
                }*/
            case STORAGE_PERMISSION_CODE2:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    //requestreadPermission();

                    return;
                }else {
                    //requestreadPermission();
                }

        }
    }

    private void setOnClickListeners(){
        btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Mobile = etMobile.getText().toString().trim();
                String androiddeviceId = Settings.Secure.getString(GenerateOTP.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                if(btnGenerateOtp.getText().equals("Verify")){
                    generateOtp(androiddeviceId);
                }else{
                    //verifyOtp();
                }
            }
        });
        btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("HardwareIds") String androiddeviceId = Settings.Secure.getString(GenerateOTP.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                generateOtp(androiddeviceId);
            }
        });
    }
    private void verifyOtp(){
        if(isBlank(etOtp)){
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
        }else {
            //onVerifyOtpSCB();
        }
    }

    private void generateOtp(String deviceId){
        if(isBlank(etMobile)){
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();

        }/*else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !Mobile.equals(SimNumber)){

            Toast.makeText(this, "Please insert your registered sim", Toast.LENGTH_SHORT).show();

        }*/else {
            String make = Build.MANUFACTURER;
            String model = Build.MODEL;
            int version = Build.VERSION.SDK_INT;
            String versionRelease = Build.VERSION.RELEASE;
            //onOtpGenerateSCB();
            //String auth = "http://portal.tpcentralodisha.com:8070/IncomingSMS/CollAppAuth.jsp?strMobileNo="+etMobile.getText()+"&strCompanyID="+companyId;
            //String auth = "https://collectionapi.tpsouthernodisha.com/collAppAuth.aspx?strMobileNo="+etMobile.getText().toString()+"&strCompanyID="+companyId+"&device_id="+deviceId+"&make="+make+"&model="+model+"&os="+version;
            String auth = ServerLinks.collAppAuth+"strMobileNo="+etMobile.getText().toString()+"&strCompanyID="+companyId+"&device_id="+deviceId+"&make="+make+"&model="+model+"&os="+version;

            if(CommonMethods.isConnected(getApplicationContext())) {

               /* Intent printTest = new Intent(GenerateOTP.this, MainActivityTest.class);
                startActivity(printTest);*/
                new GenerateOTP.UserAuthOnline().execute(auth);
            }else{
                Toast.makeText(context, "Make sure you are connected to internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isBlank(EditText editText){
        if(TextUtils.isEmpty(editText.getText())){
            return true;
        }else{
            return false;
        }
    }

    private void requestWritePermission() {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;*/

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE2);

            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE2);

            }

        }
        //And finally ask for the permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE2);

        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE2);

        }

    }
    private void requestreadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE3);

        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE3);
    }
    private void onOtpGenerateSCB(){
        etOtp.setVisibility(View.VISIBLE);
        etMobile.setVisibility(View.GONE);
        btnGenerateOtp.setText("Verify OTP");
        btnResendOTP.setVisibility(View.VISIBLE);

    }

    private void onVerifyOtpSCB(String BillInfo[]){

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();

        String insertCredentials = "INSERT into SA_User(Office_Code, Userid, passkey, Company_ID, valid_startdate, valid_enddate, user_name, user_description, Lock_Flag, retries, prv_flg)\n" +
                "values ('"+BillInfo[3]+"','"+BillInfo[4]+"','"+BillInfo[5]+"','2', '"+BillInfo[7]+"','"+BillInfo[8]+"','"+BillInfo[9]+"', '"+BillInfo[9]+"', '0', '0' , 'c');";
        Log.d("DemoApp", "strSelectSQL_02" + insertCredentials);
        DatabaseAccess.database.execSQL(insertCredentials);
        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        editor.putString("userIdPrefill", BillInfo[4]);
      //  editor.putString("authDate", BillInfo[7]);
       Log.v("startdate",BillInfo[7]);
        sharedPreferenceClass.setValue_string("un",BillInfo[4]);
        sharedPreferenceClass.setValue_string("pw",BillInfo[5]);
        sharedPreferenceClass.setValue_string("mobile",etMobile.getText().toString().trim());
        sharedPreferenceClass.setValue_string("username",BillInfo[9]);

        editor.commit();
        Intent ColDashboard = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(ColDashboard);
        finish();
    }

    private  class UserAuthOnline extends AsyncTask<String, Integer, String> {
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
                /*Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
               /* Log.d("DemoApp", " start   " + start);
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
                progressDialog = ProgressDialog.show(GenerateOTP.this, "Verifying mobile number", "Please Wait:: connecting to server");
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
                                GenerateOTP.this.finish();
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
            if (str != null) {
                String[] BillInfo = str.split("[|]");
                if (BillInfo[0].equals("0")) {
                    //Toast.makeText(context, "Mobile number not found, please contact IT admin", Toast.LENGTH_SHORT).show();
                    CommonMethods.showDialog(GenerateOTP.this, context, "Warning","Mobile no not registered with this device");
                } else if (BillInfo[0].equals("2")) {
                    CommonMethods.showDialog(GenerateOTP.this, context, "Warning","Another mobile number already registered with this device");
                    //Toast.makeText(context, "Mobile number not allowed to do collection, please contact IT admin", Toast.LENGTH_SHORT).show();
                }else if (BillInfo[1].equals("0")) {
                    CommonMethods.showDialog(GenerateOTP.this, context, "Warning","Mobile number not allowed to do collection");
                    //Toast.makeText(context, "Mobile number not allowed to do collection, please contact IT admin", Toast.LENGTH_SHORT).show();
                } else if (BillInfo[2].equals("0")) {
                    CommonMethods.showDialog(GenerateOTP.this, context, "Warning","Account Not Activated");
                    //Toast.makeText(context, "Mobile number not allowed to do collection, please contact IT admin", Toast.LENGTH_SHORT).show();
                } else if (BillInfo[2].equals("2")) {
                    CommonMethods.showDialog(GenerateOTP.this, context, "Warning","User Already Activated");
                    //onVerifyOtpSCB(BillInfo);
                    //Toast.makeText(context, "Mobile number not allowed to do collection, please contact IT admin", Toast.LENGTH_SHORT).show();
                } else if (BillInfo[2].equals("1")) {
                    //Toast.makeText(context, "Everything is fine", Toast.LENGTH_SHORT).show();
                    onVerifyOtpSCB(BillInfo);
                }
            }
        }


    }
}
