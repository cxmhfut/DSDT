package com.zy.dsdt.fragment_manage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2AdminActivity;

/**
 * Created by Chen on 2016/5/14.
 */
public class ManageUserClassFragment extends Fragment {
    private View view;
    private TabLayout tabLayout;
    private ListView lv_show;
    private Main2AdminActivity main;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manageuserclass, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tablayout_manage);

        main = (Main2AdminActivity) getActivity();

        tabLayout.addTab(tabLayout.newTab().setText("查看班级"));
        tabLayout.addTab(tabLayout.newTab().setText("增加班级"));

        main.showUserClassInManage();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("查看班级")) {
                    main.showUserClassInManage();
                } else {
                    main.showAddUserClassInManage();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

        return view;
    }
}
