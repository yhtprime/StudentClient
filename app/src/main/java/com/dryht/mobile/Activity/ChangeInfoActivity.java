package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.dryht.mobile.Adapter.RecyclerViewBannerAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

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
修改个人信息
 */
public class ChangeInfoActivity extends AppCompatActivity {
private MultiLineEditText intro;
private TitleBar titleBar;
private ButtonView buttonView;
private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        intro = findViewById(R.id.change_intro);
        buttonView = findViewById(R.id.changeinfo_sub_btn);
        titleBar = findViewById(R.id.change_titlebar);
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setLeftImageDrawable(getResources().getDrawable(R.drawable.back));
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intro.getContentText()!=null)
                {
                    OkHttpClient mOkHttpClient=new OkHttpClient();

                    FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("intro",intro.getContentText()).build();

                    Request mRequest=new Request.Builder()
                            .url(Utils.generalUrl+"changeintro/")
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
                                    XToastUtils.success("信息修改成功");
                                }
                                else
                                {
                                    XToastUtils.error("获取新闻列表失败");
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
                else
                    XToastUtils.error("简介不能为空");
            }
        });
    }
}
