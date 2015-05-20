package com.ericliudeveloper.mvpevent.provider;

/**
 * Created by eric.liu on 20/05/15.
 */
public class ProviderContract {
    private ProviderContract(){}

    public static final String CONTENT_AUTHORITY = "com.ericliudeveloper.mvpevent";



    public static class FirstModels {
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.ericliu.androidmvp.firstmodel";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.ericliu.androidmvp.firstmodel";
    }
}
