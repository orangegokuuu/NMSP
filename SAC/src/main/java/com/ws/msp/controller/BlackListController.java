package com.ws.msp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ws.mc.controller.AbstractRestController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.interceptor.annotation.PrivilegeLevel;
import com.ws.mc.pojo.RestResult;
import com.ws.msp.pojo.BlackList;
import com.ws.msp.service.BlackListManager;
import com.ws.pojo.PaginationResult;
import com.ws.pojo.SearchablePaging;

@RestController
@RequestMapping("/sms/blacklist")
public class BlackListController extends AbstractRestController<BlackList, String> {
	private static final Logger logger = LogManager.getLogger(BlackListController.class);

	@Autowired
	private BlackListManager blackListManager = null;

	@Override
	public void requiredPrivilege() {
		super.setPrivilege("SMS_SERVICE_03");
	}
	
	@Autowired
	public BlackListController(BlackListManager blackListManager) {
		super(blackListManager);
		// this.setSuccessView("/success");
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST)
//	@LogAction(type = SacConstant.LIS_BKL, message = "List Black List Record")
//	@LogEvent(type = SacConstant.LIS_BKL, message = "List Black List Record")
	@Permission(id = "SMS_SERVICE_03", level = PrivilegeLevel.READ)
	public @ResponseBody PaginationResult<BlackList> page(@RequestBody SearchablePaging page) {
		return super.page(page);
	}
	
	@RequestMapping(value = "/checkBlackList/{da}")
	public @ResponseBody RestResult<Boolean> checkBlackList(@PathVariable(value="da") String da) {
		Boolean blacklistExist = blackListManager.checkBlackListInCache(da);
		logger.trace("check blacklist in cache = [{}]", blacklistExist);
		RestResult<Boolean> result = new RestResult<Boolean>();
		result.setData(blacklistExist);
		return result ;
	}
	
}
