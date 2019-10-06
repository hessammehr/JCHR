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

public class CHRIntermediateFormDecorator implements ICHRIntermediateForm {
    private ICHRIntermediateForm intermediateForm;
    
    protected CHRIntermediateFormDecorator() {
        // NOP
    }
    
    public CHRIntermediateFormDecorator(ICHRIntermediateForm intermediateForm) {
        setIntermediateForm(intermediateForm);
    }
    
    protected ICHRIntermediateForm getCHRIntermediateForm() {
        return intermediateForm;
    }

    protected void setIntermediateForm(ICHRIntermediateForm intermediateForm) {
        this.intermediateForm = intermediateForm;
    }
    
    public Collection<UserDefinedConstraint> getUserDefinedConstraints() {
        return getCHRIntermediateForm().getUserDefinedConstraints();
    }
    public int getNbUdConstraints() {
        return getCHRIntermediateForm().getNbUdConstraints();
    }

    public List<Rule> getRules() {
        return getCHRIntermediateForm().getRules();
    }
    public int getNbRules() {
        return getCHRIntermediateForm().getNbRules();
    }
    
    public Rule getRuleAt(int index) {
        return getRules().get(index);
    }

    public Collection<Solver> getSolvers() {
        return getCHRIntermediateForm().getSolvers();
    }
    public int getNbSolvers() {
        return getCHRIntermediateForm().getNbSolvers();
    }
    
    public Handler getHandler() {
        return getCHRIntermediateForm().getHandler();
    }
    public String getHandlerName() {
        return getCHRIntermediateForm().getHandlerName();
    }
    
    public Set<VariableType> getVariableTypes() {
        return getCHRIntermediateForm().getVariableTypes();
    }
    public int getNbVariableTypes() {
        return getCHRIntermediateForm().getNbVariableTypes();
    }
    
    public DebugInfo getDebugInfo() {
        return getCHRIntermediateForm().getDebugInfo();
    }

    public HashSet<Variable> getLocalVariables() {
        return getCHRIntermediateForm().getLocalVariables();
    }
    public int getNbLocalVariables() {
        return getCHRIntermediateForm().getNbLocalVariables();
    }
}
