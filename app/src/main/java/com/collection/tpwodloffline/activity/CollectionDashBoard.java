package com.collection.tpwodloffline.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.collection.tpwodloffline.SetPrinterTypeActivity;
import com.collection.tpwodloffline.Testing.PrintRecptAmigoThermalTesting;
import com.collection.tpwodloffline.UploadManager;
import com.collection.tpwodloffline.nonenergy.NonEnergyDashboard;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CollectionDashBoard extends AppCompatActivity {

    public DatabaseAccess databaseAccess = null;

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethods.checkConnection(getApplicationContext());
        scheduleBackgrounService();  // need to uncomment later
      /*  if (timer != null) {
            timer.cancel();
            // Log.i("Main", "cancel timer");
            timer = null;
        }
*/
    }

    @Override
    protected void onPause() {
        super.onPause();

       /* timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, 900000); //auto logout in 5 minutes*/
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(CollectionDashBoard.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
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

    CardView notice_card;
    TextView uid,NoticeTv,NoticeHead;
    String usname,device_id,noticeTitle,noticeDes,noticeDate;
    SharedPreferenceClass sharedPreferenceClass;
    String energy_flag = "0";
    String non_energy_flag = "0";
    String NSC_flag = "0";
    String CSC_flag = "0";
    String DND_flag = "0";
    String FRM_flag = "0";
    public BottomSheetDialog mBottomSheetDialog;

    private static final int BT_PERMISSION_CODE2 = 666;

    private void requestBTPermission() {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;*/

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION}, BT_PERMISSION_CODE2);

            }

        }
        //And finally ask for the permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION}, BT_PERMISSION_CODE2);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        requestBTPermission();
        // CommonMethods.saveBooleanPreference(this, isDataSynced, false);
        //scheduleBackgrounService();  // need to uncomment later
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CardView reports =  findViewById(R.id.adv_col);
        CardView printer_setup = findViewById(R.id.printer_setup);
        CardView Colbtn =  findViewById(R.id.acc_coll);
        CardView printer_test = findViewById(R.id.printer_test);
        //to flash the button as per user previlage///
        //  Bundle StoreSession = getIntent().getExtras();
        //   String prv_flg = StoreSession.getString("Previlage");
        uid = (TextView) findViewById(R.id.uid);
        NoticeTv = (TextView) findViewById(R.id.notices);
        NoticeHead = (TextView) findViewById(R.id.notice_head);
        notice_card = (CardView) findViewById(R.id.notice_card);

        sharedPreferenceClass= new SharedPreferenceClass(CollectionDashBoard.this);
        usname = sharedPreferenceClass.getValue_string("un");
        noticeTitle = sharedPreferenceClass.getValue_string("noticetitle");
        noticeDes = sharedPreferenceClass.getValue_string("noticedes");
        noticeDate = sharedPreferenceClass.getValue_string("noticedate");
        //device_id = CommonMethods.getDeviceid(getApplicationContext());
        //String currentDateString = DateFormat.getDateInstance().format(new Date());
        getPrivilageFlags();

        if(noticeTitle.equals("")){
            notice_card.setVisibility(View.GONE);
        }else {
            notice_card.setVisibility(View.VISIBLE);
            uid.setText("Posted on ~ "+noticeDate+"\nTP Western Odisha Distribution Limited");
            NoticeHead.setText(noticeTitle);
            NoticeTv.setText(noticeDes);
        }


        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String prv_flg =sessiondata.getString("Previlage", null); // getting String
        Log.d("collApp", "prv_flg" + prv_flg);
        // StringBuilder str = new StringBuilder("checking the name of the consumer with 42 characters ");
        //  str.setLength(8);
        //   Log.d("DemoApp", "str  " + str);

        /////
        reports.setOnClickListener(view -> {

            /* Old BASE CODE of reports  Dt 04032023
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionDashBoard.this);
            alertDialogBuilder.setTitle("Confirmation");
            alertDialogBuilder.setMessage("Please select report type")
                    .setCancelable(false)
                    .setPositiveButton("Today", (dialog, id) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), DetailsReportActivity.class);
                        intent.putExtra("ReportTyp", "U");
                        intent.putExtra("CustID", "X");
                        intent.putExtra("screenName", "Daily Report");
                        startActivity(intent);
                    })
                    .setNegativeButton("Old", (dialog, id) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), OldDetailsReportActivity.class);
                        intent.putExtra("ReportTyp", "U");
                        intent.putExtra("CustID", "X");
                        intent.putExtra("screenName", "Daily Report");
                        startActivity(intent);
                    })
                    .setNeutralButton("Close", (dialog, which) -> {
                        dialog.dismiss();
                    }); */

            AlertDialog.Builder alertDialogBuilder = new AlertDialog
                    .Builder(this);
            alertDialogBuilder.setTitle("Confirmation");
            String[] reportArr;
            if (CommonMethods.validateEzetap(this)) {
                reportArr = new String[]{
                        "OTS COLLECTION",
                        "TODAY",
                        "OLD"
                };
            } else {
                reportArr = new String[]{
                        "OTS COLLECTION",
                        "TODAY",
                        "OLD"
                };
            }
            AtomicInteger checkedItem = new AtomicInteger();
            alertDialogBuilder.setSingleChoiceItems(reportArr,
                    checkedItem.get(), (dialog, which) -> checkedItem.set(which));

            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> {
                        dialog.dismiss();
                         startReport(checkedItem);
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {
                        dialog.dismiss();
                    });



            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            //finish();
        });
        printer_setup.setOnClickListener(view -> {
            Intent blsearch = new Intent(getApplicationContext(), SetPrinterTypeActivity.class);
            startActivity(blsearch);
            //finish();
        });
        printer_test.setOnClickListener(view -> {
            Cursor rs = null;
            String Usernm;

            SharedPreferences sessiondataa = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editors = sessiondataa.edit();
            Usernm = sessiondata.getString("userID", null);
            databaseAccess = DatabaseAccess.getInstance(CollectionDashBoard.this);
            databaseAccess.open();
            String strUpdateSQL_01 = "SELECT SBMPRV FROM SA_USER WHERE userid = '" + Usernm + "'";
            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
            int sbmflg = 0;
            while (rs.moveToNext()) {
                sbmflg = rs.getInt(0);
            }
            //   Log.d("DemoApp", "strUpdateSQL_01  01");
            rs.close();
            databaseAccess.close();
            if (sbmflg == 8 || sbmflg == 5 || sbmflg == 6 || sbmflg == 2) {

                startActivity(new Intent(CollectionDashBoard.this, PrintRecptAmigoThermalTesting.class));
            } else {
                Toast.makeText(CollectionDashBoard.this, "Please configure printer", Toast.LENGTH_SHORT).show();

            }
        });

        Colbtn.setOnClickListener(view -> {
            //Intent blsearch = new Intent(getApplicationContext(), ColDashboard.class);
            //startActivity(blsearch);
            try{
                openBottomsheet();

            }catch (Exception ex){
                ex.printStackTrace();
            }
            //finish();
        });
    }
    private void openBottomsheet() {
        try{
            databaseAccess = DatabaseAccess.getInstance(CollectionDashBoard.this);
            databaseAccess.open();
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View bottomSheetLayout = inflater.inflate(R.layout.bottomsheet_option_dialog, null);
            final CardView energy_coll = bottomSheetLayout.findViewById(R.id.energy_layout);
            final CardView non_energy_coll = bottomSheetLayout.findViewById(R.id.non_energy_layout);

            if(energy_flag.equals("1")){
                energy_coll.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                energy_coll.setOnClickListener(v->{
                    Intent energy = new Intent(getApplicationContext(), ColDashboard.class);
                    startActivity(energy);
                });
            }else {
                energy_coll.setCardBackgroundColor(getResources().getColor(R.color.progress_frame_bg));
            }

            if(non_energy_flag.equals("1")){
                non_energy_coll.setCardBackgroundColor(getResources().getColor(R.color.colorPrimarynonen));
                non_energy_coll.setOnClickListener(v->{
                    Intent non_energy = new Intent(getApplicationContext(), NonEnergyDashboard.class);
                    startActivity(non_energy);
                });
            }else {
                non_energy_coll.setCardBackgroundColor(getResources().getColor(R.color.progress_frame_bg));
            }

            mBottomSheetDialog = new BottomSheetDialog(CollectionDashBoard.this, R.style.SheetDialog);
            mBottomSheetDialog.setContentView(bottomSheetLayout);
            mBottomSheetDialog.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void getPrivilageFlags() {
        try{
            databaseAccess = DatabaseAccess.getInstance(CollectionDashBoard.this);
            databaseAccess.open();
            String strSelectSQL_01 = "SELECT energy_flag,non_energy_flag,NSC_flag,CSC_flag,DND_flag,FRM_flag FROM SA_User WHERE lock_flag=0 and userid='"+ usname +"'";
            Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
            while (cursor.moveToNext()) {
                energy_flag = cursor.getString(0);
                non_energy_flag = cursor.getString(1);
                NSC_flag = cursor.getString(2);
                CSC_flag = cursor.getString(3);
                DND_flag = cursor.getString(4);
                FRM_flag = cursor.getString(5);
            }
            databaseAccess.close();
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }

    private void startReport(AtomicInteger checkedItem) {
        Intent intent = null;
        if (checkedItem.get() == 0) {
            intent = new Intent(getApplicationContext(),
                    OTSReport.class);
        } else if (checkedItem.get() == 1) {
            intent = new Intent(
                    getApplicationContext(), DetailsReportActivity.class);
        } else if (checkedItem.get() == 2) {
            intent = new Intent(getApplicationContext(),
                    OldDetailsReportActivity.class);
        }
        startActivity(intent);
    }
}
