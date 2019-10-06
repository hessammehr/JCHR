package compiler.CHRIntermediateForm.variables;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Type;

/**
 * All variables except the nameless variable are typed. Hence this
 * abstract class.
 * 
 * @author Peter Van Weert
 */
public abstract class TypedVariable extends AbstractVariable {

    private VariableType variableType;
    
    public TypedVariable(String identifier, VariableType type)
    throws IllegalIdentifierException {
        super(identifier);
        setVariableType(type);
    }

    public VariableType getVariableType() {
        return variableType;
    }
    protected void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }
    
    public boolean hasSameVariableTypeAs(TypedVariable other) {
        return hasAsVariableType(other.getVariableType());
    }
    public boolean hasAsVariableType(VariableType variableType) {
        return this.getVariableType().equals(variableType);
    }
    
    public IType getType() {
        return getVariableType().getType();
    }
    public String getTypeString() {
        return getType().toTypeString();
    }
    public boolean isFixed() {
        return getVariableType().isFixed();
    }
    
    public boolean isBooleanVariable() {
        return Type.isBoolean(getType());
    }
    
    public MatchingInfo isAssignableTo(IType type) {
        return getType().isAssignableTo(type);
    }
    
    public boolean isDirectlyAssignableTo(IType type) {
        return getType().isDirectlyAssignableTo(type);
    }
    
    @Override
    public boolean canHaveAsIdentifier(String identifier) {
        return super.canHaveAsIdentifier(identifier)
            && ! NamelessVariable.IDENTIFIER.equals(identifier);
    }
}
