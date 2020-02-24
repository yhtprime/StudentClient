package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dryht.mobile.Adapter.FriendShareAdapter;
import com.dryht.mobile.Adapter.RecycleViewFriendCommentAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.widget.actionbar.TitleBar;
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

public class FriendInfoActivity extends AppCompatActivity {
    private TitleBar titleBar;
    private RadiusImageView friendinfo_pic;
    private TextView friendinfo_name,friendinfo_title,friendinfo_time,friendinfo_intro;
    private ImageView pic1,pic2,pic3;
    private RecyclerView recyclerView;
    private EditText friend_comment;
    private Button friend_fabu_pl;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private String circleid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        circleid =  getIntent().getStringExtra("circleid");
        mHandler = new Handler();
        initcompent();
        initTitleBar();
        getInfo();
        friend_fabu_pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friend_comment.getText() == null)
                {
                    XToastUtils.toast("评论不能为空");
                }
                else
                {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    FormBody mFormBody= null;
                    Request mRequest = null;
                    mFormBody = new FormBody.Builder().add("content", String.valueOf(friend_comment.getText())).add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("circleid",circleid ).build();
                    mRequest=new Request.Builder().url(Utils.generalUrl+"sendfriendcomment/").post(mFormBody).build();
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
                                            XToastUtils.toast("评论成功");
                                            friend_comment.setText("");
                                            friend_fabu_pl.clearFocus();
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
            }
        });
    }

    private void getInfo() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("circleid",circleid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getfriendinfo/")
                .post(mFormBody)
                .build();

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
                        final JSONObject jsonObject = new JSONObject(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    friendinfo_name.setText(jsonObject.get("pname").toString());
                                    friendinfo_title.setText(jsonObject.get("cname").toString());
                                    friendinfo_time.setText(jsonObject.get("time").toString());
                                    friendinfo_intro.setText(jsonObject.get("intro").toString());
                                    System.out.println(jsonObject.get("pic1"));
                                    if (jsonObject.get("pic1")!=null)
                                    {
                                        Picasso.with(FriendInfoActivity.this).load(jsonObject.get("pic1").toString()).into(pic1);
                                        Picasso.with(FriendInfoActivity.this).load(jsonObject.get("pic2").toString()).into(pic2);
                                        Picasso.with(FriendInfoActivity.this).load(jsonObject.get("pic3").toString()).into(pic3);
                                    }
                                    Picasso.with(FriendInfoActivity.this).load(jsonObject.get("ppic").toString()).into(friendinfo_pic);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, 0);
                        //请求评论
                        OkHttpClient mOkHttpClient=new OkHttpClient();
                        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("circleid",circleid).build();
                        Request mRequest=new Request.Builder().url(Utils.generalUrl+"getfriendcomment/").post(mFormBody).build();
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
                                        final JSONArray jsonArray = new JSONArray(result.get("data").toString());
                                        //挂起
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                RecycleViewFriendCommentAdapter friendShareAdapter = new RecycleViewFriendCommentAdapter(FriendInfoActivity.this,jsonArray,R.layout.adapter_recycle_view_fricomment_item,mHandler);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(FriendInfoActivity.this));
                                                recyclerView.setAdapter(friendShareAdapter);
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

    private void initcompent() {
        titleBar = findViewById(R.id.friendinfotitlebar);
        friendinfo_pic = findViewById(R.id.friendinfo_pic);
        friendinfo_name = findViewById(R.id.friendinfo_name);
        friendinfo_title = findViewById(R.id.friendinfo_title);
        friendinfo_time = findViewById(R.id.friendinfo_time);
        friendinfo_intro = findViewById(R.id.friendinfo_intro);
        recyclerView = findViewById(R.id.friendinfo_recycleview);
        friend_comment = findViewById(R.id.friend_comment);
        friend_fabu_pl = findViewById(R.id.friend_fabu_pl);
        pic1 = findViewById(R.id.pic1);
        pic2 = findViewById(R.id.pic2);
        pic3 = findViewById(R.id.pic3);
    }
    private void initTitleBar() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
