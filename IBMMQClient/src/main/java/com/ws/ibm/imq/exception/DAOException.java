package com.ws.ibm.imq.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class DAOException extends MPException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5379160114803189711L;
	private Exception exception;

	public DAOException(Exception exception) {
		this.exception = exception;
	}

	public void printStackTrace() {
		exception.printStackTrace();
	}

	public String getLocalizedMessage() {
		return exception.getLocalizedMessage();
	}

	public String toString() {
		return exception.toString();
	}

	public void printStackTrace(PrintStream s) {
		exception.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s) {
		exception.printStackTrace(s);
	}

	public String getMessage() {
		return exception.getMessage();
	}

	public StackTraceElement[] getStackTrace() {
		return exception.getStackTrace();
	}
}