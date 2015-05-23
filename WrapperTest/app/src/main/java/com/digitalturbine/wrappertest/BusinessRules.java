package com.digitalturbine.wrappertest;

public class BusinessRules
{
	private boolean businessRulesTBYBfinished;
	private int businessRulesNumOfStartups;
	private int businessRulesNumOfUsedStartups;
	private long businessRulesNewStartupTime;
	private long businessRulesPlayTime;
	private long businessRulesNextSubscriptionCheckTime;
	private long businessRulesLastStartTime;
	private long businessRulesLastSaveTime;
	private boolean businessRulesStartMessageShown;
	private boolean businessRulesIsNewStart;
	private boolean businessRulesMenageSubsriptionDialogShow;

	public BusinessRules(boolean businessRulesTBYBfinished, long businessRulesLastStartTime, long businessRulesLastSaveTime, boolean businessRulesStartMessageShown, boolean businessRulesIsNewStart, boolean businessRulesMenageSubsriptionDialogShow)
	{
		this.businessRulesTBYBfinished = businessRulesTBYBfinished;
		this.businessRulesLastStartTime = businessRulesLastStartTime;
		this.businessRulesLastSaveTime = businessRulesLastSaveTime;
		this.businessRulesStartMessageShown = businessRulesStartMessageShown;
		this.businessRulesIsNewStart = businessRulesIsNewStart;
		this.businessRulesMenageSubsriptionDialogShow = businessRulesMenageSubsriptionDialogShow;
	}

	public boolean isBusinessRulesTBYBfinished()
	{
		return businessRulesTBYBfinished;
	}

	public void setBusinessRulesTBYBfinished(boolean businessRulesTBYBfinished)
	{
		this.businessRulesTBYBfinished = businessRulesTBYBfinished;
	}

	public int getBusinessRulesNumOfStartups()
	{
		return businessRulesNumOfStartups;
	}

	public void setBusinessRulesNumOfStartups(int businessRulesNumOfStartups)
	{
		this.businessRulesNumOfStartups = businessRulesNumOfStartups;
	}

	public int getBusinessRulesNumOfUsedStartups()
	{
		return businessRulesNumOfUsedStartups;
	}

	public void setBusinessRulesNumOfUsedStartups(int businessRulesNumOfUsedStartups)
	{
		this.businessRulesNumOfUsedStartups = businessRulesNumOfUsedStartups;
	}

	public long getBusinessRulesNewStartupTime()
	{
		return businessRulesNewStartupTime;
	}

	public void setBusinessRulesNewStartupTime(long businessRulesNewStartupTime)
	{
		this.businessRulesNewStartupTime = businessRulesNewStartupTime;
	}

	public long getBusinessRulesPlayTime()
	{
		return businessRulesPlayTime;
	}

	public void setBusinessRulesPlayTime(long businessRulesPlayTime)
	{
		this.businessRulesPlayTime = businessRulesPlayTime;
	}

	public long getBusinessRulesNextSubscriptionCheckTime()
	{
		return businessRulesNextSubscriptionCheckTime;
	}

	public void setBusinessRulesNextSubscriptionCheckTime(long businessRulesNextSubscriptionCheckTime)
	{
		this.businessRulesNextSubscriptionCheckTime = businessRulesNextSubscriptionCheckTime;
	}

	public long getBusinessRulesLastStartTime()
	{
		return businessRulesLastStartTime;
	}

	public void setBusinessRulesLastStartTime(long businessRulesLastStartTime)
	{
		this.businessRulesLastStartTime = businessRulesLastStartTime;
	}

	public long getBusinessRulesLastSaveTime()
	{
		return businessRulesLastSaveTime;
	}

	public void setBusinessRulesLastSaveTime(long businessRulesLastSaveTime)
	{
		this.businessRulesLastSaveTime = businessRulesLastSaveTime;
	}

	public boolean isBusinessRulesStartMessageShown()
	{
		return businessRulesStartMessageShown;
	}

	public void setBusinessRulesStartMessageShown(boolean businessRulesStartMessageShown)
	{
		this.businessRulesStartMessageShown = businessRulesStartMessageShown;
	}

	public boolean isBusinessRulesIsNewStart()
	{
		return businessRulesIsNewStart;
	}

	public void setBusinessRulesIsNewStart(boolean businessRulesIsNewStart)
	{
		this.businessRulesIsNewStart = businessRulesIsNewStart;
	}

	public boolean isBusinessRulesMenageSubsriptionDialogShow()
	{
		return businessRulesMenageSubsriptionDialogShow;
	}

	public void setBusinessRulesMenageSubsriptionDialogShow(boolean businessRulesMenageSubsriptionDialogShow)
	{
		this.businessRulesMenageSubsriptionDialogShow = businessRulesMenageSubsriptionDialogShow;
	}

	@Override
	public String toString()
	{
		return "BusinessRules [businessRulesTBYBfinished=" + businessRulesTBYBfinished + ", businessRulesNumOfStartups=" + businessRulesNumOfStartups + ", businessRulesNumOfUsedStartups=" + businessRulesNumOfUsedStartups
				+ ", businessRulesNewStartupTime=" + businessRulesNewStartupTime + ", businessRulesPlayTime=" + businessRulesPlayTime + ", businessRulesNextSubscriptionCheckTime=" + businessRulesNextSubscriptionCheckTime
				+ ", businessRulesLastStartTime=" + businessRulesLastStartTime + ", businessRulesLastSaveTime=" + businessRulesLastSaveTime + ", businessRulesStartMessageShown=" + businessRulesStartMessageShown + ", businessRulesIsNewStart="
				+ businessRulesIsNewStart + ", businessRulesMenageSubsriptionDialogShow=" + businessRulesMenageSubsriptionDialogShow + "]";
	}

}
