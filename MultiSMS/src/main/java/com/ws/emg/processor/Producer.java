package com.ws.emg.processor;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.api.util.HttpApiUtils;
import com.ws.emg.constant.SmppConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.emg.smpp.retry.QueueObject;
import com.ws.emg.smpp.retry.RetryRxQueue;
import com.ws.emg.util.EmgParser;
import com.ws.jms.service.JmsService;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.SmsRecordManager;

import lombok.Setter;

@Component("emgProducer")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Producer implements Runnable {

	private static Logger logger = LogManager.getLogger(Producer.class);
	// private static Logger SLOG = LogManager.getLogger("com.ws.emg.Producer");

	@Setter
	private MessageObject msg = null;

	@Autowired
	MspProperties properties;

	@Autowired
	JmsService jmsService;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Autowired
	ContentProviderManager contentProviderManager;

	@Autowired
	RetryRxQueue retryRxQueue;

	@Override
	public void run() {

		boolean enqueue = false;
		String sysId = "";
		int cpZone = ContentProvider.CP_ZONE_UNKNOWN;
		long startTime = 0;
		long processTime = 0;
		String type = "DR";

		String smscMsgId = msg.getSmscMessageId();
		String deliverStatus = "";

		// convert Hex to Decimal
		try {
			if (SmppConstant.POSITIONAL_NOTATION_HEX == properties.getEmg().getSmpp().getRx().getPositionalNotation()) {
				smscMsgId = String.valueOf(Integer.parseInt(smscMsgId, 16));
			}
		} catch (NumberFormatException e) {
			// ignore
		}

		smscMsgId = StringUtils.leftPad(smscMsgId, properties.getEmg().getSmpp().getSmscIdMaxLength(), "0");

		if (msg.getEsmClass() == EmgParser.TYPE_DR) {// case DR

			// try {
			// Thread.sleep(3000);
			// } catch (Exception e) {
			// // ignore
			// }

			deliverStatus = msg.getState() + ";" + msg.getErrorCode();
			logger.debug("[DR] Set value from smsc [state;errCode] = [{}]", deliverStatus);

			startTime = System.currentTimeMillis();
			Map<String, String> result = null;
			try {
				result = smsRecordManager.updateDrSmsRecord(smscMsgId, deliverStatus);
			} catch (Exception e) {
				logger.info("updateDrSmsRecord failed,throw exception. result:[{}], smscMsgId:[{}]", result, smscMsgId);
			}
			processTime = System.currentTimeMillis() - startTime;
			if (result != null && result.get("resultCode").equals("0")) {
				logger.debug("[DR] update success, ws msg id : [{}]", result.get("wsMsgId"));
				try {
					cpZone = Integer.valueOf(result.get("cpZone"));
				} catch (Exception e) {
					logger.warn("[DR] Parse cpZone error, smsc msg id : [{}]", smscMsgId);
				}
				sysId = result.get("cpId");
				msg.setCpId(sysId);

				if (isIBMMq(cpZone)) {
					msg.setWsMessageId(result.get("reqMsgId"));
				} else {
					msg.setWsMessageId(result.get("wsMsgId"));
				}

				logger.debug("[DR] drFlag : [{}]", result.get("drFlag"));

				if (result.get("drFlag") != null && result.get("drFlag").equals("1")) {
					enqueue = true;
				}
			} else {
				// logger.warn("[SMPP] update fail, due to result map null ,SMSC_MSG_ID:[{}]
				// Status:[{}]",msg.getSmscMessageId(), msg.getState());
				QueueObject queue = new QueueObject();
				queue.setSmscId(smscMsgId);
				queue.setDeliverStatus(deliverStatus);
				queue.setMsg(msg);
				queue.setType("MT");
				retryRxQueue.putQueue(queue);
				logger.warn("[DR] put Rx in RxQueue retry updateDrSmsRecord,SMSC_MSG_ID:[{}] Status:[{}]",
						msg.getSmscMessageId(), msg.getState());
			}

		} else {// case MO

			Date submitTime = new Date();

			msg.setSubmitTime(HttpApiUtils.getTimestampForXml(submitTime));
			msg.setSmscMessageId(String.valueOf(msg.getSeq()));

			logger.info("[MO] Receive msg detail : [{}]", msg);

			type = "MO";
			String language = "U";
			String coding = "utf8";
			switch (msg.getDataCoding()) {
			case 0:
				language = "E";
				coding = "ISO-8859-1";
				break;
			case 1:
				language = "E";
				coding = "ISO-8859-1";
				break;
			case 3:
				language = "E";
				coding = "ISO-8859-1";
				break;
			case 8:
				language = "U";
				coding = "utf8";
				break;
			default:
				break;
			}

			// added by YC 2018-04-10
			String text = msg.getMessage();
			try {
				text = HttpApiUtils.base64Encoded(text, coding);
				msg.setMessage(text);
			} catch (Exception e) {

			}

			startTime = System.currentTimeMillis();
			Map<String, String> result = null;
			try {
				result = smsRecordManager.saveMoSmsRecord(msg.getSource(), msg.getDestination(), msg.getStatus(),
						language, text, msg.getSourceTON(), msg.getSourceNPI(), msg.getDestinationTON(),
						msg.getDestinationNPI(), msg.getEsmClass(),
						msg.getSmscMessageId() != null ? msg.getSmscMessageId() : "");

			} catch (Exception e) {
				logger.info("saveMoSmsRecord failed,throw exception. result:[{}]", result);
			}

			// Map<String, String> result =
			// smsRecordManager.saveMoSmsRecord(msg.getSource(), msg.getDestination(),
			// msg.getStatus(), language, msg.getMessage(), msg.getSourceTON(),
			// msg.getSourceNPI(),
			// msg.getDestinationTON(), msg.getDestinationNPI(), msg.getEsmClass(),
			// msg.getSmscMessageId() != null ? msg.getSmscMessageId() : "");
			processTime = System.currentTimeMillis() - startTime;

			if (result != null) {
				logger.debug("[MO] Insert success, msg id : [{}]", msg.getSmscMessageId());
				try {
					cpZone = Integer.valueOf(result.get("cpZone"));
					sysId = result.get("cpId");
					msg.setCpId(sysId);
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
				/*
				QueueObject queue = new QueueObject();
				queue.setText(text);
				queue.setLanguage(language);
				queue.setMsg(msg);
				queue.setType("MO");
				retryRxQueue.putQueue(queue);
				logger.warn("[MO] put Rx in RxQueue retry saveMoSmsRecord, msg:[{}]", msg);
				*/
				enqueue = false;
				logger.warn("[MO] Get CP Information error and give it up, detail : [{}]", msg);
			}
		}

		msg.setSystemProcessTime(processTime);

		if ("true".equals(properties.getEmg().getTest_mo_sw())) {
			enqueue = false;
		}

		// enqueue
		if (enqueue) {
			
			String queueName = getQueueName(cpZone, msg.getEsmClass());

			if (StringUtils.isNotBlank(queueName)) {
				try {
					// jmsService.sendMsg(msg, queueName, 4);
					jmsService.sendMessage(msg, 4, queueName);
					logger.debug("[Producer] Enqueue to {} success, msg : [{}]", queueName, msg.toString());
				} catch (Exception e) {
					logger.error("[WISEMQ] Enqueue fail, msg : [{}]", msg.toString());
					logger.warn(e, e);
				}
			}
		} else {
			logger.debug("[Producer] wsMsgId {} not enqueue!!", msg.getWsMessageId());
		}

		logger.info("[Producer] Job [{}] finished !! euqueue : [{}] detail [{}]", type, enqueue, msg);
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

	public String getQueueName(int cpZone, int esmClass) {

		String queueName = "";

		switch (cpZone) {
		case ContentProvider.CP_ZONE_INTRA:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIntraQueueName();
			} else {
				queueName = properties.getDal().getJms().getMoIntraQueueName();
			}
			break;
		case ContentProvider.CP_ZONE_INTER:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrInterQueueName();
			} else {
				queueName = properties.getDal().getJms().getMoInterQueueName();
			}
			break;
		case ContentProvider.CP_ZONE_QM1:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM1();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM1();
			}
			break;
		case ContentProvider.CP_ZONE_QM2:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM2();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM2();
			}
			break;
		case ContentProvider.CP_ZONE_QM3:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM3();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM3();
			}
			break;
		case ContentProvider.CP_ZONE_QM4:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM4();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM4();
			}
			break;
		case ContentProvider.CP_ZONE_QM5:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM5();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM5();
			}
			break;
		case ContentProvider.CP_ZONE_QM6:
			if (esmClass == EmgParser.TYPE_DR) {
				queueName = properties.getDal().getJms().getDrIbmQueueNameQM6();
			} else {
				queueName = properties.getDal().getJms().getMoIbmQueueNameQM6();
			}
			break;
		default:
			logger.error("[WISEMQ] Cannot find which queue need to input, msg detail : [{}]", msg.toString());
			break;
		}

		logger.debug("[Producer] cpZone:[{}] , queueName:[{}]", cpZone, queueName);

		return queueName;
	}

}
