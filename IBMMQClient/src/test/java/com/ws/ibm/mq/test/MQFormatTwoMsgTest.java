package com.ws.ibm.mq.test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.jms.JMSException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.mq.ese.intercept.JmqiGetInterceptorImpl.MsgStatus;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.util.ByteUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
public class MQFormatTwoMsgTest {
	private static String destinationName = "SMS.DBIII00.REQ.Q";

	// type = Format 2
	String msg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\"><SMS><SysId>DBIII00</SysId><Message><Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">0921761727</Target><Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">0912144168</Target><Target token=\"05BF640FD2F1C9E6006E41B53FD3D091\">0970780286</Target><Source>01543800020123400023</Source><Language>B</Language><Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text><DrFlag>true</DrFlag><ValidPeriod>1</ValidPeriod></Message></SMS>";

	@Autowired
	@Qualifier("MQConnectionPool")
	private JmsConnectionFactory MQCf;

	@Test
	public void testEnq() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < 10; i++) {
				producer.enqBytesMsgA(msg.getBytes(StandardCharsets.UTF_8));
			}
		} finally {
			producer.closeConnection();
		}
	}

}
