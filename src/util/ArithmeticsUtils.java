package util;

import annotations.JCHR_Free;
import annotations.JCHR_Inline;

@JCHR_Free
public final class ArithmeticsUtils {
	private ArithmeticsUtils() {/* not instantiatable */}
	
	@JCHR_Inline("(byte)(%1 + %2)")
    public static byte add(byte x, byte y) {
        return (byte)(x + y);
    }
    @JCHR_Inline("(byte)(%1 - %2)")
    public static byte sub(byte x, byte y) {
        return (byte)(x - y);
    }
    @JCHR_Inline("(byte)(%1 * %2)")
    public static byte mult(byte x, byte y) {
        return (byte)(x * y);
    }
    @JCHR_Inline("(byte)(%1 / %2)")
    public static byte div(byte x, byte y) {
        return (byte)(x / y);
    }
    @JCHR_Inline("(byte)(%1 %% %2)")
    public static byte mod(byte x, byte y) {        
        return (byte)(x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(byte x, byte y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static byte inc(byte x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static byte dec(byte x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2)")
    public static byte max(byte x, byte y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2)")
    public static byte min(byte x, byte y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("(byte)-%1")
    public static byte neg(byte x) {
        return (byte)-x;
    }
    
    @JCHR_Inline("(short)(%1 + %2)")
    public static short add(short x, short y) {
        return (short)(x + y);
    }
    @JCHR_Inline("(short)(%1 - %2)")
    public static short sub(short x, short y) {
        return (short)(x - y);
    }
    @JCHR_Inline("(short)(%1 * %2)")
    public static short mult(short x, short y) {
        return (short)(x * y);
    }
    @JCHR_Inline("(short)(%1 / %2)")
    public static short div(short x, short y) {
        return (short)(x / y);
    }
    @JCHR_Inline("(short)(%1 %% %2)")
    public static short mod(short x, short y) {        
        return (short)(x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(short x, short y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static short inc(short x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static short dec(short x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2")
    public static short max(short x, short y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2")
    public static short min(short x, short y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("(short)-%1")
    public static short neg(short x) {
        return (short)-x;
    }
    
    @JCHR_Inline("(%1 + %2)")
    public static double add(double x, double y) {
        return (x + y);
    }
    @JCHR_Inline("(%1 - %2)")
    public static double sub(double x, double y) {
        return (x - y);
    }
    @JCHR_Inline("(%1 * %2)")
    public static double mult(double x, double y) {
        return (x * y);
    }
    @JCHR_Inline("(%1 / %2)")
    public static double div(double x, double y) {
        return (x / y);
    }
    @JCHR_Inline("(%1 %% %2)")
    public static double mod(double x, double y) {        
        return (x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(double x, double y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static double inc(double x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static double dec(double x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2)")
    public static double max(double x, double y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2)")
    public static double min(double x, double y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("-%1)")
    public static double neg(double x) {
        return -x;
    }
    
    @JCHR_Inline("(%1 + %2)")
    public static float add(float x, float y) {
        return (x + y);
    }
    @JCHR_Inline("(%1 - %2)")
    public static float sub(float x, float y) {
        return (x - y);
    }
    @JCHR_Inline("(%1 * %2)")
    public static float mult(float x, float y) {
        return (x * y);
    }
    @JCHR_Inline("(%1 / %2)")
    public static float div(float x, float y) {
        return (x / y);
    }
    @JCHR_Inline("(%1 %% %2)")
    public static float mod(float x, float y) {        
        return (x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(float x, float y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static float inc(float x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static float dec(float x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2)")
    public static float max(float x, float y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2)")
    public static float min(float x, float y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("-%1)")
    public static float neg(float x) {
        return -x;
    }
    
    @JCHR_Inline("(%1 + %2)")
    public static int add(int x, int y) {
        return (x + y);
    }
    @JCHR_Inline("(%1 - %2)")
    public static int sub(int x, int y) {
        return (x - y);
    }
    @JCHR_Inline("(%1 * %2)")
    public static int mult(int x, int y) {
        return (x * y);
    }
    @JCHR_Inline("(%1 / %2)")
    public static int div(int x, int y) {
        return (x / y);
    }
    @JCHR_Inline("(%1 %% %2)")
    public static int mod(int x, int y) {        
        return (x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(int x, int y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static int inc(int x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static int dec(int x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2)")
    public static int max(int x, int y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2)")
    public static int min(int x, int y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("-%1")
    public static int neg(int x) {
        return -x;
    }
    
    @JCHR_Inline("(%1 << %2)")
    public static int shift_ll(int x, int d) {
        return x << d;
    }
    @JCHR_Inline("(%1 >> %2)")
    public static int shift_rr(int x, int d) {
        return x >> d;
    }
    @JCHR_Inline("(%1 >>> %2)")
    public static int shift_rrr(int x, int d) {
        return x >>> d;
    }
    @JCHR_Inline("(%1 & %2)")
    public static int and(int x, int y) {
        return x & y;
    }
    @JCHR_Inline("(%1 | %2)")
    public static int or(int x, int y) {
        return x | y;
    }
    @JCHR_Inline("(%1 ^ %2)")
    public static int xor(int x, int y) {
        return x ^ y;
    }
    @JCHR_Inline("~%1")
    public static int compl(int x) {
        return ~x;
    }
    
    @JCHR_Inline("(%1 + %2)")
    public static long add(long x, long y) {
        return (x + y);
    }
    @JCHR_Inline("(%1 - %2)")
    public static long sub(long x, long y) {
        return (x - y);
    }
    @JCHR_Inline("(%1 * %2)")
    public static long mult(long x, long y) {
        return (x * y);
    }
    @JCHR_Inline("(%1 / %2)")
    public static long div(long x, long y) {
        return (x / y);
    }
    @JCHR_Inline("(%1 %% %2)")
    public static long mod(long x, long y) {        
        return (x % y);
    }
    @JCHR_Inline("(%1 %% %2 == 0)")
    public static boolean modZero(long x, long y) {        
        return x % y == 0;
    }
    @JCHR_Inline("(%1 + 1)")
    public static long inc(long x) {
        return ++x;
    }
    @JCHR_Inline("(%1 - 1)")
    public static long dec(long x) {
        return --x;
    }
    @JCHR_Inline("((%1 > %2)? %1 : %2)")
    public static long max(long x, long y) {
        return (x > y)? x : y;
    }
    @JCHR_Inline("((%1 < %2)? %1 : %2)")
    public static long min(long x, long y) {
        return (x < y)? x : y;
    }
    @JCHR_Inline("-%1")
    public static long neg(long x) {
        return -x;
    }
    
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() + %2.byteValue()))")
    public static Byte add(Byte x, Byte y) {
        return Byte.valueOf((byte)(x.byteValue() + y.byteValue()));
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() - %2.byteValue()))")
    public static Byte sub(Byte x, Byte y) {
        return Byte.valueOf((byte)(x.byteValue() - y.byteValue()));
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() * %2.byteValue()))")
    public static Byte mult(Byte x, Byte y) {
        return Byte.valueOf((byte)(x.byteValue() * y.byteValue()));
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() / %2.byteValue()))")
    public static Byte div(Byte x, Byte y) {
        return Byte.valueOf((byte)(x.byteValue() / y.byteValue()));
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() %% %2.byteValue()))")
    public static Byte mod(Byte x, Byte y) {        
        return Byte.valueOf((byte)(x.byteValue() % y.byteValue()));
    }
    @JCHR_Inline("(%1.byteValue() %% %2.byteValue() == 0)")
    public static boolean modZero(Byte x, Byte y) {        
        return x.byteValue() % y.byteValue() == 0;
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() + 1))")
    public static Byte inc(Byte x) {
        return Byte.valueOf((byte)(x.byteValue() + 1));
    }
    @JCHR_Inline("Byte.valueOf((byte)(%1.byteValue() - 1))")
    public static Byte dec(Byte x) {
        return Byte.valueOf((byte)(x.byteValue() - 1));
    } 
    @JCHR_Inline("(%1.byteValue() > %2.byteValue())")
    public static boolean gt(Byte x, Byte y) {
        return x.byteValue() > y.byteValue();
    }
    @JCHR_Inline("(%1.byteValue() >= %2.byteValue())")
    public static boolean ge(Byte x, Byte y) {
        return x.byteValue() >= y.byteValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Byte min(Byte x, Byte y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Byte max(Byte x, Byte y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Byte.valueOf((byte)-%1.byteValue())")
    public static Byte neg(Byte x) {
        return Byte.valueOf((byte)-x.byteValue());
    }
    
    @JCHR_Inline("Double.valueOf(%1.doubleValue() + %2.doubleValue())")
    public static Double add(Double x, Double y) {
        return Double.valueOf(x.doubleValue() + y.doubleValue());
    }
    @JCHR_Inline("Double.valueOf(%1.doubleValue() - %2.doubleValue())")
    public static Double sub(Double x, Double y) {
        return Double.valueOf(x.doubleValue() - y.doubleValue());
    }
    @JCHR_Inline("Double.valueOf(%1.doubleValue() * %2.doubleValue())")
    public static Double mult(Double x, Double y) {
        return Double.valueOf(x.doubleValue() * y.doubleValue());
    }
    @JCHR_Inline("Double.valueOf(%1.doubleValue() / %2.doubleValue())")
    public static Double div(Double x, Double y) {
        return Double.valueOf(x.doubleValue() / y.doubleValue());
    }
    @JCHR_Inline("Double.valueOf(%1.doubleValue() %% %2.doubleValue())")
    public static Double mod(Double x, Double y) {        
        return Double.valueOf(x.doubleValue() % y.doubleValue());
    }
    @JCHR_Inline("(%1.doubleValue() %% %2.doubleValue() == 0)")
    public static boolean modZero(Double x, Double y) {        
        return x.doubleValue() % y.doubleValue() == 0;
    }
    @JCHR_Inline("Double.valueOf((%1.doubleValue() + 1))")
    public static Double inc(Double x) {
        return Double.valueOf((x.doubleValue() + 1));
    }
    @JCHR_Inline("Double.valueOf((%1.doubleValue() - 1))")
    public static Double dec(Double x) {
        return Double.valueOf((x.doubleValue() - 1));
    } 
    @JCHR_Inline("(%1.doubleValue() > %2.doubleValue())")
    public static boolean gt(Double x, Double y) {
        return x.doubleValue() > y.doubleValue();
    }
    @JCHR_Inline("(%1.doubleValue() >= %2.doubleValue())")
    public static boolean ge(Double x, Double y) {
        return x.doubleValue() >= y.doubleValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Double min(Double x, Double y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Double max(Double x, Double y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Double.valueOf(-%1.doubleValue())")
    public static Double neg(Double x) {
        return Double.valueOf(-x.doubleValue());
    }
    
    @JCHR_Inline("Float.valueOf(%1.floatValue() + %2.floatValue())")
    public static Float add(Float x, Float y) {
        return Float.valueOf(x.floatValue() + y.floatValue());
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() - %2.floatValue())")
    public static Float sub(Float x, Float y) {
        return Float.valueOf(x.floatValue() - y.floatValue());
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() * %2.floatValue())")
    public static Float mult(Float x, Float y) {
        return Float.valueOf(x.floatValue() * y.floatValue());
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() / %2.floatValue())")
    public static Float div(Float x, Float y) {
        return Float.valueOf(x.floatValue() / y.floatValue());
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() %% %2.floatValue())")
    public static Float mod(Float x, Float y) {        
        return Float.valueOf(x.floatValue() % y.floatValue());
    }
    @JCHR_Inline("(%1.floatValue() %% %2.floatValue() == 0)")
    public static boolean modZero(Float x, Float y) {        
        return x.floatValue() % y.floatValue() == 0;
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() + 1)")
    public static Float inc(Float x) {
        return Float.valueOf(x.floatValue() + 1);
    }
    @JCHR_Inline("Float.valueOf(%1.floatValue() - 1)")
    public static Float dec(Float x) {
        return Float.valueOf(x.floatValue() - 1);
    } 
    @JCHR_Inline("(%1.floatValue() > %2.floatValue())")
    public static boolean gt(Float x, Float y) {
        return x.floatValue() > y.floatValue();
    }
    @JCHR_Inline("(%1.floatValue() >= %2.floatValue())")
    public static boolean ge(Float x, Float y) {
        return x.floatValue() >= y.floatValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Float min(Float x, Float y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Float max(Float x, Float y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Float.valueOf(-%1.floatValue())")
    public static Float neg(Float x) {
        return Float.valueOf(-x.floatValue());
    }
    
    @JCHR_Inline("Integer.valueOf(%1.intValue() + %2.intValue())")
    public static Integer add(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() + y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() - %2.intValue())")
    public static Integer sub(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() - y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() * %2.intValue())")
    public static Integer mult(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() * y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() / %2.intValue())")
    public static Integer div(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() / y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() %% %2.intValue())")
    public static Integer mod(Integer x, Integer y) {        
        return Integer.valueOf(x.intValue() % y.intValue());
    }
    @JCHR_Inline("(%1.intValue() %% %2.intValue() == 0)")
    public static boolean modZero(Integer x, Integer y) {        
        return x.intValue() % y.intValue() == 0;
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() + 1)")
    public static Integer inc(Integer x) {
        return Integer.valueOf(x.intValue() + 1);
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() - 1)")
    public static Integer dec(Integer x) {
        return Integer.valueOf(x.intValue() - 1);
    } 
    @JCHR_Inline("(%1.intValue() > %2.intValue())")
    public static boolean gt(Integer x, Integer y) {
        return x.intValue() > y.intValue();
    }
    @JCHR_Inline("(%1.intValue() >= %2.intValue())")
    public static boolean ge(Integer x, Integer y) {
        return x.intValue() >= y.intValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Integer min(Integer x, Integer y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Integer max(Integer x, Integer y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Integer.valueOf(-%1.intValue())")
    public static Integer neg(Integer x) {
        return Integer.valueOf(-x.intValue());
    }
    
    @JCHR_Inline("Integer.valueOf(%1.intValue() <<< %2.intValue())")
    public static Integer shift_ll(Integer x, Integer d) {
        return Integer.valueOf(x.intValue() << d.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() >> %2.intValue())")
    public static Integer shift_rr(Integer x, Integer d) {
        return Integer.valueOf(x.intValue() >> d.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() >>> %2.intValue())")
    public static Integer shift_rrr(Integer x, Integer d) {
        return Integer.valueOf(x.intValue() >>> d.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() & %2.intValue())")
    public static Integer and(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() & y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() | %2.intValue())")
    public static Integer or(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() | y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(%1.intValue() ^ %2.intValue())")
    public static Integer xor(Integer x, Integer y) {
        return Integer.valueOf(x.intValue() ^ y.intValue());
    }
    @JCHR_Inline("Integer.valueOf(~%1.intValue())")
    public static Integer compl(Integer x) {
        return Integer.valueOf(~x.intValue());
    }
    
    @JCHR_Inline("Long.valueOf(%1.longValue() + %2.longValue())")
    public static Long add(Long x, Long y) {
        return Long.valueOf(x.longValue() + y.longValue());
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() - %2.longValue())")
    public static Long sub(Long x, Long y) {
        return Long.valueOf(x.longValue() - y.longValue());
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() * %2.longValue())")
    public static Long mult(Long x, Long y) {
        return Long.valueOf(x.longValue() * y.longValue());
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() / %2.longValue())")
    public static Long div(Long x, Long y) {
        return Long.valueOf(x.longValue() / y.longValue());
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() %% %2.longValue())")
    public static Long mod(Long x, Long y) {        
        return Long.valueOf(x.longValue() % y.longValue());
    }
    @JCHR_Inline("(%1.longValue() %% %2.longValue() == 0)")
    public static boolean modZero(Long x, Long y) {        
        return x.longValue() % y.longValue() == 0;
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() + 1)")
    public static Long inc(Long x) {
        return Long.valueOf(x.longValue() + 1);
    }
    @JCHR_Inline("Long.valueOf(%1.longValue() - 1)")
    public static Long dec(Long x) {
        return Long.valueOf(x.longValue() - 1);
    } 
    @JCHR_Inline("(%1.longValue() > %2.longValue())")
    public static boolean gt(Long x, Long y) {
        return x.longValue() > y.longValue();
    }
    @JCHR_Inline("(%1.longValue() >= %2.longValue())")
    public static boolean ge(Long x, Long y) {
        return x.longValue() >= y.longValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Long min(Long x, Long y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Long max(Long x, Long y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Long.valueOf(-%1.longValue())")
    public static Long neg(Long x) {
        return Long.valueOf(-x.longValue());
    }
    
    @JCHR_Inline("Short.valueOf((byte)(%1.shortValue() + %2.shortValue()))")
    public static Short add(Short x, Short y) {
        return Short.valueOf((short)(x.shortValue() + y.shortValue()));
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() - %2.shortValue()))")
    public static Short sub(Short x, Short y) {
        return Short.valueOf((short)(x.shortValue() - y.shortValue()));
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() * %2.shortValue()))")
    public static Short mult(Short x, Short y) {
        return Short.valueOf((short)(x.shortValue() * y.shortValue()));
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() / %2.shortValue()))")
    public static Short div(Short x, Short y) {
        return Short.valueOf((short)(x.shortValue() / y.shortValue()));
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() %% %2.shortValue()))")
    public static Short mod(Short x, Short y) {        
        return Short.valueOf((short)(x.shortValue() % y.shortValue()));
    }
    @JCHR_Inline("(%1.shortValue() %% %2.shortValue() == 0)")
    public static boolean modZero(Short x, Short y) {        
        return x.shortValue() % y.shortValue() == 0;
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() + 1))")
    public static Short inc(Short x) {
        return Short.valueOf((short)(x.shortValue() + 1));
    }
    @JCHR_Inline("Short.valueOf((short)(%1.shortValue() - 1))")
    public static Short dec(Short x) {
        return Short.valueOf((short)(x.shortValue() - 1));
    } 
    @JCHR_Inline("(%1.shortValue() > %2.shortValue())")
    public static boolean gt(Short x, Short y) {
        return x.shortValue() > y.shortValue();
    }
    @JCHR_Inline("(%1.shortValue() >= %2.shortValue())")
    public static boolean ge(Short x, Short y) {
        return x.shortValue() >= y.shortValue();
    }
    @JCHR_Inline("((%1.compareTo(%2) < 0)? %1 : %2)")
    public static Short min(Short x, Short y) {
        return (x.compareTo(y) < 0)? x : y;
    }
    @JCHR_Inline("((%1.compareTo(%2) > 0)? %1 : %2)")
    public static Short max(Short x, Short y) {
        return (x.compareTo(y) > 0)? x : y;
    }
    @JCHR_Inline("Short.valueOf((short)-%1.shortValue())")
    public static Short neg(Short x) {
        return Short.valueOf((short)-x.shortValue());
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(int x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(int x) {
        return x % 2 == 0;
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(short x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(short x) {
        return x % 2 == 0;
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(byte x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(byte x) {
        return x % 2 == 0;
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(long x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(long  x) {
        return x % 2 == 0;
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(float x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(float x) {
        return x % 2 == 0;
    }
    
    @JCHR_Inline("(%1 %% 2 != 0)")
    public static boolean odd(double x) {
        return x % 2 != 0;
    }
    @JCHR_Inline("(%1 %% 2 == 0)")
    public static boolean even(double x) {
        return x % 2 == 0;
    }
}