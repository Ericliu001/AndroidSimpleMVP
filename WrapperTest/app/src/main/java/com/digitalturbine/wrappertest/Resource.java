package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;
/**
 *
 * @author akurek
 */
public class Resource implements NetworkRequest
{
	public static Cookie staticCookie;
	public static String cookieString;

	public void loadStatic()
	{

		//resources = null;

		frameCounter = 1;

		//		lastNetworkRequest = 0;
		//		networkRequest_id = new String[NETWORK_REQUESTS];
		//		networkRequest_header = new Hashtable[NETWORK_REQUESTS];
		//		networkRequest_url = new String[NETWORK_REQUESTS];
		//		networkRequest_data = new byte[NETWORK_REQUESTS][];

		customTextResources = null;
		customTextLocale = null;

		assetid = null;
		contentid = null;
		nodeid = null;
		externalid = null;

		//ct = null;
		//		ct_last = null;
		//		ct_finish = false;
		//		ct_trigered = false;

	}

	private Hashtable resources;

	//use it only for tests
	//commented do to memory leak possibility
	//public static Activity m_Activity;

	protected int frameCounter = 1;
	private String androidUserAgent;

	// Stores custom text received from the server after the setup call
	private Hashtable customTextResources = null;
	private String customTextLocale = null;

	protected String assetid;
	protected String contentid;
	protected String nodeid;
	protected String externalid;

	public void incrementFrameCounter()
	{
		frameCounter++;
	}

	protected static final int DEFAULT_DPI = 160;
	protected static final String DEFAULT_RES_DIRECTORY = "/assets";

	public int currentDPI = -1;
	public float currentDensity;
	private String currentDPIfolder;

	//	public int getCurrentDPI()
	//	{
	//		return currentDPI;
	//	}

	private String locale_native;

	public String getLocaleNative()
	{
		return locale_native;
	}

	private Context context = null;

	public Resource(Activity activity)
	{
		//m_Activity = activity;
		WebView wv = new WebView(activity);
		androidUserAgent = wv.getSettings().getUserAgentString();
		this.context = activity.getApplicationContext();

		//DisplayMetrics metrics = new DisplayMetrics();
		//activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

		currentDPI = metrics.densityDpi;
		currentDensity = metrics.density;

		currentDPIfolder = "/assets/" + currentDPI;

		if (currentDPI != DEFAULT_DPI)
		{
			//			Constants.multiplyConst((float) currentDPI / DEFAULT_DPI);
		}

		locale_native = activity.getResources().getConfiguration().locale.getLanguage();

		resources = new Hashtable();
		loadStatic();

		// Load the language configuration
		fetchLanguageConfig();
	}

	public void freeStaticData()
	{

		//    	customTextResources.clear();
		//    	customTextResources = null;
		//    	customTextLocale = null;

		resources.clear();
		resources = null;

		assetid = null;
		contentid = null;
		externalid = null;
		nodeid = null;
		//    	subscriptionid	= null;
		//    	username		= null;
		//    	password		= null;

		//ct = null;
		//ct_last = null;

		System.gc();
	}

	public void fechConfigAndLoadJarParams()
	{
		// Load the application config
		fetchApplicationConfig();

		// loadJadParams
		loadJadParams();
	}

	public void loadJadParams()
	{

		/* update jad params from constants */

		//if()

		/* */

		assetid = getAppProperty(CommonConstants.JAD_PARAM_AYCE_ASSET_ID);
		contentid = getAppProperty(CommonConstants.JAD_PARAM_AYCE_CONTENT_ID);
		nodeid = getAppProperty(CommonConstants.JAD_PARAM_AYCE_NODE_ID);
		externalid = getAppProperty(CommonConstants.JAD_PARAM_AYCE_EXTERNAL_ID);

		// Store ids as resources to make them accessible from UI elements
		//		updateResource(Constants.RESOURCE_TEXT_ARTICLE_ID, "article id: " + contentid);
		//		updateResource(Constants.RESOURCE_TEXT_EXTERNAL_ID, "external id: " + externalid);
		//
		//		String version = getAppProperty(Constants.JAD_PARAM_AYCE_VERSION);
		//		if ("true".equals(getAppProperty(Constants.JAD_PARAM_AYCE_VERSION_ENABLED)) && (version != null))
		//		{
		//			updateResource(Constants.RESOURCE_TEXT_VERSION, "v." + version);
		//		}
		//		else
		//		{
		//			updateResource(Constants.RESOURCE_TEXT_VERSION, "");
		//		}

		// Read the user agent from the .jad file
		String userAgentEnabled = getAppProperty(CommonConstants.JAD_PARAM_AYCE_USER_AGENT_ENABLED);
		if ((userAgentEnabled != null) && (userAgentEnabled.toLowerCase().equals("true")))
		{
			String useragent = getAppProperty(CommonConstants.JAD_PARAM_AYCE_USER_AGENT);
			if (useragent == null || (useragent != null && "Missing".equals(useragent)))
			{
				updateResource(CommonConstants.JAD_PARAM_AYCE_USER_AGENT, androidUserAgent);
			}
		}

	}

	public Object getResource(Object id)
	{
		return resources.get(id);
	}

	public boolean removeResourceForKey(Object key)
	{
		if (resources.containsKey(key))
		{
			resources.remove(key);
			return true;
		}
		return false;
	}

	public String getString(Object id)
	{
		String ret = (String) getResource(id);
		return ret == null ? "Missing" : ret;
	}

