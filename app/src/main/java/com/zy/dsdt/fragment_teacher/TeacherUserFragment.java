package com.zy.dsdt.fragment_teacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2TeacherActivity;

/**
 * Created by Chen on 2016/5/14.
 */
public class TeacherUserFragment extends Fragment {
    private View view;
    private Main2TeacherActivity main;
    private EditText etSearch;
    private ImageView ivSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manageuser, container, false);

        etSearch = (EditText) view.findViewById(R.id.et_search);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);

        main = (Main2TeacherActivity) getActivity();

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext = etSearch.getText().toString().trim();

                main.search(searchtext);
            }
        });

        main.showUserInManage();

        return view;
    }
}
