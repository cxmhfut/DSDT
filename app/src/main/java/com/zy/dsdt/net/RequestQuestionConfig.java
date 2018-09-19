package com.zy.dsdt.net;

import android.annotation.TargetApi;
import android.os.Build;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zy.dsdt.bean.QuestionConfig;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Chen on 2016/5/12.
 */
public class RequestQuestionConfig extends Request<QuestionConfig> {
    private Response.Listener<QuestionConfig> listener;

    public RequestQuestionConfig(String url, Response.Listener<QuestionConfig> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        this.listener = listener;
    }

    @Override
    protected Response<QuestionConfig> parseNetworkResponse(NetworkResponse networkResponse) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            QuestionConfig questionConfig = mapper.readValue(new String(networkResponse.data), QuestionConfig.class);
            return Response.success(questionConfig, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(QuestionConfig questionConfig) {
        listener.onResponse(questionConfig);
    }
}
