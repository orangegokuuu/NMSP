package com.ws.ibm.mq.test.legacy;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.mq.MQException;
import com.ws.ibm.imq.dao.SubmitMessageDAO;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.handler.MQHandler;
import com.ws.ibm.mq.test.data.SdpData;
import com.ws.ibm.mq.util.MQZipUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQMultiClientF2Test {

//	// production config
//	 private static final String qmgrName[] = { "DMZ.QM", "DMZ.QM2",
//	 "DMZ.QM4", "MGW.QM", "MGW.QM2" };
//	 private static final String host[] = { "10.76.1.167", "10.76.1.168",
//	 "210.241.199.74", "10.76.91.26",
//	 "10.76.91.27" };
//	 private static final int port[] = { 8080, 8080, 8002, 8080, 8080 };
//	 private static String destinationName[] = { "SMS.SDPSMS1.REQ.Q",
//	 "SMS.SDPSMS2.REQ.Q", "SMS.SDPSMS3.REQ.Q",
//	 "SMS.SDPSMS4.REQ.Q", "SMS.SDPSMS5.REQ.Q" };
//	 private static String cpIds[] = { "SDPSMS1", "SDPSMS2", "SDPSMS3",
//	 "SDPSMS4", "SDPSMS5" };

	// lab config
	private static final String qmgrName[] = { "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3" };
	private static final String host[] = { "192.168.1.51", "192.168.1.51", "192.168.1.51", "192.168.1.51",
			"192.168.1.51" };
	// private static final String host[] = { "localhost", "localhost",
	// "localhost", "localhost", "localhost" };
	private static final int port[] = { 1414, 1414, 1414, 1414, 1414 };
//	private static String destinationName[] = { "SMS.SDPSMST.REQ.Q", "SMS.SDPSMST.REQ.Q", "SMS.SDPSMST.REQ.Q",
//			"SMS.SDPSMST.REQ.Q", "SMS.SDPSMST.REQ.Q" };
	private static String cpIds[] = { "SDPSMST", "SDPSMST", "SDPSMST", "SDPSMST", "SDPSMST" };

	private static final String chl = "SMS.WS.CHL";
	private SubmitMessageDAO[] daos = new SubmitMessageDAO[5];

	// production config
	// private static String destinationName[] = { "SMS.SDPSMS1.REQ.Q",
	// "SMS.SDPSMS2.REQ.Q", "SMS.SDPSMS3.REQ.Q",
	// "SMS.SDPSMS4.REQ.Q", "SMS.SDPSMS5.REQ.Q" };
	//
	// private static String cpIds[] = { "SDPSMS1", "SDPSMS2", "SDPSMS3",
	// "SDPSMS4", "SDPSMS5" };
	//
	// @Autowired
	// @Qualifier("MQProducerConnectionPool1")
	// private JmsConnectionFactory MQCf1;
	//
	// @Autowired
	// @Qualifier("MQProducerConnectionPool2")
	// private JmsConnectionFactory MQCf2;
	//
	// @Autowired
	// @Qualifier("MQProducerConnectionPool3")
	// private JmsConnectionFactory MQCf3;
	//
	// @Autowired
	// @Qualifier("MQProducerConnectionPool4")
	// private JmsConnectionFactory MQCf4;
	//
	// @Autowired
	// @Qualifier("MQProducerConnectionPool5")
	// private JmsConnectionFactory MQCf5;

	// production config

	// lab config

//	@Autowired
//	@Qualifier("MQProducerConnectionPool")
//	private JmsConnectionFactory MQCf1;
//
//	@Autowired
//	@Qualifier("MQProducerConnectionPool")
//	private JmsConnectionFactory MQCf2;
//
//	@Autowired
//	@Qualifier("MQProducerConnectionPool")
//	private JmsConnectionFactory MQCf3;
//
//	@Autowired
//	@Qualifier("MQProducerConnectionPool")
//	private JmsConnectionFactory MQCf4;
//
//	@Autowired
//	@Qualifier("MQProducerConnectionPool")
//	private JmsConnectionFactory MQCf5;

//	private JmsConnectionFactory getJmsConnectionFactory(int i) {
//		switch (i) {
//		case 0:
//			return MQCf1;
//		case 1:
//			return MQCf2;
//		case 2:
//			return MQCf3;
//		case 3:
//			return MQCf4;
//		case 4:
//			return MQCf5;
//		default:
//			log.info("factory not found.");
//			break;
//		}
//		return null;
//	}

	@Before
	public void init() {
		for (int i = 0; i < daos.length; i++) {
			daos[i] = new SubmitMessageDAO(qmgrName[i], host[i], port[i], chl);
			try {
				daos[i].connect();
			} catch (MQException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	// 13. invalid XML format
	public void testCase13() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(13);
	}

	// 14. invalid SysId
	@Test
	public void testCase14() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(14);
	}

	// 15. invalid Source Address
	@Test
	public void testCase15() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(15);
	}

	// 16. Short Message Length
	@Test
	public void testCase16() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(16);
	}

	// 17. invalid Language
	@Test
	public void testCase17() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(17);
	}

	// 18. invalid target
	@Test
	public void testCase18() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(18);
	}

	// 19. spam check
	@Test
	public void testCase19() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(19);
	}

	// 20. ValidPeriod not belong to 0,1,2,3,4
	@Test
	public void testCase20() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(20);
	}

	// 21. water level reached (need to config cp and reset
	// quota)
	@Test
	public void testCase21() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(21);
	}

	// 30. Without ValidPeriods
	@Test
	public void testCase30() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(30);
	}

	// 31. Without ValidPeriod, with special character
	@Test
	public void testCase31() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(31);
	}

	// 32. With ValidPeriod=1
	@Test
	public void testCase32() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(32);
	}

	// 33. With DR=true
	@Test
	public void testCase33() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(33);
	}

	// 34. With DR=false, target=2
	@Test
	public void testCase34() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(34);
	}

	// 35. With DR=false, target=3
	@Test
	public void testCase35() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(35);
	}

	// 36. With DR=true, target=2
	@Test
	public void testCase36() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(36);
	}

	// this is TextMessage
