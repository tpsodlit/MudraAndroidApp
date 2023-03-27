package com.collection.tpwodloffline.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.utils.ChatHeadService;
import com.collection.tpwodloffline.utils.Distance;
import com.collection.tpwodloffline.utils.NavReport;
import com.collection.tpwodloffline.utils.Distance;
import com.collection.tpwodloffline.utils.NavReport;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.util.ArrayList;

public class NavAdapter extends RecyclerView.Adapter<NavAdapter.MyViewHolder> {

    private ArrayList<NavReport> detailsReportsList;
    private Context mContext;
    private String search_string = "";
    SharedPreferenceClass sharedPreferenceClass;

    public NavAdapter(Context context, ArrayList<NavReport> detailsReports) {
        this.detailsReportsList = detailsReports;
        this.mContext = context;
        this.sharedPreferenceClass = new SharedPreferenceClass(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nav_report, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{


        String lat = sharedPreferenceClass.getValue_string("Latitude");
        String lang = sharedPreferenceClass.getValue_string("Longitude");
        NavReport navReport = detailsReportsList.get(position);


        holder.tv_con_name.setText(navReport.getCname());
        holder.tv_sc_num.setText(navReport.getScnum());
        holder.tv_amount.setText(navReport.getPayable());
        String clat = navReport.getClat();
        String clang = navReport.getClang();
        String distance = Distance.Distancekm(lat,lang,clat,clang);
        String[] separated = distance.split("[.]");
        String num  = separated[0];
        String denum = separated[1];
        holder.tv_distance.setText(num+"."+denum.substring(0,2)+" Km.");

        holder.drive.setOnClickListener(v->{
            sharedPreferenceClass.setValue_string("floatfrom", "adapter");
            mContext.startService(new Intent(mContext, ChatHeadService.class));
            Uri navigation = Uri.parse("google.navigation:q="+clat+","+clang);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.maps");
            intent.setData(navigation);
            mContext.startActivity(intent);

        });
        }catch (Exception exc){
            exc.printStackTrace();
        }
        //String desc = detailsReportsList.get(position).getConsumerAccount();

       /* SpannableStringBuilder sb = new SpannableStringBuilder(desc);
        Pattern word = Pattern.compile(search_string.toLowerCase());
        Matcher match = word.matcher(desc.toLowerCase());*/

      /*  while (match.find()) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(
                    ContextCompat.getColor(mContext, R.color.colorAccent));
            sb.setSpan(fcs, match.start(), match.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.tv_coll_month.setText(sb);*/


        //String desName = detailsReportsList.get(position).getConsumerName();

       /* SpannableStringBuilder sbName = new SpannableStringBuilder(desName);
        Pattern words = Pattern.compile(search_string.toLowerCase());
        Matcher matchs = words.matcher(desName.toLowerCase());*/

        /*while (matchs.find()) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(
                    ContextCompat.getColor(mContext, R.color.colorAccent));
            sbName.setSpan(fcs, matchs.start(), matchs.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.tv_con_name.setText(sbName);
*/

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

        private TextView tv_sc_num;
        private TextView tv_amount;
        private TextView tv_con_name;
        private TextView tv_distance;
        ImageView drive;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sc_num = itemView.findViewById(R.id.tv_coll);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_con_name = itemView.findViewById(R.id.tv_con_name);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            drive = itemView.findViewById(R.id.imageView5);

        }
    }

    public void filterList(ArrayList<NavReport> filteredList, String search_strings) {
        detailsReportsList = filteredList;
        search_string = search_strings;
        notifyDataSetChanged();
    }
}
