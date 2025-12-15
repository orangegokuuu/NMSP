package com.ws.emg.smpp.retry;

import com.ws.emg.pojo.MessageObject;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QueueObject {

	private String smscId;
	private String deliverStatus;
	private String type;
	private String text;
	private String language;
	private MessageObject msg;
	//add by matthew 2018-10-11
	private int retryCount = 1;

	@Override
	public String toString() {
		return "QueueObject [smscId=" + smscId + ", deliverStatus=" + deliverStatus + ", type=" + type + ", text="
				+ text + ", language=" + language + ", retryCount=" + retryCount + ", msg=" + msg.toString() + "]";
	}
	
	
	
}
