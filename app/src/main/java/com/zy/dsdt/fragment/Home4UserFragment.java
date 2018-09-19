package com.zy.dsdt.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2UserActivity;
import com.zy.dsdt.activity.MyGradeActivity;
import com.zy.dsdt.activity.WrongQuestionActivity;

/**
 * Created by Chen on 2016/4/20.
 */
public class Home4UserFragment extends Fragment {
    private View view;
    private LinearLayout tvSequential;
    private LinearLayout tvRandom;
    private LinearLayout tvWrong;
    private LinearLayout tvChapter;
    private LinearLayout tvTest;
    private LinearLayout tvGrade;
    private String uno;
    private Context context;

    private Main2UserActivity main;

    public void initData(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_home, container, false);

        tvSequential = (LinearLayout) view.findViewById(R.id.sequential_home);
        tvRandom = (LinearLayout) view.findViewById(R.id.random_home);
        tvWrong = (LinearLayout) view.findViewById(R.id.wrong_home);
        tvChapter = (LinearLayout) view.findViewById(R.id.chapter_home);
        tvTest = (LinearLayout) view.findViewById(R.id.test_home);
        tvGrade = (LinearLayout) view.findViewById(R.id.grade_home);

        main = (Main2UserActivity) getActivity();

        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showExamTest();
            }
        });

        tvSequential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showSequentialQuestion();
            }
        });

        tvChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showChapterQuestion();
            }
        });

        tvRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showRandomQuestion();
            }
        });

        tvWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showWrongQuestion();
            }
        });

        tvGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main2UserActivity.isLogin) {
                    Intent intent = new Intent(getActivity(), MyGradeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "您还未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main2UserActivity.isLogin) {
                    uno = Main2UserActivity.uno;
                    Intent intent = new Intent(getActivity(), WrongQuestionActivity.class);
                    intent.putExtra("uno", uno);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "您还未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
