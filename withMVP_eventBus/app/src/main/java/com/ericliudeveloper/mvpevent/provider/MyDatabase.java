package com.ericliudeveloper.mvpevent.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eric.liu on 20/05/15.
 */
public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ericliu.db";

    private static final int VER_2015_RELEASE_A = 1;
    private static final int CUR_DATABASE_VERSION = VER_2015_RELEASE_A;

    private final Context mContext;


    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FirstModelTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FirstModelTable.onUpgrade(db, oldVersion, newVersion);
    }


    public static void deleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }
}
