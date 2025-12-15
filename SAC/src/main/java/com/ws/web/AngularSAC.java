/**
 *
 */
package com.ws.web;

import org.springframework.boot.SpringApplication;


public class AngularSAC {
	public static void main(String[] args) {
		
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		
		SpringApplication app = new SpringApplication(WebAppInitializer.class);
		
		app.run(args);
	}

}
