package com.ws.fet.msp.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.ws.backend.cluster.hazelcast.ClusterProperties;
import com.ws.pojo.GenericBean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache.server")
public class CacheServerProperties extends GenericBean {

	private static final long serialVersionUID = -927483243821412471L;

	private String pid = null;
	private String logger = null;
	private String banner = null;
	private JMS jms = new JMS();
	private long lockPeriod = 5000;

	@Data
	public static class JMS {
		private Map<String,String> jmxUrl = new HashMap<String,String>();
		private String jmxPrefix = "org.apache.activemq.artemis:";
		private String brokerName = "broker0";
	}

	@NestedConfigurationProperty
	private ClusterProperties cluster = new ClusterProperties();
	
	@PostConstruct
	public void setup() {
		cluster.override();
	}
}
