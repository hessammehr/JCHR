package compiler.CHRIntermediateForm.constraints.ud.schedule;

import util.visitor.IExtendedVisitor;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.Lookup;
import compiler.CHRIntermediateForm.rulez.NegativeHead;

public interface IJoinOrderVisitor extends IExtendedVisitor {

    /**
     * Called when the visitor is visiting a {@link Occurrence}
     * element of the join ordering.
     * 
     * @param occurrence
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(Occurrence occurrence) throws Exception;
    
    /**
     * Called when the visitor is visiting a {@link Lookup}
     * element of the join ordering.
     * 
     * @param lookup
     *  The join order elementof the join ordering currently visited.
     * @throws Exception
     *  If an exception occurs during the visit.
     */
    public void visit(IGuardConjunct explicitGuard) throws Exception;
    
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
