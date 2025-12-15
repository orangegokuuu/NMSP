package com.ws.emg.constant;

public class OmgConstant {

	// The message has been sent to SMSC
	public static final String RC_SENT_TO_SMSC = "0201";	
	// The message has failed to send to MAP/SMPP/SMSC
	public static final String RC_FAILED_TO_SEND = "0202";	
	// The message has been recognized as a suspect spam message.
	public static final String RC_RECOGNIZED_SPAM = "0501";	
	// The suspect spam message is approved 
	public static final String RC_SPAM_APPROVED = "0502";	
	// Invalid XML format 
	public static final String RC_INVALID_XML_FORMAT = "1000";	
	// Invalid Source Address
	public static final String RC_INVALID_SOURCE_ADDR = "1002";
	// Short Message Length Too Long
	public static final String RC_MESSAGE_TOO_LONG = "1003";	
	// Language Not Supported
	public static final String RC_LANG_NOT_SUPPORT = "1005";	
	// Invalid Target Address
	public static final String RC_INVALID_TARGET_ADDR = "1008";	
	// Spam Message Rejected (the suspect spam message is rejected) 
	public static final String RC_SPAM_REJECTED = "1009";	
	// Source Address In BlackList
	public static final String RC_SOURCE_ADDR_BLACK = "1011";	
	// Throughput exceed limit
	public static final String RC_THROUGHPUT_LIMIT = "1014";
	// Time Not Allowed
	public static final String RC_TIME_NOT_ALLOWED = "1015";	
	// Source Address In Block Promotion List
	public static final String RC_SOURCE_ADDR_BLOCK_PROMOTION = "1016";	
	// Source Address is Off-Net Subscriber 
	public static final String RC_SOURCE_ADDR_OFF_NET = "1017";	
	// MNP Not available
	public static final String RC_MNP_NOT_AVAILABLE = "1018";	
	// (DR) The message has been delivered to handset
	public static final String RC_MESSAGE_DELIVERED = "1101";	
	// (DR) The message has expired
	public static final String RC_MESSAGE_EXPIRED = "1102";
	// (DR) The message has failed to deliver to handset
	public static final String RC_MESSAGE_DELIVER_FAIL = "1104";	
	// (DR) Unknown
	public static final String RC_UNKNOWN = "1199";	
	
}
