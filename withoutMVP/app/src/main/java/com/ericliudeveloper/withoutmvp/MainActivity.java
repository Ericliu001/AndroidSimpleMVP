package com.ericliudeveloper.withoutmvp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    TextView tvTop, tvDisplayName;
    Button btLeft, btRight, btGotoSecond, btIncrease;
    ProgressBar pbMain;
    private int progess;
    private final static int REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


    }

    private void initViews() {
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvDisplayName = (TextView) findViewById(R.id.tvDisplayName);

        btLeft = (Button) findViewById(R.id.btLeft);
        btRight = (Button) findViewById(R.id.btRight);
        btGotoSecond = (Button) findViewById(R.id.btGoToSecond);
        btIncrease = (Button) findViewById(R.id.btIncrease);

        pbMain = (ProgressBar) findViewById(R.id.progressBar);

        btLeft.setOnClickListener(this);
        btRight.setOnClickListener(this);
        btGotoSecond.setOnClickListener(this);
        btIncrease.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btLeft:
                tvTop.setText(getString(R.string.going_left));
                break;
            case R.id.btRight:
                tvTop.setText(getString(R.string.going_right));
                break;
            case R.id.btIncrease:
                pbMain.setProgress(progess+=5);
                break;
            case R.id.btGoToSecond:
                startSecondActivity();
                break;
            default:
                return;
        }
    }

    private void startSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            tvDisplayName.setText(data.getStringExtra(SecondActivity.NAME_FIELD));
        }
    }
}
