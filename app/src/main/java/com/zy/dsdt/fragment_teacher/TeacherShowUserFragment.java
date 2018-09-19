package com.zy.dsdt.fragment_teacher;

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
import com.zy.dsdt.activity.Main2TeacherActivity;
import com.zy.dsdt.bean.User;

import java.util.List;

/**
 * Created by Chen on 2016/5/14.
 */
public class TeacherShowUserFragment extends Fragment {
    private View view;
    private ListView lvShow;
    private List<User> users;
    private Context context;
    private String[] permission;
    private Main2TeacherActivity main;

    public void initData(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show, container, false);

        main = (Main2TeacherActivity) getActivity();

        permission = context.getResources().getStringArray(R.array.permission);

        lvShow = (ListView) view.findViewById(R.id.lv_show);
        lvShow.setAdapter(new MyListViewAdapter());

        return view;
    }

    public class MyListViewAdapter extends BaseAdapter {
        private LinearLayout llItem;
        private TextView tvID, tvUno, tvUname, tvUclass, tvUper, tvUcredit;

        @Override
        public int getCount() {
            return users.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.list_item_user, null);

            llItem = (LinearLayout) view.findViewById(R.id.ll_item);

            tvID = (TextView) view.findViewById(R.id.tv_id);
            tvUno = (TextView) view.findViewById(R.id.tv_uno);
            tvUname = (TextView) view.findViewById(R.id.tv_uname);
            tvUclass = (TextView) view.findViewById(R.id.tv_uclass);
            tvUper = (TextView) view.findViewById(R.id.tv_uper);
            tvUcredit = (TextView) view.findViewById(R.id.tv_ucredit);

            if (position != 0) {
                int index = position - 1;
                final User user = users.get(index);

                tvID.setText("" + position);
                tvUno.setText(user.getUno());
                tvUname.setText(user.getUname());
                tvUclass.setText(user.getUclassname());
                tvUper.setText(permission[user.getUpermission()]);
                tvUcredit.setText("" + user.getUcredit());

                llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.showGradeUser(user);
                    }
                });
            }

            return view;
        }
    }
}
