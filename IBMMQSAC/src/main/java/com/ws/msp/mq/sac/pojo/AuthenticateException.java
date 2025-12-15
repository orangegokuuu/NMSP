package com.ws.msp.mq.sac.pojo;

public class AuthenticateException extends RuntimeException {
	/**
     * 
     */
	private static final long serialVersionUID = -5676302190179635209L;
	private int errorCode = 0;

	/**
     * 
     */
	public AuthenticateException() {

	}

	/**
	 * 
	 * @param code
	 *            Error code
	 * @param message
	 *            Error message
	 */
	public AuthenticateException(int code, String message) {
		super(message);
		this.errorCode = code;
	}

	/**
	 * @param cause
	 *            Exception cause
	 */
	public AuthenticateException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param code
	 *            Error code
	 * @param message
	 *            Exception message
	 * @param cause
	 *            Exception cause
	 */
	public AuthenticateException(int code, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = code;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
