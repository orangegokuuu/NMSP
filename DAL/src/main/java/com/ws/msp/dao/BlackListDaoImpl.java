package com.ws.msp.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ws.hibernate.GenericDaoImpl;
import com.ws.msp.pojo.BlackList;


@Repository
public class BlackListDaoImpl extends GenericDaoImpl<BlackList, String> implements BlackListDao{
	
	static final Logger logger = LogManager.getLogger(BlackListDaoImpl.class);

	@Autowired(required = true)
	public void setSessionFactory(SessionFactory mcFactory) {
		super.setSessionFactory(mcFactory);
	}

	@Override
	public void batchSave(List<BlackList> list) {
		//Transaction tx = null;
		try{
			Session session = super.getSessionFactory().getCurrentSession();
			//tx = session.beginTransaction();
			int i=1;
			int count = 0;
			for(BlackList bl:list){
				session.save(bl);
				i++;
				if(i==50){
					session.flush();
			        session.clear();
			        i = 1;
				}
				count++;
				if(count%100000==0){
					logger.info("black list insert count:[{}]",count);
				}
			}
	       // tx.commit();

		}catch(Exception e){
			logger.error("[DB] blacklist batchSave error:[{}]", e.getMessage());
			logger.error(e,e);
		}
	}
	
	
}
