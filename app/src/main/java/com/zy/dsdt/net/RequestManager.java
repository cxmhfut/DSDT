package com.zy.dsdt.net;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Chen on 2016/4/15.
 */
public class RequestManager {
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(AppApplication.getContext());

    private RequestManager() {
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);

//		Logger.d("addRequest = " + request.getUrl());

    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
