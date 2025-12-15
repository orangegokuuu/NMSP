package com.ws.msp.legacy;

import java.nio.ByteBuffer;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class MspMoMessage {

	private static final int DR_LENGTH = 66; 
	
	private String id = null;
	private int sub = 1;
	private int dlvrd = 0;
	private String submitDate = null;
	private String doneDate = null;
	private String stat = null;
	private String err = null;
	private String text = null;
	
	public byte[] serialize() {
		byte[] dr = new byte[MspMoMessage.DR_LENGTH];
		System.arraycopy(id.getBytes(), 0, dr, 0, id.getBytes().length);
		byte[] bytesSub = ByteBuffer.allocate(3).putInt(sub).array();
		System.arraycopy(bytesSub, 0, dr, 10, bytesSub.length);
		byte[] bytesDlvrd = ByteBuffer.allocate(3).putInt(dlvrd).array();
		System.arraycopy(bytesDlvrd, 0, dr, 13, bytesDlvrd.length);
		System.arraycopy(submitDate, 0, dr, 16, submitDate.getBytes().length);
		System.arraycopy(doneDate, 0, dr, 26, doneDate.getBytes().length);
		System.arraycopy(stat, 0, dr, 36, stat.getBytes().length);
		System.arraycopy(err, 0, dr, 43, err.getBytes().length);
//		System.arraycopy(text, 0, dr, 46, text.getBytes().length);
		System.arraycopy(text, 0, dr, 46, StringUtils.substring(text, 0, 20).getBytes().length);

		return dr;
	}
	
	
}
