package com.ws.api.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.ws.api.util.XmlUtils;
import com.ws.emg.constant.ApiConstant;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.httpapi.pojo.QueryDR;
import com.ws.httpapi.pojo.QueryDRResp;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.service.ContentProviderManager;
import com.ws.msp.service.QuotaManager;
import com.ws.msp.service.SmsRecordManager;

@Controller
@RequestMapping("/api")
public class SmsQueryDRController {

	private static Logger logger = LogManager.getLogger(SmsQueryDRController.class);

	@Autowired
	private MspProperties properties;

	@Autowired
	private QuotaManager quotaManager;

	@Autowired
	ChackUtils chackUtils;

	@Autowired
	SmsRecordManager smsRecordManager;

	@Autowired
	ContentProviderManager contentProviderManager;

	@Autowired
	XmlUtils xmlUtils;

	@RequestMapping(value = "/SmsQueryDR", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String request(@RequestParam("xmlData") String xml, HttpServletRequest request) {

		String result = "";
		String resultCode = "";
		QueryDRResp resp = new QueryDRResp();
		Date nowDate = new Date();
		// String apiVersion = "";
		QueryDR dr = null;
		ContentProvider cp = null;
		try {
			// validate Xml
			// resultCode = xmlUtils.validateXmlFormat(xml, "QueryDR");
			// if(resultCode.equals("")){
			// //xml convert to object
			// dr = (QueryDR)xmlUtils.XmlToObject(xml, QueryDR.class);
			// //check xml format
			// resultCode = xmlUtils.checkSmsQueryDR(dr);
			// }
			dr = (QueryDR) xmlUtils.XmlToObject(xml, QueryDR.class);
			resultCode = xmlUtils.checkSmsQueryDR(dr);
			if (resultCode.equals("")) {
				// get ContentProvider
				cp = contentProviderManager.get(ContentProvider.class, dr.getSysId());
				// Authentication check
				if (cp == null) {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				} else if (cp.STATUS_INACTIVE.equals(cp.getStatus())) { // check cp status
					resultCode = ApiConstant.RC_INVALID_SYSID;
					logger.info("from [{}] cp sysid:[{}] ,status:[{}] ", request.getRemoteAddr(), dr.getSysId(), cp.getStatus());
				}
				// else{
				// //check sysid
				// resultCode = chackUtils.checkCpSysId(dr.getSysId(),cp);
				// }
			}
			// Query Through put check
			if (resultCode.equals("") && !quotaManager.checkQueryLimit(cp.getCpId(), 1)) {
				resultCode = ApiConstant.RC_THROUGHTPUT_EXCEED;
			}
			if (resultCode.equals("")) {
				// get cp profile apiVersion
				// apiVersion =
				// cp.getApiVersion();//contentProviderManager.get(ContentProvider.class,
				// dr.getSysId()).getApiVersion();
				// get dr from db
				DetachedCriteria dc = DetachedCriteria.forClass(SmsRecord.class);
				dc.add(Restrictions.eq("sysId", dr.getSysId()));
				dc.add(Restrictions.eq("reqMsgId", dr.getMessageId()));
				// 2018-03-26 add by matthew
				if (StringUtils.isNotBlank(dr.getBNumber())) {
					dc.add(Restrictions.eq("da", dr.getBNumber()));
				}
				// end add
				// dc.add(Restrictions.isNull("drRespDate"));
				// dc.add(Restrictions.eq("acceptStatus", ApiConstant.SC_SUCCESS));
				dc.add(Restrictions.eq("drFlag", true));
				dc.add(Restrictions.isNotNull("acceptStatus"));
				dc.addOrder(Order.desc("createDate"));
				List<SmsRecord> smsList = smsRecordManager.findByCriteria(SmsRecord.class, dc, 0, 100);
				logger.debug("==== QueryDR select data size:[{}]", smsList.size());
				if (smsList != null && smsList.size() > 0) {

					if (dr.getType().equals("01")) { // list all
						// smsRecord convert to QueryDRResp
						resp = ConvertUtils.convertQueryDRResp(smsList, resp, nowDate);
						// update db all dr
						for (SmsRecord sms : smsList) {
							// sms.setDrRespDate(nowDate);
							// smsRecordManager.update(SmsRecord.class, sms);
							for (SmsRecordSub sub : sms.getSubs()) {
								sub.setDrRespDate(nowDate);
								smsRecordManager.update(SmsRecordSub.class, sub);
							}
						}
					} else { // last one , check b number in list
						List<SmsRecord> oneList = new ArrayList<SmsRecord>();
						if (StringUtils.isNotBlank(dr.getBNumber())) {
							for (SmsRecord sms : smsList) {
								if (sms.getDa().equals(dr.getBNumber())) {
									oneList.add(sms);
									break;
								}
							}
						} else { // b number is null , get list one record
							for (SmsRecord sms : smsList) {
								oneList.add(sms);
								break;
							}
						}
						// smsRecord convert to QueryDRResp
						resp = ConvertUtils.convertQueryDRResp(oneList, resp, nowDate);
						if (resp != null) {
							// update db all dr
							for (SmsRecord sms : oneList) {
								// sms.setDrRespDate(nowDate);
								// smsRecordManager.update(SmsRecord.class, sms);
								for (SmsRecordSub sub : sms.getSubs()) {
									sub.setDrRespDate(nowDate);
									smsRecordManager.update(SmsRecordSub.class, sub);
								}
							}
						} else {
							resp = new QueryDRResp();
						}
					}
					//
					resp.setResultCode(ApiConstant.RC_SUCCESS);

					/*
					 * if(daInList){ 20171128
					 * 
					 * } else{ resp.setResultCode(ApiConstant.RC_SUCCESS);
					 * //resp.setResultCode(ApiConstant.RC_INVALID_B_PART); }
					 */
				} else {
					resp.setResultCode(ApiConstant.RC_SUCCESS);
					// resp.setResultCode(ApiConstant.RC_INVALID_MSG_ID);
				}
				quotaManager.processQueryDr(cp.getCpId(), 1);
			} else {
				resp.setResultCode(resultCode);
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
				result = xmlUtils.ObjectToXml(resp, QueryDRResp.class);
			} catch (JAXBException e) {
				logger.warn(e, e);
				resp.setResultCode(ApiConstant.RC_INVALID_XML);
			}
			if (dr != null) {
				logger.info(
						"[SmsQuery] from [{}] result code : [{}], sysId : [{}], query time : [{}], queried message id : [{}]",
						request.getRemoteAddr(),
						resp.getResultCode(), dr.getSysId(), nowDate, dr.getMessageId());
			} else {
				logger.info("[SmsQuery] from [{}] result code : [{}] , xml:[{}]", request.getRemoteAddr(),
						resp.getResultCode(), xml);
			}
		}

		return result;
	}

}
