package compiler.CHRIntermediateForm.arg.visitor;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;
import compiler.CHRIntermediateForm.arg.argumentable.IArgumentable;
import compiler.CHRIntermediateForm.arg.argumented.IArgumented;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;

public abstract class UpCastingArgumentVisitor 
    extends UpCastingLeafArgumentVisitor 
    implements IArgumentVisitor {
    
    public UpCastingArgumentVisitor(boolean explicitOnly) {
        super(explicitOnly);
    }

    public void visit(IArgument arg) {
        // NOP (default implementation)
    }
    
    public <T extends IArgumented<? extends IArgumentable<?>> & IArgument> 
        void visitArgumented(T arg) throws Exception {
        visit(arg);
    }
    
    public <T extends IArgumented<? extends IArgumentable<?>> & IArgument> 
        void leaveArgumented(T arg) throws Exception {
        // NOP (default implementation)
    }
    
    @Override
    public void visit(ILeafArgument arg) {
        visit((IArgument)arg);
    }
    
    public void visit(AbstractMethodInvocation<?> arg) throws Exception {
    	visitArgumented(arg);
    }
    
    public void visit(ConstructorInvocation arg) throws Exception {
    	visitArgumented(arg);
    }
    
    public void visit(FieldAccess arg) throws Exception {
    	visitArgumented(arg);
    }
    
    public void leave(AbstractMethodInvocation<?> arg) throws Exception {
        leaveArgumented(arg);
    }
    
    public void leave(ConstructorInvocation arg) throws Exception {
        leaveArgumented(arg);
    }
    
    public void leave(FieldAccess arg) throws Exception {
        leaveArgumented(arg);
    }
    
    public boolean recurse() {
        return true;        // default implementation
    }
}
