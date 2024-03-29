package compiler.CHRIntermediateForm.matching;

import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public interface IAssignable {

    public MatchingInfo isAssignableTo(IType type);
    
    public boolean isDirectlyAssignableTo(IType type);

}
