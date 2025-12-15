package com.ws.msp.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;

@Repository
public class MnpApiPhoneroutinginfoDaoImpl extends GenericDaoImpl<MnpApiPhoneroutinginfo, String> implements MnpApiPhoneroutinginfoDao{

	static final Logger logger = LogManager.getLogger(MnpApiPhoneroutinginfoDaoImpl.class);
	
	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}

	@Override
	public void batchSave(List<MnpApiPhoneroutinginfo> list) {
		//Transaction tx = null;
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			//tx = session.beginTransaction();
			int i=1;
			for(MnpApiPhoneroutinginfo info:list){
				session.save(info);
				i++;
				if(i==50){
					session.flush();
			        session.clear();
			        i = 1;
				}
			}
	       // tx.commit();

		}catch(Exception e){
			logger.error("[DB] mnp batchSave error:[{}]", e.getMessage());
			logger.error(e,e);
		}
	}
	
}
