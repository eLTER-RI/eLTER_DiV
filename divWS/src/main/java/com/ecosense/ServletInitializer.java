package com.ecosense;

import javax.servlet.Filter;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EcosenseApplication.class);
	}
	
	@Bean
	public ErrorPageFilter errorPageFilter() {
	    return new ErrorPageFilter();
	}

	@Bean
	public FilterRegistrationBean<Filter> disableSpringBootErrorFilter(ErrorPageFilter filter) {
	    FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
	    filterRegistrationBean.setFilter(filter);
	    filterRegistrationBean.setEnabled(false);

	    return filterRegistrationBean;
	}

}
