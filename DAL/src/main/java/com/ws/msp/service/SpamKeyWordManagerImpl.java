package com.ws.msp.service;

import java.util.ArrayList;
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
import com.ws.msp.dao.SpamKeyWordDao;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

import lombok.extern.log4j.Log4j2;


@Service(value = "spamKeyWordManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class SpamKeyWordManagerImpl extends GenericDataManagerImpl implements SpamKeyWordManager {
	@Autowired
	private MspProperties prop = null;

	@Autowired
	private SpamKeyWordDao spamKeyWordDao = null;

	@Autowired
	private CacheStatusDao cacheStatusDao = null;

	@Autowired
	private HazelcastInstance cacheClient;

	private IMap<String, SpamKeyWord> getCachedMap() {
		return cacheClient.getMap(SpamKeyWord.class.getName());
	}

	private void fillCache(PaginationResult<SpamKeyWord> result) {
		IMap<String, SpamKeyWord> mnpMap = getCachedMap();
		log.debug("Populate Page[{}/{}] with [{}]records to SpamKeyWords cache", result.getCurrPage(),
				result.getTotalPage(), result.getData().size());
		Map<String, SpamKeyWord> slice = result.getData().stream()
				.collect(Collectors.toMap(SpamKeyWord::getKey, e -> e));

		mnpMap.putAll(slice);
	}

	@Transactional
	public boolean checkSpamKeyWordInCache(String keyword) {
		Map<String, SpamKeyWord> spamKeyWordMap = getCachedMap();
		return spamKeyWordMap.containsKey(keyword);
	}

	@Transactional
	public void cacheSpamKeyWord() {
		log.debug("Fill Up Cache using properties [{}]", prop.getDal());
		SearchablePaging page = new SearchablePaging();
		page.setPageSize(prop.getDal().getCache().getLoadBatchSize());
		int totalPage = 0;
		PaginationResult<SpamKeyWord> result = this.page(SpamKeyWord.class, page);
		totalPage = result.getTotalPage();
		log.debug("Total SpamKeyWord[{}] Pages[{}]", result.getTotalSize(), result.getTotalPage());
		if (totalPage > 0) {
			fillCache(result);
			while ((result.getNextPage() != 0) && (result.getNextPage() <= result.getTotalPage())) {
				page.setPageNum(result.getNextPage());
				result = this.page(SpamKeyWord.class, page);
				fillCache(result);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addAndSave(SpamKeyWord sKey) {
		this.save(SpamKeyWord.class, sKey);
		getCachedMap().put(sKey.getKey(), sKey);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void clearCacheSpamKeyWord() {
		getCachedMap().clear();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<SpamKeyWord> getCacheSpamKeyWordList() {
		List<SpamKeyWord> list = new ArrayList<SpamKeyWord>();
		try {
			// get boolean from cache , if data load done is true else false
			if (cacheStatusDao.isReady(SpamKeyWord.class.getName())) {
				Map<String, SpamKeyWord> spamKeyWordMap = getCachedMap();
				if (spamKeyWordMap.size() > 0) {
					for (Object key : spamKeyWordMap.keySet()) {
						list.add(spamKeyWordMap.get(key));
					}
				}
			} else {
				// data not load done, select data from db
				list = spamKeyWordDao.loadAll();
				log.info("getCacheSpamKeyWordList data not load done, select from db. list.size:[{}] ", list.size());
			}
			return list;
		} catch (Exception e) {
			// data not load done or exception, select data from db
			log.error("[DB] getCacheSpamKeyWordList error:[{}]", e.getMessage());
			log.error(e, e);
			list = spamKeyWordDao.loadAll();
			log.info("getCacheSpamKeyWordList error, select from db. list.size:[{}] ", list.size());
			return list;
		}

	}

}
