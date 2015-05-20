package com.ericliudeveloper.mvpevent.presenter;

import android.os.Bundle;

import com.ericliudeveloper.mvpevent.MyEvents;
import com.ericliudeveloper.mvpevent.android_object_wrapper.ContextFace;
import com.ericliudeveloper.mvpevent.ui.DisplayInfoActivity;
import com.ericliudeveloper.mvpevent.mode.FirstModel;
import com.ericliudeveloper.mvpevent.ui.SetNameActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by liu on 12/05/15.
 * <p/>
 * Presenter in MVP
 */
public class MainActPresenter implements PresenterFace {

    MainFace fragment;
    ContextFace mContext;

    public static final String MAIN_PRESENTER_DATA = "main presenter data";
    public static final String GOING_LEFT = "Going Left .....";
    public static final String GOING_RIGHT = "Going Right.....";
    private FirstModel firstModel;


    /**
     * return the model data as a Bundle, which hides the actual data type from Activity or Fragment
     *
     * @return - Model Data Object wrapped in a Bundle
     */
    @Override
    public Bundle getModelData() {
        Bundle data = new Bundle();
        data.putParcelable(MainActPresenter.MAIN_PRESENTER_DATA, firstModel);
        return data;
    }

    public void register() {
        EventBus.getDefault().registerSticky(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(MyEvents.NameSetEvent event){
        setNameAndShow(event.name);
    }

    private void setNameAndShow(String name) {
        firstModel.setName(name);
        fragment.showName(name);
    }

    /**
     * The interface for the corresponding Activity to implement;
     * All methods declared here should be responsible for changing the displaying contents and be responsible for only that;
     * the startActivityForResult(...) method is an exception here because it is tightly coupled with an Activity so we just forward to call to the Activity.
     */
    public interface MainFace {
        void showDirection(String directionMessage);

        void showProgress(int progress);

        void showName(String name);

    }



    /**
     * The parameter passed in here should either be having no dependency on Android SDK
     * or could be Mocked during test, such as: MockCursor, MockContext, MockApplication.
     * We use an interface type ContextFace here to hide the real Context Object to de-couple the Presenter to the SDK
     *
     * @param face
     * @param cachedData
     * @param context
     */
    public MainActPresenter(MainFace face, Bundle cachedData, ContextFace context) {
        fragment = face;
        mContext = context;
        if (cachedData != null) {
            firstModel = cachedData.getParcelable(MAIN_PRESENTER_DATA);
        } else {
            firstModel = new FirstModel();
        }

//        refreshDisplay(firstModel);
    }

    @Override
    public void onPostViewCreated(){
        refreshDisplay(firstModel);
    }


    private void refreshDisplay(FirstModel firstModel) {
        if (firstModel.getDirection() == FirstModel.Direction.LEFT) {
            fragment.showDirection(GOING_LEFT);
        } else if (firstModel.getDirection() == FirstModel.Direction.RIGHT) {
            fragment.showDirection(GOING_RIGHT);
        }

        fragment.showProgress(firstModel.getProgress());
        fragment.showName(firstModel.getName());
    }

    /**
     * This method is the perfect example of the use MVP pattern.
     * The Presenter is responsible for getting & saving data,
     * dispatching commands to View to change the displaying contents,
     * and executing business logic and make changes to data.
     */
    public void buttonIncreaseClicked() {
        int progess = firstModel.getProgress(); // get data from Model
        fragment.showProgress(progess += 5);    // dispatch command to View
        firstModel.setProgress(progess);        // execute business logic
    }


    /**
     * handle user button clicks to dispatch the changing display command to the View in MVP
     */
    public void buttonLeftClicked() {
        fragment.showDirection(GOING_LEFT);         //  dispatch command to change display
        firstModel.setDirection(FirstModel.Direction.LEFT);  // execute business logic
    }

    public void buttonRightClicked() {
        fragment.showDirection(GOING_RIGHT);      //  dispatch command to change display
        firstModel.setDirection(FirstModel.Direction.RIGHT);  // execute business logic
    }


    public void buttonGoToSecondClicked() {
        mContext.startActivity(SetNameActivity.class, null);
    }


    public void buttonGoToDoNothingClicked() {
        Bundle data = new Bundle();
        data.putParcelable(MAIN_PRESENTER_DATA, firstModel);
        mContext.startActivity(DisplayInfoActivity.class, data);
    }

}
