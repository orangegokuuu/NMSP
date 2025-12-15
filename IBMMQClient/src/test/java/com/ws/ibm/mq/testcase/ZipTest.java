package com.ws.ibm.mq.testcase;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@Log4j2
public class ZipTest {

	String data = 
			"504b03041400080808007b88414c0000000000000000000000000d00000031383032303131373033"+
			"303534b3b1afc8cd51284b2d2acecccfb35532d433505248cd4bce4fc9cc4bb7550a0d71d3b55052"+
			"282e49cc4b49ccc9cf4bb555aa4c2d56b2b7e3b2f10d084acd4a4d2eb1e35200021bd7a2a2fc22e7"+
			"fc94543b430303031b7d041f49de25b538b928b3a00468959d675e59624e668a4284af8f425a7e51"+
			"6e62095413b2222e1b7d843d00504b0708608d3f4d82000000ab000000504b010214001400080808"+
			"007b88414c608d3f4d82000000ab0000000d00000000000000000000000000000000003138303230"+
			"3131373033303534504b050600000000010001003b000000bd0000000000";
	
	@Test
	public void test() throws IOException{
		byte[] orgData = MQZipUtil.decompress(ByteUtil.hexStringToByteArray(data));
		log.info("Original Data = {}", new String(orgData));
		
	}
	
}
