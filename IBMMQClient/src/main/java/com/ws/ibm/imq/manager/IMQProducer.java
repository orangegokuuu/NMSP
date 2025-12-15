package com.ws.ibm.imq.manager;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PreDestroy;
import javax.xml.bind.DatatypeConverter;

import com.ws.ibm.imq.dao.SubmitMessageDAO;
import com.ws.ibm.imq.exception.DAOException;
import com.ws.ibm.mq.util.MQUtil;
import com.ws.ibm.mq.util.MQXmlUtil;
import com.ws.ibm.mq.util.MQZipUtil;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacyPojo.MPReject;
import com.ws.msp.legacyPojo.SmsMO;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IMQProducer {

	private boolean connectionRetry = true;
	private int retryInterval = 5000;
	private int retrytimes = 4;
	private int times = 0;

	private SubmitMessageDAO dao = null;

	public IMQProducer(String qmgrName, String host, int port, String channel) {
		this.dao = new SubmitMessageDAO(qmgrName, host, port, channel);
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

	// general
	public void put(byte[] messageId, byte[] correlationId, byte[] content) throws DAOException {
		dao.put(messageId, correlationId, content);
	}

	// Format 1
	public void enqErrorMsg(byte[] correId, int errorCode) {
		try {
			byte[] messageId = "Acknowledge".getBytes();
			byte[] correlationId = correId;
			byte[] content = Integer.toString(errorCode).getBytes();
			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 1 Error message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqAckMessage(byte[] imqMsgId, String smscMsgId) {
		try {
			byte[] messageId = "Acknowledge".getBytes();
			byte[] correlationId = imqMsgId;
			// 20190531 modify by YC
//			byte[] content = MQUtil.toHex(Integer.parseInt(smscMsgId)).getBytes();
			byte[] content = smscMsgId.getBytes();
//			log.debug("imqMsgId : [{}] , smscMsgId : [{}]", String.valueOf(Hex.encodeHex(imqMsgId)), smscMsgId);
			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 1 Ack message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqMulticasAckMessage(byte[] imqMsgId, String smscMsgId) {
		try {
			byte[] messageId = "Multicas".getBytes();
			byte[] correlationId = imqMsgId;
			// 20190531 modify by YC
//			byte[] content = MQUtil.toHex(Integer.parseInt(smscMsgId)).getBytes();
			byte[] content = smscMsgId.getBytes();
			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 1 Multicas Ack message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqDrMessage(MspMessage msg) {
		try {
			// 20190704 modify by YC
//			byte[] messageId = msg.getHeader().getSource().getBytes();
			byte[] messageId = msg.getHeader().getTarget().getBytes();
			byte[] correlationId = "Delivery".getBytes();
			byte[] content = msg.toMessageA();
			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 1 Dr message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqMoMessage(MspMessage msg) {
		try {
			// 20190628 modify by YC
//			byte[] messageId = msg.getHeader().getSource().getBytes();
			byte[] messageId = msg.getHeader().getTarget().getBytes();
			byte[] correlationId = "Normal".getBytes();
			byte[] content = msg.toMessageA(LegacyConstant.LANG.get(String.valueOf(msg.getHeader().getLanguage())));
			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 1 Mo message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	// Format 2
	public void enqF2Dr(SmsMO msg, byte[] sourceMsgId) {
		try {
			byte[] messageId = "".getBytes();
			byte[] correlationId = sourceMsgId;

			// convert SmsMo to xml String
			String xmlMsg = MQXmlUtil.ObjectToXml(msg, SmsMO.class);

			// compress data and writeBytes
			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			byte[] content = MQZipUtil.compress(msgStamp, xmlMsg.getBytes(StandardCharsets.UTF_8));
			log.debug("Enqueue Format two dr msg={}", content);

			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 2 Dr message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqF2Mo(SmsMO msg, String sa) {
		try {
			byte[] messageId = "".getBytes();
			byte[] correlationId = sa == null ? "".getBytes() : sa.getBytes();

			// convert SmsMo to xml String
			String xmlMsg = MQXmlUtil.ObjectToXml(msg, SmsMO.class);

			// compress data and writeBytes
			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			byte[] content = MQZipUtil.compress(msgStamp, xmlMsg.getBytes(StandardCharsets.UTF_8));
			log.debug("Enqueue Format two Mo msg={}", content);

			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 2 Mo message. Cause: {}", e);
			e.printStackTrace();
		}
	}

	public void enqF2MsgB(String correId, int errorCode) {
		try {
			byte[] messageId = "".getBytes();
			byte[] correlationId = correId == null ? "".getBytes() : correId.getBytes();

			String msgStamp = new SimpleDateFormat(MQZipUtil.ZIP_TIME_STAMP_FORMAT).format(new Date());
			MPReject rejectSMS = new MPReject();
			rejectSMS.setErrorDescription(LegacyConstant.messageTwoMap.get(errorCode));
			rejectSMS.setErrorCode(String.valueOf(errorCode));
			String xml = MQXmlUtil.ObjectToXml(rejectSMS, MPReject.class);
			// compress and send
			log.debug("Zip xml with timeStamp[{}]", msgStamp);
			byte[] content = MQZipUtil.compress(msgStamp, xml.getBytes(StandardCharsets.UTF_8));
			log.debug("Enqueue Format two msgB={}", content);

			dao.put(messageId, correlationId, content);
		} catch (Exception e) {
			log.warn("Fail to enqueue Format 2 Message B to queue. Cause: {}", e);
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void destroy() {
		dao.close();
	}

}
