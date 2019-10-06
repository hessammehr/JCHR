package compiler.CHRIntermediateForm.arg.argument;

import java.util.Set;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.members.Field;
import compiler.CHRIntermediateForm.members.Method;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.VariableType;

public abstract class FormalArgument extends LeafArgument implements IImplicitArgument {

    protected FormalArgument(IType type) {
        setType(type);
    }
    
    public final static FormalArgument getOneInstance(VariableType variableType) {
        return new OneDummy(variableType.getType());
    }
    
    public final static FormalArgument getOtherInstance(IType type) {
        return new OtherDummy(type);
    }
    
    private IType type;
    
    public IType getType() throws UntypedArgumentException {
        return type;
    }
    protected void setType(IType type) {
        this.type = type;
    }
    
    public boolean isFixed() {
        throw new UnsupportedOperationException();
    }
    public boolean isConstant() {
        throw new UnsupportedOperationException();
    }
    
    public Set<Method> getMethods(String id) {
        return getType().getMethods(id);
    }
    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return getType().getField(name);
    }
    
    public Cost getExpectedCost() {
    	throw new UnsupportedOperationException();
    }
    
    public final static class OneDummy extends FormalArgument {
        OneDummy(IType type) {
            super(type);
        }
        
        public void accept(ILeafArgumentVisitor visitor) throws Exception {
            visitor.visit(this);
        }
        
        @Override
        public String toString() {
            return "ONE_DUMMY(" +  getType().toTypeString() + ')';
        }
    }
    
    public final static class OtherDummy extends FormalArgument {
        OtherDummy(IType type) {
            super(type);
        }
        
        public void accept(ILeafArgumentVisitor visitor) throws Exception {
            visitor.visit(this);
        }
        
        @Override
        public String toString() {
            return "OTHER_DUMMY(" +  getType().toTypeString() + ')';
        }
    }
}