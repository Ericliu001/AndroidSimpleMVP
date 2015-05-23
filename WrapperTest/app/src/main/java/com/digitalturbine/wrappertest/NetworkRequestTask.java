package com.digitalturbine.wrappertest;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkRequestTask extends AsyncTask<String, Void, NetworkRequestTask.NetworkRequestResult>
{

	public class NetworkRequestResult
	{
		private String data;
		private boolean successful = true;
		private String url;
		private int httpStatus;

		public NetworkRequestResult(String data, boolean successful, String url, int httpStatus)
		{
			super();
			this.data = data;
			this.successful = successful;
			this.url = url;
			this.httpStatus = httpStatus;
		}

		protected String getData()
		{
			return data;
		}

		protected boolean isSuccessful()
		{
			return successful;
		}

		protected String getUrl()
		{
			return url;
		}

		protected int getHttpStatus()
		{
			return httpStatus;
		}

		@Override
		public String toString()
		{
			return "NetworkRequestResult [successful=" + successful + ", url=" + url + ", httpStatus=" + httpStatus + "]";
		}

	}

	public interface NetworkRequestCallback
	{
		public void onReceived(NetworkRequestResult result);
	}

	private boolean successful = true;
	private NetworkRequestCallback mNetworkRequestCallback;
	private int httpStatus = HttpStatus.SC_OK;

	private static final String LOG_TAG = "LOGIC";

	public NetworkRequestTask(NetworkRequestCallback mNetworkRequestCallback)
	{
		super();
		this.mNetworkRequestCallback = mNetworkRequestCallback;
	}

	@Override
	protected NetworkRequestResult doInBackground(String... urls)
	{
		String url = urls[0];
		NetworkRequestResult result = null;
		if (url == null || "".equals(url))
		{
			String data = "";
			successful = false;
			httpStatus = 0;
			result = new NetworkRequestResult(data, successful, url, httpStatus);
		}
		else
		{
			String data = load(url);
			result = new NetworkRequestResult(data, successful, url, httpStatus);
		}

		return result;
	}

	@Override
	protected void onPostExecute(NetworkRequestResult result)
	{
		super.onPostExecute(result);
		mNetworkRequestCallback.onReceived(result);
	}

	private String load(String url)
	{
		Log.d(LOG_TAG, "loading: " + url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		StringBuilder data = new StringBuilder();
		try
		{
			HttpResponse response = httpClient.execute(post);
			Log.d(LOG_TAG, "HTTP_STATUS is " + response.getStatusLine().getStatusCode());
			httpStatus = response.getStatusLine().getStatusCode();
			if (response != null)
			{
				String line = "";
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

				while ((line = br.readLine()) != null)
				{
					data.append(line + "\n");
					Log.d(LOG_TAG, line + "\n");
				}
				response.getEntity().consumeContent();
			}
		}
		catch (ClientProtocolException e)
		{
			Log.d(LOG_TAG, "ClientProtocolException");
			Log.e(LOG_TAG, "", e);
			successful = false;
		}
		catch (IOException e)
		{
			Log.d(LOG_TAG, "IOException");
			Log.e(LOG_TAG, "", e);
			successful = false;
		}
		//		catch (Exception e)
		//		{
		//			Log.e(LOG_TAG, "", e);
		//			successful = false;
		//		}
		if (httpStatus != HttpStatus.SC_OK)
		{
			Log.w(LOG_TAG, "Displaying error message because HTTP status is not 200, is: " + httpStatus);
			successful = false;
		}
		return data.toString();
	}
}
