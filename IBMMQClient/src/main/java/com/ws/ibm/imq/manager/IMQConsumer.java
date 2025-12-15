package com.ws.ibm.imq.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Hex;
import org.xml.sax.SAXException;

import com.ws.ibm.imq.dao.ReceiveMessageDAO;
import com.ws.ibm.imq.exception.DAOException;
import com.ws.ibm.imq.pojo.MPInternalMessage;
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
public class IMQConsumer {

	private boolean connectionRetry = true;
	private int retryInterval = 5000;
	private int retrytimes = 4;
	private int times = 0;

	private ReceiveMessageDAO dao = null;

	public IMQConsumer(String qmgrName, String host, int port, String channel) {
		if (dao == null) {
			this.dao = new ReceiveMessageDAO(qmgrName, host, port, channel);
		}
	}

	public void start(String qName) throws Exception {
		try {
			dao.connect();
			dao.connectQueue(qName);
		} catch (Exception e) {
			log.error("Fail to connect with queue[{}]", qName);
			log.error(e);
			e.printStackTrace();
			if (connectionRetry) {
				log.info("retry connection after {} ms", retryInterval);
				times++;
				if (times >= retrytimes) {
					close();
					Exception e1 = new Exception("Connected Failed!!");
					throw e1;
				} else {
					log.info("retry {} times", times);
					try {
						Thread.sleep(retryInterval);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					start(qName);
				}
			}
		}
	}

	public void close() {
		dao.close();
		dao.disconnect();
	}

	public MspMessage receiveFormatOneMsgA(String queueName, int msg_A_timeout, int msg_C_timeout) throws SMSException {
		MspMessage formatedMsg = null;
		boolean ack = LegacyConstant.DEFAULT_FORMAT_1_ACK;

		MPInternalMessage legacyMessage = null;
		try {
			legacyMessage = dao.get(msg_A_timeout);
			if (legacyMessage == null) {
				throw new SMSException(LegacyConstant.FORMAT_ONE, 9999);
			}
			log.debug("Receive Format 1 Message A {}", new String(legacyMessage.getMqMsgBody()));

			byte[] msgId = legacyMessage.getMqMsgId();
			byte[] corrId = legacyMessage.getMqCorrelationId();
			
			// 20190812 YC modify, remove unused logic
//			if (!StringUtil.isEmpty(new String(corrId).trim())) {
//				log.info("Invalid Format 1 Message C Message with JMSCorrelationID[{}].", new String(corrId).trim());
//				throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
//			}
			

			// message.readBytes(content);

			// byte[] content = message.getText().getBytes();
			byte[] content = legacyMessage.getMqMsgBody();

			formatedMsg = new MspMessage();
			
			//20190530 modify by YC
//			formatedMsg.setImqMsgId(new String(msgId));
			formatedMsg.setImqMsgId(String.valueOf(Hex.encodeHex(msgId)));
			// format header
			byte[] headByte = new byte[MspMessage.HEADER_LENGTH];
			System.arraycopy(content, 0, headByte, 0, MspMessage.HEADER_LENGTH);

			// ack = MessageHeader.getSerializedAckFlag(headByte);

			// log.debug("get ackFlag = {}", ack);
			MessageHeader header = MessageHeader.deserialize(headByte);

			formatedMsg.setHeader(header);
			
			// format body
			/*
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
			*/
			
			// 20190524 modify by YC
			int bodyLength = 0;
			if (header.getLanguage() == 'C' || header.getLanguage() == 'V') {
				bodyLength = MspMessage.MAX_UNICODE_BODY * 2;
			} else if (header.getLanguage() == 'B' || header.getLanguage() == 'W') {
				bodyLength = MspMessage.MAX_BIG5_BODY * 2;
			} else if (header.getLanguage() == 'E' || header.getLanguage() == 'U') {
				bodyLength = MspMessage.MAX_ASCII_BODY;
			} else {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.TYPE));
			}
			
			byte[] bodyByte = new byte[bodyLength];
			System.arraycopy(content, MspMessage.HEADER_LENGTH, bodyByte, 0, content.length - MspMessage.HEADER_LENGTH);

			formatedMsg.setBody(bodyByte, LegacyConstant.LANG.get(String.valueOf(header.getLanguage())));

			log.info("MsgId = {}, CorrelId = {} , content = {}", msgId, corrId, formatedMsg.toString());
			if (header.getType() == 'M') {
				formatedMsg.setTargets(receiveFormatOneMsgC(queueName, legacyMessage.getMqMsgId(), header.getCount(),
						header.getLanguage(), msg_C_timeout));
			}
		} catch (IndexOutOfBoundsException e) {
			log.debug(e);
			SMSException ex = new SMSException(LegacyConstant.FORMAT_ONE,
					LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.BODY));
			ex.setStackTrace(e.getStackTrace());
			// record parent message ID and ack
			if (legacyMessage != null) {
				ex.setAck(ack);
				ex.setCorreId(legacyMessage.getMqMsgIdAsString());
			}
			throw ex;
		} catch (DAOException e) {
			log.debug(e);
			SMSException ex = new SMSException(LegacyConstant.FORMAT_ONE, 2001);
			ex.setStackTrace(e.getStackTrace());
			// record parent message ID and ack
			if (legacyMessage != null) {
				ex.setAck(ack);
				ex.setCorreId(legacyMessage.getMqMsgIdAsString());
			}
			throw ex;
		} catch (SMSException e) {
			// record parent message ID and ack
			if (legacyMessage != null) {
				e.setAck(ack);
				e.setCorreId(legacyMessage.getMqMsgIdAsString());
			}
			throw e;
		}
		log.debug("End formatting Message A {}", legacyMessage);
		return formatedMsg;
	}

	public byte[] receiveFormatOneMsgC(String queueName, byte[] msgId, int count, char lang, int timeout)
			throws DAOException, SMSException {
		log.info("	====  Sub Receiver Message (Format C) ====");
		MPInternalMessage legacyMessage = null;
		try {
			legacyMessage = dao.matchIdGet(queueName, msgId, timeout);
			if (legacyMessage == null) {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.TARGET));
			}
			log.debug("Receive Format 1 Message C {}", legacyMessage);

			byte[] body = legacyMessage.getMqMsgBody();

			if (body.length != MspMessage.DA_LENGTH * count) {
				throw new SMSException(LegacyConstant.FORMAT_ONE,
						LegacyConstant.FORMAT_1_ERROR.get(LegacyConstant.FIELD.COUNT));
			}

			// byte[] content = body.getBytes();
			// message.readBytes(content);
			// System.arraycopy(body.getBytes(), 0, content, destPos, length);

			return body;
		} finally {
			log.debug("	====  Sub-consumer end ====");
		}
	}
	
	
	// MSP Format 2

		public MspMessageTwo receiveFormatTwoMsgA(int msg_A_timeout, int msg_C_timeout) throws JMSException, SMSException {
			MspMessageTwo formatedMsg = null;
			MPInternalMessage message = null;
			try {
				message = dao.get(msg_A_timeout);
				if (message == null) {
					throw new SMSException(LegacyConstant.FORMAT_TWO, 9999);
				}
				
				log.debug("Receive Format 2 Message A {}", message);
				
				byte[] compressedContent = message.getMqMsgBody();
				byte[] content = MQZipUtil.decompress(compressedContent);
				
				String xml = new String(content, StandardCharsets.UTF_8);
				log.info("msg content = {}", xml);

				formatedMsg = new MspMessageTwo();
				formatedMsg.setImqMsgId(message.getMqMsgIdAsString());
				formatedMsg.setBody(MQXmlUtil.XmlToObject(xml, SMS.class));

				log.info("MsgId = {}, CorrelId = {} , content = {}", message.getMqMsgIdAsString(),
						message.getMqCorrelationIdAsString(), formatedMsg.toString());

			} catch (SMSException e) {
				// record parent message ID
//				if (message != null) {
//					e.setCorreId(message.getMqMsgIdAsString());
//				}
				throw e;
			} catch (UnsupportedEncodingException e) {
				log.debug(e);
				SMSException ex = new SMSException(LegacyConstant.FORMAT_TWO, 1005);
				ex.setCorreId(message.getMqMsgIdAsString());
				throw ex;
			} catch (IOException | JAXBException | SAXException | ParserConfigurationException e) {
				log.debug(e);
				SMSException ex = new SMSException(LegacyConstant.FORMAT_TWO, 1000);
				ex.setCorreId(message.getMqMsgIdAsString());
				throw ex;
			} catch (Exception e) {
				log.debug(e);
				throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
			} finally {
				log.debug("End formatting Message A {}", message);
			}
			return formatedMsg;
		}

}
