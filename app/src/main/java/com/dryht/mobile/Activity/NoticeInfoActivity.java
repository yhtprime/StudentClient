package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dryht.mobile.Adapter.RecycleViewCheckInfoAdapter;
import com.dryht.mobile.Bean.Student;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/*
通知详情页
 */
public class NoticeInfoActivity extends AppCompatActivity {
    private TitleBar titleBar;
    private int infoid;
    private TextView notice_title,notice_teacher,notice_time,noticeintro;
    private Button notice_check_btn;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private Handler mHandler;
    private com.github.mikephil.charting.charts.PieChart pieChart;
    private List<Student>students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_info);
        infoid = getIntent().getIntExtra("infoid",0);
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.noticeinfo_recycle);
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        mHandler = new Handler();
        pieChart = findViewById(R.id.noticeChart);
        initcomponent();
        initTitleBar();
        if (sharedPreferences.getString("identity",null).equals("0"))
        {
            notice_check_btn.setVisibility(View.INVISIBLE);
            gettnoticeinfo();
        }
        else
        {
            pieChart.setVisibility(View.INVISIBLE);
            getnoticeinfo();
        }

    }

    private void gettnoticeinfo() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody= null;
        Request mRequest = null;
        String circommonid = null;
        mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("infoid", String.valueOf(infoid)).build();
        mRequest=new Request.Builder().url(Utils.generalUrl+"gettnoticeinfo/").post(mFormBody).build();
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
                        JSONObject finalResult = new JSONObject(result.get("data").toString());
                        JSONArray stu = new JSONArray(result.get("stu").toString());
                        for (int i = 0; i < stu.length(); i++) {
                            int sid = stu.getJSONObject(i).getInt("sid");
                            String account = stu.getJSONObject(i).getString("account");
                            String name = stu.getJSONObject(i).getString("name");
                            String headpic = stu.getJSONObject(i).getString("headpic");
                            int grade = stu.getJSONObject(i).getInt("grade");
                            int major = stu.getJSONObject(i).getInt("major");
                            int status = stu.getJSONObject(i).getInt("status");
                            String email = stu.getJSONObject(i).getString("email");
                            students.add(new Student(sid,account,null,name,headpic,null,grade,major,email,null,null,status));
                        }
                        JSONObject finalResult1 = result;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Float good = Float.valueOf(finalResult1.getString("good"));
                                    Float bad = Float.valueOf(finalResult1.getString("bad"));
                                    Float total = good+bad;
                                    notice_title.setText(finalResult.getString("title"));
                                    notice_teacher.setText(finalResult.getString("tname"));
                                    notice_time.setText(finalResult.getString("time"));
                                    noticeintro.setText(finalResult.getString("intro"));
                                    pieChart.setNoDataText("考勤数据统计");
                                    List<PieEntry> strings = new ArrayList<>();
                                    strings.add(new PieEntry(bad/total*100,"未阅读"));
                                    strings.add(new PieEntry(good/total*100,"已阅读"));
                                    PieDataSet dataSet = new PieDataSet(strings,"");

                                    ArrayList<Integer> colors = new ArrayList<Integer>();
                                    colors.add(getResources().getColor(R.color.md_red_400));
                                    colors.add(getResources().getColor(R.color.md_green_400));
                                    dataSet.setColors(colors);

                                    PieData pieData = new PieData(dataSet);
                                    pieData.setDrawValues(true);
                                    pieData.setValueFormatter(new PercentFormatter());
                                    pieData.setValueTextSize(12f);

                                    pieChart.setData(pieData);
                                    pieChart.invalidate();

                                    Description description = new Description();
                                    description.setText("");
                                    pieChart.setDescription(description);
                                    pieChart.setHoleRadius(0f);
                                    pieChart.setTransparentCircleRadius(0f);
                                    RecycleViewCheckInfoAdapter r = new RecycleViewCheckInfoAdapter(NoticeInfoActivity.this,students,R.layout.adapter_recycle_view_checkinfo_item);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(NoticeInfoActivity.this));
                                    recyclerView.setAdapter(r);
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

    //获取info详情
    private void getnoticeinfo() {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody= null;
        Request mRequest = null;
        String circommonid = null;
        mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("infoid", String.valueOf(infoid)).build();
        mRequest=new Request.Builder().url(Utils.generalUrl+"getnoticeinfo/").post(mFormBody).build();
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
                        JSONObject finalResult = new JSONObject(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                        notice_title.setText(finalResult.getString("title"));
                                        notice_teacher.setText(finalResult.getString("tname"));
                                        notice_time.setText(finalResult.getString("time"));
                                        noticeintro.setText(finalResult.getString("intro"));
                                        if (finalResult.getString("status").equals("1")){
                                            notice_check_btn.setEnabled(false);
                                            notice_check_btn.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_6));
                                            notice_check_btn.setText("已经提交");
                                        }
                                        else
                                        {
                                            notice_check_btn.setEnabled(true);
                                            notice_check_btn.setBackgroundColor(getResources().getColor(R.color.md_blue_300));
                                            notice_check_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    OkHttpClient mOkHttpClient=new OkHttpClient();
                                                    FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("infoid", String.valueOf(infoid)).build();
                                                    Request mRequest=new Request.Builder()
                                                            .url(Utils.generalUrl+"checknotice/")
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

                                                                    final String finalResult = new String(result.get("data").toString());
                                                                    mHandler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                                if (finalResult.equals("1"))
                                                                                {
                                                                                    notice_check_btn.setEnabled(false);
                                                                                    notice_check_btn.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_6));
                                                                                    notice_check_btn.setText("已经提交");
                                                                                }
                                                                        }
                                                                    },0);

                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        @Override
                                                        public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                                            XToastUtils.toast("获取新闻信息失败");
                                                        }
                                                    });
                                                }
                                            });
                                        }
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
    //初始化titlebar
    private void initTitleBar() {
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initcomponent() {
        titleBar = findViewById(R.id.noticeinfotitle);
        notice_title = findViewById(R.id.notice_title);
        notice_teacher = findViewById(R.id.notice_teacher);
        notice_time = findViewById(R.id.notice_time);
        noticeintro = findViewById(R.id.noticeintro);
        notice_check_btn = findViewById(R.id.notice_check_btn);
    }
}
