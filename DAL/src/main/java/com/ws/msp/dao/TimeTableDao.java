package com.ws.msp.dao;

import org.hibernate.Session;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.TimeTable;


public interface TimeTableDao extends GenericDao<TimeTable, String>{
	public Session getSession();
}
