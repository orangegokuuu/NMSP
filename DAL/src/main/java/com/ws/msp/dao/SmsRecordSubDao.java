package com.ws.msp.dao;

import java.util.List;
import java.util.Map;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.SmsRecordSub;

public interface SmsRecordSubDao extends GenericDao<SmsRecordSub,String>{

	public String saveSmsRecordSub(String wsMsgId,String smscMsgId,String submitStatus);
	
	public Map<String,String> updateDrSmsRecord(String smscMsgId, String deliverStatus);
	
	public long getSubId();
	
	public void batchSave(List<SmsRecordSub> list);
}