//	private void testMsg(int caseNum) {
//		MQJmsProducer producer = null;
//		String cpId = null;
//		for (int i = 0; i < 5; i++) {
//			try {
//				producer = new MQJmsProducer(getJmsConnectionFactory(i));
//				producer.startConnection(destinationName[i]);
//				cpId = cpIds[i];
//				String msg = caseNum == 14 ? SdpData.FORMAT2_TEST_CASE.get(caseNum)
//						: MQTestSuite.replaceXmlCpId(SdpData.FORMAT2_TEST_CASE.get(caseNum), cpId);
//
//				String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
//				byte[] zipData = MQZipUtil.compress(msgStamp, msg.getBytes());
//				String hexData = ByteUtil.byteArrayTohexString(zipData);
//				log.debug("Data={}", msg);
//				log.debug("zipData={}", zipData);
//				log.debug("hexData={}", hexData);
//				producer.enqTextMsgA(new String(zipData));
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				producer.closeConnection();
//			}
//		}
//	}

	// this is ByteMessage
	private void testMsgWithIMQ(int caseNum) {
		String cpId = null;
		for (int i = 0; i < 5; i++) {
			try {
				cpId = cpIds[i];

				daos[i].connectQueue(MQHandler.getCPQueueName(cpId, "REQ"));
				String msgContent = caseNum == 14 ? SdpData.FORMAT2_TEST_CASE.get(caseNum)
						: MQTestSuite.replaceXmlCpId(SdpData.FORMAT2_TEST_CASE.get(caseNum), cpId);

				String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, msgContent.getBytes());
				log.debug("Data={}", msgContent);
				log.debug("zipData={}", zipData);
				daos[i].put(null, null, zipData);
				daos[i].close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@After
	public void end() {
		for (int i = 0; i < daos.length; i++) {
			daos[i].close();
		}
	}

}
