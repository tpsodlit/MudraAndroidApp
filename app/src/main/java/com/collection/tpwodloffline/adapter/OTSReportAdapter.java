package com.collection.tpwodloffline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.model.OTSModel;

import java.util.ArrayList;

public class OTSReportAdapter extends RecyclerView.Adapter<OTSReportAdapter.MyViewHolder> {

    private ArrayList<OTSModel> detailsReportsList;
    private Context mContext;
    private String search_string = "";

    public OTSReportAdapter(Context context,
                            ArrayList<OTSModel> detailsReports) {
        this.detailsReportsList = detailsReports;
        this.mContext = context;
    }

    @NonNull
    @Override
    public OTSReportAdapter.MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.adapter_ots_report, parent, false);
        return new OTSReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull OTSReportAdapter.MyViewHolder holder,
            int position) {

        OTSModel detailsReport = detailsReportsList.get(position);

        holder.tv_consno_val.setText(detailsReport.getScNo());
        holder.tv_instno_val.setText(detailsReport.getInstallmentNo());
        holder.tv_tot_paid_val.setText(detailsReport.getTotalPaid());
        holder.tv_installmentDate_val.setText(detailsReport.getInstallmentDate());
        holder.tv_mr_no_val.setText(detailsReport.getMrNo());
        holder.tv_rcpt_date_val.setText(detailsReport.getReceiptDate());
        holder.tv_paymode_val.setText(detailsReport.getPayMode());
    }

    @Override
    public int getItemCount() {
        return detailsReportsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_consno_val, tv_instno_val,
                tv_tot_paid_val, tv_installmentDate_val,
                tv_mr_no_val, tv_rcpt_date_val, tv_paymode_val;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_consno_val = itemView.findViewById(R.id.tv_consno_val);
            tv_instno_val = itemView.findViewById(R.id.tv_instno_val);
            tv_tot_paid_val = itemView.findViewById(R.id.tv_tot_paid_val);
            tv_installmentDate_val = itemView.findViewById(R.id.tv_installmentDate_val);
            tv_mr_no_val = itemView.findViewById(R.id.tv_mr_no_val);
            tv_rcpt_date_val = itemView.findViewById(R.id.tv_rcpt_date_val);
            tv_paymode_val = itemView.findViewById(R.id.tv_paymode_val);
        }
    }

    public void filterList(ArrayList<OTSModel> filteredList,
                           String search_strings) {
        detailsReportsList = filteredList;
        search_string = search_strings;
        notifyDataSetChanged();
    }
}