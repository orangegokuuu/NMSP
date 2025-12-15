package com.ws.msp.dao;

import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ParameterMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.msp.pojo.SmsRecord;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;


@Repository
public class SmsRecordDaoImpl extends GenericDaoImpl<SmsRecord,String> implements SmsRecordDao{

	static final Logger logger = LogManager.getLogger(SmsRecordDaoImpl.class);
	
	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}

	@Override
	public String getSeq(){
		String seq= "";
		try{
			String sql = "pkg_com_sms_utl.get_sms_record_seq";
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, String.class, ParameterMode.OUT);
			call.registerParameter(2, String.class, ParameterMode.OUT);
			call.registerParameter(3, Long.class, ParameterMode.OUT);

			seq = call.getOutputs().getOutputParameterValue(1).toString();
		}catch(Exception e){
			logger.error("[DB] get Req_msg_id error:[{}]", e.getMessage());
			logger.error(e, e);
		}
		return seq;
	}
	
	@Override
	public String getWsMsgId(){
		String seq= "";
		try{
			String sql = "pkg_com_sms_utl.get_ws_msg_id_seq";
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, String.class, ParameterMode.OUT);
			call.registerParameter(2, String.class, ParameterMode.OUT);
			call.registerParameter(3, Long.class, ParameterMode.OUT);
	
			seq = call.getOutputs().getOutputParameterValue(1).toString();
		}catch(Exception e){
			logger.error("[DB] get Ws_msg_id error:[{}]", e.getMessage());
			logger.error(e, e);
		}
		return seq;
	}

	@Override
	public Map<String, List<String>> batchSave(List<SmsRecord> list) {
		Map<String, List<String>> map = new HashMap<String,List<String>>();
		//String sql = "call pkg_msp_sms_adm.create_batch_sms_record";
		Session session = super.getSessionFactory().getCurrentSession();
		//Session session = super.getSessionFactory().openSession();
		

		try{
			Connection conn = super.getSessionFactory().getSessionFactoryOptions()
					.getServiceRegistry().getService(ConnectionProvider.class).getConnection();
			//session.beginTransaction();
			//Map<String, List<String>> result = new HashMap<String,List<String>>();
	    	OracleCallableStatement stmt = null;
	    	OracleConnection oralconn = conn.unwrap(OracleConnection.class);
	    	String sysId = list.get(0).getSysId();
			String oa = list.get(0).getOa();
			String language = list.get(0).getLanguage();
			String text = list.get(0).getText();
			int drFlag = list.get(0).isDrFlag()?1:0;
			String smsSourceType = list.get(0).getSmsSourceType();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			String accept_date = sdf.format(list.get(0).getAcceptDate());
			String inter = list.get(0).getIsInter();
			int priorityFl = list.get(0).getPriorityFl();
			String smsType = list.get(0).getSmsType();
			String da[] = new String[list.size()];
			String acceptStatus[] = new String[list.size()];
			String[] isBlackList = new String[list.size()];
			for(int i = 0; i < list.size(); i++){
				SmsRecord sms = list.get(i);
				da[i] = sms.getDa();
				acceptStatus[i] = sms.getAcceptStatus();
				isBlackList[i] = sms.getIsBlacklist();
			}
			ArrayDescriptor desc_da = ArrayDescriptor.createDescriptor("ARR_STR20", oralconn);
			ARRAY array_da = new ARRAY(desc_da, oralconn, da);
			ArrayDescriptor desc_acceptStatus = ArrayDescriptor.createDescriptor("ARR_STR10", oralconn);
			ARRAY array_acceptStatus = new ARRAY(desc_acceptStatus, oralconn, acceptStatus);
			ArrayDescriptor desc_isBlackList = ArrayDescriptor.createDescriptor("ARR_STR10", oralconn);
			ARRAY array_isBlackList = new ARRAY(desc_isBlackList, oralconn, isBlackList);
			
			stmt = (OracleCallableStatement) 
					oralconn.prepareCall(
					"begin pkg_msp_sms_adm.create_batch_sms_record ("
					+"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
					+"); end;");
			stmt.setString (1, sysId);
			stmt.setString (2, oa);
			stmt.setArray (3, array_da);
			stmt.setString (4, language);
			stmt.setString (5, text);
			stmt.setInt (6, drFlag);
			stmt.setString (7, smsSourceType);
			stmt.setString (8, accept_date);
			stmt.setArray (9, array_acceptStatus);
			stmt.setString (10, inter);
			stmt.setArray (11, array_isBlackList);
			stmt.setInt (12, priorityFl);
			stmt.setString (13, smsType);
			stmt.registerOutParameter (14, OracleTypes.ARRAY, "ARR_STR10");
			stmt.registerOutParameter (15, Types.VARCHAR);
			stmt.registerOutParameter (16, Types.VARCHAR);
			stmt.registerOutParameter (17, Types.INTEGER);

			stmt.execute();
			
			List<String> wId = new ArrayList<String>();
			List<String> qId = new ArrayList<String>();
			if (stmt.getLong(17) == 0) {
				String[] wsMsgId = (String[]) stmt.getARRAY(14).getArray();
				for(String s:wsMsgId){
					wId.add(s);
				}
				qId.add(stmt.getString(15));
			}
			else{
				logger.error("batchSave error, rstatus:[{}] errorMsg:[{}]", stmt.getLong(17), stmt.getString(16));
			}
			map.put("wsMsgId", wId);
			map.put("reqMsgId", qId);
			stmt.close();
			oralconn.close();
		}catch(Exception e){
			logger.error("batchSave error:[{}]", e.getMessage());
		}finally{
			//session.close();
		}
		return map;
	}

	@Override
	public void batchSave2(List<Object> list) {
		
		//Transaction tx = null;
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			//tx = session.beginTransaction();
			int i=1;
			for(Object obj:list){
				SmsRecord sms = (SmsRecord)obj;
				session.save(sms);
				i++;
				if(i==50){
					session.flush();
			        session.clear();
			        i = 1;
				}
			}
	        //tx.commit();
		}catch(Exception e){
			//logger.info("batchSave error:[{}]", e.getMessage());
			logger.error("[DB] batchSave error:[{}]", e.getMessage());
			logger.error(e, e);
		}
	}

	@Override
	public Map<String, String> saveMoSmsRecord(String oa, String da, String status, String language, String text,
			int source_ton, int source_npi, int dest_ton, int dest_npi, int esmClass, String smscMsgId,String cpId) {
		Map<String,String> map = null;
		long resultCode = 9999;
		try{
			String result= "";
			String sql = "pkg_msp_sms_adm.create_mo_sms_record";
			Session session = super.getSessionFactory().getCurrentSession();
			ProcedureCall call = session.createStoredProcedureCall(sql);
			call.registerParameter(1, String.class, ParameterMode.IN).bindValue(oa);
			call.registerParameter(2, String.class, ParameterMode.IN).bindValue(da);
			call.registerParameter(3, String.class, ParameterMode.IN).bindValue(status);
			call.registerParameter(4, String.class, ParameterMode.IN).bindValue(language);
			call.registerParameter(5, String.class, ParameterMode.IN).bindValue(text);
			call.registerParameter(6, Integer.class, ParameterMode.IN).bindValue(source_ton);
			call.registerParameter(7, Integer.class, ParameterMode.IN).bindValue(source_npi);
			call.registerParameter(8, Integer.class, ParameterMode.IN).bindValue(dest_ton);
			call.registerParameter(9, Integer.class, ParameterMode.IN).bindValue(dest_npi);
			call.registerParameter(10, Integer.class, ParameterMode.IN).bindValue(esmClass);
			call.registerParameter(11, String.class, ParameterMode.IN).bindValue(smscMsgId);
			call.registerParameter(12, String.class, ParameterMode.IN).bindValue(cpId);
			//call.registerParameter(12, String.class, ParameterMode.OUT);
			call.registerParameter(13, String.class, ParameterMode.OUT);
			call.registerParameter(14, String.class, ParameterMode.OUT);
			call.registerParameter(15, String.class, ParameterMode.OUT);
			call.registerParameter(16, Long.class, ParameterMode.OUT);
			ProcedureOutputs outputs = call.getOutputs();
			resultCode = Long.valueOf(outputs.getOutputParameterValue(16).toString());
			if(resultCode == 0){
				map = new HashMap<String,String>();
				map.put("cpId", cpId);
				map.put("cpZone", outputs.getOutputParameterValue(13).toString());
				map.put("wsMsgId", outputs.getOutputParameterValue(14).toString());
				logger.debug("saveMoSmsRecord cpId:[{}], cpZone:[{}], wsMsgId:[{}]", map.get("cpId"), map.get("cpZone"),map.get("wsMsgId"));
			}
			else{
				String errorMsg = outputs.getOutputParameterValue(15).toString();
				resultCode = Long.valueOf(outputs.getOutputParameterValue(16).toString());
				logger.warn("[RUNTIME] saveMoSmsRecord failed, resultCode:[{}], errorMsg:[{}]. oa:[{}],da:[{}],status:[{}],language:[{}],text:[{}],source_ton:[{}]"
						+ ",source_npi:[{}],dest_ton:[{}],dest_npi:[{}],esmClass:[{}],smscMsgId:[{}]"
						, resultCode, errorMsg, oa,da,status,language,text,source_ton,source_npi,dest_ton,dest_npi,esmClass,smscMsgId);
			}
		}catch(Exception e){
			logger.error("[DB] saveMoSmsRecord error:[{}]. oa:[{}],da:[{}],status:[{}],language:[{}],text:[{}],source_ton:[{}]"
						+ ",source_npi:[{}],dest_ton:[{}],dest_npi:[{}],esmClass:[{}],smscMsgId:[{}]"
						, e.getMessage(), oa,da,status,language,text,source_ton,source_npi,dest_ton,dest_npi,esmClass,smscMsgId);
			//logger.error(e, e);
			throw new DataAccessException(e);
		}
		
		return map;
	}
	
	
}
