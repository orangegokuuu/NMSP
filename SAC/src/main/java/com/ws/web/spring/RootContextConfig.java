/**
 *
 */
package com.ws.web.spring;

import java.io.IOException;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

import com.ws.mc.bootstrap.ConsoleUserInitializer;
import com.ws.mc.bootstrap.LoggingInitializer;
import com.ws.mc.bootstrap.MessageTemplateInitializer;
import com.ws.mc.bootstrap.SystemParamInitializer;
import com.ws.mc.config.MCProperties;
import com.ws.mc.spring.MCContextConfig;
import com.ws.mc.util.SerializableMessageSource;
import com.ws.msp.config.MspProperties;
import com.ws.web.config.WebProperties;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableConfigurationProperties({ MCProperties.class, WebProperties.class, MspProperties.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(value = { MCContextConfig.class, RootServletConfig.class })
@ComponentScan({ "com.ws.msp", "com.ws.mc", "com.ws.hibernate" })
@Log4j2
public class RootContextConfig {

	@Bean(name = "sacLog")
	public LoggingInitializer loggingInitializer(@Value("${sac.logger}") String logConfig) {
		LoggingInitializer logInit = new LoggingInitializer();
		logInit.setLoggerConfig(logConfig);

		return logInit;
	}

	@Bean
	WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
		return (factory) -> factory.setRegisterDefaultServlet(true);
	}

	@Bean
	public ConsoleUserInitializer consoleUserInitializer(@Value("${sac.privilege.type.conf}") Resource privTypeCfg,
			@Value("${sac.privilege.conf}") Resource privCfg, @Value("${sac.group.conf}") Resource groupCfg,
			@Value("${sac.role.conf}") Resource roleCfg, @Value("${sac.user.conf}") Resource userCfg)
			throws IOException {
		log.debug("PrivType cfg = {}", privTypeCfg.getFilename());
		log.debug("Priv     cfg = {}", privCfg.getFilename());
		log.debug("Group    cfg = {}", groupCfg.getFilename());
		log.debug("Role     cfg = {}", roleCfg.getFilename());
		log.debug("User     cfg = {}", userCfg.getFilename());

		PropertiesFactoryBean privTypeBean = new PropertiesFactoryBean();
		privTypeBean.setLocation(privTypeCfg);
		privTypeBean.afterPropertiesSet();

		PropertiesFactoryBean privBean = new PropertiesFactoryBean();
		privBean.setLocation(privCfg);
		privBean.afterPropertiesSet();

		PropertiesFactoryBean groupBean = new PropertiesFactoryBean();
		groupBean.setLocation(groupCfg);
		groupBean.afterPropertiesSet();

		PropertiesFactoryBean roleBean = new PropertiesFactoryBean();
		roleBean.setLocation(roleCfg);
		roleBean.afterPropertiesSet();

		PropertiesFactoryBean userBean = new PropertiesFactoryBean();
		userBean.setLocation(userCfg);
		userBean.afterPropertiesSet();

		ConsoleUserInitializer userInit = new ConsoleUserInitializer();
		userInit.setPrivilegeTypeConfig(privTypeBean.getObject());
		userInit.setPrivilegeConfig(privBean.getObject());
		userInit.setGroupConfig(groupBean.getObject());
		userInit.setRoleConfig(roleBean.getObject());
		userInit.setUserConfig(userBean.getObject());

		return userInit;
	}

	@Bean
	public SystemParamInitializer systemParamInitializer(@Value("${sac.sys.param.conf}") Resource sysParamCfg)
			throws IOException {
		PropertiesFactoryBean paramBean = new PropertiesFactoryBean();
		paramBean.setLocation(sysParamCfg);
		paramBean.afterPropertiesSet();

		SystemParamInitializer sysParamInit = new SystemParamInitializer();
		sysParamInit.setParamConfig(paramBean.getObject());

		return sysParamInit;
	}

	@Bean
	public MessageTemplateInitializer messageTemplateInitializer(@Value("${sac.msg.template.conf}") Resource msgCfg)
			throws IOException {
		PropertiesFactoryBean msgBean = new PropertiesFactoryBean();
		msgBean.setLocation(msgCfg);
		msgBean.afterPropertiesSet();

		MessageTemplateInitializer msgInit = new MessageTemplateInitializer();
		msgInit.setTemplateConfig(msgBean.getObject());

		return msgInit;
	}

	@Bean
	public SystemParamInitializer jasperReportInitializer(@Value("${sac.sys.param.conf}") Resource jrptCfg)
			throws IOException {
		PropertiesFactoryBean paramBean = new PropertiesFactoryBean();
		paramBean.setLocation(jrptCfg);
		paramBean.afterPropertiesSet();

		SystemParamInitializer sysParamInit = new SystemParamInitializer();
		sysParamInit.setParamConfig(paramBean.getObject());

		return sysParamInit;
	}

	// @Bean("sacMessageSource")
	// @Primary
	// public SerializableMessageSource messageSource() {
	// 	SerializableMessageSource ms = new SerializableMessageSource();
	// 	ms.setBasenames("classpath:i18n/message",
	// 			"classpath:com/ws/mc/i18n/message");
	// 	return ms;
	// }

	@ConditionalOnProperty(havingValue = "mc.date.timezone")
	@Autowired
	public void setTimeZone(@Value("${mc.date.timezone}") String tz) {
		TimeZone.setDefault(TimeZone.getTimeZone(tz));
	}

}
