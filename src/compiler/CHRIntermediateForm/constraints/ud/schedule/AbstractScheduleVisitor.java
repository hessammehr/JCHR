package compiler.CHRIntermediateForm.constraints.ud.schedule;

import util.visitor.AbstractExtendedVisitor;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.ImplicitGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;


public abstract class AbstractScheduleVisitor
    extends AbstractExtendedVisitor
    implements IScheduleVisitor {
    
    protected void visit(IScheduleElement element) throws Exception {
        // NOP
    }
    
    public void visit(ISelector selector) throws Exception {
    	visit((IScheduleElement)selector);
    }
    
    public void visit(IGuardConjunct guard) throws Exception {
        visit((ISelector)guard);
    }
    
    public void visit(ImplicitGuardConjunct implicitGuard) throws Exception {
        visit((IGuardConjunct)implicitGuard);
    }
    
    public void visit(Lookup lookup) throws Exception {
        visit((IScheduleElement)lookup);
    }
    
    public void visit(NegativeHead negativeHead) throws Exception {
        visit((ISelector)negativeHead);
    }
}