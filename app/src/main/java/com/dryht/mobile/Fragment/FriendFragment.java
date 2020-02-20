package com.dryht.mobile.Fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dryht.mobile.Adapter.FriendAdapter;
import com.dryht.mobile.R;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.devmeteor.tableview.TableView;

public class FriendFragment extends Fragment {
    View view;
    private ViewPager viewPager;
    List<Fragment> mFragments;
    private TabLayout tableView;
    private RefreshLayout refreshLayout;
    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend, container, false);
        viewPager = view.findViewById(R.id.friend_pager);
        tableView = view.findViewById(R.id.friend_tab_layout);
        setupViewPager();
        refreshLayout = (RefreshLayout)view.findViewById(R.id.friendinforefreshLayout);
        refreshLayout.setOnRefreshListener(new refreshListener());
        refreshLayout.autoRefresh();
        return view;
    }

    //刷新页面
    private class refreshListener implements OnRefreshListener {
        @Override
        public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();

                    //结束加载
                    refreshLayout.finishRefresh();
                    //加载失败的话3秒后结束加载
                    refreshLayout.finishRefresh(3000);
                    Looper.loop();
                }
            },2000);

        }
    }
    //设置fragemnt给viewpager
    private void setupViewPager() {
//        tableView.addTab(tableView.newTab().setText("圈内动态"),0);
//        tableView.addTab(tableView.newTab().setText("我的动态"),1);
        mFragments = new ArrayList<>();
        mFragments.add(new FriendListFragment(1));
        mFragments.add(new FriendListFragment(1));
        String [] titile = {"圈内动态","我的动态"};
        // 第二步：为ViewPager设置适配器
        FriendAdapter friendAdapter = new FriendAdapter(getContext(), mFragments, getChildFragmentManager(),titile);

        viewPager.setAdapter(friendAdapter);
        //  第三步：将ViewPager与TableLayout 绑定在一起
        tableView.setupWithViewPager(viewPager);
    }


}
