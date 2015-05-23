package com.digitalturbine.wrappertest;

public class PurchaseType
{
	/** TBYB fake purchase type */
	public static final int PURCHASE_TYPE_TBYB = -1;
	/** Access for limited. */
	public static final int PURCHASE_TYPE_TIME_LIMIT = 1;
	/** Given no type there will be an error shown on purchaseinfo. */
	public static final int PURCHASE_TYPE_NONE = 0;
	/** Customer can buy article one time, that means already-bought will be checked. */
	public static final int PURCHASE_TYPE_FULL = 1;
	/** Customer can buy article as often as he wants. */
	public static final int PURCHASE_TYPE_FULL_CONSUMABLE = 2;
	/** . */
	public static final int PURCHASE_TYPE_RENTAL = 3;

	public static final int PURCHASE_TYPE_SUBSCRIPTION = 4;

}