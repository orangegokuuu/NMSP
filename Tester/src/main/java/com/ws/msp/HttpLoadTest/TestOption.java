package com.ws.msp.HttpLoadTest;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Component("testOption")
@Component
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TestOption {

	private int times = 0;
	private int Success = 0;
	private int Fail = 0;
	
	public String ApiServerURL;
	public String SubmitAPI;
	public String QueryDRAPI;
	public String RetrieveDRAPI;
	public String BatchRetrieveDRAPI;

	private int sendtimes = 0;
	
	public String targetNum;
	public int targetQuantity;	
	
	public int testSubmitSMS;
	public String SubmitSMS_SYSID;
	public String SubmitSMS_SOURCE;
	public String SubmitSMS_TEXT;
	public String SubmitSMS_LANG;
	public String SubmitSMS_DRFLAG;	
	public String SubmitSMS_VAILDTYPE;
	public String SubmitSMS_LongSmsFlag;	
	
	
	public int testQueryDR;
	public String QueryDR_sysID;
	public String QueryDR_MessageId;
	public String QueryDR_BNumber;	
	public String QueryDR_Type;	
	
	
	public int testRetrieveDR;
	public String RetrieveDR_sysID;
	
	public int testBatchRetrieveDR;
	public String BatchRetrieveDR_sysID;
	public String BatchRetrieveDR_MessageId;
	
	
	public int test_loadTestTotalSec;	
	public int test_loadThreadNum;
	public int sleepTimes;

	
	public int test_loadtest;	
	public int test_interOption;
	public int test_intraOption;
	
	public int test_interCaseShortC;
	public int test_interCaseShortE;
	public int test_interCaseShortB;
	public int test_interCaseShortU;
	public int test_interCaseShortSpam;
	public int test_interCaseShortTimetable;
	public int test_interCaseShortDR;
	public int test_interCaseShortVaildType;

	public int test_interCaseLongC;
	public int test_interCaseLongE;
	public int test_interCaseLongB;
	public int test_interCaseLongU;
	public int test_interCaseLongSpam;
	public int test_interCaseLongTimetable;
	public int test_interCaseLongDR;
	public int test_interCaseLongVaildType;
	
	public int test_intraCaseShortC;
	public int test_intraCaseShortE;
	public int test_intraCaseShortB;
	public int test_intraCaseShortU;
	public int test_intraCaseShortSpam;
	public int test_intraCaseShortTimetable;
	public int test_intraCaseShortDR;
	public int test_intraCaseShortVaildType;

	public int test_intraCaseLongC;
	public int test_intraCaseLongE;
	public int test_intraCaseLongB;
	public int test_intraCaseLongU;
	public int test_intraCaseLongSpam;
	public int test_intraCaseLongTimetable;
	public int test_intraCaseLongDR;
	public int test_intraCaseLongVaildType;

	
}
