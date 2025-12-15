package com.ws.msp.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.msp.dao.TimeSlotDataDao;
import com.ws.msp.dao.TimeTableDao;
import com.ws.msp.pojo.TimeSlotData;

import lombok.extern.log4j.Log4j2;



@Service(value = "timeTableManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class TimeTableManagerImpl extends GenericDataManagerImpl implements TimeTableManager{

	@Autowired
	private TimeTableDao timeTableDao = null;
	@Autowired
	private TimeSlotDataDao timeSlotDataDao = null;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Object[]> getTimeTableList(){
		String sql = "select tt.timeTableId, tt.timeTableName, tt.status from TimeTable tt where tt.status = 'A'";
		Query ttQuery = timeTableDao.getSession().createQuery(sql);
		List<Object[]> ttList = new ArrayList<Object[]>();
		ttList = ttQuery.list();
		return ttList;
	}
	
	@Transactional
	public List<TimeSlotData> getOrderedTimeSlotDatas(String id){
		DetachedCriteria criteria = timeSlotDataDao.createDetachedCriteria();
		criteria.add(Restrictions.eq("pk.timeTableId", id));
		criteria.addOrder(Order.asc("pk.dayId"));
		
		return timeSlotDataDao.findByCriteria(criteria);
	}
	
}
