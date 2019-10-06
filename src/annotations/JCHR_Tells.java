package annotations;

import static annotations.Default.DEFAULT_STRING;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation indicating the annotated method tells the constraint
 * with given identifier. This identifier has to be the same
 * as the identifier of one of the <code>JCHR_Constraint</code>s
 * in the <code>JCHR_Constraints</code> annotation of the methods
 * class or interface. Furthermore the methods arity has to 
 * be the same as the one declared in the same <code>JCHR_Constraint</code>.
 * <br/>
 * For example:
 * <pre>
 *  &#64;JCHR_Tells(&quot;eq&quot;)
 *  public void tellEqual(Logical&lt;T&gt; X, Logical&lt;T&gt; Y);
 * </pre>
 * or, written in full:
 * <pre>
 *  &#64;JCHR_Tells(constraint = &quot;leq&quot;)
 *  public void tellLeq(Logical&lt;T&gt; X, Logical&lt;T&gt; Y);
 * </pre>
 * You can also override the infix identifiers that can be used
 * to tell this constraint with the annotated method:
 * <pre>
 *  &#64;JCHR_Tells(constraint = &quot;leq&quot;, infix = &quot;=<&quot;)
 *  public void tellLeq(Logical&lt;T&gt; X, Logical&lt;T&gt; Y);
 * </pre>
 * or, for multiple identifiers:
 * <pre>
 *  &#64;JCHR_Tells(constraint = &quot;leq&quot;, infix = {&quot;=&lt;&quot;, &quot;&lt;=&quot;})
 *  public void tellLeq(Logical&lt;T&gt; X, Logical&lt;T&gt; Y);
 * </pre>
 * or also possible:
 * <pre>
 *  &#64;JCHR_Tells(constraint = &quot;leq&quot;, infix = {})
 *  public void tellLeq(Logical&lt;T&gt; X, Logical&lt;T&gt; Y);
 * </pre>
 * 
 * @author Peter Van Weert
 * @see annotations.JCHR_Asks
 * @see annotations.JCHR_Constraint
 * @see annotations.JCHR_Constraints
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JCHR_Tells {
    /**
     * The identifier of the constraint the annotated method tells. 
     * This field is used as a shorthand notation of the
     * <code>constraint</code> field. Do not specify both.
     * 
     * @return The identifier of the constraint the annotated method tells.
     * 
     * @see #constraint()
     */
    String value() default DEFAULT_STRING;
    
    /**
     * The identifier of the constraint the annotated method tells. 
     * Can be written using the shorter syntax (cf. the {@link #value()}
     * field).
     * 
     * @return The identifier of the constraint the annotated method tells.
     * 
     * @see #infix()
     */
    String constraint() default DEFAULT_STRING;
    
    /**
     * The infix identifiers that can be used to tell the constraint using
     * the annotated method, overriding the ones specified 
     * in the corresponding {@link JCHR_Constraint} annotation.
     * 
     * @return The infix identifiers that can be used to tell the constraint 
     *  using the annotated method.
     */
    String[] infix() default DEFAULT_STRING;
    
    /**
     * Is <code>true</code> if this tell constraint warrants stack optimization
     * (expert use only: default value commonly suffices).
     * 
     * @return <code>true</code> if this tell constraint warrants stack optimization;
     * 	<code>false</code> otherwise (<code>false</code> is also default).
     */    
    boolean warrantsStackOpimization() default false;
}