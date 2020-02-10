package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dryht.mobile.Adapter.HomePageAdapter;
import com.dryht.mobile.Fragment.ClassFragment;
import com.dryht.mobile.Fragment.FriendFragment;
import com.dryht.mobile.Fragment.HomeSFragment;
import com.dryht.mobile.Fragment.MeSFragment;
import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.Fragment.NoticeFragment;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    //主界面
    private NoScrollViewPager mVp;
    //存放fragment的数组
    List<Fragment> fragmentlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取管理者
        supportFragmentManager = getSupportFragmentManager();
        //开启事务
        fragmentTransaction =  supportFragmentManager.beginTransaction();
        //添加底部导航上去
        fragmentTransaction.add(R.id.Nav_btn, new NavBarFragment()).commit();

         mVp = findViewById(R.id.vp);

        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("identity","1")==null||sharedPreferences.getString("identity","1").equals("1"))
        {
            fragmentlist .add(new HomeSFragment());
            fragmentlist .add(new ClassFragment());
            fragmentlist .add(new FriendFragment());
            fragmentlist .add(new NoticeFragment());
            fragmentlist .add(new MeSFragment());
        }
        else
        {
            //这里放老师的界面
        }
        //进入适配器进行绑定
        HomePageAdapter adapter = new HomePageAdapter(this, fragmentlist, supportFragmentManager);
        mVp.setAdapter(adapter);

    }
}
