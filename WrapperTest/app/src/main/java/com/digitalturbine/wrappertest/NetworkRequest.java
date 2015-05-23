package com.digitalturbine.wrappertest;

import java.util.Hashtable;

public interface NetworkRequest
{
	// make new network request ( cancels previous network request if it is not finished )
	/**
	 * 
	 * @param name_id
	 * @param url
	 * @param data
	 * @param headers
	 * @param method can be null, in that case default method request will be used
	 * @param handler
	 */
	public void networkRequest(String name_id, String url, byte[] data, Hashtable headers, String method, NetworkRequestHandler handler);

	// cancel current request 
	public void networkReset();

	public interface NetworkRequestHandler
	{
		public void handleNetworkRequestFail(String name_id, int code, String errorMsg);

		public void handleNetworkRequestSuccess(String name_id, byte[] data);
	}
}
