package com.integralblue.callerid;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.integralblue.callerid.sources.LibphoneNumberOfflineGeocoderSource;
import com.integralblue.callerid.sources.WhitePagesSource;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

public final class CallerIDUtil implements Closeable {

	private final static Logger LOGGER = Logger.getLogger(CallerIDUtil.class.getName());
	
	final private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil
			.getInstance();

	private List<Source> sources = new LinkedList<Source>();
	private List<Plugin> plugins = new LinkedList<Plugin>();
	
	private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public CallerIDUtil() {
		this(true);
	}

	public CallerIDUtil(boolean useDefaultSources) {
		sources.addAll(getDefaultSources());
	}

	protected List<Source> getDefaultSources() {
		return Arrays.asList(new Source[]{
				new WhitePagesSource(),
				new LibphoneNumberOfflineGeocoderSource()
		});
	}

	public void addSource(Source source) {
		sources.add(source);
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
	}

	/**
	 * Performs a lookup on the given phone number
	 * 
	 * @param inputPhoneNumber number that we are attempting to lookup. This can contain formatting
     *                          such as +, ( and -, as well as a phone number extension. It can also
     *                          be provided in RFC3966 format.
     * @param defaultRegion    region that we are expecting the number to be from. This is only used
     *                          if the number being parsed is not written in international format.
     *                          The country_code for the number in this case would be stored as that
     *                          of the default region supplied. If the number is guaranteed to
     *                          start with a '+' followed by the country calling code, then
     *                          null can be supplied. This must be specified in ISO 3166-1 two-letter
     *                          country-code format and must be in upper case. The list of codes
     *                          can be found here:
     *                          http://www.iso.org/iso/country_codes/iso_3166_code_lists/country_names_and_code_elements.htm
     * @param outputLocale
     *                          the language code for which the result should be written
	 * @return                 a CallerIDResult with as much information populated as possible. Will not return null.
     * @throws NumberParseException  if the string is not considered to be a viable phone number or if
     *                               no default region was supplied and the number is not in
     *                               international format (does not start with +)
	 */
	public CallerIDResult lookup(String inputPhoneNumber, String defaultRegion, Locale outputLocale) throws NumberParseException{
		if(inputPhoneNumber==null || inputPhoneNumber.equals("")){
			throw new IllegalArgumentException("inputPhoneNumber cannot be null or empty");
		}
		if(defaultRegion!=null && ! (defaultRegion.equals(defaultRegion.toUpperCase()) && defaultRegion.length()==2 )){
			throw new IllegalArgumentException("defaultRegion must be either null or a 2 letter upper case country code as specified in ISO 3166-1");
		}
		if(outputLocale==null){
			throw new IllegalArgumentException("outputLocale cannot be null");
		}
		try {
			final PhoneNumber phoneNumber = phoneNumberUtil.parse(inputPhoneNumber, defaultRegion);
			final Iterator<Plugin> pluginIterator = plugins.iterator();
			final Callable<CallerIDResult> result = getCallable(pluginIterator, phoneNumber, outputLocale);
			return result.call();
		} catch (com.google.i18n.phonenumbers.NumberParseException e) {
			throw libphonenumberNumberParseExceptionToNumberParseException(e);
		} catch (Exception e) {
			// this really shouldn't happen
			throw new RuntimeException(e);
		}
	}
	
	protected Callable<CallerIDResult> getCallable(final Iterator<Plugin> pluginIterator, final PhoneNumber phoneNumber, final Locale outputLocale){
		if(pluginIterator.hasNext()){
			final Plugin plugin = pluginIterator.next();
			return new Callable<CallerIDResult>(){

				@Override
				public CallerIDResult call() throws Exception {
					final Callable<CallerIDResult> next = getCallable(pluginIterator, phoneNumber, outputLocale);
					try{
						final CallerIDResult ret = plugin.onLookup(phoneNumber, outputLocale, next);
						if(ret == null){
							throw new IllegalStateException("After plugin execution, the result is null. That is not a legal state.");
						}
						return ret;
					}catch(Throwable t){
						LOGGER.log(Level.WARNING,"Plugin threw a throwable. That plugin is being skipped and the next is being executed.",t);
						return next.call();
					}
				}
			};
		}else{
			return new Callable<CallerIDResult>(){

				@Override
				public CallerIDResult call() throws Exception {
					return getResultFromSources(phoneNumber, outputLocale);
				}
				
			};
		}
	}
	
	/** Runs the phone number through all configured sources. Does not run plugins.
	 * @param phoneNumber
	 * @param outputLocale
	 * @return the result. Will not return null.
	 */
	protected CallerIDResult getResultFromSources(final PhoneNumber phoneNumber, final Locale outputLocale) {
		//start with the assumption that the result is cacheable.
		//if any source throws a throwable, the result isn't cacheable.
		//if any individual result isn't cacheable, the final result isn't cacheable.
		boolean cacheable = true;
		final List<CallerIDResult> results = new LinkedList<CallerIDResult>();
		final List<ListenableFuture<CallerIDResult>> httpFutures = new LinkedList<ListenableFuture<CallerIDResult>>();
		for(final Source source : sources){
			try{
				if(source instanceof HttpSource){
					final HttpSource httpSource = (HttpSource) source;
					final ListenableFuture<CallerIDResult> future = asyncHttpClient.prepareRequest(httpSource.getRequest(phoneNumber, outputLocale)).execute(new AsyncCompletionHandler<CallerIDResult>() {
						@Override
						public CallerIDResult onCompleted(final Response response) throws Exception {
							return httpSource.getResult(response, phoneNumber, outputLocale);
						}
					});
					httpFutures.add(future);
				}else{
					final CallerIDResult result = source.lookup(phoneNumber, outputLocale);
					if(result != null){
						results.add(result);
					}
				}
			}catch(Throwable t){
				LOGGER.log(Level.WARNING,"Source threw a throwable. That source is being skipped and the next is being executed. Note that the result is being marked as non-cacheable.",t);
				cacheable = false; //if any source throws a throwable, the result isn't cacheable.
			}
		}
		for(final ListenableFuture<CallerIDResult> future : httpFutures){
			try{
				final CallerIDResult result = future.get();
				if(result!=null) results.add(result);
			}catch(Throwable t){
				LOGGER.log(Level.WARNING,"Source threw a throwable. That source is being skipped and the next is being executed. Note that the result is being marked as non-cacheable.",t);
				cacheable = false; //if any source throws a throwable, the result isn't cacheable.
			}
		}
		CallerIDResult ret = null;
		for(CallerIDResult result : results){
			if(ret == null){
				ret = result;
			}else{
				if(! result.isCacheable()){
					cacheable = false; //if any individual result isn't cacheable, the final result isn't cacheable.
				}
				// if ret's address resolution < result's address resolution
				if(ret.getAddressResolution().compareTo(result.getAddressResolution())<0){
					ret = result;
				}
			}
		}
		if(ret == null){
			throw new IllegalStateException("After running all sources, the result is null.");
		}
		if(!cacheable && ret.isCacheable()){
			// copy ret into a new result that has cacheable set correctly
			ret = new CallerIDResult(ret.getName(),ret.getAddress(),cacheable,ret.getSource(),ret.getListingType(),ret.getPhoneNumberType(),ret.getAddressResolution());
		}
		return ret;
	}
	
	private static NumberParseException libphonenumberNumberParseExceptionToNumberParseException(final com.google.i18n.phonenumbers.NumberParseException e){
		return new NumberParseException(e);
	}

	@Override
	public void close() throws IOException {
		asyncHttpClient.close();
	}
}
