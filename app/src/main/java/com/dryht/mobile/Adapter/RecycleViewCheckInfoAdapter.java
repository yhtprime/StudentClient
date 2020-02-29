package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.CheckInfoActivity;
import com.dryht.mobile.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

public class RecycleViewCheckInfoAdapter extends RecyclerView.Adapter<RecycleViewCheckInfoAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;

    public RecycleViewCheckInfoAdapter(Context context, JSONArray data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public RecycleViewCheckInfoAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new RecycleViewCheckInfoAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewCheckInfoAdapter.Holder holder, int position) {
        try {
            holder.checkinfo_name.setText(this.mData.getJSONObject(position).get("name").toString());
            if (this.mData.getJSONObject(position).get("status").toString().equals("1"))
            {
                holder.checkinfo_status.setText("已考勤");
                holder.checkinfo_status.setTextColor(mContext.getResources().getColor(R.color.md_green_300));
            }
            else
            {
                holder.checkinfo_status.setText("未考勤");
                holder.checkinfo_status.setTextColor(mContext.getResources().getColor(R.color.md_red_300));
            }
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("headpic").toString()).into(holder.checkinfo_pic);
            holder.CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(mContext, CheckInfoActivity.class);
                        intent.putExtra("checkid",mData.getJSONObject(position).get("checkid").toString());
                        intent.putExtra("classid",mData.getJSONObject(position).get("classid").toString());
                        mContext.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public int getItemCount() {
        return mData.length();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView checkinfo_name,checkinfo_status;
        ImageView checkinfo_pic;
        CardView CardView;
        public Holder(View itemView) {
            super(itemView);
            checkinfo_pic = itemView.findViewById(R.id.checkinfo_pic);
            checkinfo_name = itemView.findViewById(R.id.checkinfo_name);
            checkinfo_status = itemView.findViewById(R.id.checkinfo_status);
            CardView = itemView.findViewById(R.id.card_view4);
        }
    }
}
