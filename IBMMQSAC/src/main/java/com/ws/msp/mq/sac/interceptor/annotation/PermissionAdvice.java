package com.ws.msp.mq.sac.interceptor.annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ws.msp.mq.sac.pojo.AuthenticateException;
import com.ws.msp.mq.sac.pojo.ErrorCode;
import com.ws.msp.mq.sac.pojo.SubUserSession;
import com.ws.util.StringUtil;

@Aspect
@Component
public class PermissionAdvice {

	private static Logger logger = LogManager.getLogger(PermissionAdvice.class);

	@Autowired(required = false)
	private SubUserSession userSession = null;

	public static void checkPermission(SubUserSession session) {
		if (session == null || StringUtil.isEmpty(session.getUserId())) {
			logger.info("User Session is null, skip privilege checking and return exception");
			throw new AuthenticateException(ErrorCode.USER_EXPIRED, "UserSession Expried");
		}
	}

	@Before(value = "@annotation(permission)")
	public void beforeExecuteMethod(JoinPoint jp, Permission permission) throws AuthenticateException {
		checkPermission(userSession);
	}

	@AfterReturning(pointcut = "@annotation(permission)", returning = "result")
	public void logMethod(JoinPoint jp, Object result, Permission permission) {
		logger.debug("Return Method[{}] execute", jp.getSignature().getName());
	}
}
