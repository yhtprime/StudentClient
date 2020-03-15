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

import com.dryht.mobile.Adapter.RecycleViewCheckHistoryAdapter;
import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.Bean.Check;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/*
考勤记录
 */
public class CheckHistoryActivity extends AppCompatActivity {
    private TitleBar mTitleBar;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private List<Check> checks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_history);
        mTitleBar = findViewById(R.id.checkhistorytitle);
        mTitleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        recyclerView = findViewById(R.id.checkhistory_recycleview);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        mHandler = new Handler();
        initTitleBar();
        getCheckHistory();
    }

    private void getCheckHistory() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getCheckHistory/")
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
                        //挂起
                        final JSONArray finalResult = new JSONArray(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < finalResult.length(); i++) {
                                    try {
                                        Check c = new Check(finalResult.getJSONObject(i).getInt("checkid"), Timestamp.valueOf(finalResult.getJSONObject(i).getString("time")),finalResult.getJSONObject(i).getString("name")
                                        ,finalResult.getJSONObject(i).getInt("teacherid"),finalResult.getJSONObject(i).getInt("classid"),finalResult.getJSONObject(i).getInt("status"));
                                        checks.add(c);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                RecycleViewCheckHistoryAdapter r = new RecycleViewCheckHistoryAdapter(CheckHistoryActivity.this,checks,R.layout.adapter_recycle_view_checkhistory_item);
                                recyclerView.setLayoutManager(new LinearLayoutManager(CheckHistoryActivity.this));
                                recyclerView.setAdapter(r);
                            }
                        }, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                XToastUtils.toast("获取课程信息失败");
            }
        });
    }

    private void initTitleBar() {
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
