package com.ws.api.httpclient;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.jms.JMSException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.ws.api.util.ChackUtils;
import com.ws.api.util.ConvertUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.SmppConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.DeliverSM;
import com.ws.httpapi.pojo.DeliverSMResp;
import com.ws.httpapi.pojo.PushDR;
import com.ws.httpapi.pojo.PushDRResp;
import com.ws.jms.service.JmsService;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.SmsRecordManager;
import lombok.extern.log4j.Log4j2;


@Component("httpClientDrHandle")
@Log4j2
public class HttpClientDrHandle {
	@Autowired
	private SmsRecordManager smsRecordManager;

	@Autowired
	private ContentProviderManager contentProviderManager;

	@Autowired
	private ChackUtils chackUtils;

	@Autowired
	private JmsService jmsService;

	@Autowired
	private XmlUtils xmlUtils;

	@Autowired
	private MspProperties properties;
	
	@Autowired
	private RestTemplate customRestTemplate;  //add by Matthew 20190520, set RestTemplate time out 

	public void processPushDr(MessageObject msg, String queueName) {
		boolean isUpdate = false;
		SmsRecord sms = null;
		Date nowDate = new Date();
		try {
			// get cp profile dr url
			String url = "";
			String cpName = "";
			String sysId = "";
			DetachedCriteria dcCp = DetachedCriteria.forClass(ContentProvider.class);
			dcCp.add(Restrictions.eq("cpId", msg.getCpId()));
			List<ContentProvider> cpList = contentProviderManager.findByCriteria(ContentProvider.class, dcCp);
			if (cpList != null) {
				ContentProvider cp = cpList.get(0);

				if (cp != null) {
					url = cp.getPushDrUrl();
					cpName = cp.getCpName();
					sysId = cp.getCpId();
				}
			}
			log.debug("==== processPushDr PushDrUrl:[{}]", url);
			// getSmsRecord
			sms = chackUtils.checkSmsIsDelivered(msg.getWsMessageId());
			if (sms != null) {
				// smsRecord convert to PushDR
				PushDR dr = ConvertUtils.convertToPushDR(msg, sms);
				// convert to xml
				String xml = xmlUtils.ObjectToXml(dr, PushDR.class);
				// send xml to CP
				String response = this.sendToCP(url, xml, sysId, cpName);
				if (!response.equals("")) {
					PushDRResp resp = xmlUtils.XmlToObject(response, PushDRResp.class);
					if (resp.getResultCode().equals("00000")) {
						isUpdate = true;
					}
				}
			}
		} catch (DataAccessException e) {
			log.error("[DB] processPushDr Error:[{}]", e.getMessage());
			log.error(e, e);
		} catch (Exception e) {
			log.error("[RUNTIME] processPushDr Error:[{}]", e.getMessage());
			log.error(e, e);
		} finally {
			if (isUpdate && sms != null) {
				// update smsrecord dr_resp_date
				// sms.setDrRespDate(new Date());
				// smsRecordManager.update(SmsRecord.class, sms);
				for (SmsRecordSub sub : sms.getSubs()) {
					sub.setDrRespDate(nowDate);
					smsRecordManager.update(SmsRecordSub.class, sub);
				}
			} else if (!isUpdate && sms != null) {
				// enqueue to WSqueue
				if (msg.getRetry() < properties.getApi().getRetry().getCount()) {
					log.debug("==== processPushDr retry:[{}] time:[{}] SysId:[{}] WsMsgId:[{}] EnqueueName[{}]",
							msg.getRetry() + 1, new Date(), msg.getCpId(), msg.getWsMessageId(), queueName);
					msg.setRetry((msg.getRetry() + 1));
					try {
						//jmsService.sendDelayMsg(msg, queueName, properties.getApi().getRetry().getDelay(), 4);
						jmsService.sendDelayMessage(msg, queueName, properties.getApi().getRetry().getDelay(), 4);
					} catch (JMSException e) {
						log.error("[WISEMQ] sendDelayMsg Error");
						log.error(e, e);
					}
				} else {
					log.info("[PushDR] retry 3 times failed, SysId:[{}] WsMsgId:[{}]", msg.getCpId(),
							msg.getWsMessageId());
				}
			} else {
				// skip this
				log.info("[PushDR] sms data is null ,skip this one. SysId:[{}] WsMsgId:[{}]", msg.getCpId(),
						msg.getWsMessageId());
			}
		}
	}

