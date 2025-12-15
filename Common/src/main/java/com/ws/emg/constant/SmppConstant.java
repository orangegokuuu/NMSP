package com.ws.emg.constant;

public class SmppConstant {

	// Message is in Enroute state
	public static final String RC_ENROUTE = "ENROUTE"; //0201
	// Message is delivered to destination
	public static final String RC_DELIVRD = "DELIVRD"; //1101
	// Message validity period has expired 
	public static final String RC_EXPIRED = "EXPIRED"; //1102
	// Message has been deleted (In the case of SMSC returned DR)
	public static final String RC_DELETED = "DELETED"; //1104
	// Message is undeliverable
	public static final String RC_UNDELIV = "UNDELIV"; //1104
	// Message is in accepted state (In the case of SMPP MT-SMS)
	public static final String RC_ACCEPTD = "ACCEPTD"; //0201
	// Message is in invalid state
	public static final String RC_UNKNOWN = "UNKNOWN"; //1199
	// Message is in a rejected state
	public static final String RC_REJECTD = "REJECTD"; //0202

	
	public static final int POSITIONAL_NOTATION_HEX = 16;
	public static final int POSITIONAL_NOTATION_DECIMAL = 10;
	
	/*
	0202 The message has failed to send to MAP/SMPP/SMSC (發送失敗)
	0501 The message has been recognized as a suspect spam message. (發送中)
	0502 The suspect spam message is approved (發送中)
	1000 Invalid XML format (資料錯誤)
	1002 Invalid Source Address (資料錯誤)
	1003 Short Message Length Too Long (資料錯誤)
	1005 Language Not Supported (資料錯誤)
	1008 Invalid Target Address (資料錯誤)
	1009 Spam Message Rejected (the suspect spam message is rejected) (拒絕發送)
	1011 Destination AddressIn BlackList  (拒絕發送)
	1014 Throughput exceed limit  (拒絕發送)
	1015 Time Not Allowed  (拒絕發送)
	1016 Source Address In Block Promotion List  (拒絕發送)
	1017 Source Address is Off-Net Subscriber (拒絕發送)
	1018 MNP Not available (拒絕發送)
	1019 Invalid SYSID (資料錯誤)
	*/
}
