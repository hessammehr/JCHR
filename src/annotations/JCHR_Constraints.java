package annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;

/**
 * This annotation lists <code>JCHR_Constraint</code>s,
 * declaring the different constraints the annotated
 * built-in constraint solver (a class or interface) 
 * solves.
 * 
 * @author Peter Van Weert
 * @see annotations.JCHR_Constraint
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface JCHR_Constraints {
    /**
     * A list of <code>JCHR_Constraint</code>s,
     * declaring the different constraints the annotated
     * built-in constraint solver (a class or interface) 
     * solves.
     * 
     * @return A list of <code>JCHR_Constraint</code>s,
     *  declaring the different constraints the annotated
     *  built-in constraint solver (a class or interface) 
     *  solves.
     */
    JCHR_Constraint[] value();
}
