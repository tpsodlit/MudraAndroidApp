package com.collection.tpwodloffline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.collection.tpwodloffline.broadcasts.DialogGPS;
import com.collection.tpwodloffline.model.OTSModel;
import com.collection.tpwodloffline.utils.ServerLinks;

import org.apache.cxf.common.util.Base64Utility;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CommonMethods {

    public static String getCurrentTimes() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        //date.setTimeZone(TimeZon
        // e.getTimeZone("GMT+5:30"));
        String localTime = date.format(currentLocalTime);

        return localTime;
    }
    public static String key = BuildConfig.Authkey;
    public static String encryptText(String plainText) {
        String iv = "12345678";
        byte[] plaintext = plainText.getBytes();
        try {
            byte[] tdesKeyData = key.getBytes("UTF8");
            byte[] myIV = iv.getBytes("UTF8");

            Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, 0,
                    tdesKeyData.length, "DESede");
            IvParameterSpec ivspec = new IvParameterSpec(myIV, 0,
                    myIV.length);
            c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
            byte[] cipherText = c3des.doFinal(plaintext);
            return Base64Utility.encode(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
            return  e.toString();
        }

    }
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes(StandardCharsets.ISO_8859_1);
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    public static String getDownloadNonenUrlNow(String un, String pw, String mobileNumer) {
        //http://collectionapi.tpsouthernodisha.co.in/DynamicReport.aspx?
        String api = ServerLinks.DynamicReportNe + "un=" + un + "&pw=" + pw + "&CompanyID=" + CompanyID + "&strMobileNo=" + mobileNumer;
        return api;
    }
    public static String getHardCodedIMEI() {
        return "358461097642898";
    }

    public static void activityCycle() {
        CountDownTimer timer = new CountDownTimer(2 *60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                //Some code
            }

            public void onFinish() {
                //Logout
            }
        };
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static String getCurrentMonth() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("MM");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static String getDeviceid(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return deviceId;
    }

    public static Boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static Boolean isDataEnabled(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
              Class cmClass = Class.forName(cm.getClass().getName());
              Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
              method.setAccessible(true); // Make the method callable

            // get the setting for "mobile data"
             mobileDataEnabled = (Boolean) method.invoke(cm);

            //Following code does not work in weak/very weak network areas where mobile data is on
            //but network is very limited.
            /*
            mobileDataEnabled = cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected();*/

          /*  if (mobileDataEnabled == true) {
                if (DialogGPS.fa != null) {
                    DialogGPS.fa.finish();
                }
            } else {
                Toast.makeText(context, "Mobile data is disabled", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(context.getApplicationContext(), DialogGPS.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }*/

        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    public static void checkConnection(Context context) {
        if (isDataEnabled(context)) {
            if (DialogGPS.fa != null) {
                DialogGPS.fa.finish();
            }
        } else {
            //showConnectivityPopup(context);
            Toast.makeText(context, "Mobile data is disabled", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context.getApplicationContext(),DialogGPS.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

    public static int checkConnections(Context context) {
        int in = 0;
        if (isConnected(context)) {
            //DialogGPS.fa.finish();
            if (DialogGPS.fa != null) {
                DialogGPS.fa.finish();
            }
            in = 1;
        } else {

            in = 0;

            //showConnectivityPopup(context);
            //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            /*Intent intent1 = new Intent(context.getApplicationContext(),DialogGPS.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        }
        return in;
    }

    public static String getcurrentTime() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static void internetListener(Context context) {
        if (isConnected(context)) {
            //DialogGPS.fa.finish();
            if (DialogGPS.fa != null) {
                DialogGPS.fa.finish();
            }
        } else {
            //showConnectivityPopup(context);
            //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();

           /* Intent intent1 = new Intent(context.getApplicationContext(),DialogGPS.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        }
    }

    public static void saveBooleanPreference(Context context, String key, Boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultVal) {
        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean value = sharedPref.getBoolean(key, defaultVal);
        return value;
    }

    public static void saveStringPreference(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPreference(Context context, String key, String defaultVal) {
        SharedPreferences sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultVal);
    }

    public static String getTodaysDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static Date getTodaysPlainDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String todaysDate = format.format(c);
        try {
            Date date = format.parse(todaysDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TPCODL URL
    public static String getDownloadUrl(String mobileNumer) {
        return "https://portal.tpcentralodisha.com:8070/CESU_API_Report/CESU_DynamicReport.jsp?un=TPCODL_COL_OFF&pw=OFF_2020&CompanyID=3&ReportID=1093&strMobileNo=" + mobileNumer;

    }

    public static String getCompanyID() {

        return "2";
    }

    //TPSODL URL

    public static String getDownloadUrlNow(String un, String pw, String mobileNumer) {
        //http://collectionapi.tpsouthernodisha.co.in/DynamicReport.aspx?
        return ServerLinks.download_data+ "un=" + un + "&pw=" + pw + "&CompanyID=" + getCompanyID() + "&strMobileNo=" + mobileNumer;
    }

    public static String getOTSDownloadUrl(String un, String pw, String mobileNumer,String entrynum) {
        //http://collectionapi.tpsouthernodisha.co.in/DynamicReport.aspx?
        return ServerLinks.download_OTSdata+ "un=" + un + "&pw=" + pw + "&CompanyID="
                + getCompanyID() + "&strMobileNo=" + mobileNumer + "&scno="+entrynum;
    }

    public static String mobNum = "9999999999";
    public static String CompanyID = "2";

    public static Date convertStringToDate(String dateStrt) {
        String dtStart = dateStrt;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(dtStart);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date convertStringToDate2(String dateStrt) {
        String dtStart = dateStrt;
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        try {
            Date date = format.parse(dtStart);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedDate(String dateStrt) {
        String dtStart = dateStrt;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            Date date = format.parse(dtStart);
            String formattedDate = format1.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStrt;
    }

    public static String getFormattedDateDDMMYYYY(String dateStrt) {
        String dtStart = dateStrt;
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = format.parse(dtStart);
            String formattedDate = format1.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStrt;
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String getMilliSeconds() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        String todaysDate = df.format(c);
        return todaysDate;
    }


    public static String getMonthCurrent() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMyyyy");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static void showDialog(final Activity activity, Context context,
                                  String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public static String replaceString(String string) {
        return string.replaceAll("[;:?\"<>|&'#%~/`]", "_");
    }

    public static String getFormattedDate2(String inputDate) {
        DateFormat originalFormat = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        return formattedDate.toLowerCase();
    }

    public static String getFormattedDate1(String inputDate) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        return formattedDate.toLowerCase();
    }

    public static String getFormattedDateMR(String inputDate) {

        System.out.println("sdfgh==" + inputDate);
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        return formattedDate;
    }

    public static boolean validateNumber(String mobNumber) {
        Pattern p = Pattern.compile("^[6-9][0-9]{9}$");
        List<String> invalidNums = new ArrayList<>();
        invalidNums.add("6666666666");
        invalidNums.add("7777777777");
        invalidNums.add("8888888888");
        invalidNums.add("9999999999");
        invalidNums.add("9988888888");

        boolean isValid = false;
        try {
            Matcher m = p.matcher(mobNumber);
            if(m.find() && m.group().equals(mobNumber)) {
                if(!invalidNums.contains(mobNumber)) {
                    isValid = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static boolean validateEzetap(Context context) {
        boolean isValid = false;
        if (CommonMethods.getEzetapDeviceName
                (context).equals(ServerLinks.ezeTapName) ||
                CommonMethods.getEzetapModelNo
                        (context).equals(ServerLinks.ezeTapModelSBI) ||
                CommonMethods.getEzetapModelNo
                        (context).equals(ServerLinks.ezeTapModelHDFC)) {
            isValid = true;
        }
        return isValid;
    }

    public static String getEzetapDeviceName(Context context) {
        return Build.MANUFACTURER;
    }

    public static String getEzetapModelNo(Context context) {
        return Build.MODEL;
    }

    public static String getImageUUID() {
        return UUID.randomUUID().toString();
    }

    //OTS Implementation
    public static void downloadOTS(Context context, JSONObject otsjsonObject){
        try {
            String scno = otsjsonObject.getString("scno");
            String otsKey = otsjsonObject.getString("otsKey");
            String consumerId = otsjsonObject.getString("consumerId");
            String consumerName = otsjsonObject.getString("consumerName");
            String address = otsjsonObject.getString("saddress")
                    .replace("'", "''");
            String installmentDate = otsjsonObject.getString("InstallmentDate")
                    .replace("T", " ");
            String installmentDueDate = otsjsonObject.getString("InstallmentDueDate")
                    .replace("T", " ");
            String otsReferenceNo = otsjsonObject.getString("OTSReferenceNo");
            String installmentNo = otsjsonObject.getString("InstallmentNo");
            int totalInstallment = otsjsonObject.getInt("TotalInstallment");
            double InstallmentAmount = otsjsonObject.getDouble("InstallmentAmount");
            OTSModel dataModel = new OTSModel();
            dataModel.setOtsKey(otsKey);
            dataModel.setConsumerId(consumerId);
            dataModel.setScNo(scno);
            dataModel.setConsumerName(consumerName);
            dataModel.setAddress(address);
            dataModel.setInstallmentDate(installmentDate);
            dataModel.setInstallmentDueDate(installmentDueDate);
            dataModel.setOTSReferenceNo(otsReferenceNo);
            dataModel.setInstallmentNo(installmentNo);
            dataModel.setTotalInstallment(totalInstallment);
            dataModel.setInstallmentAmount(InstallmentAmount);

            long inserted = new DatabaseAccess().insertOTSData(context, dataModel);
            Log.d("OTS", "OTS data inserted ::: " + inserted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void showDialogSingleBtn
            (Activity activity,
             Context context,
             String title,
             String msg,
             String btnText) {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(btnText,
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todaysDate = format.format(c);
        return todaysDate;
    }

    public static String getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        return date.format(currentLocalTime);
    }

    public static String getActMode(String payMode) {
        String actPayMode;
        switch (payMode) {
            case "2": //dd
                actPayMode = "3";
                break;
            case "3": //chq
                actPayMode = "2";
                break;
            case "7": //Pos
                actPayMode = "7";
                break;
            case "8": //NEFT
                actPayMode = "8";
                break;
            case "9": //RTGS
                actPayMode = "9";
                break;
            //cash
            default:
                actPayMode = "1";
                break;
        }
        return actPayMode;
    }
    public static String convertDateFormatPrint(String strTokenValue,
                                                String strDataFormat) {
        String strTokenValueRevDt = "";
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth = strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");

        try {
            strTokenValueRevDt = strTokenValue.substring(idxSDate, idxSDate + 2) + "-" +
                    strTokenValue.substring(idxSMonth, idxSMonth + 2) + "-" +
                    strTokenValue.substring(idxSYear + 2, idxSYear + 4);
        } catch (Exception e) {
            strTokenValueRevDt = "01-01-99";
            e.printStackTrace();
        }
        return strTokenValueRevDt;
    }

    public static String getBillType(String pay_cnt) {
        String bill_type;
        switch (pay_cnt) {
            case "0":
                bill_type = "B";
                break;
            case "1":
                bill_type = "A";
                break;
            case "2":
                bill_type = "C";
                break;
            case "3":
                bill_type = "E";
                break;
            default:
                bill_type = "F";
                break;
        }
        return bill_type;
    }


}
