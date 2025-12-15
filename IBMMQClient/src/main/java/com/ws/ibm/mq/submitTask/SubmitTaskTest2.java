package com.ws.ibm.mq.submitTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PreDestroy;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.message.MQJmsProducer;
import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.util.ByteUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Setter
@Getter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmitTaskTest2 implements Runnable {

	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQCf;

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
				String msgStamp = new SimpleDateFormat("yyMMddHHmmsss").format(new Date());
				byte[] zipData = MQZipUtil.compress(msgStamp, testData.getBytes("UTF-8"));
				log.debug("Data={}", testData);
				log.debug("Zipped Data={}", new String(zipData));
				producer.enqBytesMsgA(zipData);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
