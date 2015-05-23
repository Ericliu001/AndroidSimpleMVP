/**
 * 
 */
package com.digitalturbine.wrappertest;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * @author dgrzeszczak
 *
 */
class InAppDialog extends Dialog
{

	InAppBillingController controller;
	FrameLayout mainLayout;

	public FrameLayout getView()
	{
		return mainLayout;
	}

	public InAppDialog(final Activity activity, final InAppBillingController controller)
	{
		super(activity, android.R.style.Theme);

		this.setOwnerActivity(activity);
		this.setCancelable(false);
		this.controller = controller;

		this.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
				{
					controller.cancel();
				}
				return false;
			}
		});

		//turn off title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		//TODO Vodafone project will not work 

		//		ScrollView scrollView = new ScrollView(getContext());
		//		scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		//		scrollView.setFillViewport(true);
		//		scrollView.setBackgroundColor(Color.WHITE);

		mainLayout = new FrameLayout(getContext());

		mainLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		//mainLayout.setOrientation(LinearLayout.VERTICAL);

		//	scrollView.addView(mainLayout);

		//	setContentView(scrollView);
		setContentView(mainLayout);
	}

	@Override
	public void dismiss()
	{
		super.dismiss();
	}

	@Override
	public boolean onSearchRequested()
	{
		//disable search button
		return true;
	}

}
