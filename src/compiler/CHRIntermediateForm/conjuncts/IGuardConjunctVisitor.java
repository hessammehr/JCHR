package compiler.CHRIntermediateForm.conjuncts;

import util.visitor.IVisitor;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.variables.Variable;

public interface IGuardConjunctVisitor extends IVisitor {
    
    public void visit(ConstructorInvocation conjunct) throws Exception;
    
    public void visit(Failure conjunct) throws Exception;
    
    public void visit(FieldAccess conjunct) throws Exception;
    
    public void visit(MethodInvocation<?> conjunct) throws Exception;
    
    public void visit(NoSolverConjunct conjunct) throws Exception;
    
    public void visit(AssignmentConjunct conjunct) throws Exception;
    
    public void visit(Variable conjunct) throws Exception;

}
