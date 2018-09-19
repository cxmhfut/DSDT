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
import com.zy.dsdt.activity.WrongQuestionActivity;

/**
 * Created by Chen on 2016/4/20.
 */
public class WrongQuestionFragment extends Fragment {
    private View view;
    private LinearLayout llStart;
    private String uno;
    private Context context;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wrong_question, container, false);

        llStart = (LinearLayout) view.findViewById(R.id.ll_wrong_question_start);

        llStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Main2UserActivity.isLogin) {
                    uno=Main2UserActivity.uno;
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
