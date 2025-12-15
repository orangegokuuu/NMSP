/**
 *
 */
package com.ws.web.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import com.ws.mc.spring.MCWebConfig;
import com.ws.web.config.WebProperties;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({ "com.ws.mc.legacy" })
@Import(value = {MCWebConfig.class})
public class RootServletConfig implements WebMvcConfigurer {

	@Autowired
	private WebProperties webProp = null;

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
	 *      #addViewControllers(org.springframework.web.servlet.config.annotation.
	 *      ViewControllerRegistry)
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/logout").setViewName("/logout");
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
	 * #addResourceHandlers(org.springframework
	 * .web.servlet.config.annotation.ResourceHandlerRegistry)
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/favicon.ico").addResourceLocations(webProp.getResource().getPath());

		// Disable Cache for modules
		registry.addResourceHandler("/**").addResourceLocations(webProp.getResource().getPath())
				.resourceChain(false);

		// Enable Cache for 3rd party lib
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/")
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

	/*
	 * @Bean public JasperReportsViewResolver getJasperReportsViewResolver() { // adjust alignment
	 * in html report StringBuffer sb = new StringBuffer(); sb.append("<html>");
	 * sb.append("<head>"); sb.append("  <title></title>");
	 * sb.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
	 * sb.append("  <style type=\"text/css\">"); sb.append("    a {text-decoration: none}");
	 * sb.append("  </style>"); sb.append("</head>");
	 * sb.append("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\" vlink=\"#000000\"");
	 * sb.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
	 * sb.append("<tr><td align=\"left\">");
	 * 
	 * Map<String, Object> exporterParameters = new HashMap<String, Object>();
	 * exporterParameters.put(
	 * "net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN",
	 * Boolean.FALSE); exporterParameters.put(
	 * "net.sf.jasperreports.engine.export.JRHtmlExporterParameter.HTML_HEADER", sb.toString());
	 * 
	 * JasperReportsViewResolver resolver = new JasperReportsViewResolver();
	 * 
	 * resolver.setPrefix("classpath:/jasper/"); resolver.setSuffix(".jasper");
	 * resolver.setReportDataKey("datasource"); resolver.setViewNames("client*");
	 * resolver.setViewClass(JasperReportsMultiFormatView.class);
	 * resolver.setExporterParameters(exporterParameters); resolver.setOrder(0); return resolver; }
	 */
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

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/sms/**").allowedMethods("*").allowedOrigins("http://10.76.*.*").allowedHeaders("*");
		registry.addMapping("/user/**").allowedMethods("*").allowedOrigins("http://10.76.*.*").allowedHeaders("*");
		registry.addMapping("/system/**").allowedMethods("*").allowedOrigins("http://10.76.*.*")
				.allowedHeaders("*");
	}
}
