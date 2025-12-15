/**
 * 
 */
package com.ws.emg.exception;

public class InvalidMessage extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2617453243722287315L;

	public InvalidMessage() {
		super();
	}

	/**
	 * 
	 * @param msg Error Message
	 */
	public InvalidMessage(String msg) {
		super(msg);
	}

}
