package com.ws.ibm.mq.test.legacy;

import org.junit.runner.RunWith;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.ws.ibm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableJms
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class MQReceiveTest {

	public static String getHexString(byte[] b) throws Exception {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
	
	
	
}
