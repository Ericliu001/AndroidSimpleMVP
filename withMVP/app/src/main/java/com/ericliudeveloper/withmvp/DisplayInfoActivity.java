package com.ericliudeveloper.withmvp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayInfoActivity extends ActionBarActivity implements DisplayInfoPresenter.DisplayInfoActFace, View.OnClickListener {

    private final String tag_caching_fragment = this.getClass().getName();
    TextView tvDirecton, tvProgress, tvName;
    Button btSetDefault, btResetDisplay;
    private DisplayInfoPresenter mPresenter;
    private CacheModelFragment cacheFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_nothing);

        initViews();

        FragmentManager fm = getFragmentManager();
        cacheFragment = (CacheModelFragment) fm.findFragmentByTag(tag_caching_fragment);
        if (cacheFragment == null){
            cacheFragment = new CacheModelFragment();
            fm.beginTransaction().add(cacheFragment, tag_caching_fragment).commit();
        }

        Bundle data = cacheFragment.getCachedData();
        if (data == null) {
            Intent startedIntent = getIntent();
            data = startedIntent.getExtras();
        }
        mPresenter = new DisplayInfoPresenter(DisplayInfoActivity.this, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        // DO NOT save the data here, it's problematic
    }

    @Override
    protected void onDestroy() {
        Bundle savedData = mPresenter.getModelData();
        cacheFragment.setDataToBeCached(savedData);
        super.onDestroy();
    }

    private void initViews() {
        tvDirecton = (TextView) findViewById(R.id.tvDirection);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        tvName = (TextView) findViewById(R.id.tvName);

        btResetDisplay = (Button) findViewById(R.id.btResetDisplay);
        btSetDefault = (Button) findViewById(R.id.btSetDefault);

        btResetDisplay.setOnClickListener(this);
        btSetDefault.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            default:return;

            case R.id.btSetDefault:
                mPresenter.buttonSetDefaultClicked();
                break;

            case R.id.btResetDisplay:
                mPresenter.buttonResetDisplayClicked();
                break;
        }
    }


}
