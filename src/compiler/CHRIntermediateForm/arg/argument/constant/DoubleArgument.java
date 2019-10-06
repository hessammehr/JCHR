package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class DoubleArgument extends LiteralArgument<Double> {
    public DoubleArgument(double value) {
        this(Double.valueOf(value));
    }
    public DoubleArgument(Double value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.DOUBLE;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}