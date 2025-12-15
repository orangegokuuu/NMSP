/**
 * 
 */
package com.ws.msp.file.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import com.ws.msp.file.config.SpringConfig;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
        MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class })
@Import(SpringConfig.class)
public class FileHandlerServer {
	public static void main(String[] args) {
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		SpringApplication app = new SpringApplication(FileHandlerServer.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.setBanner(new FileHandlerBanner());
		app.run(args);
	}

}
