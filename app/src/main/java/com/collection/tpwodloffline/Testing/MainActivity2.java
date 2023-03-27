package com.collection.tpwodloffline.Testing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private DatabaseAccess databaseAccess=null;
    HashMap<String,String> map = new HashMap<>();

    ArrayList<String> done = new ArrayList<>();
    ArrayList<String> dtwo = new ArrayList<>();
    String LcustID,LConcc,Lpaymode,Lcollmonth,LBalRemain,LamountPay,LtransId,LBankName,Lddno,Ldddate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        done.add("Sradhendu");
        done.add("Atul");
        done.add("Viswanath");
        done.add("Phani");

        dtwo.add("Android");
        dtwo.add("Web");
        dtwo.add("Infra");
        dtwo.add("Scada");
        getOfflineDatatoLocal();
    }
    private int getOfflineCount(){
        /*databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0";
        Cursor cursor = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        int count = 0;
        while (cursor.moveToNext()) {
            count= cursor.getInt(0);
        }
        cursor.close();*/
        return 4;
    }
    private void getOfflineDatatoLocal() {
        int cnt = getOfflineCount();

        for(int i = 0; i < cnt; i++){
            //resetStrings();
          /*  databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
            databaseAccess.open();
            String strSelectSQL_01 = "Select" +
                    " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision,A.section,A.CON_NAME,A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                    " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                    " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                    " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                    " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                    " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                    " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG,A.DEL_FLG,A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name,A.POS_TRANS_ID,A.PHONE_NO,BAL_FETCH, A.TOT_PAID,TRANS_ID,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,RTGS_DATE" +
                    " FROM " +
                    " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and SEND_FLG = '0'";

            Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
            while (rs.moveToNext()) {
                LConcc = rs.getString(0);
                 LcustID = rs.getString(1);
                 Lpaymode = rs.getString(23);
                //chqno = rs.getString(24);
               // chqdate = rs.getString(25);
                Lcollmonth = CommonMethods.getMonthCurrent();
               Lddno = rs.getString(26);
                Ldddate = rs.getString(27);
                LBankName = rs.getString(37);
                //PosTransID = rs.getString(38);
                //phoneNo = rs.getString(39);
                 LBalRemain = rs.getString(40);
               LamountPay = String.valueOf(rs.getInt(41));
                LtransId = "" + rs.getString(42);
                //neftNo = rs.getString(43);
                //neftDate = rs.getString(44);
               // rtgsNo = rs.getString(45);
               // rtgsDate = rs.getString(46);

            }*/
            //RecptGenURL = RecptGenURL+"un="+usname+"&pw="+dbpwdnm+"&CompanyID=3&ConsumerID="+custID+"&deviceId="+device_id+"&RefID=0&Amount="+amountPay+"&DateTime="+CurTime+"&PayMod="+ActPayMode+"&RecNo="+transId+"&BankName="+BankName+"&Ins_No="+ddno+"&ClearDate="+(dddate)+"&PaymentMthh="+(collmonth)+"&BBPS=0&OffLine=1";
            String ss = done.get(i);
            String ds = dtwo.get(i);

            //map.put("user"+LConcc,LamountPay+"|"+Lpaymode+"|"+LtransId+"|"+ LBankName +"|"+Lddno+"|"+Ldddate+"|"+Lcollmonth);
            map.put(ss,ds);
        }

        takebackup(map);

    }

    private void takebackup(HashMap<String, String> map) {
        // new file object
        String _fileName = "/TPSODL/"+"tpsodlof.txt";
        //dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, _fileName);
        //File file = new File(getApplicationContext().getDir(Environment.DIRECTORY_DOCUMENTS)+_fileName);
       // File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath()+"databack.txt");
        File dir = null; //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/"+_fileName );

        //File file = new File(dir, _fileName);
       // dir.getParentFile().mkdirs();
        BufferedWriter bf = null;

        try {
            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(dir));

            // iterate map entries
            for (Map.Entry<String, String> entry :
                    map.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + "|" + entry.getValue());

                // new line
                bf.newLine();
            }

            bf.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            try {

                // always close the writer
                bf.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}