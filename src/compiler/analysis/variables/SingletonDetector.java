package compiler.analysis.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import compiler.CHRIntermediateForm.CHRIntermediateForm;
import compiler.CHRIntermediateForm.arg.visitor.NOPLeafArgumentVisitor;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.variables.IActualVariable;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.analysis.AnalysisException;
import compiler.analysis.CifAnalysor;
import compiler.options.Options;

/*
 * TODO: 
 *  - treat negative heads differently (can now result in warnings where
 *      the same identifier occurs more than once)
 */
/**
 * <p>
 * Looks for singleton variables in the cif. Each singleton variable is
 * replaced by the nameless anonymous variable (this removes all
 * unnecessary declarations of unused variables in the generated code,
 * plus avoids wasting time on them later in the compilation).
 * For each singleton variable a warning is raised to the user: this
 * could help preventing programming errors. 
 * </p>
 * <p>
 * This analysis also checks whether all local variables that were
 * declared are also actually used at some point. Again, if not, this
 * raises a warning, which could help detecting programming mistakes.
 * </p>
 * <p>
 * Note that this analysis has lost some of its value now we decided
 * to keep usage information directly in <code>Variable</code>s...
 * </p>
 * 
 * @author Peter Van Weert
 */
public class SingletonDetector extends CifAnalysor {
    
	private boolean singletonVariablesDetected;
	
    private List<IActualVariable> singletonVariables;

    public SingletonDetector(CHRIntermediateForm cif, Options options) {
        super(cif, options);
        setSingletonVariables(new ArrayList<IActualVariable>(4));
    }

    @Override
    public boolean doAnalysis() throws AnalysisException {
    	singletonVariablesDetected = false;
    	
        analyseRules();
        
        analyseLocalVariables();
        raiseWarning();
        clearSingletonVariables();
        
        if (singletonVariablesDetected) {
	        // does not seem to work always, 
	        // but there is nothing more we can do
	        System.err.flush();
	        return true;
        }
        
        return false;
    }

    /**
     * Analyses all rules, printing warnings while doing so. 
     */
    @Override
    public void analyseRules() {
        for (Rule rule : getRules()) {
            analyseRule(rule);
            raiseWarning(rule);
        }
        clearSingletonVariables();
    }
    
    /**
     * Analyses local variables, and prints a warning if unused
     * variables detected.
     * Afterwards these can be inspected using the 
     * {@link #getSingletonVariables()} method.
     * This method first clears all previously detected singleton variables. 
     */
    public void analyseLocalVariables() {
        clearSingletonVariables();

        @SuppressWarnings("unchecked")
        final HashSet<Variable> localVariables = (HashSet)getLocalVariables().clone();
        
        try {
            for (Rule rule : getRules()) {
                rule.accept(new NOPLeafArgumentVisitor() {
                    @Override
                    public void visit(Variable var) {
                        localVariables.remove(var);
                    }
                });
            }
        } catch (Exception x) {
            throw new InternalError();
        }
        
        addSingletonVariables(localVariables);
    }
    
    /**
     * Analyses a rule detecting its singleton variables. 
     * Afterwards these can be inspected using the 
     * {@link #getSingletonVariables()} method.
     * First, this method will clear all variables detected
     * in previous runs.
     * 
     * @param rule
     *  The rule to check.
     */
    public void analyseRule(Rule rule) {
        clearSingletonVariables();
        
        final HashMap<Variable, Integer> count = new HashMap<Variable, Integer>();
        try {
            rule.accept(new NOPLeafArgumentVisitor() {
                @Override
                public void visit(Variable variable) {
                    if (variable.isAnonymous() || variable.isImplicit()) 
                        return;
                    
                    Integer i = count.get(variable);
                    count.put(variable, (i == null)? 0 : i+1);
                }
            });
        } catch (Exception e) {
            throw new InternalError();
        }
        
        for (Entry<Variable, Integer> entry : count.entrySet())
            if (entry.getValue() == 0)
                addSingletonVariable(entry.getKey());
    }
    
    protected void setSingletonVariables(List<IActualVariable> singletonVariables) {
        this.singletonVariables = singletonVariables;
    }
    protected void addSingletonVariables(Collection<Variable> variables) {
    	if (!variables.isEmpty()) {
    		singletonVariablesDetected = true;
    		getSingletonVariablesRef().addAll(variables);
    	}
    }
    protected void addSingletonVariable(Variable variable) {
    	singletonVariablesDetected = true;
        getSingletonVariablesRef().add(variable);
    }
    protected List<IActualVariable> getSingletonVariablesRef() {
        return singletonVariables;        
    }
    public boolean hasDetectedSingletonVariables() {
        return !getSingletonVariablesRef().isEmpty();
    }
    
    /**
     * Returns the list of singleton variables detected by the
     * previous {@link #analyseRule(Rule)} or 
     * {@link #analyseLocalVariables()} analysis.  
     *  
     * @return the list of singleton variables detected by the
     *  previous analysis.
     *  
     * @see #analyseRule(Rule)
     * @see #analyseLocalVariables()
     */
    public List<IActualVariable> getSingletonVariables() {
        return Collections.unmodifiableList(getSingletonVariablesRef());
    }
    protected void clearSingletonVariables() {
        getSingletonVariablesRef().clear();
    }
    
    protected void raiseWarning() {
    	raiseWarning("declared", " never used");
    }
    
    protected void raiseWarning(Rule rule) {
    	raiseWarning("singleton", " in rule " + rule.getIdentifier());
    }
    
    private void raiseWarning(String pre, String post) {
        Iterator<IActualVariable> iter = singletonVariables.iterator();
        
        if (iter.hasNext()) {
        	StringBuilder message = new StringBuilder();
            IActualVariable first = iter.next();
            
            message.append(pre).append(" variable");
            if (iter.hasNext()) message.append('s');
            message.append(' ').append(first.getIdentifier());
            
            while (iter.hasNext()) 
                message.append(", ").append(iter.next().getIdentifier());
            
            message.append(post);
            raiseWarning(message.toString());
        }
    }
}