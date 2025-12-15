package com.ws.emg.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ws.emg.pojo.MessageObject;
import com.ws.emg.test.TestParser.TestParserConfig;
import com.ws.emg.util.EmgParser;
import com.ws.msp.config.MspProperties;
import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestParserConfig.class})
@Log4j2
public class TestParser {
	private Map<String, String> map = null;

	@Autowired
	private EmgParser parser = null;

	@Configuration
	@ComponentScan(basePackages = {"com.ws.api", "com.ws.emg"})
	@EnableConfigurationProperties({MspProperties.class})
	static class TestParserConfig {
	}

	@BeforeEach
	public void init() {
		map = new HashMap<>();
		try {
			map.put("DeliverSM",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><DeliverSM><SysId>TEST1</SysId><Message><Target>01999991201703400000</Target><Source>0955227034</Source><Language>E</Language><Text>Hello!</Text><Timestamp >2017/03/03-231010</Timestamp></Message></DeliverSM>");
			map.put("DeliverSMResp", "");
			map.put("PushDR",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><PushDR><MessageId>1201</MessageId><Status>1101</Status><BNumber>886955227034</BNumber><Timestamp>2005/07/01-122340</Timestamp><DeliveryReport><Id>0010571053</Id><SubmitDate>0304141835</SubmitDate><DoneDate>0304151837</DoneDate><State>DELIVRD</State><Error>060</Error></DeliveryReport></PushDR>");
			map.put("PushDRResp", "");
			map.put("QueryDR",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryDR xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SysId>TEST5</SysId><MessageId>12001</MessageId><BNumber>886955227034</BNumber><Type>01</Type></QueryDR>");
			map.put("QueryDRResp", "");
			map.put("RetrieveDR",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><RetrieveDR xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SysId>TEST5</SysId></RetrieveDR>");
			map.put("RetrieveDRResp", "");
			map.put("SMS",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?><SMS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SysId>TEST5</SysId><Message><Target>0955227034</Target><Target>0955227035</Target><Target>0955227036</Target><Source>01999991201703400000</Source><Language>E</Language><Text>Hi, you.</Text><DrFlag>true</DrFlag><ValidType>0</ValidType></Message></SMS>");
			map.put("SMSResp", "");

			log.info("Map size={}", map.size());
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Test
	public void xmlToMessageObject() {

		/*
		 * SMS;RetrieveDR;QueryDR;PushDR;DeliverSM
		 */
		parseToMessageObject("SMS");

	}

	@Test
	public void messageObjectToXml() throws JAXBException {

		/*
		 * SMSResp;RetrieveDRResp;QueryDRResp;PushDRResp
		 */
		parseToXML("SMSResp");
	}

	private void parseToMessageObject(String name) {
		log.info("Map size={}", map.size());
		List<MessageObject> msgList;
		msgList = parser.parse(map.get(name));
		for (MessageObject msg : msgList) {
			log.debug("Result : [{}]", msg.toString());
		}
	}

	private void parseToXML(String processName) throws JAXBException {

		switch (processName) {
			case "SMSResp":
				parseSMSResp();
				break;

			case "RetrieveDRResp":
				parseRetrieveDRResp();
				break;

			case "QueryDRResp":
				parseQueryDRResp();
				break;

			case "PushDRResp":
				parsePushDRResp();
				break;

			default:
				log.debug("NONE of above");
				break;
		}

	}

	private void parsePushDRResp() throws JAXBException {
		MessageObject msg = new MessageObject();
		msg.setStatus("0201");
		log.debug("PushDRResp", parser.parsePushDRResp(msg));
	}

	private void parseQueryDRResp() {
		MessageObject msg = new MessageObject();
		msg.setStatus("0201");
		msg.setDestination("886955227034");
		msg.setPid(0010571053);
		msg.setSmscMessageId("1201");
		msg.setDeliveredTime("2005/07/01-122340");
		msg.setSubmitTime("1703011435");
		msg.setDoneTime("1703011436");
		msg.setState("DELIVRD");
		msg.setErrorCode("060");
		// log("QueryDRResp", parser.parseQueryDRResp(msg));
	}

	private void parseRetrieveDRResp() throws JAXBException {
		MessageObject msg = new MessageObject();
		msg.setStatus("0201");
		msg.setDestination("886955227034");
		msg.setSmscMessageId("1201");
		msg.setDeliveredTime("2005/07/01-122340");
		msg.setSubmitTime("1703011435");
		msg.setDoneTime("1703011436");
		msg.setState("DELIVRD");
		msg.setErrorCode("060");
		log.debug("RetrieveDRResp", parser.parseRetrieveDRResp(msg));
	}

	private void parseSMSResp() throws JAXBException {
		MessageObject msg = new MessageObject();
		msg.setStatus("0201");
		msg.setSmscMessageId("1201");
		msg.setDeliveredTime("2005/07/01-122340");
		log.debug("SMSResp", parser.parseSMSResp(msg));
	}
}
