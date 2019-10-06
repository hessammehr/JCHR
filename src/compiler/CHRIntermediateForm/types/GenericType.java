package compiler.CHRIntermediateForm.types;

import static compiler.CHRIntermediateForm.matching.MatchingInfo.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import runtime.BuiltInConstraintObservable;
import runtime.hash.HashObservable;
import util.Annotations;
import util.Arrays;
import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;
import annotations.JCHR_Fixed;

import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.exceptions.ToDoException;
import compiler.CHRIntermediateForm.init.AssignmentInitialisator;
import compiler.CHRIntermediateForm.init.IDeclarator;
import compiler.CHRIntermediateForm.init.IInitialisator;
import compiler.CHRIntermediateForm.init.Initialisator;
import compiler.CHRIntermediateForm.init.InitialisatorMethod;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.members.Constructor;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;

/**
 * @author Peter Van Weert
 */
public class GenericType extends Type {
    
    /**
     * <p>
     * I think we have to cache the supertype in case there is 
     * a recursion in the type definition, like e.g. in enumeration types: 
     * <code>SomeType extends Enum&lt;SomeType&gt;</code>, with
     * <code>Enum&lt;E extends Enum&lt;E&gt;&gt;</code>.
     * The latter example would go into an infinite loop, because
     * we would be asking for the supertype of <code>SomeType</code>,
     * which is <code>Enum&lt;SomeType&gt;</code>, requiring thus
     * <code>SomeType</code> as a type parameter and to check whether
     * the latter type parameter is correct, we again need its
     * supertype and so on... (or something like that).
     * </p>
     * <p>
     * It is lazily initialized.
     * </p>
     */
    private GenericType superType;
    
    private transient static Map<Class<?>, GenericType> cache;
    private transient static Map<GenericType, GenericType> cache2;
    
    static {
        cache = new HashMap<Class<?>, GenericType>();
        cache.put(Object.class, (GenericType)OBJECT);
        cache2 = new HashMap<GenericType, GenericType>();
    }
    
    /**
     * The raw type of this generic (possibly parameterized) type.
     */
    private Class<?> rawType;
    
    /**
     * The type parameters of this generic type. For non-generic classes,
     * this array will have length 0.
     */
    private IType[] typeParameters;
    
    /**
     * A list of methods that can be used to coerce this generic type
     * to other types.
     * This list is initialized lazily.
     */
    private List<CoerceMethod> coerceMethods;
    
    /* * * * * * * * *\
     * Constructors  *
     * * * * * * * * */ 
    
    protected GenericType(Class<?> rawType) {
        this(getNbParameters(rawType), rawType);
    }
    
    protected GenericType(int nbParameters, Class<?> theClass) {
        setTypeParameters(new IType[nbParameters]);
        setRawType(theClass);
    }
    
    /* * * * * * * * * *\
     * Factory Methods *
    \* * * * * * * * * */
    
    public static GenericType getParameterizableInstance(ParameterizedType type) {
        // It is safe to cast to Class: cf. Java bug 6255169 
        // (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6255169)        
        return new GenericType((Class<?>)type.getRawType());
    }
    
    public static GenericType getUniqueInstance(GenericType type) {
        GenericType result = cache2.get(type);
        if (result != null) return result;
        cache2.put(type, type);
        return type;
    }
    
    /*
     * @pre ! rawType.isPrimitive() 
     */
    public static GenericType getInstance(Class<?> rawType) {
        final int nbParameters = getNbParameters(rawType);
        if (nbParameters > 0)
            return new GenericType(nbParameters, rawType);
        else
            return getNonParameterizableInstance(rawType);
    }
    
    /**
     * Returns the generic type object reflecting the given
     * non-parameterizable class. We use the flyweight pattern here.
     * 
     * @param rawType
     *  The raw type we want a generic type representation of
     *  (though strictly speaking this is not a generic type).
     *  
     * @return The generic type object reflecting the given
     *  non-parameterizable class.
     */
    public static GenericType getNonParameterizableInstance(Class<?> rawType) {
        GenericType result = cache.get(rawType);
        
        if (result == null) {
            result = new GenericType(0, rawType);
            cache.put(rawType, result);
        }

        return result;
    }
    
    public static GenericType getClassInstance(Class<?> someClass) {
        final GenericType classInstance = 
            GenericType.getInstance(Class.class);
        classInstance.addTypeParameter(
            GenericType.getInstance(someClass)
        );
        return classInstance;
    }
    
    public boolean isInterface() {
        return getRawType().isInterface();
    }
    
