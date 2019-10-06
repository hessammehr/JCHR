package annotations;

public final class Default {
    private Default() { /* not instantiatable */ }

    /**
     * Because we can't use reference comparison unfortunataly
     * (a clone is implicitly taken), 
     * we are using a string no-one would normally use 
     * (&quot;\u0000&quot; to be precise).
     */
    private final static char DEFAULT_CHAR = '\u0000';
    final static String DEFAULT_STRING = "\u0000";
    
    
    public static boolean isDefault(String value) {
        return value.equals(DEFAULT_STRING);
    }
    
    public static boolean isDefault(String[] value) {
        return (value != null) 
            && (value.length == 1)
            && value[0].charAt(0) == DEFAULT_CHAR;
    }
    
    public static boolean isSet(String value) {
        return !isDefault(value);
    }
    
    public static boolean isSet(String[] value) {
        return !isDefault(value);
    }
}
