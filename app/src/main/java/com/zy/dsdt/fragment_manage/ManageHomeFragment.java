package com.zy.dsdt.fragment_manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.AboutActivity;
import com.zy.dsdt.activity.Main2AdminActivity;

/**
 * Created by Teresa on 2016/5/19.
 */
public class ManageHomeFragment extends Fragment {

    private View view;
    private LinearLayout llQuestion;
    private LinearLayout llClass;
    private LinearLayout llUser;
    private LinearLayout llAbout;

    private Main2AdminActivity main;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_home, container, false);

        llQuestion = (LinearLayout) view.findViewById(R.id.manage_home_question);
        llClass = (LinearLayout) view.findViewById(R.id.manage_home_class);
        llUser = (LinearLayout) view.findViewById(R.id.manage_home_user);
        llAbout = (LinearLayout) view.findViewById(R.id.manage_home_about);

        main = (Main2AdminActivity) getActivity();

        llQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showManageQuestion();
            }
        });

        llClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showManageUserClass();
            }
        });

        llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.showManageUser();
            }
        });

        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });

        return view;
    }

}
