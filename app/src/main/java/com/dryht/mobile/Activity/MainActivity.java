package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dryht.mobile.Adapter.DrawerAdapter;
import com.dryht.mobile.Adapter.DrawerItem;
import com.dryht.mobile.Adapter.HomePageAdapter;
import com.dryht.mobile.Adapter.SimpleItem;
import com.dryht.mobile.Adapter.SpaceItem;
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
import com.dryht.mobile.Util.TokenUtils;
import com.squareup.picasso.Picasso;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SharedPreferences sharedPreferences;
    private DrawerAdapter mAdapter;
    private String[] mMenuTitles;
    private Drawable[] mMenuIcons;
    public static SlidingRootNav mSlidingRootNav;
    private TextView perintro,pername;
    private RadiusImageView perpic;
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

        //设置顶部导航栏
        StatusBarUtils.setStatusBarDarkMode(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.thiscolor));

         mVp = findViewById(R.id.vp);
         sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("identity",null)==null||sharedPreferences.getString("identity","1").equals("1"))
        {
            fragmentlist .add(new HomeSFragment());
            fragmentlist .add(new ClassFragment());
            fragmentlist .add(new FriendFragment());
            fragmentlist .add(new NoticeFragment());
        }
        else
        {
            //这里放老师的界面
            fragmentlist .add(new HomeTFragment());
            fragmentlist .add(new ClassFragment());
            fragmentlist .add(new FriendFragment());
            fragmentlist .add(new NoticeFragment());
        }
        //进入适配器进行绑定
        HomePageAdapter adapter = new HomePageAdapter(this, fragmentlist, supportFragmentManager);
        mVp.setOffscreenPageLimit(2);
        mVp.setAdapter(adapter);

        mMenuTitles = ResUtils.getStringArray(R.array.menu_titles);
        mMenuIcons = ResUtils.getDrawableArray(this, R.array.menu_icons);

        mSlidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        mAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(0),
                createItemFor(1),
                createItemFor(2),
                new SpaceItem(48),
                createItemFor(4)));
        mAdapter.setListener(this::onItemSelected);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
        perintro = findViewById(R.id.perintro);
        perintro.setText(sharedPreferences.getString("intro",""));
        pername = findViewById(R.id.pername);
        pername.setText(sharedPreferences.getString("name","錯誤用戶"));
        perpic = findViewById(R.id.perpic);
        Picasso.with(this).load(sharedPreferences.getString("pic",null)).into(perpic);



    }
    public void onItemSelected(int position) {
        Intent intent=new Intent();
        switch (position) {
            case 0:
                //创建Intent对象
                intent=new Intent();
                //跳转到指定的Activity
                intent.setClass(MainActivity.this, PersonActivity.class);
                //启动Activity
                startActivity(intent);
                onStop();
                break;
            case 1:
                //创建Intent对象
                intent=new Intent();
                //将参数放入intent
                intent.putExtra("flag", 3);
                //跳转到指定的Activity
                intent.setClass(MainActivity.this, FdActivity.class);
                //启动Activity
                startActivity(intent);
                onStop();
                break;
            case 2:
                //创建Intent对象
                intent=new Intent();
                //跳转到指定的Activity
                intent.setClass(MainActivity.this, SearchFriendActivity.class);
                //启动Activity
                startActivity(intent);
                onStop();
                break;
            case 4:
                DialogLoader.getInstance().showConfirmDialog(
                        this,
                        getString(R.string.lab_logout_confirm),
                        getString(R.string.lab_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                                sharedPreferences.edit().clear().commit();
                                TokenUtils.handleLogoutSuccess();
                                finish();
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
                break;
            default:
                break;
        }
    }
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(mMenuIcons[position], mMenuTitles[position])
                .withIconTint(ThemeUtils.resolveColor(this, R.attr.xui_config_color_content_text))
                .withTextTint(ThemeUtils.resolveColor(this, R.attr.xui_config_color_content_text))
                .withSelectedIconTint(ThemeUtils.resolveColor(this, R.attr.colorAccent))
                .withSelectedTextTint(ThemeUtils.resolveColor(this, R.attr.colorAccent));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏
//        StatusBarUtils.fullScreen(this);
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
