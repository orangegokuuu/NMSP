package com.ws.ibm.mq.handler;

import java.io.IOException;
import java.util.List;

import javax.inject.Provider;
import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.imq.manager.IMQConsumer;
import com.ws.ibm.mq.submitTask.SubmitTaskOne;
import com.ws.ibm.mq.submitTask.SubmitTaskTwo;
import com.ws.msp.config.MspProperties;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacy.MspMessageTwo;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.legacyPojo.SMS;
import com.ws.msp.pojo.ContentProvider;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class MQHandler implements Runnable {

	@Autowired
	@Qualifier("MQHandlerList")
	private List<String> mqHandlerList = null;

	@Autowired
	@Qualifier("MQConsumerConnectionPool")
	private JmsConnectionFactory MQConsumerCf = null;

	@Value("${ibm.msg.a.timeout}")
	public final int MSG_A_TIMEOUT = 30000;

	@Value("${ibm.msg.c.timeout}")
	public final int MSG_C_TIMEOUT = 3000;

	@Autowired
	private Provider<SubmitTaskOne> SubmitTaskOneProvider;
	@Autowired
	private Provider<SubmitTaskTwo> SubmitTaskTwoProvider;

	@Autowired
	private Provider<MQSubmitHandler> mqSubmitHandlerProvider;

	@Autowired
	private MspProperties properties = null;

	@Autowired
	@Qualifier("submitExecutor")
	private ThreadPoolTaskExecutor submitExecutor = null;

	private MQSubmitHandler mqSubmitHandler = null;
	private ContentProvider cp = null;
	private String requestQName = null;

	private IMQConsumer imqConsumer = null; // format1

//	private MQJmsConsumer jmsConsumer = null; // format2

	String qmgrName = null;
	String host = null;
	int port = 1414;
	String channel = null;

	public void init(ContentProvider cp) {

		qmgrName = properties.getIbm().getJms().getQueueManagerName();
		host = properties.getIbm().getJms().getHost();
		port = properties.getIbm().getJms().getPort();
		channel = properties.getIbm().getJms().getChannel();

		this.cp = cp;
//		this.requestQName = getCPQueueName(cp.getCpId(), "REQ");

//Lenddice.20190522
		this.requestQName = getCPQueueName(cp, "REQ");

		mqSubmitHandler = mqSubmitHandlerProvider.get();
		mqSubmitHandler.init(cp);
//		this.jmsConsumer = new MQJmsConsumer(MQConsumerCf);

	}

	public static String getCPQueueName(String cpId, String type) {
		return "SMS." + cpId + "." + type + ".Q";
	}

	// Lenddice.20190522
	public static String getCPQueueName(ContentProvider cp, String type) {
		if (type.equals("REQ")) {
			if (cp.getMqReqQName() == null || cp.getMqReqQName().isEmpty()) {
				return "SMS." + cp.getCpId() + "." + type + ".Q";
			} else {
				return cp.getMqReqQName();
			}
		} else if (type.equals("PLY")) {
			if (cp.getMqRespQName() == null || cp.getMqRespQName().isEmpty()) {
				return "SMS." + cp.getCpId() + "." + type + ".Q";
			} else {
				return cp.getMqRespQName();
			}
		} else {
			return "SMS." + cp.getCpId() + "." + type + ".Q";
		}
	}

	public void handleMQ() {
		log.info("Start MQ Handling.");
		log.info("Start connect to IMQ");
		imqConsumer = new IMQConsumer(qmgrName, host, port, channel);
		final long startTime = System.currentTimeMillis();

		int count = 0;
		try {
			if (cp.isLegacy()) {
				count = handleFormatOneMsg();
			} else {
				count = handleFormatTwoMsg();
			}
		} catch (Exception e) {
			log.error("Fail to handle MQ operation. Please check MQ connection.");
			log.debug(e);
			
			// 20190617 YC modify, close connection when error
			final long endTime = System.currentTimeMillis();
			log.info("MQ Handling Ended. {} message(s) have been received using [{}] ms.", count,
					endTime - startTime);
			mqHandlerList.remove(cp.getCpId());
			
		} finally {

			/*
			// 20190617 YC modify, keep connection alive
			try {
				// close connection
				imqConsumer.close();
//				if (cp.isLegacy()) {
//				} else {
//					jmsConsumer.closeConnection();
//				}

				// count time when all thread end.
				for (;;) {
					int threadCount = submitExecutor.getActiveCount();
					log.info("Active Threads : {}", threadCount);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (threadCount == 0) {
						break;
					}
				}
			} catch (Exception e) {
				log.warn("Fail to close consumer connection.");
				log.debug(e);
			} finally {
				final long endTime = System.currentTimeMillis();
				log.info("MQ Handling Ended. {} message(s) have been received using [{}] ms.", count,
						endTime - startTime);
				mqHandlerList.remove(cp.getCpId());
			}
			*/
		}
	}

	// start receive message
	private int handleFormatOneMsg() throws JMSException {
		int msgCount = 0;
		MspMessage msg = null;
//		imqConsumer.start(MQHandler.getCPQueueName(cp.getCpId(), "REQ"));
		// Lenddice.20190522
		try {
			imqConsumer.start(MQHandler.getCPQueueName(cp, "REQ"));
			do {
				log.trace("Handling Format One Msg...");
				try {
					msg = imqConsumer.receiveFormatOneMsgA(requestQName, MSG_A_TIMEOUT, MSG_C_TIMEOUT);
					if (msg != null) {
						msgCount++;
						log.debug("msg count[{}]", msgCount);
						// check if msg belong to this queue
						if (!msg.getHeader().getSysID().equals(cp.getCpId())) {
							SMSException e = new SMSException(LegacyConstant.FORMAT_ONE,
									LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.SYSID),
									msg.getHeader().isAckFlag());
							e.setCorreId(msg.getImqMsgId());
							throw e;
						}

						// submit task to executor
						SubmitTaskOne submitTaskOne = SubmitTaskOneProvider.get();
						submitTaskOne.init(cp, msg);
						submitExecutor.submit(submitTaskOne);
						log.debug("Submit Task to execuotr.");
					}
				} catch (SMSException e) {
					
					if(e.getErrorCode() != 9999) {
					
						log.info("SMSException occurred. MsgId = [{}], ErrorCode=[{}], Message=[{}]", e.getCorreId(),
								e.getErrorCode(), e.getMessage());
	
						// 20190628 YC modify don't stop receive message
						/*
						// if queue empty
						if (e.getErrorCode() == 9999) {
							break;
						}
						*/
						// enqueue to reply q when ackFlag is true
						log.debug("Ack = {}.", e.isAck());
						if (e.isAck()) {
							mqSubmitHandler.handleFormatOneSMSException(e);
						}

					}
				}
			} while (true);
		} catch (Exception e) {
			log.info("Exception occurred. Message=[{}]! REQ.Q=[{}]", e.getMessage(),MQHandler.getCPQueueName(cp, "REQ"));
			mqHandlerList.remove(cp.getCpId());
		}
		return msgCount;
	}

	// start receive message
	private int handleFormatTwoMsg() throws JMSException, IOException {
		MspMessageTwo msg = null;
		int msgCount = 0;
//		jmsConsumer.startConnection(requestQName);
//		imqConsumer.start(MQHandler.getCPQueueName(cp.getCpId(), "REQ"));
		// Lenddice.20190522
		try {
			imqConsumer.start(MQHandler.getCPQueueName(cp, "REQ"));
			do {
				try {
					msg = imqConsumer.receiveFormatTwoMsgA(MSG_A_TIMEOUT, MSG_C_TIMEOUT);
					if (msg != null) {
						msgCount++;
						log.debug("msg count[{}]", msgCount);
						// check if msg belong to this queue
						if (!msg.getBody().getSysId().equals(cp.getCpId())) {
							throw new SMSException(LegacyConstant.FORMAT_ONE,
									LegacyConstant.FORMAT_2_ERROR.get(LegacyConstant.FIELD.SYSID),
									LegacyConstant.DEFAULT_FORMAT_2_ACK);
						}

						// replace validPeriod if necessary
						for (SMS.Message msgContent : msg.getBody().getMessage()) {
							int validPeriod = 4;
							try {
								validPeriod = Integer.parseInt(msgContent.getValidPeriod());
							} catch (NumberFormatException e) {
								log.info("ValidPeriod not equal to [0-4], replace value[{}] to 4",
										msgContent.getValidPeriod());
								validPeriod = 4;
							}

							if (!(validPeriod >= 0 && validPeriod <= 4)) {
								log.info("ValidPeriod not equal to [0-4], replace value[{}] to 4",
										msgContent.getValidPeriod());
								validPeriod = 4;
							}
							msgContent.setValidPeriod(Integer.toString(validPeriod));
						}
						log.debug("Adjusted Msg={}", msg);

						// submit task to executor
						SubmitTaskTwo submitTaskTwo = SubmitTaskTwoProvider.get();
						submitTaskTwo.init(cp, msg);
						submitExecutor.submit(submitTaskTwo);

					}
				} catch (SMSException e) {

					// 20190628 YC modify don't stop receive message
					/*
					// if queue empty
					if (e.getErrorCode() == 9999) {
						break;
					}
					*/
					if (e.getErrorCode() != 9999) {
						log.info("SMSException occurred. MsgId = [{}], ErrorCode=[{}], Message=[{}]", e.getCorreId(),
								e.getErrorCode(), e.getMessage());
						
						mqSubmitHandler.handleFormatTwoSMSException(e);
					}
				}
			} while (true);
		} catch (Exception e) {
			log.info("Exception occurred. Message=[{}]! REQ,Q=[{}]", e.getMessage(),MQHandler.getCPQueueName(cp, "REQ"));
			mqHandlerList.remove(cp.getCpId());
		}
		return msgCount;
	}

	@Override
	public void run() {
		this.handleMQ();
	}

}
