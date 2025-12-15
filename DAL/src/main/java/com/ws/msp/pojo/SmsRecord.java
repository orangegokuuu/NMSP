package com.ws.msp.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.ws.hibernate.pojo.BaseProfileBean;
import lombok.Getter;
import lombok.Setter;


//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "SMS_RECORD", uniqueConstraints = @UniqueConstraint(columnNames = "WS_MSG_ID", name = "PK_SMS_RECORD"))
public class SmsRecord extends BaseProfileBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7089846319700379767L;

	@Id
	@Column(name = "WS_MSG_ID", nullable = false, length = 32)
	private String wsMsgId;
	
	@Column(name = "REQ_MSG_ID", nullable = false, length = 10)
	private String reqMsgId;
	
//	@Column(name = "SMSC_MSG_ID", length = 8)
//	private String smscMsgId;
	
	@Column(name = "SYS_ID", nullable = false, length = 30)
	private String sysId;
	
	@Column(name = "OA", nullable = false, length = 20)
	private String oa;
	
	@Column(name = "DA", nullable = false, length = 20)
	private String da;
	
	@Column(name = "LANGUAGE", nullable = false, length = 1)
	private String language;
	
	@Column(name = "TEXT", nullable = false, length = 4000)
	private String text;
	
	@Column(name = "DR_FLAG", length = 5)
	private boolean drFlag = false;;
	
	@Column(name = "VALID_TYPE", length = 1)
	private String validType;
	
	@Column(name = "SMS_SOURCE_TYPE", length = 1)
	private String smsSourceType;
	
	@Column(name = "ACCEPT_DATE")
	private Date acceptDate;
	
	@Column(name = "ACCEPT_STATUS", length = 10)
	private String acceptStatus;
	
	@Column(name = "RESULT_CODE", length = 10)
	private String resultCode;
	
//	@Column(name = "DR_RESP_DATE")
//	private Date drRespDate;
	
	@Column(name = "SOURCE_TON")
	private int sourceTon;
	
	@Column(name = "SOURCE_NPI")
	private int sourceNpi;
	
	@Column(name = "DEST_TON")
	private int destTon;
	
	@Column(name = "DEST_NPI")
	private int destNpi;
	
	@Column(name = "ESM_CLASS")
	private int esmClass;
	
	@Column(name = "IS_INTER", length = 4)
	private String isInter;
	
	@Column(name = "IS_BLACKLIST", length = 1)
	private String isBlacklist;
	
	@Column(name = "IS_SPAM", length = 1)
	private String isSpam;
	
	@Column(name = "PRIORITY_FL")
	private int priorityFl;
	
	@Column(name = "TOTAL_SEG")
	private int totalSeg;
	
	@Column(name = "SMS_TYPE", length = 5)
	private String smsType;
	
	
	@JsonProperty(value = "subs", access = Access.READ_ONLY)
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "wsMsgId")
	private List<SmsRecordSub> subs = new ArrayList<SmsRecordSub>();

	public List<SmsRecordSub> getSubs() {
		return subs;
	}
	
	public void setSubs(List<SmsRecordSub> subs) {
		if (this.subs == null){
			this.subs = subs;
		}else{
			this.subs.clear();
			this.subs.addAll(subs);
		}
	}
}