    public boolean isFixed() {
        return isLiteralType() 
            || getRawType().isEnum()
            || getRawType().isAnnotationPresent(JCHR_Fixed.class)
            || FIXED_CLASSES.contains(getRawType());
    }
    
    @Override
    public boolean isCompatibleWith(IType other) {
        return super.isCompatibleWith(other)
            || (this.isInterface() && other.isInterface());   
    }
    
    public MatchingInfo isAssignableTo(IType other) {
        if (this.equals(other))
            return EXACT_MATCH;
        if (this.isDirectlyAssignableTo(other))
            return DIRECT_MATCH;
        
        MatchingInfo result = new MatchingInfo();
        MatchingInfo info;
        for (CoerceMethod coerce : getCoerceMethods()) {
            info = coerce.getReturnType().isAssignableTo(other);
            if (info.isMatch() && ! result.initCoerceMethods(info, coerce))
                break;  /* AMBIGUOUS! */
        }
        
        try {
            if (! result.isMatch() || result.isInitMatch()) 
                result.initInitialisator(other.getInitialisatorFrom(this));
            
        } catch (AmbiguityException ae) {
            result.setAmbiguous();
        }
        
        return result;
    } 
    
    public boolean isDirectlyAssignableTo(IType type) {
        if (this.equals(type)) return true;
        
        if (type instanceof GenericType) {
            final GenericType other = (GenericType)type;
                        
            if (! other.getRawType().isAssignableFrom(this.getRawType()))
                return false;
            
            // TODO: the following is probably not correct, but it should
            //  work in most cases
            if (!this.hasTypeParameters() || !other.hasTypeParameters())
                return true;
            
            // We know now that <other> is a superclass or superinterface
            // of <this>. It does not have to be the same raw type though.
            // When comparing the type parameters we have to take this
            // into account: the value of the type variable of <this>
            // when treated as a "<other>" has to be the same as the
            // type parameter of <other>...
            
            for (int i = 0; i < other.getNbTypeVariables(); i++)
                if (! Reflection.reflect(this, other.getTypeVariableAt(i))
                        .equals(other.getTypeParameterAt(i)))
                  return false;
            
            return true;
            
        } else if (type instanceof PrimitiveType) {
            return false;
        } else if (type instanceof TypeParameter) {
            for (IType other : ((TypeParameter)type).getUpperBounds())
                if (this.isDirectlyAssignableTo(other))
                    return true;

            return false;
        } else throw new RuntimeException();
    }
    
    public boolean isLiteralType() {
        return isPrimitiveWrapper()
            || isImmutableNonPrimitiveNumberWrapper()
            || getRawType().equals(String.class);
    }
    
    public static boolean isLiteralType(Class<?> clazz) {
    	return isPrimitiveWrapper(clazz)
    		|| isImmutableNonPrimitiveNumberWrapper(clazz)
    		|| clazz.equals(String.class);
    }
    
    public boolean isPrimitiveWrapper() {
        return isComparablePrimitiveWrapper()
            || getRawType().equals(Boolean.class);
    }
    
    public boolean isComparablePrimitiveWrapper() {
        return isPrimitiveNumberWrapper()
            || getRawType().equals(Character.class);
    }
    
    public boolean isPrimitiveNumberWrapper() {
        return isPrimitiveNumberWrapper(getRawType());
    }
    public static boolean isPrimitiveNumberWrapper(Class<?> clazz) {
    	return clazz.equals(Integer.class)
	        || clazz.equals(Long.class)
	        || clazz.equals(Float.class)
	        || clazz.equals(Double.class)
	        || clazz.equals(Byte.class)
	        || clazz.equals(Short.class);
    }

