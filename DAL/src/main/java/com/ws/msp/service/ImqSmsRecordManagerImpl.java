package com.ws.msp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.ws.hibernate.GenericDataManagerImpl;
import com.ws.msp.dao.FetPrefixDao;
import com.ws.msp.dao.ImqSmsRecordDao;
import com.ws.msp.pojo.FetPrefix;

import lombok.extern.log4j.Log4j2;

@SuppressWarnings("unused")
@Service(value = "imqSmsRecordManager")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Log4j2
public class ImqSmsRecordManagerImpl extends GenericDataManagerImpl implements ImqSmsRecordManager{

	@Autowired
	private ImqSmsRecordDao imqSmsRecordDao = null;
	
	
}
