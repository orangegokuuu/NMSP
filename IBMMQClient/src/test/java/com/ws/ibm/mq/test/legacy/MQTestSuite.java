package com.ws.ibm.mq.test.legacy;

import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MQTestSuite {

	public static byte[] getFormat1MessageA(String version, String cpId, String type, String drFlag, String ackFlag,
			String language, String target, String source, String count) {
		log.debug("input hexString[{}]", version + cpId + type + drFlag + ackFlag + language + target + source + count);
		return ByteUtil
				.hexStringToByteArray(version + cpId + type + drFlag + ackFlag + language + target + source + count);
	}

	public static byte[] getFormat1MessageC(String... targets) {
		return ByteUtil.hexStringToByteArray(String.join(",", targets));
	}

	public static String replaceCpId(String content, String cpId) {
		log.debug(content.substring(0 * 2, 1 * 2));
		log.debug(ByteUtil.byteArrayTohexString(cpId.getBytes()));
		log.debug(content.substring(8 * 2));
		return content.substring(0 * 2, 1 * 2) + ByteUtil.byteArrayTohexString(cpId.getBytes())
				+ content.substring(8 * 2);
	}

	public static String replaceXmlCpId(String content, String cpId) {
		String result = content.replaceFirst("(<SysId>)(\\w{7})(<\\/SysId>)", "<SysId>"+cpId+"</SysId>");
		log.debug("before: {}", content);
		log.debug("after: {}", result);
		return  result;
	}

}
