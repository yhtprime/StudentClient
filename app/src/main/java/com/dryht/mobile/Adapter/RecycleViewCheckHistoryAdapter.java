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
import com.dryht.mobile.Bean.Check;
import com.dryht.mobile.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class RecycleViewCheckHistoryAdapter extends RecyclerView.Adapter<RecycleViewCheckHistoryAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Check> mData;
    private int mLayoutId;

    public RecycleViewCheckHistoryAdapter(Context context, List<Check> data, int layoutId) {
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
            holder.history_name.setText(this.mData.get(position).getName().toString());
            holder.history_time.setText(this.mData.get(position).getTime().toString());
            if (this.mData.get(position).getStatus()==1)
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
                        Intent intent = new Intent(mContext, CheckInfoActivity.class);
                        intent.putExtra("checkid",String.valueOf(mData.get(position).getCheckid()));
                        intent.putExtra("classid",String.valueOf(mData.get(position).getClassid()));
                        mContext.startActivity(intent);

                }
            });

    }




    @Override
    public int getItemCount() {
        return mData.size();
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
