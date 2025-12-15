package com.ws.api.processor;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import com.ws.api.listener.DeQueueListener;
import com.ws.emg.pojo.MessageObject;
import com.ws.jms.service.JmsService;

@Component("apiConsumer")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Consumer implements Runnable {

	private static Logger logger = LogManager.getLogger(Consumer.class);
	// private static Logger SLOG = LogManager.getLogger("com.ws.emg.Consumer");

	private UUID id = UUID.randomUUID();

	//private DeQueueListener listener;
	private MessageObject obj = null;

	private String queueName = "";

	@Autowired
	JmsService jmsService;

	/**
	 * Initial worker properties
	 * 
	 * @param listener
	 *            : use to sending message
	 * @param queueName
	 *            : listening queue name
	 */
//	public void init(DeQueueListener listener, String queueName) {
//		this.listener = listener;
//		this.queueName = queueName;
//	}

	@Override
	public void run() {

		// dequeue
		obj = jmsService.receiverMessage(queueName);
		logger.debug("receiver message uuid[{}] :message[{}]", id, obj.toString());
		
		// TODO insert table
	}

}
