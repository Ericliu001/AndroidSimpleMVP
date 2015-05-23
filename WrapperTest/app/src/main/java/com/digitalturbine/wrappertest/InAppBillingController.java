/**
 * 
 */
package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

/**
 * @author dgrzeszczak
 *
 */
public class InAppBillingController implements LogicInterface.LogicHandler
{

	public static final String BILLING_REQUEST_METHOD = "BILLING_REQUEST";
	public static final String BILLING_RESPONSE_RESPONSE_CODE = "RESPONSE_CODE";

	public static final int RESULT_OK = 0;
	public static final int RESULT_USER_CANCELED = 1;
	public static final int RESULT_ERROR = 2;

	public static final String BILLING_ERROR_CODE = "ERROR_CODE";
	public static final String BILLING_ERROR_MESSAGE = "ERROR_MESSAGE";

	public static final int ERROR_LAST_ACTION_NOT_FINISHED = 0;
	public static final int ERROR_INCORRECT_PARAMETER = 1;
	public static final int ERROR_LIBRARY_ERROR = 2;

	public static final int NUM_CONTROLLER_ERROR_CODES = 3;
	//private static final String LOG_TAG = "LOGIC";

	private LogicInterface logic;
	InAppBillingAsynchronousHandler handler;

	//	protected static final int TYPE_NO_ACTION = 0;
	//	protected static final int TYPE_AUTHENTICATE = 1;
	//	protected static final int TYPE_PURCHASE = 2;
	//	protected static final int TYPE_CHECK_SUBSRIPTION = 3;
	//
	private boolean started = false;
	private Bundle bundle = null;
	//	private int type = TYPE_NO_ACTION;
	//	private Object data;
	//	private Object return_value;
	//	private int return_type = LogicInterface.RETURN_IN_PROGRESS;
	//	private int increment;

	private boolean wrapper = false;

	public boolean isWrapper()
	{
		return wrapper;
	}

	protected Handler callbackHandler;

	private Activity activity;

	public boolean isStarted()
	{
		return started;
	}

	protected void dismissDialogs()
	{
//		Log.d(LOG_TAG, "controller.dismissDialogs()");
		if (dialog != null)
		{
			try
			{
				dialog.dismiss();
			}
			catch (IllegalArgumentException ex)
			{

			}
			dialog = null;
		}
	}

	public interface DialogCallback
	{
		public static final int STATUS_OK = 0;
		public static final int STATUS_CANCEL = 1;

		void dialogFinished(int status);
	}

	public void showMessage(String title, String message, String positiveButton, String negativeButton, Boolean html, DialogCallback callback)
	{
//		Log.d(LOG_TAG, "controller.showMessage()");
		Message m = new Message();
		m.what = MESSAGE_SHOW_MESSAGE;
		Bundle b = new Bundle();
		if (html)
			b.putString(SHOW_MESSAGE_HTML, message);
		else
			b.putString(SHOW_MESSAGE, message);
		b.putString(SHOW_TITLE, title);

		//positive button always must be set standard is OK 
		if (positiveButton == null)
			b.putString(SHOW_POSITIVE_BUTTON, (String) getResource().getResource(CommonConstants.RESOURCE_SOFT_OK));
		else
			b.putString(SHOW_POSITIVE_BUTTON, positiveButton);

		b.putString(SHOW_NEGATIVE_BUTTON, negativeButton);

		m.setData(b);
		dialogCallback = callback;
		messageHandler.sendMessage(m);
	}

	public void showMessageWithCancel(String title, String message, Boolean html, DialogCallback callback)
	{
		showMessage(title, message, null, (String) getResource().getResource(CommonConstants.RESOURCE_SOFT_CANCEL), html, callback);
		//		Message m = new Message();
		//		m.what = MESSAGE_SHOW_MESSAGE_WITH_CANCEL;
		//		Bundle b = new Bundle();
		//		if (html)
		//			b.putString(SHOW_MESSAGE_HTML, message);
		//		else
		//			b.putString(SHOW_MESSAGE, message);
		//		b.putString(SHOW_TITLE, title);
		//		m.setData(b);
		//		dialogCallback = callback;
		//		messageHandler.sendMessage(m);
	}

