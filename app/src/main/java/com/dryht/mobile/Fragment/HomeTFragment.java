package com.dryht.mobile.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dryht.mobile.Activity.CheckHistoryActivity;
import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Activity.FdActivity;

import com.dryht.mobile.Activity.MainActivity;
import com.dryht.mobile.Activity.NoticeInfoActivity;
import com.dryht.mobile.Activity.TabNewActivity;
import com.dryht.mobile.Adapter.RecycleViewCheckInfoAdapter;
import com.dryht.mobile.Adapter.RecyclerViewBannerAdapter;

import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.Bean.Check;
import com.dryht.mobile.Bean.Class;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.TokenUtils;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.button.RippleView;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeTFragment extends Fragment implements View.OnClickListener {
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
    private RippleView checkbtn,check_btn1;
    private TextView check_btn_text;
    private Handler mHandler;
    private JSONArray Top5News = null;
    private TitleBar titleBar;
    private RecyclerViewBannerAdapter mAdapterHorizontal;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout constraintLayout;
    private TextView moreviews;
    private ImageView weather;
    private Check check;
    private Class aClass;
    public HomeTFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    private void initComponent() {
        class_place = view.findViewById(R.id.class_placet);
        time = view.findViewById(R.id.comment_timet);
        course_name = view.findViewById(R.id.course_namet);
        temp = view.findViewById(R.id.temp);
        intro = view.findViewById(R.id.comment_intro);
        pm = view.findViewById(R.id.pm);
        temp_advice = view.findViewById(R.id.temp_advice);
        titleBar = view.findViewById(R.id.home_titlebar);
        moreviews = view.findViewById(R.id.morenews);
        bannerLayout = view.findViewById(R.id.home_banner);
        weather = view.findViewById(R.id.weather);
        banner_title = view.findViewById(R.id.home_banner_title);
        checkbtn = view.findViewById(R.id.check_btn2);
        check_btn1 = view.findViewById(R.id.check_btn1);
        check_btn_text = view.findViewById(R.id.check_btn_textt);
        constraintLayout = view.findViewById(R.id.check_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homet, container, false);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        initComponent();
        check_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CheckHistoryActivity.class);
                startActivity(intent);
            }
        });
        titleBar.setBackground(getResources().getDrawable(R.color.thiscolor));
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mSlidingRootNav.openMenu();
            }
        });
        moreviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TabNewActivity.class);
                startActivity(intent);
            }
        });
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
        getInstantClass();
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



    }
    //获取即将上课的课程信息
    private void getInstantClass() {
        OkHttpClient mOkHttpClient=new OkHttpClient();

        FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).build();

        Request mRequest=new Request.Builder()
                .url(Utils.generalUrl+"GetTInstantClass/")
                .post(mFormBody)
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
                        result = (JSONObject) result.get("data");
                        final JSONObject finalResult = result;
                        aClass = new Class(finalResult.getString("classid"),finalResult.getString("place"),finalResult.getString("name"),finalResult.getString("time"));
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    class_place.setText(aClass.getPlace());
                                    course_name.setText(aClass.getName());
                                    time.setText(aClass.getTime());
                                    if (aClass.getClassid()==0)
                                    {
                                        checkbtn.setEnabled(false);
                                        check_btn_text.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_3));
                                        check_btn_text.setText("今天没有课程");
                                    }
                            }
                        }, 0);
                        if (!(aClass.getClassid()==0))
                        {
                            OkHttpClient mOkHttpClient=new OkHttpClient();
                            FormBody mFormBody=new FormBody.Builder().add("identity",sharedPreferences.getString("identity",null)).add("auth",sharedPreferences.getString("auth",null)).add("classid",finalResult.getString("classid")).build();
                            Request mRequest=new Request.Builder()
                                    .url(Utils.generalUrl+"getTeacherCheck/")
                                    .post(mFormBody)
                                    .build();
                            mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
                                @Override
                                public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                                    Looper.prepare();
                                    //String转JSONObject
                                    JSONObject result = null;
                                    try {
                                        result = new JSONObject(response.body().string());
                                        check = new Check(Integer.parseInt(result.get("status").toString()),result.get("result").toString());
                                        //没有开启考勤
                                        if(check.getStatus()==0) {
                                            mHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    checkbtn.setEnabled(true);
                                                    check_btn_text.setBackgroundColor(getResources().getColor(R.color.md_green_400));
                                                    check_btn_text.setText("您可以开启考勤");
                                                    checkbtn.setOnClickListener(v->{
                                                        DialogLoader.getInstance().showConfirmDialog(
                                                                getContext(),
                                                                getString(R.string.lab_check_confirm),
                                                                getString(R.string.lab_yes),
                                                                new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        OkHttpClient mOkHttpClient=new OkHttpClient();
                                                                        FormBody mFormBody= null;
                                                                        Request mRequest = null;
                                                                        mFormBody = new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("identity",sharedPreferences.getString("identity",null)).add("classid", String.valueOf(aClass.getClassid())).build();
                                                                        mRequest=new Request.Builder().url(Utils.generalUrl+"startCheck/").post(mFormBody).build();
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
                                                                                                checkbtn.setEnabled(false);
                                                                                                check_btn_text.setBackgroundColor
                                                                                                        (getResources().getColor(R.color.xui_config_color_gray_3));
                                                                                                check_btn_text.setText("您已经开启了考勤");
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
                                                                },
                                                                getString(R.string.lab_no),
                                                                new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                        );
                                                    });
                                                }
                                            }, 0);
                                        }
                                        //需要考勤
                                        else if(check.getStatus()==1) {
                                            mHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    checkbtn.setEnabled(false);
                                                    check_btn_text.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_3));
                                                    check_btn_text.setText("您已经开启了考勤");
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

                    };

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//                Looper.prepare();
                XToastUtils.toast("失败");
