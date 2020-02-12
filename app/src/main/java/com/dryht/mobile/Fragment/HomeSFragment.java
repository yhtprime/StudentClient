package com.dryht.mobile.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dryht.mobile.Activity.FdActivity;
import com.dryht.mobile.Activity.LoginActivity;
import com.dryht.mobile.Adapter.RecyclerViewBannerAdapter;
import com.dryht.mobile.DemoDataProvider;
import com.dryht.mobile.R;
import com.dryht.mobile.utils.XToastUtils;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.banner.recycler.layout.BannerLayoutManager;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.button.RippleView;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
import com.xuexiang.xui.widget.textview.marqueen.DisplayEntity;
import com.xuexiang.xui.widget.toast.XToast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HomeSFragment extends Fragment {
    private View view;
    private MarqueeTextView mTvMarquee;
    private BannerLayout bannerLayout;
    private TextView banner_title;
    private RippleView checkbtn;
    private TextView checkbtntitle;
    private ImageView startrecogn;
    private RecyclerViewBannerAdapter mAdapterHorizontal;

    public HomeSFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    private void initComponent() {
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
        initComponent();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //最顶部得滚动条
        mTvMarquee.setOnMarqueeListener(new MarqueeTextView.OnMarqueeListener() {
            @Override
            public DisplayEntity onStartMarquee(DisplayEntity displayMsg, int index) {
                if (displayMsg.toString().equals("离离原上草，一岁一枯荣。")) {
                    return null;
                } else {
                    XToast.warning(getContext(),"滚动失败");
                    return displayMsg;
                }
            }
            @Override
            public List<DisplayEntity> onMarqueeFinished(List<DisplayEntity> displayDatas) {
                XToast.warning(getContext(),"滚动成功");
                return displayDatas;
            }
        });
        mTvMarquee.addDisplayString("《《《欢迎使用校园小助手，更多校园信息请点击这里》》》");
        mTvMarquee.setSpeed(7);
        mTvMarquee.startRoll();
        //轮播图
        bannerLayout.setAdapter(mAdapterHorizontal = new RecyclerViewBannerAdapter(DemoDataProvider.urls));
        bannerLayout.setAutoPlaying(true);
        banner_title.setText("这个是仪狄格");
        bannerLayout.setOnIndicatorIndexChangedListener(new BannerLayout.OnIndicatorIndexChangedListener() {
            @Override
            public void onIndexChanged(int position) {
                banner_title.setText("轮播到第" + (position + 1) + "个");
            }
        });
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

    }

    //获取天气情况
    private void getWeather(){

    }

    //获取上课考勤情况
    private void getCheckState(){

    }




}
