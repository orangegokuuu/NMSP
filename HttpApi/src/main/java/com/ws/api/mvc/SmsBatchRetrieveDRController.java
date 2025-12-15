package com.ws.api.mvc;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.ws.api.util.ChackUtils;
import com.ws.api.util.ConvertUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.BatchRetrieveDR;
import com.ws.httpapi.pojo.BatchRetrieveDRResp;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.SmsRecordManager;

@Controller
@RequestMapping("/api")
public class SmsBatchRetrieveDRController {

	private static Logger logger = LogManager.getLogger(SmsBatchRetrieveDRController.class);

	@Autowired
	private MspProperties properties;

	// @Autowired
	// @Qualifier("mtExcutor")
	// private TaskExecutor taskExecutor = null;
	//
	// @Autowired
	// private Provider<Producer> workerProvider = null;

	// @Autowired
	// private EmgParser parser;

	@Autowired
	ChackUtils chackUtils;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Autowired
	ContentProviderManager contentProviderManager;

	@Autowired
	XmlUtils xmlUtils;

	@RequestMapping(value = "/SmsBatchRetrieveDR", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String request(@RequestParam("xmlData") String xml, HttpServletRequest request) {

		String result = "";
		String resultCode = "";
		BatchRetrieveDRResp resp = new BatchRetrieveDRResp();
		Date nowDate = new Date();
		BatchRetrieveDR dr = null;
		ContentProvider cp = null;
		try {
			// validate Xml
			// resultCode = xmlUtils.validateXmlFormat(xml, "BatchRetrieveDR");
			// if(resultCode.equals("")){
			// //xml convert to object
			// dr = (BatchRetrieveDR)xmlUtils.XmlToObject(xml, BatchRetrieveDR.class);
			// //check xml format
			// resultCode = xmlUtils.checkSmsBatchRetrieve(dr);
			// }
			dr = (BatchRetrieveDR) xmlUtils.XmlToObject(xml, BatchRetrieveDR.class);
			resultCode = xmlUtils.checkSmsBatchRetrieve(dr);
			if (resultCode.equals("")) {
				// get ContentProvider
				cp = contentProviderManager.get(ContentProvider.class, dr.getSysId());
				// Authentication check
				if (cp == null) {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				} else if (cp.STATUS_INACTIVE.equals(cp.getStatus())) { // check cp status
					resultCode = ApiConstant.RC_INVALID_SYSID;
					logger.info("from [{}] cp sysid:[{}] ,status:[{}] ", request.getRemoteAddr(), dr.getSysId(),
							cp.getStatus());
				} else {
					// check sysid
					resultCode = chackUtils.checkCpSysId(dr.getSysId(), cp);
				}
			}

			if (resultCode.equals("")) {
				// get dr from db
				DetachedCriteria dc = DetachedCriteria.forClass(SmsRecord.class);
				DetachedCriteria subDc = dc.createCriteria("subs");
				subDc.add(Restrictions.isNull("drRespDate"));
				dc.add(Restrictions.eq("sysId", dr.getSysId()));
				dc.add(Restrictions.eq("reqMsgId", dr.getMessageId()));
				dc.add(Restrictions.eq("drFlag", true));
				// dc.add(Restrictions.isNull("drRespDate"));
				// dc.add(Restrictions.eq("acceptStatus", ApiConstant.SC_SUCCESS));
				dc.add(Restrictions.isNotNull("acceptStatus"));
				dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				List<SmsRecord> smsList = smsRecordManager.findByCriteria(SmsRecord.class, dc);
				logger.debug("==== BatchRetrieveDR select data size:[{}]", smsList.size());
				if (smsList != null && smsList.size() > 0) {
					// smsRecord convert to BatchRetrieveDRResp
					resp = ConvertUtils.convertBatchRetrieveDRResp(smsList, resp);
					if (resp != null) {
						// update db
						for (SmsRecord sms : smsList) {
							// sms.setDrRespDate(nowDate);
							// smsRecordManager.update(SmsRecord.class, sms);
							for (SmsRecordSub sub : sms.getSubs()) {
								if (sub.getDrRespDate() == null && sub.getDeliverDate() != null) {
									sub.setDrRespDate(nowDate);
									smsRecordManager.update(SmsRecordSub.class, sub);
								}
							}
						}
					} else {
						resp = new BatchRetrieveDRResp();
					}
					//
					resp.setResultCode(ApiConstant.RC_SUCCESS);
					resp.setMessageId(dr.getMessageId());

				} else {
					resp.setResultCode(ApiConstant.RC_SUCCESS);
					resp.setMessageId(dr.getMessageId());
				}

			} else {
				resp.setResultCode(resultCode);
				if (dr != null && dr.getMessageId() != null) {
					resp.setMessageId(dr.getMessageId());
				}
			}

		} catch (JAXBException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
		} catch (SAXException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
		} catch (ParserConfigurationException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
		} catch (DataAccessException e) {
			logger.error("[DB] error:[{}]", e.getMessage());
			logger.error(e, e);
			resp.setResultCode(ApiConstant.RC_DB_ERROR);
		} finally {
			// object convert to xml
			try {
				result = xmlUtils.ObjectToXml(resp, BatchRetrieveDRResp.class);
			} catch (JAXBException e) {
				logger.warn(e, e);
				resp.setResultCode(ApiConstant.RC_INVALID_XML);
			}
			if (dr != null) {
				logger.info(
						"[SmsBatchRetrieve] from [{}] result code : [{}], sysId : [{}], retrieve date : [{}], message id : [{}]",
						request.getRemoteAddr(),
						resp.getResultCode(), dr.getSysId(), nowDate, dr.getMessageId());
			} else {
				logger.info("[SmsBatchRetrieve] from [{}] result code : [{}] , xml:[{}]", request.getRemoteAddr(),
						resp.getResultCode(), xml);
			}
		}

		return result;
	}
}
