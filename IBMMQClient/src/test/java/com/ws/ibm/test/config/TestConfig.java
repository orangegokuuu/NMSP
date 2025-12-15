/**
 * 
 */
package com.ws.ibm.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.mc.spring.MCContextConfig;
//import com.ws.mc.config.MCProperties;
//import com.ws.mc.spring.MCHibernateConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:appInfo.properties"),
		@PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = false),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true) })
@Import({MQJmsConfig.class})
@EnableAsync
@ComponentScan({"com.ws.ibm.mq.submitTask"})
public class TestConfig {

	@Autowired
	private MspProperties properties;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit() {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(properties.getIbm().getLogger());
		return logging;
	}
	
    @Bean("submitExecutor")
    public ThreadPoolTaskExecutor submitExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Submit-");
        executor.initialize();
        return executor;
    }
    
    @Bean("receiveExecutor")
    public ThreadPoolTaskExecutor receiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Receive-");
        executor.initialize();
        return executor;
    }

}
