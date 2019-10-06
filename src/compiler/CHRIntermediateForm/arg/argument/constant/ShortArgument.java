package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class ShortArgument extends LiteralArgument<Short> {
    public ShortArgument(short value) {
        this(Short.valueOf(value));
    }
    public ShortArgument(Short value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.SHORT;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}