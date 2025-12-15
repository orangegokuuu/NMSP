package com.ws.msp.legacy;

import org.apache.commons.lang3.StringUtils;

import com.ws.msp.legacy.LegacyConstant.FIELD;
import com.ws.pojo.GenericBean;
import com.ws.util.RegexUtil;
import com.ws.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageHeader extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2807508697207908366L;

	private char version = 0;
	private String sysID = null;
	private char type = 0;
	private boolean drFlag = false;
	private boolean ackFlag = false;
	private char language = 0;
	private String target = null;
	private String source = null;
	private int count = 0;

	public MessageHeader() {
		super();
	}

	private static SMSException formSMSException(int errorCode) {
		return new SMSException(LegacyConstant.FORMAT_ONE, errorCode);
	}

	private static void fillField(byte[] dest, byte[] value, FIELD field) throws SMSException {
		if (value != null) {
			if (value.length > LegacyConstant.FORMAT_1_LENGTH.get(field)) {
				// throw new SMSException(field + " length too long");
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(field));
			} else {
				System.arraycopy(value, 0, dest, LegacyConstant.FORMAT_1_OFFSET.get(field), value.length);
			}
		}
	}

	private static void fillField(byte[] dest, char value, FIELD field) throws SMSException {
		byte b = (byte) value;
		fillField(dest, new byte[] { b }, field);
	}

	private static void fillField(byte[] dest, String value, FIELD field) throws SMSException {
		fillField(dest, value.getBytes(), field);
	}

	private static byte[] getField(byte[] header, FIELD field) {
		byte[] buffer = null;
		buffer = new byte[LegacyConstant.FORMAT_1_LENGTH.get(field)];
		System.arraycopy(header, LegacyConstant.FORMAT_1_OFFSET.get(field), buffer, 0,
				LegacyConstant.FORMAT_1_LENGTH.get(field));

		return buffer;
	}

	public byte[] serialize() {
		byte[] header = new byte[MspMessage.HEADER_LENGTH];
		try {
			if (version != 0) {
				fillField(header, version, FIELD.VERSION);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.VERSION));
			}

			if (!StringUtil.isEmpty(sysID)) {
				fillField(header, sysID, FIELD.SYSID);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.SYSID));
			}

			if (type != 0) {
				fillField(header, type, FIELD.TYPE);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.TYPE));
			}

			fillField(header, drFlag ? '1' : '0', FIELD.DRFLAG);
			fillField(header, ackFlag ? '1' : '0', FIELD.ACKFLAG);

			if (language != 0) {
				fillField(header, language, FIELD.LANGUAGE);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.LANGUAGE));
			}

			if (!StringUtil.isEmpty(target)) {
				fillField(header, target, FIELD.TARGET);
			}

			if (!StringUtil.isEmpty(source)) {
				fillField(header, source, FIELD.SOURCE);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.SOURCE));
			}

			fillField(header, StringUtils.leftPad(String.valueOf(count), 4, "0"), FIELD.COUNT);
			
		} catch (SMSException e) {
			e.printStackTrace();
		}
		return header;
	}

	public static boolean getSerializedAckFlag(byte[] headBytes) throws SMSException {
		byte[] buffer = null;
		buffer = getField(headBytes, FIELD.ACKFLAG);
		if ("1".equals(new String(buffer))) {
			return true;
		} else if ("0".equals(new String(buffer))) {
			return false;
		} else {
			throw new SMSException(LegacyConstant.FORMAT_ONE, LegacyConstant.FORMAT_1_ERROR.get(FIELD.ACKFLAG), true);
		}
	}

	public static MessageHeader deserialize(byte[] headBytes) throws SMSException {
		MessageHeader header = new MessageHeader();
		byte[] buffer = null;
		if (headBytes != null && headBytes.length == MspMessage.HEADER_LENGTH) {

			// handle other fields
			buffer = getField(headBytes, FIELD.VERSION);
			header.setVersion((char) buffer[0]);
			if (header.getVersion() != '1') {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.VERSION));
			}

			buffer = getField(headBytes, FIELD.SYSID);
			header.setSysID(new String(buffer).trim());

			buffer = getField(headBytes, FIELD.TYPE);
			header.setType((char) buffer[0]);

			buffer = getField(headBytes, FIELD.DRFLAG);
			if ("1".equals(new String(buffer))) {
				header.setDrFlag(true);
			} else if ("0".equals(new String(buffer))) {
				header.setDrFlag(false);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.DRFLAG));
			}

			buffer = getField(headBytes, FIELD.ACKFLAG);
			if ("1".equals(new String(buffer))) {
				header.setAckFlag(true);
			} else if ("0".equals(new String(buffer))) {
				header.setAckFlag(false);
			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.ACKFLAG));
			}

			buffer = getField(headBytes, FIELD.LANGUAGE);
			header.setLanguage((char) buffer[0]);
			if (!LegacyConstant.LANG.containsKey(String.valueOf(header.getLanguage()))) {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.LANGUAGE));
			}

			buffer = getField(headBytes, FIELD.SOURCE);
			// checking
			header.setSource(new String(buffer));

			if (header.getType() == 'M') {
				buffer = getField(headBytes, FIELD.COUNT);
				try {
					int c = Integer.parseInt(new String(buffer).trim());
					if (c < 2 || c > 9999) {
						throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.COUNT));
					}
					header.setCount(c);
				} catch (NumberFormatException e) {
					throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.COUNT));
				}
			} else if (header.getType() == 'S') {
				buffer = getField(headBytes, FIELD.TARGET);
				String t = new String(buffer).trim();

				// if (t.length() != 10) {
				// throw
				// formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
				// }

				if (!RegexUtil.matchAny(t, LegacyConstant.PREFIX_CODE_REG_EX)) {
					throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
				}
				
				// remove '+'
//				if (RegexUtil.exactMatches(t, LegacyConstant.PREFIX_CODE_REG_EX[3])) {
//					header.setTarget(t.substring(1));
//				} else {
					header.setTarget(t);
//				}

			} else {
				throw formSMSException(LegacyConstant.FORMAT_1_ERROR.get(FIELD.TYPE));
			}

		} else {
			throw new SMSException(LegacyConstant.FORMAT_ONE, 2001,
					String.format("Expected [%d]bytes but actual [%d]bytes", MspMessage.HEADER_LENGTH,
							(headBytes == null ? 0 : headBytes.length)));
		}

		return header;
	}
}
