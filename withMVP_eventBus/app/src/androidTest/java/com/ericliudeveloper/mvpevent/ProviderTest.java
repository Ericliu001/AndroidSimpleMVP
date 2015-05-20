package com.ericliudeveloper.mvpevent;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import android.util.Log;

import com.ericliudeveloper.mvpevent.mode.FirstModel;
import com.ericliudeveloper.mvpevent.provider.FirstModelTable;
import com.ericliudeveloper.mvpevent.provider.MyProvider;
import com.ericliudeveloper.mvpevent.provider.ProviderContract;

/**
 * Created by eric.liu on 20/05/15.
 */
public class ProviderTest extends ProviderTestCase2<MyProvider> {

    private MockContentResolver resolver;

    Class<MyProvider> providerClass = MyProvider.class;
    String providerAuthority = ProviderContract.CONTENT_AUTHORITY;

    public ProviderTest() {
        this(MyProvider.class, ProviderContract.CONTENT_AUTHORITY);

    }

    public ProviderTest(Class<MyProvider> providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        resolver = getMockContentResolver();
    }


    public void not_testInsertValues(){
        ContentValues values = new ContentValues();
        values.put(FirstModelTable.COL_DIRECTION, FirstModel.Direction.RIGHT.name());
        values.put(FirstModelTable.COL_PROGRESS, String.valueOf(50));
        values.put(FirstModelTable.COL_NAME, "Johny King");

        Uri uri = resolver.insert(ProviderContract.FirstModels.CONTENT_URI, values);
        Log.d("Eric", uri.toString());
        assertNotNull("The returned uri from insert is null.", uri);



        String[] projection = {
                FirstModelTable.COL_ID,
                FirstModelTable.COL_DIRECTION,
                FirstModelTable.COL_PROGRESS,
                FirstModelTable.COL_NAME,
        };

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        Cursor cursor = resolver.query(ProviderContract.FirstModels.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        assertEquals("You have wrong number of rows returned.", cursor.getCount(), 1);

        cursor.moveToFirst();
        cursor.getInt(cursor.getColumnIndex(FirstModelTable.COL_ID));
        String direction = cursor.getString(cursor.getColumnIndexOrThrow(FirstModelTable.COL_DIRECTION));
        assertEquals("The direction return by Provider is not correct", FirstModel.Direction.RIGHT, direction);
    }


    public void testPreconditions(){
        String[] projection = {
                FirstModelTable.COL_ID,
                FirstModelTable.COL_DIRECTION,
                FirstModelTable.COL_PROGRESS,
                FirstModelTable.COL_NAME,
        };

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor result = resolver.query(ProviderContract.FirstModels.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        assertNotNull("The returned cursor is null.", result);
    }
}
