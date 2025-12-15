package com.ws.ibm.mq.test;

import javax.inject.Provider;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.mq.ese.intercept.JmqiGetInterceptorImpl.MsgStatus;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.emg.pojo.MessageObject;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.handler.MQHandler;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.ContentProviderManager;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
public class MQConnectionTreadTest {

	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQCf = null;
	

	private String destinationName = "SMS.MQTESTC.PLY.Q";

	private String msg = "Test Connection";
	
	@Test
	public void test() throws JMSException {

		// create new producer and connection
		for (int i = 0; i < 100; i++) {
			Connection connection  = MQCf.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("queue:///" + destinationName + "?targetClient=1");
			MessageProducer producer = session.createProducer(queue);
			TextMessage msgA = session.createTextMessage(msg);
			producer.send(msgA);
		}

		try {
			Thread.sleep(3000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
