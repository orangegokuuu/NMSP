package com.ws.ibm.imq.dao;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;
import com.ws.ibm.imq.exception.DAOException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Setter
@Getter
@Log4j2
public class SubmitMessageDAO extends GenericMqDAO {

	private MQQueue plyQueue = null;

	public SubmitMessageDAO() {
	}

	public SubmitMessageDAO(String qmgrName, String host, int port, String channel) {
		this.qmgrName = qmgrName;
		this.host = host;
		this.port = port;
		this.channel = channel;
	}

	/**
	 * Connect with plyQueue
	 * 
	 * @param plyQName
	 * @return result
	 * @throws MQException
	 * 
	 */
	public void connectQueue(String plyQName) throws MQException {
		int openOptions = MQConstants.MQOO_OUTPUT | MQConstants.MQOO_FAIL_IF_QUIESCING;
		plyQueue = qmgr.accessQueue(plyQName, openOptions, null, null, null);
		log.trace("Connect to queue[{}]", plyQName);
	}

	/**
	 * Close plyQueue
	 */
	public void close() {
		try {
			if (plyQueue != null) {
				plyQueue.close();
				log.trace("Close queue[{}]", plyQueue.getName());
			}
		} catch (MQException ex) {
			ex.printStackTrace();
		}
	}

	public void put(byte[] messageId, byte[] correlationId, byte[] content) throws DAOException {
		try {
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = MQConstants.MQPMO_SYNCPOINT;
			MQMessage outMsg = new MQMessage();
			outMsg.messageId = messageId;
			outMsg.correlationId = correlationId;
			outMsg.write(content);
			plyQueue.put(outMsg, pmo);
			log.debug("Submit message[{}] to queue[{}], messageId[{}], correlationId[{}], content[{}]", outMsg, plyQueue.getName(),
					String.valueOf(String.valueOf(Hex.encodeHex(outMsg.messageId))),
					String.valueOf(String.valueOf(Hex.encodeHex(outMsg.correlationId))),
					String.valueOf(String.valueOf(Hex.encodeHex(content))));
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.commit();
		}
	}

}
