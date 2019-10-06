package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Van Weert
 */
public abstract class Annotations {

    /**
     * <p>
     * Returns a <code>Method</code> reflecting the first public member
     * method of the class or interface represented by the given 
     * <code>Class</code> object that is annotated with an annotation
     * of the given annotation class. Returns null if none is found.
     * Both public methods declared by the class or interface and those 
     * inherited from superclasses and superinterfaces are taken into account.
     * </p>
     * <p>
     * The &quot;first&quot; in the above paragraph has to be interpreted
     * non-deterministically: there is no order defined on the methods of
     * a given class! Therefor this method should propably only be used
     * when one is sure there is in fact only one method containing the
     * given marker annotation.
     * </p>
     * 
     * @param annotation
     *  A <code>Class</code> object reflecting an annotation class.
     * @param someClass
     *  The <code>Class</code> object of which the returned 
     *  <code>Method</code> will be reflection of a public member method.
     *  
     * @return  A <code>Method</code> reflecting the first public member
     *  method of the class or interface represented by the given 
     *  <code>Class</code> object that is annotated with an annotation
     *  of the given annotation class. Returns null if none is found.
     * 
     * @see java.lang.Class#getMethods()
     * @see #getAnnotatedMethods(Class, Class)
     */
    public static Method getAnnotatedMethod(
        Class<?> someClass,
        Class<? extends Annotation> annotation
        ) {
        
        for (Method m : someClass.getMethods())
            if (m.isAnnotationPresent(annotation))                
                return m;            
        
        return null;
    }
    
    /**
     * <p>
     * Returns a list containing all <code>Method</code>s reflecting 
     * public member methods of the class or interface represented by the given 
     * <code>Class</code> object that is annotated with an annotation
     * of the given annotation class.
     * Both public methods declared by the class or interface and those 
     * inherited from superclasses and superinterfaces are taken into account.
     * </p>
     * <p>
     * The order in which the objects are returned is non-deterministic.
     * </p>
     * 
     * @param annotation
     *  A <code>Class</code> object reflecting an annotation class.
     * @param someClass
     *  The <code>Class</code> object of which the returned 
     *  <code>Method</code>s will be reflections of a public member 
     *  methods.
     *  
     * @return A list containing all <code>Method</code>s reflecting 
     *  public member methods of the class or interface represented by the given 
     *  <code>Class</code> object that is annotated with an annotation
     *  of the given annotation class.
     * 
     * @see java.lang.Class#getMethods()
     * @see #getAnnotatedMethod(Class, Class)
     */
    public static List<Method> getAnnotatedMethods(
        Class<?> someClass,
        Class<? extends Annotation> annotation
    	) {
        
        final List<Method> result = new ArrayList<Method>();
        
        for (Method m : someClass.getMethods())
            if (m.isAnnotationPresent(annotation))
                result.add(m);             
        
        return result;
    }
    
    /**
     * Returns a list containing the reflections of the annotations of the given
     * annotation type present in the given class or one of its superclasses or
     * -interfaces. The list will be empty if none found.
     * 
     * @param <T>
     *  The annotation type.
     * @param someClass
     *  The class in which annotations are to be searched. Note that also its 
     *  superclasses and interfaces are searched.
     * @param annotation
     *  An object reflecting the annotation type.
     * @return A list containing the reflections of the annotations of the given
     *  annotation type present in the given class or one of its superclasses or
     *  -interfaces.
     */
    public static <T extends Annotation> List<T> getAnnotations(
        Class<?> someClass,
        Class<T> annotation
    ) {        
        List<T> result = new ArrayList<T>();
        recurseGetAnnotations(someClass, annotation, result);        
        return result;
    }
    
    protected static <T extends Annotation> void recurseGetAnnotations(
        Class<?> someClass,
        Class<T> annotation,
        List<T> result
    ) {
        // base cases:
        if (someClass == null || someClass.equals(Object.class)) return;
        
        // recursive case:
        T anno = someClass.getAnnotation(annotation);
        if (anno != null) result.add(anno);
        
        recurseGetAnnotations(someClass.getSuperclass(), annotation, result);
        for (Class<?> someInterface : someClass.getInterfaces())
            recurseGetAnnotations(someInterface, annotation, result);
    }
}