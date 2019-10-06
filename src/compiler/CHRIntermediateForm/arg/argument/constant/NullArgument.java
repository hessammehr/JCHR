package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.argument.LeafArgument;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public class NullArgument extends LeafArgument {
    private NullArgument() {
        /* SINGLETON */
    }
    
    private static NullArgument instance;
    public static NullArgument getInstance() {
        if (instance == null)
            instance = new NullArgument();
        return instance;
    }

    @Override
    public String toString() {
        return "null";
    }
    
    public IType getType() {
        return IType.OBJECT;
    }
    
    public boolean isFixed() {
        return true;
    }
    public boolean isConstant() {
        return true;
    }
    
    @Override
    public MatchingInfo isAssignableTo(IType other) {
        return MatchingInfo.valueOf(isDirectlyAssignableTo(other));
    }
    @Override
    public boolean isDirectlyAssignableTo(IType other) {
        return other.isDirectlyAssignableTo(IType.OBJECT);
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}