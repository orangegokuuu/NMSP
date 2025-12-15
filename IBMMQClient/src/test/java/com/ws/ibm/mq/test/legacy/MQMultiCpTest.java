package com.ws.ibm.mq.test.legacy;

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
import com.ws.ibm.mq.submitTask.SubmitTaskTest2;
import com.ws.ibm.test.config.TestConfig;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQMultiCpTest {

	private static String destinationName = "SMS.MDPSMS1.REQ.Q";
	private static String destinationName2 = "SMS.SDPSMS1.REQ.Q";

	private static final int SUBMIT_VOLUME = 333;
	private static final int NUM_OF_THREADS = 2;

	String testData = "<?xml version=\"2.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
			+ "<SMS><SysId>SDPSMS1</SysId><Message>"
			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">0921761727</Target>"
			+ "<Source>01000410300000050001</Source><Language>B</Language><Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>";

	String testData2 = "314d4450534d53315330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d" ;

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
	
	@Autowired
	private Provider<SubmitTaskTest> submitTaskTestProvider;
	
	@Autowired
	private Provider<SubmitTaskTest2> submitTaskTestProvider2;
	
	@Autowired
	@Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Test
	public void multiThreadsloadTest() throws JMSException, UnsupportedEncodingException {
		SubmitTaskTest submitTaskTest = submitTaskTestProvider.get();
		submitTaskTest.setDestinationName(destinationName);
		submitTaskTest.setSUBMIT_VOLUME(SUBMIT_VOLUME);
		submitTaskTest.setMQCf(MQCf1);
		submitTaskTest.setTestData(testData2);
		submitTaskTest.init();

		SubmitTaskTest2 submitTaskTest2 = submitTaskTestProvider2.get();
		submitTaskTest2.setDestinationName(destinationName2);
		submitTaskTest2.setSUBMIT_VOLUME(SUBMIT_VOLUME);
		submitTaskTest2.setMQCf(MQCf1);
		submitTaskTest2.setTestData(testData);
		submitTaskTest2.init();

		for (int i = 0; i < NUM_OF_THREADS; i++) {
			submitExecutor.submit(submitTaskTest);
		}

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
