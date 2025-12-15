package com.ws.api.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.ws.api.util.ChackUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.pojo.MessageObject;
import com.ws.httpapi.pojo.DeliverSM;
import com.ws.httpapi.pojo.DeliverSMResp;
import com.ws.httpapi.pojo.PushDR;
import com.ws.httpapi.pojo.PushDRResp;
import com.ws.jms.service.JmsService;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.pojo.CpDestinationAddress;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.BlackListManager;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.FetPrefixManager;
import com.ws.msp.service.MnpApiPhoneroutinginfoManager;
import com.ws.msp.service.SmsRecordManager;
import com.ws.smpp.SmsRequest;

@Controller
@RequestMapping("/test")
public class DemoXmlController {

	private static Logger logger = LogManager.getLogger(DemoXmlController.class);
	
	@Autowired
	JmsService jmsService;
	
	@Autowired
	BlackListManager blackListManager;
	
	@Autowired
	MnpApiPhoneroutinginfoManager mnpApiPhoneroutinginfoManager;
	
	@Autowired
	FetPrefixManager fetPrefixManager;
	
	@Autowired
	SmsRecordManager smsRecordManager;
	
	@Autowired
	ContentProviderManager contentProviderManager;
	
	@Autowired
	ChackUtils chackUtils;
	
	@Autowired
	XmlUtils xmlUtils;

	// @RequestMapping(value = "/Demo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String demo(@RequestParam("xml") String xml) {

		String result = "";

		//System.out.println("==========   seq :" + smsRecordManager.getSeq());
		String da = "0919000001";
		String type = "mnp";
		String array[] = xml.split(",");
		if(array.length>1){
			type = array[0];
			da = array[1];
		}
		
		if(type.equals("blacklist")){
			boolean sw = blackListManager.checkBlackListInCache(da);
			logger.debug("==== test http api get cache blacklist:[{}]", sw);
		}
		else if(type.equals("mnp")){
			MnpApiPhoneroutinginfo  info = mnpApiPhoneroutinginfoManager.getFromCache(da);
			logger.debug("==== test http api get cache mnp:[{}]", info.getPhoneNumber());
		}
		else if(type.equals("prefix")){
			boolean sw = fetPrefixManager.checkFetPrefixInCache(da);
			logger.debug("==== test http api get cache prefix:[{}]", sw);
		}
		result = "xml="+xml;

		return result;
	}

	// @RequestMapping(value = "/db", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String dbTest(@RequestParam("xml") String xml) {

		String result = "";
//		String type = "";
//		int count = 0;
//		String array[] = xml.split(",");
//		if(array.length>1){
//			type = array[0];
//			count = Integer.valueOf(array[1]);
//		}
//		double tempTimes = System.currentTimeMillis();
//		
//		List<Object> list = new ArrayList<Object>();
//		List<SmsRecordSub> subList = new ArrayList<SmsRecordSub>();
//		try{
//			Date insertDate = new Date();
//			for(int i=1 ;i<=count;i++){
//				SmsRecord smsRecord = new SmsRecord();
//				String wsMsgId = smsRecordManager.getWsMsgId();
//				smsRecord.setWsMsgId(wsMsgId);
//				smsRecord.setReqMsgId("12345678");
//				smsRecord.setSysId("TEST1");
//				smsRecord.setOa("55511");
//				smsRecord.setDa("0909000001");
//				smsRecord.setLanguage("E");
//				smsRecord.setText("fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666fuckyoumom0809446666"
//						+ "fuckyoumom0809446666fuckyoumom0809446666");
//				smsRecord.setDrFlag(0);
//				smsRecord.setSmsSourceType("");      
//				smsRecord.setAcceptDate(new Date());            
//				smsRecord.setPriorityFl(4);         
//				smsRecord.setSmsType("MT");
//				smsRecord.setIsInter("1");
//				smsRecord.setAcceptStatus("0000");
//				smsRecord.setIsBlacklist("0");
//				smsRecord.setCreateDate(insertDate);
//				list.add(smsRecord);
//				SmsRecordSub sub = new SmsRecordSub();
//				long subId = smsRecordManager.getSubId();
//				sub.setSubId(subId);
//				sub.setSubmitDate(new Date());
//				sub.setSubmitStatus("0");
//				sub.setSegNum("1");
//				sub.setCreateDate(insertDate);
//				sub.setWsMsgId(wsMsgId);
//				subList.add(sub);
//			}
//			
//			logger.info("=========== test insert db generate SmsRecord time:[{}]", (System.currentTimeMillis() - tempTimes));
//			
//			if(type.equals("testdb")){
//				logger.info("=========== test insert db start time:[{}]", tempTimes);
//				for(Object obj:list){
//					SmsRecord smsRecord = (SmsRecord)obj;
//					smsRecordManager.save(SmsRecord.class, smsRecord);
//				}
//			}
//			else if(type.equals("testdb2")){
//				
//				tempTimes = System.currentTimeMillis();
//				logger.info("=========== test insert db 2 start time:[{}]", tempTimes);
//				smsRecordManager.batchSave2(list);
//				smsRecordManager.subBatchSave(subList);
//			}
//			tempTimes = (System.currentTimeMillis() - tempTimes);
//			logger.info("=========== test insert db end time:[{}]", tempTimes);
//			result = "insert db Times="+(tempTimes/1000);
//		}catch(Exception e){
//			logger.info("=========== ERROR:[{}]", e.getMessage());
//		}

		return result;
	}

	// @RequestMapping(value = "/mo", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String moTest(@RequestParam("xml") String xml) {
		String result = "ssss";
