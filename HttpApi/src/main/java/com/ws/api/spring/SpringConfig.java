/**
 * 
 */
package com.ws.api.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.ws.mc.spring.MCContextConfig;
//import com.ws.mc.config.MCProperties;
//import com.ws.mc.spring.MCHibernateConfig;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(value = { MspProperties.class })
@PropertySources({ @PropertySource(value = "classpath:appInfo.properties"),
		@PropertySource(value = "classpath:springboot.properties"),
		@PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = false),
		@PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file://${MSP_HOME}/config/httpapi/httpapi.properties", ignoreResourceNotFound = true)})
@Import(MCContextConfig.class)
@ComponentScan({ "com.ws.api", "com.ws.jms", "com.ws.msp.dao", "com.ws.msp.service", "com.ws.emg.util" })
public class SpringConfig {

	@Autowired
	private MspProperties properties;

	@Bean("api")
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public LoggingInitializer loggingInit() {
		LoggingInitializer logging = new LoggingInitializer();
		logging.setLoggerConfig(properties.getApi().getLogger());
		return logging;
	}

	// @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    //     return (web) -> web.ignoring()
    //         .antMatchers("/**");
    // }

//	@Bean
//    @ConfigurationProperties(prefix = "custom.rest.connection")
//    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
//		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		factory.setConnectTimeout(30000);
//		factory.setReadTimeout(30000);
//		factory.setConnectionRequestTimeout(30000);
//        return new HttpComponentsClientHttpRequestFactory();
//    }

    @Bean
    public RestTemplate customRestTemplate(){
    	//add by Matthew 20190520, set RestTemplate time out 
    	HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(properties.getApi().getRest().getRequesttimeout());
        httpRequestFactory.setConnectTimeout(properties.getApi().getRest().getConnecttimeout());
        httpRequestFactory.setReadTimeout(properties.getApi().getRest().getReadtimeout());
        return new RestTemplate(httpRequestFactory);
    }
	
}
