package com.ws.msp.pojo;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.pojo.GenericBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" }, ignoreUnknown = true)
@Entity
@Table(name = "IMQ_SMS_RECORD", uniqueConstraints = @UniqueConstraint(columnNames = {"WS_MSG_ID","DA"}, name = "PK_IMQ_SMS_RECORD"))
public class ImqSmsRecord extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8976239128202428761L;

	@EmbeddedId
	private ImqSmsRecordPk pk = new ImqSmsRecordPk();

	@Column(name = "IMQ_MSG_ID", length = 60)
	private String imqMsgId = null;
	
	@Column(name = "IMQ_TOKEN", length = 40)
	private String imqToken = null;
	@Column(name = "IMQ_LANGUAGE", length = 1)
	private String language = null;

	@Column(name = "IMQ_MSG_HEADER", length = 112)
	private String imqMsgHeader = null;
	
	// === YC Added for CP timebase collector start ===
	@Column(name = "CP_ID", length = 30)
	private String cpId = null;
	
	@Column(name = "WS_MAPPING_ID", length = 32)
	private String wsMappingId = null;
	
	@Column(name = "DESTINATION", length = 20)
	private String destination = null;
	
	@Column(name = "SUBMIT_TIME", length = 20)
	private String submitTime = null;
	
	@Column(name = "DONE_TIME", length = 20)
	private String doneTime = null;
	
	@Column(name = "MESSAGE", length = 4000)
	private String message = null;
	
	@Column(name = "ERROR_CODE", length = 10)
	private String errorCode = null;
	
	@Column(name = "STATE", length = 20)
	private String state = null;

	@Column(name = "ENQUEUE_TIME", length = 20)
	private String enqueueTime = null;
	// === YC Added for CP timebase collector end ===
}
