package compiler.CHRIntermediateForm.constraints;

import annotations.JCHR_Constraint;

import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraint;
import compiler.CHRIntermediateForm.modifiers.IModified;

/**
 * @author Peter Van Weert
 */
public interface IConstraint<T extends IConstraint<?>> 
    extends IArgumentable<T>, IModified {
    
	/**
     * Returns the identifier of this constraint.
     * 
     * @return the identifier of this constraint. 
     */
    public String getIdentifier();
    
    /**
     * Returns the infix identifier(s) of this constraint,
     * <code>null</code> if none.
     * 
     * @return the infix identifier(s) of this constraint,
     * <code>null</code> if none. 
     */
    public String[] getInfixIdentifiers();

    public boolean isAskConstraint();
    
    public boolean triggersConstraints();
    
    /**
     * Is this constraint an equality constraint or not.
     * <br/>
     * This property is only applicable for binary constraints.
     * <br/>
     * Care should be taken with coreflexive constraints that are not
     * equality, like for example <code>==</code> for most objects
     * (not for enums though for example...).
     * <br/>
     * Every equality constraint is also reflexive, coreflecive, 
     * transitive and symmetric.
     * 
     * @return Whether this constraint is an equality constraint or not.
     * 
     * @see #isCoreflexive()
     */
    public boolean isEquality();
    
    /**
     * Is this constraint known to be idempotent or not. 
     * <br/>
     * An idempotent tell constraint is a constraint that,
     * if told more than once with equal arguments, 
     * only the first time may have an effect.
     * All ask constraints have to be idempotent.
     */ 
    public boolean isIdempotent();
    
    /**
     * Is this constraint known to be reflexive or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a reflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> <i>X</i> = <i>Y</i> <b>then</b> R(<i>X</i>, <i>Y</i>)</code>  
     *
     * @see JCHR_Constraint#reflexive()
     * @see SolverBuiltInConstraint#getReflexiveDefault(String, String)
     */
    public boolean isReflexive();
    
    /**
     * Is this constraint known to be irreflexive or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a irreflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> <i>X</i> = <i>Y</i> <b>then</b> <em><b>NOT</b></em> R(<i>X</i>, <i>Y</i>)</code>  
     *
     * @see JCHR_Constraint#irreflexive()
     * @see SolverBuiltInConstraint#getIrreflexiveDefault(String, String)
     */
    public boolean isIrreflexive();
    
    /**
     * Is this constraint known to be coreflexive or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a coreflexive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) <b>then</b> <i>X</i> = <i>Y</i></code>  
     *
     * @see JCHR_Constraint#coreflexive()
     * @see SolverBuiltInConstraint#getCoreflexiveDefault(String, String)
     */
    public boolean isCoreflexive();
    
    /**
     * Is this constraint known to be transitive or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a transitive binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>, <i>Z</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) <b>and</b> R(<i>Y</i>, <i>Z</i>), 
     * <b>then</b> R(<i>Y</i>, <i>Z</i>)</code>  
     * </br>
     *
     * @see JCHR_Constraint#transitive()
     * @see SolverBuiltInConstraint#getTransitiveDefault(String, String)
     */
    public boolean isTransitive();
    
    /**
     * Is this constraint known to be symmetric or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a symmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>),
     * <b>then</b> R(<i>Y</i>, <i>X</i>)</code>  
     *
     * @see JCHR_Constraint#symmetric() 
     * @see SolverBuiltInConstraint#getSymmetricDefault(String, String)
     */
    public boolean isSymmetric();
    
    /**
     * Is this constraint known to be anitsymmetric or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a anitsymmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>) and R(<i>Y</i>, <i>X</i>),
     * <b>then</b> <i>X</i> = <i>Y</i></code>  
     *
     * @see JCHR_Constraint#antisymmetric()
     * @see SolverBuiltInConstraint#getAntisymmetricDefault(String, String)
     */
    public boolean isAntisymmetric();
    
    /**
     * Is this constraint known to be asymmetric or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a asymmetric binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <b>if</b> R(<i>X</i>, <i>Y</i>), <b>then</b> <em><b>NOT</b></em> R(<i>Y</i>, <i>X</i>)
     * </code>
     *
     * @see JCHR_Constraint#asymmetric() 
     * @see SolverBuiltInConstraint#getAsymmetricDefault(String, String)
     */
    public boolean isAsymmetric();
    
    /**
     * Is this constraint known to be total or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a total binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * either R(<i>X</i>, <i>Y</i>) or R(<i>Y</i>, <i>X</i>) holds
     * (or both)
     * </code>
     *
     * @see JCHR_Constraint#total() 
     * @see SolverBuiltInConstraint#getTotalDefault(String, String)
     */
    public boolean isTotal();
    
    /**
     * Is this constraint known to be trichotomous or not. 
     * <br/>
     * This property is only applicable for binary constraints.
     * For a trichotomous binary constraint <code>R</code> the following is true:
     * <code><b>for each</b> <i>X</i>, <i>Y</i>: 
     * <em>exactly one</em> of R(<i>X</i>, <i>Y</i>), R(<i>Y</i>, <i>X</i>) or <i>X</i> = <i>Y</i>
     * is true
     * </code>
     *
     * @see JCHR_Constraint#trichotomous() 
     * @see SolverBuiltInConstraint#getTrichotomousDefault(String, String)
     */
    public boolean isTrichotomous();
}