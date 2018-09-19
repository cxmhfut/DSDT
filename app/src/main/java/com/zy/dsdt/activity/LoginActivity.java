package com.zy.dsdt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.bean.WrongQuestion;
import com.zy.dsdt.bean.WrongQuestionDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.net.RequestWrongQuestion;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Teresa on 2016/5/9.
 */
public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.activity_login_iv_left)
    ImageView imageViewLeft;
    @InjectView(R.id.activity_login_iv_right)
    ImageView imageViewRight;
    @InjectView(R.id.activity_login_et_username)
    EditText editTextUserName;
    @InjectView(R.id.activity_login_et_userpswd)
    EditText editTextUserPswd;
    @InjectView(R.id.activity_login_btn_register)
    Button buttonRegister;
    @InjectView(R.id.activity_login_btn_login)
    Button buttonLogin;
    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;

    private DBManager manager;
    private UserDao userDao;
    private WrongQuestionDao wrongQuestionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        userDao = manager.getUserDao();
        wrongQuestionDao = manager.getWrongQuestionDao();

        //初始化界面
        initToolbar();
        initView();

    }

    private User getUserByUno(String uno) {
        return userDao.
                queryBuilder().
                where(UserDao.Properties.Uno.eq(uno)).
                list().
                get(0);
    }

    private void loadWrongQuestion(final String uno) {
        String url = NetURLUtils.getURLpath(this, "LoadWrongQuestionServlet");
        RequestWrongQuestion requestWrongQuestion = new RequestWrongQuestion(url, new Response.Listener<ArrayList<WrongQuestion>>() {
            @Override
            public void onResponse(ArrayList<WrongQuestion> wrongQuestions) {

                wrongQuestionDao.deleteAll();

                for(WrongQuestion wrongQuestion:wrongQuestions){
                    wrongQuestionDao.insertOrReplace(wrongQuestion);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                map.put("uno", uno);

                return map;
            }
        };

        RequestManager.addRequest(requestWrongQuestion, this);
    }

    //登陆
    @OnClick(R.id.activity_login_btn_login)
    public void login(View view) {
        final String username = editTextUserName.getText().toString().trim();
        final String password = editTextUserPswd.getText().toString().trim();

        String url = NetURLUtils.getURLpath(this, "Login4UserServlet");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        switch (result) {
                            case 0://登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                                User user = getUserByUno(username);

                                if (user.getUpermission() == 0) {
                                    //普通用户登录
                                    Intent intent = new Intent();
                                    intent.putExtra("uno", user.getUno());
                                    setResult(0, intent);
                                    finish();
                                } else if (user.getUpermission() == 1) {
                                    //教师登录
                                    Intent intent = new Intent(LoginActivity.this, Main2TeacherActivity.class);
                                    intent.putExtra("uno", user.getUno());

                                    startActivity(intent);
                                    finish();
                                } else {
                                    //管理员登录
                                    Intent intent = new Intent(LoginActivity.this, Main2AdminActivity.class);
                                    intent.putExtra("uno", user.getUno());

                                    startActivity(intent);
                                    finish();
                                }

                                //加载用户错题

                                loadWrongQuestion(user.getUno());

                                break;
                            case 1://账号错误
                                Toast.makeText(LoginActivity.this, "账号错误", Toast.LENGTH_SHORT).show();
                                break;
                            case 2://密码错误
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("username", username);
                map.put("password", password);

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    //注册
    @OnClick(R.id.activity_login_btn_register)
    public void register() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("登陆");
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1, null);
                LoginActivity.this.onBackPressed();
            }
        });
    }

    private void initView() {
        buttonLogin.setEnabled(false);

        //当焦点在账号输入框时，用ic_22,ic_33作为图片
        editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imageViewLeft.setImageResource(R.drawable.ic_22);
                    imageViewRight.setImageResource(R.drawable.ic_33);
                }
            }
        });
        //捂眼睛
        editTextUserPswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imageViewLeft.setImageResource(R.drawable.ic_22_hide);
                    imageViewRight.setImageResource(R.drawable.ic_33_hide);
                }
            }
        });

        //监控输入框文字数量 ，用于改变登录按钮是否可用
        editTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && editTextUserPswd.getText().length() > 0) {
                    buttonLogin.setEnabled(true);
                } else {
                    buttonLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextUserPswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && editTextUserName.getText().length() > 0) {
                    buttonLogin.setEnabled(true);
                } else {
                    buttonLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
