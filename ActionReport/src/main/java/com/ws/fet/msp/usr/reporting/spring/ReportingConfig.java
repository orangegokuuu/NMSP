/**
 *
 */
package com.ws.fet.msp.usr.reporting.spring;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.util.CryptUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true) })
@ComponentScan({ "com.ws.fet.msp.usr.reporting" })
public class ReportingConfig {
	
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer()
	        throws IOException {
		final PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
		return ppc;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit(@Value("usr.rptd.logger") String logger) {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(logger);
		return logging;
	}

	@Bean(name = "dataSource", destroyMethod = "close")
	public DataSource dataSource(@Value("${mc.datasource.db.driver}") String driver,
	        @Value("${mc.datasource.db.url}") String url, @Value("${mc.datasource.db.user}") String user,
	        @Value("${mc.datasource.db.password}") String pass,
	        @Value("${mc.datasource.db.pool.keepaliveSql}") String keepaliveSQL,
	        @Value("${mc.datasource.db.pool.min}") int poolMin,
	        @Value("${mc.datasource.db.pool.max}") int poolMax,
	        @Value("${mc.datasource.db.pool.idleTimeout}") int poolTimeout,
	        @Value("${mc.datasource.db.pool.maxStatement}") int statementMax) {

		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(CryptUtil.decrypt(pass));
		config.setAutoCommit(false);
		config.setIdleTimeout(poolTimeout * 1000);
		config.setMinimumIdle(poolMin);
		config.setMaximumPoolSize(poolMax);

		final HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}
	
}
