package com.zy.dsdt.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hrules.charter.CharterBar;
import com.hrules.charter.CharterYLabels;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.QuestionLog;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.CommonUtils;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.xiaopan.swsv.CircularLayout;
import me.xiaopan.swsv.SpiderWebScoreView;

/**
 * Created by Teresa on 2016/5/17.
 */
public class ShowGradeActivity extends AppCompatActivity {

    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.tv_show_grade_grade)
    TextView tvGrade;
    @InjectView(R.id.tv_show_grade_credits)
    TextView tvCredits;
    @InjectView(R.id.charter_bar_YLabels_question)
    CharterYLabels cylQuestion;
    @InjectView(R.id.charter_bar_with_YLabels_question)
    CharterBar cbQuestion;
    @InjectView(R.id.spiderWeb_show_grade_chapter)
    SpiderWebScoreView swsChapter;
    @InjectView(R.id.circular_show_grade_chapter)
    CircularLayout clChapter;

    @InjectView(R.id.tv_bar_chart_grade_1)
    TextView tvBCGrade_1;
    @InjectView(R.id.tv_bar_chart_grade_2)
    TextView tvBCGrade_2;
    @InjectView(R.id.tv_bar_chart_grade_3)
    TextView tvBCGrade_3;
    @InjectView(R.id.tv_bar_chart_grade_4)
    TextView tvBCGrade_4;
    @InjectView(R.id.tv_bar_chart_grade_5)
    TextView tvBCGrade_5;
    @InjectView(R.id.tv_bar_chart_grade_6)
    TextView tvBCGrade_6;
    @InjectView(R.id.tv_bar_chart_grade_7)
    TextView tvBCGrade_7;

    private float[] values = new float[7];
    private String useracount;
    private int QUESTION_MODE;
    private int score;
    private int credit;
    private int choice_count;
    private int blank_count;
    private int C_rightCount = 0;
    private int C_wrongcount = 0;
    private int B_rightcount = 0;
    private int B_wrongcount = 0;
    private float chaptercount[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_grade);
        ButterKnife.inject(this);

        initData();
        initToolbar();
        initViews();

        if (!TextUtils.isEmpty(useracount)) {
            submitCredit();//提交积分
            submitLog();//提交答题日志
        }
    }

    private void submitLog() {
        final QuestionLog ql = new QuestionLog();
        ql.setQno(useracount);
        ql.setQmodule(getQmodule());
        ql.setQtime(CommonUtils.getFormatDate(System.currentTimeMillis()));
        ql.setQscore("" + score);

        String url = NetURLUtils.getURLpath(this, "AddQuestionLogServlet");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            Toast.makeText(ShowGradeActivity.this, "日志提交成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ShowGradeActivity.this, "日志提交失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ShowGradeActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                ObjectMapper mapper = new ObjectMapper();

                try {
                    String array = mapper.writeValueAsString(ql);
                    map.put("data", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    private int getQmodule() {
        if (QUESTION_MODE == 0) {
            //顺序答题
            return 0;
        } else if (QUESTION_MODE >= 1 && QUESTION_MODE <= 9) {
            //章节答题
            return 1;
        } else if (QUESTION_MODE == 10) {
            //随机答题
            return 2;
        } else if (QUESTION_MODE == 11) {
            //模拟考试
            return 3;
        } else {
            return 1;
        }
    }

    private void submitCredit() {
        String url = NetURLUtils.getURLpath(this, "UpdateCreditServlet");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //更新积分成功
                            DBManager manager = new DBManager(ShowGradeActivity.this);

                            User user = manager.getUserDao().queryBuilder().where(UserDao.Properties.Uno.eq(useracount)).unique();

                            user.setUcredit(user.getUcredit() + credit);

                            manager.getUserDao().update(user);
                        } else {
                            //更新积分失败
                            Toast.makeText(ShowGradeActivity.this, "更新积分失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ShowGradeActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("uno", useracount);
                map.put("credit", "" + credit);

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    private void initData() {
        useracount = (String) getIntent().getExtras().get("uno");
        QUESTION_MODE = (int) getIntent().getExtras().get("QUESTION_MODE");
        choice_count = (int) getIntent().getExtras().get("choice_count");
        blank_count = (int) getIntent().getExtras().get("blank_count");
        C_rightCount = (int) getIntent().getExtras().get("C_rightcount");
        C_wrongcount = (int) getIntent().getExtras().get("C_wrongcount");
        B_rightcount = (int) getIntent().getExtras().get("B_rightcount");
        B_wrongcount = (int) getIntent().getExtras().get("B_wrongcount");
        chaptercount = (float[]) getIntent().getExtras().get("chaptercount");

        values[0] = C_rightCount + C_wrongcount + B_rightcount + B_wrongcount;
        values[1] = C_rightCount + C_wrongcount;
        values[2] = B_rightcount + B_wrongcount;
        values[3] = C_rightCount + B_rightcount;
        values[4] = C_wrongcount + B_wrongcount;
        values[5] = C_rightCount;
        values[6] = B_rightcount;

        if (QUESTION_MODE != 11) {
            score = (C_rightCount + B_rightcount) * 100 / (C_rightCount + C_wrongcount + B_rightcount + B_wrongcount);
            credit = (C_rightCount + B_rightcount)/10;
        } else {
            //模拟考试
            score = (C_rightCount + B_rightcount) * 100 / (choice_count + blank_count);
            credit = C_rightCount + B_rightcount;
        }
    }

    private void initViews() {

        tvGrade.setText("" + score);
        tvCredits.setText("此次答题获得" + credit + "个积分");

        tvBCGrade_1.setText("" + (int) values[0]);
        tvBCGrade_2.setText("" + (int) values[1]);
        tvBCGrade_3.setText("" + (int) values[2]);
        tvBCGrade_4.setText("" + (int) values[3]);
        tvBCGrade_5.setText("" + (int) values[4]);
        tvBCGrade_6.setText("" + (int) values[5]);
        tvBCGrade_7.setText("" + (int) values[6]);

        Resources res = getResources();
        int[] barColors = new int[]{
                res.getColor(R.color.lightBlue500), res.getColor(R.color.colorYellow),
                res.getColor(R.color.colorPink), res.getColor(R.color.colorGreen),
                res.getColor(R.color.colorRed), res.getColor(R.color.colorYellow),
                res.getColor(R.color.colorPink)
        };

        // Y轴数值
        cylQuestion.setVisibilityPattern(new boolean[]{true, false});
        cylQuestion.setValues(values, true);
        // 柱状图
        cbQuestion.setValues(values);
        cbQuestion.setColors(barColors);
        // 章节分布图
        if (!(findMax(chaptercount) <= 0)) {
            setupChapter(swsChapter, clChapter, chaptercount);
        }
    }

    private void setupChapter(SpiderWebScoreView spiderWebScoreView, CircularLayout circularLayout, float... scores) {
        spiderWebScoreView.setScores(findMax(scores), scores);

        circularLayout.removeAllViews();
        for (Float score : scores) {
            TextView scoreTextView = (TextView) LayoutInflater.from(getBaseContext()).inflate(R.layout.item_show_grade_chapter, circularLayout, false);
            scoreTextView.setText(score.intValue() + "");
            circularLayout.addView(scoreTextView);
        }
    }

    private float findMax(float[] scores) {
        List<Float> list = new ArrayList<Float>();
        for (float score : scores) {
            list.add(score);
        }

        return Collections.max(list);
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("答题结果");
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
