package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.SortedSet;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * Unlike ordinary join order elements, certain conditions have to be
 * fulfilled before a selective join order element is allowed to be
 * added to a join order. Once it is added to the join order it will 
 * make a selection of valid (partial) matches. The selectivity
 * of these elements is an estimate of how selective they 
 * are, i.e. how many (partial) matches they will reject.
 *   
 * @author Peter Van Weert
 */
public interface ISelector extends IJoinOrderElement, IScheduleElement {
    
    /**
     * Returns the set of all variables that has to be fixed for this
     * join order element to be added to a join order.
     * 
     * @return the set of all variables that has to be fixed for this
     *  join order element to be ordered.
     */ 
    public SortedSet<Variable> getJoinOrderPrecondition();
    
    /**
     * Returns a number between 0 and 1 indicating the <em>selectivity</em> 
     * if this conjunct were to be used in a guard, 
     * with 1 being a very selective guard (e.g. an equality), 
     * and 0 being a more or less superfluous guard (i.e. a guard that 
     * is almost always true, e.g. a guard that tests for exceptional cases).
     * Note that this number is, of course, only an heuristic value.
     * 
     * @return A heuristic value between 0 and 1 that gives an indication to
     *  the expected <em>selectivity</em> when used as a guard. 
     */
    public float getSelectivity();
    
    /**
     * Returns the expected cost for checking the selector.
     * 
     * @return The expected cost for checking the selector.
     */
    public Cost getSelectionCost();
    
    /**
     * Returns <code>true</code> if the selector <em>never</em> 
     * excludes any joins, because it is always &quot;true&quot;.
     * 
     * @return <code>true</code> if the selector <em>never</em> 
     *  excludes any joins; <code>false</code> otherwise.
     */
    public boolean succeeds();
    
    /**
     * Returns <code>true</code> if the selector excludes <em>all</em>
     * joins, because it is always &quot;false&quot;.
     * 
     * @return <code>true</code> if the selector excludes all joins; 
     *  <code>false</code> otherwise.
     */
    public boolean fails();
}