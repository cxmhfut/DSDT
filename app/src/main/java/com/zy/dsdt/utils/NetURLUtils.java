package com.zy.dsdt.utils;

import android.content.Context;

import com.zy.dsdt.R;

/**
 * Created by Chen on 2016/5/12.
 */
public class NetURLUtils {
    /**
     * 根据Servlet的名字获取访问路径
     *
     * @param context
     * @param servletname
     * @return
     */
    public static String getURLpath(Context context, String servletname) {
        String server = context.getResources().getString(R.string.server);
        String url = "http://" + server + "/DSDTSystemServer/" + servletname;
        return url;
    }
}
