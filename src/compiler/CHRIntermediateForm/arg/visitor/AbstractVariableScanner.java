package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.variables.Variable;

public abstract class AbstractVariableScanner extends NOPLeafArgumentVisitor {

    private boolean result;
    
    @Override
    public final void visit(Variable arg) {
        if (!result && scanVariable(arg)) result = true; 
    }
    
    protected abstract boolean scanVariable(Variable variable);
    
    public boolean getResult() {
        return result;
    }
    
    public boolean scan(ILeafArgumentVisitable visitable) {
        try{
            visitable.accept(this);
            return getResult();
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }
}
