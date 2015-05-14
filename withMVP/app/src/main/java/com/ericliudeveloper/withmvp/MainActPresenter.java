package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by liu on 12/05/15.
 * <p/>
 * Presenter in MVP
 */
public class MainActPresenter {

    public static final String MAIN_PRESENTER_DATA = "main presenter data";
    private FirstModel firstModel;

    /**
     * The interface for the corresponding Activity to implement;
     * All methods declared here should be responsible for changing the display and only be responsible for that;
     * the startActivityForResult(...) method is an exception here because it is tightly coupled with an Activity so we just forward to call to the Activity.
     */
    public interface MainActFace {
        void displayLeft();

        void displayRight();

        void makeProgress(int progress);

        void displayName(String name);

        void startActivityForResult(Class<?> dest, int requestCode);
    }

    MainActFace mMainActFace;
    ContextFace mContext;
    private int progess = 0;
    private final static int REQUEST_CODE = 123;

    /**
     * The parameter passed in here should either be having no dependency on Android SDK
     * or could be Mocked during test, such as: MockCursor, MockContext, MockApplication
     *
     * @param face
     * @param context
     */
    public MainActPresenter(MainActFace face, ContextFace context) {
        mMainActFace = face;
        mContext = context;

        firstModel = new FirstModel();
    }

    /**
     * handle user button click to dispatch the changing display command to the View in MVP
     */
    public void buttonLeftClicked() {

        mMainActFace.displayLeft();
        firstModel.setDirection(FirstModel.Direction.LEFT);
    }

    public void buttonRightClicked() {

        mMainActFace.displayRight();
        firstModel.setDirection(FirstModel.Direction.RIGHT);
    }

    public void buttonIncreaseClicked() {

        mMainActFace.makeProgress(progess += 5);
        firstModel.setProgress(progess);
    }


    public void buttonGoToSecondClicked() {
        mMainActFace.startActivityForResult(SetNameActivity.class, REQUEST_CODE);
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
            mMainActFace.displayName(name);
            firstModel.setName(name);
        }
    }
}
