package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class RecycleViewCheckHistoryAdapter extends RecyclerView.Adapter<RecycleViewCheckHistoryAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;

    public RecycleViewCheckHistoryAdapter(Context context, JSONArray data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public RecycleViewCheckHistoryAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new RecycleViewCheckHistoryAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewCheckHistoryAdapter.Holder holder, int position) {
        try {
            holder.history_name.setText(this.mData.getJSONObject(position).get("name").toString());
            holder.history_time.setText(this.mData.getJSONObject(position).get("time").toString());
            if (this.mData.getJSONObject(position).get("status").toString().equals("1"))
            {
                holder.history_status.setText("考勤中");
                holder.history_status.setTextColor(mContext.getColor(R.color.md_green_300));
            }
            else
            {
                holder.history_status.setText("已结束");
                holder.history_status.setTextColor(mContext.getColor(R.color.md_red_300));
            }
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
        TextView history_name,history_time,history_status;
        CardView CardView;
        public Holder(View itemView) {
            super(itemView);
            history_name = itemView.findViewById(R.id.history_name);
            history_time = itemView.findViewById(R.id.history_time);
            history_status = itemView.findViewById(R.id.history_status);
            CardView = itemView.findViewById(R.id.card_view3);
        }
    }
}
