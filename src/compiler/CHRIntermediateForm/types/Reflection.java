package compiler.CHRIntermediateForm.types;

import static compiler.CHRIntermediateForm.modifiers.Modifier.DEFAULT;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.exceptions.IllegalArgumentException;

import compiler.CHRIntermediateForm.exceptions.ToDoException;
import compiler.CHRIntermediateForm.modifiers.Modifier;

/**
 * @author Peter Van Weert
 */
public abstract class Reflection {
    private Reflection() {/* not instantiatable */ }
    
    private final static Comparator<Method> METHOD_COMPARATOR
        = new Comparator<Method>() {
            public int compare(Method other, Method key) {
                if (other == null) return +1;
                int comp = other.getName().compareTo(key.getName());
                if (comp == 0) return +1;
                return comp;
            }
        };
        
    public static Set<Method> getSuperMethods(Method method) {
    	Set<Method> result = new HashSet<Method>(4);
    	addSuperMethods(method.getDeclaringClass(), false, method, result);
    	return result;
    }
    private static void addSuperMethods(Class<?> clazz, boolean rec, Method method, Set<Method> result) {
    	if (clazz == null) return;
    	if (rec) for (Method m : clazz.getDeclaredMethods())
    		if (overrides(method, m)) result.add(m);
    	addSuperMethods(clazz.getSuperclass(), true, method, result);
    	for (Class<?> interfoce : clazz.getInterfaces())
    		addSuperMethods(interfoce, true, method, result);
    }
    
