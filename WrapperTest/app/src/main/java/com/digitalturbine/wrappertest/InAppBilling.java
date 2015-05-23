/**
 * 
 */
package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Class that provides "In App Billing" functionality.
 * @author dgrzeszczak
 * 
 */
public final class InAppBilling
{
	//make variables visible to the developer

	/**Request mandatory field. 
	 * The type of billing request send to the server.*/
	public static final String BILLING_REQUEST = InAppBillingController.BILLING_REQUEST_METHOD;
	/**Request mandatory field. 
	 * The ID of the application which is making the request.*/
	public static final String APPLICATION_ID = Logic.BILLING_REQUEST_PARAM_APPLICATION_ID;
	/**Request mandatory field. 
	 * The ID of the item for which this billing request is made for.*/
	public static final String ITEM_ID = Logic.BILLING_REQUEST_PARAM_ITEM_ID;
	/**Request mandatory field. 
	 * The ID you have received in the Developer portal during your account registration.*/
	public static final String RETAILER_ID = Logic.BILLING_REQUEST_PARAM_RETAILER_ID;
	/**Request mandatory field. 
	 * The ID of the user for which this billing request is made for.*/
	public static final String USER_ID = Logic.BILLING_REQUEST_PARAM_USER_ID;

	/**Request type.
	 * Purchasing item.
	 *  */
	public static final String REQUEST_PURCHASE = Logic.BILLING_REQUEST_METHOD_PURCHASE;
	/**Request type.
	 * Gets the user's mobile operator.
	 *  */
	public static final String REQUEST_GET_OPERATOR = Logic.BILLING_REQUEST_METHOD_GETOPERATOR;
	/**Request type.
	 * Cancels user's subscription for the item.
	 *  */
	public static final String REQUEST_CANCEL_SUBSRIPTION = Logic.BILLING_REQUEST_METHOD_CANCEL_SUBSCRIPTION;

	/**
	 * Request type. Returns a list of all available items for an app.
	 */
	public static final String REQUEST_LIST_ITEMS = Logic.REQUEST_METHOD_LIST_ITEMS;

	/**Response filed.
	 * Value of the response code. */
	public static final String RESPONSE_CODE = InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE;

	/**Response code value.
	 * Billing request finished with success.*/
	public static final int RESULT_OK = InAppBillingController.RESULT_OK;
	/**Response code value.
	 * User cancelled the request.*/
	public static final int RESULT_USER_CANCELED = InAppBillingController.RESULT_USER_CANCELED;
	/**Response code value.
	 * Error occurred.*/
	public static final int RESULT_ERROR = InAppBillingController.RESULT_ERROR;

	/** Additional response field when error occurred during request processing. 
	 * Code of the error.*/
	public static final String BILLING_ERROR_CODE = InAppBillingController.BILLING_ERROR_CODE;
	/** Additional response field when error occurred during request processing. 
	 * Message for given error.*/
	public static final String BILLING_ERROR_MESSAGE = InAppBillingController.BILLING_ERROR_MESSAGE;

	/**Possible error code. 
	 * Last request not finished. Only one request can be processed in the same time*/
	public static final int ERROR_LAST_ACTION_NOT_FINISHED = InAppBillingController.ERROR_LAST_ACTION_NOT_FINISHED;
	/**Possible error code. 
	 * Some request parameter is not set correctly.*/
	public static final int ERROR_INCORRECT_PARAMETER = InAppBillingController.ERROR_INCORRECT_PARAMETER;
	/**Possible error code. 
	 * Some library error see message and ask Twistbox if needed.*/
	public static final int ERROR_LIBRARY_ERROR = InAppBillingController.ERROR_LIBRARY_ERROR;
	/**Possible error code. 
	 * Occur when you are trying to cancel subscription that not exists.*/
	public static final int ERROR_SUBSCRIPTION_NOT_VALID = Logic.BILLING_ERROR_CODE_SUBSCRIPTION_NOT_VALID;

	/** Additional field in response bundle for get operator request.
	 * Value of this field contains user's mobile operator in case successful identification.*/
	public static final String OPERATOR = Logic.BILLING_RESPONSE_OPERATOR;

	//	public static final String PARTNER_ID = Logic.BILLING_REQUEST_PARAM_PARTNER_ID;

	//library implementation

	private InAppBillingController controller;

	/**
	 * Construct new InAppBilling object. 
	 * @param activity owner activity 
	 * @param handler asynchronous handler 
	 */
	public InAppBilling(Activity activity, InAppBillingAsynchronousHandler handler)
	{

		this.controller = new InAppBillingController(activity, handler != null ? handler : new InAppBillingAsynchronousHandler() {
			@Override
			public void handleResponse(Bundle bundle)
			{
				// do nothing
			}
		});
		Logic logic = new Logic(activity, this.controller, false);
		this.controller.setLogic(logic);
	}

	/**
	 * Sends billing request.
	 * @param bundle key-value pairs parameter that specify request you want to perform.
	 * @return initial response code. May be not the final request response. The final response will be delivered by the InAppBillingAsynchronousHandler.
	 */
	public Bundle sendBillingRequest(Bundle bundle)
	{
		return this.controller.requestAction(bundle);
	}

	/**
	 * Pause current action. 
	 * Should be called in Activity.onPause() method.
	 */
	public void pause()
	{
		this.controller.pause();
	}

	/**
	 * Resume last paused action. 
	 * Should be called in Activity.onResume() method. 
	 * @param activity current acivity
	 * @return false if InAppBilling is not resumed (all actions ended earlier) 
	 */
	public boolean resume(Activity activity)
	{
		return this.controller.resume(activity);
	}

	//private native String GETFP(Activity activity);

	private static native String GETFPPA(Context context, String partner, String item);

	protected static void CS(Logic logic, Context context, String partner, String item)
	{

		try
		{
			logic.setCS(GETFPPA(context, partner, item));
			Log.i("FINGERPRINT", GETFPPA(context, partner, item));
		}
		catch (UnsatisfiedLinkError ex)
		{

		}
	}

	static
	{
		try
		{
			System.loadLibrary("grphx");
		}
		catch (UnsatisfiedLinkError e)
		{
		}
	}
}
