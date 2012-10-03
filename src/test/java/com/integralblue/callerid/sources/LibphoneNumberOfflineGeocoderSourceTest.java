package com.integralblue.callerid.sources;

import java.util.Locale;

import org.junit.Test;

import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.CallerIDResult.AddressResolution;
import com.integralblue.callerid.CallerIDResult.ListingType;
import com.integralblue.callerid.CallerIDResult.PhoneNumberType;

public class LibphoneNumberOfflineGeocoderSourceTest extends SourceTest {
	public LibphoneNumberOfflineGeocoderSourceTest(){
		super(new LibphoneNumberOfflineGeocoderSource());
	}
	
	@Test
	public void testFound() throws Exception{
		testLookup(
				"+17817498755",
				Locale.US,
				new CallerIDResult(
						null,
						"Hingham, MA",
						true,
						source.getName(),
						ListingType.UNKNOWN,
						PhoneNumberType.FIXED_LINE_OR_MOBILE,
						AddressResolution.REGION));
	}
}