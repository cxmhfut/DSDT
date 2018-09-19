package com.zy.dsdt.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zy.dsdt.R;
import com.zy.dsdt.adapter.GradeListAdapter;
import com.zy.dsdt.bean.QuestionLog;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Teresa on 2016/5/17.
 */
public class MyGradeActivity extends AppCompatActivity {

    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.cubic_line_chart_my_grade)
    ValueLineChart vlcGrade;
    @InjectView(R.id.list_view_my_grade)
    ListView listView;

    private List<QuestionLog> questionLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grade);
        ButterKnife.inject(this);

        initData();
        initToolbar();
    }

    private void initData() {
        String url = NetURLUtils.getURLpath(this, "LoadQuestionLogServlet");

        RequestQuestionLog requestQuestionLog = new RequestQuestionLog(url,
                new Response.Listener<ArrayList<QuestionLog>>() {
                    @Override
                    public void onResponse(ArrayList<QuestionLog> questionLogs1) {
                        questionLogs = questionLogs1;
                        Collections.reverse(questionLogs);

                        initViews();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyGradeActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("uno", Main2UserActivity.uno);

                return map;
            }
        };

        RequestManager.addRequest(requestQuestionLog, this);
    }

    private void initViews() {
        initChart();

        listView.setAdapter(new GradeListAdapter(this, questionLogs));
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

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("我的成绩");
        }
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
