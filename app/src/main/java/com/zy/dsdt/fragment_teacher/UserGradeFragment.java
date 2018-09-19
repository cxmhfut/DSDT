package com.zy.dsdt.fragment_teacher;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2TeacherActivity;
import com.zy.dsdt.adapter.GradeListAdapter;
import com.zy.dsdt.bean.QuestionLog;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.net.RequestQuestionLog;
import com.zy.dsdt.utils.NetURLUtils;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chen on 2016/5/14.
 */
public class UserGradeFragment extends Fragment {
    private View view;
    private Main2TeacherActivity main;
    private ListView lvUserGrade;
    private Context context;
    private User user;
    private List<QuestionLog> questionLogs;
    private ValueLineChart vlcGrade;

    public void initData(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teacher_user_grade, container, false);

        initLog();

        lvUserGrade = (ListView) view.findViewById(R.id.lv_user_grade);
        vlcGrade = (ValueLineChart) view.findViewById(R.id.cubic_line_chart_user_grade);

        return view;
    }

    private void initLog() {
        String url = NetURLUtils.getURLpath(context, "LoadQuestionLogServlet");

        RequestQuestionLog requestQuestionLog = new RequestQuestionLog(url,
                new Response.Listener<ArrayList<QuestionLog>>() {
                    @Override
                    public void onResponse(ArrayList<QuestionLog> questionLogs1) {
                        questionLogs = questionLogs1;
                        Collections.reverse(questionLogs);

                        initChart();
                        lvUserGrade.setAdapter(new GradeListAdapter(getActivity(), questionLogs));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("uno", user.getUno());

                return map;
            }
        };

        RequestManager.addRequest(requestQuestionLog, context);
    }

    private void initChart() {
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

//        series.addPoint(new ValueLinePoint("5.18", 2.4f));

        for (QuestionLog log : getLatestLogs(questionLogs)) {
            series.addPoint(new ValueLinePoint(log.getQtime(), Float.parseFloat(log.getQscore())));
        }

        vlcGrade.addSeries(series);
        vlcGrade.startAnimation();
    }

    private List<QuestionLog> getLatestLogs(List<QuestionLog> questionLogs) {
        List<QuestionLog> result = new ArrayList<QuestionLog>();

        int count = 0;
        for (int i = questionLogs.size() - 1; i >= 0; i--) {
            if (++count == 8) {
                break;
            }
            result.add(questionLogs.get(i));
        }

        return result;
    }

}
