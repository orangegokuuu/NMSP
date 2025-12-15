package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

@Data
@ConfigurationProperties
public class MspProperties {

	@NestedConfigurationProperty
	private DalProperties dal = new DalProperties();
	
	@NestedConfigurationProperty
	private EmgProperties emg = new EmgProperties();

	@NestedConfigurationProperty
	private ApiProperties api = new ApiProperties();
	
	@NestedConfigurationProperty
	private FileHandlerProperties file = new FileHandlerProperties();

	@NestedConfigurationProperty
	private IbmMqProperties ibm = new IbmMqProperties();
}
