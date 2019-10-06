package annotations;

import static annotations.Default.DEFAULT_STRING;
import static annotations.JCHR_Constraint.Value.DEFAULT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import compiler.CHRIntermediateForm.constraints.bi.BuiltInConstraint;


/**
 * An annotation originally intended to be listed in a 
 * <code>JCHR_Constraints</code> annotation of a JCHR built-in constraint solver 
 * class or interface.
 * Since version 1.5.2, the annotation no longer has to be wrapped
 * in an enclosing <code>JCHR_Constraints</code> list for built-in constraint
 * solvers only declaring a single constraint, but can be added to the type 
 * directly.
 * Each constraint indicates its (unique) identifier, its arity,
 * and an optional infix identifier.
 * It is also possible to specify multiple identifiers, and to specify
 * separate (i.e. possibly different) identifiers for ask and tell variants
 * of the constraint.
 * 
 * @author Peter Van Weert
 * 
 * @see annotations.JCHR_Constraints
 * @see annotations.JCHR_Asks
 * @see annotations.JCHR_Tells
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface JCHR_Constraint {
    /**
     * The (unique!) identifier of this constraint. This has to be a
     * String containing a valid identifier for a constraint. Please
     * refer to the manual for constrains about constraint identifiers.
     * This identifier can than be used in <code>JCHR_Asks</code> and
     * <code>JCHR_Tells</code> annotations.
     *  
     * @return The (unique!) identifier of this constraint.
     */
    String identifier();
    
    /**
     * The arity of this constraint (this is its number of 
     * arguments).
     *  
     * @return The arity of this constraint.
     */
    int arity();
    
    /**
     * The infix-identifier(s) of this constraint. These has to be
     * Strings each containing a valid identifier for a constraint. 
     * Please refer to the manual for constraints on constraint 
     * identifiers.
     * This is an optional field. It cannot be used if either 
     * {@link #tell_infix()} or {@link #ask_infix()} is used.
     *  
     * @return The infix identifier(s) of this constraint.
     */
    String[] infix() default DEFAULT_STRING;
    
    /**
     * The infix-identifier(s) for the tell variant of the constraint. 
     * These has to be a Strings each containing a valid identifier 
     * for a constraint. 
     * Please refer to the manual for constraints on constraint 
     * identifiers.
     * This is an optional field. It cannot be used together
     * with {@link #infix()}, and must be used together with
     * {@link #ask_infix()}: in other words: either specify 
     * {@link #infix()}, or <em>both</em> this and the 
     * {@link #ask_infix()} field. You can always specify an 
     * empty list if you want to.
     *  
     * @return The infix identifier(s) for the tell variant of 
     *  this constraint.
     *  
     * @see #ask_infix()
     * @see #infix()
     */    
    String[] tell_infix() default DEFAULT_STRING;
    
    /**
     * The infix-identifier(s) for the ask variant of the constraint. 
     * These has to be a Strings each containing a valid identifier 
     * for a constraint. 
     * Please refer to the manual for constraints on constraint 
     * identifiers.
     * This is an optional field. It cannot be used together
     * with {@link #infix()}, and must be used together with
     * {@link #tell_infix()}: in other words: either specify 
     * {@link #infix()}, or <em>both</em> this and the 
     * {@link #tell_infix()} field. You can always specify an 
     * empty list if you want to.
     *  
     * @return The infix identifier(s) for the ask variant of 
     *  this constraint.
     *  
     * @see #tell_infix()
     * @see #infix()
     */ 
    String[] ask_infix() default DEFAULT_STRING;
    

    public static enum Value {
        YES, NO, DEFAULT
    }
    
    /**
     * Does telling this constraint trigger JCHR constraints or not?
     * By default, it is assumed that telling a JCHR constraint might trigger
     * JCHR constraints.
     */
    Value triggers() default DEFAULT;
    
    /**
     * Is this constraint idempotent when told or not. 
     * <br/>
     * This field is applicable for all constraints.
     * An idempotent constraint is a constraint that,
     * if told more than once with equal arguments, 
     * only the first time may have effect.
     * <br/>
     * By default a JCHR constraint is considered not idempotent.
     */
    Value idempotent() default DEFAULT;
    
    /**
     * Is this constraint reflexive or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a reflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> <i>X</i> = <i>Y</i> <b>then</b> R(<i>X</i>, <i>Y</i>)</code>  
     * </br>
     * The default value is {@link Value#DEFAULT}.
     * The actual value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getReflexiveDefault(String, String)}
     * 
     * @see BuiltInConstraint#getReflexiveDefault(String, String)
     */
    Value reflexive() default DEFAULT;
    
    /**
     * Is this constraint irreflexive or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a irreflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> <i>X</i> = <i>Y</i> <b>then</b> <em><b>NOT</b></em> R(<i>X</i>, <i>Y</i>)</code>  
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getIrreflexiveDefault(String, String)}
     * 
     * @see BuiltInConstraint#getIrreflexiveDefault(String, String)
     */
    Value irreflexive() default DEFAULT;
    
    /**
     * Is this constraint coreflexive or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a irreflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) <b>then</b> <i>X</i> = <i>Y</i></code>  
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getCoreflexiveDefault(String, String)}
     * 
     * @see BuiltInConstraint#getCoreflexiveDefault(String, String)
     */
    Value coreflexive() default DEFAULT;
    
    /**
     * Is this constraint transitive or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a transitive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>, <i>Z</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) <b>and</b> R(<i>Y</i>, <i>Z</i>), 
     * <b>then</b> R(<i>Y</i>, <i>Z</i>)</code>  
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getTransitiveDefault(String, String)}
     * 
     * @see BuiltInConstraint#getTransitiveDefault(String, String)
     */
    Value transitive() default DEFAULT;
    
    /**
     * Is this constraint symmetric or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a symmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>),
     * <b>then</b> R(<i>Y</i>, <i>X</i>)</code>  
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getSymmetricDefault(String, String)}
     * 
     * @see BuiltInConstraint#getSymmetricDefault(String, String)
     */
    Value symmetric() default DEFAULT;
    
    /**
     * Is this constraint anitsymmetric or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a anitsymmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) and R(<i>Y</i>, <i>X</i>),
     * <b>then</b> <i>X</i> = <i>Y</i></code>  
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getAntisymmetricDefault(String, String)}
     * 
     * @see BuiltInConstraint#getAntisymmetricDefault(String, String)
     */
    Value antisymmetric() default DEFAULT;
    
    /**
     * Is this constraint asymmetric or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a asymmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>), <b>then</b> <em><b>NOT</b></em> R(<i>Y</i>, <i>X</i>)
     * </code>
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getAsymmetricDefault(String, String)}
     * 
     * @see BuiltInConstraint#getAsymmetricDefault(String, String)
     */
    Value asymmetric() default DEFAULT;
    
    /**
     * Is this constraint total or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a total binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * either R(<i>X</i>, <i>Y</i>) or R(<i>Y</i>, <i>X</i>) holds
     * (or both)
     * </code>
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getTotalDefault(String, String)}
     * 
     * @see BuiltInConstraint#getTotalDefault(String, String)
     */
    Value total() default DEFAULT;
    
    /**
     * Is this constraint trichotomous or not. 
     * <br/>
     * This field is only applicable for binary constraints.
     * For a trichotomous binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <em>exactly one</em> of R(<i>X</i>, <i>Y</i>), R(<i>Y</i>, <i>X</i>) or <i>X</i> = <i>Y</i>
     * is true
     * </code>
     * </br>
     * The default Value is {@link Value#DEFAULT}.
     * The actual Value this corresponds with depends on the identifiers
     * of the constraint and is determined by 
     * {@link BuiltInConstraint#getTrichotomousDefault(String, String)}
     * 
     * @see BuiltInConstraint#getTrichotomousDefault(String, String)
     */
    Value trichotomous() default DEFAULT;
}