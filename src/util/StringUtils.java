package util;

import static java.lang.Integer.toHexString;

public class StringUtils {
	private StringUtils() {/* non-instantiatable utility class */}
	
	public static String getLineSeparator() {
		return System.getProperty("line.separator", "\n");
	}
	
	public static String capFirst(String s) {
		return (s == null || s.length() == 0)? s : s.substring(0,1).toUpperCase().concat(s.substring(1));
	}
	
	public static String toString(int i) {
		switch (i) {
			case 0:	return "zero";
			case 1: return "one";
			case 2: return "two";
			case 3: return "three";
			case 4: return "four";
			case 5: return "five";
			case 6: return "six";
			case 7: return "seven";
			case 8: return "eight";
			case 9: return "nine";
			case 10: return "ten";
			default: return Integer.toString(i);
		}
	}
	
	public static String javaEscape(String s) {
        if (s == null) return null;
        final int length = s.length();
        
        StringBuilder out = new StringBuilder(length * 2);
        
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);

            if (ch > 0xfff)
                out.append("\\u" + toHexString(ch));
            else if (ch > 0xff)
	        	out.append("\\u0" + toHexString(ch));
	        else if (ch > 0x7f)
	        	out.append("\\u0" + toHexString(ch));
	        else if (ch < 32)
                switch (ch) {
                    case '\b':
                        out.append('\\');
                        out.append('b');
                    break;
                    case '\f':
                    	out.append('\\');
                        out.append('f');
                    break;
                    case '\n':
                    	out.append('\\');
                    	out.append('n');
                    break;
                    case '\r':
                    	out.append('\\');
                        out.append('r');
                    break;
                    case '\t':
                    	out.append('\\');
                    	out.append('t');
                    break;
                    default:
                        if (ch > 0xf)
                            out.append("\\u00" + toHexString(ch));
                        else
                            out.append("\\u000" + toHexString(ch));
                    break;
                }
	        else switch (ch) {
                case '\'':
                    out.append('\\');
                    out.append('\'');
                break;
                case '"':
                    out.append('\\');
                    out.append('"');
                break;
                case '\\':
                    out.append('\\');
                    out.append('\\');
                break;
                default :
                    out.append(ch);
                break;
	        }
	    }
        return out.toString();
	}
	
	
	/**
     * Returns the final substring of a {@link String} with as many of the final
     * characters as possible, up to a given length. For instance,
     * <code>lastSubstring ("hello", 4)</code> will return 
     * <code>"ello"</code>, but lastSubstring ("hello", 8) returns
     * <code>"hello"</code>. If the whole string is to be returned,
     * the given parameter is returned rather than a copy.
     * 
     * @param s String to take the last part of. Must not be null.
     * @param x maximum size of string to return
     * 
     * @throws IllegalArgumentException if x is negative
     */
    public static String lastSubstring (String s, int x) {
        if (x < 0) throw new IllegalArgumentException(x + " < 0");
        int l = s.length();
        return (l <= x)? s : s.substring (l-x, l);
    }
}