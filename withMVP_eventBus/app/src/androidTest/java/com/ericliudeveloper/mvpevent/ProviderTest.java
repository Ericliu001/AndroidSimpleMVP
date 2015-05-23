package com.ericliudeveloper.mvpevent;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.ericliudeveloper.mvpevent.model.DaoFactory;
import com.ericliudeveloper.mvpevent.model.FirstModel;
import com.ericliudeveloper.mvpevent.model.FirstModelDAO;
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
//        ContentProvider provider = new MyProvider();
//        provider.attachInfo(getContext(), null);
//        resolver.addProvider(providerAuthority, new MyProvider());
    }


    public void testInsertValues(){
        ContentValues values = new ContentValues();
        values.put(FirstModelTable.COL_DIRECTION, FirstModel.Direction.RIGHT.name());
        values.put(FirstModelTable.COL_PROGRESS, String.valueOf(50));
        values.put(FirstModelTable.COL_NAME, "Johny King");

        Uri uri = resolver.insert(ProviderContract.FirstModels.CONTENT_URI, values);
//        assertNotNull("The returned uri from insert is null.", uri);



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
        assertEquals("The direction return by Provider is not correct", FirstModel.Direction.RIGHT.name(), direction);
    }

    public void testProviderDaoFactory(){
        DaoFactory daoFactory = DaoFactory.getDaoFactory(DaoFactory.DaoFactoryType.CONTENT_PROVIDER);
        FirstModelDAO firstModelDAO = daoFactory.getFirstModelDAO();

        FirstModel firstModel = new FirstModel();
        firstModel.setProgress(55);
        firstModel.setName("King's Cross");
        firstModel.setDirection(FirstModel.Direction.LEFT);

        long modelId = firstModelDAO.saveFirstModel(firstModel);
        assertTrue("No id returned when trying to save the firstModel.", modelId >= 0);

        FirstModel readObject = firstModelDAO.getFirstModel(modelId);
        assertEquals("Read and Write FirstModel Objects are not the same.", firstModel.getId(), readObject.getId());
        assertEquals("Read and Write FirstModel Objects are not the same.", firstModel.getDirection(), readObject.getDirection());
        assertEquals("Read and Write FirstModel Objects are not the same.", firstModel.getProgress(), readObject.getProgress());
        assertEquals("Read and Write FirstModel Objects are not the same.", firstModel.getName(), readObject.getName());
    }




}
