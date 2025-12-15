package com.ws.ibm.imq.exception;

public class MPException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5956921170424028895L;
	private String errorCode;
	private Throwable cause;

	/**
	 * @roseuid 3EE920AA0224
	 */
	public MPException() {

	}

	public MPException(String errorCode) {
		this.errorCode = errorCode;
	}

	public MPException(String errorCode, Throwable cause) {
		this.errorCode = errorCode;
		this.cause = cause;
	}

	public String getMessage() {
		return "error code: " + errorCode + ((cause != null) ? " -> " + cause.toString() : "");
	}

	public String getErrorCode() {
		return errorCode;
	}
}
