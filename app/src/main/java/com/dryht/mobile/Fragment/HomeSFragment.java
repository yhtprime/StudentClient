package com.dryht.mobile.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dryht.mobile.Activity.ClassInfoActivity;
import com.dryht.mobile.Activity.FdActivity;

import com.dryht.mobile.Activity.MainActivity;
import com.dryht.mobile.Activity.TabNewActivity;
import com.dryht.mobile.Adapter.RecyclerViewBannerAdapter;

import com.dryht.mobile.Adapter.RecyclerViewCommentAdapter;
import com.dryht.mobile.Bean.Check;
import com.dryht.mobile.Bean.Class;
import com.dryht.mobile.Bean.local;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.Utils;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.button.RippleView;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeSFragment extends Fragment implements View.OnClickListener {
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
    private TextView check_btn_text;
    private Handler mHandler;
    private JSONArray Top5News = null;
    private TitleBar titleBar;
    private RecyclerViewBannerAdapter mAdapterHorizontal;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout constraintLayout;
    private TextView moreviews;
    private ImageView weather;
    private local location;
    private Check check;
    private Class aClass;
    public HomeSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    private void initComponent() {
        class_place = view.findViewById(R.id.class_place);
        time = view.findViewById(R.id.comment_time);
        course_name = view.findViewById(R.id.course_name);
        teacher_name = view.findViewById(R.id.teacher_name);
        temp = view.findViewById(R.id.temp);
        intro = view.findViewById(R.id.comment_intro);
        pm = view.findViewById(R.id.pm);
        temp_advice = view.findViewById(R.id.temp_advice);
        titleBar = view.findViewById(R.id.home_titlebar);
        moreviews = view.findViewById(R.id.morenews);
        bannerLayout = view.findViewById(R.id.home_banner);
        weather = view.findViewById(R.id.weather);
        banner_title = view.findViewById(R.id.home_banner_title);
        checkbtn = view.findViewById(R.id.check_btn);
        check_btn_text = view.findViewById(R.id.check_btn_text);
        constraintLayout = view.findViewById(R.id.check_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        initComponent();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        if(hour<=10)
        {
            constraintLayout.setBackgroundResource(R.drawable.morning);
            constraintLayout.getBackground().mutate().setAlpha(100);
        }
        else if (hour<=12)
        {
            constraintLayout.setBackgroundResource(R.drawable.nooning);
            constraintLayout.getBackground().mutate().setAlpha(100);
        }
        else if (hour<17)
        {
            constraintLayout.setBackgroundResource(R.drawable.afternoon);
            constraintLayout.getBackground().mutate().setAlpha(100);
        }
        else if (hour>=17)
        {
            constraintLayout.setBackgroundResource(R.drawable.evening);
            constraintLayout.getBackground().mutate().setAlpha(100);
        }
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
                .url(Utils.generalUrl+"getInstantClass/")
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
                        aClass = new Class(finalResult.getString("classid"),finalResult.getString("place"),finalResult.getString("name"),
                                finalResult.getString("tname"),finalResult.getString("time"));
                        result = null;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    class_place.setText(aClass.getPlace());
                                    course_name.setText(aClass.getName());
                                    teacher_name.setText(aClass.getTeachname());
                                    time.setText(aClass.getTime());
                            }
                        }, 0);
                                    OkHttpClient mOkHttpClient=new OkHttpClient();
                                    FormBody mFormBody=new FormBody.Builder().add("auth",sharedPreferences.getString("auth",null)).add("classid", String.valueOf(aClass.getClassid())).build();
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
                                                    final JSONObject finalResult = result;
                                                    mHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            checkbtn.setEnabled(false);
                                                                check_btn_text.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_3));
                                                        }
                                                    }, 0);
                                                }
                                                //需要考勤
                                                else if(check.getStatus()==1) {
                                                    mHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            checkbtn.setEnabled(false);
                                                                check_btn_text.setBackgroundColor(getResources().getColor(R.color.md_green_400));
                                                                check_btn_text.setText(check.getResult());
                                                        }
                                                    }, 0);
                                                }
                                                //已考勤
                                                else if(check.getStatus()==2) {
                                                    final JSONObject finalResult1 = result;
                                                    mHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            checkbtn.setEnabled(true);
                                                            check_btn_text.setBackgroundColor(getResources().getColor(R.color.md_red_300));
                                                            check_btn_text.setText(check.getResult());
                                                            //签到按钮
                                                            checkbtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    getlocal();
                                                                    if(location.getLongitude()>121.541148&&location.getLongitude()<121.54556&&location.getLatitude()>38.894071&&location.getLatitude()<38.899271)
                                                                    {
                                                                        //创建Intent对象
                                                                        Intent intent=new Intent();
                                                                        //将参数放入intent
                                                                        intent.putExtra("flag", 1);
                                                                        if (aClass.getClassid()>0)
                                                                        intent.putExtra("classid",String.valueOf(aClass.getClassid()));
                                                                    System.out.println("--------------------------------------------------");
                                                                        System.out.println(aClass.getClassid());
                                                                    System.out.println("--------------------------------------------------");
                                                                    //跳转到指定的Activity
                                                                        intent.setClass(getContext(), FdActivity.class);
                                                                        //启动Activity
                                                                        startActivity(intent);
                                                                    }
                                                                    else
                                                                    {
                                                                        XToastUtils.error("您的未到达教室");
                                                                    }
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
                                            XToastUtils.toast("获取新闻列表失败");
                                        }
                                    });
                    };

                    } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//                Looper.prepare();
                XToastUtils.toast("获取新闻列表失败");
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

    //获取地理信息
    private void getlocal(){
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location locate = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        location = new local(locate.getLongitude(),locate.getLatitude());
    }




}
