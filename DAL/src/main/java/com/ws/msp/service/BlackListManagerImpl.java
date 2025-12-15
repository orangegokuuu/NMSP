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
import com.ws.msp.dao.BlackListDao;
import com.ws.msp.dao.CacheStatusDao;
import com.ws.msp.pojo.BlackList;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

import lombok.extern.log4j.Log4j2;


@SuppressWarnings("unused")
@Service(value = "blackListManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class BlackListManagerImpl extends GenericDataManagerImpl implements BlackListManager {
	@Autowired
	private MspProperties prop = null;

	@Autowired
	private BlackListDao blackListDao = null;
	
	@Autowired
	private CacheStatusDao cacheStatusDao = null;

	@Autowired
	private HazelcastInstance cacheClient;

	public void setcacheClient(HazelcastInstance cacheClient) {
		this.cacheClient = cacheClient;
	}

	private IMap<String, BlackList> getCachedMap() {
		return cacheClient.getMap(BlackList.class.getName());
	}

	@Transactional
	public boolean checkBlackListInCache(String da) {
		//IMap<String, BlackList> map = null;
		//boolean isCache = false;
		boolean checkInCache = false;
		try{
			// get boolean from cache , if data load done is true else false
			if(cacheStatusDao.isReady(BlackList.class.getName())){
				checkInCache = getCachedMap().containsKey(da);
				if(!checkInCache){
					BlackList bl = blackListDao.get(da);
					if(bl!=null && bl.getDestNumber().equals(da)){
						checkInCache = true;
					}
				}
			}
			else{
				//data not load done, select data from db
				BlackList bl = blackListDao.get(da);
				if(bl!=null && bl.getDestNumber().equals(da)){
					checkInCache = true;
				}
				log.info("checkBlackListInCache data not load done, select from db. bl:[{}] , checkInCache:[{}]", bl,checkInCache);
			}
			return checkInCache;
		}catch(Exception e){
			//data not load done or exception, select data from db
			log.error("[DB] checkBlackListInCache error:[{}]", e.getMessage());
			log.error(e, e);
			BlackList bl = blackListDao.get(da);
			if(bl!=null && bl.getDestNumber().equals(da)){
				checkInCache = true;
			}
			log.info("checkBlackListInCache error, select from db. bl:[{}] , checkInCache:[{}]", bl,checkInCache);
			return checkInCache;
		}
		//return getCachedMap().containsKey(da);
	}

	private void fillBlCache(PaginationResult<BlackList> result) {
		IMap<String, BlackList> blMap = getCachedMap();
		log.debug("Populate Page[{}/{}] with [{}]records to BlackList cache", result.getCurrPage(),
				result.getTotalPage(), result.getData().size());
		Map<String, BlackList> slice = result.getData().stream()
				.collect(Collectors.toMap(BlackList::getDestNumber, e -> e));

		blMap.putAll(slice);
	}

	@Transactional
	public void cacheBlackList() {
		log.debug("Fill Up Cache using properties [{}]", prop.getDal());
		SearchablePaging page = new SearchablePaging();
		page.setPageSize(prop.getDal().getCache().getLoadBatchSize());
		int totalPage = 0;
		PaginationResult<BlackList> result = this.page(BlackList.class, page);
		totalPage = result.getTotalPage();
		log.debug("Total Blacklist[{}] Pages[{}]", result.getTotalSize(), result.getTotalPage());
		if (totalPage > 0) {
			fillBlCache(result);
			while ((result.getNextPage() != 0) && (result.getNextPage() <= result.getTotalPage())) {
				page.setPageNum(result.getNextPage());
				result = this.page(BlackList.class, page);
				fillBlCache(result);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addAndSave(BlackList bl) {
		if (this.get(BlackList.class, bl.getDestNumber()) == null) {
			this.save(BlackList.class, bl);
		} else {
			log.debug("Record {} exist", bl.getDestNumber());
		}

		getCachedMap().put(bl.getDestNumber(), bl);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void clearCacheBlackList() {
		getCachedMap().clear();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void batchSave(List<BlackList> list) {
		blackListDao.batchSave(list);
//		IMap<String, BlackList> blMap = getCachedMap();
//		list.forEach(e -> {
//			blMap.put(e.getDestNumber(), e);
//		});
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteCacheAndDb(BlackList bl) {
		getCachedMap().remove(bl.getDestNumber());
		BlackList blTemp = this.get(BlackList.class, bl.getDestNumber());
		if(blTemp!=null){
			log.info("delete blacklist,phoneNumber:[{}]",bl.getDestNumber());
			this.delete(BlackList.class, bl.getDestNumber());
		}
	}

}
