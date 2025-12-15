package com.ws.api.processor;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.emg.pojo.MessageObject;
import com.ws.jms.service.JmsService;

@Component("apiProducer")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Producer implements Runnable {

	private static Logger logger = LogManager.getLogger(Producer.class);
	// private static Logger SLOG = LogManager.getLogger("com.ws.emg.Producer");

	private UUID id = UUID.randomUUID();

	private MessageObject msg = null;

	private String processQueue = "";
	private String dataQueue = "";

	@Autowired
	JmsService jmsService;

	/**
	 * Initial worker properties
	 * 
	 * @param queueName
	 *            : listening queue name
	 */
	public void init(MessageObject msg, String processQueue, String dataQueue) {
		this.msg = msg;
		this.processQueue = processQueue;
		this.dataQueue = dataQueue;
	}

	@Override
	public void run() {

		// enqueue
		//jmsService.sendMessage(msg, processQueue);
		//jmsService.sendMessage(msg, dataQueue);
		logger.debug("enqueue message uuid[{}] :message[{}]", id, msg.toString());

		// TODO update table
	}

}
