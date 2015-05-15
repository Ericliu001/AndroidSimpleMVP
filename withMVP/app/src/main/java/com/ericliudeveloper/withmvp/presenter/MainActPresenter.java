package com.ericliudeveloper.withmvp.presenter;

import android.os.Bundle;

import com.ericliudeveloper.withmvp.android_object_wrapper.ContextFace;
import com.ericliudeveloper.withmvp.DisplayInfoActivity;
import com.ericliudeveloper.withmvp.mode.FirstModel;
import com.ericliudeveloper.withmvp.SetNameActivity;

/**
 * Created by liu on 12/05/15.
 * <p/>
 * Presenter in MVP
 */
public class MainActPresenter implements PresenterFace {

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

    /**
     * The interface for the corresponding Activity to implement;
     * All methods declared here should be responsible for changing the displaying contents and be responsible for only that;
     * the startActivityForResult(...) method is an exception here because it is tightly coupled with an Activity so we just forward to call to the Activity.
     */
    public interface MainActFace {
        void showDirection(String directionMessage);

        void showProgress(int progress);

        void showName(String name);

        void startActivityForResult(Class<?> dest, int requestCode);
    }

    MainActFace activity;
    ContextFace mContext;
    private final static int REQUEST_CODE = 123;

    /**
     * The parameter passed in here should either be having no dependency on Android SDK
     * or could be Mocked during test, such as: MockCursor, MockContext, MockApplication.
     * We use an interface type ContextFace here to hide the real Context Object to de-couple the Presenter to the SDK
     *
     * @param face
     * @param cachedData
     * @param context
     */
    public MainActPresenter(MainActFace face, Bundle cachedData, ContextFace context) {
        activity = face;
        mContext = context;
        if (cachedData != null) {
            firstModel = cachedData.getParcelable(MAIN_PRESENTER_DATA);
        } else {
            firstModel = new FirstModel();
        }

        refreshDisplay(firstModel);
    }


    private void refreshDisplay(FirstModel firstModel) {
        if (firstModel.getDirection() == FirstModel.Direction.LEFT) {
            activity.showDirection(GOING_LEFT);
        } else if (firstModel.getDirection() == FirstModel.Direction.RIGHT) {
            activity.showDirection(GOING_RIGHT);
        }

        activity.showProgress(firstModel.getProgress());
        activity.showName(firstModel.getName());
    }

    /**
     * This method is the perfect example of the use MVP pattern.
     * The Presenter is responsible for getting & saving data,
     * dispatching commands to View to change the displaying contents,
     * and executing business logic and make changes to data.
     */
    public void buttonIncreaseClicked() {
        int progess = firstModel.getProgress(); // get data from Model
        activity.showProgress(progess += 5);    // dispatch command to View
        firstModel.setProgress(progess);        // execute business logic
    }


    /**
     * handle user button clicks to dispatch the changing display command to the View in MVP
     */
    public void buttonLeftClicked() {
        activity.showDirection(GOING_LEFT);         //  dispatch command to change display
        firstModel.setDirection(FirstModel.Direction.LEFT);  // execute business logic
    }

    public void buttonRightClicked() {
        activity.showDirection(GOING_RIGHT);      //  dispatch command to change display
        firstModel.setDirection(FirstModel.Direction.RIGHT);  // execute business logic
    }


    public void buttonGoToSecondClicked() {
        activity.startActivityForResult(SetNameActivity.class, REQUEST_CODE);
    }


    public void buttonGoToDoNothingClicked() {
        Bundle data = new Bundle();
        data.putParcelable(MAIN_PRESENTER_DATA, firstModel);
        mContext.startActivity(DisplayInfoActivity.class, data);
    }

    /**
     * handle the callback method call forwarded by the corresponding Activity
     *
     * @param requestCode
     * @param extras
     */
    public void onActivityResult(int requestCode, Bundle extras) {
        if (requestCode == REQUEST_CODE) {
            String name = extras.getString(SetNameActivity.NAME_FIELD);
            activity.showName(name);
            firstModel.setName(name);
        }
    }
}
