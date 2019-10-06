package compiler.CHRIntermediateForm.init;

import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.Variable;


public interface IDeclarator<T extends IDeclarator<?>> {

    public IType getType();
    
    public boolean usesIdentifier();
    
    public NoSolverConjunct getInstance(Variable variable);
    
    public NoSolverConjunct getInstance(Variable variable, String identifier);

}