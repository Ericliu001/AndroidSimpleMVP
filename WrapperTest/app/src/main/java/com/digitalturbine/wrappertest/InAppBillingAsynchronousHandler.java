package com.digitalturbine.wrappertest;

import android.os.Bundle;

/**
 * Interface used to handle asynchronous responses from inAppBilling library. 
 */
public interface InAppBillingAsynchronousHandler
{
	/**
	 * InAppBilling library calls this method when last request is finished.  
	 * @param bundle response data from last request. 
	 */
	public void handleResponse(Bundle bundle);
}