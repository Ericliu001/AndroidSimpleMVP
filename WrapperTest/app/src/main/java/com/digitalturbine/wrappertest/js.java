package com.digitalturbine.wrappertest;

import android.util.Log;

import com.digitalturbine.wrappertest.Logic.WebViewHandler;

public class js
{
	private WebViewHandler webViewHandler = null;

	public js(WebViewHandler webViewHandler)
	{
		this.webViewHandler = webViewHandler;
	}

	public void setParam(String paramName, String value)
	{
//		Log.i("wrapper JS", "setParam");
		this.webViewHandler.setParam(paramName, value);
	}

	public void putParam(String paramName, String value)
	{
//		Log.i("wrapper JS", "putParam");
		this.webViewHandler.putParam(paramName, value);
	}

	public void open(String URL)
	{
		Log.i("wrapper JS", "open: " + URL);
		this.webViewHandler.open(URL);
	}

	public void openPost(String URL)
	{
		Log.i("wrapper JS", "openPost: " + URL);
		this.webViewHandler.openPost(URL);
	}
	
	public void parseString(String str)
	{
//		Log.i("wrapper JS", "parseResponse: " + str);
		this.webViewHandler.parseString(str);
	}

	public void goPendingState(String timeInMs, String url)
	{
//		Log.i("wrapper JS", "goToPending");
		this.webViewHandler.goPendingState(timeInMs, url);
	}

	public void sendSMS(final String successURL, final String failURL, final String phoneNumber, final String message)
	{
//		Log.i("wrapper JS", "sendSms");
		this.webViewHandler.sendSMS(successURL, failURL, phoneNumber, message);
	}

	public void unlock(String numOfStartups, String nextTimeCheck, String type)
	{
//		Log.i("wrapper JS", "unlock");
		this.webViewHandler.unlock(numOfStartups, nextTimeCheck, type);
	}

	public String getTBYBMessage()
	{
//		Log.i("wrapper JS", "getTBYBMessage");
		return webViewHandler.getTBYBMsg();
	}

	public void exit()
	{
//		Log.i("wrapper JS", "exit");
		this.webViewHandler.exit();
	}

	public void disableWifi(String successURL, String failURL, String message)
	{
//		Log.i("wrapper JS", "disableWifi");
		this.webViewHandler.disableWifi(successURL, failURL, message);
	}

	public void enableWifi(String URL)
	{
//		Log.i("wrapper JS", "enableWifi");
		this.webViewHandler.enableWifi(URL);
	}

	public void disableWifiPost(String successURL, String failURL, String message)
	{
//		Log.i("wrapper JS", "disableWifi");
		this.webViewHandler.disableWifiPost(successURL, failURL, message);
	}

	public void enableWifiPost(String URL)
	{
//		Log.i("wrapper JS", "enableWifi");
		this.webViewHandler.enableWifiPost(URL);
	}
	
	public void log(String message)
	{
		Log.i("wrapper JS", "log: " + message);
	}
}
