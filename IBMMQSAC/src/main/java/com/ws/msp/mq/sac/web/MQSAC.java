package com.ws.msp.mq.sac.web;

import org.springframework.boot.SpringApplication;
// import org.springframework.boot.system.ApplicationPidFileWriter;

import com.ws.util.StringUtil;

public class MQSAC {
	public static void main(String[] args) {
		
		// Skip Jansi output stream on Windows
		System.setProperty("log4j.skipJansi", "true");
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		String pidFile = System.getProperty("mqsac.pid");
		
		SpringApplication app = new SpringApplication(WebAppInitializer.class);
		// if (!StringUtil.isEmpty(pidFile)) {
		// 	app.addListeners(new ApplicationPidFileWriter(pidFile));
		// }
		app.run(args);
	}

}
