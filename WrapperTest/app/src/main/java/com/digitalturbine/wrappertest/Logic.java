/**
 * 
 */
package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.digitalturbine.wrappertest.WiFiHelper.WiFiStateChangeHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


/**
 * @author dgrzeszczak
 * 
 */
public class Logic implements LogicInterface
{

	//TODO common constants
	public static final String BILLING_REQUEST_METHOD_PURCHASE = "REQUEST_PURCHASE";
	public static final String BILLING_REQUEST_PARAM_APPLICATION_ID = "PACKAGE_NAME";
	public static final String BILLING_REQUEST_PARAM_ITEM_ID = "ITEM_ID";
	public static final String BILLING_REQUEST_PARAM_RETAILER_ID = "RETAILER_ID";
	public static final String BILLING_REQUEST_PARAM_PARTNER_ID = "PARTNER_ID";

	public static final String BILLING_REQUEST_PARAM_USER_ID = "USER_ID";
	public static final String BILLING_REQUEST_METHOD_GETOPERATOR = "GET_OPERATOR";
	public static final String BILLING_REQUEST_METHOD_CANCEL_SUBSCRIPTION = "CANCEL_SUBSCRIPTION";
	public static final String REQUEST_METHOD_LIST_ITEMS = "LIST_ITEMS";

	public static final String BILLING_RESPONSE_OPERATOR = "OPERATOR";

	public static final int BILLING_ERROR_CODE_SUBSCRIPTION_NOT_VALID = InAppBillingController.NUM_CONTROLLER_ERROR_CODES;

	//	private static final int ACTION_ID_AUTHENTICATE = 1;
	//	private static final int ACTION_ID_SETUP = 2;
	//	private static final int ACTION_ID_PURCHASE = 3;
	//	private static final int NUM_OF_ACTIONS = 3;

	private static final int RETURN_FINISHED = -2;
	private static final int RETURN_NOT_STARTED = -1;
	private static final int RETURN_IN_PROGRESS = 0;
	private static final int RETURN_CANCELLED = 1;
	private static final int RETURN_ERROR = 2;
	private static final int RETURN_LOGIN_SUCCESS = 3;
	private static final int RETURN_SUBSCRIPTION_PURCHASED = 4;
	private static final int RETURN_SUBSCRIPTION_VALID = 5;
	private static final int RETURN_SUBSCRIPTION_NOT_VALID = 6;
	/// --------------

	private static final String LOG_TAG = "LOGIC";
	private static final String RANDOM_STRING = "ab778159-f106-49d9-b8d4-00a7f9fb042b";
	// private Activity activity;
	private Resource resource;
	private Context context;
	private NetworkRequest network;

	private InAppBillingController controller;

	public Resource getResource()
	{
		return resource;
	}

	/** Current locale */
	private String locale;
	private String savedLocale;

	/** If true, the user has bought the game (no subscription!), and the game is started immediately. */
	public boolean isFullPurchase = false;
	/** If true, the user has rented the game. */
	public boolean isRental = false;

	public boolean isTryBeforeYouBuy = false;

	public boolean isPreUnlocked = false;

	/** If <code>true</code>, server-provided help text is available. */
	boolean customHelpTextAvailable;

	private int numTimesStarted = 0;
	private int numTimesConnected = 0;

	/** The ID of the currently displayed screen. */
	private int screenId = -1;

	/** The most recently executed action. */
	private int lastAction = 0;
	private Object actionParam = null;

	// Softkeys
	// Object dynamicLsk, dynamicRsk;
	int dynamicLskAction = 0, dynamicRskAction = 0;

	private boolean isInPurchase = false;
	private boolean isInvokingIpTerms = false;
	// private boolean purchaseFirstStep = true;
	private String purchaseButton;

	private boolean isSubscriptionValid = false;

	private boolean initialized = false;
	private boolean isInitializig = false;

	private String serviceId = "";

	private String CS = "0";

	private String validIMSI = "";
	private String savedChecksum = "";

	private BillingMethod billingMethod = BillingMethod.MO_MT;

	protected boolean isInitialized()
	{
		return initialized;
	}

	private boolean isAuthenticated = false;

	protected boolean isAuthenticated()
	{
		return isAuthenticated;
	}

	/**
	 * Returns if customer is allowed to play the game instantly after purchase.
	 * @return <code>true</code> if pre unlock is allowed 
	 */
	public boolean isPreUnlockEnabled()
	{
		return GeneratedConstants.BUSINESS_RULES_ALLOW_PREUNLOCK;
	}

	//	private void handleAuthenticate()
	//	{
	//
	//	}

	//	public static final int ACTION_ID_LOGIN = NUM_OF_ACTIONS + 1;
	//	public static final int ACTION_ID_HELP_SCREEN = NUM_OF_ACTIONS + 2;
	//	public static final int ACTION_ID_EXIT = NUM_OF_ACTIONS + 3;

	private int return_state = RETURN_IN_PROGRESS;

	private int versionCode = 1;
	//	private long crc = 0;
	private String IMSI = null;
	private String MCC_MNC = null;
	//	private String token = "";
	//	private String keyword = null;
	//	private String shortcode = null;
	//	private boolean isSMSsent = false;
	//	private int purchaseType = PurchaseType.PURCHASE_TYPE_NONE;

	private TelephonyManager telephonyManager = null;

	/**
	 * Returns the current IMSI.
	 * @return the IMSI as a string
	 */
	private String getImsi()
	{
		String result = "";
		if (telephonyManager != null)
		{
			result = telephonyManager.getSubscriberId();
		}
		//TODO for testing only
		//result = "234022104892450";
		return result;
	}

	/**
	 * Returns the ID of the sim operator.
	 * @return the ID as a string
	 */
	private String getMccMnc()
	{
		String result = "";
		if (telephonyManager != null)
		{
			result = telephonyManager.getSimOperator();
		}
		return result;
	}

	public Logic(Activity activity, InAppBillingController controller, boolean init)
	{
		//		Log.i(LOG_TAG, "Logic.Logic()");
		this.context = activity.getApplicationContext();
		this.resource = new Resource(activity);
		this.network = this.resource;
		this.controller = controller;

		this.webHandler = new WebViewHandler();

		readRMS();

		loadBusinessRules(); // for now here

		pendingHandler = new Handler();

		telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

		IMSI = getImsi();
		MCC_MNC = getMccMnc();

		if (IMSI != null && "".equals(IMSI.trim()))
			IMSI = null;

		if (MCC_MNC != null && "".equals(MCC_MNC.trim()))
			MCC_MNC = null;

		try
		{
			versionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
		}
		catch (NameNotFoundException e)
		{
		}

		// If current IMSI doesn't equals saved IMSI, clear token and request a new one
		//		if (isFullPurchase || isRental)
		//		{
		//			if (!IMSI.equals(readImsiFromFile()))
		//			{
		//				this.token = "";
		//			}
		//		}

		if (init)
			init(true);
	}

	/**
	 * Checks if current Imsi equals the stored Imsi.
	 * @param currentImsi
	 * @param storedImsi
	 * @return <code>false</code> if both Imsi don't equal
	 */
	private boolean checkImsi(String currentImsi, String storedImsi)
	{
		return currentImsi.equals(storedImsi);
	}

	//	public void setCRC(long crc)
	//	{
	//		this.crc = crc;
	//	}

	/**
	 * Frees up internal resources and sets variables to <code>null</code>.
	 */
	public void free()
	{
		this.context = null;
		this.resource.freeStaticData();
		this.resource = null;
		this.controller = null;
		this.telephonyManager = null;
	}

	/**
	 * Loads configuration and strings used for the wrapping app to display.
	 * @param showProgress set to <code>true</code> to show progress dialog while loading
	 */
	public void init(boolean showProgress)
	{
		if (initialized)
			return;
		if (isInitializig)
		{
			while (isInitializig)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
			}

			return;
		}

		isInitializig = true;

		// // Load the language configuration
		// resource.fetchLanguageConfig();

		// Get the default language (may be null if not available or not
		// supported)
		locale = resource.fetchDefaultLanguageName(resource.getLocaleNative());

		if (locale == null)
		{
			// Select first locale as default locale
			locale = resource.fetchLanguageName(0);
		}

		if (savedLocale == null)
		{
			savedLocale = locale;
		}

		// Set the locale
		setLocale(locale, false);

		if (showProgress)
			controller.startProgressDialog((String) resource.getResource(CommonConstants.RESOURCE_TEXT_LOADING));
		// load config
		resource.fetchApplicationConfig();
		// update parameters

