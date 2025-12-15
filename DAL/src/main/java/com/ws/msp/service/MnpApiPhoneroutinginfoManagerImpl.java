package com.ws.msp.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.msp.config.MspProperties;
import com.ws.msp.dao.CacheStatusDao;
import com.ws.msp.dao.MnpApiPhoneroutinginfoDao;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

import lombok.extern.log4j.Log4j2;


@Service(value = "mnpApiPhoneroutinginfoManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class MnpApiPhoneroutinginfoManagerImpl extends GenericDataManagerImpl implements MnpApiPhoneroutinginfoManager {
	@Autowired
	private MspProperties prop = null;

	@Autowired
	private MnpApiPhoneroutinginfoDao mnpApiPhoneroutinginfoDao = null;

	@Autowired
	private CacheStatusDao cacheStatusDao = null;

	@Autowired
	private HazelcastInstance cacheClient;

	public void setcacheClient(HazelcastInstance cacheClient) {
		this.cacheClient = cacheClient;
	}

	private IMap<String, MnpApiPhoneroutinginfo> getCachedMap() {
		return cacheClient.getMap(MnpApiPhoneroutinginfo.class.getName());
	}

	private void fillCache(PaginationResult<MnpApiPhoneroutinginfo> result) {
		IMap<String, MnpApiPhoneroutinginfo> mnpMap = getCachedMap();
		log.debug("Populate Page[{}/{}] with [{}]records to MnpApiPhoneroutinginfo cache", result.getCurrPage(),
				result.getTotalPage(), result.getData().size());
		Map<String, MnpApiPhoneroutinginfo> slice = result.getData().stream()
				.collect(Collectors.toMap(MnpApiPhoneroutinginfo::getPhoneNumber, e -> e));

		mnpMap.putAll(slice);
	}

	@Override
	@Transactional
	public boolean checkMnpInCache(String da) {

		// Map<String,MnpApiPhoneroutinginfo> map = null;
		boolean checkInCache = false;
		try {
			// get boolean from cache , if data load done is true else false
			if (cacheStatusDao.isReady(MnpApiPhoneroutinginfo.class.getName())) {
				checkInCache = cacheClient.getMap(MnpApiPhoneroutinginfo.class.getName()).containsKey(da);
			} else {
				// data not load done, select data from db
				MnpApiPhoneroutinginfo info = mnpApiPhoneroutinginfoDao.get(da);
				if (info != null && info.getPhoneNumber().equals(da)) {
					checkInCache = true;
				}
				log.info("checkMnpInCache data not load done, select from db. info:[{}] , checkInCache:[{}]", info,
						checkInCache);
			}
			return checkInCache;
		} catch (Exception e) {
			// data not load done or exception, select data from db
			log.error("checkMnpInCache error:[{}]", e.getMessage());
			MnpApiPhoneroutinginfo info = mnpApiPhoneroutinginfoDao.get(da);
			if (info != null && info.getPhoneNumber().equals(da)) {
				checkInCache = true;
			}
			log.info("checkMnpInCache error, select from db. info:[{}] , checkInCache:[{}]", info, checkInCache);
			return checkInCache;
		}

	}

	@Override
	@Transactional
	public void cacheMnpApiPhoneroutinginfo() {
		log.debug("Fill Up Cache using properties [{}]", prop.getDal());
		SearchablePaging page = new SearchablePaging();
		page.setPageSize(prop.getDal().getCache().getLoadBatchSize());
		int totalPage = 0;
		PaginationResult<MnpApiPhoneroutinginfo> result = this.page(MnpApiPhoneroutinginfo.class, page);
		totalPage = result.getTotalPage();
		log.debug("Total MnpApiPhoneroutinginfo[{}] Pages[{}]", result.getTotalSize(), result.getTotalPage());
		if (totalPage > 0) {
			fillCache(result);
			while ((result.getNextPage() != 0) && (result.getNextPage() <= result.getTotalPage())) {
				page.setPageNum(result.getNextPage());
				result = this.page(MnpApiPhoneroutinginfo.class, page);
				fillCache(result);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void clearCacheMnpApiPhoneroutinginfo() {
		getCachedMap().clear();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addAndSave(MnpApiPhoneroutinginfo mnp) {
		this.saveOrUpdate(MnpApiPhoneroutinginfo.class, mnp);
		getCachedMap().put(mnp.getPhoneNumber(), mnp);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateCacheAndDb(MnpApiPhoneroutinginfo mnp) {
		this.update(MnpApiPhoneroutinginfo.class, mnp);
		getCachedMap().put(mnp.getPhoneNumber(), mnp);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteCacheAndDb(MnpApiPhoneroutinginfo mnp) {
		mnp = this.get(MnpApiPhoneroutinginfo.class, mnp.getPhoneNumber());
		if(mnp!=null){
			log.info("delete mnp,phoneNumber:[{}]",mnp.getPhoneNumber());
			getCachedMap().remove(mnp.getPhoneNumber());
			this.delete(MnpApiPhoneroutinginfo.class, mnp.getPhoneNumber());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void mergeCacheAndDb(MnpApiPhoneroutinginfo mnp) {
		this.merge(MnpApiPhoneroutinginfo.class, mnp);
		getCachedMap().put(mnp.getPhoneNumber(), mnp);
	}

	@Override
	@Transactional
	public MnpApiPhoneroutinginfo getFromCache(String da) {
		MnpApiPhoneroutinginfo info = null;
		// if(this.checkMnpInCache(da)){
		// info = (MnpApiPhoneroutinginfo)
		// cacheClient.getMap("mnpApiPhoneMap").get(da);
		// }
		try {
			// get boolean from cache , if data load done is true else false
			if (cacheStatusDao.isReady(MnpApiPhoneroutinginfo.class.getName())) {
				info = (MnpApiPhoneroutinginfo) getCachedMap().get(da);
				if(info==null){
					info = mnpApiPhoneroutinginfoDao.get(da);
					log.debug("getFromCache data not load done, select from db. info:[{}] ", info);
				}
			} else {
				// data not load done, select data from db
				info = mnpApiPhoneroutinginfoDao.get(da);
				log.debug("getFromCache data not load done, select from db. info:[{}] ", info);
			}
			return info;
		} catch (Exception e) {
			// data not load done or exception, select data from db
			log.error("[DB] getFromCache error:[{}]", e.getMessage());
			log.error(e, e);
			info = mnpApiPhoneroutinginfoDao.get(da);
			log.info("getFromCache error, select from db. info:[{}] ", info);
			return info;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchSave(List<MnpApiPhoneroutinginfo> list) {
//		IMap<String, MnpApiPhoneroutinginfo> mnpMap = getCachedMap();
		mnpApiPhoneroutinginfoDao.batchSave(list);
		cacheMnpApiPhoneroutinginfo();
//		list.forEach(e -> {
//			mnpMap.put(e.getPhoneNumber(), e);
//		});
	}

}
