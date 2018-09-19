package com.zy.dsdt.fragment_manage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zy.dsdt.R;
import com.zy.dsdt.activity.Main2AdminActivity;
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.utils.DBManager;

import java.util.List;

/**
 * Created by Chen on 2016/5/14.
 */
public class ShowUserClassFragment extends Fragment {
    private View view;
    private ListView lvShow;
    private List<UserClass> userClasses;
    private Context context;
    private Main2AdminActivity main;

    public void initData(Context context, List<UserClass> userClasses) {
        this.userClasses = userClasses;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show, container, false);

        main = (Main2AdminActivity) getActivity();

        lvShow = (ListView) view.findViewById(R.id.lv_show);
        lvShow.setAdapter(new MyListViewAdapter());

        return view;
    }


    public class MyListViewAdapter extends BaseAdapter {
        private LinearLayout llItem;
        private TextView tvID, tvCno, tvCname, tvCschool, tvCteacher;

        @Override
        public int getCount() {
            return userClasses.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return userClasses.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View viewitem = View.inflate(context, R.layout.list_item_userclass, null);

            llItem = (LinearLayout) viewitem.findViewById(R.id.ll_item);

            tvID = (TextView) viewitem.findViewById(R.id.tv_id);
            tvCno = (TextView) viewitem.findViewById(R.id.tv_cno);
            tvCname = (TextView) viewitem.findViewById(R.id.tv_cname);
            tvCschool = (TextView) viewitem.findViewById(R.id.tv_cschool);
            tvCteacher = (TextView) viewitem.findViewById(R.id.tv_cteacher);

            if (position != 0) {
                int index = position - 1;

                final UserClass userClass = userClasses.get(index);

                tvID.setText("" + position);
                tvCno.setText(userClass.getCno());
                tvCname.setText(userClass.getCname());
                tvCschool.setText(userClass.getCschool());
                tvCteacher.setText(userClass.getCteacher());

                llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.showUpdateUserClass(userClass);
                    }
                });
            }

            return viewitem;
        }
    }
}
