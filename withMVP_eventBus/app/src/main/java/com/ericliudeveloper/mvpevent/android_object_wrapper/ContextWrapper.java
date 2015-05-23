package com.ericliudeveloper.mvpevent.android_object_wrapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liu on 12/05/15.
 */

/**
 * A Wrapper class to hide the real Context Object from Presenters.
 * The main reason of doing this is to reduce the Presenters' dependency on Android SDK
 * so that we can mock the Context during Unit Test
 */
public class ContextWrapper implements ContextFace {
    Context mContext;

    public ContextWrapper(Context context) {
        mContext = context;
    }


    @Override
    public void startActivity(Class<?> dest, Bundle data) {
        Intent intent = new Intent(mContext, dest);
        if (data != null) {
            intent.putExtras(data);
        }

        if (! (mContext instanceof Activity)){
            // System will throw an Exception if you try to call startActivity from outside an Activity without having the flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }


}
