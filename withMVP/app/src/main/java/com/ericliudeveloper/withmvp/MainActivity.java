package com.ericliudeveloper.withmvp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The Activiy serves as the View in MVP;
 * It is only responsible for handling content display and user input.
 * The Activity shall have no knowledge of data and business logic.
 */
public class MainActivity extends ActionBarActivity implements MainActPresenter.MainActFace, View.OnClickListener {


    private final String tag_caching_fragment = this.getClass().getName();
    CacheModelFragment cacheFragment;
    TextView tvTop, tvDisplayName;
    Button btLeft, btRight, btGotoSecond, btGotoDoNothing, btIncrease;
    ProgressBar pbMain;

    private MainActPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();

        FragmentManager fm = getFragmentManager();
        cacheFragment = (CacheModelFragment) fm.findFragmentByTag(tag_caching_fragment);
        if (cacheFragment == null) {
            cacheFragment = new CacheModelFragment();
            fm.beginTransaction().add(cacheFragment, tag_caching_fragment).commit();
        }


        // Initialise the Presenter and pass in data
        mPresenter = new MainActPresenter(MainActivity.this, null, new ContextWrapper(MainActivity.this));
    }

    private void initViews() {
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvDisplayName = (TextView) findViewById(R.id.tvDisplayName);

        btLeft = (Button) findViewById(R.id.btLeft);
        btRight = (Button) findViewById(R.id.btRight);
        btGotoSecond = (Button) findViewById(R.id.btGoToSecond);
        btGotoDoNothing = (Button) findViewById(R.id.btGotoDoNothing);
        btIncrease = (Button) findViewById(R.id.btIncrease);

        pbMain = (ProgressBar) findViewById(R.id.progressBar);

        btLeft.setOnClickListener(this);
        btRight.setOnClickListener(this);
        btGotoSecond.setOnClickListener(this);
        btGotoDoNothing.setOnClickListener(this);
        btIncrease.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // Button click events, forwarding all actions to Presenter
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btLeft:
                mPresenter.buttonLeftClicked();
                break;
            case R.id.btRight:
                mPresenter.buttonRightClicked();
                break;
            case R.id.btIncrease:
                mPresenter.buttonIncreaseClicked();
                break;
            case R.id.btGoToSecond:
                mPresenter.buttonGoToSecondClicked();
                break;
            case R.id.btGotoDoNothing:
                mPresenter.buttonGoToDoNothingClicked();
                break;
            default:
                return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // result ok, action needs to be handled, forward it to Presenter
            mPresenter.onActivityResult(requestCode, data.getExtras());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle savedData = mPresenter.getModelData();
        cacheFragment.setDataToBeCached(savedData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle cachedData = cacheFragment.getCachedData();
        if(cachedData != null){
            mPresenter = new MainActPresenter(MainActivity.this, cachedData, new ContextWrapper(MainActivity.this));
        }
    }

    @Override
    public void showDirection(String directionMessage) {
        tvTop.setText(directionMessage);
    }


    @Override
    public void showProgress(int progress) {
        pbMain.setProgress(progress);
    }

    @Override
    public void showName(String name) {
        tvDisplayName.setText(name);
    }

    @Override
    public void startActivityForResult(Class<?> dest, int requestCode) {
        Intent intent = new Intent(MainActivity.this, dest);
        startActivityForResult(intent, requestCode);
    }
}
