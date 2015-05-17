package com.ericliudeveloper.mvpevent;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * The Activiy serves as the View in MVP;
 * It is only responsible for handling content display and user input.
 * The Activity shall have no knowledge of data and business logic.
 */
public class MainActivity extends Activity {


    private final String tag_caching_fragment = this.getClass().getName() + "cache"; // the tag is used to retrieve Fragment instance
    private final String tag_main_fragment = this.getClass().getName() + "main"; // the tag is used to retrieve Fragment instance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_container);


        FragmentManager fm = getFragmentManager();


        MainFragment mainFragment = (MainFragment) fm.findFragmentByTag(tag_main_fragment);
        if (mainFragment == null){
            mainFragment = new MainFragment();
            fm.beginTransaction().add(R.id.container, mainFragment, tag_main_fragment).commit();
        }


    }

}
