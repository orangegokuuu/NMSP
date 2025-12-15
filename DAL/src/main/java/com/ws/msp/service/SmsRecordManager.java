package com.ws.msp.service;

import java.util.List;
import java.util.Map;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;

public interface SmsRecordManager extends GenericDataManager{

	public String getSeq();
	
	public String getWsMsgId();
	
	public Map<String,List<String>> batchSave(List<SmsRecord> list);
	
	public void batchSave2(List<Object> list);
	
	public String saveSmsRecordSub(String wsMsgId,String smscMsgId,String submitStatus);
	
	public Map<String,String> saveMoSmsRecord(String oa,String da,String status,String language,String text,int source_ton
			,int source_npi,int dest_ton,int dest_npi,int esmClass,String smscMsgId);

	public Map<String,String> updateDrSmsRecord(String smscMsgId, String deliverStatus);
	
	public long getSubId();
	
	public void subBatchSave(List<SmsRecordSub> list);
}
