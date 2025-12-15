package com.ws.msp.bootstrap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.beans.factory.annotation.Required;

import com.ws.util.StringUtil;

public class LoggingInitializer {
	private String loggerConfig = null;

	@Required
	public void setLoggerConfig(String loggerConfig) {
		this.loggerConfig = loggerConfig;
	}

	@PostConstruct
	public void onEvent() {
		if (!StringUtil.isEmpty(loggerConfig)) {
			Configurator.initialize(null, loggerConfig);
		}
	}
}
