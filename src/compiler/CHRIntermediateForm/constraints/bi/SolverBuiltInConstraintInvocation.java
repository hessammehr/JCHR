package compiler.CHRIntermediateForm.constraints.bi;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.members.MethodInvocation;

public class SolverBuiltInConstraintInvocation extends MethodInvocation<SolverBuiltInConstraint> 
implements IBuiltInConjunct<SolverBuiltInConstraint>{

    public SolverBuiltInConstraintInvocation(SolverBuiltInConstraint constraint, IArguments arguments) {
        super(constraint, arguments);
    }
    
    public SolverBuiltInConstraint getConstraint() {
        return getArgumentable();
    }
    
    public String getIdentifier() {
        return getConstraint().getIdentifier();
    }
    
    @Override
    public boolean isEquality() {
        return getConstraint().isEquality() && !isNegated();
    }
    
    @Override
    public float getSelectivity() {
        return isEquality()? 1 : .5f;
    }
    
    public boolean warrantsStackOptimization() {
    	return getConstraint().warrantsStackOptimization();
    }
}
