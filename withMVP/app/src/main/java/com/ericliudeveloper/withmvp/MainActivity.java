package com.ericliudeveloper.withmvp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The Activiy serves as the View in MVP;
 * It is only responsible for handle display and user input.
 */
public class MainActivity extends ActionBarActivity implements MainActPresenter.MainActFace, View.OnClickListener {

    TextView tvTop, tvDisplayName;
    Button btLeft, btRight, btGotoSecond, btGotoDoNothing, btIncrease;
    ProgressBar pbMain;
//    private int progess;
//    private final static int REQUEST_CODE = 123;

    private MainActPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();


        mPresenter = new MainActPresenter(MainActivity.this, MainActivity.this);
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
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btLeft:
//                tvTop.setText(getString(R.string.going_left));

                mPresenter.buttonLeftClicked();
                break;
            case R.id.btRight:
//                tvTop.setText(getString(R.string.going_right));

                mPresenter.buttonRightClicked();
                break;
            case R.id.btIncrease:
//                pbMain.setProgress(progess+=5);

                mPresenter.buttonIncreaseClicked();
                break;
            case R.id.btGoToSecond:
//                startSecondActivity();

                mPresenter.buttonGoToSecondClicked();
                break;

            case R.id.btGotoDoNothing:
                mPresenter.buttonGoToDoNothingClicked();
                break;
            default:
                return;
        }
    }

//    private void startSecondActivity() {
//        Intent intent = new Intent(this, SetNameActivity.class);
//        startActivityForResult(intent, REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mPresenter.onActivityResult(requestCode, data.getExtras());
        }

//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
//            tvDisplayName.setText(data.getStringExtra(SetNameActivity.NAME_FIELD));
//        }
    }

    @Override
    public void displayLeft() {
        tvTop.setText(getString(R.string.going_left));
    }

    @Override
    public void displayRight() {
        tvTop.setText(getString(R.string.going_right));
    }

    @Override
    public void makeProgress(int progress) {
        pbMain.setProgress(progress);
    }

    @Override
    public void displayName(String name) {
        tvDisplayName.setText(name);
    }

    @Override
    public void startActivityForResult(Class<?> dest, int requestCode) {
        Intent intent = new Intent(MainActivity.this, dest);
        startActivityForResult(intent, requestCode);
    }
}
