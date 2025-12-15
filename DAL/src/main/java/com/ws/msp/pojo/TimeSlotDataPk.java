package com.ws.msp.pojo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.ws.pojo.GenericBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class TimeSlotDataPk extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4805486590411009925L;

	
	@Column(name = "TIME_TABLE_ID", nullable = false, length = 30)
	private String timeTableId = null;

	@Column(name = "DAY_ID")
	private int dayId;

	public TimeSlotDataPk() {

	}

	public TimeSlotDataPk(String timeTableId, int dayId) {
		super();
		this.timeTableId = timeTableId;
		this.dayId = dayId;
	}
}
