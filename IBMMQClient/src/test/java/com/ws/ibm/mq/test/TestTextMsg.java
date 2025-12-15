package com.ws.ibm.mq.test;

import java.io.EOFException;
import java.io.IOException;

import javax.jms.BytesMessage;
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
import com.ws.ibm.mq.message.MQJmsConsumer;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.msp.legacy.SMSException;
import com.ws.util.ByteUtil;
import com.ws.util.StringUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
public class TestTextMsg {
	private static String destinationName = "SMS.PRBT900.REQ.Q";

	byte[] data4 = ByteUtil.hexStringToByteArray(
	"31505242543930305330304230393033353234353431000000000000000000003031323334373230303030303030303030303030000000006447567a64413d3d");

	@Autowired
	@Qualifier("MQProducerConnectionPool4")
	private JmsConnectionFactory MQCf;

	@Test
	public void testEnq() throws JMSException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < 3; i++) {
				producer.enqTextMsgA(new String(data4));
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}

	}
	
	
}
