package com.ws.msp.dao;

import java.util.List;

import com.ws.hibernate.GenericDao;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;


public interface MnpApiPhoneroutinginfoDao extends GenericDao<MnpApiPhoneroutinginfo, String>{

	public void batchSave(List<MnpApiPhoneroutinginfo> list);
}
