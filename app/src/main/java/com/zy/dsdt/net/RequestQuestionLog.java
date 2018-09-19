package com.zy.dsdt.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zy.dsdt.bean.QuestionLog;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2016/5/19.
 */
public class RequestQuestionLog extends Request<ArrayList<QuestionLog>> {
    private Response.Listener<ArrayList<QuestionLog>> listener;

    public RequestQuestionLog(String url, Response.Listener<ArrayList<QuestionLog>> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        this.listener = listener;
    }

    @Override
    protected Response<ArrayList<QuestionLog>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory factory = TypeFactory.defaultInstance();
            ArrayList<QuestionLog> questions = mapper.readValue(new String(networkResponse.data), factory.constructCollectionType(List.class, QuestionLog.class));

            return Response.success(questions, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<QuestionLog> questionLogs) {
        listener.onResponse(questionLogs);
    }
}
