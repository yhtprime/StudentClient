package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNoticeClassActivity extends AppCompatActivity {
    private String classid;
    private TitleBar titleBar;
    private MultiLineEditText intro;
    private ClearEditText title;
    private SharedPreferences sharedPreferences;
    private ButtonView send;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notice_class);
        titleBar = findViewById(R.id.notice_class_titlebar);
        title = findViewById(R.id.notice_class_title);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        intro = findViewById(R.id.notice_class_intro);
        send = findViewById(R.id.notice_class_send);
        classid = getIntent().getStringExtra("classid");
        mHandler = new Handler();
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setLeftImageDrawable(getResources().getDrawable(R.drawable.back));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString()==null)
                {
                    XToastUtils.error("请输入主题");
                }
                else if(intro.getContentText()==null)
                {
                    XToastUtils.error("请输入内容");
                }
                else {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
                    FormBody mFormBody=new FormBody.Builder().add("classid",classid).add("intro",intro.getContentText()).add("title",title.getText().toString()).add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();

                    Request mRequest=new Request.Builder()
                            .url(Utils.generalUrl+"sendnoticeclass/")
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
                                System.out.println(result.get("status"));
                                //取数据
                                if(result.get("status").equals("1"))
                                {
                                    XToastUtils.success("发送成功");
                                    finish();
                                }
                                else
                                {
                                    XToastUtils.error("发送失败");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Looper.loop();

                        }

                        @Override
                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                            Looper.prepare();
                            XToastUtils.error("网络请求失败");
                            Looper.loop();
                        }
                    });
                }
            }
        });
    }
}
