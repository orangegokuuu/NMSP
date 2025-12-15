package com.ws.emg.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.ws.api.util.HttpApiUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.emg.smpp.retry.QueueObject;
import com.ws.emg.smpp.retry.RetryRxQueue;
import com.ws.emg.util.EmgParser;
import com.ws.jms.service.JmsService;
import com.ws.msp.config.EmgProperties;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.SmsRecordManager;
import com.ws.smpp.AsyncReceiver;
import com.ws.smpp.AsyncTransmitter;
import com.ws.smpp.Receiver;
import com.ws.smpp.SmppConnector;
import com.ws.smpp.SmsRequest;
import com.ws.smpp.Transmitter;

import ie.omk.smpp.message.SubmitSM;

@Component("emgDeQueueListener")
public class DeQueueListener {

	private static Logger logger = LogManager.getLogger(DeQueueListener.class);

	public static final String KEY_TPS_TX = "KEY_TPS_TX";
	public static final String KEY_TPS_RX = "KEY_TPS_RX";

	@Autowired(required = false)
	@Qualifier("txPsaPool")
	CommonsPool2TargetSource txPsaPool = null;

	@Autowired(required = false)
	@Qualifier("txNormalPool")
	CommonsPool2TargetSource txNormalPool = null;

	@Autowired(required = false)
	@Qualifier("rxPsaPool")
	CommonsPool2TargetSource rxPsaPool = null;

	@Autowired(required = false)
	@Qualifier("rxNormalPool")
	CommonsPool2TargetSource rxNormalPool = null;

	@Autowired
	HazelcastInstance cacheClient;

	@Autowired
	JmsService jmsService;

	@Autowired
	EmgParser parser;

	@Autowired
	MspProperties properties;

	@Autowired
	RetryRxQueue retryRxQueue;

	@Autowired
	SmsRecordManager smsRecordManager;

	private int failCount = 0;
	private Timer checkProcess = new Timer();

	// @Scheduled(fixedDelay = 1000)
	private void resetTPS() {
		try {
			resetTxTps();
		} catch (Exception e) {
			logger.debug("[TPS] reset TX counter error, due to {}", e.getMessage());
			logger.debug(e, e);
		}
		try {
			resetRxTps();
		} catch (Exception e) {
			logger.debug("[TPS] reset RX counter error, due to {}", e.getMessage());
			logger.debug(e, e);
		}
		// logger.debug("[TPS] clear tps count !!");
	}

	public long getTxTps() {
		// return cacheClient.getCPSubsystem().getAtomicLong(KEY_TPS_TX).incrementAndGet();
		return cacheClient.getAtomicLong(KEY_TPS_TX).incrementAndGet();
	}

	public long getRxTps() {
		// return cacheClient.getCPSubsystem().getAtomicLong(KEY_TPS_RX).incrementAndGet();
		return cacheClient.getAtomicLong(KEY_TPS_RX).incrementAndGet();
	}

	private void resetTxTps() {
		// cacheClient.getCPSubsystem().getAtomicLong(KEY_TPS_TX).set(0);
		cacheClient.getAtomicLong(KEY_TPS_TX).set(0);
	}

	private void resetRxTps() {
		// cacheClient.getCPSubsystem().getAtomicLong(KEY_TPS_RX).set(0);
		cacheClient.getAtomicLong(KEY_TPS_RX).set(0);
	}

	private boolean canProcess() {

		boolean result = false;

		String host = "UNKNOWN";
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.debug("Cannot get host name");
			logger.debug(e, e);
		}

		if (checkSMPPBinding(getSMPPStatus(txPsaPool), getSMPPStatus(txNormalPool), getSMPPStatus(rxPsaPool),
				getSMPPStatus(rxNormalPool))) {
			result = true;
		}

		if (result) {
			try {
				Map<String, Boolean> map = cacheClient.getMap(ApiConstant.KEY_HTTPAPI_ENABLE);
				result = map.put(host, true);
			} catch (Exception e) {
				logger.warn("Cannot get map from hz : {}", e.getMessage());
				logger.warn(e, e);
			}
		}

