package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;

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

public class SendCommentFragment  extends Fragment {
    View view;
    EditText editText;
    SharedPreferences sharedPreferences;
    Handler mHandler = new Handler();
    Button sendText;
    public SendCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send_comment, container, false);
        editText = view.findViewById(R.id.ed_comment);
        editText.clearFocus();
        editText.setSelected(false);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        sendText = view.findViewById(R.id.btn_fabu_pl);
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("identity",null).equals("1") || editText.getText().equals(""))
                {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    FormBody mFormBody=new FormBody.Builder().add("classid",sharedPreferences.getString("classid",null)).add("content", String.valueOf(editText.getText())).add("auth",sharedPreferences.getString("auth",null)).build();
                    Request mRequest=new Request.Builder()
                            .url(Utils.generalUrl+"sendClassComment/")
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
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            XToastUtils.toast("发表评论成功");
                                            editText.setText("");
                                            editText.clearFocus();
                                            editText.setSelected(false);
                                        }
                                    }, 0);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//                            XToastUtils.toast("获取课程信息失败");
                        }
                    });
                }
                else
                {
                    if(editText.getText().equals(""))
                        XToastUtils.toast("内容为空");
                    else
                        XToastUtils.toast("教师无法评价课程");
                }

            }
        });

        return view;
    }
}
