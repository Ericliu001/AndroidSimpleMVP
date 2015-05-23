package com.digitalturbine.wrappertest;

public class StatusCode
{
	/** Operation is valid. */
	public static final int STATUS_CODE_SUCCESS = 0;
	public static final int STATUS_CODE_PENDING = 1;
	/** Purchase required to access the service. */
	public static final int STATUS_CODE_PURCHASE_REQUIRED = 2;
	public static final int STATUS_CODE_FAILED = 3;
	public static final int STATUS_CODE_SUCCES_ALREADY_PURCHASED = 4;
	public static final int STATUS_CODE_FAILED_NO_MONEY = 5;
	public static final int STATUS_CODE_SUBSCRIPTION_UPDATE = 6;
	/** Invalid or missing parameter. */
	public static final int STATUS_CODE_INVALID_PARAM = 20;
	/** Internal error check internal service configuration. */
	public static final int STATUS_CODE_INTERNAL_ERROR = 22;
	/** Internal service error. */
	public static final int STATUS_CODE_INTERNAL_SERVICE_ERROR = 99;
	/** Unable to connect to operator's billing system. */
	public static final int STATUS_CODE_UNABLE_CONNECT_BILLING = 100;
	/** Operator's billing system has returned an error. */
	public static final int STATUS_CODE_BILLING_ERROR = 101;
	/** Unable to load client's data from the billing database. */
	public static final int STATUS_CODE_BILLING_LOAD_ERROR = 102;
	/** Unable to save client's data to the billing database. */
	public static final int STATUS_CODE_BILLING_SAVE_ERROR = 103;
}