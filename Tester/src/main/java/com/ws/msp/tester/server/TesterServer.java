/**
 * 
 */
package com.ws.msp.tester.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.ws.msp.tester.spring.SpringConfig;

@SpringBootApplication
@Import(SpringConfig.class)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
		MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
public class TesterServer {
	public static void main(String[] args) {
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		SpringApplication app = new SpringApplication(TesterServer.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setBanner(new StartupBanner());
		app.run(args);
	}

}
