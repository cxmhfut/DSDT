package com.zy.dsdt.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2UserActivity;
import com.zy.dsdt.adapter.RecentsAdapter;
import com.zy.dsdt.view.RecentsList;

public class ChapterQuestionFragment extends Fragment {
    private View view;
    private Context context;
    private TextView[] tv;
    private Main2UserActivity main;
    private int chapter = 0;

    public void initData(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chapter_question, container, false);

        main = (Main2UserActivity) getActivity();

        final Button btChapterBegin = (Button) view.findViewById(R.id.bt_chapter_begin);

        btChapterBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.goToQuestionActivity(chapter + 1);
            }
        });

        final int[] colors = new int[] {
                0xff7fffff,
                0xffff7fff,
                0xffffff7f,
                0xff7f7fff,
                0xffff7f7f,
                0xff7fff7f,
                0xff7fff7f,
                0xff7fff7f,
                0xff7fff7f,
        };

        final String[] chaptername = context.getResources().getStringArray(R.array.chapter);
        RecentsList recents = (RecentsList) view.findViewById(R.id.recents);
        recents.setAdapter(new RecentsAdapter() {
            @Override
            public String getTitle(int position) {
                return "第" + (++position) + "章 " + chaptername[position - 1];
            }

            @Override
            public View getView(int position) {
                TextView tv = new TextView(getActivity());
                tv.setText(chaptername[position]);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                return tv;
            }

            @Override
            public Drawable getIcon(int position) {
                return getResources().getDrawable(R.drawable.chapter_icon);
            }

            @Override
            public int getHeaderColor(int position) {
                return colors[position];
            }

            @Override
            public int getCount() {
                return 9;
            }
        });

        recents.setOnItemClickListener(new RecentsList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                btChapterBegin.setText("开始第" + (++pos) + "章节");
                chapter = pos-1;
            }
        });

        return view;
    }
}
