package com.ws.emg.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
//import com.ws.msp.pojo.ContentProvider;
//import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.tester.spring.SpringConfig;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TestThings {

	//@Autowired
//	ContentProviderManager contentProviderManager;
	
	@Autowired
	@Qualifier("hzClient")
	private HazelcastInstance hzClient;
	
	@Test
	public void testThroughput(){
		
		String KEY_COUNTER = "test_tps";
		
		// IAtomicLong counter = hzClient.getCPSubsystem().getAtomicLong(KEY_COUNTER);
		IAtomicLong counter = hzClient.getAtomicLong(KEY_COUNTER);
		log.debug("Counter : {}", counter);
		Long de = counter.get();
		log.debug("Get : {}", de);
		Long incrementedValue = counter.incrementAndGet();
		log.debug("incrementAndGet : {}", incrementedValue);
		log.debug("incrementAndGet : {}", counter.incrementAndGet());
		counter.set(0);
		log.debug("After clear : {}", counter.get());
	}

	// @Test
	public void test1() {

		String msg = "id:9 sub:001 dlvrd:001 submit date:1708041209 done date:1708041209 stat:DELIVRD err:000 Text:Connection tes";

		log.debug("### pwd : [{}]", getDrValue(msg, "pwd"));
		log.debug("### id : [{}]", getDrValue(msg, "id"));
		log.debug("### sub : [{}]", getDrValue(msg, "sub"));
		log.debug("### err : [{}]", getDrValue(msg, "err"));
		log.debug("### submit date : [{}]", getDrValue(msg, "submit date"));
		log.debug("### done date : [{}]", getDrValue(msg, "done date"));
		log.debug("### stat : [{}]", getDrValue(msg, "stat"));
		log.debug("### Text : [{}]", getDrValue(msg, "Text"));
	}

	//@Test
/*	public void testUpdate() {

		ContentProvider test1 = null;
		String sysId = "TEST1";

		DetachedCriteria dcCp = DetachedCriteria.forClass(ContentProvider.class);
		dcCp.add(Restrictions.eq("cpId", sysId));
		List<ContentProvider> cpList = contentProviderManager.findByCriteria(ContentProvider.class, dcCp);

		if (cpList != null && cpList.size() > 0) {

			test1 = cpList.get(0);
			test1.setTimeTableId("2");

			try {
				contentProviderManager.update(ContentProvider.class, test1);
				log.debug("Update data success");
			} catch (Exception e) {
				log.warn("Update for {} failed", sysId);
			}

		} else {
			log.warn("Cannot query CONTENT_PROVIDER from sysId : [{}]", sysId);
		}
	}
*/
	/**
	 * get values from short message in deliver SM.
	 * 
	 * @param shortMessage
	 * @param key
	 * @return
	 */
	public String getDrValue(String shortMessage, String key) {

		String result = "";
		String mKey = key + ":";

		if (StringUtils.isNotBlank(shortMessage)) {
			if (shortMessage.contains(mKey)) {
				int start = shortMessage.indexOf(mKey);
				shortMessage = shortMessage.substring(start, shortMessage.length() - 1);
				shortMessage = shortMessage.replace(mKey, "");
				result = shortMessage.substring(0, shortMessage.indexOf(" "));
			}
		}

		return result;
	}
}
