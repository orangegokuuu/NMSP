package com.ws.jms.test;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.emg.pojo.MessageObject;
import com.ws.jms.configuration.Jmsconfig;
import com.ws.jms.service.JmsService;
import com.ws.smpp.SmsRequest;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.ws.jms.enqueue")
@ContextConfiguration(classes = Jmsconfig.class, loader = AnnotationConfigContextLoader.class)
@Log4j2
public class SenderTest {

//	@Autowired
//	JmsTemplate jmsTemplate;
	
	private int queryThread = 2;

	private int queryTime = 30;

	@Autowired
	JmsService jmsService;

	@Test
	public void sender() {

		int queryTime = 30;
		Date start = new Date();
		Date endTime = new Date(start.getTime() + (queryTime * 1000));
		long success = 0;
		long fail = 0;
		long i = 0;
		long maxElapsed = 0;

		while (System.currentTimeMillis() < endTime.getTime()) {
			try {
				// TODO enqueue
				
				MessageObject msg = new MessageObject();
				msg.setWsMessageId("" + i);
				msg.setDataCoding(SmsRequest.DCS_UCS2);
				msg.setMessage("this message number is " + i);
				//jmsService.sendMsg(msg, "sms.mt",4);
				jmsService.sendMessage(msg, 4, "sms.mt");
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
		
		log.info("Not PooledConnectionFactory ,Success ["+success+"] Failed dipping in ["+fail+"] seconds ["+(end.getTime() - start.getTime()) / 1000+"]");
		
//		start = new Date();
//		endTime = new Date(start.getTime() + (queryTime * 1000));
//		success = 0;
//		fail = 0;
//		i = 0;
//		maxElapsed = 0;
//
//		while (System.currentTimeMillis() < endTime.getTime()) {
//			try {
//				// TODO enqueue
//				
//				MessageObject msg = new MessageObject();
//				msg.setWsMessageId("" + i);
//				msg.setDataCoding(SmsRequest.DCS_UCS2);
//				msg.setMessage("this message number is " + i);
//				jmsService.sendMsg(msg, "sms.mt",4);
//				i++;
//				success += 1;
//
//			} catch (Exception e) {
//				// logger.warn(e.getMessage());
//				fail += 1;
//			}
//
//		}
//		end = new Date();
//		elapsed = ((end.getTime() - start.getTime()) / 1000);
//		if (elapsed > maxElapsed) {
//			maxElapsed = elapsed;
//		}
//		
//		System.out.println("Use PooledConnectionFactory ,Success ["+success+"] Failed dipping in ["+fail+"] seconds ["+(end.getTime() - start.getTime()) / 1000+"]");
		
//		MessageObject msg;
//		for (int i = 1; i < 10; i++) {
//			msg = new MessageObject();
//			msg.setWsMessageId("" + i);
//			msg.setDataCoding(SmsRequest.DCS_UCS2);
//			msg.setMessage("this message number is " + i);
//			System.out.println("message:" + msg.toString());
//			jmsService.sendMessage(msg, i, "sms.mo.intra");
//			//jmsService.send(msg, i, "sms.mo.intra");
//		}
		

	}
	
//	@Test
//	public void testEnqueue() {
//		log.info("Query Start using [{}] client for [{}]second\n", queryThread, queryTime);
//		for (int i = 0; i < queryThread; i++) {
//			Thread t = new TestThread(UUID.randomUUID().toString());
//			t.start();
//		}
//		try {
//			Thread.sleep((queryTime+10)*1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	class TestThread extends Thread {
//
//		public TestThread(String id) {
//			super(id);
//		}
//
//		public void run() {
//			//int queryTime = 60;
//			Date start = new Date();
//			Date endTime = new Date(start.getTime() + (queryTime * 1000));
//			long success = 0;
//			long fail = 0;
//			long i = 0;
//			long maxElapsed = 0;
//
//			while (System.currentTimeMillis() < endTime.getTime()) {
//				try {
//					// TODO enqueue
//					
//					MessageObject msg = new MessageObject();
//					msg.setWsMessageId("" + i);
//					msg.setDataCoding(SmsRequest.DCS_UCS2);
//					msg.setMessage("this message number is " + i);
//					//jmsService.sendMsg(msg, "sms.mt",4);
//					jmsService.sendMessage(msg, 4, "sms.mt");
//					i++;
//					success += 1;
//
//				} catch (Exception e) {
//					// logger.warn(e.getMessage());
//					fail += 1;
//				}
//
//			}
//			Date end = new Date();
//			long elapsed = ((end.getTime() - start.getTime()) / 1000);
//			if (elapsed > maxElapsed) {
//				maxElapsed = elapsed;
//			}
//			
//			log.info("[{}] [{}] Success [{}] Failed dipping in [{}] seconds \n", this.getName(), success, fail,
//					(end.getTime() - start.getTime()) / 1000);
//		}
//	}
}
