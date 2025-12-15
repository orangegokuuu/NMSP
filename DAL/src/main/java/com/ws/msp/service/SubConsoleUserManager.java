package com.ws.msp.service;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.SubConsoleUser;

public interface SubConsoleUserManager extends GenericDataManager{

	SubConsoleUser getUser(String userId);

	boolean loginUser(String username, String password);

	SubConsoleUser updatePassword(String username, String password, String newPassword);

}
