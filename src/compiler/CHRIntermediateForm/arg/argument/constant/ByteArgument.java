package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class ByteArgument extends LiteralArgument<Byte> {    
    public ByteArgument(byte value) {
        this(Byte.valueOf(value));
    }
    public ByteArgument(Byte value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.BYTE;
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}