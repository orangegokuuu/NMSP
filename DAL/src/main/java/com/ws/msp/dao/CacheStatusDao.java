package com.ws.msp.dao;

import java.util.Map;

public interface CacheStatusDao {
	public static final String STORE_NAME = "CACHE_READY";
	
	public Map<String, Boolean> getStatus();
	
	public void ready(String cacheName);
	public void loading(String cacheName);
	
	public boolean isReady(String cacheName);
	public boolean isLoading(String cacheName);
}
