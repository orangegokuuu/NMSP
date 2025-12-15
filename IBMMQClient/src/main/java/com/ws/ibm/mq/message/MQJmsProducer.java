package com.ws.ibm.mq.message;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.ibm.mq.util.MQXmlUtil;
import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacyPojo.MPReject;
import com.ws.msp.legacyPojo.SmsMO;
import com.ws.util.ByteUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MQJmsProducer {

	private JmsConnectionFactory MQCf;

	private Connection connection = null;
	private Session session = null;
	private Queue queue = null;
	private MessageProducer mainProducer = null;

	public MQJmsProducer(JmsConnectionFactory MQCf) {
		this.MQCf = MQCf;
		// try {
		// this.connectQueueManager();
		// } catch (JMSException e) {
		// log.warn("Fail to connect MQ.");
		// log.debug(e);
		// }
	}

	public void connectQueue(String destinationName) throws JMSException {
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.queue = session.createQueue("queue:///" + destinationName + "?targetClient=1");
		this.mainProducer = session.createProducer(queue);
	}

	public void startConnection(String destinationName) throws JMSException {
		this.connection = MQCf.createConnection();
		connection.start();
		connectQueue(destinationName);
		log.debug("start producer connection to queue[{}]", queue.getQueueName());
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
				log.debug("close producer session for queue[{}]", queue.getQueueName());
			}
			if (connection != null) {
				connection.close();
				log.debug("close producer connection to queue[{}]", queue.getQueueName());
			}
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	// For testing only
	public void enqTextMsgA(String dataA) throws JMSException {
		TextMessage msgA = session.createTextMessage(dataA);
		this.mainProducer.send(msgA);
		log.debug(ByteUtil.byteArrayTohexString(dataA.getBytes()));
		log.debug("Enqueue Text msg={}", msgA);
	}

	public void enqTextFormatOneMsgB(String correId, int errorCode) {
		TextMessage msgB;
		try {
			msgB = session.createTextMessage();
			msgB.setJMSCorrelationID(correId);
			msgB.setText(Integer.toString(errorCode));
			this.mainProducer.send(msgB);
			log.debug("Enqueue Format one msgB={}", msgB);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqTextMsgAwithC(String dataA, String dataC) throws JMSException {
		TextMessage msgA = session.createTextMessage();
		msgA.setText(dataA);

		TextMessage msgC = session.createTextMessage();
		msgC.setJMSMessageID(msgA.getJMSMessageID());
		msgC.setJMSCorrelationID(msgA.getJMSMessageID());
		msgC.setText(dataC);

		this.mainProducer.send(msgA);
		this.mainProducer.send(msgC);
		log.debug("Enqueue Format One msgA={} with msgC={}", msgA, msgC);
	}

	//
	// public void enqTextMsgAwithC(String dataA, String dataC) throws
	// JMSException {
	// TextMessage msgA = session.createTextMessage(dataA);
	// this.mainProducer.send(msgA);
	//
	// TextMessage msgC = session.createTextMessage(dataC);
	// msgC.setJMSMessageID(msgA.getJMSMessageID());
	// msgC.setJMSCorrelationID(msgA.getJMSMessageID());
	// this.mainProducer.send(msgC);
	// }

	public void enqBytesMsgA(byte[] dataA) throws JMSException, UnsupportedEncodingException {
		BytesMessage msgA = session.createBytesMessage();
		msgA.writeBytes(dataA);
		this.mainProducer.send(msgA);
		log.debug("Enqueue msgA={}", msgA);
	}

	public void enqBytesFormatOneMsgB(String correId, int errorCode) {
		BytesMessage msgB;
		try {
			msgB = session.createBytesMessage();
			msgB.setJMSCorrelationID(correId);
			msgB.writeBytes(Integer.toString(errorCode).getBytes(StandardCharsets.UTF_8));
			this.mainProducer.send(msgB);
			log.debug("Enqueue Format one msgB={}", msgB);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqBytesFormatTwoMsgB(String correId, int errorCode) {
		BytesMessage msgB;
		try {
			msgB = session.createBytesMessage();
			msgB.setJMSCorrelationID(correId);
			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			MPReject rejectSMS = new MPReject();
			rejectSMS.setErrorDescription(LegacyConstant.messageTwoMap.get(errorCode));
			rejectSMS.setErrorCode(String.valueOf(errorCode));
			String xml = MQXmlUtil.ObjectToXml(rejectSMS, MPReject.class);
			// compress and send
			log.debug("Zip xml with timeStamp[{}]", msgStamp);
			msgB.writeBytes(MQZipUtil.compress(msgStamp, xml.getBytes(StandardCharsets.UTF_8)));
			this.mainProducer.send(msgB);
			log.debug("Enqueue Format two msgB={}", msgB);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format2 Message B to queue");
			log.debug(e);
		}
	}

	public void enqBytesMsgAwithC(byte[] dataA, byte[] dataC) throws JMSException {
		BytesMessage msgA = session.createBytesMessage();
		msgA.writeBytes(dataA);
		this.mainProducer.send(msgA);

		BytesMessage msgC = session.createBytesMessage();
		msgC.setJMSMessageID(msgA.getJMSMessageID());
		msgC.setJMSCorrelationID(msgA.getJMSMessageID());
		msgC.writeBytes(dataC);
		this.mainProducer.send(msgC);
		log.debug("Enqueue Format One msgA={} with msgC={}", msgA, msgC);
	}

	public void enqBytesMsgAck(String correId, String content) {
		BytesMessage ack;
		try {
			ack = session.createBytesMessage();
			ack.setStringProperty("JMS_IBM_Character_Set", "1051");
			ack.setJMSMessageID("Acknowledge");
			ack.setJMSCorrelationID("Acknowledge");
//			ack.setObjectProperty("JMS_IBM_MQMD_MsgId", "Acknowledge".getBytes());
//			ack.setObjectProperty("JMS_IBM_MQMD_CorrelId", correId.getBytes());
//			ack.setJMSCorrelationID(correId);
			ack.writeBytes(content.getBytes(StandardCharsets.UTF_8));
			log.debug("Enqueue Format One ack msg={}", ack);
			this.mainProducer.send(ack);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqTextMsgAck(String correId, String content) {
		TextMessage ack;
		try {
			ack = session.createTextMessage(content);
			ack.setJMSMessageID("Acknowledge");
			ack.setJMSCorrelationID(correId);
			log.debug("Enqueue Format One ack msg={}", ack);
			this.mainProducer.send(ack);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqBytesMulticasMsgAck(String correId, String content) {
		BytesMessage ack;
		try {
			ack = session.createBytesMessage();
			ack.setJMSMessageID("Multicas");
			ack.setJMSCorrelationID(correId);
			ack.writeBytes(content.getBytes(StandardCharsets.UTF_8));
			log.debug("Enqueue Format One ack msg={}", ack);
			this.mainProducer.send(ack);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqTextMulticasMsgAck(String correId, String content) {
		TextMessage ack;
		try {
			ack = session.createTextMessage(content);
			ack.setJMSMessageID("Multicas");
			ack.setJMSCorrelationID(correId);
			log.debug("Enqueue Format One ack msg={}", ack);
			this.mainProducer.send(ack);
		} catch (JMSException e) {
			log.debug(e);
		}
	}

	public void enqBytesFrtOneMsgDr(MspMessage msg) {
		BytesMessage dr;
		try {
			dr = session.createBytesMessage();
			dr.setJMSMessageID(msg.getHeader().getSource());
			dr.setJMSCorrelationID("Delivery");
			dr.writeBytes(msg.toMessageA());
			log.debug("Enqueue Format One dr msg={}", dr);
			this.mainProducer.send(dr);
		} catch (JMSException e) {
			log.debug(e);
		} catch (UnsupportedEncodingException e) {
			log.warn(e);
		}
	}

	public void enqTextFrtOneMsgDr(MspMessage msg) {
		TextMessage dr;
		try {
			dr = session.createTextMessage(new String(msg.toMessageA()));
			dr.setJMSMessageID(msg.getHeader().getSource());
			dr.setJMSCorrelationID("Delivery");
			log.debug("Enqueue Format One dr msg={}", dr);
			this.mainProducer.send(dr);
		} catch (JMSException e) {
			log.debug(e);
		} catch (UnsupportedEncodingException e) {
			log.warn(e);
		}
	}

	public void enqBytesFrtOneMsgMo(MspMessage msg) {
		BytesMessage mo;
		try {
			mo = session.createBytesMessage();
			mo.setJMSMessageID(msg.getHeader().getSource());
			mo.setJMSCorrelationID("Normal");
			mo.writeBytes(msg.toMessageA());
			log.debug("Enqueue Format One mo msg={}", mo);
			this.mainProducer.send(mo);
		} catch (JMSException e) {
			log.debug(e);
		} catch (UnsupportedEncodingException e) {
			log.warn(e);
		}
	}

	// Dr or mo
	public void enqBytesFrtTwoMsgDrMo(SmsMO msg) {
		BytesMessage mo;
		try {
			mo = session.createBytesMessage();

			// may need to format this two fields
			// mo.setJMSMessageID(msg.getHeader().getSource());
			// mo.setJMSCorrelationID("Normal");

			// convert SmsMo to xml String
			String xmlMsg = MQXmlUtil.ObjectToXml(msg, SmsMO.class);

			// compress data and writeBytes
			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			mo.writeBytes(MQZipUtil.compress(msgStamp, xmlMsg.getBytes()));

			log.debug("Enqueue Format two dr/mo msg={}", mo);
			this.mainProducer.send(mo);

		} catch (Exception e) {
			log.warn("Fail to enqueue SmsMo message to queue");
			log.debug(e);
		}
	}

	public void enqTextFrtTwoMsgDrMo(SmsMO msg) {
		TextMessage mo;
		try {
			mo = session.createTextMessage();

			// may need to format this two fields
			// mo.setJMSMessageID(msg.getHeader().getSource());
			// mo.setJMSCorrelationID("Normal");

			// convert SmsMo to xml String
			String xmlMsg = MQXmlUtil.ObjectToXml(msg, SmsMO.class);

			// compress data and writeBytes
			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			mo.setText(new String(MQZipUtil.compress(msgStamp, xmlMsg.getBytes())));
			log.debug("Enqueue Format two dr/mo msg={}", mo);
			this.mainProducer.send(mo);

		} catch (Exception e) {
			log.warn("Fail to enqueue SmsMo message to queue");
			log.debug(e);
		}
	}

}
