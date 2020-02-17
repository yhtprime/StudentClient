package com.dryht.mobile.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.dryht.mobile.Fragment.NavBarFragment;
import com.dryht.mobile.Fragment.tabsegment.TabSegmentScrollableModeFragment;
import com.dryht.mobile.R;

public class TabNewActivity extends AppCompatActivity {
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_new);
        //获取管理者
        supportFragmentManager = getSupportFragmentManager();
        //开启事务
        fragmentTransaction =  supportFragmentManager.beginTransaction();
        //添加底部导航上去
        fragmentTransaction.add(R.id.tabnew, new TabSegmentScrollableModeFragment()).commit();
    }
}
