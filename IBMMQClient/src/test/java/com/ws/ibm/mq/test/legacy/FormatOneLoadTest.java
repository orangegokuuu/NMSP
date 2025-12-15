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

import com.ws.ibm.mq.submitTask.FormatOneLoadTask;
import com.ws.ibm.test.config.TestConfig;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class FormatOneLoadTest {

	private static String cpId = "MDPSMST";
	private static final int SUBMIT_VOLUME = 3;

	private static final String qmgrName = "DMZ.QM3";
//	private static final String qmgrName = "MGW.QM";
	private static final String host = "192.168.1.51";
//	private static final String host = "localhost";
	private static final int port = 1414;
//	private static final int port = 8080;
	private static final String chl = "SMS.WS.CHL";
	
	private static int threadNum = 5;
	private static int duration = 120;

	String testData =
			// 1. Normal Message
			"314d4450534d53345330304230393033343933343734000000000000000000003031303030343130333030303030303530303030000000006c6f61642074657374";

	@Autowired
	private Provider<FormatOneLoadTask> formatOneLoadTaskProvider;

	@Autowired
	@Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Test
	public void multiThreadsloadTest() throws JMSException, UnsupportedEncodingException {
		FormatOneLoadTask formatOneLoadTask = formatOneLoadTaskProvider.get();
		formatOneLoadTask.setCpId(cpId);
		formatOneLoadTask.setSubmitVolume(SUBMIT_VOLUME);
		formatOneLoadTask.setTestData(MQTestSuite.replaceCpId(testData, cpId));
		formatOneLoadTask.init(qmgrName, host, port, chl);

		for (int i = 0; i < threadNum; i++) {
			submitExecutor.submit(formatOneLoadTask);
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

	
	@Test
	public void multiThreadsloadTest2() throws JMSException, UnsupportedEncodingException {
		FormatOneLoadTask formatOneLoadTask = formatOneLoadTaskProvider.get();
		formatOneLoadTask.setCpId(cpId);
		formatOneLoadTask.setSubmitVolume(SUBMIT_VOLUME);
		formatOneLoadTask.setTestData(MQTestSuite.replaceCpId(testData, cpId));
		formatOneLoadTask.init(qmgrName, host, port, chl);
		formatOneLoadTask.setDuration(duration);
		
		while(submitExecutor.getActiveCount() < threadNum) {
			submitExecutor.submit(formatOneLoadTask);
		}
		
		try {
			Thread.sleep((duration+1)*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		submitExecutor.shutdown();
	}
}
