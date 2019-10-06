package compiler.CHRIntermediateForm.constraints;

import compiler.CHRIntermediateForm.conjuncts.IConjunct;


public interface IConstraintConjunct<T extends IConstraint<?>> extends IConjunct {

    public String getIdentifier();
    
    public T getConstraint();

}