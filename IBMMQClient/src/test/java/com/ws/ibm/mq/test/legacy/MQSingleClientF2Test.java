package com.ws.ibm.mq.test.legacy;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;

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
public class MQSingleClientF2Test {

	// production config
	// private static final String qmgrName = "DMZ.QM3";
	// private static final String host= "192.168.1.51";
	// private static final int port = 1414;
	// private static final String chl = "SMS.WS.CHL";
	// private static String cpId = "SDPSMST";
	// private SubmitMessageDAO dao = new SubmitMessageDAO();

	// lab config
	private static final String qmgrName = "DMZ.QM3";
	private static final String host = "192.168.1.51";
	private static final int port = 1414;
	private static final String chl = "SMS.WS.CHL";
	private static String cpId = "SDPSMST";
	private SubmitMessageDAO dao = new SubmitMessageDAO();

	@Before
	public void init() {
		dao = new SubmitMessageDAO(qmgrName, host, port, chl);
		try {
			dao.connect();
		} catch (MQException e) {
			e.printStackTrace();
		}
	}

	// @Autowired
	// @Qualifier("MQProducerConnectionPool")
	// private JmsConnectionFactory MQCf;

	@Test
	// 13. invalid XML format
	public void testCase13() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(13);
	}

	@Test
	// 14. invalid SysId
	public void testCase14() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(14);
	}

	@Test
	// 15. invalid Source Address
	public void testCase15() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(15);
	}

	@Test
	// 16. Short Message Length
	public void testCase16() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(16);
	}

	@Test
	// 17. invalid Language
	public void testCase17() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(17);
	}

	@Test
	// 18. invalid target
	public void testCase18() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(18);
	}

	@Test
	// 19. spam check
	public void testCase19() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(19);
	}

	@Test
	// 20. ValidPeriod not belong to 0,1,2,3,4
	public void testCase20() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(20);
	}

	@Test
	// 21. water level reached (need to config cp and reset
	// quota)
	public void testCase21() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(21);
	}

	@Test
	// 30. Without ValidPeriods
	public void testCase30() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(30);
	}

	@Test
	// 31. Without ValidPeriod, with special character
	public void testCase31() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(31);
	}

	@Test
	// 32. With ValidPeriod=1
	public void testCase32() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(32);
	}

	@Test
	// 33. With DR=true
	public void testCase33() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(33);
	}

	@Test
	// 34. With DR=false, target=2
	public void testCase34() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(34);
	}

	@Test
	// 35. With DR=false, target=3
	public void testCase35() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(35);
	}

	@Test
	// 36. With DR=true, target=2
	public void testCase36() throws JMSException, UnsupportedEncodingException {
		testMsgWithIMQ(36);
	}

	// private void testMsg(int caseNum) {
	// MQJmsProducer producer = null;
	// try {
	// producer = new MQJmsProducer(MQCf);
	// producer.startConnection(destinationName);
	// String msg = caseNum == 14 ? SdpData.FORMAT2_TEST_CASE.get(caseNum)
	// : MQTestSuite.replaceXmlCpId(SdpData.FORMAT2_TEST_CASE.get(caseNum),
	// cpId);
	//
	// String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new
	// Date());
	// byte[] zipData = MQZipUtil.compress(msgStamp, msg.getBytes("UTF-8"));
	// log.debug("Data={}", msg);
	// log.debug("Zipped Data={}", new String(zipData));
	// producer.enqTextMsgA(new String(zipData));
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }finally {
	// producer.closeConnection();
	// }
	// }

	private void testMsgWithIMQ(int caseNum) {
		try {

			dao.connectQueue(MQHandler.getCPQueueName(cpId, "REQ"));
			String msgContent = caseNum == 14 ? SdpData.FORMAT2_TEST_CASE.get(caseNum)
					: MQTestSuite.replaceXmlCpId(SdpData.FORMAT2_TEST_CASE.get(caseNum), cpId);

			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			byte[] zipData = MQZipUtil.compress(msgStamp, msgContent.getBytes());
			log.debug("Data={}", msgContent);
			log.debug("zipData={}", zipData);
			dao.put(null, null, zipData);
			dao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
