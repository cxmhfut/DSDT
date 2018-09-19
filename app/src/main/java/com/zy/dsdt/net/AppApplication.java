package com.zy.dsdt.net;

import android.app.Application;
import android.content.Context;

/**
 * 提供应用的上下文
 * <p/>
 * Created by Chen on 2016/4/15.
 */
public class AppApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
