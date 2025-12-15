package com.ws.msp.mq.sac.interceptor;

import java.time.LocalDateTime;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.ws.msp.mq.sac.pojo.AuthenticateException;
import com.ws.msp.mq.sac.pojo.ErrorCode;
import com.ws.msp.mq.sac.pojo.SubUserSession;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.msp.service.SubConsoleUserManager;
import com.ws.util.CryptUtil;
import com.ws.util.StringUtil;
import com.ws.web.ServletUtil;
import lombok.Setter;


@Setter
public class HeaderAuthenticator extends HandlerInterceptorAdapter {
	public static final String AUTH_KEY = "AgentSignature";

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(HeaderAuthenticator.class);

	@Autowired
	private SubUserSession userSession = null;

	@Autowired
	private SubConsoleUserManager userManager = null;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	        throws Exception {
		logger.debug("Check for URI[{}] ", request.getRequestURI());
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			logger.trace("Header name[{}]", headers.nextElement());
		}
		// If Signature found, override user session
		if (request.getHeader(AUTH_KEY) != null) {
			logger.debug("Signature Key[{} = {}] found in HTTP header, try to override session", AUTH_KEY,
			        request.getHeader(AUTH_KEY));
			String userId = CryptUtil.decrypt(request.getHeader(AUTH_KEY));
			if (StringUtil.isEmpty(userId)) {
				throw new AuthenticateException(ErrorCode.RUNTIME_ERROR,
				        String.format("Invalid %s [%s]", AUTH_KEY, userId));
			} else {
				SubConsoleUser user = userManager.getUser(userId);
				if (user != null) {
					logger.debug("Signature Key[{}] Approved, generate UserSession", request.getHeader(AUTH_KEY));
					userSession.setSessionId(request.getSession().getId());
					userSession.setUserId(userId);
					userSession.setUser(user);
					userSession.setLoggedOn(true);
					userSession.setLogonDate(LocalDateTime.now());
					userSession.setIpAddress(ServletUtil.getClientIp(request));
					userSession.setUserAgent(ServletUtil.getUserAgent(request).getBrowser().getName());
					logger.debug("UserSession = " + userSession);
				} else {
					throw new AuthenticateException(ErrorCode.USER_NOT_FOUND,
					        String.format("User[%s] not found", userId));
				}
			}
		}

		return true;
	}

}
