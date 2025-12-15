package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ws.smpp.SmppConnector.VERSION;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "emg")
public class EmgProperties {

	public static final String BIND_SYNC = "SYNC";
	public static final String BIND_ASYNC = "ASYNC";

	private String logger = "log4j2.xml";
	private String pid = "/home/msp/var/emg.pid";
	private String profile = "active-profile";
	private String test_mo_sw = "false";
	//add by matthew 2018-04-16
	private int retryCount = -1;
	private int retryTime = 60000;
	//add by matthew 2018-10-11
	private int retryDrCount = 1000;

	private Admin admin = new Admin();
	private Server server = new Server();
	private Servlet servlet = new Servlet();
	private Manage manage = new Manage();
	private SMPP smpp = new SMPP();
	private Consumer consumer = new Consumer();
	private Producer producer = new Producer();

	@Data
	public class Admin {
		private String user = "admin";
		private String pass = "admin";
		private String context = "/manage";
		private Command command = new Command();

		@Data
		public class Command {
			private String disable = "jpa*,jdbc*,jndi*,cron*,mail*,shell*";
		}
	}

	@Data
	public class Server {
		private String name = "MSP EMG";
		private String waitOnClose = "true";
	}

	@Data
	public class Servlet {
		private String port = "8080";
		private String address = "0.0.0.0";
	}

	@Data
	public class Manage {
		private String port = "8081";
		private String address = "0.0.0.0";
	}

	@Data
	public class SMPP {
		private int tps = 10;
		private long connectionTimeOutSec = 10;
		private long poolWaitingTime = 60;
		private Source source = new Source();
		private Tx tx = new Tx();
		private Rx rx = new Rx();
		private String bindMode = BIND_ASYNC;
		private int enquireLinkTime = 10;
		private int dcsASCII = 0;
		private int defaultSourceTon = 0;
		private int defaultSourceNpi = 0;
		private int defaultDestinationTon = 0;
		private int defaultDestinationNpi = 0;
		private int smscIdMaxLength = 10;

		@Data
		public class Tx {

			private SmppSetting psa = new SmppSetting();
			private SmppSetting normal = new SmppSetting();
			private int positionalNotation = 16;
		}

		@Data
		public class Rx {

			private SmppSetting psa = new SmppSetting();
			private SmppSetting normal = new SmppSetting();
			private int positionalNotation = 10;
		}

		@Data
		public class SmppSetting {
			private String host = "0.0.0.0";
			private int port = 2775;
			private String systemId = "";
			private String systemType = "";
			private String password = "";
			private int connection = 1;
			private VERSION version = VERSION.V33;
			private boolean enable = false;
		}

		@Data
		public class Source {
			private int ton = 0;
			private int npi = 0;
		}
	}

	@Data
	public class Consumer {
		private boolean waitOnClose = true;
		private Pool pool = new Pool();

		@Data
		public class Pool {
			private int min = 3;
			private int max = 10;
			private int queue = 2;
		}
	}

	@Data
	public class Producer {
		private boolean waitOnClose = true;
		private Pool pool = new Pool();

		@Data
		public class Pool {
			private int min = 3;
			private int max = 10;
			private int queue = 2;
		}
	}
}
