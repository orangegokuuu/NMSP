package com.ws.fet.msp.task;

import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.spring.context.SpringAware;
import com.ws.backend.Task;
import com.ws.msp.service.QuotaManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringAware
public class ResetMinCounterTask implements Task<Void> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2107174912546917597L;

	@Autowired
	private QuotaManager quotaManager = null;

	@Override
	public Void call() throws Exception {
		log.debug("Resetting Minutely Counter...");
		quotaManager.resetQueryDrMinutelyCount();
		return null;
	}

}
