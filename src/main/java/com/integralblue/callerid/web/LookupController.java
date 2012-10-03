package com.integralblue.callerid.web;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.CallerIDUtil;

@Controller
public class LookupController {
	@Autowired CallerIDUtil callerIDUtil;

	@RequestMapping(value="/lookup", method=RequestMethod.GET)
	public Callable<CallerIDResult> lookup(@RequestParam(required=false) final String country, @RequestParam(value="num",required=true) final String number, final HttpServletResponse response, final HttpServletRequest request) throws Exception {
		final Locale outputLocale = request.getLocale();
		return new Callable<CallerIDResult>(){

			@Override
			public CallerIDResult call() throws Exception {
				final CallerIDResult result = callerIDUtil.lookup(number, country, outputLocale);
				if(result.isCacheable()){
					response.setHeader("Cache-Control", "public");
					response.setDateHeader("Expires", System.currentTimeMillis() + 24*60*60*1000); //one day
				}else{
					response.setHeader("Cache-Control", "no-cache");
					response.setDateHeader("Expires", 0);
				}
				return result;
			}
			
		};
	}

}
