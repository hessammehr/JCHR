package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class ArgumentedGuardConjunctVisitor
    implements IGuardConjunctVisitor {

    public abstract void visit(IBasicArgumented conjunct) throws Exception;
    
    public void visit(MethodInvocation<?> conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(AssignmentConjunct conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(ConstructorInvocation conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(FieldAccess conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(NoSolverConjunct conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(Failure conjunct) throws Exception {
        // NOP
    }
    
    public void visit(Variable conjunct) throws Exception {
        // NOP
    }
}