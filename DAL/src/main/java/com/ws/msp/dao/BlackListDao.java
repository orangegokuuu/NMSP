package com.ws.msp.dao;

import java.util.List;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.BlackList;

public interface BlackListDao extends GenericDao<BlackList, String>{

	public void batchSave(List<BlackList> list);
}
