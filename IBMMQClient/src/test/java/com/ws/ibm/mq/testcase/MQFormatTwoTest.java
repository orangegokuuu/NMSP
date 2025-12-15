package com.ws.ibm.mq.testcase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.ws.ibm.mq.test.data.SdpData;
import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.util.ByteUtil;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQFormatTwoTest {
	
	private static String destinationName[] = { "SMS.SDPSMS1.REQ.Q", "SMS.SDPSMS2.REQ.Q", "SMS.SDPSMS3.REQ.Q",
			"SMS.SDPSMS4.REQ.Q", "SMS.SDPSMS5.REQ.Q" };

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
	public void testCase13() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 0;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase14() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 1;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase15() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 2;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase16() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 3;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase17() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 4;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase18() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 5;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase19() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 6;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase20() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 7;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	
	// 21. water level reached (need to config cp and reset quota)
	@Test
	public void testCase21() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 8;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase30() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 9;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase31() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 10;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase32() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 11;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase33() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 12;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase34() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 13;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase35() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 14;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	@Test
	public void testCase36() throws JMSException, IOException {
		MQJmsProducer producer = null;
		int dataIndex = 15;
		for (int i = 0; i < 5; i++) {
			try {
				producer = new MQJmsProducer(getJmsConnectionFactory(i));
				producer.startConnection(destinationName[i]);
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, SdpData.testData[i][dataIndex].getBytes("UTF-8"));
				log.debug("Data={}", SdpData.testData[i][dataIndex]);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			} finally {
				if (producer != null) {
					producer.closeConnection();
				}
			}
		}
	}
	
	
}
