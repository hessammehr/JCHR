package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class FloatArgument extends LiteralArgument<Float> {
    public FloatArgument(float value) {
        this(Float.valueOf(value));
    }
    public FloatArgument(Float value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.FLOAT;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}