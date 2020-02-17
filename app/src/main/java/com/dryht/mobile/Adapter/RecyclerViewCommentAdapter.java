package com.dryht.mobile.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;

    public RecyclerViewCommentAdapter(Context context, JSONArray data, int layoutId) {
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
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            holder.name.setText(this.mData.getJSONObject(position).get("sname").toString());
            holder.intro.setText(this.mData.getJSONObject(position).get("intro").toString());
            holder.time.setText(this.mData.getJSONObject(position).get("time").toString());
            System.out.println(this.mData.getJSONObject(position).get("pic").toString());
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.pic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public int getItemCount() {
        return mData.length();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView name,intro,time;

        public Holder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.comment_pic);
            name = itemView.findViewById(R.id.comment_name);
            intro  = itemView.findViewById(R.id.comment_intro);
            time = itemView.findViewById(R.id.comment_time);
        }
    }
}
