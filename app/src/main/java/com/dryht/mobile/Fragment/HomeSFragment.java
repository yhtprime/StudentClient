package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dryht.mobile.Activity.FdActivity;

import com.dryht.mobile.Adapter.RecyclerViewBannerAdapter;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.button.RippleView;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
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

public class HomeSFragment extends Fragment {
    private View view;
    private TextView class_place;
    private TextView time;
    private TextView course_name;
    private TextView teacher_name;
    private TextView temp;
    private TextView intro;
    private TextView pm;
    private TextView temp_advice;
    private MarqueeTextView mTvMarquee;
    private BannerLayout bannerLayout;
    private TextView banner_title;
    private RippleView checkbtn;
    private TextView checkbtntitle;
    private ImageView startrecogn;
    private JSONArray Top5News = null;
    private RecyclerViewBannerAdapter mAdapterHorizontal;
    private SharedPreferences sharedPreferences;

    public HomeSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponent() {
        class_place = view.findViewById(R.id.class_place);
        time = view.findViewById(R.id.time);
        course_name = view.findViewById(R.id.course_name);
        teacher_name = view.findViewById(R.id.teacher_name);
        temp = view.findViewById(R.id.temp);
        intro = view.findViewById(R.id.intro);
        pm = view.findViewById(R.id.pm);
        temp_advice = view.findViewById(R.id.temp_advice);
        mTvMarquee = view.findViewById(R.id.tv_marquee);
        bannerLayout = view.findViewById(R.id.home_banner);
        banner_title = view.findViewById(R.id.home_banner_title);
        checkbtn = view.findViewById(R.id.check_btn);
        checkbtntitle = view.findViewById(R.id.check_btn_text);
        checkbtntitle.setOnClickListener(new startcheckListener());
        startrecogn = view.findViewById(R.id.startrecogn);
        startrecogn.setOnClickListener(new startrecognListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        initComponent();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //轮播图
        if (sharedPreferences.getString("Top5News","").equals(""))
            getTop5News();
        else{
            try {
                String[] urls = new String[0];
                Top5News = new JSONArray(sharedPreferences.getString("Top5News","").toString());
                urls = new String[]{
                        Top5News.getJSONObject(0).get("pic").toString(),
                        Top5News.getJSONObject(1).get("pic").toString(),
                        Top5News.getJSONObject(2).get("pic").toString(),
                        Top5News.getJSONObject(3).get("pic").toString(),
                        Top5News.getJSONObject(4).get("pic").toString(),
                };
                bannerLayout.setAdapter(mAdapterHorizontal = new RecyclerViewBannerAdapter(urls));
                bannerLayout.setAutoPlaying(true);
                banner_title.setText(Top5News.getJSONObject(0).get("name").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getWeather();



        bannerLayout.setOnIndicatorIndexChangedListener(new BannerLayout.OnIndicatorIndexChangedListener() {
            @Override
            public void onIndexChanged(int position) {
                try {
                    banner_title.setText( Top5News.getJSONObject(position).get("name").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mTvMarquee.addDisplayString("《《《欢迎使用校园小助手，更多校园信息请点击这里》》》");
        mTvMarquee.setSpeed(7);
        mTvMarquee.startRoll();
        //签到按钮
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //设置人脸
    private class startrecognListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //创建Intent对象
            Intent intent=new Intent();
            //将参数放入intent
            intent.putExtra("flag", 3);
            //跳转到指定的Activity
            intent.setClass(getContext(), FdActivity.class);
            //启动Activity
            startActivity(intent);

        }
    }

    //考勤
    private class startcheckListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //创建Intent对象
            Intent intent=new Intent();
            //将参数放入intent
            intent.putExtra("flag", 1);
            //跳转到指定的Activity
            intent.setClass(getContext(), FdActivity.class);
            //启动Activity
            startActivity(intent);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //获取前5个新闻
    private void getTop5News(){
        OkHttpClient mOkHttpClient=new OkHttpClient();

        FormBody mFormBody=new FormBody.Builder().build();

        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getTop5News/")
                .get()
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
                        Top5News = new JSONArray(result.get("data").toString());
                        //步骤2： 实例化SharedPreferences.Editor对象
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        //步骤3：将获取过来的值放入文件
                        editor.putString("Top5News",result.get("data").toString());
                        //步骤4：提交
                        editor.commit();
                        String[] urls = new String[0];
                        urls = new String[]{
                                Top5News.getJSONObject(0).get("pic").toString(),
                                Top5News.getJSONObject(1).get("pic").toString(),
                                Top5News.getJSONObject(2).get("pic").toString(),
                                Top5News.getJSONObject(3).get("pic").toString(),
                                Top5News.getJSONObject(4).get("pic").toString(),
                        };
                        bannerLayout.setAdapter(mAdapterHorizontal = new RecyclerViewBannerAdapter(urls));
                        bannerLayout.setAutoPlaying(true);
                        banner_title.setText(Top5News.getJSONObject(0).get("name").toString());
                    }
                    else
                    {
                        XToastUtils.toast("获取新闻列表失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Looper.loop();

            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//                Looper.prepare();
                XToastUtils.toast("获取新闻列表失败");
//                Looper.loop();
            }
        });
    }

    //获取天气情况
    private void getWeather(){
        OkHttpClient mOkHttpClient=new OkHttpClient();

        FormBody mFormBody=new FormBody.Builder().build();

        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"getWeather/")
                .get()
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
                        result = new JSONObject(result.get("data").toString());
                        System.out.println("***************************");
                        System.out.println(result);
                        System.out.println("***************************");
                        temp.setText(result.get("temp").toString());
                        intro.setText("详细天气情况："+result.get("intro").toString());
                        pm.setText("pm2.5指数："+result.get("pm").toString());
                        String pmadvice = null;
                        String tempadvice = null;
                        if(Integer.parseInt(result.get("pm").toString())>50)
                            pmadvice = "要记得戴口罩哦 ";
                        else
                            pmadvice = "今天天气质量不错哟 ";
                        if (Integer.parseInt(result.get("temp").toString())<10)
                            tempadvice = "要注意保暖";
                        else
                            tempadvice = "温度适宜";
                        temp_advice.setText(tempadvice+pmadvice);
                    }
                    else
                    {
                        XToastUtils.toast("获取天气失败");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Looper.loop();

            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//                Looper.prepare();
                XToastUtils.toast("获取天气失败");
//                Looper.loop();
            }
        });
    }

    //获取上课考勤情况
    private void getCheckState(){

    }




}
