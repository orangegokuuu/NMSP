package com.ws.ibm.mq.sit;

import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;

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
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQFormatOneTest {
	
	private static String destinationName = "SMS.MDPSMST.REQ.Q";
	
	String testData1[] = {
			// 1. invalid version 2MDPSMSTS01B0981931914
			"324d4450534d53545330314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			// 2. invalid sysId (not exist) 1XDPSMSTS01B0981931914
			"31584450534d53545330314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			// 3. invalid type 1MDPSMSTX01B0981931914
			"314d4450534d53545830314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			// 4. invalid dr flag 1MDPSMSTSX1B0981931914
			"314d4450534d53545358314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			// 5. invalid ack flag 1MDPSMSTS0XB0981931914
			"314d4450534d53545330584230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			// 6. invalid language 1MDPSMSTS01X0981931914
			"314d4450534d53545330315830393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			
//			// invalid target
//			// 7a. target = '9' start, length = 9
//			"314D515445535431533031423938313933313931340000000000000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136", 
//			// 7b. target = '09' start, length = 10//
//			"314D5154455354315330314230393831393331393134000000000000000000003031303030343/13033303030303030353030303000000000B4FAB8D5A668B5A73136", 
//			// 7c. target = '8869' start, length = 12
//			"314D515445535431533031423838363938313933313931340000000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136",
//			// 7d. target = '+' start, length = 6~20 after remove '+'
//			"314D515445535431533031422B38383639383139333139313400000000000000303130303034313033303030303030353030303000000000B4FAB8D5A668B5A73136",
//			// 7. not belong to cases above. 1MDPSMSTS00B9981931914
			"314d4450534d53545330314239393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d",
						
			// 8. invalid source 1MDPSMSTS01B0981931914
			"314d4450534d53545330314230393831393331393134000000000000000000003030303030343130333030303030303530303030000000006447567a64413d3d", 

			// 11. spam check 1MDPSMSTM01B01B0981931914
			"314d4450534d53545330314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000005370616D206B6579776F726420436865636B2C206675636B2E", 
			
			// 12.invalid message length (length = 164, big5） 1MDPSMSTM01B01B0981931914
			"314d4450534d53545330314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c633352305a584e306447567a6448526c6333513d"  
	};
	
	String testData1b[] = {
			// 9. invalid count (count = E) 1MDPSMSTM01B
			"314d4450534d53544d30314200000000000000000000000000000000000000003031303838383931323030303130303030303030450000006447567a64413d3d", 
			"30393535363238333032000000000000000000003039323632393639313800000000000000000000", 
			// 10. invalid count (count = 1) 1MDPSMSTM01B
			"314d4450534d53544d30314200000000000000000000000000000000000000003031303838383931323030303130303030303030320000006447567a64413d3d", 
			"3039353536323833303200000000000000000000", 
	};
	

	String testData2[] = {    
			// 22. MT with ack 1MDPSMSTS01B0981931914
//			"314d4450534d53545330314230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 
			
			// 23. MT without ack 1MDPSMSTS00B0981931914
//			"314d4450534d53545330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d", 

			// 24. MT without ack with special character 1MDPSMSTS01B0981931914
			"314d4450534d53545330304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000005143596a4d54597a4f79516d497a45324e54736d497a497a4d6a736d497a497a4d7a736d497a49304f54736d497a497a4e6a736d497a49304d6a736d497a45354f54736d497a49784e6a76492b79596a4d546b334f79596a4d6a49354f31396565333163573335646643596a4d6a4d774f79596a4d6a497a4f79436a52364e596f30616a54714e626f314f6a57714e566f30756a556150684a694d784f5467374a694d794d444537",
							
			// 25. MT with DR 1MDPSMSTS10B0981931914
//			"314d4450534d53545331304230393831393331393134000000000000000000003031303030343130333030303030303530303030000000006447567a64413d3d"
	};	
	
	String testData2b[] = {
			// 26. Multi-Mt with ack 1MDPSMSTM01B
			"314d4450534d53544d30304200000000000000000000000000000000000000003031303030343130333030303030303530303030320000006447567a64413d3d", 
			"30393535363238333032000000000000000000003039323632393639313800000000000000000000", 
			
			// 27. Multi-Mt without ack 1MDPSMSTM00B
			"314d4450534d53544d30304200000000000000000000000000000000000000003031303030343130333030303030303530303030320000006447567a64413d3d", 
			"30393535363238333032000000000000000000003039323632393639313800000000000000000000", 
			
			// 28. Multi-Mt with dr 1MDPSMSTM10B
			"314d4450534d53544d31304200000000000000000000000000000000000000003031303030343130333030303030303530303030320000006447567a64413d3d", 
			"30393535363238333032000000000000000000003039323632393639313800000000000000000000", 
			
			// 29. Receive Format1 MO message
			// call smscsim sendMo
	};

	
	
	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQCf;

	@Test
	public void testCase() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {	
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < testData1.length; i++) {
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData1[i])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData1[i])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(testData1[i]));
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}
	}
	
	@Test
	public void testCase1b() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < testData1b.length; i+=2) {
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData1b[i])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(testData1b[i])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData1b[i+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(testData1b[i+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(testData1b[i]), ByteUtil.hexStringToByteArray(testData1b[i+1]));
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}
	}
	
	
	
	@Test
	public void testCase2() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < testData2.length; i++) {
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData2[i])));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData2[i])));
				producer.enqBytesMsgA(ByteUtil.hexStringToByteArray(testData2[i]));
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}
	}
	
	@Test
	public void testCase2b() throws JMSException, UnsupportedEncodingException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
			for (int i = 0; i < testData2b.length; i+=2) {
				log.debug("Msg A byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData2b[i])));
				log.debug("Msg A String={}", new String(ByteUtil.hexStringToByteArray(testData2b[i])));
				log.debug("Msg C byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData2b[i+1])));
				log.debug("Msg C String={}", new String(ByteUtil.hexStringToByteArray(testData2b[i+1])));
				producer.enqBytesMsgAwithC(ByteUtil.hexStringToByteArray(testData2b[i]), ByteUtil.hexStringToByteArray(testData2b[i+1]));
			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}
	}
}
