package compiler.CHRIntermediateForm.constraints;

import compiler.CHRIntermediateForm.arg.argumented.Argumented;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;

public abstract class ConstraintConjunct<T extends IConstraint<?>> 
    extends Argumented<T> 
    implements IConstraintConjunct<T> {

    public ConstraintConjunct(T constraint, IArguments arguments) {
        super(constraint, arguments);
    }
    
    public String getIdentifier() {
        return getConstraint().getIdentifier();
    }
    public String[] getInfixIdentifiers() {
        return getConstraint().getInfixIdentifiers();
    }
    
    @Override
    public String toString() {
        return getIdentifier() + getArguments();
    }
    
    public T getConstraint() {
        return getArgumentable();
    }
    
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
}
