package com.ericliudeveloper.withmvp;

import android.content.Context;
import android.content.Intent;

/**
 * Created by liu on 12/05/15.
 */
public class ContextWrapper implements ContextFace {
    Context mContext;

    public ContextWrapper(Context context){
        mContext = context;
    }


    @Override
    public void startActivity(Class<?> dest) {
        Intent intent = new Intent(mContext, dest);
        mContext.startActivity(intent);
    }


}
