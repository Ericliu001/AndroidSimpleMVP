package com.ericliudeveloper.mvpevent;

import android.app.Application;

/**
 * Created by eric.liu on 21/05/15.
 */
public class MyApplication extends Application {
    private static Application mApplication;

    public static Application getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mApplication == null){
            mApplication = this;
        }
    }
}
