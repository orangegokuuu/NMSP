package com.ws.emg.processor;

import java.util.Date;

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
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.SmsRecordManager;

import lombok.Setter;

@Component("emgConsumer")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Consumer implements Runnable {

	private static Logger logger = LogManager.getLogger(Consumer.class);
	// private static Logger SLOG = LogManager.getLogger("com.ws.emg.Consumer");

	@Setter
	private MessageObject msg = null;

	@Autowired
	MspProperties properties;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Override
	public void run() {

		// Update Database
		// boolean update = false;
		// Date processDate = new Date();
		// String wsMsgId = String.valueOf(msg.getSeq());
		//
		// SmsRecord sms = null;
		// SmsRecordSub sub = null;
		//
		// DetachedCriteria dcSMS = DetachedCriteria.forClass(SmsRecord.class);
		// dcSMS.add(Restrictions.eq("wsMsgId", wsMsgId));
		// List<SmsRecord> smsList = smsRecordManager.findByCriteria(SmsRecord.class,
		// dcSMS);
		//
		// if (smsList != null && smsList.size() > 0) {
		// sms = smsList.get(0);
		// sms.setWsMsgId(wsMsgId);
		// sms.setUpdateDate(processDate);
		// sms.setUpdateBy("SYS");
		//
		// // setting sub table information
		// sub = new SmsRecordSub();
		// sub.setWsMsgId(wsMsgId);
		// sub.setSmscMsgId(msg.getSmscMessageId());
		// sub.setSubmitStatus(msg.getStatus());
		// sub.setSubmitDate(processDate);
		//
		// update = true;
		// } else {
		// logger.warn("[Consumer] Cannot query SMS_RECORD from wsMsgId : [{}]",
		// wsMsgId);
		// }
		//
		// if (update) {
		// try {
		// smsRecordManager.save(SmsRecordSub.class, sub);
		// smsRecordManager.saveOrUpdate(SmsRecord.class, sms);
		// } catch (Exception e) {
		// logger.error("[Consumer] update Database got [{}] error", e.getMessage());
		// logger.error(e, e);
		// }
		// }

		// String wsMsgId = String.valueOf(msg.getSeq());
		// String smscMsgId = "";
		// if (msg != null && msg.getSmscMessageId() != null) {
		// smscMsgId = msg.getSmscMessageId();
		// }
		// logger.debug("==== Consumer.run() WS_MSG_ID:[{}], SMSC_MSG_ID:[{}]", wsMsgId,
		// smscMsgId);
		// String result = smsRecordManager.saveSmsRecordSub(wsMsgId, smscMsgId,
		// msg.getStatus());
		// logger.debug("==== Consumer.run() WS_MSG_ID:[{}], SMSC_MSG_ID:[{}], result
		// :[{}]", wsMsgId, smscMsgId, result);
		// if (!result.equals("0")) {
		// logger.error("[Consumer] update Database got [{}] error", result);
		// }

		// long subId = 0;
		String subId = "";
		if (msg != null && msg.getSeq() != 0) {
			subId = HttpApiUtils.formatDate("yyyyMMdd", new Date()) + msg.getSeq();
			logger.debug("==== Consumer.run() subId:[{}]", subId);
		}
		SmsRecordSub sub = smsRecordManager.get(SmsRecordSub.class, subId);
		String smscMsgId = msg.getSmscMessageId();
		// convert Hex to Decimal
		try {
			if (SmppConstant.POSITIONAL_NOTATION_HEX == properties.getEmg().getSmpp().getTx()
					.getPositionalNotation()) {
				smscMsgId = String.valueOf(Integer.parseInt(smscMsgId, 16));
			}
		} catch (NumberFormatException e) {
			// ignore
			logger.warn("convert smsc_msg_id fail. smscMsgId:[{}]", smscMsgId);
		}
		smscMsgId = StringUtils.leftPad(smscMsgId, properties.getEmg().getSmpp().getSmscIdMaxLength(), "0");
		
		if (sub != null && !sub.getSubId().equals("")) {
			logger.debug("==== Consumer.run() get sub:[{}]", sub.toString());
			
			sub.setSmscMsgId(smscMsgId);
			sub.setSubmitStatus(msg.getStatus());
			try{
				smsRecordManager.update(SmsRecordSub.class, sub);
			}catch(Exception e){
				//retry				
				SmsRecordSub retrySub = smsRecordManager.get(SmsRecordSub.class, subId);
				int retry = 0;
				int retryCount = properties.getEmg().getRetryCount();
				int retryTime = properties.getEmg().getRetryTime();
				while(true){
					retry++;
					logger.warn("[TX] update sms_record_sub error,retry update sleep {} ms ,retry [{}] times. smscMsgId:[{}] ,SubmitStatus:[{}]",retryTime,retry ,smscMsgId,msg.getStatus());
					try {
						Thread.sleep(retryTime);
						retrySub.setSmscMsgId(smscMsgId);
						retrySub.setSubmitStatus(msg.getStatus());
						smsRecordManager.update(SmsRecordSub.class, retrySub);
					} catch (InterruptedException e1) {}
					retrySub = smsRecordManager.get(SmsRecordSub.class, subId);
					if(retrySub.getSmscMsgId()!=null && !"".equals(retrySub.getSmscMsgId())){
						break;
					}
					if(retryCount != -1 && retry > retryCount){
						logger.info("[TX] retry update sms_record_sub done. retry [{}] times. retryCount:[{}] , smscMsgId:[{}] ,SubmitStatus:[{}]", retry, retryCount,smscMsgId,msg.getStatus());
						break;
					}
					
				}
			}
			logger.debug("==== Consumer.run() update sub:[{}]", sub.toString());
		}
		else{
			logger.warn("[TX] not find sms_record_sub. sub_id:[{}] smscMsgId:[{}] ,SubmitStatus:[{}]" ,subId,smscMsgId,msg.getStatus());
//			SmsRecordSub insertSub = new SmsRecordSub();
//			insertSub.setSubId(subId);
//			insertSub.setSmscMsgId(smscMsgId);
//			insertSub.setSubmitStatus(msg.getStatus());
//			insertSub.setCreateDate(new Date());
//			smsRecordManager.save(SmsRecordSub.class, insertSub);
		}
	}
}
