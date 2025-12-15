package com.ws.ibm.mq.test;

import java.util.List;

import javax.jms.JMSException;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.ibm.mq.spring.SpringConfig;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.SmsRecordManager;
import com.ws.util.ByteUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings(value = { "unused" })
@ContextConfiguration(classes = SpringConfig.class, loader = AnnotationConfigContextLoader.class)
public class SACTest {
	
	@Autowired
	private ContentProviderManager cpManger;
	
	@Autowired
	private SmsRecordManager smsRecordManager;

	@Test
	public void testSAC3() {
		SmsRecord sms = smsRecordManager.get(SmsRecord.class, "1568476");
		System.out.println(sms.toString());
		System.out.println("Done");
	}
	
	@Test
	public void testSAC2() {
		ContentProvider cp = cpManger.get(ContentProvider.class, "MQTEST");
		System.out.println(cp.toString());
		System.out.println("Done");
	}

}
