package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;

public abstract class NOPArgumentVisitor extends NOPLeafArgumentVisitor 
    implements IArgumentVisitor {
    
    
    public NOPArgumentVisitor() {
        super();
    }
    public NOPArgumentVisitor(boolean explicitOnly) {
        super(explicitOnly);
    }

    public void visit(AbstractMethodInvocation<?> arg) throws Exception {
        // NOP
    }
    
    public void visit(ConstructorInvocation arg) throws Exception {
        // NOP
    }
    
    public void visit(FieldAccess arg) throws Exception {
        // NOP
    }
    
    public void leave(AbstractMethodInvocation<?> arg) throws Exception {
        // NOP
    }
    
    public void leave(ConstructorInvocation arg) throws Exception {
        // NOP
    }
    
    public void leave(FieldAccess arg) throws Exception {
        // NOP
    }
    
    public boolean recurse() {
        return true;        // default implementation
    }
}
