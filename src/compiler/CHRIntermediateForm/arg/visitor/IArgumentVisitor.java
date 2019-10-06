package compiler.CHRIntermediateForm.arg.visitor;

import util.visitor.IExtendedVisitor;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;


public interface IArgumentVisitor extends ILeafArgumentVisitor, IExtendedVisitor {
    
    public boolean recurse();
    
    public void visit(ConstructorInvocation arg) throws Exception;
    public void leave(ConstructorInvocation arg) throws Exception;

    public void visit(AbstractMethodInvocation<?> arg) throws Exception;
    public void leave(AbstractMethodInvocation<?> arg) throws Exception;
    
    public void visit(FieldAccess arg) throws Exception;
    public void leave(FieldAccess arg) throws Exception;
    
}
