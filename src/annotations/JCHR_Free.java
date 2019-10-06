package annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares that if a JCHR handler calls the annotated method or constructor,
 * its execution will not result in recursive calls back to the JCHR handler.
 * If a class is annotated, this means that all members are {@link JCHR_Free};
 * if a package is annotated all its classes are assumed {@link JCHR_Free}.
 * 
 * @author Peter Van Weert
 */
@Inherited
@Documented
@Target({PACKAGE, TYPE, CONSTRUCTOR, METHOD})
@Retention(RUNTIME)
public @interface JCHR_Free {
	// This just an empty annotation
}
