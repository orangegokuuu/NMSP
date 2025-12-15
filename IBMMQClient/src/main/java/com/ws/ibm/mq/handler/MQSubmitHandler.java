package com.ws.ibm.mq.handler;

import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.api.util.HttpApiUtils;
import com.ws.emg.pojo.MessageObject;
import com.ws.ibm.imq.manager.IMQProducer;
import com.ws.msp.config.MspProperties;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.MessageHeader;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.legacyPojo.SmsMO;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.ImqSmsRecord;
import com.ws.msp.pojo.ImqSmsRecordPk;
import com.ws.msp.service.ImqSmsRecordManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class MQSubmitHandler {

	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQProducerCf = null;

	@Autowired
	private MspProperties properties = null;

	@Autowired
	private ImqSmsRecordManager imqSmsRecordManager = null;

	private IMQProducer imqProducer = null; // format 1

	// private MQJmsProducer jmsProducer = null; // format 2

	private ContentProvider cp = null;

	public void init(ContentProvider cp) {
		String qmgrName = properties.getIbm().getJms().getQueueManagerName();
		String host = properties.getIbm().getJms().getHost();
		int port = properties.getIbm().getJms().getPort();
		String channel = properties.getIbm().getJms().getChannel();
		this.cp = cp;

		// if (cp.isLegacy()) {
		imqProducer = new IMQProducer(qmgrName, host, port, channel);
		// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));
		// } else {
		// jmsProducer = new MQJmsProducer(MQProducerCf);
		// }
	}

	@PreDestroy
	public void shutdown() {
		imqProducer.close();
	}

	// enqueue format 1 dr message to ibm mq
	public void submitFormatOneDrMessage(MessageObject msg) throws JMSException {
		log.debug("Start submit format 1 Dr message");
		// Lenddice.20190522
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {

				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));
				// jmsProducer.startConnection(MQHandler.getCPQueueName(msg.getCpId(), "PLY"));

				log.debug("WsMsgId={}, target={}", msg.getWsMessageId(), msg.getDestination());

				// retrieve ImqMsgId and msg header from db
				ImqSmsRecord record = imqSmsRecordManager.get(ImqSmsRecord.class,
						new ImqSmsRecordPk(msg.getWsMessageId(), msg.getDestination()));
				if (record == null) {
					log.warn("Fail to retrieve ImqSmsRecord with WsMsgId[{}], DA[{}]", msg.getWsMessageId(),
							msg.getDestination());
					throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
				}

				log.debug("imqRecord={}", record);
				MspMessage mspMessage = new MspMessage();

				// 20195031 modify by YC
				// MessageHeader header =
				// MessageHeader.deserialize(record.getImqMsgHeader().getBytes());
				MessageHeader header = MessageHeader
						.deserialize(DatatypeConverter.parseHexBinary(record.getImqMsgHeader()));
				
				String oriSourceAddress = header.getSource();
				String oriDestAddress = header.getTarget();

				// 20190704 YC modify, make language always be "E", and ack & dr flag = 0
				header.setLanguage(new Character('E'));
				header.setAckFlag(false);
				header.setDrFlag(false);
				header.setCount(0);
				
				// dr need to change oa to da
				header.setSource(oriDestAddress);
				header.setTarget(oriSourceAddress);
				
				mspMessage.setHeader(header);

				// construct dr message body
				// drMsg.setId(msg.getWsMessageId());
				// drMsg.setSub();
				// drMsg.setDlvrd(0);
				// // should be YYMMDDhhmm
				// drMsg.setSubmitDate(msg.getSubmitTime());
				// drMsg.setDoneDate(msg.getDoneTime());
				//
				// drMsg.setStat(msg.getStatus());
				//
				// // Need Error Mapping
				// drMsg.setErr(msg.getErrorCode());
				//
				// drMsg.setText(msg.getMessage());

				// convert to mspMessage A
				String body = msg.getMessage();
				log.debug("Dr report = {}", body);

				// drId could be length = 10
				String drId = body.substring("id:".length(), body.indexOf("sub:")).trim();

				// 20190613 YC Modify
				log.debug("get DR from SMSC messageId [{}], mapping http req msgId [{}]", drId, msg.getWsMessageId());

				// String drReport = "id:" + String.format("%010d", Integer.parseInt(drId)) + "
				// "
				// + body.substring(body.indexOf("sub:"));
				String drReport = "id:" + String.format("%010d", Integer.parseInt(msg.getWsMessageId())) + " "
						+ body.substring(body.indexOf("sub:"));
				mspMessage.setBody(drReport.replaceFirst("Text:", "text:"));
				log.debug(Hex.encodeHexString(mspMessage.toMessageA()));

				imqProducer.enqDrMessage(mspMessage);
				// jmsProducer.enqBytesFrtOneMsgDr(mspMessage);
			} catch (SMSException e) {
				// This should not occur
				log.warn("Incorrect header format");
				log.debug(e);
			} finally {
				// jmsProducer.closeConnection();
				imqProducer.close();
				log.debug("End submit format 1 Dr message for CP[{}]", cp.getCpId());
			}
		} catch (Exception e) {
			log.info("Exception occurred.DR [{}]! PLY.QUEUE=[{}]", e.getMessage(), MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 1 mo message to ibm mq
	public void submitFormatOneMoMessage(MessageObject msg) throws JMSException {
		log.debug("Start submit format 1 Mo message");
		try {
			// Lenddice.20190522
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));
				// jmsProducer.startConnection(MQHandler.getCPQueueName(msg.getCpId(), "PLY"));

				MspMessage mspMessage = new MspMessage();

				// construct mo header
				MessageHeader header = new MessageHeader();
				header.setVersion('1');
				header.setSysID(msg.getCpId());
				header.setType('S');
				header.setDrFlag(false);
				header.setAckFlag(false);
				header.setLanguage(LegacyConstant.GSM_CODING.get(new Integer(msg.getDataCoding())).charAt(0));
				header.setTarget(msg.getDestination());
				header.setSource(msg.getSource());
				header.setCount(0);
				mspMessage.setHeader(header);

				// construct mo body
				// 20190628 modify by YC
//				mspMessage.setBody(HttpApiUtils.base64Encoded(msg.getMessage(),
//						LegacyConstant.LANG.get("" + header.getLanguage())));
				
				String language = "U";
				String coding = "utf8";
				switch (msg.getDataCoding()) {
				case 0:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 1:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 3:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 8:
					// change coding from UTF-8 to Unicode, unicode use UTF-16BE
					language = "U";
					coding = "utf8";
					break;
				default:
					break;
				}
				
				
				String text = msg.getMessage();
				try {
					text = HttpApiUtils.getBase64DecodedText(text, language, coding);
					msg.setMessage(text);
				} catch (Exception e) {

				}
				mspMessage.setBody(msg.getMessage());

				// jmsProducer.enqBytesFrtOneMsgMo(mspMessage);
				imqProducer.enqMoMessage(mspMessage);
			} finally {
				// jmsProducer.closeConnection();
				imqProducer.close();
				log.debug("End submit format 1 Mo message for CP[{}]", cp.getCpId());
			}
		} catch (Exception e) {
			log.info("Exception occurred.MO [{}]! PLY.QUEUE=[{}]", e.getMessage(), MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 2 dr message to ibm mq
	public void submitFormatTwoDrMessage(MessageObject msg) throws JMSException {
		log.debug("Start submit format 2 Dr message");
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {
				// jmsProducer.startConnection(MQHandler.getCPQueueName(msg.getCpId(), "PLY"));
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));
				// Lenddice.20190522

				SmsMO drMsg = new SmsMO();
				SmsMO.Message drBody = new SmsMO.Message();

				SmsMO.Message.Source source = new SmsMO.Message.Source();

				// retrieve ImqMsg info from db
				ImqSmsRecord record = imqSmsRecordManager.get(ImqSmsRecord.class,
						new ImqSmsRecordPk(msg.getWsMessageId(), msg.getDestination()));
				// Da become source in dr
				source.setValue(msg.getDestination());
				source.setToken(record.getImqToken());

				drBody.setSource(source);

				drBody.setLanguage(record.getLanguage());

				drBody.setTarget(msg.getSource());

				// 20190701 YC modify, ibmmq need to revert msg from base64 to normal
//				drBody.setText(msg.getMessage().substring(msg.getMessage().indexOf("Text:") + "Text:".length()));
				String language = "U";
				String coding = "utf8";
				switch (msg.getDataCoding()) {
				case 0:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 1:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 3:
					language = "E";
					coding = "ISO-8859-1";
					break;
				case 8:
					language = "U";
					coding = "utf8";
					break;
				default:
					break;
				}
				
				
				String text = msg.getMessage();
				// decode text
				try {
					text = HttpApiUtils.getBase64DecodedText(text, language, coding);
				} catch (Exception e) {

				}
				// trim dr content for needed
				text = text.substring(text.indexOf("Text:") + "Text:".length());
				drBody.setText(HttpApiUtils.base64Encoded(text, coding));

				SmsMO.Message.DeliveryReport drContent = new SmsMO.Message.DeliveryReport();
				drContent.setId(msg.getWsMessageId());

				drContent.setSubmitDate(msg.getSubmitTime());
				drContent.setDoneDate(msg.getDoneTime());
				drContent.setError(msg.getErrorCode());
				drContent.setState(msg.getState());

				drBody.setDeliveryReport(drContent);

				drMsg.getMessage().add(drBody);

				imqProducer.enqF2Dr(drMsg, record.getImqMsgId().getBytes());
			} finally {
				// jmsProducer.closeConnection();
				imqProducer.close();
				log.debug("End submit format 2 Dr message for CP[{}]", cp.getCpId());
			}
		} catch (Exception e) {
			log.info("Exception occurred.DR [{}]! PLY.QUEUE=[{}]", e.getMessage(), MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 2 mo message to ibm mq
	public void submitFormatTwoMoMessage(MessageObject msg) throws JMSException {
		// Lenddice.20190522
		log.debug("start submit format 2 mo message");
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {

				// jmsProducer.startConnection(MQHandler.getCPQueueName(msg.getCpId(), "PLY"));
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));

				SmsMO moMsg = new SmsMO();
				SmsMO.Message drBody = new SmsMO.Message();

				SmsMO.Message.Source source = new SmsMO.Message.Source();
				source.setValue(msg.getSource());
				drBody.setSource(source);
				drBody.setLanguage(LegacyConstant.GSM_CODING.get(new Integer(msg.getDataCoding())));

				drBody.setTarget(msg.getDestination());
				drBody.setText(msg.getMessage());

				moMsg.getMessage().add(drBody);

				// jmsProducer.enqBytesFrtTwoMsgDrMo(moMsg);
				imqProducer.enqF2Mo(moMsg, drBody.getTarget());
			} finally {
				// jmsProducer.closeConnection();
				imqProducer.close();
				log.debug("End submit format 2 Mo message for CP[{}]", cp.getCpId());
			}
		} catch (Exception e) {
			log.info("Exception occurred.MO [{}]! PLY.QUEUE=[{}]", e.getMessage(), MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 1 ack message to ibm mq
	public void handleFormatOneAck(byte[] imqMsgId, String respMsgId, char type) {
		log.info("Enqueue Ack msg. Original MsgId[{}]", imqMsgId);
		// Lenddice.20190522
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {
				// String replyQName = MQHandler.getCPQueueName(cpId, "PLY");
				// jmsProducer.startConnection(replyQName);
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));

				// 20190613 YC modify change Decimal String to Hex String
				try {
					respMsgId = StringUtils.leftPad(Integer.toHexString(Integer.parseInt(respMsgId)), 8, "0");
					// change MSG ID to upper case
					respMsgId = StringUtils.upperCase(respMsgId);
				} catch (Exception e) {
					log.warn("Parse Req MsgId to Hex fail!, use default value : [{}]", respMsgId);
				}

				if (type == 'M') {
					// jmsProducer.enqBytesMulticasMsgAck(imqMsgId, respMsgId);
					imqProducer.enqMulticasAckMessage(imqMsgId, respMsgId);
				} else {
					// jmsProducer.enqBytesMsgAck(imqMsgId, respMsgId);
					imqProducer.enqAckMessage(imqMsgId, respMsgId);
				}
			} finally {
				imqProducer.close();
				log.debug("End enqueue Ack msg for Original Msg[{}]", imqMsgId);
			}
		} catch (Exception e) {
			log.info("Exception occurred.Ack [{}]! PLY.QUEUE=[{}]", e.getMessage(),
					MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 1 error message to Ibm mq
	public void handleFormatOneSMSException(SMSException e) throws JMSException {
		log.info("SMSException Occurred. Error Code[{}]. Start Submit Error message", e.getErrorCode());

		// Lenddice.20190522
		String replyQName = MQHandler.getCPQueueName(cp, "PLY");
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {
				// String replyQName = MQHandler.getCPQueueName(cp.getCpId(), "PLY");
				// jmsProducer.startConnection(replyQName);
				// jmsProducer.enqBytesFormatOneMsgB(e.getCorreId(), e.getErrorCode());
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));

				// 20190613 YC modify
				// imqProducer.enqErrorMsg(e.getCorreId().getBytes(), e.getErrorCode());
				imqProducer.enqErrorMsg(DatatypeConverter.parseHexBinary(e.getCorreId()), e.getErrorCode());
				log.debug("Exception code enqueued to replyQName[{}]", replyQName);
			} finally {
				imqProducer.close();
				log.debug("End enqueue SMSException[{}] msg", e.getErrorCode());
			}
		} catch (Exception e1) {
			log.info("Exception occurred.enqueue SMSException [{}]! PLY.QUEUE=[{}]", e1.getMessage(),
					MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	// enqueue format 2 error message to Ibm mq
	public void handleFormatTwoSMSException(SMSException e) throws JMSException {
		log.info("SMSException Occurred. Error Code[{}]. Start Submit Error message", e.getErrorCode());
		// Lenddice.20190522
		String replyQName = MQHandler.getCPQueueName(cp, "PLY");
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {
				// String replyQName = MQHandler.getCPQueueName(cp.getCpId(), "PLY");

				// jmsProducer.startConnection(replyQName);
				// jmsProducer.enqBytesFormatTwoMsgB(e.getCorreId(), e.getErrorCode());
				// imqProducer.start(MQHandler.getCPQueueName(cp.getCpId(), "PLY"));

				imqProducer.enqF2MsgB(e.getCorreId(), e.getErrorCode());
				log.debug("Exception code enqueued to replyQName[{}]", replyQName);
			} finally {
				imqProducer.close();
				log.debug("End enqueue SMSException[{}] msg", e.getErrorCode());
			}
		} catch (Exception e1) {
			log.info("Exception occurred.enqueue SMSException [{}]! PLY.QUEUE=[{}]", e1.getMessage(),
					MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	public void submitFormatOne(MessageObject msg) throws JMSException {

		try {

			log.debug("WsMsgId={}, target={}", msg.getWsMessageId(), msg.getDestination());

			// retrieve ImqMsgId and msg header from db
			ImqSmsRecord record = imqSmsRecordManager.get(ImqSmsRecord.class,
					new ImqSmsRecordPk(msg.getWsMessageId(), msg.getDestination()));
			if (record == null) {
				log.warn("Fail to retrieve ImqSmsRecord with WsMsgId[{}], DA[{}]", msg.getWsMessageId(),
						msg.getDestination());
				throw new SMSException(LegacyConstant.FORMAT_ONE, 2001);
			}

			log.debug("imqRecord={}", record);

			String state = record.getState();
			MessageHeader header = MessageHeader
					.deserialize(DatatypeConverter.parseHexBinary(record.getImqMsgHeader()));

			if (StringUtils.isNoneBlank(state)) {
				// check method is ack or DR
				if (state.contains("A")) {
					// TODO check smsc status then ack
					handleFormatOneAck(DatatypeConverter.parseHexBinary(record.getImqMsgId()), msg.getWsMessageId(),
							header.getType());
					if (state.contains("D")) {
						record.setState(state.replace("A", ""));
					} else {
						record.setState("S");
					}
					imqSmsRecordManager.save(ImqSmsRecord.class, record);

				} else {
					if (state.contains("D")) {
						submitFormatOneDrMessage(header, msg.getMessage(), msg.getWsMessageId());
					}
					record.setState("S");
					imqSmsRecordManager.save(ImqSmsRecord.class, record);
				}
			}

		} catch (SMSException e) {
			// This should not occur
			log.warn("Incorrect header format");
			log.debug(e);
		} finally {
			// jmsProducer.closeConnection();
			imqProducer.close();
			log.debug("End submit format 1 Dr message for CP[{}]", cp.getCpId());
		}
	}

	public void submitFormatOneDrMessage(MessageHeader header, String body, String reqMsgId) throws JMSException {
		log.debug("Start submit format 1 Dr message");
		try {
			imqProducer.start(MQHandler.getCPQueueName(cp, "PLY"));
			try {

				log.debug("Dr report = {}", body);

				MspMessage mspMessage = new MspMessage();

				// drId could be length = 10
				String drId = body.substring("id:".length(), body.indexOf("sub:")).trim();

				// 20190613 YC Modify
				log.debug("get DR from SMSC messageId [{}], mapping http req msgId [{}]", drId, reqMsgId);

				// String drReport = "id:" + String.format("%010d", Integer.parseInt(drId)) + "
				// "
				// + body.substring(body.indexOf("sub:"));
				String drReport = "id:" + String.format("%010d", Integer.parseInt(reqMsgId)) + " "
						+ body.substring(body.indexOf("sub:"));
				mspMessage.setBody(drReport.replaceFirst("Text:", "text:"));
				log.debug(new String(mspMessage.toMessageA()));

				imqProducer.enqDrMessage(mspMessage);
				// jmsProducer.enqBytesFrtOneMsgDr(mspMessage);
			} finally {
				// jmsProducer.closeConnection();
				imqProducer.close();
				log.debug("End submit format 1 Dr message for CP[{}]", cp.getCpId());
			}
		} catch (Exception e) {
			log.info("Exception occurred.DR [{}]! PLY.QUEUE=[{}]", e.getMessage(), MQHandler.getCPQueueName(cp, "PLY"));
		}
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * MessageHeader header = null; String strHeader =
	 * "314d4450534d5331533131423039303334393334373400000000000000000000303132333437323030303030303030303030303000000000";
	 * try { header =
	 * MessageHeader.deserialize(DatatypeConverter.parseHexBinary(strHeader)); }
	 * catch (SMSException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * if(header != null) { System.out.println("getSysID :" + header.getSysID());
	 * System.out.println("getCount :" + header.getCount());
	 * System.out.println("getLanguage :" + header.getLanguage());
	 * System.out.println("getSource :" + header.getSource());
	 * System.out.println("getTarget :" + header.getTarget());
	 * System.out.println("getTarget :" + header.getType());
	 * System.out.println("getVersion :" + header.getVersion()); } }
	 */

}
