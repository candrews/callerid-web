package com.integralblue.callerid.sources;

import java.util.Locale;

import org.junit.experimental.categories.Category;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.IntegrationTest;
import com.integralblue.callerid.Source;
import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public abstract class SourceTest {
	protected final Source source;
	
	public SourceTest(Source source){
		this.source = source;
	}
	
	/**
	 * @param source
	 * @param phoneNumber The phone number must be in international format and start with a +
	 * @param outputLocale
	 * @param expectedResult
	 * @throws Exception
	 */
	protected void testLookup(String phoneNumber, Locale outputLocale, CallerIDResult expectedResult) throws Exception{
		PhoneNumber inputPhoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumber, "ZZ");
		CallerIDResult actualResult = source.lookup(inputPhoneNumber, outputLocale);
		assertEquals("The expected and actual results were not equal", expectedResult,actualResult);
	}
}