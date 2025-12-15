/**
 * 
 */
package com.ws.api.server;

import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import com.ws.api.spring.SpringConfig;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = { DataSourceTransactionManagerAutoConfiguration.class,
		MongoDataAutoConfiguration.class, MongoAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class, KeycloakAutoConfiguration.class , SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@Import(SpringConfig.class)
public class ApiServer {
	public static void main(String[] args) {
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		SpringApplication app = new SpringApplication(ApiServer.class);
		app.setBanner(new ApiBanner());
		app.run(args);
	}

}
