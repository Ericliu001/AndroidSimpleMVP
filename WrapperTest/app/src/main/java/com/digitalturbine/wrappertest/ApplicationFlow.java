package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eric.liu on 1/05/15.
 */
public class ApplicationFlow extends Application {
    private InAppBillingController inAppBillingController = null;

    private boolean isInApp = false;
    private boolean wasInApp = false;
    private boolean shouldSkipOnStopAction = false;

    // private boolean isActivityCreated = true;
    // private boolean isActivityStarted = true;

    // private boolean isFullPurchase = false;
    private Logic logic;

    private boolean mShouldCreate(Activity activity)
    {
        // if (isFullPurchase)
        // return true;

        try
        {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);

            Bundle bundle = info.metaData;
            String myApiKey = bundle.getString("vodafone_inappbilling_enabled");
            String myApiKey2 = bundle.getString("vodafone_inappbilling");
            boolean b = bundle.getBoolean("vodafone-billing");
            int i = 0;
        }
        catch (NameNotFoundException e)
        {
            int i = 0;
        }
        catch (NullPointerException e)
        {
            int i = 0;
        }

        if (inAppBillingController == null)
        {
            inAppBillingController = new InAppBillingController(activity, new InAppBillingAsynchronousHandler() {

                @Override
                public void handleResponse(Bundle bundle)
                {
                    int responseCode = bundle.getInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE);
                    switch (responseCode)
                    {
                        case InAppBillingController.RESULT_OK:
                            startApp(inAppBillingController.getActiviy());
                            break;
                        default: // error or cancelled
                            quitApp(inAppBillingController.getActiviy());
                    }

                }
            });

            this.logic = new Logic(activity, inAppBillingController, false);

