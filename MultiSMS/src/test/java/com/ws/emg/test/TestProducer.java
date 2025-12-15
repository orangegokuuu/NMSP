package com.ws.emg.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ws.emg.smpp.handler.RxHandler;
import com.ws.emg.spring.SpringConfig;
import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.ConnectionException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TestProducer {

	private static Logger logger = LogManager.getLogger(TestProducer.class);

	@Autowired
	private AsyncReceiver asyncReceiver;

	@Autowired
	private RxHandler rxHandler;

	@BeforeEach
	public void init() {
		logger.info("Initializing Asynchronized Receiver...");
		asyncReceiver.setEventHandler(rxHandler);
		if (!asyncReceiver.isConnected()) {
			try {
				asyncReceiver.connectAndBind();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Done!!");
	}

	@AfterAll
	public void shut() {
	}

	@Test
	public void receiveMessage() {

		logger.debug("Start receive message");
	}
}
