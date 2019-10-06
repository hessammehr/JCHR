package compiler.CHRIntermediateForm.types;

import static compiler.CHRIntermediateForm.matching.MatchingInfo.DIRECT_MATCH;
import static compiler.CHRIntermediateForm.matching.MatchingInfo.EXACT_MATCH;
import static compiler.CHRIntermediateForm.matching.MatchingInfo.NO_MATCH;

import java.util.List;
import java.util.Set;

import util.Arrays;
import util.collections.Empty;
import util.comparing.Comparison;
import util.exceptions.IllegalArgumentException;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;

import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.init.IDeclarator;
import compiler.CHRIntermediateForm.init.IInitialisator;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

/**
 * @author Peter Van Weert
 */
public enum PrimitiveType implements IType {
    BOOLEAN(Boolean.class, Boolean.TYPE),    
    DOUBLE(Double.class, Double.TYPE),
    FLOAT(Float.class, Float.TYPE, DOUBLE),
    LONG(Long.class, Long.TYPE, FLOAT, DOUBLE),
    INT(Integer.class, Integer.TYPE, LONG, FLOAT, DOUBLE),
    CHAR(Character.class, Character.TYPE, INT, LONG, FLOAT, DOUBLE),    
    SHORT(Short.class, Short.TYPE, INT, LONG, FLOAT, DOUBLE),
    BYTE(Byte.class, Byte.TYPE, SHORT, INT, LONG, FLOAT, DOUBLE),
    // A bit of a special case, but a primitive type nonetheless.
    VOID(Void.class, Void.TYPE);
    
    private PrimitiveType[] assignableTos;
    
    private Class<?> primitiveType;
    
    private GenericType wrapperType;
    
    private PrimitiveType(Class<?> wrapperClass, Class<?> primitiveType, PrimitiveType... assignableTos) {        
        setAssignableTos(assignableTos);
        setWrapperType(GenericType.getNonParameterizableInstance(wrapperClass));
        setPrimitiveType(primitiveType);
    }
    
