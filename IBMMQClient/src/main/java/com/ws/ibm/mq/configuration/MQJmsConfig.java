package com.ws.ibm.mq.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

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

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ws.msp.config.MspProperties;

import lombok.extern.log4j.Log4j2;

@Configuration
@ComponentScan(basePackages = { "com.ws.jms" })
@EnableConfigurationProperties(value = { MspProperties.class })
@EnableJms
@Log4j2
public class MQJmsConfig {
	@Autowired
	private MspProperties properties;

	// WiseMQ Listener
	// @Bean("consumerConnectionPool")
	// public ConnectionFactory consumerPoolConnectionFactory() throws Exception {
	// 	// ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient
	// 	// .createConnectionFactory(properties.getDal().getJms().getBrokerURL(),
	// 	// "msp");
	// 	// connectionFactory.setUseGlobalPools(false);
	// 	// connectionFactory.setThreadPoolMaxSize(properties.getEmg().getConsumer().getPool().getMax());
	// 	// return connectionFactory;
	// 	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
	// 			properties.getDal().getJms().getBrokerURL());
	// 	connectionFactory.setUseGlobalPools(properties.getDal().getJms().isUseGlobalPools());
	// 	connectionFactory.setThreadPoolMaxSize(properties.getDal().getJms().getPoolMaxConnections());
	// 	// connectionFactory.setProducerMaxRate(50);
	// 	connectionFactory.setReconnectAttempts(properties.getDal().getJms().getReconnectAttempts());
	// 	connectionFactory.setRetryInterval(properties.getDal().getJms().getRetryInterval());
	// 	connectionFactory.setClientFailureCheckPeriod(properties.getDal().getJms().getClientFailureCheckPeriod());
	// 	connectionFactory.setCallTimeout(properties.getDal().getJms().getCallTimeout());
	// 	connectionFactory.setFailoverOnInitialConnection(properties.getDal().getJms().isFailoverOnInitialConnection());
	// 	connectionFactory.setBlockOnAcknowledge(properties.getDal().getJms().isBlockOnAcknowledge());
	// 	connectionFactory.setBlockOnDurableSend(properties.getDal().getJms().isBlockOnDurableSend());
	// 	connectionFactory.setProducerWindowSize(properties.getDal().getJms().getProducerWindowSize());
	// 	// return connectionFactory;

	// 	CachingConnectionFactory cachedFactory = new CachingConnectionFactory(connectionFactory);
	// 	cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
	// 	cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
	// 	return cachedFactory;
	// }

	// @Bean("consumerJmsTemplate")
	// @Autowired
	// public JmsTemplate consumerJmsTemplate(@Qualifier("consumerConnectionPool") ConnectionFactory cf) {
	// 	JmsTemplate template = new JmsTemplate();
	// 	template.setConnectionFactory(cf);
	// 	template.setDefaultDestinationName(properties.getDal().getJms().getMtQueueName());
	// 	template.setExplicitQosEnabled(true);
	// 	return template;
	// }

	// @Bean
	// @Autowired
	// public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
	// 		@Qualifier("consumerConnectionPool") ConnectionFactory cf) {
	// 	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	// 	factory.setConnectionFactory(cf);
	// 	factory.setConcurrency(properties.getIbm().getWiseMq().getConcurrency());
	// 	factory.setSessionTransacted(false);
	// 	// factory.setAutoStartup(false);
	// 	return factory;
	// }

	// IBM MQ
	@Bean("MQProducerConnectionPool")
	public JmsConnectionFactory mqProducerFactory() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, properties.getIbm().getJms().getHost());
		cf.setIntProperty(WMQConstants.WMQ_PORT, properties.getIbm().getJms().getPort());
		log.info("host = {}, {}", properties.getIbm().getJms().getHost(),  properties.getIbm().getJms().getPort());
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.getIbm().getJms().getQueueManagerName());
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;

		// CachingConnectionFactory cachedFactory = new
		// CachingConnectionFactory(cf);
		// cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
		// cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
		// return cachedFactory;

	}

	// IBM MQ
	@Bean("MQConsumerConnectionPool")
	public JmsConnectionFactory mqConsumerFactory() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, properties.getIbm().getJms().getHost());
		cf.setIntProperty(WMQConstants.WMQ_PORT, properties.getIbm().getJms().getPort());
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.getIbm().getJms().getQueueManagerName());
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;

		// CachingConnectionFactory cachedFactory = new
		// CachingConnectionFactory(cf);
		// cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
		// cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
		// return cachedFactory;

	}

	// IBM MQ
	@Bean("MQProducerConnectionPool1")
	public JmsConnectionFactory mqProducerFactory1() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "10.76.1.167");
		cf.setIntProperty(WMQConstants.WMQ_PORT, 8080);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "DMZ.QM");
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;

		// CachingConnectionFactory cachedFactory = new
		// CachingConnectionFactory(cf);
		// cachedFactory.setReconnectOnException(properties.getDal().getJms().isReconnectOnException());
		// cachedFactory.setSessionCacheSize(properties.getDal().getJms().getSessionCacheSize());
		// return cachedFactory;

	}
	
	@Bean("MQProducerConnectionPool2")
	public JmsConnectionFactory mqProducerFactory2() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "10.76.1.168");
		cf.setIntProperty(WMQConstants.WMQ_PORT, 8080);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "DMZ.QM2");
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;
	}

	@Bean("MQProducerConnectionPool3")
	public JmsConnectionFactory mqProducerFactory3() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "210.241.199.74");
		cf.setIntProperty(WMQConstants.WMQ_PORT, 8002);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "DMZ.QM4");
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;
	}
	
	@Bean("MQProducerConnectionPool4")
	public JmsConnectionFactory mqProducerFactory4() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "10.76.91.26");
		cf.setIntProperty(WMQConstants.WMQ_PORT, 8080);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "MGW.QM");
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;
	}
	
	@Bean("MQProducerConnectionPool5")
	public JmsConnectionFactory mqProducerFactory5() throws JMSException {
		JmsConnectionFactory cf = null;
		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		cf = ff.createConnectionFactory();
		// Set the properties
		cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "10.76.91.27");
		cf.setIntProperty(WMQConstants.WMQ_PORT, 8080);
		cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getIbm().getJms().getChannel());
		if (properties.getIbm().getJms().isClientTransport()) {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		} else {
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
		}
		cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "MGW.QM2");
		if (properties.getIbm().getJms().getUser() != null) {
			cf.setStringProperty(WMQConstants.USERID, properties.getIbm().getJms().getUser());
			cf.setStringProperty(WMQConstants.PASSWORD, properties.getIbm().getJms().getPassword());
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, properties.getIbm().getJms().isUseAuth());
		}
		return cf;
	}

	@Bean("MQHandlerList")
	public List<String> mqHandlerList() {
		List<String> mqHandlerList = new ArrayList<String>();
		return mqHandlerList;
	}

}
