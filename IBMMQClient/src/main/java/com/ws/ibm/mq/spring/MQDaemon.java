/**
 * 
 */
package com.ws.ibm.mq.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MQDaemon {

	@PostConstruct
	public void start() throws Exception {
		log.info("IBM MQ Client Started");
	}

	@PreDestroy
	public void shut() {
		// close all jms connection
	}

	public void pause() {
	}

	public void resume() {
	}

	
	
}