	public void showMessage(String title, String message, Boolean html, DialogCallback callback)
	{
		showMessage(title, message, null, null, html, callback);
		//		Message m = new Message();
		//		m.what = MESSAGE_SHOW_MESSAGE;
		//		Bundle b = new Bundle();
		//		if (html)
		//			b.putString(SHOW_MESSAGE_HTML, message);
		//		else
		//			b.putString(SHOW_MESSAGE, message);
		//		b.putString(SHOW_TITLE, title);
		//		m.setData(b);
		//		dialogCallback = callback;
		//		messageHandler.sendMessage(m);
	}

	public void startProgressDialog(String message)
	{
//		Log.d(LOG_TAG, "controller.startProgressDialog()");
		Message m = new Message();
		m.what = MESSAGE_SHOW_PROGRESS;
		Bundle b = new Bundle();
		b.putString(SHOW_MESSAGE, message);
		m.setData(b);
		messageHandler.sendMessage(m);

	}

	public void hideDialog()
	{
// 		Log.d(LOG_TAG, "controller.hideDialog()");
		//messageHandler.sendEmptyMessage(MESSAGE_HIDE_DIALOG);
		Message msg = new Message();
		msg.what = MESSAGE_HIDE_DIALOG;
		messageHandler.sendMessage(msg);
	}

	public void runOnUiThread(Runnable runnable)
	{
		this.activity.runOnUiThread(runnable);
	}

	//	public void authenticate()
	//	{
	//		if (!canStartNewAction())
	//			return;
	//
	//		startNewAction(TYPE_AUTHENTICATE, null);
	//	}
	//
	//	public void checkTransactions(String[] articleIDs)
	//	{
	//		if (!canStartNewAction())
	//			return;
	//
	//		startNewAction(TYPE_CHECK_SUBSRIPTION, articleIDs);
	//	}
	//
	//	public void purchase(String articleID)
	//	{
	//		if (!canStartNewAction())
	//			return;
	//
	//		startNewAction(TYPE_PURCHASE, articleID);
	//	}

	public Bundle requestAction(Bundle bundle)
	{
		Bundle ret;
		if (started && inAppDialog != null)
		{
			ret = new Bundle();
			ret.putInt(BILLING_RESPONSE_RESPONSE_CODE, RESULT_ERROR);
			ret.putInt(BILLING_ERROR_CODE, ERROR_LAST_ACTION_NOT_FINISHED);
			ret.putString(BILLING_ERROR_MESSAGE, "Last request must be finished !");
			return ret;
		}

		ret = logic.checkAction(bundle);
		if (ret.getInt(BILLING_RESPONSE_RESPONSE_CODE) == RESULT_OK)
		{
			startNewAction(bundle);
		}

		return ret;
	}

