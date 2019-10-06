package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
	private DateUtils() { /* cannot be instantiated */ }
	
	private final static SimpleDateFormat 
		ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ"),
		RFC822_DATE_FORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

	public static String toRFC822String(Date date) {
		return RFC822_DATE_FORMAT.format(date);
	}

	public static String toISO8601String(Date date) {
		String result = ISO8601_DATE_FORMAT.format(date);
		int l = result.length();
		return new StringBuilder(l+1)
			.append(result.substring(0,l-2))
			.append(':') 
			.append(result.substring(l-2))
			.toString();
	}
}