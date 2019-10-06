package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class IntArgument extends LiteralArgument<Integer> {
    public final static IntArgument ZERO = new IntArgument(0);
    
    public IntArgument(int value) {
        this(Integer.valueOf(value));
    }
    public IntArgument(Integer value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.INT;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}