	//	public Image getImage(Object key)
	//	{
	//
	//		// Note: added because the game image may not be present. In this case the id is null, and the placeholder must be used
	//		if (key == null)
	//		{
	//			return (Image) getResource(Constants.RESOURCE_IMAGE_PLACEHOLDER);
	//		}
	//
	//		Image image = (Image) getResource(key);
	//
	//		if (image == null)
	//		{
	//			// Return the placeholder
	//			image = (Image) getResource(Constants.RESOURCE_IMAGE_PLACEHOLDER);
	//		}
	//
	//		return image;
	//	}
	//
	//	public Font getFont(Object id)
	//	{
	//		return (Font) getResource(id);
	//	}

	public void putResource(Object id, Object val)
	{
		updateResource(id, val);
	}

	public void updateResource(Object id, Object val)
	{
		if (val != null)
		{
			Object test = resources.remove(id);
			resources.put(id, val);
		}
	}

	public void setCustomTextLocale(String locale)
	{
		customTextLocale = processLocale(locale);

		if (customTextResources != null)
		{
			customTextResources.clear();
		}
		else
		{
			customTextResources = new Hashtable();
		}
	}

	public void putCustomText(Object key, String val)
	{
		customTextResources.put(key, val);
	}

	private void copyCustomTextOverDefaults(String locale)
	{

		if ((customTextLocale == null) || (customTextResources == null))
		{
			return;
		}

		for (Enumeration e = customTextResources.keys(); e.hasMoreElements();)
		{
			Object obj = e.nextElement();

			String key = (String) obj;
			String val = (String) customTextResources.get(obj);

			if (key.startsWith(CommonConstants.RESOURCE_TEXT_HELP_PREFIX))
			{
				// Copy the help topics and help texts independent from the locale
				updateResource(obj, customTextResources.get(obj));
			}
			else
			{
				// Copy other text only if the custom text locale matches the device locale
				if (customTextLocale.equals(locale))
				{
					updateResource(obj, customTextResources.get(obj));
				}
			}
		}
	}

	public String processLocale(String locale)
	{
		if (locale != null)
		{

			// Convert to lower case and cut off leading and trailing spaces
			locale = locale.toLowerCase().trim();

			// Use only the language code 
			if (locale.length() > 2)
			{
				locale = locale.substring(0, 2);
			}
		}
		return locale;
	}

	public int getNumHelpPages()
	{
		int num = 0;

		while (getResource(CommonConstants.RESOURCE_TEXT_HELP_PREFIX_TEXT + num) != null)
		{
			num++;
		}

		return num;
	}

	public void fetchConfig(String name)
	{
		final StringBuffer sb = new StringBuffer();

		// Read the utf8 data into a string
		final String s = bytesToString(fetchBytes(CommonConstants.RESOURCE_TWISTBOX_PREFIX + name));

		int index = 0, separator;

		while (index < s.length())
		{

			index = Resource.readLine(sb, s, index);

			// Trim the line and make it a string
			String t = sb.toString().trim();

			// Find the first '=' (separates string identifier and string value)
			if ((separator = t.indexOf('=')) < 0 || t.startsWith("#"))
			{
				// Line does not contain a separator, so skip it
				continue;
			}

			String key = t.substring(0, separator);
			String val = replaceString(t.substring(separator + 1, t.length()), "\\n", "\n");

			updateResource(key, val);
		}
	}

