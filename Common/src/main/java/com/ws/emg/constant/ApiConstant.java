package com.ws.emg.constant;

public class ApiConstant {
	
	public static final String KEY_HTTPAPI_ENABLE = "HTTPAPI_ENABLE";
	public static final String KEY_THROUGH_PUT = "THROUGH_PUT";

	// Request successfully processed.
	public static final String RC_SUCCESS = "00000";
	// "xmlData" parameter is empty.
	public static final String RC_PARAM_EMPTY = "11101";
	// Missing SysId
	public static final String RC_MISS_SYSID = "11102";
	// Invalid SysId
	public static final String RC_INVALID_SYSID = "11103";
	// XML cannot pass format check
	public static final String RC_INVALID_XML = "11104";
	// Target is incorrect (i.e. target field is empty)
	public static final String RC_INVALID_DA = "11105";
	// Source is incorrect (i.e. source field is empty)
	public static final String RC_INVALID_OA = "11106";
	// Language is incorrect
	public static final String RC_INVALID_LANG = "11107";
	// DrFlag is incorrect
	public static final String RC_INVALID_DR = "11108";
	// ValidType is incorrect
	public static final String RC_INVALID_VALIDTYPE = "11109";
	// Text is too long
	public static final String RC_TEXT_TOO_LONG = "11110";
	// No Type
	public static final String RC_INVALID_TYPE = "11112";
	// TODO (strange)B number is incorrect
	public static final String RC_INVALID_B_PART = "11113";
	// Text is rejected because it contains spam keywords
	public static final String RC_SPAM_TEXT = "11114";
	// Unsupported parameter
	public static final String RC_UNSUPPORT_PARAM = "11115";
	// Invalid MessageID
	public static final String RC_INVALID_MSG_ID = "11116";
	// Throughput exceed limit
	public static final String RC_THROUGHTPUT_EXCEED = "11117";
	// Time Not Allowed
	public static final String RC_INVALID_TIME = "11118";
	// Unknown Error (Report this error to FET Contact Window for help)
	public static final String RC_UNKNOW_ERROR = "11998";
	// Service unavailable.
	public static final String RC_SERVICE_UNAVAILABLE = "11999";
	// DB error.
	public static final String RC_DB_ERROR = "11997";
	
	
	/**
	 * HTTP API type
	 */
	public static final String TYPE_DELIVERSM = "DeliverSM";
	public static final String TYPE_SELIVERSMRESP = "DeliverSMResp";
	public static final String TYPE_PUSHDR = "PushDR";
	public static final String TYPE_PUSHDRRESP = "PushDRResp";
	public static final String TYPE_QUERYDR = "QueryDR";
	public static final String TYPE_QUERYDRRESP = "QueryDRResp";
	public static final String TYPE_RETRIEVEDR = "RetrieveDR";
	public static final String TYPE_RETRIEVEDRResp = "RetrieveDRResp";
	public static final String TYPE_SMS = "SMS";
	public static final String TYPE_SMSRESP = "SMSResp"; 
	public static final String TYPE_OBJECTFACTORY = "ObjectFactory";
	
