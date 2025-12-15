package com.ws.api.util;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ws.emg.constant.ApiConstant;
import com.ws.hibernate.exception.DataAccessException;
import com.ws.msp.config.MspProperties;
import com.ws.msp.pojo.ContentProvider;
import com.ws.msp.pojo.CpSourceAddress;
import com.ws.msp.pojo.MnpApiPhoneroutinginfo;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.pojo.SmsRecordSub;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.msp.pojo.TimeSlotData;
import com.ws.msp.pojo.TimeSlotDataPk;
import com.ws.msp.service.BlackListManager;
import com.ws.msp.service.FetPrefixManager;
import com.ws.msp.service.MnpApiPhoneroutinginfoManager;
import com.ws.msp.service.SmsRecordManager;
import com.ws.msp.service.SpamKeyWordManager;
import com.ws.msp.service.TimeTableManager;

@Component
public class ChackUtils {

	private static final Logger logger = LogManager.getLogger(ChackUtils.class);

	@Autowired
	private MspProperties properties;

	@Autowired
	private BlackListManager blackListManager;

	@Autowired
	private SpamKeyWordManager spamKeyWordManager;

	// @Autowired
	// ContentProviderManager contentProviderManager;

	@Autowired
	private TimeTableManager timeTableManager;

	@Autowired
	private MnpApiPhoneroutinginfoManager mnpApiPhoneroutinginfoManager;

	@Autowired
	private SmsRecordManager smsRecordManager;
	
	@Autowired
	private FetPrefixManager fetPrefixManager;

	@Autowired
	XmlUtils xmlUtils;

