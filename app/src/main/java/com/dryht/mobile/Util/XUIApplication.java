package com.dryht.mobile.Util;

import android.app.Application;
import android.content.Context;


import com.dryht.mobile.Fragment.FriendSendFragment;
import com.xuexiang.xpage.PageConfig;
import com.xuexiang.xpage.PageConfiguration;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xpage.core.CoreConfig;
import com.xuexiang.xpage.model.PageInfo;
import com.xuexiang.xpage.utils.Utils;
import com.xuexiang.xui.XUI;
import com.xuexiang.xutil.XUtil;

import java.util.ArrayList;
import java.util.List;

public class XUIApplication extends Application {

    public XUIApplication() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        XUtil.init(this);
        // TODO Auto-generated method stub
        PageConfig.getInstance()
                .setPageConfiguration(new PageConfiguration() { //页面注册
                    @Override
                    public List<PageInfo> registerPages(Context context) {
                        List<PageInfo> pageInfos = new ArrayList<>();
                        pageInfos.add(PageConfig.getPageInfo(FriendSendFragment.class));
                        return pageInfos;        //手动注册页面
                    }
                })
                .debug("PageLog")       //开启调试
                .enableWatcher(false)   //设置是否开启内存泄露监测
                .init(this);            //初始化页面配置
        super.onCreate();
    }
    }
