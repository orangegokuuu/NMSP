/**
 * 
 */
package com.ws.msp.file.config;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

@Configuration
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:default.properties"),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/file/filehandler.properties", ignoreResourceNotFound = true) })
@Import(MCContextConfig.class)
@ComponentScan({ "com.ws.api", "com.ws.msp.file", "com.ws.util", "com.ws.msp.dao", "com.ws.msp.service" })
public class SpringConfig {

	@Autowired
	MspProperties properties;

	@Bean("fileLog")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit(@Value("${file.logger}") String loggerCfg) {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(loggerCfg);
		return logging;
	}

	@Bean(name = "fileExecutor")
	public ThreadPoolTaskExecutor smsExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(properties.getFile().getSmsProcesser().getPool().getMin());
		threadPool.setMaxPoolSize(properties.getFile().getSmsProcesser().getPool().getMax());
		threadPool.setQueueCapacity(properties.getFile().getSmsProcesser().getPool().getQueue());
		threadPool.setWaitForTasksToCompleteOnShutdown(properties.getFile().getSmsProcesser().isWaitOnClose());

		return threadPool;
	}
}