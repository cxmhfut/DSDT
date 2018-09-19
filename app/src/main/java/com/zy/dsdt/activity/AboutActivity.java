package com.zy.dsdt.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zy.dsdt.BuildConfig;
import com.zy.dsdt.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Teresa on 2016/5/18.
 */
public class AboutActivity extends AppCompatActivity {

    @InjectView(R.id.common_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.tv_version)
    TextView mVersionTextView;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        initToolbar();
        initViews();
    }

    private void initViews() {
        mVersionTextView.setText("Version " + BuildConfig.VERSION_NAME);
        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
    }

    private void initToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("关于");
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }

}
