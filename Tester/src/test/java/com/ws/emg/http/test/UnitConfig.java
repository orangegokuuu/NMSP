/**
 * 
 */
package com.ws.emg.http.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties(value = { MspProperties.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
		MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@PropertySources({ @PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/tester.properties", ignoreResourceNotFound = true) })
@ComponentScan({ "com.ws.emg.http.test", "com.ws.msp.dao", "com.ws.msp.service", "com.ws.core.dao", "com.ws.core.service"  })
//@Import(MCContextConfig.class)
public class UnitConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit(@Value("${tester.logger}") String loggerCfg) {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(loggerCfg);
		return logging;
	}
}
