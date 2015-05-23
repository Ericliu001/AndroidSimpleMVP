package com.digitalturbine.wrappertest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

public class WiFiHelper
{

	public interface WiFiStateChangeHandler
	{
		public void onWifiDisabled();

		public void onWifiEnabled();
	}

	public class EnableWifiTask extends AsyncTask
	{
		private static final String LOG_TAG = "LOGIC";
		private WiFiHelper mWiFiHelper;
		private WiFiStateChangeHandler mWiFiStateChangeHandler;

		@Override
		protected Object doInBackground(Object... params)
		{
			mWiFiHelper = (WiFiHelper) params[0];
			mWiFiStateChangeHandler = (WiFiStateChangeHandler) params[1];

			enableWifi();
			while (!isWifiConnected())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					Log.e(LOG_TAG, "An exception occured while enabling WiFi", e);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			mWiFiStateChangeHandler.onWifiEnabled();
		}

	}

	public class DisableWifiTask extends AsyncTask
	{
		private static final String LOG_TAG = "LOGIC";
		private WiFiHelper mWiFiHelper;
		private WiFiStateChangeHandler mWiFiStateChangeHandler;

		@Override
		protected Object doInBackground(Object... params)
		{
			mWiFiHelper = (WiFiHelper) params[0];
			mWiFiStateChangeHandler = (WiFiStateChangeHandler) params[1];

			disableWifi();
			while ((isWifiEnabled() || isMobileConnectionConnecting()) && !isMobileConnectionAvailable())
			{
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					Log.e(LOG_TAG, "An exception occured while disabling WiFi", e);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			mWiFiStateChangeHandler.onWifiDisabled();
		}

	}

	private static final String NETWORK_MOBILE = "MOBILE";

	private Context mContext;
	private WifiManager mWifiManager;

	public WiFiHelper(Context mContext)
	{
		super();
		this.mContext = mContext;
	}

	/**
	 * Checks if wifi is enabled on the device.
	 * @return <code>true</code> if enabled
	 */
	public boolean isWifiEnabled()
	{
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		this.mWifiManager = wifiManager;
		return wifiManager.isWifiEnabled();
	}

	/**
	 * Checks if wifi is enabled and device is associated with an AP.
	 * @return <code>true</code> if connected
	 */
	public boolean isWifiConnected()
	{
		boolean result = false;
		if (isWifiEnabled() && mWifiManager != null)
		{
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			SupplicantState state = wifiInfo.getSupplicantState();
			//			Log.d(LOG_TAG, "WiFi state=" + state);
			if (state == SupplicantState.COMPLETED)
			{
				result = true;
			}
		}
		return result;
	}

	/** Disables WiFi if enabled. */
	private void disableWifi()
	{
		if (isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(false);
		}
	}

	private void enableWifi()
	{
		if (!isWifiEnabled())
		{
			mWifiManager.setWifiEnabled(true);
		}
	}

	public void disableWifi(WiFiStateChangeHandler handler)
	{
		new DisableWifiTask().execute(this, handler);
	}

	public void enableWifi(WiFiStateChangeHandler handler)
	{
		new EnableWifiTask().execute(this, handler);
	}

	/**
	* Gets the state of Airplane Mode.
	* 
	* @param context
	* @return <code>true</code> if enabled
	*/
	public boolean isAirplaneModeOn()
	{
		return Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

	/**
	 * Checks if mobile connection is connecting right at the moment.
	 * @return <code>true</code> if mobile connection is trying to connect
	 */
	public boolean isMobileConnectionConnecting()
	{
		boolean result = false;

		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
		for (NetworkInfo networkInfo : networkInfos)
		{
			if (networkInfo.getTypeName().equalsIgnoreCase(NETWORK_MOBILE))
			{
				if (!networkInfo.isConnected() && networkInfo.isConnectedOrConnecting())
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Gets the type of the connection.
	 * @return <code>true</code> when mobile connection is available
	 */
	public boolean isMobileConnectionAvailable()
	{
		boolean result = false;

		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
		for (NetworkInfo networkInfo : networkInfos)
		{
			if (networkInfo.getTypeName().equalsIgnoreCase(NETWORK_MOBILE))
			{
				while (isMobileConnectionConnecting())
				{
					/* Sleeping to check the outcome of the connecting. */
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						//						Log.e(LOG_TAG, "Failed pausing the thread, while checking if connection is available!", e);
					}
				}
				if (networkInfo.isConnected())
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}
}
