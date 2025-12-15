/**
 * 
 */
package com.ws.msp.tester.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.ws.emg.smpp.handler.RxHandler;
import com.ws.emg.smpp.handler.TxHandler;
//import com.ws.mc.spring.MCContextConfig;
import com.ws.msp.HttpLoadTest.TestOption;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;
import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.AsyncTransmitter;
import com.ws.smpp.ConnectionException;
import com.ws.smpp.SmppConnector.VERSION;
import com.ws.test.properties.AllTestProperties;

@Configuration
@EnableConfigurationProperties(value = { MspProperties.class, AllTestProperties.class })
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
//		MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@PropertySources({ @PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${MSP_HOME}/config/tester.properties", ignoreResourceNotFound = true) })
//@Import(MCContextConfig.class)
@ComponentScan({ "com.ws.msp.tester", "com.ws.emg", "com.ws.api", "com.ws.jms", "com.ws.msp.dao", "com.ws.msp.service",
		"com.ws.msp.HttpLoadTest" })
public class SpringConfig {

	private static Logger logger = LogManager.getLogger(SpringConfig.class);

	@Autowired
	private MspProperties properties;

	@Autowired
	private AllTestProperties testProperties;

//	@Autowired
//	@Qualifier("txHandler")
	private TxHandler txHandler;

//	@Autowired
//	@Qualifier("rxHandler")
	private RxHandler rxHandler;

