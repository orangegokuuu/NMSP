package com.ws.msp.service;

import java.util.List;

import com.ws.hibernate.GenericDataManager;
import com.ws.msp.pojo.TimeSlotData;


public interface TimeTableManager extends GenericDataManager{
	public List<Object[]> getTimeTableList();
	public List<TimeSlotData> getOrderedTimeSlotDatas(String id);
}
