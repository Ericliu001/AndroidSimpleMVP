package com.digitalturbine.wrappertest;

import java.util.Hashtable;

/**
 * POJO which saves a purchase option send by the server.
 * @author carndt
 *
 */
public class PurchaseOption
{
	//	private static final String LOG_TAG = "LOGIC";
	private String serviceId = "";
	private String purchaseDescription;
	private String storename;
	private String storeurl;
	private String token;
	private String keyword;
	private String shortcode;
	private String terms;
	private String description;
	private String price;
	private int purchaseType = PurchaseType.PURCHASE_TYPE_NONE;

	protected String getServiceId()
	{
		return serviceId;
	}

	protected void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}

	protected String getPurchaseDescription()
	{
		return purchaseDescription;
	}

	protected void setPurchaseDescription(String purchaseDescription)
	{
		this.purchaseDescription = purchaseDescription;
	}

	protected String getStorename()
	{
		return storename;
	}

	protected void setStorename(String storename)
	{
		this.storename = storename;
	}

	protected String getStoreurl()
	{
		return storeurl;
	}

	protected void setStoreurl(String storeurl)
	{
		this.storeurl = storeurl;
	}

	protected String getToken()
	{
		return token;
	}

	protected void setToken(String token)
	{
		this.token = token;
	}

	protected String getKeyword()
	{
		return keyword;
	}

	protected void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	protected String getShortcode()
	{
		return shortcode;
	}

	protected void setShortcode(String shortcode)
	{
		this.shortcode = shortcode;
	}

	protected String getTerms()
	{
		return terms;
	}

	protected void setTerms(String terms)
	{
		this.terms = terms;
	}

	protected String getDescription()
	{
		return description;
	}

	protected void setDescription(String description)
	{
		this.description = description;
	}

	protected String getPrice()
	{
		return price;
	}

	protected void setPrice(String price)
	{
		this.price = price;
	}

	protected int getPurchaseType()
	{
		return purchaseType;
	}

	protected void setPurchaseType(int purchaseType)
	{
		this.purchaseType = purchaseType;
	}

	public PurchaseOption(Hashtable hashtable, int optionNumber)
	{
		String suffix = "";
		if (optionNumber > 1)
		{
			suffix = suffix + optionNumber;
		}

		//		Log.d(LOG_TAG, "suffix=" + suffix);

		String pt = (String) hashtable.get("purchase-type" + suffix);
		if (pt != null)
		{
			this.purchaseType = Integer.parseInt(pt);
		}
		this.price = (String) hashtable.get("price" + suffix);
		this.description = (String) hashtable.get("description" + suffix);
		this.terms = (String) hashtable.get("terms" + suffix);
		this.shortcode = (String) hashtable.get("shortcode" + suffix);
		this.keyword = (String) hashtable.get("keyword" + suffix);
		this.token = (String) hashtable.get("tok" + suffix);
		this.storeurl = (String) hashtable.get("storeurl" + suffix);
		this.storename = (String) hashtable.get("storename" + suffix);
		this.purchaseDescription = (String) hashtable.get("purchase-description" + suffix);
		this.serviceId = (String) hashtable.get("service-id" + suffix);
	}

	@Override
	public String toString()
	{
		return "PurchaseOption [serviceId=" + serviceId + ", price=" + price + ", token=" + token + "]";
	}

}
