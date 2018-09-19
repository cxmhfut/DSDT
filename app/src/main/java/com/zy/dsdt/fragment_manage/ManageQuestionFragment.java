package com.zy.dsdt.fragment_manage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.zy.dsdt.activity.Main2AdminActivity;
import com.zy.dsdt.bean.QuestionConfig;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.NetURLUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chen on 2016/5/14.
 */
public class ManageQuestionFragment extends Fragment {
    private View view;
    private Button btSubmit;
    private EditText etChoice, etBlank, etCChapter[], etBChapter[];
    private Context context;
    private QuestionConfig qc = null;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_managequestion, container, false);

        initViews();

        initQuestionConfig();

        if (qc != null) {
            etChoice.setText("" + qc.getChoiceQuestionWeight());
            etBlank.setText("" + qc.getBlankQuestionWeight());

            for (int i = 0; i < 9; i++) {
                etCChapter[i].setText("" + qc.getChchpaterweightList().get(i));
                etBChapter[i].setText("" + qc.getBlchapterweightList().get(i));
            }
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choiceStr = etChoice.getText().toString().trim();
                String blankStr = etBlank.getText().toString().trim();
                String cchaptersStr[] = new String[9];
                String bchaptersStr[] = new String[9];

                for (int i = 0; i < 9; i++) {
                    cchaptersStr[i] = etCChapter[i].getText().toString().trim();
                    bchaptersStr[i] = etBChapter[i].getText().toString().trim();
                }

                try {
                    int choice = Integer.parseInt(choiceStr);
                    int blank = Integer.parseInt(blankStr);

                    List<Integer> cchapters = new ArrayList<Integer>();
                    List<Integer> bchapters = new ArrayList<Integer>();

                    for (int j = 0; j < 9; j++) {
                        cchapters.add(Integer.parseInt(cchaptersStr[j]));
                        bchapters.add(Integer.parseInt(bchaptersStr[j]));
                    }

                    QuestionConfig qc = new QuestionConfig();

                    qc.setChoiceQuestionWeight(choice);
                    qc.setBlankQuestionWeight(blank);
                    qc.setChchpaterweightList(cchapters);
                    qc.setBlchapterweightList(bchapters);

                    submit(qc);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "请输入正确的数字", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void initQuestionConfig() {
        try {
            TrayAppPreferences preferences = new TrayAppPreferences(context);

            String value = preferences.getString("questionconfig", "");

            ObjectMapper mapper = new ObjectMapper();
            qc = mapper.readValue(value, QuestionConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "初始化题目分配信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit(final QuestionConfig qc) {
        String url = NetURLUtils.getURLpath(context, "UpdateQuestionConfig4AdminServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //设置成功

                            try {
                                TrayAppPreferences preferences = new TrayAppPreferences(context);

                                ObjectMapper mapper = new ObjectMapper();
                                String value = null;
                                value = mapper.writeValueAsString(qc);

                                preferences.put("questionconfig", value);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "保存题目分配信息数据失败", Toast.LENGTH_SHORT).show();
                            }


                            Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //设置失败
                            Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
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
                    String array = mapper.writeValueAsString(qc);
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
        btSubmit = (Button) view.findViewById(R.id.bt_qc_submit);
        etChoice = (EditText) view.findViewById(R.id.et_qc_choice);
        etBlank = (EditText) view.findViewById(R.id.et_qc_blank);

        etCChapter = new EditText[9];
        etBChapter = new EditText[9];

        etCChapter[0] = (EditText) view.findViewById(R.id.et_qc_choice_1);
        etCChapter[1] = (EditText) view.findViewById(R.id.et_qc_choice_2);
        etCChapter[2] = (EditText) view.findViewById(R.id.et_qc_choice_3);
        etCChapter[3] = (EditText) view.findViewById(R.id.et_qc_choice_4);
        etCChapter[4] = (EditText) view.findViewById(R.id.et_qc_choice_5);
        etCChapter[5] = (EditText) view.findViewById(R.id.et_qc_choice_6);
        etCChapter[6] = (EditText) view.findViewById(R.id.et_qc_choice_7);
        etCChapter[7] = (EditText) view.findViewById(R.id.et_qc_choice_8);
        etCChapter[8] = (EditText) view.findViewById(R.id.et_qc_choice_9);

        etBChapter[0] = (EditText) view.findViewById(R.id.et_qc_blank_1);
        etBChapter[1] = (EditText) view.findViewById(R.id.et_qc_blank_2);
        etBChapter[2] = (EditText) view.findViewById(R.id.et_qc_blank_3);
        etBChapter[3] = (EditText) view.findViewById(R.id.et_qc_blank_4);
        etBChapter[4] = (EditText) view.findViewById(R.id.et_qc_blank_5);
        etBChapter[5] = (EditText) view.findViewById(R.id.et_qc_blank_6);
        etBChapter[6] = (EditText) view.findViewById(R.id.et_qc_blank_7);
        etBChapter[7] = (EditText) view.findViewById(R.id.et_qc_blank_8);
        etBChapter[8] = (EditText) view.findViewById(R.id.et_qc_blank_9);
    }
}
