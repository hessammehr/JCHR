package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.ud.schedule.ISelector;

public interface IGuardConjunct
extends IConjunct, ISelector, IGuardConjunctVisitable {

    public boolean canBeAskConjunct();
    
    /**
     * Returns <code>true </code> if this conjunct is known to always fail
     * if used as a guard.
     *    
     * @return <code>true </code> if this conjunct is known to always fail
     * if it were used as a guard; <code>false</code> if it could succeed 
     * (or it is not known).
     */
    public boolean fails();
    
    public boolean isNegated();
    
    /**
     * Checks whether this guard conjunct is an equality. Note that
     * you have to take into account the fact that a <em>negated</em>
     * equality no longer is an equality!
     * 
     * @return <code>true</code> if this guard conjunct is an equality;
     *  <code>false</code> otherwhise.
     */
    public boolean isEquality();
}
