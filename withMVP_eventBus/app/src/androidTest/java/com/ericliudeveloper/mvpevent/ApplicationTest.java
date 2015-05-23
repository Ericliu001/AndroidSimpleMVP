package com.ericliudeveloper.mvpevent;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.ericliudeveloper.mvpevent.model.DaoFactory;
import com.ericliudeveloper.mvpevent.model.FirstModel;
import com.ericliudeveloper.mvpevent.model.FirstModelDAO;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<MyApplication> {
    Application mApplication;

    public ApplicationTest() {
        super(MyApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        mApplication = getApplication();
    }


    public void testPreconditions(){
        assertNotNull(mApplication);
        assertTrue("Not instance of MyApplication", mApplication instanceof MyApplication);
    }


}