package com.ws.ibm.mq.submitTask;

import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.ibm.mq.handler.ApiSubmitHandler;
import com.ws.ibm.mq.handler.MQSubmitHandler;
import com.ws.msp.legacy.MspMessageTwo;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.pojo.ContentProvider;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmitTaskTwo implements Runnable {

	@Autowired
	private ApiSubmitHandler apiSubmitHandler = null;

	@Autowired
	private Provider<MQSubmitHandler> mqSubmitHandlerProvider;

	private MQSubmitHandler mqSubmitHandler = null;
	private MspMessageTwo msg = null;

	public void init(ContentProvider cp, MspMessageTwo msg) {
		this.msg = msg;
		mqSubmitHandler = mqSubmitHandlerProvider.get();
		mqSubmitHandler.init(cp);
	}

	@Override
	public void run() {
		// send to httpapi
		String threadName = Thread.currentThread().getName();
		log.debug("Thread[{}] Started.", threadName);
		
		try {
			try {
				apiSubmitHandler.submitFormatTwoSMS(msg);
			} catch (SMSException e) {
				log.info("SMSException occurred. MsgId = [{}], ErrorCode=[{}], Message=[{}]", e.getCorreId(),
						e.getErrorCode(), e.getMessage());
				
				mqSubmitHandler.handleFormatTwoSMSException( e);
			}
		} catch (Exception e) {
			log.debug("Exception occurred. {}", e);
			e.printStackTrace();
		}
		log.debug("Thread[{}] End.", threadName);

	}

}
