package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;;
import com.dryht.mobile.Fragment.TabNewFragent;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.google.android.material.tabs.TabLayout;

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

public class TabNewActivity extends AppCompatActivity {
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TabLayout tblayout;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_new);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        //获取管理者
        supportFragmentManager = getSupportFragmentManager();
        initcomponent();
    }

    private void initcomponent() {
        tblayout = findViewById(R.id.tb_new);

        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getNews/")
                .get()
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
                        sharedPreferences.edit().putString("newsList", String.valueOf(result.get("data"))).commit();
                        final JSONArray jsonArray = new JSONArray(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        tblayout.addTab(tblayout.newTab().setText(jsonArray.getJSONObject(i).get("name").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                            XToastUtils.toast("获取新闻失败");
            }
        });
        initListener();
    }
    private void initListener() {
        tblayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(sharedPreferences.getString("newsList", null));
                    //开启事务
                    fragmentTransaction =  supportFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.new_fragent, new TabNewFragent(jsonArray.getJSONObject(tab.getPosition()).get("categoryid").toString())).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
