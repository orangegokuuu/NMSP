/**
 *
 */
package com.ws.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.ws.web.spring.RootContextConfig;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MongoDataAutoConfiguration.class, MongoAutoConfiguration.class, 
				HazelcastJpaDependencyAutoConfiguration.class, SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@Import(RootContextConfig.class)
@PropertySources({ @PropertySource(value = "classpath:springboot.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file://${SAC_HOME}/config/application.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file://${SAC_HOME}/config/sac/sac.properties", ignoreResourceNotFound = true) })
public class WebAppInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebAppInitializer.class);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet
	 * .ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Context Param
		servletContext.setInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext", "message");
		servletContext.setInitParameter("webAppRootKey", "sac.root");
	}

}