    public static Method[] getAccessibleMethods(Class<?> clazz, String pockage) {
        Method[] result = new Method[16];
        int num = 0, len = result.length;
        
        do {
            methods: for (Method method : clazz.getDeclaredMethods()) {
                if (! isAccessible(method, pockage)) continue;                    
                
                String name = method.getName();
                int index = Arrays.binarySearch(result, method, METHOD_COMPARATOR);
                
                if (num == len) {
                    Method[] oldResult = result;
                    result = new Method[3*len/2];
                    System.arraycopy(oldResult, 0, result, 0, len);
                }
                
                int i = -index-1;
                while (true) {
                    if (result[i] == null) {
                        result[i] = method;
                        num++;
                        continue methods;
                    } else if (result[i].getName().equals(name)) {
                        if (overrides(result[i], method)) 
                            continue methods;
                        i++;
                    } else {
                        System.arraycopy(result, i, result, i+1, num-i);
                        result[i] = method;
                        num++;
                        continue methods;
                    }
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class);
        
        Method[] oldResult = result;
        result = new Method[num];
        System.arraycopy(oldResult, 0, result, 0, num);
        return result;
    }
    
    private static boolean isAccessible(Method method, String pockage) {
        switch (Modifier.getAccessModifier(method.getModifiers())) {
            case PUBLIC: return true;
            case PRIVATE: return false;
            case PROTECTED: // we are interested in accessing from generated code: subclass is out of the question
            case DEFAULT:
                return method.getDeclaringClass().getPackage().
                    getName().equals(pockage);
            default:
                throw new InternalError();
        }
    }
    
    public static boolean overrides(Method one, Method other) {
        return one.getName().equals(other.getName())
        	&& Arrays.equals(one.getParameterTypes(),
                           other.getParameterTypes());
    }
    
    /**
     * <p>
     * First some examples will help to understand the need for this method:
     * </p>
     * <ul>
     *  <li>
     *  Say the signature of some method is <code>tellEqual(Logical&lt;T&gt; X, T val)</code>, and we know
     *  types of both arguments, say <code>Logical&lt;Integer&gt;</code>
     *  and <code>Integer</code>. Both argument types are represented as 
     *  <code>{@link IType}</code>'s, because there is no way to create 
     *  <code>{@link Type}</code> representations.  
     *  How do we know these types are valid?
     *  </li>
     *  <li>
     *  Consider the following method: <code>{@link java.util.List#get(int)}</code> 
     *  with signature <code>E get(int index)</code>. If we know something is of
     *  type <code>ArrayList&lt;String&gt;</code> (represented as an 
     *  <code>{@link IType}</code>), how to get a representation of the
     *  type that results from a method call?
     *  </li>
     * </ul>
     * <p>
     * That's where this method is for!
     * </p>
     * <p>
     * Notes: does not work with type variables declared by methods or
     * constructors, does not work with array types, ...
     * </p>
     * 
     * @param mirror
     *  The mirror we are reflecting of (isn't that nice).
     * @param typeVariable
     *  The type variable we want to know the value of.
     *  
     * @return A representation of the actual value of the given 
     *  type variable, given the knowledge contained in the given mirror.
     * 
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     * @throws ClassCastException
     *  A type variable declared by a method or a constructor is encountered.
     */   
    public static IType reflect(GenericType mirror, Type type) throws IllegalArgumentException
    {
        // base cases:
        if (type instanceof TypeVariable<?>)
            return reflect(mirror, (TypeVariable<?>)type);
        if (type instanceof Class<?>)
            return TypeFactory.getInstance((Class<?>)type);
        
        // recursive case:
        if (type instanceof ParameterizedType) {
            final CacheKey key = new CacheKey(type, mirror);
            GenericType result = (GenericType)cache.get(key);
            if (result != null) return result;
            
            GenericType temp = GenericType.getParameterizableInstance((ParameterizedType)type); 
            cache.put(key, temp);                

        	for (Type t : ((ParameterizedType)type).getActualTypeArguments())                
        		temp.addTypeParameter(reflect(mirror, t));

            if ((result = GenericType.getUniqueInstance(temp)) != temp)
            	cache.put(key, result);
            	
        	return result;
        }
        
        throw new ToDoException("Unsupported reflection...");
    }
    
    public static IType reflect(GenericType mirror, TypeVariable<?> var) 
    throws IllegalArgumentException {
        final CacheKey key = new CacheKey(var, mirror);
        IType result = cache.get(key);
        if (result != null) return result;
        
        try {
            Class<?> declaror = (Class<?>)var.getGenericDeclaration();
        
            if (! declaror.isAssignableFrom(mirror.getRawType()))
                throw new IllegalArgumentException(var);
        
            result = declaror.isInterface()
                ? reflectDeclaredInInterface(mirror, var, declaror)
                : reflectDeclaredInClass(mirror, var);
            
        } catch (ClassCastException cce) {
            throw new ToDoException("Type variable declared by method/constructor");
        }
        
        cache.put(key, result);
        return result;
    }
    
    private static IType reflectDeclaredInClass(GenericType mirror, TypeVariable<?> var/*, Class<?> declaringClass*/) {
        return (mirror.hasAsTypeVariable(var))
            ? mirror.getValueOf(var)                // base case
            : reflectDeclaredInClass(mirror.getSupertype(), var); // recursive case
    }
    
    private static IType reflectDeclaredInInterface(GenericType mirror, TypeVariable<?> var, Class<?> declaringInterface) {
        // base case:
        if (mirror.hasAsTypeVariable(var)) return mirror.getValueOf(var);
        
        // recursive cases:
        // check the directly implemented interfaces:
        for (GenericType interface_ : mirror.getInterfaces())
            if (declaringInterface.isAssignableFrom(interface_.getRawType()))
                return reflectDeclaredInInterface(interface_, var, declaringInterface);
        
        // it is also possible the interface is implemented by one of the superclasses:
        return reflectDeclaredInInterface(mirror.getSupertype(), var, declaringInterface);
    }
    
    /**
     * A cache that is not only used to improve (time) performance
     * (if it improves this at all), but also and more importingly so
     * to make the reflection algorithm correct. If this cache were
     * not here, recursive types (like e.g. 
     * <code>Enum&lt;E extends Enum&lt;E&gt;&gt;</code>) would go in
     * an infinite loop.
     */
    private final transient static Map<CacheKey, IType> cache;
    static { cache = new HashMap<CacheKey, IType>(); }
    
    protected static class CacheKey {
        private Type type;
        private GenericType base;
        
        public CacheKey(Type type, GenericType base) {
            this.type = type;
            this.base = base;
        }
        
        @Override
        public int hashCode() {
            // based on http://www.javapractices.com/Topic28.cjp

            // XXX Volgende werkt mogelijk niet altijd vanwege bug in java 1.5 -code??
            return 37 * (37 * 23 + type.hashCode()) + base.hashCode();
        }
        
        @Override
        public boolean equals(Object other) {
            return this.type.equals(((CacheKey)other).type)
            	&& this.base.equals(((CacheKey)other).base);
        }
        
        @Override
        public String toString() {
            return type + " from " + base;
        }
    }
}