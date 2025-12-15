package com.ws.ibm.mq.testcase;

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
import com.ws.ibm.mq.test.data.MdpData;
import com.ws.util.ByteUtil;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQFormatOneTest {

	private static String destinationName[] = { "SMS.MDPSMS1.REQ.Q", "SMS.MDPSMS2.REQ.Q", "SMS.MDPSMS3.REQ.Q",
			"SMS.MDPSMS4.REQ.Q", "SMS.MDPSMS5.REQ.Q" };

	@Autowired
	@Qualifier("MQProducerConnectionPool1")
	private JmsConnectionFactory MQCf1;

	@Autowired
	@Qualifier("MQProducerConnectionPool2")
	private JmsConnectionFactory MQCf2;

	@Autowired
	@Qualifier("MQProducerConnectionPool3")
	private JmsConnectionFactory MQCf3;

	@Autowired
	@Qualifier("MQProducerConnectionPool4")
	private JmsConnectionFactory MQCf4;

	@Autowired
	@Qualifier("MQProducerConnectionPool5")
	private JmsConnectionFactory MQCf5;

	private JmsConnectionFactory getJmsConnectionFactory(int i) {
		switch (i) {
		case 0:
			return MQCf1;
		case 1:
			return MQCf2;
		case 2:
			return MQCf3;
		case 3:
			return MQCf4;
		case 4:
			return MQCf5;
		default:
			log.info("factory not found.");
			break;
		}
		return null;
	}

	@Test
	public void testCase1() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 0;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase2() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 1;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase3() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 2;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase4() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 3;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase5() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 4;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}

	@Test
	public void testCase6() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 5;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase7() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 6;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase8() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 7;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	
	// Multi-message
	@Test
	public void testCase9() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 0;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex]), ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	// Multi-message
	@Test
	public void testCase10() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 2;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex]), ByteUtil.hexStringToByteArray(MdpData.testData1b[i][dataIndex+1]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	
	
	@Test
	public void testCase11() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 8;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase12() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 9;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData1[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	
	@Test
	public void testCase22() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 0;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase23() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 1;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase24() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 2;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase25() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 3;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(MdpData.testData2[i][dataIndex]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	// Multi-message
	@Test
	public void testCase26() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 0;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex]), ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	// Multi-message
	@Test
	public void testCase27() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 2;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex]), ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	// Multi-message
	@Test
	public void testCase28() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		int dataIndex = 4;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex]), ByteUtil.hexStringToByteArray(MdpData.testData2b[i][dataIndex+1]));
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
}
