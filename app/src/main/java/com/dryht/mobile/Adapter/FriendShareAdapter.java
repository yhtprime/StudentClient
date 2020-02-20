package com.dryht.mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

/*
朋友圈的share绑定
 */
public class FriendShareAdapter extends RecyclerView.Adapter<FriendShareAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;

    public FriendShareAdapter(Context context, JSONArray data, int layoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public FriendShareAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        return new FriendShareAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            holder.shareName.setText(this.mData.getJSONObject(position).get("name").toString());
            holder.shareIntro.setText(this.mData.getJSONObject(position).get("intro").toString());
            holder.shareTitle.setText(this.mData.getJSONObject(position).get("title").toString());
            holder.shareTime.setText(this.mData.getJSONObject(position).get("time").toString());
            holder.shareZan.setText(this.mData.getJSONObject(position).get("zan").toString());
            holder.shareComment.setText(this.mData.getJSONObject(position).get("com").toString());
            holder.shareRead.setText("浏览量 "+this.mData.getJSONObject(position).get("read").toString());
            if (this.mData.getJSONObject(position).get("islike").toString().equals("1"))
            {
                holder.sharebtn_zan.setImageResource(R.drawable.ic_like);
            }
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.sharePic);
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("image").toString()).into(holder.shareImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView sharePic,shareImage;
        TextView shareName,shareIntro,shareTime,shareZan,shareComment,shareRead,shareTitle;
        ImageView sharebtn_zan;

        public Holder(View itemView) {
            super(itemView);
            sharePic = itemView.findViewById(R.id.share_pic);
            shareImage = itemView.findViewById(R.id.share_image);
            shareName = itemView.findViewById(R.id.share_name);
            shareIntro = itemView.findViewById(R.id.share_intro);
            shareTime = itemView.findViewById(R.id.share_time);
            shareZan = itemView.findViewById(R.id.share_zan);
            shareComment = itemView.findViewById(R.id.share_comment);
            shareRead = itemView.findViewById(R.id.share_read);
            sharebtn_zan = itemView.findViewById(R.id.share_zan_btn);
            shareTitle = itemView.findViewById(R.id.share_title);
        }
    }
}