	/**
	 *  HTTP API Status Code 
	 */
	//Success 
	public static final String SC_SUCCESS = "0000";
	//The message has been sent to SMSC
	public static final String SC_MSG_SEND_SMSC = "0201";
	//The message has failed to send to MAP/SMPP/SMSC
	public static final String SC_FAILED_MSG_SEND_SMSC = "0202";
	//The message has been recognized as a suspect spam message. 
	public static final String SC_SPAM_MSG = "0501";
	//The suspect spam message is approved
	public static final String SC_SPAM_MSG_APPROVED = "0502";
	//Invalid XML format
	public static final String SC_INVALID_XML = "1000";
	//Invalid Source Address
	public static final String SC_INVALID_SOURCE_ADDR = "1002";
	//Short Message Length Too Long
	public static final String SC_MSG_TOO_LONG = "1003";
	//Language Not Supported
	public static final String SC_INVALID_LANG = "1005";
	//Invalid Target Address
	public static final String SC_INVALID_TARGET = "1008";
	//Spam Message Rejected
	public static final String SC_SPAM_MSG_REJ= "1009";
	//Destination Address In BlackList
	public static final String SC_DA_IN_BL = "1011";
	//Throughput exceed limit
	public static final String SC_THROUGHPUT_EXCEED_LIMIT = "1014";
	//Time Not Allowed
	public static final String SC_TIME_NOT_ALLOWED = "1015";
	//Destination Address In Block Promotion List
	public static final String SC_DA_IN_BLOCK_LIST = "1016";
	//Destination Address is Off-Net Subscriber
	public static final String SC_DA_IS_OFF_NET = "1017";
	//MNP Not available
	public static final String SC_MNP_NOT_AVAILABLE = "1018";
	//Invalid SYSID
	public static final String SC_INVALID_SYSID = "1019";
	//The message has been delivered to handset
	public static final String SC_MSG_DELIVER = "1101";
	//The message has expired
	public static final String SC_MSG_EXPIRED = "1102";
	//The message has failed to deliver to handset 
	public static final String SC_MSG_FAILED_DELIVER = "1104";
	//Unknown
	public static final String SC_UNKNOWN = "1199";
	
	
	public static String getStatusCode(int resultCode){
		String result = "";
		switch (resultCode) {
		case 11101:
			result = SC_INVALID_XML;
			break;
		case 11102:
			result = SC_INVALID_SYSID;
			break;
		case 11103:
			result = SC_INVALID_SYSID;
			break;
		case 11104:
			result = SC_INVALID_XML;
			break;
		case 11105:
			result = SC_INVALID_TARGET;
			break;
		case 11106:
			result = SC_INVALID_SOURCE_ADDR;
			break;
		case 11107:
			result = SC_INVALID_LANG;
			break;
		case 11108:
			result = SC_INVALID_XML;
			break;
		case 11109:
			result = SC_INVALID_XML;
			break;
		case 11110:
			result = SC_MSG_TOO_LONG;
			break;
		case 11113:
			result = SC_INVALID_TARGET;
			break;
		case 11114:
			result = SC_SPAM_MSG;
			break;
		case 11116:
			result = SC_INVALID_XML;
			break;
		case 11115:
			result = SC_INVALID_XML;
			break;
		case 11117:
			result = SC_THROUGHPUT_EXCEED_LIMIT;
			break;
		case 11118:
			result = SC_TIME_NOT_ALLOWED;
			break;
		case 11998:
			result = SC_UNKNOWN;
			break;
		case 11999:
			result = SC_UNKNOWN;
			break;
		default:
			result = SC_UNKNOWN;
		}

		return result;
	}
	
	public static String httpApiStatusMapping(String smppStatus){
		String result = SC_MSG_SEND_SMSC;
		if(smppStatus!=null && !smppStatus.equals("")){
			if(smppStatus.equals(SmppConstant.RC_ACCEPTD))      result = SC_MSG_SEND_SMSC;
			else if(smppStatus.equals(SmppConstant.RC_DELETED)) result = SC_MSG_FAILED_DELIVER;
			else if(smppStatus.equals(SmppConstant.RC_DELIVRD)) result = SC_MSG_DELIVER;
			else if(smppStatus.equals(SmppConstant.RC_ENROUTE)) result = SC_MSG_SEND_SMSC;
			else if(smppStatus.equals(SmppConstant.RC_EXPIRED)) result = SC_MSG_EXPIRED;
			else if(smppStatus.equals(SmppConstant.RC_REJECTD)) result = SC_FAILED_MSG_SEND_SMSC;
			else if(smppStatus.equals(SmppConstant.RC_UNDELIV)) result = SC_MSG_FAILED_DELIVER;
			else if(smppStatus.equals(SmppConstant.RC_UNKNOWN)) result = SC_UNKNOWN;
			else  result = SC_UNKNOWN;
		}
		return result;
	}
}
