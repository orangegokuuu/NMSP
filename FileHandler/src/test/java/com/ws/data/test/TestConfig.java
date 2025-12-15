package com.ws.data.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

@Configuration
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true) })
@Import(MCContextConfig.class)
@ComponentScan({ "com.ws.api","com.ws.util","com.ws.msp.dao", "com.ws.msp.service" })
public class TestConfig {

	@Autowired
	private MspProperties properties;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit(@Value("${file.logger}") String loggerCfg) {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(loggerCfg);
		return logging;
	}
}
