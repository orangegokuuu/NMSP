/**
 * 
 */
package com.ws.msp.tester;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.ws.msp.HttpLoadTest.HttpThread;
import com.ws.msp.HttpLoadTest.TestOption;
import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.AsyncTransmitter;

@Component
public class TesterMain {
	private static Logger logger = LogManager.getLogger(TesterMain.class);

//	// @Autowired
//	HazelcastInstance hzClient;
//
////	@Autowired
//	@Qualifier("asyncTransmitter")
//	private AsyncTransmitter asyncTransmitter;
//
////	@Autowired
//	@Qualifier("asyncReceiver")
//	private AsyncReceiver asyncReceiver;

	@Autowired
//	@Qualifier("testconfing")
	TestOption option;

	@Autowired
	Provider<HttpThread> provider;

	@PostConstruct
	public void doTest() {

		// IMap<Long, String> map = hzClient.getMap("data");

		HttpThread t = provider.get();
		t.setTargetQuantity(option.getTargetQuantity());
		t.setTargetNum(option.getTargetNum());
		t.setApiServerURL(option.getApiServerURL());
		t.setSubmitAPI(option.getSubmitAPI());
		t.setQueryDRAPI(option.getQueryDRAPI());
		t.setBatchRetrieveDRAPI(option.getBatchRetrieveDRAPI());

		// t.tteess();

		if (option.getTestSubmitSMS() == 1) {
			int sendtimes = option.getSendtimes();
			int sleepSec = option.getSleepTimes();
			logger.info("@@@@@@SubmitSMS API:");
			String sysID = option.getSubmitSMS_SYSID();
			String source = option.getSubmitSMS_SOURCE();
			String Content = option.getSubmitSMS_TEXT();
			String Lang = option.getSubmitSMS_LANG();
			String Drflag = option.getSubmitSMS_DRFLAG();
			String validType = option.getSubmitSMS_VAILDTYPE();
			String longSmsFlag = option.getSubmitSMS_LongSmsFlag();

			for (int times = 0; times < sendtimes; times++) {
				logger.info("@@@@@@times "+ times + "!!");
				t.smsSubmittest(sysID, source, Content, Lang, Drflag, validType, longSmsFlag);
				try {
					TimeUnit.MILLISECONDS.sleep(sleepSec);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		if (option.getTestQueryDR() == 1) {
			logger.info("@@@@@@QueryDR API:");
			String sysID = option.getQueryDR_sysID();
			String MessageId = option.getQueryDR_MessageId();
			String BNumber = option.getQueryDR_BNumber();
			String Type = option.getQueryDR_Type();

			t.queryDRtest(sysID, MessageId, BNumber, Type);
		}

		if (option.getTestRetrieveDR() == 1) {
			logger.info("@@@@@@RetrieveDR API:");
			String sysID = option.getRetrieveDR_sysID();

			t.retrieveDRtest(sysID);
		}

		if (option.getTestBatchRetrieveDR() == 1) {
			logger.info("@@@@@@BatchRetrieveDR API:");
			String sysID = option.getBatchRetrieveDR_sysID();
			String MessageId = option.getBatchRetrieveDR_MessageId();

			t.batchRetrieveDRtest(sysID, MessageId);
		}

		if (option.getTest_interOption() == 1) {
			logger.info("@@@@@@Do inter Cases :");
			InterTestOption();
		}

		if (option.getTest_intraOption() == 1) {
			logger.info("@@@@@@Do intra Cases :");
			IntraTestOption();
		}

		if (option.getTest_loadtest() == 1) {
			logger.info("@@@@@@Do Loadtest :");
			TestThread();
		}

		// TestThread();
	}

	public void InterTestOption() {
		HttpThread t = provider.get();
		t.setTargetQuantity(option.getTargetQuantity());
		t.setTargetNum(option.getTargetNum());
		t.setApiServerURL(option.getApiServerURL());
		t.setSubmitAPI(option.getSubmitAPI());
		t.setQueryDRAPI(option.getQueryDRAPI());
		t.setBatchRetrieveDRAPI(option.getBatchRetrieveDRAPI());

		if (option.getTest_interCaseShortC() == 1) {
			logger.info("###Do inter Case 1 : {}...", option.getTest_interCaseShortC());
			t.HTTP_MT_inter_short_LanC();
		}
		if (option.getTest_interCaseShortE() == 1) {
			logger.info("###Do inter Case 2 : {}...", option.getTest_interCaseShortE());
			t.HTTP_MT_inter_short_LanE();
		}
		if (option.getTest_interCaseShortB() == 1) {
			logger.info("###Do inter Case 3 : {}...", option.getTest_interCaseShortB());
			t.HTTP_MT_inter_short_LanB();
		}
		if (option.getTest_interCaseShortU() == 1) {
			logger.info("###Do inter Case 4 : {}...", option.getTest_interCaseShortU());
			t.HTTP_MT_inter_short_LanU();
		}
		if (option.getTest_interCaseShortSpam() == 1) {
			logger.info("###Do inter Case 5 : {}...", option.getTest_interCaseShortSpam());
			t.HTTP_MT_inter_short_SpamKeyword();
		}
		if (option.getTest_interCaseShortTimetable() == 1) {
			logger.info("###Do inter Case 6 : {}...", option.getTest_interCaseShortTimetable());
			t.HTTP_MT_inter_short_TimeTable();
		}
		if (option.getTest_interCaseShortDR() == 1) {
			logger.info("###Do inter Case 8 : {}...", option.getTest_interCaseShortDR());
			t.HTTP_MT_inter_short_DR();
		}
		if (option.getTest_interCaseShortVaildType() == 1) {
			logger.info("###Do inter Case 9 : {}...", option.getTest_interCaseShortVaildType());
			t.HTTP_MT_inter_short_Validtype();
		}
		// ======================================================================================
		if (option.getTest_interCaseLongC() == 1) {
			logger.info("###Do inter Case 10 : {}...", option.getTest_interCaseLongC());
			t.HTTP_MT_inter_long_LanC();
		}
		if (option.getTest_interCaseLongE() == 1) {
			logger.info("###Do inter Case 11 : {}...", option.getTest_interCaseLongE());
			t.HTTP_MT_inter_long_LanE();
		}
		if (option.getTest_interCaseLongB() == 1) {
			logger.info("###Do inter Case 12 : {}...", option.getTest_interCaseLongB());
			t.HTTP_MT_inter_long_LanB();
		}
		if (option.getTest_interCaseLongU() == 1) {
			logger.info("###Do inter Case 13 : {}...", option.getTest_interCaseLongU());
			t.HTTP_MT_inter_long_LanU();
		}
		if (option.getTest_interCaseLongSpam() == 1) {
			logger.info("###Do inter Case 14 : {}...", option.getTest_interCaseLongSpam());
			t.HTTP_MT_inter_long_SpamKeyword();
		}
		if (option.getTest_interCaseLongTimetable() == 1) {
			logger.info("###Do inter Case 15 : {}...", option.getTest_interCaseLongTimetable());
			t.HTTP_MT_inter_long_TimeTable();
		}
		if (option.getTest_interCaseLongDR() == 1) {
			logger.info("###Do inter Case 17 : {}...", option.getTest_interCaseLongDR());
			t.HTTP_MT_inter_long_DR();
		}
		if (option.getTest_interCaseLongVaildType() == 1) {
			logger.info("###Do inter Case 18 : {}...", option.getTest_interCaseLongVaildType());
			t.HTTP_MT_inter_long_Validtype();
		}

	}

	public void IntraTestOption() {
		HttpThread t = provider.get();
		t.setTargetQuantity(option.getTargetQuantity());
		t.setTargetNum(option.getTargetNum());
		t.setApiServerURL(option.getApiServerURL());
		t.setSubmitAPI(option.getSubmitAPI());
		t.setQueryDRAPI(option.getQueryDRAPI());
		t.setBatchRetrieveDRAPI(option.getBatchRetrieveDRAPI());

		if (option.getTest_intraCaseShortC() == 1) {
			logger.info("###Do intra Case 19 : {}...", option.getTest_intraCaseShortC());
			t.HTTP_MT_intra_short_LanC();
		}
		if (option.getTest_intraCaseShortE() == 1) {
			logger.info("###Do intra Case 20 : {}...", option.getTest_intraCaseShortE());
			t.HTTP_MT_intra_short_LanE();
		}
		if (option.getTest_intraCaseShortB() == 1) {
			logger.info("###Do intra Case 21 : {}...", option.getTest_intraCaseShortB());
			t.HTTP_MT_intra_short_LanB();
		}
		if (option.getTest_intraCaseShortU() == 1) {
			logger.info("###Do intra Case 22 : {}...", option.getTest_intraCaseShortU());
			t.HTTP_MT_intra_short_LanU();
		}
		if (option.getTest_intraCaseShortSpam() == 1) {
			logger.info("###Do intra Case 23 : {}...", option.getTest_intraCaseShortSpam());
			t.HTTP_MT_intra_short_SpamKeyword();
		}
		if (option.getTest_intraCaseShortTimetable() == 1) {
			logger.info("###Do intra Case 24 : {}...", option.getTest_intraCaseShortTimetable());
			t.HTTP_MT_intra_short_TimeTable();
		}
		if (option.getTest_intraCaseShortDR() == 1) {
			logger.info("###Do intra Case 26 : {}...", option.getTest_intraCaseShortDR());
			t.HTTP_MT_intra_short_DR();
		}
		if (option.getTest_intraCaseShortVaildType() == 1) {
			logger.info("###Do intra Case 27 : {}...", option.getTest_intraCaseShortVaildType());
			t.HTTP_MT_intra_short_Validtype();
		}
		// ======================================================================================
		if (option.getTest_intraCaseLongC() == 1) {
			logger.info("###Do intra Case 28 : {}...", option.getTest_intraCaseLongC());
			t.HTTP_MT_intra_long_LanC();
		}
		if (option.getTest_intraCaseLongE() == 1) {
			logger.info("###Do intra Case 29 : {}...", option.getTest_intraCaseLongE());
			t.HTTP_MT_intra_long_LanE();
		}
		if (option.getTest_intraCaseLongB() == 1) {
			logger.info("###Do intra Case 30 : {}...", option.getTest_intraCaseLongB());
			t.HTTP_MT_intra_long_LanB();
		}
		if (option.getTest_intraCaseLongU() == 1) {
			logger.info("###Do intra Case 31 : {}...", option.getTest_intraCaseLongU());
			t.HTTP_MT_intra_long_LanU();
		}
		if (option.getTest_intraCaseLongSpam() == 1) {
			logger.info("###Do intra Case 32 : {}...", option.getTest_intraCaseLongSpam());
			t.HTTP_MT_intra_long_SpamKeyword();
		}
		if (option.getTest_intraCaseLongTimetable() == 1) {
			logger.info("###Do intra Case 33 : {}...", option.getTest_intraCaseLongTimetable());
			t.HTTP_MT_intra_long_TimeTable();
		}
		if (option.getTest_intraCaseLongDR() == 1) {
			logger.info("###Do intra Case 35 : {}...", option.getTest_intraCaseLongDR());
			t.HTTP_MT_intra_long_DR();
		}
		if (option.getTest_intraCaseLongVaildType() == 1) {
			logger.info("###Do intra Case 36 : {}...", option.getTest_intraCaseLongVaildType());
			t.HTTP_MT_intra_long_Validtype();
		}
	}

	public void TestThread() {
		int threadnum = option.getTest_loadThreadNum();
		int totalTimes = option.getTest_loadTestTotalSec();
		int sleepTimes = option.getSleepTimes();

		logger.info("@@@@@@@@@@threadnum : {}...", threadnum);
		logger.info("@@@@@@@@@@totalTimes : {}...", totalTimes);
		logger.info("@@@@@@@@@@sleepTimes : {}...", sleepTimes);

		for (int i = 1; i <= threadnum; i++) {
			// HttpThread t = new HttpThread(i);
			// t.start();
			
			String sysID = option.getSubmitSMS_SYSID();
			String source = option.getSubmitSMS_SOURCE();
			String Content = option.getSubmitSMS_TEXT();
			String Lang = option.getSubmitSMS_LANG();
			String Drflag = option.getSubmitSMS_DRFLAG();
			String validType = option.getSubmitSMS_VAILDTYPE();

			HttpThread t = provider.get();
			t.setId(i);
			t.setTotalSec(totalTimes);
			t.setSleepSec(sleepTimes);
			t.setTargetQuantity(option.getTargetQuantity());
			t.setTargetNum(option.getTargetNum());
			t.setApiServerURL(option.getApiServerURL());
			t.setSubmitAPI(option.getSubmitAPI());
			t.setQueryDRAPI(option.getQueryDRAPI());
			t.setBatchRetrieveDRAPI(option.getBatchRetrieveDRAPI());
			
			t.setSubmit_sysid(sysID);
			t.setSubmit_source(source);
			t.setSubmit_Content(Content);
			t.setSubmit_Lang(Lang);
			t.setSubmit_Drflag(Drflag);
			t.setSubmit_validType(validType);

			t.start();
		}
		// try {
		// TimeUnit.SECONDS.sleep(15);
		//// TimeUnit.MINUTES.sleep(3);
		//
		//// TimeUnit.HOURS.sleep(1);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//// System.out.print("############@@@@@@@@@@@@@@@END");
	}

	// public void testSMSC() {
	//
	// logger.debug("Sending message...");
	//
	// SmsRequest messageObject = genMessage();
	//
	// try {
	// asyncTransmitter.sendSMS(messageObject);
	// } catch (MessageException | ConnectionException e) {
	// logger.error("[SMPP CONSUMER] Send to SMPP fail");
	// }
	//
	// logger.debug("Sending message...done");
	// }
	//
	// private SmsRequest genMessage() {
	//
	// SmsRequest request = new SmsRequest();
	//
	// int seq = (int) (System.currentTimeMillis() % 1998795549);
	// request.setSmsSeq(seq);
	// logger.debug("Gen seq : [{}]", seq);
	// request.setSource("85262830600");
	// request.setSourceNPI(1);
	// request.setSourceTON(1);
	// request.setDataCoding(SmsRequest.ASCII);
	// request.setDestination("85294140290");
	// request.setDestinationTON(1);
	// request.setDestinationNPI(1);
	// request.setRequestDR(true);
	//
	// String MSG1 = "Connection test
	// abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
	//
	// request.setMessage(MSG1);
	//
	// return request;
	// }
}
