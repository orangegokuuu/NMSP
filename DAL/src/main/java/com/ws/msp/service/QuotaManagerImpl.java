package com.ws.msp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.ws.core.service.SystemServiceManager;
import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.msp.pojo.ContentProvider;
import com.ws.sac.constant.SacConstant;

import lombok.extern.log4j.Log4j2;

@Service(value = "quotaManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class QuotaManagerImpl extends GenericDataManagerImpl implements QuotaManager {

	@Autowired
	private ContentProviderManager cpMan = null;

	@Autowired
	private SystemServiceManager sysMan = null;

	@Autowired
	private HazelcastInstance cacheClient;

	private IAtomicLong globalSmsLimit;

	private IAtomicLong getGlobalSubmitLimit() {
		// return cacheClient.getCPSubsystem().getAtomicLong(GLOBAL_SUBMIT_LIMIT);
		return cacheClient.getAtomicLong(GLOBAL_SUBMIT_LIMIT);
	}

	private IAtomicLong getCpSubmitLimit(String cpId) {
		// return cacheClient.getCPSubsystem().getAtomicLong(CP_SUBMIT_LIMIT + cpId);
		return cacheClient.getAtomicLong(CP_SUBMIT_LIMIT + cpId);
	}

	private IAtomicLong getHrQueryLimit(String cpId) {
		// return cacheClient.getCPSubsystem().getAtomicLong(HOUR_QUERY_LIMIT + cpId);
		return cacheClient.getAtomicLong(HOUR_QUERY_LIMIT + cpId);
	}

	private IAtomicLong getMinQueryLimit(String cpId) {
		// return cacheClient.getCPSubsystem().getAtomicLong(MINUTE_QUERY_LIMIT + cpId);
		return cacheClient.getAtomicLong(MINUTE_QUERY_LIMIT + cpId);
	}

	@PostConstruct
	@Override
	public void init() {
		globalSmsLimit = getGlobalSubmitLimit();
	}

	@Override
	public synchronized boolean checkSubmitLimit(String cpId, long tokenCount) {

		// get counter
		IAtomicLong smsQuota = getCpSubmitLimit(cpId);
		IAtomicLong smsGlobalQuota = getGlobalSubmitLimit();

		if (smsQuota.get() < 0 && smsGlobalQuota.get() < 0) {
			log.debug("[Quota] Cp sms limit disabled && Global sms limit disabled");
			return true;
		}


//		if (smsGlobalQuota.get() < 0) {
//			log.debug("[Quota] Global sms limit disabled");
//			return true;
//		}

		log.info("Current smsQuota = {}", smsQuota.get());
		if (tokenCount > smsQuota.get() && (smsQuota.get() != -1)) {
			log.debug("[Quota] Requested amount of token [{}] exceeds cp limit [{}]", tokenCount, smsQuota.get());
			return false;
		}
		log.info("Current smsGlobalQuota = {}", smsGlobalQuota.get());
		if (tokenCount > smsGlobalQuota.get() && (smsGlobalQuota.get() != -1)) {
			log.debug("[Quota] Requested amount of token [{}] exceeds global limit [{}]", tokenCount,
					smsGlobalQuota.get());
			return false;
		}

		return true;
	}

	private synchronized void subtractSubmitQuota(String cpId, long tokenCount) {
		IAtomicLong smsLimit = getCpSubmitLimit(cpId);
		IAtomicLong smsGlobalLimit = getGlobalSubmitLimit();

		log.debug("[Quota] cpId : [{}], enqueueCount : [{}]", cpId, tokenCount);
		log.debug("[Quota] (smsLimit.get() != -1) : [{}], CP limit : [{}]", (smsLimit.get() != -1), smsLimit.get());
		// -1 by pass
		if(smsLimit.get() != -1){
			if ((smsLimit.get() - tokenCount) < 0) {
				smsLimit.set(0);
			} else {
				smsLimit.set(smsLimit.get() - tokenCount);
			}
		}
		log.debug("[Quota] (smsGlobalLimit.get() != -1) : [{}], Global limit : [{}]", (smsGlobalLimit.get() != -1), smsGlobalLimit.get());
		// -1 by pass
		if(smsGlobalLimit.get() != -1){
			if ((smsGlobalLimit.get() - tokenCount) < 0) {
				smsGlobalLimit.set(0);
			} else {
				smsGlobalLimit.set(smsGlobalLimit.get() - tokenCount);
			}
		}
		
		log.debug("[Quota] CP limit : [{}], Global limit : [{}]", smsLimit.get(), smsGlobalLimit.get());
	}

	// check and run
	@Override
	public synchronized boolean processSmsSubmit(String cpId, long tokenCount) {
		log.debug("[Process Quota] Count : {}", tokenCount);
		if (checkSubmitLimit(cpId, tokenCount) == true) {
			subtractSubmitQuota(cpId, tokenCount);
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean checkQueryLimit(String cpId, long tokenCount) {
		IAtomicLong hrQueryQuota = getHrQueryLimit(cpId);
		IAtomicLong minQueryQuota = getMinQueryLimit(cpId);
		
//		if (hrQueryQuota.get() < 0) {
//			log.debug("[Quota] Hourly Query limit disabled");
//			return true;
//		}
//
//		if (minQueryQuota.get() < 0) {
//			log.debug("[Quota] Minutely Query limit disabled");
//			return true;
//		}
		
		if (hrQueryQuota.get() < 0 && minQueryQuota.get() < 0) {
			log.debug("[Quota] Hourly Query limit disabled");
			return true;
		}		

		log.info("Current Hourly Query limit = {}", hrQueryQuota.get());
		if (tokenCount > hrQueryQuota.get() && hrQueryQuota.get() >= 0) {
			log.debug("[Quota] Requested amount of token [{}] exceeds hourly query limit [{}]", tokenCount,
					hrQueryQuota.get());
			return false;
		}
		log.info("Current Minutely Query limit = {}", minQueryQuota.get());
		if (tokenCount > minQueryQuota.get() && minQueryQuota.get() >= 0) {
			log.debug("[Quota] Requested amount of token [{}] exceeds minutely query [{}]", tokenCount,
					minQueryQuota.get());
			return false;
		}

		return true;
	}

	private synchronized void subtractQueryQuota(String cpId, long tokenCount) {
		IAtomicLong hrQueryQuota = getHrQueryLimit(cpId);
		IAtomicLong minQueryQuota = getMinQueryLimit(cpId);
		
		// -1 by pass
		if(hrQueryQuota.get() != -1){
			if ((hrQueryQuota.get() - tokenCount) < 0) {
				hrQueryQuota.set(0);
			} else {
				hrQueryQuota.set(hrQueryQuota.get() - tokenCount);
			}
		}
		
		// -1 by pass
		if(minQueryQuota.get() != -1){
			if ((minQueryQuota.get() - tokenCount) < 0) {
				minQueryQuota.set(0);
			} else {
				minQueryQuota.set(minQueryQuota.get() - tokenCount);
			}
		}
		
	}

	// check and run
	@Override
	public synchronized boolean processQueryDr(String cpId, long tokenCount) {
		if (checkQueryLimit(cpId, tokenCount) == true) {
			subtractQueryQuota(cpId, tokenCount);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetCpSubmitLimitCount() {
		log.info("Reset Content Provider hourly SMS limit");
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong smsLimit = getCpSubmitLimit(cp.getCpId());
			smsLimit.set(cp.getSmsLimit());
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetCpSubmitLimitCount(String cpId) {
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		log.info("Reset Content Provider[{}] hourly SMS limit", cpId);
		IAtomicLong smsLimit = getCpSubmitLimit(cp.getCpId());
		smsLimit.set(cp.getSmsLimit());

	}

	@Override
	@Transactional(value = "WiseMC.transactionManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetGlobalSmsLimitCount() {
		log.info("Reset Content Provider Global SMS limit");
		globalSmsLimit.set(sysMan.getLongParam(SacConstant.GLOBAL_HR_SUBMIT_LIMIT));
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetQueryDrHourlyCount() {
		log.info("Reset Query Dr Hourly limit");
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
			log.trace("CP hrQueryQuota = {}", cp.getQueryDrHrLimit());
			hrQueryQuota.set(cp.getQueryDrHrLimit());
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetQueryDrHourlyCount(String cpId) {
		log.info("Reset [{}] Query Dr Hourly limit", cpId);
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
		log.trace("CP hrQueryQuota = {}", cp.getQueryDrHrLimit());
		hrQueryQuota.set(cp.getQueryDrHrLimit());
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetQueryDrMinutelyCount() {
		log.info("Reset Query Dr Minutely limit");
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
			log.trace("CP minQueryQuota = {}", cp.getQueryDrMinLimit());
			minQueryQuota.set(cp.getQueryDrMinLimit());
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void resetQueryDrMinutelyCount(String cpId) {
		log.info("Reset [{}] Query Dr Minutely limit", cpId);
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
		log.trace("CP minQueryQuota = {}", cp.getQueryDrMinLimit());
		minQueryQuota.set(cp.getQueryDrMinLimit());
	}

	@Override
	public void resetSubmitLimit() {
		resetCpSubmitLimitCount();
		resetGlobalSmsLimitCount();
	}

	// better performance
	@Override
	public void resetQueryLimit() {
		log.info("Reset All Query Dr limit");
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
			IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
			hrQueryQuota.set(cp.getQueryDrHrLimit());
			minQueryQuota.set(cp.getQueryDrMinLimit());
		}
	}

	@Override
	public void resetAll() {
		resetSubmitLimit();
		resetQueryLimit();
	}

	@Override
	public Map<String, Long> getCpSubmitLimitCount() {
		Map<String, Long> quotaMap = new HashMap<String, Long>();
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong smsLimit = getCpSubmitLimit(cp.getCpId());
			quotaMap.put(cp.getCpId(), new Long(smsLimit.get()));
		}
		return quotaMap;
	}

	@Override
	public long getCpSubmitLimitCount(String cpId) {
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		IAtomicLong smsLimit = getCpSubmitLimit(cp.getCpId());
		return smsLimit.get();
	}

	@Override
	public long getGlobalSmsLimitCount() {
		return globalSmsLimit.get();
	}

	@Override
	public Map<String, Long> getQueryDrHourlyCount() {
		Map<String, Long> quotaMap = new HashMap<String, Long>();
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
			quotaMap.put(cp.getCpId(), new Long(hrQueryQuota.get()));
		}
		return quotaMap;
	}

	@Override
	public long getQueryDrHourlyCount(String cpId) {
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
		return hrQueryQuota.get();
	}

	@Override
	public Map<String, Long> getQueryDrMinutelyCount() {
		Map<String, Long> quotaMap = new HashMap<String, Long>();
		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
			quotaMap.put(cp.getCpId(), new Long(minQueryQuota.get()));
		}
		return quotaMap;
	}

	@Override
	public long getQueryDrMinutelyCount(String cpId) {
		ContentProvider cp = cpMan.get(ContentProvider.class, cpId);
		IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
		return minQueryQuota.get();
	}

	@Override
	public Map<String, List<Long>> getQueryDrCount() {
		Map<String, List<Long>> quotaMap = new HashMap<String, List<Long>>();

		List<ContentProvider> cps = cpMan.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			IAtomicLong hrQueryQuota = getHrQueryLimit(cp.getCpId());
			IAtomicLong minQueryQuota = getMinQueryLimit(cp.getCpId());
			List<Long> countList = new ArrayList<>();
			countList.add(hrQueryQuota.get());
			countList.add(minQueryQuota.get());
			quotaMap.put(cp.getCpId(), countList);
		}
		return quotaMap;
	}

}
