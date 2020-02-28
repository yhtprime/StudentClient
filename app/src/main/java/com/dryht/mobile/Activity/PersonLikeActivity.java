package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;

import com.dryht.mobile.Adapter.RecycleViewLikeAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

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

public class PersonLikeActivity extends AppCompatActivity {
    private TitleBar titleBar;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_like);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();
        titleBar = findViewById(R.id.person_likebar);
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setLeftImageDrawable(getResources().getDrawable(R.drawable.back));
        recyclerView = findViewById(R.id.perlike_recycle);
        getlikes();
    }

    private void getlikes() {
        OkHttpClient mOkHttpClient=new OkHttpClient();

        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();

        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getperlike/")
                .post(mFormBody)
                .build();

        mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                Looper.prepare();
                //String转JSONObject
                JSONObject result = null;
                try {
                    result = new JSONObject(response.body().string());
                    //取数据
                    if(result.get("status").equals("1"))
                    {
                        JSONArray jsonArray = new JSONArray(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RecycleViewLikeAdapter recycleViewLikeAdapter = new RecycleViewLikeAdapter(PersonLikeActivity.this,jsonArray,R.layout.adapter_recycle_view_like_item);
                                recyclerView.setLayoutManager(new LinearLayoutManager(PersonLikeActivity.this));
                                recyclerView.setAdapter(recycleViewLikeAdapter);
                            }
                        }, 0);
                    }
                    else
                    {
                        XToastUtils.error("获取失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Looper.loop();

            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
            }
        });
    }

}
