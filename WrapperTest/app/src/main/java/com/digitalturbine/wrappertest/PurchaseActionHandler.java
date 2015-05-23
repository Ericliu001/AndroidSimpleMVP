package com.digitalturbine.wrappertest;

import android.os.Handler;

/**
 * Handles the new purchase action command, send by the new buttons. It passes an object of the type {@link PurchaseOption} 
 * rather than a string which must then be parsed later on.
 * @author carndt
 *
 */
public interface PurchaseActionHandler
{
	/**
	 * Handles the execution of a purchase action.
	 * @param option the selected purchase option
	 * @param sender the sender (button)
	 * @param handler the handler
	 */
	public void handlePurchaseAction(PurchaseOption option, Object sender, Handler handler);
}
