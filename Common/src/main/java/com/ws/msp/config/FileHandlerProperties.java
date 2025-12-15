package com.ws.msp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file")
public class FileHandlerProperties {

	private String logger = "log4j2.xml";
	private String pid = "/home/msp/var/file.pid";
	private String profile = "active-profile";
	private String user = "admin";
	private int targetSize = 100;
	

	private Admin admin = new Admin();
	private Ssh ssh = new Ssh();
	private Consumer consumer = new Consumer();
	private Producer producer = new Producer();
	private Watcher watcher = new Watcher();
	private SmsProcesser smsProcesser = new SmsProcesser();
	
	@Data
	public class Ssh {
		private String port = "2055";
	}

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
	public class SmsProcesser {
		private boolean waitOnClose = true;
		private Pool pool = new Pool();

		@Data
		public class Pool {
			private int min = 50;
			private int max = 9999;
			private int queue = 9999;
		}
	}
	
	@Data
	public class Watcher {
		private int watchInterval = 5;
		private String submitsms_url = "http://localhost:9090/api/smsSubmit?xmlData=";
		private String skip_folders = "history,log,logs,working,error";
		private String working_folder = "/data/msp/ftp/working/";
		private String sms_path = "/data/msp/ftp/";
		private String sms_backup_path = "history/";
		private String file_extension1 = "txt";
		private String file_extension2 = "end";
		private String blacklist_path ="/data/msp/blacklist/";
		private String blacklist_backup_path ="/data/msp/blacklist/history/";
		private String spam_keyword_path ="/data/msp/keyword/";
		private String spam_keyword_backup_path ="/data/msp/keyword/history/";
		private String mnp_path ="/data/msp/mnp/";
		private String mnp_backup_path ="/data/msp/mnp/history/";
		private int difference_count = 250000;
		private String error_path = "error/";
	}
}
