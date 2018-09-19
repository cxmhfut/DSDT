package com.zy.dsdt.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Teresa on 2016/5/9.
 */
public class RegisterActivity extends AppCompatActivity {
    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.spinner)
    MaterialBetterSpinner spinner;
    @InjectView(R.id.activity_register_et_num)
    EditText editTextNum;
    @InjectView(R.id.activity_register_username)
    EditText editTextUserName;
    @InjectView(R.id.activity_register_pwd)
    EditText editTextPwd;
    @InjectView(R.id.activity_register_rpwd)
    EditText editTextRPwd;
    @InjectView(R.id.activity_register_btn_register)
    Button buttonRegister;

    private DBManager manager;
    private UserClassDao userClassDao;
    private UserDao userDao;
    private List<UserClass> userClasses;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        userClassDao = manager.getUserClassDao();
        userDao = manager.getUserDao();

        user = new User();

        userClasses = userClassDao.loadAll();


        CLASSES = new String[userClasses.size()];

        for (int i = 0; i < userClasses.size(); i++) {
            CLASSES[i] = userClasses.get(i).getCname();
        }

        initToolbar();
        initViews();
    }

    /**
     * 通过班级名称获取班级编号
     *
     * @param cname
     * @return
     */
    private String getCnoByCname(String cname) {

        return userClassDao.
                queryBuilder().
                where(UserClassDao.Properties.Cname.eq(cname)).
                list().
                get(0).
                getCno();
    }

    /**
     * 通过班级编号获取班级
     *
     * @param cno
     * @return
     */
    private UserClass getUserClassByCno(String cno) {
        return userClassDao.
                queryBuilder().
                where(UserClassDao.Properties.Cno.eq(cno)).
                list().
                get(0);
    }

    //注册
    @OnClick(R.id.activity_register_btn_register)
    public void register() {
        String userno = editTextNum.getText().toString().trim();
        String username = editTextUserName.getText().toString().trim();
        String password1 = editTextPwd.getText().toString().trim();
        String password2 = editTextRPwd.getText().toString().trim();
        String userclass = getCnoByCname(spinner.getText().toString());

        if (!TextUtils.isEmpty(userno) && !TextUtils.isEmpty(username)) {
            if (!TextUtils.isEmpty(password1)) {
                if (password1.equals(password2)) {
                    user.setUno(userno);
                    user.setUname(username);
                    user.setUpassword(password1);
                    user.setUclass(userclass);
                    user.setUpermission(0);
                    user.setUcredit(0);

                    submitRegister(user);
                } else {
                    Toast.makeText(this, "请输入相同的密码", Toast.LENGTH_SHORT).show();
                    editTextRPwd.setText("");
                }
            } else {
                Toast.makeText(this, "输入密码不能为空", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "学号或姓名不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitRegister(final User user) {
        final String url = NetURLUtils.getURLpath(this, "RegisterServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //注册成功
                            UserClass userClass = getUserClassByCno(user.getUclass());

                            user.setUclassname(userClass.getCname());
                            user.setUschool(userClass.getCschool());
                            user.setUteacher(userClass.getCteacher());

                            userDao.insertOrReplace(user);

                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            RegisterActivity.this.onBackPressed();//返回登录页面
                        } else if (result == 1) {
                            //该学号已被注册
                            Toast.makeText(RegisterActivity.this, "该学号已被注册", Toast.LENGTH_SHORT).show();
                        } else {
                            //注册失败
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(RegisterActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("uno", user.getUno());
                map.put("name", user.getUname());
                map.put("pwd", user.getUpassword());
                map.put("uclass", user.getUclass());

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    private static String[] CLASSES;

    private void initViews() {
        ;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CLASSES);
        spinner.setAdapter(adapter);
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("注册");
        }
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.onBackPressed();
            }
        });
    }

}
