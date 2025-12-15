package com.ws.msp.service;

import java.util.List;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpSourceAddress;
public interface ContentProviderManager extends GenericDataManager {

	public List<CpSourceAddress> listCpSa(String cpId);
	public boolean checkTimetableLinkage(String timetableId);
	public List<ContentProvider> getAllMQCP();
	public ContentProvider updateSmsLimit(String id, int smsLimit);
	public List<ContentProvider> getMQCPList(int mqManager);
	
}
