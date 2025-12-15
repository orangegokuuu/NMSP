package com.ws.msp.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.FetPrefix;
import com.ws.msp.pojo.ImqSmsRecord;

@Repository
public class ImqSmsRecordDaoImpl extends GenericDaoImpl<ImqSmsRecord, String> implements ImqSmsRecordDao{

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
}
