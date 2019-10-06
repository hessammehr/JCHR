package util;

public class BooleanUtils {
    public static boolean and(boolean x, boolean y) {
        return x && y;
    }
    
    public static boolean or(boolean x, boolean y) {
        return x || y;
    }
    
    public static boolean xor(boolean x, boolean y) {
        return x ^ y;
    }
    
    public static boolean imp(boolean x, boolean y) {
        return !x || y;
    }
    
    public static boolean not(boolean x) {
        return !x;
    }
    
    public static Boolean and(Boolean x, Boolean y) {
        return (x.booleanValue() && y.booleanValue())? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static Boolean or(Boolean x, Boolean y) {
        return (x.booleanValue() || y.booleanValue())? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static Boolean xor(Boolean x, Boolean y) {
        return (x.booleanValue() ^ y.booleanValue())? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static Boolean imp(Boolean x, Boolean y) {
        return (!x.booleanValue() || y.booleanValue())? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static Boolean not(Boolean x) {
        return x.booleanValue()? Boolean.FALSE : Boolean.TRUE;
    }
}
