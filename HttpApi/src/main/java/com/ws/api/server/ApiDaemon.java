/**
 * 
 */
package com.ws.api.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ApiDaemon {

	private static Logger logger = LogManager.getLogger(ApiDaemon.class);


	@PostConstruct
	public void start() throws Exception {
		logger.info("API Server Started");
	}

	@PreDestroy
	public void shut() {
	}

	public void pause() {
	}

	public void resume() {
	}

}
