package com.ws.msp.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.CpDestinationAddress;

@Repository
public class CpDestinationDaoImpl extends GenericDaoImpl<CpDestinationAddress, String>
		implements CpDestinationAddressDao {

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}
}
