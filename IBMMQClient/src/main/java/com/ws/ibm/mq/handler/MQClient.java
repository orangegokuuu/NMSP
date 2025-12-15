package com.ws.ibm.mq.handler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ws.msp.config.MspProperties;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.ContentProviderManager;

import lombok.extern.log4j.Log4j2;

/**
 * @Component MQClient
 * @Description A client object that maintain Thread of MQHandlers.
 *              Each MQHandler manages one pair of response queue and reply
 *              queue for one CP.
 * 
 * 
 **/
@Log4j2
@Component
public class MQClient {

	@Autowired
	private MspProperties properties = null;

	@Autowired
	private ContentProviderManager cpManager = null;

	@Autowired
	@Qualifier("MQHandlerList")
	private List<String> mqHandlerList = null;

	@Autowired
	private Provider<MQHandler> mqHandlerProvider;

	public void triggerThread(String cpId) {
		try {
			if (mqHandlerList.contains(cpId)) {
				log.info("There is a Handler for cp[{}] already.", cpId);
			} else {
				mqHandlerList.add(cpId);
				log.info("There is a Handler for cp[{}] already.", cpId);
				// retrieve cp
				ContentProvider cp = cpManager.get(ContentProvider.class, cpId);
				if (cp == null) {
					log.warn("CP[{}] not found!", cpId);
					mqHandlerList.remove(cpId);
					return;
				}
				log.debug("retrieved cp[{}].", cp);

				if (!validateCP(cp)) {
					log.warn("Invalid CP. Please contact System Admin.");
					mqHandlerList.remove(cpId);
					return;
				} else {
					final ExecutorService exService = Executors.newSingleThreadExecutor();
					MQHandler mqHandler = mqHandlerProvider.get();
					mqHandler.init(cp);
					exService.submit(mqHandler);
					log.debug("Thread count : [{}] ", Thread.activeCount());
				}
			}
		} catch (Exception e) {
			log.error("Fail to trigger cp[{}].", cpId);
			log.error(e);
			e.printStackTrace();
			mqHandlerList.remove(cpId);
		}
	}

	private boolean validateCP(ContentProvider cp) {
		if (!LegacyConstant.QM_MAP_R.get(new Integer(cp.getCpZone()))
				.equals(properties.getIbm().getJms().getQueueManagerName())) {
			log.warn("CP[{}] is not belong for Queue Manager [{}] but [{}]. CpZone = {}, ", cp.getCpId(),
					properties.getIbm().getJms().getQueueManagerName(),
					LegacyConstant.QM_MAP_R.get(new Integer(cp.getCpZone())), cp.getCpZone());
			return false;
		}
		boolean result = true;
		result = StringUtils.isAllUpperCase(cp.getCpId());
		result = cp.getCpType().equals(ContentProvider.CP_TYPE_MQ) ? true : false;
		return result;
	}

}