		if (!"".equals(GeneratedConstants.LIB_AUTHENTICATION_URL))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_URL, GeneratedConstants.LIB_AUTHENTICATION_URL);

		if (!"".equals(GeneratedConstants.LIB_ARTICLE_ID))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_CONTENT_ID, GeneratedConstants.LIB_ARTICLE_ID);

		if (!"".equals(GeneratedConstants.LIB_PARTNER_ID))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_PARTNER_ID, GeneratedConstants.LIB_PARTNER_ID);

		if (!"".equals(GeneratedConstants.LIB_VERSION))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_VERSION, GeneratedConstants.LIB_VERSION);

		if (!"".equals(GeneratedConstants.LIB_PROTOCOL_VERSION))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_PROTOCOL_VERSION, GeneratedConstants.LIB_PROTOCOL_VERSION);

		if (!"".equals(GeneratedConstants.LIB_RETAILER_ID))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_RETAILER_ID, GeneratedConstants.LIB_RETAILER_ID);

		if (!"".equals(GeneratedConstants.LIB_OPERATOR))
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_OPERATOR, GeneratedConstants.LIB_OPERATOR);

		if (GeneratedConstants.DEMO_BUILD && GeneratedConstants.DEMO_OFFLINE)
			resource.updateResource(CommonConstants.JAD_PARAM_AYCE_URL, "file:///android_asset");

		// only for testing purposes (must be commented)
		//		if (true)
		//		{
		//			//http://89.19.231.149:8090/bps/api/purchaseinfo?
		//
		//			//resource.updateResource(CommonConstants.JAD_PARAM_AYCE_TEST_HEADERS_ENABLED, "true");
		//			this.crc = 0;
		//			//this.IMSI = "214039441705125";
		//			//this.MCC_MNC = "21401";
		//			this.versionCode = 100;
		//
		//		}

		// Load .jad parameters
		resource.loadJadParams();

		loadResources();

		// If the wrapper has not been started before
		if (numTimesStarted++ == 0)
		{

			// It has not been connected either
			numTimesConnected = 0;
		}

		initialized = true;
		isInitializig = false;
	}

	//	public int logic()
	//	{
	//		if (isPaused)
	//			resume();
	//
	//		return return_state;
	//	}

	public void end()
	{
		writeRMS();

		//clear webview 
		String data = "<div></div>";
		webView.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
		webView.clearView();
	}

	private boolean loadResources()
	{

		return true;
	}

	/**
	 * Reads the RMS(?). This method restores saved values like, local, number of times started & connected, 
	 * if application is purchased, the purchase type, the token to identify the customer, if the activation 
	 * sms is send and the purchase type.
	 */
	private synchronized void readRMS()
	{
		// #if DEBUG_true || DEBUG_rms
		// @ System.out.println("Reading configuration from RMS");
		// #endif

		DataInputStream dis = null;

		// Read the record that stores the configuration
		byte[] data = Resource.getRecord(context, CommonConstants.RECORDSTORE_NAME_CONFIG);

		if (data == null)
		{
			// #if DEBUG_true
			// @ System.out.println("record not found");
			// #endif
			return;
		}

		try
		{
			dis = new DataInputStream(new ByteArrayInputStream(data));

			savedLocale = dis.readUTF();

			// Read the number of times the wrapper has been started
			numTimesStarted = dis.readInt();

			// Read the number of times the wrapper has been connected
			numTimesConnected = dis.readInt();

			isFullPurchase = dis.readBoolean();

			isRental = dis.readBoolean();

			//			token = dis.readUTF();
			//
			//			isSMSsent = dis.readBoolean();
			//
			//			purchaseType = dis.readInt();

			validIMSI = dis.readUTF();

			savedChecksum = dis.readUTF();

			webHandler.loadParams(dis);

		}
		catch (Exception e)
		{

		}
		finally
		{
			try
			{
				dis.close();
				dis = null;
			}
			catch (Exception ignore)
			{
			}
		}
	}

	/**
	 * Saves the RMS(?). This method stores saved values like, local, number of times started & connected, 
	 * if application is purchased, the purchase type, the token to identify the customer, if the activation 
	 * sms is send and the purchase type.
	 */
	private synchronized void writeRMS()
	{

		// #if DEBUG_true || DEBUG_rms
		// @ System.out.println("Writing configuration to RMS");
		// #endif

		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		try
		{
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);

			dos.writeUTF(savedLocale);

			// Write the number of times the wrapper has been started
			dos.writeInt(numTimesStarted);
			//			Log.i(LOG_TAG, "writeRMS(): numTimesStarted=" + numTimesStarted);

			// Write the number of times the wrapper has been connected
			dos.writeInt(numTimesConnected);

			dos.writeBoolean(isFullPurchase);
			//			Log.i(LOG_TAG, "writeRMS(): isFullPurchase=" + isFullPurchase);

			dos.writeBoolean(isRental);
			//			Log.i(LOG_TAG, "writeRMS(): isRental=" + isRental);

			//			dos.writeUTF(token);
			//			//			Log.i(LOG_TAG, "writeRMS(): token=" + token);
			//
			//			dos.writeBoolean(isSMSsent);
			//
			//			dos.writeInt(purchaseType);

			dos.writeUTF(validIMSI);

			dos.writeUTF(savedChecksum);

			webHandler.saveParams(dos);

			Resource.putRecord(context, CommonConstants.RECORDSTORE_NAME_CONFIG, bos.toByteArray());

		}
		catch (Exception e)
		{
			// #if DEBUG_true || DEBUG_rms
			// @ System.out.println("FAILED: " + e.getClass().getName() + ":" +
			// e.getMessage());
			// #endif
		}
		finally
		{
			try
			{
				dos.close();
				dos = null;
			}
			catch (Exception ignore)
			{
			}
		}
	}

	private void setLocale(String loc, boolean updateGB)
	{

		locale = resource.processLocale(loc);

		// #if DEBUG_true
		// @ System.out.println("setting locale: " + locale);
		// #endif

		try
		{
			// if (ComTwistboxWrapperMIDlet.isInWrapper()) {
			// Load text resources
			resource.fetchLanguage(locale, customHelpTextAvailable);

			// Change the language stored in the Now+ client
			// resource.npc.setAcceptLanguage(locale);

			if (updateGB)
			{
				Hashtable params = new Hashtable();
				params.put("language", locale);
				// resource.callSetParameters(params);
			}
			// }
		}
		catch (Exception e)
		{
		}
	}

	//	private boolean isInPendingState = false;
	private Handler pendingHandler = null;

	private Runnable pendingTask = null;

	/**
	 * Stores a given IMSI.
	 * @param imsi
	 */
	private synchronized void storeImsi(String imsi)
	{

		ByteArrayOutputStream bos = null;
		DataOutputStream dos = null;

		try
		{
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);

			dos.writeUTF(imsi);

			Resource.putRecord(context, CommonConstants.RECORDSTORE_NAME_IMSI, bos.toByteArray());

		}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				dos.close();
				dos = null;
				bos.close();
				bos = null;
			}
			catch (Exception ignore)
			{
			}
		}

	}

	/**
	 * Reads an IMSI from the SharedPreferences.
	 * @return the stored IMSI, or an empty string when reading fails
	 */
	private synchronized String readImsiFromFile()
	{
		String result = "";
		DataInputStream dis = null;

		// Read the record that stores the configuration
		byte[] data = Resource.getRecord(context, CommonConstants.RECORDSTORE_NAME_IMSI);

		try
		{
			dis = new DataInputStream(new ByteArrayInputStream(data));

			result = dis.readUTF();
		}
		catch (Exception e)
		{

		}
		finally
		{
			try
			{
				dis.close();
				dis = null;
			}
			catch (Exception ignore)
			{
			}
		}

		return result;
	}

	private Hashtable parseResponse(String data)
	{
		Hashtable resp = new Hashtable();

		String[] lines = data.split("\n");
		String[] out = null;
		for (String line : lines)
		{
			if (line != null)
			{
				//out = line.split("=");
				// split on first occurence 
				int id = line.indexOf("=");
				if (id != -1)
				{
					String a = null, b = null, c = null;
					c = line.substring(0, id);
					if (c != null && !"".equals(c.trim()))
						a = c;
					c = line.substring(id + 1);
					if (c != null && !"".equals(c.trim()))
						b = c;
					if (a != null && b != null)
						out = new String[] { a, b };
				}
			}
			if (out != null && out.length > 1)
				resp.put(out[0], out[1]);
		}

		return resp;
	}

	private Hashtable parseResponse(byte[] data)
	{
		String strData = "";
		try
		{
			strData = new String(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{

		}

		return parseResponse(strData);
	}

	private void callWebBrowser(String url)
	{
		try
		{
			controller.callWebBrowser(url);
		}
		catch (Exception e)
		{
			resource.updateResource(CommonConstants.CLIENT_TEXT_ERROR_EXCEPTION, e.getMessage());
			//		setDynamicTextScreen(CommonConstants.RESOURCE_TEXT_TITLE_ERROR, CommonConstants.RESOURCE_TEXT_ERROR_EXCEPTION, CommonConstants.RESOURCE_SOFT_EXIT, CommonConstants.RESOURCE_SOFT_OK, ACTION_ID_EXIT, screenId, Graphics.LEFT);
		}
	}

	public void triggerExitEvent()
	{
		//		Log.i(LOG_TAG, "triggerExitEvent()");
		endLogic(RETURN_CANCELLED);
	}

	private String errorMessage;
	private int errorCode;

	public void triggerError(int code, String msg)
	{
		this.errorCode = code;
		this.errorMessage = msg;
		endLogic(RETURN_ERROR);
	}

	private void endLogic(Bundle bundle)
	{
		controller.runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				layout.removeAllViews();
			}
		});
		enableWifi();
		bundle.putAll(lastBundleAction); // put all params from request if someone need information 
		if (!isPaused)
			logicHandler.logicEnded(bundle);
		else
			bundleToReturn = bundle;

		return_state = RETURN_FINISHED;

		currentWebView = null;

	}

	private void endLogic(int state)
	{
		return_state = state;

		Bundle response = new Bundle();

		switch (return_state)
		{
			case RETURN_SUBSCRIPTION_VALID:
			case RETURN_SUBSCRIPTION_PURCHASED:
				response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
				break;
			case RETURN_ERROR:
				response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_ERROR);
				response.putInt(InAppBillingController.BILLING_ERROR_CODE, this.errorCode);
				response.putString(InAppBillingController.BILLING_ERROR_MESSAGE, this.errorMessage);
				break;
			case RETURN_SUBSCRIPTION_NOT_VALID:
				response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_ERROR);
				response.putInt(InAppBillingController.BILLING_ERROR_CODE, BILLING_ERROR_CODE_SUBSCRIPTION_NOT_VALID);
				response.putString(InAppBillingController.BILLING_ERROR_MESSAGE, "Subscription not valid");
			case RETURN_CANCELLED:
				response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_USER_CANCELED);
				break;
		}

		endLogic(response);
	}

	public String getLastError()
	{
		return this.errorMessage;
	}

	private String lastSVCID = null;
	public static final String START_ACTION_ID = "dtpayauthentication";
	public static final String PURCHASE_ACTION_ID = "purchasestatus";
	public static final String CREATE_SESSION_ACTION_ID = "createsession";
	public static final String FINALIZE_SESSION_ACTION_ID = "finalizesession";
	public static final String GET_OPERATOR_ACTION_ID = "getoperator";
	public static final String LIST_ITEMS_ACTION_ID = "listitems";
	public static final String CANCEL_SUBSCRIPTION_ACTION_ID = "terminatesubscription";

	protected static final String PARAM_ARTICLE_ID = "artid";

	protected static final String PARAM_RETAILER_ID = "retid";
	protected static final String PARAM_MONDIA_OPERATOR = "op";

	protected static final String PARAM_CHECKSUM = "cs";
	protected static final String PARAM_PROTOCOL_VERSION = "protv";
	protected static final String PARAM_ARTICLE_VERSION = "artv";
	protected static final String PARAM_WRAPPER_VERSION = "libv";
	protected static final String PARAM_SERVICE_ID = "svcid";

	protected static final String PARAM_IMSI = "imsi";
	protected static final String PARAM_TOKEN = "tok";
	protected static final String PARAM_MCCMNC = "mccmnc";

	protected static final String PARAM_STATUS = "status";
	protected static final String PARAM_MESSAGE = "message";
	protected static final String PARAM_DESCRIPTION = "description";
	protected static final String PARAM_PRICE = "price";
	protected static final String PARAM_TERMS_AND_CONDITIONS = "terms";
	protected static final String PARAM_SHORTCODE = "shortcode";
	protected static final String PARAM_KEYWORD = "keyword";
	//protected static final String PARAM_TOKEN = "token"; 
	protected static final String PARAM_STORE_URL = "storeurl";
	protected static final String PARAM_STORE_NAME = "sorename";
	protected static final String PARAM_PURCHASE_DESCRITION = "purchase-description";
	protected static final String PARAM_PURCHASE_TYPE = "purchase-type";
	protected static final String PARAM_BILLING_METHOD = "billing-method";
	protected static final String PARAM_IDENTIFICATION_URL = "identification-url";
	protected static final String PARAM_WIFI = "wifi";

	protected static final String PARAM_TRY_BEFORE_YOU_BUY = "tbyb";
	protected static final String PARAM_USER_ID = "uid";
	protected static final String PARAM_OPERATOR = "operator";
	protected static final String PARAM_ITEM_ID = "itemid";

	// private boolean wasWifiMessageShown = false;
	private boolean wasWifiDisabled = false;

	private void enableWifi()
	{
		if (wasWifiDisabled)
		{
			wasWifiDisabled = false;
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);

		}
	}

	private int demoCheckedTimes = -1;

	private void networkRequest(String name_id, String url, String params, Hashtable header, boolean blocking)
	{

	}

	private Hashtable setupRequestParams()
	{
		Hashtable params = new Hashtable();
		params.put("Content-Type", "application/x-www-form-urlencoded");

		String userAgentEnabled = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_USER_AGENT_ENABLED);
		if ((userAgentEnabled != null) && (userAgentEnabled.toLowerCase().equals("true")))
		{
			String useragent = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_USER_AGENT);
			params.put("User-Agent", useragent);
		}

		// params.put("Accept-Language", "de");

		String test = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_TEST_HEADERS_ENABLED);
		if ((test != null) && test.toLowerCase().equals("true"))
		{
			params.put("x-up-calling-line-id", "491730451875");
		}

		return params;

	}

	private boolean isPaused = false;

	@Override
	public void pause()
	{
		isPaused = true;
		if (pendingTask != null)
		{
			pendingHandler.removeCallbacks(pendingTask);
		}

		layout.removeAllViews();
		//enableWifi();
	}

	@Override
	public void resume()
	{
		isPaused = false;
		if (return_state == RETURN_FINISHED && bundleToReturn != null)
			logicHandler.logicEnded(bundleToReturn);
		else if (pendingTask != null)
			pendingTask.run();

	}

	// ************************************************************************
	// * Business Rules

	private boolean businessRulesTBYBfinished = false;

	private int businessRulesNumOfStartups;
	private int businessRulesNumOfUsedStartups;
	private long businessRulesNewStartupTime;
	private long businessRulesPlayTime;
	private long businessRulesTbybPlaytime = GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME;
	private int businessRulesNumOfFreeStartups = GeneratedConstants.BUSINESS_RULES_TBYB_NUM_OF_FREE_STARTUPS;

	private long businessRulesNextSubscriptionCheckTime;

	private long businessRulesLastStartTime = 0;
	/** Saves business rules on every 1 minute. */
	public static final int BUSINESS_RULES_SAVE_TIME = 1000;
	private long businessRulesLastSaveTime = 0;
	private boolean businessRulesStartMessageShown = false;

	private boolean businessRulesIsNewStart = false;

	private boolean businessRulesMenageSubsriptionDialogShow = true;
	private boolean isMessageShown = false;

	public void saveBusinessRules(boolean force)
	{
		//		Log.i(LOG_TAG, "saveBusinessRules()");
		long currentTime = getCurrentTime();
		if (force || currentTime < businessRulesLastSaveTime || currentTime - businessRulesLastSaveTime > BUSINESS_RULES_SAVE_TIME)
		{
			//			Log.i(LOG_TAG, "do save");
			businessRulesLastSaveTime = currentTime;
			// do save
			ByteArrayOutputStream bos = null;
			DataOutputStream dos = null;

			try
			{
				bos = new ByteArrayOutputStream();
				dos = new DataOutputStream(bos);

				dos.writeInt(businessRulesNumOfStartups);
				//				Log.i(LOG_TAG, "businessRulesNumOfStartups=" + businessRulesNumOfStartups);
				dos.writeInt(businessRulesNumOfUsedStartups);
				//				Log.i(LOG_TAG, "businessRulesNumOfUsedStartups=" + businessRulesNumOfUsedStartups);
				dos.writeLong(businessRulesNewStartupTime);
				//				Log.i(LOG_TAG, "businessRulesNewStartupTime=" + businessRulesNewStartupTime);
				dos.writeLong(businessRulesPlayTime);
				//				Log.i(LOG_TAG, "businessRulesPlayTime=" + businessRulesPlayTime);
				dos.writeBoolean(businessRulesStartMessageShown);
				//				Log.i(LOG_TAG, "businessRulesStartMessageShown=" + businessRulesStartMessageShown);
				dos.writeBoolean(businessRulesMenageSubsriptionDialogShow);
				//				Log.i(LOG_TAG, "businessRulesMenageSubsriptionDialogShow=" + businessRulesMenageSubsriptionDialogShow);
				dos.writeBoolean(businessRulesTBYBfinished);
				//				Log.i(LOG_TAG, "businessRulesTBYBfinished=" + businessRulesTBYBfinished);
				dos.writeLong(businessRulesNextSubscriptionCheckTime);
				//				Log.i(LOG_TAG, "businessRulesNextSubscriptionCheckTime=" + businessRulesNextSubscriptionCheckTime);

				dos.writeBoolean(isTryBeforeYouBuy);

				dos.writeLong(businessRulesTbybPlaytime);
				dos.writeInt(businessRulesNumOfFreeStartups);

				Resource.putRecord(context, CommonConstants.RECORDSTORE_NAME_WRAPPER, bos.toByteArray());

				//				BusinessRules rules = new BusinessRules(businessRulesTBYBfinished, businessRulesLastStartTime, businessRulesLastSaveTime, businessRulesStartMessageShown, businessRulesIsNewStart, businessRulesMenageSubsriptionDialogShow);
				//				Log.i(LOG_TAG, rules.toString());

			}
			catch (Exception e)
			{
				//				Log.e(LOG_TAG, "Failed to save business rules!", e);
			}
			finally
			{
				try
				{
					dos.close();
					dos = null;
				}
				catch (Exception e)
				{
					//					Log.e(LOG_TAG, "Failed to close stream after saving business rules!", e);
				}
			}
		}
	}

	private void loadBusinessRules()
	{
		//		Log.i(LOG_TAG, "loadBusinessRules()");
		DataInputStream dis = null;

		// Read the record that stores the configuration
		byte[] data = Resource.getRecord(context, CommonConstants.RECORDSTORE_NAME_WRAPPER);

		if (data != null)
		{

			try
			{
				dis = new DataInputStream(new ByteArrayInputStream(data));

				businessRulesNumOfStartups = dis.readInt();
				businessRulesNumOfUsedStartups = dis.readInt();
				businessRulesNewStartupTime = dis.readLong();
				businessRulesPlayTime = dis.readLong();
				businessRulesStartMessageShown = dis.readBoolean();
				businessRulesMenageSubsriptionDialogShow = dis.readBoolean();
				businessRulesTBYBfinished = dis.readBoolean();
				businessRulesNextSubscriptionCheckTime = dis.readLong();
				isTryBeforeYouBuy = dis.readBoolean();
				businessRulesTbybPlaytime = dis.readLong();
				businessRulesNumOfFreeStartups = dis.readInt();
				BusinessRules rules = new BusinessRules(businessRulesTBYBfinished, businessRulesLastStartTime, businessRulesLastSaveTime, businessRulesStartMessageShown, businessRulesIsNewStart, businessRulesMenageSubsriptionDialogShow);
				//				Log.i(LOG_TAG, rules.toString());
				return;

			}
			catch (Exception e)
			{
			}
			finally
			{
				try
				{
					dis.close();
					dis = null;
				}
				catch (Exception ignore)
				{
				}
			}
		}

		// if it is not saved yet read standard from config file

		businessRulesNumOfStartups = businessRulesNumOfFreeStartups;
		businessRulesNumOfUsedStartups = 0;
		businessRulesNewStartupTime = GeneratedConstants.BUSINESS_RULES_NEW_STARTUP_TIME;// 60000
		businessRulesTBYBfinished = false; // *
		businessRulesNextSubscriptionCheckTime = 0;
		// 2;//*
		// 6
		// *60;
		businessRulesPlayTime = 0;

	}

	/**
	 * Updates the subscription model after a successful subscription.
	 * @param checkTime when the next check if still rented occurs
	 * @param numOfStartups the number of allowed start ups
	 */
	private void businessRulesHandleSubscriptionUpdate(long checkTime, int numOfStartups)
	{
		//		Log.i(LOG_TAG, MessageFormat.format("businessRulesHandleSubscriptionUpdate({0}, {1})", checkTime, numOfStartups));
		if (checkTime > 0)
		{
			businessRulesNextSubscriptionCheckTime = getCurrentTime() + (checkTime);// * 60 * 1000);
		}
		else
		{
			businessRulesNextSubscriptionCheckTime = getCurrentTime() + GeneratedConstants.BUSINESS_RULES_NEXT_SUBSCRIPTION_CHECK_TIME;
		}

		if (numOfStartups > 1)
			businessRulesNumOfStartups = numOfStartups;
		businessRulesNumOfUsedStartups = -1;

		// why it was added ? 
		//		if (isRental)
		//		{
		//			businessRulesNumOfUsedStartups = 0;
		//		}

		//		Log.i("businessRulesSubscritpionUpdate", isFullPurchase + " " + businessRulesNextSubscriptionCheckTime + " " + businessRulesNumOfStartups);
		saveBusinessRules(true);
	}

	// private static final long LAST_START_TIME = 60 * 1000;//ONE_HOUR * 8;
	// private long actionStartTime = 0;

	/**
	 * Checks if business rules still apply.
	 * @return <code>true</code> if everything is okay, <code>false</code> when business rules have been violated
	 */
	public boolean checkBusinessRules()
	{
		//		Log.i(LOG_TAG, "checkBusinessRules()");
		long currentTime = getCurrentTime();
		//		Log.d(LOG_TAG, "savedChecksum: " + savedChecksum);

		/* Checks if rental conditions still apply */
		if (isRental())
		{
			//			Log.d(LOG_TAG, "generatedChecksum: " + generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isRental), RANDOM_STRING));

			if (!savedChecksum.equals(generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isRental), RANDOM_STRING)))
			{
				isRental = false;
				savedChecksum = "";
				webHandler.clearSavedParams();
				writeRMS();
				Log.i(LOG_TAG, "checkBusinessRules, Rental Checksum error");
				return false;
			}
			if (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime)
			{
				businessRulesIsNewStart = true;
			}
			/* Checks if amount of allowed rental startups is reached */
			if (hasNumberOfStartupsExceeded() || (businessRulesNextSubscriptionCheckTime < getCurrentTime()))
			{
				Log.i(LOG_TAG, "checkBusinessRules, Rental Number of startups exceeded");
				return false;
			}
			else
			{
				Log.i(LOG_TAG, ((businessRulesNextSubscriptionCheckTime - getCurrentTime()) / 1000) + " seconds left.");
				Log.i(LOG_TAG, (businessRulesNumOfStartups - businessRulesNumOfUsedStartups) + " starts left.");
				Log.i(LOG_TAG, "checkBusinessRules, Rental check OK");
				return true;
			}
		}

		if (isFullPurchase)
		{
			//			Log.d(LOG_TAG, "generatedChecksum: " + generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isFullPurchase), RANDOM_STRING));
			if (!savedChecksum.equals(generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isFullPurchase), RANDOM_STRING)))
			{
				isFullPurchase = false;
				savedChecksum = "";
				webHandler.clearSavedParams();
				writeRMS();
				Log.i(LOG_TAG, "checkBusinessRules, Full purchase Cehcksum error");
				return false;
			}

			if (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime
			// if we have full purchase we do not check playtime
			// || businessRulesPlayTime >=
			// GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME
			)
			{
				businessRulesIsNewStart = true;
			}

			/* Additional check if sim cards have been swapped. If true, isFullPurchase will be set to false
			 * and token will be reset. */
			if (!checkImsi(IMSI, readImsiFromFile()))
			{
				//				Log.i(LOG_TAG, "IMSI check failed!");
				isFullPurchase = false;
				//				token = "";
				writeRMS();
				Log.i(LOG_TAG, "checkBusinessRules, Full purchase IMSI error");
				return false;
			}
			else
			{
				Log.i(LOG_TAG, "checkBusinessRules, Full purchase check OK");
				return true;
			}
		}

		/* Checks if try before you by is finished */
		if (businessRulesTBYBfinished)
		{
			Log.i(LOG_TAG, "businessRulesTBYBfinished==true");
			if (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime)
			{
				businessRulesIsNewStart = true;
			}
			Log.d(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);

			/*if (GeneratedConstants.DEMO_BUILD)
			{
				if (demoCheckedTimes++ > 0)
				{
					businessRulesIsNewStart = true;
					return false;
				}
			}*/

			//check if playduration is used up
			if (businessRulesNextSubscriptionCheckTime > 0 && currentTime >= businessRulesNextSubscriptionCheckTime && !isPreUnlocked)
			{
				Log.i(LOG_TAG, "checkBusinessRules, TBYB finished playduration exceeded");
				return false;
			}
			else if (businessRulesNextSubscriptionCheckTime == -1)
			{
				Log.d(LOG_TAG, "businessRulesNextSubscriptionCheckTime == -1");
				if (businessRulesNumOfStartups > businessRulesNumOfUsedStartups)
				{
					return false;
				}
			}
			//check if startups are used
			if (businessRulesNumOfUsedStartups + (businessRulesIsNewStart ? 1 : 0) <= businessRulesNumOfStartups)
			{
				Log.i(LOG_TAG, "checkBusinessRules, TBYB trial startups available");
				return true;
			}
			else
			{
				Log.i(LOG_TAG, "checkBusinessRules, TBYB all startups used!");
				return false;
			}
		}
		else
		{
			Log.i(LOG_TAG, "businessRulesTBYBfinished==false");
			boolean isTBYBfinished = false;

			Log.d(LOG_TAG, "currentTime=" + currentTime);
			Log.d(LOG_TAG, "businessRulesLastSaveTime=" + businessRulesLastSaveTime);
			Log.d(LOG_TAG, "businessRulesNewStartupTime=" + businessRulesNewStartupTime);

			Log.i(LOG_TAG + "_tbyb", MessageFormat.format("{0} > {1} = {2}", currentTime - businessRulesLastSaveTime, businessRulesNewStartupTime, (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime)));
			if (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime)
			{
				businessRulesIsNewStart = true;
			}

			Log.d(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);

			Log.i(LOG_TAG + "_tbyb", "businessRulesNumOfStartups=" + businessRulesNumOfStartups);
			if (businessRulesNumOfStartups < 0) // play time only - unlimited startups 
			{
				Log.i(LOG_TAG + "_tbyb", "businessRulesPlayTime=" + businessRulesPlayTime);
				Log.i(LOG_TAG + "_tbyb", "businessRulesPlayTime >= GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME==" + (businessRulesPlayTime >= businessRulesTbybPlaytime));
				if (businessRulesPlayTime >= businessRulesTbybPlaytime)
				{
					isTBYBfinished = true;
					Log.i(LOG_TAG, "TBYB finished");
				}
			}
			else if (businessRulesTbybPlaytime < 0) // startups only - unlimited time 
			{
				Log.d(LOG_TAG, "startups only - unlimited time");
				Log.d(LOG_TAG, "GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME=" + businessRulesTbybPlaytime);
				if (businessRulesNumOfUsedStartups > businessRulesNumOfStartups)
				{
					Log.i(LOG_TAG, "TBYB finished, businessRulesNumOfUsedStartups:" + businessRulesNumOfUsedStartups + " businessRulesIsNewStart:" + businessRulesIsNewStart);
					isTBYBfinished = true;
				}
				else
				{
					Log.d(LOG_TAG, "TBYB not finished, returning true");
					//					return true;
				}
			}
			else
			// limited startups with limited time each
			{
				Log.d(LOG_TAG, "limited startups with limited time each");
				if (businessRulesPlayTime >= businessRulesTbybPlaytime)
					businessRulesIsNewStart = true;

				Log.d(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);
				Log.d(LOG_TAG, "businessRulesPlayTime=" + businessRulesPlayTime);
				Log.d(LOG_TAG, "GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME=" + businessRulesTbybPlaytime);

				if (businessRulesNumOfUsedStartups + (businessRulesIsNewStart ? 1 : 0) > businessRulesNumOfStartups)
					isTBYBfinished = true;

				Log.d(LOG_TAG, "businessRulesNumOfUsedStartups=" + businessRulesNumOfUsedStartups);
				Log.d(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);
				Log.d(LOG_TAG, "businessRulesNumOfStartups=" + businessRulesNumOfStartups);

				Log.d(LOG_TAG, "isTBYBfinished=" + isTBYBfinished);
			}

			if (isTBYBfinished)
			{
				Log.d(LOG_TAG, "isTBYBfinished=true");
				businessRulesTBYBfinished = true;
				saveBusinessRules(true);
				// need init if TBYB was not added
				init(false);
				Log.i(LOG_TAG, "checkBusinessRules, isTBYBfinished and check failed");
				return false;
			}
			else
			{
				Log.d(LOG_TAG, "isTBYBfinished=false");

				Log.d(LOG_TAG, "GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE=" + GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE);
				Log.d(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);

				if (GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE && businessRulesIsNewStart && !isTryBeforeYouBuy)
				{
					Log.i(LOG_TAG, "checkBusinessRules, isTBYBfinished==false and businessRulesIsNewStart==true and GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE==true");
					return false;
				}
				Log.i(LOG_TAG, "checkBusinessRules, isTBYBfinished==false and check OK (??? what check)");
				return true;
			}

			//			if (currentTime - businessRulesLastSaveTime > businessRulesNewStartupTime || businessRulesPlayTime >= GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME)
			//			{
			//				businessRulesIsNewStart = true;
			//			}
			//
			//			if (businessRulesNumOfUsedStartups + (businessRulesIsNewStart ? 1 : 0) <= businessRulesNumOfStartups)
			//			{
			//				if (GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE && businessRulesIsNewStart)
			//					return false;
			//
			//				return true;
			//			}
			//			else
			//			{
			//				businessRulesTBYBfinished = true;
			//				saveBusinessRules(true);
			//				// need init if TBYB was not added
			//				init(false);
			//				return false;
			//			}
		}
	}

	/**
	 * States if there is a message to display or not.
	 * @return <code>true</code> when there is a message to display
	 */
	// return null if there is no message to show
	public boolean businessRulesHasMessage()
	{
		Log.i(LOG_TAG, "businessRulesHasMessage()");
		if (isFullPurchase || businessRulesTBYBfinished)
		{
			//			Log.i(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);
			//			Log.i(LOG_TAG, "GeneratedConstants.BUSINESS_RULES_MENAGE_SUPSRITPION_DIALOG_ENABLED=" + GeneratedConstants.BUSINESS_RULES_MENAGE_SUPSRITPION_DIALOG_ENABLED);
			//			Log.i(LOG_TAG, "businessRulesMenageSubsriptionDialogShow=" + businessRulesMenageSubsriptionDialogShow);
			if (businessRulesIsNewStart && GeneratedConstants.BUSINESS_RULES_MENAGE_SUPSRITPION_DIALOG_ENABLED && businessRulesMenageSubsriptionDialogShow)
				return true;
			else
				return false;
		}

		//if (GeneratedConstants.BUSINESS_RULES_TBYB_SHOW_START_MESSAGE && !businessRulesTBYBfinished && businessRulesIsNewStart && businessRulesNumOfUsedStartups + 1 <= businessRulesNumOfStartups)
		if (GeneratedConstants.BUSINESS_RULES_TBYB_SHOW_START_MESSAGE && !businessRulesTBYBfinished && businessRulesIsNewStart && (businessRulesNumOfStartups < 0 || businessRulesNumOfUsedStartups + 1 <= businessRulesNumOfStartups) && !isMessageShown)
			return true;

		return false;
	}

	public Dialog businessRulesShowMessage(Activity activity, DialogInterface.OnClickListener listener)
	{
		Log.i(LOG_TAG, "businessRulesShowMessage()");
		if (isFullPurchase || businessRulesTBYBfinished)
		{
			if (businessRulesIsNewStart && GeneratedConstants.BUSINESS_RULES_MENAGE_SUPSRITPION_DIALOG_ENABLED && businessRulesMenageSubsriptionDialogShow)
			{
				init(false);
				// String message = " go to http://vodafone.de";
				CharSequence messageTxt = Html.fromHtml((String) resource.getResource(CommonConstants.RESOURCE_TEXT_MENAGE_SUBSRIPTIONS_DIALOG_MESSAGE));
				// SpannableString spanText = new SpannableString(message);

				// Linkify.addLinks(spanText, Linkify.ALL);
				// messageTxt = spanText;
				CheckBox checkBox = new CheckBox(activity);
				checkBox.setText((String) resource.getResource(CommonConstants.RESOURCE_TEXT_MENAGE_SUBSRIPTIONS_DIALOG_DO_NOT_SHOW));

				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1)
					{
						businessRulesMenageSubsriptionDialogShow = !arg1;
						saveBusinessRules(true);
					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle((String) resource.getResource(CommonConstants.RESOURCE_TEXT_MENAGE_SUBSRIPTIONS_DIALOG_TITLE)).setMessage(messageTxt).setView(checkBox).setCancelable(false)
						.setPositiveButton((String) resource.getResource(CommonConstants.RESOURCE_SOFT_OK), listener);

				Dialog dialog = builder.create();

				dialog.show();

				((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
				((TextView) dialog.findViewById(android.R.id.message)).setAutoLinkMask(Linkify.ALL);
				((TextView) dialog.findViewById(android.R.id.message)).setLinksClickable(true);

				return dialog;
			}
			return null;
		}
		if (GeneratedConstants.BUSINESS_RULES_TBYB_SHOW_START_MESSAGE && !businessRulesTBYBfinished && businessRulesIsNewStart && (businessRulesNumOfStartups < 0 || businessRulesNumOfUsedStartups + 1 <= businessRulesNumOfStartups))
		{
			init(false);
			// businessRulesStartMessageShown = true;
			//			String msg = resource.getResource(CommonConstants.CLIENT_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 0) 
			//					+ " " + (businessRulesNumOfStartups - businessRulesNumOfUsedStartups) + " "
			//					+ resource.getResource(CommonConstants.CLIENT_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 1 + ((businessRulesNumOfStartups - businessRulesNumOfUsedStartups) > 1 ? "b" : "a")) + " " + formatPlaytime(GeneratedConstants.BUSINESS_RULES_TBYB_PLAYTIME)
			//					+ " " + resource.getResource(CommonConstants.CLIENT_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 2);

			String msg = getTBYBMessage();

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(msg).setCancelable(false).setPositiveButton((String) resource.getResource(CommonConstants.RESOURCE_SOFT_OK), listener);

			Dialog msgDialog = builder.create();
			msgDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			msgDialog.show();

			isMessageShown = true;

			return msgDialog;
		}

		return null;
	}

	private String getTBYBMessage()
	{
		String msg = (String) resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 0);
		if (businessRulesNumOfStartups >= 0)
		{
			msg += " " + (businessRulesNumOfStartups - businessRulesNumOfUsedStartups) + " "
					+ resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 1 + ((businessRulesNumOfStartups - businessRulesNumOfUsedStartups) > 1 ? "b" : "a"));
		}

		if (businessRulesTbybPlaytime >= 0)
		{
			msg += " " + resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 2) + " " + formatPlaytime(businessRulesNumOfStartups >= 0 ? businessRulesTbybPlaytime : businessRulesTbybPlaytime - businessRulesPlayTime);
		}

		msg += resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 3);

		return msg;
	}

	/**
	 * Creates a formatted string for the Try Before You Buy message, containing the rest amount of time for trying.
	 * @param miliseconds the TBYB time for evaluation
	 * @return a formatted string
	 */
	private String formatPlaytime(long miliseconds)
	{
		//		Log.i(LOG_TAG, "formatPlaytime");
		String ret = "";

		long days = miliseconds / 24 / 60 / 60 / 1000;
		miliseconds -= days * 24 * 60 * 60 * 1000;
		long hours = miliseconds / 60 / 60 / 1000;
		miliseconds -= hours * 60 * 60 * 1000;
		long minutes = miliseconds / 60 / 1000;
		miliseconds -= minutes * 60 * 1000;
		long seconds = miliseconds / 1000;

		if (days != 0)
		{
			ret += days + " " + resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 3 + ((days > 1) ? "b" : "a"));
			if (hours != 0 || minutes != 0)
				ret += " ";
		}

		if (hours != 0)
		{
			ret += hours + " " + resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 4 + ((hours > 1) ? "b" : "a"));
			if (minutes != 0)
				ret += " ";
		}

		if (minutes != 0)
		{
			ret += minutes + " " + resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 5 + ((minutes > 1) ? "b" : "a"));
			if (seconds != 0)
			{
				ret += " ";
			}
		}

		if (seconds != 0)
		{
			ret += seconds + " " + resource.getResource(CommonConstants.RESOURCE_TEXT_TRY_BEFORE_YOU_BUY_MESSAGE + 6 + ((seconds > 1) ? "b" : "a"));
		}

		//		Log.i(LOG_TAG, ret);
		return ret;
	}

	/**
	 * Updates the number of startups and saves the current time for playtime limitation.
	 * @return <code>true</code> if game is a full purchase
	 */
	public boolean businessRulesNotifyStart()
	{
		//		Log.i(LOG_TAG, "businessRulesNotifyStart()");

		businessRulesLastStartTime = getCurrentTime();

		//		Log.i(LOG_TAG, "businessRulesIsNewStart=" + businessRulesIsNewStart);
		if (businessRulesIsNewStart)
		{
			businessRulesIsNewStart = false;
			businessRulesNumOfUsedStartups++;
			//			Log.i(LOG_TAG, "---> INCREASING STARTUPS NOW");

			if (businessRulesNumOfStartups >= 0)
				businessRulesPlayTime = 0;
		}

		saveBusinessRules(true);
		return isFullPurchase || isRental;
	}

	/**
	 * Does nothing at the moment.
	 * @return <code>true</code> if game is a full purchase
	 */
	public boolean businessRulesNotifyStop()
	{
		//		Log.i(LOG_TAG, "businessRulesNotifyStop()=" + (isFullPurchase || isRental));
		return isFullPurchase || isRental;
	}

	public void deltaUseTime()
	{
		//		Log.i(LOG_TAG, "deltaUseTime()");
		long currentTime = getCurrentTime();
		long deltaTime = 0;
		if (currentTime < businessRulesLastSaveTime)
			deltaTime = BUSINESS_RULES_SAVE_TIME;
		else
			deltaTime = getCurrentTime() - businessRulesLastSaveTime;

		businessRulesPlayTime += deltaTime;
		saveBusinessRules(true);
	}

	/** @return the current time in milliseconds */
	private long getCurrentTime()
	{
		return System.currentTimeMillis();
	}

	/** @return <code>true</code> if the product is only rented */
	public boolean isRental()
	{
		return isRental;
		//		return (purchaseType == PurchaseType.PURCHASE_TYPE_RENTAL);
	}

	/** @return <code>true</code> if next checktime is < current time */
	public boolean isNextSubscriptionCheckTime()
	{
		//		Log.i(LOG_TAG, "isNextSubscriptionCheckTime()");
		//		Log.i(LOG_TAG, "isRental: " + isRental);
		boolean result = false;
		if (isRental)
		{
			result = (businessRulesNextSubscriptionCheckTime < getCurrentTime());
		}
		return result;
	}

	/** @return <code>true</code> if number of startups is depleted and wrapper has to check if product is still rented */
	public boolean hasNumberOfStartupsExceeded()
	{
		//		Log.i(LOG_TAG, "hasNumberOfStartupsExceeded()");
		//		Log.i(LOG_TAG, "businessRulesIsNewStart:" + businessRulesIsNewStart);
		boolean result = false;
		if (isRental)
		{
			int thisIncrease = 0;
			if (businessRulesIsNewStart)
			{
				thisIncrease++;
			}
			result = ((businessRulesNumOfUsedStartups + thisIncrease) >= businessRulesNumOfStartups);

			//			if (businessRulesNumOfStartups == -1 && businessRulesNumOfUsedStartups == -1)
			//			{
			//				result = false;
			//			}
		}
		//		Log.i(LOG_TAG, "result=" + result);
		return result;
	}

	public boolean isTBYBfinished()
	{
		return businessRulesTBYBfinished;
	}

	public void setCS(String cs)
	{
		this.CS = cs;
	}

	/**
	 * Converts a byte array to a hexadecimal string representation.
	 * @param bytes the byte array to display as a hex string
	 * @return a hex string
	 */
	public static String bytesToHex(byte[] bytes)
	{
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < bytes.length; j++)
		{
			buf.append(hexDigit[(bytes[j] >> 4) & 0x0f]);
			buf.append(hexDigit[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	/**
	 * Generates a SHA1 checksum from the given params.
	 * @param params an undefined amount of string parameters
	 * @return a hex string representing the SHA1 checksum of the given params
	 */
	private String generateChecksumFile(InputStream is)
	{
		String result = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte data[] = new byte[is.available()];
			md.update(data);
			byte[] output = md.digest();
			result = bytesToHex(output);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	private String generateChecksum(String... params)
	{
		String result = "";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < params.length; i++)
		{
			builder.append(params[i]);
		}

		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(builder.toString().getBytes());
			byte[] output = md.digest();
			result = bytesToHex(output);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	// NEW STUFF

	final class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result)
		{
			result.confirm();
			return true;
		}
	}

	final class WebViewHandler extends WebViewClient implements NetworkRequestTask.NetworkRequestCallback, NetworkRequest.NetworkRequestHandler
	{
		private Hashtable<String, String> params = new Hashtable<String, String>();
		private Vector<String> paramsToSave = new Vector<String>();

		abstract class StringParser
		{
			abstract public void parseString(String response);
		}

		// made only for security reason 
		private js JSHandler = new js(this);

		protected String getTBYBMsg()
		{
			return getTBYBMessage();
		}

		protected void clearSavedParams() // after sim chaanged
		{
			Vector<String> cpy = (Vector<String>) paramsToSave.clone();
			for (String param : cpy)
				setParam(param, null);
		}

		protected void clearNotSavedParams()
		{
			Vector<String> cpy = new Vector<String>(params.keySet());
			for (String param : cpy)
				if (!paramsToSave.contains(param))
					setParam(param, null);
		}

		// final methods implementation 
		protected void setParam(String paramName, String value)
		{
			if (value == null)
			{
				params.remove(paramName);
				paramsToSave.remove(paramName);
			}
			else
			{
				params.put(paramName, value);
				paramsToSave.add(paramName);
			}
		}

		protected void putParam(String paramName, String value)
		{
			if (value == null)
			{
				params.remove(paramName);
				paramsToSave.remove(paramName);
			}
			else
				params.put(paramName, value);
		}

		protected String getParam(String paramName)
		{
			return params.get(paramName);
		}

		protected void loadParams(DataInputStream dis) throws IOException
		{
			int num = dis.readInt();
			for (int i = 0; i < num; i++)
			{
				String paramName = dis.readUTF();
				paramsToSave.add(paramName);
				params.put(paramName, dis.readUTF());
			}

		}

		protected void saveParams(DataOutputStream dos) throws IOException
		{
			int num = paramsToSave.size();
			dos.writeInt(num);
			for (int i = 0; i < num; i++)
			{
				String key = paramsToSave.elementAt(i);
				dos.writeUTF(key);
				dos.writeUTF(params.get(key));
			}
		}

		protected void open(String URL)
		{
			open(URL, null, false);
		}

		protected void openPost(String URL)
		{
			open(URL, null, true);
		}

		protected void open(String URL, Runnable errorHandler, boolean isPost)
		{
			//Log.i(LOG_TAG, "open: " + URL);
			Log.i(LOG_TAG, "Request: " + URL + "started! Number of open pages before: " + openedPages);
			openedPages++;
			Log.i(LOG_TAG, "Request: " + URL + "started! Number of open pages now: " + openedPages);
			//tbd			if (!openFinished)
			//				messageToShow = true;
			//
			//			openFinished = false;

			//			Log.i(LOG_TAG, "forcingMessageShown: " + forcingMessageShown);
			if (!forcingMessageShown)
			{
				//				Log.i(LOG_TAG, "Trying to show new progress dialog.");
				//controller.hideDialog();
				controller.startProgressDialog((String) resource.getResource(CommonConstants.RESOURCE_TEXT_LOADING));
			}

			this.errorHandler = errorHandler;

			if (wiFiHelper.isWifiEnabled())
				putParam(PARAM_WIFI, "1");
			else
				putParam(PARAM_WIFI, "0");

			//new NetworkRequestTask(this).execute(makeURL(URL));
			if (GeneratedConstants.DEMO_BUILD && GeneratedConstants.DEMO_OFFLINE)
			{
				if (URL.equals(START_ACTION_ID))
				{
					if (params.containsKey(PARAM_TRY_BEFORE_YOU_BUY) && ((String) params.get(PARAM_TRY_BEFORE_YOU_BUY)).equals("true"))
						URL += 1;
					else
						URL += 2;
				}
				webView.loadUrl(makeURL(URL));
			}
			else
				networkRequest(makeURL(URL), isPost);
			//			String data = load(makeURL(URL));
			//			webView.loadDataWithBaseURL(makeURL(URL), data, null, "utf-8", null);
		}

		protected void parseString(String str)
		{
			if (this.stringParser != null)
				this.stringParser.parseString(str);
			else
				triggerError(InAppBillingController.ERROR_LIBRARY_ERROR, "Not expected backend response.");
		}

		private final WiFiHelper wiFiHelper = new WiFiHelper(context);

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			//			Log.i(LOG_TAG, "shouldOverrideUrlLoading()");
			//			String data = load(makeURL(url));
			//			view.loadDataWithBaseURL(makeURL(url), data, null, "utf-8", null);

			//new NetworkRequestTask(this).execute(url);
			networkRequest(makeURL(url), false);

			return true;
		}

		private String makeURL(String URL)
		{
			Log.i(LOG_TAG, "makeURL(): " + URL);
			if (URL.startsWith("http://"))
			{
				Log.i("final URL", "makeURL(): result=" + URL);
				return URL;
			}
			String finalURL = resource.getString(CommonConstants.JAD_PARAM_AYCE_URL) + "/" + URL;

			String suffix = "";

			if (URL.contains("?"))
				finalURL += "&";
			else
				finalURL += "?";

			String[] keys = params.keySet().toArray(new String[] {});//= (String[]) ;
			for (int i = 0; i < keys.length; i++)
			{
				if (i > 0)
					suffix += "&";
				suffix += keys[i] + "=" + params.get(keys[i]);
			}

			finalURL += suffix;

			Log.i("final URL", "makeURL(): result=" + finalURL);
			return finalURL;

		}

		protected void goPendingState(String timeInMs, final String url)
		{
			//			Log.i(LOG_TAG, "goPendingState()");

			if (openedPages > 0)
				messageToShow = true;

			forcingMessageShown = true;
			controller.startProgressDialog(resource.getString(CommonConstants.RESOURCE_TEXT_LOADING));

			pendingTask = new Runnable() {
				public void run()
				{
					pendingTask = null; // remove pending task
					open(url);
				}
			};

			long time = 0;
			try
			{
				time = Long.valueOf(timeInMs);
			}
			catch (NumberFormatException ex)
			{

			}

			if (!isPaused)
			{
				pendingHandler.postDelayed(pendingTask, time);
			}

		}

		protected void sendSMS(final String successURL, final String failURL, final String phoneNumber, final String message)
		{
			//			Log.i(LOG_TAG, "sendSMS()");
			if (openedPages > 0)
				messageToShow = true;

			forcingMessageShown = true;
			controller.startProgressDialog(resource.getString(CommonConstants.RESOURCE_TEXT_LOADING));

			if (GeneratedConstants.DEMO_BUILD)
			{
				open(successURL);
				return;
			}

			String SENT = "SMS_SENT";
			//String DELIVERED = "SMS_DELIVERED";

			//final Context context = this.context;

			PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

			// remove dilivery notification 
			//PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

			//			open(successURL);
			//			if (true)
			//				return;
			//---when the SMS has been sent---
			context.registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent arg1)
				{
					switch (getResultCode())
					{
						case Activity.RESULT_OK:
							open(successURL);
							break;
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						case SmsManager.RESULT_ERROR_NO_SERVICE:
						case SmsManager.RESULT_ERROR_NULL_PDU:
						case SmsManager.RESULT_ERROR_RADIO_OFF:
						default:
							open(failURL);
							break;
					}

					context.unregisterReceiver(this);
				}
			}, new IntentFilter(SENT));

			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
		}

		protected void unlock(final String numOfStartups, final String nextTimeCheck, final String type)
		{
			Log.d(LOG_TAG, "unlock()");
			controller.runOnUiThread(new Runnable() {

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					int startups = -1;
					int nextTime = -1;
					//int purchType =-1 ;
					int purchaseType = -1;

					try
					{
						Log.i(LOG_TAG, "startups:" + numOfStartups + " nextTimeCheck:" + nextTimeCheck);
						startups = Integer.parseInt(numOfStartups);
						if (nextTimeCheck.equals("-1"))
						{
							nextTime = Integer.parseInt(nextTimeCheck);
						}
						else
						{
							nextTime = Integer.parseInt(nextTimeCheck) * 60 * 1000;
						}
						purchaseType = Integer.parseInt(type);

						//set values in businessrules
						businessRulesNumOfStartups = startups;
						businessRulesNewStartupTime = nextTime;
						businessRulesTbybPlaytime = nextTime;
						businessRulesNumOfFreeStartups = startups;

						saveBusinessRules(true);
						Log.i(LOG_TAG, "startups:" + startups + " nextTimeCheck:" + nextTime);
					}
					catch (NumberFormatException e)
					{
						Log.e(LOG_TAG, "", e);
					}

					if (purchaseType == PurchaseType.PURCHASE_TYPE_TBYB)
					{
						Log.d(LOG_TAG, "purchaseType == PurchaseType.PURCHASE_TYPE_TBYB");
						isTryBeforeYouBuy = true;
						isMessageShown = true;
						saveBusinessRules(true);
						endLogic(RETURN_SUBSCRIPTION_PURCHASED);
					}
					else
					{
						Log.d(LOG_TAG, "purchaseType != PurchaseType.PURCHASE_TYPE_TBYB");
						storeImsi(IMSI);

						if (!businessRulesTBYBfinished)
						{
							Log.d(LOG_TAG, "businessRulesTBYBfinished == false");
							businessRulesTBYBfinished = true;
							saveBusinessRules(true);
						}
						if (/*!GeneratedConstants.DEMO_BUILD && */purchaseType == PurchaseType.PURCHASE_TYPE_FULL)
						{
							Log.d(LOG_TAG, "purchaseType == PurchaseType.PURCHASE_TYPE_FULL");
							isFullPurchase = true;
							isRental = false;
							savedChecksum = generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isFullPurchase), RANDOM_STRING);
							//writeRMS();
						}

						if (/*!GeneratedConstants.DEMO_BUILD && */purchaseType == PurchaseType.PURCHASE_TYPE_RENTAL)
						{
							Log.d(LOG_TAG, "purchaseType == PurchaseType.PURCHASE_TYPE_RENTAL");
							isRental = true;
							isFullPurchase = false;
							savedChecksum = generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isRental), RANDOM_STRING);
							//writeRMS();
						}

						//						if (GeneratedConstants.DEMO_OFFLINE)
						//						{
						//							isFullPurchase = true;
						//							isRental = false;
						//							savedChecksum = generateChecksum(IMSI, GeneratedConstants.LIB_ARTICLE_ID, Boolean.toString(isFullPurchase), RANDOM_STRING);
						//
						//						}

						//						isSMSsent = false;
						validIMSI = IMSI;
						writeRMS();
						businessRulesHandleSubscriptionUpdate(nextTime, startups);

						endLogic(RETURN_SUBSCRIPTION_VALID);

					}

				}
			});

		}

		protected void exit()
		{
			//			Log.i(LOG_TAG, "exit()");
			controller.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					endLogic(RETURN_CANCELLED);
				}
			});
		}

		protected void disableWifi(final String successURL, final String failURL, String message)
		{
			//			Log.i(LOG_TAG, "disableWifi()");

			if (wiFiHelper.isWifiEnabled() || wiFiHelper.isMobileConnectionConnecting())
			{
				if (openedPages > 0)
					messageToShow = true;

				forcingMessageShown = true;
				controller.startProgressDialog(message);
				wiFiHelper.disableWifi(new WiFiStateChangeHandler() {

					@Override
					public void onWifiEnabled()
					{
						// nothing todo here
					}

					@Override
					public void onWifiDisabled()
					{
						wasWifiDisabled = true; // wifi disabled 
						//						Log.i(LOG_TAG, "WiFi successfully disabled!");
						open(successURL, new Runnable() {
							int numOfTries = 3;

							@Override
							public void run()
							{
								if (--numOfTries > 0)
								{
									final Runnable self = this;
									new Thread() {
										public void run()
										{
											try
											{
												Thread.sleep(10000);
											}
											catch (InterruptedException e)
											{
											}
											open(successURL, self, false);
										};
									}.start();
								}
								else
									wiFiHelper.enableWifi(new WiFiStateChangeHandler() {
										@Override
										public void onWifiEnabled()
										{
											wasWifiDisabled = false; // wifi enabled again - if mobile connection failed 
											open(failURL, new Runnable() {
												int numOfTries = 2;

												@Override
												public void run()
												{
													if (--numOfTries > 0)
													{
														final Runnable self = this;
														new Thread() {
															public void run()
															{
																try
																{
																	Thread.sleep(10000);
																}
																catch (InterruptedException e)
																{
																}
																open(failURL, self, false);
															};
														}.start();
													}
													else
														open(failURL); // after 2 tries normal behavior
												}
											}, false);
										}

										@Override
										public void onWifiDisabled()
										{
											//nothing to do
										}
									});
							}
						}, false);
					}
				});
			}
			else
				open(successURL, new Runnable() {
					@Override
					public void run()
					{
						open(failURL);
					}
				}, false);
		}

		protected void disableWifiPost(final String successURL, final String failURL, String message)
		{
			//			Log.i(LOG_TAG, "disableWifi()");

			if (wiFiHelper.isWifiEnabled() || wiFiHelper.isMobileConnectionConnecting())
			{
				if (openedPages > 0)
					messageToShow = true;

				forcingMessageShown = true;
				controller.startProgressDialog(message);
				wiFiHelper.disableWifi(new WiFiStateChangeHandler() {

					@Override
					public void onWifiEnabled()
					{
						// nothing todo here
					}

					@Override
					public void onWifiDisabled()
					{
						wasWifiDisabled = true; // wifi disabled 
						//						Log.i(LOG_TAG, "WiFi successfully disabled!");
						open(successURL, new Runnable() {
							int numOfTries = 3;

							@Override
							public void run()
							{
								if (--numOfTries > 0)
								{
									final Runnable self = this;
									new Thread() {
										public void run()
										{
											try
											{
												Thread.sleep(10000);
											}
											catch (InterruptedException e)
											{
											}
											open(successURL, self, true);
										};
									}.start();
								}
								else
									wiFiHelper.enableWifi(new WiFiStateChangeHandler() {
										@Override
										public void onWifiEnabled()
										{
											wasWifiDisabled = false; // wifi enabled again - if mobile connection failed 
											open(failURL, new Runnable() {
												int numOfTries = 2;

												@Override
												public void run()
												{
													if (--numOfTries > 0)
													{
														final Runnable self = this;
														new Thread() {
															public void run()
															{
																try
																{
																	Thread.sleep(10000);
																}
																catch (InterruptedException e)
																{
																}
																open(failURL, self, true);
															};
														}.start();
													}
													else
														openPost(failURL); // after 2 tries normal behavior
												}
											}, true);
										}

										@Override
										public void onWifiDisabled()
										{
											//nothing to do
										}
									});
							}
						}, true);
					}
				});
			}
			else
				open(successURL, new Runnable() {
					@Override
					public void run()
					{
						open(failURL);
					}
				}, false);
		}
		
		protected void enableWifi(final String URL)
		{
			//			Log.i(LOG_TAG, "enableWifi(): " + URL);

			if (wasWifiDisabled)
			{

				if (wiFiHelper.isWifiEnabled())
				{
					wasWifiDisabled = false; // user enabled wifi by himself
					open(URL);
				}
				else
				{
					if (openedPages > 0)
						messageToShow = true;

					forcingMessageShown = true;
					controller.startProgressDialog(resource.getString(CommonConstants.RESOURCE_TEXT_LOADING));

					wiFiHelper.enableWifi(new WiFiStateChangeHandler() {
						@Override
						public void onWifiEnabled()
						{
							wasWifiDisabled = false; // wifi enabled again - if mobile connection failed 
							open(URL, new Runnable() {
								int numOfTries = 2;

								@Override
								public void run()
								{
									if (--numOfTries > 0)
									{
										final Runnable self = this;
										new Thread() {
											public void run()
											{
												try
												{
													Thread.sleep(10000);
												}
												catch (InterruptedException e)
												{
												}
												open(URL, self, false);
											};
										}.start();
									}
									else
									{
										// wifi cannot be turned on again, turn it off to make rest flow
										wiFiHelper.disableWifi(new WiFiStateChangeHandler() {
											@Override
											public void onWifiEnabled()
											{

											}

											@Override
											public void onWifiDisabled()
											{
												wasWifiDisabled = true; // after end logic will be enabled again 
												open(URL, new Runnable() {
													int numOfTries = 2;

													@Override
													public void run()
													{
														if (--numOfTries > 0)
														{
															final Runnable self = this;
															new Thread() {
																public void run()
																{
																	try
																	{
																		Thread.sleep(10000);
																	}
																	catch (InterruptedException e)
																	{
																	}
																	//																	controller.dismissDialogs();
																	//controller.hideDialog();
																	open(URL, self, false);
																};
															}.start();
														}
														else
														{
															open(URL); // open last time without wifi and show error msg 
															//controller.hideDialog();
															//															controller.dismissDialogs();
														}
													}
												}, false);

											}
										});
									}
								}
							}, false);

						}

						@Override
						public void onWifiDisabled()
						{
						}
					});
				}
			}
			else
				open(URL);
		}

		protected void enableWifiPost(final String URL)
		{
			//			Log.i(LOG_TAG, "enableWifi(): " + URL);

			if (wasWifiDisabled)
			{

				if (wiFiHelper.isWifiEnabled())
				{
					wasWifiDisabled = false; // user enabled wifi by himself
					openPost(URL);
				}
				else
				{
					if (openedPages > 0)
						messageToShow = true;

					forcingMessageShown = true;
					controller.startProgressDialog(resource.getString(CommonConstants.RESOURCE_TEXT_LOADING));

					wiFiHelper.enableWifi(new WiFiStateChangeHandler() {
						@Override
						public void onWifiEnabled()
						{
							wasWifiDisabled = false; // wifi enabled again - if mobile connection failed 
							open(URL, new Runnable() {
								int numOfTries = 2;

								@Override
								public void run()
								{
									if (--numOfTries > 0)
									{
										final Runnable self = this;
										new Thread() {
											public void run()
											{
												try
												{
													Thread.sleep(10000);
												}
												catch (InterruptedException e)
												{
												}
												open(URL, self, true);
											};
										}.start();
									}
									else
									{
										// wifi cannot be turned on again, turn it off to make rest flow
										wiFiHelper.disableWifi(new WiFiStateChangeHandler() {
											@Override
											public void onWifiEnabled()
											{

											}

											@Override
											public void onWifiDisabled()
											{
												wasWifiDisabled = true; // after end logic will be enabled again 
												open(URL, new Runnable() {
													int numOfTries = 2;

													@Override
													public void run()
													{
														if (--numOfTries > 0)
														{
															final Runnable self = this;
															new Thread() {
																public void run()
																{
																	try
																	{
																		Thread.sleep(10000);
																	}
																	catch (InterruptedException e)
																	{
																	}
																	//																	controller.dismissDialogs();
																	//controller.hideDialog();
																	open(URL, self, true);
																};
															}.start();
														}
														else
														{
															openPost(URL); // open last time without wifi and show error msg 
															//controller.hideDialog();
															//															controller.dismissDialogs();
														}
													}
												}, true);

											}
										});
									}
								}
							}, true);

						}

						@Override
						public void onWifiDisabled()
						{
						}
					});
				}
			}
			else
				openPost(URL);
		}
		
		private boolean forcingMessageShown = false;
		private boolean messageToShow = false;
		//private boolean openFinished = true;
		private int openedPages = 0;

		//private boolean error = false;
		private String failingUrl = null;
		private Runnable errorHandler = null;
		private StringParser stringParser = null;

		protected void setStringParser(StringParser stringParser)
		{
			this.stringParser = stringParser;
		}

		protected String getFailingUrl()
		{
			return failingUrl;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl)
		{
			super.onReceivedError(view, errorCode, description, failingUrl);
			//			Log.i(LOG_TAG, "onReceivedError()");

		}

		private void handleLoadingFinish()
		{
			if (--openedPages < 0)
				openedPages = 0;

			boolean messageToShow = this.messageToShow;
			this.messageToShow = false;
			final String failingUrl = this.failingUrl;
			this.failingUrl = null;

			if (failingUrl == null)
			{
				//				Log.i(LOG_TAG, "failingUrl == null + " + openedPages + " " + messageToShow);
				if (openedPages == 0 && !messageToShow)
				{
					ViewGroup parent = null;
					if(currentWebView != null)
					{
						parent = (ViewGroup) currentWebView.getParent();
						if (parent != null)
						{
							parent.removeView(currentWebView);
							parent = (ViewGroup) currentWebView.getParent();
						}
					}
					layout.removeAllViewsInLayout();
					layout.removeAllViews();
					currentWebView = webView;
					parent = (ViewGroup) currentWebView.getParent();
					if (parent != null)
					{
						//Log.i(LOG_TAG, "New Current webview parent: " + parent);
						parent.removeView(currentWebView);
						parent = (ViewGroup) currentWebView.getParent();
						//Log.i(LOG_TAG, "New Current webview parent after remove: " + parent);
					}
					try
					{
						layout.addView(currentWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
					}
					catch (IllegalStateException e)
					{
						Log.i(LOG_TAG, "Illegal state exception catched: " + e.getLocalizedMessage());
					}
					controller.hideDialog();
					forcingMessageShown = false;
					currentWebView.setVisibility(View.VISIBLE);
					currentWebView.requestFocus(View.FOCUS_UP);
				}
			}
			else
			{
				if (errorHandler == null)
				{
					controller.showMessage(resource.getString(CommonConstants.RESOURCE_TEXT_TITLE_ERROR), resource.getString(CommonConstants.RESOURCE_TEXT_ERROR_CONNECTION), resource.getString("continue"),
							resource.getString(CommonConstants.RESOURCE_SOFT_EXIT), false, new InAppBillingController.DialogCallback() {

								@Override
								public void dialogFinished(int status)
								{
									if (status == InAppBillingController.DialogCallback.STATUS_OK)
									{
										//TODO move text 
										forcingMessageShown = true;
										controller.startProgressDialog(resource.getString(CommonConstants.RESOURCE_TEXT_LOADING));
										// TODO change loading
										//										webView.loadUrl(failingUrl);
										//										String data = load(failingUrl);
										//										webView.loadDataWithBaseURL(failingUrl, data, null, "utf-8", null);
										//new NetworkRequestTask(WebViewHandler.this).execute(failingUrl);

										networkRequest(makeURL(failingUrl), false);
									}

									if (status == InAppBillingController.DialogCallback.STATUS_CANCEL)
									{
										triggerExitEvent();
									}
								}
							});
				}
				else
				{
					controller.runOnUiThread(errorHandler);
				}
			}
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			Log.i(LOG_TAG, "onPageFinished(): " + url);

			super.onPageFinished(view, url);

			String jsLog = "javascript:window.wrapper.log('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');";
			webView.loadUrl(jsLog);

			if (url.startsWith("data:"))
				return;

			handleLoadingFinish();
		}

		@Override
		public void onReceived(NetworkRequestTask.NetworkRequestResult result)
		{
			//			Log.i(LOG_TAG, result.toString());
			if (result.isSuccessful())
			{
				//				Log.i(LOG_TAG, "onReceived: success");
				String baseUrl = result.getUrl();
				if (baseUrl.contains("ipx.com"))
				{
					//					Log.i(LOG_TAG, "Changing baseUrl");
					baseUrl = GeneratedConstants.LIB_AUTHENTICATION_URL;
				}
				//				Log.i(LOG_TAG, "Setting content of webview");
				String data = result.getData();
				webView.loadDataWithBaseURL(baseUrl, data, null, "utf-8", null);
			}
			else
			{
				//				Log.i(LOG_TAG, "onReceived: failed");
				this.failingUrl = result.getUrl();
				handleLoadingFinish();
			}
		}

		private void networkRequest(String url, boolean isPost)
		{
			Log.i(LOG_TAG, "networkRequest: " + url);
			//TODO: TEST entry for TIM needs to be removed!
			//headers.put("TIM_MSISDN", "393386994583");
			if (isPost)
			{
				network.networkRequest(url, url, url.getBytes(), headers, "POST", this);
			}
			else
			{
				network.networkRequest(url, url, null, headers, "GET", this);
			}
		}

		@Override
		public void handleNetworkRequestFail(String name_id, int code, String errorMsg)
		{
			Log.i(LOG_TAG, "handleNetworkRequestFail");
			this.failingUrl = name_id;
			handleLoadingFinish();
		}

		@Override
		public void handleNetworkRequestSuccess(String name_id, byte[] data)
		{
			//	Log.i(LOG_TAG, "handleNetworkRequestSuccess");
			Log.d(LOG_TAG, "handleNetworkRequestSuccess, name_id=" + name_id);

			if (name_id.contains("ipx.com"))
			{
				//				Log.i(LOG_TAG, "Changing baseUrl");
				name_id = GeneratedConstants.LIB_AUTHENTICATION_URL;
			}
			Log.i(LOG_TAG, "Setting content of webview");
			//			Log.i(LOG_TAG, new String(data));

			//store cookies
			CookieSyncManager.createInstance(context);
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeSessionCookie();
			String domain = ((Resource)network).staticCookie.getDomain();
			String cookieString = ((Resource)network).cookieString;
			Log.i(LOG_TAG, "Cookie domain:" + domain + " Cookie-String:" + cookieString);
			cookieManager.setCookie(domain, cookieString);
			CookieSyncManager.getInstance().sync();
			
			// Check if request name id is from BPS then display in webview otherwise handleLoadingFinish
			if (name_id.contains("/bps/api"))
			{
				String dataString = new String(data);
				webView.loadDataWithBaseURL(name_id, dataString, null, "utf-8", null);
			}
			else
			{
				handleLoadingFinish();
			}
		}

	}

	WebView webView = null;
	WebView currentWebView = null;
	WebViewHandler webHandler = null;
	Hashtable headers = new Hashtable();

	//helper method for clearCache() , recursive
	//returns number of deleted files
	//private static final String TAG = "clear";

	static int clearCacheFolder(final File dir, final int numDays)
	{

		int deletedFiles = 0;
		if (dir != null && dir.isDirectory())
		{
			try
			{
				for (File child : dir.listFiles())
				{

					//first delete subdirectories recursively
					if (child.isDirectory())
					{
						deletedFiles += clearCacheFolder(child, numDays);
					}

					//then delete the files and subdirectories in this dir
					//only empty directories can be deleted, so subdirs have been done first
					if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS)
					{
						if (child.delete())
						{
							deletedFiles++;
						}
					}
				}
			}
			catch (Exception e)
			{
				//				Log.e(TAG, String.format("Failed to clean the cache, error %s", e.getMessage()));
			}
		}
		return deletedFiles;
	}

	/*
	 * Delete the files older than numDays days from the application cache
	 * 0 means all files.
	 */
	public static void clearCache(final Context context, final int numDays)
	{
		//		Log.i(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
		int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
		//		Log.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
	}

	abstract class NetworkRequestFail implements NetworkRequest.NetworkRequestHandler
	{
		@Override
		public void handleNetworkRequestFail(String name_id, int code, String errorMsg)
		{
			Log.d(LOG_TAG, "handleNetworkRequestFail, name_id=" + name_id + ", code=" + code + ", msg=" + errorMsg);
			controller.showMessage(resource.getString(CommonConstants.RESOURCE_TEXT_TITLE_ERROR), resource.getString(CommonConstants.RESOURCE_TEXT_ERROR_CONNECTION), resource.getString("continue"),
					resource.getString(CommonConstants.RESOURCE_SOFT_EXIT), false, new InAppBillingController.DialogCallback() {

						@Override
						public void dialogFinished(int status)
						{
							if (status == InAppBillingController.DialogCallback.STATUS_OK)
							{
								controller.startProgressDialog((String) resource.getResource(CommonConstants.RESOURCE_TEXT_LOADING));
								handleAction(lastBundleAction);
							}

							if (status == InAppBillingController.DialogCallback.STATUS_CANCEL)
							{
								triggerExitEvent();
							}
						}
					});
		}
	}

	@Override
	public Bundle checkAction(Bundle bundle)
	{
		//		Log.i(LOG_TAG, "checkAction");
		// PURCHASE ACTION
		if (BILLING_REQUEST_METHOD_PURCHASE.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)) ||
		// GET OPERATOR ACTION		
				BILLING_REQUEST_METHOD_GETOPERATOR.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD))
				// CANCEL SUBSCRIPTION ACTION
				|| BILLING_REQUEST_METHOD_CANCEL_SUBSCRIPTION.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD))
				// GETTING LIST OF AVAILABLE ITEMS
				|| REQUEST_METHOD_LIST_ITEMS.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))

		{
			String articleID = bundle.getString(BILLING_REQUEST_PARAM_APPLICATION_ID);
			String itemID = bundle.getString(BILLING_REQUEST_PARAM_ITEM_ID); // is optional
			String userID = bundle.getString(BILLING_REQUEST_PARAM_USER_ID); // is optional
			String retailerID = bundle.getString(BILLING_REQUEST_PARAM_RETAILER_ID);
			//String partnerID = bundle.getString(BILLING_REQUEST_PARAM_PARTNER_ID);

			if (controller.isWrapper()
					|| (articleID != null /*&& itemID != null && userID != null */&& retailerID != null && !"".equals(articleID.trim()) /*&& !"".equals(itemID.trim()) *//*&& !"".equals(userID.trim())*/&& !"".equals(retailerID.trim())))
			{
				Bundle ret = new Bundle();
				ret.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
				return ret;
			}

			Bundle ret = new Bundle();
			ret.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_ERROR);
			ret.putInt(InAppBillingController.BILLING_ERROR_CODE, InAppBillingController.ERROR_INCORRECT_PARAMETER);
			ret.putString(InAppBillingController.BILLING_ERROR_MESSAGE, "You need to specify application, retailer and user ids");

			return ret;

		}
		// GET OPERATOR ACTION
		//		else if ()
		//		{
		//			String userID = bundle.getString(BILLING_REQUEST_PARAM_USER_ID);
		//			if (userID == null || "".equals(userID.trim()))
		//			{
		//				Bundle ret = new Bundle();
		//				ret.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_ERROR);
		//				ret.putInt(InAppBillingController.BILLING_ERROR_CODE, InAppBillingController.ERROR_INCORRECT_PARAMETER);
		//				ret.putString(InAppBillingController.BILLING_ERROR_MESSAGE, "You need to specify userID");
		//
		//				return ret;
		//
		//			}
		//
		//		}

		else
		{
			Bundle ret = new Bundle();
			ret.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_ERROR);
			ret.putInt(InAppBillingController.BILLING_ERROR_CODE, InAppBillingController.ERROR_LIBRARY_ERROR);
			ret.putString(InAppBillingController.BILLING_ERROR_MESSAGE, "Request method not specified or not supported");

			return ret;
		}

		//		Bundle ret = new Bundle();
		//		ret.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
		//		return ret;
	}

	@Override
	public void handleAction(Bundle bundle)
	{
		//		Log.i(LOG_TAG, "handleAction()");
		// TODO currently we have only one action 

		lastBundleAction = bundle;
		bundleToReturn = null;
		if (!initialized)
		{
			init(true);
		}

		return_state = RETURN_NOT_STARTED;

		// requestingPurchaseOptionsForSubscriptionValidationEnabled =
		// requestingPurchaseOptionsForSubscriptionValidation = false;
		isInPurchase = false;
		isInvokingIpTerms = false;
		isSubscriptionValid = false;

		network.networkReset();

		if (isPaused)
			return;

		return_state = RETURN_IN_PROGRESS;

		if (webView == null)
		{
			webView = new WebView(context);
			webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			//			clearCache(context, 0);
			webView.getSettings().setRenderPriority(RenderPriority.LOW);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebChromeClient(new MyWebChromeClient());
			webView.addJavascriptInterface(webHandler.JSHandler, "wrapper");
			webView.setWebViewClient(webHandler);

			webView.setInitialScale(1);
			// to check 
			webView.setBackgroundColor(Color.TRANSPARENT);

			webView.setHorizontalScrollBarEnabled(false);
			webView.setHorizontalFadingEdgeEnabled(false);
			webView.setHorizontalScrollbarOverlay(false);
			//			webView.setVerticalScrollBarEnabled(false);
			//			webView.setVerticalFadingEdgeEnabled(false);
			webView.setVerticalScrollbarOverlay(true);

			layout.addView(webView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));

		}

		//clear webview 
		String data = "<div></div>";
		webView.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
		webView.clearView();

		//clear all not saved params every new action
		webHandler.clearNotSavedParams();
		webHandler.setStringParser(null);

		if (true) // temp 
		{
			//	resource.putResource(CommonConstants.JAD_PARAM_AYCE_CONTENT_ID, "protox");
			//	resource.putResource(CommonConstants.JAD_PARAM_AYCE_RETAILER_ID, "IPX");
			//	resource.putResource(CommonConstants.JAD_PARAM_AYCE_PROTOCOL_VERSION, "3");
			//pi/purchaseinfo?libv=6&protv=3&wifi=1&retid=IPX&cs=06A46F17B119C9C12BB0565115450DCB2447ED21&artv=6&artid=arndt_archipelago_2
			//pi/purchaseinfo?wifi=1&retid=Samsung&svcid=1&artv=6&artid=arndt_archipelago_2&libv=6&protv=3&tok=cb576546-aa9f-4ba3-abc9-62d497a69879&cs=06A46F17B119C9C12BB0565115450DCB2447ED21
			//versionCode = 63423;
			//	resource.putResource(CommonConstants.JAD_PARAM_AYCE_VERSION, "1");
			//CS = "13";
			//CS = "502D01471540377BDF77E5B4808EA7DA59AB9AF8";
			//IMSI = "26201944170512";
			//MCC_MNC = "26201";
			//IMSI = "262021234";
			//MCC_MNC = "23415";
			//headers.put("db-ip-cn", "218.56.241.32");
		}

		String articleID = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_CONTENT_ID);
		if (!controller.isWrapper())
			articleID = bundle.getString(BILLING_REQUEST_PARAM_APPLICATION_ID).trim();
		String retailerID = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_RETAILER_ID);
		if (!controller.isWrapper())
			retailerID = bundle.getString(BILLING_REQUEST_PARAM_RETAILER_ID).trim();

		String operator = resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_OPERATOR);
		if (!controller.isWrapper())
			InAppBilling.CS(this, context, retailerID, articleID);

		//set up common params 
		if (BILLING_REQUEST_METHOD_PURCHASE.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)) || BILLING_REQUEST_METHOD_GETOPERATOR.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD))
				|| BILLING_REQUEST_METHOD_CANCEL_SUBSCRIPTION.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))
		{

			String userID = bundle.getString(BILLING_REQUEST_PARAM_USER_ID);
			String itemID = bundle.getString(BILLING_REQUEST_PARAM_ITEM_ID);

			if (userID != null && !"".equals(userID.trim()))
				webHandler.putParam(PARAM_USER_ID, userID);

			if (itemID != null && !"".equals(itemID.trim()))
			{
				webHandler.putParam(PARAM_ITEM_ID, itemID.trim());
			}

			webHandler.putParam(PARAM_ARTICLE_ID, articleID);
			webHandler.putParam(PARAM_RETAILER_ID, retailerID);
			webHandler.putParam(PARAM_MONDIA_OPERATOR, operator);
			webHandler.putParam(PARAM_PROTOCOL_VERSION, resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_PROTOCOL_VERSION));
			webHandler.putParam(PARAM_ARTICLE_VERSION, "" + versionCode);
			//webHandler.putParam(PARAM_SERVICE_ID, "22");
			webHandler.putParam(PARAM_WRAPPER_VERSION, resource.getAppProperty(CommonConstants.JAD_PARAM_AYCE_VERSION));
			if (CS != null && !CS.trim().equals(""))
				webHandler.putParam(PARAM_CHECKSUM, "" + CS);
			if (IMSI != null)
				webHandler.putParam(PARAM_IMSI, IMSI);
			if (MCC_MNC != null)
				webHandler.putParam(PARAM_MCCMNC, MCC_MNC);
			if (GeneratedConstants.BUSINESS_RULES_TBYB_IN_PURCHASE && !businessRulesTBYBfinished && controller.isWrapper())
				webHandler.putParam(PARAM_TRY_BEFORE_YOU_BUY, "true");
			else
				webHandler.putParam(PARAM_TRY_BEFORE_YOU_BUY, null); // must be reset 
			//dtpay specific add appKey and subscriptionId from filename
			Log.d(LOG_TAG, "adding AppKey and SubscriptionId to network request");
			addAppKeyAndSubscriptionIdParams(webHandler);
		}

		// PURCHASE ACTION
		if (BILLING_REQUEST_METHOD_PURCHASE.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))
		{
			//check text update on every start 
			String updateURL = resource.getString(CommonConstants.JAD_PARAM_AYCE_URL) + "/wrapperupdate?lang=" + resource.getCurrentLang() + "&txtsha=" + resource.getCurrentLangSHA() + "&" + PARAM_ARTICLE_ID + "=" + articleID + "&"
					+ PARAM_RETAILER_ID + "=" + retailerID + "&" + PARAM_IMSI + "=" + IMSI + "&" + PARAM_ARTICLE_VERSION + "=" + versionCode;

			if (GeneratedConstants.DEMO_BUILD) // do not send wrapper update request
				webHandler.open(START_ACTION_ID);
			else
				network.networkRequest("UPDATE_TEXT", updateURL, null, headers, "POST", new NetworkRequestFail() {
					@Override
					public void handleNetworkRequestSuccess(String name_id, byte[] data)
					{
						Log.d(LOG_TAG, "handleNetworkRequestSuccess, name_id=" + name_id);
						//update text if needed
						Hashtable<String, String> resp = parseResponse(data);
						if (resp.get(PARAM_STATUS).equals("0")) // text need to update
							resource.updateLanguage(resp.get("text").split("\\\\n\\\\r"));

						//open
						webHandler.open(START_ACTION_ID);
					}
				});
		}
		// GET OPERATOR ACTION
		else if (BILLING_REQUEST_METHOD_GETOPERATOR.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))
		{
			webHandler.setStringParser(webHandler.new StringParser() {
				@Override
				public void parseString(String str)
				{
					Hashtable<String, String> resp = parseResponse(str);

					if (resp.get(PARAM_STATUS).equals("0"))
					{
						Bundle response = new Bundle();
						response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
						response.putString(BILLING_RESPONSE_OPERATOR, resp.get(PARAM_OPERATOR));
						endLogic(response);
					}
					else
						triggerError(InAppBillingController.ERROR_LIBRARY_ERROR, "Server returns: status=" + resp.get(PARAM_STATUS) + " message=" + resp.get(PARAM_MESSAGE));
				}
			});
			webHandler.open(GET_OPERATOR_ACTION_ID);
		}
		// CANCEL SUBSCRIPTION ACTION
		else if (BILLING_REQUEST_METHOD_CANCEL_SUBSCRIPTION.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))
		{
			webHandler.setStringParser(webHandler.new StringParser() {
				@Override
				public void parseString(String str)
				{
					Hashtable<String, String> resp = parseResponse(str);

					if (resp.get(PARAM_STATUS).equals("0"))
					{
						//return ok if subscription cancelled with success
						Bundle response = new Bundle();
						response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
						endLogic(response);
					}
					else if (resp.get(PARAM_STATUS).equals("3"))
					{
						triggerError(BILLING_ERROR_CODE_SUBSCRIPTION_NOT_VALID, resp.get(PARAM_MESSAGE));
					}
					else
						triggerError(InAppBillingController.ERROR_LIBRARY_ERROR, "Server returns: status=" + resp.get(PARAM_STATUS) + " message=" + resp.get(PARAM_MESSAGE));
				}
			});
			webHandler.open(CANCEL_SUBSCRIPTION_ACTION_ID);
		}
		else if (REQUEST_METHOD_LIST_ITEMS.equals(bundle.get(InAppBillingController.BILLING_REQUEST_METHOD)))
		{
			webHandler.setStringParser(webHandler.new StringParser() {
				@Override
				public void parseString(String str)
				{
					Hashtable<String, String> resp = parseResponse(str);

					if (resp.get(PARAM_STATUS).equals("0"))
					{
						//return ok if subscription cancelled with success
						Bundle response = new Bundle();
						response.putInt(InAppBillingController.BILLING_RESPONSE_RESPONSE_CODE, InAppBillingController.RESULT_OK);
						endLogic(response);
					}
					else
						triggerError(InAppBillingController.ERROR_LIBRARY_ERROR, "Server returns: status=" + resp.get(PARAM_STATUS) + " message=" + resp.get(PARAM_MESSAGE));
				}
			});
			webHandler.open(LIST_ITEMS_ACTION_ID);
		}
		else
			// wrong action return lib error
			triggerError(InAppBillingController.ERROR_LIBRARY_ERROR, "Action not supported");

	}

	private void addAppKeyAndSubscriptionIdParams(WebViewHandler webViewHandler)
	{
//		PackageManager pm = context.getPackageManager();
//        String packageName = context.getPackageName();
//
//        for (ApplicationInfo app : pm.getInstalledApplications(0)) 
//        {
//            if (app.packageName.contains(packageName)) 
//            {
//                Log.d("PackageList", "package: " + app.packageName + ", sourceDir: " + app.sourceDir);
//            }
//        }
        AssetManager assetManager = context.getAssets();
		try
		{
			InputStream in = assetManager.open("dtpayparams.txt");
			InputStreamReader inputreader = new InputStreamReader(in);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line, line1 = "";
			try
			{
				while ((line = buffreader.readLine()) != null)
				{
					line1 += line;
					String[] lineParts = line.split(":");
					if(lineParts != null && lineParts.length > 1)
					{
						webViewHandler.putParam(lineParts[0], lineParts[1]);
						Log.d(LOG_TAG, "Params content:" + lineParts[0] + ":" + lineParts[1]);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			buffreader.close();
			inputreader.close();
			in.close();
		}
		catch (Exception e)
		{

		}
	}
	
	LogicHandler logicHandler;
	Bundle lastBundleAction;
	Bundle bundleToReturn;
	FrameLayout layout;

	@Override
	public void setViewAndHandler(FrameLayout layout, LogicHandler handler)
	{
		//		Log.i(LOG_TAG, "setViewAndHandler()");
		this.layout = layout;
		this.logicHandler = handler;

		this.layout.setBackgroundColor(Color.GREEN);
		if (currentWebView != null)
		{

			currentWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			currentWebView.postInvalidate();

			ViewGroup parent = (ViewGroup) currentWebView.getParent();
			if (parent != null)
			{
				parent.removeView(currentWebView);
			}
			layout.addView(currentWebView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));//, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		}
	}

	//	@Override
	//	public void handleNetworkRequestFail(String name_id, int code, String errorMsg)
	//	{
	//		Log.i(LOG_TAG, "handleNetworkRequestFail()");
	//		//handleError(ERROR_CONNECTION);
	//	}
	//
	//	@Override
	//	public void handleNetworkRequestSuccess(String name_id, byte[] data)
	//	{
	//		Log.i(LOG_TAG, "handleNetworkRequestSuccess()");
	//		Hashtable resp = parseResponse(data);
	//
	//		// responses are the same 
	//		//triggerError(InAppBillingCallbackInterface.ERROR_IN_APP_BILLING_LIB_ERROR);
	//	}

}

/////////
