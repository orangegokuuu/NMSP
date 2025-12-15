package com.ws.api.util;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.ws.emg.constant.ApiConstant;
import com.ws.emg.pojo.ThroughPutObj;
import com.ws.msp.config.MspProperties;
import com.ws.pojo.GenericBean;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ThroughPut extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4460600401131901973L;

	@Autowired
	private HazelcastInstance hzClient;

	@Autowired
	private MspProperties properties;

	private IMap<String, ThroughPutObj> timers = null;

	private boolean canProcess;
	private IAtomicLong totalCounter;

	@PostConstruct
	private void init() {

		// totalCounter = hzClient.getCPSubsystem().getAtomicLong(ApiConstant.KEY_THROUGH_PUT);
		totalCounter = hzClient.getAtomicLong(ApiConstant.KEY_THROUGH_PUT);
		timers = hzClient.getMap(ApiConstant.KEY_THROUGH_PUT);

		// createTimer();
		// properties.getDal().getJms().getCheckTime()
	}

	@PreDestroy
	private void distroy() {

//		if (timers != null && timers.size() > 0) {
//			for (String key : timers.keySet()) {
//				Timer timer = timers.get(key);
//				log.debug("key : [{}], value : [{}]", key, timer);
//				timer.purge();
//				timer.cancel();
//			}
//		}
	}

	public boolean canProcess() {
		// TODO
		return canProcess;
	}

	public void createTimer(long limit, int timePeriod, String name) {

		Timer timer = null;
		long mLimit = 0;

		if (!timers.containsKey(name)) {
			timer = new Timer();
		} else {
//			timer = timers.get(name);
		}

		try {
			timer.cancel();
			timer.purge();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					clearCounter();
				}
			}, 0l, timePeriod * 1000);
			timer.notify();
		} catch (Exception e) {
			log.error("[{}] reset timer error, due to [{}]", name, e.getMessage());
			log.error(e, e);
		}

//		timers.put(name, timer);
	}

	/**
	 * clear counter
	 */
	public void clearCounter() {
		totalCounter.set(0);
	}

}
