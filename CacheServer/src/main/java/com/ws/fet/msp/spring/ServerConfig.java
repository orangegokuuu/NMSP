package com.ws.fet.msp.spring;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
// import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.hazelcast.core.HazelcastInstance;
import com.ws.backend.cluster.spring.HazelcastConfig;
import com.ws.fet.msp.config.CacheServerProperties;
import com.ws.fet.msp.server.ClusterDaemon;
import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableConfigurationProperties({ CacheServerProperties.class, MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:appInfo.properties"),
		@PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/server.properties", ignoreResourceNotFound = true) })
@Import({ HazelcastConfig.class, MCContextConfig.class })
@ComponentScan({ "com.ws.jmx", "com.ws.msp.dao", "com.ws.msp.service", "com.ws.fet.msp.management" })
@Log4j2
public class ServerConfig {
	public static final String urlTemplate = "service:jmx:rmi:///jndi/rmi://%SERVER/jmxrmi";

	@Autowired
	private CacheServerProperties serverCfg = null;

	@Autowired
	private MspProperties mspCfg = null;

	@Bean("cacheLog")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit() {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(serverCfg.getLogger());
		return logging;
	}

	@Bean
	public ConnectionFactory producerFactory() throws Exception {
		log.info("Create ActiveMQ Connection factory, pool size[{}]", mspCfg.getDal().getJms().getPoolMaxConnections());
		ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient
				.createConnectionFactory(mspCfg.getDal().getJms().getBrokerURL(), "msp");
		connectionFactory.setUseGlobalPools(false);
		connectionFactory.setThreadPoolMaxSize(mspCfg.getDal().getJms().getPoolMaxConnections());
		return connectionFactory;
	}

	@Bean("clusterDaemon")
	public ClusterDaemon cluster() {
		ClusterDaemon daemon = new ClusterDaemon(serverCfg);

		return daemon;
	}

	@Bean("hazelcastInstance")
	@DependsOn("clusterDaemon")
	@Autowired
	public HazelcastInstance cacheClient(ClusterDaemon daemon) {
		return daemon.getHz();
	}

}
