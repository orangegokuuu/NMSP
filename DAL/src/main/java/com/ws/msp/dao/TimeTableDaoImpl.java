package com.ws.msp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.TimeTable;

@Repository
public class TimeTableDaoImpl extends GenericDaoImpl<TimeTable, String> implements TimeTableDao{

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
	
	public Session getSession(){
		return this.getCurrentSession();
	}
	
}
