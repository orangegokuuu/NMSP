package com.ws.emg.http.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.ws.msp.HttpLoadTest.HttpThread;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UnitConfig.class)
public class TestHttpClient {

	public static final String ApiServerURL = "http://192.168.1.51:8800/api/";
//	public static final String ApiServerURL = "http://192.168.1.94:9090/api/";	
//	public static final String ApiServerURL = "http://localhost:9090/api/";

	public static final String SubmitAPI = "smsSubmit?";
	public static final String QueryDRAPI = "smsQueryDR?";
	public static final String RetrieveDRAPI = "smsRetrieveDR?";
	public static final String BatchRetrieveDRAPI = "smsBatchRetrieveDR?";

	public String xmlStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SMS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "$SysId" + "<Message>" + "$Target" + "$Source" + "$Text" + "$Language" + "$DrFlag" + "$ValidType"
			+ "</Message></SMS>";

	// @Autowired
	Provider<HttpThread> provider;

	public int targetQuantity = 1;
	public String targetNum = "0919000";
//	public String targetNum = "886909000";

	int threadnum = 1;
	int totalTimes = 1;
	int sleepTimes = 0;
	
	int sleepSec = 0;
	
	int sendtimes = 1;
	int TestSubmitSMS = 1;
	int TestSubmitSMSXML = 0;
	
	int TestQueryDR = 0;
	int TestRetrieveDR = 0;
	int TestBatchRetrieveDR = 0;
	int TestLoadTest = 0;

//	String submitsms_sysID = "TEST1";
//	String submitsms_source = "55111";
	String submitsms_sysID = "TEST3";
	String submitsms_source = "55511";
//	String submitsms_sysID = "TEST4";
//	String submitsms_source = "55511";	
//	String submitsms_sysID = "short_inter";
//	String submitsms_source = "55111";			
	
	String submitsms_Content = "100";
	String submitsms_Lang = "C";
	String submitsms_Drflag = "true";
	String submitsms_validType = "0";
	String submitsms_longSmsFlag = "true";

	String queryDR_sysID = "TEST3";
	String queryDR_MessageId = "0000000469";
	String queryDR_BNumber = "";
	String queryDR_Type = "01";

	String retrieveDRsysID = "TEST3";

	String batchRetrieveDR_sysID = "TEST3";
	String batchRetrieveDR_MessageId = "0000000469";

