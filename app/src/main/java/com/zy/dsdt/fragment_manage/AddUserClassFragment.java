package com.zy.dsdt.fragment_manage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by Chen on 2016/5/16.
 */
public class AddUserClassFragment extends Fragment {
    private View view;
    private Button btAdd;
    private EditText etName, etGrade, etDNo, etCNo, etTeacher;
    private MaterialBetterSpinner spinnerschool;

    private DBManager manager;
    private UserClassDao userClassDao;
    private Main2AdminActivity main;

    private Context context;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adduserclass, container, false);

        main = (Main2AdminActivity) getActivity();
        manager = new DBManager(context);
        userClassDao = manager.getUserClassDao();

        initViews();

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cname = etName.getText().toString().trim();
                String grade = etGrade.getText().toString().trim();
                String dno = etDNo.getText().toString().trim();
                String etcno = etCNo.getText().toString().trim();
                String teacher = etTeacher.getText().toString().trim();

                if (!TextUtils.isEmpty(cname) && !TextUtils.isEmpty(dno) && !TextUtils.isEmpty(etcno)) {
                    String school = spinnerschool.getText().toString();

                    String cno = "";

                    if (school.equals("安庆师范大学")) {
                        cno = cno + "1";
                    } else {
                        cno = cno + "2";
                    }

                    cno = cno + grade + dno + etcno;//形成班级编号

                    UserClass userclass = new UserClass();

                    userclass.setCno(cno);
                    userclass.setCname(cname);
                    userclass.setCteacher(teacher);
                    userclass.setCschool(school);

                    addUserClass(userclass);

                } else {
                    Toast.makeText(context, "班级名称院系编号班级编号不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void addUserClass(final UserClass userclass) {
        String url = NetURLUtils.getURLpath(context, "AddUserClassServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //添加成功
                            userClassDao.insertOrReplace(userclass);
                            main.showUserClassInManage();
                            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                        } else if (result == 1) {
                            //该班级已存在
                            Toast.makeText(context, "该班级已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            //添加失败
                            Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
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
                    String array = mapper.writeValueAsString(userclass);
                    map.put("data", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, context);
    }

    private void initViews() {
        btAdd = (Button) view.findViewById(R.id.adduserclass_bt_add);

        etName = (EditText) view.findViewById(R.id.adduserclass_et_cname);
        etGrade = (EditText) view.findViewById(R.id.adduserclass_et_grade);
        etDNo = (EditText) view.findViewById(R.id.adduserclass_et_department);
        etCNo = (EditText) view.findViewById(R.id.adduserclass_et_class);
        etTeacher = (EditText) view.findViewById(R.id.adduserclass_et_teacher);

        spinnerschool = (MaterialBetterSpinner) view.findViewById(R.id.spinner_school);

        ArrayAdapter<String> adapterschool = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, context.getResources().getStringArray(R.array.school));

        spinnerschool.setAdapter(adapterschool);
    }
}
