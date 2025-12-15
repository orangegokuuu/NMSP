package com.ws.ibm.mq.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.util.MQXmlUtil;
import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MessageHeader;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacy.MspMessageTwo;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.legacyPojo.SMS;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MQJmsConsumer {

	private JmsConnectionFactory MQCf;

	private Connection connection = null;
	private Session session = null;
	private Queue queue = null;
	private MessageConsumer consumer = null;

	public MQJmsConsumer(JmsConnectionFactory MQCf) {
		this.MQCf = MQCf;
		// try {
		// this.connectQueueManager();
		// } catch (JMSException e) {
		// log.warn("Fail to connect MQ.");
		// log.debug(e);
		// }
	}

	public void connectQueue(String destinationName) throws JMSException {
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// targetClient=1 means this queue is non-jms queue
		queue = session.createQueue("queue:///" + destinationName + "?targetClient=1");
		// consumer = session.createConsumer(queue, "JMSCorrelationID IS NULL");
		// // select only Message A
		consumer = session.createConsumer(queue, "");
	}

	public void connectQueue(String destinationName, String constraints) throws JMSException {
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = session.createQueue("queue:///" + destinationName + "?targetClient=1");
		consumer = session.createConsumer(queue, constraints);
	}

	public void startConnection(String destinationName) throws JMSException {
		connection = MQCf.createConnection();
		connection.start();
		connectQueue(destinationName);
		log.debug("start consumer connection to queue[{}]", queue.getQueueName());
	}

	// public void stopConnection() {
	// try {
	// if (session != null) {
	// session.close();
	// }
	// if (connection != null) {
	// connection.stop();
	// }
	// } catch (JMSException e) {
	// log.debug(e);
	// }
	// }

	public void closeConnection() {
		try {
			if (session != null) {
				session.close();
				log.debug("close consumer session for queue[{}]", queue.getQueueName());
			}
			if (connection != null) {
				connection.close();
				log.debug("close consumer connection to queue[{}]", queue.getQueueName());
			}
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	// For MSP Format 1 MSG
	public MspMessage receiveFormatOneMsgA(int msg_A_timeout, int msg_C_timeout) throws JMSException, SMSException {
		// BytesMessage message = null;
		// if BytesMessage not work, you may used TextMessage to receive the
		// message then convert the Text to bytes
		TextMessage message = null;

		MspMessage formatedMsg = null;
		boolean ack = LegacyConstant.DEFAULT_FORMAT_1_ACK;
		try {
			// message = (BytesMessage) consumer.receive(msg_A_timeout);
			message = (TextMessage) consumer.receive(msg_A_timeout);
			if (message == null) {
				throw new SMSException(LegacyConstant.FORMAT_ONE, 9999);
			}
			log.debug("Receive Format 1 Message A {}", message);

			String msgId = message.getJMSMessageID();
			String corrId = message.getJMSCorrelationID();
			if (corrId != null) {
				log.info("Invalid Format 1 Message C Message with JMSCorrelationID[{}].",
						message.getJMSCorrelationID());
				throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
			}

			// byte[] content = new byte[body.length()];
			// message.readBytes(content);

			byte[] content = message.getText().getBytes();

			formatedMsg = new MspMessage();
			formatedMsg.setImqMsgId(msgId);

			// format header
			byte[] headByte = new byte[MspMessage.HEADER_LENGTH];
			System.arraycopy(content, 0, headByte, 0, MspMessage.HEADER_LENGTH);

			// get ack flag before any SMSException throw
			ack = MessageHeader.getSerializedAckFlag(headByte);
			MessageHeader header = MessageHeader.deserialize(headByte);

			formatedMsg.setHeader(header);
			// format body
			int bodyLength = 0;
			if (header.getLanguage() == 'C' || header.getLanguage() == 'V') {
				bodyLength = MspMessage.MAX_UNICODE_BODY;
			} else if (header.getLanguage() == 'B' || header.getLanguage() == 'W') {
				bodyLength = MspMessage.MAX_BIG5_BODY;
			} else if (header.getLanguage() == 'E' || header.getLanguage() == 'U') {
				bodyLength = MspMessage.MAX_ASCII_BODY;
			} else {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.TYPE));
			}
			byte[] bodyByte = new byte[bodyLength];
			System.arraycopy(content, MspMessage.HEADER_LENGTH, bodyByte, 0, content.length - MspMessage.HEADER_LENGTH);

			formatedMsg.setBody(bodyByte, LegacyConstant.LANG.get(String.valueOf(header.getLanguage())));

			log.info("MsgId = {}, CorrelId = {} , content = {}", message.getJMSMessageID(),
					message.getJMSCorrelationID(), formatedMsg.toString());
			if (header.getType() == 'M') {
				formatedMsg.setTargets(receiveFormatOneMsgC(message.getJMSMessageID(), header.getCount(),
						header.getLanguage(), msg_C_timeout));
			}
		} catch (IndexOutOfBoundsException e) {
			log.debug(e);
			SMSException ex = new SMSException(LegacyConstant.FORMAT_ONE,
					LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.BODY));
			ex.setStackTrace(e.getStackTrace());
			// record parent message ID and ack
			if (message != null) {
				ex.setAck(ack);
				ex.setCorreId(message.getJMSMessageID());
			}
			throw ex;
		} catch (SMSException e) {
			// record parent message ID and ack
			if (message != null) {
				e.setAck(ack);
				e.setCorreId(message.getJMSMessageID());
			}
			throw e;
		}
		log.debug("End formatting Message A {}", message);
		return formatedMsg;
	}

	public TextMessage receiveMsgB(int timeout) throws JMSException, SMSException {
		log.info("	====  Receiver Message (Format B) ====");
		MessageConsumer consumer = session.createConsumer(queue);
		TextMessage message = (TextMessage) consumer.receive(timeout);
		if (message != null) {
			log.debug("Receive Message Format B {}", message);
		}
		return message;
	}

	public byte[] receiveFormatOneMsgC(String msgId, int count, char lang, int timeout)
			throws JMSException, SMSException {
		log.info("	====  Sub Receiver Message (Format C) ====");
		MessageConsumer subConsumer = null;
		try {
			String query = "JMSCorrelationID = '" + msgId + "'";
			subConsumer = session.createConsumer(queue, query);
			log.debug("Start sub-consumer with queryselector[{}]", query);

			TextMessage message = (TextMessage) subConsumer.receive(timeout);
			if (message == null) {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.TARGET));
			}
			log.debug("Receive Format 1 Message C {}", message);

			String body = message.getText();

			if (body.getBytes().length != MspMessage.DA_LENGTH * count) {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.COUNT));
			}

			// byte[] content = body.getBytes();
			// message.readBytes(content);
			// System.arraycopy(body.getBytes(), 0, content, destPos, length);

			return body.getBytes();
		} finally {
			subConsumer.close();
			log.debug("	====  Sub-consumer closed ====");
		}
	}

	// MSP Format 2

	public MspMessageTwo receiveFormatTwoMsgA(int msg_A_timeout, int msg_C_timeout) throws JMSException, SMSException {
		MspMessageTwo formatedMsg = null;
		Message tmpMessage = null;
		try {
			tmpMessage = consumer.receive(msg_A_timeout);
			if (tmpMessage == null) {
				throw new SMSException(LegacyConstant.FORMAT_TWO, 9999);
			}
			
//			if (tmpMessage.getJMSCorrelationID() != null) {
//				log.debug("Invalid Format 2 Message C Message with JMSCorrelationID[{}].",
//						tmpMessage.getJMSCorrelationID());
//				// resp mp-reject
//				throw new SMSException(LegacyConstant.FORMAT_TWO, 2001);
//			}
			
			log.debug("Receive Format 2 Message A {}", tmpMessage);
			
			try {
				BytesMessage message = (BytesMessage) tmpMessage;
				
				byte[] compressedContent = new byte[(int) message.getBodyLength()];
				message.readBytes(compressedContent);
				byte[] content = MQZipUtil.decompress(compressedContent);
				
				String xml = new String(content, StandardCharsets.UTF_8);
				log.info("msg content = {}", xml);

				formatedMsg = new MspMessageTwo();
				formatedMsg.setImqMsgId(message.getJMSMessageID());
				formatedMsg.setBody(MQXmlUtil.XmlToObject(xml, SMS.class));

				log.info("MsgId = {}, CorrelId = {} , content = {}", message.getJMSMessageID(),
						message.getJMSCorrelationID(), formatedMsg.toString());
			} catch (ClassCastException e) {
				try {
					TextMessage message = (TextMessage) tmpMessage;
					
					byte[] compressedContent = message.getText().getBytes();
					byte[] content = MQZipUtil.decompress(compressedContent);
					
					String xml = new String(content, StandardCharsets.UTF_8);
					log.info("msg content = {}", xml);

					formatedMsg = new MspMessageTwo();
					formatedMsg.setImqMsgId(message.getJMSMessageID());
					formatedMsg.setBody(MQXmlUtil.XmlToObject(xml, SMS.class));

					log.info("MsgId = {}, CorrelId = {} , content = {}", message.getJMSMessageID(),
							message.getJMSCorrelationID(), formatedMsg.toString());
				} catch (ClassCastException e2) {
					log.error("Fail to handle Message Type. Trace: {}", e2);
				}
			}

		} catch (SMSException e) {
			// record parent message ID
			if (tmpMessage != null) {
				e.setCorreId(tmpMessage.getJMSMessageID());
			}
			throw e;
		} catch (UnsupportedEncodingException e) {
			log.debug(e);
			SMSException ex = new SMSException(LegacyConstant.FORMAT_TWO, 1005);
			ex.setCorreId(tmpMessage.getJMSMessageID());
			throw ex;
		} catch (IOException | JAXBException | SAXException | ParserConfigurationException e) {
			log.debug(e);
			SMSException ex = new SMSException(LegacyConstant.FORMAT_TWO, 1000);
			ex.setCorreId(tmpMessage.getJMSMessageID());
			throw ex;
		} catch (Exception e) {
			log.debug(e);
			throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
		}
		log.debug("End formatting Message A {}", tmpMessage);
		return formatedMsg;
	}

}
