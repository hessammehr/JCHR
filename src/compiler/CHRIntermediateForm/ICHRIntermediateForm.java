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

public interface ICHRIntermediateForm {

    public Collection<UserDefinedConstraint> getUserDefinedConstraints();
    public int getNbUdConstraints();

    public List<Rule> getRules();
    public int getNbRules();
    
    public Collection<Solver> getSolvers();
    public int getNbSolvers();

    public Handler getHandler();
    public String getHandlerName();

    public Set<VariableType> getVariableTypes();
    public int getNbVariableTypes();
    
    public HashSet<Variable> getLocalVariables();
    public int getNbLocalVariables();
    
    public DebugInfo getDebugInfo();
}