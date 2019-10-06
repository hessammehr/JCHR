package compiler.CHRIntermediateForm.arg.argument;

import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public abstract class Argument implements IArgument {

    @Override
    public abstract String toString();
    
    public MatchingInfo isAssignableTo(IType other) {
        return getType().isAssignableTo(other);
    }
    public boolean isDirectlyAssignableTo(IType other) {
        return getType().isDirectlyAssignableTo(other);
    }
}