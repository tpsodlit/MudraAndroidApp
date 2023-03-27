package com.collection.tpwodloffline.activity;

import static com.collection.tpwodloffline.CommonMethods.getCurrentTime;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.model.OTSModel;
import com.collection.tpwodloffline.utils.Constants;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

public class OTSInfo extends AppCompatActivity {

    private TextView tv_ots_ConsNo, ots_tv_custID,
            ots_tv_name, ots_tv_address, tv_otsRefNo,
            ots_tv_tot_installments, ots_tv_installmentNo,
            ots_tv_InstallmentDt, ots_tv_InstallmentDueDt;
    private EditText ots_edtv_installment_amount;
    String SelChoice_string = "", EntryNum_string = "",
            BalFetch = "", username, Trans_IDfetch = "",
            lat = "0.0", lang = "0.0", installmentDate,
            installmentDueDate,otsKey;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ots_info);
        initViews();
        getData();
        setLocalData();
    }

    private void initViews() {

        Toolbar toolbarback = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv_ots_ConsNo = findViewById(R.id.tv_ots_ConsNo);
        ots_tv_custID = findViewById(R.id.ots_tv_custID);
        ots_tv_name = findViewById(R.id.ots_tv_name);
        ots_tv_address = findViewById(R.id.ots_tv_address);
        tv_otsRefNo = findViewById(R.id.tv_otsRefNo);
        ots_tv_tot_installments = findViewById(R.id.ots_tv_tot_installments);
        ots_tv_installmentNo = findViewById(R.id.ots_tv_installmentNo);
        ots_tv_InstallmentDt = findViewById(R.id.ots_tv_InstallmentDt);
        ots_tv_InstallmentDueDt = findViewById(R.id.ots_tv_InstallmentDueDt);
        ots_edtv_installment_amount = findViewById(R.id.ots_edtv_installment_amount);

        toolbarback.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AcCollection.class));
            finish();
        });

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            condCheck();
        });
    }

    private void condCheck() {
        try {
            Trans_IDfetch = username + CommonMethods.getMilliSeconds();
            lat = sharedPreferenceClass.getValue_string("Latitude");
            lang = sharedPreferenceClass.getValue_string("Longitude");

            if (lat.equals("")) {
                lat = "0.0";
            }
            if (lang.equals("")) {
                lang = "0.0";
            }
            String pybleamt = ots_edtv_installment_amount.getText().toString().trim();
            if (Double.parseDouble(pybleamt) <= 0) {

                CommonMethods.showDialogSingleBtn
                        (this, this, Constants.titleWarning,
                                Constants.amountLessThan, Constants.btnClose);

            } else if (Double.parseDouble(BalFetch) <
                    Double.parseDouble(pybleamt)) {

                CommonMethods.showDialogSingleBtn
                        (this, this, Constants.titleBal,
                                Constants.msgBal, Constants.btnClose);

            /*} else if (Double.parseDouble(pybleamt) >=
                    Double.parseDouble("200000")) {

                CommonMethods.showDialogSingleBtn
                        (this, this, Constants.titleWarning,
                                Constants.msgWarning, Constants.btnClose);*/
//            }
//           Commented Because Multiple Installments can be collected.
//            else if (new DatabaseAccess().checkBillCountOTS
//                    (this, EntryNum_string) > 0) {
//
//                CommonMethods.showDialogSingleBtn
//                        (this, this, Constants.title,
//                                Constants.msg_one, Constants.btnOk);
            } else {
                //pay
                showConfirmationDialog(Constants.msgWithPayable(pybleamt));
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommonMethods.showDialog(this, this,
                    Constants.titleError, Constants.msgError);
        }
    }

    private void showConfirmationDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new
                AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Constants.otsConfTitleDialog);
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(Constants.btnConfirm,
                        (dialog, id) -> {
                            if (new DatabaseAccess().
                                    checkTxnIdOTS(this, Trans_IDfetch) > 0) {
                                Trans_IDfetch = username + CommonMethods.getMilliSeconds();
                            }
                            insertData();
                        })
                .setNegativeButton(Constants.btnCancel, (dialog, id) ->
                        dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void insertData() {

        String time = getCurrentTime();

        String pybleamt = ots_edtv_installment_amount.getText().toString().trim();
        String consNo = tv_ots_ConsNo.getText().toString().trim();
        String consumerId = ots_tv_custID.getText().toString().trim();
        String name = ots_tv_name.getText().toString().trim().
                replace("'", "''");
        OTSModel dataModel = new OTSModel();

        dataModel.setOtsKey(otsKey);
        dataModel.setOTSReferenceNo(tv_otsRefNo.getText().toString().trim());
        dataModel.setInstallmentNo(ots_tv_installmentNo.getText().toString().trim());
        dataModel.setTotalInstallment(Integer.parseInt(
                ots_tv_tot_installments.getText().toString().trim()));
//        dataModel.setInstallmentNo(Integer.parseInt(
//                ots_tv_installmentNo.getText().toString().trim()));
        dataModel.setInstallmentAmount(Double.parseDouble(
                ots_edtv_installment_amount.getText().toString().trim()));
        dataModel.setInstallmentDate(installmentDate);
        dataModel.setInstallmentDueDate(installmentDueDate);

        dataModel.setScNo(consNo);
        dataModel.setConsumerId(consumerId);
        dataModel.setConsumerName(name);
        dataModel.setAddress(ots_tv_address.getText().toString().trim().
                replace("'", "''"));
        dataModel.setTransId(Trans_IDfetch);
        dataModel.setReceiptFlag("0");
        dataModel.setTransDate(CommonMethods.
                getTodayDate());
        dataModel.setReceiptDate(CommonMethods.
                getTodayDate());
        dataModel.setReceiptTime(time);

        dataModel.setDbTypeServer("O");
        dataModel.setOperationType("0");
        dataModel.setSpinnerNonEnergy("OTS");
        dataModel.setLatitude(lat);
        dataModel.setLongitude(lang);
        dataModel.setTotalPaid(pybleamt);

        long rows = new DatabaseAccess().insertDataOTSConsumerUploadTable
                (this, dataModel);
        Log.d("OTS Updated Data",
                "OTSConsumerDataUpload data inserted : " + rows);

        Intent intent = new Intent(getApplicationContext(), PaySummary.class);
        Bundle bundle = new Bundle();
        bundle.putString("Pableamt", pybleamt);
        bundle.putString("consacc", consNo);
        bundle.putString("custID", consumerId);
        bundle.putString("TransID", Trans_IDfetch);
        bundle.putString("SelChoice", SelChoice_string);
        bundle.putString("BalFetch", BalFetch);
        bundle.putString("namefetch", name);
        bundle.putString("MobileNofetch", "");
        bundle.putString("from", "account");
        bundle.putBoolean("manual", false);
        bundle.putString("PayFlag", "OTS");

        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void setLocalData() {
        DatabaseAccess databaseAccess = null;
        Cursor cursor = null;
        try {
            databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();

            String query = "select *, strftime('%d-%m-%Y',InstallmentDate) InstDate, " +
                    "strftime('%d-%m-%Y',InstallmentDueDate) InstDueDate from " +
                    "OTSConsumerData where CONS_ACC='" + EntryNum_string + "'";

            cursor = DatabaseAccess.database.rawQuery(query, null);

            while (cursor.moveToNext()) {
                otsKey = cursor.getString(cursor.
                        getColumnIndexOrThrow("OTSKey"));
                String otsReferenceNo = cursor.getString(cursor.
                        getColumnIndexOrThrow("OTSReferenceNo"));
                int totalInstallment = cursor.getInt(cursor.
                        getColumnIndexOrThrow("TotalInstallment"));
                String installmentNo = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstallmentNo"));
                double installmentAmount = cursor.getDouble(cursor.
                        getColumnIndexOrThrow("InstallmentAmount"));
                installmentDate = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstallmentDate"));
                installmentDueDate = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstallmentDueDate"));

                String instDate = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstDate"));
                String instDueDate = cursor.getString(cursor.
                        getColumnIndexOrThrow("InstDueDate"));

                String consno = cursor.getString(cursor.
                        getColumnIndexOrThrow("CONS_ACC"));
                String custId = cursor.getString(cursor.
                        getColumnIndexOrThrow("CUST_ID"));
                String name = cursor.getString(cursor.
                        getColumnIndexOrThrow("CON_NAME"));
                String address = cursor.getString(cursor.
                        getColumnIndexOrThrow("CON_ADD1"));

                tv_ots_ConsNo.setText(consno);
                ots_tv_custID.setText(custId);
                ots_tv_name.setText(name);
                ots_tv_address.setText(address);
                tv_otsRefNo.setText(otsReferenceNo);
                ots_tv_tot_installments.setText("" + totalInstallment);
                ots_tv_installmentNo.setText("" + installmentNo);
                ots_tv_InstallmentDt.setText(instDate);
                ots_tv_InstallmentDueDt.setText(instDueDate);
                int toPayOTS = (int) Math.floor(installmentAmount);
                ots_edtv_installment_amount.setText("" + toPayOTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
            databaseAccess.close();
        }
    }

    private void getData() {
        sharedPreferenceClass = new SharedPreferenceClass(this);
        username = sharedPreferenceClass.getValue_string("un");
        BalFetch = new DatabaseAccess().fetchMainBal(this, username);

        Bundle extrasVal = getIntent().getExtras();
        SelChoice_string = extrasVal.getString("SelChoice");
        EntryNum_string = extrasVal.getString("EntryNum");
    }


}