	// get SHA1 from texts 
	private String getSHA1(Map<String, String> textTable) throws NoSuchAlgorithmException
	{

		// sort text table by keys
		ArrayList<String> keys = new ArrayList<String>(textTable.keySet());
		Collections.sort(keys);
		StringBuffer text = new StringBuffer();
		for (String k : keys)
		{
			text.append((k + "=" + textTable.get(k)).trim() + "\\n\\r");
		}

		//alsoavailable=Also available\n\rauthenticatingtext=Authenticating...\n\rback=Back\n\rcancel=Cancel\n\rcontinue=Retry\n\rdiscovergames=Discover other games\n\rerror=Error\n\rerrorVDEsimText=You need to use mobile connection to use this product.\n\rerrorVDEsimTitle=Error\n\rerrorconnection=A successful network connection was not possible. Please ensure that this game can use a network connection! If you have allowed a network connection for this game and this problem persists, please restart the phone and try again!\n\rerrorcredentials=This might be because\n\nYou've entered the wrong username or password\n\nTap Continue to try again\n\rerrorpurchaselong=We are sorry, an error occured during your transaction.\nYour account has not been charged. Please allow the use of a data connection when asked.\nIf the issue persists please check data transfer availability and configuration.\n\rerrorpurchaseshort=We are sorry, an error occured during your transaction.\nYour account has not been charged. Please go back and try again.\n\rerrortransaction=We are sorry, an error occured during your transaction.\nPlease allow a network connection when asked.\nIf the issue persists please check data transfer availability and configuration.\n\rerrorunsufficientmoney=We are sorry, it seems that you are out of credit. Please top up and try again.\n\rexit=Exit\n\rgameinvalid=This Game is no more available. You can visit our shop to find out about more games available for you.\n\rhelp=Help\n\rinfo=Info\n\rinfoWiFiTurnedOff=Your WiFi connection will be turned off for authorization, after that it will be turned on again automatically.\n\nPlease make sure your mobile connection data is enabled.\n\rlanguage=Language\n\rlearnmore=Learn more about games\n\rlimitedplay=A successful network connection was not possible. You may play this game for a limited time. Please ensure that this game can use a network connection! If you have allowed a network connection for this game and this problem persists, please restart the phone and try again!\n\rloadingTransaction=We are now processing your transaction please wait.\n\rloadingtext=Loading...\n\rlogin=Login\n\rloginfailed=Unable to log in\n\rloginrequired=In order to validate the game you need to login with your username and password.\n\rmansubdial_dns=Don't show that again\n\rmansubdial_msg=Go to the store to find more exciting applications.<br /> <br /> <a href="http://wap.uk.samsungmobile.com/touch_UI/category.jsp?diu=&m=%21dxsHk1g7H64NqI&l=&wls=T5kSTPkP%21-29731275&ca=&i=&h=N&mp3=&op=005&cs=&ts=&d=&cp=&co=&a=0&s">Get More Apps</a>\n\rmansubdial_tit=Get More Apps\n\rmoregames=More games\n\rok=OK\n\rpassword=Password\n\rplay=Play\n\rpurchase=Purchase\n\rpurchaseCompleteText=Thank you for purchasing the product. Enjoy your purchase.\n\rpurchaseCompleteTitle=Purchase completed\n\rpurchaseValidText=You have already purchased this application, you have not been charged again. You can now use the application. \n\rpurchaseValidTitle=Purchase valid\n\rpurchaseoptions=Purchase options\n\rrembme=Remember me\n\rrentalCompleteText=Thank you for renting the product. Enjoy your rental.\n\rrentalCompleteTitle=Rental completed\n\rrentalValidText=You are already renting this application, you have not been charged again. You can now use the application. \n\rrentalValidTitle=Rental valid\n\rsave=Save\n\rsmsSendingError=Unable to send SMS. Please check that you have a mobile connection, that airplane mode is disabled and try again. Do you want to retry?\n\rtbyb0=This is a free try before you buy version, you can play\n\rtbyb1a=time\n\rtbyb1b=times\n\rtbyb2=for\n\rtbyb3=. Press 'OK' to start the trial.\n\rtbyb3a=day\n\rtbyb3b=days\n\rtbyb4a=hour\n\rtbyb4b=hours\n\rtbyb5a=minute\n\rtbyb5b=minutes\n\rtbyb6a=second\n\rtbyb6b=seconds\n\rtbybLabel=TRY IT!\n\rterms=Terms & Conditions\n\rthankyou=Thank you.\nYour transaction will now be verified.\n\rusername=Username\n\rvalidating=A validation is required. Your content will be available shortly.\n\r
		//alsoavailable=Also available\n\rauthenticatingtext=Authenticating...\n\rback=Back\n\rcancel=Cancel\n\rcontinue=Retry\n\rdiscovergames=Discover other games\n\rerror=Error\n\rerrorVDEsimText=You need to use mobile connection to use this product.\n\rerrorVDEsimTitle=Error\n\rerrorconnection=A successful network connection was not possible. Please ensure that this game can use a network connection! If you have allowed a network connection for this game and this problem persists, please restart the phone and try again!\n\rerrorcredentials=This might be because\n\nYou've entered the wrong username or password\n\nTap Continue to try again\n\rerrorpurchaselong=We are sorry, an error occured during your transaction.\nYour account has not been charged. Please allow the use of a data connection when asked.\nIf the issue persists please check data transfer availability and configuration.\n\rerrorpurchaseshort=We are sorry, an error occured during your transaction.\nYour account has not been charged. Please go back and try again.\n\rerrortransaction=We are sorry, an error occured during your transaction.\nPlease allow a network connection when asked.\nIf the issue persists please check data transfer availability and configuration.\n\rerrorunsufficientmoney=We are sorry, it seems that you are out of credit. Please top up and try again.\n\rexit=Exit\n\rgameinvalid=This Game is no more available. You can visit our shop to find out about more games available for you.\n\rhelp=Help\n\rinfo=Info\n\rinfoWiFiTurnedOff=Your WiFi connection will be turned off for authorization, after that it will be turned on again automatically.\n\nPlease make sure your mobile connection data is enabled.\n\rlanguage=Language\n\rlearnmore=Learn more about games\n\rlimitedplay=A successful network connection was not possible. You may play this game for a limited time. Please ensure that this game can use a network connection! If you have allowed a network connection for this game and this problem persists, please restart the phone and try again!\n\rloadingTransaction=We are now processing your transaction please wait.\n\rloadingtext=Loading...\n\rlogin=Login\n\rloginfailed=Unable to log in\n\rloginrequired=In order to validate the game you need to login with your username and password.\n\rmansubdial_dns=Don't show that again\n\rmansubdial_msg=Go to the store to find more exciting applications.<br /> <br /> <a href="http://wap.uk.samsungmobile.com/touch_UI/category.jsp?diu=&m=%21dxsHk1g7H64NqI&l=&wls=T5kSTPkP%21-29731275&ca=&i=&h=N&mp3=&op=005&cs=&ts=&d=&cp=&co=&a=0&s">Get More Apps</a>\n\rmansubdial_tit=Get More Apps\n\rmoregames=More games\n\rok=OK\n\rpassword=Password\n\rplay=Play\n\rpurchase=Purchase\n\rpurchaseCompleteText=Thank you for purchasing the product. Enjoy your purchase.\n\rpurchaseCompleteTitle=Purchase completed\n\rpurchaseValidText=You have already purchased this application, you have not been charged again. You can now use the application.\n\rpurchaseValidTitle=Purchase valid\n\rpurchaseoptions=Purchase options\n\rrembme=Remember me\n\rrentalCompleteText=Thank you for renting the product. Enjoy your rental.\n\rrentalCompleteTitle=Rental completed\n\rrentalValidText=You are already renting this application, you have not been charged again. You can now use the application.\n\rrentalValidTitle=Rental valid\n\rsave=Save\n\rsmsSendingError=Unable to send SMS. Please check that you have a mobile connection, that airplane mode is disabled and try again. Do you want to retry?\n\rtbyb0=This is a free try before you buy version, you can play\n\rtbyb1a=time\n\rtbyb1b=times\n\rtbyb2=for\n\rtbyb3=. Press 'OK' to start the trial.\n\rtbyb3a=day\n\rtbyb3b=days\n\rtbyb4a=hour\n\rtbyb4b=hours\n\rtbyb5a=minute\n\rtbyb5b=minutes\n\rtbyb6a=second\n\rtbyb6b=seconds\n\rtbybLabel=TRY IT!\n\rterms=Terms & Conditions\n\rthankyou=Thank you.\nYour transaction will now be verified.\n\rusername=Username\n\rvalidating=A validation is required. Your content will be available shortly.\n\r
		// generate sha1 from text
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(text.toString().getBytes());
		byte[] output = md.digest();
		String sha1 = bytesToHex(output);

		return sha1;
	}

