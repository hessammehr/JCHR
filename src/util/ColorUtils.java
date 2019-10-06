package util;

import java.awt.Color;

public class ColorUtils {
    private ColorUtils() {/* non-instantiatable utility class */}
    
    /**
     * Creates a new <code>Color</code> that is a brighter version of the given
     * <code>Color</code>.
     * <p>
     * This method applies a given scale factor to each of the three RGB 
     * components of this <code>Color</code> to create a brighter version
     * of this <code>Color</code>. Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors. 
     *
     * @param color
     *  The color we want a brighter version of.
     * @param factor
     *  The factor to multiply the different RGB components with;
     *  between 0.0f and 1.0f
     *  (the {@link Color#brighter()} method of the Sun reference implementation
     *  usus 0.7f).
     * 
     * @return     a new <code>Color</code> object that is  
     *                 a brighter version of this <code>Color</code>.
     * @see        Color#brighter()
     */
    public static Color brighter(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(
            java.lang.Math.min((int)(r/factor), 255),
            java.lang.Math.min((int)(g/factor), 255),
            java.lang.Math.min((int)(b/factor), 255)
        );
    }
    
    /**
     * Creates a new <code>Color</code> that is a darker version of the given
     * <code>Color</code>.
     * <p>
     * This method applies a given scale factor to each of the three RGB 
     * components of this <code>Color</code> to create a darker version
     * of this <code>Color</code>. Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors. 
     *
     * @param color
     *  The color we want a darker version of.
     * @param factor
     *  The factor to multiply the different RGB components with;
     *  between 0.0f and 1.0f
     *  (the {@link Color#darker()} method of the Sun reference implementation
     *  usus 0.7f).
     * 
     * @return     a new <code>Color</code> object that is  
     *                 a darker version of this <code>Color</code>.
     * @see        Color#darker()
     */
    public static Color darker(Color color, float factor) {
        return new Color(
            java.lang.Math.max((int)(color.getRed()  * factor), 0), 
            java.lang.Math.max((int)(color.getGreen()* factor), 0),
            java.lang.Math.max((int)(color.getBlue() * factor), 0)
        );
    }
}
