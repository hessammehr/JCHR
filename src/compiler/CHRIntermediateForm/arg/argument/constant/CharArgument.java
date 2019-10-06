package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.PrimitiveType;

/**
 * @author Peter Van Weert
 */
public class CharArgument extends LiteralArgument<String> {
    public CharArgument(char value) {
        this(String.valueOf(value));
    }
    public CharArgument(String value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return PrimitiveType.CHAR;
    }
    
    @Override
    public String toString() {
        return "'" + getValue() + "'";
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}