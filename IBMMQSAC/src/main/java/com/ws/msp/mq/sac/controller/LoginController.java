package com.ws.msp.mq.sac.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ws.msp.mq.sac.dao.RestDataManager;
import com.ws.msp.mq.sac.pojo.AuthenticateException;
import com.ws.msp.mq.sac.pojo.ErrorCode;
import com.ws.msp.mq.sac.pojo.LoginCommand;
import com.ws.msp.mq.sac.pojo.SubUserSession;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.web.ServletUtil;

@Controller
public class LoginController  {
	
	@Autowired
	protected SubUserSession userSession = null;
	
	protected String successView = null;
	
	public LoginController() {
		this.successView = "redirect:/main.htm";
	}

	private static Logger logger = LogManager.getLogger(LoginController.class);

	@Value(value = "${mqsac.session.timeout}")
	private int sessionTimeout = 600;
	
	@Autowired
	private RestDataManager restDataManager = null;

	@RequestMapping(value = { "/login", "/auth/login.htm" }, method = RequestMethod.GET)
	public @ModelAttribute("loginCommand") LoginCommand showForm(HttpServletRequest request) {
		logger.debug("Create Backing Object, session timeout set to " + this.sessionTimeout);
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(this.sessionTimeout);
		String sessionId = session.getId();
		userSession.setSessionId(sessionId);
		logger.debug("UserSession = " + userSession);
		return new LoginCommand();
	}


	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody SubUserSession processLogin(HttpServletRequest request, @RequestBody LoginCommand credentials)
	        throws AuthenticateException, Exception {
		logger.info("Validating user credentials for: " + credentials.getUsername());
		if (!restDataManager.loginSubConsoleUser(credentials.getUsername(), credentials.getPassword())) {
			logger.debug("Incorrect password"); 
			throw new AuthenticateException(ErrorCode.PWD_INCORRECT, "User Password incorrect");
		} else {
			HttpSession session = request.getSession(true);
			session.setMaxInactiveInterval(this.sessionTimeout);
			logger.debug("HTTP Session = [{}]",session);
			userSession.setSessionId(session.getId());
			logger.debug("UserSession = " + userSession);
			
			logger.debug(credentials.getUsername() + " Login success");
			SubConsoleUser user = restDataManager.getSubConsoleUser(credentials.getUsername());
			userSession.setUser(user);
			logger.debug("Client IP from Util:" + ServletUtil.getClientIp(request) + " from request:"
			        + request.getRemoteAddr());
			userSession.setLoggedOn(true);
			userSession.setLogonDate(LocalDateTime.now());
			userSession.setIpAddress(ServletUtil.getClientIp(request));
			userSession.setUserAgent(ServletUtil.getUserAgent(request).getBrowser().getName());
			userSession.setUserId(credentials.getUsername());
			logger.debug("UserSession = " + userSession);

			// Jackson Mapping fail for session scope
			SubUserSession clone = new SubUserSession();
			BeanUtils.copyProperties(userSession, clone);
			return clone;
		}

	}

	@ExceptionHandler(AuthenticateException.class)
	public void errorHandling(AuthenticateException e, HttpServletResponse response) throws IOException {
		if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
			response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
		} else if (e.getErrorCode() == ErrorCode.PWD_INCORRECT) {
			response.sendError(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
		} else if (e.getErrorCode() == ErrorCode.USER_SUSPENDED) {
			response.sendError(HttpStatus.LOCKED.value(), e.getMessage());
		} else if (e.getErrorCode() == ErrorCode.USER_EXPIRED) {
			response.sendError(HttpStatus.REQUEST_TIMEOUT.value(), e.getMessage());
		} else {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}
	
}
