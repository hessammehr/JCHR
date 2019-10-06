package compiler.CHRIntermediateForm.members;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.Arguments;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.TypeFactory;

/**
 * @author Peter Van Weert
 */
public class Method extends AbstractMethod<Method> {

    public Method(java.lang.reflect.Method method) {
       super(method);
    }
    public Method(GenericType base, java.lang.reflect.Method method) {        
        super(base, method);
    }
    public Method(GenericType base, java.lang.reflect.Method method, boolean imported) {
        super(base, method, imported);
    }
    
    public MethodInvocation<Method> createStaticInstance(IArgument... arguments) {
        return createStaticInstance(new Arguments(arguments));
    }
    
    public MethodInvocation<Method> createStaticInstance(IArguments arguments) {
        arguments = setImplicitArgumentOf(arguments);
        return new MethodInvocation<Method>(this, arguments);
    }
    
    public MethodInvocation<Method> createInstance(IArguments arguments) {
    	if (!hasDefaultImplicitArgument()) arguments.markFirstAsImplicitArgument();
        return new MethodInvocation<Method>(this, arguments);
    }
    
    /**
     * Returns a set containing the <code>Method</code>-objects 
     * reflecting the member methods (static or not) with the 
     * given identifier of the given class. 
     * 
     * @param base
     *  The class of which we want to have a set of 
     *  <code>Method</code>-objects, reflecting its member methods 
     *  with the given identifier.
     * @param id
     *  The identifier the member methods have to have.
     *  
     * @throws A set containing the <code>Method</code>-objects 
     *  reflecting the member methods with the given identifier 
     *  of the given class.
     *  
     * @throws NullPointerException
     *  If either one of the given arguments is a null-pointer.
     */
    public static Set<Method> getMethods(GenericType base, String id) {
        Set<Method> result = new HashSet<Method>(2);
        
        for (java.lang.reflect.Method method : base.getRawType().getMethods())
            if (method.getName().equals(id))
                result.add(new Method(base, method));
        
        return result;
    }
    
    /**
     * Returns a set containing the <code>Method</code>-objects 
     * reflecting the static member methods with the given identifier 
     * of the given class. 
     * 
     * @param base
     *  The class of which we want to have a set of 
     *  <code>Method</code>-objects, reflecting its static
     *  member methods with the given identifier.
     * @param id
     *  The identifier the static member methods have to have.
     *  
     * @throws A set containing the <code>Method</code>-objects 
     *  reflecting the static member methods with the given identifier 
     *  of the given class.
     *  
     * @throws NullPointerException
     *  If either one of the given arguments is a null-pointer.
     */
    public static Set<Method> getStaticMethods(Class<?> base, String id) {
        Set<Method> result = new HashSet<Method>(2);
        addStaticMethods(result, base, id, false);
        return result;
    }
    
    /**
     * Tests whether the given class has a static method with the given name.
     * 
     * @param base
     *  The class.
     * @param id
     *  The name a static method of the given base class should have.
     *  
     * @return True iff the given class has a static method with the given name. 
     */
    public static boolean hasStaticMethod(Class<?> base, String id) {
        for (java.lang.reflect.Method method : base.getMethods())
            if (testStaticMethod(method, id))
                return true;
        return false;
    }
    
    /**
     * Tests whether the given class has a method with the given name.
     * 
     * @param base
     *  The class.
     * @param id
     *  The name a method of the given base class should have.
     *  
     * @return True iff the given class has a method with the given name. 
     */
    public static boolean hasMethod(Class<?> base, String id) {
        for (java.lang.reflect.Method method : base.getMethods())
            if (method.getName().equals(id))
                return true;
        return false;
    }
    
    /**
     * Adds <code>Method</code>-objects reflecting the static 
     * member methods with the given identifier of the given class to
     * a given set of methods. 
     * 
     * @param result
     *  The set all <code>Method</code>s have to be added to.
     * @param base
     *  The class of which we want to have a set of 
     *  <code>Method</code>-objects, reflecting its static
     *  member methods with the given identifier.
     * @param id
     *  The identifier the static member methods have to have.
     * @param imported
     *  A boolean value indicating whether the resulting methods
     *  are have their statically imported flag turned on or not. 
     *  
     * @throws NullPointerException
     *  If either one of the given arguments is a null-pointer.
     */
    public static void addStaticMethods(Set<Method> result, Class<?> base, String id, boolean imported) {
        GenericType actualBase = TypeFactory.getClassInstance(base);
        for (java.lang.reflect.Method method : base.getMethods())
            if (testStaticMethod(method, id))
                result.add(new Method(actualBase, method, imported));
    }
    
    /**
     * Tests whether the given method is a static method with the given
     * name.
     * 
     * @param method
     *  The method to test.
     * @param id
     *  The name the method should have.
     *
     * @return True iff the given method is a static method with the given
     *  name.
     */
    public static boolean testStaticMethod(java.lang.reflect.Method method, String id) {
        return Modifier.isStatic(method.getModifiers()) && id.equals(method.getName());
    }
}
