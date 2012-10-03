package com.integralblue.callerid.sources;

import java.util.Locale;

import org.junit.Test;

import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.CallerIDResult.AddressResolution;
import com.integralblue.callerid.CallerIDResult.ListingType;
import com.integralblue.callerid.CallerIDResult.PhoneNumberType;

public class WhitePagesSourceTest extends SourceTest {
	
	public WhitePagesSourceTest(){
		super(new WhitePagesSource());
	}
	
	@Test
	public void testFound() throws Exception{
		testLookup(
				"+17817498755",
				Locale.US,
				new CallerIDResult(
						"Lynn D Donovan",
						"Myers Farm Rd, Hingham, MA",
						true,
						source.getName(),
						ListingType.PERSONAL,
						PhoneNumberType.FIXED_LINE_OR_MOBILE,
						AddressResolution.STREET));
	}
	
	@Test
	public void testNotFound() throws Exception{
		testLookup("+18002927508", Locale.US, null);
	}
}