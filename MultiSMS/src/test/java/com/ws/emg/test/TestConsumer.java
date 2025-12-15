package com.ws.emg.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ws.emg.spring.SpringConfig;
import com.ws.smpp.AsyncTransmitter;
import com.ws.smpp.ConnectionException;
import com.ws.smpp.MessageException;
import com.ws.smpp.SmsRequest;
import ie.omk.smpp.message.SMPPResponse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TestConsumer {

	private static Logger logger = LogManager.getLogger(TestConsumer.class);

	@Autowired
	@Qualifier("asyncTransmitter")
	private AsyncTransmitter asyncTransmitter;

	@AfterEach
	public void shut() {
		asyncTransmitter.shutdown();
	}

	@Test
	public void sendMessage() {

		logger.debug("Sending message...");

		SmsRequest messageObject = genMessage();

		SMPPResponse result = null;
		try {
			result = asyncTransmitter.sendSMS(messageObject);
		} catch (MessageException | ConnectionException e) {
			logger.error("[SMPP CONSUMER] Send to SMSC fail", e.getMessage());
		}

		logger.debug("#### result [{}]", result);
		logger.debug("Sending message...done");
	}

	private SmsRequest genMessage() {

		SmsRequest messageObject = new SmsRequest();

		messageObject.setSmsSeq((int) (System.currentTimeMillis() % 1998795549));
		messageObject.setSource("85262830600");
		messageObject.setSourceNPI(1);
		messageObject.setSourceTON(1);
		messageObject.setDataCoding(SmsRequest.UTF);
		messageObject.setDestination("85294140290");
		messageObject.setDestinationTON(1);
		messageObject.setDestinationNPI(1);
		messageObject.setRequestDR(true);
		String MSG1 = "Connection test";

		messageObject.setMessage(MSG1);

		return messageObject;
	}
}
