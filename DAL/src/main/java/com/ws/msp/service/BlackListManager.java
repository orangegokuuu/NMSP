package com.ws.msp.service;

import java.util.List;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.BlackList;

public interface BlackListManager extends GenericDataManager{
	
	public boolean checkBlackListInCache(String da);
	
	public void cacheBlackList();
	
	public void addAndSave(BlackList bl);
	
	public void clearCacheBlackList();
	
	public void batchSave(List<BlackList> list);
	
	public void deleteCacheAndDb(BlackList bl);
}
