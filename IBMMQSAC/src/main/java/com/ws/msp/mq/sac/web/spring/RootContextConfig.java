/**
 *
 */
package com.ws.msp.mq.sac.web.spring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ws.msp.bootstrap.LoggingInitializer;
import com.ws.msp.config.MspProperties;
import com.ws.msp.mq.sac.web.config.WebProperties;

@Configuration
@EnableConfigurationProperties({ WebProperties.class, MspProperties.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = { "com.ws.msp.mq.sac" })
@Import(value = { RootServletConfig.class })
public class RootContextConfig {

    @Autowired
    WebProperties properties = null;

    @Bean("mqSacLog")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public LoggingInitializer loggingInit() {
        LoggingInitializer logging = new LoggingInitializer();
        logging.setLoggerConfig(properties.getLogger());
        return logging;
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }

    @Bean
    public RestTemplate createRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        converters.add(jsonConverter);
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
