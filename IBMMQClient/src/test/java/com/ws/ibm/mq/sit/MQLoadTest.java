package com.ws.ibm.mq.sit;

import java.io.UnsupportedEncodingException;

import javax.inject.Provider;
import javax.jms.JMSException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.submitTask.SubmitTaskTest;
import com.ws.ibm.test.config.TestConfig;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQLoadTest {
	
	private static String destinationName = "SMS.MDPSMST.REQ.Q";
	private static final int SUBMIT_VOLUME= 333;
	
	String testData = 
			// 1. Normal Message
			"314d4450534d53545330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d";

	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQCf;
	
	@Autowired
	private Provider<SubmitTaskTest> submitTaskTestProvider;
	
	@Autowired
    @Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Test
	public void multiThreadsloadTest() throws JMSException, UnsupportedEncodingException {
		SubmitTaskTest submitTaskTest = submitTaskTestProvider.get();
		submitTaskTest.setDestinationName(destinationName);
		submitTaskTest.setSUBMIT_VOLUME(SUBMIT_VOLUME);
		submitTaskTest.setTestData(testData);
		submitTaskTest.init();
		// 10 threads
		for (int i = 0; i < 3; i++) {
			submitExecutor.submit(submitTaskTest);
		}
		
		for (;;) {
			int count = submitExecutor.getActiveCount();
			System.out.println("Active Threads : " + count);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count == 0) {
				submitExecutor.shutdown();
				break;
			}
		}
		
		
	}
	
	
//	@Test
//	public void loadTest() throws JMSException, UnsupportedEncodingException {
//		MQJmsProducer producer = null;
//		try {	
//			producer = new MQJmsProducer(MQCf);
//			producer.startConnection(destinationName);
//			for (int i = 0; i < SUBMIT_VOLUME; i++) {
//				log.info("loop {}", i);
//				for (int j = 0; j < testData1.length; j++) {
//					log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData1[j])));
//					log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData1[j])));
//					producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(testData1[j]));
//				}
//			}
//
//		} finally {
//			if (producer != null) {
//				producer.closeConnection();
//			}
//		}
//	}
	


}
