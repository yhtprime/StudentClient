package com.dryht.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.FriendInfoActivity;
import com.dryht.mobile.Activity.InfomationActivity;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.squareup.picasso.Picasso;

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

/*
朋友圈的share绑定
 */
public class FriendShareAdapter extends RecyclerView.Adapter<FriendShareAdapter.Holder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private JSONArray mData;
    private int mLayoutId;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private int flag;
    public FriendShareAdapter(Context context, JSONArray data, int layoutId, android.os.Handler mHandler,int flag) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
        this.mHandler = mHandler;
        this.flag = flag;
    }

    @Override
    public FriendShareAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        sharedPreferences= view.getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
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
                holder.sharebtn_zan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like));
            }
            else
            {
                holder.sharebtn_zan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_praise));
            }
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("pic").toString()).into(holder.sharePic);
            Picasso.with(mContext).load(this.mData.getJSONObject(position).get("image").toString()).into(holder.shareImage);
            //添加事件跳转
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FriendInfoActivity.class);
                    try {
                        intent.putExtra("circleid",mData.getJSONObject(position).get("circleid").toString());
                        mContext.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            if(flag!=2)
            {
                holder.sharePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            System.out.println("---------");
                            Intent intent = new Intent(mContext, InfomationActivity.class);
                            intent.putExtra("circleid",mData.getJSONObject(position).get("circleid").toString());
                            mContext.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.shareName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mContext, InfomationActivity.class);
                            intent.putExtra("circleid",mData.getJSONObject(position).get("circleid").toString());
                            mContext.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            holder.sharebtn_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    FormBody mFormBody= null;
                    Request mRequest = null;
                    try {
                        mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("circleid",mData.getJSONObject(position).get("circleid").toString() ).build();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mRequest=new Request.Builder().url(Utils.generalUrl+"friendcirclelike/").post(mFormBody).build();
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
                                                    holder.sharebtn_zan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_like));
                                                    holder.shareZan.setText(String.valueOf(Integer.parseInt( holder.shareZan.getText().toString())+1));

                                                }
                                                else
                                                {
                                                    holder.sharebtn_zan.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_praise));
                                                    holder.shareZan.setText(String.valueOf(Integer.parseInt( holder.shareZan.getText().toString())-1));
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
                            XToastUtils.toast("失败");
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

    class Holder extends RecyclerView.ViewHolder {
        ImageView sharePic,shareImage;
        TextView shareName,shareIntro,shareTime,shareZan,shareComment,shareRead,shareTitle;
        ImageView sharebtn_zan;
        CardView cardView;

        public Holder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view1);
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
