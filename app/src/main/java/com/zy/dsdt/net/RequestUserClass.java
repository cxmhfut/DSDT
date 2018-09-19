package com.zy.dsdt.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zy.dsdt.bean.UserClass;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2016/5/12.
 */
public class RequestUserClass extends Request<ArrayList<UserClass>> {
    private Response.Listener<ArrayList<UserClass>> listener;

    public RequestUserClass(String url, Response.Listener<ArrayList<UserClass>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        this.listener = listener;
    }

    @Override
    protected Response<ArrayList<UserClass>> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory factory = TypeFactory.defaultInstance();
            ArrayList<UserClass> questions = mapper.readValue(new String(networkResponse.data), factory.constructCollectionType(List.class, UserClass.class));

            return Response.success(questions, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<UserClass> userClasses) {
        listener.onResponse(userClasses);
    }
}
