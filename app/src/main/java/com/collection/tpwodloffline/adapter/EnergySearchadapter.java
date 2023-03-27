package com.collection.tpwodloffline.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.collection.tpwodloffline.R;
import com.collection.tpwodloffline.activity.EnergySearchActivity;
import com.collection.tpwodloffline.model.EnergyModel;
import com.collection.tpwodloffline.nonenergy.NonenModel;
import com.collection.tpwodloffline.nonenergy.SearchActivity;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.util.ArrayList;

public class EnergySearchadapter extends RecyclerView.Adapter<EnergySearchadapter.MyViewHolder> {

    private ArrayList<EnergyModel> dataList;
    private Context mContext;
    private String search_string = "";
    SharedPreferenceClass sharedPreferenceClass;

    public EnergySearchadapter(Context context, ArrayList<EnergyModel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        this.sharedPreferenceClass = new SharedPreferenceClass(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.adapter_ensearch, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            EnergyModel dataModel = dataList.get(position);
            holder.tv_con_name.setText(dataModel.getCname());
            holder.tv_sc_num.setText(dataModel.getScnum());
            holder.tv_amount.setText(dataModel.getPayable());

            holder.copy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) mContext.
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EN", dataModel.getScnum());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "SC number copied", Toast.LENGTH_SHORT).show();
                ((EnergySearchActivity) mContext).finish();
            });
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
        ImageView copy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sc_num = itemView.findViewById(R.id.tv_coll);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_con_name = itemView.findViewById(R.id.tv_con_name);

            copy = itemView.findViewById(R.id.imageView5);
        }
    }

    public void filterList(ArrayList<EnergyModel> filteredList, String search_strings) {
        dataList = filteredList;
        search_string = search_strings;
        notifyDataSetChanged();
    }
}
