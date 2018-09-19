package com.zy.dsdt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2UserActivity;

/**
 * Created by Chen on 2016/4/19.
 */
public class ExamTestFragment extends Fragment {
    private View view;
    private LinearLayout llStart;
    private Main2UserActivity main;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exam_test, container, false);

        main = (Main2UserActivity) getActivity();
        llStart = (LinearLayout) view.findViewById(R.id.ll_test_start);

        llStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToQuestionActivity(11);
            }
        });
        return view;
    }
}
