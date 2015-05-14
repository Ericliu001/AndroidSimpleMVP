package com.ericliudeveloper.withmvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by liu on 12/05/15.
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