//		String[] ad = {"01200100000000000001","01226[0-9]{15}","01227[0-9]{13}00","01299900000000000000","013632[0-9]{10}0000","013633[0-9]{10}0000","0137280000000000[0-9]{4}","01470009310000990000","01500009310000990000","01500[0-9]{1}09310000990102","01501009310000990100","01501109310000990102","01508009310000990000","01508009310000990[0-9]{1}02","01512309310000990000","01512309310000990502","01512309310000991002","01512309310000992002","01512309310000993002","01512309310000995002","01512309310000999002","0151380931000099[0-9]{4}","0151680931000099[0-9]{4}","01520009310000990016","01525809310000990000","01525809310000990[0-9]{1}02","0152580931000099[0-9]{4}","01550009310000990000","01550009310000990100","01550[1-9]{1}09310000990102","0155123[0-9]{13}","015585[0-9]{10}0000","015585[0-9]{10}0509","015585[0-9]{10}1002","015585[0-9]{10}2002","015585[0-9]{10}3002","015585[0-9]{10}5002","015585[0-9]{10}7002","0155888[0-9]{13}","0155987[0-9]{11}00","0156860931000099[0-9]{4}","01581809310000990[0-6]{1}00","01581809310000990[0-6]{1}02","01582809310000990[0-1]{1}00","01582809310000990[3-5]{1}02","0158280931000099[1-5]{1}002","01588609310000990000","01588609310000990502","01588609310000999902","0158860931000099[1-7]{1}002","0158880931000099[0-9]{4}","0158880931000099[1-5]{1}002","01589809310000990000","01589809310000990[1-9]{1}02","01589809310000991[0-2]{1}02","01589809310000991[0-7]{1}02","01589809310000999900","01596810399999990502","01596810399999991[1-5]{1}02","01596810399999995502","01596810399999999502","0159681039999999[6-7]{1}002","015968[0-2]{14}","01598709310000990502","01598709310000990[0-1]{1}00","01598709310000999902","0159870931000099[1-7]{1}002","01598710399999991202","01598710399999991502","01598710399999999002","01666809310000990000","01666809310000990102","017110[0-9]{10}0100","017110[0-9]{10}0202","017110[0-9]{10}0302","01711[0-9]{11}0102","01711[0-9]{15}","01818110399999990000","01818110399999990502","01818110399999991002","01828[0-9]{15}","01828[0-9]{1}0931000099[0-9]{2}(00|02|03|09)","018380[0-9]{12}00","018381[0-9]{12}02","01900200001290000100","01900200014109600017","01900200014109800017","01900200014110300017","01900200014111300017","01900200014120700017","01900200014135500017","01900200014170900000","01900200020210600017","01900200020223000017","01900200024224100124","019007[0-9]{2}000000000000","019710[0-9]{10}0201","01987609540009210000","01999900028010000000","01999900028020000000","01999900028030000000","01999900028040000000","01999900047010000000","01999900049010000000","01999900049020000000","01999900056010000000","019999000580[0-9]{1}0000000","019999000581[0-9]{1}0000000","01999900062010000000","01999900062020000000","01999900063010000000","01999900066010000000","01999900067010000000","01999900068010000000","01999900119000000000","01999900158000000099","558181","55828","55886","55888","55987","66688","886931181801","886931181802","886931181810","886931181821","886931181856","886931181878","886931181898","886931188814","886931188880","886931188881","886936018183","886936019027","886936019031","886936019032","886936019033","886936019177","886936019178","886936019236","886936019398","886936019925","886936019926","886936019931","^211","^211[0-9]*","^211[0-9]{10}","^211[0-9]{12}","^211[0-9]{9}","^214"};
//		String[] cp = {"QWAREWF","VPSDMZQ","VPSDMZ2","WLANC5A","EMOTICP","EMOTICT","MOBWALT","55147QA","55150QA","55150QA","55150QA","55150QA","55080QA","55080QA","55123TW","55123TW","55123TW","55123TW","55123TW","55123TW","55123TW","55138QA","55168QA","5200NEW","55258QA","55258QA","55258QA","55155QB","55155QB","55155QB","55123TW","NETLUON","NETLUON","NETLUON","NETLUON","NETLUON","NETLUON","NETLUON","55987E3","3E55987","55686QA","55818AB","55818AB","55828QA","55828QA","55828QA","55886E3","55886E3","55886E3","55886E3","55987E3","55987E3","55898QA","55898QA","55898QA","55898QA","55898QA","WK77968","WK77968","WK77968","WK77968","WK77968","WK77968","3E55987","3E55987","3E55987","3E55987","3E55987","3E55987","3E55987","66688QA","66688QA","711888A","711888A","711888A","711888A","711888A","55818AB","55818AB","55818AB","BLKOCTO","BLKOCTO","NETLUON","NETLUON","PETLINE","INTE366","SYNCCOM","BCDLINK","ARCOACO","ABKIQEG","GYYSFCT","1KL3Q2A","CUNDOCA","YAIWIOQ","CNMEWO5","900PRBT","YAHOOQA","EZMOBOA","OTAQISA","OTAQIPA","OTAQESA","OTAQEPA","CHESSPA","MMSAPQA","MMSAPQB","MTAIWAN","RBTME2A","RBTME2B","NATRALA","NATRALA","CSPSMSP","DVBHMQA","PCCLNTT","CCNETQT","NATRALA","MRNAVIP","55818AB","55828QA","55886E3","55987E3","3E55987","66688QA","VPSDMZQ","VPSDMZQ","EZMOBOA","MTAIWAN","CNMEWO5","APMOBIL","APMOBIL","PETLINE","CITIBQA","CITIBQB","5200NEW","CHESSPA","NATRALA","NATRALA","CSPSMSP","55818AB","55818AB","NATRALA","MRNAVIP","MMSAPQA","MMSAPQB","QWAREWF","EMOTICP","EMOTICP","EMOTICP","EMOTICP","EMOTICP","EMOTICT"};
//		
//		for(int i=0;i<cp.length;i++){
//			CpDestinationAddress cda = new CpDestinationAddress();
//			cda.setCpId(cp[i]);
//			cda.setDestinationAddress(ad[i]);
//			contentProviderManager.save(CpDestinationAddress.class, cda);
//		}
		
		for(int i=0;i<20000;i++){
			logger.info("getsubId:[{}]",smsRecordManager.getSubId());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/deliversm", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String deliversm(@RequestParam("xmlData") String xml) {

		String result = "";

		try {
			logger.debug("deliversm xml:[{}]", xml);
			DeliverSM dsm = (DeliverSM) xmlUtils.XmlToObject(xml, DeliverSM.class);
			
			Thread.sleep(15000);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DeliverSMResp resp = new DeliverSMResp();
			resp.setResultCode("00000");
			try {
				result = xmlUtils.ObjectToXml(resp, DeliverSMResp.class);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
	
	@RequestMapping(value = "/pushdr", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String pushdr(@RequestParam("xmlData") String xml) {

		String result = "";

		try {
			logger.debug("pushdr xml:[{}]", xml);
			PushDR dr = (PushDR) xmlUtils.XmlToObject(xml, PushDR.class);
			
			Thread.sleep(15000);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PushDRResp resp = new PushDRResp();
			resp.setResultCode("00000");
			try {
				result = xmlUtils.ObjectToXml(resp, PushDRResp.class);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
}
