package com.ws.emg.loadTest;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ws.emg.pojo.MessageObject;
import com.ws.jms.message.JmsProducer;
import com.ws.jms.service.JmsService;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.service.SmsRecordManager;
import com.ws.smpp.SmsRequest;

@Component
public class EnqueueTest {
	private static Logger logger = LogManager.getLogger(EnqueueTest.class);

	@Value("${test.enqueue.client:200}")
	private int queryThread = 2;

	@Value("${test.enqueue.client.time:60}")
	private int queryTime = 30;

	long maxElapsed = 0;
	
	@Autowired
	JmsService jmsService;
	
	@Autowired
	JmsProducer jmsProducer;
	
//	@Autowired
//	SmsRecordManager smsRecordManager;

	public void testEnqueue() {

		logger.info("Query Start using [{}] client for [{}]second\n", queryThread, queryTime);
		for (int i = 0; i < queryThread; i++) {
			Thread t = new TestThread(UUID.randomUUID().toString());
			t.start();
		}
	}

	class TestThread extends Thread {

		public TestThread(String id) {
			super(id);
		}

		public void run() {
			Date start = new Date();
			Date endTime = new Date(start.getTime() + (queryTime * 1000));
			long success = 0;
			long fail = 0;
			long i = 0;
//			jmsProducer.connect("sms.mo.ibm.inter");
			while (System.currentTimeMillis() < endTime.getTime()) {
				try {
					// TODO insert db 
//					SmsRecord sms = new SmsRecord();
//					sms.setWsMsgId(String.valueOf(i));
//					sms.setSysId("55512");
//					sms.setDa("0988123123");
//					sms.setOa("55512");
//					sms.setLanguage("E");
//					sms.setText("this message number is " + i);
					//smsRecordManager.save(SmsRecord.class, sms);
					// TODO enqueue
					
					MessageObject msg = new MessageObject();
					msg.setWsMessageId("" + i);
					msg.setDataCoding(SmsRequest.DCS_UCS2);
					msg.setMessage("this message number is " + i);
//					jmsService.sendMsg(msg, "sms.mo.ibm.inter", 4);
//					jmsProducer.send(msg, 4, "");
					jmsService.sendMessage(msg, 4, "sms.mt");
//					jmsService.sendTextMessage("hello queue world", "sms.mt");
					i++;
					success += 1;

				} catch (Exception e) {
					// logger.warn(e.getMessage());
					fail += 1;
				}

			}
			Date end = new Date();
			long elapsed = ((end.getTime() - start.getTime()) / 1000);
			if (elapsed > maxElapsed) {
				maxElapsed = elapsed;
			}
			logger.info("[{}] [{}] Success [{}] Failed dipping in [{}] seconds \n", this.getName(), success, fail,
					(end.getTime() - start.getTime()) / 1000);
		}
	}

}