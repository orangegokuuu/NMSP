package com.ws.ibm.mq.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ws.emg.constant.ApiConstant;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.SMSResp;
import com.ws.ibm.mq.util.MQXmlUtil;
import com.ws.msp.config.MspProperties;
import com.ws.msp.legacy.LegacyConstant;
import com.ws.msp.legacy.LegacyConstant.FIELD;
import com.ws.msp.legacy.MspMessage;
import com.ws.msp.legacy.MspMessageTwo;
import com.ws.msp.legacy.SMSException;
import com.ws.msp.legacyPojo.SMS;
import com.ws.msp.pojo.ImqSmsRecord;
import com.ws.msp.pojo.ImqSmsRecordPk;
import com.ws.msp.service.ImqSmsRecordManager;
import com.ws.util.RegexUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ApiSubmitHandler {

	@Autowired
	private MspProperties properties = null;

	@Autowired
	@Qualifier("MQProducerConnectionPool")
	private JmsConnectionFactory MQProducerCf = null;

	@Autowired
	private ImqSmsRecordManager imqSmsRecordManager = null;
	
	@Autowired
	private RestTemplate rest = null;
	
	private StringBuffer uri = null;
	private HttpHeaders headers = null;
	
	@PostConstruct
	private void init(){
		uri = new StringBuffer(properties.getIbm().getSubmitAPIURL());
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	}
	
	
	// send message to http Api
	public String submitFormatOneSMS(MspMessage msg) throws SMSException {
		String respMsgId = null;
		int format = LegacyConstant.FORMAT_ONE;

		// format to xml and send to HTTP API
		String xmlSMS = MQXmlUtil.getFormatOneXmlData(msg);

		log.debug("### XML :\r\n {}", xmlSMS);
		
		SMSResp resp = null;
		try {

		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("xmlData", xmlSMS);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> responseEntity = rest.exchange(uri.toString(), HttpMethod.POST, request,String.class);

			log.debug("### responseCode : [{}]", responseEntity.getStatusCode());

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				log.debug("#####Submit Success - ");
			} else {
				log.debug("#####Submit Fail- ");
				log.debug("resp : {}", responseEntity.getBody());
				throw new SMSException(format, 2001);
			}

			// string xml to obj
//			InputStream inStream = conn.getInputStream();
//			String respXml = CharStreams.toString(new InputStreamReader(inStream, StandardCharsets.UTF_8));
			
			String respXml = responseEntity.getBody();
			
			resp = (SMSResp) MQXmlUtil.XmlToObject(respXml, SMSResp.class);
			log.info("respMsgId:{}, respCode:{}, timestamp:{}", resp.getMessageId(), resp.getResultCode(),
					resp.getTimestamp());

			if (!resp.getResultCode().equals(ApiConstant.RC_SUCCESS)) {
				throw new SMSException(format,
						LegacyConstant.HTTPAPI_RESP_ERROR_MAP_FORMAT_1.get(resp.getResultCode()));
			} else {
				log.info("Submission Completed");
				respMsgId = resp.getMessageId();
			}

			// record header to db if dr is required
			if (msg.getHeader().isDrFlag()) {

				if (msg.getHeader().getType() != 'M') {

					String parsedTarget = parseTarget(new String(msg.getHeader().getTarget()));
					if (parsedTarget == null) {
						throw new SMSException(LegacyConstant.FORMAT_ONE,
								LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
					}

					ImqSmsRecord record = new ImqSmsRecord();
					ImqSmsRecordPk pk = new ImqSmsRecordPk(resp.getMessageId(), parsedTarget);
					record.setPk(pk);
					record.setImqMsgId(msg.getImqMsgId());
					record.setLanguage(String.valueOf(msg.getHeader().getLanguage()));
					// 20190531 YC modify
//					record.setImqMsgHeader(new String(msg.getHeader().serialize()));
					record.setImqMsgHeader(String.valueOf(Hex.encodeHex(msg.getHeader().serialize())));
					
					String responseStatus = "";
					if(msg.getHeader().isAckFlag()) {
						responseStatus += "A";
					}
					if(msg.getHeader().isDrFlag()) {
						responseStatus += "D";
					}
					record.setState(responseStatus);
					
					log.info("DrFlag[true], Record Msg Header. MsgId[{}], Da[{}]", resp.getMessageId(), parsedTarget);
					imqSmsRecordManager.save(ImqSmsRecord.class, record);
				} else {
					ArrayList<String> targetList = msg.getTargetList();
					for (String target : targetList) {
						String parsedTarget = parseTarget(target);
						if (parsedTarget == null) {
							throw new SMSException(LegacyConstant.FORMAT_ONE,
									LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
						}

						ImqSmsRecord record = new ImqSmsRecord();
						ImqSmsRecordPk pk = new ImqSmsRecordPk(resp.getMessageId(), parsedTarget);
						record.setPk(pk);
//						log.debug("&&&& before : [{}]", msg.getImqMsgId());
						record.setImqMsgId(msg.getImqMsgId());
						record.setLanguage(String.valueOf(msg.getHeader().getLanguage()));
						// 20190531 YC modify
//						record.setImqMsgHeader(new String(msg.getHeader().serialize()));
						record.setImqMsgHeader(String.valueOf(Hex.encodeHex(msg.getHeader().serialize())));
						
						String responseStatus = "";
						if(msg.getHeader().isAckFlag()) {
							responseStatus += "A";
						}
						if(msg.getHeader().isDrFlag()) {
							responseStatus += "D";
						}
						record.setState(responseStatus);
						
						log.info("DrFlag[true], Record Msg Header. MsgId[{}], Da[{}]", resp.getMessageId(),
								parsedTarget);
						imqSmsRecordManager.save(ImqSmsRecord.class, record);
					}
				}
			}
			return respMsgId;
		} catch (SMSException e) {
			e.setAck(msg.getHeader().isAckFlag());
			e.setCorreId(msg.getImqMsgId());
			throw e;
		} catch (Exception e) {
			log.debug(e);
			SMSException ex = new SMSException(format, 2001, msg.getHeader().isAckFlag(), msg.getImqMsgId());
			ex.setStackTrace(e.getStackTrace());
			ex.setAck(msg.getHeader().isAckFlag());
			ex.setCorreId(msg.getImqMsgId());
			throw ex;
		} finally {
//			conn.disconnect();
			log.debug("End httpApi submission.");
		}
	}

	// send message to http Api
	public void submitFormatTwoSMS(MspMessageTwo msg) throws SMSException {
		int format = LegacyConstant.FORMAT_TWO;

		// format to xml and send to HTTP API
		List<String> xmlSMSList = MQXmlUtil.getFormatTwoXmlData(msg.getBody());

//		xmlSMSList.forEach(xml -> {
//			log.debug("### XML :\r\n {}", xml);
//		});

		for (int i = 0; i < xmlSMSList.size(); i++) {
			SMSResp resp = null;
			try {
				
				
				String xmlSMS = xmlSMSList.get(i);
				log.debug("### XML :\r\n {}", xmlSMS);
				
				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("xmlData", xmlSMS);

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				ResponseEntity<String> responseEntity = rest.exchange(uri.toString(), HttpMethod.POST, request,String.class);
				log.debug("### responseCode : [{}]", responseEntity.getStatusCode());

				if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
					log.debug("#####Submit Success - ");
				} else {
					log.debug("#####Submit Fail- ");
					log.debug("resp : {}", responseEntity.getBody());
					throw new SMSException(format, 2001);
				}

				// string xml to obj
//				InputStream inStream = conn.getInputStream();
//				String respXml = CharStreams.toString(new InputStreamReader(inStream, StandardCharsets.UTF_8));
				
				String respXml = responseEntity.getBody();
				resp = (SMSResp) MQXmlUtil.XmlToObject(respXml, SMSResp.class);

				log.info("respMsgId:{}, respCode:{}, timestamp:{}", resp.getMessageId(), resp.getResultCode(),
						resp.getTimestamp());

				if (!resp.getResultCode().equals(ApiConstant.RC_SUCCESS)) {
					throw new SMSException(LegacyConstant.FORMAT_TWO,
							LegacyConstant.HTTPAPI_RESP_ERROR_MAP_FORMAT_2.get(resp.getResultCode()));
				}

				// record imqMsgId, target token to db if dr is required
				SMS.Message imqMsg = msg.getBody().getMessage().get(i);
				if (imqMsg.isDrFlag()) {
					for (SMS.Message.Target target : imqMsg.getTarget()) {
						String parsedTarget = parseTarget(target.getValue());
						if (parsedTarget == null) {
							throw new SMSException(LegacyConstant.FORMAT_ONE,
									LegacyConstant.FORMAT_1_ERROR.get(FIELD.TARGET));
						}

						ImqSmsRecord record = new ImqSmsRecord();
						ImqSmsRecordPk pk = new ImqSmsRecordPk(resp.getMessageId(), parsedTarget);
						record.setImqMsgId(msg.getImqMsgId());
						record.setPk(pk);
						record.setImqToken(target.getToken());
						record.setLanguage(imqMsg.getLanguage());
						log.info("DrFlag[true]. Record ImqMsg. MsgId[{}], Da[{}]", resp.getMessageId(), parsedTarget);
						try {
							imqSmsRecordManager.save(ImqSmsRecord.class, record);
						} catch (DataAccessException e) {
						}
					}
				}

			} catch (SMSException e) {
				e.setCorreId(msg.getImqMsgId());
				throw e;
			} catch (Exception e) {
				throw new SMSException(format, 2001, true, msg.getImqMsgId());
			} finally {
//				conn.disconnect();
				log.debug("End httpApi submission.");
			}
		}

	}

	private static String parseTarget(String target) {
		String result = null;
		if (RegexUtil.matchAny(target, LegacyConstant.PREFIX_CODE_REG_EX[0])) {
			// add 886
			result = "886" + target;
		} else if (RegexUtil.matchAny(target, LegacyConstant.PREFIX_CODE_REG_EX[1])) {
			// remove '0', add 886
			result = "886" + target.substring(1);
		} else if (RegexUtil.matchAny(target, LegacyConstant.PREFIX_CODE_REG_EX[2])) {
			result = target;
		} else if (RegexUtil.matchAny(target, LegacyConstant.PREFIX_CODE_REG_EX[3])) {
			// remove '+'
			result = target.substring(1);
		}
		return result;
	}
}
