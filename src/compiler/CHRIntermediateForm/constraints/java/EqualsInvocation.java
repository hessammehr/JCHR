package compiler.CHRIntermediateForm.constraints.java;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.members.MethodInvocation;

public class EqualsInvocation 
    extends MethodInvocation<Equals>
    implements IJavaConjunct<Equals> {

    public EqualsInvocation(Equals constraint, IArguments arguments) {
        super(constraint, arguments);
    }

    public Equals getConstraint() {
        return getArgumentable();
    }
    
    @Override
    public boolean canBeAskConjunct() {
        return true;
    }
    
    @Override
    public float getSelectivity() {
        return isNegated()? 0 : 1;
    }

    public String getIdentifier() {
        return getConstraint().getIdentifier();
    }
    
    @Override
    public boolean isNegated() {
        return getConstraint() == Equals.getNegatedInstance();
    }
    
    @Override
    public boolean isEquality() {
        return getConstraint().isEquality();
    }
    
    public boolean isAntisymmetric() {
        return getConstraint().isAntisymmetric();
    }
    public boolean isAsymmetric() {
        return getConstraint().isAsymmetric();
    }
    public boolean isCoreflexive() {
        return getConstraint().isCoreflexive();
    }
    public boolean isIrreflexive() {
        return getConstraint().isIrreflexive();
    }
    public boolean isReflexive() {
        return getConstraint().isReflexive();
    }
    public boolean isSymmetric() {
        return getConstraint().isSymmetric();
    }
    public boolean isTotal() {
        return getConstraint().isTotal();
    }
    public boolean isTransitive() {
        return getConstraint().isTransitive();
    }
    public boolean isTrichotomous() {
        return getConstraint().isTrichotomous();
    }
    
    public boolean warrantsStackOptimization() {
    	return getConstraint().warrantsStackOptimization();
    }    
}