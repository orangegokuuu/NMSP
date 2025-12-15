package com.ws.ibm.mq.testcase;

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

	private static String destinationName[] = { "SMS.VDPSMS1.REQ.Q", "SMS.VDPSMS2.REQ.Q", "SMS.VDPSMS3.REQ.Q",
	"SMS.VDPSMS4.REQ.Q" };
	
	private static final int SUBMIT_VOLUME = 250;
	private static final int NUM_OF_THREADS = 2;

	String testData[] = {
			// 1. Normal Message
			"31564450534d53315330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			"31564450534d53325330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			"31564450534d53335330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			"31564450534d53345330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
	};

	@Autowired
	@Qualifier("MQProducerConnectionPool1")
	private JmsConnectionFactory MQCf;
	
	@Autowired
	private Provider<SubmitTaskTest> submitTaskTestProvider;
	
	@Autowired
	@Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Test
	public void multiThreadsloadTest() throws JMSException, UnsupportedEncodingException {


		for (int i = 0; i < NUM_OF_THREADS; i++) {
			SubmitTaskTest submitTaskTest = submitTaskTestProvider.get();
			submitTaskTest.setDestinationName(destinationName[i]);
			submitTaskTest.setSUBMIT_VOLUME(SUBMIT_VOLUME);
			submitTaskTest.setTestData(testData[i]);
			submitTaskTest.init();
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

}
