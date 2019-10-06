package util;

import java.util.Formatter;

public class Timing {
    private Timing() {/* non-instantiatable utility class */}
    
    public static long timeNano(Runnable runnable) {
        long l = System.nanoTime();
        runnable.run();
        return System.nanoTime() - l;
    }
    
    public static long timeMilli(Runnable runnable) {
        long l = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - l;
    }
    
    public static void printNano(Runnable runnable) {
        System.out.println(timeNano(runnable));
    }
    
    public static void printMilli(Runnable runnable) {
        System.out.println(timeMilli(runnable));
    }
    
    public static String nano2secs(long nano) {
        return new Formatter().format("%fs", nano / 1000000000D).toString();
    }
}
