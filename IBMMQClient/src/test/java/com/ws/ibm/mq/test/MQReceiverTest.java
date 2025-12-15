package com.ws.ibm.mq.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.handler.MQHandler;
import com.ws.ibm.mq.spring.SpringConfig;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.ContentProviderManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class, loader = AnnotationConfigContextLoader.class)
@Import(SpringConfig.class)
public class MQReceiverTest {

	private static final String cpId = "MDPSMST";

	@Autowired
	@Qualifier("MQConsumerConnectionPool")
	private JmsConnectionFactory MQCf;

	@Autowired
	private Provider<MQHandler> mqHandlerProvider;
	
	@Autowired
    @Qualifier("receiveExecutor")
	private ThreadPoolTaskExecutor taskExecutor = null;

	@Autowired
	private ContentProviderManager cpManager = null;
	private ContentProvider cp = null;

	@Before
	public void init() {
		cp = cpManager.get(ContentProvider.class, cpId);
	}

	@Test
	public void testMultiReceive(){
		for (int i = 0; i < 3; i++) {
			MQHandler mqHandler = mqHandlerProvider.get();
			mqHandler.init(cp);
			taskExecutor.submit(mqHandler);
		}
		
		for (;;) {
			int count = taskExecutor.getActiveCount();
			System.out.println("Active Threads : " + count);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count == 0) {
				taskExecutor.shutdown();
				break;
			}
		}
		
	}

}