    public static boolean isPrimitiveWrapper(IType type) {
        return (type instanceof GenericType)
            && ((GenericType)type).isPrimitiveWrapper();
    }
    
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
    	return isPrimitiveNumberWrapper(clazz)
    		|| clazz.equals(Boolean.class);
    }
    
    public boolean isImmutableNumberWrapper() {
        // better (since there are Number subclasses that are mutable):
        return isPrimitiveNumberWrapper()
            || isImmutableNonPrimitiveNumberWrapper();
    }
    
    public boolean isImmutableNonPrimitiveNumberWrapper() {
        return isImmutableNonPrimitiveNumberWrapper(getRawType());
    }
    
    public static boolean isImmutableNumberWrapper(Class<?> clazz) {
    	return isPrimitiveNumberWrapper(clazz)
    		|| isImmutableNonPrimitiveNumberWrapper(clazz);
    }
    
    public static boolean isImmutableNonPrimitiveNumberWrapper(Class<?> clazz) {
    	return clazz.equals(BigInteger.class)
        	|| clazz.equals(BigDecimal.class);
    }
    
    public List<CoerceMethod> getCoerceMethods() {
        // eens het als variabletype wordt gebruikt is het niet
        // onredelijk te verwachten dat deze info nog wel eens
        // zal worden opgevraagd ==> cachen?
        if (coerceMethods == null) initCoerceMethods();
        return coerceMethods;
    }
    
    public void initCoerceMethods() {
        setCoerceMethods(CoerceMethod.getCoerceMethods(this));
    }
    
    protected void setCoerceMethods(List<CoerceMethod> coerceMethods) {
        this.coerceMethods = coerceMethods;
    }
    
    public IInitialisator<?> getInitialisatorFrom(IType type) 
    throws AmbiguityException {
        return Initialisator.getInitialisatorFrom(this, type);
    }
    public IDeclarator<?> getInitialisationDeclaratorFrom(IType type) throws AmbiguityException {
        IInitialisator<?> initialisator 
            = InitialisatorMethod.getInitialisatorMethodFrom(this, type);
        if (initialisator == null)
            return null;
        return new AssignmentInitialisator(initialisator);
    }
    public IDeclarator<?> getDeclarator() throws AmbiguityException {
        IInitialisator<?> initialisator 
            = InitialisatorMethod.getDeclaratorInitialisatorMethod(this);
        if (initialisator == null)
            initialisator = Constructor.getDeclaratorInitialisator(this);
        if (initialisator == null)
            return null;
        return new AssignmentInitialisator(initialisator);
    }
    
    public Field getField(String name) throws NoSuchFieldException {
        return new Field(this, name);
    }
    
    public Set<Method> getMethods(String id) {
        return Method.getMethods(this, id);
    }
    
    /**
     * <p>
     * Returns the <tt>GenericType</tt> representing the direct superclass of
     * the entity (class or interface) represented by this <tt>GenericType</tt>.
     * </p>
     * <p>
     * If this <tt>GenericType</tt> represents either the <tt>Object</tt>
     * class or an interface, then <code>null</code> is returned.  
     * </p> 
     * 
     * @return the <tt>GenericType</tt> representing the direct superclass of
     *  the entity (class or interface) represented by this <tt>GenericType</tt>.
     */
    public GenericType getSupertype() {
        if (superType != null) return superType;
        java.lang.reflect.Type type = getRawType().getGenericSuperclass();
        return (type == null)? null : (GenericType)getValueOf(type, true);
    }
    
    /**
     * <p>
     * Returns representations of all interfaces directly implemented by
     * the class represented by this generic type.
     * The order of the interface objects in the array corresponds to 
     * the order of the interface names in the <code>implements</code> clause 
     * of the declaration of the class represented by this object.
     * </p>
     * <p>
     * If this object represents an interface, the array contains objects 
     * representing all interfaces directly extended by the interface. 
     * The order of the interface objects in the array corresponds to 
     * the order of the interface names in the <code>extends</code> clause 
     * of the declaration of the interface represented by this object.
     * </p>
     * 
     * @return Representations of all interfaces directly implemented (or
     *  extended) by the class (or interface) represented by this generic 
     *  type.
     */
    public GenericType[] getInterfaces() {
        java.lang.reflect.Type[] interfaces = getRawType().getGenericInterfaces();
        GenericType[] result = new GenericType[interfaces.length];
        for (int i = 0; i < interfaces.length; i++)
            result[i] = (GenericType)getValueOf(interfaces[i], false);
        return result;
    }
    
    /**
     * <p>
     * Returns a representation of the <code>index</code>'th interface
     * directly implemented by this generic type.
     * The order of the interface objects in the array corresponds to 
     * the order of the interface names in the <code>implements</code> clause 
     * of the declaration of the class represented by this object.
     * </p>
     * <p>
     * If this object represents an interface, the result is represents
     * the <code>index</code>'th interface directly extended by the 
     * interface. 
     * The order of the interface objects in the array corresponds to 
     * the order of the interface names in the <code>extends</code> clause 
     * of the declaration of the interface represented by this object.
     * </p>
     * 
     * @param index
     *  The index of the interface we are interested in.
     *  
     * @return A reflection of the <code>index</code>'th interface
     *  implemented or extended by this generic type.
     */
    public GenericType getInterfaceAt(int index) {
        java.lang.reflect.Type[] interfaces = getRawType().getGenericInterfaces();
        return (GenericType)getValueOf(interfaces[index], false);
    }

    /*
     * Returns either a GenericType or a TypeParameter 
     *  (the latter can only occur in the recursive case!)
     */
    protected IType getValueOf(java.lang.reflect.Type type, boolean cacheSuperType) {
        // base cases:
        if (type instanceof Class<?>)
            return GenericType.getNonParameterizableInstance((Class<?>)type);
        if (type instanceof TypeVariable<?>)
            return getValueOf((TypeVariable<?>)type);
        
        // recursive case:
        if (type instanceof ParameterizedType) {
            GenericType result =
                GenericType.getParameterizableInstance(((ParameterizedType)type));
            
            if (cacheSuperType) superType = result;
            
            for (java.lang.reflect.Type typearg
                    : ((ParameterizedType)type).getActualTypeArguments())
                result.addTypeParameter(getValueOf(typearg, false));
            
            result = getUniqueInstance(result);
            if (cacheSuperType) superType = result;
            return result;
        }
        
        // todo cases (arrays, wildcards, etc)
        throw new ToDoException("Unsupported reflection?");
    }
    
    /**
     * Returns the actual value of the given type variable.
     * 
     * @param var
     *  A type variable of (the raw class of) this parameterized generic 
     *  type.
     * 
     * @return The actual value of the given type variable.
     * 
     * @throws IndexOutOfBoundsException
     *  If the given type variable is not a type variable of 
     *  (the raw class of) this parameterized generic type.   
     */
    public IType getValueOf(TypeVariable<?> var) throws IndexOutOfBoundsException {
        return getTypeParameterAt(
            Arrays.firstIndexOf(getRawType().getTypeParameters(), var)
        );
    }
    
    /**
     * Checks whether the given type variable is a type variable of 
     * (the raw type) of this generic type.
     * 
     * @param var
     *  The type variable to check.
     *  
     * @return <code>true</code> iff the given type variable is a 
     *  type variable of (the raw type) of this generic type.
     */
    public boolean hasAsTypeVariable(TypeVariable<?> var) {
        return Arrays.contains(getRawType().getTypeParameters(), var);
    }
    
    public boolean isParametrizable() {
        return getNbTypeVariables() > 0;
    }
    
    public static int getNbParameters(Class<?> rawType) {
        return rawType.getTypeParameters().length;
    }
    
    public static boolean isParameterizable(Class<?> rawType) {
        return getNbParameters(rawType) > 0;
    }
    
    public Constructor[] getConstructors() {
        final java.lang.reflect.Constructor<?>[] constructors = getRawType().getConstructors();
        final Constructor[] result = new Constructor[constructors.length];
        for (int i = 0; i < constructors.length; i++)
            result[i] = new Constructor(this, constructors[i]);
        return result;
    }
    
    public List<JCHR_Constraints> getJCHR_ConstraintsAnnotations() {
        return Annotations.getAnnotations(getRawType(), JCHR_Constraints.class);
    }
    
    public List<JCHR_Constraint> getJCHR_ConstraintAnnotations() {
        return Annotations.getAnnotations(getRawType(), JCHR_Constraint.class);
    }
    
    public boolean isHashObservable() {
        return HashObservable.class.isAssignableFrom(getRawType());
    }
    public boolean isBuiltInConstraintObservable() {
        return BuiltInConstraintObservable.class.isAssignableFrom(getRawType());
    }
    
    public Class<?> getErasure() {
        return getRawType();
    }
    public String getClassString() {
    	return getRawType().getCanonicalName().concat(".class");
    }
    
    public IType[] getTypeParameters() {
        if (hasTypeParameters())
            return typeParameters;
        else {
            IType[] result = new IType[typeParameters.length];
            java.util.Arrays.fill(result, OBJECT);
            return result;
        }
    }
    protected void setTypeParameters(IType[] typeParameters) {
        this.typeParameters = typeParameters;
    }
    
    public IType getTypeParameterAt(int index) {
        if (! hasTypeParameterAt(index)) return OBJECT;
        return typeParameters[index];
    }
    protected boolean hasTypeParameterAt(int index) {
        if (index < 0 || index > getNbTypeVariables())
            throw new IndexOutOfBoundsException(String.valueOf(index));
        return (typeParameters.length != 0 && typeParameters[index] != null);
    }
    public int getNbTypeParameters() {
        int result = 0;
        for (int i = 0; i < getNbTypeVariables(); i++)
            if (hasTypeParameterAt(i)) result++;
        return result;
    }
    public boolean hasTypeParameters() {
        return getNbTypeParameters() > 0;
    }
    
    public void addTypeParameter(IType typeParameter) 
    throws IllegalArgumentException, IndexOutOfBoundsException {
        putTypeParameterAt(typeParameter, getNbTypeParameters());
    }
    public void putTypeParameterAt(IType typeParameter, int index)    
    throws IllegalArgumentException, IndexOutOfBoundsException {
        if (! isValidTypeParameter(typeParameter, index))
            throw new IllegalArgumentException(
                typeParameter.toString() + " @ index " + index
            );
        
        typeParameters[index] = typeParameter;
    }
    
    public boolean isValid() {
        int nb = getNbTypeParameters();
        return (nb == 0) || (nb == getNbTypeVariables());
    }
    
    public boolean isValidTypeParameter(IType typeParameter, int index) {
        if (! isValidTypeParameter(typeParameter)) return false;
        
        // Because bounds may use the type parameter itself, we need to
        // temporary assign it in order test the validity...
        // For example:     T extends Comparable<T>
        //          or      E extends Enum<E>
        
        IType old = typeParameters[index];
        typeParameters[index] = typeParameter;
        
        boolean result = testBounds(index);
        
        typeParameters[index] = old;
        
        return result;
    }
    
    public boolean hasValidTypeParameterAt(int index) {        
        return isValidTypeParameter(getTypeParameterAt(index)) 
            && testBounds(index);
    }
    
    /**
     * This method test whether the actual type parameter at the given index is
     * within the bounds specified by the formal type variable at that index.
     * 
     * @param index
     *  The index of the type parameter we want to test.
     * @return Whether or not the actual type parameter at the given index is
     *  within the bounds specified by the formal type variable at that index.
     */
    protected boolean testBounds(int index) {
        final IType typeParameter = getTypeParameterAt(index);
        for (java.lang.reflect.Type bound : getTypeVariableAt(index).getBounds())
            //if (! typeParameter.isAssignableTo(Reflection.reflect(this, bound)).isNonAmbiguousMatch())
            if (! typeParameter.isDirectlyAssignableTo(Reflection.reflect(this, bound)))
                return false;
        return true;
    }
    
    public static boolean isValidTypeParameter(IType typeParameter) {
        return (typeParameter != null) 
            && !PrimitiveType.isPrimitive(typeParameter);
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof GenericType)
            && this.equals((GenericType)other);
    }
    
    public boolean equals(GenericType other) {
        return (this == other)
            || (this.getRawType().equals(other.getRawType()) 
                    && java.util.Arrays.equals(
                            this.getTypeParameters(), 
                            other.getTypeParameters()
                        )
            );
    }
    
    @Override
    public int hashCode() {        
        int result = 23;
        for (IType typeParameter : getTypeParameters())            
            result = 37 * result + 
                ((typeParameter == null)? 0 : typeParameter.hashCode());
        return 37 * result + getRawType().hashCode();
    }
    
    public TypeVariable<?>[] getTypeVariables() {
        return getRawType().getTypeParameters();
    }
    public TypeVariable<?> getTypeVariableAt(int index) {
        return getTypeVariables()[index];
    }
    public int getNbTypeVariables() {
        return getTypeVariables().length;
    }

    public Class<?> getRawType() {
        return rawType;
    }
    protected void setRawType(Class<?> rawType) {
        this.rawType = rawType;
    }
    public boolean hasAsRawType(Class<?> rawType) {
        return getRawType().equals(rawType);
    }
    
    @Override
    public String toTypeString() {
        StringBuilder result = new StringBuilder(getRawType().getCanonicalName());
        if (hasTypeParameters()) {
            result.append('<').append(getTypeParameterAt(0).toTypeString());
            final int nbTypeParameters = getNbTypeParameters();
            for (int i = 1; i < nbTypeParameters; i++) 
                result.append(',').append(getTypeParameterAt(i).toTypeString());
            result.append('>');
        }
        return result.toString();
    }    
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getRawType().getCanonicalName());
        if (hasTypeParameters()) {
            result.append('<').append(getTypeParameterAt(0));
            final int nbTypeParameters = getNbTypeParameters();
            for (int i = 1; i < nbTypeParameters; i++)
                result.append(',').append(getTypeParameterAt(i));
            result.append('>');
        }
        return result.toString();
    }
    
    @SuppressWarnings("unchecked")
    public String toParametrizableString() {
        Class raw = getRawType();
        TypeVariable<Class>[] typeVariables = raw.getTypeParameters();
        StringBuilder result = new StringBuilder(raw.getCanonicalName());
        if (typeVariables.length != 0) {
            result.append('<').append(typeVariables[0]);
            for (int i = 1; i < typeVariables.length; i++)
                result.append(',').append(typeVariables[i]);
            result.append('>');
        }
        return result.toString();
    }
}