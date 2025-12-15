package com.ws.msp.mq.sac.web.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.ws.msp.mq.sac.pojo.SubUserSession;
import com.ws.msp.mq.sac.web.config.WebProperties;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RootServletConfig implements WebMvcConfigurer {

	@Autowired
	private WebProperties webProp = null;

	@Value("${sac.url.path}")
	private String sacPath = null;

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#
	 * addResourceHandlers(org.springframework
	 * .web.servlet.config.annotation.ResourceHandlerRegistry)
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/favicon.ico").addResourceLocations(webProp.getResource().getPath());

		// Disable Cache for modules
		registry.addResourceHandler("/**").addResourceLocations(webProp.getResource().getPath()).resourceChain(false);

		// Enable Cache for 3rd party lib
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
				.setCachePeriod(webProp.getResource().getCachePeriod())
				.resourceChain(webProp.getResource().isCacheEnabled());

		registry.addResourceHandler("/favicon.ico").addResourceLocations("/");

	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);
		registrationBean.setFilter(encodingFilter);

		// registrationBean.addUrlPatterns("/*");
		registrationBean.addUrlPatterns("*.jsp");
		registrationBean.addUrlPatterns("*.htm");

		return registrationBean;
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/jsp/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		resolver.setOrder(1);
		return resolver;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	// @Bean
	// @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	// public SubUserSession userSession() {
	// 	return new SubUserSession();
	// }

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/sms/**").allowedMethods("*").allowedOrigins("http://10.76.*.*")
				.allowedHeaders("*");
		registry.addMapping("/user/**").allowedMethods("*").allowedOrigins("http://10.76.*.*").allowedHeaders("*");
		registry.addMapping("/system/**").allowedMethods("*").allowedOrigins("http://10.76.*.*").allowedHeaders("*");
	}

}
