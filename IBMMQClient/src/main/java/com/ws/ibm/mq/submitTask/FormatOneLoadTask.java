package com.ws.ibm.mq.submitTask;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.ibm.imq.exception.DAOException;
import com.ws.ibm.imq.manager.IMQProducer;
import com.ws.ibm.mq.handler.MQHandler;
import com.ws.util.ByteUtil;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FormatOneLoadTask implements Runnable {

	@Setter
	private String testData = null;
	@Setter
	private int submitVolume = 1;
	@Setter
	private byte[] msgId = null;
	@Setter
	private byte[] correId = null;
	@Setter
	private String cpId = null;
	/**
	 * unit in seconds
	 */
	@Setter
	private int duration = 600;
	@Setter
	private IMQProducer producer = null;

	private long createTime;

	public void init(String qmgrName, String host, int port, String channel) {
		producer = new IMQProducer(qmgrName, host, port, channel);
		try {
			producer.start(MQHandler.getCPQueueName(cpId, "REQ"));
		} catch (Exception e) {
			log.info("Exception occurred. Message=[{}]!", e.getMessage());
		}
	}

//	@PreDestroy
//	public void cleanUp() {
//		if (producer != null) {
//			producer.close();
//			log.debug("End Submit Task");
//		}
//	}

	@Override
	public void run() {

		createTime = System.currentTimeMillis();

		for (int i = 1; i < submitVolume + 1; i++) {
			log.info("loop {}", i);
			log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData)));
			log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData)));
			try {
				if ((System.currentTimeMillis() - createTime) < (duration * 1000)) {
					producer.put(msgId, correId, ByteUtil.hexStringToByteArray(testData));
				} else {
					log.info("Times up, close socket now !!");
//					cleanUp();
					break;
				}
			} catch (DAOException e) {
				log.warn("Fail to submit message to queue");
			}
		}
		log.info("Send [{}] message successed, close socket for next round !", submitVolume);
//		cleanUp();
	}

}
