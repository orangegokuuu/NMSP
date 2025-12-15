package com.ws.ibm.mq.test;

import javax.jms.JMSException;

import org.junit.After;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
public class MQMsgAandCTest {
	private static String destinationName = "SMS.CSPSMST.REQ.Q";

	// type = Message A
	byte[] dataA = ByteUtil.hexStringToByteArray(
			"31435350534D53544D3030570000000000000000000000000000000000000000303130383838393132303030313030303030303032000000AEA5B3DFB17AA4A4A446C059BCFA3630B8552CBDD0B374BCB73137323430");
	// type = Message C
	byte[] dataC = ByteUtil
			.hexStringToByteArray("30393535363238333032000000000000000000003039323632393639313800000000000000000000");
	// type = Message A
	byte[] dataD = ByteUtil.hexStringToByteArray(
			"31534450534D5354533030423039383139333139313400000000000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136");

	byte[] errorDataA = ByteUtil.hexStringToByteArray(
			"31534450534D5354533030423039383139333139313400000000000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136");
	
	@Autowired
	@Qualifier("MQConnectionPool")
	private JmsConnectionFactory MQCf;

	@Test
	public void testEnq() throws JMSException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < 100; i++) {
//				producer.enqBytesMsgA(dataD);
//				producer.enqBytesMsgA(dataD);
//				producer.enqBytesMsgA(dataD);
//				producer.enqBytesMsgAwithC(dataA, dataC);
				
//				producer.enqBytesMsgA(dataD);
//				producer.enqBytesMsgA(dataD);
//				producer.enqBytesMsgA(dataD);
				producer.enqBytesMsgAwithC(dataA, dataC);
				
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}

	}
	
}
