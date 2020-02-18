package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.NewInfoActivity;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.json.JSONArray;
import org.json.JSONException;

public class RecycleViewNewAdapter extends RecyclerView.Adapter<RecycleViewNewAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;
    //声明自定义的监听接口
    private OnRecyclerItemClickListener monItemClickListener;

    public RecycleViewNewAdapter(Context context, JSONArray data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position){
        try {
            holder.tv_user_name.setText(this.mData.getJSONObject(position).get("cname").toString());
            holder.tv_tag.setText(this.mData.getJSONObject(position).get("time").toString());
            holder.tv_title.setText(this.mData.getJSONObject(position).get("name").toString());
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.iv_image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return mData.length();
    }


    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener){
        monItemClickListener=listener;
    }


    class Holder extends RecyclerView.ViewHolder  {
        TextView tv_user_name,tv_tag,tv_title;
        RadiusImageView radiusImageView,iv_image;

        public Holder(View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_tag = itemView.findViewById(R.id.tv_tag);
            tv_title = itemView.findViewById(R.id.tv_title);
            radiusImageView = itemView.findViewById(R.id.iv_avatar);
            iv_image = itemView.findViewById(R.id.iv_image);
            //将监听传递给自定义接口
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (monItemClickListener!=null){
                        monItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

    }
}
