package com.ws.msp.service;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.FetPrefix;

public interface FetPrefixManager extends GenericDataManager{
	
	public boolean checkFetPrefixInCache(String da);
	
	public void cacheFetPrefix();
	
	public void addAndSave(FetPrefix prefix);
	
	public void clearCacheFetPrefix();
}
