package com.ws.msp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.pojo.SpamKeyWord;
import com.ws.msp.service.SpamKeyWordManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

@RestController
@RequestMapping("/sms/spamkeyword")
public class SpamKeywordController extends AbstractRestController<SpamKeyWord, String> {
	private static final Logger logger = LogManager.getLogger(SpamKeywordController.class);

	@Autowired
	private SpamKeyWordManager spamKeyWordManager = null;

	@Autowired
	public SpamKeywordController(SpamKeyWordManager spamKeyWordManager) {
		super(spamKeyWordManager);
		// this.setSuccessView("/success");
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
//	@LogAction(type = SacConstant.LIS_SK, message = "List Spam Keyword record")
//	@LogEvent(type = SacConstant.LIS_SK, message = "List Spam Keyword record")
	@Permission(id = "SMS_SERVICE_04", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<SpamKeyWord> page(@RequestBody SearchablePaging page) {
		return super.page(page);
	}

	@Override
	public void requiredPrivilege() {
		// TODO Auto-generated method stub
		super.setPrivilege("SMS_RECORD_04");
	}
}