	private void startNewAction(Bundle bundle)
	{
		//		this.type = type;
		//		this.data = data;
		this.bundle = bundle;
		this.started = true;

		if (inAppDialog == null)
		{
			final Activity activity = this.activity;
			final InAppBillingController controller = this;

			this.activity.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					inAppDialog = new InAppDialog(activity, controller);
					logic.setViewAndHandler(inAppDialog.getView(), controller);
					inAppDialog.show();

					startAction();
				}
			});
		}
		else
			startAction();

	}

	//	private boolean canStartNewAction()
	//	{
	//		// last action not finished 
	//		//		if (logic == null)
	//		//		{
	//		//			handler.error(InAppBillingCallbackInterface.ERROR_IN_APP_BILLING_LIB_ERROR);
	//		//			return false;
	//		//		}
	//		//		if (/*this.type != TYPE_NO_ACTION*/started && inAppDialog != null)
	//		//		{
	//		//			handler.error(InAppBillingCallbackInterface.ERROR_LAST_ACTION_NOT_FINISHED);
	//		//			return false;
	//		//		}
	//
	//		return true;
	//	}

	private Dialog dialog = null;
	private DialogCallback dialogCallback = null;
	private Handler messageHandler;

	private static final int MESSAGE_HIDE_DIALOG = 0;
	private static final int MESSAGE_SHOW_PROGRESS = 1;
	private static final int MESSAGE_SHOW_MESSAGE = 2;
	//private static final int MESSAGE_SHOW_MESSAGE_WITH_CANCEL = 3;

	private static final String SHOW_MESSAGE = "message";
	private static final String SHOW_MESSAGE_HTML = "messageHTML";
	private static final String SHOW_TITLE = "title";
	private static final String SHOW_POSITIVE_BUTTON = "positiveButton";
	private static final String SHOW_NEGATIVE_BUTTON = "negativeButton";

	private int lastMessage = MESSAGE_HIDE_DIALOG;
	private Bundle lastBundle = null;

	private void handleCallbackMessage(Bundle bundle)
	{
		started = false;
		boolean dismiss = true;
		//		if (value == LogicInterface.RETURN_ERROR)
		//			callback.error((String) return_value);
		//		else if (value == LogicInterface.RETURN_CANCELLED)
		//			callback.cancelled();
		//		else if (value == LogicInterface.RETURN_LOGIN_SUCCESS)
		//			callback.loginSuccess();
		//		else if (this.type == TYPE_CHECK_SUBSRIPTION)
		//		{
		//			callback.transactionsStatus((String[]) data, (boolean[]) return_value);
		//		}
		//		else if (value == LogicInterface.RETURN_SUBSCRIPTION_VALID)
		//			callback.subscribed((String) data, false);
		//		else if (value == LogicInterface.RETURN_SUBSCRIPTION_PURCHASED)
		//		{
		//			//purchase canfirmation moved to logic 
		//			callback.subscribed((String) data, true);
		//
		//			//dismiss = false;
		//			//showPurchaseConfirmation((String) data, logic.getResource().getString(Constants.RESOURCE_TEXT_TITLE_GAME));
		//		}
		//		else
		//			// shouldn't happen ;)
		//			callback.error(InAppBillingCallbackInterface.ERROR_IN_APP_BILLING_LIB_ERROR);

		handler.handleResponse(bundle);

		end(dismiss);
	}

	private void startAction()
	{

		logic.handleAction(bundle);


	}

	@Override
	public void logicEnded(Bundle bundle)
	{
		endLogic(bundle);
	}

	private void endLogic(Bundle bundle)
	{
		logic.end();
		// mark that all is finished 
		// dg dialog.nullShop();
		//this.type = TYPE_NO_ACTION;
		Message msg = new Message();
		//msg.what = this.return_type;
		msg.setData(bundle);
		callbackHandler.sendMessage(msg);
	}

	//	public void logicEnded(int returned_type)
	//	{
	//		this.return_type = returned_type;
	//		if (this.return_type != LogicInterface.RETURN_IN_PROGRESS)
	//		{
	//			if (this.type == TYPE_CHECK_SUBSRIPTION && (this.return_type == LogicInterface.RETURN_SUBSCRIPTION_VALID || this.return_type == LogicInterface.RETURN_SUBSCRIPTION_NOT_VALID))
	//			{
	//				boolean[] ret = (boolean[]) this.return_value;
	//				if (this.return_type == LogicInterface.RETURN_SUBSCRIPTION_VALID)
	//					ret[increment++] = true;
	//				else
	//					ret[increment++] = false;
	//
	//				if (increment >= ret.length)
	//					endLogic();
	//				else
	//					logic.handleAction(LogicInterface.ACTION_ID_SETUP, ((String[]) data)[increment]);
	//			}
	//			else
	//			{
	//				return_value = logic.getLastError();
	//				endLogic();
	//			}
	//		}
	//	}

	public InAppBillingController(Activity activity, InAppBillingAsynchronousHandler callback)
	{
		Log.i("INAPPLIB", "version " + GeneratedConstants.LIB_VERSION);
		this.activity = activity;
		this.handler = callback;

		try
		{
			Class appClass = Class.forName("com.digitalturbine.wrappertest.ApplicationFlow");
			this.wrapper = appClass.isInstance(activity.getApplication());
		}
		catch (Exception e)
		{
			// no wrapper :)
		}

		callbackHandler = new Handler() {
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);

				Bundle bundle = msg.getData();
				handleCallbackMessage(bundle);
			}
		};

		messageHandler = new Handler() {

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);

				handleMessageHandlerMessage(msg);
			}

		};

		//		triggerAppStart = true;
	}

	public void setLogic(LogicInterface logic)
	{
		this.logic = logic;
	}

	public void handleMessageHandlerMessage(Message msg)
	{
		lastMessage = msg.what;
		lastBundle = msg.getData();
		switch (msg.what)
		{
			case MESSAGE_SHOW_MESSAGE:
			//case MESSAGE_SHOW_MESSAGE_WITH_CANCEL:
			{
				dismissDialogs();

				String message = lastBundle.getString(SHOW_MESSAGE);
				String messageHTML = lastBundle.getString(SHOW_MESSAGE_HTML);
				String title = lastBundle.getString(SHOW_TITLE);
				String positiveButton = lastBundle.getString(SHOW_POSITIVE_BUTTON);
				String negativeButton = lastBundle.getString(SHOW_NEGATIVE_BUTTON);

				CharSequence messageTxt;
				if (message != null)
				{
					SpannableString spanText = new SpannableString(message);

					Linkify.addLinks(spanText, Linkify.ALL);
					messageTxt = spanText;
				}
				else
				{
					messageTxt = Html.fromHtml(messageHTML);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);

				builder.setTitle(title).setMessage(messageTxt)
				/*setView(tvMessage)*/.setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id)
					{
						if (dialogCallback != null)
							dialogCallback.dialogFinished(DialogCallback.STATUS_OK);
						dismissDialogs();

						lastMessage = MESSAGE_HIDE_DIALOG;
					}
				});

				if (negativeButton != null)
				{
					builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
							if (dialogCallback != null)
								dialogCallback.dialogFinished(DialogCallback.STATUS_CANCEL);
							dismissDialogs();

							lastMessage = MESSAGE_HIDE_DIALOG;
						}
					});
				}

				dialog = builder.create();
				dialog.show();
				dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event)
					{
						if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
				});
				((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
				((TextView) dialog.findViewById(android.R.id.message)).setAutoLinkMask(Linkify.ALL);
				((TextView) dialog.findViewById(android.R.id.message)).setLinksClickable(true);
				break;
			}
			case MESSAGE_SHOW_PROGRESS:
			{
				String message = lastBundle.getString(SHOW_MESSAGE);
				if (dialog != null && dialog instanceof ProgressDialog)
				{
					((ProgressDialog) dialog).setMessage(message);
				}
				else
				{
					dismissDialogs();

					dialog = ProgressDialog.show(activity, null, message);
					dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent event)
						{
							if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)
							{
								return true;
							}
							else
							{
								return false;
							}
						}
					});

				}

				break;
			}
			case MESSAGE_HIDE_DIALOG:
				dismissDialogs();
				//						if (!isShowing())
				//							show();
				break;
		}
	}

	protected Resource getResource()
	{
		return logic.getResource();
	}

	private InAppDialog inAppDialog;

	public boolean resume(final Activity activity)
	{

		//boolean ret = this.type != TYPE_NO_ACTION;

		if (inAppDialog != null)
		{
			//			callback.error(InAppBillingCallback.ERROR_TRY_RESUME_NOT_PAUSED);
			//			return false;
			pause();
		}

		this.activity = activity;

		if (started)
		{
			final InAppBillingController controller = this;
			this.activity.runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					inAppDialog = new InAppDialog(activity, controller);
					logic.setViewAndHandler(inAppDialog.getView(), controller);
					inAppDialog.show();

					if (lastMessage != MESSAGE_HIDE_DIALOG)
					{
						Message msg = new Message();
						msg.what = lastMessage;
						if (lastBundle != null)
							msg.setData(lastBundle);
						//messageHandler.handleMessage(msg);
						handleMessageHandlerMessage(msg);
					}

					logic.resume();
				}
			});
		}
		return started;

	}

	private void end(boolean dismiss)
	{
		if (!started && dismiss)
		{
			if (inAppDialog != null && inAppDialog.isShowing() && !wrapper)
			{
				dismissInAppDialog();
				hideDialog();
			}
		}
		else
			started = true;
	}

	// on back button 
	public void cancel()
	{
		started = false;
		if (!wrapper)
		{
			dismissDialogs();
			dismissInAppDialog();
		}

		logic.triggerExitEvent();

	}

	public void pause()
	{
		dismissDialogs();
		dismissInAppDialog();
		//triggerPauseEvent = true;
		if (started)
			logic.pause();
	}

	protected void free()
	{
		logic.triggerExitEvent();

		dismissDialogs();
		dismissInAppDialog();
		this.logic.free();
		this.logic = null;
	}

	private void dismissInAppDialog()
	{
		if (this.inAppDialog != null)
		{
			try
			{
				this.inAppDialog.dismiss();
			}
			catch (IllegalArgumentException ex)
			{

			}

			this.inAppDialog = null;
		}
	}

	public void callWebBrowser(String url)
	{
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		activity.startActivity(browserIntent);
	}

	public Activity getActiviy()
	{
		return this.activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
}
