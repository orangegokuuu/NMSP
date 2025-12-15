package com.ws.api.listener;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.ws.api.httpclient.HttpClientDrHandle;
import com.ws.api.util.ChackUtils;
import com.ws.api.util.ConvertUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.pojo.MessageObject;
import com.ws.httpapi.pojo.PushDR;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.SmsRecordManager;

@Component("apiDeQueueListener")
public class ApiDeQueueListener {

	private static Logger logger = LogManager.getLogger(ApiDeQueueListener.class);

	@Autowired
	HttpClientDrHandle httpClientDrHandle;
	
	@Autowired
	private MspProperties properties;
	
	//vas intra
	//dmz inter
	
	@JmsListener(destination = "${dal.jms.drQueueName}")
	public void receiveDrMessage(final Message message) {
		try{
			logger.debug("==== receiveDrMessage queue name:[{}]", properties.getDal().getJms().getDrQueueName());
			ObjectMessage objectMessage = (ObjectMessage)message;
			MessageObject msg = (MessageObject)objectMessage.getObject();
			httpClientDrHandle.processPushDr(msg,properties.getDal().getJms().getDrQueueName());
		}catch(JMSException e){
			logger.error("[WISEMQ] receiveDrMessage error:[{}]", e.getMessage());
		}
	}
	
//	@JmsListener(destination = "${dal.jms.drInterQueueName}")
//	public void receiveDrInterMessage(final Message message) {
//		try{
//			logger.debug("==== receiveDrInterMessage time:[{}]", new Date());
//			ObjectMessage objectMessage = (ObjectMessage)message;
//			MessageObject msg = (MessageObject)objectMessage.getObject();
//			httpClientDrHandle.processPushDr(msg,properties.getDal().getJms().getDrInterQueueName());
//		}catch(JMSException e){
//			
//		}
//	}
	
	@JmsListener(destination = "${dal.jms.moQueueName}")
	public void receiveMoMessage(final Message message) {
		try{
			logger.debug("==== receiveMoMessage queue name:[{}]", properties.getDal().getJms().getMoQueueName());
			ObjectMessage objectMessage = (ObjectMessage)message;
			MessageObject msg = (MessageObject)objectMessage.getObject();
			httpClientDrHandle.processDeliverSM(msg,properties.getDal().getJms().getMoQueueName());
		}catch(JMSException e){
			logger.error("[WISEMQ] receiveMoMessage error:[{}]", e.getMessage());
		}
		
	}
	
//	@JmsListener(destination = "${dal.jms.moInterQueueName}")
//	public void receiveMoInterMessage(final Message message) {
//		try{
//			logger.debug("==== receiveMoInterMessage time:[{}]", new Date());
//			ObjectMessage objectMessage = (ObjectMessage)message;
//			MessageObject msg = (MessageObject)objectMessage.getObject();
//			httpClientDrHandle.processDeliverSM(msg,properties.getDal().getJms().getMoInterQueueName());
//		}catch(JMSException e){
//			
//		}
//	}
}
