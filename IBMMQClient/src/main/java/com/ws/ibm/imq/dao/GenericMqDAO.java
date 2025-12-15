package com.ws.ibm.imq.dao;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPoolToken;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ws.ibm.imq.config.MQConnectionFactory;
import com.ws.ibm.imq.exception.DAOException;
import com.ws.ibm.imq.pojo.MPInternalMessage;

public class GenericMqDAO {
	protected String qmgrName;
	protected String host;
	protected int port;
	protected String channel;
	protected MQQueueManager qmgr = null;
	protected MQPoolToken token;

	public GenericMqDAO() {
	}

	public GenericMqDAO(String qmgrName) {
		this.qmgrName = qmgrName;
	}

	public GenericMqDAO(String qmgrName, String host, int port, String channel) {
		this.qmgrName = qmgrName;
		this.host = host;
		this.port = port;
		this.channel = channel;
	}

	public void connect() throws MQException {
		MQEnvironment.hostname = host;
		MQEnvironment.port = port;
		MQEnvironment.channel = channel;
		token = MQEnvironment.addConnectionPoolToken();
		try {
			qmgr = MQConnectionFactory.createConnection(qmgrName);
		} catch (MQException e) {
			MQEnvironment.removeConnectionPoolToken(token);
			e.printStackTrace();
			throw e;
		}
	}

	public void disconnect() {
		try {
			if (qmgr != null) {
				qmgr.disconnect();
			}
		} catch (MQException ex) {
			ex.printStackTrace();
		}
		MQEnvironment.removeConnectionPoolToken(token);
	}

	public void commit() {
		try {
			if (qmgr != null) {
				qmgr.commit();
			}
		} catch (MQException ex) {
			ex.printStackTrace();
		}
	}

	public void backout() {
		try {
			if (qmgr != null) {
				qmgr.backout();
			}
		} catch (MQException ex) {
			ex.printStackTrace();
		}
	}

	public byte[] put(MPInternalMessage msg, String queueName) throws Exception {
		MQQueue queue = null;
		try {
			int openOptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
			queue = qmgr.accessQueue(queueName, openOptions, null, null, null);
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = MQConstants.MQPMO_SYNCPOINT + MQConstants.MQPMO_NEW_MSG_ID;
			MQMessage outMsg = new MQMessage();
			outMsg.correlationId = msg.getMqCorrelationId();
			outMsg.replyToQueueName = msg.getMqReplyToQ();
			outMsg.write(msg.getMqMsgBody());
			queue.put(outMsg, pmo);
			return outMsg.messageId;
		} catch (Exception e) {
			throw e;
		} finally {
			if (queue != null) {
				try {
					this.commit();
					queue.close();
				} catch (MQException ex) {
				}
			}
		}
	}

	public MPInternalMessage get(String queueName, int waitInterval) throws DAOException {
		MPInternalMessage returnMsg = null;
		MQQueue queue = null;
		try {
			int openOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_FAIL_IF_QUIESCING;
			queue = qmgr.accessQueue(queueName, openOptions, null, null, null);
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQGMO_WAIT + MQConstants.MQGMO_SYNCPOINT + MQConstants.MQGMO_FAIL_IF_QUIESCING;
			gmo.waitInterval = waitInterval;
			// Get MQMessage from queue.
			try {
				MQMessage inMsg = new MQMessage();
				queue.get(inMsg, gmo);
				returnMsg = new MPInternalMessage();
				returnMsg.setMqMsgId(inMsg.messageId);
				returnMsg.setMqCorrelationId(inMsg.correlationId);
				returnMsg.setMqReplyToQ(inMsg.replyToQueueName);
				byte[] msgBody = new byte[inMsg.getMessageLength()];
				inMsg.readFully(msgBody);
				returnMsg.setMqMsgBody(msgBody);
			} catch (MQException e) {
				if (e.reasonCode != MQConstants.MQRC_NO_MSG_AVAILABLE) {
					throw e;
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (queue != null) {
				try {
					this.commit();
					queue.close();
				} catch (MQException ex) {
				}
			}
		}
		return returnMsg;
	}

	public MPInternalMessage get(String queueName) throws DAOException {
		return get(queueName, 500);
	}

}