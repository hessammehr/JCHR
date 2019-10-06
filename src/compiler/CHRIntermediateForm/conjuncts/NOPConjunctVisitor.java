package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.bi.Failure;
import compiler.CHRIntermediateForm.constraints.java.AssignmentConjunct;
import compiler.CHRIntermediateForm.constraints.java.NoSolverConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;
import compiler.CHRIntermediateForm.members.ConstructorInvocation;
import compiler.CHRIntermediateForm.members.FieldAccess;
import compiler.CHRIntermediateForm.members.MethodInvocation;
import compiler.CHRIntermediateForm.variables.Variable;

public class NOPConjunctVisitor implements IConjunctVisitor {

    public void visit(InitialisatorMethodInvocation conjunct) throws Exception {
        // NOP
    }

    public void visit(UserDefinedConjunct conjunct) throws Exception {
        // NOP
    }

    public void visit(Occurrence occurrence) throws Exception {
        // NOP
    }

    public boolean visits(OccurrenceType type) {
        return true;
    }

    public void visit(ConstructorInvocation conjunct) throws Exception {
        // NOP
    }

    public void visit(Failure conjunct) throws Exception {
        // NOP
    }

    public void visit(FieldAccess conjunct) throws Exception {
        // NOP
    }

    public void visit(MethodInvocation<?> conjunct) throws Exception {
        // NOP
    }

    public void visit(NoSolverConjunct conjunct) throws Exception {
        // NOP
    }

    public void visit(AssignmentConjunct conjunct) throws Exception {
        // NOP
    }

    public void visit(Variable conjunct) throws Exception {
        // NOP
    }
}