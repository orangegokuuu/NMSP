package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.ws.hibernate.config.DataSourceProperties;
import com.ws.hibernate.config.HibernateProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "dal")
public class DalProperties {

	private Jms jms = new Jms();
	private Cache cache = new Cache();
	
	@Data
	public class Jms {
		// private String brokerURL =
		// "tcp://192.168.1.245:60006?jms.messagePrioritySupported=true";
		//private String brokerURL = "tcp://192.168.1.51:10066?jms.useAsyncSend=true";
		private String brokerURL = "tcp://192.168.1.228:10066";
		private String dataDir = null;
		private String concurrency = "1-20";
		private String mtQueueName = "sms.mt";
		private String moIntraQueueName = "sms.mo.intra";
		private String moInterQueueName = "sms.mo.inter";
		private String drIntraQueueName = "sms.dr.intra";
		private String drInterQueueName = "sms.dr.inter";
		private String moIbmQueueNameQM1 = "sms.mo.ibm.qm1";
		private String moIbmQueueNameQM2 = "sms.mo.ibm.qm2";
		private String moIbmQueueNameQM3 = "sms.mo.ibm.qm3";
		private String moIbmQueueNameQM4 = "sms.mo.ibm.qm4";
		private String moIbmQueueNameQM5 = "sms.mo.ibm.qm5";
		private String moIbmQueueNameQM6 = "sms.mo.ibm.qm6";
		private String drIbmQueueNameQM1 = "sms.dr.ibm.qm1";
		private String drIbmQueueNameQM2 = "sms.dr.ibm.qm2";
		private String drIbmQueueNameQM3 = "sms.dr.ibm.qm3";
		private String drIbmQueueNameQM4 = "sms.dr.ibm.qm4";
		private String drIbmQueueNameQM5 = "sms.dr.ibm.qm5";
		private String drIbmQueueNameQM6 = "sms.dr.ibm.qm6";
		private String drQueueName = "sms.dr.intra"; // can not remove
		private String moQueueName = "sms.mo.intra"; // can not remove
		private int poolMaxConnections = 100;
		private int poolMaximumActiveSessionPerConnection = 100;
		
		/**
		 * In second
		 */
		private int checkTime = 15;
		
		/**
		 * CacheFactory
		 */
		private boolean useGlobalPools = true;
	    private int reconnectAttempts = -1;
		private int retryInterval = 50;
		private int clientFailureCheckPeriod = 2000;
		private int callTimeout = 5000;
		private boolean failoverOnInitialConnection = true;
		private boolean blockOnAcknowledge = false;
		private boolean blockOnDurableSend = false;
		private int producerWindowSize = 100000;
		private boolean reconnectOnException = true;
		private int sessionCacheSize = 50;
	}

	@Data
	public class Cache {
		private int loadBatchSize = 10000;
	}
	
	@NestedConfigurationProperty
	private DataSourceProperties datasource = new DataSourceProperties();

	@NestedConfigurationProperty
	private HibernateProperties hbm = new HibernateProperties();

}
