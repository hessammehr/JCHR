package compiler.CHRIntermediateForm.constraints.bi;

import util.exceptions.IllegalArgumentException;
import annotations.JCHR_Constraint;

import compiler.CHRIntermediateForm.constraints.Constraint;
import compiler.CHRIntermediateForm.constraints.IConstraint;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

/**
 * @author Peter Van Weert
 */
public abstract class BuiltInConstraint<T extends IBuiltInConstraint<?>> 
    extends Constraint<T> 
    implements IBuiltInConstraint<T> {

    private boolean isAskConstraint;
    
    public BuiltInConstraint(String id, boolean isAskConstraint) throws IllegalIdentifierException {
        super(id);
        setAskConstraint(isAskConstraint);
    }
    
    public BuiltInConstraint(String id, String infix, boolean isAskConstraint) throws IllegalIdentifierException {
        super(id, infix);
        setAskConstraint(isAskConstraint);
    }
  
    public boolean isAskConstraint() {
        return isAskConstraint;
    }
    protected void setAskConstraint(boolean isAskConstraint) {
        this.isAskConstraint = isAskConstraint;
    }
    
    public boolean isEquality() {
        return getIdentifier().equals(EQ);
    }
    
    /**
     * @param infix
     *  One of the built-in infix identifiers
     */
    public static String getCorrespondingPrefix(String infix) {
        switch (infix.charAt(0)) {
            case '!': 
                return NEQ;
            case '<': 
                return (infix.length() == 1)? LT : LEQ;
            case '>': 
                return (infix.length() == 1)? GT : GEQ;
            case '=': 
                return (infix.length() == 2 && infix.charAt(1) == '<')? LEQ : EQ;
            default:
                throw new IllegalArgumentException(infix);
        }
    }
    
    /**
     * Returns <code>false</code> (conservative) 
     */
    public final static boolean getIdempotentDefault(IConstraint<?> constraint) {
        return false;
    }
    
    /**
     * @see #getReflexiveDefault(String, String[]) 
     */
    public final static boolean getReflexiveDefault(IConstraint<?> constraint) {
        return getReflexiveDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }

    /**
     * @see #getIrreflexiveDefault(String, String[]) 
     */
    public final static boolean getIrreflexiveDefault(IConstraint<?> constraint) {
        return getIrreflexiveDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getCoreflexiveDefault(String, String[]) 
     */
    public final static boolean getCoreflexiveDefault(IConstraint<?> constraint) {
        return getCoreflexiveDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getTransitiveDefault(String, String[]) 
     */
    public final static boolean getTransitiveDefault(IConstraint<?> constraint) {
        return getTransitiveDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getSymmetricDefault(String, String[]) 
     */
    public final static boolean getSymmetricDefault(IConstraint<?> constraint) {
        return getSymmetricDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getAntisymmetricDefault(String, String[]) 
     */
    public final static boolean getAntisymmetricDefault(IConstraint<?> constraint) {
        return getAntisymmetricDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getAsymmetricDefault(String, String[]) 
     */
    public final static boolean getAsymmetricDefault(IConstraint<?> constraint) {
        return getAsymmetricDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getTotalDefault(String, String[]) 
     */
    public final static boolean getTotalDefault(IConstraint<?> constraint) {
        return getTotalDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * @see #getTrichotomousDefault(String, String[]) 
     */
    public final static boolean getTrichotomousDefault(IConstraint<?> constraint) {
        return getTrichotomousDefault(constraint.getIdentifier(), constraint.getInfixIdentifiers());
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the 
     *      following values, the default value is <code>true</code>:
     *      <code>eq</code>, <code>leq</code>, <code>geq</code>
     *  </li>
     *  <li>
     *      if the infix identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>=</code>, <code>==</code>, <code>&lt;=</code>, 
     *      <code>=&lt;</code>, <code>&gt;=</code> 
     *  </li>
     *  <li>
     *      for all other constraints the default value is <code>false</code>
     *  </li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infix
     *  The infix identifier of the constraint
     * 
     * @see JCHR_Constraint#reflexive() 
     */
    public final static boolean getReflexiveDefault(String id, String[] infixes) {
        if (EQ.equals(id) || LEQ.equals(id) || GEQ.equals(id))
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (EQi.equals(infix) || EQi2.equals(infix) 
                || LEQi.equals(infix) || LEQi2.equals(infix) || GEQi.equals(infix)
            )   return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>neq</code>, <code>lt</code>, <code>gt</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>!=</code>, <code>!==</code>, <code>&lt;</code>, <code>&gt;</code> 
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#irreflexive() 
     */
    public final static boolean getIrreflexiveDefault(String id, String[] infixes) {
        if (NEQ.equals(id) || LT.equals(id) || GT.equals(id))
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (NEQi.equals(infix) || NEQi2.equals(infix) 
                || LTi.equals(infix) || GTi.equals(infix)
            )   return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>eq</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>=</code>, <code>==</code>, <code>===</code>
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#coreflexive() 
     */
    public final static boolean getCoreflexiveDefault(String id, String[] infixes) {
        if (EQ.equals(id))
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (EQi.equals(infix) || EQi2.equals(infix) || EQi3.equals(infix))
                return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>eq</code>, <code>lt</code>, <code>gt</code>, 
     *      <code>leq</code>, <code>geq</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>=</code>, <code>==</code>, <code>===</code>, 
     *      <code>&lt;</code>, <code>&gt;</code>,
     *      <code>&lt;=</code>, <code>=&lt;</code>, <code>&gt;=</code> 
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#transitive() 
     */
    public final static boolean getTransitiveDefault(String id, String[] infixes) {
        if (EQ.equals(id) || LT.equals(id) || GT.equals(id) 
            || LEQ.equals(id) || GEQ.equals(id)
        ) return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (EQi.equals(infix) || EQi2.equals(infix) || EQi3.equals(infix)  
                || LTi.equals(infix) || GTi.equals(infix)
                || LEQi.equals(infix) || LEQi2.equals(infix) || GEQi.equals(infix)
            )   return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>eq</code>, <code>neq</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>=</code>, <code>==</code>, <code>===</code>, 
     *      <code>!=</code>, <code>!==</code> 
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#symmetric() 
     */
    public final static boolean getSymmetricDefault(String id, String[] infixes) {
        if (EQ.equals(id) || NEQ.equals(id)
        ) return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (EQi.equals(infix) || EQi2.equals(infix) || EQi3.equals(infix) 
                    || NEQi.equals(infix) || NEQi2.equals(infix)
            )   return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>eq</code>, <code>leq</code>, <code>geq</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>=</code>, <code>==</code>, <code>===</code>, <code>&lt;=</code>, 
     *      <code>=&lt;</code>, <code>&gt;=</code> 
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#antisymmetric() 
     */
    public final static boolean getAntisymmetricDefault(String id, String[] infixes) {
        if (EQ.equals(id) || LEQ.equals(id) || GEQ.equals(id)
        ) return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (EQi.equals(infix) || EQi2.equals(infix) || EQi3.equals(infix)
                || LEQi.equals(infix) || LEQi2.equals(infix) || GEQi.equals(infix)
            )   return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>lt</code>, <code>gt</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>&lt;</code>, <code>&gt;</code>,
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#asymmetric()
     */
    public final static boolean getAsymmetricDefault(String id, String[] infixes) {
        if (LT.equals(id) || GT.equals(id)) 
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (LTi.equals(infix) || GTi.equals(infix))
                return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>leq</code>, <code>geq</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>&lt;=</code>, <code>=&lt;</code>, <code>&gt;=</code> 
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#total()
     */
    public final static boolean getTotalDefault(String id, String[] infixes) {
        if (LEQ.equals(id) || GEQ.equals(id)) 
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (LEQi.equals(infix) || LEQi2.equals(infix) || GEQi.equals(infix)) 
                return true;
        return false;
    }
    
    /**
     * <ol>
     *  <li>
     *      if the identifier of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>lt</code>, <code>gt</code>
     *  </li>
     *  <li>
     *      if the infix identifiers of the constraint is either of the following
     *      values, the default value is <code>true</code>:
     *      <code>&lt;</code>, <code>&gt;</code>,
     *  </li>
     *  <li>for all other constraints the default value is <code>false</code></li>
     * </ol>
     * 
     * @param id
     *  The identifier of the constraint
     * @param infixes
     *  The infix identifiers of the constraint
     * 
     * @see JCHR_Constraint#trichotomous()
     */
    public final static boolean getTrichotomousDefault(String id, String[] infixes) {
        if (LT.equals(id) || GT.equals(id)) 
            return true;
        if (infixes == null || infixes.length == 0)
            return false;
        for (String infix : infixes)
            if (LTi.equals(infix) || GTi.equals(infix))
                return true;
        return false;
    }
}