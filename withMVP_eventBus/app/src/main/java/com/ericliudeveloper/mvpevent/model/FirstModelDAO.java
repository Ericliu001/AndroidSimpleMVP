package com.ericliudeveloper.mvpevent.model;

import java.util.List;

/**
 * Created by liu on 23/05/15.
 */
public interface FirstModelDAO {

    public FirstModel getFirstModel(long id);
    public long saveFirstModel(FirstModel firstModel);
    public void bulkInsertFirstModelList(List<FirstModel> list);
}
