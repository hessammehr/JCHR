package compiler.CHRIntermediateForm.constraints.java;

import util.exceptions.IndexOutOfBoundsException;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Type;
import compiler.CHRIntermediateForm.variables.Variable;

public class Assignment extends NoSolverConstraint {
    final public static String ID  = "="; 
    
    public Assignment(IType argumentType) {
        super(argumentType, ID, Type.isBoolean(argumentType));
    }
    
    @Override
    public boolean isEquality() {
        return true;
    }
    
    /*
     * version 1.0.3
     *  - In case of an assignment the type of the first argument
     *      should be equal to the formal type, and not some
     *      supertype.
     */
    @Override    
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        if (index == 0) 
            return MatchingInfo.valueOf(
                (argument instanceof Variable) &&
                // argument.isDirectlyAssignableTo(getFormalParameterTypeAt(0))
                argument.getType().equals(getFormalParameterTypeAt(0))
            );
        else if (index == 1)
            return super.canHaveAsArgumentAt(index, argument);
        else throw new IndexOutOfBoundsException(index, 1);
    }
    
    @Override
    public AssignmentConjunct createInstance(IArgument... arguments) {
    	return (AssignmentConjunct)super.createInstance(arguments);
    }
    @Override
    public AssignmentConjunct createInstance(IArguments arguments) {
        return new AssignmentConjunct(this, arguments);
    }
    
    @Override
    public boolean equals(NoSolverConstraint other) {
        return (other instanceof Assignment)
            && super.equals(other);
    }

    @Override
    public boolean isAntisymmetric() {
        return false;
    }
    @Override
    public boolean isAsymmetric() {
        return false;
    }
    @Override
    public boolean isCoreflexive() {
        return false;
    }
    @Override
    public boolean isIrreflexive() {
        return false;
    }
    @Override
    public boolean isReflexive() {
        return false;
    }
    @Override
    public boolean isSymmetric() {
        return false;
    }
    @Override
    public boolean isTotal() {
        return false;
    }
    @Override
    public boolean isTransitive() {
        return false;
    }
    @Override
    public boolean isTrichotomous() {
        return false;
    }
}