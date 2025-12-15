package com.ws.ibm.mq.test.legacy;

import java.io.UnsupportedEncodingException;

import javax.inject.Provider;
import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.ibm.imq.manager.IMQProducer;
import com.ws.ibm.mq.handler.MQHandler;
import com.ws.ibm.mq.submitTask.FormatOneLoadTask;
import com.ws.ibm.test.config.TestConfig;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQMultiClientLoadTest {

	private static int threadNum = 5;
	private static final int SUBMIT_VOLUME = 100;

	// production config
//	private static final String queueName[] = { "SMS.MDPSMS1.REQ.Q", "SMS.MDPSMS2.REQ.Q", "SMS.MDPSMS3.REQ.Q",
//			"SMS.MDPSMS4.REQ.Q", "SMS.MDPSMS5.REQ.Q" };
//	private static final String qmgrName[] = { "DMZ.QM", "DMZ.QM2", "DMZ.QM4", "MGW.QM", "MGW.QM2" };
//	private static final String host[] = { "10.76.1.167", "10.76.1.168", "210.241.199.74", "10.76.91.26", "10.76.91.27" };
//	private static final int port[] = { 8080, 8080, 8002, 8080, 8080 };
//	private static final String cpId[] = { "MDPSMS1", "MDPSMS2", "MDPSMS3", "MDPSMS4", "MDPSMS5" };

	// lab config
	private static final String queueName[] = { "SMS.MDPSMS1.REQ.Q", "SMS.MDPSMS2.REQ.Q", "SMS.MDPSMS3.REQ.Q",
			"SMS.MDPSMS4.REQ.Q", "SMS.MDPSMS5.REQ.Q" };
	private static final String qmgrName[] = { "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3", "DMZ.QM3" };
	private static final String host[] = { "192.168.1.51", "192.168.1.51", "192.168.1.51", "192.168.1.51",
			"192.168.1.51" };
//	private static final String host[] = { "localhost", "localhost", "localhost", "localhost", "localhost" };
	private static final int port[] = { 1414, 1414, 1414, 1414, 1414 };
	private static final String cpId[] = { "MDPSMS1", "MDPSMS2", "MDPSMS3", "MDPSMS4", "MDPSMS5" };

	private static final String chl = "SMS.WS.CHL";
	private IMQProducer[] producers = new IMQProducer[5];

	String testData =
			// 1. Normal Message
			"314d4450534d53545330304230393831393331393134000000000000000000003031323334373230303030303030303030303030000000006c6f61642074657374";

	@Autowired
	private Provider<FormatOneLoadTask> formatOneLoadTaskProvider;

	@Autowired
	@Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	@Before
	public void init() {
		for (int i = 0; i < producers.length; i++) {
			producers[i] = new IMQProducer(qmgrName[i], host[i], port[i], chl);
			try {
				producers[i].start(queueName[i]);
			} catch (Exception e) {
				log.info("Exception occurred. Message=[{}]!", e.getMessage());
			}
		}
	}

	@Test
	public void loadTest() throws JMSException, UnsupportedEncodingException {
		for (int i = 0; i < 5; i++) {
			FormatOneLoadTask formatOneLoadTask = formatOneLoadTaskProvider.get();
			formatOneLoadTask.setCpId(cpId[i]);
			formatOneLoadTask.setSubmitVolume(SUBMIT_VOLUME);
			formatOneLoadTask.setTestData(MQTestSuite.replaceCpId(testData, cpId[i]));
			formatOneLoadTask.setProducer(producers[i]);

			for (int j = 0; j < threadNum; j++) {
				submitExecutor.submit(formatOneLoadTask);
			}
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

//	@After
//	public void end() {
//		for (int i = 0; i < producers.length; i++) {
//			producers[i].close();
//		}
//	}
}
