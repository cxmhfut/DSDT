package com.zy.dsdt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dd.CircularProgressButton;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.zy.dsdt.R;
import com.zy.dsdt.bean.BlankQuestion;
import com.zy.dsdt.bean.BlankQuestionPDao;
import com.zy.dsdt.bean.ChoiceQuestion;
import com.zy.dsdt.bean.ChoiceQuestionDao;
import com.zy.dsdt.bean.QuestionConfig;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.UserDao;
import com.zy.dsdt.bean.WrongQuestion;
import com.zy.dsdt.bean.WrongQuestionDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.BlankQuestionUtils;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;
import com.zy.dsdt.utils.QuestionUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.drakeet.materialdialog.MaterialDialog;

public class QuestionActivity extends AppCompatActivity {

    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.question_fab_menu)
    FloatingActionMenu floatingActionMenu;
    @InjectView(R.id.fab_item_apply)
    FloatingActionButton floatingActionApply;
    @InjectView(R.id.fab_item_finish)
    FloatingActionButton floatingActionDownload;
    @InjectView(R.id.vp_showquestion)
    ViewPager vpShowQuestion;
    @InjectView(R.id.tv_progress)
    TextView tv_progress;

    private List<ChoiceQuestion> choiceQuestions;
    private List<BlankQuestion> blankQuestions;
    private List<View> views;
    private QuestionViewPagerAdapter adapter;
    private int selected;

    boolean isRight = true;
    private User user;
    private String useracount;
    private int QUESTION_MODE;

    private DBManager manager;
    private ChoiceQuestionDao choiceQuestionDao;
    private BlankQuestionPDao blankQuestionPDao;
    private WrongQuestionDao wrongQuestionDao;
    private UserDao userDao;

    private int C_rightCount = 0;
    private int C_wrongcount = 0;
    private int B_rightcount = 0;
    private int B_wrongcount = 0;

    private float chaptercount[] = new float[9];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        choiceQuestionDao = manager.getChoiceQuestionDao();
        blankQuestionPDao = manager.getBlankQuestionPDao();
        wrongQuestionDao = manager.getWrongQuestionDao();
        userDao = manager.getUserDao();

        initToolbar();
        initFab();
        initData();
        initViews();
    }

    private void initViews() {
        adapter = new QuestionViewPagerAdapter(choiceQuestions, blankQuestions);
        vpShowQuestion.setAdapter(adapter);

        tv_progress.setText("答题进度 1/" + views.size());

        vpShowQuestion.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tv_progress.setText("答题进度 " + (position + 1) + "/" + views.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void initData() {
        final Intent intent = getIntent();

        QUESTION_MODE = intent.getIntExtra("QUESTION_MODE", 0);
        useracount = intent.getStringExtra("useracount");

        //用户号不为空时加载用户信息
        if (!TextUtils.isEmpty(useracount)) {
            user = userDao.queryBuilder().where(UserDao.Properties.Uno.eq(useracount)).unique();
        }


        choiceQuestions = new ArrayList<ChoiceQuestion>();
        blankQuestions = new ArrayList<BlankQuestion>();

        TrayAppPreferences preferences = new TrayAppPreferences(this);

        if (QUESTION_MODE == 0) {
            //顺序答题
            choiceQuestions = choiceQuestionDao.loadAll();
            blankQuestions = BlankQuestionUtils.getBlankQuestionList(blankQuestionPDao.loadAll());
        } else if (QUESTION_MODE >= 1 && QUESTION_MODE <= 9) {
            //章节答题
            choiceQuestions = getChoiceQuestionsByChapter(QUESTION_MODE);
            blankQuestions = getBlankQuestionsByChapter(QUESTION_MODE);
        } else if (QUESTION_MODE == 10) {
            //随机答题
            int random = preferences.getInt("random", 20);
            drawQuestion(random);
        } else if (QUESTION_MODE == 11) {
            //模拟考试
            int exam = preferences.getInt("exam", 30);
            drawQuestion(exam);
        } else if (QUESTION_MODE == 12) {
            //我的错题
        }
    }

    private void submitWrongQuestion(final String uno, final String qno, final int type) {
        String url = NetURLUtils.getURLpath(this, "AddWrongQuestionServlet");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //添加错题成功
                            WrongQuestion wq = new WrongQuestion(qno, uno, type);
                            wrongQuestionDao.insertOrReplace(wq);
                            Toast.makeText(QuestionActivity.this, "添加错题成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //添加错题失败
                            Toast.makeText(QuestionActivity.this, "添加错题失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (user != null) {
                    Toast.makeText(QuestionActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("uno", uno);
                map.put("qno", qno);
                map.put("type", "" + type);

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    /**
     * 抽取题目方法
     */
    private void drawQuestion(int count) {
        try {
            //读取题目分配配置
            TrayAppPreferences preferences = new TrayAppPreferences(this);
            String value = preferences.getString("questionconfig", "");
            ObjectMapper mapper = new ObjectMapper();
            QuestionConfig qc = mapper.readValue(value, QuestionConfig.class);

            if (qc != null) {
                QuestionUtils utils = new QuestionUtils(manager, count, qc);

                choiceQuestions = utils.getChoiceQuestionOfChapter(1);

                choiceQuestions = utils.getChoiceQuestionList();
                blankQuestions = utils.getBlankQuestionList();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "读取题目分配配置失败", Toast.LENGTH_SHORT).show();
        }
    }

    private List<BlankQuestion> getBlankQuestionsByChapter(int chapter) {
        return BlankQuestionUtils.getBlankQuestionList(
                blankQuestionPDao.
                        queryBuilder().
                        where(BlankQuestionPDao.Properties.Bchapter.eq(chapter)).
                        list());
    }

    /**
     * 获取各章节的题目
     *
     * @return
     */
    private List<ChoiceQuestion> getChoiceQuestionsByChapter(int chapter) {
        return choiceQuestionDao.
                queryBuilder().
                where(ChoiceQuestionDao.Properties.Chchapter.eq(chapter)).
                list();
    }

    private void initFab() {
        //setup fab
        floatingActionMenu.setClosedOnTouchOutside(true);

        //交卷
        floatingActionApply.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_check).color(Color.WHITE).sizeDp(16));
        floatingActionApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }

                int total = C_rightCount + C_wrongcount + B_rightcount + B_wrongcount;
                if (total == 0) {
                    Toast.makeText(QuestionActivity.this, "请先答题再交卷", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(QuestionActivity.this, ShowGradeActivity.class);

                intent.putExtra("uno", useracount);
                intent.putExtra("C_rightcount", C_rightCount);
                intent.putExtra("C_wrongcount", C_wrongcount);
                intent.putExtra("B_rightcount", B_rightcount);
                intent.putExtra("B_wrongcount", B_wrongcount);
                intent.putExtra("chaptercount", chaptercount);
                intent.putExtra("choice_count", choiceQuestions.size());
                intent.putExtra("blank_count", blankQuestions.size());
                //QUESTION_MODE=11为模拟考试
                intent.putExtra("QUESTION_MODE", QUESTION_MODE);

                startActivity(intent);
                finish();
            }
        });

        //结束考试
        floatingActionDownload.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_exit_to_app).color(Color.WHITE).sizeDp(16));
        floatingActionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }

                exit();
            }
        });
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("答题界面");
        }
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }

        return false;
    }

    private void exit() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("确认结束考试")
                .setMessage("结束考试后 此次答题将不会被记录")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();
    }

    public class QuestionViewPagerAdapter extends PagerAdapter {
        private List<ChoiceQuestion> choiceQuestions;
        private List<BlankQuestion> blankQuestions;

        public QuestionViewPagerAdapter(List<ChoiceQuestion> choiceQuestions, List<BlankQuestion> blankQuestions) {
            this.choiceQuestions = choiceQuestions;
            this.blankQuestions = blankQuestions;
            initQuestionViews();
        }

        private void initQuestionViews() {
            views = new ArrayList<View>();
            initChoice();
            initBlank();
        }

        private void initChoice() {
            LayoutInflater inflater = LayoutInflater.from(QuestionActivity.this);
            for (final ChoiceQuestion choiceQuestion : choiceQuestions) {


                View view = inflater.inflate(R.layout.choice_question_view, null);

                TextView tvStem;
                final TextView tvAnalysis;

                tvStem = (TextView) view.findViewById(R.id.tv_questionstem);
                tvAnalysis = (TextView) view.findViewById(R.id.tv_questionanalysis);

                tvStem.setText(choiceQuestion.getChstem());
                tvAnalysis.setText(choiceQuestion.getChanalysis());
                //解析先不显示
                tvAnalysis.setVisibility(View.GONE);
                final CircularProgressButton sureButton = (CircularProgressButton) view.findViewById(R.id.sureBtn);
                final RadioGroup ansGroup = (RadioGroup) view.findViewById(R.id.rg_ans);
                final RadioButton rbA = (RadioButton) view.findViewById(R.id.rb_A);
                final RadioButton rbB = (RadioButton) view.findViewById(R.id.rb_B);
                final RadioButton rbC = (RadioButton) view.findViewById(R.id.rb_C);
                final RadioButton rbD = (RadioButton) view.findViewById(R.id.rb_D);

                if (!choiceQuestion.getChA().equals("")) {
                    rbA.setText("A:" + choiceQuestion.getChA());
                } else {
                    rbA.setVisibility(View.INVISIBLE);
                }

                if (!choiceQuestion.getChB().equals("")) {
                    rbB.setText("B:" + choiceQuestion.getChB());
                } else {
                    rbB.setVisibility(View.INVISIBLE);
                }

                if (!choiceQuestion.getChC().equals("")) {
                    rbC.setText("C:" + choiceQuestion.getChC());
                } else {
                    rbC.setVisibility(View.INVISIBLE);
                }

                if (!choiceQuestion.getChD().equals("")) {
                    rbD.setText("D:" + choiceQuestion.getChD());
                } else {
                    rbD.setVisibility(View.INVISIBLE);
                }

                ansGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        selected = checkedId;
                    }
                });

                sureButton.setIndeterminateProgressMode(true);
                sureButton.setProgress(0);
                sureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ansGroup.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(QuestionActivity.this, "请先选择选项", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sureButton.setProgress(50);
                        // 处理选择题
                        int checkId = selected;
                        if (checkId == R.id.rb_A) {
                            if (choiceQuestion.getChanswer() == 1) {
                                sureButton.setProgress(100);
                                C_rightCount++;
                                chaptercount[choiceQuestion.getChchapter() - 1]++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                        sureButton.setEnabled(false);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
                                C_wrongcount++;
                                if (user != null) {
                                    submitWrongQuestion(useracount, choiceQuestion.getChno(), 1);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvAnalysis.setVisibility(View.VISIBLE);
                                        sureButton.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        } else if (checkId == R.id.rb_B) {
                            if (choiceQuestion.getChanswer() == 2) {
                                sureButton.setProgress(100);
                                C_rightCount++;
                                chaptercount[choiceQuestion.getChchapter() - 1]++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                        sureButton.setEnabled(false);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
                                C_wrongcount++;
                                if (user != null) {
                                    submitWrongQuestion(useracount, choiceQuestion.getChno(), 1);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvAnalysis.setVisibility(View.VISIBLE);
                                        sureButton.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        } else if (checkId == R.id.rb_C) {
                            if (choiceQuestion.getChanswer() == 3) {
                                sureButton.setProgress(100);
                                C_rightCount++;
                                chaptercount[choiceQuestion.getChchapter() - 1]++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                        sureButton.setEnabled(false);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
                                C_wrongcount++;
                                if (user != null) {
                                    submitWrongQuestion(useracount, choiceQuestion.getChno(), 1);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvAnalysis.setVisibility(View.VISIBLE);
                                        sureButton.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        } else if (checkId == R.id.rb_D) {
                            if (choiceQuestion.getChanswer() == 4) {
                                sureButton.setProgress(100);
                                C_rightCount++;
                                chaptercount[choiceQuestion.getChchapter() - 1]++;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                        sureButton.setEnabled(false);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
                                C_wrongcount++;
                                if (user != null) {
                                    submitWrongQuestion(useracount, choiceQuestion.getChno(), 1);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvAnalysis.setVisibility(View.VISIBLE);
                                        sureButton.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        }
                    }

                });

                views.add(view);
            }
        }

        private void initBlank() {
            LayoutInflater inflater = LayoutInflater.from(QuestionActivity.this);

            for (final BlankQuestion blankQuestion : blankQuestions) {

                View view = inflater.inflate(R.layout.blank_question_view, null);

                TextView tvStem = (TextView) view.findViewById(R.id.tv_question_stem);
                final TextView tvAnalysis = (TextView) view.findViewById(R.id.tv_question_analysis);
                final CircularProgressButton sureButton = (CircularProgressButton) view.findViewById(R.id.sureBtn);

                tvStem.setText(blankQuestion.getBstem());
                tvAnalysis.setText(blankQuestion.getBsanalysis());

                // 解析先不显示
                tvAnalysis.setVisibility(View.GONE);

                final List<EditText> etAns = new ArrayList<EditText>();
                etAns.clear();
                for (int i = 1; i <= blankQuestion.getAnswerList().size(); i++) {
                    etAns.add(generateEt(view, i));
                }

                // 处理填空题
                isRight = true;
                sureButton.setIndeterminateProgressMode(true);
                sureButton.setProgress(0);
                sureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String ansStr : blankQuestion.getAnswerList()) {
                            for (EditText et : etAns) {
                                if (!checkAns(et.getText().toString().trim(), ansStr)) {
                                    isRight = false;
                                    break;
                                }
                            }
                        }

                        if (isRight) {
                            B_rightcount++;
                            chaptercount[blankQuestion.getBchapter() - 1]++;
                            sureButton.setProgress(100);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                }
                            }, 1500);
                        } else {
                            B_wrongcount++;
                            if (user != null) {
                                submitWrongQuestion(useracount, blankQuestion.getBno(), 2);
                            }
                            sureButton.setProgress(-1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvAnalysis.setVisibility(View.VISIBLE);
                                    sureButton.setVisibility(View.GONE);
                                }
                            }, 1500);
                        }
                    }
                });

                views.add(view);
            }
        }

        private boolean checkAns(String etString, String ans) {
            if (etString.toUpperCase().equals(ans.toUpperCase())) {
                return true;
            }

            return false;
        }

        private EditText generateEt(View view, int pos) {
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.ans_question_blank);
            EditText ans = new EditText(QuestionActivity.this);
            ans.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ans.setHint("选项" + pos);
            ll.addView(ans);

            return ans;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //销毁容器中的view对象
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //加载view的方法
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
