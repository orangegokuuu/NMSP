package com.ws.msp.legacy;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class LegacyConstant {

	public static final String[] PREFIX_CODE_REG_EX = {
			"([9][0-9]{8})",
			"([0][9][0-9]{8})",
			"([8][8][6][9][0-9]{8})", 
			"([+][0-9]{6,20})",
			"([0-9]{6,20})"
	};
	
	public static final int FORMAT_ONE = 1;
	public static final int FORMAT_TWO = 2;

	public static final boolean DEFAULT_FORMAT_1_ACK = true;
	public static final boolean DEFAULT_FORMAT_2_ACK = true;
	
	public static final int DEFAULT_ERROR_CODE = 2001;

	public static enum FIELD {
		VERSION, SYSID, TYPE, DRFLAG, ACKFLAG, LANGUAGE, TARGET, SOURCE, COUNT, BODY
	};

	public static Map<String, Integer> QM_MAP = new ImmutableMap.Builder<String, Integer>().put("MGW.QM", 11)
			.put("MGW.QM2", 12).put("DMZ.QM", 13).put("DMZ.QM2", 14).put("DMZ.QM4", 15).put("DMZ.QM3", 16).build();
	
	public static Map<Integer, String> QM_MAP_R = new ImmutableMap.Builder<Integer, String>().put(11, "MGW.QM")
			.put(12, "MGW.QM2").put(13, "DMZ.QM").put(14, "DMZ.QM2").put(15, "DMZ.QM4").put(16, "DMZ.QM3").build();


	public static Map<FIELD, Integer> FORMAT_1_OFFSET = new ImmutableMap.Builder<FIELD, Integer>().put(FIELD.VERSION, 0)
			.put(FIELD.SYSID, 1).put(FIELD.TYPE, 8).put(FIELD.DRFLAG, 9).put(FIELD.ACKFLAG, 10).put(FIELD.LANGUAGE, 11)
			.put(FIELD.TARGET, 12).put(FIELD.SOURCE, 32).put(FIELD.COUNT, 52).build();

	public static Map<FIELD, Integer> FORMAT_1_LENGTH = new ImmutableMap.Builder<FIELD, Integer>().put(FIELD.VERSION, 1)
			.put(FIELD.SYSID, 7).put(FIELD.TYPE, 1).put(FIELD.DRFLAG, 1).put(FIELD.ACKFLAG, 1).put(FIELD.LANGUAGE, 1)
			.put(FIELD.TARGET, 20).put(FIELD.SOURCE, 20).put(FIELD.COUNT, 4).build();

	public static Map<FIELD, Integer> FORMAT_1_ERROR = new ImmutableMap.Builder<FIELD, Integer>()
			.put(FIELD.VERSION, 1000).put(FIELD.SYSID, 1001).put(FIELD.BODY, 1003).put(FIELD.TYPE, 1004)
			.put(FIELD.DRFLAG, 1004).put(FIELD.ACKFLAG, 1004).put(FIELD.LANGUAGE, 1005).put(FIELD.TARGET, 1008)
			.put(FIELD.SOURCE, 2001).put(FIELD.COUNT, 1006).build();

	public static Map<FIELD, Integer> FORMAT_2_ERROR = new ImmutableMap.Builder<FIELD, Integer>()
			.put(FIELD.VERSION, 1000).put(FIELD.SYSID, 1001).put(FIELD.BODY, 1003).put(FIELD.TYPE, 1004)
			.put(FIELD.DRFLAG, 1004).put(FIELD.ACKFLAG, 1004).put(FIELD.LANGUAGE, 1005).put(FIELD.TARGET, 1008)
			.put(FIELD.SOURCE, 2001).put(FIELD.COUNT, 1006).build();

	public static Map<String, String> LANG = new ImmutableMap.Builder<String, String>().put("C", "UTF-16BE")
			.put("E", "ISO-8859-1").put("B", "Big5").put("V", "UTF-16BE").put("U", "ISO-8859-1").put("W", "Big5").build();

	public static Map<String, Integer> HTTPAPI_RESP_ERROR_MAP_FORMAT_1 = new ImmutableMap.Builder<String, Integer>()
			.put("11101", 2001).put("11102", 1001).put("11103", 1001).put("11104", 2001).put("11105", 1008)
			.put("11106", 1002).put("11107", 1005).put("11108", 2001).put("11109", 1004).put("11110", 1003)
			.put("11114", 1009).put("11115", 2001).put("11117", 1009).build();

	public static Map<String, Integer> HTTPAPI_RESP_ERROR_MAP_FORMAT_2 = new ImmutableMap.Builder<String, Integer>()
			.put("11101", 2001).put("11102", 1001).put("11103", 1001).put("11104", 1000).put("11105", 1008)
			.put("11106", 1002).put("11107", 1005).put("11108", 2001).put("11109", 1004).put("11110", 1003)
			.put("11113", 1008).put("11114", 1009).put("11115", 2001).put("11117", 1012).build();

	public static Map<Integer, String> messageMap = new ImmutableMap.Builder<Integer, String>()
			.put(1000, "Message version error, not support").put(1001, "SYSID not valid")
			.put(1002, "Service code not valid").put(1003, "Short message length error")
			.put(1004, "Type error, not support").put(1005, "Language error, not support")
			.put(1006, "Count for multicast error").put(1007, "Receive multicast target list error")
			.put(1008, "Invalid target address").put(1009, "Duplicated message not allowed")
			.put(2000, "System fatal error occurred").put(2001, "Internal error occurred")
			.put(9999, "No message in queue").build();
	
	public static Map<Integer, String> messageTwoMap = new ImmutableMap.Builder<Integer, String>()
			.put(1000, "Invalid XML format").put(1001, "Invalid SysId")
			.put(1002, "Invalid Source Address").put(1003, "Short Message Length Too Long")
			.put(1005, "Language Not Supported").put(1008, "Invalid Target Address")
			.put(1009, "Spam Message Rejected.").put(1010, "Bulk Message Oversized")
			.put(1011, "Source Address In BlackList").put(1012, "Maximum Water Level Reached").put(1013, "FRAUD Message")
			.put(2000, "System fatal error occurred").put(2001, "Internal error occurred")
			.put(9999, "No message in queue").build();

	public static Map<Integer, String> GSM_CODING = new ImmutableMap.Builder<Integer, String>()
			.put(0, "E").put(1, "E").put(3, "E").put(8, "C").put(72, "C")
			.build();
	
	
}
