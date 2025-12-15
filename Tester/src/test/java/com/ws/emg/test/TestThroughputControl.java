//package com.ws.emg.test;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.hazelcast.core.HazelcastInstance;
//import com.ws.emg.http.test.UnitConfig;
//import com.ws.msp.pojo.ContentProvider;
//import com.ws.msp.service.ContentProviderManager;
//import com.ws.msp.service.QuotaManager;
//
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = UnitConfig.class)
//public class TestThroughputControl {
//
////	@RunWith(ConcurrentTestRunner.class)
////	public static class TestMultiThroughputControl {
////	    @BeforeClass
////	    public static void init() throws Exception {
////	      log.info("ConcurrentTestRunner init()");
////	    }
////	}
//
//	@Autowired
//	private ContentProviderManager contentProviderManager;
//
//	@Autowired
//	private QuotaManager quotaManager;
//
//	private List<ContentProvider> cps;
//
////	@Before
////	public void beforeInit(){
////		JUnitCore.runClasses(TestMultiThroughputControl.class);
////		log.info("ConcurrentTestRunner beforeInit()");
////	}
////	
//	@Before
//	public void init() {
//		cps = contentProviderManager.listAll(ContentProvider.class);
//		quotaManager.resetAll();
//	}
//
//	@Test
//	public void testSMSThroughput() {
//		
//		ContentProvider cp = cps.get(1); // TEST1, cp sms quota per hour = 100
////		quotaManager.initExecutor(); // start timer
//		
//		TimerTask timerTask = new TimerTask() {
//			@Override
//			public void run() {
//				quotaManager.processSmsSubmit(cp.getCpId(), 1);
//			}
//		};
//		Timer minutelyTimer = new Timer("minutelyTimer");
//		minutelyTimer.scheduleAtFixedRate(timerTask, new Date(), 10000L);
//
//
//		for (int x = 0; x < 60; x++) {
//    		quotaManager.processSmsSubmit(cp.getCpId(), 30);
//            try {
//    			Thread.sleep(5000L);
//    		} catch (InterruptedException e) {
//    			
//    		}
//        }
//	}
//
//	@Test
//	public void testQueryThroughput() {
//		
//		ContentProvider cp = cps.get(1); // TEST1
////		quotaManager.initExecutor(); // start timer
//		
//		TimerTask timerTask = new TimerTask() {
//			@Override
//			public void run() {
//				quotaManager.processQueryDr(cp.getCpId(), 1);
//			}
//		};
//		Timer minutelyTimer = new Timer("minutelyTimer");
//		minutelyTimer.scheduleAtFixedRate(timerTask, new Date(), 10000L);
//
//		
//		for (int x = 0; x < 60; x++) {
//    		quotaManager.processQueryDr(cp.getCpId(), 2);
//            try {
//    			Thread.sleep(5000L);
//    		} catch (InterruptedException e) {
//    			
//    		}
//        }
//	}
//	
//
//}
