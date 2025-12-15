package com.ws.msp.mq.sac.pojo;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ws.hibernate.pojo.BaseBean;
import com.ws.msp.pojo.SubConsoleUser;
import com.ws.pojo.SessionIntf;
import com.ws.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SubUserSession extends BaseBean implements SessionIntf {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4036357950772119243L;

	private String sessionId = null;
	private String ipAddress = null;
	private String userAgent = null;
	private String userId = null;
	private LocalDateTime logonDate = null;
	private LocalDateTime lastAccess = null;

	private boolean loggedOn = false;
	private SubConsoleUser user = null;

}