    /**
     * Use this method instead of the {@link #valueOf(String)} method, since 
     * the latter method requires the characters to be in upper case.
     * 
     * @param name
     *  The name of the primitive type you want a representation of.
     * @return The <code>PrimitiveType</code> value representing the primitive
     *  type with name <code>name</code>.
     * @throws IllegalArgumentException
     *  If <code>name</code> is not a name of a primitive type.
     * 
     * @see #valueOf(String)
     */
    public static PrimitiveType getInstance(String name) throws IllegalArgumentException {
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (! Character.isLowerCase(chars[i]))
                throw new IllegalArgumentException(name);
            chars[i] = Character.toUpperCase(chars[i]);
        }
        return valueOf(new String(chars));
    }    
    
    public boolean isHashObservable() {
        return false;
    }
    public boolean isBuiltInConstraintObservable() {
        return false;
    }
    
    public static boolean isPrimitive(IType type) {
        return Arrays.identityContains(values(), type); 
    }
    
    public static PrimitiveType getCorrespondingPrimitiveType(GenericType wrapper) {
        for (PrimitiveType primitiveType : values())
            if (primitiveType.getWrapperType() == wrapper)
                return primitiveType;
        throw new IllegalArgumentException(wrapper);
    }
    
    public static boolean isPrimitiveType(String id) {
        try {
            // Lol, I must've been realy bored when writing this, 
            // but, hey, it *is* quite efficient!
            final char[] c = id.toCharArray();
            switch (c[0]) {
                case 'd':
                    return c.length == 6 
                        && c[1] == 'o' 
                        && c[2] == 'u'
                        && c[3] == 'b'
                        && c[4] == 'l'
                        && c[5] == 'e';
                case 'f': 
                    return c.length == 5 
                        && c[1] == 'l' 
                        && c[2] == 'o'
                        && c[3] == 'a'
                        && c[4] == 't';
                case 'l': 
                    return c.length == 4 
                        && c[1] == 'o' 
                        && c[2] == 'n'
                        && c[3] == 'g';
                case 'i': 
                    return c.length == 3 
                        && c[1] == 'n' 
                        && c[2] == 't';
                case 'c': 
                    return c.length == 4 
                        && c[1] == 'h' 
                        && c[2] == 'a'
                        && c[3] == 'r';
                case 's': 
                    return c.length == 5
                        && c[1] == 'h' 
                        && c[2] == 'o'
                        && c[3] == 'r'
                        && c[4] == 't';            
                case 'b': 
                    return (c.length == 7
                        && c[1] == 'o'
                        && c[2] == 'o'
                        && c[3] == 'l'
                        && c[4] == 'e'
                        && c[5] == 'a'
                        && c[6] == 'n')
                    || (c.length == 4
                        && c[1] == 'y' 
                        && c[2] == 't'
                        && c[3] == 'e');
                case 'v':
                    return c.length == 4
                        && c[1] == 'o' 
                        && c[2] == 'i'
                        && c[3] == 'd';
                
                default : return false;
            }
        } catch (NullPointerException e) {  // id == null
            return false;
        } catch (ArrayIndexOutOfBoundsException e) { // id != null && id.length() == 0 
            return false;
        }
    }

    public boolean isInterface() {
        return false;
    }
    public boolean isFixed() {
        return true;
    }
    
    public MatchingInfo isAssignableTo(IType type) {
        if (this == type) return EXACT_MATCH;
        if (isDirectlyAssignableTo(type)) return DIRECT_MATCH;
        
        if (! isPrimitive(type)) {
            MatchingInfo result = new MatchingInfo(); 
            try {
                result.initInitialisator(type.getInitialisatorFrom(this));
            } catch (AmbiguityException ae) {
                result.setAmbiguous();
            }
            return result;
        }
        else return NO_MATCH;
    }
    
    public boolean isCompatibleWith(IType other) {
        return Type.areCompatible(this, other);
    }
    
    public boolean isDirectlyAssignableTo(IType type) {
        return (type == this) || Arrays.identityContains(getAssignableTos(), type);
    }
    
    protected PrimitiveType[] getAssignableTos() {
        return assignableTos;
    }
    protected PrimitiveType getAssignableToAt(int index) {
        return getAssignableTos()[index];
    }
    protected int getNbAssignablesTos() {
        return getAssignableTos().length;
    }
    
    protected void setAssignableTos(PrimitiveType[] assignableTos) {
        this.assignableTos = assignableTos;
    }
    
    public List<CoerceMethod> getCoerceMethods() {
        return Empty.getInstance();
    }
    
    public IInitialisator<?> getInitialisatorFrom(IType type) throws AmbiguityException {
        return null;
    }
    public IDeclarator<?> getInitialisationDeclaratorFrom(IType type) throws AmbiguityException {
        return null;
    }
    public IDeclarator<?> getDeclarator() {
        return null;
    }

    public Field getField(String name) throws NoSuchFieldException {
        throw new NoSuchFieldException("Primitive types don't have fields"); 
    }
    
    public Set<Method> getMethods(String id) {
        return Empty.getInstance();
    }
    
    public List<JCHR_Constraints> getJCHR_ConstraintsAnnotations() {
        return Empty.getInstance();
    }
    public List<JCHR_Constraint> getJCHR_ConstraintAnnotations() {
        return Empty.getInstance();
    }
    
    public Class<?> getPrimitiveType() {
        return primitiveType;
    }
    protected void setPrimitiveType(Class<?> primitiveType) {
        this.primitiveType = primitiveType;
    }
    
    @Override
    public String toString() {
        return getPrimitiveType().getName();
    }

    public GenericType[] getWrapperTypes() {
        final int nbAssignableTos = getNbAssignablesTos();
        final GenericType[] result = new GenericType[nbAssignableTos + 1];
        result[0] = wrapperType;
        for (int i = 0; i < nbAssignableTos; i++)
            result[i+1] = getAssignableToAt(i).getWrapperType();
        return result;
    }    
    public GenericType getWrapperType() {
        return wrapperType;
    }
    protected void setWrapperType(GenericType wrapperType) {
        this.wrapperType = wrapperType;
    }
    
    public Comparison compareWith(IType other) {
        return Type.compare(this, other);
    }
    public String toTypeString() {
        return toString();
    }
    
    public Class<?> getErasure() {
        return getPrimitiveType();
    }
    public String getClassString() {
    	return getWrapperType().toTypeString().concat(".TYPE");
    }
}