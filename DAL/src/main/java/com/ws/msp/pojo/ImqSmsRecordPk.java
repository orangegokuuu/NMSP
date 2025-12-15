package com.ws.msp.pojo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ws.pojo.GenericBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ImqSmsRecordPk extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5967557789388452209L;

	@Column(name = "WS_MSG_ID", nullable = false, length = 30)
	private String wsMsgId = null;

	@Column(name = "DA", nullable = false, length = 30)
	private String da = null;

	public ImqSmsRecordPk() {

	}

	public ImqSmsRecordPk(String wsMsgId, String da) {
		super();
		this.wsMsgId = wsMsgId;
		this.da = da;
	}
}
