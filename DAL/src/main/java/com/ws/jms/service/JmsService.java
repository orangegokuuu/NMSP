package com.ws.jms.service;

import javax.jms.JMSException;

import com.ws.emg.pojo.MessageObject;

public interface JmsService {

//	public void sendMessage(MessageObject msg);
//	
//	public void sendMessage(MessageObject msg, String destinationName);
//	
	public void sendDelayMessage(MessageObject msg, String destinationName, long delay, int priority)throws JMSException;
	
	public void sendMessage(MessageObject msg, int priority, String destinationName)throws JMSException;
//	
//	public void sendTextMessage(String text,String destinationName);
	
	public void sendMsg(MessageObject msg, String destinationName, int priority) throws JMSException;
	
	public void sendDelayMsg(MessageObject msg, String destinationName, long delay, int priority) throws JMSException;
	
	public MessageObject receiverMessage();
	
	public MessageObject receiverMessage(String destinationName);
	
	public boolean isListener();
	
	public void stopListener(String id);
	
	public void startListener();
	
//	public MessageObject receiver(String destinationName);
}
