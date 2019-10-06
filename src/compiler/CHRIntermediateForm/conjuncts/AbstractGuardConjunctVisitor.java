package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.IConstraintConjunct;
import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class AbstractGuardConjunctVisitor implements IGuardConjunctVisitor {

    protected abstract void visit(IConjunct conjunct) throws Exception;
    
    public void visit(ConstructorInvocation conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }

    public void visit(Failure conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }

    public void visit(FieldAccess conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }

    public void visit(MethodInvocation<?> conjunct) throws Exception {
    	if (conjunct instanceof IConstraintConjunct)
    		visit((IConstraintConjunct<?>)conjunct);
    	else
    		visit((IConjunct)conjunct);
    }
    
    protected void visit(IConstraintConjunct<?> conjunct) throws Exception {
    	visit((IConjunct)conjunct);
    }

    public void visit(NoSolverConjunct conjunct) throws Exception {
        visit((IConstraintConjunct<?>)conjunct);
    }

    public void visit(AssignmentConjunct conjunct) throws Exception {
        visit((IConstraintConjunct<?>)conjunct);
    }

    public void visit(Variable conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }
}