package compiler.CHRIntermediateForm;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.debug.DebugInfo;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.solver.Solver;
import compiler.CHRIntermediateForm.variables.Variable;
import compiler.CHRIntermediateForm.variables.VariableType;

/**
 * @author Peter Van Weert
 */
public class CHRIntermediateForm implements ICHRIntermediateForm {
    
    private Handler handler;

    private Set<VariableType> variableTypes;
    
    //  TODO the following should be members of the handler!!
    
    private HashSet<Variable> localVariables;
    
    private Collection<UserDefinedConstraint> udConstraints;
    
    private List<Rule> rules;
    
    private Collection<Solver> solvers;
    
    private DebugInfo debugInfo;
    
    public CHRIntermediateForm(
        Handler handler, 
        Set<VariableType> variableTypes,
        HashSet<Variable> localVariables,
        Collection<UserDefinedConstraint> udConstraints,
        List<Rule> rules,
        Collection<Solver> solvers,
        DebugInfo debugInfo
    ) {       
        
        setHandler(handler);
        setVariableTypes(variableTypes);
        setLocalVariables(localVariables);
        setUdConstraints(udConstraints);
        setRules(rules);
        setSolvers(solvers);
        setDebugInfo(debugInfo);
    }

    /**
     * @see compiler.CHRIntermediateForm.ICHRIntermediateForm#getUserDefinedConstraints()
     */
    public Collection<UserDefinedConstraint> getUserDefinedConstraints() {
        return udConstraints;
    }
    public int getNbUdConstraints() {
        return getUserDefinedConstraints().size();
    }

    public List<Rule> getRules() {
        return rules;
    }
    public int getNbRules() {
        return getRules().size();
    }

    public Collection<Solver> getSolvers() {
        return solvers;
    }
    public int getNbSolvers() {
        return getSolvers().size();
    }

    @Override
    public String toString() {
        return new StringBuffer()
            .append(getHandler())
            .append("\n\n")
            .append(getVariableTypes())
            .append("\n\n")
            .append(getUserDefinedConstraints())
            .append("\n\n")
            .append(getRules())
            .toString();
    }

    /**
     * @see compiler.CHRIntermediateForm.ICHRIntermediateForm#getHandler()
     */
    public Handler getHandler() {
        return handler;
    }
    public String getHandlerName() {
        return getHandler().getIdentifier();
    }
    protected void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Set<VariableType> getVariableTypes() {
        return variableTypes;
    }
    public int getNbVariableTypes() {
        return getVariableTypes().size();
    }
    protected void setVariableTypes(Set<VariableType> variableTypes) {
        this.variableTypes = variableTypes;
    }

    protected void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    protected void setSolvers(Collection<Solver> solvers) {
        this.solvers = solvers;
    }

    protected void setUdConstraints(Collection<UserDefinedConstraint> udConstraints) {
        this.udConstraints = udConstraints;
    }
    
    public DebugInfo getDebugInfo() {
        return debugInfo;
    }
    protected void setDebugInfo(DebugInfo debugInfo) {
        this.debugInfo = debugInfo;
    }

    public HashSet<Variable> getLocalVariables() {
        return localVariables;
    }
    public int getNbLocalVariables() {
        return getLocalVariables().size();
    }
    protected void setLocalVariables(HashSet<Variable> localVariables) {
        this.localVariables = localVariables;
    }
}