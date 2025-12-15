package com.ws.msp.mq.sac.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ws.msp.mq.sac.interceptor.annotation.Permission;
import com.ws.msp.mq.sac.pojo.SubUserSession;

@Controller
public class IndexPageController  {
	private static Logger logger = LogManager.getLogger(IndexPageController.class);

	@Value("${mqsac.view.home:home}")
	private String homePage = null;

	@Value("${mqsac.view.login:login}")
	private String loginPage = null;

	@Autowired
	protected SubUserSession userSession = null;
	
	@RequestMapping(value = "/")
	public String indexPage() {
		if (userSession != null && userSession.isLoggedOn()) {
			logger.debug("User[{}] logged on, go to[{}]", userSession.getUserId(), homePage);
			return homePage;
		} else {
			logger.debug("No User session, go to[{}]", userSession.getUserId(), loginPage);
			return loginPage;
		}
	}

	@Permission
	@RequestMapping(value = "/expired", method = RequestMethod.GET)
	public @ResponseBody void expired(HttpServletRequest request) {
		request.getSession().invalidate();
	}
}
