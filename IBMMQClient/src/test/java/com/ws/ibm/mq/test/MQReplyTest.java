package com.ws.ibm.mq.test;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.message.MQJmsConsumer;
import com.ws.ibm.mq.spring.SpringConfig;
import com.ws.msp.legacy.SMSException;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = MQJmsConfig.class, loader =
// AnnotationConfigContextLoader.class)
@ContextConfiguration(classes = SpringConfig.class, loader = AnnotationConfigContextLoader.class)
@Log4j2
@Import(SpringConfig.class)
public class MQReplyTest {

	private static final String destinationName = "SMS.CSPSMST.PLY.Q";

	private static String submitAPIURL = "http://192.168.1.51:8080/api/smsSubmit?";

	@Autowired
	@Qualifier("MQConnectionPool")
	private JmsConnectionFactory MQCf;

	@Test
	public void testReceiveMessage() throws JMSException, SMSException {
		MQJmsConsumer consumer = null;
		try {
			consumer = new MQJmsConsumer(MQCf);
			consumer.startConnection(destinationName);
			TextMessage msg = null;
			do {
				msg = consumer.receiveMsgB(1000);
				if (msg != null) {
					log.info(msg);
				}
			} while (msg != null);
		} finally {
			log.info("No message B in Queue");
			consumer.closeConnection();
		}
	}

}
