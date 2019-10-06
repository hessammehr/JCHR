package annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import compiler.codeGeneration.InlineDefinitionStore;

/**
 * If a method is annotated with {@link JCHR_Inline},
 * it means the method can safely be inlined.
 * Inline definitions can also be added directly in the {@link InlineDefinitionStore}.
 * 
 * @see InlineDefinitionStore
 * @see java.util.Formatter
 * @author Peter Van Weert
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface JCHR_Inline {
	/**
	 * An array of format strings in the following simple format:
	 * 
	 */
	String value();
	
	boolean onlyIfVariableArguments() default true;
}
