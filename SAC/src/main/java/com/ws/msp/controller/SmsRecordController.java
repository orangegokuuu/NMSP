package com.ws.msp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.pojo.SmsRecord;
import com.ws.msp.service.SmsRecordManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

@RestController
@RequestMapping("/sms/record")
public class SmsRecordController extends AbstractRestController<SmsRecord, String> {
	private static final Logger logger = LogManager.getLogger(SmsRecordController.class);

	@Autowired
	private SmsRecordManager smsRecordManager = null;

	@Autowired
	public SmsRecordController(SmsRecordManager smsRecordManager) {
		super(smsRecordManager);
		// this.setSuccessView("/success");
	}
	
	// @InitBinder
	// protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
	// }
	

	@RequestMapping(value = "/page", method = RequestMethod.POST)
//	@LogAction(type = SacConstant.LIS_SK, message = "List SMS record")
//	@LogEvent(type = SacConstant.LIS_SK, message = "List SMS record")
	@Permission(id = "SMS_SERVICE_02", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<SmsRecord> page(@RequestBody SearchablePaging page) {
		return super.page(page);
	}

//	// for testing only
//	@RequestMapping(value = "/addSampleSmsRecord", method = RequestMethod.GET)
//	public @ResponseBody RestResult<SmsRecord> addSampleSmsRecord() {
//		RestResult<SmsRecord> result = new RestResult<SmsRecord>();
//		 
//		SmsRecord smsRecord = new SmsRecord();
//		SmsRecordSub smsRecordSub = new SmsRecordSub();
//		SmsRecordSub smsRecordSub2  = new SmsRecordSub();
// 
//		int randomNum = ThreadLocalRandom.current().nextInt(0, 500 + 1);
//		String id = "id" + Integer.toString(randomNum);
//		smsRecord.setCreateBy(userSession.getUser().getUserId());
//		smsRecord.setCreateDate(new Date());
//		smsRecord.setUpdateBy(userSession.getUser().getUserId());
//		smsRecord.setUpdateDate(new Date());
//		
//		smsRecord.setSysId("sysId1");
//		smsRecord.setDa("Point A");
//		smsRecord.setOa("Point B");
//		smsRecord.setSmsSourceType("1");
//		smsRecord.setAcceptDate(new Date());
//		smsRecord.setAcceptStatus("SUCCESS");
//		smsRecord.setTotalSeg(10);
//
//		smsRecord.setReqMsgId("ReqId1");
//		smsRecord.setWsMsgId(id); //pk
//		smsRecord.setLanguage("1");
//		smsRecord.setText("Message content shown here. This is a sample SMS record for testing."
//				+ "This is a sample SMS record for testing.This is a sample SMS record for testing.This is a sample SMS record for testing."
//				+ "This is a sample SMS record for testing.This is a sample SMS record for testing.This is a sample SMS record for testing.");
//		smsRecord.setDrFlag(0);
//		smsRecord.setValidType("1");
//		smsRecord.setDrRespDate(new Date());
////		smsRecord.setDeliverSMDate(new Date());
////		smsRecord.setDrState("SUCCESS");
////		smsRecord.setDrErrCode("Err01");
//		smsRecord.setSourceTon(1);
//		smsRecord.setSourceNpi(1);
//		smsRecord.setSmsType("MO");
//
//		smsRecord.setDestTon(1);
//		smsRecord.setDestNpi(1);
//		smsRecord.setEsmClass(1);
////		smsRecord.setRequestDr(1);
////		smsRecord.setServiceType("Service type");
////		smsRecord.setValidity("true");
//		smsRecord.setIsInter("1");
//		smsRecord.setIsBlacklist("1");
//		smsRecord.setIsSpam("1");
////		smsRecord.setPriorityFlag(1);
//		smsRecord.setResultCode("200");
////		smsRecord.setLongMsgCount(12);
////		smsRecord.setRetryCount(12);
//		
//		smsRecordSub.setSegNum("1");
//		smsRecordSub.setWsMsgId(id); 
//		smsRecordSub.setSubmitDate(new Date());;
//		smsRecordSub.setSubmitStatus("SUCCESS");;
//		smsRecordSub.setDeliverDate(new Date());
//		smsRecordSub.setDeliverStatus("DELIVRD");
//
//		smsRecordSub2.setSegNum("2");
//		smsRecordSub2.setWsMsgId(id); 
//		smsRecordSub2.setSubmitDate(new Date());;
//		smsRecordSub2.setSubmitStatus("SUCCESS");;
//		smsRecordSub2.setDeliverDate(new Date());
//		smsRecordSub2.setDeliverStatus("DELIVRD");
//		
//		
//		List<SmsRecordSub> smsRecordSubList = new ArrayList<SmsRecordSub>();
//		smsRecordSubList.add(smsRecordSub);
//		smsRecordSubList.add(smsRecordSub2);
//		smsRecord.setSubs(smsRecordSubList);
//		smsRecordManager.save(SmsRecord.class, smsRecord);
//		result.setData(smsRecord);
//		
//		if (!getSampleData(smsRecord.getWsMsgId()))
//			result.setSuccess(false);
//		return result;
//	}

//	private Boolean getSampleData(String wsMsgId) {
//		try {
//			SmsRecord sr = smsRecordManager.get(SmsRecord.class, wsMsgId);
//			if (sr == null) {
//				throw new DataAccessException("SmsRecord [" + wsMsgId + "] not found");
//			}
//			logger.debug("Sample data = " + sr);
//		} catch (DataAccessException e) {
//			logger.debug("Fail to save Sample data");
//			return false;
//		}
//		return true;
//	}

 

	@Override
	public void requiredPrivilege() {
		// TODO Auto-generated method stub
		super.setPrivilege("SMS_SERVICE_02");
	}

}
