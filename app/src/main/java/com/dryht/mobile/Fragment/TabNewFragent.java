package com.dryht.mobile.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Activity.NewInfoActivity;
import com.dryht.mobile.Activity.TabNewActivity;
import com.dryht.mobile.Adapter.RecycleViewNewAdapter;
import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.OnRecyclerItemClickListener;
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

public class TabNewFragent extends Fragment {
    View view;
    String categoryid;
    Handler mHandler = new Handler();
    RecyclerView recyclerView;
    public TabNewFragent(String categoryid) {
        this.categoryid= categoryid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tabnew, container, false);
        recyclerView = view.findViewById(R.id.newsrecycle);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("categoryid",categoryid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getNewsList/")
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
                                RecycleViewNewAdapter r = new RecycleViewNewAdapter(getContext(),finalResult,R.layout.adapter_recycle_new_list_item);
                                r.setRecyclerItemClickListener(new OnRecyclerItemClickListener() {
                                    @Override
                                    public void onItemClick(int Position) {
                                        Intent intent = new Intent(getContext(), NewInfoActivity.class);
                                        try {
                                            intent.putExtra("newid",finalResult.getJSONObject(Position).get("newid").toString());
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
}
