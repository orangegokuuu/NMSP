package com.ws.emg.pojo;

import java.util.Timer;

import com.ws.pojo.GenericBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThroughPutObj extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 990440425887373369L;

	private String key = "";
	private long limit = 0;
	private Timer timer = null;
	private int period = 0;
}
