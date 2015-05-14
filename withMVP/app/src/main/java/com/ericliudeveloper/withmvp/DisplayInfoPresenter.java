package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by liu on 14/05/15.
 */
public class DisplayInfoPresenter {

    private DisplayInfoActFace activity;




    /**
     * Data passed to TextView should only be Strings.
     *
     * There SHOULD NOT be anymore operations on the data in Activity, it should only do the job of displaying data.
     * so the data type that passed to Activity should be directly displayed on the widget.
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
        String name = firstModel.getName();

        refreshDisplay(direction, progress, name);
    }

    private void refreshDisplay(String direction, String progress, String name) {
        activity.showDirection(direction);
        activity.showProgress(progress);
        activity.showName(name);
    }


    public void buttonSetDefaultClicked() {
        refreshDisplay("No Direction", "100", "Eric Liu");
    }

    public void buttonResetDisplayClicked() {
        refreshDisplay("", "", "");
    }
}