	private static String bytesToHex(byte[] bytes)
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

	public void fetchLanguage(String locale, boolean customHelpTextAvailable)
	{
		final StringBuffer sb = new StringBuffer();
		Hashtable<String, String> texts = new Hashtable<String, String>();

		// Read the utf8 data into a string
		byte[] data = readFile(context, CommonConstants.RESOURCE_TWISTBOX_PREFIX + locale);
		if (data == null) // no saved language 
			data = fetchBytes(CommonConstants.RESOURCE_TWISTBOX_PREFIX + locale);

		final String s = bytesToString(data);

		int index = 0, separator;

		// Remove help text resources
		for (int i = 0;; i++)
		{
			String key = CommonConstants.RESOURCE_TEXT_HELP_PREFIX_TEXT + i;
			if (!removeResourceForKey(key))
			{
				break;
			}
			removeResourceForKey(CommonConstants.RESOURCE_TEXT_HELP_PREFIX_INDEX + i);
		}

		while (index < s.length())
		{

			index = Resource.readLine(sb, s, index);

			// Trim the line and make it a string
			String t = sb.toString().trim();

			// Find the first '=' (separates string identifier and string value)
			if ((separator = t.indexOf('=')) < 0 || t.startsWith("#"))
			{
				// Line does not contain a separator, so skip it
				continue;
			}

			String key = t.substring(0, separator);
			String val = "";
			String tmp = null;
			try
			{
				tmp = t.substring(separator + 1, t.length());
			}
			catch (IndexOutOfBoundsException ex)
			{

			}

			texts.put(key, tmp);

			if (tmp != null)
				val = replaceString(tmp, "\\n", "\n");

			if (!customHelpTextAvailable || !(key.startsWith(CommonConstants.RESOURCE_TEXT_HELP_PREFIX)))
			{
				// Update the resource associated with the string identifier and replace newline characters
				updateResource(key, val);
			}
		}

		currentLanguage = locale;
		currentCustomHelpTextAvailable = customHelpTextAvailable;
		try
		{
			currentLanguageSHA1 = getSHA1(texts);
		}
		catch (NoSuchAlgorithmException e)
		{
		}

		// Overwrite default help text resources with custom help texts
		copyCustomTextOverDefaults(locale);
	}

	public void fetchLanguageConfig()
	{
		fetchLanguage(CommonConstants.RESOURCE_FILENAME_LANGUAGE_CONFIG, false);
	}

	public String fetchLanguageName(int n)
	{
		return getString("lang" + n);
	}

	public String fetchDefaultLanguageName(String loc)
	{

		if ((loc == null) || ("".equals(loc)))
		{
			return null;
		}

		loc = processLocale(loc);

		// Find locale string in set of supported locales 
		int numLocales = fetchLanguageCount();
		for (int i = 0; i < numLocales; i++)
		{
			if (loc.equals(getString("lang" + i)))
			{
				return loc;
			}
		}

		// The locale string may be valid but is not among the supported locales
		return null;
	}

	public int fetchLanguageCount()
	{
		int num = 0;
		while (getResource("lang" + num) != null)
		{
			num++;
		}
		return num;
	}

	private String currentLanguage = "";
	private String currentLanguageSHA1 = "";
	private boolean currentCustomHelpTextAvailable;

	public String getCurrentLang()
	{
		return currentLanguage;
	}

	public String getCurrentLangSHA()
	{
		return currentLanguageSHA1;
	}

	//NEVER TESTED - draft version
	public void updateLanguage(String[] lines) // not sure what we get 
	{
		StringBuffer sb = new StringBuffer();
		for (String line : lines)
		{
			sb.append(line);
			sb.append('\n');
		}

		byte[] text = sb.toString().getBytes();
		byte[] data = new byte[text.length + 3];

		//patch the correct length to generate modified utf8 complient format (some old stuf from Adrian :) )

		data[0] = (byte) (((data.length - 2) >> 8) & 0xff);
		data[1] = (byte) ((data.length - 2) & 0xff);
		data[2] = ' '; //need to fill with space character

		System.arraycopy(text, 0, data, 3, text.length);
		writeFile(context, CommonConstants.RESOURCE_TWISTBOX_PREFIX + currentLanguage, data, 0, data.length);

		fetchLanguage(currentLanguage, currentCustomHelpTextAvailable);
	}

