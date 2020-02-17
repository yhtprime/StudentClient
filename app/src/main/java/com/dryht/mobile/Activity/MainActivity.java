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
import android.view.KeyEvent;
import android.widget.Toast;

import com.dryht.mobile.Adapter.HomePageAdapter;
import com.dryht.mobile.Fragment.ClassFragment;
import com.dryht.mobile.Fragment.FriendFragment;
import com.dryht.mobile.Fragment.HomeSFragment;
import com.dryht.mobile.Fragment.HomeTFragment;
import com.dryht.mobile.Fragment.MeSFragment;
import com.dryht.mobile.Fragment.MeTFragment;
import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.Fragment.NoticeFragment;
import com.dryht.mobile.R;
import com.dryht.mobile.Util.NoScrollViewPager;
import com.xuexiang.xui.XUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;
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
        if(sharedPreferences.getString("identity",null)==null||sharedPreferences.getString("identity","1").equals("1"))
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
            fragmentlist .add(new HomeTFragment());
            fragmentlist .add(new ClassFragment());
            fragmentlist .add(new FriendFragment());
            fragmentlist .add(new NoticeFragment());
            fragmentlist .add(new MeTFragment());
        }
        //进入适配器进行绑定
        HomePageAdapter adapter = new HomePageAdapter(this, fragmentlist, supportFragmentManager);
        mVp.setAdapter(adapter);

    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();        //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
            System.exit(0);
        }
    }


}
