package com.ws.ibm.mq.listener;

import javax.inject.Provider;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ws.emg.pojo.MessageObject;
import com.ws.ibm.mq.handler.MQSubmitHandler;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.ContentProviderManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MQListener {

	@Autowired
	private MspProperties properties;

	@Autowired
	private ContentProviderManager cpManager = null;
	
	@Autowired
	private Provider<MQSubmitHandler> mQSubmitHandlerProvider;
	
	
	// handle dr message
	@JmsListener(destination = "${ibm.jms.dr.queue}")
	public void receiveDrMessage(final Message message) {
		try{
			log.debug("==== receiveDrMessage queue name:[{}]", properties.getIbm().getJms().getDr().getQueue());
			ObjectMessage objectMessage = (ObjectMessage)message;
			MessageObject msg = (MessageObject)objectMessage.getObject();
			log.debug(" receive msg:[{}]", msg);
			
			// retrieve cp 
			log.debug("retrieve cp[{}] from db", msg.getCpId());
			ContentProvider cp = cpManager.get(ContentProvider.class, msg.getCpId());
			
			// create new handler
			log.debug("create new MQSubmitHandler");
			MQSubmitHandler mqSubmitHandler = mQSubmitHandlerProvider.get();
			mqSubmitHandler.init(cp);
			
			if (cp.isLegacy()) {
				mqSubmitHandler.submitFormatOneDrMessage(msg);
				// 20190613 modify by YC
//				mqSubmitHandler.submitFormatOne(msg);
			} else {
				mqSubmitHandler.submitFormatTwoDrMessage(msg);
			}
			
		}catch(Exception e){
			log.error("==== receiveDrMessage error:[{}]", e);
		}
	}
	
	// handle mo message
	@JmsListener(destination = "${ibm.jms.mo.queue}")
	public void receiveMoMessage(final Message message) {
		try{
			log.debug("==== receiveMoMessage queue name:[{}]", properties.getIbm().getJms().getMo().getQueue());
			ObjectMessage objectMessage = (ObjectMessage)message;
			MessageObject msg = (MessageObject)objectMessage.getObject();
			log.debug(" receive msg:[{}]", msg);
			
			// retrieve cp 
			log.debug("retrieve cp[{}] from db", msg.getCpId());
			ContentProvider cp = cpManager.get(ContentProvider.class, msg.getCpId());
			
			// create new handler
			log.debug("create new MQSubmitHandler");
			MQSubmitHandler mqSubmitHandler = mQSubmitHandlerProvider.get();
			mqSubmitHandler.init(cp);
			
			if (cp.isLegacy()) {
				mqSubmitHandler.submitFormatOneMoMessage(msg);
			} else {
				mqSubmitHandler.submitFormatTwoMoMessage(msg);
			}
			
		}catch(Exception e){
			log.error("==== receiveDrMessage error:[{}]", e);
		}
	}
	
}
