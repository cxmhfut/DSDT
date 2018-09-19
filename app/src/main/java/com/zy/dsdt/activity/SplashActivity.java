package com.zy.dsdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.BlankQuestion;
import com.zy.dsdt.bean.BlankQuestionPDao;
import com.zy.dsdt.bean.ChoiceQuestion;
import com.zy.dsdt.bean.ChoiceQuestionDao;
import com.zy.dsdt.bean.QuestionConfig;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.net.RequestBlankQuestion;
import com.zy.dsdt.net.RequestChoiceQuestion;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.net.RequestQuestionConfig;
import com.zy.dsdt.net.RequestUser;
import com.zy.dsdt.net.RequestUserClass;
import com.zy.dsdt.utils.BlankQuestionUtils;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chen on 2016/5/12.
 */
public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);
            boolean isFirstIn = preferences.getBoolean("isFirstIn", true);

            if (isFirstIn) {
                preferences.put("isFirstIn", false);

                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                enterMain2User();
            }
        }
    };

    private DBManager manager;
    private UserClassDao userClassDao;
    private UserDao userDao;
    private ChoiceQuestionDao choiceQuestionDao;
    private BlankQuestionPDao blankQuestionPDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manager = new DBManager(this);

        userClassDao = manager.getUserClassDao();
        userDao = manager.getUserDao();
        choiceQuestionDao = manager.getChoiceQuestionDao();
        blankQuestionPDao = manager.getBlankQuestionPDao();

        checkDataVersion();

        handler.sendEmptyMessageDelayed(0, 3000);
    }

    public void enterMain2User() {
        Intent intent = new Intent(SplashActivity.this, Main2UserActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkDataVersion() {
        checkUserClassVersion();//检查用户班级数据版本
        checkUserVersion();//检查用户数据版本
        checkChoiceQuestionVersion();//检查选择题数据版本
        checkBlankQuestionVersion();//检查填空题数据版本
        checkQuestionConfigVersion();//检查题目分配配置信息版本
    }

    public void checkUserClassVersion() {
        String url = NetURLUtils.getURLpath(this, "CheckDataVersionServlet");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                        String version = preferences.getString("userclass", "");

                        if (!version.equals(s)) {
                            //版本不同进行数据更新操作
                            updateUserClass();
                            preferences.put("userclass", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                //2表示请求用户班级数据版本
                map.put("versiontype", "2");

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    public void updateUserClass() {
        String url = NetURLUtils.getURLpath(this, "UpdateUserClassServlet");
        RequestUserClass requestUserClass = new RequestUserClass(url,
                new Response.Listener<ArrayList<UserClass>>() {
                    @Override
                    public void onResponse(ArrayList<UserClass> userClasses) {
                        userClassDao.deleteAll();

                        for (UserClass userClass : userClasses) {
                            userClassDao.insertOrReplace(userClass);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "更新班级数据失败", Toast.LENGTH_SHORT).show();
            }
        });

        RequestManager.addRequest(requestUserClass, this);
    }

    public void checkUserVersion() {
        String url = NetURLUtils.getURLpath(this, "CheckDataVersionServlet");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                        String version = preferences.getString("user", "");

                        if (!version.equals(s)) {
                            //版本不同进行数据更新操作
                            updateUser();
                            preferences.put("user", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                //1表示请求用户数据版本
                map.put("versiontype", "1");

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    public void updateUser() {
        String url = NetURLUtils.getURLpath(this, "UpdateUserServlet");

        RequestUser requestUser = new RequestUser(url,
                new Response.Listener<ArrayList<User>>() {
                    @Override
                    public void onResponse(ArrayList<User> users) {
                        userDao.deleteAll();

                        for (User user : users) {
                            userDao.insertOrReplace(user);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "更新用户数据失败", Toast.LENGTH_SHORT).show();
            }
        });

        RequestManager.addRequest(requestUser, this);
    }

    public void checkChoiceQuestionVersion() {
        String url = NetURLUtils.getURLpath(this, "CheckDataVersionServlet");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                        String version = preferences.getString("choicequestion", "");

                        if (!version.equals(s)) {
                            //版本不同进行数据更新操作
                            updateChoiceQuestion();
                            preferences.put("choicequestion", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                //3表示请求选择题数据版本
                map.put("versiontype", "3");

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    public void updateChoiceQuestion() {
        String url = NetURLUtils.getURLpath(this, "UpdateChoiceQuestionServlet");

        RequestChoiceQuestion requestChoiceQuestion = new RequestChoiceQuestion(url,
                new Response.Listener<ArrayList<ChoiceQuestion>>() {
                    @Override
                    public void onResponse(ArrayList<ChoiceQuestion> choiceQuestions) {
                        choiceQuestionDao.deleteAll();

                        for (ChoiceQuestion choiceQuestion : choiceQuestions) {
                            choiceQuestionDao.insertOrReplace(choiceQuestion);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "更新选择题数据失败", Toast.LENGTH_SHORT).show();
            }
        });

        RequestManager.addRequest(requestChoiceQuestion, this);
    }

    public void checkBlankQuestionVersion() {
        String url = NetURLUtils.getURLpath(this, "CheckDataVersionServlet");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                        String version = preferences.getString("blankquestion", "");

                        if (!version.equals(s)) {
                            //版本不同进行数据更新操作
                            updateBlankQuestion();
                            preferences.put("blankquestion", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                //4表示请求填空题数据版本
                map.put("versiontype", "4");

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    public void updateBlankQuestion() {
        String url = NetURLUtils.getURLpath(this, "UpdateBlankQuestionServlet");

        RequestBlankQuestion requestBlankQuestion = new RequestBlankQuestion(url,
                new Response.Listener<ArrayList<BlankQuestion>>() {
                    @Override
                    public void onResponse(ArrayList<BlankQuestion> blankQuestions) {
                        blankQuestionPDao.deleteAll();

                        for(BlankQuestion blankQuestion:blankQuestions){
                            blankQuestionPDao.insertOrReplace(BlankQuestionUtils.getBlankQuestionP(blankQuestion));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "更新填空题数据失败", Toast.LENGTH_SHORT).show();
            }
        });

        RequestManager.addRequest(requestBlankQuestion, this);
    }

    public void checkQuestionConfigVersion() {
        String url = NetURLUtils.getURLpath(this, "CheckDataVersionServlet");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                        String version = preferences.getString("questionconfigversion", "");

                        if (!version.equals(s)) {
                            //版本不同进行数据更新操作
                            updateQuestionConfig();
                            preferences.put("questionconfigversion", s);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                //5表示请求题目配置信息数据版本
                map.put("versiontype", "5");

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    public void updateQuestionConfig() {
        String url = NetURLUtils.getURLpath(this, "UpdateQuestionConfigServlet");

        RequestQuestionConfig requestQuestionConfig = new RequestQuestionConfig(url,
                new Response.Listener<QuestionConfig>() {
                    @Override
                    public void onResponse(QuestionConfig questionConfig) {
                        try {
                            TrayAppPreferences preferences = new TrayAppPreferences(SplashActivity.this);

                            ObjectMapper mapper = new ObjectMapper();
                            String value = mapper.writeValueAsString(questionConfig);

                            preferences.put("questionconfig", value);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, "保存题目分配信息数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "更新题目分配信息数据失败", Toast.LENGTH_SHORT).show();
            }
        });

        RequestManager.addRequest(requestQuestionConfig, this);
    }
}