	@Deprecated
	public void updateLanguage(String loc, String version, byte[] data, Context context)
	{

		int i = 0;
		String langLoc = (String) getResource(CommonConstants.RESOURCE_LANGUAGE_PREFIX + i);

		String output = "";

		while (langLoc != null)
		{
			if (langLoc.equals(loc)) //update version first
				putResource(CommonConstants.RESOURCE_LANGUAGE_PREFIX + CommonConstants.RESOURCE_LANGUAGE_VERSION_PREFIX + i, version);

			output += CommonConstants.RESOURCE_LANGUAGE_PREFIX + i + "=" + langLoc + "\n";
			output += CommonConstants.RESOURCE_LANGUAGE_PREFIX + CommonConstants.RESOURCE_LANGUAGE_VERSION_PREFIX + i + "=" + ((String) getResource(CommonConstants.RESOURCE_LANGUAGE_PREFIX + CommonConstants.RESOURCE_LANGUAGE_VERSION_PREFIX + i))
					+ "\n";

			output += CommonConstants.RESOURCE_LANGUAGE_PREFIX + CommonConstants.RESOURCE_LANGUAGE_NAME_PREFIX + i + "=" + ((String) getResource(CommonConstants.RESOURCE_LANGUAGE_PREFIX + CommonConstants.RESOURCE_LANGUAGE_NAME_PREFIX + i)) + "\n";

			langLoc = (String) getResource(CommonConstants.RESOURCE_LANGUAGE_PREFIX + ++i);
		}

		byte[] outConfig = output.getBytes();

		// write new config file 
		writeFile(context, CommonConstants.RESOURCE_TWISTBOX_PREFIX + CommonConstants.RESOURCE_FILENAME_LANGUAGE_CONFIG, outConfig, 0, outConfig.length);

		// write new language
		writeFile(context, CommonConstants.RESOURCE_TWISTBOX_PREFIX + loc, data, 0, data.length);

		//writeFile(context, "", , 0, 2)
		//		String path = context.getApplicationContext().getFilesDir() + "/assets/com_twistbox_en.bin";
		//		try
		//		{
		//			//URL url = new URL(path);
		//			//URI uri = url.toURI();
		//			//context.getApplicationContext().ge
		//			URL url = Resource.class.getResource("/assets/com_twistbox_en.bin");
		//
		//			InputStream is = context.getContentResolver().openInputStream(Uri.parse(url.toString()));//Uri.parse(path));
		//		}
		//		catch (Exception e3)
		//		{
		//			// TODO Auto-generated catch block
		//			e3.printStackTrace();
		//		}
		//		//URL url = Resource.class.getResource("/assets/com_twistbox_en.bin");
		//		URL url = null;
		//		try
		//		{
		//			url = new URL("file://data/app/com.test.ipx-1.apk!/assets/com_twistbox_en.bin");
		//		}
		//		catch (MalformedURLException e2)
		//		{
		//			// TODO Auto-generated catch block
		//			e2.printStackTrace();
		//		}
		//		//File f = new File(url.toURI())
		//		String[] list = null;
		//
		//		;
		//		try
		//		{
		//			list = context.getAssets().list("");
		//		}
		//		catch (IOException e1)
		//		{
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}
		//		OutputStream myOutput;
		//		try
		//		{
		//			myOutput = new BufferedOutputStream(new FileOutputStream(new File(url.toURI())));
		//			myOutput.write(data);
		//
		//			myOutput.flush();
		//			myOutput.close();
		//		}
		//		catch (Exception e)
		//		{
		//			int i = 0;
		//		}

