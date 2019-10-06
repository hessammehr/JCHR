package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.Iterator;

import util.iterator.ChainingIterator;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;

public class DefaultNegativeJoinOrder extends AbstractJoinOrder {
    /**
     * The negative head this is a default join order for.
     */
    private NegativeHead head;
    
    /**
     * Creates a default join order for a negative head.
     * 
     * @param active
     *  The active occurrence for the join order (will not be included).
     */
    public DefaultNegativeJoinOrder(NegativeHead head) {
        setHead(head);
    }

    protected NegativeHead getHead() {
        return head;
    }
    protected void setHead(NegativeHead head) {
        this.head = head;
    }

    public Iterator<IJoinOrderElement> iterator() {
        final Head head = getHead();
        
        // First, we need an iterator over the lookups:
        Iterator<Occurrence> occurrences = head.getOccurrences().iterator();
        
        // Next, we need an iterator over the explicit (and explicitized) guards:
        Iterator<IGuardConjunct> explicit = head.getGuard().iterator();
        
        // Finally we chain them chain together and we're done:
        @SuppressWarnings("unchecked")
        Iterator<IJoinOrderElement> result = 
            new ChainingIterator<IJoinOrderElement>(occurrences, explicit);
        
        return result;
    }
}
