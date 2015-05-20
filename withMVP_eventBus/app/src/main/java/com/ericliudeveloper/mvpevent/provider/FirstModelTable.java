package com.ericliudeveloper.mvpevent.provider;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by eric.liu on 20/05/15.
 */
public class FirstModelTable implements BaseColumns{
    private FirstModelTable(){}

    public static final String TABLE_FIRSTMODEL = "firstmodel";
    public static final String COL_ID = _ID;
    public static final String COL_DIRECTION = "direction";
    public static final String COL_PROGRESS = "progress";
    public static final String COL_NAME = "name";


    private static final String TABLE_CREATE =
            " create table "
            + TABLE_FIRSTMODEL
            + " ( "
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + COL_DIRECTION + " TEXT, "
            + COL_PROGRESS + " INTEGER,  "
            + COL_NAME + " TEXT NOT NULL "
            + " ) "
             ;


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }



    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TRIGGER IF EXISTS " + TABLE_FIRSTMODEL);
        onCreate(db);
    }
}
