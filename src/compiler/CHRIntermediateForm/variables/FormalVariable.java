package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConstraint;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

public class FormalVariable extends TypedVariable 
    implements Comparable<FormalVariable> {

    public FormalVariable(
        UserDefinedConstraint constraint, String identifier, VariableType type
    ) throws IllegalIdentifierException, DuplicateIdentifierException {
        super(identifier, type);
        constraint.linkFormalVariable(this);
    }
    
    public int compareTo(FormalVariable other) {
        return this.getIdentifier().compareTo(other.getIdentifier());
    }
}
