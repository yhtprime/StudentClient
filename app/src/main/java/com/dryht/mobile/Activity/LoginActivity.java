package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText account;
    private EditText password;
    private TextView forget;
    private Button login;
    private ImageView startrecogn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initcomponent();
        login.setOnClickListener(new loginListener());
        startrecogn.setOnClickListener(new startrecognListener());
//        SharedPreferences sharedPreferences= getSharedPreferences("data", Context .MODE_PRIVATE);
//        String userId=sharedPreferences.getString("authen","");
//        System.out.println(userId+"*************************************");
    }

    private void initcomponent() {
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        forget = findViewById(R.id.forget);
        login = findViewById(R.id.login_btn);
        startrecogn = findViewById(R.id.startrecogn);
    }


    private class loginListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            OkHttpClient mOkHttpClient=new OkHttpClient();

            FormBody mFormBody=new FormBody.Builder()
                    .add("account", String.valueOf(account.getText()))
                    .add("password",String.valueOf(password.getText()))
                    .build();

            Request mRequest=new Request.Builder()
                    .url(Utils.generalUrl+"login/")
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
                        if(result.get("authen")!=null)
                        {
                            Toast.makeText(getBaseContext(),String.valueOf(result.get("result")),Toast.LENGTH_SHORT).show();
                            //步骤1：创建一个SharedPreferences对象
                            SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                            //步骤2： 实例化SharedPreferences.Editor对象
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            //步骤3：将获取过来的值放入文件
                            editor.putString("authen", result.get("authen").toString());
                            //步骤4：提交
                            editor.commit();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,String.valueOf(result.get("result")),Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Looper.loop();

                }

                @Override
                public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            });
        }
    }

    private class startrecognListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //创建Intent对象
            Intent intent=new Intent();
            //将参数放入intent
            intent.putExtra("flag", 3);
            //跳转到指定的Activity
            intent.setClass(LoginActivity.this, FdActivity.class);
            //启动Activity
            startActivity(intent);

        }
    }
}
