package com.ws.ibm.mq.util;

public class MQUtil {

	public static String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static String toHex(int messageId) {
		StringBuffer result = new StringBuffer(Integer.toHexString(messageId));
		while (result.length() < 8) {
			result.insert(0, '0');
		}
		return result.toString().toUpperCase();
	}

}
