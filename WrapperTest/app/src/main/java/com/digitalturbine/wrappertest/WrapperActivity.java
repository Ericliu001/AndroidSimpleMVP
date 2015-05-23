package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.os.Bundle;

public class WrapperActivity extends Activity implements ActivityInterface
{

    @Override
    protected void onPause()
    {
        super.onPause();
        ApplicationFlow.pause(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //		this.savedInstanceStateWrapper = savedInstanceState;
        ApplicationFlow.create(this, savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ApplicationFlow.resume(this);

    }

    @Override
    public void onCreate2(Bundle savedInstanceState)
    {
        
    }

    @Override
    public void onPause2()
    {

    }

    @Override
    public void onResume2()
    {

    }

    @Override
    public void onStop2()
    {

    }
}