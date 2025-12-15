/**
 * 
 */
package com.ws.emg.test;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.AsyncTransmitter;
import com.ws.smpp.ConnectionException;
import com.ws.smpp.Receiver;
import com.ws.smpp.SmppConnector.VERSION;
import com.ws.smpp.Transmitter;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SmppConfig {
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
		final PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath:test.properties"));
		return ppc;
	}

	@Bean(destroyMethod = "shutdown")
	public Transmitter transmitter(@Value("${smpp.host}") String hostName, @Value("${smpp.port}") int port,
			@Value("${smpp.systemId}") String systemID, @Value("${smpp.systemType}") String systemType,
			@Value("${smpp.password}") String password, @Value("${smpp.source.ton}") int sourceTON,
			@Value("${smpp.source.npi}") int sourceNPI, @Value("${smpp.source.address}") String sourceAddress)
			throws ConnectionException {
		AsyncTransmitter transmitter = new AsyncTransmitter(hostName, port, systemID, systemType, password,
				new SmppLoggingHandler());
		transmitter.setSourceAddress(sourceAddress);
		transmitter.setVersion(VERSION.V34);
		transmitter.setTps(5);
		transmitter.setEnquiryInterval(10);
		transmitter.connectAndBind();
		return transmitter;
	}
	


	@Bean(destroyMethod = "shutdown")
	public Receiver receiver(@Value("${smpp.host}") String hostName, @Value("${smpp.port}") int port,
			@Value("${smpp.systemId}") String systemID, @Value("${smpp.systemType}") String systemType,
			@Value("${smpp.password}") String password, @Value("${smpp.source.ton}") int sourceTON,
			@Value("${smpp.source.npi}") int sourceNPI, @Value("${smpp.source.address}") String sourceAddress)
			throws ConnectionException {
		AsyncReceiver receiver = new AsyncReceiver(hostName, port, systemID, systemType, password,
				new SmppLoggingHandler());
		receiver.setSourceAddress(sourceAddress);
		receiver.setVersion(VERSION.V34);
		receiver.setEnquiryInterval(10);
		receiver.connectAndBind();
		return receiver;
	}
}
