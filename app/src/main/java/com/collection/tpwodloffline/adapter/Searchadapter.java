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
import com.collection.tpwodloffline.nonenergy.NonenModel;
import com.collection.tpwodloffline.nonenergy.SearchActivity;
import com.collection.tpwodloffline.utils.SharedPreferenceClass;

import java.util.ArrayList;

public class Searchadapter extends RecyclerView.Adapter<Searchadapter.MyViewHolder> {

    private ArrayList<NonenModel> nonenList;
    private Context mContext;
    private String search_string = "";
    SharedPreferenceClass sharedPreferenceClass;

    public Searchadapter(Context context, ArrayList<NonenModel> nonenList) {
        this.nonenList = nonenList;
        this.mContext = context;
        this.sharedPreferenceClass = new SharedPreferenceClass(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nonensearch, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try{

        NonenModel nonenModel = nonenList.get(position);


        holder.tv_con_name.setText(nonenModel.getCname());
        holder.tv_sc_num.setText(nonenModel.getScnum());
        holder.tv_amount.setText(nonenModel.getPayable());
        holder.tv_ref_name.setText(nonenModel.getRef());
        holder.tv_module.setText(nonenModel.getModule());


        holder.copy.setOnClickListener(v->{

            ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("NONEN", nonenModel.getRef());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "Reference number copied", Toast.LENGTH_SHORT).show();
            ((SearchActivity)mContext).finish();

        });
        }catch (Exception exc){
            exc.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return nonenList.size();
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
        private TextView tv_ref_name;
        private TextView tv_module;
        ImageView copy;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sc_num = itemView.findViewById(R.id.tv_coll);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_con_name = itemView.findViewById(R.id.tv_con_name);
            tv_ref_name = itemView.findViewById(R.id.tv_ref);
            tv_module = itemView.findViewById(R.id.tv_module);
            copy = itemView.findViewById(R.id.imageView5);

        }
    }

    public void filterList(ArrayList<NonenModel> filteredList, String search_strings) {
        nonenList = filteredList;
        search_string = search_strings;
        notifyDataSetChanged();
    }
}
