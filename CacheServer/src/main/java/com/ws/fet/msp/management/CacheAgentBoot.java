package com.ws.fet.msp.management;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.ws.fet.msp.server.ClusterDaemon;
import com.ws.msp.service.CacheAgent;

import lombok.extern.log4j.Log4j2;

@Component
@EnableScheduling
@Log4j2
public class CacheAgentBoot {
	@Autowired
	private ClusterDaemon daemon = null;

	@Autowired
	private CacheAgent agent = null;

	
	@PostConstruct
	public void loadOnBoot() {
		if (daemon.isMasterNode()) {
			log.info("Master Node detected, Load All Cache on Boot");
			agent.asyncLoadBl();
			agent.asyncLoadSw();
			agent.asyncLoadMnp();
			agent.asyncLoadPrefix();
			agent.load2LevelCache();
		}
	}

}
