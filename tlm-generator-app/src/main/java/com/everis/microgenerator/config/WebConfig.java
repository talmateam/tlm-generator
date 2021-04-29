package com.everis.microgenerator.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@SuppressWarnings("deprecation")
@Configuration
//@EnableWebMvc
public class WebConfig /*extends WebMvcConfigurerAdapter*/ {

	@Bean(name = "myProperties")
	public static PropertiesFactoryBean mapper() {
	        PropertiesFactoryBean bean = new PropertiesFactoryBean();
	        bean.setLocation(new ClassPathResource(
	                "application.properties"));
	        return bean;
	}

}
