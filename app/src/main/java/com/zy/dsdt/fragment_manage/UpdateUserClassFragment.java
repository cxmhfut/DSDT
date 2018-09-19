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
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chen on 2016/5/15.
 */
public class UpdateUserClassFragment extends Fragment {
    private View view;

    private Context context;
    private UserClass userClass;
    private DBManager manager;
    private UserClassDao userClassDao;
    private Main2AdminActivity main;

    private Button btBack, btDelete, btUpdate;
    private MaterialBetterSpinner spinnerschool;
    private EditText etCname, etTeacher;

    public void initData(Context context, UserClass userClass) {
        this.context = context;
        this.userClass = userClass;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_updateuserclass, container, false);

        main = (Main2AdminActivity) getActivity();
        manager = new DBManager(context);
        userClassDao = manager.getUserClassDao();

        initViews();

        etCname.setText(userClass.getCname());
        etTeacher.setText(userClass.getCteacher());
        spinnerschool.setText(userClass.getCschool());

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
                builder.setMessage("确定删除" + userClass.getCname() + "?");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!userClass.getCno().equals("10701")) {
                            delete();
                        } else {
                            Toast.makeText(context, "不能删除该班级", Toast.LENGTH_SHORT).show();
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
                String cname = etCname.getText().toString().trim();
                String teacher = etTeacher.getText().toString().trim();

                if (!TextUtils.isEmpty(cname) && !TextUtils.isEmpty(teacher)) {
                    userClass.setCname(cname);
                    userClass.setCteacher(teacher);
                    userClass.setCschool(spinnerschool.getText().toString());

                    update();
                } else {
                    Toast.makeText(context, "班级名称或教师不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void update() {
        String url = NetURLUtils.getURLpath(context, "Update4UserClassServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //更新成功
                            userClassDao.update(userClass);
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
                    String array = mapper.writeValueAsString(userClass);
                    map.put("data", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, context);
    }

    private void delete() {
        String url = NetURLUtils.getURLpath(context, "Delete4UserClassServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //删除成功
                            userClassDao.delete(userClass);
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

                map.put("cno", userClass.getCno());

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, context);
    }

    private void back() {
        main.showManageUserClass();
    }

    private void initViews() {
        btBack = (Button) view.findViewById(R.id.updateuserclass_bt_back);
        btDelete = (Button) view.findViewById(R.id.updateuserclass_bt_delete);
        btUpdate = (Button) view.findViewById(R.id.updateuserclass_bt_update);
        spinnerschool = (MaterialBetterSpinner) view.findViewById(R.id.spinner_school);
        etCname = (EditText) view.findViewById(R.id.updateuserclass_et_cname);
        etTeacher = (EditText) view.findViewById(R.id.updateuserclass_et_teacher);

        ArrayAdapter<String> adapterschool = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.school));

        spinnerschool.setAdapter(adapterschool);

    }
}
