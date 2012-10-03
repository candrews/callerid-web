package com.integralblue.callerid.sources;

import java.util.Locale;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.integralblue.callerid.CallerIDResult;
import com.integralblue.callerid.CallerIDResult.AddressResolution;
import com.integralblue.callerid.CallerIDResult.ListingType;
import com.integralblue.callerid.Source;

public class LibphoneNumberOfflineGeocoderSource extends Source {
	final private static PhoneNumberOfflineGeocoder phoneNumberOfflineGeocoder = PhoneNumberOfflineGeocoder
			.getInstance();
	final private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil
			.getInstance();

	@Override
	public boolean canLookup(PhoneNumber inputPhoneNumber) {
		return true;
	}

	@Override
	public CallerIDResult lookup(PhoneNumber inputPhoneNumber,
			Locale outputLocale) {
		
		final String address = phoneNumberOfflineGeocoder.getDescriptionForNumber(inputPhoneNumber,
				outputLocale);
		
		// There is currently no way to tell what resolution the geocoder provided, see https://code.google.com/p/libphonenumber/issues/detail?id=192
		// As a hack, we figure out what the country is, and see if the geocoder simply returned the country name. If it didn't, we assume it returned at least a region level description. 
		final AddressResolution addressResolution = address.equals(getCountryNameForNumber(inputPhoneNumber, outputLocale))? AddressResolution.COUNTRY : AddressResolution.REGION;
		return resultBuilder(inputPhoneNumber, null, address, ListingType.UNKNOWN, addressResolution);
	}

	  /**
	   * Returns the customary display name in the given language for the given territory the phone
	   * number is from.
	   */
	  private String getCountryNameForNumber(PhoneNumber number, Locale language) {
	    String regionCode = phoneNumberUtil.getRegionCodeForNumber(number);
	    return getRegionDisplayName(regionCode, language);
	  }
	  
	  /**
	   * Returns the customary display name in the given language for the given region.
	   */
	  private String getRegionDisplayName(String regionCode, Locale language) {
	    return (regionCode == null || regionCode.equals("ZZ") ||
	            regionCode.equals(PhoneNumberUtil.REGION_CODE_FOR_NON_GEO_ENTITY))
	        ? "" : new Locale("", regionCode).getDisplayCountry(language);
	  }

}