            inAppBillingController.setLogic(this.logic);
        }

        // boolean retVal = shouldCSR();
        // if (retVal)
        // isActivityCreated = true;
        // else
        // isActivityCreated = false;
        //
        // return retVal;
        return true;
    }

    private Dialog msgDialog;
    private Timer timer;

    private boolean businessRulesNotifyStart()
    {
        // if (isFullPurchase)
        // return;

        boolean ret = logic.businessRulesNotifyStart();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                // timer is working only when inApp == true
                logic.deltaUseTime();

                if (logic.checkBusinessRules())
                {
                    if (logic.businessRulesHasMessage())
                    {
                        isInApp = false;
                        businessRulesNotifyStop();

                        lastActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                ((ActivityInterface) lastActivity).onPause2();

                                msgDialog = logic.businessRulesShowMessage(lastActivity, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // start app
                                        startApp(lastActivity);
                                    }
                                });
                            }
                        });
                    }
                }
                else
                {
                    isInApp = false;
                    businessRulesNotifyStop();

                    lastActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {

                            ((ActivityInterface) lastActivity).onPause2();
                            showWrapper();

                        }
                    });

                }

                logic.saveBusinessRules(true);

            }
        }, 0, Logic.BUSINESS_RULES_SAVE_TIME);

        return ret;
    }

    private boolean businessRulesNotifyStop()
    {
        // if (isFullPurchase)
        // return;

        if (timer != null)
        {
            timer.cancel();
            timer = null;

            logic.deltaUseTime();
            logic.saveBusinessRules(true);
        }

        return logic.businessRulesNotifyStop();
    }

    private Activity lastActivity = null;

    // private void showMessage(Activity activity, String message) {
    // AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    // builder.setMessage(message).setCancelable(false)
    // .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // // start app
    // startApp(lastActivity);
    // }
    // });
    //
    // msgDialog = builder.create();
    // msgDialog.getWindow().setFlags(
    // WindowManager.LayoutParams.FLAG_FULLSCREEN,
    // WindowManager.LayoutParams.FLAG_FULLSCREEN);
    // msgDialog.show();
    // }

    private void showWrapper()
    {
        // start wrapper
        Handler h = new Handler();
        h.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run()
            {
                Bundle request = new Bundle();
                request.putString(InAppBillingController.BILLING_REQUEST_METHOD, Logic.BILLING_REQUEST_METHOD_PURCHASE);

                Bundle respose = inAppBillingController.requestAction(request);
                int responseCode = respose.getInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE);

                if (responseCode != InAppBillingController.RESULT_OK)
                {
                    //					Log.e("wrapper", "error starting the wrapper please debug what is happen");
                    quitApp(lastActivity);
                }
            }
        });
    }

    private boolean checkingBusinessRules()
    {
    	boolean result = logic.checkBusinessRules();
        if (result)
        {
            boolean message = logic.businessRulesHasMessage();
            if (message)
            {
                // show message
                isInApp = false;
                msgDialog = logic.businessRulesShowMessage(lastActivity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // start app
                        startApp(lastActivity);
                    }
                });
                // showMessage(activity, message);
            }
            else
            {
                isInApp = true;
                // notify start
                wasInApp = true;
                businessRulesNotifyStart();
            }
        }
        else
        {
            isInApp = false;
            showWrapper();
        }

        return isInApp;
    }

    private boolean mShouldResume(final Activity activity)
    {
        // if (isFullPurchase)
        // return true;
        this.lastActivity = activity;
        inAppBillingController.setActivity(activity);

        if (isInApp)
        {
            return checkingBusinessRules();
        }
        else
        {
            if (inAppBillingController.resume(activity))
            {
                return false;
            }
            else
            {
                return checkingBusinessRules();
            }
        }
    }

    private boolean mShouldPause(Activity activity)
    {
        // if (isFullPurchase)
        // return true;

        if (msgDialog != null)
        {
            msgDialog.dismiss();
            msgDialog = null;
        }

        inAppBillingController.pause();

        if (businessRulesNotifyStop())
            return true;

        if (isInApp)
        {
            return true;
        }
        else
            return false;

    }

    private boolean mShouldStop(Activity activity)
    {
        // if (isFullPurchase)
        // return true;

        if (!isInApp && !shouldSkipOnStopAction && !wasInApp)
        {
            ((ActivityInterface) activity).onResume2();
            ((ActivityInterface) activity).onPause2();
        }
        shouldSkipOnStopAction = false;
        return true;
    }

    // ***************************************************************************************
    private void startApp(Activity activity)
    {
        wasInApp = true;
        businessRulesNotifyStart();
        isInApp = true;
        ((ActivityInterface) activity).onResume2();
        inAppBillingController.pause();
    }

    private void quitApp(Activity activity)
    {
        if (!isInApp && !wasInApp)
        {
            ((ActivityInterface) activity).onResume2();
            ((ActivityInterface) activity).onPause2();
            shouldSkipOnStopAction = true;
        }

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(homeIntent);
    }

    // ***************************************************************************************
    public static void pause(Activity activity)
    {
        if (shouldPause(activity))
        {
            ((ActivityInterface) activity).onPause2();
        }
    }

    public static void resume(Activity activity)
    {
        if (shouldResume(activity))
        {
            ((ActivityInterface) activity).onResume2();
        }
    }

    public static void create(Activity activity, Bundle savedInstanceState)
    {
        if (shouldCreate(activity))
        {
            ((ActivityInterface) activity).onCreate2(savedInstanceState);
        }
    }

    public static void stop(Activity activity)
    {
        if (shouldStop(activity))
        {
            ((ActivityInterface) activity).onStop2();
        }
    }

    public static boolean shouldPause(Activity activity)
    {
        return ((ApplicationFlow) activity.getApplication()).mShouldPause(activity);
    }

    public static boolean shouldResume(Activity activity)
    {
        return ((ApplicationFlow) activity.getApplication()).mShouldResume(activity);
    }

    public static boolean shouldStop(Activity activity)
    {
        return ((ApplicationFlow) activity.getApplication()).mShouldStop(activity);
    }

    // public static boolean shouldStart(Activity activity)
    // {
    // return ((ApplicationFlow)
    // activity.getApplication()).mShouldStart(activity);
    // }

    public static boolean shouldCreate(Activity activity)
    {
        return ((ApplicationFlow) activity.getApplication()).mShouldCreate(activity);
    }
}
