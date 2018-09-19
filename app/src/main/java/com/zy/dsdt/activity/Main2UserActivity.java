package com.zy.dsdt.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zy.dsdt.R;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.fragment.ChangeInfoFragment;
import com.zy.dsdt.fragment.ChapterQuestionFragment;
import com.zy.dsdt.fragment.ExamTestFragment;
import com.zy.dsdt.fragment.Home4UserFragment;
import com.zy.dsdt.fragment.MyGradeFragment;
import com.zy.dsdt.fragment.QuestionSettingFragment;
import com.zy.dsdt.fragment.RandomQuestionFragment;
import com.zy.dsdt.fragment.SequentialQuestionFragment;
import com.zy.dsdt.fragment.WrongQuestionFragment;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.view.CircleImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Main2UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.home_drawer)
    DrawerLayout drawer;

    CircleImageView header;
    TextView tvUserName;
    TextView tvUserClass;
    TextView tvUserCredit;

    public static boolean isLogin = false;
    private Home4UserFragment home4UserFragment;
    private SequentialQuestionFragment sequentialQuestionFragment;
    private ChapterQuestionFragment chapterQuestionFragment;
    private RandomQuestionFragment randomQuestionFragment;
    private ExamTestFragment examTestFragment;
    private WrongQuestionFragment wrongQuestionFragment;
    private MyGradeFragment myGradeFragment;
    private ChangeInfoFragment changeInfoFragment;
    private QuestionSettingFragment questionSettingFragment;

    private User user;
    private DBManager manager;
    private UserDao userDao;
    public static String uno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        userDao = manager.getUserDao();

        Intent intent = getIntent();

        uno = intent.getStringExtra("uno");
        if (!TextUtils.isEmpty(uno)) {
            isLogin = true;
        }

        initToolBar();
        initFragment();

        showHome();
    }

    @Override
    protected void onStop() {
        isLogin = false;
        uno = "";
        super.onStop();
    }

    @Override
    protected void onRestart() {

        if (user != null) {
            isLogin = true;
            uno = user.getUno();
        } else {
            uno = "";
            isLogin = false;
        }

        super.onRestart();
    }

    private User getUserByUno(String uno) {
        return userDao.
                queryBuilder().
                where(UserDao.Properties.Uno.eq(uno)).
                list().
                get(0);
    }

    private void initViews() {
        header = (CircleImageView) findViewById(R.id.iv_header);

        tvUserName = (TextView) findViewById(R.id.tv_main_username);
        tvUserClass = (TextView) findViewById(R.id.tv_main_userclass);
        tvUserCredit = (TextView) findViewById(R.id.tv_main_usercrdits);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2UserActivity.this, LoginActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            //登录成功返回的结果码
            String uno = data.getStringExtra("uno");

            user = getUserByUno(uno);

            isLogin = true;
            initUserData();
        }
    }

    private void initUserData() {
        tvUserName.setText("用户名:" + user.getUname());
        tvUserClass.setText("班级:" + user.getUclassname());
        tvUserCredit.setText("积分:" + user.getUcredit());
    }

    private void initFragment() {
        home4UserFragment = new Home4UserFragment();
        sequentialQuestionFragment = new SequentialQuestionFragment();
        chapterQuestionFragment = new ChapterQuestionFragment();
        randomQuestionFragment = new RandomQuestionFragment();
        examTestFragment = new ExamTestFragment();
        myGradeFragment = new MyGradeFragment();
        changeInfoFragment = new ChangeInfoFragment();
    }

    private void initToolBar() {
        //开启ActionBar上APP ICON的功能
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTranslucentStatus(true);
        drawer.setDrawerListener(this);
    }

    public void goToQuestionActivity(int QUESTION_MODE) {
        Intent intent = new Intent(this, QuestionActivity.class);

        intent.putExtra("QUESTION_MODE", QUESTION_MODE);
        if (user == null) {
            intent.putExtra("useracount", "");
        } else {
            intent.putExtra("useracount", user.getUno());
        }
        startActivity(intent);
    }

    public void showSequentialQuestion() {
        getSupportActionBar().setTitle("顺序答题");
        sequentialQuestionFragment.initData(this);
        changeContent(sequentialQuestionFragment);
    }

    public void showChapterQuestion() {
        getSupportActionBar().setTitle("章节答题");
        chapterQuestionFragment.initData(this);
        changeContent(chapterQuestionFragment);
    }

    public void showRandomQuestion() {
        getSupportActionBar().setTitle("随机答题");
        randomQuestionFragment.initData(this);
        changeContent(randomQuestionFragment);
    }

    public void showExamTest() {
        getSupportActionBar().setTitle("模拟考试");
        changeContent(examTestFragment);
    }

    public void showWrongQuestion() {
        getSupportActionBar().setTitle("我的错题");
        wrongQuestionFragment = new WrongQuestionFragment();
        wrongQuestionFragment.initData(this);
        changeContent(wrongQuestionFragment);
    }

    public void showMyGrade() {
        getSupportActionBar().setTitle("我的成绩");
        myGradeFragment.initData(this);
        changeContent(myGradeFragment);
    }

    public void showQuestionSetting() {
        questionSettingFragment = new QuestionSettingFragment();
        questionSettingFragment.initData(this);
        changeContent(questionSettingFragment);
    }

    public void showChangeInfo() {
        getSupportActionBar().setTitle("修改信息");
        if (user == null) {
            Toast.makeText(this, "请登录后再来修改", Toast.LENGTH_SHORT).show();
        } else {
            changeInfoFragment.initData(this, user);
            changeContent(changeInfoFragment);
        }
    }

    public void showHome() {
        drawer.setSelected(true);
        home4UserFragment.initData(this);
        changeContent(home4UserFragment);
    }

    public void changeContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        boolean flag = false;
        if (id == R.id.back_home) {
            //返回首页
            showHome();
            flag = true;
        } else if (id == R.id.sequential_question) {
            //顺序答题
            showSequentialQuestion();
            flag = true;
        } else if (id == R.id.chapter_question) {
            //章节答题
            showChapterQuestion();
            flag = true;
        } else if (id == R.id.random_question) {
            //随机答题
            showRandomQuestion();
            flag = true;
        } else if (id == R.id.exam_test) {
            //模拟考试
            showExamTest();
            flag = true;
        } else if (id == R.id.wrong_question) {
            //我的错题
            showWrongQuestion();
            flag = true;
        } else if (id == R.id.my_grade) {
            //我的成绩
            showMyGrade();
            flag = true;
        } else if (id == R.id.change_info) {
            //修改资料
            showChangeInfo();
            flag = true;
        } else if (id == R.id.question_setting) {
            //题目抽题设置
            showQuestionSetting();
        } else if (id == R.id.about_application) {
            //关于应用
            startActivity(new Intent(this, AboutActivity.class));
            flag = false;
        }

        drawer.closeDrawer(GravityCompat.START);
        if (flag) {
            if (id == R.id.back_home) {
                getSupportActionBar().setTitle("数据结构答题系统");
            } else {
                getSupportActionBar().setTitle(item.getTitle());
            }
        }
        return flag;
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

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        initViews();

        if (!TextUtils.isEmpty(uno)) {
            user = userDao.queryBuilder().where(UserDao.Properties.Uno.eq(uno)).unique();
            initUserData();
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
