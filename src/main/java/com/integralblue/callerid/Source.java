package com.integralblue.callerid;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.integralblue.callerid.CallerIDResult.AddressResolution;
import com.integralblue.callerid.CallerIDResult.ListingType;
import com.integralblue.callerid.CallerIDResult.PhoneNumberType;

public abstract class Source {
	final private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil
			.getInstance();
	private final static Logger LOGGER = Logger.getLogger(Source.class.getName());
	
	/** Can this source perform lookups on this phone number?
	 * This is the place to make sure this source can handle the country that a given phone number is in, for example.
	 * @param inputPhoneNumber
	 * @return
	 */
	public abstract boolean canLookup(PhoneNumber inputPhoneNumber);
	
	/** Get the name of this source.
	 * This is used for logging and informational purposes.
	 * @return
	 */
	public String getName(){
		return getClass().getName();
	}
	
	/**
	 * Performs a lookup on the given phone number.
	 * Can throw exceptions if there's a problem (ex the source uses a web service, and that service isn't available).
	 * Should not return null.
	 * @param inputPhoneNumber number that we are attempting to lookup.
     * @param outputLocale
     *                          the language code for which the result should be written
	 * @return                 a CallerIDResult with as much information populated as possible
	 * @throws Exception
	 */
	public abstract CallerIDResult lookup(PhoneNumber inputPhoneNumber, Locale outputLocale) throws Exception;
	
	protected CallerIDResult resultBuilder(PhoneNumber phoneNumber, String name, String address, ListingType listingType, AddressResolution addressResolution){
		return new CallerIDResult(name,address,true,getName(),listingType,libphoneNumberPhoneNumberTypeToPhoneNumberType(phoneNumberUtil.getNumberType(phoneNumber)),addressResolution);
	}
	
	private PhoneNumberType libphoneNumberPhoneNumberTypeToPhoneNumberType(com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType phoneNumberType){
		switch(phoneNumberType){
		case FIXED_LINE:
			return PhoneNumberType.FIXED_LINE;
		case MOBILE:
			return PhoneNumberType.MOBILE;
		case FIXED_LINE_OR_MOBILE:
			return PhoneNumberType.FIXED_LINE_OR_MOBILE;
		case PAGER:
			return PhoneNumberType.PAGER;
		case PERSONAL_NUMBER:
			return PhoneNumberType.PERSONAL_NUMBER;
		case PREMIUM_RATE:
			return PhoneNumberType.PREMIUM_RATE;
		case SHARED_COST:
			return PhoneNumberType.SHARED_COST;
		case TOLL_FREE:
			return PhoneNumberType.TOLL_FREE;
		case UAN:
			return PhoneNumberType.UAN;
		case UNKNOWN:
			return PhoneNumberType.UNKNOWN;
		case VOICEMAIL:
			return PhoneNumberType.VOICEMAIL;
		case VOIP:
			return PhoneNumberType.VOICEMAIL;
		default:
			LOGGER.log(Level.WARNING, "Got an unexpected enum value from com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType: {0}", phoneNumberType);
			return PhoneNumberType.UNKNOWN;
		}
	}
}
