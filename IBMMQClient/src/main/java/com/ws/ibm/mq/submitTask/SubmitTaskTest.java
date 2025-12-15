package com.ws.ibm.mq.submitTask;

import javax.annotation.PreDestroy;
import javax.jms.JMSException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.util.ByteUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Setter
@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmitTaskTest implements Runnable {

	private JmsConnectionFactory MQCf = null;

	private String destinationName = null;
	private String testData = null;
	private int SUBMIT_VOLUME = 1;

	private MQJmsProducer producer = null;

	public void init() {
		producer = new MQJmsProducer(MQCf);
	}

	@PreDestroy
	public void cleanUp() {
		if (producer != null) {
			producer.closeConnection();
			log.info("End Submit Task");
		}
	}

	@Override
	public void run() {
		try {
			producer.startConnection(destinationName);
			for (int i = 0; i < SUBMIT_VOLUME; i++) {
				log.info("loop {}", i);
				log.debug("byte={}", ByteUtil.byteArrayTohexString(ByteUtil.hexStringToByteArray(testData)));
				log.debug("String={}", new String(ByteUtil.hexStringToByteArray(testData)));
				producer.enqTextMsgA(new String(ByteUtil.hexStringToByteArray(testData)));
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
