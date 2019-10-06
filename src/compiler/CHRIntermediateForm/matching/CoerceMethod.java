package compiler.CHRIntermediateForm.matching;

import static util.comparing.Comparison.EQUAL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.Annotations;
import util.collections.Empty;
import util.collections.Singleton;
import util.comparing.Comparison;
import annotations.JCHR_Coerce;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class CoerceMethod extends AbstractMethod<CoerceMethod> {
    private static List<CoerceMethod> numberCoerceMethods;
    
    public CoerceMethod(GenericType base, Method method) {
        super(base, method);
    }
        
    public static List<CoerceMethod> getCoerceMethods(GenericType base) {
        if (base.isLiteralType())
            return getWrapperCoerceMethods(base);
        else {
            List<CoerceMethod> result = new ArrayList<CoerceMethod>();        
            for (Method method : Annotations.getAnnotatedMethods(base.getRawType(), JCHR_Coerce.class))
                result.add(new CoerceMethod(base, method));
            return result;
        }
    }
    
    public static List<CoerceMethod> getWrapperCoerceMethods(GenericType base) {
        try {
            if (base.isPrimitiveWrapper())
                return new Singleton<CoerceMethod>(new CoerceMethod(
                    base, base.getRawType().getMethod(
                        PrimitiveType.getCorrespondingPrimitiveType(base)
                            .toTypeString() + "Value"
                    )
                ));
            if (base.hasAsRawType(String.class))
                return Empty.getInstance();
            if (base.isImmutableNonPrimitiveNumberWrapper())
                return getNumberWrapperCoerceMethods();
            
//            if (base.isImmutableNumberWrapper())
//                return getNumberWrapperCoerceMethods();
//            else if (base.hasAsRawType(Boolean.class))
//                return new Singleton<CoerceMethod>(new CoerceMethod(
//                    base, Boolean.class.getMethod("booleanValue")
//                ));
//            else if (base.hasAsRawType(Character.class))
//                return new Singleton<CoerceMethod>(new CoerceMethod(
//                    base, Character.class.getMethod("charValue")
//                ));
//            else if (base.hasAsRawType(String.class))
//                return Empty.getInstance();
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        throw new RuntimeException(base.toTypeString());
    }

    /**
     * Returns a list containing representations of all six wrapper methods of 
     * <code>Number</code> objects.
     *  
     * @return A list containing representations of all six wrapper methods of 
     * <code>Number</code> objects.
     * 
     * @see Number#byteValue()
     * @see Number#doubleValue()
     * @see Number#floatValue()
     * @see Number#intValue()
     * @see Number#longValue()
     * @see Number#shortValue()
     */
    public static List<CoerceMethod> getNumberWrapperCoerceMethods() {
        if (numberCoerceMethods == null) {
            final int NB_NUMBER_WRAPPER_COERCE_METHODS = 6;
            final Method[] methods = 
                Number.class.getDeclaredMethods();
            numberCoerceMethods = 
                new ArrayList<CoerceMethod>(NB_NUMBER_WRAPPER_COERCE_METHODS);
            GenericType Number = GenericType.getInstance(Number.class);
            for (Method method : methods)
                if (methods.length == NB_NUMBER_WRAPPER_COERCE_METHODS 
                        || isNumberWrapperCoerceMethod(method))
                    numberCoerceMethods.add(new CoerceMethod(Number, method));
        }
        return numberCoerceMethods;
    }
    /**
     * @param method
     *  An object reflecting a member method of the <code>Number</code> class. 
     * @return True iff the given arguments reflects one of the six
     *  coerce-methods of <code>Number</code>.
     *  
     * @see Number#byteValue()
     * @see Number#doubleValue()
     * @see Number#floatValue()
     * @see Number#intValue()
     * @see Number#longValue()
     * @see Number#shortValue()
     */
    protected static boolean isNumberWrapperCoerceMethod(Method method) {
        final String name = method.getName();
        return method.getDeclaringClass() == Number.class
        	&& name.endsWith("Value") 
            && PrimitiveType.isPrimitiveType(name.substring(0, name.length() - 5));
    }
    
    public Comparison compareTo(CoerceMethod other) {
        return this.getReturnType().compareWith(other.getReturnType());
    }
    
    public static Comparison compare(List<CoerceMethod> one, int previous, CoerceMethod other) {
        Comparison comparison = one.get(0).compareTo(other);
        if (comparison == EQUAL)
            return Comparison.get(one.size() - previous - 1);
        else
            return comparison;
    }
    
    public static Comparison compare(List<CoerceMethod> one, List<CoerceMethod> other) {
        Comparison comparison = one.get(0).compareTo(other.get(0));
        if (comparison == EQUAL)
            return Comparison.get(one.size() - other.size());
        else
            return comparison;
    }
    
    public static Comparison compare(List<CoerceMethod> one, CoerceMethod other) {
        return compare(one, 0, other);
    }
    
    public boolean isValidCoerceMethod() {
        return getArity() == (isStatic()? 2 : 1) /* vergeet implicit argument niet! */ 
            && ! getMethod().getReturnType().equals(Void.TYPE);
    }
    
    public MethodInvocation<CoerceMethod> getInstance(IArgument argument) {
        final IArguments arguments 
            = new Arguments(hasDefaultImplicitArgument()? 2 : 1);
        arguments.addArgument(argument);
        setImplicitArgumentOf(arguments);
        return createInstance(arguments);
    }
    
    public MethodInvocation<CoerceMethod> createInstance(IArguments arguments) {
        return new MethodInvocation<CoerceMethod>(this, arguments);
    }
    
    public static Field getFieldCoerced(IType type, String name) 
    throws NoSuchFieldException, AmbiguityException {
        
        try {
            return type.getField(name);
        } catch (NoSuchFieldException nsfe) {
            Field result = null, temp;
            for (CoerceMethod method : type.getCoerceMethods()) {
                try {
                    temp = method.getReturnType().getField(name);
                    if (result != null)
                        result = temp;
                    else
                        throw new AmbiguityException("Ambiguous field: " + name);
                    
                } catch (NoSuchFieldException nsfe2) {
                    // NOP
                }
            }
            
            throw nsfe;
        }
    }
    
    public static Set<compiler.CHRIntermediateForm.members.Method> getMethodsCoerced(IType type, String name) {
        Set<compiler.CHRIntermediateForm.members.Method> result 
            = new HashSet<compiler.CHRIntermediateForm.members.Method>(type.getMethods(name));
        for (CoerceMethod method : type.getCoerceMethods())
            result.addAll(method.getReturnType().getMethods(name));
        return result;
    }
    
    public static boolean isCoerceMethodInvocation(MethodInvocation<?> methodInvocation) {
    	final String name = methodInvocation.getMethodName();
    	return methodInvocation.getArity() == 1
    		&& (
				methodInvocation.getMethod().isAnnotationPresent(JCHR_Coerce.class)
	    		|| (
					GenericType.isImmutableNumberWrapper(
							((GenericType)methodInvocation.getImplicitArgument().getType()).getRawType())
						&& name.endsWith("Value") 
						&& PrimitiveType.isPrimitiveType(name.substring(0, name.length() - 5))
				)
			);
    }
    public static boolean isCoerceMethod(Method method) {
    	final String name = method.getName();
    	return method.getParameterTypes().length == 0
    		&& (
				method.isAnnotationPresent(JCHR_Coerce.class)
	    		|| (
					GenericType.isImmutableNumberWrapper(method.getDeclaringClass())
						&& name.endsWith("Value") 
						&& PrimitiveType.isPrimitiveType(name.substring(0, name.length() - 5))
				)
			);
    }
}
