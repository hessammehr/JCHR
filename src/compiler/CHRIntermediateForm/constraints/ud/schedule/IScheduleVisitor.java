package compiler.CHRIntermediateForm.constraints.ud.schedule;

import util.visitor.IExtendedVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.ImplicitGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;

public interface IScheduleVisitor extends IExtendedVisitor {

    /**
     * Called when the visitor is visiting a {@link Lookup}
     * element of the join ordering.
     * 
     * @param occurrence
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(Lookup lookup) throws Exception;
    
    /**
     * Called when the visitor is visiting a {@link IGuardConjunct}
     * element of the join ordering.
     * 
     * @param lookup
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(IGuardConjunct explicitGuard) throws Exception;
    
    /**
     * Called when the visitor is visiting a {@link ImplicitGuardConjunct}
     * element of the join ordering.
     * 
     * @param lookup
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(ImplicitGuardConjunct implicitGuard) throws Exception;
    
    /**
     * Called when the visitor is visiting a {@link NegativeHead}
     * element of the join ordering.
     * 
     * @param lookup
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(NegativeHead negativeHead) throws Exception;
}
