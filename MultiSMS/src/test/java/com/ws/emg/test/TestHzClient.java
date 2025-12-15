package com.ws.emg.test;

import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.hazelcast.core.HazelcastInstance;
import com.ws.emg.spring.SpringConfig;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.BlackListManager;
import com.ws.msp.service.ContentProviderManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TestHzClient {

	@Autowired
	HazelcastInstance hzClient;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	ContentProviderManager contentProviderManager;
	
	@Autowired
	BlackListManager blackListManager;

	@Test
	public void test1() {

		log.debug("######DATA######");

		log.debug("session get BlackList : [{}]", sessionFactory.openSession().get(BlackList.class, "886909000001"));
		log.debug("manager get BlackList : [{}]", blackListManager.get(BlackList.class, "886909000001"));
		
		log.debug("session get BlackList 2: [{}]", sessionFactory.openSession().get(BlackList.class, "886909000010"));
		log.debug("manager get BlackList 2: [{}]", blackListManager.get(BlackList.class, "886909000010"));
		log.debug("hzClient get BlackList 2: [{}]", hzClient.getMap("blacklistMap").get("886909000010"));
		try {
			//blackListManager.cacheBlackList();
			BlackList bl = new BlackList();
			bl.setDestNumber("886909000010");
			bl.setCreateBy("SYSTEM");
			bl.setCreateDate(new Date());
			blackListManager.addAndSave(bl);
			log.debug("session get BlackList 3: [{}]", sessionFactory.openSession().get(BlackList.class, "886909000010"));
			log.debug("manager get BlackList 3: [{}]", blackListManager.get(BlackList.class, "886909000010"));
			log.debug("is bl: [{}]", blackListManager.checkBlackListInCache("886909000010"));
			
//			IMap<Object, Value> map = hzClient.getMap(BlackList.class.getName());
//			for (Object k : map.keySet()) {
//				StandardCacheEntryImpl v = (StandardCacheEntryImpl) map.get(k).getValue();
//				log.debug("key : [{}], value : [{}]", k, v.getDisassembledState());
//				log.debug("v : [{}]", v.toString());
//			}

		} catch (Exception e) {
			log.error(e, e);
		}

//		for (ContentProvider cp : executeQuery(sessionFactory)) {
//			if (cp.getCpId().equals("TEST1")) {
//				log.debug("query sql     : [{}]", cp);
//			}
//		}
		
		log.debug("######DATA######");
	}

	// @Test
	public void test2() {
		log.debug("######DATA######");
		log.debug(hzClient.getMap(ContentProvider.class.getName()).get("TEST1"));
		log.debug("######DATA######");
	}

	protected List<ContentProvider> executeQuery(SessionFactory factory) {
		Session session = factory.openSession();
		try {
			Query query = session.createQuery("from " + ContentProvider.class.getName());
			query.setCacheable(true);
			return query.list();
		} finally {
			session.close();
		}
	}
}
