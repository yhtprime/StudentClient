package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NoticeInfoActivity extends AppCompatActivity {
    private TitleBar titleBar;
    private String infoid;
    private TextView notice_title,notice_teacher,notice_time,noticeintro;
    private Button notice_check_btn;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_info);
        infoid = getIntent().getStringExtra("infoid");
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();
        initcomponent();
        initTitleBar();
        getnoticeinfo();
    }
    //获取info详情
    private void getnoticeinfo() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody= null;
        Request mRequest = null;
        String circommonid = null;
        mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("infoid",infoid ).build();
        mRequest=new Request.Builder().url(Utils.generalUrl+"getnoticeinfo/").post(mFormBody).build();
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
                        JSONObject finalResult = new JSONObject(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                        notice_title.setText(finalResult.getString("title"));
                                        notice_teacher.setText(finalResult.getString("tname"));
                                        notice_time.setText(finalResult.getString("time"));
                                        noticeintro.setText(finalResult.getString("intro"));
                                        if (finalResult.getString("status").equals("1")){
                                            notice_check_btn.setEnabled(false);
                                            notice_check_btn.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_6));
                                            notice_check_btn.setText("已经提交");
                                        }
                                        else
                                        {
                                            notice_check_btn.setEnabled(true);
                                            notice_check_btn.setBackgroundColor(getResources().getColor(R.color.md_blue_300));
                                            notice_check_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    OkHttpClient mOkHttpClient=new OkHttpClient();
                                                    FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("infoid",infoid ).build();
                                                    Request mRequest=new Request.Builder()
                                                            .url(Utils.generalUrl+"checknotice/")
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

                                                                    final String finalResult = new String(result.get("data").toString());
                                                                    mHandler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                                if (finalResult.equals("1"))
                                                                                {
                                                                                    notice_check_btn.setEnabled(false);
                                                                                    notice_check_btn.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_6));
                                                                                    notice_check_btn.setText("已经提交");
                                                                                }
                                                                        }
                                                                    },0);

                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        @Override
                                                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                                            XToastUtils.toast("获取新闻信息失败");
                                                        }
                                                    });
                                                }
                                            });
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
    //初始化titlebar
    private void initTitleBar() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initcomponent() {
        titleBar = findViewById(R.id.noticeinfotitle);
        notice_title = findViewById(R.id.notice_title);
        notice_teacher = findViewById(R.id.notice_teacher);
        notice_time = findViewById(R.id.notice_time);
        noticeintro = findViewById(R.id.noticeintro);
        notice_check_btn = findViewById(R.id.notice_check_btn);

    }
}
