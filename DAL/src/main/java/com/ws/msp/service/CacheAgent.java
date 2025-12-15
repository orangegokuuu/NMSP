package com.ws.msp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.ws.core.pojo.SystemParam;
import com.ws.core.service.SystemServiceManager;
import com.ws.msp.dao.CacheStatusDao;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpDestinationAddress;
import com.ws.msp.pojo.CpSourceAddress;
import com.ws.msp.pojo.FetPrefix;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.msp.pojo.TimeSlotData;
import com.ws.msp.pojo.TimeTable;

import lombok.extern.log4j.Log4j2;

@ManagedResource(objectName = "com.ws.msp:name=CacheBean", description = "Cache MBean", currencyTimeLimit = 1)
@Component
@Log4j2
public class CacheAgent {

	public static final String HZ_MAP = "hz:impl:mapService";
	
	public static final String BK_MAP = "com.ws.msp.pojo.BlackList";
	public static final String CP_MAP = "com.ws.msp.pojo.ContentProvider";
	public static final String CPSA_MAP = "com.ws.msp.pojo.CpSourceAddress";
	public static final String CPSA_LIST_MAP = "com.ws.msp.pojo.ContentProvider.cpsaMap";
	public static final String CPDA_MAP = "com.ws.msp.pojo.CpDestinationAddress";
	public static final String CPDA_LIST_MAP = "com.ws.msp.pojo.ContentProvider.cpdaMap";
	public static final String FET_PREFIX_MAP = "com.ws.msp.pojo.FetPrefix";
	public static final String MNP_MAP = "com.ws.msp.pojo.MnpApiPhoneroutinginfo";
	public static final String SPAM_MAP = "com.ws.msp.pojo.SpamKeyWord";
	public static final String TT_MAP = "com.ws.msp.pojo.TimeTable";
	public static final String TSD_MAP = "com.ws.msp.pojo.TimeSlotData";
	public static final String TT_LIST_MAP = "com.ws.msp.pojo.TimeTable.timeData";
	
	
	public enum MspCache {
		all, blacklist, cp, mnp, spam, prefix, timetable
	}

	@Autowired
	private HazelcastInstance hz = null;

	@Autowired
	private CacheStatusDao cacheStatusDao = null;

	@Autowired
	private TimeTableManager timeTableManager = null;

	@Autowired
	private ContentProviderManager contentProviderManager = null;

	@Autowired
	private BlackListManager blackListManager = null;

	@Autowired
	private SpamKeyWordManager spamKeyWordManager = null;

	@Autowired
	private MnpApiPhoneroutinginfoManager mnpManager = null;

	@Autowired
	private FetPrefixManager fetPrefixManager = null;

	@Autowired
	private SystemServiceManager sysManager = null;

	@SuppressWarnings("rawtypes")
	public List<IMap> getCache() {
		List<IMap> caches = new ArrayList<IMap>();
		for (DistributedObject o : hz.getDistributedObjects()) {
			if (o.getServiceName().equals(HZ_MAP)) {
				IMap m = hz.getMap(o.getName());
				caches.add(m);
			}
		}
		log.debug("Loading all cache object, return[{}] objects", caches.size());
		return caches;
	}

	@SuppressWarnings("rawtypes")
	public IMap getCache(String name) {
		List<IMap> caches = getCache();

		return caches.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
	}

	public Map<String, MapConfig> getConfigs() {
		return hz.getConfig().getMapConfigs();
	}

	public MapConfig getConfig(String name) {
		return hz.getConfig().getMapConfig(name);
	}

	private void loadTtCache() {
		log.debug("Loading Time Table cache");
		cacheStatusDao.loading(TimeTable.class.getName());
		List<TimeTable> tts = timeTableManager.listAll(TimeTable.class);
		for (TimeTable tt : tts) {
			if (tt.getTimeData() != null) {
				log.debug("Time Table[{}] [{}]child loaded", tt.getTimeTableId(), tt.getTimeData().size());
				tt.getTimeData().size();
			}
		}
		log.debug("Loading Time Slot cache");
		timeTableManager.listAll(TimeSlotData.class);
		cacheStatusDao.ready(TimeTable.class.getName());
	}

