package com.ericliudeveloper.withmvp;

import android.content.Intent;
import android.database.Cursor;
import android.test.mock.MockContext;
import android.test.mock.MockCursor;

import junit.framework.TestCase;

/**
 * Created by eric.liu on 13/05/15.
 */
public class MainActPresenterTests extends TestCase {

    static final String ERIC_IS_TIRED = "Eric is tired.";
    boolean hasStartedSetNameActivity = false; // flag to indicate if start activity has been called
    boolean hasStartedNothingActivity = false;

    /**
     * Mock the Activity.
     * It could be done because Presenter doesn't call methods in Activity directly,
     * alternatively, method calls are only forwarded to Activity through an interface: MainActFace
     */
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
            if (dest.equals(SetNameActivity.class)) {
                hasStartedSetNameActivity = true; // set the flag to indicate that start activity method has been called.
            }
        }
    };

    /**
     * MockContext class comes with Android SDK,
     * Not used here, just to show you that you can use it when you need it.
     */
    MockContext mockedContext = new MockContext() {

        @Override
        public void startActivity(Intent intent) {

        }

        @Override
        public String getPackageName() {
            return "com.something";
        }
    };

    ContextFace context = new ContextFace() {
        @Override
        public void startActivity(Class<?> dest) {

            hasStartedNothingActivity = true;
        }
    };


    MainActPresenter presenter = new MainActPresenter(activity, context);

    public void testActivitiesStarted() {
        presenter.buttonGoToSecondClicked();
        assertEquals("The method to start SetNameActivity has not been called.", true, hasStartedSetNameActivity);

        presenter.buttonGoToDoNothingClicked();
        assertTrue("Could not start DisplayInfoActivity", hasStartedNothingActivity);
    }


    public void testCursor() {
        Cursor cursor = new MockCursor() {
            @Override
            public boolean moveToFirst() {
                return true;
            }


            @Override
            public int getInt(int columnIndex) {
                return 3;
            }

            @Override
            public String getString(int columnIndex) {
                return ERIC_IS_TIRED;
            }
        };
        boolean isCursorValid = cursor.moveToFirst();
        assertTrue("The cursor is not valid", isCursorValid);

        assertEquals(3, cursor.getInt(0));
        assertSame(ERIC_IS_TIRED, cursor.getString(1));

    }
}
