package com.collection.tpwodloffline;

/**
 * Created by CESU-user on 31-05-2018.
 */

import static com.collection.tpwodloffline.CommonMethods.convertDateFormatPrint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.collection.tpwodloffline.model.OTSModel;
import com.collection.tpwodloffline.model.ResponseModel;
import com.collection.tpwodloffline.utils.ServerLinks;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    public static SQLiteDatabase database;
    private static DatabaseAccess instance;

    public DatabaseAccess() {
    }

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    public DatabaseAccess(Context context) {

        this.openHelper = new DatabaseHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
            Log.d("DemoApp", "Database Close Sucessfully");
        }
    }



    public String getAuthenticate(String strUserid,String strPwd) {

        String dbuserID="";
        String dbpwd="";
        String prv_flg="0";
        String Max_Date="";
        String todaydate="";
          try {
        String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now')  " +
                "FROM SA_USER  WHERE date('now')>=date(substr(valid_startdate,7,4)||'-'||substr(valid_startdate,4,2)||'-'||substr(valid_startdate,1,2)) and date('now')<=date(substr(valid_enddate,7,4)||'-'||substr(valid_enddate,4,2)||'-'||substr(valid_enddate,1,2)) and lock_flag=0 " +
                "and userid='" + strUserid + "' and passkey='" + strPwd + "'";
        Cursor cursor = database.rawQuery(strSelectSQL_01, null);

            //  SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg,Coll_Limit,Max_Date,Bal_Remain,date('now')
           //   FROM SA_USER  WHERE date('now')>=date(substr(valid_startdate,7,4)||'-'||substr(valid_startdate,4,2)||'-'||substr(valid_startdate,1,2)) and date('now')<=date(substr(valid_enddate,7,4)||'-'||substr(valid_enddate,4,2)||'-'||substr(valid_enddate,1,2)) and lock_flag=0 and userid='9122' and passkey='123456'
        Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
        while (cursor.moveToNext()) {
            dbuserID = cursor.getString(1);
            dbpwd = cursor.getString(2);
            prv_flg = cursor.getString(7);
            Max_Date = cursor.getString(3);
            todaydate = cursor.getString(11);

            Log.d("DemoApp", "in Loop" + dbuserID);
            Log.d("DemoApp", "in Loop" + dbpwd);
            Log.d("DemoApp", "in Loop" + prv_flg);
        }
        cursor.close();

        try {
            if(!prv_flg.equals("0")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = sdf.parse(Max_Date);
                Date date2 = sdf.parse(todaydate);
                Log.d("DemoApp", "date1" + sdf.format(date1));
                Log.d("DemoApp", "date2" + sdf.format(date2));
                //Date date1 = sdf.parse("2009-12-31");
                //Date date2 = sdf.parse("2010-01-31");
                if (date1 == null || date1.equals("") || date1.equals(" ")) {
                    date1 = date2;
                }
                if (date1.compareTo(date2) > 0) {
                    prv_flg = "11";// alert
                    Log.d("DemoApp", "Date1 is after Date2");
                } else if (date1.compareTo(date2) <= 0) {
                    Log.d("DemoApp", "Date1 is before Date2");
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }
          }catch(Exception e){
              e.printStackTrace();

              //  prv_flg="0";
         }
        Log.d("DemoApp", "Query prv_flg " + prv_flg);
        return prv_flg;
    }

    public int custDataCnt(Context context) {
        DatabaseAccess.getInstance(context).open();
        String strSelectSQL_01 = "select count(*) from CUST_DATA";
        Cursor cursor = database.rawQuery(strSelectSQL_01, null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        if (database != null)
            database.close();
        return count;
    }

    public int refreshUserData(Context context, ResponseModel dataModel) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();
            cv.put("valid_enddate", dataModel.getIssueTo());
            cv.put("BAL_REMAIN", dataModel.getBalance());
            rows = database.update("SA_USER", cv,
                    "USERID=" + dataModel.getUserid(), null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public boolean getOfflineCountAll(Context context) {
        try {
            DatabaseAccess.getInstance(context).open();
            String strSelectSQL_01 = "select count(*) from COLL_SBM_DATA where SEND_FLG=0";
            Cursor cursor = database.rawQuery(strSelectSQL_01, null);
            cursor.moveToNext();
            if(cursor.getInt(0)>0)
                return false;

            cursor.close();

            String strSelectSQL_02 = "select count(*) from COLL_NEN_DATA where SEND_FLG=0";
            Cursor cur = database.rawQuery(strSelectSQL_02, null);
            cur.moveToNext();
            if(cur.getInt(0)>0)
                return false;
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            database.close();
        }

        return true;
    }

    public long insertOTSData(Context context, OTSModel dataModel) {
        long rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();

            cv.put("CONS_ACC", dataModel.getScNo());
            cv.put("CUST_ID", dataModel.getConsumerId());
            cv.put("CON_NAME", dataModel.getConsumerName());
            cv.put("CON_ADD1", dataModel.getAddress());
            cv.put("OTSKey", dataModel.getOtsKey());
            cv.put("InstallmentDate", dataModel.getInstallmentDate());
            cv.put("InstallmentDueDate", dataModel.getInstallmentDueDate());
            cv.put("OTSReferenceNo", dataModel.getOTSReferenceNo());
            cv.put("InstallmentNo", dataModel.getInstallmentNo());
            cv.put("totalInstallment", dataModel.getTotalInstallment());
            cv.put("InstallmentAmount", dataModel.getInstallmentAmount());

            rows = database.insert("OTSConsumerData", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public int checkOTSData(Context context, String cons_no) {
        Cursor cursor = null;
        int count = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(1) FROM OTSConsumerData " +
                    "WHERE CONS_ACC='" + cons_no + "'";
            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return count;
    }

    public long insertDataOTSConsumerUploadTable(Context context, OTSModel dataModel) {
        long rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();

            cv.put("OTSKey", dataModel.getOtsKey());
            cv.put("OTSReferenceNo", dataModel.getOTSReferenceNo());
            cv.put("TotalInstallment", dataModel.getTotalInstallment());
            cv.put("InstallmentNo", dataModel.getInstallmentNo());
            cv.put("InstallmentAmount", dataModel.getInstallmentAmount());
            cv.put("InstallmentDate", dataModel.getInstallmentDate());
            cv.put("InstallmentDueDate", dataModel.getInstallmentDueDate());

            cv.put("CONS_ACC", dataModel.getScNo());
            cv.put("CUST_ID", dataModel.getConsumerId());
            cv.put("CON_NAME", dataModel.getConsumerName());
            cv.put("CON_ADD1", dataModel.getAddress());

            cv.put("TOT_PAID", dataModel.getTotalPaid());
            cv.put("TRANS_ID", dataModel.getTransId());
            cv.put("RECPT_FLG", dataModel.getReceiptFlag());
            cv.put("TRANS_DATE", dataModel.getTransDate());
            cv.put("RECPT_DATE", dataModel.getReceiptDate());
            cv.put("RECPT_TIME", dataModel.getReceiptTime());
            cv.put("DB_TYPE_SERVER", dataModel.getDbTypeServer());
            cv.put("OPERATION_TYPE", dataModel.getOperationType());
            cv.put("SPINNER_NON_ENERGY", dataModel.getSpinnerNonEnergy());
            cv.put("LATTITUDE", dataModel.getLatitude());
            cv.put("LONGITUDE", dataModel.getLongitude());
            cv.put("EMAIL", "");
            cv.put("REASON", "");
            cv.put("REMARKS", "");

            rows = database.insert("OTSConsumerDataUpload", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public void insertIntoOTSConsumerDataUpload_BKP(Context context,
                                                    String custID,
                                                    String trans_ID) {
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "INSERT INTO OTSConsumerDataUpload_BKP (" +
                    "CONS_ACC, CUST_ID,Division," +
                    "OTSKey,OTSReferenceNo,CON_NAME,TotalInstallment,InstallmentNo," +
                    "InstallmentAmount,InstallmentDate,InstallmentDueDate," +
                    "CON_ADD1,TOT_PAID," +
                    "RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,PAY_MODE, CHEQUE_NO," +
                    "CHEQUE_DATE,DD_NO,DD_DATE,Bank_ID,RECPT_FLG,OPERATOR_ID, OPERATOR_NAME," +
                    "SEND_FLG,DEL_FLG,Bill_ID,COLL_FLG,TRANS_ID,PMT_TYP, POS_TRANS_ID,PHONE_NO," +
                    "TRANS_DATE,BAL_FETCH,EMAIL,NEFT_NO,NEFT_DATE,RTGS_NO, RTGS_DATE,MICR_NO," +
                    "DIV_CODE_SERVER,CA_SERVER,DB_TYPE_SERVER, NON_ENERGY_TYPE,MONEY_RECPT_ID," +
                    "MONEY_RECPT_DATE,MONEY_TYPE,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE," +
                    "LONGITUDE,REASON,REMARKS,EMAIL" +
                    ") " +
                    "SELECT " +
                    "CONS_ACC, CUST_ID,Division," +
                    "OTSKey,OTSReferenceNo,CON_NAME,TotalInstallment,InstallmentNo," +
                    "InstallmentAmount,InstallmentDate,InstallmentDueDate," +
                    "CON_ADD1,TOT_PAID," +
                    "RECPT_DATE,RECPT_TIME,MR_No,MACHINE_NO,PAY_MODE, CHEQUE_NO," +
                    "CHEQUE_DATE,DD_NO,DD_DATE,Bank_ID,RECPT_FLG,OPERATOR_ID, OPERATOR_NAME," +
                    "SEND_FLG,DEL_FLG,Bill_ID,COLL_FLG,TRANS_ID,PMT_TYP, POS_TRANS_ID,PHONE_NO," +
                    "TRANS_DATE,BAL_FETCH,EMAIL,NEFT_NO,NEFT_DATE,RTGS_NO, RTGS_DATE,MICR_NO," +
                    "DIV_CODE_SERVER,CA_SERVER,DB_TYPE_SERVER, NON_ENERGY_TYPE,MONEY_RECPT_ID," +
                    "MONEY_RECPT_DATE,MONEY_TYPE,OPERATION_TYPE,SPINNER_NON_ENERGY,LATTITUDE," +
                    "LONGITUDE,REASON,REMARKS,EMAIL " +
                    "FROM OTSConsumerDataUpload WHERE CUST_ID='" + custID + "' AND " +
                    "TRANS_ID='" + trans_ID + "'";
            database.execSQL(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            database.close();
        }
    }

    public int updateCollFlagRecptPrintOTS(
            Context context,
            String custId,
            String transId) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();
            cv.put("SEND_FLG", 0);
            cv.put("COLL_FLG", 1);
            cv.put("RECPT_FLG", 1);
            cv.put("MR_NO", transId);
            rows = database.update("OTSConsumerDataUpload", cv,
                    "CUST_ID ='" + custId + "' AND " +
                            "TRANS_ID='" + transId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public String receiptNoOTS(Context context,
                               String custID,
                               String trans_ID) {
        Cursor cursor = null;
        String receiptNo = "";
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select CONS_ACC, InstallmentNo " +
                    "FROM OTSConsumerDataUpload WHERE " +
                    "CUST_ID = '" + custID + "' AND " +
                    "TRANS_ID='" + trans_ID + "'";

            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                String scNo = cursor.getString(cursor.
                        getColumnIndexOrThrow("CONS_ACC"));
                int installmentNo = cursor.getInt(cursor.
                        getColumnIndexOrThrow("InstallmentNo"));
                receiptNo = "O" + scNo + installmentNo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return receiptNo;
    }

    public int updateMachineNoOTS(
            Context context,
            String custId,
            String transId) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();

            cv.put("MACHINE_NO", 1);

            rows = database.update("OTSConsumerDataUpload", cv,
                    "CUST_ID ='" + custId + "' AND " +
                            "TRANS_ID='" + transId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public int getOfflineCountOTS(Context context) {
        int count = 0;
        Cursor cursor = null;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(*) from OTSConsumerDataUpload where SEND_FLG=0";
            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return count;
    }

    public int getUploadedCountOTS(Context context) {
        int count = 0;
        Cursor cursor = null;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(*) from OTSConsumerDataUpload where SEND_FLG=1";
            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return count;
    }

    public String fetchMainBal(Context context, String userName) {
        String balFetch = "";
        Cursor cursor = null;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "SELECT BAL_REMAIN  " +
                    "FROM SA_USER  WHERE USERID='" + userName + "'";
            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                balFetch = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return balFetch;
    }

    public int checkBillCountOTS(Context context, String entryNum_string) {
        int count = 0;
        Cursor cursor = null;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(*) from OTSConsumerDataUpload where " +
                    "CONS_ACC = '" + entryNum_string + "' and " +
                    "strftime('%d-%m-%Y', 'now') = strftime('%d-%m-%Y', recpt_date) " +
                    "and RECPT_FLG=1";

            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return count;
    }

    public int checkTxnIdOTS(Context context, String trans_ID) {
        int count = 0;
        Cursor cursor = null;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(*) from " +
                    "OTSConsumerDataUpload where TRANS_ID=" + trans_ID + "";
            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return count;
    }

    public String printOTSEnergyData(Context context,
                                     String custId,
                                     String trans_ID,
                                     String rcptType,
                                     String prntFlag) {
        Cursor cursor = null;
        String paymode, recptDate,add1, add2,
                blPrepTm = "", divn = "",
                printData = "", transId,
                division, cons_num,
                pay_against, pay_mode,
                recvd_cash, total_paid;

        SharedPreferenceClass sharedPreferenceClass = new
                SharedPreferenceClass(context);

        String username = sharedPreferenceClass.getValue_string("username");
        String mobile = sharedPreferenceClass.getValue_string("mobile");
        String uid = sharedPreferenceClass.getValue_string("un");
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select" +
                    " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision, A.section,A.CON_NAME, " +//5
                    " A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                    " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                    " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                    " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                    " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                    " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                    " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG, A.DEL_FLG, " +//33
                    " A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name, A.POS_TRANS_ID,A.PHONE_NO," +//39
                    " BAL_FETCH,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,A.RTGS_DATE, " +//44
                    " A.MONEY_RECPT_ID," +//45
                    " A.MONEY_RECPT_DATE,A.DB_TYPE_SERVER,A.SPINNER_NON_ENERGY, " +//48
                    " A.OTSReferenceNo, A.InstallmentNo, A.totalInstallment " + //51
                    " FROM " +
                    " OTSConsumerDataUpload A,mst_bank b WHERE a.bank_id=b.bank_id and " +
                    " CUST_ID = '" + custId + "' AND TRANS_ID='" + trans_ID + "'";

            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {

                try {
                    blPrepTm = cursor.getString(19);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cursor.getInt(23) == 2) {
                    paymode = "DD";
                } else if (cursor.getInt(23) == 3) {
                    paymode = "CHEQUE";
                } else if (cursor.getInt(23) == 7) {
                    paymode = "POS";
                } else if (cursor.getInt(23) == 8) {
                    paymode = "NEFT";
                } else if (cursor.getInt(23) == 9) {
                    paymode = "RTGS";
                } else if (cursor.getInt(23) == 4) {
                    paymode = "MR";
                } else {
                    paymode = "  CASH";
                }

                String receiptNo = new DatabaseAccess().receiptNoOTS(context, custId, trans_ID);
                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = "                                           " + rcptType + "\n" +
                            "                          TPWODL    \n" +
                            "                     MONEY RECEIPT.     \n";
                } else {

                    printData = "                     " + rcptType + "\n" +
                            "             TPWODL    \n" +
                            "          MONEY RECEIPT.     \n";
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }
                if (!blPrepTm.contains(":")) {
                    blPrepTm = blPrepTm.substring(0, 2) + ":" +
                            blPrepTm.substring(2, 4) + ":" +
                            blPrepTm.substring(4, 6);
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    recptDate = "RECPT DT:     ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:               ";
                    cons_num = "CONSUMER NO:                  ";
                    pay_against = "PAYMENT AGAINST:                 ";
                    pay_mode = "PAYMENT MODE:                             ";
                    recvd_cash = "RECEIVED CASH:              ";
                    total_paid = "TOTAL PAID:                 ";
                } else {
                    recptDate = "RECPT DT:   ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:       ";
                    cons_num = "CONSUMER NO:       ";
                    pay_against = "PAYMENT AGAINST:          ";
                    pay_mode = "PAYMENT MODE:             ";
                    recvd_cash = "RECEIVED CASH:        ";
                    total_paid = "TOTAL PAID:           ";
                }

                printData = printData + recptDate + convertDateFormatPrint(
                        cursor.getString(18),
                        "DD-MM-YYYY") + "   " +
                        blPrepTm + "\n";
                printData = printData + "RECEIPT NO: " + receiptNo + "\n";

                printData = printData + transId + cursor.getString(20) + "\n";
                if (cursor.getString(2) == null)
                    divn = "";
                printData = printData + division + divn + "\n";
                printData = printData + cons_num + cursor.getString(0) + "\n";

                printData = printData + "OTS INST. NO.: " + cursor.getString(50) + "\n";

                printData = printData + "NAME: " + cursor.getString(5) + "\n";
                printData = printData + "ADDRS:" + cursor.getString(6).trim() + "\n";
                //printData = printData + "ADDRS:" + add1 + "" + add2 + "\n";

                printData = printData + "\n";

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData
                            + "------------------------------------------------------\n";
                } else {
                    printData = printData
                            + "--------------------------------\n";
                }

                printData = printData + pay_against + cursor.getString(48) + "\n";
                printData = printData + pay_mode + paymode + "\n";

                if (cursor.getString(23).equals("7")) {
                    printData = printData + "RECEIVED AMT: " + cursor.getString(22) + "\n";
                    printData = printData + "POS ID:       " + cursor.getString(39) + "\n";
                    printData = printData + "POS DATE:     " + cursor.getString(27) + "\n";


                } else if (cursor.getString(23).equals("3")) {
                    printData = printData + "RECEIVED CHQ:         " + cursor.getString(22) + "\n";
                    printData = printData + "CHQ NO:              " + cursor.getString(24) + "\n";
                    printData = printData + "CHQ DATE:           " + cursor.getString(25) + "\n";

                } else if (cursor.getString(23).equals("2")) {
                    printData = printData + "RECEIVED DD: " + cursor.getString(22) + "\n";
                    printData = printData + "DD NO:       " + cursor.getString(26) + "\n";
                    printData = printData + "DD DATE:     " + cursor.getString(27) + "\n";

                } else if (cursor.getString(23).equals("8")) {
                    printData = printData + "RECEIVED NEFT: " + cursor.getString(22) + "\n";
                    printData = printData + "NEFT NO:       " + cursor.getString(40) + "\n";
                    printData = printData + "NEFT DATE:     " + cursor.getString(41) + "\n";

                } else if (cursor.getString(23).equals("9")) {
                    printData = printData + "RECEIVED RTGS: " + cursor.getString(22) + "\n";
                    printData = printData + "RTGS NO:       " + cursor.getString(42) + "\n";
                    printData = printData + "RTGS DATE:     " + cursor.getString(43) + "\n";

                } else if (cursor.getString(23).equals("4")) {
                    printData = printData + "RECEIVED MR: " + cursor.getString(22) + "\n";
                    printData = printData + "MR NO:       " + cursor.getString(44) + "\n";
                    printData = printData + "MR DATE:     " + cursor.getString(45) + "\n";

                } else {
                    printData = printData + recvd_cash +
                            cursor.getString(22) + "\n";
                }

                printData = printData + total_paid +
                        cursor.getString(22) + ".00" + "\n";

                printData = printData + "AMOUNT RECEIVED (in word):\n" +
                        NumberToWordConverter.numberToWord(cursor.getInt(22))
                        + " only" + "\n\n";

                printData = printData + "SIGNATURE" + "\n";
                printData = printData + "Thanks" + "\n\n";
                printData = printData + "RECEIVED BY:  " + username + "\n";
                printData = printData + "MOBILE NO. :  " + mobile + "\n";

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }
                printData = printData + CommonMethods.SHA1(
                        cursor.getString(20) + convertDateFormatPrint(
                                cursor.getString(18),
                                "DD-MM-YYYY")) + "\n";
                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }

                printData = printData + "THIS IS AUTO-GENERATED " + "\n";
                printData = printData + "DOCUMENT AND SIGNATURE" + "\n";
                printData = printData + "MAY NOT BE REQUIRED" + "\n";
                printData = printData + "-\n";

                printData = printData + "\n\n ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return printData;
    }

    public String printNonEnergyData(Context context,
                                     String trans_ID,
                                     String billType,
                                     String rcptType,
                                     String prntFlag) {

        Cursor cursor = null;
        String paymode = "  CASH", recptDate,
                pmttype = "NON-ENERGY", sec,
                blPrepTm = "", source, refNo,
                printData = "", transId,
                division, pay_against, pay_mode,
                total_paid;

        SharedPreferenceClass sharedPreferenceClass = new
                SharedPreferenceClass(context);

        String username = sharedPreferenceClass.getValue_string("username");
        String Mobile = sharedPreferenceClass.getValue_string("mobile");
        String uid = sharedPreferenceClass.getValue_string("un");

        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select" +
                    " USER_ID,COMPANY_CODE,SCNO,REF_MODULE, REF_REG_NO,CUST_ID,DIVISION," +
                    "SUBDIVISION," + //7
                    "SECTION,CON_NAME,CON_ADD1,AMOUNT,DEMAND_DATE, MOBILE_NO,EMAIL," +
                    "RECPT_DATE,RECPT_TIME," + //16
                    "MR_No,MACHINE_NO,TOT_PAID,PAY_MODE,RECPT_FLG, OPERATOR_ID,OPERATOR_NAME," +
                    "SEND_FLG," + //24
                    "COLL_FLG,TRANS_ID,PMT_TYP,TRANS_DATE,BAL_FETCH, OPERATION_TYPE,REMARKS," +
                    "LATTITUDE," + //32
                    "LONGITUDE,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,ENTRYDATE" + //39
                    " FROM " +
                    " COLL_NEN_DATA WHERE COLL_FLG = '" + "1" + "' AND " +
                    "RECPT_FLG='" + "1" + "' AND TRANS_ID='" + trans_ID + "'";

            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {

                try {
                    blPrepTm = cursor.getString(16);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String ref_module = cursor.getString(3);
                String rec_num = cursor.getString(4);
                String receiptNo = billType + ref_module + rec_num;

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = "                                           " + rcptType + "\n" +
                            "                          TPWODL    \n" +
                            "                     MONEY RECEIPT.     \n";
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {

                    printData = "                     " + rcptType + "\n" +
                            "             TPWODL    \n" +
                            "          MONEY RECEIPT.     \n";

                    printData = printData +
                            "--------------------------------\n";
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    recptDate = "RECPT DT:     ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:               ";
                    pay_against = "PAYMENT AGAINST:          ";
                    pay_mode = "PAYMENT MODE:                             ";
                    total_paid = "TOTAL PAID:                              ";
                    source = "SOURCE:                                                  ";
                    sec = "SECTION:       ";
                    refNo = "REF. NO:         ";
                } else {
                    recptDate = "RECPT DT:  ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:       ";
                    pay_against = "PAYMENT AGAINST: ";
                    pay_mode = "PAYMENT MODE:             ";
                    total_paid = "TOTAL PAID:              ";
                    source = "SOURCE:          ";
                    sec = "SECTION:    ";
                    refNo = "REF. NO:    ";
                }

                if (!blPrepTm.contains(":")) {
                    blPrepTm = blPrepTm.substring(0, 2) + ":" +
                            blPrepTm.substring(2, 4) + ":" +
                            blPrepTm.substring(4, 6);
                }

                printData = printData + recptDate + (cursor.getString(15)) + "   " +
                        blPrepTm + "\n";
                printData = printData + "RECEIPT NO: " + receiptNo + "\n";
                printData = printData + transId + cursor.getString(26) + "\n";
                printData = printData + division + cursor.getString(6) + "\n";
                printData = printData + sec + cursor.getString(8) + "\n";
                printData = printData + "CONSUMER NO:" + cursor.getString(2) + "\n";
                printData = printData + refNo + cursor.getString(4) + "\n";
                printData = printData + "NAME: " + cursor.getString(9) + "\n";
                printData = printData + "ADDRS:" + cursor.getString(10) + "\n";

                printData = printData + "\n";
                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }

                printData = printData + pay_against + pmttype + "\n";
                printData = printData + source + cursor.getString(3) + "\n";
                printData = printData + pay_mode + paymode + "\n";

                printData = printData + total_paid +
                        cursor.getString(19) + ".00" + "\n";

                printData = printData + "AMOUNT RECEIVED (in word):\n" +
                        NumberToWordConverter.numberToWord(cursor.getInt(19))
                        + " only" + "\n\n";

                printData = printData + "SIGNATURE" + "\n";
                printData = printData + "Thanks" + "\n\n";
                if (cursor.getString(3).equals("FRM")) {
                    printData = printData + "RECEIVED BY:  " + uid + "\n";
                } else {
                    printData = printData + "RECEIVED BY:  " + username + "\n";
                    printData = printData + "MOBILE NO. :  " + Mobile + "\n";
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }

                printData = printData + CommonMethods.SHA1(cursor.getString(4) +
                        (cursor.getString(28))) + "\n";

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }
                printData = printData + "THIS IS AUTO-GENERATED " + "\n";
                printData = printData + "DOCUMENT AND SIGNATURE" + "\n";
                printData = printData + "MAY NOT BE REQUIRED" + "\n";
                printData = printData + "-\n";
                printData = printData + "\n\n ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return printData;
    }

    public String printEnergyData(Context context,
                                  String custId,
                                  String trans_ID,
                                  String rcptType,
                                  String prntFlag) {
        Cursor cursor = null;
        String paymode,add2, recptDate,
                billType, blPrepTm = "",
                printData = "", transId,
                division, cons_num,
                pay_against, pay_mode,
                recvd_cash, total_paid;

        SharedPreferenceClass sharedPreferenceClass = new
                SharedPreferenceClass(context);

        String username = sharedPreferenceClass.getValue_string("username");
        String mobile = sharedPreferenceClass.getValue_string("mobile");
        String uid = sharedPreferenceClass.getValue_string("un");
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select" +
                    " A.CONS_ACC,CUST_ID,A.Division,A.Subdivision, A.section,A.CON_NAME, " +//5
                    " A.CON_ADD1,A.CON_ADD2,A.CAT_CODE," +  //8
                    " A.RCF,A.COLL_MONTH,A.COLL_YEAR,A.Message,A.CHQ_DISHNRD," +  //13
                    " A.Cur_TOTAL,A.BILL_TOTAL,A.Rebate,strftime('%d-%m-%Y',A.Due_Date)," + //17
                    " strftime('%d-%m-%Y',A.RECPT_DATE),A.RECPT_TIME,A.MR_No, A.MACHINE_NO," +//21
                    " A.TOT_PAID,A.PAY_MODE,A.CHEQUE_NO,strftime('%d-%m-%Y',A.CHEQUE_DATE)," +//25
                    " A.DD_NO,strftime('%d-%m-%Y',A.DD_DATE),A.Bank_ID," + //28
                    " A.RECPT_FLG,A.OPERATOR_ID,A.OPERATOR_NAME,A.SEND_FLG, A.DEL_FLG, " +//33
                    " A.Bill_ID,A.COLL_FLG,A.PMT_TYP,b.bank_name, A.POS_TRANS_ID,A.PHONE_NO," +//39
                    " BAL_FETCH,A.NEFT_NO,A.NEFT_DATE,A.RTGS_NO,A.RTGS_DATE, " +//44
                    " A.MONEY_RECPT_ID," +//45
                    " A.MONEY_RECPT_DATE,A.DB_TYPE_SERVER,A.SPINNER_NON_ENERGY" +//48
                    " FROM " +
                    " COLL_SBM_DATA A,mst_bank b WHERE a.bank_id=b.bank_id and " +
                    " CUST_ID = '" + custId + "' AND TRANS_ID='" + trans_ID + "'";

            cursor = DatabaseAccess.database.rawQuery(query, null);
            while (cursor.moveToNext()) {

                try {
                    blPrepTm = cursor.getString(19);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cursor.getInt(23) == 2) {
                    paymode = "DD";
                } else if (cursor.getInt(23) == 3) {
                    paymode = "CHEQUE";
                } else if (cursor.getInt(23) == 7) {
                    paymode = "POS";
                } else if (cursor.getInt(23) == 8) {
                    paymode = "NEFT";
                } else if (cursor.getInt(23) == 9) {
                    paymode = "RTGS";
                } else if (cursor.getInt(23) == 4) {
                    paymode = "MR";
                } else {
                    paymode = "  CASH";
                }

                String pay_cnt = cursor.getString(47);
                String collMode = cursor.getString(48);
                if (collMode.equals("ADV")) {
                    billType = "D";
                } else {
                    billType = CommonMethods.getBillType(pay_cnt);
                }
//                String receiptNo = billType + cursor.getString(1) +
//                        cursor.getString(10);
                String receiptNo = billType + cursor.getString(1) + uid;

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = "                                           " + rcptType + "\n" +
                            "                          TPWODL    \n" +
                            "                     MONEY RECEIPT.     \n";
                } else {

                    printData = "                     " + rcptType + "\n" +
                            "             TPWODL    \n" +
                            "          MONEY RECEIPT.     \n";
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }
                if (cursor.getString(7).equals("NULL")) {
                    add2 = "";
                } else {
                    add2 = "," + cursor.getString(7).trim();
                }

                if (!blPrepTm.contains(":")) {
                    blPrepTm = blPrepTm.substring(0, 2) + ":" +
                            blPrepTm.substring(2, 4) + ":" +
                            blPrepTm.substring(4, 6);
                }

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    recptDate = "RECPT DT:     ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:               ";
                    cons_num = "CONSUMER NO:                  ";
                    pay_against = "PAYMENT AGAINST:          ";
                    pay_mode = "PAYMENT MODE:                             ";
                    recvd_cash = "RECEIVED CASH:                            ";
                    total_paid = "TOTAL PAID:                              ";
                } else {
                    recptDate = "RECPT DT:   ";
                    transId = "TRANSACTION ID: ";
                    division = "DIVN:       ";
                    cons_num = "CONSUMER NO:       ";
                    pay_against = "PAYMENT AGAINST:       ";
                    pay_mode = "PAYMENT MODE:             ";
                    recvd_cash = "RECEIVED CASH:        ";
                    total_paid = "TOTAL PAID:          ";
                }

                printData = printData + recptDate + convertDateFormatPrint(
                        cursor.getString(18),
                        "DD-MM-YYYY") + "   " +
                        blPrepTm + "\n";
                printData = printData + "RECEIPT NO: " + receiptNo + "\n";

                printData = printData + transId + cursor.getString(20) + "\n";

                printData = printData + division + cursor.getString(2) + "\n";
                printData = printData + cons_num + cursor.getString(0) + "\n";
                printData = printData + "NAME: " + cursor.getString(5).trim() + "\n";
                printData = printData + "ADDRS:" + cursor.getString(6) + "" + add2 + "\n";

                printData = printData + "\n";

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData
                            + "------------------------------------------------------\n";
                } else {
                    printData = printData
                            + "--------------------------------\n";
                }

                String pmttype = "ENERGY BILL";
                if (!cursor.getString(36).equals("AcctNo")) {
                    pmttype = cursor.getString(36);
                }

                printData = printData + pay_against + pmttype + "\n";
                printData = printData + pay_mode + paymode + "\n";

                if (cursor.getString(23).equals("7")) {
                    printData = printData + "RECEIVED AMT: " + cursor.getString(22) + "\n";
                    printData = printData + "POS ID:       " + cursor.getString(39) + "\n";
                    printData = printData + "POS DATE:     " + cursor.getString(27) + "\n";


                } else if (cursor.getString(23).equals("3")) {
                    printData = printData + "RECEIVED CHQ:         " + cursor.getString(22) + "\n";
                    printData = printData + "CHQ NO:              " + cursor.getString(24) + "\n";
                    printData = printData + "CHQ DATE:           " + cursor.getString(25) + "\n";

                } else if (cursor.getString(23).equals("2")) {
                    printData = printData + "RECEIVED DD: " + cursor.getString(22) + "\n";
                    printData = printData + "DD NO:       " + cursor.getString(26) + "\n";
                    printData = printData + "DD DATE:     " + cursor.getString(27) + "\n";

                } else if (cursor.getString(23).equals("8")) {
                    printData = printData + "RECEIVED NEFT: " + cursor.getString(22) + "\n";
                    printData = printData + "NEFT NO:       " + cursor.getString(40) + "\n";
                    printData = printData + "NEFT DATE:     " + cursor.getString(41) + "\n";

                } else if (cursor.getString(23).equals("9")) {
                    printData = printData + "RECEIVED RTGS: " + cursor.getString(22) + "\n";
                    printData = printData + "RTGS NO:       " + cursor.getString(42) + "\n";
                    printData = printData + "RTGS DATE:     " + cursor.getString(43) + "\n";

                } else if (cursor.getString(23).equals("4")) {
                    printData = printData + "RECEIVED MR: " + cursor.getString(22) + "\n";
                    printData = printData + "MR NO:       " + cursor.getString(44) + "\n";
                    printData = printData + "MR DATE:     " + cursor.getString(45) + "\n";

                } else {
                    printData = printData + recvd_cash +
                            cursor.getString(22) + "\n";
                }

                printData = printData + total_paid +
                        cursor.getString(22) + ".00" + "\n";

                printData = printData + "AMOUNT RECEIVED (in word):\n" +
                        NumberToWordConverter.numberToWord(cursor.getInt(22))
                        + " only" + "\n\n";

                printData = printData + "SIGNATURE" + "\n";
                printData = printData + "Thanks" + "\n\n";
                printData = printData + "RECEIVED BY:  " + username + "\n";
                printData = printData + "MOBILE NO. :  " + mobile + "\n";

                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }
                printData = printData + CommonMethods.SHA1(
                        cursor.getString(20) + convertDateFormatPrint(
                                cursor.getString(18),
                                "DD-MM-YYYY")) + "\n";
                if (prntFlag.equals(ServerLinks.ezeTapModelSBI)) {
                    printData = printData +
                            "------------------------------------------------------\n";
                } else {
                    printData = printData +
                            "--------------------------------\n";
                }

                printData = printData + "THIS IS AUTO-GENERATED " + "\n";
                printData = printData + "DOCUMENT AND SIGNATURE" + "\n";
                printData = printData + "MAY NOT BE REQUIRED" + "\n";
                printData = printData + "-\n";

                printData = printData + "\n\n ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            database.close();
        }
        return printData;
    }

    public int updateCollFlagInPrint(
            Context context,
            String cons_acc,
            String transId) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();
            cv.put("COLL_FLG", 2);
            rows = database.update("COLL_SBM_DATA", cv,
                    "CONS_ACC ='" + cons_acc + "' AND " +
                            "TRANS_ID='" + transId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public int updateCollFlagRecptPrint(
            Context context,
            String custId,
            String transId) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();
            cv.put("SEND_FLG", 0);
            cv.put("COLL_FLG", 1);
            cv.put("RECPT_FLG", 1);
            cv.put("MR_NO", transId);
            rows = database.update("COLL_SBM_DATA", cv,
                    "CUST_ID ='" + custId + "' AND " +
                            "TRANS_ID='" + transId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public int otsRecordCountReceipt(Context context, String cons_no) {
        Cursor cursor = null;
        int count = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(1) FROM OTSConsumerDataUpload " +
                    "WHERE CONS_ACC='" + cons_no + "' and RECPT_FLG=1";
            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return count;
    }

    public int recordCountReceipt(Context context, String cons_no) {
        Cursor cursor = null;
        int count = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(1) FROM COLL_SBM_DATA A " +
                    "WHERE CONS_ACC='" + cons_no + "' and RECPT_FLG=1";
            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return count;
    }

    public int checkOTSDataSearching(Context context, String cons_no) {
        Cursor cursor = null;
        int count = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "select count(1) FROM OTSConsumerData " +
                    "WHERE CONS_ACC='" + cons_no + "' AND " +
                    "(FIELD1 is NULL OR FIELD1 = '' OR FIELD1 = '0')";
            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return count;
    }

    public int updateEzetapTable(Context context, String transId, String consno) {
        int rows = 0;
        try {
            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();
            cv.put("SEND_FLG", "1");
            rows = database.update("EzytapTransactionDetails", cv,
                    "TRANS_ID='" + transId
                            + "' AND CONS_ACC='" + consno + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public String getInstallmntNo(Context context,
                                  String custID,
                                  String trans_ID) {
        Cursor cursor = null;
        String installmentNo = "";
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select InstallmentNo " +
                    "FROM OTSConsumerDataUpload WHERE " +
                    "CUST_ID = '" + custID + "' AND " +
                    "TRANS_ID='" + trans_ID + "'";

            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                installmentNo = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstallmentNo"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return installmentNo;
    }

    public int updateFIELD1ForPaymentDone(
            Context context,
            String custId,
            String transId) {
        int rows = 0;
        try {
            String cond = getOTSKeyInstallmntNo(context, custId, transId);

            DatabaseAccess.getInstance(context).open();
            ContentValues cv = new ContentValues();

            cv.put("FIELD1", "1");

            rows = database.update("OTSConsumerData", cv,
                    "CUST_ID = '" + custId + "' AND " + cond,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        return rows;
    }

    public String getOTSKeyInstallmntNo(Context context,
                                        String custID,
                                        String trans_ID) {
        Cursor cursor = null;
        String cond = "";
        try {
            DatabaseAccess.getInstance(context).open();
            String query = "Select OTSKey, InstallmentNo " +
                    "FROM OTSConsumerDataUpload WHERE " +
                    "CUST_ID = '" + custID + "' AND " +
                    "TRANS_ID='" + trans_ID + "'";

            cursor = database.rawQuery(query, null);
            while (cursor.moveToNext()) {
                String otsKey = cursor.getString(cursor.
                        getColumnIndexOrThrow("OTSKey"));
                String installmentNo = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstallmentNo"));
                cond = "OTSKey = '" + otsKey + "' AND " +
                        "InstallmentNo='" + installmentNo + "'";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
            database.close();
        }
        return cond;
    }
}