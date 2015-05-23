package com.digitalturbine.wrappertest;

public enum BillingMethod
{
	DIRECT, MO_MT;

	public static BillingMethod fromString(String value)
	{
		BillingMethod result = MO_MT;
		if ("direct".equals(value))
		{
			result = DIRECT;
		}
		return result;
	}
}
