package compiler.CHRIntermediateForm.init;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.members.AbstractMethodInvocation;

public class InitialisatorMethodInvocation 
extends AbstractMethodInvocation<InitialisatorMethod> 
implements IInitialisatorInvocation<InitialisatorMethod> {

    public InitialisatorMethodInvocation(InitialisatorMethod type, IArguments arguments) {
        super(type, arguments);
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
