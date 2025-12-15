package com.ws.msp.service;

import java.util.List;
import java.util.Map;

import com.ws.hibernate.GenericDataManager;

public interface QuotaManager extends GenericDataManager {
	public static final String GLOBAL_SUBMIT_LIMIT = "GLOBAL_SUBMIT";
	public static final String CP_SUBMIT_LIMIT = "CP_SUBMIT@";
	public static final String HOUR_QUERY_LIMIT = "CP_HOUR_QUERY@";
	public static final String MINUTE_QUERY_LIMIT = "CP_MINUTE_QUERY@";

	public void init();
	
	public boolean checkSubmitLimit(String cpId, long tokenCount);
	public boolean checkQueryLimit(String cpId, long tokenCount);
	
	public boolean processSmsSubmit(String cpId, long tokenCount);
	public boolean processQueryDr(String cpId, long tokenCount);
	
	public void resetCpSubmitLimitCount();
	public void resetCpSubmitLimitCount(String cpId);
	
	public void resetGlobalSmsLimitCount();
	
	public void resetQueryDrHourlyCount();
	public void resetQueryDrHourlyCount(String cpId);
	
	public void resetQueryDrMinutelyCount();
	public void resetQueryDrMinutelyCount(String cpId);
	
	public void resetQueryLimit();
	public void resetSubmitLimit();
	
	public void resetAll();
	
	// get
	public Map<String, Long> getCpSubmitLimitCount();
	public long getCpSubmitLimitCount(String cpId);
	
	public long getGlobalSmsLimitCount();
	
	public Map<String, Long> getQueryDrHourlyCount();
	public long getQueryDrHourlyCount(String cpId);
	
	public Map<String, Long> getQueryDrMinutelyCount();
	public long getQueryDrMinutelyCount(String cpId);

	public Map<String, List<Long>> getQueryDrCount();

}
