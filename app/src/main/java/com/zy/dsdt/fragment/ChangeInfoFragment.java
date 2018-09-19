package com.zy.dsdt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chen on 2016/4/20.
 */
public class ChangeInfoFragment extends Fragment {
    private View view;
    private Context context;
    private User user;
    private EditText etName, etFPwd, etCPwd;
    private Button btupdate;

    public void initData(Context context, User user) {
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_info, container, false);

        etName = (EditText) view.findViewById(R.id.et_change_info_name);
        etFPwd = (EditText) view.findViewById(R.id.et_change_info_fpwd);
        etCPwd = (EditText) view.findViewById(R.id.et_change_info_cpwd);
        btupdate = (Button) view.findViewById(R.id.bt_change_info_update);

        etName.setText(user.getUname());

        btupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = etFPwd.getText().toString().trim();
                String pwd2 = etCPwd.getText().toString().trim();
                String name = etName.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd1)) {
                    if (pwd1.equals(pwd2)) {

                        user.setUname(name);
                        user.setUpassword(pwd1);

                        updateUserInfo();
                    } else {
                        etCPwd.setText("");
                        Toast.makeText(context, "请输入相同的密码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    private void updateUserInfo() {
        String url = NetURLUtils.getURLpath(context, "UpdateUserInfoServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //更新成功
                            DBManager manager = new DBManager(context);
                            manager.getUserDao().update(user);
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
}
