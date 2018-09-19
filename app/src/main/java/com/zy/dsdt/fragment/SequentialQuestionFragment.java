package com.zy.dsdt.fragment;

import android.content.Context;
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
 * Created by Chen on 2016/4/18.
 */
public class SequentialQuestionFragment extends Fragment {
    private View view;
    private LinearLayout llStart;
    private Context context;
    private Main2UserActivity main;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sequential_question, container, false);

        main = (Main2UserActivity) getActivity();
        llStart = (LinearLayout) view.findViewById(R.id.ll_sequential_question_start);

        llStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToQuestionActivity(0);
            }
        });
        return view;
    }
}
