package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.InfomationActivity;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
import com.dryht.mobile.utils.XToastUtils;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.json.JSONArray;
import org.json.JSONException;

public class RecycleViewLikeAdapter extends RecyclerView.Adapter<RecycleViewLikeAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;
    //声明自定义的监听接口
    private OnRecyclerItemClickListener monItemClickListener;

    public RecycleViewLikeAdapter(Context context, JSONArray data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public RecycleViewLikeAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new RecycleViewLikeAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewLikeAdapter.Holder holder, int position){
        try {
            holder.name.setLeftString(this.mData.getJSONObject(position).get("name").toString());
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.likepic);
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(mContext, InfomationActivity.class);
                        intent.putExtra("id",mData.getJSONObject(position).get("id").toString());
                        intent.putExtra("iden",mData.getJSONObject(position).get("iden").toString());
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




    class Holder extends RecyclerView.ViewHolder  {
        RadiusImageView likepic;
        SuperTextView name;
        LinearLayout linearLayout;
        public Holder(View itemView) {
            super(itemView);
            likepic = itemView.findViewById(R.id.like_pic);
            name = itemView.findViewById(R.id.like_name);
            linearLayout = itemView.findViewById(R.id.frienditem);
        }

    }
}

