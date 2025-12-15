package com.ws.ibm.mq.sit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.ws.ibm.mq.util.MQZipUtil;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MQJmsConfig.class, loader = AnnotationConfigContextLoader.class)
@EnableJms
@Log4j2
public class MQFormatTwoTest {
	
	private static String destinationName = "SMS.SDPSMST.REQ.Q";
	
	String testData[] = {
//			// 13. invalid XML format 
//			"<?xml version=\"3.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language><Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 14. invalid SysId
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SXPSMS1</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language><Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//
//			// 15. invalid Source Address
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>00000410300000050001</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//
//			// 16. Short Message Length
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6FesFSup6r4q9e0+rjVsFSup6r4q9e0+rjV</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//
//			// 17. invalid Language
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>X</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// valid target
//			// 18a. target = '9' start, length = 9
//			// 18b. target = '09' start, length = 10
//			// 18c. target = '8869' start, length = 12
////			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
////			+ "<SMS><SysId>SDPSMST</SysId><Message>"
////			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
////			+ "<Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">903493474</Target>"
////			+ "<Target token=\"05BF640FD2F1C9E6006E41B53FD3D091\">903524540</Target>"
////			+ "<Source>01234720000000000000</Source><Language>B</Language>"
////			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
////			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// invalid target
//			// 18d. target = '+' start, length = 6~20 after remove '+'
//			// 18. not belong to cases above
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">+921761727</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">1912144168</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 19. spam check 
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBc3BhbSB0ZXN0IGZ1Y2s=</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 20. ValidPeriod not belong to 0,1,2,3,4 
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>6</ValidPeriod></Message></SMS>", 
//			
//			// 21. water level reached (need to config cp and reset quota)
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>",
//			
//			// 30. Without ValidPeriod
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag></Message></SMS>", 
//			
//			// 31. Without ValidPeriod, with special character
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
			+ "<SMS><SysId>SDPSMS1</SysId><Message>"
			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">903493474</Target>"
			+ "<Source>01234720000000000000</Source><Language>E</Language>"
			+ "<Text>QKMkpejp+ezyx9j4xeU/Xz8/Pz8/Pz8/P157fVxbfl18P8bm3w==</Text>"
			+ "<DrFlag>false</DrFlag></Message></SMS>", 
//			
//			// 32. With ValidPeriod=1
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>1</ValidPeriod></Message></SMS>", 
//			
//			// 33. With DR=true
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>true</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 34. With DR=false, target=2
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">903493474</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 35. With DR=false, target=3
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">903493474</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B53FD3D091\">903524540</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>false</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
//			// 36. With DR=true, target=2
//			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE SMS SYSTEM \"sms.dtd\">"
//			+ "<SMS><SysId>SDPSMST</SysId><Message>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B5936973E7\">966942694</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B516FDD525\">903493474</Target>"
//			+ "<Target token=\"05BF640FD2F1C9E6006E41B53FD3D091\">903524540</Target>"
//			+ "<Source>01234720000000000000</Source><Language>B</Language>"
//			+ "<Text>sXqmbqHjsXqp81BVRklJsNOrsLBotKuzZqzbw/aw3cNEoUGkd6nzsNOrsKtIpfOmXsLQsXqhQb3QsXq6yafWptyrSL1jvVS7e6Fdpqyl86eoqFOms6VpptypVaejq0il86zdrN2hXqFBwcLBwrF6oV2kxaZesFSup6Fe</Text>"
//			+ "<DrFlag>true</DrFlag><ValidPeriod>0</ValidPeriod></Message></SMS>", 
//			
			// 37. Receive Format 2 MO
			// call smscsim sendMo
			
	};
	

	@Autowired
	@Qualifier("MQProducerConnectionPool1")
	private JmsConnectionFactory MQCf;

	@Test
	public void testCase() throws JMSException, IOException {
		MQJmsProducer producer = null;
		try {
			producer = new MQJmsProducer(MQCf);
			producer.startConnection(destinationName);
//			while (true) {
				for (int i = 0; i < testData.length; i++) {
					String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
					byte[] zipData = MQZipUtil.compress(msgStamp, testData[i].getBytes("UTF-8"));
					log.debug("Data={}", testData[i]);
					log.debug("Zipped Data={}", new String(zipData));
					producer.enqBytesMsgA(zipData);
				}
//			}
		} finally {
			if (producer != null) {
				producer.closeConnection();
			}
		}

	}
}
