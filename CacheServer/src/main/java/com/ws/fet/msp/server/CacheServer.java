/**
 * 
 */
package com.ws.fet.msp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.ws.fet.msp.spring.ServerConfig;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,
        MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
@EnableAsync	
@Import(ServerConfig.class)
public class CacheServer {

	public static void main(String[] args) {
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

		SpringApplication app = new SpringApplication(CacheServer.class);

		app.run(args);
	}

}