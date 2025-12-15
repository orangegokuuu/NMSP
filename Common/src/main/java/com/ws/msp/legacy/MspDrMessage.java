package com.ws.msp.legacy;

import java.nio.ByteBuffer;

import lombok.Data;

@Data
public class MspDrMessage {

//	private static final int DR_LENGTH = 161;

	private String id = null;
	private int sub = 1;
	private int dlvrd = 0;
	private String submitDate = null;
	private String doneDate = null;
	private String stat = null;
	private String err = null;
	private String text = null;

	// public byte[] serialize() {
	// byte[] dr = new byte[MspDrMessage.DR_LENGTH];
	// System.arraycopy(addHeader("id", id).getBytes(), 0, dr, 0,
	// id.getBytes().length);
	//
	// byte[] bytesSub = ByteBuffer.allocate(3).putInt(sub).array();
	// System.arraycopy(addHeader("sub", new String(bytesSub)).getBytes(), 0,
	// dr, 10, bytesSub.length);
	//
	// byte[] bytesDlvrd = ByteBuffer.allocate(3).putInt(dlvrd).array();
	// System.arraycopy(addHeader("dlvrd", new String(bytesDlvrd)).getBytes(),
	// 0, dr, 13, bytesDlvrd.length);
	//
	// System.arraycopy(addHeader("submit date", submitDate), 0, dr, 16,
	// submitDate.getBytes().length);
	//
	// System.arraycopy(addHeader("done date", doneDate), 0, dr, 26,
	// doneDate.getBytes().length);
	// System.arraycopy(addHeader("stat", stat), 0, dr, 36,
	// stat.getBytes().length);
	// System.arraycopy(addHeader("err", err), 0, dr, 43,
	// err.getBytes().length);
	// // System.arraycopy(text, 0, dr, 46, text.getBytes().length);
	// System.arraycopy(addHeader("text", text), 0, dr, 46,
	// StringUtils.substring(text, 0, 20).getBytes().length);
	//
	// return dr;
	// }

	public String serialize() {
		StringBuilder dr = new StringBuilder();
		
		byte[] bytesId = new byte[10];
		System.arraycopy(id.getBytes(), 0, bytesId, 0, id.getBytes().length);
		dr.append(addHeader("id", new String(bytesId)));
		
		byte[] bytesSub = ByteBuffer.allocate(3).putInt(sub).array();
		dr.append(addHeader("sub", bytesSub.toString()));
		
		byte[] bytesDlvrd = ByteBuffer.allocate(3).putInt(dlvrd).array();
		dr.append(addHeader("dlvrd", bytesDlvrd.toString()));
		
		// should be YYMMDDhhmm
		byte[] bytesSubmitDate = new byte[10];
		System.arraycopy(submitDate, 0, bytesSubmitDate, 0, submitDate.getBytes().length);
		dr.append(addHeader("submit date", new String(bytesSubmitDate)));
		
		byte[] bytesDoneDate = new byte[10];
		System.arraycopy(doneDate, 0, bytesDoneDate, 0, doneDate.getBytes().length);
		dr.append(addHeader("done date", new String(bytesDoneDate)));
		
		byte[] bytesStat = new byte[7];
		System.arraycopy(stat, 0, bytesStat, 0, stat.getBytes().length);
		dr.append(addHeader("stat", stat));
		
		byte[] bytesErr = new byte[3];
		System.arraycopy(err, 0, bytesErr, 0, err.getBytes().length);
		dr.append(addHeader("err", err));
		
		byte[] bytesText = new byte[20];
		System.arraycopy(text, 0, bytesText, 0, text.getBytes().length);
		dr.append(addHeader("text", text));
		
		return dr.toString();
	}

	private String addHeader(String field, String content) {
		return field + ":" + content + " ";
	}

}
