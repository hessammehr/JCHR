package be.kuleuven.jchr;

import java.net.MalformedURLException;
import java.net.URL;

public final class About {
    
    private About() { /* non-instantiatable */ }
    
    public final static int MAJOR = 1;
    public final static int MINOR = 6;
    public final static int REVISION = 0;
    
    public final static String getVersionString() {
    	return new StringBuilder(6)
            .append(MAJOR).append('.')
            .append(MINOR).append('.')
            .append(REVISION)
            .toString();
    }
    
    public final static String SYSTEM_NAME = "K.U.Leuven JCHR";
    
    public final static String getSystemNameAndVersion() { 
		return new StringBuilder(25)
			.append(SYSTEM_NAME)
			.append(" v")
			.append(getVersionString())
			.toString();
    }
    public final static String getFullSystemNameAndVersion() {
    	return new StringBuilder(40)
    		.append("The ")
    		.append(SYSTEM_NAME)
    		.append(" System")
    		.append(" v")
    		.append(getVersionString())
    		.toString();
    }
    
    static {
    	try {
			SYSTEM_URL = new URL("http://www.cs.kuleuven.be/~petervw/JCHR/");
		} catch (MalformedURLException e) {
			throw new InternalError();
		}
    }
    public final static URL SYSTEM_URL;
    
    public static void main(String[] args) {
        System.out.println(getFullSystemNameAndVersion());
    }
}