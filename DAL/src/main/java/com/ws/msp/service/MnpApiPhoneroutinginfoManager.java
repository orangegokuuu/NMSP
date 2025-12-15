package com.ws.msp.service;

import java.util.List;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;


public interface MnpApiPhoneroutinginfoManager extends GenericDataManager{

	public boolean checkMnpInCache(String da);
	
	public void cacheMnpApiPhoneroutinginfo();
	
	public void clearCacheMnpApiPhoneroutinginfo();
	
	public void addAndSave(MnpApiPhoneroutinginfo mnp);
	
	public void updateCacheAndDb(MnpApiPhoneroutinginfo mnp);
	
	public void deleteCacheAndDb(MnpApiPhoneroutinginfo mnp);
	
	public void mergeCacheAndDb(MnpApiPhoneroutinginfo mnp);
	
	public MnpApiPhoneroutinginfo getFromCache(String da);
	
	public void batchSave(List<MnpApiPhoneroutinginfo> list);
}
