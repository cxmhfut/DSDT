package com.zy.dsdt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zy.dsdt.R;

import net.grandcentrix.tray.TrayAppPreferences;

/**
 * Created by Chen on 2016/5/19.
 */
public class QuestionSettingFragment extends Fragment {
    private View view;
    private Context context;
    private EditText etRandom, etExam;
    private int random, exam;
    private Button btSave;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questionsetting, container, false);

        iniViews();
        initQuestionData();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomStr=etRandom.getText().toString().trim();
                String examStr=etExam.getText().toString().trim();

                try {
                    random=Integer.parseInt(randomStr);
                    exam=Integer.parseInt(examStr);

                    if(random<=100 && exam<=100){
                        save();
                    }else{
                        Toast.makeText(context,"抽题数不可超过100",Toast.LENGTH_SHORT).show();
                    }

                }catch (NumberFormatException e){
                    Toast.makeText(context,"请输入正确的数字",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void save() {
        TrayAppPreferences preferences = new TrayAppPreferences(context);

        preferences.put("random",random);
        preferences.put("exam",exam);

        Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
    }

    private void initQuestionData() {
        TrayAppPreferences preferences = new TrayAppPreferences(context);

        random = preferences.getInt("random", 20);
        exam = preferences.getInt("exam", 30);

        etRandom.setText("" + random);
        etExam.setText("" + exam);
    }

    private void iniViews() {
        etRandom = (EditText) view.findViewById(R.id.et_random);
        etExam = (EditText) view.findViewById(R.id.et_exam);
        btSave= (Button) view.findViewById(R.id.bt_save);
    }


}
