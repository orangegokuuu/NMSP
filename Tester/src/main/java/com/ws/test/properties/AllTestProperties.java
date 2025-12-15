package com.ws.test.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "test")
public class AllTestProperties {
	
	private String ApiServerURL = "http://localhost:9090/api/";
	private String SubmitAPI = "smsSubmit?";
	private String QueryDRAPI = "smsQueryDR?";
	private String RetrieveDRAPI = "smsRetrieveDR?";
	private String BatchRetrieveDRAPI = "smsBatchRetrieveDR?";
	
	//submit target parameter
	private String targetNum = "0919000";
	private int targetQuantity = 100;
	private int sleepTimes = 0;
	
	
	//load test parameter
	private Loadtest loadtest = new Loadtest();
	@Data
	public class Loadtest {
		
		private int option = 0;
		private int TestTotalSec = 600;
		private int ThreadNum = 20;

	}
	

	//submit uni-test times
	private int sendtimes = 2;
	
	private SubmitSMS submitSMS = new SubmitSMS();
	@Data
	public class SubmitSMS {
		
		private int option = 0;
		private String sysID = "TEST1";	
		private String source = "55111";	
		private String TextContent = "10C";	
		private String Lang = "C";
		private String Drflag = "false";
		private String validType = "0";
	}
	
	private QueryDR queryDR = new QueryDR();
	@Data
	public class QueryDR {
		
		private int option = 0;
		private String sysID = "long_inter";	
		private String MessageId = "0000007253";	
		private String BNumber = "";	
		private String Type = "01";
	}	
	
	private RetrieveDR retrieveDR = new RetrieveDR();
	@Data
	public class RetrieveDR {
		
		private int option = 0;
		private String sysID = "short_inter";	
	}
	
	private BatchRetrieveDR batchRetrieveDR = new BatchRetrieveDR();
	@Data
	public class BatchRetrieveDR {
		
		private int option = 0;
		private String sysID = "short_inter";
		private String MessageId = "0000007250";	
	}
	
	private int interOption = 0;
	private int intraOption = 0;
	private int Case1 = 1;
	private int Case2 = 1;
	private int Case3 = 1;
	private int Case4 = 1;
	private int Case5 = 1;
	private int Case6 = 1;
	private int Case7 = 1;
	private int Case8 = 1;
	private int Case9 = 1;
	private int Case10 = 1;
	private int Case11 = 1;
	private int Case12 = 1;
	private int Case13 = 1;
	private int Case14 = 1;
	private int Case15 = 1;
	private int Case16 = 1;
	private int Case17 = 1;
	private int Case18 = 1;
	private int Case19 = 1;
	private int Case20 = 1;
	private int Case21 = 1;
	private int Case22 = 1;
	private int Case23 = 1;
	private int Case24 = 1;
	private int Case25 = 1;
	private int Case26 = 1;
	private int Case27 = 1;
	private int Case28 = 1;
	private int Case29 = 1;
	private int Case30 = 1;
	private int Case31 = 1;
	private int Case32 = 1;
	private int Case33 = 1;
	private int Case34 = 1;
	private int Case35 = 1;
	private int Case36 = 1;

}
