package compiler.CHRIntermediateForm.debug;

import static compiler.CHRIntermediateForm.debug.DebugLevel.DEFAULT;
import static compiler.CHRIntermediateForm.debug.DebugLevel.FULL;
import static compiler.CHRIntermediateForm.debug.DebugLevel.OFF;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import util.collections.Empty;

import compiler.CHRIntermediateForm.rulez.Rule;

public class DebugInfo {
    
    /**
     * The debug level.
     */
    private DebugLevel debugLevel;
    
    /**
     * The set of rules that have to raise events if they fire.
     * Cannot be <code>null</code>.
     */
    private Set<Rule> rules;
    
    /**
     * Creates a new <code>DebugInfo</code> with the default
     * debug level.
     *
     * @see DebugLevel
     */
    public DebugInfo() {
        this(DEFAULT);
    }
    
    /**
     * Creates a new <code>DebugInfo</code> with a given debug
     * level.
     * 
     * @param level
     *  The debug level to use.
     */
    public DebugInfo(DebugLevel level) {
        setDebugLevel(level);
    }

    /**
     * <p>
     *  Checks whether or not the given rule should give raise to trace
     *  events if it fires. This depends on the debug level and possibly 
     *  on whether or not the rule has been specified to raise these events 
     *  or not.
     * </p>
     * <p>
     *  The current logic is as follows: if the debug-level is <code>FULL</code>, 
     *  the result is always <code>true</code>, if the debug-level is 
     *  <code>OFF</code>, it will always be <code>false</code>. 
     *  For the <code>DEFAULT</code> debug-level, only rules annotated with 
     *  <code>debug</code> result in rule-firing-events.  
     * </p>
     * 
     * @param rule
     *  The rule to check.
     * @return <code>true</code> iff the given rule should give raise to trace
     *  events if it fires, <code>false</code> otherwise.
     */
    public boolean hasToDebug(Rule rule) {
        return (getDebugLevel() == FULL)
            || ((getDebugLevel() != OFF) && hasSpecifiedRules() && getRulesRef().contains(rule));
    }
    
    public boolean hasToDebug() {
        return (getDebugLevel() == FULL) 
            || ((getDebugLevel() != OFF) && hasSpecifiedRules());
    }
    
    public DebugLevel getDebugLevel() {
        return debugLevel;
    }
    public void setDebugLevel(DebugLevel debugLevel) {
        this.debugLevel = debugLevel;
    }
    
    protected Set<Rule> getRulesRef() {
        return rules;
    }
    
    protected void initRules() {
        setRules(new HashSet<Rule>(4));
    }
    
    public boolean hasSpecifiedRules() {
        return getRulesRef() != null;
    }

    /**
     * Gets the set of rules that are specified to give rise
     * to events if they fire. Cannot be null. 
     * 
     * @return The set of that are specified to give rise
     *  to events if they fire.
     */
    public Set<Rule> getSpecifiedRules() {
        if (! hasSpecifiedRules())
            return Empty.getInstance();
        else
            return Collections.unmodifiableSet(getRulesRef());
    }
    
    protected void setRules(Set<Rule> rules) {
        this.rules = rules;
    }
    
    /**
     * Specifies that the firings of the given rule should result
     * in trace events.
     * 
     * @param rule
     *  A rule.
     */
    public void specifyRule(Rule rule) {
        if (! hasSpecifiedRules()) initRules();
        getRulesRef().add(rule);
    }
}