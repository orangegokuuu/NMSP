package com.ws.msp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.msp.config.MspProperties;
import com.ws.msp.dao.CpDestinationAddressDao;
import com.ws.msp.dao.ContentProviderDao;
import com.ws.msp.dao.SmsRecordDao;
import com.ws.msp.dao.SmsRecordSubDao;
import com.ws.msp.pojo.CpDestinationAddress;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.pojo.ContentProvider;
import com.ws.util.RegexUtil;

import lombok.extern.log4j.Log4j2;


@Service(value = "smsRecordManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class SmsRecordManagerImpl extends GenericDataManagerImpl implements SmsRecordManager{
	
	@Autowired
	private MspProperties prop = null;
	
	@Autowired
	private SmsRecordDao smsRecordDao = null;
	
	@Autowired
	private SmsRecordSubDao smsRecordSubDao = null;
	
	@Autowired
	private ContentProviderDao contentProviderDao = null;
	
	@Autowired
	private CpDestinationAddressDao cpDestAddressDao = null;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getSeq() {
		return smsRecordDao.getSeq();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getWsMsgId() {
		return smsRecordDao.getWsMsgId();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Map<String, List<String>> batchSave(List<SmsRecord> list) {
		// TODO Auto-generated method stub
		return smsRecordDao.batchSave(list);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchSave2(List<Object> list) {
		// TODO Auto-generated method stub
		smsRecordDao.batchSave2(list);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String saveSmsRecordSub(String wsMsgId, String smscMsgId, String submitStatus) {
		// TODO Auto-generated method stub
		return smsRecordSubDao.saveSmsRecordSub(wsMsgId, smscMsgId, submitStatus);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Map<String, String> saveMoSmsRecord(String oa, String da, String status, String language, String text,
			int source_ton, int source_npi, int dest_ton, int dest_npi, int esmClass, String smscMsgId) {
		//XXX modify by matthew 2017-12-22 get Cpid use source address
		long startTime = System.currentTimeMillis();
		String cpId = this.getCpIdFromMoAddress(da);
		if(cpId==null || cpId.equals("")){
			log.info("==== saveMoSmsRecord ,can not find CPID , Address:[{}]", da);
			return null;
		}
		log.debug("====== saveMoSmsRecord get Cp Id time:[{}]",System.currentTimeMillis()-startTime);
		startTime = System.currentTimeMillis();
		Map<String,String> map = null;
//		int retryCount = 0;
//		int retryTime = 180000;
		try{
			map = smsRecordDao.saveMoSmsRecord(oa, da, status, language, text,
					source_ton, source_npi, dest_ton, dest_npi, esmClass, smscMsgId,cpId);
			if(map == null){
				throw new DataAccessException("result is null");
			}
//			while(map==null){
//				retryCount++;
//				log.warn("saveMoSmsRecord sleep {} ms , retry {} times ", retryTime, retryCount);
//				Thread.sleep(retryTime);
//				map = smsRecordDao.saveMoSmsRecord(oa, da, status, language, text,
//						source_ton, source_npi, dest_ton, dest_npi, esmClass, smscMsgId,cpId);
//				if(map!=null){
//					break;
//				}
//			}
		}catch(Exception e){
			log.error("[DB] saveMoSmsRecord error:[{}] ,oa:[{}],da:[{}],status:[{}],language:[{}],text:[{}],source_ton:[{}]"
					+ ",source_npi:[{}],dest_ton:[{}],dest_npi:[{}],esmClass:[{}],smscMsgId:[{}]"
					,e.getMessage(),oa,da,status,language,text,source_ton,source_npi,dest_ton,dest_npi,esmClass,smscMsgId);
			//log.error(e, e);
			
//			while(map==null){
//				retryCount++;
//				log.warn("saveMoSmsRecord sleep {} ms , retry {} times ", retryTime, retryCount);
//				try {
//					Thread.sleep(retryTime);
//				} catch (InterruptedException e1) {
//				}
//				map = smsRecordDao.saveMoSmsRecord(oa, da, status, language, text,
//						source_ton, source_npi, dest_ton, dest_npi, esmClass, smscMsgId,cpId);
//				if(map!=null){
//					break;
//				}
//			}
		}
		log.debug("====== saveMoSmsRecord DB stroed Procedure process time:[{}]",System.currentTimeMillis()-startTime);
		return map;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Map<String,String> updateDrSmsRecord(String smscMsgId, String deliverStatus) {
		Map<String,String> map = null;
//		int retry = 0;
//		int retryTime = prop.getEmg().getRetryTime();
		try{
			map = smsRecordSubDao.updateDrSmsRecord(smscMsgId, deliverStatus);
//			log.info("updateDrSmsRecord map:[{}]", map);
			if(map == null || (map!=null && !map.get("resultCode").equals("0"))){
				if(map!=null){
					throw new DataAccessException(map.get("errorMsg"));
				}
				else{
					throw new DataAccessException("result is null");
				}
				
			}
//			while(true){				
//				map = smsRecordSubDao.updateDrSmsRecord(smscMsgId, deliverStatus);
//				if(map!=null && map.get("resultCode").equals("0")){
//					break;
//				}
//				retry++;
//				log.warn("updateDrSmsRecord retry {} times ,sleep {} ms .smscMsgId:[{}] ",retry,retryTime,smscMsgId);
//				Thread.sleep(retryTime);
//			}
	
		}catch(Exception e){
			log.error("[DB] updateDrSmsRecord error:[{}]",e.getMessage());
			log.error(e, e);
			throw new DataAccessException(e);
//			while(true){
//				retry++;
//				log.warn("updateDrSmsRecord retry {} times ,sleep {} ms .smscMsgId:[{}], ",retry,retryTime,smscMsgId);
//				try {
//					Thread.sleep(retryTime);
//				} catch (InterruptedException e1) {
//				}
//				map = smsRecordSubDao.updateDrSmsRecord(smscMsgId, deliverStatus);
//				if(map!=null && map.get("resultCode").equals("0")){
//					break;
//				}
//			}
		}
		return map;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public long getSubId() {
		// TODO Auto-generated method stub
		return smsRecordSubDao.getSubId();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void subBatchSave(List<SmsRecordSub> list) {
		// TODO Auto-generated method stub
		try{
			smsRecordSubDao.batchSave(list);
		}catch(DataAccessException e){
			log.error("[DB] batch save sub record failed.. message:[{}]", e.getMessage());
			log.error(e, e);
			throw e; 
		}
	}

	private String getCpIdFromMoAddress(String da){
		String cpId = "";
		//Completely aligned
		DetachedCriteria criteria = cpDestAddressDao.createDetachedCriteria();
		criteria.add(Restrictions.sqlRestriction("regexp_like(?,DEST_ADDRESS)", da, StringType.INSTANCE));
		List<CpDestinationAddress> sourceList = cpDestAddressDao.findByCriteria(criteria);
		List<String> regexpList = new ArrayList<String>();
		for(CpDestinationAddress cpDest:sourceList){
			regexpList.add(cpDest.getDestinationAddress());
		}
		if(regexpList.size() > 0){
			String regexp = RegexUtil.longestMatch(regexpList, da);
			for(CpDestinationAddress cpDest:sourceList){
				if(cpDest.getDestinationAddress().equals(regexp)){
					String tempCpId = cpDest.getCpId();
					//new add by matthew 2018-09-17
					ContentProvider cp = contentProviderDao.get(tempCpId);
					log.info("[MO] getCpIdFromMoAddress(), cpId:[{}] ,status:[{}]", tempCpId, cp.getStatus());
					if(cp!=null && cp.STATUS_ACTIVE.equals(cp.getStatus())){
						cpId = cpDest.getCpId();
						break;
					}
				}
			}
		}
		return cpId;
	}
	
}
