package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public class StringArgument extends LiteralArgument<String> {
    public StringArgument(String value) {
        super(value);
    }
    
    @Override
    public IType getType() {
        return GenericType.getNonParameterizableInstance(String.class);
    }
    
    @Override
    public String toString() {
        return '"' + getValue() + '"';
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}