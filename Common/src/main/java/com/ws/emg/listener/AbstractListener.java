package com.ws.emg.listener;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractListener extends Thread implements SocketListener {

	private static Logger logger = LogManager.getLogger(AbstractListener.class);
	private boolean running = false;
	private boolean reading = false;
	protected Date uptime = null;

	private boolean startError = false;

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the reading
	 */
	public boolean isReading() {
		return reading;
	}

	/**
	 * @param reading
	 *            the reading to set
	 */
	public void setReading(boolean reading) {
		this.reading = reading;
	}

	/**
	 * @return the startError
	 */
	public boolean isStartError() {
		return startError;
	}

	/**
	 * @param startError
	 *            the startError to set
	 */
	public void setStartError(boolean startError) {
		this.startError = startError;
	}

	/**
	 * @return the uptime
	 */
	public Date getUptime() {
		return uptime;
	}

	/**
	 * @param uptime
	 *            the uptime to set
	 */
	public void setUptime(Date uptime) {
		this.uptime = uptime;
	}

	public void startup() throws Exception {
		logger.debug("Starting listener");
		this.setUptime(new Date());
		startupCallback();
		setRunning(true);
		setReading(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ws.mnp.listener.SocketListener#stop()
	 */
	@Override
	public void shutdown() {
		pauseAll();
		setRunning(false);
		shutdownCallback();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ws.mnp.listener.SocketListener#pause()
	 */
	@Override
	public void pauseAll() {
		setReading(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ws.mnp.listener.SocketListener#resume()
	 */
	@Override
	public void resumeAll() {
		setReading(true);
	}

	public void start() {
		try {
			this.startup();
			super.start();
		} catch (Exception e) {
			logger.error("Error starting listener", e);
			setRunning(false);
			setStartError(true);
		}
	}

	public void run() {
		logger.debug("Starting listening to packet");
		while (isRunning()) {
			while (isReading()) {
				handleRequest();
			}
		}
	}
}
