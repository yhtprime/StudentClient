package com.dryht.mobile.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dryht.mobile.Adapter.FriendAdapter;
import com.dryht.mobile.Fragment.FriendFragment;
import com.dryht.mobile.Fragment.FriendListFragment;
import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Lesson;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.devmeteor.tableview.TableView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfomationActivity extends AppCompatActivity {
    private RefreshLayout refreshLayout;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    private LinearLayout linearLayout;
    private ImageView perlike,per_icon,per_background,per_back;
    private FragmentManager supportFragmentManager;
    private String circleid;
    private FragmentTransaction fragmentTransaction;
    private TextView per_text_name,percourse,personintro;
    private List<LocalMedia> mSelectList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        perlike = findViewById(R.id.perlike);
        per_icon = findViewById(R.id.per_icon);
        per_text_name = findViewById(R.id.per_text_name);
        percourse = findViewById(R.id.percourse);
        per_background = findViewById(R.id.per_background);
        personintro = findViewById(R.id.personintro);
        per_back = findViewById(R.id.per_back);
        per_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout = (RefreshLayout)findViewById(R.id.perinforefreshLayout);
        circleid = getIntent().getStringExtra("circleid");
        linearLayout = findViewById(R.id.per_post);
        //关注
        linearLayout.setOnClickListener(new likeper());
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();

        refreshLayout.setOnRefreshListener(new refreshListener());
        refreshLayout.autoRefresh();
    }

    //刷新页面
    private class refreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    //这一块没有处理好，第一个走的是从朋友圈点击查看详细资料，
                    //第二个是从搜索方式查看用户资料
                    System.out.println(getIntent().getStringExtra("id"));
                    if (getIntent().getStringExtra("id")==null)
                        getPersonProfile();
                    else {
                        getfriendProfile();
                    }
                    //结束加载
                    refreshLayout.finishRefresh();
                    //加载失败的话3秒后结束加载
                    refreshLayout.finishRefresh(3000);
                    Looper.loop();
                }
            },2000);

        }
    }
    private void getfriendProfile(){
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("iden",getIntent().getStringExtra("iden")).add("id",getIntent().getStringExtra("id")).add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest;
        mRequest=new Request.Builder().url(Utils.generalUrl+"getFriendProfile/").post(mFormBody).build();
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
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(jsonObject.get("background").toString().indexOf("http")>=0)
                                        Picasso.with(InfomationActivity.this).load(jsonObject.getString("background")).into(per_background);
                                    Picasso.with(InfomationActivity.this).load(jsonObject.getString("pic")).into(per_icon);
                                    per_text_name.setText(jsonObject.getString("name"));
                                    personintro.setText(jsonObject.getString("intro"));
                                    if (jsonObject.getString("iden").equals("0"))
                                    {
                                        percourse.setText("来自"+jsonObject.getString("course")+"的老师");
                                    }
                                    else {
                                        percourse.setText("来自"+jsonObject.getString("course")+"的大"+jsonObject.getInt("grade")+"学生");
                                    }
                                    if (jsonObject.getInt("islike")==0)
                                    {
                                        perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_addlike));
                                    }
                                    else {
                                        perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
                                    }
                                    personintro.setText(jsonObject.getString("intro"));
                                    //添加底部导航上去
                                    //获取管理者
                                    supportFragmentManager = getSupportFragmentManager();
                                    //开启事务
                                    fragmentTransaction =  supportFragmentManager.beginTransaction();
                                    sharedPreferences.edit().putString("id", String.valueOf(jsonObject.getInt("id"))).putString("iden", String.valueOf(jsonObject.getInt("iden"))).commit();
                                    fragmentTransaction.replace(R.id.per_pager, new FriendListFragment(2)).commit();
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
    private void getPersonProfile(){
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("circleid",circleid).add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest;
        mRequest=new Request.Builder().url(Utils.generalUrl+"getPersonProfile/").post(mFormBody).build();
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
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(jsonObject.get("background").toString().indexOf("http")>=0)
                                        Picasso.with(InfomationActivity.this).load(jsonObject.getString("background")).into(per_background);
                                    Picasso.with(InfomationActivity.this).load(jsonObject.getString("pic")).into(per_icon);
                                    per_text_name.setText(jsonObject.getString("name"));
                                    personintro.setText(jsonObject.getString("intro"));
                                    if (jsonObject.getString("iden").equals("0"))
                                    {
                                        percourse.setText("来自"+jsonObject.getString("course")+"的老师");
                                    }
                                    else {
                                        percourse.setText("来自"+jsonObject.getString("course")+"的大"+jsonObject.getInt("grade")+"学生");
                                    }
                                    if (jsonObject.getInt("islike")==0)
                                    {
                                        perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_addlike));
                                    }
                                    else {
                                        perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
                                    }
                                    personintro.setText(jsonObject.getString("intro"));
                                    //添加底部导航上去
                                    //获取管理者
                                    supportFragmentManager = getSupportFragmentManager();
                                    //开启事务
                                    fragmentTransaction =  supportFragmentManager.beginTransaction();
                                    sharedPreferences.edit().putString("id", String.valueOf(jsonObject.getInt("id"))).putString("iden", String.valueOf(jsonObject.getInt("iden"))).commit();
                                    fragmentTransaction.replace(R.id.per_pager, new FriendListFragment(2)).commit();
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

    private class likeper implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sharedPreferences.getString("auth",null).equals(sharedPreferences.getString("id",null))&&sharedPreferences.getString("iden",null).equals(sharedPreferences.getString("identity",null)))
                XToastUtils.error("您不能自己关注自己");
            else {
                OkHttpClient mOkHttpClient = new OkHttpClient();
                FormBody mFormBody = new FormBody.Builder().add("iden", sharedPreferences.getString("iden", null)).add("id", sharedPreferences.getString("id", null)).add("auth", sharedPreferences.getString("auth", null)).add("identity", sharedPreferences.getString("identity", null)).build();
                Request mRequest;
                mRequest = new Request.Builder().url(Utils.generalUrl + "followperson/").post(mFormBody).build();
                mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                        //String转JSONObject
                        JSONObject result = null;
                        try {
                            result = new JSONObject(response.body().string());
                            //取数据
                            if (result.get("status").equals("1")) {
                                //挂起
                                String finalResult = result.get("data").toString();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalResult.equals("1")) {
                                            perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked));
                                            XToastUtils.success("关注成功");
                                        } else {
                                            perlike.setImageDrawable(getResources().getDrawable(R.drawable.ic_addlike));
                                            XToastUtils.success("取消关注");
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
        }
    }
}
