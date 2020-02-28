package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Adapter.FriendShareAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Lesson;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.devmeteor.tableview.LessonView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendListFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    View view;
    private int flag;
    private RecyclerView recyclerView;
    private Handler mHandler;
    public FriendListFragment(int flag) {
        // Required empty public constructor
        this.flag = flag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        recyclerView = view.findViewById(R.id.friendshare);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //朋友圈动态
        OkHttpClient mOkHttpClient=new OkHttpClient();

        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest = null;
        if(flag==0){
            mRequest=new Request.Builder().url(Utils.generalUrl+"getfriendshare/").post(mFormBody).build();
        }
        else if(flag==1) {
            mRequest=new Request.Builder().url(Utils.generalUrl+"getmyshare/").post(mFormBody).build();
        }
        if (flag==2)
        {
            mFormBody=new FormBody.Builder().add("id",sharedPreferences.getString("id",null)).add("auth",sharedPreferences.getString("auth",null)).add("iden",sharedPreferences.getString("iden",null)).add("identity",sharedPreferences.getString("identity",null)).build();
            mRequest=new Request.Builder().url(Utils.generalUrl+"getmyshare/").post(mFormBody).build();
        }
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
                        final JSONArray jsonArray = new JSONArray(result.get("data").toString());
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FriendShareAdapter friendShareAdapter = new FriendShareAdapter(getContext(),jsonArray,R.layout.adapter_recycle_view_friend_item,mHandler);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(friendShareAdapter);
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