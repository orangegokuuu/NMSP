package com.ws.msp.legacy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ws.msp.legacy.LegacyConstant.FIELD;
import com.ws.pojo.GenericBean;
import com.ws.util.RegexUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class MspMessage extends GenericBean {

	public static final int HEADER_LENGTH = 56;
	public static final int MAX_UNICODE_BODY = 70;
	public static final int MAX_BIG5_BODY = 70;
	public static final int MAX_ASCII_BODY = 160;
	public static final int DA_LENGTH = 20;
	public static final int SA_LENGTH = 20;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3512178822992369506L;

	private String imqMsgId = null;
	private MessageHeader header = null;
	private String body = null;
	private byte[] targets = null;

	public void setBody(byte[] msg, String lang) throws SMSException {
		try {
			this.body = new String(msg, 0, msg.length, lang).trim();
			log.debug("set content [{}], using coding [{}]", body, lang);
		} catch (UnsupportedEncodingException e) {
			throw new SMSException(LegacyConstant.FORMAT_ONE,
					LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.LANGUAGE));
		}
	}

	public byte[] toMessageA() throws UnsupportedEncodingException {
		return toMessageA("");
	}

	public byte[] toMessageA(String lang) throws UnsupportedEncodingException {

		int bodyLength = body.getBytes().length;

		if (StringUtils.isNotBlank(lang)) {
			bodyLength = body.getBytes(lang).length;
		}

		byte[] message = new byte[HEADER_LENGTH + bodyLength];

		System.arraycopy(header.serialize(), 0, message, 0, HEADER_LENGTH);
		log.debug("to message A body [{}] lan [{}]", body, lang);
		if (StringUtils.isNotBlank(lang)) {
			System.arraycopy(body.getBytes(lang), 0, message, HEADER_LENGTH,
					bodyLength);
		} else {
			System.arraycopy(body.getBytes(), 0, message, HEADER_LENGTH,
					bodyLength);
		}

		return message;
	}

	public byte[] toMessageC() {
		return targets;
	}

	public ArrayList<String> getTargetList() throws SMSException {
		ArrayList<String> targetList = new ArrayList<String>();
		int offset = 0;
		for (int i = 0; i < this.getHeader().getCount(); i++) {
			offset = DA_LENGTH * i;
			if (offset >= targets.length) {
				SMSException ex = new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(FIELD.COUNT));
				ex.setAck(header.isAckFlag());
				ex.setCorreId(imqMsgId);
				throw ex;
			}
			String target = new String(targets, offset, DA_LENGTH).trim();
			if (!RegexUtil.matchAny(target, LegacyConstant.PREFIX_CODE_REG_EX)) {
				log.debug("Target[{}] format not valid!", target);
				SMSException ex = new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
				ex.setAck(header.isAckFlag());
				ex.setCorreId(imqMsgId);
				throw ex;
			}
			targetList.add(target);
		}
		return targetList;
	}
}
