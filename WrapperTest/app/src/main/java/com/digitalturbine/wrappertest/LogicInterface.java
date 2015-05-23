package com.digitalturbine.wrappertest;

import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by eric.liu on 1/05/15.
 */
public interface LogicInterface
{
    //	public static final int ACTION_ID_AUTHENTICATE = 1;
    //	public static final int ACTION_ID_SETUP = 2;
    //	public static final int ACTION_ID_PURCHASE = 3;
    //	public static final int NUM_OF_ACTIONS = 3;
    //
    //	public static final int RETURN_NOT_STARTED = -1;
    //	public static final int RETURN_IN_PROGRESS = 0;
    //	public static final int RETURN_CANCELLED = 1;
    //	public static final int RETURN_ERROR = 2;
    //	public static final int RETURN_LOGIN_SUCCESS = 3;
    //	public static final int RETURN_SUBSCRIPTION_PURCHASED = 4;
    //	public static final int RETURN_SUBSCRIPTION_VALID = 5;
    //	public static final int RETURN_SUBSCRIPTION_NOT_VALID = 6;

    public void resume();

    public void pause();

    //public void init(boolean showProgress);

    //public int logic();

    public void end();

    public void free();

    public Resource getResource();

    // check action can modify bundle parameter so it should be passed to handle action
    public Bundle checkAction(Bundle bundle);

    public void handleAction(Bundle bundle);

    //public void handleNetworkAction();

    public void triggerExitEvent();

    //public void triggerError(String error);

    //public String getLastError();

    public void setViewAndHandler(FrameLayout layout, LogicHandler handler);

    public interface LogicHandler
    {
        public void logicEnded(Bundle bundle);
    }

}

