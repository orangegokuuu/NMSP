package com.ws.msp.service;

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
import com.ws.msp.dao.FetPrefixDao;
import com.ws.msp.pojo.FetPrefix;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

import lombok.extern.log4j.Log4j2;


@SuppressWarnings("unused")
@Service(value = "fetprefixManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class FetPrefixManagerImpl extends GenericDataManagerImpl implements FetPrefixManager {
	@Autowired
	private MspProperties prop = null;

	@Autowired
	private FetPrefixDao fetPrefixDao = null;

	@Autowired
	private CacheStatusDao cacheStatusDao = null;
	
	@Autowired
	private HazelcastInstance cacheClient;

	public void setcacheClient(HazelcastInstance cacheClient) {
		this.cacheClient = cacheClient;
	}

	private IMap<String, FetPrefix> getCachedMap() {
		return cacheClient.getMap(FetPrefix.class.getName());
	}

	private void fillCache(PaginationResult<FetPrefix> result) {
		IMap<String, FetPrefix> blMap = getCachedMap();
		log.debug("Populate Page[{}/{}] with [{}]records to FetPrefix cache", result.getCurrPage(),
				result.getTotalPage(), result.getData().size());
		Map<String, FetPrefix> slice = result.getData().stream()
				.collect(Collectors.toMap(FetPrefix::getPrefix, e -> e));

		blMap.putAll(slice);
	}

	@Transactional
	public boolean checkFetPrefixInCache(String da) {
		boolean checkInCache = false;
		try{
			// get boolean from cache , if data load done is true else false
			if(cacheStatusDao.isReady(FetPrefix.class.getName())){
				for(int i=4;i<da.length();i++){
					String prefix = da.substring(0, i);
					log.debug("checkFetPrefixInCache da:[{}] ,prefix:[{}] , subStr num:[{}]", da,prefix,i);
					checkInCache = getCachedMap().containsKey(prefix);
					if(checkInCache){
						break;
					}
					
					FetPrefix fetPrefix = fetPrefixDao.get(prefix);
					if(fetPrefix!=null && fetPrefix.getPrefix().equals(prefix)){
						checkInCache = true;
						break;
					}
				}
				
			}
			else{
				//data not load done, select data from db
				FetPrefix prefix = null;
				for(int i=4;i<da.length();i++){
					String prefixTemp = da.substring(0, i);
					log.debug("checkFetPrefixInCache ,select from db. da:[{}] ,prefix:[{}] , subStr num:[{}]", da,prefixTemp,i);
					prefix = fetPrefixDao.get(prefixTemp);
					if(prefix!=null && prefix.getPrefix().equals(prefixTemp)){
						checkInCache = true;
						break;
					}
				}
				log.info("checkFetPrefixInCache data not load done, select from db. prefix:[{}] , checkInCache:[{}]", prefix,checkInCache);
			}
			return checkInCache;
		}catch(Exception e){
			//data not load done or exception, select data from db
			log.error("[DB] checkFetPrefixInCache error:[{}]", e.getMessage());
			log.error(e, e);
			FetPrefix prefix = fetPrefixDao.get(da);
			if(prefix!=null && prefix.getPrefix().equals(da)){
				checkInCache = true;
			}
		}
		return checkInCache;
	}

	@Transactional
	public void cacheFetPrefix() {
		log.debug("Fill Up Cache using properties [{}]", prop.getDal());
		SearchablePaging page = new SearchablePaging();
		page.setPageSize(prop.getDal().getCache().getLoadBatchSize());
		int pageNum = 0;
		int totalPage = 0;
		PaginationResult<FetPrefix> result = this.page(FetPrefix.class, page);
		totalPage = result.getTotalPage();
		log.debug("Total FetPrefix[{}] Pages[{}]", result.getTotalSize(), result.getTotalPage());
		if (totalPage > 0) {
			fillCache(result);
			while ((result.getNextPage() != 0) && (result.getNextPage() <= result.getTotalPage())) {
				page.setPageNum(result.getNextPage());
				result = this.page(FetPrefix.class, page);
				fillCache(result);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addAndSave(FetPrefix prefix) {
		this.save(FetPrefix.class, prefix);
		getCachedMap().put(prefix.getPrefix(), prefix);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void clearCacheFetPrefix() {
		getCachedMap().clear();
	}

}
