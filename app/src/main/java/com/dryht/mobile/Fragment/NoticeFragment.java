package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Adapter.ListViewSearchClassAdapter;
import com.dryht.mobile.Adapter.RecycleViewNoticeAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Bean.Lesson;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NoticeFragment extends Fragment{
    View view;
    private SharedPreferences sharedPreferences;
    private JSONArray jsonArray;
    private Handler mHandler;
    private List<Lesson> lessons;
    private List<Lesson> searchlessons;
    private ListView listView;
    private androidx.appcompat.widget.SearchView mSearchView;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;

    public NoticeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, container, false);
        mSearchView = view.findViewById(R.id.notice_search_view);
        listView = view.findViewById(R.id.search_noticeList);
        recyclerView = view.findViewById(R.id.notice_recycleview);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.noticefreshLayout);
        refreshLayout.setOnRefreshListener(new refreshListener());
        refreshLayout.autoRefresh();
        initSearchView();
        return view;
    }
    //刷新页面
    private class refreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    getAllNotice();
                    //结束加载
                    refreshLayout.finishRefresh();
                    //加载失败的话3秒后结束加载
                    refreshLayout.finishRefresh(3000);
                    Looper.loop();
                }
            },2000);

        }
    }
    //获取所有信息
    private void getAllNotice() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getallnotice/")
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
                        jsonArray = new JSONArray(result.get("data").toString());
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RecycleViewNoticeAdapter adapter = new RecycleViewNoticeAdapter(getContext(),jsonArray,R.layout.adapter_recycle_view_notice_item);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                            }
                        }, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                XToastUtils.toast("失败");
            }
        });
    }

    private void initSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                XToastUtils.toast("点击了" + query);
                //点击搜索
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //检索课程
                    OkHttpClient mOkHttpClient = new OkHttpClient();
                    Request mRequest = new Request.Builder()
                            .url(Utils.generalUrl + "getAllClass/")
                            .get()
                            .build();

                    mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                            //String转JSONObject
                            JSONObject result = null;
                            try {
                                result = new JSONObject(response.body().string());
                                //取数据
                                if (result.get("status").equals("1")) {
                                    jsonArray = new JSONArray(result.get("data").toString());
                                    searchlessons = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            if (jsonArray.getJSONObject(i).get("name").toString().indexOf(newText) >= 0 && !newText.equals(""))
                                                searchlessons.add(new Lesson(jsonArray.getJSONObject(i).get("classid").toString(), null, jsonArray.getJSONObject(i).get("name").toString(), jsonArray.getJSONObject(i).get("weekday").toString(), Integer.parseInt(jsonArray.getJSONObject(i).get("time").toString()), Integer.parseInt(jsonArray.getJSONObject(i).get("count").toString()), jsonArray.getJSONObject(i).get("place").toString(), jsonArray.getJSONObject(i).get("tname").toString()));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //挂起
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ListViewSearchClassAdapter adapter = new ListViewSearchClassAdapter(getContext(), R.layout.adapter_list_view_search_item, searchlessons);
                                            listView.setAdapter(adapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                        int position, long id) {
                                                    mSearchView.setQuery("",false);
                                                    mSearchView.clearFocus();
                                                    Lesson lesson = searchlessons.get(position);
                                                    //获取课程的通知
                                                    OkHttpClient mOkHttpClient=new OkHttpClient();
                                                    FormBody mFormBody=new FormBody.Builder().add("classid",lesson.getTerm()).add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
                                                    Request mRequest=new Request.Builder()
                                                            .url(Utils.generalUrl+"getclassnotice/")
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
                                                                    jsonArray = new JSONArray(result.get("data").toString());
                                                                    //挂起
                                                                    mHandler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            RecycleViewNoticeAdapter adapter = new RecycleViewNoticeAdapter(getContext(),jsonArray,R.layout.adapter_recycle_view_notice_item);
                                                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                            recyclerView.setAdapter(adapter);
                                                                        }
                                                                    }, 0);
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        @Override
                                                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                                            XToastUtils.toast("获取所有课程表信息失败");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                            XToastUtils.toast("获取所有课程表信息失败");
                        }
                    });
                  return false;
                }
        });
    }
}
