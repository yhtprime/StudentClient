package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.dryht.mobile.Adapter.RecycleViewCheckHistoryAdapter;
import com.dryht.mobile.Adapter.RecycleViewCheckInfoAdapter;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xuexiang.xui.widget.actionbar.TitleBar;

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

public class CheckInfoActivity extends AppCompatActivity {
    private com.github.mikephil.charting.charts.PieChart pieChart;
    private TitleBar mTitleBar;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private String checkid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_info);
        pieChart = findViewById(R.id.pieChart);
        mTitleBar = findViewById(R.id.checkinfotitle);
        mTitleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.checkinfo_recycle);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        checkid = getIntent().getStringExtra("checkid");
        mHandler = new Handler();
        setpieChart();

    }

    private void setpieChart() {

        OkHttpClient mOkHttpClient=new OkHttpClient();
        FormBody mFormBody=new FormBody.Builder().add("checkid",checkid).build();
        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"GetClassCheck/")
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
                        Float good = Float.valueOf(result.getString("good"));
                        Float bad = Float.valueOf(result.getString("bad"));
                        Float total = good+bad;
                        final JSONArray finalResult = new JSONArray(result.get("data").toString());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pieChart.setNoDataText("考勤数据统计");
                                List<PieEntry> strings = new ArrayList<>();
                                strings.add(new PieEntry(bad/total*100,"未签"));
                                strings.add(new PieEntry(good/total*100,"已签"));
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
                                RecycleViewCheckInfoAdapter r = new RecycleViewCheckInfoAdapter(CheckInfoActivity.this,finalResult,R.layout.adapter_recycle_view_checkinfo_item);
                                recyclerView.setLayoutManager(new LinearLayoutManager(CheckInfoActivity.this));
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
