package compiler.CHRIntermediateForm.types;

import static compiler.CHRIntermediateForm.types.PrimitiveType.BOOLEAN;
import static util.comparing.Comparison.AMBIGUOUS;
import static util.comparing.Comparison.BETTER;
import static util.comparing.Comparison.EQUAL;
import static util.comparing.Comparison.WORSE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import util.comparing.Comparison;

/**
 * @author Peter Van Weert
 */
public abstract class Type implements IType {

    /**
     * <p>
     * The set of all classes of which the compiler knows they are
     * fixed. This does not include the eight primitive types or
     * their wrappers, nor the {@link String}, {@link BigInteger}, {@link BigDecimal} 
     * or {@link Enum} types, all of which are also known to fixed.
     * </p><p>
     * Classes that are in this set should be immutable.
     * Adding mutable classes is also possible, but take care that the 
     * possible mutations do not influence the guards of the compiled programs.
     * Also take care when adding non-final classes if subclasses 
     * can be made mutable. 
     * </p>
     */
    public final static Set<Class<?>> FIXED_CLASSES = new HashSet<Class<?>>();
    static {
        FIXED_CLASSES.add(java.awt.AWTKeyStroke.class);
        FIXED_CLASSES.add(javax.swing.KeyStroke.class);
        FIXED_CLASSES.add(java.util.Locale.class);
        FIXED_CLASSES.add(java.net.URI.class);
        FIXED_CLASSES.add(java.net.URL.class);
        FIXED_CLASSES.add(java.lang.Class.class);
        FIXED_CLASSES.add(java.lang.Package.class);
//        FIXED_CLASSES.add(javax.management.ImmutableDescriptor.class);
        FIXED_CLASSES.add(java.awt.Color.class);
        FIXED_CLASSES.add(java.math.MathContext.class);
        FIXED_CLASSES.add(java.util.Currency.class);
        FIXED_CLASSES.add(java.util.Formatter.class);
    }

    public boolean isCompatibleWith(IType other) {
        return areCompatible(this, other);
    }
    
    static boolean areCompatible(IType one, IType other) {
        return one == other
            || one.isDirectlyAssignableTo(other)
            || other.isDirectlyAssignableTo(one);
    }
    
    public Comparison compareWith(IType other) {
        return compare(this, other);
    }
    
    public String toTypeString() {
        return toString();
    }
    
    public static Comparison compare(IType x, IType y) {
        boolean 
            x2y = x.isDirectlyAssignableTo(y),
            y2x = y.isDirectlyAssignableTo(x);
        
        if (x2y && !y2x) return WORSE;  // y is algemener dan x
        if (!x2y && y2x) return BETTER; // x is algemener dan y
        if (x2y && y2x)  return EQUAL;
        
        /* !x2y && !y2x */
        
        // speciaal geval: in een van de richtingen is coercing mogelijk
        x2y = x.isAssignableTo(y).isNonInitMatch();
        y2x = y.isAssignableTo(x).isNonInitMatch();
        
        if (x2y && !y2x) return BETTER; // x is algemener dan y
        if (y2x && !x2y) return WORSE;  // y is algemener dan x
        
        return AMBIGUOUS;
    }
    
    /**
     * Returns whether the given type is considered a boolean type.
     * No coercion is allowed, but auto-unboxing is. This means
     * that there are two boolean types: the primitive <code>boolean</code>
     * type itself and its wrapper type, {@link Boolean}.
     * 
     * @param type
     * @return <code>true</code> if the given type is a boolean type,
     *  <code>false</code> otherwise.
     */
    public static boolean isBoolean(IType type) {
        return type == BOOLEAN
            || type == BOOLEAN.getWrapperType();
    }
}