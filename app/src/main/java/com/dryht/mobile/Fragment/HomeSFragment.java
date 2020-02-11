package com.dryht.mobile.Fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dryht.mobile.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.banner.recycler.layout.BannerLayoutManager;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
import com.xuexiang.xui.widget.textview.marqueen.DisplayEntity;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.ArrayList;
import java.util.List;

public class HomeSFragment extends Fragment {
    private View view;
    private MarqueeTextView mTvMarquee;
    private BannerLayout bannerLayout;


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
        mTvMarquee.setSpeed(5);
        mTvMarquee.startRoll();
        //轮播图
        Resources resources = getContext().getResources();
        Drawable drawable = resources.getDrawable(R.drawable.home);
        BannerItem bannerItem = new BannerItem();
        bannerItem.setImgUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1026905852,647435744&fm=26&gp=0.jpg");
        bannerItem.setTitle("第一个消息");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
