package com.ericliudeveloper.withmvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liu on 12/05/15.
 */

/**
 *  A Wrapper class to hide the real Context Object from Presenters.
 *  The main reason of doing this is to reduce the Presenters' dependency on Android SDK
 *  so that we can mock the Context during Unit Test
 */
public class ContextWrapper implements ContextFace {
    Context mContext;

    public ContextWrapper(Context context){
        mContext = context;
    }


    @Override
    public void startActivity(Class<?> dest, Bundle data) {
        Intent intent = new Intent(mContext, dest);
        intent.putExtras(data);
        mContext.startActivity(intent);
    }


}
