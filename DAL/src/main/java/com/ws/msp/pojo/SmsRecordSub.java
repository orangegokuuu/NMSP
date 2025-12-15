package com.ws.msp.pojo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.BaseBean;
import lombok.Getter;
import lombok.Setter;

//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "SMS_RECORD_SUB", uniqueConstraints = @UniqueConstraint(columnNames = "SUB_ID", name = "PK_SMS_RECORD_SUB"))
public class SmsRecordSub extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6588248148818100741L;

	@Id
	@Column(name = "SUB_ID", nullable = false, length = 30)
	private String subId; 
//	@GeneratedValue(generator = "IdGenerator")
//	@GenericGenerator(name = "IdGenerator", strategy = "com.ws.hibernate.IdGenerator", parameters = {
//	        @Parameter(name = "prefix", value = "SMSC_MSG") })
	@Column(name = "SMSC_MSG_ID", length = 32)
	private String smscMsgId;
	
	@Column(name = "WS_MSG_ID", nullable = false, length = 10)
	private String wsMsgId;
	
	@Column(name = "SEG_NUM", length = 10)
	private String segNum;
	
	@Column(name = "SUBMIT_DATE")
	private Date submitDate;
	
	@Column(name = "SUBMIT_STATUS", length = 10)
	private String submitStatus;
	
	@Column(name = "DELIVER_DATE")
	private Date deliverDate;
	
	@Column(name = "DELIVER_STATUS", length = 15)
	private String deliverStatus;
	
	@Column(name = "DR_RESP_DATE")
	private Date drRespDate;
	
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	public SmsRecordSub(){
		super();
	}
	
}
