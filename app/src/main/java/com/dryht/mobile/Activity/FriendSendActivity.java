package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import com.dryht.mobile.Fragment.FriendSendFragment;
import com.dryht.mobile.Fragment.TabNewFragent;
import com.dryht.mobile.R;
import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xui.utils.StatusBarUtils;

public class FriendSendActivity extends AppCompatActivity {
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_send);
        sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));
        //获取管理者
        supportFragmentManager = getSupportFragmentManager();
        fragmentTransaction =  supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.send_fragment,new FriendSendFragment()).commit();
    }
}
