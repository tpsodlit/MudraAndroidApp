package com.collection.tpwodloffline.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.model.DetailsReport;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsReportAdapter extends RecyclerView.Adapter<DetailsReportAdapter.MyViewHolder> {

    private ArrayList<DetailsReport>detailsReportsList;
    private Context mContext;
    private String search_string="";

    public DetailsReportAdapter(Context context,ArrayList<DetailsReport>detailsReports){
        this.detailsReportsList=detailsReports;
        this.mContext=context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_details_report, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

         DetailsReport detailsReport = detailsReportsList.get(position);

            holder.tv_date1.setText(detailsReport.getDate());
            holder.tv_month.setText(detailsReport.getMonth());
            holder.tv_amount.setText(detailsReport.getAmountReceived());
            holder.tv_mr.setText(detailsReport.getMrNumber());
            holder.tv_recpt_dt.setText(detailsReport.getReceiptDate());



        String desc = detailsReportsList.get(position).getConsumerAccount();

        SpannableStringBuilder sb = new SpannableStringBuilder(desc);
        Pattern word = Pattern.compile(search_string.toLowerCase());
        Matcher match = word.matcher(desc.toLowerCase());

        while (match.find()) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(
                    ContextCompat.getColor(mContext, R.color.colorAccent));
            sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.tv_coll_month.setText(sb);


        String desName = detailsReportsList.get(position).getConsumerName();

        SpannableStringBuilder sbName = new SpannableStringBuilder(desName);
        Pattern words = Pattern.compile(search_string.toLowerCase());
        Matcher matchs = words.matcher(desName.toLowerCase());

        while (matchs.find()) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(
                    ContextCompat.getColor(mContext, R.color.colorAccent));
            sbName.setSpan(fcs, matchs.start(), matchs.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.tv_con_name.setText(sbName);


    }

    @Override
    public int getItemCount()
    {
        return detailsReportsList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_date1;
        private TextView tv_month;
        private TextView tv_coll_month;
        private TextView tv_amount;
        private TextView tv_mr;
        private TextView tv_recpt_dt;
        private TextView tv_con_name;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date1=itemView.findViewById(R.id.tv_date1);
            tv_month=itemView.findViewById(R.id.tv_month);
            tv_coll_month=itemView.findViewById(R.id.tv_coll);
            tv_amount=itemView.findViewById(R.id.tv_amount);
            tv_mr=itemView.findViewById(R.id.tv_mr);
            tv_recpt_dt=itemView.findViewById(R.id.tv_recpt_dt);
            tv_con_name=itemView.findViewById(R.id.tv_con_name);

        }
    }

    public void filterList(ArrayList<DetailsReport> filteredList,String search_strings) {
        detailsReportsList = filteredList;
        search_string=search_strings;
        notifyDataSetChanged();
    }
}
