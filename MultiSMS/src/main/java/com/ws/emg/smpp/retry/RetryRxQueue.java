package com.ws.emg.smpp.retry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RetryRxQueue {
	
	private static Logger logger = LogManager.getLogger(RetryRxQueue.class);
	
	private Queue<QueueObject> rxQueue = new LinkedList<QueueObject>();
	
	public synchronized QueueObject getQueue(){
		logger.debug("==== RetryRxQueue getQueue");
		return rxQueue.poll();
	}
	
	public synchronized List<QueueObject> getListQueue(){
		logger.debug("==== RetryRxQueue getListQueue");
		
		return new ArrayList<QueueObject>(rxQueue);
	}
	
	public synchronized int getQueueSzie(){
		logger.debug("==== RetryRxQueue getQueueSzie");
		return new ArrayList<QueueObject>(rxQueue).size();
	}
	
	public synchronized boolean putQueue(QueueObject queue){
		logger.debug("==== RetryRxQueue putQueue");
		if(rxQueue.contains(queue)){
			return true;
		}
		else{
			return rxQueue.add(queue);
		}
	}
}
