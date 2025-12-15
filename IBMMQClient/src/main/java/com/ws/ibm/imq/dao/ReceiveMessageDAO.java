package com.ws.ibm.imq.dao;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;
import com.ws.ibm.imq.exception.DAOException;
import com.ws.ibm.imq.pojo.MPInternalMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Setter
@Getter
@Log4j2
public class ReceiveMessageDAO extends GenericMqDAO {

	private MQQueue reqQueue = null;

	public ReceiveMessageDAO() {
	}

	public ReceiveMessageDAO(String qmgrName, String host, int port, String channel) {
		this.qmgrName = qmgrName;
		this.host = host;
		this.port = port;
		this.channel = channel;
	}

	// get from reqQueue
	public MPInternalMessage get(int waitInterval) throws DAOException {
		MPInternalMessage returnMsg = null;
		try {
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQGMO_WAIT + MQConstants.MQGMO_SYNCPOINT + MQConstants.MQGMO_FAIL_IF_QUIESCING;
			gmo.waitInterval = waitInterval;
			// Get MQMessage from queue.
			try {
				MQMessage inMsg = new MQMessage();
				reqQueue.get(inMsg, gmo);
				returnMsg = new MPInternalMessage();
				returnMsg.setMqMsgId(inMsg.messageId);
				returnMsg.setMqCorrelationId(inMsg.correlationId);
				returnMsg.setMqReplyToQ(inMsg.replyToQueueName);
				byte[] msgBody = new byte[inMsg.getMessageLength()];
				inMsg.readFully(msgBody);
				returnMsg.setMqMsgBody(msgBody);
				log.debug("Get message[{}] from queue[{}]", returnMsg.toString(), reqQueue.getName());
			} catch (MQException e) {
				if (e.reasonCode != MQConstants.MQRC_NO_MSG_AVAILABLE) {
					throw e;
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.commit();
		}
		return returnMsg;
	}

	/**
	 * Get message with MessageId.
	 * 
	 * @param queueName
	 * @param messageId
	 * @param waitInterval
	 * @return MPInternalMessage
	 * @throws DAOException
	 */
	public MPInternalMessage matchIdGet(String queueName, byte[] messageId, int waitInterval) throws DAOException {
		MPInternalMessage returnMsg = null;
		try {
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.matchOptions = MQConstants.MQMO_MATCH_CORREL_ID;
			gmo.options = MQConstants.MQGMO_WAIT + MQConstants.MQGMO_SYNCPOINT + MQConstants.MQGMO_FAIL_IF_QUIESCING;
			gmo.waitInterval = waitInterval;
			// Get MQMessage with msgId from queue.
			try {
				MQMessage inMsg = new MQMessage();
				log.debug("Find message with messageId[{}]", messageId);
				System.arraycopy(messageId, 0, inMsg.messageId, 0, messageId.length);
				System.arraycopy(messageId, 0, inMsg.correlationId, 0, messageId.length);
				reqQueue.get(inMsg, gmo);
				returnMsg = new MPInternalMessage();
				returnMsg.setMqMsgId(inMsg.messageId);

				returnMsg.setMqCorrelationId(inMsg.correlationId);
				returnMsg.setMqReplyToQ(inMsg.replyToQueueName);
				byte[] msgBody = new byte[inMsg.getMessageLength()];
				inMsg.readFully(msgBody);
				returnMsg.setMqMsgBody(msgBody);
				log.debug("Get message[{}] from queue[{}]", returnMsg.toString(), reqQueue.getName());
			} catch (MQException e) {
				if (e.reasonCode != MQConstants.MQRC_NO_MSG_AVAILABLE) {
					throw new MQException(e.completionCode, e.reasonCode, reqQueue);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.commit();
		}
		return returnMsg;
	}

	/**
	 * Get message with MessageId using default interval
	 * 
	 * @param queueName
	 * @param messageId
	 * @return MPInternalMessage
	 * @throws DAOException
	 */
	public MPInternalMessage matchIdGet(String queueName, byte[] messageId) throws DAOException {
		return matchIdGet(queueName, messageId, 500);
	}

	/**
	 * @param reqQName
	 * @return boolean
	 *         Connect with reqQueue
	 * @throws MQException 
	 */
	public void connectQueue(String reqQName) throws MQException {
		int openOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_FAIL_IF_QUIESCING;
		reqQueue = qmgr.accessQueue(reqQName, openOptions, null, null, null);
		log.trace("Connect to queue[{}]", reqQName);
	}

	/**
	 * Close reqQueue
	 */
	public void close() {
		try {
			if (reqQueue != null) {
				log.trace("Closequeue[{}]", reqQueue.getName());
				reqQueue.close();
			}
		} catch (MQException ex) {
			ex.printStackTrace();
		}
	}

}
