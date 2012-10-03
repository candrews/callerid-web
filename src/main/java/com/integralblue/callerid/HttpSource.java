package com.integralblue.callerid;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;

public abstract class HttpSource extends Source {
	
	public abstract Request getRequest(PhoneNumber inputPhoneNumber,
			Locale outputLocale);

	public abstract CallerIDResult getResult(Response response, PhoneNumber inputPhoneNumber, Locale outputLocale) throws Exception;
	
	private static final Pattern unicodeWhitespacePattern = Pattern.compile("[\\p{Z}\\s]+",Pattern.MULTILINE);
	
	private static final Pattern stripTagsPattern = Pattern.compile("<.+?>");

	@Override
	public final CallerIDResult lookup(final PhoneNumber inputPhoneNumber,
			final Locale outputLocale) throws Exception {
		final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		return asyncHttpClient.prepareRequest(getRequest(inputPhoneNumber, outputLocale)).execute(new AsyncCompletionHandler<CallerIDResult>() {

			@Override
			public CallerIDResult onCompleted(final Response response) throws Exception {
				return getResult(response, inputPhoneNumber, outputLocale);
			}
		}).get();
	}
	
	/** given a block of messy, scraped html contains tags, escape sequences, etc, return a cleaned up string ready to be displayed to the user
	 * 
	 * if the parameter is empty, return null
     * removes multiple white spaces
     * replaces new line with spaces,
     * convert html entities to unicode characters
     * strips html tags
     * trim leading/trailing white space
     * 
	 * @param html
	 * @return
	 */
	protected String cleanScrapedHtml(String html){
		if(html == null){
			return null;
		}else{
			return stripTagsPattern.matcher(unicodeWhitespacePattern.matcher(HtmlUtils.htmlUnescape(html)).replaceAll(" ")).replaceAll("").trim();
		}
	}
}
