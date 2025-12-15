package com.ws.msp.dao;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.SubConsoleUser;

public interface SubConsoleUserDao extends GenericDao<SubConsoleUser, String>{
	
	public boolean loginUser(String id, String password);
}
