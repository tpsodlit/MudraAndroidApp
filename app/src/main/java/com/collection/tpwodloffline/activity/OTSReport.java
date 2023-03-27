package com.collection.tpwodloffline.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.CommonMethods;
import com.collection.tpwodloffline.DatabaseAccess;
import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.adapter.OTSReportAdapter;
import com.collection.tpwodloffline.model.OTSModel;

import java.util.ArrayList;

public class OTSReport extends AppCompatActivity {

    private RecyclerView rv_details_recycler;
    private Context context = this;
    private TextView tv_no_data_found;
    private ImageView iv_back, iv_search, iv_close;
    private EditText et_search;

    private ArrayList<OTSModel> detailsReportsList = new ArrayList<>();
    private ArrayList<OTSModel> originalData = new ArrayList<>();

    private OTSReportAdapter otsReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otsreport);

        intView();
        clickListener();
        initAdapter();
        fetchData();
    }

    private void intView() {
        rv_details_recycler = findViewById(R.id.rv_details_recycler);
        tv_no_data_found = findViewById(R.id.tv_no_data_found);
        iv_back = findViewById(R.id.iv_back);
        iv_search = findViewById(R.id.iv_search);
        et_search = findViewById(R.id.et_search);
        iv_close = findViewById(R.id.iv_close);
        detailsReportsList.clear();
    }

    private void clickListener() {

        iv_back.setOnClickListener(v -> onBackPressed());

        iv_close.setOnClickListener(v -> {
            et_search.setText("");
            iv_close.setVisibility(View.GONE);
            iv_search.setVisibility(View.VISIBLE);
            otsReportAdapter.filterList(originalData, "");
        });

        et_search.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                iv_close.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.GONE);
                iv_search.performClick();
                handled = true;
            }
            return handled;
        });

        iv_search.setOnClickListener(v -> {
            if (et_search.getText().toString().trim().length() > 0) {
                iv_close.setVisibility(View.VISIBLE);
                iv_search.setVisibility(View.GONE);
                try {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().
                            getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                searchResult(et_search.getText().toString().trim());
            } else {
                Toast.makeText(this,
                        "Please enter binder no.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new
                GridLayoutManager(this, 1);
        rv_details_recycler.setLayoutManager(linearLayoutManager);
        otsReportAdapter = new OTSReportAdapter(context, detailsReportsList);
        rv_details_recycler.setAdapter(otsReportAdapter);
    }

    private void fetchData() {
        DatabaseAccess databaseAccess = null;
        try {
            String query = "select CONS_ACC, InstallmentNo, TOT_PAID, " +
                    "strftime('%d-%m-%Y', InstallmentDate) InstallmentDate," +
                    "strftime('%d-%m-%Y', InstallmentDueDate) InstallmentDueDate, " +
                    "MR_No, strftime('%d-%m-%Y', RECPT_DATE) RECPT_DATE, " +
                    "PAY_MODE from " +
                    "OTSConsumerDataUpload_BKP where RECPT_FLG = 1 " +
                    "Order by TRANS_ID desc";

            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            Cursor rs = DatabaseAccess.database.rawQuery(query, null);
            while (rs.moveToNext()) {

                OTSModel detailsReport = new OTSModel();

                String cons_acc = rs.getString(rs.getColumnIndexOrThrow("CONS_ACC"));
                String installmentNo = rs.getString(rs.getColumnIndexOrThrow(
                        "InstallmentNo"));
                String tot_paid = rs.getString(rs.getColumnIndexOrThrow("TOT_PAID"));
                String installmentDate = rs.getString(rs.getColumnIndexOrThrow(
                        "InstallmentDate"));
                String installmentDueDate = rs.getString(rs.getColumnIndexOrThrow(
                        "InstallmentDueDate"));
                String mr_no = rs.getString(rs.getColumnIndexOrThrow("MR_No"));
                String recpt_date = rs.getString(rs.getColumnIndexOrThrow("RECPT_DATE"));
                String pay_mode = rs.getString(rs.getColumnIndexOrThrow("PAY_MODE"));

                pay_mode = CommonMethods.getActMode(pay_mode);

                detailsReport.setScNo(cons_acc);
                detailsReport.setInstallmentNo(installmentNo);
                detailsReport.setTotalPaid(tot_paid);
                detailsReport.setInstallmentDate(installmentDate);
                detailsReport.setInstallmentDueDate(installmentDueDate);
                detailsReport.setMrNo(mr_no);
                detailsReport.setReceiptDate(recpt_date);
                detailsReport.setPayMode(pay_mode);

                detailsReportsList.add(detailsReport);
            }
            originalData.addAll(detailsReportsList);

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (databaseAccess != null)
                databaseAccess.close();
        }
    }

    void searchResult(String text) {
        ArrayList<OTSModel> temp = new ArrayList<>();
        for (OTSModel d : detailsReportsList) {
            if ((d.getScNo().contains(text))) {
                temp.add(d);
            }
        }
        if (detailsReportsList.size() >= 1) {
            tv_no_data_found.setVisibility(View.GONE);
            rv_details_recycler.setVisibility(View.VISIBLE);
            otsReportAdapter.filterList(temp, et_search.getText().toString().trim());
        } else {
            tv_no_data_found.setVisibility(View.VISIBLE);
            rv_details_recycler.setVisibility(View.GONE);
        }
    }
}