	public void processDeliverSM(MessageObject msg, String queueName) {
		//boolean isUpdate = false;
		SmsRecord sms = null;
		String resultCode = "99999";
		try {
			// get cp profile dr url
			String url = "";
			String cpName = "";
			String sysId = "";
			ContentProvider cp = contentProviderManager.get(ContentProvider.class, msg.getCpId());
			if (cp != null) {
				url = cp.getDeliverSmUrl();
				cpName = cp.getCpName();
				sysId = cp.getCpId();
			}
			log.debug("==== processDeliverSM DeliverSmUrl:[{}]", url);
			// getSmsRecord
			sms = smsRecordManager.get(SmsRecord.class, msg.getWsMessageId());
			if (sms != null) {
				// smsRecord convert to DevliverSM
				DeliverSM dr = ConvertUtils.convertToDeliverSM(msg);
				// convert to xml
				String xml = xmlUtils.ObjectToXml(dr, DeliverSM.class);
				// update sms sub
				this.updateSmsRecordSub(sms, new Date(), "0", "2");
				// send xml to CP
				String response = this.sendToCP(url, xml, sysId, cpName);
				if (!response.equals("")) {
					DeliverSMResp resp = xmlUtils.XmlToObject(response, DeliverSMResp.class);
					resultCode = resp.getResultCode();
//					if (resultCode.equals("00000") || !resultCode.equals("99999")) {
//						isUpdate = true;
//					}
				}
			}

		} catch (DataAccessException e) {
			log.error("[DB] processDeliverSM Error:[{}]", e.getMessage());
			log.error(e, e);
		} catch (Exception e) {
			log.error("[RUNTIME] processDeliverSM Error:[{}]", e.getMessage());
			log.error(e, e);
		} finally {
			if (resultCode.equals("00000")) {
				// update smsrecordsub
				this.updateSmsRecordSub(sms, new Date(), SmppConstant.RC_DELIVRD, "3");
				sms.setUpdateDate(LocalDateTime.now());
				sms.setUpdateBy("SYSTEM");
				smsRecordManager.update(SmsRecord.class, sms);
			} else if (!resultCode.equals("99999")){
				this.updateSmsRecordSub(sms, new Date(), SmppConstant.RC_UNDELIV, "3");
				sms.setUpdateDate(LocalDateTime.now());
				sms.setUpdateBy("SYSTEM");
				smsRecordManager.update(SmsRecord.class, sms);
			} else {
				// retry 3 times ,enqueue to WSqueue
				if (msg.getRetry() < properties.getApi().getRetry().getCount()) {
					log.debug("==== processDeliverSM retry:[{}], time:[{}], EnqueueName[{}]", msg.getRetry() + 1,
							new Date(), queueName);
					msg.setRetry((msg.getRetry() + 1));
					try {
						//jmsService.sendDelayMsg(msg, queueName, properties.getApi().getRetry().getDelay(), 4);
						jmsService.sendDelayMessage(msg, queueName, properties.getApi().getRetry().getDelay(), 4);
					} catch (JMSException e) {
						log.error("[WISEMQ] sendDelayMsg Error");
						log.error(e, e);
					}
				} else if (sms != null) {
					this.updateSmsRecordSub(sms, new Date(), "127", "2");
					sms.setUpdateDate(LocalDateTime.now());
					sms.setUpdateBy("SYSTEM");
					smsRecordManager.update(SmsRecord.class, sms);
					log.info("[MO] retry 3 times failed, SysId:[{}] WsMsgId:[{}]", msg.getCpId(),
							msg.getWsMessageId());
				} else {
					log.info("[MO] error, sms_record is null, SysId:[{}] WsMsgId:[{}]", msg.getCpId(),
							msg.getWsMessageId());
				}
			}
		}
	}

	private String sendToCP(String url, String xml, String sysId, String cpName) {

		String result = "";
		//StringBuffer uri = new StringBuffer(url);
		try {
			//RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			//headers.add("Content-Type", "application/json");
			//headers.add("Accept", "*/*");
			//
			log.info("[PushToCP] Sysid:[{}] CpName:[{}], Url:[{}], xml[{}]", sysId, cpName, url, xml);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("xmlData", xml);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			//ResponseEntity<String> response = rest.exchange(url, HttpMethod.POST, request,String.class);
			//add by Matthew 20190520, set RestTemplate time out 
			ResponseEntity<String> response = customRestTemplate.exchange(url, HttpMethod.POST, request,String.class);
			
			log.debug("[PushToCP] Sysid:[{}] CpName:[{}], Url:[{}], StatusCode:[{}]", sysId, cpName, url, response.getStatusCode());
			
			//uri.append("{xml}");
			//HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
			//ResponseEntity<String> responseEntity = rest.exchange(uri.toString(), HttpMethod.GET, requestEntity,String.class, xml);
			
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				result = response.getBody().trim();
				log.info("[PushToCP] response.getBody:[{}]", result);
			}
		} catch (HttpClientErrorException e) {
			log.error("[PUSH] sendToCP HttpClientError:[{}]", e.getMessage());
			log.error(e, e);
		} catch (Exception e) {
			log.error("[PUSH] sendToCP Error:[{}]", e.getMessage());
			log.error(e, e);
		}
		return result;
	}

	private String sendToCP(String url, String xml) {
		return sendToCP(url, xml, "Unknown", "Unknown");
	}

	private void updateSmsRecordSub(SmsRecord sms, Date nowDate, String status, String step) {
		for (SmsRecordSub sub : sms.getSubs()) {
			log.debug("sub:[{}]", sub.toString());
			if ("2".equals(step)) {
				sub.setSubmitDate(nowDate);
				sub.setSubmitStatus(status);
			} else if ("3".equals(step)) {
				//add by matthew 2018-09-03
				if(status.equals(SmppConstant.RC_DELIVRD)){
					status = SmppConstant.RC_DELIVRD+";000";
				}
				else{
					status = SmppConstant.RC_UNDELIV+";500";
				}
				//end add
				sub.setDeliverDate(nowDate);
				sub.setDeliverStatus(status);
			}
			smsRecordManager.update(SmsRecordSub.class, sub);
		}
	}

	// public static void main(String a[]){
	// HttpClientDrHandle h = new HttpClientDrHandle();
	// System.out.println("result
	// :"+h.sendToCP("http://localhost:9090/test/deliversm?xml=", "123"));
	// }
}