	/**
	 * Check sysId is correct
	 * 
	 * @param sysId
	 * @return resultCode
	 */
	public String checkCpSysId(String sysId, ContentProvider cp) {
		String resultCode = "";
		try {
			if (sysId != null && !sysId.equals("")) {
				// ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
				// DetachedCriteria dcCp = DetachedCriteria.forClass(ContentProvider.class);
				// dcCp.add(Restrictions.eq("cpId", sysId));
				// List<ContentProvider> cpList = contentProviderManager.findByCriteria(ContentProvider.class, dcCp);
				if (cp == null) {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				} else if (cp != null && !cp.getCpId().equals(sysId)) {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				}
				logger.debug("cp id:[{}],apiversion:[{}]", cp.getCpId(), cp.getApiVersion());
			} else {
				resultCode = ApiConstant.RC_MISS_SYSID;
				logger.debug("[checkCpSysId] sysId is null or empty");
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_INVALID_SYSID;
			logger.error("[checkCpSysId] error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * Check OA is correct
	 * 
	 * @param sysId
	 * @param oa
	 * @return resultCode
	 */
	public String checkOA(String sysId, String oa, ContentProvider cp) {
		String resultCode = "";
		try {
			if (sysId != null && !sysId.equals("")) {
				if (oa != null && !oa.equals("")) {
					// ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
					// boolean isMatch = true;
					for (CpSourceAddress cpSource : cp.getCpsaMap()) {

						logger.debug("cp sysid:[{}],source:[{}]", cpSource.getCpId(), cpSource.getSourceAddress());

						String regex = cpSource.getSourceAddress();
						// if (this.regexMatches(Pattern.quote(oa), Pattern.quote(regex))) { //fix snyk code issue
						if (this.regexMatches(oa, regex)) {
							logger.debug("cp source match oa, oa:[{}] source:[{}]", oa, regex);
							resultCode = "";
							break;
						} else {
							resultCode = ApiConstant.RC_INVALID_OA;
						}
					}
				} else {
					resultCode = ApiConstant.RC_PARAM_EMPTY;
					logger.debug("oa is null or empty");
				}
			} else {
				resultCode = ApiConstant.RC_MISS_SYSID;
				logger.debug("sysId is null or empty");
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_INVALID_OA;
			logger.error("checkOA error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * check da is black list or not
	 * 
	 * @param sysId
	 * @param da
	 * @return
	 */
	public String checkDABlackList(String sysId, String da, ContentProvider cp) {
		String resultCode = "";
		try {
			// ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
			logger.debug("cp id:[{}],isBlacklistCheckFl:[{}]", cp.getCpId(), cp.isBlacklistCheckFl());
			if (cp.isBlacklistCheckFl()) {
				resultCode = this.checkBlackList(da);
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_PARAM_EMPTY;
			logger.error("checkDABlackList error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * check da is black list or not
	 * 
	 * @param da
	 * @return resultCode
	 */
	public String checkBlackList(String da) {
		String resultCode = "";
		try {
			// da format change 09XX to 8869XX
//			if (da != null && da.length() == 10) {
//				da = "886" + da.substring(1, da.length());
//			} else if (da != null && da.length() == 13) {
//				da = da.substring(1, da.length());
//			}
			if (da != null && !da.equals("")) {
				// BlackList bl = blackListManager.get(BlackList.class, da);
				// if (bl != null && bl.getDestNumber().equals(da)) {
				// resultCode = ApiConstant.RC_INVALID_DA;
				// logger.info("Target is incorrect ,DA:[{}]", da);
				// }
				if (blackListManager.checkBlackListInCache(da)) {
					resultCode = ApiConstant.RC_INVALID_DA;
					logger.debug("Target is incorrect ,DA:[{}]", da);
				}
			} else {
				resultCode = ApiConstant.RC_PARAM_EMPTY;
				logger.debug("da is null or empty");
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_PARAM_EMPTY;
			logger.warn("checkBlackList error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * Check content contains spam keyword
	 * 
	 * @param sysId
	 * @param text
	 * @return
	 */
	public String checkSpamKeyWord(String sysId, String text, ContentProvider cp) {
		String resultCode = "";
		try {
			// ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
			logger.debug("cp id:[{}],isSpamCheckFl:[{}]", cp.getCpId(), cp.isSpamCheckFl());
			if (cp.isSpamCheckFl()) {
				resultCode = this.checkText(text);
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_UNKNOW_ERROR;
			logger.warn("checkSpamKeyWord error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * Check content contains spam keyword
	 * 
	 * @param text
	 * @return resultCode
	 */
	public String checkText(String text) {
		String resultCode = "";
		try {
			if (text != null && !text.equals("")) {
				//List<SpamKeyWord> list = spamKeyWordManager.listAll(SpamKeyWord.class);
				List<SpamKeyWord> list = spamKeyWordManager.getCacheSpamKeyWordList();
//				if(list.size() < 1){
//					logger.debug("get cache server spam keyword ,list size:[{}] ",list.size());
//					list = spamKeyWordManager.listAll(SpamKeyWord.class);
//					logger.debug("get db spam keyword ,list size:[{}] ",list.size());
//				}
				for (SpamKeyWord key : list) {
					if (key.getStatus().equals(SpamKeyWord.ACTIVE)) {
						if (text.indexOf(key.getKey()) > -1) {
							resultCode = ApiConstant.RC_SPAM_TEXT;
							logger.info("Text is rejected because it contains spam keywords ,text:[{}] keyword:[{}]",
							        text, key.getKey());
							break;
						}
					}
				}
			} else {
				resultCode = ApiConstant.RC_UNKNOW_ERROR;
				logger.debug("text is null or empty");
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_UNKNOW_ERROR;
			logger.warn("checkText error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	public String checkTextLength(String sysId, String text, String language, ContentProvider cp) {
		String resultCode = "";
		try {
			if (text != null && !text.equals("")) {
				// ContentProvider cp = contentProviderManager.get(ContentProvider.class, sysId);
				if (cp != null) {
					logger.debug("cp id:[{}],apiversion:[{}]", cp.getCpId(), cp.getApiVersion());
					if (xmlUtils.checkTextLength(text, language, cp.getApiVersion())) {
						resultCode = ApiConstant.RC_TEXT_TOO_LONG;
					}
				} else {
					resultCode = ApiConstant.RC_INVALID_SYSID;
				}
			} else {
				resultCode = ApiConstant.RC_PARAM_EMPTY;
				logger.debug("text is null or empty");
			}
		} catch (Exception e) {
			resultCode = ApiConstant.RC_PARAM_EMPTY;
			logger.warn("checkTextLength error:[{}]",e.getMessage());
		}
		return resultCode;
	}

	/**
	 * check send time
	 * 
	 * @param sysId
	 * @param now
	 * @return
	 */
	public String checkTimeTable(String sysId, Date now, ContentProvider cp) {
		String resultCode = "";
		try {

			if (cp.getTimeTableId() != null && !cp.getTimeTableId().equals("")) {
				//TimeTable timeTable = timeTableManager.get(TimeTable.class,cp.getTimeTableId());
				TimeSlotDataPk pk = new TimeSlotDataPk(cp.getTimeTableId(), HttpApiUtils.getDayOfWeek());
				//logger.debug("TimeTableId : [{}], DayOfWeek : [{}]", cp.getTimeTableId(), HttpApiUtils.getDayOfWeek());

				TimeSlotData timeData = timeTableManager.get(TimeSlotData.class, pk);
				logger.debug("XXXXXX TimeTableId : [{}], DayOfWeek : [{}] , TimeData : [{}]"
						, timeData.getPk().getTimeTableId(), HttpApiUtils.getDayOfWeek()
						,timeData.getSendTimeData());
				//TimeSlotDataPk pk = new TimeSlotDataPk(cp.getTimeTableId(), HttpApiUtils.getDayOfWeek());
				DetachedCriteria dc = DetachedCriteria.forClass(TimeSlotData.class);
				dc.add(Restrictions.eq("pk", pk));				
				List<TimeSlotData> list = timeTableManager.findByCriteria(TimeSlotData.class, dc);
				
				//TimeSlotData timeData = null;
				if(list !=null && list.size() > 0){
					timeData = list.get(0);
					logger.debug("TimeTableId : [{}], DayOfWeek : [{}] , TimeData : [{}]"
							, timeData.getPk().getTimeTableId(), HttpApiUtils.getDayOfWeek()
							,timeData.getSendTimeData());
				}
				if (timeData != null && StringUtils.isNotBlank(timeData.getSendTimeData())) {
					if (!TimeTableUtils.CheckSendTime(timeData.getSendTimeData(), now)) {
						resultCode = ApiConstant.RC_INVALID_TIME;
						logger.debug("Time not allowed");
					}
				} else {
					resultCode = ApiConstant.RC_INVALID_TIME;
					logger.info("TimeSlotData SendTimeData is null or empty");
				}
			} else {
				resultCode = ApiConstant.RC_INVALID_TIME;
				logger.info("TimeTableId is null or empty");
			}

		} catch(DataAccessException e){
			logger.error("[DB] checkTimeTable error:[{}] , sysId:[{}]", e.getMessage(),sysId);
			logger.error(e, e);
		} catch (Exception e) {
			resultCode = ApiConstant.RC_INVALID_TIME;
			logger.error("[RUNTIME] checkTimeTable error:[{}], sysId:[{}]",e.getMessage(),sysId);
		}
		return resultCode;
	}

	/**
	 * @param da
	 * @return
	 */
	public boolean checkMnp(String da) {
		boolean sw = false;
		String routingNumber[] = properties.getApi().getFet().getRouting_number().split(",");
		try {
			// da format change 8869XX to 09XX
			if (da != null && da.startsWith("886") && da.length() == 12) {
				da = "0" + da.substring(3, da.length());
			} else if (da != null && da.startsWith("+886") && da.length() == 13) {
				da = "0" + da.substring(4, da.length());
			}
			else if(da != null && da.startsWith("+")){
				logger.debug("==== check mnp da:[{}]", da);
				return true;
			}
			MnpApiPhoneroutinginfo info = mnpApiPhoneroutinginfoManager.getFromCache(da);
			if (info != null && !info.getPhoneNumber().equals("")) {
				for(String routing:routingNumber){
					logger.debug("info.ss7n:[{}] , properties routing:[{}]", info.getSs7Rn(),routing);
					if (info.getSs7Rn().equals(routing)) {
						sw = true;
						break;
					} else {
						logger.debug("=== Charging Party Address is Off-Net, PhoneNumber:[{}]", info.getPhoneNumber());
					}
				}
//				if (info.getSs7Rn().equals(properties.getApi().getFet().getRn4g())
//				        || info.getSs7Rn().equals(properties.getApi().getFet().getRnr4g())
//				        || info.getSs7Rn().equals(properties.getApi().getFet().getRn2g())
//				        || info.getSs7Rn().equals(properties.getApi().getFet().getRn2g2())) {
//
//					sw = true;
//				} else {
//					logger.debug("=== Charging Party Address is Off-Net, PhoneNumber:[{}]", info.getPhoneNumber());
//				}
			}
			else if(fetPrefixManager.checkFetPrefixInCache(da)){
				sw = true;
			}
			else{
				logger.debug("=== is not MNP and not in Prefix list ,PhoneNumber:[{}]", da);
			}
		} catch (Exception e) {
			logger.error("[RUNTIME] checkMnp error:[{}]",e.getMessage());
		}
		return sw;
	}

	/**
	 * if smsRecordSub is delivered then return SmsRecord else return null
	 * 
	 * @param wsMsgId
	 * @return
	 */
	public SmsRecord checkSmsIsDelivered(String wsMsgId) {
		SmsRecord sms = null;
		DetachedCriteria dcSMS = DetachedCriteria.forClass(SmsRecord.class);
		dcSMS.add(Restrictions.eq("wsMsgId", wsMsgId));
		List<SmsRecord> smsList = smsRecordManager.findByCriteria(SmsRecord.class, dcSMS);
		if (smsList != null && smsList.size() > 0) {
			sms = smsList.get(0);
			for (SmsRecordSub sub : sms.getSubs()) {
				// check smsrecordsub is delivered
				if (sub.getDeliverDate() != null && sub.getDeliverStatus() != null
				        && !sub.getDeliverStatus().equals("")) {
					continue;
				} else {
					return null;
				}
			}
		}

		return sms;
	}

	private boolean regexMatches(String oa, String regex) {
		boolean isTrue = true;
		String regex_change = "[0-9]{7}\\?{13}";
		if (regex.matches(regex_change)) {
			regex = regex.substring(0, 7) + "[0-9]{13}";
		}
		if (!oa.matches(regex)) {
			isTrue = false;
		}
		return isTrue;
	}

	/*
	 * public static void main(String a[]){ String regex =
	 * "0912345?????????????"; String regex2 = "0912345[0-9]{13}"; String regex3
	 * = "09123451234567890[0-7]{1}12"; String regex4 = "55111"; String regex5 =
	 * "09123451234567890123"; String regex6 = "886912345123"; String regex7 =
	 * "0912345123456789012(0|2)"; String regex8 =
	 * "0912\\d{1}09361234567\\d{2}(00|02|03|09)"; String oa
	 * ="09123451234567890123"; String oa2="0912345123456789012a"; String
	 * oa3="09123451234567890912"; String oa4="55112"; String
	 * oa5="09123451234567890124"; String oa6="886912345124"; String
	 * oa7="09123451234567890123"; String oa8="09129093612345679908";
	 * String regex_change = "[0-9]{7}\\?{13}";
	 * System.out.println("regex_change:"+regex.matches(regex_change));
	 * if(regex.matches(regex_change)){ regex = regex.substring(0,
	 * 7)+"[0-9]{13}"; System.out.println("regex:"+regex); }
	 * System.out.println("matche 1:"+oa.matches(regex));
	 * System.out.println("matche 2:"+oa2.matches(regex2));
	 * System.out.println("matche 3:"+oa3.matches(regex3));
	 * System.out.println("matche 4:"+oa4.matches(regex4));
	 * System.out.println("matche 5:"+oa5.matches(regex5));
	 * System.out.println("matche 6:"+oa6.matches(regex6));
	 * System.out.println("matche 7:"+oa7.matches(regex7));
	 * System.out.println("matche 8:"+oa8.matches(regex8));
	 * }
	 */
	
//	public static void main(String a[]){
//		CryptUtil u = new CryptUtil();
//		System.out.println("pawd:"+u.encrypt("msp123"));
//		String s ="12345678900123456789";
//		if(s.length() == 20){
//			System.out.println(s.substring(18, 20));
//		}
//		
//	}
}
