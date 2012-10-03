package com.integralblue.callerid.sources;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.CallerIDResult.AddressResolution;
import com.integralblue.callerid.CallerIDResult.ListingType;
import com.integralblue.callerid.HttpSource;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

public class WhitePagesSource extends HttpSource {
	
	private static final Pattern notFoundPattern = Pattern.compile("phone no_results");
	
	private static final Pattern addressPattern = Pattern.compile("<span\\s+class=\"street-address\">(.*?)</span>.*<span class=\"locality\">(.*?)</span>.*<span class=\"region\">(.*?)</span>");
	private static final Pattern companyPattern = Pattern.compile("Company:\\s+</strong><span\\s+class=\"org\">(.*?)</span>");
	private static final Pattern namePattern = Pattern.compile("<span\\s+class=\"name\\s+fn\">(.*?)<\\/span><\\/span>", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean canLookup(PhoneNumber inputPhoneNumber) {
		return inputPhoneNumber.getCountryCode()==1;
	}

	@Override
	public Request getRequest(PhoneNumber inputPhoneNumber, Locale outputLocale) {
		final String nationalNumber = Long.toString(inputPhoneNumber.getNationalNumber());
		return new RequestBuilder()
			.setUrl("http://www.whitepages.com/phone/1-" + nationalNumber.substring(0, 3) + "-" + nationalNumber.substring(3, 6) + "-" + nationalNumber.substring(6, 10))
			.build();
	}
	
	public CallerIDResult getResult(Response response, PhoneNumber inputPhoneNumber, Locale outputLocale) throws Exception {
		if(response.getStatusCode()/100==2){
			final String responseBody = response.getResponseBody();
			if(notFoundPattern.matcher(responseBody).find()){
				return null;
			}else{
				String name;
				String address;
				ListingType listingType;
				
				Matcher matcher = namePattern.matcher(responseBody);
				if(matcher.find()){
					name = matcher.group(1);
					matcher = addressPattern.matcher(responseBody);
					if(matcher.find()){
						address = matcher.group(1) + ", " + matcher.group(2) + ", " + matcher.group(3);
					}else{
						address = null;
					}
					matcher = companyPattern.matcher(responseBody);
					if(matcher.find()){
						listingType = ListingType.BUSINESS;
					}else{
						listingType = ListingType.PERSONAL;
					}
					return resultBuilder(inputPhoneNumber, cleanScrapedHtml(name), cleanScrapedHtml(address), listingType, AddressResolution.STREET);
				}else{
					throw new Exception("Name pattern should have matched, but it didn't.");
				}
			}
		}else{
			if(response.getStatusCode()==404){
				return null;
			}else{
				throw new Exception("Server responsed with unexpected response code " + response.getStatusCode());
			}
		}
	}

}
