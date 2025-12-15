package com.ws.emg.pojo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ws.smpp.SmsRequest;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageObject extends SmsRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -73899253104571275L;

	/**
	 * Define this message type e.g. submit, dr, etc...
	 */
	private String messageType;

	/**
	 * MSP message id
	 */
	private String wsMessageId;

	/**
	 * OMG message id
	 */
	private String smscMessageId;

	/**
	 * Long message seq number
	 */
	private int seq;

	/**
	 * CP id
	 */
	private String cpId;

	/**
	 * CP password
	 */
	private String cpPwd;
	/**
	 * response of SmsRetrieveDR and SmsQueryDR and request of PushDR
	 */
	private String status;
	/**
	 * The time request
	 */
	private String submitTime;
	/**
	 * The time to response the request
	 */
	private String deliveredTime;
	/**
	 * The time done the request
	 */
	private String doneTime;	
	/**
	 * Status of delivery
	 */
	private String state;
	/**
	 * Error Code
	 */
	private String errorCode;
	
	/**
	 * retry times
	 */
	private int retry = 0;
	
	/**
	 * Psa
	 */
	private boolean isPsa = false;
	
	private boolean isEnqueue = true;
	
	private boolean isLongType = true;
	
	private long systemProcessTime = 0;
	
	private long smscProcessTime = 0;
	
	//XXX 2017-11-30 add by matthew
	private List<String> da;
	//key is da
	private Map<String,String> wsMessageIdMap;
	
	private Date createDate;
	
	private Map<String,Date> createDateMap;
	
	//2018-04-04 add by matthew for multisms retry
	private List<Map<String,String>> retrySeqNumList;

}
