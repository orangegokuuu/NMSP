package com.ws.api.mvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.ws.api.util.ChackUtils;
import com.ws.api.util.HttpApiUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.SMS;
import com.ws.httpapi.pojo.SMSResp;
import com.ws.jms.service.JmsService;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.QuotaManager;
import com.ws.msp.service.SmsRecordManager;
import com.ws.smpp.SmsRequest;

import ie.omk.smpp.util.DefaultAlphabetEncoding;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/api")
@Log4j2
public class SmsSubmitController {

	@Autowired
	private MspProperties properties;

	@Autowired
	private QuotaManager quotaManager;

	@Autowired
	ChackUtils chackUtils;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Autowired
	ContentProviderManager contentProviderManager;

	@Autowired
	JmsService jmsService;

	@Autowired
	XmlUtils xmlUtils;

	boolean switchServer = false;

	DefaultAlphabetEncoding defaultEnc = new DefaultAlphabetEncoding();

	@RequestMapping(value = "/SmsSubmit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String request(
			@RequestParam(value = "xmlData", required = false, defaultValue = "") String xml,
			HttpServletRequest request) {
		String result = "";
		String resultCode = "";
		long systemTimes = System.currentTimeMillis();
		long tempTimes = 0;
		SMSResp resp = new SMSResp();
		Date nowDate = new Date();
		SMS sms = null;
		ContentProvider cp = null;
		String reqMsgId = null;
		String text = "";
		MessageObject mObj = null;

		try {
			reqMsgId = smsRecordManager.getSeq();
			// validate Xml
			if (xml != null && !"".equals(xml)
					&& xml.length() > properties.getApi().getContent().getXml().getMaxLength()) {
				log.warn("xml length is too long ,xml:[{}]", xml);
			}
			log.debug("SmsSubmit request xml:[{}], from [{}]", xml, request.getRemoteAddr());
			sms = (SMS) xmlUtils.XmlToObject(xml, SMS.class);
			resultCode = xmlUtils.checkSms(sms);
			log.debug("==== checkSms resultCode:[{}]", resultCode);
			tempTimes = System.currentTimeMillis();
			log.debug(" check Through_put & TimeTable & OA & Text & SpamKeyWord  start Time :[{}]",
					tempTimes);
			if (resultCode.equals("")) {
				// get ContentProvider
				cp = contentProviderManager.get(ContentProvider.class, sms.getSysId());
				if (cp == null) {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				} else if (cp.STATUS_INACTIVE.equals(cp.getStatus())) { // check cp status
					resultCode = ApiConstant.RC_INVALID_SYSID;
					log.info("from [{}] cp sysid:[{}] ,status:[{}] ", request.getRemoteAddr(), sms.getSysId(),
							cp.getStatus());
				} else {
					// Authentication check
					text = HttpApiUtils.getBase64DecodedText(
							removeNonVisible(sms.getMessage().getText()),
							sms.getMessage().getLanguage(), "UTF-8");
					resultCode = this.validateSMS(sms, text, nowDate, cp);
					log.debug("==== validateSMS resultCode:[{}]", resultCode);
					log.debug(
							" check Through_put & TimeTable & OA & Text & SpamKeyWord  end Time :[{}]",
							System.currentTimeMillis() - tempTimes);
				}
			}
			// generate MessageObject
			if (resultCode.equals("")) {

				tempTimes = System.currentTimeMillis();
				log.debug(" check BlackList & MNP start Time :[{}]", tempTimes);

				Map<String, List<Object>> map = this.parserSms(sms, text, nowDate, cp, reqMsgId);

				log.debug(" check BlackList & MNP end Time :[{}]",
						System.currentTimeMillis() - tempTimes);
				tempTimes = System.currentTimeMillis();
				log.debug(" DB insert start Time :[{}]", tempTimes);

				smsRecordManager.batchSave2(map.get("sms"));
				// for(Object obj:map.get("sms")){
				// SmsRecord smsr = (SmsRecord)obj;
				// smsRecordManager.save(SmsRecord.class, smsr);
				// }

				log.debug(" DB insert end Time :[{}]", System.currentTimeMillis() - tempTimes);
				tempTimes = System.currentTimeMillis();
				log.debug(" Enqueue start Time :[{}]", tempTimes);
				log.debug("===== Enqueue map size:[{}]", map.get("mobj").size());
				int quotaCount = 0;
				// XXX 2017-11-30 modify by matthew
				List<String> daList = new ArrayList<String>();
				Map<String, String> msgIdMap = new HashMap<String, String>();
				Map<String, Date> dateMap = new HashMap<String, Date>();
				for (Object obj : map.get("mobj")) {
					mObj = (MessageObject) obj;
					if (mObj.isEnqueue()) {
						log.debug("===== Enqueue mobj ws_msg_id:[{}]", mObj.getWsMessageId());
						daList.add(mObj.getDestination());
						msgIdMap.put(mObj.getDestination(), mObj.getWsMessageId());
						dateMap.put(mObj.getDestination(), mObj.getCreateDate());
						// jmsService.sendMsg(mObj,
						// properties.getDal().getJms().getMtQueueName(),cp.getPriority());
						quotaCount++;
					}
				}
				if (mObj != null && daList.size() > 0) {
					log.debug("===== Enqueue isEnqueue:[{}]", mObj.isEnqueue());
					log.debug("===== daList size:[{}], Da:[{}]", daList.size(), daList.toString());
					mObj.setDa(daList);
					mObj.setWsMessageIdMap(msgIdMap);
					mObj.setCreateDateMap(dateMap);
					mObj.setSystemProcessTime(systemTimes);
					// if (mObj.isEnqueue()) {
					// jmsService.sendMsg(mObj, properties.getDal().getJms().getMtQueueName(),
					// cp.getPriority());
					jmsService.sendMessage(mObj, cp.getPriority(),
							properties.getDal().getJms().getMtQueueName());
					// }
					log.debug(" Enqueue end Time :[{}]", System.currentTimeMillis() - tempTimes);
				} else {
					log.debug(" Not Enqueue end Time :[{}]",
							System.currentTimeMillis() - tempTimes);
				}

				boolean combine = true;
				if (StringUtils.isNotBlank(sms.getMessage().getLongSmsFlag())) {
					combine = sms.getMessage().getLongSmsFlag().equals("false") ? false : true;
				}

				quotaManager.processSmsSubmit(cp.getCpId(), quotaCount
						* this.getMessageSplitCount(sms.getMessage().getLanguage(), text, combine));
				resp.setMessageId(reqMsgId);
				resp.setResultCode(ApiConstant.RC_SUCCESS);
				resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			} else {
				resp.setResultCode(resultCode);
				resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			}
		} catch (JAXBException e) {
			log.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_INVALID_XML, xml);
		} catch (SAXException e) {
			log.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_INVALID_XML, xml);
		} catch (ParserConfigurationException e) {
			log.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_INVALID_XML, xml);
		} catch (JMSException e) {
			log.error("[WISEMQ] error");
			log.error(e, e);
			resp.setResultCode(ApiConstant.RC_UNKNOW_ERROR);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_UNKNOW_ERROR, xml);
		} catch (DataAccessException e) {
			log.error("[DB] error:[{}]", e.getMessage());
			log.error(e, e);
			resp.setResultCode(ApiConstant.RC_DB_ERROR);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_DB_ERROR, xml);
		} catch (Exception e) {
			log.warn("smsSubmit error:[{}]", e.getMessage());
			log.warn(e, e);
			resp.setResultCode(ApiConstant.RC_UNKNOW_ERROR);
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_UNKNOW_ERROR, xml);
		} finally {
			// object convert to xml
			try {
				resp.setMessageId(reqMsgId);
				result = xmlUtils.ObjectToXml(resp, SMSResp.class);
			} catch (JAXBException e) {
				log.error(e, e);
				resp.setResultCode(ApiConstant.RC_INVALID_XML);
				resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
				log.warn("From [{}] ResultCode:[{}] , xml:[{}]", request.getRemoteAddr(), ApiConstant.RC_INVALID_XML,
						xml);
			}
			if (mObj != null) {
				log.info(
						"[SmsSubmit] submit from [{}], return code [{}], REQ_MSG_ID [{}], sysId : [{}], submit date : [{}], source address : [{}], destnation : [{}], API process time [{}]",
						request.getRemoteAddr(),
						resp.getResultCode(),
						resp.getMessageId(),
						mObj.getCpId(), nowDate, mObj.getSource(),
						sms.getMessage().getTarget().toString(),
						System.currentTimeMillis() - systemTimes);
			} else {
				log.info("[SmsSubmit] submit from [{}] return code [{}], xml:[{}], API process time [{}]",
						request.getRemoteAddr(),
						resp.getResultCode(),
						xml,
						System.currentTimeMillis() - systemTimes);
			}
		}

		return result;
	}

	@RequestMapping(value = "/healthCheck", method = { RequestMethod.GET, RequestMethod.POST })
	public void healthCheck(final HttpServletResponse response) throws IOException {
		if (switchServer) {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "something wring, switch server!");
		}
	}

	@RequestMapping(value = "/switchServer/{flag}", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public boolean switchServer(@PathVariable("flag") boolean flag) {
		switchServer = flag;
		return switchServer;
	}

	private String validateSMS(SMS sms, String text, Date nowDate, ContentProvider cp) {
		String resultCode = "";
		try {
			// check mq user
			if (cp.getCpType().equals("2") && sms.getMessage().getIsMQ().equals("false")) {
				resultCode = ApiConstant.RC_INVALID_SYSID;
				log.debug(
						"==== validateSMS ,MQ user use http Api , reject it. cp_id[{}], resultCode:[{}]",
						cp.getCpId(), resultCode);
				return resultCode;
			}

			// Through put check
			// 20171214 YC MODIFY
			boolean combine = true;
			if (StringUtils.isNotBlank(sms.getMessage().getLongSmsFlag())) {
				combine = sms.getMessage().getLongSmsFlag().equals("false") ? false : true;
			}
			// if(!quotaManager.checkSubmitLimit(cp.getCpId(),
			// sms.getMessage().getTarget().size())){
			if (!quotaManager.checkSubmitLimit(cp.getCpId(), sms.getMessage().getTarget().size()
					* this.getMessageSplitCount(sms.getMessage().getLanguage(), text, combine))) {
				resultCode = ApiConstant.RC_THROUGHTPUT_EXCEED;
				log.debug(
						"==== validateSMS ,Through put check , cp_id:[{}], message count:[{}], resultCode:[{}]",
						cp.getCpId(),
						sms.getMessage().getTarget().size() * this.getMessageSplitCount(
								sms.getMessage().getLanguage(), text, combine),
						resultCode);
				return resultCode;
			}

			// check cp submit limit
			if (cp.getCpType().equals("1")
					&& sms.getMessage().getTarget().size() > cp.getDaLimit()) {
				resultCode = ApiConstant.RC_INVALID_DA;
				log.debug("==== validateSMS ,cp submit limit check , cp_id[{}], resultCode:[{}]",
						cp.getCpId(), resultCode);
				return resultCode;
			}
			// CP API_Version =2 ,check longSMSFlag
			if (cp.getApiVersion().equals("2") && sms.getMessage().getLongSmsFlag() == null) {
				resultCode = ApiConstant.RC_INVALID_XML;
				log.debug("==== validateSMS CP API_Version:[{}], XML LongSmsFlag:[{}]",
						cp.getApiVersion(), sms.getMessage().getLongSmsFlag());
				return resultCode;
			}
			// Time Table check ,just check HTTP, HTTP:1 MQ:2 FILE:3
			if (cp.getCpType().equals("1")) {
				resultCode = chackUtils.checkTimeTable(sms.getSysId(), nowDate, cp);
				log.debug("==== validateSMS ,Time Table check , cp_id[{}], resultCode:[{}]",
						cp.getCpId(), resultCode);
			}
			// check oa
			if (resultCode.equals("")) {
				resultCode = chackUtils.checkOA(sms.getSysId(), sms.getMessage().getSource(), cp);
				log.debug("==== validateSMS ,OA check , cp_id[{}], resultCode:[{}]", cp.getCpId(),
						resultCode);
			}
			// XXX modify 2017-10-30
			if (!StringUtils.isBlank(text)) {
				// check text length
				if (resultCode.equals("")) {
					resultCode = chackUtils.checkTextLength(sms.getSysId(), text,
							sms.getMessage().getLanguage(), cp);
					log.debug(
							"==== validateSMS ,text length check done, text:[{}], cp_id[{}], resultCode:[{}]",
							text, cp.getCpId(), resultCode);
				}
				// Spam check
				if (resultCode.equals("")) {
					resultCode = chackUtils.checkSpamKeyWord(sms.getSysId(), text, cp);
					log.debug(
							"==== validateSMS ,Spam keyword check , text:[{}], cp_id[{}], resultCode:[{}]",
							text, cp.getCpId(), resultCode);
				}
			}
			// XXX end

		} catch (DataAccessException e) {
			log.error("[DB] validateSMS Error, mesage:[{}]", e.getMessage());
			log.error(e, e);
			throw new DataAccessException(e);
		} catch (Exception e) {
			log.error("[RUNTIME] validateSMS Error, mesage:[{}]", e.getMessage());
			log.error(e, e);
			resultCode = ApiConstant.RC_INVALID_XML;
		}
		return resultCode;
	}

	private Map<String, List<Object>> parserSms(SMS sms, String text, Date nowDate,
			ContentProvider cp, String reqMsgId) {
		Map<String, List<Object>> map = null;
		try {
			// source Address
			boolean isChargeB = false;
			if (sms.getMessage().getSource().length() == 20) {
				String chargeArray[] = properties.getApi().getFet().getChargeB().split(",");
				String oa = sms.getMessage().getSource().substring(18, 20);
				if (!oa.equals("00") && !oa.equals("99")) {
					for (String s : chargeArray) {
						if (oa.equals(s)) {
							isChargeB = true;
							break;
						}
					}
				}
			} else {
				isChargeB = true;
			}
			map = new HashMap<String, List<Object>>();
			List<Object> smsList = new ArrayList<Object>();
			List<Object> mObjList = new ArrayList<Object>();

			// add by YC 2018-04-10
			boolean combine = true;
			if (StringUtils.isNotBlank(sms.getMessage().getLongSmsFlag())) {
				combine = sms.getMessage().getLongSmsFlag().equals("false") ? false : true;
			}

			// DA list
			List<String> DAs = sms.getMessage().getTarget();
			for (String da : DAs) {
				LocalDateTime insertDate = LocalDateTime.now();
				MessageObject emg = new MessageObject();
				SmsRecord smsRecord = new SmsRecord();
				String wsMsgId = smsRecordManager.getWsMsgId();
				smsRecord.setWsMsgId(wsMsgId);
				smsRecord.setReqMsgId(reqMsgId);
				smsRecord.setSysId(sms.getSysId());
				smsRecord.setOa(sms.getMessage().getSource());

				smsRecord.setDa(da);
				// add by matthew 2018-04-10
				smsRecord.setValidType(sms.getMessage().getValidType());
				// add by YC 2018-04-10
				smsRecord.setTotalSeg(
						this.getMessageSplitCount(sms.getMessage().getLanguage(), text, combine));

				// XXX trim + and prefix 09 change to 886
				if (da != null && da.trim().startsWith("+")) {
					da = da.substring(1, da.trim().length());
				}

				if (da != null && da.trim().length() == 9 && da.trim().startsWith("9")) {
					da = "886" + da;
				} else if (da != null && da.trim().length() == 10 && da.trim().startsWith("09")) {
					da = "886" + da.substring(1, da.length());
				} else if (da != null && da.trim().length() == 13 && da.trim().startsWith("040")) {
					da = "886" + da.substring(1, da.length());
				}
				// XXX end change prefix

				// smsRecord.setDa(da); move up
				smsRecord.setLanguage(sms.getMessage().getLanguage());
				smsRecord.setText(sms.getMessage().getText());
				if ("true".equals(sms.getMessage().getDrFlag())) {
					smsRecord.setDrFlag(true);
				} else if ("false".equals(sms.getMessage().getDrFlag())) {
					smsRecord.setDrFlag(false);
				}
				smsRecord.setSmsSourceType("");
				smsRecord.setAcceptDate(nowDate);
				smsRecord.setPriorityFl(cp.getPriority());
				smsRecord.setSmsType("MT");
				smsRecord.setIsInter(String.valueOf(cp.getCpZone()));
				smsRecord.setCreateDate(insertDate);
				// check black list
				String blcode = chackUtils.checkDABlackList(sms.getSysId(), da, cp);
				if (!blcode.equals("")) { // is black list
					smsRecord.setIsBlacklist("1");
					smsRecord.setAcceptStatus(ApiConstant.SC_DA_IN_BL);
					emg.setEnqueue(false);
				} else { // not black list
					smsRecord.setAcceptStatus(ApiConstant.SC_SUCCESS);
					smsRecord.setIsBlacklist("0");
				}
				// check mnp
				if (blcode.equals("")) {
					log.debug("===== check mnp,B Number:[{}],soure Address:[{}],isChargeB[{}]", da,
							sms.getMessage().getSource(), isChargeB);
					if (isChargeB) {
						if (!chackUtils.checkMnp(da)) {
							// not fet customer ,Charging Party Address is Off-Net
							smsRecord.setAcceptStatus(ApiConstant.SC_DA_IS_OFF_NET);
							emg.setEnqueue(false);
						} else {
							smsRecord.setAcceptStatus(ApiConstant.SC_SUCCESS);
						}
					} else {
						smsRecord.setAcceptStatus(ApiConstant.SC_SUCCESS);
					}
				}
				// wsMsgId
				emg.setWsMessageId(wsMsgId);
				// DA
				emg.setDestination(da);
				// DR
				if (StringUtils.isNoneBlank(sms.getMessage().getDrFlag())) {
					boolean flag = false;
					if (sms.getMessage().getDrFlag().equals("true")) {
						flag = true;
					}
					// for test use
					if ("true".equals(properties.getEmg().getTest_mo_sw())) {
						flag = false;
					} else {
						flag = true;
					}
					emg.setRequestDR(flag);
				}
				// Data coding
				String tmpLang = sms.getMessage().getLanguage();
				int dataCoding = SmsRequest.ASCII;
				if (StringUtils.isNoneBlank(tmpLang)) {
					if (tmpLang.toUpperCase().equals("C")) { // Chinese
						dataCoding = 8;
					} else if (tmpLang.toUpperCase().equals("E")) { // English
						// setting on properties for FET environment
						dataCoding = properties.getEmg().getSmpp().getDcsASCII();
					} else if (tmpLang.toUpperCase().equals("B")) { // Big5
						dataCoding = 8;
					} else if (tmpLang.toUpperCase().equals("U")) { // UTF
						dataCoding = 8;
					}
				}
				emg.setDataCoding(dataCoding);
				// OA
				emg.setSource(sms.getMessage().getSource());
				// Text
				// XXX modify 2017-10-30
				emg.setMessage(text);
				// XXX end
				// Validate period
				// String tmpValidPeriod = sms.getMessage().getValidType();
				emg.setValidity(sms.getMessage().getValidType());

				emg.setCpId(sms.getSysId());
				emg.setMessageType(ApiConstant.TYPE_SMS);
				if (cp.getApiVersion().equals("2")) {
					if (sms.getMessage().getLongSmsFlag().equals("false")) {
						emg.setLongType(false);
					} else {
						emg.setLongType(true);
					}
				}
				//
				emg.setPsa(cp.isThroughPSA());
				emg.setCreateDate(Date.from(insertDate.atZone(ZoneId.systemDefault()).toInstant()));

				// add to mObj list
				mObjList.add(emg);
				// add to sms list
				smsList.add(smsRecord);

			}
			map.put("sms", smsList);
			map.put("mobj", mObjList);
		} catch (Exception e) {
			log.error("[RUNTIME] parserSms Error, mesage:[{}]", e.getMessage());
			map = null;
		}
		return map;
	}

	private int getMessageSplitCount(String codingType, String text, boolean combine) {

		int result = 1;
		int splitLength = properties.getApi().getContent().getSplitLength();
		int headerLength = properties.getApi().getContent().getHeaderLength();
		int contentLength = 0;

		if (StringUtils.isNotBlank(text)) {
			contentLength = text.length();
			if (StringUtils.isNotBlank(codingType) && "E".equals(codingType)) {
				headerLength += 1; // UNUSED_BIT need to added for gsm7bit
				splitLength = 160;

				// get GSM7bit
				try {
					byte[] message = null;
					message = defaultEnc.encodeString(new String(text.getBytes("UTF-8")));
					contentLength = message.length;

					if (message.length <= properties.getApi().getContent().getEnglish().getMinLength()) {
						return result;
					}
				} catch (Exception e) {
					log.warn("Get GSM7bit lenth fail, cause [{}]", e.getMessage());
					log.warn(e, e);
					return result;
				}
			} else {// chinese text double byte
				contentLength *= 2;
			}

			if (!combine) {
				headerLength = 0;
			}

			if (contentLength > splitLength) {
				result = (contentLength / (splitLength - headerLength));
				if ((contentLength % (splitLength - headerLength)) != 0) {
					result += 1;
				}
			}
		} else {
			result = 0;
		}

		log.debug(
				"IsCombine : [{}], Text Length : [{}], Language : [{}], UDH Length : [{}], Split length : [{}], Total seg for massage : [{}]",
				combine, contentLength, codingType, headerLength, splitLength, result);

		return result;
	}

	/**
	 * remove split line in content
	 * 
	 * @param content
	 * @return
	 */
	private String removeNonVisible(String content) {

		if (StringUtils.isNoneBlank(content)) {
			content = content.replaceAll("\\s+", "");
		}
		return content;
	}
}
