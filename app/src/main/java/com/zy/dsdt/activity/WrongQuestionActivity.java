package com.zy.dsdt.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.zy.dsdt.bean.BlankQuestionP;
import com.zy.dsdt.bean.BlankQuestionPDao;
import com.zy.dsdt.bean.ChoiceQuestion;
import com.zy.dsdt.bean.ChoiceQuestionDao;
import com.zy.dsdt.bean.User;
import com.zy.dsdt.bean.WrongQuestion;
import com.zy.dsdt.bean.WrongQuestionDao;
import com.zy.dsdt.net.RequestManager;
import com.zy.dsdt.utils.BlankQuestionUtils;
import com.zy.dsdt.utils.DBManager;
import com.zy.dsdt.utils.NetURLUtils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WrongQuestionActivity extends AppCompatActivity {

    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.question_fab_menu)
    FloatingActionMenu floatingActionMenu;
    @InjectView(R.id.fab_item_delete)
    FloatingActionButton floatingActionDelete;
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
    private String uno;
    private WrongQuestion wrongQuestion = new WrongQuestion();

    private DBManager manager;
    private ChoiceQuestionDao choiceQuestionDao;
    private BlankQuestionPDao blankQuestionPDao;
    private WrongQuestionDao wrongQuestionDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_question);
        ButterKnife.inject(this);

        manager = new DBManager(this);
        choiceQuestionDao = manager.getChoiceQuestionDao();
        blankQuestionPDao = manager.getBlankQuestionPDao();
        wrongQuestionDao = manager.getWrongQuestionDao();

        Intent intent = getIntent();

        uno = intent.getStringExtra("uno");

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

        initChoiceQuestions();
        initBlankQuestions();
    }

    private void initChoiceQuestions() {
        choiceQuestions = new ArrayList<>();

        for (WrongQuestion wrongQuestion : wrongQuestionDao.queryBuilder().where(WrongQuestionDao.Properties.Type.eq(1)).list()) {
            choiceQuestions.addAll(choiceQuestionDao.queryBuilder().where(ChoiceQuestionDao.Properties.Chno.eq(wrongQuestion.getQno())).list());
        }
    }

    private void initBlankQuestions() {
        List<BlankQuestionP> blankQuestionPs = new ArrayList<>();

        for (WrongQuestion wrongQuestion : wrongQuestionDao.queryBuilder().where(WrongQuestionDao.Properties.Type.eq(2)).list()) {
            blankQuestionPs.addAll(blankQuestionPDao.queryBuilder().where(BlankQuestionPDao.Properties.Bno.eq(wrongQuestion.getQno())).list());
        }

        blankQuestions = BlankQuestionUtils.getBlankQuestionList(blankQuestionPs);
    }

    private void initFab() {
        //setup fab
        floatingActionMenu.setClosedOnTouchOutside(true);

        //删除错题
        floatingActionDelete.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_delete).color(Color.WHITE).sizeDp(16));
        floatingActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }

                int position = vpShowQuestion.getCurrentItem();

                if (position < choiceQuestions.size()) {
                    wrongQuestion.setUno(uno);
                    wrongQuestion.setQno(choiceQuestions.get(position).getChno());
                    wrongQuestion.setType(1);
                } else {
                    position = position - choiceQuestions.size();
                    wrongQuestion.setUno(uno);
                    wrongQuestion.setQno(blankQuestions.get(position).getBno());
                    wrongQuestion.setType(2);
                }

                submitDelete();

            }
        });
    }

    private void submitDelete() {
        String url = NetURLUtils.getURLpath(this, "DeleteWrongQuestionServlet");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int result = Integer.parseInt(s);

                        if (result == 0) {
                            //删除成功
                            wrongQuestionDao.delete(wrongQuestion);
                            Toast.makeText(WrongQuestionActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //删除失败
                            Toast.makeText(WrongQuestionActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(WrongQuestionActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                ObjectMapper mapper = new ObjectMapper();

                try {
                    String array = mapper.writeValueAsString(wrongQuestion);
                    map.put("data", array);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return map;
            }
        };

        RequestManager.addRequest(stringRequest, this);
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("我的错题");
        }
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
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
            LayoutInflater inflater = LayoutInflater.from(WrongQuestionActivity.this);
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
                            Toast.makeText(WrongQuestionActivity.this, "请先选择选项", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sureButton.setProgress(50);
                        // 处理选择题
                        int checkId = selected;
                        if (checkId == R.id.rb_A) {
                            if (choiceQuestion.getChanswer() == 1) {
                                sureButton.setProgress(100);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                        ansGroup.setEnabled(false);
                                        ansGroup.setClickable(false);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
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
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
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
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                    }
                                }, 1500);
                            } else {
                                sureButton.setProgress(-1);
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
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                    }
                                }, 1500);
                            } else {
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
                    }

                });

                views.add(view);
            }
        }

        private void initBlank() {
            LayoutInflater inflater = LayoutInflater.from(WrongQuestionActivity.this);

            for (final BlankQuestion blankQuestion : blankQuestions) {

                View view = inflater.inflate(R.layout.blank_question_view, null);

                TextView tvStem = (TextView) view.findViewById(R.id.tv_question_stem);
                final TextView tvAnalysis = (TextView) view.findViewById(R.id.tv_question_analysis);
                final CircularProgressButton sureButton = (CircularProgressButton) view.findViewById(R.id.sureBtn);

                tvStem.setText(blankQuestion.getBstem());
                tvAnalysis.setText(blankQuestion.getBsanalysis());

                // 解析先不显示
                tvAnalysis.setVisibility(View.GONE);

                final List<EditText> ans = new ArrayList<EditText>();
                ans.clear();
                for (int i = 1; i <= blankQuestion.getAnswerList().size(); i++) {
                    ans.add(generateEt(view, i));
                }

                // 处理填空题
                isRight = true;
                sureButton.setIndeterminateProgressMode(true);
                sureButton.setProgress(0);
                sureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String ansStr : blankQuestion.getAnswerList()) {
                            for (EditText et : ans) {
                                if (!et.getText().toString().trim().equals(ansStr)) {
                                    isRight = false;
                                    break;
                                }
                            }
                        }

                        if (isRight) {
                            sureButton.setProgress(100);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    vpShowQuestion.setCurrentItem(vpShowQuestion.getCurrentItem() + 1);
                                }
                            }, 1500);
                        } else {
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

        private EditText generateEt(View view, int pos) {
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.ans_question_blank);
            EditText ans = new EditText(WrongQuestionActivity.this);
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
