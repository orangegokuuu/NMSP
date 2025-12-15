package com.ws.msp.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.msp.dao.ContentProviderDao;
import com.ws.msp.dao.CpDestinationAddressDao;
import com.ws.msp.dao.CpSourceAddressDao;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpSourceAddress;

import lombok.extern.log4j.Log4j2;

@Service(value = "ContentProviderManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class ContentProviderManagerImpl extends GenericDataManagerImpl implements
		ContentProviderManager {

	@Autowired
	private ContentProviderDao contentProviderDao = null;
	@Autowired
	private CpSourceAddressDao cpSourceAddressDao = null;
	@Autowired
	private CpDestinationAddressDao cpDestAddressDao = null;

	@Override
	@Transactional
	public List<CpSourceAddress> listCpSa(String cpId) {
		DetachedCriteria criteria = cpSourceAddressDao.createDetachedCriteria();
		criteria.add(Restrictions.eq("cpId", cpId));
		criteria.addOrder(Order.asc("addressId"));
		return cpSourceAddressDao.findByCriteria(criteria);
	}

	@Transactional
	public boolean checkTimetableLinkage(String timetableId){
		DetachedCriteria criteria = contentProviderDao.createDetachedCriteria();
		criteria.add(Restrictions.eq("timeTableId", timetableId));
		return !contentProviderDao.findByCriteria(criteria).isEmpty();
	}
	
	@Transactional
	public List<ContentProvider> getAllMQCP(){
		DetachedCriteria criteria = contentProviderDao.createDetachedCriteria();
		criteria.add(Restrictions.eq("cpType", ContentProvider.CP_TYPE_MQ));
		return contentProviderDao.findByCriteria(criteria);
	}
	
	@Transactional
	public List<ContentProvider> getMQCPList(int mqManager){
		DetachedCriteria criteria = contentProviderDao.createDetachedCriteria();
		criteria.add(Restrictions.eq("cpType", ContentProvider.CP_TYPE_MQ));
		criteria.add(Restrictions.eq("cpZone", mqManager));
		return contentProviderDao.findByCriteria(criteria);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ContentProvider updateSmsLimit(String id, int smsLimit){
		ContentProvider cp = contentProviderDao.get(id);
		cp.setSmsLimit(smsLimit);
		return contentProviderDao.save(cp);
	}
	
}
