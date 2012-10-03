package com.integralblue.callerid.web;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.integralblue.callerid.CallerIDUtil;

@Configuration
public class CallerIDUtilProvider implements Provider<CallerIDUtil> {

	@Override
	@Singleton
	@Named
	@Bean
	public CallerIDUtil get() {
		final CallerIDUtil callerIDUtil = new CallerIDUtil(true);
		return callerIDUtil;
	}

}
