package com.ws.msp.legacy;

public class SMSException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8154931813302325315L;

	private int errorCode = LegacyConstant.DEFAULT_ERROR_CODE;

	private String correId = null;
	private boolean ack = false;
	private int format = LegacyConstant.FORMAT_ONE;

	public SMSException() {
		super();
	}

	public SMSException(String s) {
		super(s);
	}

	/**
	 * @param format
	 * 
	 * @param errorCode
	 * 
	 */
	public SMSException(int format, int errorCode) {
		super(format == LegacyConstant.FORMAT_ONE ? LegacyConstant.messageMap.get(errorCode)
				: LegacyConstant.messageTwoMap.get(errorCode));
		this.setFormat(format);
		this.errorCode = errorCode;
	}

	/**
	 * @param format
	 * 
	 * @param errorCode
	 * 
	 * @param ack
	 * 
	 */
	public SMSException(int format, int errorCode, boolean ack) {
		super(format == LegacyConstant.FORMAT_ONE ? LegacyConstant.messageMap.get(errorCode)
				: LegacyConstant.messageTwoMap.get(errorCode));
		this.setFormat(format);
		this.errorCode = errorCode;
		this.ack = ack;
	}

	/**
	 * @param format
	 * 
	 * @param errorCode
	 * 
	 * @param ack
	 * 
	 * @param correId
	 */
	public SMSException(int format, int errorCode, boolean ack, String correId) {
		super(format == LegacyConstant.FORMAT_ONE ? LegacyConstant.messageMap.get(errorCode)
				: LegacyConstant.messageTwoMap.get(errorCode));
		this.setFormat(format);
		this.errorCode = errorCode;
		this.ack = ack;
		this.correId = correId;
	}

	/**
	 * @param format
	 *            Format
	 * @param errorCode
	 *            Error Code
	 * @param msg
	 *            Error Message
	 */
	public SMSException(int format, int errorCode, String msg) {
		super(msg);
		this.setFormat(format);
		this.errorCode = errorCode;
	}

	/**
	 * @param code
	 *            Error code
	 * @param message
	 *            Exception message
	 * @param cause
	 *            Exception cause
	 */
	public SMSException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the correId
	 */
	public String getCorreId() {
		return correId;
	}

	/**
	 * @param correId
	 */
	public void setCorreId(String correId) {
		this.correId = correId;
	}

	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(int format) {
		this.format = format;
	}

	/**
	 * @return the ack
	 */
	public boolean isAck() {
		return ack;
	}

	/**
	 * @param ack
	 *            the ack to set
	 */
	public void setAck(boolean ack) {
		this.ack = ack;
	}
}
