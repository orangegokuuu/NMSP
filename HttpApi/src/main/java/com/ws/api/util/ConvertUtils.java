package com.ws.api.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ws.emg.constant.ApiConstant;
import com.ws.emg.constant.SmppConstant;
import com.ws.emg.pojo.MessageObject;
import com.ws.httpapi.pojo.BatchRetrieveDRResp;
import com.ws.httpapi.pojo.DeliverSM;
import com.ws.httpapi.pojo.PushDR;
import com.ws.httpapi.pojo.QueryDRResp;
import com.ws.httpapi.pojo.RetrieveDRResp;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;

public class ConvertUtils {

	private static Logger logger = LogManager.getLogger(ConvertUtils.class);

	public static RetrieveDRResp convertToRetrieveDRResp(SmsRecord sms, RetrieveDRResp resp) {
		try {
			resp.setMessageId(sms.getReqMsgId());
			resp.setBNumber(sms.getDa());
			// resp.setStatus(sms.getSubs().get(0).getDeliverStatus());
			// XXX remark by matthew 2018-05-30
			if (!sms.getAcceptStatus().equals(ApiConstant.SC_SUCCESS)) {
				resp.setStatus(sms.getAcceptStatus());
			}
			// else{
			// String array[] =
			// getDeliverStateAndErrCode(sms.getSubs().get(0).getDeliverStatus());
			// if(array!=null && array.length > 0){
			// resp.setStatus(ApiConstant.httpApiStatusMapping(array[0]));
			// }
			// }
			// XXX end remark
			resp.setTimestamp(HttpApiUtils.getTimestampForXml(sms.getAcceptDate()));
			int total = sms.getSubs().size();
			int count = 1;
			int errorcode = 0;
			// SmsRecordSub sub = sms.getSubs().get(0);
			for (SmsRecordSub sub : sms.getSubs()) {
				String array[] = getDeliverStateAndErrCode(sub.getDeliverStatus());
				// XXX add by matthew 2018-05-30
				if (array != null && !array[0].equals("DELIVRD")) {
					resp.setStatus(ApiConstant.httpApiStatusMapping(array[0]));
				}
				if (count == total) {
					if (StringUtils.isBlank(resp.getStatus())) {
						resp.setStatus(ApiConstant.httpApiStatusMapping(array[0]));
					}
				}
				count++;
				// XXX end add
				if (sub.getDrRespDate() == null && sub.getDeliverDate() != null) {
					RetrieveDRResp.DeliveryReport deliveryReport = new RetrieveDRResp.DeliveryReport();
					deliveryReport.setSeq(sub.getSegNum());
					deliveryReport.setTotal(String.valueOf(total));
					// deliveryReport.setId(sub.getSmscMsgId());
					deliveryReport.setId("");
					deliveryReport.setSubmitDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getSubmitDate()));
					deliveryReport.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getDeliverDate()));
					// deliveryReport.setState(sub.getDeliverStatus());
					// deliveryReport.setError(sub.getSubmitStatus());
					// XXX modify by matthew 2018-03-23
					if (array != null && array.length > 1) {
						deliveryReport.setState(array[0]);
						errorcode = getGsmCode2Int(array[1]);
						deliveryReport.setError(String.valueOf(errorcode));
					} else if (array != null) {
						deliveryReport.setState(array[0]);
					}
					// XXX end modify
					resp.getDeliveryReport().add(deliveryReport);
				}
			}
		} catch (Exception e) {
			logger.error("[RUNTIME] convertToRetrieveDRResp error:[{}]", e.getMessage());
			logger.error(e, e);
			resp = null;
		}
		return resp;
	}

	public static BatchRetrieveDRResp convertBatchRetrieveDRResp(List<SmsRecord> smsList, BatchRetrieveDRResp resp) {
		try {
			for (SmsRecord sms : smsList) {
				resp.setTimestamp(HttpApiUtils.getTimestampForXml(sms.getAcceptDate()));
				int total = sms.getSubs().size();
				for (SmsRecordSub sub : sms.getSubs()) {
					if (sub.getDrRespDate() == null && sub.getDeliverDate() != null) {
						BatchRetrieveDRResp.DeliveryReport deliveryReport = new BatchRetrieveDRResp.DeliveryReport();
						deliveryReport.setSeq(sub.getSegNum());
						deliveryReport.setTotal(String.valueOf(total));
						// deliveryReport.setId(sub.getWsMsgId());
						deliveryReport.setId("");
						deliveryReport.setBNumber(sms.getDa());
						deliveryReport.setSubmitDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getSubmitDate()));
						deliveryReport.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getDeliverDate()));
						// deliveryReport.setState(sub.getDeliverStatus());
						// deliveryReport.setError(sub.getSubmitStatus());
						// XXX modify by matthew 2018-03-23
						String array[] = getDeliverStateAndErrCode(sub.getDeliverStatus());
						int errorCode = 0;
						if (array != null && array.length > 1) {
							deliveryReport.setState(array[0]);
							errorCode = getGsmCode2Int(array[1]);
							deliveryReport.setError(String.valueOf(errorCode));
						} else if (array != null) {
							deliveryReport.setState(array[0]);
						}
						// XXX end modify
						resp.getDeliveryReport().add(deliveryReport);
					}
				}
			}
		} catch (Exception e) {
			logger.error("[RUNTIME] convertBatchRetrieveDRResp error:[{}]", e.getMessage());
			logger.error(e, e);
			resp = null;
		}
		return resp;
	}

	public static QueryDRResp convertQueryDRResp(List<SmsRecord> smsList, QueryDRResp resp, Date nowDate) {
		try {
			for (SmsRecord sms : smsList) {
				QueryDRResp.Message message = new QueryDRResp.Message();
				message.setMessageId(sms.getReqMsgId());
				// message.setStatus(sms.getAcceptStatus());
				if (!sms.getAcceptStatus().equals(ApiConstant.SC_SUCCESS)) {
					message.setStatus(sms.getAcceptStatus());
				} else {
					// message.setStatus(ApiConstant.httpApiStatusMapping(sms.getSubs().get(0).getDeliverStatus()));
					if (sms.getSubs() != null) {
						for (SmsRecordSub sub : sms.getSubs()) {
							String array[] = getDeliverStateAndErrCode(sub.getDeliverStatus());
							if (StringUtils.isBlank(message.getStatus())) {
								if (array != null && array.length > 0) {
									message.setStatus(ApiConstant.httpApiStatusMapping(array[0]));
								}
							} else if (message.getStatus().equals(ApiConstant.SC_MSG_SEND_SMSC)) {
								if (array != null && array.length > 0) {
									message.setStatus(ApiConstant.httpApiStatusMapping(array[0]));
								}
							}
						}
					}
				}
				message.setBNumber(sms.getDa());
				message.setTimestamp(HttpApiUtils.getTimestampForXml(nowDate));
				QueryDRResp.Message.DeliveryReport deliveryReport = new QueryDRResp.Message.DeliveryReport();
				QueryDRResp.Message.DRDetail drDetail = new QueryDRResp.Message.DRDetail();
				int count = 1;
				for (SmsRecordSub sub : sms.getSubs()) {
					// DeliverReport
					// deliveryReport.setId(sub.getWsMsgId());
					deliveryReport.setId("");
					String array[] = getDeliverStateAndErrCode(sub.getDeliverStatus());
					String state = null;
					int errcode = 0;
					if (array != null && array.length > 1) {
						state = array[0];
						errcode = getGsmCode2Int(array[1]);
					} else if (array != null) {
						state = array[0];
					}
					if (state != null && !state.equals("DELIVRD")) {
						deliveryReport.setState(state);
						deliveryReport.setError(String.valueOf(errcode));
					}
					if (count == sms.getSubs().size()) {
						deliveryReport.setSubmitDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getSubmitDate()));
						deliveryReport.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getDeliverDate()));
						if (StringUtils.isBlank(deliveryReport.getState())) {
							deliveryReport.setState(state);
						}
						if (StringUtils.isBlank(deliveryReport.getError())) {
							deliveryReport.setError(String.valueOf(errcode));
						}
					}
					count++;
					// Detail
					QueryDRResp.Message.DRDetail.Detail detail = new QueryDRResp.Message.DRDetail.Detail();
					detail.setSeqNo(sub.getSegNum());
					detail.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getDeliverDate()));
					detail.setState(state);
					drDetail.getDetail().add(detail);
				}
				message.setDeliveryReport(deliveryReport);
				message.setDRDetail(drDetail);
				resp.getMessage().add(message);
			}
		} catch (Exception e) {
			logger.error("[RUNTIME] convertQueryDRResp error:[{}]", e.getMessage());
			logger.error(e, e);
			resp = null;
		}
		return resp;
	}

	public static PushDR convertToPushDR(MessageObject obj, SmsRecord sms) {
		PushDR dr = null;
		boolean delivered = true;
		try {
			dr = new PushDR();
			dr.setMessageId(sms.getReqMsgId());
			// dr.setBNumber(obj.getDestination());
			dr.setBNumber(sms.getDa());
			// dr.setTimestamp(HttpApiUtils.formatDate("yyMMddHHmm",new Date()));
			dr.setTimestamp(HttpApiUtils.getTimestampForXml(new Date()));
			PushDR.DeliveryReport deliveryReport = new PushDR.DeliveryReport();
			// deliveryReport.setId(sms.getWsMsgId());
			deliveryReport.setId(sms.getSysId());
			deliveryReport.setSubmitDate(HttpApiUtils.formatDate("yyMMddHHmm", sms.getSubs().get(0).getSubmitDate()));
			deliveryReport.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sms.getSubs().get(0).getDeliverDate()));
			deliveryReport.setState(obj.getState());
			// deliveryReport.setError(obj.getErrorCode());
			deliveryReport.setError(String.valueOf(getGsmCode2Int(obj.getErrorCode())));
			PushDR.DRDetail dRDetail = new PushDR.DRDetail();
			for (SmsRecordSub sub : sms.getSubs()) {
				PushDR.DRDetail.Detail detail = new PushDR.DRDetail.Detail();
				detail.setSeqNo(sub.getSegNum());
				String stat = "";
				try {
					stat = getDeliverStateAndErrCode(sub.getDeliverStatus())[0];
				} catch (Exception e) {
					stat = "UNKNOWN";
					logger.warn("[PushDR] Get sub deliver stat fail, smsc msg id : [{}]", sub.getSmscMsgId());
				}
				if (StringUtils.isNotBlank(stat)) {
					detail.setState(stat);
				}
				if (StringUtils.isNotBlank(sub.getDeliverStatus())) {
					if (StringUtils.isBlank(stat) || !stat.equals(SmppConstant.RC_DELIVRD)) {
						delivered = false;
					}
				}
				detail.setDoneDate(HttpApiUtils.formatDate("yyMMddHHmm", sub.getDeliverDate()));
				dRDetail.getDetail().add(detail);
			}
			if (delivered) {
				dr.setStatus(ApiConstant.SC_MSG_DELIVER);
			} else {
				dr.setStatus(ApiConstant.SC_MSG_FAILED_DELIVER);
			}
			dr.setDeliveryReport(deliveryReport);
			dr.setDRDetail(dRDetail);
		} catch (Exception e) {
			logger.error("[RUNTIME] convertToPushDR error:[{}]", e.getMessage());
			logger.error(e, e);
			throw e;
		}
		return dr;
	}

	public static DeliverSM convertToDeliverSM(MessageObject obj) {
		DeliverSM sm = null;
		try {
			sm = new DeliverSM();
			sm.setSysId(obj.getCpId());
			DeliverSM.Message message = new DeliverSM.Message();
			message.setTarget(obj.getDestination());
			message.setSource(obj.getSource());
			if (obj.getDataCoding() == 8) {
				message.setLanguage("C");
			} else if (obj.getDataCoding() == 0 || obj.getDataCoding() == 1 || obj.getDataCoding() == 3) {
				message.setLanguage("E");
			} else {
				message.setLanguage("C");
			}
			message.setText(obj.getMessage());
			message.setTimestamp(obj.getSubmitTime());
			sm.setMessage(message);
		} catch (Exception e) {
			logger.error("[RUNTIME] convertToDeliverSM error:[{}]", e.getMessage());
			logger.error(e, e);
			throw e;
		}
		return sm;
	}

	private static String[] getDeliverStateAndErrCode(String value) {

		try {
			logger.info("getDeliverStateAndErrCode  value:[{}]", value);

			if(StringUtils.isBlank(value)){
				logger.debug("[RUNTIME] use default value [UNDELIV;500]");
				value = "UNDELIV;500";
			}
			String[] array = value.split(";");
			return array;
		}catch(Exception e){
			logger.error("[RUNTIME] getDeliverStateAndErrCode error , error_msg:[{}] ", e.getMessage());
			logger.error(e, e);
			return null;
		}
	}

	/**
	 * Get integer value for GSM error code and give unknown error code = 500 while
	 * parse error
	 * 
	 * @param value
	 * @return
	 */
	private static int getGsmCode2Int(String value) {

		int result = 500;

		try {
			result = Integer.parseInt(value);
		} catch (Exception e) {
			logger.warn("[PushDR] parse gsm error code fail, use unknown = [500]");
		}

		return result;
	}
}
