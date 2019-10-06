package compiler.CHRIntermediateForm.constraints.ud.schedule;

import util.visitor.AbstractExtendedVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.rulez.NegativeHead;

public abstract class AbstractJoinOrderVisitor
    extends AbstractExtendedVisitor
    implements IJoinOrderVisitor {
    
    protected void visit(IJoinOrderElement element) throws Exception {
        // NOP
    }
    
    public void visit(IGuardConjunct explicitGuard) throws Exception {
        visit((IJoinOrderElement)explicitGuard);
    }
    
    public void visit(Occurrence occurrence) throws Exception {
        visit((IJoinOrderElement)occurrence);
    }
    
    public void visit(NegativeHead negativeHead) throws Exception {
        visit((IJoinOrderElement)negativeHead);
    }
}