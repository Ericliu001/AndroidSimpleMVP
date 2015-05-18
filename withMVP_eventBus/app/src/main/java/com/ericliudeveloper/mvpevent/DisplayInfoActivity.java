package com.ericliudeveloper.mvpevent;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ericliudeveloper.mvpevent.presenter.DisplayInfoPresenter;

public class DisplayInfoActivity extends Activity {

    private final String tag_display_fragment = this.getClass().getName() + "display";
    TextView tvDirecton, tvProgress, tvName;
    Button btSetDefault, btResetDisplay;
    private DisplayInfoPresenter mPresenter;
    private CacheModelFragment cacheFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_container);


        FragmentManager fm = getFragmentManager();

        Intent startedIntent = getIntent();
        Bundle data = startedIntent.getExtras();

        DisplayInfoFragment displayInfoFragment = (DisplayInfoFragment) fm.findFragmentByTag(tag_display_fragment);
        if (displayInfoFragment == null) {
            displayInfoFragment = DisplayInfoFragment.newInstance(data);
            fm.beginTransaction().add(R.id.container, displayInfoFragment, tag_display_fragment).commit();
        }

    }


}
