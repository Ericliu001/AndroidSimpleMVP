package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by liu on 14/05/15.
 */
public class DisplayInfoPresenter implements PresenterFace{

    private DisplayInfoActFace activity;
    private FirstModel firstModel;

    @Override
    public Bundle getModelData() {
        Bundle data = new Bundle();
        data.putParcelable(MainActPresenter.MAIN_PRESENTER_DATA, firstModel);
        return data;
    }


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

        firstModel = data.getParcelable(MainActPresenter.MAIN_PRESENTER_DATA);
        processInfoData(firstModel);
    }

    private void processInfoData(FirstModel model) {

        String direction = "";
        if (model.getDirection() != null){
             direction = model.getDirection().name();
        }


        String progress = String.valueOf(model.getProgress());
        String name = model.getName();

        refreshDisplay(direction, progress, name);
    }

    private void refreshDisplay(String direction, String progress, String name) {
        activity.showDirection(direction);
        activity.showProgress(progress);
        activity.showName(name);
    }


    public void buttonSetDefaultClicked() {

        firstModel = new FirstModel();
        firstModel.setDirection(FirstModel.Direction.LEFT);
        firstModel.setProgress(50);
        firstModel.setName("Eric Liu");

        processInfoData(firstModel);
    }

    public void buttonResetDisplayClicked() {
        firstModel = new FirstModel();
        processInfoData(firstModel);
    }
}
