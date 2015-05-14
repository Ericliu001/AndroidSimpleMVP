package com.ericliudeveloper.withmvp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class DisplayInfoActivity extends ActionBarActivity implements DisplayInfoPresenter.DisplayInfoActFace{

    TextView tvDirecton, tvProgress, tvName;
    private DisplayInfoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_nothing);

        initViews();

        Intent startedIntent = getIntent();
        Bundle data = startedIntent.getExtras();

        mPresenter = new DisplayInfoPresenter(DisplayInfoActivity.this, data);
    }

    private void initViews() {
        tvDirecton = (TextView) findViewById(R.id.tvDirection);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        tvName = (TextView) findViewById(R.id.tvName);
    }

    @Override
    public void showDirection(String direction) {
        tvDirecton.setText(direction);
    }

    @Override
    public void showProgress(String progress) {
        tvProgress.setText(progress);
    }

    @Override
    public void showName(String name) {
        tvName.setText(name);
    }
}
