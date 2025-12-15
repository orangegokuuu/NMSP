/**
 * 
 */
package com.ws.emg.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import com.ws.emg.spring.SpringConfig;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = { DataSourceTransactionManagerAutoConfiguration.class,
		MongoDataAutoConfiguration.class, MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class })
@Import(SpringConfig.class)
public class EmgServer {
	public static void main(String[] args) {
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
		SpringApplication app = new SpringApplication(EmgServer.class);
		app.setBanner(new EmgBanner());
		
		app.run(args);
	}

}
