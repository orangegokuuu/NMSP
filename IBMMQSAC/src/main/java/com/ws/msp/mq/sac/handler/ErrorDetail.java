/**
 * 
 */
package com.ws.msp.mq.sac.handler;

import org.springframework.http.HttpStatus;

import com.ws.pojo.GenericBean;


public class ErrorDetail extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2398773726748031058L;
	private HttpStatus status = null;
	private String message = null;
	private Throwable throwabe = null;

	/**
	 * @param status
	 *            HTTP Status code
	 * @param message
	 *            Error Message
	 * @param throwabe
	 *            Exception Cause
	 */
	public ErrorDetail(HttpStatus status, String message, Throwable throwabe) {
		super();
		this.status = status;
		this.message = message;
		this.throwabe = throwabe;
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the throwabe
	 */
	public Throwable getThrowabe() {
		return throwabe;
	}

	/**
	 * @param throwabe
	 *            the throwabe to set
	 */
	public void setThrowabe(Throwable throwabe) {
		this.throwabe = throwabe;
	}

}
