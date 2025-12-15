package com.ws.jms.message;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.ws.emg.pojo.MessageObject;

@Component("jmsProducer")
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JmsProducer {

	static final Logger logger = LogManager.getLogger(JmsProducer.class);

	 @Autowired
	 @Qualifier("producerConnectionPool")
	 private ConnectionFactory connectionFactory;
	 
	 private Connection connection = null;
	 private Session session = null;
	 private MessageProducer producer = null;
	 private Destination destination = null;

	// @Autowired
	// private JmsTemplate jmsTemplate;

	@Autowired
	@Qualifier("porducerJmsTemplate")
	private JmsTemplate porducerJmsTemplate;
	
	public void sendTextMessage(String text,String destinationName){
		porducerJmsTemplate.send(destinationName, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
              return session.createTextMessage(text);
            }
        });
	}

	public void sendMessage(final MessageObject msg) {

		porducerJmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objectMessage = session.createObjectMessage();
				objectMessage.setObject(msg);
				return objectMessage;
			}
		});
	}

	public void sendMessage(final MessageObject msg, String destinationName) {
		porducerJmsTemplate.setDefaultDestinationName(destinationName);
		// porducerJmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		porducerJmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
		porducerJmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage objectMessage = session.createObjectMessage();
				objectMessage.setObject(msg);
				return objectMessage;
			}
		});
	}

	public void sendMessage(final MessageObject msg, int priority, String destinationName)  throws JMSException{
		try{
			porducerJmsTemplate.setDefaultDestinationName(destinationName);
			porducerJmsTemplate.setPriority(priority);
			// porducerJmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			porducerJmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
			porducerJmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
			porducerJmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage();
					objectMessage.setObject(msg);
					return objectMessage;
				}
			});
		}catch(Exception e){
			logger.error("[WISEMQ] wisemq send message failed..");
			logger.error(e, e);
			throw e;
		}
	}

	public void sendDelayMessage(final MessageObject msg, String destinationName, long delay, int priority)  throws JMSException{
		try{
			porducerJmsTemplate.setDefaultDestinationName(destinationName);
			porducerJmsTemplate.setPriority(priority);
			// porducerJmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			porducerJmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
			porducerJmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
			porducerJmsTemplate.send(new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage();
					objectMessage.setObject(msg);
					objectMessage.setLongProperty("_AMQ_SCHED_DELIVERY", System.currentTimeMillis()+delay);
					return objectMessage;
				}
			});
		}catch(Exception e){
			logger.error("[WISEMQ] wisemq send delay message failed..");
			logger.error(e, e);
			throw e;
		}
	}

	// public void sendMessage2(final MessageObject msg, String destinationName)
	// {
	// jmsTemplate.setDefaultDestinationName(destinationName);
	// jmsTemplate.send(new MessageCreator() {
	// @Override
	// public Message createMessage(Session session) throws JMSException {
	// ObjectMessage objectMessage = session.createObjectMessage();
	// objectMessage.setObject(msg);
	// return objectMessage;
	// }
	// });
	// }

	public void connect(String destinationName) throws JMSException{
		logger.debug("=== producer connect");
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(destinationName);
			producer = session.createProducer(destination);
		} catch (JMSException e) {
			logger.error("[WISEMQ] wisemq connect failed..");
			logger.error(e, e);
			throw e;
		}
		
	}
	
	
	
	public void sendMsg(final MessageObject msg, String destinationName, int priority) throws JMSException{
		try {		 
			this.connect(destinationName);		 
			ObjectMessage objectMessage = session.createObjectMessage();
			objectMessage.setObject(msg);
			// producer.send(objectMessage, DeliveryMode.NON_PERSISTENT, priority, 0);
			producer.send(objectMessage, DeliveryMode.PERSISTENT, priority, 0);
	
		} catch (JMSException e) {
			logger.error("[WISEMQ] sendMsg failed..");
			logger.error(e, e);
			throw e;
		}
	}
	
	public void sendDelayMsg(final MessageObject msg, String destinationName, long delay, int priority) throws JMSException{
		try {		 
			this.connect(destinationName);		 
			ObjectMessage objectMessage = session.createObjectMessage();
			objectMessage.setObject(msg);
			objectMessage.setLongProperty("_AMQ_SCHED_DELIVERY", System.currentTimeMillis()+delay);
			// producer.send(objectMessage, DeliveryMode.NON_PERSISTENT, priority, 0);
			producer.send(objectMessage, DeliveryMode.PERSISTENT, priority, 0);
	
		} catch (JMSException e) {
			logger.error("[WISEMQ] sendDelayMsg failed..");
			logger.error(e, e);
			throw e;
		}
	}

}
