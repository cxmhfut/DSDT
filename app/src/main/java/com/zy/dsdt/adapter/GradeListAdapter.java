package com.zy.dsdt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zy.dsdt.R;
import com.zy.dsdt.bean.QuestionLog;

import java.util.List;

/**
 * Created by Teresa on 2016/5/19.
 */
public class GradeListAdapter extends BaseAdapter {

    private TextView tvScore;
    private TextView tvModule;
    private TextView tvTime;

    private Context context;
    private List<QuestionLog> questionLogs;

    public GradeListAdapter(Context context, List<QuestionLog> questionLogs) {
        this.context = context;
        this.questionLogs = questionLogs;
    }

    @Override
    public int getCount() {
        return questionLogs.size();
    }

    @Override
    public Object getItem(int position) {
        return questionLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.list_item_question_log, null);

        tvScore = (TextView) view.findViewById(R.id.grade_list_score);
        tvModule = (TextView) view.findViewById(R.id.grade_list_module);
        tvTime = (TextView) view.findViewById(R.id.grade_list_time);

        tvScore.setText("" + questionLogs.get(position).getQscore());
        tvModule.setText("" + context.getResources().getStringArray(R.array.qmodule)[questionLogs.get(position).getQmodule()]);
        tvTime.setText("" + questionLogs.get(position).getQtime());

        return view;
    }
}
