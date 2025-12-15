package com.ws.ibm.mq.sit;

import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQLoadTest5 {
	
	private static String destinationName = "SMS.MQTEST3.REQ.Q";
	private static final int SUBMIT_VOLUME= 500;
	
	String testData1[] = {
			// 1. Normal Message
			"314d4450534d53545330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d"
	};

	@Autowired
	@Qualifier("MQProducerConnectionPool3")
	private JmsConnectionFactory MQCf;

	@Test
	public void loadTest() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {	
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < SUBMIT_VOLUME; i++) {
				log.info("loop {}", i);
				for (int j = 0; j < testData1.length; j++) {
					log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData1[j])));
					log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData1[j])));
					producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(testData1[j]));
				}
			}

		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}
	}

}
