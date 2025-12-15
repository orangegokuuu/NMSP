package com.ws.fet.msp.management;

import java.util.Date;

import com.ws.pojo.GenericBean;

public class ClientConnection extends GenericBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2146219541614638620L;

	private String connectionID = null;
	private String clientAddress = null;
	private Date creationTime = null;
	private String implementation = null;
	private int sessionCount = 0;

	public String getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(String connectionID) {
		this.connectionID = connectionID;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public int getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}

}
