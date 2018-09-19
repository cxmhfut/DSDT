package com.zy.dsdt.fragment_manage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.zy.dsdt.activity.Main2AdminActivity;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chen on 2016/5/15.
 */
public class UpdateUserFragment extends Fragment {
    private View view;
    private Context context;
    private User user;
    private EditText etName, etPwd, etTeacher, etCredit;
    private MaterialBetterSpinner spinnerClass, spinnerPermission;

    private DBManager manager;
    private UserClassDao userClassDao;
    private UserDao userDao;
    private String[] CLASSES;

    private Button btBack, btDelete, btUpdate;

    private Main2AdminActivity main;

    public void initData(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_updateuser, container, false);

        initView();
        etName.setText(user.getUname());
        etPwd.setText(user.getUpassword());
        etTeacher.setText(user.getUteacher());
        etCredit.setText("" + user.getUcredit());

        etTeacher.setEnabled(false);

        spinnerClass.setText(user.getUclassname());
        spinnerPermission.setText(context.getResources().getStringArray(R.array.permission)[user.getUpermission()]);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("警告");
                builder.setMessage("确定删除" + user.getUname() + "?");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!user.getUno().equals("070011")) {
                            //不允许删除超级管理员
                            delete();
                        } else {
                            Toast.makeText(context, "该用户不允许删除", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = etName.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                String creditStr = etCredit.getText().toString().trim();
                String userclass = spinnerClass.getText().toString();
                String permissionStr = spinnerPermission.getText().toString();

                if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(pwd)) {
                    try {
                        int credit = Integer.parseInt(creditStr);

                        if (credit >= 0) {
                            user.setUname(uname);
                            user.setUpassword(pwd);
                            user.setUcredit(credit);
                            user.setUclass(getCnoByCname(userclass));
                            user.setUclassname(userclass);

                            int per = 0;
                            if (permissionStr.equals("学生")) {
                                per = 0;
                            } else if (permissionStr.equals("教师")) {
                                per = 1;
                            } else if (permissionStr.equals("管理员")) {
                                per = 2;
                            }

                            user.setUpermission(per);

                            update();
                        } else {
                            Toast.makeText(context, "请输入正确的积分", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "请输入正确的积分", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
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

    private void update() {
        String url = NetURLUtils.getURLpath(context, "Update4UserServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //更新成功
                            userDao.update(user);
                            back();
                            Toast.makeText(context, "更新成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //更新失败
                            Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                ObjectMapper mapper = new ObjectMapper();

                try {
                    String array = mapper.writeValueAsString(user);
                    map.put("data", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, context);
    }

    private void back() {
        main.showManageUser();
    }

    private void delete() {
        final String url = NetURLUtils.getURLpath(context, "Delete4UserServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //删除成功
                            userDao.delete(user);
                            back();
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //删除失败
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("uno", user.getUno());
                return map;
            }
        };

        RequestManager.addRequest(stringRequest, context);
    }

    private void initView() {
        etName = (EditText) view.findViewById(R.id.updateuser_et_name);
        etPwd = (EditText) view.findViewById(R.id.updateuser_et_pwd);
        etTeacher = (EditText) view.findViewById(R.id.updateuser_et_teacher);
        etCredit = (EditText) view.findViewById(R.id.updateuser_et_credit);
        btBack = (Button) view.findViewById(R.id.updateuser_bt_back);
        btDelete = (Button) view.findViewById(R.id.updateuser_bt_delete);
        btUpdate = (Button) view.findViewById(R.id.updateuser_bt_update);
        spinnerClass = (MaterialBetterSpinner) view.findViewById(R.id.spinner_userclass);
        spinnerPermission = (MaterialBetterSpinner) view.findViewById(R.id.spinner_permission);

        main = (Main2AdminActivity) getActivity();
        manager = new DBManager(context);
        userClassDao = manager.getUserClassDao();
        userDao = manager.getUserDao();

        List<UserClass> userClasses = userClassDao.loadAll();

        CLASSES = new String[userClasses.size()];

        for (int i = 0; i < userClasses.size(); i++) {
            CLASSES[i] = userClasses.get(i).getCname();
        }

        ArrayAdapter<String> adapterclass = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, CLASSES);
        ArrayAdapter<String> adapterpermission = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.permission));

        spinnerClass.setAdapter(adapterclass);
        spinnerPermission.setAdapter(adapterpermission);
    }
}
