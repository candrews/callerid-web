package com.integralblue.callerid;

public final class CallerIDResult {
	private final String name;
	private final String address;
	private final boolean cacheable;
	private final String source;
	private final ListingType listingType;
	private final PhoneNumberType phoneNumberType;
	private final AddressResolution addressResolution;

	public static enum ListingType {
		PERSONAL, BUSINESS, OTHER, UNKNOWN
	}
	
	public static enum AddressResolution {
		// partial address: country only
		COUNTRY,
		// partial address: region and country
		REGION,
		// partial address: city, region, and country
		CITY,
		// full address: street, city, region, and country
		STREET
	}

	public static enum PhoneNumberType {
		FIXED_LINE, MOBILE,
		// In some regions (e.g. the USA), it is impossible to distinguish
		// between fixed-line and
		// mobile numbers by looking at the phone number itself.
		FIXED_LINE_OR_MOBILE,
		// Freephone lines
		TOLL_FREE, PREMIUM_RATE,
		// The cost of this call is shared between the caller and the recipient,
		// and is hence typically
		// less than PREMIUM_RATE calls. See //
		// http://en.wikipedia.org/wiki/Shared_Cost_Service for
		// more information.
		SHARED_COST,
		// Voice over IP numbers. This includes TSoIP (Telephony Service over
		// IP).
		VOIP,
		// A personal number is associated with a particular person, and may be
		// routed to either a
		// MOBILE or FIXED_LINE number. Some more information can be found here:
		// http://en.wikipedia.org/wiki/Personal_Numbers
		PERSONAL_NUMBER, PAGER,
		// Used for "Universal Access Numbers" or "Company Numbers". They may be
		// further routed to
		// specific offices, but allow one number to be used for a company.
		UAN,
		// Used for "Voice Mail Access Numbers".
		VOICEMAIL,
		// A phone number is of type UNKNOWN when it does not fit any of the
		// known patterns for a
		// specific region.
		UNKNOWN
	}

	/**
	 * @param name May be null if the name is not known.
	 * @param address May be null if the address is not known.
	 * @param cacheable
	 * @param source Cannot be null.
	 * @param listingType Cannot be null.
	 * @param phoneNumberType Cannot be null.
	 * @param addressResolution If address is not null, this cannot be null. If address is null, this must be null.
	 */
	public CallerIDResult(String name, String address, boolean cacheable,
			String source, ListingType listingType,
			PhoneNumberType phoneNumberType, AddressResolution addressResolution) {
		super();
		this.name = name;
		this.address = address;
		this.cacheable = cacheable;
		if(source == null) throw new IllegalArgumentException("source cannot be null");
		this.source = source;
		if(listingType == null) throw new IllegalArgumentException("listingType cannot be null");
		this.listingType = listingType;
		if(phoneNumberType == null) throw new IllegalArgumentException("phoneNumberType cannot be null");
		this.phoneNumberType = phoneNumberType;
		if(address!=null && addressResolution == null) throw new IllegalArgumentException("If address is not null, addressResolution cannot be null");
		if(address==null && addressResolution != null) throw new IllegalArgumentException("If address is null, addressResolution must be null.");
		this.addressResolution = addressResolution;
	}
	
	/** Copy constructor
	 * @param copyFrom
	 */
	public CallerIDResult(final CallerIDResult copyFrom){
		this(copyFrom.name, copyFrom.address, copyFrom.cacheable, copyFrom.source, copyFrom.listingType, copyFrom.phoneNumberType, copyFrom.addressResolution);
	}

	/** Get the name. May be null if the name is not known.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/** Get the address. May be null if the address is not known.
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	public boolean isCacheable() {
		return cacheable;
	}

	/** Get the name of the source that got this result. Cannot be null.
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/** Get the listing type. Cannot be null.
	 * @return
	 */
	public ListingType getListingType() {
		return listingType;
	}

	/** Get the phone number type. Cannot be null.
	 * @return
	 */
	public PhoneNumberType getPhoneNumberType() {
		return phoneNumberType;
	}

	/** Get the resolution of the address contained in the address field. If address is not null, this cannot be null. If address is null, this must be null.
	 * @return
	 */
	public AddressResolution getAddressResolution() {
		return addressResolution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime
				* result
				+ ((addressResolution == null) ? 0 : addressResolution
						.hashCode());
		result = prime * result + (cacheable ? 1231 : 1237);
		result = prime * result
				+ ((listingType == null) ? 0 : listingType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((phoneNumberType == null) ? 0 : phoneNumberType.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CallerIDResult other = (CallerIDResult) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (addressResolution != other.addressResolution)
			return false;
		if (cacheable != other.cacheable)
			return false;
		if (listingType != other.listingType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNumberType != other.phoneNumberType)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	
	
}
