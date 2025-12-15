package com.ws.msp.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ParameterMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.CannotCreateTransactionException;

import com.ws.api.util.HttpApiUtils;
import com.ws.hibernate.GenericDaoImpl;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.msp.pojo.SmsRecordSub;


@Repository
public class SmsRecordSubDaoImpl extends GenericDaoImpl<SmsRecordSub,String> implements SmsRecordSubDao{

	static final Logger logger = LogManager.getLogger(SmsRecordSubDaoImpl.class);
	
	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}

	@Override
	public long getSubId(){
		long id= 0;
		try{
			String sql = "pkg_com_sms_utl.get_sms_record_sub_seq";
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, Long.class, ParameterMode.OUT);
			call.registerParameter(2, String.class, ParameterMode.OUT);
			call.registerParameter(3, Long.class, ParameterMode.OUT);
	
			id = (long) call.getOutputs().getOutputParameterValue(1);
//			Session session = super.getSessionFactory().getCurrentSession();
//			SQLQuery sqlQuery = session.createSQLQuery("select SEQ_SMS_RECORD_SUB.NEXTVAL from dual");
//			Object result = sqlQuery.uniqueResult();
//			if (result instanceof BigDecimal) {
//                id = ((BigDecimal)result).longValue();
//			}
		}catch(CannotCreateTransactionException e){
			logger.error("getSubId error:[{}]", e.getMessage());
			logger.error(e, e);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {

			}
			id = Long.parseLong(HttpApiUtils.formatDate("hhmmssSSS", new Date()));
		}catch(Exception e){
			logger.error("[DB] getSubId error:[{}]", e.getMessage());
			logger.error(e, e);
		}
		return id;
	}
	
	@Override
	public String saveSmsRecordSub(String wsMsgId, String smscMsgId, String submitStatus) {
		logger.debug("==== saveSmsRecordSub start, wsMsgId:[{}] , smscMsgId:[{}]",wsMsgId,smscMsgId);
		String result= "9999";
		long resultCode = 9999;
		String sql = "pkg_msp_sms_adm.create_sms_record_sub";
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(wsMsgId);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(smscMsgId);
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(submitStatus);
			call.registerParameter(4, String.class, ParameterMode.OUT);
			call.registerParameter(5, Long.class, ParameterMode.OUT);
			ProcedureOutputs outputs = call.getOutputs();
			resultCode = Long.valueOf(outputs.getOutputParameterValue(5).toString());
			if(resultCode == 0){
				result = "0";//outputs.getOutputParameterValue(5).toString();
				logger.debug("==== saveSmsRecordSub Success, resultCode:[{}] ,wsMsgId:[{}] , smscMsgId:[{}]", resultCode,wsMsgId,smscMsgId);
			}
			else{
				String errorMsg = outputs.getOutputParameterValue(4).toString();
				resultCode = Long.valueOf(outputs.getOutputParameterValue(5).toString());
				logger.error("saveSmsRecordSub failed, resultCode:[{}], errorMsg:[{}]", resultCode, errorMsg);
			}
			
		}catch(Exception e){
			logger.error("[DB] saveSmsRecordSub error:[{}]", e.getMessage());
		}
		return result;
	}
	
	@Override
	public Map<String,String> updateDrSmsRecord(String smscMsgId, String deliverStatus) {
		Map<String,String> map = null;
		long resultCode = 9999;
		String sql = "pkg_msp_sms_adm.update_dr_sms_record";
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(smscMsgId);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(deliverStatus);
			call.registerParameter(3, String.class, ParameterMode.OUT);
			call.registerParameter(4, String.class, ParameterMode.OUT);
			call.registerParameter(5, String.class, ParameterMode.OUT);
			call.registerParameter(6, String.class, ParameterMode.OUT);
			call.registerParameter(7, String.class, ParameterMode.OUT);
			call.registerParameter(8, String.class, ParameterMode.OUT);
			call.registerParameter(9, Long.class, ParameterMode.OUT);
			
			ProcedureOutputs outputs = call.getOutputs();
			resultCode = Long.valueOf(outputs.getOutputParameterValue(9).toString());
			if(resultCode == 0){
				map = new HashMap<String,String>();
				map.put("wsMsgId", outputs.getOutputParameterValue(3).toString());
				map.put("cpId", outputs.getOutputParameterValue(4).toString());
				map.put("drFlag", outputs.getOutputParameterValue(5).toString());
				map.put("cpZone", outputs.getOutputParameterValue(6).toString());
				map.put("reqMsgId", outputs.getOutputParameterValue(7).toString());
				map.put("resultCode", "0");
			}
			else{
				map = new HashMap<String,String>();
				String errorMsg = outputs.getOutputParameterValue(8).toString();
				resultCode = Long.valueOf(outputs.getOutputParameterValue(9).toString());
				map.put("resultCode", outputs.getOutputParameterValue(9).toString());
				map.put("errorMsg", errorMsg);
				logger.warn("updateDrSmsRecord failed, resultCode:[{}], errorMsg:[{}],smscMsgId:[{}],deliverStatus:[{}]"
						, resultCode, errorMsg,smscMsgId,deliverStatus);
			}
			
		}catch(Exception e){
			logger.error("[DB] updateDrSmsRecord error:[{}]", e.getMessage());
			logger.error(e,e);
			throw new DataAccessException(e);
		}
		return map;
	}
	
	@Override
	public void batchSave(List<SmsRecordSub> list) {
		
		//Transaction tx = null;
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			//tx = session.beginTransaction();
			int i=1;
			for(SmsRecordSub sub:list){
				session.saveOrUpdate(sub);
				i++;
				if(i==50){
					session.flush();
			        session.clear();
			        i = 1;
				}
			}
	       // tx.commit();

		}catch(Exception e){
			logger.error("[DB] batchSave error:[{}]", e.getMessage());
			throw new DataAccessException(e);
		}
	}
}
