package com.ws.emg.smpp.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ws.smpp.ReceiverEventHandler;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.message.DeliverSM;

@Component("rxHandler")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RxHandler extends ReceiverEventHandler {

	private static Logger logger = LogManager.getLogger(RxHandler.class);

	@Override
	public void deliverSM(Connection source, DeliverSM dm) {
		// TODO handle receive dr
		logger.debug("[RX][deliverSM]");
		
		logger.debug("[RX]CommandId : [{}], [RX]status : [{}], Error code : [{}]", dm.getCommandId(),
				dm.getCommandStatus(), dm.getErrorCode());
		logger.debug("[RX]Message id : [{}], [RX]message : [{}]", dm.getMessageId(), dm.getMessageText());
		logger.debug("[RX]esm class : [{}], [RX]data coding : [{}]", dm.getEsmClass(), dm.getDataCoding());
		logger.debug("[RX]delivery time : [{}], [RX]message status : [{}]", dm.getDeliveryTime(),
				dm.getMessageStatus());
		logger.debug("[RX]OA : [{}], [RX]DA : [{}], [RX]seq : [{}]", dm.getSource(), dm.getDestination(),
				dm.getSequenceNum());
		logger.debug("[RX]Error code : [{}]", dm.getErrorCode());

		// query cp for which queue need to response

		// enqueue back to httpapi

		// update database
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
