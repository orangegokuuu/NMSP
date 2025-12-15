package com.ws.emg.loadTest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.ws.msp.bootstrap.LoggingInitializer;

public class EnqueueMain {

	@Configuration
	@PropertySources({ @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
			@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
			@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
			@PropertySource(value = "file://${MSP_HOME}/config/emg/test.properties", ignoreResourceNotFound = true),
			@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true) })
	@ComponentScan({ "com.ws.emg.loadTest", "com.ws.api","com.ws.jms" })
	static class ServiceConfiguration {
		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE)
		public LoggingInitializer loggingInit(@Value("${test.enqueue.client.logger}") String loggerCfg) {
			LoggingInitializer logging = new LoggingInitializer();
			logging.setLoggerConfig(loggerCfg);
			return logging;
		}

	}

	@SuppressWarnings("resource")
	public static void main(String[] argv) {

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ServiceConfiguration.class);

		EnqueueTest test = ctx.getBean(EnqueueTest.class);
		test.testEnqueue();
	}
}