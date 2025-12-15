/**
 * 
 */
package com.ws.emg.listener;


public interface SocketListener extends Runnable {

	public void startup() throws Exception;

	public void shutdown();

	public void pauseAll();

	public void resumeAll();

	public void handleRequest();

	public void startupCallback() throws Exception;

	public void shutdownCallback();
}
