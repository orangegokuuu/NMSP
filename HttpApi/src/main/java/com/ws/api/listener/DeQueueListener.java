//package com.ws.api.listener;
//
//import javax.inject.Provider;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.core.task.TaskRejectedException;
//import org.springframework.stereotype.Component;
//
//import com.ws.api.processor.Consumer;
//import com.ws.emg.listener.AbstractListener;
//import com.ws.emg.listener.SocketListener;
//import com.ws.msp.config.MspProperties;
//
//@Component("apiDeQueueListener2")
//public class DeQueueListener extends AbstractListener implements SocketListener {
//	private static Logger logger = LogManager.getLogger(DeQueueListener.class);
//
//	@Autowired
//	private MspProperties properties;
//
//	@Autowired
//	@Qualifier("mtExcutor")
//	private TaskExecutor taskExecutor = null;
//
//	@Autowired
//	@Qualifier("moIntraExcutor")
//	private TaskExecutor moIntraTaskExecutor = null;
//
//	@Autowired
//	@Qualifier("moInterExcutor")
//	private TaskExecutor moInterTaskExecutor = null;
//
//	@Autowired
//	@Qualifier("drIntraExcutor")
//	private TaskExecutor drIntraExcutor = null;
//
//	@Autowired
//	@Qualifier("drInterExcutor")
//	private TaskExecutor drInterExcutor = null;
//
//	@Autowired
//	private Provider<Consumer> workerProvider = null;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.ws.emg.listener.SocketListener#handleRequest()
//	 */
//	@Override
//	public void handleRequest() {
//		try { // get message from JMS queue
//			createMTQueue();
//		} catch (TaskRejectedException e) {
//			// limited thread size so nothing to do here
//		} catch (Exception e) {
//			logger.error("[API CONSUMER] runtime error : [{}]", e.getMessage());
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.ws.emg.listener.SocketListener#startupCallback()
//	 */
//	@Override
//	public void startupCallback() throws Exception {
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.ws.emg.listener.SocketListener#shutdownCallback()
//	 */
//	@Override
//	public void shutdownCallback() {
//		setReading(false);
//	}
//
//
//	/**
//	 * Create MT queue worker
//	 */
//	private void createMTQueue() throws Exception {
//		Consumer worker = workerProvider.get();
//		worker.init(this, properties.getDal().getJms().getMtQueueName());
//		taskExecutor.execute(worker);
//	}
//
//}
