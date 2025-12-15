package com.ws.msp.HttpLoadTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpThread extends Thread {
	@Autowired
	// @Qualifier("testconfing")
	TestOption option;

	@Setter
	int multiResult=1;
	
	@Setter
	String ApiServerURL = "http://192.168.1.59:8080/api/";

	@Setter
	String SubmitAPI = "smsSubmit?";

	@Setter
	String QueryDRAPI = "smsQueryDR?";

	@Setter
	String RetrieveDRAPI = "smsRetrieveDR?";

	@Setter
	String BatchRetrieveDRAPI = "smsBatchRetrieveDR?";

	@Setter
	int id = 0;

	@Setter
	int totalSec = 0;

	@Setter
	int sleepSec = 0;

	@Setter
	int targetQuantity = 1;

	@Setter
	String targetNum = "0919000";

	@Setter
	String submit_sysid = "";
	@Setter
	String submit_source = "";
	@Setter
	String submit_Content = "";
	@Setter
	String submit_Lang = "";
	@Setter
	String submit_Drflag = "";
	@Setter
	String submit_validType = "";
	@Setter
	String submit_longSmsFlag = "";

	public void run() {
		Thread.currentThread().setName("Thread - " + id);
		int nowtime;
		int thread_times = 0;

		long startTime = System.currentTimeMillis();

		while (true) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTime > totalSec * 1000) {
				// System.out.println("Thread - " + id + " End!!");
				log.debug("Thread Success - " + option.getSuccess() + " End!!");
				log.debug("Thread getFail- " + option.getFail() + " End!!");
				log.debug("Thread - " + id + " End!!");
				break;
			} else {
				nowtime = option.getTimes();
				nowtime++;
				thread_times++;
				option.setTimes(nowtime);
				log.debug("@@@@@@@@@@times : {} !!", option.getTimes());
				log.debug("@@@@@@@@@@thresd {} : {} !!", id, thread_times);
				smsSubmittest(submit_sysid, submit_source, submit_Content, submit_Lang, submit_Drflag,
						submit_validType,submit_longSmsFlag);
				try {
					// Thread.sleep((int)(sleepSec*1000*Math.random()));
					Thread.sleep((int) (sleepSec * 1000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void tteess() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		log.debug("@@@@@@@@@@TestURL : {} !!", TestURL);
		log.debug("@@@@@@@@@@smsSubmitCorrentXmlData : {} !!", smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_LanC() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_LanE() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10E", "E", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_LanB() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10B", "B", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_LanU() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10C", "U", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_SpamKeyword() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "Spam", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_TimeTable() {
		String smsSubmitCorrentXmlData = genXmlData("TimeOff", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_DR() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_short_Validtype() {
		String smsSubmitCorrentXmlData = genXmlData("short_inter", "55111", "10C", "C", "true", "5", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_LanC() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71C", "C", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_LanE() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "161E", "E", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_LanB() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71B", "B", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_LanU() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71C", "U", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_SpamKeyword() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71Spam", "C", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_TimeTable() {
		String smsSubmitCorrentXmlData = genXmlData("TimeOff", "55111", "71C", "C", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_DR() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71C", "C", "true", "0", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_inter_long_Validtype() {
		String smsSubmitCorrentXmlData = genXmlData("long_inter", "55111", "71C", "C", "true", "5", "true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_LanC() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_LanE() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10E", "E", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_LanB() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10B", "B", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_LanU() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10C", "U", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_SpamKeyword() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "Spam", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_TimeTable() {
		String smsSubmitCorrentXmlData = genXmlData("TimeOff", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_DR() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10C", "C", "true", "0", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_short_Validtype() {
		String smsSubmitCorrentXmlData = genXmlData("short_intra", "55111", "10C", "C", "true", "5", "false");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_LanC() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71C", "C", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_LanE() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "161E", "E", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_LanB() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71B", "B", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_LanU() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71C", "U", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_SpamKeyword() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71Spam", "C", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_TimeTable() {
		String smsSubmitCorrentXmlData = genXmlData("TimeOff", "55111", "71C", "C", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_DR() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71C", "C", "true", "0","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void HTTP_MT_intra_long_Validtype() {
		String smsSubmitCorrentXmlData = genXmlData("long_intra", "55111", "71C", "C", "true", "5","true");
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void smsSubmittest(String sysID, String source, String Content, String Lang, String Drflag,
			String validType,String longSmsFlag) {
		String smsSubmitCorrentXmlData = genXmlData(sysID, source, Content, Lang, Drflag, validType, longSmsFlag);
		String TestURL = ApiServerURL + SubmitAPI;
		testAPI(TestURL, smsSubmitCorrentXmlData);
	}

	public void smsSubmitXmltest(String sysID, String source, String Content, String Lang, String Drflag,
			String validType,String longSmsFlag) {
		String smsSubmitCorrentXmlData = genRandXmlData(sysID, source, Content, Lang, Drflag, validType, longSmsFlag);
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
			String validtype, String longSmsFlag) {

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
		xml.append(genXmlBody("LongSmsFlag", longSmsFlag));
		
		xml.append("</Message>");
		xml.append("</SMS>");

		log.debug(xml);
		return xml.toString();
	}

	public String genRandXmlData(String sysid, String source, String textcontent, String lang, String drflag,
			String validtype ,String longSmsFlag) {

		String mdn = "";

		StringBuilder xml = new StringBuilder();
		xml.append(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><SMS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml.append(genXmlBody("SysId", sysid));
		xml.append("<Message>");
		int[] order;
		order = GenerateNonDuplicateRan(6);

		for (int ord = 0; ord < 6; ord++) {
			switch (order[ord]) {

			case 0:
				for (int i = 1; i < targetQuantity + 1; i++) {
					mdn = targetNum + String.format("%03d", i);
					xml.append(genXmlBody("Target", mdn));
				}
				break;
			case 1:
				xml.append(genXmlBody("Source", source));
				break;
			case 2:
				xml.append(genXmlBody("Text", genText(textcontent)));
				break;
			case 3:
				xml.append(genXmlBody("Language", lang));
				break;
			case 4:
				xml.append(genXmlBody("DrFlag", drflag));
				break;
			case 5:
				xml.append(genXmlBody("ValidType", validtype));
				break;
			case 6:
				xml.append(genXmlBody("LongSmsFlag", longSmsFlag));
				break;				

			default:
				log.debug("### genRandXmlData : wrong number");
				break;

			}

		}
		xml.append(genXmlBody("Target", "0919000100"));
		xml.append("</Message>");
		xml.append("</SMS>");

		log.debug(xml);
		return xml.toString();
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

		case "70C":
			// C long message : 71 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
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

		case "333C":
			// C long message : 334 char in base64
			result = "5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz5aaz";
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

		case "160E":
			// Eng message : 161 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
			break;
		// case 5:
		case "161E":
			// Eng message : 161 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
			break;

		case "1000E":
			// Eng message : 1001 char in base64
			result = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
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

		case "Symbol":
			// “×←&÷↑<±→>ƒ↓√↔¿∞⇐«∠⇑»∫⇒‘°⇓’≠⇔“≡♠”≤♣¶≥♥§⊥©½α®¼β™¾γ€‰Δ¢∴θ£πλ¥¹Σ…²τ⊕³ω∇Ω~!@#$%^&*()_+{}":?
			result = "4oCcw5fihpAmw7fihpE8wrHihpI+xpLihpPiiJrihpTCv+KInuKHkMKr4oig4oeRwrviiKvih5LigJjCsOKHk+KAmeKJoOKHlOKAnOKJoeKZoOKAneKJpOKZo8K24oml4pmlwqfiiqXCqcK9zrHCrsK8zrLihKLCvs6z4oKs4oCwzpTCouKItM64wqPPgM67wqXCuc6j4oCmwrLPhOKKlcKzz4niiIfOqX4hQCMkJV4mKigpXyt7fSI6Pw==";
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
		log.debug("### XML :");
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

			if (multiResult == 1) {
				int result;
				if (responseCode == 200) {
					result = option.getSuccess();
					result++;
					option.setSuccess(result);
					log.debug("#####Thread Success - " + option.getSuccess());
				} else {
					result = option.getFail();
					result++;
					option.setFail(result);
					log.debug("#####Thread Fail- " + option.getFail());
				}
			}

			InputStream inStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			String line = null;
			log.debug("### responseContent :");
			while ((line = reader.readLine()) != null) {
				// log.debug("{}", line);
				System.out.println(line);
			}
			conn.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