		//		try
		//		{
		//			FileOutputStream fos = context.getAssets().openFd("com_twistbox_en.mp3").createOutputStream();
		//			fos.write(8);
		//			fos.flush();
		//			fos.close();
		//			//			BufferedOutputStream bos = new BufferedOutputStream();
		//			//			//bos.write(data);
		//			//			bos.write(6);
		//			//			bos.flush();
		//			//			bos.close();
		//		}
		//		catch (IOException e)
		//		{
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		//		try
		//		{
		//			FileOutputStream fos = context.openFileOutput("s/com_twistbox_en.bin", Context.MODE_PRIVATE);
		//			fos.write(new byte[] { 0, 0, 0, 0, 0 });
		//			fos.close();
		//		}
		//		catch (Exception e)
		//		{
		//			int i = 0;
		//		}
	}

	public static Object getDataHessianStruct(Object res, String link)
	{
		int index = link.indexOf(';');
		if (index < 0)
			index = link.length();
		while (link.length() > 0 && res != null)
		{
			String el = link.substring(0, index);
			link = link.substring(Math.min(index + 1, link.length()));

			if (res instanceof Hashtable)
			{
				Hashtable hash = (Hashtable) res;
				res = hash.get(el);
			}
			else if (res instanceof Vector)
			{
				Vector v = (Vector) res;
				int i = Integer.parseInt(el);
				if (i >= v.size())
					res = null;
				else
					res = v.elementAt(i);
			}
			else if (res instanceof Object[])
			{
				Object[] v = (Object[]) res;
				int i = Integer.parseInt(el);
				if (i >= v.length)
					res = null;
				else
					res = ((Object[]) res)[i];
			}
			index = link.indexOf(';');
			if (index < 0)
				index = link.length();
		}
		return res;
	}

	//	private Image fetchImageFromDirectory(String name, String directory, final short[] colorsToChange, int DPI)
	//	{
	//
	//		name = directory + name;
	//
	//		try
	//		{
	//			return Image.createImage(name + ".png", colorsToChange, DEFAULT_DPI, DPI);
	//		} catch (Exception ignore)
	//		{
	//		}
	//
	//		try
	//		{
	//			return Image.createImage(name + ".jpg", colorsToChange, DEFAULT_DPI, DPI);
	//		} catch (Exception ignore)
	//		{
	//		}
	//
	//		try
	//		{
	//			return Image.createImage(name + ".gif", colorsToChange, DEFAULT_DPI, DPI);
	//		} catch (Exception ignore)
	//		{
	//		}
	//
	//		return null;
	//	}
	//
	//	public Image fetchImage(String name)
	//	{
	//		return fetchImage(name, null);
	//	}
	//
	//	public Image fetchImage(String name, final short[] colorsToChange)
	//	{
	//		if (!name.startsWith("/"))
	//		{
	//			name = "/" + name;
	//		}
	//
	//		Image ret = fetchImageFromDirectory(name, currentDPIfolder, colorsToChange, currentDPI);
	//		if (ret == null)
	//		{
	//			ret = fetchImageFromDirectory(name, DEFAULT_RES_DIRECTORY, colorsToChange, currentDPI);
	//		}
	//
	//		return ret;
	//	}

	public Bitmap fetchImageFromAssets(String name)
	{
		return fetchImageFromAssets(name, null);
	}

	public Bitmap fetchImageFromAssets(String name, BitmapFactory.Options opts)
	{
		InputStream is = null;
		Bitmap bitmap = null;
		try
		{
			is = Resource.class.getResourceAsStream("/assets" + (name.startsWith("/") ? "" : "/") + name);
			//in method above context is not needed
			//is = context.getAssets().open("/assets" + (name.startsWith("/") ? "" : "/") + name);

			if (opts != null)
				bitmap = BitmapFactory.decodeStream(is, null, opts);
			else
				bitmap = BitmapFactory.decodeStream(is);
		}
		catch (Exception e)
		{
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		return bitmap;
	}

	public byte[] fetchBytes(String name)
	{
		return fetchBytes(name, "bin");
	}

	private byte[] fetchBytesFromDirectory(String name, String extension, String directory)
	{

		name = directory + name;

		InputStream is = Resource.class.getResourceAsStream(name + '.' + extension);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int next;
		try
		{
			while ((next = is.read()) >= 0)
			{
				baos.write(next);
			}
		}
		catch (Exception e)
		{

		}
		finally
		{

			try
			{
				is.close();
			}
			catch (Exception e)
			{
			}

			is = null;
		}

		byte[] bytes = baos.toByteArray();

		try
		{
			baos.close();
		}
		catch (Exception e)
		{
		}

		baos = null;

		return bytes;
	}

	public byte[] fetchBytes(String name, String extension)
	{
		if (!name.startsWith("/"))
			name = "/" + name;

		byte[] ret = fetchBytesFromDirectory(name, extension, currentDPIfolder);
		if (ret == null || ret.length == 0)
		{
			ret = fetchBytesFromDirectory(name, extension, DEFAULT_RES_DIRECTORY);
		}

		return ret;
	}

	/**
	 * Read a String out of a byte array containing characters in UTF-8 format.
	 *
	 * @param b the byte array contining the UTF-8 data
	 * @return the String the data has been converted to
	 */
	public String bytesToString(final byte[] b)
	{
		if (b == null || b.length <= 3)
		{
			return "";
		}

		//patch the correct length to generate modified utf8 complient format
		b[0] = (byte) (((b.length - 2) >> 8) & 0xff);
		b[1] = (byte) ((b.length - 2) & 0xff);
		b[2] = ' '; //need to fill with space character

		//now convert into a string by reading from a datainputstream
		final ByteArrayInputStream bais = new ByteArrayInputStream(b);
		final DataInputStream is = new DataInputStream(bais);
		String s = null;

		try
		{
			s = is.readUTF();
			is.close();
			bais.close();
		}
		catch (Exception ignore)
		{
		}

		return s;
	}

	/**
	 * Read the next line out of the String str starting at position pos into the provided Stringbuffer sb.
	 *
	 * @param sb the Stringbuffer to read the line into
	 * @param str the string containing several lines of text seperated by \n characters
	 * @param pos the position to start reading from
	 * @return the position within str where the next line starts, use this as the pos parameter for subsequence calls to read line after line from str
	 */
	public static int readLine(final StringBuffer sb, final String str, int pos)
	{
		if (pos >= str.length())
		{
			throw new IllegalArgumentException("string index out of bounds: " + pos + ">=" + str.length());
		}

		// Reset the stringbuffer
		sb.setLength(0);

		// Read line
		for (;;)
		{
			for (; pos < str.length();)
			{
				char next = str.charAt(pos++);

				if (next == '\n')
				{
					break;
				}
				else if (next != '\r')
				{
					sb.append(next);
				}
			}

			// Skip over empty lines
			if (sb.length() > 0)
			{
				break;
			}
		}

		return pos;
	}

	public static final String[] splitString(String s, char separator)
	{
		// Next index of the separator character
		int index;

		// Vector of tokens, returned as String[] as soon as its size is known
		Vector v = new Vector();
		while ((index = s.indexOf(separator)) >= 0)
		{
			v.addElement(s.substring(0, index));
			s = s.substring(index + 1);
		}
		v.addElement(s);

		// Copy the vector to a String[], clear the vector and return
		int size = v.size();
		String[] tokens = new String[size];
		for (int i = 0; i < size; i++)
		{
			tokens[i] = (String) v.elementAt(i);
		}
		v.removeAllElements();
		v = null;

		return tokens;
	}

	public static int[] extendIntArray(int[] srcIntArray, int size)
	{
		int[] dstIntArray = new int[srcIntArray.length + size];
		System.arraycopy(srcIntArray, 0, dstIntArray, 0, srcIntArray.length);
		return dstIntArray;
	}

	public static char[] extendCharArray(char[] src, int increment)
	{
		char[] dst = new char[src.length + increment];
		System.arraycopy(src, 0, dst, 0, src.length);
		return dst;
	}

	/*
	 * If the given string is wider than the available width, the string is abbreviated and
	 * ellipsis dots "..." are appended. For example, the string "Abbreviated" may become "Abbrev..."
	 */
	//	public static String abbreviateString(String string, Font font, int availableWidth)
	//	{
	//		if (font.stringWidth(string) - availableWidth > 0)
	//		{
	//			String abbreviated;
	//			int dotsWidth = font.stringWidth("...");
	//			for (int i = string.length() - 1; i > 0; i--)
	//			{
	//				while (string.charAt(i - 1) == ' ')
	//				{
	//					i--;
	//				}
	//				abbreviated = string.substring(0, i);
	//				if (font.stringWidth(abbreviated) + dotsWidth < availableWidth)
	//				{
	//					return (abbreviated + "...");
	//				}
	//			}
	//		}
	//		return string;
	//	}

	public static void doGC()
	{
		System.gc();
		try
		{
			Thread.sleep(10);
		}
		catch (Exception ignore)
		{
		}
	}

	//	public static String[] splitLines(String text, int width, Font font)
	//	{
	//		System.out.println("Split line :" + text + ", width = " + width);
	//		String[] texts = null;
	//
	//		int _splitIndex = 0;
	//		int[] _split = new int[10];
	//		_split[_splitIndex++] = 1;
	//
	//		int lastSplitIndex = 1;
	//		int nextSplitIndex = 0;
	//		int lastSpace = -1;
	//		while (nextSplitIndex <= text.length())
	//		{
	//			boolean wrap = false;
	//			boolean wrap_new = false;
	//			String subPart = text.substring(lastSplitIndex - 1, nextSplitIndex);
	//			if (font.drawString(subPart, 0, 0, null) > width && width > 0)
	//			{
	//				wrap = true;
	//			}
	//			if (subPart.endsWith("\n"))
	//			{
	//				wrap = true;
	//				wrap_new = true;
	//			}
	//			if (wrap)
	//			{
	//				if (wrap_new || lastSpace == -1 || lastSpace + 1 == lastSplitIndex)
	//				{
	//					_split[_splitIndex++] = nextSplitIndex;
	//					lastSplitIndex = nextSplitIndex;
	//					lastSpace = nextSplitIndex - 1;
	//				}
	//				else
	//				{
	//					_split[_splitIndex++] = lastSpace + 1;
	//					lastSplitIndex = lastSpace + 1;
	//				}
	//				if (_splitIndex == _split.length)
	//				{
	//					int[] newone = new int[_split.length + 10];
	//					System.arraycopy(_split, 0, newone, 0, _split.length);
	//					_split = newone;
	//				}
	//			}
	//			else
	//			{
	//				if (nextSplitIndex < text.length() && text.charAt(nextSplitIndex) == ' ')
	//					lastSpace = nextSplitIndex;
	//			}
	//			nextSplitIndex++;
	//		}
	//
	//		texts = new String[_splitIndex];
	//		for (int i = 0; i < _splitIndex - 1; i++)
	//		{
	//			if (_split[i + 1] > _split[i])
	//				texts[i] = text.substring(_split[i] - 1, _split[i + 1] - 1).trim();
	//			else
	//				texts[i] = "";
	//		}
	//		texts[texts.length - 1] = text.substring(_split[_splitIndex - 1] - 1).trim();
	//
	//		return texts;
	//	}

	public static String replaceString(String _text, String _searchStr, String _replacementStr)
	{
		// String buffer to store str
		StringBuffer sb = new StringBuffer();

		// Search for search
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();

		// Iterate to add string
		while (searchStringPos != -1)
		{
			sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}

		// Create string
		sb.append(_text.substring(startPos, _text.length()));

		return sb.toString();
	}

	Vector<ConnectionThread> cts = new Vector<ConnectionThread>();
	

	@Override
	public void networkRequest(String name_id, String url, byte[] data, Hashtable headers, String method, final NetworkRequestHandler handler)
	{
		if (method != null && !"GET".equals(method) && !"POST".equals(method))
		{
			handler.handleNetworkRequestFail(name_id, -1, "Request method not supported. Method: " + method);
		}
		else
		{
			ConnectionThread ct = new ConnectionThread(name_id, "POST".equals(method), new ConnectionThreadHandler() {

				@Override
				public void handleNetworkRequestSuccess(String requestID, byte[] data, ConnectionThread cth)
				{
					for(ConnectionThread connectionThread : cts)
					{
						Log.i("Resource.ConnectionThreadHandler", "Success, requestid: " + requestID + " datat: " + new String(data) + " old connectionthread: " + connectionThread + " new connectionthread: " + cth);
						if (connectionThread.uuid.equals(cth.uuid)) // make response only on actual requests
						{
							cts.remove(connectionThread);
							connectionThread = null;
							Log.i("Resource.ConnectionThreadHandler", "Success, oconnectionthreads equal");
							handler.handleNetworkRequestSuccess(requestID, data);
							break;
						}
					}
				}

				@Override
				public void handleNetworkRequestFail(String requestID, int code, String errorMsg, ConnectionThread cth)
				{
					for(ConnectionThread connectionThread : cts)
					{
						if (connectionThread.uuid.equals(cth.uuid))// make response only on actual requests
						{
							cts.remove(connectionThread);
							connectionThread = null;
							handler.handleNetworkRequestFail(requestID, code, errorMsg);
							break;
						}
					}
				}
			});

			cts.add(ct);
			ct.request(url, data, headers);
		}
	}

	public void networkReset()
	{
		//ct = null;
		cts.removeAllElements();
	}

	public interface ConnectionThreadHandler
	{
		public void handleNetworkRequestSuccess(String requesID, byte[] data, ConnectionThread ct);

		public void handleNetworkRequestFail(String requesID, int code, String errorMsg, ConnectionThread ct);
	}

	protected class ConnectionThread implements Runnable
	{

		//public Resource tbna;

		private volatile String strUrl;
		protected volatile Hashtable headers;
		protected volatile Hashtable<String, String> params;
		public volatile byte[] data;

		private int retryCount;

		ConnectionThreadHandler handler;

		//private DataOutputStream os;
		private DataInputStream dis;

		private Thread t;
		private Timer timer;

		private static final int TIME_OUT_TIME = 120000;

		String requestId;
		String uuid;
		
		private boolean isPost = false;

		public ConnectionThread(String requestID, boolean isPost, ConnectionThreadHandler handler)
		{
			this.requestId = requestID;
			this.handler = handler;
			this.isPost = isPost;
			UUID threadUUID = UUID.randomUUID();
			this.uuid = threadUUID.toString();
		}

		public void request(String url, byte[] data, Hashtable headers)
		{

			//setup new connection thread
			strUrl = url;
			this.data = data;
			this.headers = headers;

			retryCount = 3;

			startRequest();
		}

		private void startRequest()
		{
			retryCount--;
			if (retryCount == 0)
			{
				finishFail(getString(CommonConstants.RESOURCE_TEXT_TITLE_ERROR));
				return;
			}

			t = new Thread(this);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();

			if (timer != null)
				timer.cancel();

			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run()
				{
					timer = null;
					finishFail(getString(CommonConstants.RESOURCE_TEXT_TITLE_ERROR));
				}
			}, TIME_OUT_TIME);
		}

		private void finishSuccess(byte[] data)
		{
			if (timer != null)
			{
				timer.cancel();
				timer = null;
			}
			Log.i("Resource.ConnectionThread", "requestid: " + requestId + " data: " + data);
			handler.handleNetworkRequestSuccess(requestId, data, this);
			Log.i("Resource.ConnectionThread", "called handleNetworkRequestSuccess with requestid: " + requestId + " data: " + data);
		}

		private void finishFail(int code, String error)
		{
			if (timer != null)
			{
				timer.cancel();
				timer = null;
			}

			handler.handleNetworkRequestFail(requestId, code, error, this);
		}

		private void finishFail(String error)
		{
			finishFail(0, error);
		}

		public void run()
		{
			try
			{

				DefaultHttpClient client = new DefaultHttpClient();
				//String postURL = strUrl;

				HttpRequestBase request = null;
				if (isPost)
				{
					HttpPost post = new HttpPost(strUrl);
					if (data != null)
					{
						Log.i("Resource.ConnectionThread", "data:" + new String(data));
						ByteArrayEntity bae = new ByteArrayEntity(data);
						bae.setContentType("application/x-www-form-urlencoded");
						post.setEntity(bae);
					}

					request = post;
				}
				else
					request = new HttpGet(strUrl);

				//post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

				if (headers != null && headers.size() > 0)
				{
					Enumeration e = headers.keys();
					while (e.hasMoreElements())
					{
						String key = (String) e.nextElement();
						request.setHeader(key, (String) headers.get(key));
					}
				}

				HttpResponse response = client.execute(request);

				int code = response.getStatusLine().getStatusCode();
				Log.i("Resource.ConnectionThread","url:" + strUrl + " code:" + code);
				if (code == HttpStatus.SC_OK)
				{

					HttpEntity resEntity = response.getEntity();
					//if resEntity is null global catch will trigger
					dis = new DataInputStream(resEntity.getContent());

					strUrl = null;

					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					byte[] buffer = new byte[1024];
					int read;
					while ((read = dis.read(buffer)) > 0)
					{
						baos.write(buffer, 0, read);
					}

					data = baos.toByteArray();
					Log.i("Resource.ConnectionThread","url:" + strUrl + " data:" + new String(data));
					List<Cookie> cookies = client.getCookieStore().getCookies();
					if (cookies != null)
					{
						for (Cookie cookie : cookies)
						{
							staticCookie = cookie;
							cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
							CookieManager.getInstance().setCookie(cookie.getDomain(), cookieString);
							Log.i("Resource.ConnectionThread", "Cookie:" + cookieString);
						}
					}
					CookieSyncManager.getInstance().sync();
					finishSuccess(data);

				}
				else if (code / 100 == 3)
				{
					//#if DEBUG_true
					//@                            System.out.println("redirecting...");
					//#endif                            
					data = null;
					for (Header header : response.getAllHeaders())
					{
						for (HeaderElement headerElement : header.getElements())
						{
							System.out.println("HTTP " + headerElement.getName() + "=" + headerElement.getValue());
							if (headerElement.getName().equals("Location"))
							{
								strUrl = headerElement.getValue();
							}
						}
					}

					finishSuccess(null);
				}
				else
				{
					finishFail(code, "HTTP " + code);
				}
			}
			catch (Exception e)
			{
				startRequest(); // 3 tries 
			}
			finally
			{
				try
				{
					if (dis != null)
					{
						dis.close();
					}
				}
				catch (Exception ignore)
				{
				}

				dis = null;

			}

		}
	}

	/**
	 * Load a record store and return the data as a byte array.
	 *
	 * @param name Name of the record store (no more than 32 characters)
	 * @return record store data, or null if the record store could not be found
	 */
	public static byte[] getRecord(Context context, String name)
	{
		return readFile(context, name);
	}

	/**
	 * Save a record store.
	 * This will never return an error!
	 *
	 * @param name Name of the record store to save to
	 * @param data	 Data to store
	 */
	public static void putRecord(Context context, String name, byte[] data)
	{
		writeFile(context, name, data, 0, data.length);
	}

	public static void writeFile(Context context, String name, byte[] data, int offset, int size)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(name + ".dat", Context.MODE_PRIVATE);
			fos.write(data, offset, size);
			fos.close();
		}
		catch (Exception e)
		{
		}
	}

	public static byte[] readFile(Context context, String name)
	{
		try
		{
			FileInputStream fis = context.openFileInput(name + ".dat");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int read;
			while ((read = fis.read(buffer)) > 0)
			{
				baos.write(buffer, 0, read);
			}

			return baos.toByteArray();

		}
		catch (Exception e)
		{
			return null;
		}
	}

	public void fetchApplicationConfig()
	{
		fetchConfig(CommonConstants.RESOURCE_FILENAME_APPLICATION_CONFIG);
	}

	public String getAppProperty(String jadParam)
	{
		return getString(jadParam);
	}

}