//                Looper.loop();
            }
        });
    }

    @Override
    public void onClick(View v) {

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
                        final String[] finalUrls = urls;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bannerLayout.setAdapter(mAdapterHorizontal = new RecyclerViewBannerAdapter(finalUrls));
                                bannerLayout.setAutoPlaying(true);
                                try {
                                    banner_title.setText(Top5News.getJSONObject(0).get("name").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 0);
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
                        //挂起
                        final JSONObject finalResult = result;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    temp.setText(finalResult.get("temp").toString()+"°C");
                                    intro.setText(finalResult.get("intro").toString());
                                    pm.setText("  pm指数："+ finalResult.get("pm").toString());
                                    String pmadvice = null;
                                    String tempadvice = null;
                                    if(Integer.parseInt(finalResult.get("pm").toString())>50)
                                        pmadvice = "要记得戴口罩哦 ";
                                    else
                                        pmadvice = "今天天气质量不错哟 ";
                                    if (Integer.parseInt(finalResult.get("temp").toString())<10)
                                        tempadvice = "要注意保暖";
                                    else
                                        tempadvice = "温度适宜";
                                    temp_advice.setText(tempadvice+pmadvice);
                                    if (finalResult.get("weather").toString().equals("多云"))
                                    {
                                        weather.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloudy));
                                    }
                                    else if(finalResult.get("weather").toString().equals("晴"))
                                    {
                                        weather.setImageDrawable(getResources().getDrawable(R.drawable.ic_sunny));
                                    }
                                    else if(finalResult.get("weather").toString().equals("阴"))
                                    {
                                        weather.setImageDrawable(getResources().getDrawable(R.drawable.ic_cloudy));
                                    }
                                    else if(finalResult.get("weather").toString().equals("雨"))
                                    {
                                        weather.setImageDrawable(getResources().getDrawable(R.drawable.ic_rain));
                                    }
                                    else if(finalResult.get("weather").toString().equals("雪"))
                                    {
                                        weather.setImageDrawable(getResources().getDrawable(R.drawable.ic_snow));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, 0);

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
