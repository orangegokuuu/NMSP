package com.ws.emg.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import com.ws.api.util.HttpApiUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.emg.constant.SmppConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.httpapi.pojo.DeliverSM;
import com.ws.httpapi.pojo.DeliverSMResp;
import com.ws.httpapi.pojo.PushDR;
import com.ws.httpapi.pojo.PushDRResp;
import com.ws.httpapi.pojo.QueryDR;
import com.ws.httpapi.pojo.RetrieveDR;
import com.ws.httpapi.pojo.RetrieveDRResp;
import com.ws.httpapi.pojo.RetrieveDRResp.DeliveryReport;
import com.ws.httpapi.pojo.SMS;
import com.ws.httpapi.pojo.SMSResp;
import com.ws.smpp.SmsRequest;
import com.ws.util.CommonFileUtil;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class EmgParser {


	public static final int TYPE_DR = 4;

	@Autowired(required = true)
	private XmlUtils xmlUtils;

	/**
	 * parse XML to MessageObject
	 * 
	 * @param xml
	 * @return
	 */
	public List<MessageObject> parse(String xml) {

		List<MessageObject> result = null;

		try {

			if (xml.contains("<DeliverSM")) {
				result = parseDeliverSM(parseXML(xml, DeliverSM.class));
			} else if (xml.contains("<PushDR")) {
				result = parsePushDR(parseXML(xml, PushDR.class));
			} else if (xml.contains("<QueryDR")) {
				result = parseQueryDR(parseXML(xml, QueryDR.class));
			} else if (xml.contains("<RetrieveDR")) {
				result = parseRetrieveDR(parseXML(xml, RetrieveDR.class));
			} else if (xml.contains("<SMS")) {
				result = parseSMS(parseXML(xml, SMS.class));
			}
		} catch (Exception e) {
			log.error("[EMG PARSER] parse xml fail, [{}]", e.getMessage());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object parseXML(String data, Class clazz) throws JAXBException, SAXException, ParserConfigurationException {
		log.debug(xmlUtils);
		return xmlUtils.XmlToObject(data, clazz);
	}

	/**
	 * parse submit_sm from http api
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<MessageObject> parseDeliverSM(Object obj) throws InstantiationException, IllegalAccessException {

		List<MessageObject> result = new ArrayList<>();

		if (obj != null) {
			MessageObject message = new MessageObject();

			if (StringUtils.isNotBlank(((DeliverSM) obj).getSysId())) {
				message.setCpId(((DeliverSM) obj).getSysId());
			}

			if (StringUtils.isNotBlank(((DeliverSM) obj).getMessage().getTarget())) {
				message.setDestination(((DeliverSM) obj).getMessage().getTarget());
			}

			if (StringUtils.isNotBlank(((DeliverSM) obj).getMessage().getSource())) {
				message.setSource(((DeliverSM) obj).getMessage().getSource());
			}

			// Data coding
			String tmpLang = ((DeliverSM) obj).getMessage().getLanguage();
			int dataCoding = SmsRequest.ASCII;
			if (StringUtils.isNoneBlank(tmpLang)) {
				if (tmpLang.toUpperCase().equals("C")) { // Chinese
					dataCoding = SmsRequest.UCS2;
				} else if (tmpLang.toUpperCase().equals("E")) { // English
					dataCoding = SmsRequest.ASCII;
				} else if (tmpLang.toUpperCase().equals("B")) { // Big5
					dataCoding = SmsRequest.UCS2;
				} else if (tmpLang.toUpperCase().equals("U")) { // UTF
					dataCoding = SmsRequest.UCS2;
				}
			}
			message.setDataCoding(dataCoding);

			if (StringUtils.isNotBlank(((DeliverSM) obj).getMessage().getText())) {
				message.setMessage(((DeliverSM) obj).getMessage().getText());
			}

			if (StringUtils.isNotBlank(((DeliverSM) obj).getMessage().getTimestamp())) {
				message.setDeliveredTime(((DeliverSM) obj).getMessage().getTimestamp());
			}

			message.setMessageType(ApiConstant.TYPE_SMS);
			// end of loop
			result.add(message);
		}

		return result;
	}

	/**
	 * parse submit_PushDR from http api
	 * 
	 * @param obj
	 * @return
	 */
	private List<MessageObject> parsePushDR(Object obj) {

		List<MessageObject> result = new ArrayList<>();

		if (obj != null) {
			MessageObject message = new MessageObject();

			if (StringUtils.isNotBlank(((PushDR) obj).getMessageId())) {
				message.setSmscMessageId(((PushDR) obj).getMessageId());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getStatus())) {
				message.setStatus(((PushDR) obj).getStatus());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getBNumber())) {
				message.setDestination(((PushDR) obj).getBNumber());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getTimestamp())) {
				message.setDeliveredTime(((PushDR) obj).getTimestamp());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getDeliveryReport().getId())) {
				message.setPid(Integer.valueOf(((PushDR) obj).getDeliveryReport().getId()));
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getDeliveryReport().getSubmitDate())) {
				message.setSubmitTime(((PushDR) obj).getDeliveryReport().getSubmitDate());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getDeliveryReport().getDoneDate())) {
				message.setDoneTime(((PushDR) obj).getDeliveryReport().getDoneDate());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getDeliveryReport().getState())) {
				message.setState(((PushDR) obj).getDeliveryReport().getState());
			}

			if (StringUtils.isNotBlank(((PushDR) obj).getDeliveryReport().getError())) {
				message.setErrorCode(((PushDR) obj).getDeliveryReport().getError());
			}

			message.setMessageType(ApiConstant.TYPE_PUSHDR);
			result.add(message);
		}

		return result;
	}

	/**
	 * parse submit_QueryDR from http api
	 * 
	 * @param obj
	 * @return
	 */
	public List<MessageObject> parseQueryDR(Object obj) {

		List<MessageObject> result = new ArrayList<>();

		if (obj != null) {
			MessageObject message = new MessageObject();

			if (StringUtils.isNotBlank(((QueryDR) obj).getSysId())) {
				message.setCpId(((QueryDR) obj).getSysId());
			}

			if (StringUtils.isNotBlank(((QueryDR) obj).getMessageId())) {
				message.setSmscMessageId(((QueryDR) obj).getMessageId());
			}

			if (StringUtils.isNotBlank(((QueryDR) obj).getBNumber())) {
				message.setDestination(((QueryDR) obj).getBNumber());
			}

			if (StringUtils.isNotBlank(((QueryDR) obj).getType())) {
				message.setMessageType(((QueryDR) obj).getType());
			}

			message.setMessageType(ApiConstant.TYPE_QUERYDR);
			result.add(message);
		}

		return result;
	}

	/**
	 * parse submit_RetrieveDR from http api
	 * 
	 * @param obj
	 * @return
	 */
	public List<MessageObject> parseRetrieveDR(Object obj) {

		List<MessageObject> result = new ArrayList<>();

		if (obj != null && StringUtils.isNotBlank(((RetrieveDR) obj).getSysId())) {
			MessageObject message = new MessageObject();
			message.setMessageType(ApiConstant.TYPE_RETRIEVEDR);
			message.setCpId(((RetrieveDR) obj).getSysId());

			result.add(message);
		}

		return result;
	}

	/**
	 * parse submit_sm from http api
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<MessageObject> parseSMS(Object obj) throws InstantiationException, IllegalAccessException {

		List<MessageObject> result = new ArrayList<>();

		// DA list
		List<String> DAs = ((SMS) obj).getMessage().getTarget();

		for (String da : DAs) {

			MessageObject emg = new MessageObject();

			// DA
			emg.setDestination(da);

			// DR
			if (StringUtils.isNoneBlank(((SMS) obj).getMessage().getDrFlag())) {
				boolean flag = false;
				if (((SMS) obj).getMessage().getDrFlag().equals("1")) {
					flag = true;
				}
				emg.setRequestDR(flag);
			}

			// Data coding
			String tmpLang = ((SMS) obj).getMessage().getLanguage();
			int dataCoding = SmsRequest.ASCII;
			if (StringUtils.isNoneBlank(tmpLang)) {
				if (tmpLang.toUpperCase().equals("C")) { // Unicode
//					dataCoding = SmsRequest.UCS2;
					dataCoding = SmsRequest.UTF;
				} else if (tmpLang.toUpperCase().equals("E")) { // English
					dataCoding = SmsRequest.ASCII;
				} else if (tmpLang.toUpperCase().equals("B")) { // Big5
//					dataCoding = SmsRequest.UCS2;
					dataCoding = SmsRequest.UTF;
				} else if (tmpLang.toUpperCase().equals("U")) { // UTF
//					dataCoding = SmsRequest.UCS2;
					dataCoding = SmsRequest.UTF;
				}
			}
			
			emg.setDataCoding(dataCoding);

			// OA
			emg.setSource(((SMS) obj).getMessage().getSource());

			// Text
			String coding = "utf8";
			String language = ((SMS) obj).getMessage().getLanguage();
			if ("B".equals(language)) {
				coding = "big5";
			} else if ("E".equals(language)) {
				coding = "ISO-8859-1";
			} else if ("C".equals(language)) { // unicode use UTF-16BE
				// disable by YC 20190716 because old spec was wrong
//				coding = "UTF-16BE";
			}
			emg.setMessage(HttpApiUtils.base64Decoded(((SMS) obj).getMessage().getText(), coding, "utf8"));

			// Validate period
			String tmpValidPeriod = ((SMS) obj).getMessage().getValidType();
			if (StringUtils.isNoneBlank(tmpValidPeriod)) {
				// TODO need to figure out 5 type of validate period in spec
				if (tmpValidPeriod.equals("0")) {
					emg.setValidity("0");
				}
			}

			// cp id
			emg.setCpId(((SMS) obj).getSysId());

			emg.setMessageType(ApiConstant.TYPE_SMS);
			// end of loop
			result.add(emg);
		}

		return result;
	}

	/**
	 * parse submit_resp from MultiSMS
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public String parseSMSResp(MessageObject msg) throws JAXBException {

		String result = "";

		SMSResp resp = null;

		if (msg != null) {

			resp = new SMSResp();

			if (StringUtils.isNoneBlank(msg.getStatus())) {
				resp.setResultCode(getAPIReturnCode(msg.getStatus()));
			}

			if (StringUtils.isNoneBlank(msg.getSmscMessageId())) {
				resp.setMessageId(msg.getSmscMessageId());
			}

			if (StringUtils.isNoneBlank(msg.getDeliveredTime())) {
				resp.setTimestamp(msg.getDeliveredTime());
			}

			result = xmlUtils.ObjectToXml(resp, SMSResp.class);

		}

		return result;
	}

	/**
	 * parse submit_resp from RetrieveDR
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public String parseRetrieveDRResp(MessageObject msg) throws JAXBException {

		String result = "";

		RetrieveDRResp resp = null;

		if (msg != null) {

			resp = new RetrieveDRResp();

			if (StringUtils.isNoneBlank(msg.getStatus())) {
				resp.setResultCode(msg.getStatus());
			}

			if (StringUtils.isNoneBlank(msg.getSmscMessageId())) {
				resp.setMessageId(msg.getSmscMessageId());
			}

			if (StringUtils.isNoneBlank(msg.getStatus())) {
				resp.setStatus(getAPIReturnCode(msg.getStatus()));
			}

			if (StringUtils.isNoneBlank(msg.getDestination())) {
				resp.setBNumber(msg.getDestination());
			}

			if (StringUtils.isNoneBlank(msg.getDeliveredTime())) {
				resp.setTimestamp(msg.getDeliveredTime());
			}

			DeliveryReport deliveryDetail = new DeliveryReport();

			if (StringUtils.isNoneBlank(String.valueOf(msg.getPid()))) {
				deliveryDetail.setId(String.valueOf(msg.getPid()));
			}

			if (StringUtils.isNoneBlank(msg.getDeliveredTime())) {
				deliveryDetail.setSubmitDate(msg.getDeliveredTime());
			}

			if (StringUtils.isNoneBlank(msg.getDoneTime())) {
				deliveryDetail.setDoneDate(msg.getDoneTime());
			}

			if (StringUtils.isNoneBlank(msg.getState())) {
				deliveryDetail.setState(getSMPPReturnCode(msg.getState()));
			}

			if (StringUtils.isNoneBlank(msg.getErrorCode())) {
				deliveryDetail.setError(msg.getErrorCode());
			}

			// resp.setDeliveryReport(deliveryDetail);
			resp.getDeliveryReport().add(deliveryDetail);

			result = xmlUtils.ObjectToXml(resp, RetrieveDRResp.class);

		}

		return result;
	}

	/**
	 * parse submit_resp from QueryDR
	 * 
	 * @param obj
	 * @return
	 */
	// public String parseQueryDRResp(MessageObject msg) {
	//
	// String result = "";
	//
	// QueryDRResp resp = null;
	//
	// if (msg != null) {
	//
	// resp = new QueryDRResp();
	//
	// if(StringUtils.isNoneBlank(msg.getStatus()))
	// {
	// resp.setResultCode(getAPIReturnCode(msg.getStatus()));
	// }
	//
	// //set Message segment
	// Message messageDetail = new Message();
	//
	// if(StringUtils.isNoneBlank(String.valueOf(msg.getPid())))
	// {
	// messageDetail.setMessageId(String.valueOf(msg.getPid()));
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getStatus()))
	// {
	// messageDetail.setStatus(getAPIReturnCode(msg.getStatus()));
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getDestination()))
	// {
	// messageDetail.setBNumber(msg.getDestination());
	// }
	//
	//
	// //set DeliveryReport segment
	// Message.DeliveryReport deliveryDetail = new Message.DeliveryReport();
	//
	// if(StringUtils.isNoneBlank(String.valueOf(msg.getPid())))
	// {
	// deliveryDetail.setId(String.valueOf(msg.getPid()));
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getDeliveredTime()))
	// {
	// deliveryDetail.setSubmitDate(msg.getDeliveredTime());
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getDoneTime()))
	// {
	// deliveryDetail.setDoneDate(msg.getDoneTime());
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getStatus()))
	// {
	// deliveryDetail.setState(getSMPPReturnCode(msg.getStatus()));
	// }
	//
	// if(StringUtils.isNoneBlank(msg.getErrorCode()))
	// {
	// deliveryDetail.setError(msg.getErrorCode());
	// }
	//
	// messageDetail.setDeliveryReport(deliveryDetail);
	//
	// resp.setMessage(messageDetail);
	//
	// result = XmlUtils.ObjectToXml(resp, QueryDRResp.class);
	//
	// }
	//
	// return result;
	// }

	/**
	 * parse submit_resp from PushDRResp
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public String parsePushDRResp(MessageObject msg) throws JAXBException {

		String result = "";

		PushDRResp resp = null;

		if (msg != null) {

			resp = new PushDRResp();

			if (StringUtils.isNoneBlank(msg.getStatus())) {
				resp.setResultCode(msg.getStatus());
			}

			result = xmlUtils.ObjectToXml(resp, PushDRResp.class);

		}

		return result;
	}

	/**
	 * parse submit_resp from DeliverSM
	 * 
	 * @param obj
	 * @return
	 * @throws JAXBException
	 */
	public String parseDeliverSMResp(MessageObject msg) throws JAXBException {

		String result = "";

		DeliverSMResp resp = null;

		if (msg != null) {

			resp = new DeliverSMResp();

			if (StringUtils.isNoneBlank(msg.getStatus())) {
				resp.setResultCode(getAPIReturnCode(msg.getStatus()));
			}

			result = xmlUtils.ObjectToXml(resp, DeliverSMResp.class);

		}

		return result;
	}

	// public String getResponse(String requestType, MessageObject msg) {
	// //TODO
	// String result = "";
	//
	// switch (requestType) {
	// case "SMSresp":
	// result = parseSMSResp(msg);
	// break;
	// case "PushDRResp":
	// result = parsePushDRResp(msg);
	// break;
	// case "QueryDRResp":
	// result = parseQueryDRResp(msg);
	// break;
	// case "RetrieveDRResp":
	// result = parseRetrieveDRResp(msg);
	// break;
	// case "DeliverSMResp":
	// result = parseDeliverSMResp(msg);
	// break;
	//
	// }
	//
	// return result;
	// }

	private String getAPIReturnCode(String status) {

		String result;
		// TODO match Smsc response code to complete

		switch (status) {
		case "0201":
		case "0501":
		case "0502":
		case "1101":
			result = ApiConstant.RC_SUCCESS;
			break;
		case "0202":
		case "1000":
		case "1002":
		case "1011":
		case "1018":
		case "1102":
		case "1104":
		case "1199":
			result = ApiConstant.RC_UNKNOW_ERROR;
			break;
		case "1008":
			result = ApiConstant.RC_INVALID_DA;
			break;
		case "1016":
		case "1017":
			result = ApiConstant.RC_INVALID_OA;
			break;
		case "1005":
			result = ApiConstant.RC_INVALID_LANG;
			break;
		case "1003":
			result = ApiConstant.RC_TEXT_TOO_LONG;
			break;
		case "1009":
			result = ApiConstant.RC_SPAM_TEXT;
			break;
		case "1014":
			result = ApiConstant.RC_THROUGHTPUT_EXCEED;
			break;
		case "1015":
			result = ApiConstant.RC_INVALID_TIME;
			break;
		// case 11101:
		// result = ApiConstant.RC_PARAM_EMPTY;
		// break;
		// case 11102:
		// result = ApiConstant.RC_MISS_SYSID;
		// break;
		// case 11103:
		// result = ApiConstant.RC_INVALID_SYSID;
		// break;
		// case 11104:
		// result = ApiConstant.RC_INVALID_XML;
		// break;
		// case 11108:
		// result = ApiConstant.RC_INVALID_DR;
		// break;
		// case 11109:
		// result = ApiConstant.RC_INVALID_VALIDTYPE;
		// break;
		// case 11113:
		// result = ApiConstant.RC_INVALID_B_PART;
		// break;
		// case 11115:
		// result = ApiConstant.RC_UNSUPPORT_PARAM;
		// break;
		// case 11116:
		// result = ApiConstant.RC_INVALID_MSG_ID;
		// break;
		// case 11999:
		// result = ApiConstant.RC_SERVICE_UNAVAILABLE;
		// break;

		default:
			result = ApiConstant.RC_UNKNOW_ERROR;
		}

		return result;
	}

	/*
	 * private String getESMEReturnCode(int status) {
	 * 
	 * String result; // TODO match Smsc response code to complete
	 * 
	 * switch (status) { case 0: result = EsmeConstant.RC_ESME_ROK; break; case 1:
	 * result = EsmeConstant.RC_ESME_RINVMSGLEN; break; case 2: result =
	 * EsmeConstant.RC_ESME_RINVCMDLEN; break; case 3: result =
	 * EsmeConstant.RC_ESME_RINVCMDID; break; case 4: result =
	 * EsmeConstant.RC_ESME_RINVBNDSTS; break; case 5: result =
	 * EsmeConstant.RC_ESME_RALYBND; break; case 6: result =
	 * EsmeConstant.RC_ESME_RINVPRTFLG; break; case 7: result =
	 * EsmeConstant.RC_ESME_RINVREGDLVFLG; break; case 8: result =
	 * EsmeConstant.RC_ESME_RSYSERR; break; case 10: result =
	 * EsmeConstant.RC_ESME_RINVSRCADR; break; case 11: result =
	 * EsmeConstant.RC_ESME_RINVDTSADR; break; case 12: result =
	 * EsmeConstant.RC_ESME_RINVMSGID; break; case 13: result =
	 * EsmeConstant.RC_ESME_RBINDFAIL; break; case 14: result =
	 * EsmeConstant.RC_ESME_RINVPASWD; break; case 15: result =
	 * EsmeConstant.RC_ESME_RINVSYSID; break; case 16: result =
	 * EsmeConstant.RC_ESME_RESERVED_2; break; case 17: result =
	 * EsmeConstant.RC_ESME_RCANCELFAIL; break; case 18: result =
	 * EsmeConstant.RC_ESME_RESERVED_3; break; case 19: result =
	 * EsmeConstant.RC_ESME_RREPLACEFAIL; break; case 20: result =
	 * EsmeConstant.RC_ESME_RMSGQFUL; break; case 21: result =
	 * EsmeConstant.RC_ESME_RINVSERVTYP; break; case 51: result =
	 * EsmeConstant.RC_ESME_RINVNUMDESTS; break; case 52: result =
	 * EsmeConstant.RC_ESME_RINVDLNAME; break; case 64: result =
	 * EsmeConstant.RC_ESME_RINVDESTFLAG; break; case 66: result =
	 * EsmeConstant.RC_ESME_RINVSUBREP; break; case 67: result =
	 * EsmeConstant.RC_ESME_RINVESMCLASS; break; case 68: result =
	 * EsmeConstant.RC_ESME_RCNTSUBDL; break; case 69: result =
	 * EsmeConstant.RC_ESME_RSUBMITFAIL; break; case 72: result =
	 * EsmeConstant.RC_ESME_RINVSRCTON; break; case 73: result =
	 * EsmeConstant.RC_ESME_RINVSRCNPI; break; case 80: result =
	 * EsmeConstant.RC_ESME_RINVDSTTON; break; case 81: result =
	 * EsmeConstant.RC_ESME_RINVDSTNPI; break; case 83: result =
	 * EsmeConstant.RC_ESME_RINVSYSTYP; break; case 84: result =
	 * EsmeConstant.RC_ESME_RINVREPFLAG; break; case 85: result =
	 * EsmeConstant.RC_ESME_RINVNUMMSGS; break; case 88: result =
	 * EsmeConstant.RC_ESME_RTHROTTLED; break; case 97: result =
	 * EsmeConstant.RC_ESME_RINVSCHED; break; case 98: result =
	 * EsmeConstant.RC_ESME_RINVEXPIRY; break; case 99: result =
	 * EsmeConstant.RC_ESME_RINVDFTMSGID; break; case 100: result =
	 * EsmeConstant.RC_ESME_RX_T_APPN; break; case 101: result =
	 * EsmeConstant.RC_ESME_RX_P_APPN; break; case 102: result =
	 * EsmeConstant.RC_ESME_RX_R_APPN; break; case 103: result =
	 * EsmeConstant.RC_ESME_RQUERYFAIL; break; case 192: result =
	 * EsmeConstant.RC_ESME_RINVOPTPARSTREAM; break; case 193: result =
	 * EsmeConstant.RC_ESME_RINVOPTPARNOTALLWD; break; case 194: result =
	 * EsmeConstant.RC_ESME_RINVPARLEN; break; case 195: result =
	 * EsmeConstant.RC_ESME_RMISSINGOPTPARAM; break; case 196: result =
	 * EsmeConstant.RC_ESME_RINVOPTPARAMVAL; break; case 254: result =
	 * EsmeConstant.RC_ESME_RDELIVERYFAILURE; break; case 255: result =
	 * EsmeConstant.RC_ESME_RUNKNOWNERR; break;
	 * 
	 * default: result = EsmeConstant.RC_ESME_ROK; }
	 * 
	 * return result; }
	 */

	/*
	 * private String getSmscReturnCode(int status) {
	 * 
	 * String result; // TODO match Smsc response code to complete
	 * 
	 * switch (status) { case 201: result = OmgConstant.RC_SENT_TO_SMSC; break; case
	 * 202: result = OmgConstant.RC_FAILED_TO_SEND; break; case 501: result =
	 * OmgConstant.RC_RECOGNIZED_SPAM; break; case 502: result =
	 * OmgConstant.RC_SPAM_APPROVED; break; case 1000: result =
	 * OmgConstant.RC_INVALID_XML_FORMAT; break; case 1002: result =
	 * OmgConstant.RC_INVALID_SOURCE_ADDR; break; case 1003: result =
	 * OmgConstant.RC_MESSAGE_TOO_LONG; break; case 1005: result =
	 * OmgConstant.RC_LANG_NOT_SUPPORT; break; case 1008: result =
	 * OmgConstant.RC_INVALID_TARGET_ADDR; break; case 1009: result =
	 * OmgConstant.RC_SPAM_REJECTED; break; case 1011: result =
	 * OmgConstant.RC_SOURCE_ADDR_BLACK; break; case 1014: result =
	 * OmgConstant.RC_THROUGHPUT_LIMIT; break; case 1015: result =
	 * OmgConstant.RC_TIME_NOT_ALLOWED; break; case 1016: result =
	 * OmgConstant.RC_SOURCE_ADDR_BLOCK_PROMOTION; break; case 1017: result =
	 * OmgConstant.RC_SOURCE_ADDR_OFF_NET; break; case 1018: result =
	 * OmgConstant.RC_MNP_NOT_AVAILABLE; break; case 1101: result =
	 * OmgConstant.RC_MESSAGE_DELIVERED; break; case 1102: result =
	 * OmgConstant.RC_MESSAGE_EXPIRED; break; case 1104: result =
	 * OmgConstant.RC_MESSAGE_DELIVER_FAIL; break; case 1199: result =
	 * OmgConstant.RC_UNKNOWN; break;
	 * 
	 * default: result = OmgConstant.RC_SENT_TO_SMSC; }
	 * 
	 * return result; }
	 */

	private String getSMPPReturnCode(String status) {

		String result;
		// TODO match OMG response code to complete

		switch (status) {
		case "0201":
		case "0502":
			result = SmppConstant.RC_ENROUTE;
			break;

		case "0501":
		case "1101":
			result = SmppConstant.RC_DELIVRD;
			break;
		case "1102":
			result = SmppConstant.RC_EXPIRED;
			break;
		case "1008":
		case "1104":
			result = SmppConstant.RC_UNDELIV;
			break;
		case "1199":
			result = SmppConstant.RC_UNKNOWN;
			break;
		case "1009":
			result = SmppConstant.RC_REJECTD;
			break;

		default:
			result = SmppConstant.RC_UNKNOWN;
		}

		return result;
	}

	public SmsRequest messageObjectToSmsRequest(MessageObject request) {

		SmsRequest result = null;

		if (request != null) {
			result = new SmsRequest();
			try {
				result.setDataCoding(request.getDataCoding());
			} catch (Exception e) {
				// ignore
				log.debug("[setDataCoding]", e.getMessage());
			}
			try {
				result.setDestination(request.getDestination());
			} catch (Exception e) {
				// ignore
				log.debug("[setDestination]", e.getMessage());
			}
			try {
				result.setDestinationNPI(request.getDestinationNPI());
			} catch (Exception e) {
				// ignore
				log.debug("[setDestinationNPI]", e.getMessage());
			}
			try {
				result.setDestinationTON(request.getDestinationTON());
			} catch (Exception e) {
				// ignore
				log.debug("[setDestinationTON]", e.getMessage());
			}
			try {
				result.setEsmClass(request.getEsmClass());
			} catch (Exception e) {
				// ignore
				log.debug("[setEsmClass]", e.getMessage());
			}
			try {
				result.setMessage(request.getMessage());
			} catch (Exception e) {
				// ignore
				log.debug("[setMessage]", e.getMessage());
			}
			try {
				result.setPid(request.getPid());
			} catch (Exception e) {
				// ignore
				log.debug("[setPid]", e.getMessage());
			}
			try {
				result.setRequestDR(request.isRequestDR());
				// result.setRequestDR(true);
			} catch (Exception e) {
				// ignore
				log.debug("[setRequestDR]", e.getMessage());
			}
			try {
				result.setServiceType(request.getServiceType());
			} catch (Exception e) {
				// ignore
				log.debug("[setServiceType]", e.getMessage());
			}
			try {
				result.setSmsSeq(request.getSeq());
			} catch (Exception e) {
				// ignore
				log.debug("[setSmsSeq]", e.getMessage());
			}
			try {
				result.setSource(request.getSource());
			} catch (Exception e) {
				// ignore
				log.debug("[setSource]", e.getMessage());
			}
			try {
				result.setSourceNPI(request.getSourceNPI());
			} catch (Exception e) {
				// ignore
				log.debug("[setSourceNPI]", e.getMessage());
			}
			try {
				result.setSourceTON(request.getSourceTON());
			} catch (Exception e) {
				// ignore
				log.debug("[setSourceTON]", e.getMessage());
			}

		}
		return result;
	}

	// XXX 2017-11-30 add by matthew
	public List<SmsRequest> messageObjectToSmsRequestList(MessageObject request) {
		List<SmsRequest> list = new ArrayList<>();
		if (request != null && request.getDa() != null && request.getDa().size() > 0) {

			for (String da : request.getDa()) {
				SmsRequest result = new SmsRequest();
				try {
					result.setDataCoding(request.getDataCoding());
				} catch (Exception e) {
					// ignore
					log.debug("[setDataCoding]", e.getMessage());
				}
				try {
					result.setDestination(da);
				} catch (Exception e) {
					// ignore
					log.debug("[setDestination]", e.getMessage());
				}
				try {
					result.setDestinationNPI(request.getDestinationNPI());
				} catch (Exception e) {
					// ignore
					log.debug("[setDestinationNPI]", e.getMessage());
				}
				try {
					result.setDestinationTON(request.getDestinationTON());
				} catch (Exception e) {
					// ignore
					log.debug("[setDestinationTON]", e.getMessage());
				}
				try {
					result.setEsmClass(request.getEsmClass());
				} catch (Exception e) {
					// ignore
					log.debug("[setEsmClass]", e.getMessage());
				}
				try {
					// result.setMessage(request.getMessage());
					result.setMessage(CommonFileUtil.removeLinefeedInTheEnd(request.getMessage()));
				} catch (Exception e) {
					// ignore
					log.debug("[setMessage]", e.getMessage());
				}
				try {
					result.setPid(request.getPid());
				} catch (Exception e) {
					// ignore
					log.debug("[setPid]", e.getMessage());
				}
				try {
					result.setRequestDR(request.isRequestDR());
					// result.setRequestDR(true);
				} catch (Exception e) {
					// ignore
					log.debug("[setRequestDR]", e.getMessage());
				}
				try {
					result.setServiceType(request.getServiceType());
				} catch (Exception e) {
					// ignore
					log.debug("[setServiceType]", e.getMessage());
				}
				try {
					result.setSmsSeq(request.getSeq());
				} catch (Exception e) {
					// ignore
					log.debug("[setSmsSeq]", e.getMessage());
				}
				try {
					result.setSource(request.getSource());
				} catch (Exception e) {
					// ignore
					log.debug("[setSource]", e.getMessage());
				}
				try {
					result.setSourceNPI(request.getSourceNPI());
				} catch (Exception e) {
					// ignore
					log.debug("[setSourceNPI]", e.getMessage());
				}
				try {
					result.setSourceTON(request.getSourceTON());
				} catch (Exception e) {
					// ignore
					log.debug("[setSourceTON]", e.getMessage());
				}
				list.add(result);
			}
		}
		return list;
	}

	public MessageObject parseSMSResp(SMPPResponse request) {
		return parseSMSResp(request, null);
	}

	public MessageObject parseSMSResp(SMPPResponse request, MessageObject result) {

		if (result == null) {
			result = new MessageObject();
		}

		if (request != null) {
			result.setSmscMessageId(request.getMessageId());
			result.setErrorCode(String.valueOf(request.getErrorCode()));
			result.setStatus(String.valueOf(request.getCommandStatus()));
			result.setSeq(request.getSequenceNum());
		}

		return result;
	}

	public MessageObject parseDeliverSM(ie.omk.smpp.message.DeliverSM request) {
		return parseDeliverSM(request, null);
	}

	public MessageObject parseDeliverSM(ie.omk.smpp.message.DeliverSM request, MessageObject result) {

		if (result == null) {
			result = new MessageObject();
		}

		if (request != null) {
//			String msg = new String(request.getMessage());
			
			String msg = request.getMessageText();

			result.setMessage(msg);
			result.setSmscMessageId(getDrValue(msg, "id"));
			result.setDoneTime(getDrValue(msg, "done date"));
			result.setState(getDrValue(msg, "stat"));
			result.setErrorCode(getDrValue(msg, "err"));

			try {
				result.setEsmClass(request.getEsmClass());
			} catch (Exception e) {
				log.warn("Set EsmClass got {}, use default value 0", e.getMessage());
				result.setEsmClass(0);
			}
			try {
				result.setDataCoding(request.getDataCoding());
			} catch (Exception e) {
				log.warn("Set DataCoding got {}, use default value 0", e.getMessage());
				result.setDataCoding(0);
			}
			try {
				result.setSeq(request.getSequenceNum());
			} catch (Exception e) {
				log.warn("Set SequenceNum got {}", e.getMessage());
			}
			try {
				result.setDestination(request.getSource().getAddress());
			} catch (Exception e) {
				log.warn("Set Destination got {}", e.getMessage());
			}
			try {
				result.setDestinationTON(request.getSource().getTON());
			} catch (Exception e) {
				log.warn("Set DestinationTON got {}, use default value 0", e.getMessage());
				result.setDestinationTON(0);
			}
			try {
				result.setDestinationNPI(request.getSource().getNPI());
			} catch (Exception e) {
				log.warn("Set DestinationNPI got {}, use default value 0", e.getMessage());
				result.setDestinationNPI(0);
			}
			try {
				result.setSource(request.getDestination().getAddress());
			} catch (Exception e) {
				log.warn("Set Source got {}", e.getMessage());
			}
			try {
				result.setSourceTON(request.getDestination().getTON());
			} catch (Exception e) {
				log.warn("Set SourceTON got {}, use default value 0", e.getMessage());
				result.setSourceTON(0);
			}
			try {
				result.setSourceNPI(request.getDestination().getNPI());
			} catch (Exception e) {
				log.warn("Set SourceNPI got {}, use default value 0", e.getMessage());
				result.setSourceNPI(0);
			}
			try {
				result.setStatus(String.valueOf(request.getMessageStatus()));
			} catch (Exception e) {
				log.warn("Set Status got {}", e.getMessage());
			}
		}
		log.debug("[ParseDR] {}", result.toString());

		return result;
	}

	/**
	 * get values from short message in deliver SM.
	 * 
	 * @param shortMessage
	 * @param key
	 * @return
	 */
	public String getDrValue(String shortMessage, String key) {

		String result = "";
		String mKey = key + ":";

		if (StringUtils.isNotBlank(shortMessage)) {
			if (shortMessage.contains(mKey)) {
				int start = shortMessage.indexOf(mKey);
				shortMessage = shortMessage.substring(start, shortMessage.length() - 1);
				shortMessage = shortMessage.replace(mKey, "");
				result = shortMessage.substring(0, shortMessage.indexOf(" "));

				log.debug("[DR Shortmessage] Get [{}] response [{}]", key, result);
			}
		} else {
			log.debug("[DR Shortmessage] shortMessage is empty");
		}

		return result;
	}

	public MessageObject parseMO(ie.omk.smpp.message.DeliverSM request) {
		return parseMO(request, null);
	}

	public MessageObject parseMO(ie.omk.smpp.message.DeliverSM request, MessageObject result) {

		if (result == null) {
			result = new MessageObject();
		}

		if (request != null) {
//			String msg = new String(request.getMessage());
			
			String msg = request.getMessageText();

			result.setMessage(msg);

			try {
				result.setEsmClass(request.getEsmClass());
			} catch (Exception e) {
				log.warn("Set EsmClass got {}, use default value 0", e.getMessage());
				result.setEsmClass(0);
			}
			try {
				result.setDataCoding(request.getDataCoding());
			} catch (Exception e) {
				log.warn("Set DataCoding got {}, use default value 0", e.getMessage());
				result.setDataCoding(0);
			}
			try {
				result.setSeq(request.getSequenceNum());
			} catch (Exception e) {
				log.warn("Set SequenceNum got {}", e.getMessage());
			}
			try {
				result.setDestination(request.getDestination().getAddress());
			} catch (Exception e) {
				log.warn("Set Destination got {}", e.getMessage());
			}
			try {
				result.setDestinationTON(request.getDestination().getTON());
			} catch (Exception e) {
				log.warn("Set DestinationTON got {}, use default value 0", e.getMessage());
				result.setDestinationTON(0);
			}
			try {
				result.setDestinationNPI(request.getDestination().getNPI());
			} catch (Exception e) {
				log.warn("Set DestinationNPI got {}, use default value 0", e.getMessage());
				result.setDestinationNPI(0);
			}
			try {
				result.setSource(request.getSource().getAddress());
			} catch (Exception e) {
				log.warn("Set Source got {}", e.getMessage());
			}
			try {
				result.setSourceTON(request.getSource().getTON());
			} catch (Exception e) {
				log.warn("Set SourceTON got {}, use default value 0", e.getMessage());
				result.setSourceTON(0);
			}
			try {
				result.setSourceNPI(request.getSource().getNPI());
			} catch (Exception e) {
				log.warn("Set SourceNPI got {}, use default value 0", e.getMessage());
				result.setSourceNPI(0);
			}
			try {
				result.setErrorCode(String.valueOf(request.getErrorCode()));
			} catch (Exception e) {
				log.warn("Set ErrorCode got {}", e.getMessage());
			}
			try {
				result.setStatus(String.valueOf(request.getMessageStatus()));
			} catch (Exception e) {
				log.warn("Set Status got {}", e.getMessage());
			}
		}

		return result;
	}

	public SubmitSM smsRequsetToSubmitSM(SmsRequest request) {

		SubmitSM result = null;

		if (request != null) {
			result = new SubmitSM();

			try {

			} catch (Exception e) {
				log.warn("set  failed!!!");
			}

			result.setMessage(request.getBinaryMessage());
			result.setDataCoding(request.getDataCoding());

			Address destination = new Address();
			destination.setAddress(request.getDestination());
			destination.setNPI(request.getDestinationNPI());
			destination.setTON(request.getDestinationTON());
			result.setDestination(destination);

			result.setEsmClass(request.getEsmClass());
			result.setProtocolID(request.getPid());
			result.setServiceType(request.getServiceType());
			result.setSequenceNum(request.getSmsSeq());

			Address source = new Address();
			source.setAddress(request.getSource());
			source.setNPI(request.getSourceNPI());
			source.setTON(request.getSourceTON());
			result.setSource(source);

			/*
			String validaty = request.getValidity();
			if (StringUtils.isNoneBlank(validaty)) {
				Calendar cal = Calendar.getInstance();
				if ("0".equals(validaty)) {
					cal.add(Calendar.MINUTE, 1);
				} else if ("1".equals(validaty)) {
					cal.add(Calendar.MINUTE, 30);
				} else if ("2".equals(validaty)) {
					cal.add(Calendar.MINUTE, 3 * 60);
				} else if ("3".equals(validaty)) {
					cal.add(Calendar.MINUTE, 8 * 60);
				} else if ("4".equals(validaty)) {
					cal.add(Calendar.MINUTE, 24 * 60);
				}
				result.setExpiryTime(cal.getTime());
			}
			*/
			result.setExpiryTime(getExpireyTime(request.getValidity()));

		}

		return result;
	}

	/**
	 * Get expired time for type
	 * <br/>
	 * type = 0 : 1 mins
	 * <br/>
	 * type = 1 : 30 mins
	 * <br/>
	 * type = 2 : 3 hrs
	 * <br/>
	 * type = 3 : 8 hrs
	 * <br/>
	 * type = 4 or others : 24 hrs
	 * <br/>
	 * @param expiredType
	 * @return Date after that time
	 */
	public Date getExpireyTime(String expiredType) {
		
		Date result = null;

		if (StringUtils.isNoneBlank(expiredType)) {
			if ("0".equals(expiredType)) {
				result = getDelayDate(1);
			} else if ("1".equals(expiredType)) {
				result = getDelayDate(30);
			} else if ("2".equals(expiredType)) {
				result = getDelayDate(3 * 60);
			} else if ("3".equals(expiredType)) {
				result = getDelayDate(8 * 60);
			} 
		}
		
		if(result == null) {
			result  = getDelayDate(24 * 60);
		}
		return result;
	}
	
	public Date getDelayDate(int validMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, validMinutes);
		return cal.getTime();
	}
}
