package com.ws.msp.mq.sac.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.WebApplicationInitializer;

import com.ws.msp.mq.sac.web.spring.RootContextConfig;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, 
		MongoDataAutoConfiguration.class, MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class, SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@Import(RootContextConfig.class)
@PropertySources({ @PropertySource(value = "classpath:springboot.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:default.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file://${MSP_HOME}/config/application.properties", ignoreResourceNotFound = true) })
public class WebAppInitializer  extends SpringBootServletInitializer implements WebApplicationInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebAppInitializer.class);
	}
	

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Context Param
		servletContext.setInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext", "message");
		servletContext.setInitParameter("webAppRootKey", "mqsac.root");
	}

}
