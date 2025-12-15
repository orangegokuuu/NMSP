/**
 * 
 */
package com.ws.emg.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ws.emg.smpp.handler.RxHandler;
import com.ws.emg.smpp.handler.TxHandler;
import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.EmgProperties;
import com.ws.msp.config.MspProperties;
import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.AsyncTransmitter;
import com.ws.smpp.ConnectionException;
import com.ws.smpp.Receiver;
import com.ws.smpp.Transmitter;

import ie.omk.smpp.Connection;

@Configuration
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:appInfo.properties"),
		@PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/multisms/multisms.properties", ignoreResourceNotFound = true) })
@Import(MCContextConfig.class)
@ComponentScan({ "com.ws.api", "com.ws.jms", "com.ws.msp.dao", "com.ws.msp.service", "com.ws.emg" })
public class SpringConfig {

	private static Logger logger = LogManager.getLogger(SpringConfig.class);

	@Autowired
	private MspProperties properties;

	@Autowired
	@Lazy
	private TxHandler txHandler;

	@Autowired
	@Lazy
	private RxHandler rxHandler;

	@Bean("multismslog")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit() {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(properties.getEmg().getLogger());
		return logging;
	}

	@ConditionalOnProperty("emg.smpp.rx.psa.enable")
	@Bean(name = "rxPsa", destroyMethod = "shutdown")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Receiver rxPsa() throws ConnectionException {

		Receiver receiver = null;

		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			receiver = new Receiver(properties.getEmg().getSmpp().getRx().getPsa().getHost(),
					properties.getEmg().getSmpp().getRx().getPsa().getPort(),
					properties.getEmg().getSmpp().getRx().getPsa().getSystemId(),
					properties.getEmg().getSmpp().getRx().getPsa().getSystemType(),
					properties.getEmg().getSmpp().getRx().getPsa().getPassword()) {

				@Override
				public void connectAndBind() throws ConnectionException {
					super.connectAndBind(Connection.RECEIVER);
					startEnquiryLink();
				}
			};
		} else {
			receiver = new AsyncReceiver(properties.getEmg().getSmpp().getRx().getPsa().getHost(),
					properties.getEmg().getSmpp().getRx().getPsa().getPort(),
					properties.getEmg().getSmpp().getRx().getPsa().getSystemId(),
					properties.getEmg().getSmpp().getRx().getPsa().getSystemType(),
					properties.getEmg().getSmpp().getRx().getPsa().getPassword(), rxHandler);

		}

		logger.info("Initializing PSA {} Receiver : {}...", properties.getEmg().getSmpp().getBindMode(), receiver);
		receiver.setVersion(properties.getEmg().getSmpp().getRx().getPsa().getVersion());
		receiver.setEnquiryInterval(properties.getEmg().getSmpp().getEnquireLinkTime());
		try {
			receiver.connectAndBind();
		} catch (Exception e) {
			// cannot connect to SMSC
			logger.warn("[SMPP][RX PSA] something wrong with SMSC [{}]... please check",
					properties.getEmg().getSmpp().getRx().getPsa().getHost() + ":"
							+ properties.getEmg().getSmpp().getRx().getPsa().getPort());
		}
		logger.info("Initializing PSA {} Receiver... Done!!", properties.getEmg().getSmpp().getBindMode());

