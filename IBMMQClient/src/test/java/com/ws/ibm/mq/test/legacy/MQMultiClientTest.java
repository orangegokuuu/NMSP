package com.ws.ibm.mq.test.legacy;

import java.io.UnsupportedEncodingException;

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
import com.ws.ibm.imq.pojo.MPInternalMessage;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.test.data.MdpData;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQMultiClientTest {

	// production
//	private static String queueName[] = { "SMS.MDPSMS1.REQ.Q", "SMS.MDPSMS2.REQ.Q", "SMS.MDPSMS3.REQ.Q",
//			"SMS.MDPSMS4.REQ.Q", "SMS.MDPSMS5.REQ.Q" };
//
//	private static final String qmgrName[] = { "DMZ.QM", "DMZ.QM2", "DMZ.QM4", "MGW.QM", "MGW.QM2" };
//	private static final String host[] = { "10.76.1.167", "10.76.1.168", "210.241.199.74", "10.76.91.26", "10.76.91.27" };
//	private static final int port[] = {8080, 8080, 8002, 8080, 8080};
//	private static final String chl = "SMS.WS.CHL";
//
//	private SubmitMessageDAO[] daos = new SubmitMessageDAO[5];
//	private String cpId[] = { "MDPSMS1", "MDPSMS2", "MDPSMS3", "MDPSMS4", "MDPSMS5" };
	
	// lab
	private static String queueName[] = { "SMS.MDPSMST.REQ.Q", "SMS.MDPSMST.REQ.Q", "SMS.MDPSMST.REQ.Q",
			"SMS.MDPSMST.REQ.Q", "SMS.MDPSMST.REQ.Q" };

	private static final String qmgrName[] = { "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3" };
	private static final String host[] = { "192.168.1.51", "192.168.1.51", "192.168.1.51", "192.168.1.51", "192.168.1.51" };
	private static final int port[] = {1414, 1414, 1414, 1414, 1414};
	private static final String chl = "SMS.WS.CHL";

	private SubmitMessageDAO[] daos = new SubmitMessageDAO[5];
	private String cpId[] = { "MDPSMST", "MDPSMST", "MDPSMST", "MDPSMST", "MDPSMST" };

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
	// 1. invalid version 2MDPSMSTS01B0981931914
	public void testCase1() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(1);
	}

	@Test
	// 2. invalid sysId (not exist) 1XDPSMSTS01B0981931914
	public void testCase2() throws JMSException, UnsupportedEncodingException {
		testSingleMsg("XDPSMST", 2);
	}

	@Test
	// 3. invalid type 1MDPSMSTX01B0981931914
	public void testCase3() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(3);
	}

	// 4. invalid dr flag 1MDPSMSTSX1B0981931914
	@Test
	public void testCase4() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(4);
	}

	@Test
	// 5. invalid ack flag 1MDPSMSTS0XB0981931914
	public void testCase5() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(5);
	}

	@Test
	// 6. invalid language 1MDPSMSTS01X0981931914
	public void testCase6() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(6);
	}

	@Test
	// 7. not belong to cases above. 1MDPSMSTS00B9981931914
	public void testCase7() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(7);
	}

	@Test
	// 8. invalid source 1MDPSMSTS01B0981931914
	public void testCase8() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(8);
	}

	// Multi-message
	@Test
	// 9. invalid count (count = E) 1MDPSMSTM01B
	public void testCase9() throws JMSException, UnsupportedEncodingException {
		testMultiMsg(9);
	}

	// Multi-message
	@Test
	// 10. invalid count (count = 1) 1MDPSMSTM01B
	public void testCase10() throws JMSException, UnsupportedEncodingException {
		testMultiMsg(10);
	}

	@Test
	// 11. spam check 1MDPSMSTM01B01B0981931914
	public void testCase11() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(11);
	}

	@Test
	// 12.invalid message length (length = 164, big5）
	public void testCase12() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(12);
	}

	@Test
	// 22. MT with ack 1MDPSMSTS01B0981931914
	public void testCase22() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(22);
	}

	@Test
	// 23. MT without ack 1MDPSMSTS00B0981931914
	public void testCase23() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(23);
	}

	@Test
	// 24. MT without ack with special character 1MDPSMSTS01B0981931914
	public void testCase24() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(24);
	}

	@Test
	// 25. MT with DR 1MDPSMSTS10B0981931914
	public void testCase25() throws JMSException, UnsupportedEncodingException {
		testSingleMsg(25);
	}

	// Multi-message
	@Test
	// 26. Multi-Mt with ack 1MDPSMSTM01B
	public void testCase26() throws JMSException, UnsupportedEncodingException {
		testMultiMsg(26);
	}

	// Multi-message
	@Test
	// 27. Multi-Mt without ack 1MDPSMSTM00B
	public void testCase27() throws JMSException, UnsupportedEncodingException {
		testMultiMsg(27);
	}

	// Multi-message
	@Test
	// 28. Multi-Mt with dr 1MDPSMSTM10B
	public void testCase28() throws JMSException, UnsupportedEncodingException {
		testMultiMsg(28);
	}

	private void testSingleMsg(String cpId, int caseNum){
		MPInternalMessage msg = new MPInternalMessage();
		for (int i = 0; i < 5; i++) {
			try {
				msg.setMqMsgBody(
						ByteUtil.hexStringToByteArray(MQTestSuite.replaceCpId(MdpData.FORMAT1_TEST_CASE.get(caseNum), cpId)));
				log.debug("enq msg[{}]", msg);
				daos[i].put(msg, queueName[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void testSingleMsg(int caseNum) {
		MPInternalMessage msg = new MPInternalMessage();
		String id = null;
		for (int i = 0; i < 5; i++) {
			try {
				id = this.cpId[i];
				msg.setMqMsgBody(ByteUtil
						.hexStringToByteArray(MQTestSuite.replaceCpId(MdpData.FORMAT1_TEST_CASE.get(caseNum), id)));
				log.debug("enq msg[{}]", msg);
				daos[i].put(msg, queueName[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void testMultiMsg(int caseNum) {
		MPInternalMessage msgA = new MPInternalMessage();
		MPInternalMessage msgC = new MPInternalMessage();
		String id = null;
		for (int i = 0; i < 5; i++) {
			try {
				id = this.cpId[i];
				msgA.setMqMsgBody(ByteUtil
						.hexStringToByteArray(MQTestSuite.replaceCpId(MdpData.FORMAT1_TEST_CASE_M.get(caseNum), id)));
				log.debug("enq msgA[{}]", msgA);
				byte[] msgId = daos[i].put(msgA, queueName[i]);

				msgC.setMqMsgId(msgId);
				msgC.setMqCorrelationId(msgId);
				msgC.setMqMsgBody(ByteUtil.hexStringToByteArray(MdpData.FORMAT1_TEST_CASE_M_T.get(caseNum)));
				log.debug("enq msgC[{}]", msgC);

				daos[i].put(msgC, queueName[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@After
	public void end() {
		for (int i = 0; i < daos.length; i++) {
			daos[i].disconnect();
		}
	}
}
