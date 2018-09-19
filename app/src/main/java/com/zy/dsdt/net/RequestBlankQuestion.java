package com.zy.dsdt.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zy.dsdt.bean.BlankQuestion;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2016/5/12.
 */
public class RequestBlankQuestion extends Request<ArrayList<BlankQuestion>> {
    private Response.Listener<ArrayList<BlankQuestion>> listener;

    public RequestBlankQuestion(String url, Response.Listener<ArrayList<BlankQuestion>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        this.listener = listener;
    }

    @Override
    protected Response<ArrayList<BlankQuestion>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory factory = TypeFactory.defaultInstance();
            ArrayList<BlankQuestion> questions = mapper.readValue(new String(networkResponse.data), factory.constructCollectionType(List.class, BlankQuestion.class));

            return Response.success(questions, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<BlankQuestion> blankQuestions) {
        listener.onResponse(blankQuestions);
    }
}
