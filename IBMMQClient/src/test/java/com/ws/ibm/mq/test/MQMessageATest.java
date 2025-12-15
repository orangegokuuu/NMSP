package com.ws.ibm.mq.test;

import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.msp.config.MspProperties;
import com.ws.util.ByteUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
public class MQMessageATest {
	private static String destinationName = "SMS.SDPSMST.REQ.Q";

	// type = Message A
	byte[] dataA = ByteUtil.hexStringToByteArray(ByteUtil.byteArrayTohexString("1CSPSMSTM11W010888912000100000002®¥³ß±z¤¤¤FÀY¼ú60¸U,½Ð³t¼·17240".getBytes()));
	// type = Message C
	byte[] dataC = ByteUtil.hexStringToByteArray(ByteUtil.byteArrayTohexString("09556283020926296918".getBytes()));
	// type = Message A
	byte[] dataD = ByteUtil.hexStringToByteArray(
			ByteUtil.byteArrayTohexString("1SDPSMSTS10B098193191401000410300000050000´ú¸Õ¦hµ§16".getBytes()));

	byte[] errorDataA = ByteUtil.hexStringToByteArray(
			ByteUtil.byteArrayTohexString("1SDPSMSTS10B098193191401000410300000050000´ú¸Õ¦hµ§16".getBytes()));

	@Autowired
	@Qualifier("MQConnectionPool")
	private JmsConnectionFactory MQCf;

	@Test
	public void testEnq() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < 50; i++) {
				producer.enqBytesMsgA(dataD);
				// producer.enqBytesMsgA(dataD);
				// producer.enqBytesMsgA(dataD);
			}
			// producer.enqBytesMsgA(dataD);
			// Thread.sleep(3000);

			// } catch (InterruptedException e) {
			// e.printStackTrace();
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}

	}

}
