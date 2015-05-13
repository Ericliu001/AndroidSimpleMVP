package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by liu on 12/05/15.
 */
public class MainActPresenter {

    public interface MainActFace{
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

    public MainActPresenter(MainActFace face, ContextFace context){
        mMainActFace = face;
        mContext = context;
    }


    public void buttonLeftClicked() {
        mMainActFace.displayLeft();
    }

    public void buttonRightClicked() {
        mMainActFace.displayRight();
    }

    public void buttonIncreaseClicked() {
        mMainActFace.makeProgress(progess += 5);
    }

    public void buttonGoToSecondClicked() {
        mMainActFace.startActivityForResult(SecondActivity.class, REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, Bundle extras) {
        if (requestCode == REQUEST_CODE) {

            mMainActFace.displayName(extras.getString(SecondActivity.NAME_FIELD));
        }
    }
}
