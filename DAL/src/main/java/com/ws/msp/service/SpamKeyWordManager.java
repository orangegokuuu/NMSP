package com.ws.msp.service;

import java.util.List;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.SpamKeyWord;

public interface SpamKeyWordManager extends GenericDataManager{

	public boolean checkSpamKeyWordInCache(String keyword);
	
	public void cacheSpamKeyWord();
	
	public void addAndSave(SpamKeyWord sKey);
	
	public void clearCacheSpamKeyWord();
	
	public List<SpamKeyWord> getCacheSpamKeyWordList();
}
