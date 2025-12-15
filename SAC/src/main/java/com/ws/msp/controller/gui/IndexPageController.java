package com.ws.msp.controller.gui;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ws.mc.controller.AbstractAnnotateController;
import com.ws.mc.interceptor.annotation.Permission;
import com.ws.mc.pojo.RestResult;
import com.ws.mc.pojo.UserSession;

@Controller("mspIndexController")
public class IndexPageController extends AbstractAnnotateController {
	private static Logger logger = LogManager.getLogger(IndexPageController.class);

	@Value("${mc.view.home:home}")
	private String homePage = null;

	@Value("${mc.view.login:login}")
	private String loginPage = null;

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
	@RequestMapping(value = "/session", method = RequestMethod.GET)
	public @ResponseBody RestResult<UserSession> getSession() {
		RestResult<UserSession> result = new RestResult<UserSession>();

		// Jackson Mapping fail for session scope
		UserSession clone = new UserSession();
		BeanUtils.copyProperties(userSession, clone);

		result.setSuccess(true);
		result.setData(clone);
		return result;
	}

	@Permission
	@RequestMapping(value = "/expired", method = RequestMethod.GET)
	public @ResponseBody void expired(HttpServletRequest request) {
		request.getSession().invalidate();
	}
}
