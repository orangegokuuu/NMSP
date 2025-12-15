package com.ws.ibm.mq.testcase;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class EncodeTest {
	String data = "B4FAB8D5A668B5A73136";
	
	@Test
	public void test() throws UnsupportedEncodingException{
//		byte[] text = ByteUtil.hexStringToByteArray(data);
//		String encodedText = new String(text, "big5");
//		log.info("{}", text);
		String newText = new String("Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check, Length Check");
		String newEncodedText =  ByteUtil.byteArrayTohexString(newText.getBytes("big5"));
		log.info("{}", newEncodedText);
		
		byte[] text = ByteUtil.hexStringToByteArray(newEncodedText);
		String encodedText = new String(text, "big5");
		log.info("{}", encodedText);
		
		
		
	}
}
