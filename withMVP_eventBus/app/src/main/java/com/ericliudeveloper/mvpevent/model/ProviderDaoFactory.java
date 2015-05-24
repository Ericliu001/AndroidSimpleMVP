package com.ericliudeveloper.mvpevent.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ericliudeveloper.mvpevent.MyApplication;
import com.ericliudeveloper.mvpevent.provider.FirstModelTable;
import com.ericliudeveloper.mvpevent.provider.ProviderContract;

import java.util.List;

/**
 * Created by liu on 23/05/15.
 */
public class ProviderDaoFactory extends DaoFactory {


    @Override
    public FirstModelDAO getFirstModelDAO() {
        return new FirstModelDaoImpl();
    }


    public static class FirstModelDaoImpl implements FirstModelDAO {
        Context mContext = MyApplication.getApplication();
        ContentResolver resolver = mContext.getContentResolver();

        @Override
        public FirstModel getFirstModel(long id) {

            Uri uri = ProviderContract.FirstModels.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            String[] projection = ProviderContract.FirstModels.PROJECTION;
            String selection = ProviderContract.FirstModels.SELECTION_BY_ID;
            String[] selectionArgs = {String.valueOf(id)};

            FirstModel firstModel = null;

            Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
            if ( cursor != null &&  cursor.moveToFirst()) {
                final long firstModel_id = cursor.getLong(cursor.getColumnIndexOrThrow(FirstModelTable.COL_ID));
                final String direction = cursor.getString(cursor.getColumnIndexOrThrow(FirstModelTable.COL_DIRECTION));
                final int progress = cursor.getInt(cursor.getColumnIndexOrThrow(FirstModelTable.COL_PROGRESS));
                final String name = cursor.getString(cursor.getColumnIndexOrThrow(FirstModelTable.COL_NAME));

                firstModel = new FirstModel();
                firstModel.setId(firstModel_id);
                firstModel.setDirection(FirstModel.Direction.valueOf(direction));
                firstModel.setProgress(progress);
                firstModel.setName(name);
            }


            return firstModel;
        }

        @Override
        public long saveFirstModel(FirstModel firstModel) {
            ContentValues values = getContentValues(firstModel);

            Uri uri = resolver.insert(ProviderContract.FirstModels.CONTENT_URI, values);

            String firstModel_id = uri.getLastPathSegment();
            if (firstModel_id == null){return  -1;}

            long id = Long.valueOf(firstModel_id);
            firstModel.setId(id);
            return id;
        }

        @Override
        public void bulkInsertFirstModelList(List<FirstModel> list) {
            ContentValues[] valuesArray = new ContentValues[list.size()];
            int index = 0;
            for(FirstModel firstModel: list){
                ContentValues values = getContentValues(firstModel);

                valuesArray[index] = values;
                index++;
            }

            //TODO move the bulk insert into a Service
            int count = resolver.bulkInsert(ProviderContract.FirstModels.CONTENT_URI, valuesArray);
        }

        private ContentValues getContentValues(FirstModel firstModel) {
            ContentValues values = new ContentValues();
            values.put(FirstModelTable.COL_DIRECTION, firstModel.getDirection().name());
            values.put(FirstModelTable.COL_PROGRESS, firstModel.getProgress());
            values.put(FirstModelTable.COL_NAME, firstModel.getName());
            return values;
        }
    }
}
