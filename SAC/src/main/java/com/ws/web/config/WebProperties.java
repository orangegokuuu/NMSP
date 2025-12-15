package com.ws.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("sac")
public class WebProperties {

	private String logger = null;
	private Resource resource = null;

	@Data
	public static class Resource {
		private String[] path = null;
		private boolean cacheEnabled = false;
		private Integer cachePeriod = 0;
	}

}