		return receiver;
	}

	@ConditionalOnProperty("emg.smpp.rx.psa.enable")
	@Bean(name = "rxPsaPool")
	public CommonsPool2TargetSource rxPsaPool() {

		CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
		pool.setTargetBeanName("rxPsa");
		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			pool.setTargetClass(Receiver.class);
		} else {
			pool.setTargetClass(AsyncReceiver.class);
		}
		pool.setMinIdle(properties.getEmg().getSmpp().getRx().getPsa().getConnection());
		pool.setMaxSize(properties.getEmg().getSmpp().getRx().getPsa().getConnection());
		pool.setBlockWhenExhausted(true);
		pool.setMaxWait(properties.getEmg().getSmpp().getPoolWaitingTime() * 1000);

		return pool;
	}

	@ConditionalOnProperty("emg.smpp.rx.normal.enable")
	@Bean(name = "rxNormal", destroyMethod = "shutdown")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Receiver rxNormal() throws ConnectionException {

		Receiver receiver = null;

		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			receiver = new Receiver(properties.getEmg().getSmpp().getRx().getNormal().getHost(),
					properties.getEmg().getSmpp().getRx().getNormal().getPort(),
					properties.getEmg().getSmpp().getRx().getNormal().getSystemId(),
					properties.getEmg().getSmpp().getRx().getNormal().getSystemType(),
					properties.getEmg().getSmpp().getRx().getNormal().getPassword()) {

				@Override
				public void connectAndBind() throws ConnectionException {
					super.connectAndBind(Connection.RECEIVER);
					startEnquiryLink();
				}
			};
		} else {
			receiver = new AsyncReceiver(properties.getEmg().getSmpp().getRx().getNormal().getHost(),
					properties.getEmg().getSmpp().getRx().getNormal().getPort(),
					properties.getEmg().getSmpp().getRx().getNormal().getSystemId(),
					properties.getEmg().getSmpp().getRx().getNormal().getSystemType(),
					properties.getEmg().getSmpp().getRx().getNormal().getPassword(), rxHandler);

		}

		logger.info("Initializing Normal {} Receiver : {}...", properties.getEmg().getSmpp().getBindMode(), receiver);
		receiver.setVersion(properties.getEmg().getSmpp().getRx().getNormal().getVersion());
		receiver.setEnquiryInterval(properties.getEmg().getSmpp().getEnquireLinkTime());
		try {
			receiver.connectAndBind();
		} catch (Exception e) {
			// cannot connect to SMSC
			logger.warn("[SMPP][RX Normal] something wrong with SMSC [{}]... please check",
					properties.getEmg().getSmpp().getRx().getNormal().getHost() + ":"
							+ properties.getEmg().getSmpp().getRx().getNormal().getPort());
		}
		logger.info("Initializing Normal {} Receiver... Done!!", properties.getEmg().getSmpp().getBindMode());

		return receiver;
	}

	@ConditionalOnProperty("emg.smpp.rx.normal.enable")
	@Bean(name = "rxNormalPool")
	public CommonsPool2TargetSource rxNormalPool() {

		CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
		pool.setTargetBeanName("rxNormal");
		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			pool.setTargetClass(Receiver.class);
		} else {
			pool.setTargetClass(AsyncReceiver.class);
		}
		pool.setMinIdle(properties.getEmg().getSmpp().getRx().getNormal().getConnection());
		pool.setMaxSize(properties.getEmg().getSmpp().getRx().getNormal().getConnection());
		pool.setBlockWhenExhausted(true);
		pool.setMaxWait(properties.getEmg().getSmpp().getPoolWaitingTime() * 1000);

		return pool;
	}

	@ConditionalOnProperty("emg.smpp.tx.psa.enable")
	@Bean(name = "txPsa", destroyMethod = "shutdown")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Transmitter txPsa() throws ConnectionException {
		Transmitter transmitter = null;

		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			transmitter = new Transmitter(properties.getEmg().getSmpp().getTx().getPsa().getHost(),
					properties.getEmg().getSmpp().getTx().getPsa().getPort(),
					properties.getEmg().getSmpp().getTx().getPsa().getSystemId(),
					properties.getEmg().getSmpp().getTx().getPsa().getSystemType(),
					properties.getEmg().getSmpp().getTx().getPsa().getPassword()) {

				@Override
				public void connectAndBind() throws ConnectionException {
					super.connectAndBind(Connection.TRANSMITTER);
					startEnquiryLink();
				}
			};
		} else {
			transmitter = new AsyncTransmitter(properties.getEmg().getSmpp().getTx().getPsa().getHost(),
					properties.getEmg().getSmpp().getTx().getPsa().getPort(),
					properties.getEmg().getSmpp().getTx().getPsa().getSystemId(),
					properties.getEmg().getSmpp().getTx().getPsa().getSystemType(),
					properties.getEmg().getSmpp().getTx().getPsa().getPassword(), txHandler);
		}

		logger.info("Initializing PSA {} Transmitter : {}...", properties.getEmg().getSmpp().getBindMode(),
				transmitter);
		transmitter.setVersion(properties.getEmg().getSmpp().getTx().getPsa().getVersion());
		transmitter.setEnquiryInterval(properties.getEmg().getSmpp().getEnquireLinkTime());
		transmitter.setTps(properties.getEmg().getSmpp().getTps());
		try {
			transmitter.connectAndBind();
		} catch (Exception e) {
			// cannot connect to SMSC
			logger.warn("[SMPP][TX PSA] something wrong with SMSC [{}]... please check",
					properties.getEmg().getSmpp().getTx().getPsa().getHost() + ":"
							+ properties.getEmg().getSmpp().getTx().getPsa().getPort());
		}
		logger.info("Initializing PSA {} Transmitter... Done!!", properties.getEmg().getSmpp().getBindMode());

		return transmitter;
	}

	@ConditionalOnProperty("emg.smpp.tx.psa.enable")
	@Bean(name = "txPsaPool")
	public CommonsPool2TargetSource txPsaPool() {

		CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
		pool.setTargetBeanName("txPsa");
		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			pool.setTargetClass(Transmitter.class);
		} else {
			pool.setTargetClass(AsyncTransmitter.class);
		}
		pool.setMinIdle(properties.getEmg().getSmpp().getTx().getPsa().getConnection());
		pool.setMaxSize(properties.getEmg().getSmpp().getTx().getPsa().getConnection());
		pool.setBlockWhenExhausted(true);
		pool.setMaxWait(properties.getEmg().getSmpp().getPoolWaitingTime() * 1000);

		return pool;
	}

	@ConditionalOnProperty("emg.smpp.tx.normal.enable")
	@Bean(name = "txNormal", destroyMethod = "shutdown")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Transmitter txNormal() throws ConnectionException {

		Transmitter transmitter = null;

		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			transmitter = new Transmitter(properties.getEmg().getSmpp().getTx().getNormal().getHost(),
					properties.getEmg().getSmpp().getTx().getNormal().getPort(),
					properties.getEmg().getSmpp().getTx().getNormal().getSystemId(),
					properties.getEmg().getSmpp().getTx().getNormal().getSystemType(),
					properties.getEmg().getSmpp().getTx().getNormal().getPassword()) {

				@Override
				public void connectAndBind() throws ConnectionException {
					super.connectAndBind(Connection.TRANSMITTER);
					startEnquiryLink();
				}
			};
		} else {
			transmitter = new AsyncTransmitter(properties.getEmg().getSmpp().getTx().getNormal().getHost(),
					properties.getEmg().getSmpp().getTx().getNormal().getPort(),
					properties.getEmg().getSmpp().getTx().getNormal().getSystemId(),
					properties.getEmg().getSmpp().getTx().getNormal().getSystemType(),
					properties.getEmg().getSmpp().getTx().getNormal().getPassword(), txHandler);
		}

		logger.info("Initializing Normal {} Transmitter : {}...", properties.getEmg().getSmpp().getBindMode(),
				transmitter);
		transmitter.setVersion(properties.getEmg().getSmpp().getTx().getNormal().getVersion());
		transmitter.setEnquiryInterval(properties.getEmg().getSmpp().getEnquireLinkTime());
		transmitter.setTps(properties.getEmg().getSmpp().getTps());
		try {
			transmitter.connectAndBind();
		} catch (Exception e) {
			// cannot connect to SMSC
			logger.warn("[SMPP][TX Normal] something wrong with SMSC [{}]... please check",
					properties.getEmg().getSmpp().getTx().getNormal().getHost() + ":"
							+ properties.getEmg().getSmpp().getTx().getNormal().getPort());
		}
		logger.info("Initializing Normal {} Transmitter... Done!!", properties.getEmg().getSmpp().getBindMode());

		return transmitter;
	}

	@ConditionalOnProperty("emg.smpp.tx.normal.enable")
	@Bean(name = "txNormalPool")
	public CommonsPool2TargetSource txNormalPool() {

		CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
		pool.setTargetBeanName("txNormal");
		if (properties.getEmg().getSmpp().getBindMode().equals(EmgProperties.BIND_SYNC)) {
			pool.setTargetClass(Transmitter.class);
		} else {
			pool.setTargetClass(AsyncTransmitter.class);
		}
		pool.setMinIdle(properties.getEmg().getSmpp().getTx().getNormal().getConnection());
		pool.setMaxSize(properties.getEmg().getSmpp().getTx().getNormal().getConnection());
		pool.setBlockWhenExhausted(true);
		pool.setMaxWait(properties.getEmg().getSmpp().getPoolWaitingTime() * 1000);

		return pool;
	}

	@Bean(name = "consumerExcutor")
	public TaskExecutor consumerExcutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(properties.getEmg().getConsumer().getPool().getMin());
		threadPool.setMaxPoolSize(properties.getEmg().getConsumer().getPool().getMax());
		threadPool.setQueueCapacity(properties.getEmg().getConsumer().getPool().getQueue());
		threadPool.setWaitForTasksToCompleteOnShutdown(properties.getEmg().getConsumer().isWaitOnClose());

		return threadPool;
	}

	@Bean(name = "producerExcutor")
	public TaskExecutor producerExcutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(properties.getEmg().getProducer().getPool().getMin());
		threadPool.setMaxPoolSize(properties.getEmg().getProducer().getPool().getMax());
		threadPool.setQueueCapacity(properties.getEmg().getProducer().getPool().getQueue());
		threadPool.setWaitForTasksToCompleteOnShutdown(properties.getEmg().getProducer().isWaitOnClose());

		return threadPool;
	}

}
