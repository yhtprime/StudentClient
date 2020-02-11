package com.dryht.mobile.Util;

import android.app.Application;
import android.view.LayoutInflater;

import com.xuexiang.xui.XUI;

import java.io.File;

public class XUIApplication extends Application {

    public XUIApplication() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        // TODO Auto-generated method stub
        super.onCreate();

    }

    }
