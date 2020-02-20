package com.dryht.mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
/*
朋友圈两个选项绑定
 */
public class FriendAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private LayoutInflater mInflater;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private String [] tabTitleArray;

    public FriendAdapter(Context context, List<Fragment> fragments, FragmentManager fragmentManager, String[] tabTitleArray) {
        super(fragmentManager);
        this.mInflater = LayoutInflater.from(context);
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
        this.tabTitleArray = tabTitleArray;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleArray[position];
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
