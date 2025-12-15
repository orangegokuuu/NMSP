package com.ws.msp.sac.subsac.pojo;

import lombok.Data;

@Data
public class MqCpRequestBody {
	
	private String cpId = null;
	private String cpName = null;
	private int waterLevel = -1;
	private String sourceAddress = null;
	private String destinationAddress = null;
	private String mqManagerName = null;
	private boolean spamCheck = true;
	private boolean legacy = true;
	
}