		return result;
	}

	private Map<String, Boolean> getSMPPStatus(CommonsPool2TargetSource pool) {

		Map<String, Boolean> result = new HashMap<String, Boolean>();
		SmppConnector smppConnector = null;

		try {
			if (pool != null) {
				
				String targetBeanName = pool.getTargetBeanName();

				if ((properties.getEmg().getSmpp().getBindMode()).equals(EmgProperties.BIND_SYNC)) {
					if (StringUtils.isNotBlank(targetBeanName) && targetBeanName.startsWith("tx")) {
						smppConnector = (Transmitter) pool.getTarget();
					} else if (StringUtils.isNotBlank(targetBeanName) && targetBeanName.startsWith("rx")) {
						smppConnector = (Receiver) pool.getTarget();
					}
				} else {
					if (StringUtils.isNotBlank(targetBeanName) && targetBeanName.startsWith("tx")) {
						smppConnector = (AsyncTransmitter) pool.getTarget();
					} else if (StringUtils.isNotBlank(targetBeanName) && targetBeanName.startsWith("rx")) {
						smppConnector = (AsyncReceiver) pool.getTarget();
					}
				}
				result.put(pool.getTargetBeanName(), smppConnector.isConnected());
				pool.releaseTarget(smppConnector);
				// logger.debug("[{}] Max pool size = {}, {} active, {} idle", pool.getTargetBeanName(), pool.getMaxSize(), pool.getActiveCount(), pool.getIdleCount());
				// logger.debug("getActiveCount {} , getIdleCount {} , getMaxIdle {} , getMaxSize {} , getMinIdle {}", pool.getActiveCount(), pool.getIdleCount(), pool.getMaxIdle(), pool.getMaxSize(), pool.getMinIdle());
			}
		} catch (Exception e) {
			logger.warn("[SMPP] SMPP connetion for {}} is abnormal, due to {}", pool.getTargetBeanName(),
					e.getMessage());
			logger.warn(e, e);
			result.put(pool.getTargetBeanName(), false);
		}

		return result;
	}

	private boolean checkSMPPBinding(Map<String, Boolean>... binding) {

		boolean result = false;

		for (Map<String, Boolean> map : binding) {
			for (String key : map.keySet()) {
				if (map.get(key)) {
					logger.debug("SMPP binding : [{}]  status : [{}]", key, map.get(key));
				} else {
					logger.warn("[SMPP] binding : [{}] failed!!", key);
				}
				result |= map.get(key);
			}
		}
		return result;
	}

	@PostConstruct
	public void init() {

		if (!canProcess()) {
			jmsService.stopListener("multiSmsMTListener");
		}

		initPool();

		try {
			checkProcess.schedule(new TimerTask() {
				@Override
				public void run() {
					if (checkSMPPBinding(getSMPPStatus(txPsaPool), getSMPPStatus(txNormalPool), getSMPPStatus(rxPsaPool), getSMPPStatus(rxNormalPool))) {
						failCount = 0;
						jmsService.startListener();
					}
				}
			}, 0, properties.getDal().getJms().getCheckTime() * 1000);
		} catch (Exception e) {
			logger.warn("checkProcess timer error, due to [{}]", e.getMessage());
		}
	}

	public void initPool() {
		fillPool(txNormalPool);
		fillPool(txPsaPool);
		fillPool(rxNormalPool);
		fillPool(rxPsaPool);
	}

	public void fillPool(CommonsPool2TargetSource pool) {

		if (pool != null) {

			try {
				for (int i = 1; i <= (pool.getMaxSize() - (pool.getIdleCount() + pool.getActiveCount())); i++) {
					pool.activateObject(pool.makeObject());
				}
			} catch (Exception e) {
				logger.warn("Create {} fail, due to [{}]", pool.getTargetBeanName(), e.getMessage());
			}
		}
	}

	@PreDestroy
	private void distroy() {
		checkProcess.cancel();
	}

	@JmsListener(destination = "${dal.jms.mtQueueName}", id = "multiSmsMTListener")
	public void receiveMessage(final Message message) throws JMSException {
		logger.debug("==receive MT Message==");
		ObjectMessage objectMessage = (ObjectMessage) message;
		MessageObject msg = (MessageObject) objectMessage.getObject();

		boolean isPsa = false;
		Transmitter transmitter = null;
		// 2018-04-04 for retry
		// List<SubmitSM> retrySmsReqList = new ArrayList<SubmitSM>();
		List<Map<String, String>> retrySeqNumList = new ArrayList<Map<String, String>>();
		if (msg.getRetrySeqNumList() != null && msg.getRetrySeqNumList().size() > 0) {
			retrySeqNumList = msg.getRetrySeqNumList();
		}
		//
		// send message to SMSC and handle logic in TxHandler
		try {
			msg.setSystemProcessTime(System.currentTimeMillis() - msg.getSystemProcessTime());
			logger.debug("[MT] Receive msg : [{}]", msg);

			// change setting for SMSC
			msg.setSourceTON(properties.getEmg().getSmpp().getDefaultSourceTon());
			msg.setSourceNPI(properties.getEmg().getSmpp().getDefaultSourceNpi());
			msg.setDestinationTON(properties.getEmg().getSmpp().getDefaultDestinationTon());
			msg.setDestinationNPI(properties.getEmg().getSmpp().getDefaultDestinationNpi());

			// msg.setSeq(Integer.valueOf(msg.getWsMessageId()));
			isPsa = msg.isPsa();

			msg.setSmscProcessTime(System.currentTimeMillis());
			if (isPsa) {
				transmitter = getTransmitter(txPsaPool);
			} else {
				transmitter = getTransmitter(txNormalPool);
			}
			// transmitter.sendSMS(parser.messageObjectToSmsRequest(msg));

			// XXX 2017-11-30 modify by matthew
			List<SmsRecordSub> smsSubList = new ArrayList<SmsRecordSub>();
			List<SubmitSM> smsReqList = new ArrayList<SubmitSM>();
			// generate SmsRecordSub list && SubmitSM list
			Map<String, String> wsgIdMap = msg.getWsMessageIdMap();
			Map<String, Date> dateMap = msg.getCreateDateMap();
			List<SmsRequest> requestList = parser.messageObjectToSmsRequestList(msg);
			if (msg.getCpId().equals("ALARM")) { // for alarm use
				int seqNumber = 1;
				logger.debug("===== ALARM SMS START =====");
				for (SmsRequest request : requestList) {
					logger.info("ALARM SMS da:[{}], oa:[{}], message:[{}]", request.getDestination(),
							request.getSource(), request.getMessage());
					List<SubmitSM> smList = transmitter.getSMSList(request, msg.isLongType());
					logger.info("ALARM smList size:[{}]", smList.size());
					for (SubmitSM sm : smList) {
						sm.setSequenceNum(seqNumber);
						// sm.setRegistered(false);
						sm.setRegistered(0);
						smsReqList.add(sm);
						// retrySeqNumList.add(seqNumber);
						Map<String, String> retryMap = new HashMap<String, String>();
						retryMap.put("subId", String.valueOf(seqNumber));
						retryMap.put("boolean", "true");
						retrySeqNumList.add(retryMap);
						seqNumber++;

					}
				}
				logger.debug("ALARM smsReqList size:[{}]", smsReqList.size());
				logger.debug("===== ALARM SMS END =====");
			} else if (msg.getCpId().equals("RETRY") && retrySeqNumList != null && retrySeqNumList.size() > 0) {
				// int seqNumber = 1;
				logger.debug("===== RETRY SMS START =====");
				
				/*for(SmsRequest request:requestList){
					logger.debug("RETRY SMS da:[{}], oa:[{}], message:[{}]",request.getDestination(),request.getSource(),request.getMessage());
					List<SubmitSM> smList = transmitter.getSMSList(request, msg.isLongType());
					for(SubmitSM sm:smList){
						sm.setSequenceNum(seqNumber);
						sm.setExpiryTime(parser.getExpireyTime(msg.getValidity()));
						// if language was english, then change dataCoding back to 0 (smsc default)
						if(msg.getDataCoding() < 8) {
							sm.setDataCoding(properties.getEmg().getSmpp().getDcsASCII());
						}
						smsReqList.add(sm);
						seqNumber++;
					}
				}*/
				logger.debug("Retry List size:[{}]", retrySeqNumList.size());
				int count = 0;
				for (SmsRequest request : requestList) {
					logger.debug("RETRY SMS da:[{}], oa:[{}], message:[{}]", request.getDestination(),
							request.getSource(), request.getMessage());
					List<SubmitSM> smList = transmitter.getSMSList(request, msg.isLongType());
					int index = 0;
					for (SubmitSM sm : smList) {
						Map<String, String> retryMap = retrySeqNumList.get(index);
						if ("true".equals(retryMap.get("boolean"))) {
							sm.setSequenceNum(Integer.parseInt(retryMap.get("subId")));
							sm.setExpiryTime(parser.getExpireyTime(msg.getValidity()));
							// if language was english, then change dataCoding back to 0 (smsc default)
							if (msg.getDataCoding() < 8) {
								sm.setDataCoding(properties.getEmg().getSmpp().getDcsASCII());
							}
							smsReqList.add(sm);
						}
						index++;
					}
				}
				logger.debug("===== RETRY SMS END =====");
			} else {
				try {
					for (SmsRequest request : requestList) {
						List<SubmitSM> smList = transmitter.getSMSList(request, msg.isLongType());
						String wsMsgId = wsgIdMap.get(request.getDestination());
						Date insertDate = dateMap.get(request.getDestination());
						int count = 1;
						for (SubmitSM sm : smList) {
							Map<String, String> retryMap = new HashMap<String, String>();
							SmsRecordSub sub = new SmsRecordSub();
							long subId = smsRecordManager.getSubId();
							logger.debug("==== receiveMessage seq_subId:[{}]", subId);
							sub.setSubId(HttpApiUtils.formatDate("yyyyMMdd", new Date()) + subId);
							logger.debug("==== receiveMessage date+seq_subId:[{}]",
									HttpApiUtils.formatDate("yyyyMMdd", new Date()) + subId);
							sub.setSubmitDate(new Date());
							sub.setWsMsgId(wsMsgId);
							sub.setSegNum(String.valueOf(count));
							sub.setCreateDate(insertDate);
							smsSubList.add(sub);

							sm.setSequenceNum(Math.toIntExact(subId));
							sm.setExpiryTime(parser.getExpireyTime(msg.getValidity()));
							// if language was english, then change dataCoding back to 0 (smsc default)
							if (msg.getDataCoding() < 8) {
								sm.setDataCoding(properties.getEmg().getSmpp().getDcsASCII());
							}
							smsReqList.add(sm);
							retryMap.put("subId", String.valueOf(subId));
							retryMap.put("boolean", "true");
							retrySeqNumList.add(retryMap);
							count++;
						}
					}
					// insert sub table
					if (msg.getCpId().equals("RETRYDB")) { // for retry
						logger.info("[RETRYDB] start");
						DetachedCriteria dc = DetachedCriteria.forClass(SmsRecordSub.class);
						for (SmsRecordSub sub : smsSubList) { // for delete
							dc.add(Restrictions.eq("wsMsgId", sub.getWsMsgId()));
							List<SmsRecordSub> list = smsRecordManager.findByCriteria(SmsRecordSub.class, dc);
							logger.info("[RETRYDB] delete list size:[{}]", list.size());
							for (SmsRecordSub delSub : list) {
								try {
									logger.info("[RETRYDB] delete sms_record_sub data, SubId:[{}],WsMsgId:[{}]",
											delSub.getSubId(), delSub.getWsMsgId());
									smsRecordManager.delete(SmsRecordSub.class, delSub.getSubId());
								} catch (Exception e) {

								}
							}
						}
						smsRecordManager.subBatchSave(smsSubList);
					} else { // for normal
						smsRecordManager.subBatchSave(smsSubList);
					}

				} catch (Exception e) {
					if (failCount > 3) {
						jmsService.stopListener("multiSmsMTListener");
						logger.info("*** MultiSms Stop Listener ***");
					}
					failCount++;
					logger.error("[DB] Sending message fail due to {}", e.getMessage());
					logger.warn(e, e);

					// Enqueue back to MT queue
					if (!"ALARM".equals(msg.getCpId())) {
						msg.setCpId("RETRYDB");
					}
					if (msg.getMessage() != null && !"MSP-".equals(msg.getMessage())) {
						// jmsService.sendDelayMsg(msg,
						// properties.getDal().getJms().getMtQueueName(),properties.getApi().getRetry().getDelay(),
						// 4);
						jmsService.sendDelayMessage(msg, properties.getDal().getJms().getMtQueueName(),
								properties.getApi().getRetry().getDelay(), 4);
						logger.info("[RETRY] Enqueue data back to queue, detail (RETRYDB): [{}]", msg);
					}

				}
			}

			// send to smsc
			logger.debug("Send to SMSC List size:[{}]", smsReqList.size());
			int index = 0;
			for (SubmitSM sm : smsReqList) {
				transmitter.send(sm);
				Map<String, String> retryMap = retrySeqNumList.get(index);
				retryMap.put("boolean", "false");
				index++;
			}
			// end modify
			
			
			//XXX modify 2017-11-17
//			List<SubmitSM> smsReqList = transmitter.getSMSList(parser.messageObjectToSmsRequest(msg));
//			int count = 1;
//			for(SubmitSM sm:smsReqList){
//				SmsRecordSub sub = new SmsRecordSub();
//				long subId = smsRecordManager.getSubId();
//				logger.debug("==== receiveMessage subId:[{}]", subId);
//				sub.setSubId(subId);
//				sub.setSubmitDate(new Date());
//				sub.setWsMsgId(msg.getWsMessageId());
//				sub.setSegNum(String.valueOf(count));
//				smsRecordManager.save(SmsRecordSub.class, sub);
//				sm.setSequenceNum(Math.toIntExact(subId));
//				transmitter.send(sm);
//				count++;
//			}
			//
			msg.setSmscProcessTime(System.currentTimeMillis() - msg.getSmscProcessTime());
		} catch (Exception e) {
			if (failCount > 3) {
				jmsService.stopListener("multiSmsMTListener");
				logger.info("*** MultiSms Stop Listener ***");
			}
			failCount++;
			logger.error("[SMPP] Sending message fail due to {}", e.getMessage());
			logger.warn(e, e);

			// Enqueue back to MT queue
			logger.debug("[RETRY] Retry List size:[{}],failCount:[{}]", retrySeqNumList.size(), failCount);
			if (!"ALARM".equals(msg.getCpId())) {
				msg.setCpId("RETRY");
			}
			msg.setRetrySeqNumList(retrySeqNumList);
			// jmsService.sendDelayMsg(msg, properties.getDal().getJms().getMtQueueName(),
			// properties.getApi().getRetry().getDelay(), 4);
			jmsService.sendDelayMessage(msg, properties.getDal().getJms().getMtQueueName(),
					properties.getApi().getRetry().getDelay(), 4);
			logger.info("[RETRY] Enqueue data back to queue, detail : [{}]", msg);
		}

		// release resource back to queue
		if (isPsa) {
			releaseTarget(txPsaPool, transmitter);
		} else {
			releaseTarget(txNormalPool, transmitter);
		}

		logger.debug("[MT] Sending message asynchronigzed ... Done. Detail : [{}]", msg);
	}

	private Transmitter getTransmitter(CommonsPool2TargetSource pool) throws Exception {

		Transmitter result = null;
		// YC marked at 20230921
		// boolean makeObj = false;

		// if (pool.getMinIdle() == 0) {
		// makeObj = true;
		// }

		// if (makeObj) {
		// result = (Transmitter) pool.makeObject();
		// } else {
		// result = (Transmitter) pool.getTarget();
		// }

		result = (Transmitter) pool.getTarget();

		return result;
	}

	private void releaseTarget(CommonsPool2TargetSource pool, Transmitter transmitter) {
		try {
			pool.releaseTarget(transmitter);
		} catch (Exception e) {
			logger.error("[SMPP] Release source to [{}] fail, due to {}", pool, e.getMessage());
			logger.warn(e, e);
			transmitter.unbindAndDisconnect();
		}
	}

	@Scheduled(fixedDelay = 30000)
	public void retryUpdateRx() {
		logger.debug("==== retryUpdateRx() start ");
		// List<QueueObject> qObjList = null;
		int queueSize = 0;
		QueueObject qObjTemp = null;
		Map<String, String> result = null;
		boolean enqueue = false;
		MessageObject msg = null;
		int cpZone = 999;
		int retryCount = 0;
		// try{
		queueSize = retryRxQueue.getQueueSzie();
		logger.info("[RETRY_Queue] Queue size :[{}] ", queueSize);
		for (int i = 0; i < queueSize; i++) {
			try {
				qObjTemp = retryRxQueue.getQueue();
				logger.debug("==== retryUpdateRx() Queue :[{}] ", qObjTemp.toString());
				if ("MT".equals(qObjTemp.getType())) {
					// modify by matthew 2018-10-11
					// retry count < properties.getEmg().getRetryDrCount() , default 1000
					retryCount = qObjTemp.getRetryCount();
					if (retryCount < properties.getEmg().getRetryDrCount()) {
						logger.info("[RETRY_DR] smscMsgId:[{}],deliverStatus:[{}]", qObjTemp.getSmscId(),
								qObjTemp.getDeliverStatus());
						result = smsRecordManager.updateDrSmsRecord(qObjTemp.getSmscId(), qObjTemp.getDeliverStatus());
						if (result != null && result.get("resultCode").equals("0")) {
							msg = qObjTemp.getMsg();
							msg.setCpId(result.get("cpId"));
							// msg.setWsMessageId(result.get("wsMsgId"));
							cpZone = Integer.valueOf(result.get("cpZone"));

							if (isIBMMq(cpZone)) {
								msg.setWsMessageId(result.get("reqMsgId"));
							} else {
								msg.setWsMessageId(result.get("wsMsgId"));
							}

							if (result.get("drFlag") != null && result.get("drFlag").equals("1")) {
								enqueue = true;
							}
							logger.info("[RETRY_DR] is done. SMSC_MSG_ID:[{}] , DeliverStatus:[{}]",
									qObjTemp.getSmscId(), qObjTemp.getDeliverStatus());
						} else {
							// retry count + 1
							qObjTemp.setRetryCount((retryCount + 1));
							retryRxQueue.putQueue(qObjTemp);
							logger.warn(
									"[RETRY_DR] result is null,update fail,put back to RxQueue, SMSC_MSG_ID:[{}], DeliverStatus:[{}]",
									qObjTemp.getSmscId(), qObjTemp.getDeliverStatus());

							// if(result!=null && result.get("errorMsg").equals("-1422")){
							// logger.warn("[RETRY_DR] DB errorMsg:[{}],not put back to RxQueue .
							// SMSC_MSG_ID:[{}] , DeliverStatus:[{}]"
							// ,result.get("errorMsg"), qObjTemp.getSmscId(), qObjTemp.getDeliverStatus());
							// }
							// else{
							// retryRxQueue.putQueue(qObjTemp);
							// logger.warn("[RETRY_DR] result is null,update fail,put back to RxQueue,
							// SMSC_MSG_ID:[{}], DeliverStatus:[{}]"
							// , qObjTemp.getSmscId(), qObjTemp.getDeliverStatus());
							// }
						}
					} else { // retry count > properties.getEmg().getRetryDrCount() , default 1000
						logger.info("[REJECT_DR] smscMsgId:[{}],deliverStatus:[{}],retry count :[{}] ",
								qObjTemp.getSmscId(), qObjTemp.getDeliverStatus(), retryCount);
					}
				} else { // MO RETRY
					msg = qObjTemp.getMsg();
					logger.info("[RETRY_MO] Language:[{}],Text:[{}],msg:[{}]", qObjTemp.getLanguage(),
							qObjTemp.getText(), msg);
					result = smsRecordManager.saveMoSmsRecord(msg.getSource(), msg.getDestination(),
							msg.getStatus(), qObjTemp.getLanguage(), qObjTemp.getText(), msg.getSourceTON(),
							msg.getSourceNPI(),
							msg.getDestinationTON(), msg.getDestinationNPI(), msg.getEsmClass(),
							msg.getSmscMessageId() != null ? msg.getSmscMessageId() : "");

					if (result != null) {
						try {
							cpZone = Integer.valueOf(result.get("cpZone"));
							msg.setCpId(result.get("cpId"));
							// msg.setWsMessageId(result.get("wsMsgId"));
							if (isIBMMq(cpZone)) {
								msg.setWsMessageId(result.get("reqMsgId"));
							} else {
								msg.setWsMessageId(result.get("wsMsgId"));
							}
							enqueue = true;
						} catch (Exception e) {
							logger.warn("Parse cp map error");
						}
					} else {
						retryRxQueue.putQueue(qObjTemp);
						logger.warn(
								"[RETRY_MO] result is null,insert fail,put back to RxQueue, Language:[{}],Text:[{}],msg:[{}]",
								qObjTemp.getLanguage(), qObjTemp.getText(), msg);
					}
				}

				if (enqueue) {
					String queueName = "";

					switch (cpZone) {
						case ContentProvider.CP_ZONE_INTRA:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIntraQueueName();
							} else {
								queueName = properties.getDal().getJms().getMoIntraQueueName();
							}
							break;
						case ContentProvider.CP_ZONE_INTER:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrInterQueueName();
							} else {
								queueName = properties.getDal().getJms().getMoInterQueueName();
							}
							break;
						case ContentProvider.CP_ZONE_QM1:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM1();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM1();
							}
							break;
						case ContentProvider.CP_ZONE_QM2:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM2();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM2();
							}
							break;
						case ContentProvider.CP_ZONE_QM3:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM3();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM3();
							}
							break;
						case ContentProvider.CP_ZONE_QM4:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM4();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM4();
							}
							break;
						case ContentProvider.CP_ZONE_QM5:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM5();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM5();
							}
							break;
						case ContentProvider.CP_ZONE_QM6:
							if (msg.getEsmClass() == EmgParser.TYPE_DR) {
								queueName = properties.getDal().getJms().getDrIbmQueueNameQM6();
							} else {
								queueName = properties.getDal().getJms().getMoIbmQueueNameQM6();
							}
							break;
						default:
							logger.error(
									"[RETRY_Queue] Cannot find which queue need to input, sysId : [{}], msg detail : [{}]",
									msg.getCpId(), msg.toString());
							break;
					}

					if (StringUtils.isNotBlank(queueName)) {
						try {
							// jmsService.sendMsg(msg, queueName, 4);
							jmsService.sendMessage(msg, 4, queueName);
							logger.info("[RETRY_Queue] Enqueue to {} success, msg : [{}]", queueName, msg.toString());
						} catch (Exception e) {
							logger.error("[RETRY_Queue] Enqueue fail, msg : [{}]", msg.toString());
							logger.error(e, e);
						}
					}
				}
			} catch (Exception e) {
				logger.error("[RETRY_Queue] Error ,message:[{}]", e.getMessage());
				logger.error(e, e);
				if (qObjTemp != null) {
					// retry count + 1
					qObjTemp.setRetryCount((retryCount + 1));
					retryRxQueue.putQueue(qObjTemp);
				}
				logger.warn("[RETRY_Queue] Error , put back to RxQueue ,QueueObject:[{}]", qObjTemp.toString());
			}
		}
		// }catch(Exception e){
		// if(qObjTemp!=null){
		// retryRxQueue.putQueue(qObjTemp);
		// }
		// logger.error("[RETRY_DR] Error , put back to RxQueue ,QueueObject:[{}]",
		// qObjTemp.toString());
		// }
	}

	private boolean isIBMMq(Integer cpZone) {

		boolean result = false;

		switch (cpZone) {
			case ContentProvider.CP_ZONE_QM1:
			case ContentProvider.CP_ZONE_QM2:
			case ContentProvider.CP_ZONE_QM3:
			case ContentProvider.CP_ZONE_QM4:
			case ContentProvider.CP_ZONE_QM5:
			case ContentProvider.CP_ZONE_QM6:
				result = true;
				break;
			default:
				result = false;
				break;
		}

		logger.debug("cp zone {} ; isIBMMQ : {}", cpZone, result);

		return result;
	}
}
