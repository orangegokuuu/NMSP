package com.ws.emg.smpp.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TxHandler extends TransmitterEventHandler {

	private static Logger logger = LogManager.getLogger(TxHandler.class);

	@Override
	public void submitSMResponse(Connection source, SubmitSMResp smr) {
		// TODO handle submit response
		logger.debug("[TX]CommandId : [{}], [TX]status : [{}], Error code : [{}]", smr.getCommandId(),
				smr.getCommandStatus(), smr.getErrorCode());
		logger.debug("[TX]Message id : [{}], [TX]message : [{}]", smr.getMessageId(), smr.getMessageText());
		logger.debug("[TX]esm class : [{}], [TX]data coding : [{}]", smr.getEsmClass(), smr.getDataCoding());
		logger.debug("[TX]delivery time : [{}], [TX]message status : [{}]", smr.getDeliveryTime(),
				smr.getMessageStatus());
		logger.debug("[TX]OA : [{}], [TX]DA : [{}], [TX]seq : [{}]", smr.getSource(), smr.getDestination(),
				smr.getSequenceNum());
		logger.debug("[TX]Error code : [{}]", smr.getErrorCode());

		if (smr != null) {
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
	public void receiverExitException(Connection source, ReceiverExitEvent rev) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiverException(Connection source, ReceiverExceptionEvent rev) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryResponse(Connection source, SMPPResponse qr) {
		// TODO Auto-generated method stub
		
	}

}
