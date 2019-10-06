package util;

public class Math {

    private final static double LOG_2 = java.lang.Math.log(2); 
    public final static double log2(double a) {
        return java.lang.Math.log(a) / LOG_2;
    }
    
    public final static double log(double a, double base) {
        if (base == 10) return java.lang.Math.log10(a);    /* new in Java 1.5 */
        return java.lang.Math.log(a) / java.lang.Math.log(base);
    }

}