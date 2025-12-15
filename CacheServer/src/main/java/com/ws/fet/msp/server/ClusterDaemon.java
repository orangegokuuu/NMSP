/**
 * 
 */
package com.ws.fet.msp.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.hazelcast.core.HazelcastInstance;
import com.ws.backend.Result;
import com.ws.backend.cluster.hazelcast.HazelcastBackedDaemon;
import com.ws.fet.msp.config.CacheServerProperties;
import com.ws.fet.msp.task.ResetHourCounterTask;
import com.ws.fet.msp.task.ResetMinCounterTask;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ClusterDaemon extends HazelcastBackedDaemon {

	@Autowired
	public ClusterDaemon(CacheServerProperties clusterCfg) {
		super(clusterCfg.getCluster());
	}

	
	@PostConstruct
	public void startCluster() throws Exception {
		super.start();
	}

	public HazelcastInstance getHz() {
		return this.hz;
	}

	@PreDestroy
	public void shut() {
		log.debug("Shutdown Command Listener");
		super.shut();
	}

	public void pause() throws Exception {
		super.pause();
	}

	public void resume() throws Exception {
		super.resume();
	}

	// reset minutely
	@Scheduled(cron = "0 * * * * *")
	public void minuteTimer() {
		ResetMinCounterTask minuteTask = new ResetMinCounterTask();
		if (this.isMasterNode()) {
			Result<Void> result = this.election(minuteTask, 0);
			log.debug("Result = [{}]", result);
		}
	}

	// reset hourly
	@Scheduled(cron = "0 0 * * * *")
	public void hourTimer() {
		ResetHourCounterTask hourTask = new ResetHourCounterTask();
		if (this.isMasterNode()) {
			Result<Void> result = this.election(hourTask, 0);
			log.debug("Result = [{}]", result);
		}
	}

}
