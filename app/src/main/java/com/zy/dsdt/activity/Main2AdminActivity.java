package com.zy.dsdt.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zy.dsdt.R;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserClass;
import com.zy.dsdt.bean.UserClassDao;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.fragment_manage.AddUserClassFragment;
import com.zy.dsdt.fragment_manage.ManageHomeFragment;
import com.zy.dsdt.fragment_manage.ManageQuestionFragment;
import com.zy.dsdt.fragment_manage.ManageUserClassFragment;
import com.zy.dsdt.fragment_manage.ManageUserFragment;
import com.zy.dsdt.fragment_manage.ShowUserClassFragment;
import com.zy.dsdt.fragment_manage.ShowUserFragment;
import com.zy.dsdt.fragment_manage.UpdateUserClassFragment;
import com.zy.dsdt.fragment_manage.UpdateUserFragment;
import com.zy.dsdt.utils.DBManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Main2AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.home_drawer)
    DrawerLayout drawer;
    TextView tvName;
    TextView tvClass;
    TextView tvCredit;

    private User user;

    private DBManager manager;
    private UserClassDao userClassDao;
    private UserDao userDao;

    private ManageHomeFragment manageHomeFragment;
    private ManageUserClassFragment manageUserClassFragment;
    private ShowUserClassFragment showUserClassFragment;
    private UpdateUserClassFragment updateUserClassFragment;
    private ManageQuestionFragment manageQuestionFragment;
    private ManageUserFragment manageUserFragment;
    private ShowUserFragment showUserFragment;
    private UpdateUserFragment updateUserFragment;
    private AddUserClassFragment addUserClassFragment;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initTextView();
        }
    };

    private void initTextView() {
        tvName = (TextView) findViewById(R.id.tv_main_username);
        tvClass = (TextView) findViewById(R.id.tv_main_userclass);
        tvCredit = (TextView) findViewById(R.id.tv_main_usercrdits);

        tvName.setText("用户名:" + user.getUname());
        tvClass.setText("班级:" + user.getUclassname());
        tvCredit.setText("积分:" + user.getUcredit());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        userClassDao = manager.getUserClassDao();
        userDao = manager.getUserDao();

        Intent intent = getIntent();
        String uno = intent.getStringExtra("uno");

        user = userDao.queryBuilder().where(UserDao.Properties.Uno.eq(uno)).unique();

        initToolBar();
        initFragment();

        handler.sendEmptyMessage(0);

        showHome();
    }

    private void initFragment() {
        manageHomeFragment = new ManageHomeFragment();
    }

    /**
     * 搜索操作
     *
     * @param searchtext
     */
    public void search(String searchtext) {
        if (!TextUtils.isEmpty(searchtext)) {
            List<User> searchresult = new ArrayList<User>();

            //学号搜索
            searchresult.
                    addAll(
                            userDao.
                                    queryBuilder().
                                    where(UserDao.
                                            Properties.Uno.eq("%" + searchtext + "%")).
                                    list());

            //姓名搜索
            searchresult.
                    addAll(
                            userDao.
                                    queryBuilder().
                                    where(UserDao.Properties.Uname.like("%" + searchtext + "%")).
                                    list());

            //班级搜索
            searchresult.addAll(
                    userDao.
                            queryBuilder().
                            where(UserDao.Properties.Uclassname.like("%" + searchtext + "%")).
                            list());

            //教师搜索
            searchresult.addAll(
                    userDao.
                            queryBuilder().
                            where(UserDao.Properties.Uteacher.like("%" + searchtext + "%")).
                            list());

            //学校搜索
            searchresult.addAll(
                    userDao.
                            queryBuilder().
                            where(UserDao.Properties.Uschool.like("%" + searchtext + "%")).
                            list());

            //积分搜索

            try {
                int credit = Integer.parseInt(searchtext);

                searchresult.addAll(
                        userDao.
                                queryBuilder().
                                where(UserDao.Properties.Ucredit.between(credit, Integer.MAX_VALUE)).
                                list());
            } catch (NumberFormatException e) {

            }


            showUserFragment = new ShowUserFragment();
            showUserFragment.initData(this, searchresult);
            changeContentInManage(showUserFragment);
        } else {
            showUserInManage();
        }
    }

    public void showUpdateUser(User user) {
        updateUserFragment=new UpdateUserFragment();
        updateUserFragment.initData(this, user);
        changeContent(updateUserFragment);
    }

    public void showUpdateUserClass(UserClass userClass) {
        updateUserClassFragment=new UpdateUserClassFragment();
        updateUserClassFragment.initData(this, userClass);
        changeContentInManage(updateUserClassFragment);
    }

    public void showUserClassInManage() {
        showUserClassFragment=new ShowUserClassFragment();
        showUserClassFragment.initData(this, userClassDao.loadAll());
        changeContentInManage(showUserClassFragment);
    }

    public void showAddUserClassInManage() {
        addUserClassFragment=new AddUserClassFragment();
        addUserClassFragment.initData(this);
        changeContentInManage(addUserClassFragment);
    }

    public void showUserInManage() {
        showUserFragment=new ShowUserFragment();
        showUserFragment.initData(this, userDao.loadAll());
        changeContentInManage(showUserFragment);
    }

    public void showManageUserClass() {
        manageUserClassFragment=new ManageUserClassFragment();
        changeContent(manageUserClassFragment);
    }

    public void showManageQuestion() {
        manageQuestionFragment=new ManageQuestionFragment();
        manageQuestionFragment.initData(this);
        changeContent(manageQuestionFragment);
    }

    public void showManageUser() {
        manageUserFragment=new ManageUserFragment();
        changeContent(manageUserFragment);
    }

    public void showHome() {
        changeContent(manageHomeFragment);
    }

    /**
     * 修改显示界面
     */
    private void changeContent(Fragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.content_frame, fragment).
                commit();
    }

    /**
     * 修改管理界面内部的界面
     */
    private void changeContentInManage(Fragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.manage_frame_content, fragment).
                commit();
    }


    private void initToolBar() {
        //开启ActionBar上APP ICON的功能
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("系统管理");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTranslucentStatus(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ((id == R.id.manage_home)) {
            //首页
            showHome();
        } else if (id == R.id.manage_userclass) {
            //班级管理
            showManageUserClass();
        } else if (id == R.id.manage_user) {
            //用户管理
            showManageUser();
        } else if (id == R.id.manage_question) {
            //题目管理
            showManageQuestion();
        } else if (id == R.id.manage_logout) {
            //退出管理
            Intent intent = new Intent(Main2AdminActivity.this, Main2UserActivity.class);

            intent.putExtra("uno", user.getUno());

            startActivity(intent);
            finish();
        } else if (id == R.id.manage_logout_teacher) {
            // 退出到教师登陆
            Intent intent = new Intent(Main2AdminActivity.this, Main2TeacherActivity.class);

            intent.putExtra("uno", user.getUno());

            startActivity(intent);
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        getSupportActionBar().setTitle(item.getTitle());

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }
}
