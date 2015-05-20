package com.ericliudeveloper.mvpevent.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ericliudeveloper.mvpevent.util.SelectionBuilder;

/**
 * Created by eric.liu on 20/05/15.
 */
public class MyProvider extends ContentProvider {

    private MyDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();


    private static final int FIRST_MODELS = 100;
    private static final int FIRST_MODELS_ID = 101;


    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProviderContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FirstModelTable.TABLE_FIRSTMODEL, FIRST_MODELS);
        matcher.addURI(authority, FirstModelTable.TABLE_FIRSTMODEL + "/*", FIRST_MODELS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MyDatabase(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FIRST_MODELS:
                return ProviderContract.FirstModels.CONTENT_TYPE;

            case FIRST_MODELS_ID:
                return ProviderContract.FirstModels.CONTENT_ITEM_TYPE;
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);


        switch (match) {
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);

            case FIRST_MODELS:
                final SelectionBuilder builder = new SelectionBuilder();

                builder.table(FirstModelTable.TABLE_FIRSTMODEL);
                builder.where(selection, selectionArgs);

                projection = new String[]{
                  FirstModelTable.COL_ID,
                  FirstModelTable.COL_DIRECTION,
                  FirstModelTable.COL_PROGRESS,
                  FirstModelTable.COL_NAME,
                };

                return builder.query(db, projection, null);
        }
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
