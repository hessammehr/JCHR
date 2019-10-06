package compiler.CHRIntermediateForm.arg.argument.constant;

import compiler.CHRIntermediateForm.arg.argument.LeafArgument;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public abstract class LiteralArgument<Value> extends LeafArgument {

    public LiteralArgument(Value value) {
        setValue(value);
    }

    private Value value;

    public Value getValue() {
        return value;
    }

    protected void setValue(Value value) {
        this.value = value;
    }

    public abstract IType getType();

    public boolean isFixed() {
        return true;
    }
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
    
    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return (obj instanceof LiteralArgument)
            && ((LiteralArgument)obj).getValue().equals(getValue());
    }
}