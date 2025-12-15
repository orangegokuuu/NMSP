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
import com.ws.emg.processor.Producer;
import com.ws.emg.util.EmgParser;
import com.ws.smpp.ReceiverEventHandler;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ReceiverExceptionEvent;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.message.DeliverSM;

@Component("rxHandler")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RxHandler extends ReceiverEventHandler {

	private static Logger logger = LogManager.getLogger(RxHandler.class);

	@Autowired
	private Provider<Producer> workerProvider;

	@Autowired
	private EmgParser parser;

	@Autowired
	@Qualifier("producerExcutor")
	private TaskExecutor taskExecutor;

	// @Autowired
	// @Qualifier("emgDeQueueListener")
	// DeQueueListener deQueue;

	@Override
	public void deliverSM(Connection source, DeliverSM dm) {

		logger.debug("[RX] Receive deliverSM");

		if (dm != null) {

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

			try {
				//logger.debug("[TPS][RX] now TPS is {}", deQueue.getRxTps());
				Producer worker = workerProvider.get();
				if (dm.getEsmClass() == EmgParser.TYPE_DR) {
					worker.setMsg(parser.parseDeliverSM(dm));
				} else {
					worker.setMsg(parser.parseMO(dm));
				}
				taskExecutor.execute(worker);
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
	public void receiverExitException(Connection source, ReceiverExitEvent rev) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiverException(Connection source, ReceiverExceptionEvent rev) {
		// TODO Auto-generated method stub

	}

}
