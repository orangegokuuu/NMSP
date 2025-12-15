/**
 * 
 */
package com.ws.ibm.mq.spring;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.ws.ibm.mq.configuration.MQJmsConfig;
import com.ws.mc.spring.MCContextConfig;
//import com.ws.mc.config.MCProperties;
//import com.ws.mc.spring.MCHibernateConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:appInfo.properties"),
		@PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = false),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${config.file}", ignoreResourceNotFound = true) })
@Import({ MCContextConfig.class, MQJmsConfig.class})
@EnableAsync
@ComponentScan({ "com.ws.msp.dao", "com.ws.msp.service", "com.ws.ibm", "com.ws.jms" })
@Log4j2
public class SpringConfig {

	@Autowired
	private MspProperties properties;
	
	private static final int SUBMIT_API_TIMEOUT = 3000;

	@Bean("mqclientLogger")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit() {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(properties.getIbm().getLogger());

//		log.debug("7257 : [{}]", properties);
		return logging;
	}

	// @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     return (web) -> web.ignoring()
    //         .antMatchers("/**");
    // }

	@Bean("submitExecutor")
	public ThreadPoolTaskExecutor submitExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(properties.getIbm().getExecutor().getSubmit().getCorePoolSize());
		executor.setMaxPoolSize(properties.getIbm().getExecutor().getSubmit().getMaxPoolSize());
		executor.setQueueCapacity(properties.getIbm().getExecutor().getSubmit().getQueueCapacity());
		executor.setThreadNamePrefix("Submit-");
		executor.initialize();
		return executor;
	}

	@Bean("receiveExecutor")
	public ThreadPoolTaskExecutor receiveExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(properties.getIbm().getExecutor().getReceive().getCorePoolSize());
		executor.setMaxPoolSize(properties.getIbm().getExecutor().getReceive().getMaxPoolSize());
		executor.setQueueCapacity(properties.getIbm().getExecutor().getReceive().getQueueCapacity());
		executor.setThreadNamePrefix("Receive-");
		executor.initialize();
		return executor;
	}

	@Bean
    public RestTemplate customRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder
                .create()
                .setConnectionManager(new PoolingHttpClientConnectionManager() {{
                    setDefaultMaxPerRoute(properties.getIbm().getHttpPool().getMaxPerRoute());
                    setMaxTotal(properties.getIbm().getHttpPool().getMaxTotal());
                }}).build());
//		 httpRequestFactory.setConnectionRequestTimeout(1000);
		 httpRequestFactory.setConnectTimeout(SUBMIT_API_TIMEOUT);
		// httpRequestFactory.setReadTimeout(...);
        return new RestTemplate(httpRequestFactory);
    }

}
