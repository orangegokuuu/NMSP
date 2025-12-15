package com.ws.jms.test;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.FailoverEventListener;
import org.apache.activemq.artemis.api.core.client.FailoverEventType;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import com.ws.msp.config.MspProperties;

@Configuration
@EnableConfigurationProperties(value = { MspProperties.class })
public class JmsConfig {

	private static class FailoverListenerImpl implements FailoverEventListener {

		@Override
		public void failoverEvent(FailoverEventType eventType) {
			System.out.println("Failover event triggered :" + eventType.toString());
		}
	}

	@Bean
	public ConnectionFactory cachingConnectionFactory() {
//		Map<String, Object> param1 = new HashMap<String, Object>();
//		param1.put(TransportConstants.HOST_PROP_NAME, "192.168.1.228");
//		param1.put(TransportConstants.PORT_PROP_NAME, 10066);
//
//		TransportConfiguration transport1 = new TransportConfiguration(
//				"org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory", param1);
//
//		ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithHA(JMSFactoryType.CF,
//				transport1);
//		// ActiveMQConnectionFactory connectionFactory = new
//		// ActiveMQConnectionFactory(true, transport1, transport2);
//		// connectionFactory.

		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.228:10066");
		connectionFactory.setUseGlobalPools(true);
		connectionFactory.setThreadPoolMaxSize(250);
		//connectionFactory.setProducerMaxRate(50);
		connectionFactory.setReconnectAttempts(-1);
		connectionFactory.setRetryInterval(50);
		connectionFactory.setClientFailureCheckPeriod(2000L);
		connectionFactory.setCallTimeout(5000L);
		connectionFactory.setFailoverOnInitialConnection(true);
		connectionFactory.setBlockOnAcknowledge(false);
		connectionFactory.setBlockOnDurableSend(false);
		connectionFactory.setProducerWindowSize(100000);
		//return connectionFactory;

		CachingConnectionFactory cachedFactory = new CachingConnectionFactory(connectionFactory);
		cachedFactory.setReconnectOnException(true);
		cachedFactory.setSessionCacheSize(50);
		return cachedFactory;
	}

	@Bean("testQueueTemplate")
	public JmsTemplate consumerJmsTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(cachingConnectionFactory());
		template.setDefaultDestinationName("testing.queue");
		template.setExplicitQosEnabled(false);
		return template;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(cachingConnectionFactory());
		factory.setConcurrency("3-100");
		factory.setAutoStartup(true);
		factory.setReceiveTimeout(5000L);
		factory.setRecoveryInterval(2000L);

		return factory;
	}

	@Bean
	public Consumer consumer() {
		return new Consumer();
	}
}
