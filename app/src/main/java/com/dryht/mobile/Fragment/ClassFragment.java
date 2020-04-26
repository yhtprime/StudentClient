package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.annotation.GlideModule;
import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Adapter.ListViewSearchClassAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dryht.mobile.Bean.Lesson;

import cn.devmeteor.tableview.LessonView;
import cn.devmeteor.tableview.TableView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@GlideModule
public class ClassFragment extends Fragment {
    private View view;
    private TableView tableView;
    private SharedPreferences sharedPreferences;
    private JSONArray jsonArray;
    private Handler mHandler;
    private List<Lesson> lessons;
    private List<Lesson> searchlessons;
    private androidx.appcompat.widget.SearchView mSearchView;
    private ListView listView;
    public ClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_class, container, false);
        tableView=view.findViewById(R.id.main_table);
        mSearchView = view.findViewById(R.id.search_view);
        listView = view.findViewById(R.id.search_classList);
        mHandler = new Handler();
        initSearchView();
        if(sharedPreferences.getString("alllesson",null)==null)
            getLessons();
        else
        {
            try {
                jsonArray = new JSONArray( sharedPreferences.getString("lessons",null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lessons=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    lessons.add(new Lesson(jsonArray.getJSONObject(i).get("classid").toString(),null,jsonArray.getJSONObject(i).get("name").toString(),jsonArray.getJSONObject(i).get("weekday").toString(),Integer.parseInt(jsonArray.getJSONObject(i).get("time").toString()),Integer.parseInt(jsonArray.getJSONObject(i).get("count").toString()),jsonArray.getJSONObject(i).get("place").toString(),jsonArray.getJSONObject(i).get("tname").toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            tableView.setLessons(lessons,getBgMap(), new LessonView.LessonClickListener() {
                @Override
                public void onClick(cn.devmeteor.tableview.Lesson lesson) {
                    Toast.makeText(getContext(),lesson.toString(),Toast.LENGTH_LONG).show();
                }

            });
        }
        return view;
    }



    private void initSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(sharedPreferences.getString("alllesson",null)==null)
                {
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    Request mRequest=new Request.Builder()
                            .url(Utils.generalUrl+"getAllClass/")
                            .get()
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
                                    sharedPreferences.edit().putString("alllesson",result.get("data").toString());
                                    searchlessons=new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            if(jsonArray.getJSONObject(i).get("name").toString().indexOf(newText)>=0&&!newText.equals(""))
                                                searchlessons.add(new Lesson(jsonArray.getJSONObject(i).get("classid").toString(),null,jsonArray.getJSONObject(i).get("name").toString(),jsonArray.getJSONObject(i).get("weekday").toString(),Integer.parseInt(jsonArray.getJSONObject(i).get("time").toString()),Integer.parseInt(jsonArray.getJSONObject(i).get("count").toString()),jsonArray.getJSONObject(i).get("place").toString(),jsonArray.getJSONObject(i).get("tname").toString()));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //挂起
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ListViewSearchClassAdapter adapter = new ListViewSearchClassAdapter(getContext(),R.layout.adapter_list_view_search_item,searchlessons);
                                            listView.setAdapter(adapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                        int position, long id) {
                                                    Lesson lesson= searchlessons.get(position);
                                                    XToastUtils.toast(lesson.getName());
                                                    Intent intent = new Intent(getContext(), ClassInfoActivity.class);
                                                    intent.putExtra("classid",lesson.getTerm());
                                                    startActivity(intent);
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
                }
                else
                {
                    try {
                        jsonArray = new JSONArray(sharedPreferences.getString("alllesson",null));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    searchlessons=new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if(jsonArray.getJSONObject(i).get("name").toString().indexOf(newText)>=0&&!newText.equals(""))
                                searchlessons.add(new Lesson(jsonArray.getJSONObject(i).get("classid").toString(),null,jsonArray.getJSONObject(i).get("name").toString(),jsonArray.getJSONObject(i).get("weekday").toString(),Integer.parseInt(jsonArray.getJSONObject(i).get("time").toString()),Integer.parseInt(jsonArray.getJSONObject(i).get("count").toString()),jsonArray.getJSONObject(i).get("place").toString(),jsonArray.getJSONObject(i).get("tname").toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //挂起
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ListViewSearchClassAdapter adapter = new ListViewSearchClassAdapter(getContext(),R.layout.adapter_list_view_search_item,searchlessons);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Lesson lesson= searchlessons.get(position);
                                    XToastUtils.toast(lesson.getName());
                                    Intent intent = new Intent(getContext(), ClassInfoActivity.class);
                                    intent.putExtra("classid",lesson.getTerm());
                                    startActivity(intent);
                                }
                            });
                        }
                    }, 0);
                }
                return false;
            }
        });
    }


    //bgMap示例
    private Map<String,Integer> getBgMap(){
        Map<String,Integer> bgMap=new HashMap<>();
        Utils randcolor = new Utils();
        for (int i = 0; i < lessons.size(); i++) {
            bgMap.put(lessons.get(i).getName(),randcolor.createRandomColor());
        }
        return bgMap;
    }

    //模拟生成课程数据，自定义课程bean可直接替换
    private void getLessons(){
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getPClass/")
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
                        System.out.println("**************************************");
                        jsonArray = new JSONArray(result.get("data").toString());
                        sharedPreferences.edit().putString("lessons",result.get("data").toString());
                        lessons=new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                lessons.add(new Lesson(jsonArray.getJSONObject(i).get("classid").toString(),null,jsonArray.getJSONObject(i).get("name").toString(),jsonArray.getJSONObject(i).get("weekday").toString(),Integer.parseInt(jsonArray.getJSONObject(i).get("time").toString()),Integer.parseInt(jsonArray.getJSONObject(i).get("count").toString()),jsonArray.getJSONObject(i).get("place").toString(),jsonArray.getJSONObject(i).get("tname").toString()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(jsonArray);
                        System.out.println("**************************************");
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tableView.setLessons(lessons,getBgMap(), new LessonView.LessonClickListener() {
                                    @Override
                                    public void onClick(cn.devmeteor.tableview.Lesson lesson) {
                                        Toast.makeText(getContext(),lesson.toString(),Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getContext(), ClassInfoActivity.class);
                                        intent.putExtra("classid",lesson.getTerm());
                                        startActivity(intent);
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
                XToastUtils.toast("获取课程表失败");
            }
        });
    }
}
