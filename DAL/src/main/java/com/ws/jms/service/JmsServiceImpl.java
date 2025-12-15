package com.ws.jms.service;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ws.emg.pojo.MessageObject;
import com.ws.jms.message.JmsConsumer;
import com.ws.jms.message.JmsProducer;

@Service("jmsService")
public class JmsServiceImpl implements JmsService {

	static final Logger logger = LogManager.getLogger(JmsServiceImpl.class);

	@Autowired
	JmsProducer jmsProducer;
	
	@Autowired
	JmsConsumer jmsConsumer;

//	@Override
//	public void sendMessage(MessageObject msg) {
//		jmsProducer.sendMessage(msg);
//	}
//
//	@Override
//	public void sendMessage(MessageObject msg, String destinationName) {
//		jmsProducer.sendMessage(msg, destinationName);
//	}
//	
	@Override
	public void sendDelayMessage(MessageObject msg, String destinationName, long delay, int priority) throws JMSException{
		jmsProducer.sendDelayMessage(msg, destinationName,delay,priority);
	}
	
	@Override
	public void sendMessage(MessageObject msg, int priority, String destinationName) throws JMSException{
		jmsProducer.sendMessage(msg, priority, destinationName);
	}
//	
//	
//	
//	@Override
//	public void send(MessageObject msg, int priority, String destinationName) {
//		jmsProducer.send(msg, priority, destinationName);
//	}
//
//	@Override
//	public void sendTextMessage(String text, String destinationName) {
//		// TODO Auto-generated method stub
//		jmsProducer.sendTextMessage(text, destinationName);
//	}


	@Override
	public void sendMsg(MessageObject msg, String destinationName, int priority) throws JMSException{
		// TODO Auto-generated method stub
		jmsProducer.sendMsg(msg, destinationName, priority);
	}

	@Override
	public void sendDelayMsg(MessageObject msg, String destinationName, long delay, int priority) throws JMSException{
		// TODO Auto-generated method stub
		jmsProducer.sendDelayMsg(msg, destinationName, delay, priority);
	}
	
	@Override
	public MessageObject receiverMessage() {
		// TODO Auto-generated method stub
		return jmsConsumer.receiverMessage();
	}

	@Override
	public MessageObject receiverMessage(String destinationName) {
		return jmsConsumer.receiverMessage(destinationName);
	}

	@Override
	public boolean isListener() {
		return jmsConsumer.isListener();
	}

	@Override
	public void stopListener(String id) {
		jmsConsumer.stopListener(id);
	}

	@Override
	public void startListener() {
		jmsConsumer.startListener();
	}
	
//	@Override
//	public MessageObject receiver(String destinationName) {
//		// TODO Auto-generated method stub
//		return jmsConsumer.receiver(destinationName);
//	}

}
