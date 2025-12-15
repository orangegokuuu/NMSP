package com.ws.msp.mq.sac.pojo;

import com.ws.pojo.GenericComparable;

public class LoginCommand extends GenericComparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 446447004184798372L;
	
	private String username = null;
	private String password = null;
	private String newPassword = null;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
