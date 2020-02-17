package com.dryht.mobile.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.Fragment.SendCommentFragment;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Lesson;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.grouplist.XUICommonListItemView;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.devmeteor.tableview.LessonView;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClassInfoActivity extends AppCompatActivity {
    private XUIGroupListView mGroupListView;
    private TitleBar mTitleBar;
    private RefreshLayout refreshLayout;
    private SharedPreferences sharedPreferences;
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RecyclerView recyclerView;
    private Handler mHandler;
    private String classid;
    public ClassInfoActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_info);
        classid = getIntent().getStringExtra("classid");
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("classid",classid).commit();
        mGroupListView = findViewById(R.id.classGroupListView);
        refreshLayout = (RefreshLayout)findViewById(R.id.classinforefreshLayout);
        mTitleBar = findViewById(R.id.classinfotitle);
        recyclerView = findViewById(R.id.classcomments);
        refreshLayout.setOnRefreshListener(new refreshListener());
        refreshLayout.autoRefresh();
        mHandler = new Handler();
        //获取管理者
        supportFragmentManager = getSupportFragmentManager();
        //开启事务
        fragmentTransaction =  supportFragmentManager.beginTransaction();
        //添加底部导航上去
        fragmentTransaction.add(R.id.sendclasscomment, new SendCommentFragment()).commit();
        initTitleBar();
        initGroupListView();
    }
    //获取课程评论
    private void getclassComment() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("classid",classid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getClassComment/")
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
                                RecyclerViewCommentAdapter r = new RecyclerViewCommentAdapter(ClassInfoActivity.this,finalResult,R.layout.adapter_recycle_view_comment_item);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ClassInfoActivity.this));
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


    private void initTitleBar() {
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).setCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XToastUtils.toast("点击标题");
            }
        }).addAction(new TitleBar.ImageAction(R.drawable.ic_add_white_24dp) {
            @Override
            public void performAction(View view) {
                XToastUtils.toast("点击更多！");
            }
        });
    }
    private void initGroupListView() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("classid",classid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getClassInfo/")
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
                        result = new JSONObject(result.get("data").toString());
                        //挂起
                        final JSONObject finalResult = result;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //把信息赋值
                                XUICommonListItemView classname = null;
                                try {
                                classname = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "班级名称",
                                        finalResult.get("name").toString(),
                                        XUICommonListItemView.HORIZONTAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                XUICommonListItemView intro = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "班级介绍",
                                        finalResult.get("intro").toString(),
                                        XUICommonListItemView.VERTICAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                XUICommonListItemView teacher = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "任课教师",
                                        finalResult.get("tname").toString(),
                                        XUICommonListItemView.HORIZONTAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                XUICommonListItemView place = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "上课地点",
                                        finalResult.get("place").toString(),
                                        XUICommonListItemView.HORIZONTAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                XUICommonListItemView weekday = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "上课日",
                                        finalResult.get("weekday").toString(),
                                        XUICommonListItemView.HORIZONTAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                weekday.setOrientation(XUICommonListItemView.VERTICAL);
                                XUICommonListItemView week = mGroupListView.createItemView(
                                        ContextCompat.getDrawable(ClassInfoActivity.this, R.mipmap.ic_launcher),
                                        "周数",
                                        finalResult.get("week").toString(),
                                        XUICommonListItemView.HORIZONTAL,
                                        XUICommonListItemView.ACCESSORY_TYPE_NONE);
                                weekday.setOrientation(XUICommonListItemView.VERTICAL);
                                View.OnClickListener onClickListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (v instanceof XUICommonListItemView) {
                                            CharSequence text = ((XUICommonListItemView) v).getText();
                                            XToastUtils.toast(text + " is Clicked");
                                        }
                                    }
                                };
                                int size = DensityUtils.dp2px(ClassInfoActivity.this, 20);
                                XUIGroupListView.newSection(ClassInfoActivity.this)
                                        .setTitle("班级介绍")
                                        .setDescription("班级评论")
                                        .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .addItemView(classname, onClickListener)
                                        .addItemView(teacher, onClickListener)
                                        .addItemView(intro, onClickListener)
                                        .addItemView(place, onClickListener)
                                        .addItemView(weekday, onClickListener)
                                        .addItemView(week, onClickListener)
                                        // .addItemView(itemWithSwitch, onClickListener)
                                        .addTo(mGroupListView);
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
                XToastUtils.toast("获取课程信息失败");
            }
        });
    }

    private class refreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    getclassComment();
                    //结束加载
                    refreshLayout.finishRefresh();
                    //加载失败的话3秒后结束加载
                    refreshLayout.finishRefresh(3000);
                    Looper.loop();
                }
            },2000);

        }
    }
}
