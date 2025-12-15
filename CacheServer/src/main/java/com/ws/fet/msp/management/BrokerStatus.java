package com.ws.fet.msp.management;

import java.io.Serializable;

import lombok.Data;

@Data
public class BrokerStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8967222122452155024L;

	private boolean started = false;
	private boolean	backup = false;
	private boolean clustered = false;
	private boolean replicaSync = false;
	private boolean persistenceEnabled = false;
	private String nodeID = null;
	private String version = null;
	private String uptime = null;
	private int connectionCount = 0;
	private int threadPoolMaxSize = 0;
	private String bindingsDirectory = null;
	private String journalType = null;
	private String journalDirectory = null;

}