	// @Bean(name = "testconfing")
	@Bean
	public TestOption option() {

		TestOption config = new TestOption();

		// API URL
		config.setApiServerURL(testProperties.getApiServerURL());
		config.setSubmitAPI(testProperties.getSubmitAPI());
		config.setQueryDRAPI(testProperties.getQueryDRAPI());
		config.setRetrieveDRAPI(testProperties.getBatchRetrieveDRAPI());
		config.setBatchRetrieveDRAPI(testProperties.getBatchRetrieveDRAPI());

		// target number
		config.setTargetQuantity(testProperties.getTargetQuantity());
		config.setTargetNum(testProperties.getTargetNum());

		// uni-test times
		config.setSendtimes(testProperties.getSendtimes());
		// test sleep times
		config.setSleepTimes(testProperties.getSleepTimes());

		// SubmitSMS parameter
		config.setTestSubmitSMS(testProperties.getSubmitSMS().getOption());
		config.setSubmitSMS_SYSID(testProperties.getSubmitSMS().getSysID());
		config.setSubmitSMS_SOURCE(testProperties.getSubmitSMS().getSource());
		config.setSubmitSMS_TEXT(testProperties.getSubmitSMS().getTextContent());
		config.setSubmitSMS_LANG(testProperties.getSubmitSMS().getLang());
		config.setSubmitSMS_DRFLAG(testProperties.getSubmitSMS().getDrflag());
		config.setSubmitSMS_VAILDTYPE(testProperties.getSubmitSMS().getValidType());

		// QueryDR parameter
		config.setTestQueryDR(testProperties.getQueryDR().getOption());
		config.setQueryDR_sysID(testProperties.getQueryDR().getSysID());
		config.setQueryDR_MessageId(testProperties.getQueryDR().getMessageId());
		config.setQueryDR_BNumber(testProperties.getQueryDR().getBNumber());
		config.setQueryDR_Type(testProperties.getQueryDR().getType());

		// RetrieveDR parameter
		config.setTestRetrieveDR(testProperties.getRetrieveDR().getOption());
		config.setRetrieveDR_sysID(testProperties.getRetrieveDR().getSysID());

		// BatchRetrieveDR parameter
		config.setTestBatchRetrieveDR(testProperties.getBatchRetrieveDR().getOption());
		config.setBatchRetrieveDR_sysID(testProperties.getBatchRetrieveDR().getSysID());
		config.setBatchRetrieveDR_MessageId(testProperties.getBatchRetrieveDR().getMessageId());

		// Load test
		config.setTest_loadtest(testProperties.getLoadtest().getOption());
		config.setTest_loadTestTotalSec(testProperties.getLoadtest().getTestTotalSec());
		config.setTest_loadThreadNum(testProperties.getLoadtest().getThreadNum());

		// test inter & intra option
		config.setTest_interOption(testProperties.getInterOption());
		config.setTest_intraOption(testProperties.getIntraOption());

		// test case option
		config.setTest_interCaseShortC(testProperties.getCase1());
		config.setTest_interCaseShortE(testProperties.getCase2());
		config.setTest_interCaseShortB(testProperties.getCase3());
		config.setTest_interCaseShortU(testProperties.getCase4());
		config.setTest_interCaseShortSpam(testProperties.getCase5());
		config.setTest_interCaseShortTimetable(testProperties.getCase6());
		config.setTest_interCaseShortDR(testProperties.getCase8());
		config.setTest_interCaseShortVaildType(testProperties.getCase9());

		config.setTest_interCaseLongC(testProperties.getCase10());
		config.setTest_interCaseLongE(testProperties.getCase11());
		config.setTest_interCaseLongB(testProperties.getCase12());
		config.setTest_interCaseLongU(testProperties.getCase13());
		config.setTest_interCaseLongSpam(testProperties.getCase14());
		config.setTest_interCaseLongTimetable(testProperties.getCase15());
		config.setTest_interCaseLongDR(testProperties.getCase17());
		config.setTest_interCaseLongVaildType(testProperties.getCase18());

		config.setTest_intraCaseShortC(testProperties.getCase19());
		config.setTest_intraCaseShortE(testProperties.getCase20());
		config.setTest_intraCaseShortB(testProperties.getCase21());
		config.setTest_intraCaseShortU(testProperties.getCase22());
		config.setTest_intraCaseShortSpam(testProperties.getCase23());
		config.setTest_intraCaseShortTimetable(testProperties.getCase24());
		config.setTest_intraCaseShortDR(testProperties.getCase26());
		config.setTest_intraCaseShortVaildType(testProperties.getCase27());

		config.setTest_intraCaseLongC(testProperties.getCase28());
		config.setTest_intraCaseLongE(testProperties.getCase29());
		config.setTest_intraCaseLongB(testProperties.getCase30());
		config.setTest_intraCaseLongU(testProperties.getCase31());
		config.setTest_intraCaseLongSpam(testProperties.getCase32());
		config.setTest_intraCaseLongTimetable(testProperties.getCase33());
		config.setTest_intraCaseLongDR(testProperties.getCase35());
		config.setTest_intraCaseLongVaildType(testProperties.getCase36());

		return config;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit(@Value("${tester.logger}") String loggerCfg) {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(loggerCfg);
		return logging;
	}

	@Bean(name = "asyncTransmitter", destroyMethod = "unbindAndDisconnect")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AsyncTransmitter asyncTransmitter() {
		AsyncTransmitter asyncTransmitter = new AsyncTransmitter(
				properties.getEmg().getSmpp().getTx().getNormal().getHost(),
				properties.getEmg().getSmpp().getTx().getNormal().getPort(),
				properties.getEmg().getSmpp().getTx().getNormal().getSystemId(),
				properties.getEmg().getSmpp().getTx().getNormal().getSystemType(),
				properties.getEmg().getSmpp().getTx().getNormal().getPassword(),
				txHandler);

		logger.info("Initializing Asynchronized Transmitter : {}...", asyncTransmitter);
		asyncTransmitter.setVersion(VERSION.V34);
		asyncTransmitter.setTps(properties.getEmg().getSmpp().getTps());
		if (!asyncTransmitter.isConnected()) {
			try {
				asyncTransmitter.connectAndBind();
			} catch (ConnectionException e) {
				logger.error(e, e);
			}
		}
		logger.info("Done!!");

		return asyncTransmitter;
	}

	@Bean(name = "asyncReceiver", destroyMethod = "unbindAndDisconnect")
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public AsyncReceiver asyncReceiver() {
		AsyncReceiver asyncReceiver = new AsyncReceiver(properties.getEmg().getSmpp().getRx().getNormal().getHost(),
				properties.getEmg().getSmpp().getRx().getNormal().getPort(),
				properties.getEmg().getSmpp().getRx().getNormal().getSystemId(),
				properties.getEmg().getSmpp().getRx().getNormal().getSystemType(),
				properties.getEmg().getSmpp().getRx().getNormal().getPassword(),
				rxHandler);

		logger.info("Initializing Asynchronized Receiver : {}...", asyncReceiver);
		asyncReceiver.setVersion(VERSION.V34);
		if (!asyncReceiver.isConnected()) {
			try {
				asyncReceiver.connectAndBind();
			} catch (ConnectionException e) {
				logger.error(e, e);
			}
		}
		logger.info("Done!!");

		return asyncReceiver;
	}
}
