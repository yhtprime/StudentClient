package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.FriendInfoActivity;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecycleViewFriendCommentAdapter extends RecyclerView.Adapter<RecycleViewFriendCommentAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private Handler mHandler;
    private int mLayoutId;
    private SharedPreferences sharedPreferences;
    //声明自定义的监听接口
    private OnRecyclerItemClickListener monItemClickListener;

    public RecycleViewFriendCommentAdapter(Context context, JSONArray data, int layoutId, android.os.Handler mHandler) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
        this.mHandler = mHandler;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        sharedPreferences= view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewFriendCommentAdapter.Holder holder, int position){
        try {
            holder.friendcom_name.setText(this.mData.getJSONObject(position).get("name").toString());
            holder.friendcom_title.setText(this.mData.getJSONObject(position).get("intro").toString());
            holder.friendcom_zan.setText(this.mData.getJSONObject(position).get("zan").toString());
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.friendcom_pic);
            if(this.mData.getJSONObject(position).get("flag").toString().equals("1"))
            {
                holder.friendcom_zan_btn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like));
            }
            else
            {
                holder.friendcom_zan_btn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_praise));
            }
            
            holder.friendcom_zan_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    FormBody mFormBody= null;
                    Request mRequest = null;
                    String circommonid = null;
                    try {
                        circommonid = mData.getJSONObject(position).get("circommonid").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("circommonid",circommonid ).build();
                        mRequest=new Request.Builder().url(Utils.generalUrl+"friendcomlike/").post(mFormBody).build();
                        mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
                            @Override
                            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                                //String转JSONObject
                                JSONObject result = null;
                                try {
                                    result = new JSONObject(response.body().string());
                                    //取数据
                                    if(result.get("status").equals("1"))
                                    {
                                        //挂起
                                        JSONObject finalResult = result;
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if (finalResult.get("data").equals("1"))
                                                    {
                                                        holder.friendcom_zan_btn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like));
                                                        holder.friendcom_zan.setText(String.valueOf(Integer.parseInt( holder.friendcom_zan.getText().toString())+1));

                                                    }
                                                    else
                                                    {
                                                        holder.friendcom_zan_btn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_praise));
                                                        holder.friendcom_zan.setText(String.valueOf(Integer.parseInt( holder.friendcom_zan.getText().toString())-1));
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 0);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                XToastUtils.toast("获取课程表失败");
                            }
                        });
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
        TextView friendcom_name,friendcom_title,friendcom_zan;
        RadiusImageView friendcom_pic;
        AppCompatImageView friendcom_zan_btn;

        public Holder(View itemView) {
            super(itemView);
            friendcom_name = itemView.findViewById(R.id.friendcom_name);
            friendcom_title = itemView.findViewById(R.id.friendcom_title);
            friendcom_pic = itemView.findViewById(R.id.friendcom_pic);
            friendcom_zan_btn = itemView.findViewById(R.id.friendcom_zan_btn);
            friendcom_zan = itemView.findViewById(R.id.friendcom_zan);
        }

    }
}
