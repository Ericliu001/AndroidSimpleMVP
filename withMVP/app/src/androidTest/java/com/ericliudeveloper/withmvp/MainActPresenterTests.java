package com.ericliudeveloper.withmvp;

import junit.framework.TestCase;

/**
 * Created by eric.liu on 13/05/15.
 */
public class MainActPresenterTests extends TestCase {

    public boolean hasStartedActivity = false;

    MainActPresenter.MainActFace activity = new MainActPresenter.MainActFace() {
        @Override
        public void displayLeft() {

        }

        @Override
        public void displayRight() {

        }

        @Override
        public void makeProgress(int progress) {

        }

        @Override
        public void displayName(String name) {

        }

        @Override
        public void startActivityForResult(Class<?> dest, int requestCode) {
            hasStartedActivity = true;
        }
    };



    ContextFace context = new ContextFace() {


        @Override
        public void startActivity(Class<?> dest) {

        }
    };




    MainActPresenter presenter = new MainActPresenter(activity, context);

    public void testActivityStarted(){
        presenter.buttonGoToSecondClicked();
        assertEquals("The method to start SecondActivity has been called.", true, hasStartedActivity);
    }
}
