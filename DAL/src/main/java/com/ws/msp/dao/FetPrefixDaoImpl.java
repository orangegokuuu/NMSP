package com.ws.msp.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.FetPrefix;


@Repository
public class FetPrefixDaoImpl extends GenericDaoImpl<FetPrefix, String> implements FetPrefixDao{

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
}
