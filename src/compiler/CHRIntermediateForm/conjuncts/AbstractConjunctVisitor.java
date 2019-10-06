package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;

public abstract class AbstractConjunctVisitor 
    extends AbstractGuardConjunctVisitor
    implements IConjunctVisitor {

    public void visit(InitialisatorMethodInvocation conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }

    public void visit(UserDefinedConjunct conjunct) throws Exception {
        visit((IConjunct)conjunct);
    }

    public void visit(Occurrence occurrence) throws Exception {
        visit((IConjunct)occurrence);
    }

    public boolean visits(OccurrenceType type) {
        return true;
    }
}