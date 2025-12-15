package com.ws.msp.dao;

import java.util.List;
import java.util.Map;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.SmsRecord;

public interface SmsRecordDao extends GenericDao<SmsRecord,String>{

	public String getSeq();
	
	public String getWsMsgId();
	
	public Map<String,List<String>> batchSave(List<SmsRecord> list);
	
	public void batchSave2(List<Object> list);
	
	public Map<String, String> saveMoSmsRecord(String oa, String da, String status, String language, String text,
			int source_ton, int source_npi, int dest_ton, int dest_npi, int esmClass, String smscMsgId,String cpId);
}
