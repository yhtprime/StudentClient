package com.dryht.mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.R;


public class HomePageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private LayoutInflater mInflater;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    private int[] colorArray = new int[]{android.R.color.black, android.R.color.holo_blue_dark, android.R.color.holo_green_dark, android.R.color.holo_red_dark};


    public HomePageAdapter(Context context, List<Fragment> fragments, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.mInflater = LayoutInflater.from(context);
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 将实例化的fragment进行显示即可。
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentManager.beginTransaction().show(fragment).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);// 注释父类方法
        Fragment fragment = fragments.get(position);// 获取要销毁的fragment
        fragmentManager.beginTransaction().hide(fragment).commit();// 将其隐藏即可，并不需要真正销毁，这样fragment状态就得到了保存
    }

}


