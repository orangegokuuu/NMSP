/**
 * 
 */
package com.ws.emg.exception;

public class ReadTimeout extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2617453243722287315L;

	public ReadTimeout() {
		super();
	}

	/**
	 * @param msg
	 *            Error Message
	 */
	public ReadTimeout(String msg) {
		super(msg);
	}

}
