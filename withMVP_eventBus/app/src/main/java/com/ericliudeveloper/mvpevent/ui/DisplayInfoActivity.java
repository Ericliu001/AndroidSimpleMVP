package com.ericliudeveloper.mvpevent.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.ericliudeveloper.mvpevent.R;
import com.ericliudeveloper.mvpevent.presenter.DisplayInfoPresenter;

public class DisplayInfoActivity extends Activity {

    private final String tag_display_fragment = this.getClass().getName() + "display";
    private DisplayInfoPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_container);


        FragmentManager fm = getFragmentManager();



        DisplayInfoFragment displayInfoFragment = (DisplayInfoFragment) fm.findFragmentByTag(tag_display_fragment);
        if (displayInfoFragment == null) {
            displayInfoFragment = new DisplayInfoFragment();
            fm.beginTransaction().add(R.id.container, displayInfoFragment, tag_display_fragment).commit();
        }

    }


}
