package com.ws.jms.configuration;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Session;

import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import com.ws.jms.service.JmsService;
import com.ws.jms.service.JmsServiceImpl;
import com.ws.msp.config.MspProperties;

@Configuration
@ComponentScan(basePackages = { "com.ws.jms" })
@EnableConfigurationProperties(value = { MspProperties.class })
@EnableJms
public class Jmsconfig {

	@Autowired
	private MspProperties properties;

	// @Autowired
	// ConnectionFactory connectionFactory;

	@Bean("producerConnectionPool")
	public ConnectionFactory producerPoolConnectionFactory() throws Exception {
//		ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient
//		        .createConnectionFactory(properties.getDal().getJms().getBrokerURL(), "msp");
//		connectionFactory.setUseGlobalPools(false);
//		connectionFactory.setThreadPoolMaxSize(properties.getDal().getJms().getPoolMaxConnections());
//		return connectionFactory;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getDal().getJms().getBrokerURL());
		connectionFactory.setUseGlobalPools(properties.getDal().getJms().isUseGlobalPools());
		connectionFactory.setThreadPoolMaxSize(properties.getDal().getJms().getPoolMaxConnections());
		//connectionFactory.setProducerMaxRate(50);
		connectionFactory.setReconnectAttempts(properties.getDal().getJms().getReconnectAttempts());
		connectionFactory.setRetryInterval(properties.getDal().getJms().getRetryInterval());
		connectionFactory.setClientFailureCheckPeriod(properties.getDal().getJms().getClientFailureCheckPeriod());
		connectionFactory.setCallTimeout(properties.getDal().getJms().getCallTimeout());
		connectionFactory.setFailoverOnInitialConnection(properties.getDal().getJms().isFailoverOnInitialConnection());
		connectionFactory.setBlockOnAcknowledge(properties.getDal().getJms().isBlockOnAcknowledge());
		connectionFactory.setBlockOnDurableSend(properties.getDal().getJms().isBlockOnDurableSend());
		connectionFactory.setProducerWindowSize(properties.getDal().getJms().getProducerWindowSize());
		//return connectionFactory;
		
		CachingConnectionFactory cachedFactory = new CachingConnectionFactory(connectionFactory);
		cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
		cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
		return cachedFactory;

	}

	@Bean("consumerConnectionPool")
	public ConnectionFactory consumerPoolConnectionFactory() throws Exception {
//		ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient
//		        .createConnectionFactory(properties.getDal().getJms().getBrokerURL(), "msp");
//		connectionFactory.setUseGlobalPools(false);
//		connectionFactory.setThreadPoolMaxSize(properties.getEmg().getConsumer().getPool().getMax());
//		return connectionFactory;
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getDal().getJms().getBrokerURL());
		connectionFactory.setUseGlobalPools(properties.getDal().getJms().isUseGlobalPools());
		connectionFactory.setThreadPoolMaxSize(properties.getDal().getJms().getPoolMaxConnections());
		//connectionFactory.setProducerMaxRate(50);
		connectionFactory.setReconnectAttempts(properties.getDal().getJms().getReconnectAttempts());
		connectionFactory.setRetryInterval(properties.getDal().getJms().getRetryInterval());
		connectionFactory.setClientFailureCheckPeriod(properties.getDal().getJms().getClientFailureCheckPeriod());
		connectionFactory.setCallTimeout(properties.getDal().getJms().getCallTimeout());
		connectionFactory.setFailoverOnInitialConnection(properties.getDal().getJms().isFailoverOnInitialConnection());
		connectionFactory.setBlockOnAcknowledge(properties.getDal().getJms().isBlockOnAcknowledge());
		connectionFactory.setBlockOnDurableSend(properties.getDal().getJms().isBlockOnDurableSend());
		connectionFactory.setProducerWindowSize(properties.getDal().getJms().getProducerWindowSize());
		//return connectionFactory;
		
		CachingConnectionFactory cachedFactory = new CachingConnectionFactory(connectionFactory);
		cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
		cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
		return cachedFactory;
	}

	@Bean("porducerJmsTemplate")
	@Autowired
	public JmsTemplate porducerJmsTemplate(@Qualifier("producerConnectionPool") ConnectionFactory cf) {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(cf);
		template.setDefaultDestinationName(properties.getDal().getJms().getMtQueueName());
		template.setExplicitQosEnabled(true);
		// 2017-05-05 add
		template.setMessageIdEnabled(false);
		template.setMessageTimestampEnabled(false);
		
		// template.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		template.setDeliveryMode(DeliveryMode.PERSISTENT);
		return template;
	}

	@Bean("consumerJmsTemplate")
	@Autowired
	public JmsTemplate consumerJmsTemplate(@Qualifier("consumerConnectionPool") ConnectionFactory cf) {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(cf);
		template.setDefaultDestinationName(properties.getDal().getJms().getMtQueueName());
		template.setExplicitQosEnabled(true);
		return template;
	}

//	@Bean
//	@Autowired
//	public JmsTemplate jmsTemplate(@Qualifier("consumerConnectionPool") ConnectionFactory cf) {
//		JmsTemplate template = new JmsTemplate();
//		template.setConnectionFactory(cf);
//		template.setDefaultDestinationName(properties.getDal().getJms().getMtQueueName());
//		template.setExplicitQosEnabled(true);
//		return template;
//	}

	@Bean
	@Autowired
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
	        @Qualifier("consumerConnectionPool") ConnectionFactory cf) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(cf);
		factory.setConcurrency(properties.getDal().getJms().getConcurrency());
		factory.setSessionTransacted(false);
		// factory.setAutoStartup(false);
		return factory;
	}

	@Bean
	public JmsService getJmsService() {
		return new JmsServiceImpl();
	}
}
