package compiler.CHRIntermediateForm.variables;

import util.Cloneable;
import util.builder.Current;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.conjuncts.IConjunct;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.exceptions.IllegalVariableTypeException;

public class VariableType implements Cloneable<VariableType> {
    private IType type;
    
    private boolean fixed;
    
    private Current<IConjunct> eq = new Current<IConjunct>();
    
    protected VariableType() {
        // NOP
    }

    public VariableType(IArgument argument) throws IllegalVariableTypeException {
        this(argument.getType(), argument.isFixed());
    }
    
    public VariableType(IType type, boolean fixed) {
        init(type, fixed);
    }
    
    protected void init(IType type, boolean fixed) {
        setType(type);
        setFixed(fixed);
    }
    
    public boolean isFixed() {
        return fixed || getType().isFixed();
    }
    protected void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    public boolean canBeUsedInHashMap() {
        return isFixed() || isHashObservable();
    }
    
    public boolean isBuiltInConstraintObservable() {
        return getType().isBuiltInConstraintObservable();
    }
    
    public boolean isHashObservable() {
        return getType().isHashObservable();
    }

    public String getTypeString() {
        return getType().toTypeString();
    }
    public IType getType() {
        return type;
    }
    protected void setType(IType type) {
        this.type = type;
    }
    
    public boolean hasInitializedBuiltInConjuncts() {
        return hasInitializedEq();
    }

    public IConjunct getEq() throws IllegalStateException {
        return eq.get();
    }
    public boolean hasInitializedEq() {
        return eq.isSet();
    }
    public void initEq(IConjunct eq) throws IllegalStateException {
        this.eq.set(eq);
    }
    
    @Override
    public boolean equals(Object other) {
        return (this == other) 
            || (other instanceof VariableType) && this.equals((VariableType)other);
    }
    public boolean equals(VariableType other) {
        return (this.isFixed() == other.isFixed())
            && this.getType().equals(other.getType());
    }
    
    @Override
    public int hashCode() {
        return 37 * (37 * (23) + (fixed? 666 : 17011983)) + type.hashCode(); 
    }
    
    @Override
    public VariableType clone() {
        try {
            return (VariableType)super.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new InternalError();
        }
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        if (isFixed()) result.append('+');
        result.append(getTypeString()); 
        
//        if (isObservable()) {
//            result.append(" implements Observable");
//            if (isHashObservable()) {
//                result.append(", HashObservable");
//            }
//        } else {
//            if (isHashObservable()) {
//                result.append("implements HashObservable");
//            }
//        }
        
        return result.toString();
    }
}
