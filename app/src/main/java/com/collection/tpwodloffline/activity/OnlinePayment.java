package com.collection.tpwodloffline.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.collection.tpwodloffline.BuildConfig;
import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.model.EzetapDbModel;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;
import com.eze.api.EzeAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class OnlinePayment extends AppCompatActivity {

    private final int REQUEST_CODE_INITIALIZE = 10001;
    private final int REQUEST_CODE_PAY = 10015;

    Bundle pmtsmry = null;
    String cons_No, name, payamt, transId, mobile_no, user_Id,
            custID, div_code, txnError, lat, longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_payment);
        pmtsmry = getIntent().getExtras();
        getConsData();
        initEzetap();
    }

    private void initEzetap() {
        JSONObject jsonRequest = new JSONObject();
        try {
//            //Live device details
//            jsonRequest.put("demoAppKey", BuildConfig.EzetapAppKey);
//            jsonRequest.put("prodAppKey", BuildConfig.EzetapAppKey);
//            jsonRequest.put("merchantName", BuildConfig.EzetapmerchantName);
//            jsonRequest.put("userName", BuildConfig.EzetapuserName);
//            jsonRequest.put("currencyCode", "INR");
//            jsonRequest.put("appMode", "PROD");
//            jsonRequest.put("captureSignature", "false");
//            jsonRequest.put("prepareDevice", "false");

            //testing device
            jsonRequest.put("demoAppKey", BuildConfig.EzetapAppKey);
            jsonRequest.put("prodAppKey", BuildConfig.EzetapAppKey);
            jsonRequest.put("merchantName", BuildConfig.EzetapmerchantName);
            jsonRequest.put("userName", BuildConfig.EzetapuserName);
            jsonRequest.put("currencyCode", "INR");
            jsonRequest.put("appMode", "DEMO");
            jsonRequest.put("captureSignature", "false");
            jsonRequest.put("prepareDevice", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE, jsonRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_INITIALIZE) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        // Initialization of SDK is successful, proceed with your action
                        doPay();
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_PAY) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.
                                getStringExtra("response"));
                        Log.d("REQUEST_CODE_PAY", "paid res" + response);
                        getEzetapResponse(response);

                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.
                                getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        txnError = errorCode + " - " + response.getString("message");
                        Log.d("REQUEST_CODE_PAY", "paid res" + response);
                        Toast.makeText(this, "Error Code Msg - " + txnError + ""
                                , Toast.LENGTH_SHORT).show();
                        // Show the error to user as a pop-up informing the
                        // details so that he can take action against it.
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Do your exception handling
            }
        }
    }

    private void doPay() {

        JSONObject jsonRequest = null;
        try {
            jsonRequest = new JSONObject();
            JSONObject jsonOptionalParams = new JSONObject();
            JSONObject jsonReferences = new JSONObject();
            JSONObject jsonCustomer = new JSONObject();

            jsonReferences.put("reference1", cons_No);//scno
            jsonReferences.put("reference2", "");
            jsonReferences.put("reference3", name);//consumer name
            jsonReferences.put("reference4", cons_No);//consumer number
            jsonReferences.put("reference5", transId);//internal generated

            jsonCustomer.put("name", name);
            jsonCustomer.put("mobileNo", mobile_no);
            jsonCustomer.put("email", "");

            jsonOptionalParams.put("references", jsonReferences);
            jsonOptionalParams.put("customer", jsonCustomer);

            jsonRequest.put("amount", payamt);
            jsonRequest.put("options", jsonOptionalParams);

            EzeAPI.pay(this, REQUEST_CODE_PAY, jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getEzetapResponse(JSONObject response) {
        EzetapDbModel data = new EzetapDbModel();
        try {
            data.setEze_txn_status(response.getString("status"));
            data.setEze_error(response.getString("error"));

            response = response.getJSONObject("result");
            JSONObject responseRecpt = response.getJSONObject("receipt");
            data.setEze_rcpt_date(responseRecpt.getString("receiptDate"));

            response = response.getJSONObject("txn");
            data.setEze_txn_amnt(response.getString("amountOriginal"));
            data.setEze_txn_date(response.getString("txnDate"));
            data.setEze_txn_Id(response.getString("txnId"));
            data.setEze_paymnt_mode(response.getString("paymentMode"));

            data.setCons_userId(user_Id);
            data.setDivision_code(div_code);
            data.setCons_lat(lat);
            data.setCons_longi(longi);
            data.setCons_mob(mobile_no);
            data.setCons_acc(cons_No);
            data.setCons_name(name);
            data.setCons_cur_totl(payamt);
            data.setCons_transId(transId);
            data.setEze_device_Id(CommonMethods.getDeviceid(this));
            data.setCons_paymntId("");
            data.setCons_franchiseId("");
            data.setEze_send_flg("0");
            data.setCons_remark("");
            saveTxnDbData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getConsData() {
        SharedPreferences sessiondata = getApplicationContext().
                getSharedPreferences("sessionval", 0);

        user_Id = sessiondata.getString("userID", null);
        div_code = sessiondata.getString("div_code", null);

        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(this);
        lat = sharedPreferenceClass.getValue_string("Latitude");
        longi = sharedPreferenceClass.getValue_string("Longitude");

        if (lat.equals("")) {
            lat = "0.0";
        }

        if (longi.equals("")) {
            longi = "0.0";
        }

        mobile_no = pmtsmry.getString("MobileNofetch");
        cons_No = pmtsmry.getString("vstrCons_no");
        //as per disscussion with suneha (In phone call)
        name = pmtsmry.getString("namefetch");
        if (name.length() > 45) {
            name = name.substring(0, 43);
        }
        payamt = pmtsmry.getString("vstrpayamt");
        transId = pmtsmry.getString("TransID");
        custID = pmtsmry.getString("custID");
        Log.d("", "" + payamt + ":::" + custID);
    }

    private void saveTxnDbData(EzetapDbModel data) {
        DatabaseAccess databaseAccess = null;
        try {
            ContentValues values = new ContentValues();
            values.put("CONS_ACC", data.getCons_acc());
            values.put("CONS_NAME", data.getCons_name());
            values.put("MOBILE_NO", data.getCons_mob());
            values.put("DIVISION_CODE", data.getDivision_code());
            values.put("LATITUDE", data.getCons_lat());
            values.put("LONGITUDE", data.getCons_longi());
            values.put("TRANS_ID", data.getCons_transId());
            values.put("PAYMENT_ID", data.getCons_paymntId());
            values.put("USER_ID", data.getCons_userId());
            values.put("FRANCHISE_ID", data.getCons_franchiseId());
            values.put("CUR_TOTAL", data.getCons_cur_totl());
            values.put("EZE_TXN_AMOUNT", data.getEze_txn_amnt());
            values.put("EZE_TXN_DATE", data.getEze_txn_date());
            values.put("EZE_TXN_STATUS", data.getEze_txn_status());
            values.put("EZE_TXN_ID", data.getEze_txn_Id());
            values.put("PAYMENT_MODE", data.getEze_paymnt_mode());
            values.put("RECPTDATE", data.getEze_rcpt_date());
            values.put("ERROR", data.getEze_error());
            values.put("DEVICE_ID", data.getEze_device_Id());
            values.put("SEND_FLG", data.getEze_send_flg());
            values.put("REMARKS", data.getCons_remark());

            databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            long rows = DatabaseAccess.database.insert("EzytapTransactionDetails",
                    null, values);
            Log.d("", "EzytapTransactionDetails inserted::" + rows);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert databaseAccess != null;
            databaseAccess.close();
        }
    }
}