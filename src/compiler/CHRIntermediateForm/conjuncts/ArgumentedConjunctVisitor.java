package compiler.CHRIntermediateForm.conjuncts;

import compiler.CHRIntermediateForm.arg.argumented.IBasicArgumented;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.constraints.ud.UserDefinedConjunct;
import compiler.CHRIntermediateForm.init.InitialisatorMethodInvocation;

public abstract class ArgumentedConjunctVisitor
    extends ArgumentedGuardConjunctVisitor
    implements IConjunctVisitor {

    public void visit(InitialisatorMethodInvocation conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public void visit(Occurrence occurrence) throws Exception {
        visit((IBasicArgumented)occurrence);
    }
    
    public void visit(UserDefinedConjunct conjunct) throws Exception {
        visit((IBasicArgumented)conjunct);
    }
    
    public boolean visits(OccurrenceType type) {
        return true;
    }
}