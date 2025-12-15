package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ibm")
public class IbmMqProperties {

	private String logger = "log4j2.xml";
	private String pid = "/var/msp/mqclient.pid";
	private String submitAPIURL = "http://192.168.1.51:8080/api/SmsSubmit?";

	private Jms jms = new Jms();
	private WiseMq wiseMq = new WiseMq();
	private Executor executor = new Executor();
	private HttpPool httpPool = new HttpPool();

	@Data
	public class Jms {
		private String host = "192.168.1.51";
		private int port = 1414;
		private String channel = "SMS.WS.CHL";
		private String queueManagerName = "DMZ.QM3";
		private String user = null;
		private String password = null;
		private boolean clientTransport = true;
		private boolean useAuth = true;

		private Dr dr = new Dr();
		private Mo mo = new Mo();

		@Data
		public class Dr {
			private String queue = "sms.dr.ibm.qm3";
		}

		@Data
		public class Mo {
			private String queue = "sms.mo.ibm.qm3";
		}

	}

	@Data
	public class WiseMq {
		private String concurrency = "1-5";
	}

	@Data
	public class Executor {
		private Submit submit = new Submit();
		private Receive receive = new Receive();
		
		@Data
		public class Submit {
			private int corePoolSize = 10;
			private int maxPoolSize = 50;
			private int queueCapacity = 20000;
		}

		@Data
		public class Receive {
			private int corePoolSize = 10;
			private int maxPoolSize = 50;
			private int queueCapacity = 20000;
		}

	}
	
	@Data
	public class HttpPool{
		private int maxPerRoute = 200;
		private int maxTotal = 200;
	}

}
