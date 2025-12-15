package com.ws.emg.test;

import com.ws.smpp.TransmitterEventHandler;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.message.CancelSMResp;
import ie.omk.smpp.message.ReplaceSMResp;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitMultiResp;
import ie.omk.smpp.message.SubmitSMResp;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SmppLoggingHandler extends TransmitterEventHandler {

	@Override
	public void submitSMResponse(Connection source, SubmitSMResp smr) {
		log.debug("SubmitSM Seq[{}] ID[{}] return[{}]", smr.getSequenceNum(), smr.getMessageId(),
				smr.getCommandStatus());
	}

	@Override
	public void submitMultiResponse(Connection source, SubmitMultiResp smr) {
		log.debug("SubmitMulti Seq[{}] ID[{}] return[{}]", smr.getSequenceNum(), smr.getMessageId(),
				smr.getCommandStatus());

	}

	@Override
	public void cancelSMResponse(Connection source, CancelSMResp cmr) {
		log.debug("CancelSM Seq[{}] ID[{}] return[{}]", cmr.getSequenceNum(), cmr.getMessageId(),
				cmr.getCommandStatus());

	}

	@Override
	public void replaceSMResponse(Connection source, ReplaceSMResp rmr) {
		log.debug("ReplaceSM Seq[{}] ID[{}] return[{}]", rmr.getSequenceNum(), rmr.getMessageId(),
				rmr.getCommandStatus());
	}

	@Override
	public void queryResponse(Connection source, SMPPResponse qr) {
		log.debug("QuerySM Seq[{}] ID[{}] return[{}]", qr.getSequenceNum(), qr.getMessageId(), qr.getCommandStatus());
	}

	@Override
	public void receiverExitException(Connection source, ReceiverExitEvent rev) {
		log.debug("Receiver Exit Exception", rev.getException());
	}

	@Override
	public void receiverException(Connection source, ReceiverExceptionEvent rev) {
		log.debug("Receiver Exception", rev.getException());
	}

}