	@Test
	public void API_test() {

		// HttpThread t = provider.get();
		HttpThread t = new HttpThread();

		t.setApiServerURL(ApiServerURL);
		t.setSubmitAPI(SubmitAPI);
		t.setQueryDRAPI(QueryDRAPI);
		t.setRetrieveDRAPI(RetrieveDRAPI);
		t.setBatchRetrieveDRAPI(BatchRetrieveDRAPI);
		t.setTargetQuantity(targetQuantity);
		t.setTargetNum(targetNum);
		t.setMultiResult(0);


		if (TestSubmitSMS == 1) {

			log.debug("@@@@@@SubmitSMS API:");
			log.debug("@@@@@@ApiServerURL:[{}]",ApiServerURL);

			for (int times = 0; times < sendtimes; times++) {
				log.debug("@@@@@@times " + times + "!!");
				t.smsSubmittest(submitsms_sysID, submitsms_source, submitsms_Content, submitsms_Lang, submitsms_Drflag,
						submitsms_validType,submitsms_longSmsFlag);
				try {
					TimeUnit.MILLISECONDS.sleep(sleepSec);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		if (TestSubmitSMSXML == 1) {
			int sendtimes = 1;
			int sleepSec = 0;
			log.debug("@@@@@@SubmitSMS API:");
			log.debug("@@@@@@ApiServerURL:[{}]",ApiServerURL);

			for (int times = 0; times < sendtimes; times++) {
				log.debug("@@@@@@times " + times + "!!");
				t.smsSubmitXmltest(submitsms_sysID, submitsms_source, submitsms_Content, submitsms_Lang, submitsms_Drflag,
						submitsms_validType,submitsms_longSmsFlag);
				try {
					TimeUnit.MILLISECONDS.sleep(sleepSec);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		

		if (TestQueryDR == 1) {
			log.debug("@@@@@@QueryDR API:");

			t.queryDRtest(queryDR_sysID, queryDR_MessageId, queryDR_BNumber, queryDR_Type);
		}

		if (TestRetrieveDR == 1) {
			log.debug("@@@@@@RetrieveDR API:");
			t.retrieveDRtest(retrieveDRsysID);
		}

		if (TestBatchRetrieveDR == 1) {
			log.debug("@@@@@@BatchRetrieveDR API:");
			t.batchRetrieveDRtest(batchRetrieveDR_sysID, batchRetrieveDR_MessageId);
		}

		// if (TestLoadTest == 1) {
		// log.debug("@@@@@@Do Loadtest :");
		// TestThread();
		// }
	}


	public void function_test() {
		// function test
		int[] arrI;
		arrI = GenerateNonDuplicateRan(5);
		log.debug("@@@@@@arrI:{}", arrI);
	}

	public static int[] GenerateNonDuplicateRan(int range) {
		Random rand = new Random();
		int rdm[] = new int[range];
		ArrayList<String> list = new ArrayList<String>();
		String ch;
		for (int i = 0; i < range; i++) {
			list.add(i, Integer.toString(i));
		}
		int cnt = 0;
		while (list.size() > 0) {
			int pv = rand.nextInt(list.size());
			ch = list.get(pv);
			rdm[cnt++] = Integer.valueOf(ch);
			list.remove(pv);
		}
		return rdm;
	}

	// public void TestThread() {
	//
	// for (int i = 1; i <= threadnum; i++) {
	// // HttpThread t = new HttpThread(i);
	// // t.start();
	//
	// log.debug("@@@@@@SubmitSMS API:");
	//
	//
	// HttpThread t = provider.get();
	// t.setId(i);
	// t.setTotalSec(totalTimes);
	// t.setSleepSec(sleepTimes);
	// t.setTargetQuantity(targetQuantity);
	// t.setTargetNum(targetNum);
	//
	// t.setSubmit_sysid(submitsms_sysID);
	// t.setSubmit_source(submitsms_source);
	// t.setSubmit_Content(submitsms_Content);
	// t.setSubmit_Lang(submitsms_Lang);
	// t.setSubmit_Drflag(submitsms_Drflag);
	// t.setSubmit_validType(submitsms_validType);
	//
	// t.start();
	// }
	// // try {
	// // TimeUnit.SECONDS.sleep(15);
	// //// TimeUnit.MINUTES.sleep(3);
	// //
	// //// TimeUnit.HOURS.sleep(1);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// //// System.out.print("############@@@@@@@@@@@@@@@END");
	// }

	@Test
	public void test() {
		// String xmlStructure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SMS
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
		// + "$SysId" + "<Message>" + "$Target" + "$Source" + "$Text" +
		// "$Language" + "$DrFlag" + "$ValidType" + "</Message></SMS>";
		xmlStructure = xmlStructure.replace("$SysId", genXmlBody("SysId", "TEST1"));
		xmlStructure = xmlStructure.replace("$Target", genXmlBody("Target", "0919000001"));
		xmlStructure = xmlStructure.replace("$Source", genXmlBody("Source", "55111"));
		xmlStructure = xmlStructure.replace("$Text", genXmlBody("Text", genText("10C")));
		xmlStructure = xmlStructure.replace("$Language", genXmlBody("Language", "C"));
		xmlStructure = xmlStructure.replace("$DrFlag", genXmlBody("DrFlag", "ture"));
		xmlStructure = xmlStructure.replace("$ValidType", genXmlBody("ValidType", "0"));
		log.debug(xmlStructure);
	}

	public void smsSubmittest(String sysID, String source, String Content, String Lang, String Drflag,
			String validType) {
		String smsSubmitCorrentXmlData = genXmlData(sysID, source, Content, Lang, Drflag, validType);
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void queryDRtest(String sysID, String MessageId, String BNumber, String Type) {
		String smsSubmitCorrentXmlData = genQueryDRXmlData(sysID, MessageId, BNumber, Type);

		String TestURL = ApiServerURL + QueryDRAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void retrieveDRtest(String sysID) {
		String smsSubmitCorrentXmlData = genRetrieveDRXmlData(sysID);

		String TestURL = ApiServerURL + RetrieveDRAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void batchRetrieveDRtest(String sysID, String MessageId) {
		String smsSubmitCorrentXmlData = genBatchRetrieveDRXmlData(sysID, MessageId);

		String TestURL = ApiServerURL + BatchRetrieveDRAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public String genXmlData(String sysid, String source, String textcontent, String lang, String drflag,
			String validtype) {

		String mdn = "";

		StringBuilder xml = new StringBuilder();
		xml.append(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><SMS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml.append(genXmlBody("SysId", sysid));
		xml.append("<Message>");
		for (int i = 1; i < targetQuantity + 1; i++) {
			mdn = targetNum + String.format("%03d", i);
			xml.append(genXmlBody("Target", mdn));
		}
		xml.append(genXmlBody("Source", source));
		xml.append(genXmlBody("Text", genText(textcontent)));
		xml.append(genXmlBody("Language", lang));
		xml.append(genXmlBody("DrFlag", drflag));
		xml.append(genXmlBody("ValidType", validtype));
		xml.append("</Message>");
		xml.append("</SMS>");

		log.debug(xml);
		return xml.toString();
	}

	public String genQueryDRXmlData(String sysid, String MessageId, String BNumber, String Type) {

		String mdn = "";

		StringBuilder xml = new StringBuilder();
		xml.append(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><QueryDR xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml.append(genXmlBody("SysId", sysid));
		xml.append(genXmlBody("MessageId", MessageId));
		xml.append(genXmlBody("BNumber", BNumber));
		xml.append(genXmlBody("Type", Type));
		xml.append("</QueryDR>");

		log.debug(xml);
		return xml.toString();
	}

	public String genRetrieveDRXmlData(String sysid) {

		String mdn = "";

		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><RetrieveDR>");
		xml.append(genXmlBody("SysId", sysid));
		xml.append("</RetrieveDR>");

		log.debug(xml);
		return xml.toString();
	}

	public String genBatchRetrieveDRXmlData(String sysid, String MessageId) {

		String mdn = "";

		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><BatchRetrieveDR>");
		xml.append(genXmlBody("SysId", sysid));
		xml.append(genXmlBody("MessageId", MessageId));
		xml.append("</BatchRetrieveDR>");

		log.debug(xml);
		return xml.toString();
	}

	private String genText(String type) {
		String result = "";
		switch (type) {
		// case 1:
		case "10C":
			// C message : 10 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;

		// case 2:
		case "71C":
			// C long message : 71 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;

		case "100C":
			// C long message : 71 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;
			
		case "150C":
			// C long message : 71 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;	

		// case 3:
		case "334C":
			// C long message : 334 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;

		// case 4:
		case "10E":
			// Eng message : 10 char in base64
			result = "YWFhYWFhYWFhYQ==";
			break;

		case "100E":
			// Eng message : 100 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ==";
			break;

		// case 5:
		case "161E":
			// Eng message : 161 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
			break;

		// case 6:
		case "1001E":
			// Eng message : 1001 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
			break;

		// case 7:
		case "10B":
			// Big-5 message : 10 char in base64
			result = "qXCpcKlwqXCpcKlwqXCpcKlwqXA=";
			break;

		case "71B":
			// BIG5 long message : 71 char in base64
			result = "qXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcKlwqXCpcA==";
			break;

		// case 8:
		case "Spam":
			// Spam Keywords
			result = "5bm55aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;

		case "71Spam":
			// Spam Keywords
			result = "5bm55aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
			break;

		default:
			result = genString(type);
			break;
		}
		return result;
	}

	private String genString(String value) {
		StringBuilder text = new StringBuilder();
		int intValue = Integer.valueOf(value);
		
		for(int n=0;n<intValue;n++)
		{
			text.append("5aaz");
		}

		return text.toString();
	}	
	
	private String genXmlBody(String tag, String value) {
		String result = "";
		result = "<" + tag + ">" + value + "</" + tag + ">";
		return result;
	}

	public void testAPI(String ServerURL, String XmlData) {
		log.info("### XML :");
		log.debug("##############\r\n");
		// System.out.println(XmlData);
		// System.out.println("##############\r\n");

		try {
			String encodeString = "xmlData=" + URLEncoder.encode(XmlData, "UTF-8");
			URL url = new URL(ServerURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			OutputStream outStream = conn.getOutputStream();
			outStream.write(encodeString.getBytes("UTF-8"));
			conn.connect();
			int responseCode = conn.getResponseCode();
			log.debug("### responseCode : [{}]", responseCode);

			InputStream inStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			String line = null;
			log.debug("### responseContent :");
			while ((line = reader.readLine()) != null) {
				log.debug("{}", line);
				// System.out.println(line);
			}
			conn.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
