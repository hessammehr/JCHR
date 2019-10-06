package runtime.debug;

import runtime.Constraint;

/**
 * The interface you have to implement if you want to listen
 * for events during the execution of a JCHR program.
 * Currently, events can be thrown on the following execution points: 
 * <ul>
 *  <li>Right after a constraint is activated (for the first time).</li>
 *  <li>Right after a constraint is reactivated (by some built-in event).</li>
 *  <li>Right after a constraint is stored in the constraint store.</li>
 *  <li>Right before a constraint is suspended 
 *  	(will <em>always</em> be a constraint that has been stored)</li> 
 *  <li>Right after a constraint is removed from the constraint store.</li>
 *  <li>Right <em>before</em> a rule fires. Arguments passed here include
 *      also the constraints that matched the head and the index of the
 *      active constraint.</li>
 *  <li>Right <em>after</em> a rule fired, that is: right after 
 *  	the execution of its body.</li>
 * </ul>
 * 
 * @author Peter Van Weert
 */
public interface Tracer {

    /**
     * Called right after the given constraint is activated.
     * 
     * @param constraint
     *  The constraint that has just been activated.
     */
    public void activated(Constraint constraint);
    
    /**
     * Called right after the given constraint is reactivated.
     * 
     * @param constraint
     *  The constraint that has just been reactivated.
     */
    public void reactivated(Constraint constraint);
    
    /**
     * Called right after the given constraint is stored in the constraint store.
     * 
     * @param constraint
     *  The constraint that has just been stored.
     */
    public void stored(Constraint constraint);
    
    /**
     * Called right before the given constraint is suspended.
     * This constraint is always stored.
     * 
     * @param constraint
     *  The constraint that is about to be suspended.
     */
    public void suspended(Constraint constraint);
    
    /**
     * Called right after the given constraint is removed from the constraint store.
     * 
     * @param constraint
     *  The constraint that has just been removed.
     */
    public void removed(Constraint constraint);
    
    /**
     * Called right after the given constraint is terminated.
     * 
     * @param constraint
     *  The constraint that has just been terminated.
     */
    public void terminated(Constraint constraint);
    
    /**
     * Called right <em>before</em> the rule with the given identifier fires,
     * with the given constraints matching its head (<code>activeIndex</code>
     * is the index of the active constraint). Right <em>after</em> this call, the
     * runtime will delete the necessary constraints and execute the rule's
     * body.
     * 
     * @param ruleId
     *  The identifier of the rule that is about to fire.
     * @param activeIndex
     *  The index of the active constraint, i.e. the <code>activeIndex</code>'th
     *  constraint of <code>constraints</code> is the active constraint.
     * @param constraints
     *  The list of constraints that match the rule's head. The constraints are
     *  given in order: the first constraint matches the first occurrence in the
     *  rule's head (left-to-right), etc
     */
    public void fires(String ruleId, int activeIndex, Constraint... constraints);
    
    /**
     * Called right <em>after</em> the body of some rule is entirely executed.
     * 
     * @param ruleId
     *  The identifier of the rule that just fired.
     * @param activeIndex
     *  The index of the active constraint, i.e. the <code>activeIndex</code>'th
     *  constraint of <code>constraints</code> is the active constraint.
     * @param constraints
     *  The list of constraints that match the rule's head. The constraints are
     *  given in order: the first constraint matches the first occurrence in the
     *  rule's head (left-to-right), etc
     */
    public void fired(String ruleId, int activeIndex, Constraint... constraints);
}