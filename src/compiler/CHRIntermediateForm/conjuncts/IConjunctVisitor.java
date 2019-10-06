package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;
import compiler.CHRIntermediateForm.rulez.IOccurrenceVisitor;


public interface IConjunctVisitor extends IOccurrenceVisitor, IGuardConjunctVisitor {
    
    public void visit(InitialisatorMethodInvocation conjunct) throws Exception;
    
    public void visit(UserDefinedConjunct conjunct) throws Exception;

}
