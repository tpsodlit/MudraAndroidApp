package com.collection.tpwodloffline;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.collection.tpwodloffline.model.EzetapDbModel;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UploadManager extends Worker {

    private DatabaseAccess databaseAccess = null;
    private String custID = "";
    private String consumerNo = "";
    String entrydate,LatLong,ReasonTxt, futurepayDate, Remarks, userid, consno, cons_idfetch, divisionfetch, subdivisionfetch, sectionfetch, namefetch, add1fetch, add2fetch, billMonth, pybleamt, dueDate;
    private String DeniedUploadURL = null;
    private String RecptGenURL = null;
    private String TransID = "";
    private String custId = "";
    private String custNamenonen = "";
    private String custAdd = "";
    private String scnum = "";
    private String rem_module = "";
    private String ref_reg_num = "";
    private String section = "";
    private String RecptGenURLNen = null;
    private String ReceiptNonen = "";

    private String strmsgnen = "";
    private String TxnTime = "";
    int amount = 0;
    String paymode_;
    String remarks;
    private String lat = "";
    private String lang = "";
    private String tr_date = "";
    private String oprn_type = "";
    String BalRemains;
    String demand_date;
    private String custName = "";
    private String strmsg = "";
    private String Recfound = "";
    private String usernm = "";
    private String ActPayMode = "";
    private String CurTime = "";
    private String CurTime1 = "";
    String usname = "";
    String dbpwdnm = "";
    int amountPay = 0;
    String paymode;
    String chqno;
    String chqdate;
    String collmonth;
    String ddno;
    String dddate;
    String BankName;
    String PosTransID;
    String phoneNo;
    String BalRemain;
    String transId;
    private Context mContext;
    private String neftNo = "";
    private String neftDate = "";
    private String rtgsNo = "";
    private String rtgsDate = "";
    private String ReceiptNo = "";

    SharedPreferenceClass sharedPreferenceClass;
    String CompanyId = CommonMethods.getCompanyID();
    private String lattitude = "";
    private String longitude = "";
    private String vtype = "";
    private String collMode = "";
    HashMap<String, String> map = new HashMap<>();
    String LcollMode,rcptTime,rcptDate,LReceiptNo,Lvtype, LcustID, LConcc, Lpaymode, Lcollmonth, LBalRemain, LamountPay, LtransId, LBankName, Lddno, Ldddate, Lchqno, Lchqdate;


    public UploadManager(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
        sharedPreferenceClass = new SharedPreferenceClass(mContext);
        usname = sharedPreferenceClass.getValue_string("un");


    }

    public void backup() {
        try {
            final String inFileName = mContext.getFilesDir().getAbsolutePath() + "COLLDB.db";
            File dbFile = new File(inFileName);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dbFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            //File file = new File(dir, _fileName);
            String outFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/database_copy.db";

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
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        if (getOfflineCount() > 0) {
            getOfflineData();
            getOfflineDatatoLocal();
        } else {
            Log.d("ToUpload", "Nothing there");
        }

        if(getDenieddataCount()>0){
            UploadDeniedData();
        }
        if (getNenOfflineCount() > 0) {
            uploadOfflineNenData();
        }
        if (getEzetapDataCount() > 0) {
            uploadEzetapData();
        }

        if (new DatabaseAccess().getOfflineCountOTS(mContext) > 0) {
            uploadOTSData();
        }

        return Result.success();
    }
    private int getNenOfflineCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_NEN_DATA where SEND_FLG=0 AND RECPT_FLG='1'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    private void uploadOfflineNenData() {
        resetStringsNen();
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "Select" +
                " USER_ID,COMPANY_CODE,SCNO,REF_MODULE,REF_REG_NO,CUST_ID,DIVISION,SUBDIVISION," + //7
                " SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE,MOBILE_NO,EMAIL,RECPT_DATE,RECPT_TIME," + //16
                " MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG,OPERATOR_ID,OPERATOR_NAME,SEND_FLG," + //24
                " COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH,OPERATION_TYPE,REMARKS,LATTITUDE," + //32
                " LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE " + //39
                " FROM " +
                " COLL_NEN_DATA WHERE RECPT_FLG='1' and SEND_FLG ='0'";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while (rs.moveToNext()) {
            scnum = rs.getString(2);
            rem_module = rs.getString(3);
            ref_reg_num = rs.getString(4);
            section = rs.getString(8);
            custId = rs.getString(5);
            custNamenonen = rs.getString(9);
            custAdd = rs.getString(10);
            paymode_ = rs.getString(20);
            BalRemains = rs.getString(29);
            demand_date = rs.getString(12);
            amount = rs.getInt(19);
            TransID = "" + rs.getString(26);
            lat = rs.getString(32);
            lang = rs.getString(33);
            tr_date = rs.getString(28);
            oprn_type = rs.getString(30);
            remarks = rs.getString(31);
            String dt = rs.getString(28);
            String tm = rs.getString(16);

            TxnTime = dt +" "+ tm;

            String BillType = "W";
            ReceiptNonen = BillType+rs.getString(17);
        }
        String device_id = CommonMethods.getDeviceid(getApplicationContext());

        Log.d("userName", "" + usname);

        RecptGenURLNen = ServerLinks.postPayment_ne;
        CompanyId = CommonMethods.CompanyID;

        String postData = usname + "|" + device_id + "|" + CompanyId + "|" + scnum + "|" + rem_module + "|" + ref_reg_num + "|" + custId + "|" + section + "|" + custNamenonen + "|" + custAdd + "|" + amount + "|" + demand_date + "|" + TxnTime + "|" + ReceiptNonen + "|" + "9999999999" + "|" + paymode_ + "|" + TransID + "|" + "NRML" + "|" + tr_date + "|" + BalRemains + "|" + oprn_type + "|" + remarks + "|" + lat + "|" + lang+ "|" + "MD";
        RecptGenURLNen = RecptGenURLNen + "encKey=" + CommonMethods.encryptText(postData);

        Log.d("Url:::", RecptGenURLNen);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            new ReceiptNenGenOnline().execute(RecptGenURLNen);
        }

    }
    private class ReceiptNenGenOnline extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
                bodycontent = a.toString();
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bodycontent;
        }

        @Override
        protected void onPreExecute() {

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String strSelectSQL_01 = "";
                strSelectSQL_01 = "UPDATE COLL_NEN_DATA SET MACHINE_NO=1";
                strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custId + "' AND TRANS_ID='" + TransID + "'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            }
        }

        @Override
        protected void onPostExecute(String str) {

            try {
                Log.d("res", " str   " + str);
                strmsgnen = str;
                String pipeDelRecptInfo = str;
                String[] RecptInfo = pipeDelRecptInfo.split("[|]");
                String ConsValid = RecptInfo[0];
                String imeiflg = RecptInfo[1];
                String Txn_svr = RecptInfo[6];
                String recprno = "";
                String RecptDt = "";
                String RecptTime = "";

                if (ConsValid.equals("1")) {
                  /*  recprno = RecptInfo[3];
                    RecptDt = RecptInfo[4];

                    RecptTime = RecptDt.substring(8, 14);
                    RecptDt = RecptDt.substring(4, 8) + "-" + RecptDt.substring(2, 4) + "-" + RecptDt.substring(0, 2);
                    Log.d("DemoApp", " RecptDt   " + RecptDt);
                    Log.d("DemoApp", " RecptTime   " + RecptTime);
                    int balremain = 0;*/

                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String strSelectSQL_01 = "";
                    strSelectSQL_01 = "UPDATE COLL_NEN_DATA SET RECPT_FLG=1,SEND_FLG=1";

                    strSelectSQL_01 = strSelectSQL_01 + " WHERE REF_REG_NO='" + ref_reg_num + "' AND TRANS_ID='" + Txn_svr + "'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();
                    // progressDialog.dismiss();
                    if (getNenOfflineCount() > 0) {
                        uploadOfflineNenData();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }

    private void resetStringsNen() {
        scnum = "";
        rem_module = "";
        ref_reg_num = "";
        custId = "";
        section = "";
        custNamenonen = "";
        custAdd = "";
        amount = 0;
        demand_date = "";
        TxnTime = "";
        ReceiptNonen = "";
        paymode_ = "";
        TransID = "";
        tr_date = "";
        BalRemains = "";
        oprn_type = "";
        remarks = "";
        lat = "";
        lang = "";
    }

    private void UploadDeniedData() {
        resetDDStrings();
        String circle="";
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_03 = "Select" +
                " USER_ID, SEND_FLG, CONS_ACC, CUST_ID, DIVISION, SUBDIVISION, SECTION, CON_NAME, CON_ADD1, CON_ADD2, BILL_MTH, BILL_TOTAL , DUE_DATE, REASON, REMARKS, FIELD1, FIELD2, ENTRYDATE" +  //17
                " FROM " +
                " DENIEDCONSUMER WHERE SEND_FLG = '0'";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_03, null);
        while (rs.moveToNext()) {
            userid=rs.getString(0);
            consno= rs.getString(2);
            cons_idfetch= rs.getString(3);
            divisionfetch= rs.getString(4);
            subdivisionfetch= rs.getString(5);
            sectionfetch= rs.getString(6);
            namefetch= rs.getString(7);
            add1fetch= rs.getString(8);
            add2fetch= rs.getString(9);
            billMonth= rs.getString(10);
            pybleamt= rs.getString(11);
            dueDate= rs.getString(12);
            ReasonTxt=rs.getString(13);
            Remarks=rs.getString(14);
            LatLong=rs.getString(15); //f1
            futurepayDate=rs.getString(16);//futurepaymentdate
            entrydate=rs.getString(17);
        }

        DeniedUploadURL = ServerLinks.deniedConsumer;
        //userid, consumerNo, cons_ref, circle, division, subdivision, section, billmth,amount, duedate, reason, remarks, field1, field2, field3, field4, field5
        DeniedUploadURL = DeniedUploadURL+"userid="+userid+"&consumerNo="+consno+"&cons_ref="+cons_idfetch+"&circle="+circle+"&division="+divisionfetch+"&subdivision="+subdivisionfetch+"&section="+sectionfetch+"&billmth="+billMonth+"&amount="+pybleamt+"&duedate="+dueDate+"&reason="+ReasonTxt+"&remarks="+Remarks+"&field1="+(LatLong)+"&field2="+(futurepayDate)+"&field3="+entrydate+"&field4="+"&field5=";

        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            new UploadReason().execute(DeniedUploadURL);
        }

    }

    private void resetDDStrings() {
        entrydate="";
        LatLong="";
        ReasonTxt="";
        futurepayDate="";
        Remarks="";
        userid="";
        consno="";
        cons_idfetch="";
        divisionfetch="";
        subdivisionfetch="";
        sectionfetch="";
        namefetch="";
        add1fetch="";
        add2fetch="";
        billMonth="";
        pybleamt="";
        dueDate="";
    }
    private int getDenieddataCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL = "select count(*) from DENIEDCONSUMER where SEND_FLG='0'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL, null);
        Log.d("DeniedDataCount", "Query SQL " + strSelectSQL);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void Takebackup(HashMap<String, String> map) {
        // new file object
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        String date = df.format(c);
        // new file object
        String _fileName = "/TPWODL/"+date+"tpmudra.txt";
        //dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, _fileName);
        //File file = new File(getApplicationContext().getDir(Environment.DIRECTORY_DOCUMENTS)+_fileName);
        // File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath()+"databack.txt");
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //java.io.File xmlFile = new java.io.File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));

        File file = new File(dir, _fileName);
        if(!file.exists()){
            file.getParentFile().mkdirs();
        }
        BufferedWriter bf = null;

        try {
            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, String> entry :
                    map.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + "|" + entry.getValue());

                // new line
                bf.newLine();
            }

            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {

                // always close the writer
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getOfflineDatatoLocal() {
        int cnt = getOfflineCount();

        for (int i = 0; i < cnt; i++) {
            //resetStrings();
            databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            databaseAccess.open();
            String strSelectSQL_01 = "Select" +
                    " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                    " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                    " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                    " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                    " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                    " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                    " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH, A.TOT_PAID,TRANS_ID,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,RTGS_DATE,A.OPERATION_TYPE,A.SPINNER_NON_ENERGY" +
                    " FROM " +
                    " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and SEND_FLG = '0' AND RECPT_FLG= '1'";

            Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
            while (rs.moveToNext()) {
                LConcc = rs.getString(0);
                LcustID = rs.getString(1);
                Lpaymode = rs.getString(23);
                Lchqno = rs.getString(24);
                Lchqdate = rs.getString(25);
                Lcollmonth = rs.getString(10);
                Lddno = rs.getString(26);
                Ldddate = rs.getString(27);
                LBankName = rs.getString(37);
                //PosTransID = rs.getString(38);
                phoneNo = rs.getString(39);
                LBalRemain = rs.getString(40);
                LamountPay = String.valueOf(rs.getInt(41));
                LtransId = rs.getString(42);
                Lvtype = String.valueOf(rs.getInt(47));
                rcptTime = rs.getString(19);
                rcptDate = rs.getString(18);
                LcollMode = rs.getString(48);
                // rtgsNo = rs.getString(45);
                // rtgsDate = rs.getString(46);
                String BillType = "";
                String pay_cnt = rs.getString(47);
                if(LcollMode.equals("ADV")){
                    BillType = "D";
                }else {
                    switch (pay_cnt) {
                        case "0":
                            BillType = "B";
                            break;
                        case "1":
                            BillType = "A";
                            break;
                        case "2":
                            BillType = "C";
                            break;
                        default:
                            BillType = "E";
                            break;
                    }
                }
                LReceiptNo = BillType+rs.getString(1)+rs.getString(10);

                map.put(usname+"|"+LConcc,LamountPay+"|"+Lpaymode+"|"+LtransId+"|"+LReceiptNo+"|"+ LBankName +"|"+Lddno+Lchqno+"|"+Ldddate+Lchqdate+"|"+Lcollmonth+"|"+Lvtype+"|"+"MD"+"|"+rcptTime+"|"+rcptDate+"|"+LcollMode+";");

            }

        }
        Takebackup(map);


    }

    private void countData() {
        for (int i = 0; i < 10; i++) {
            Log.d("Data", "" + i);
        }
    }

    private int getOfflineCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0 AND RECPT_FLG='1'";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private void getOfflineData() {

        resetStrings();
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "Select" +
                " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH, A.TOT_PAID,TRANS_ID,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,RTGS_DATE,A.DB_TYPE_SERVER,A.LATTITUDE,A.LONGITUDE,A.OPERATION_TYPE,A.SPINNER_NON_ENERGY" + //51
                " FROM " +
                " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and SEND_FLG = '0' AND RECPT_FLG = '1'";

        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while (rs.moveToNext()) {
            consumerNo = rs.getString(0);
            custID = rs.getString(1);
            custName = rs.getString(5);
            paymode = rs.getString(23);
            if (paymode.equals("") || paymode.equals("Null") || paymode.equals("NULL")) {
                paymode = "1";
            }
            chqno = rs.getString(24);
            chqdate = rs.getString(25);
            collmonth = rs.getString(10);////colmonth
            ddno = rs.getString(26);
            dddate = rs.getString(27);
            BankName = rs.getString(37);
            PosTransID = rs.getString(38);
            phoneNo = rs.getString(39);
            BalRemain = rs.getString(40);
            amountPay = rs.getInt(41);
            transId = "" + rs.getString(42);
            neftNo = rs.getString(43);
            neftDate = rs.getString(44);
            rtgsNo = rs.getString(45);
            rtgsDate = rs.getString(46);
            lattitude = rs.getString(48);
            longitude = rs.getString(49);
            vtype = rs.getString(50);
            collMode = rs.getString(51);
            String dt= rs.getString(18);
            String tm = rs.getString(19);
            if(dt.contains("-")){
                dt= dt.replace("-","");
            }
            if(tm.contains(":")){
                tm = tm.replace(":","");
            }
            CurTime = dt+tm;
            String BillType = "";
            String pay_cnt = rs.getString(47);
            if(collMode.equals("ADV")){
                BillType = "D";
            }else {
                if(pay_cnt.equals("0")){
                    BillType = "B";
                }else if(pay_cnt.equals("1")) {
                    BillType = "A";
                }else if(pay_cnt.equals("2")){
                    BillType = "C";
                }else {
                    BillType = "E";
                }
            }
            ReceiptNo = BillType + rs.getString(1) + rs.getString(10);
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
        Date date = new Date();
        //CurTime = dateFormat.format(date);

        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        String imeinum = sessiondata.getString("imeinum", null);
        String device_id = CommonMethods.getDeviceid(getApplicationContext());

        SharedPreferences sessiondata1 = getApplicationContext().getSharedPreferences("sessionval1", 0);
        SharedPreferences.Editor editor1 = sessiondata.edit();
       /* usname=sessiondata1.getString("usname", null);
        dbpwdnm=sessiondata1.getString("dbpwdnm", null);*/
        usname = sharedPreferenceClass.getValue_string("un");
        dbpwdnm = sharedPreferenceClass.getValue_string("pw");

        Log.d("userName", "" + usname);
        Log.d("pwd", "" + dbpwdnm);
        Log.d("Phone", "" + phoneNo);
        Log.d("TransactionId", "" + transId);
        Log.d("collmonth", "" + collmonth);
        Log.d("BankName", "" + BankName);
        Log.d("imei", "" + imeinum);
        Log.d("AmountToPay", "" + amountPay);
        Log.d("CurrentTime", "" + CurTime);
        Log.d("DDNo", "" + ddno);
        Log.d("DDDate", "" + dddate);
        Log.d("ChequeNo", "" + chqno);
        Log.d("consumerNo", "" + consumerNo);
        Log.d("ReceiptNo", "" + ReceiptNo);


        SharedPreferences savedUrl = getApplicationContext().getSharedPreferences("sessionUrl", 0);
        String urlName = savedUrl.getString("savedUrl", null); // getting String


        RecptGenURL = ServerLinks.postPayment;
        // RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=3&ConsumerID="+custID+"&deviceId="+device_id+"&RefID=0&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+CommonMethods.getFormattedDate1(dddate)+"&PaymentMthh=0&BBPS=0&OffLine=1";
        CompanyId = CommonMethods.CompanyID;

        if(paymode.equals("2")){//dd
            ActPayMode="3";//      "+strTransDt+"
            //RecptGenURL = RecptGenURL+un=atul&pw=atulkumar&companyID=3&ConsumerID=2921028800&deviceId=9935cbbda6b4c723&RefID=0&Amount=100&DateTime=05-05-2021&PayMod=CASH&RecNo=124&BankName=NA&Ins_No=NA&ClearDate=05-05-2021&PaymentMthh=05&BBPS=0&OffLine=1
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }else if(paymode.equals("3")){//chq
            ActPayMode="2";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }else if(paymode.equals("7")) {//Pos
            ActPayMode = "7";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        else if(paymode.equals("8")) {//NEFT
            ActPayMode = "8";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        else if(paymode.equals("9")) {//RTGS
            ActPayMode = "9";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        } else if(paymode.equals("0")){//cash
            ActPayMode="1";
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);            // new OfflineRecords.ReceiptGenOnline().execute(RecptGenURL);
        }else {
            ActPayMode="1";
            // RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID="+CompanyId+"&ConsumerID="+custID+"&deviceId="+device_id+"&RefID="+ReceiptNo+"&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&mobile="+phoneNo+"&BBPS=0&OffLine=1"+"&consumerNo="+consumerNo+"&lattitude="+lattitude+"&longitude="+longitude+"&vtype=" + vtype+ "&cname=" + custName+ "&cbal=" + BalRemain + "&cmode=" + collMode;
            String postData = usname+"|"+dbpwdnm+"|"+CompanyId+"|"+custID+"|"+device_id+"|"+ReceiptNo+"|"+amountPay+"|"+CurTime+"|"+ActPayMode+"|"+transId+"|"+BankName+"|"+ddno+"|"+(dddate)+"|"+(collmonth)+"|"+phoneNo+"|0|1"+"|"+consumerNo+"|"+lattitude+"|"+longitude+"|" + vtype+ "|" + custName+ "|" + BalRemain + "|" + collMode;
            RecptGenURL = RecptGenURL+"encKey="+ CommonMethods.encryptText(postData);
        }
        Log.d("Url:::", RecptGenURL);

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            new ReceiptGenOnline().execute(RecptGenURL);
        }


    }

    private void resetStrings() {
        custID = "";
        custName = "";
        TransID = "";
        strmsg = "";
        Recfound = "";
        usernm = "";
        ActPayMode = "";
        CurTime = "";
        CurTime1 = "";
        amountPay = 0;
        paymode = "";
        chqno = "";
        chqdate = "";
        ddno = "";
        dddate = "";
        BankName = "";
        PosTransID = "";
        phoneNo = "";
        transId = "";
        lattitude = "";
        longitude = "";
        vtype = "";
        BalRemain = "";
        paymode = "";

    }

    private class ReceiptGenOnline extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
                /*Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
                //Log.d("DemoApp", " start   " + start);
                //Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String strSelectSQL_01 = "";
                strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MACHINE_NO=1";
                strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + transId + "'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            } else {
            }
        }

        @Override
        protected void onPostExecute(String str) {

            try {
                Log.d("DemoApp", " str   " + str);
                strmsg = str;
                String pipeDelRecptInfo = str;
                String[] RecptInfo = pipeDelRecptInfo.split("[|]");
                String ConsValid = RecptInfo[0];
                String imeiflg = RecptInfo[1];
                String Txn_Svr = RecptInfo[3];
                String recprno = "";
                String RecptDt = "";
                String RecptTime = "";
                //Recfound=RecptInfo[2];;
///have to check again
                if (ConsValid.equals("1")) {
                    recprno = RecptInfo[3];
                    RecptDt = RecptInfo[4];
                    RecptTime = RecptDt.substring(8, 14);
                    //RecptDt=RecptDt.substring(0,2)+"-"+RecptDt.substring(2,4)+"-"+RecptDt.substring(4,8);
                    RecptDt = RecptDt.substring(4, 8) + "-" + RecptDt.substring(2, 4) + "-" + RecptDt.substring(0, 2);
                    Log.d("DemoApp", " RecptDt   " + RecptDt);
                    Log.d("DemoApp", " RecptTime   " + RecptTime);
                    int balremain = 0;
                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String strSelectSQL_01 = "";
                    strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MR_NO='" + recprno + "',RECPT_DATE=strftime('%Y-%m-%d', '" + RecptDt + "'),RECPT_FLG=1,SEND_FLG=1,coll_flg=1,RECPT_TIME='" + RecptTime + "'";

                    strSelectSQL_01 = strSelectSQL_01 + " WHERE CUST_ID='" + custID + "' AND TRANS_ID='" + Txn_Svr + "'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();

                    if (getOfflineCount() > 0) {
                        getOfflineData();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }

    private class UploadReason extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //activity = (MainActivity)params[0];
            String strURL = params[0];
            URLConnection conn = null;
            InputStream inputStreamer = null;
            String bodycontent = null;
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
                /*Log.d("DemoApp", " fullString   " + a.toString());
                String html = a.toString();
                int start = html.indexOf("<body>") + "<body>".length();
                int end = html.indexOf("</body>", start);*/
                bodycontent = a.toString();
                //Log.d("DemoApp", " start   " + start);
                //Log.d("DemoApp", " end   " + end);
                Log.d("DemoApp", " body   " + bodycontent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bodycontent;
        }

        @Override

        protected void onPreExecute() {
            /*ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String strSelectSQL_01 ="";
                strSelectSQL_01 = "UPDATE COLL_SBM_DATA SET MACHINE_NO=1";
                strSelectSQL_01= strSelectSQL_01 +" WHERE CUST_ID='"+ custID +"' AND TRANS_ID='"+ transId +"'";
                Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                DatabaseAccess.database.execSQL(strSelectSQL_01);
                databaseAccess.close();
            }*/
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                String res  = str.substring(0,1);

                Log.d("Denied response", " str " + str);
                if(res.equals("1")){
                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String strSelectSQL_01 ="";
                    strSelectSQL_01 = "UPDATE DENIEDCONSUMER SET SEND_FLG='1' ";

                    strSelectSQL_01=strSelectSQL_01+" WHERE CUST_ID='"+ cons_idfetch +"' AND CONS_ACC='"+ consno +"'";
                    Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
                    DatabaseAccess.database.execSQL(strSelectSQL_01);
                    databaseAccess.close();

                    if(getDenieddataCount()>0){
                        UploadDeniedData();
                    }
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }


        }
    }
    //OTS Implementation
    private void uploadOTSData() {

        String email = "", reason = "", remarks = "",
                otskey = "", otsrefno = "", installmentNo = "";

        resetStrings();

        usname = sharedPreferenceClass.getValue_string("un");
        dbpwdnm = sharedPreferenceClass.getValue_string("pw");

        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        String query = "Select" +
                " A.CONS_ACC, A.CUST_ID, A.OTSKey, A.OTSReferenceNo, " +
                " A.TotalInstallment, A.InstallmentNo, " +
                " A.InstallmentAmount, A.InstallmentDate, A.InstallmentDueDate, " +
                " A.CON_NAME, A.CON_ADD1, A.CON_ADD2, " +
                " strftime('%d-%m-%Y',A.RECPT_DATE) RECPT_DATE," +
                " A.RECPT_TIME, A.MR_No, A.MACHINE_NO, A.TOT_PAID, A.PAY_MODE, " +
                " A.DD_NO, strftime('%d-%m-%Y',A.DD_DATE) DD_DATE, A.Bank_ID," +
                " A.RECPT_FLG, A.SEND_FLG, A.DEL_FLG, " +
                " A.COLL_FLG, A.PMT_TYP, b.bank_name as BankName, A.PHONE_NO, A.BAL_FETCH, " +
                " A.TRANS_ID, A.DB_TYPE_SERVER, " +
                " A.LATTITUDE, A.LONGITUDE, A.SPINNER_NON_ENERGY, " +
                " A.REASON, A.REMARKS, A.EMAIL " +
                " FROM OTSConsumerDataUpload A, mst_bank b WHERE " +
                " a.bank_id=b.bank_id and RECPT_FLG='1' " +
                " and SEND_FLG = '0'";

        Cursor rs = DatabaseAccess.database.rawQuery(query, null);
        while (rs.moveToNext()) {
            otskey = rs.getString(rs.getColumnIndexOrThrow("OTSKey"));
            otsrefno = rs.getString(rs.getColumnIndexOrThrow("OTSReferenceNo"));
            installmentNo = rs.getString(rs.getColumnIndexOrThrow("InstallmentNo"));

            consumerNo = rs.getString(rs.getColumnIndexOrThrow("CONS_ACC"));
            custID = rs.getString(rs.getColumnIndexOrThrow("CUST_ID"));
            custName = rs.getString(rs.getColumnIndexOrThrow("CON_NAME"));

            paymode = rs.getString(rs.getColumnIndexOrThrow("PAY_MODE"));
            if (paymode.equals("") || paymode.equals("Null") || paymode.equals("NULL")) {
                paymode = "1";
            }
            collmonth = "";
            ddno = rs.getString(rs.getColumnIndexOrThrow("DD_NO"));
            if (ddno == null)
                ddno = "";
            dddate = rs.getString(rs.getColumnIndexOrThrow("DD_DATE"));
            if (dddate == null)
                dddate = "";
            BankName = rs.getString(rs.getColumnIndexOrThrow("BankName"));
            phoneNo = "";
            BalRemain = rs.getString(rs.getColumnIndexOrThrow("BAL_FETCH"));
            amountPay = rs.getInt(rs.getColumnIndexOrThrow("TOT_PAID"));
            transId = rs.getString(rs.getColumnIndexOrThrow("TRANS_ID"));
            lattitude = rs.getString(rs.getColumnIndexOrThrow("LATTITUDE"));
            longitude = rs.getString(rs.getColumnIndexOrThrow("LONGITUDE"));
            collMode = rs.getString(rs.getColumnIndexOrThrow("SPINNER_NON_ENERGY"));
            vtype = "FG";
            String d = rs.getString(rs.getColumnIndexOrThrow("RECPT_DATE"));
            String t = rs.getString(rs.getColumnIndexOrThrow("RECPT_TIME"));
            if (d.contains("-")) {
                d = d.replace("-", "");
            }
            if (t.contains(":")) {
                t = t.replace(":", "");
            }
            CurTime = d + t;

            ReceiptNo = new DatabaseAccess().
                    receiptNoOTS(getApplicationContext(), custID, transId);
            email = rs.getString(rs.getColumnIndexOrThrow("EMAIL"));
            if (email == null)
                email = "";
            reason = rs.getString(rs.getColumnIndexOrThrow("REASON"));
            if (reason == null)
                reason = "";
            remarks = rs.getString(rs.getColumnIndexOrThrow("REMARKS"));
            if (remarks == null)
                remarks = "";
        }
        String device_id = CommonMethods.getDeviceid(getApplicationContext());
        RecptGenURL = ServerLinks.postPaymentOTS;
        CompanyId = CommonMethods.CompanyID;
        ActPayMode = CommonMethods.getActMode(paymode);
        if (!otskey.isEmpty()) {
            String postData = usname + "|" + dbpwdnm + "|" + CompanyId + "|"
                    + custID + "|" + device_id + "|" + ReceiptNo + "|" + amountPay
                    + "|" + CurTime + "|" + ActPayMode + "|" + transId + "|"
                    + BankName + "|" + ddno + "|" + (dddate) + "|" + (collmonth)
                    + "|" + phoneNo + "|0|1" + "|" + consumerNo + "|" + lattitude
                    + "|" + longitude + "|" + vtype + "|" + custName + "|" + BalRemain
                    + "|" + collMode + "|" + reason + "|" + remarks + "|" + email
                    + "|" + otskey + "|" + otsrefno + "|" + installmentNo;

            RecptGenURL = RecptGenURL + "encKey=" + CommonMethods.encryptText(postData);

            Log.d("Url:::", RecptGenURL);
            Log.d("UploadManager-POST OTS Data",postData);

            ConnectivityManager cm = (ConnectivityManager) mContext.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                new UploadOTSDataAsync().execute(RecptGenURL);
            }
        }
    }

    private class UploadOTSDataAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String strURL = params[0];
            String bodycontent = null, inputLine;
            try {
                URL url = new URL(strURL);
                URLConnection uc = url.openConnection();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(uc.getInputStream()));
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                bodycontent = a.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bodycontent;
        }

        @Override
        protected void onPreExecute() {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                int rows = new DatabaseAccess().
                        updateMachineNoOTS(getApplicationContext(), custID, transId);
                Log.d("MachineNo OTS", "Rows updated OTS::: " + rows);
            }
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                strmsg = str;
                String[] RecptInfo = str.split("[|]");
                String ConsValid = RecptInfo[0];
                String imeiflg = RecptInfo[1];
                String Txn_Svr = RecptInfo[3];
                String recprno, RecptDt, RecptTime;

                if (ConsValid.equals("1") && imeiflg.equals("1")) {
                    recprno = RecptInfo[3];
                    RecptDt = RecptInfo[4];
                    RecptTime = RecptDt.substring(8, 14);

                    RecptDt = RecptDt.substring(4, 8) + "-"
                            + RecptDt.substring(2, 4) + "-"
                            + RecptDt.substring(0, 2);
                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    String query = "UPDATE OTSConsumerDataUpload SET MR_NO='" + recprno
                            + "',RECPT_DATE=strftime('%Y-%m-%d', '" + RecptDt + "')," +
                            "RECPT_FLG=1, SEND_FLG=1, " +
                            "coll_flg=1, RECPT_TIME='" + RecptTime + "'";

                    query = query + " WHERE CUST_ID='" + custID + "' AND " +
                            "TRANS_ID='" + Txn_Svr + "'";
                    DatabaseAccess.database.execSQL(query);
                    databaseAccess.close();
                    if (new DatabaseAccess().getOfflineCountOTS(getApplicationContext()) > 0) {
                        uploadOTSData();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getEzetapDataCount() {
        databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String query = "select count(*) from EzytapTransactionDetails where SEND_FLG='0'";
        Cursor cursor = DatabaseAccess.database.rawQuery(query, null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        Log.d("EzetapDataCount",String.valueOf(count));
        cursor.close();
        return count;
    }

    private void uploadEzetapData() {
        Cursor rs = null;
        try {
            databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            databaseAccess.open();
            String query = "Select " +
                    "CONS_ACC, CONS_NAME, MOBILE_NO, DIVISION_CODE, LATITUDE,LONGITUDE, " +
                    "TRANS_ID, PAYMENT_ID, USER_ID, FRANCHISE_ID, CUR_TOTAL, EZE_TXN_AMOUNT, " +
                    "EZE_TXN_DATE, EZE_TXN_STATUS, EZE_TXN_ID, PAYMENT_MODE, RECPTDATE, ERROR, " +
                    "DEVICE_ID, SEND_FLG, REMARKS, FIELD1, FIELD2, FIELD3, FIELD4, FIELD5 " +
                    "FROM EzytapTransactionDetails WHERE SEND_FLG = '0'";

            rs = DatabaseAccess.database.rawQuery(query, null);
            while (rs.moveToNext()) {
                EzetapDbModel data = new EzetapDbModel();
                data.setCons_acc(rs.getString(0));
                data.setCons_name(rs.getString(1));
                data.setCons_mob(rs.getString(2));
                data.setDivision_code(rs.getString(3));
                data.setCons_lat(rs.getString(4));
                data.setCons_longi(rs.getString(5));
                data.setCons_transId(rs.getString(6));
                data.setCons_paymntId(rs.getString(7));
                data.setCons_userId(rs.getString(8));
                data.setCons_franchiseId(rs.getString(9));
                data.setCons_cur_totl(rs.getString(10));
                data.setEze_txn_amnt(rs.getString(11));
                data.setEze_txn_date(rs.getString(12));
                data.setEze_txn_status(rs.getString(13));
                data.setEze_txn_Id(rs.getString(14));
                data.setEze_paymnt_mode(rs.getString(15));
                data.setEze_rcpt_date(rs.getString(16));
                data.setEze_error(rs.getString(17));
                data.setEze_device_Id(rs.getString(18));
                data.setEze_send_flg(rs.getString(19));
                data.setCons_remark(rs.getString(20));
                data.setEze_field1(rs.getString(21));
                data.setEze_field2(rs.getString(22));
                data.setEze_field3(rs.getString(23));
                data.setEze_field4(rs.getString(24));
                data.setEze_field5(rs.getString(25));

                ConnectivityManager cm = (ConnectivityManager) mContext.
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    sendEzetapTxnDataToServer(data);
                }
            }
            if (getEzetapDataCount() > 0) {
                uploadEzetapData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert rs != null;
            rs.close();
            databaseAccess.close();
        }
    }

    private void sendEzetapTxnDataToServer(EzetapDbModel data) {

        Map<String, String> postParam = new HashMap<>();
        postParam.put(Constants.division, data.getDivision_code());
        postParam.put(Constants.cons_no, data.getCons_acc());
        postParam.put(Constants.cons_name, data.getCons_name());
        postParam.put(Constants.cons_mobile, data.getCons_mob());
        postParam.put(Constants.cons_latLong, data.getCons_lat()
                + "," + data.getCons_longi());
        postParam.put(Constants.localTid, data.getCons_transId());
        postParam.put(Constants.paymentID, data.getCons_paymntId());
        postParam.put(Constants.agentID, data.getCons_userId());
        postParam.put(Constants.franchiseID, data.getCons_franchiseId());
        postParam.put(Constants.localamount, data.getCons_cur_totl());
        postParam.put(Constants.txnAmount, data.getEze_txn_amnt());
        postParam.put(Constants.txnDate, data.getEze_txn_date());
        postParam.put(Constants.txnStatus, data.getEze_txn_status());
        postParam.put(Constants.txnID, data.getEze_txn_Id());
        postParam.put(Constants.paymentMode, data.getEze_paymnt_mode());
        postParam.put(Constants.recDate, data.getEze_rcpt_date());
        postParam.put(Constants.error, data.getEze_error());
        postParam.put(Constants.deviceId, data.getEze_device_Id());
        JSONObject obj = new JSONObject(postParam);
        Log.d("Ezetap Post",obj.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                ServerLinks.ezytapUrl, new JSONObject(postParam),
                response -> {
                    Log.d("TAG", response.toString());

                    try {
                        if (response.getInt("resCode") == 200) {
                            new DatabaseAccess().updateEzetapTable
                                    (mContext, data.getCons_transId(),
                                            data.getCons_acc());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                },
                error -> Log.d("TAG", error.toString())) {

        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjReq);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
    }

}

