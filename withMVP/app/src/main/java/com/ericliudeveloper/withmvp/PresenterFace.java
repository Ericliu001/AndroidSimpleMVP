package com.ericliudeveloper.withmvp;

import android.os.Bundle;

/**
 * Created by eric.liu on 15/05/15.
 */

/**
 * A common interface for all Presenters to implement
 */
public interface PresenterFace {
    /**
     * return the Model data as a Bundle, which prevents the Views in MVP have any knowledge about the data.
     * It is usually called the {@link CacheModelFragment} to retrieve data and save it during configuration changes
     * @return
     */
    Bundle getModelData();
}
