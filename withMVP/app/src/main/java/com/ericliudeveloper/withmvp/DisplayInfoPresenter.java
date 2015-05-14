package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by liu on 14/05/15.
 */
public class DisplayInfoPresenter {

    private DisplayInfoActFace activity;


    /**
     * Data passed to TextView should only be in String format
     */
    interface DisplayInfoActFace{
        void showDirection(String direction);
        void showProgress(String progress);
        void showName(String name);
    }

    public DisplayInfoPresenter(DisplayInfoActFace activity, Bundle data){
        this.activity = activity;

        processInfoData(data);
    }

    private void processInfoData(Bundle data) {
        FirstModel firstModel = data.getParcelable(MainActPresenter.MAIN_PRESENTER_DATA);

        String direction = "";
        if (firstModel.getDirection() != null){
             direction = firstModel.getDirection().name();
        }


        String progress = String.valueOf(firstModel.getProgress());

        activity.showDirection(direction);
        activity.showProgress(progress);
        activity.showName(firstModel.getName());
    }

}
