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
import com.ws.ibm.mq.submitTask.SubmitTaskTest2;
import com.ws.ibm.test.config.TestConfig;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQLoadTest2 {

	private static String destinationName = "SMS.SDPSMST.REQ.Q";
	private static final int SUBMIT_VOLUME= 333;
	private static final int NUM_OF_THREADS = 3;
	
	String testData = "<?xml version=\"2.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
			+ "<SMS><SysId>SDPSMST</SysId><Message>"
			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">0921761727</Target>"
			+ "<Source>01000410300000050001</Source><Language>B</Language><Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>";


	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQCf;
	
	@Autowired
	private Provider<SubmitTaskTest2> submitTaskTestProvider;
	
	@Autowired
    @Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Test
	public void multiThreadsloadTest() throws JMSException, UnsupportedEncodingException {
		SubmitTaskTest2 submitTaskTest2 = submitTaskTestProvider.get();
		submitTaskTest2.setDestinationName(destinationName);
		submitTaskTest2.setSUBMIT_VOLUME(SUBMIT_VOLUME);
		submitTaskTest2.setTestData(testData);
		submitTaskTest2.init();
		
		for (int i = 0; i < NUM_OF_THREADS; i++) {
			submitExecutor.submit(submitTaskTest2);
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

}
