package com.ws.ibm.mq.test.legacy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ws.ibm.imq.dao.SubmitMessageDAO;
import com.ws.ibm.imq.pojo.MPInternalMessage;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQSubmitTest {

	private static final String qmgrName = "DMZ.QM3";
//	private static final String host = "10.76.1.166";
//	private static final int port = 8080;

	private static final String host = "192.168.1.51";
	private static final int port = 1414;

	private static final String chl =  "SMS.WS.CHL";
	private static final String queueName =  "SMS.MDPSMST.REQ.Q";
	
	private static final int loop = 100;
	private static final String testData = "314d4450534d5354533031423039383139333139313400000000000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136";
	
	private SubmitMessageDAO dao = null;
	
	@Before
	public void init(){
		dao = new SubmitMessageDAO( qmgrName, host, port, chl);
	}
	
	@Test
	public void enqTest() {
		try {
			dao.connect();
			MPInternalMessage msg = new MPInternalMessage();
//			msg.setMqMsgId("Acknowledge".getBytes());
			msg.setMqMsgBody(ByteUtil.hexStringToByteArray(testData));
			for (int i = 0; i < loop; i++) {
				log.debug("enq msg[{}]", i);
				dao.put(msg, queueName);
			}
			dao.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.disconnect();
		}
	}
	
	
	
	
}
