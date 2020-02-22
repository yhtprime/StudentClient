package com.dryht.mobile.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.dryht.mobile.Activity.FdActivity;
import com.dryht.mobile.Activity.FriendSendActivity;
import com.dryht.mobile.Activity.MainActivity;
import com.dryht.mobile.Adapter.FriendAdapter;
import com.dryht.mobile.Adapter.FriendShareAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.devmeteor.tableview.TableView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FriendFragment extends Fragment {
    View view;
    private ViewPager viewPager;
    List<Fragment> mFragments;
    private TabLayout tableView;
    private RefreshLayout refreshLayout;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    private LinearLayout linearLayout;
    private ImageView friend_background,friend_icon;
    private TextView friend_text_name;
    private List<LocalMedia> mSelectList = new ArrayList<>();
    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        viewPager = view.findViewById(R.id.friend_pager);
        tableView = view.findViewById(R.id.friend_tab_layout);
        friend_background = view.findViewById(R.id.friend_background);
        friend_icon = view.findViewById(R.id.friend_icon);
        friend_text_name = view.findViewById(R.id.friend_text_name);
        refreshLayout = (RefreshLayout)view.findViewById(R.id.friendinforefreshLayout);
        linearLayout = view.findViewById(R.id.friend_post);
        //设置点击时间跳转到发送朋友圈
        linearLayout.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), FriendSendActivity.class);
            startActivity(intent);
        });
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        mHandler = new Handler();
        refreshLayout.setOnRefreshListener(new refreshListener());
        refreshLayout.autoRefresh();
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
                    getPersonProfile();
                    setupViewPager();
                    //结束加载
                    refreshLayout.finishRefresh();
                    //加载失败的话3秒后结束加载
                    refreshLayout.finishRefresh(3000);
                    Looper.loop();
                }
            },2000);

        }
    }

    private void getPersonProfile(){
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).build();
        Request mRequest;
        mRequest=new Request.Builder().url(Utils.generalUrl+"getPersonProfile/").post(mFormBody).build();
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
                        final JSONObject jsonObject = new JSONObject(result.get("data").toString());
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(jsonObject.get("background").toString().indexOf("http")>=0)
                                        Picasso.with(getContext()).load(jsonObject.getString("background")).into(friend_background);
                                    friend_background.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Utils.getPictureSelector(FriendFragment.this)
                                                    .maxSelectNum(1)
                                                    .forResult(PictureConfig.CHOOSE_REQUEST);
                                        }
                                    });
                                    Picasso.with(getContext()).load(jsonObject.getString("pic")).into(friend_icon);
                                    friend_text_name.setText(jsonObject.getString("name"));
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择
                    mSelectList = PictureSelector.obtainMultipleResult(data);
                    OkHttpClient mOkHttpClient=new OkHttpClient();
                    MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
                    for (int i = 0;i<1;i++) {
                        //截取文件名
                        String name =  mSelectList.get(0).getPath().substring(mSelectList.get(0).getPath().lastIndexOf(".")-1);
                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), new File(String.valueOf(mSelectList.get(0).getPath())));
                        builder.addFormDataPart("img",name,fileBody);
                        builder.addFormDataPart("pic",name);
                    }
                    builder.addFormDataPart("auth",sharedPreferences.getString("auth","0"));
                    builder.addFormDataPart("identity",sharedPreferences.getString("identity","0"));
                    Request mRequest=new Request.Builder()
                            .url(com.dryht.mobile.Util.Utils.generalUrl+"setbackground/")
                            .post(builder.build())
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
                                    final JSONObject jsonObject = new JSONObject(result.get("data").toString());
                                    //挂起
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(jsonObject.get("background").toString().indexOf("http")>=0)
                                                    Picasso.with(getContext()).load(jsonObject.getString("background")).into(friend_background);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 0);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Looper.loop();

                        }

                        @Override
                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                            Looper.prepare();
                            Toast.makeText(getContext(),"背景替换失败",Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
    //设置fragemnt给viewpager
    private void setupViewPager() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
            mFragments = new ArrayList<>();
            mFragments.add(new FriendListFragment(0));
            mFragments.add(new FriendListFragment(1));
            String [] titile = {"圈内动态","我的动态"};
            // 第二步：为ViewPager设置适配器
            FriendAdapter friendAdapter = new FriendAdapter(getContext(), mFragments, getChildFragmentManager(),titile);

            viewPager.setAdapter(friendAdapter);
            //  第三步：将ViewPager与TableLayout 绑定在一起
            tableView.setupWithViewPager(viewPager);
            }
        }, 0);
    }


}
