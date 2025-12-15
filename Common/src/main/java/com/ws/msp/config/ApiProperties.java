package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

	private String logger = "log4j2.xml";
	private String pid = "/home/msp/var/api.pid";
	private String profile = "active-profile";

	private Admin admin = new Admin();
	private Server server = new Server();
	private Servlet servlet = new Servlet();
	private Manage manage = new Manage();
	private Consumer consumer = new Consumer();
	private Producer producer = new Producer();
	private Fet fet = new Fet();
	private Da da = new Da();
	private Retry retry = new Retry();
	private Rest rest = new Rest();
	private Content content = new Content();

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
		private String name = "MSP HTTP API";
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

	@Data
	public class Fet {
		private boolean waitOnClose = true;
		private String routing_number = "1432,1417";
		// private String rn4g = "1432";
		// private String rnr4g = "1417";
		// private String rn2g = "1410";
		// private String rn2g2 = "1416";
		private String chargeB = "02,05,09,10,12,13,14,15,18,21,25,26,28,29,30,31,34,37,41,42,44,45,46,47";
	}

	@Data
	public class Da {
		private Format format = new Format();

		@Data
		public class Format {
			private String prefix = "09,886,+886";
			private String length = "10,12,13";
		}
	}

	@Data
	public class Retry {
		private int delay = 20000;
		private int count = 3;

	}

	@Data
	public class Rest {
		private int requesttimeout = 30000;
		private int connecttimeout = 30000;
		private int readtimeout = 30000;

	}

	@Data
	public class Content {

		private English english = new English();
		private Chinese chinese = new Chinese();
		private Target target = new Target();
		private Xml xml = new Xml();
		private int splitLength = 140;
		private int headerLength = 6;

		@Data
		public class English {
			private int minLength = 160;
			private int maxLength = 1520;

		}

		@Data
		public class Chinese {
			private int minLength = 70;
			private int maxLength = 660;

		}

		@Data
		public class Target {
			private int maxLength = 100;
		}

		@Data
		public class Xml {
			private int maxLength = 8888;
		}
	}
}
