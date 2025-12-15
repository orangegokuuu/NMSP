package com.ws.msp.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.TimeSlotData;
import com.ws.msp.pojo.TimeSlotDataPk;

@Repository
public class TimeSlotDataDaoImpl extends GenericDaoImpl<TimeSlotData, TimeSlotDataPk> implements TimeSlotDataDao{

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
}
