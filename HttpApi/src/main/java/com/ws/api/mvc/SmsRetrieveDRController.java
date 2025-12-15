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
import org.hibernate.criterion.Order;
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
import com.ws.api.util.HttpApiUtils;
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.emg.util.EmgParser;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.RetrieveDR;
import com.ws.httpapi.pojo.RetrieveDRResp;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.SmsRecordManager;

@Controller
@RequestMapping("/api")
public class SmsRetrieveDRController {

	private static Logger logger = LogManager.getLogger(SmsRetrieveDRController.class);

	@Autowired
	private MspProperties properties;

	// @Autowired
	// @Qualifier("mtExcutor")
	// private TaskExecutor taskExecutor = null;
	//
	// @Autowired
	// private Provider<Producer> workerProvider = null;

	@Autowired
	private EmgParser parser;

	@Autowired
	ChackUtils chackUtils;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Autowired
	ContentProviderManager contentProviderManager;

	@Autowired
	XmlUtils xmlUtils;

	@RequestMapping(value = "/SmsRetrieveDR", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String request(@RequestParam("xmlData") String xml, HttpServletRequest request) {

		String result = "";
		String resultCode = "";
		RetrieveDRResp resp = new RetrieveDRResp();
		Date nowDate = new Date();
		RetrieveDR dr = null;
		ContentProvider cp = null;
		try {
			// validate Xml
			// resultCode = xmlUtils.validateXmlFormat(xml, "RetrieveDR");
			// if(resultCode.equals("")){
			// //xml convert to object
			// dr = (RetrieveDR)xmlUtils.XmlToObject(xml, RetrieveDR.class);
			// //check xml format
			// resultCode = xmlUtils.checkSmsRetrieve(dr);
			// }
			dr = (RetrieveDR) xmlUtils.XmlToObject(xml, RetrieveDR.class);
			resultCode = xmlUtils.checkSmsRetrieve(dr);
			// Authentication check
			if (resultCode.equals("")) {
				// get ContentProvider
				cp = contentProviderManager.get(ContentProvider.class, dr.getSysId());
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
				subDc.add(Restrictions.isNotNull("deliverDate"));
				dc.add(Restrictions.eq("sysId", dr.getSysId()));
				dc.add(Restrictions.eq("drFlag", true));
				// dc.add(Restrictions.isNull("drRespDate"));
				// dc.add(Restrictions.eq("acceptStatus", ApiConstant.SC_SUCCESS));
				dc.add(Restrictions.isNotNull("acceptStatus"));
				// add by matthew 2018-09-17
				// dc.add(Restrictions.ge("createDate", HttpApiUtils.getDateForAddOrSub(-14)));
				// modify by YC 20230612 for date to localdatetime
				dc.add(Restrictions.ge("createDate", HttpApiUtils.date2LocalDateTime(HttpApiUtils.getDateForAddOrSub(-14))));
				// end add
				dc.addOrder(Order.asc("createDate"));
				dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				List<SmsRecord> smsList = smsRecordManager.findByCriteria(SmsRecord.class, dc);
				logger.debug("==== RetrieveDR select data size:[{}]", smsList.size());
				if (smsList != null && smsList.size() > 0) {
					SmsRecord sms = smsList.get(0);
					// smsRecord convert to RetrieveDRResp
					resp = ConvertUtils.convertToRetrieveDRResp(sms, resp);
					if (resp != null) {
						// update db
						// sms.setDrRespDate(nowDate);
						// smsRecordManager.update(SmsRecord.class, sms);
						for (SmsRecordSub sub : sms.getSubs()) {
							if (sub.getDrRespDate() == null && sub.getDeliverDate() != null) {
								logger.debug("==== RetrieveDR update sub_id:[{}]", sub.getSubId());
								sub.setDrRespDate(nowDate);
								smsRecordManager.update(SmsRecordSub.class, sub);
								break;
							}
						}
					} else {
						resp = new RetrieveDRResp();
					}
					//
					resp.setResultCode(ApiConstant.RC_SUCCESS);
					// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
				} else {
					resp.setResultCode(ApiConstant.RC_SUCCESS);
					// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
				}
			} else {
				resp.setResultCode(resultCode);
				// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			}

		} catch (JAXBException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
		} catch (SAXException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
		} catch (ParserConfigurationException e) {
			logger.warn(e, e);
			resp.setResultCode(ApiConstant.RC_INVALID_XML);
			// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
		} catch (DataAccessException e) {
			logger.error("[DB] error:[{}]", e.getMessage());
			logger.error(e, e);
			resp.setResultCode(ApiConstant.RC_DB_ERROR);
		} finally {
			// object convert to xml
			try {
				result = xmlUtils.ObjectToXml(resp, RetrieveDRResp.class);
			} catch (JAXBException e) {
				logger.warn(e, e);
				resp.setResultCode(ApiConstant.RC_INVALID_XML);
				// resp.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
			}
			if (dr != null) {
				logger.info("[SmsRetrieve] from [{}] result code : [{}], sysId : [{}], retrieve time : [{}]",
						request.getRemoteAddr(),
						resp.getResultCode(), dr.getSysId(), nowDate);
			} else {
				logger.info("[SmsRetrieve] form [{}] result code : [{}] , xml:[{}]", request.getRemoteAddr(),
						resp.getResultCode(), xml);
			}
		}

		return result;
	}

}
