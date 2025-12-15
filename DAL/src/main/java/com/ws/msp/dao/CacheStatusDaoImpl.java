package com.ws.msp.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;

@Repository
public class CacheStatusDaoImpl implements CacheStatusDao {
	@Autowired
	private HazelcastInstance cacheClient = null;

	private void setStatus(String cacheName, boolean status) {
		ReplicatedMap<String, Boolean> statusMap = cacheClient.getReplicatedMap(STORE_NAME);
		statusMap.put(cacheName, status);
	}

	@Override
	public void ready(String cacheName) {
		setStatus(cacheName,true);
	}

	@Override
	public void loading(String cacheName) {
		setStatus(cacheName,false);
	}

	@Override
	public boolean isReady(String cacheName) {
		ReplicatedMap<String, Boolean> statusMap = cacheClient.getReplicatedMap(STORE_NAME);
		
		if (statusMap.containsKey(cacheName)) {
			return statusMap.get(cacheName);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isLoading(String cacheName) {
		ReplicatedMap<String, Boolean> statusMap = cacheClient.getReplicatedMap(STORE_NAME);
		
		if (statusMap.containsKey(cacheName)) {
			return !statusMap.get(cacheName);
		} else {
			return false;
		}
	}
	
	@Override
	public Map<String, Boolean> getStatus() {
		ReplicatedMap<String, Boolean> statusMap = cacheClient.getReplicatedMap(STORE_NAME);
		
		Map<String,Boolean> status = new HashMap<String,Boolean>();
		status.putAll(statusMap);
		
		return Collections.unmodifiableMap(status);
	}

}
