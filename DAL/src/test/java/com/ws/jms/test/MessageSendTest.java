package com.ws.jms.test;

import java.util.Date;

import javax.jms.JMSException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


import com.ws.api.util.HttpApiUtils;
import com.ws.emg.pojo.MessageObject;
import com.ws.jms.configuration.Jmsconfig;
import com.ws.jms.message.JmsProducer;
import com.ws.jms.service.JmsService;
import com.ws.smpp.SmsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Jmsconfig.class, loader = AnnotationConfigContextLoader.class)
public class MessageSendTest {

	@Autowired
	JmsService jmsService;
	
	@Autowired
	JmsProducer jmsProducer;

	@Test
	public void sender() {
		//jmsProducer.connect("sms.mo.ibm.inter");
		MessageObject msg;
		
//		for (int i = 1; i <= 500; i++) {
//			msg = new MessageObject();
//			msg.setWsMessageId("" + i);
//			msg.setDataCoding(SmsRequest.DCS_UCS2);
//			msg.setMessage("this message number is " + i);
//			System.out.println("message id:" + msg.getWsMessageId());
//			//jmsProducer.send(msg, 4, "sms.mo.ibm.inter");
//			jmsService.sendMsg(msg, "sms.mo.ibm.inter",4);
//			//jmsService.sendDelayMsg(msg, "sms.mt", 20000, 4);
//			//System.out.println("date:" + new Date());
//		}
		//jmsProducer.commit();
		try {
			msg = new MessageObject();
			msg.setWsMessageId("1");
			msg.setDataCoding(SmsRequest.DCS_UCS2);
			msg.setMessage("this message number is 1" );
			System.out.println("message id:" + msg.getWsMessageId()+"date:"+System.currentTimeMillis());
			long start = System.currentTimeMillis();
			//jmsService.sendMsg(msg, "sms.mt", 9);
			System.out.println("send");
			jmsService.sendDelayMessage(msg, "sms.mt", 10000, 4);
			//jmsService.sendDelayMsg(msg, "sms.mt", 25000, 4);
			Thread.sleep(20000);
//			System.out.println("============================");
//			System.out.println("============================");
//			System.out.println("============================");
//			System.out.println("date:"+System.currentTimeMillis());
			//jmsTemplate.receive();
			//jmsService.receiverMessage("sms.mt");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void deliversm() {
//		MessageObject msg = new MessageObject();
//		msg.setCpId("TEST1");
//		msg.setWsMessageId("3076ef1f00694be6a636e927a99cf896");
//		msg.setDestination("0909000001");
//		msg.setSource("55511");
//		msg.setSumitTime(HttpApiUtils.formatDate("yyMMddHHmm", new Date()));
//		msg.setMessage("Hi, you.嗨，妳");
//		msg.setDataCoding(SmsRequest.UCS2);
//		msg.setState("DELIVRD");
//		msg.setStatus("0000");
//		msg.setErrorCode("0000");
//		jmsService.sendMessage(msg, "sms.dr.intra");
//	}
}
