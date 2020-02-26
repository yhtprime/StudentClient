package com.dryht.mobile.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dryht.mobile.R;
import com.dryht.mobile.Util.NoScrollViewPager;

public class NavBarFragment extends Fragment {
    private View view;
    NoScrollViewPager viewPager;
    private ImageView home,classes,friend,notice;
    private LinearLayout Linearhome,Linearclasses ,Linearfriend,Linearnotice;
    public NavBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nav_bar, container, false);
        initcompent();
        dealcompent();
        return view;
    }
    //监听所有组件
    private void dealcompent() {
        Linearhome.setOnClickListener(new swicthpage(1));
        Linearclasses.setOnClickListener(new swicthpage(2));
        Linearfriend.setOnClickListener(new swicthpage(3));
        Linearnotice.setOnClickListener(new swicthpage(4));
    }

    //初始化组件
    private void initcompent(){
        home = view.findViewById(R.id.nav_home);
        classes = view.findViewById(R.id.nav_class);
        friend = view.findViewById(R.id.nav_friend);
        notice = view.findViewById(R.id.nav_notice);
        viewPager = getActivity().findViewById(R.id.vp);
        Linearhome = view.findViewById(R.id.Linearhome);
        Linearclasses = view.findViewById(R.id.Linearclasses);
        Linearfriend = view.findViewById(R.id.Linearfriend);
        Linearnotice = view.findViewById(R.id.Linearnotice);;
    }

    private class swicthpage implements View.OnClickListener {
        private int flag;
        public swicthpage(int i) {
            flag = i;
        }
        //把所有图标变成灰色
        private void makeAllGray(){
            home.setImageDrawable(getResources().getDrawable(R.drawable.home));
            classes.setImageDrawable(getResources().getDrawable(R.drawable.classn));
            friend.setImageDrawable(getResources().getDrawable(R.drawable.friend));
            notice.setImageDrawable(getResources().getDrawable(R.drawable.notice));
        }

        @Override
        public void onClick(View v) {
            makeAllGray();
            switch (flag)
            {
                case 1:
                    viewPager.setCurrentItem(0);
                    home.setImageDrawable(getResources().getDrawable(R.drawable.homec));
                    break;
                case 2:
                    viewPager.setCurrentItem(1);
                    classes.setImageDrawable(getResources().getDrawable(R.drawable.classc));
                    break;
                case 3:
                    viewPager.setCurrentItem(2);
                    friend.setImageDrawable(getResources().getDrawable(R.drawable.friendc));
                    break;
                case 4:
                    viewPager.setCurrentItem(3);
                    notice.setImageDrawable(getResources().getDrawable(R.drawable.noticec));
                    break;
            }
        }
    }
}
