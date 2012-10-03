package com.integralblue.callerid;

import java.util.Locale;
import java.util.concurrent.Callable;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public abstract class Plugin {
	/** Called when a lookup is being performed.
	 * @param inputPhoneNumber number that we are attempting to lookup.
     * @param outputLocale
     *                          the language code for which the result should be written
	 * @param next next.call() will run further plugins and sources
	 * @return a CallerIDResult with as much information populated as possible. Should not return null.
	 * @throws Exception
	 */
	public CallerIDResult onLookup(PhoneNumber phoneNumber, Locale outputLocale, Callable<CallerIDResult> next) throws Exception {
		return next.call();
	}
}