	private void loadCpCache() {
		log.debug("Loading Content provider cache");
		cacheStatusDao.loading(ContentProvider.class.getName());
		cacheStatusDao.loading(CpSourceAddress.class.getName());
		cacheStatusDao.loading(CpDestinationAddress.class.getName());
		List<ContentProvider> cps = contentProviderManager.listAll(ContentProvider.class);
		for (ContentProvider cp : cps) {
			if (cp.getCpsaMap() != null) {
				log.debug("Content Provider[{}] [{}]SA loaded", cp.getCpId(), cp.getCpsaMap().size());
			}
			if (cp.getCpdaMap() != null) {
				log.debug("Content Provider[{}] [{}]DA loaded", cp.getCpId(), cp.getCpdaMap().size());
			}
		}
		log.debug("Loading Content provider Source Address");
		contentProviderManager.listAll(CpSourceAddress.class);
		contentProviderManager.listAll(CpDestinationAddress.class);

		cacheStatusDao.ready(ContentProvider.class.getName());
		cacheStatusDao.ready(CpSourceAddress.class.getName());
		cacheStatusDao.ready(CpDestinationAddress.class.getName());
	}

	private void loadBlCache() {
		log.debug("Loading BlackList");
		if (!cacheStatusDao.isLoading(BlackList.class.getName())) {
			cacheStatusDao.loading(BlackList.class.getName());
			blackListManager.cacheBlackList();
			cacheStatusDao.ready(BlackList.class.getName());
		} else {
			log.info("Other Agent loading BlackList, Skip load");
		}
	}

	private void loadSwCache() {
		if (!cacheStatusDao.isLoading(SpamKeyWord.class.getName())) {
			log.debug("Loading Spam Keywords");
			cacheStatusDao.loading(SpamKeyWord.class.getName());
			spamKeyWordManager.cacheSpamKeyWord();
			cacheStatusDao.ready(SpamKeyWord.class.getName());
		} else {
			log.info("Other Agent loading Spam Keywords, Skip load");
		}
	}

	private void loadMnpCache() {
		if (!cacheStatusDao.isLoading(MnpApiPhoneroutinginfo.class.getName())) {
			log.debug("Loading MNP records");
			cacheStatusDao.loading(MnpApiPhoneroutinginfo.class.getName());
			mnpManager.cacheMnpApiPhoneroutinginfo();
			cacheStatusDao.ready(MnpApiPhoneroutinginfo.class.getName());
		} else {
			log.info("Other Agent loading MNP records, Skip load");
		}
	}

	private void loadPrefixCache() {
		if (!cacheStatusDao.isLoading(FetPrefix.class.getName())) {
			log.debug("Loading Prefix records");
			cacheStatusDao.loading(FetPrefix.class.getName());
			fetPrefixManager.cacheFetPrefix();
			cacheStatusDao.ready(FetPrefix.class.getName());
		} else {
			log.info("Other Agent loading Prefix records, Skip load");
		}
	}

	public void load2LevelCache() {
		loadCpCache();
		loadTtCache();
	}

	@Async
	public void asyncLoadBl() {
		loadBlCache();
	}

	@Async
	public void asyncLoadSw() {
		loadSwCache();
	}

	@Async
	public void asyncLoadMnp() {
		loadMnpCache();
	}

	@Async
	public void asyncLoadPrefix() {
		loadPrefixCache();
	}

	@Async
	public void load(MspCache cache) {
		switch (cache) {
		case blacklist:
			clear(BK_MAP);
			loadBlCache();
			break;
		case cp:
			clear(CP_MAP);
			clear(CPSA_MAP);
			clear(CPSA_LIST_MAP);
			clear(CPDA_MAP);
			clear(CPDA_LIST_MAP);
			loadCpCache();
			break;
		case spam:
			clear(SPAM_MAP);
			loadSwCache();
			break;
		case mnp:
			clear(MNP_MAP);
			loadMnpCache();
			break;
		case prefix:
			clear(FET_PREFIX_MAP);
			loadPrefixCache();
			break;
		case timetable:
			clear(TT_MAP);
			clear(TT_LIST_MAP);
			clear(TSD_MAP);
			loadTtCache();
		case all:
		default:
			clear("all");
			loadCpCache();
			loadTtCache();
			loadBlCache();
			loadSwCache();
			loadMnpCache();
			loadPrefixCache();
			break;
		}
	}

	@ManagedOperation(description = "Load MSP cache to memory")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "name", description = "Cache name") })
	@SuppressWarnings("rawtypes")
	public void clear(String name) {
		List<IMap> caches = getCache();
		log.debug("Attempt to clear [{}] records", name);
		if ("all".equals(name)) {
			for (IMap m : caches) {
				m.clear();
			}
		} else {
			for (IMap m : caches) {
				if (m.getName().equals(name)) {
					m.clear();
					return;
				}
			}
			throw new EntityNotFoundException("Cache " + name + " Not found");
		}
	}

	@ManagedOperation(description = "Clear MSP cache from memory")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "cache", description = "Cache name") })
	public void load(String cache) {
		load(MspCache.valueOf(cache));
	}

	public SystemParam getSystemParam(String paramId) {
		return sysManager.getSystemParam(paramId);
	}

}
