package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class LongArgument extends LiteralArgument<Long> {
    public LongArgument(long value) {
        this(Long.valueOf(value));
    }
    public LongArgument(Long value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.LONG;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}