package com.ws.emg.smpp.handler;

import javax.inject.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

import com.ws.emg.listener.DeQueueListener;
import com.ws.emg.processor.Consumer;
import com.ws.emg.util.EmgParser;
import com.ws.smpp.TransmitterEventHandler;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSMResp;

@Component("txHandler")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TxHandler extends TransmitterEventHandler {

	private static Logger logger = LogManager.getLogger(TxHandler.class);

	@Autowired
	private Provider<Consumer> workerProvider;

	@Autowired
	private EmgParser parser;

	@Autowired
	@Qualifier("consumerExcutor")
	private TaskExecutor taskExecutor;
	
	// @Autowired
	// @Qualifier("emgDeQueueListener")
	// DeQueueListener deQueue;

	@Override
	public void submitSMResponse(Connection source, SubmitSMResp smr) {

		logger.debug("[TX] Receive submitSMResponse");

		if (smr != null) {

			logger.debug("[TX]CommandId : [{}], [TX]status : [{}], Error code : [{}]", smr.getCommandId(),
					smr.getCommandStatus(), smr.getErrorCode());
			logger.debug("[TX]Message id : [{}], [TX]message : [{}]", smr.getMessageId(), smr.getMessageText());
			logger.debug("[TX]esm class : [{}], [TX]data coding : [{}]", smr.getEsmClass(), smr.getDataCoding());
			logger.debug("[TX]delivery time : [{}], [TX]message status : [{}]", smr.getDeliveryTime(),
					smr.getMessageStatus());
			logger.debug("[TX]OA : [{}], [TX]DA : [{}], [TX]seq : [{}]", smr.getSource(), smr.getDestination(),
					smr.getSequenceNum());
			logger.debug("[TX]Error code : [{}]", smr.getErrorCode());

			try {
				//logger.debug("[TPS][TX] now TPS is {}", deQueue.getTxTps());
				logger.info("[SMPP] Send smscMsgId {} to SMSC finished !!", smr.getMessageId());
				Consumer worker = workerProvider.get();
				worker.setMsg(parser.parseSMSResp(smr));
				taskExecutor.execute(worker);
				
				//saveSmsRecordSub
//				MessageObject msg = parser.parseSMSResp(smr);
//				String wsMsgId = String.valueOf(msg.getSeq());
//				String smscMsgId = "";
//				if (msg != null && msg.getSmscMessageId() != null) {
//					smscMsgId = msg.getSmscMessageId();
//				}
//				logger.debug("saveSmsRecordSub(2) WS_MSG_ID:[{}], SMSC_MSG_ID:[{}]", wsMsgId, smscMsgId);
//				String result = smsRecordManager.saveSmsRecordSub(wsMsgId, smscMsgId, msg.getStatus());
//				logger.debug("saveSmsRecordSub(2) WS_MSG_ID:[{}], SMSC_MSG_ID:[{}], result :[{}]", wsMsgId, smscMsgId, result);
//				if (!result.equals("0")) {
//					logger.error("[Consumer] update Database got [{}] error", result);
//				}
				
				
			} catch (TaskRejectedException e) {
				// limited thread size so put the message into queue again
				// TODO need to tune up queue size for prevent this situation
			} catch (Exception e) {
				logger.error("[SMPP] runtime error : [{}]", e.getMessage());
				logger.warn(e, e);
			}
		}
	}

	@Override
	public void submitMultiResponse(Connection source, SubmitMultiResp smr) {
		// TODO handle submit response
		logger.debug("CommandId : [{}], status : [{}], Error code : [{}]", smr.getCommandId(), smr.getCommandStatus(),
				smr.getErrorCode());
	}

	@Override
	public void cancelSMResponse(Connection source, CancelSMResp cmr) {
		// TODO Auto-generated method stub
		logger.debug("[TX][cancelSMResponse]");

	}

	@Override
	public void replaceSMResponse(Connection source, ReplaceSMResp rmr) {
		// TODO Auto-generated method stub
		logger.debug("[TX][replaceSMResponse]");
	}

	@Override
	public void queryResponse(Connection source, SMPPResponse qr) {
		// TODO Auto-generated method stub
		logger.debug("[TX][queryResponse]");

	}

	@Override
	public void receiverExitException(Connection source, ReceiverExitEvent rev) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiverException(Connection source, ReceiverExceptionEvent rev) {
		// TODO Auto-generated method stub
		
	}

}
